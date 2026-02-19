package com.bookticket.notification_service.service;

import com.bookticket.notification_service.dto.BookingFailedEvent;
import com.bookticket.notification_service.dto.BookingSuccessEvent;
import com.bookticket.notification_service.dto.SeatDetails;
import com.bookticket.notification_service.dto.ShowDetails;
import com.bookticket.notification_service.dto.TicketDetails;
import com.bookticket.notification_service.enums.NotificationType;
import com.bookticket.notification_service.exception.NonRetryableNotificationException;
import com.bookticket.notification_service.exception.RetryableNotificationException;
import com.bookticket.notification_service.security.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class NotificationOrchestrationService {

    private final EmailService emailService;
    private final PdfService pdfService;
    private final UserService userService;
    private final TheaterService theaterService;
    private final BookingService bookingService;
    private final EmailTemplateService emailTemplateService;
    private final NotificationDLQService dlqService;
    private final Executor notificationExecutor;

    public NotificationOrchestrationService(EmailService emailService, PdfService pdfService, UserService userService,
                                            TheaterService theaterService, BookingService bookingService,
                                            EmailTemplateService emailTemplateService,
                                            NotificationDLQService dlqService,
                                            @Qualifier("notificationExecutor")Executor notificationExecutor) {
        this.emailService = emailService;
        this.pdfService = pdfService;
        this.userService = userService;
        this.theaterService = theaterService;
        this.bookingService = bookingService;
        this.emailTemplateService = emailTemplateService;
        this.dlqService = dlqService;
        this.notificationExecutor = notificationExecutor;
    }

    /**
     * Process booking success notification with retry mechanism
     * Retries 3 times with exponential backoff (1s, 2s, 4s) for retryable errors
     * Excludes 4xx client errors (404, 403) from retry as they won't succeed
     * If all retries fail, stores in Dead Letter Queue
     */
    @Async("notificationExecutor")
    @Retryable(
        retryFor = {RetryableNotificationException.class},
        noRetryFor = {NonRetryableNotificationException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void processBookingSuccess(BookingSuccessEvent bookingSuccessEvent) {
        log.info("Processing successful booking for user: {}", bookingSuccessEvent.userId());

        TicketDetails ticketDetails;
        try {
            UserContext.setUserId(bookingSuccessEvent.userId());
            ticketDetails = fetchTicketDetails(bookingSuccessEvent);

        } catch (RetryableNotificationException e) {
            log.error("Retryable error fetching ticket details for booking {}: {}",
                    bookingSuccessEvent.bookingId(), e.getMessage());
            throw e; // Re-throw to trigger retry
        } catch (NonRetryableNotificationException e) {
            log.error("Non-retryable error fetching ticket details for booking {}: {}",
                    bookingSuccessEvent.bookingId(), e.getMessage());
            throw e; // Re-throw but won't retry
        } finally {
            UserContext.clear();
        }

        // Generate and send email
        try {
            byte[] pdfBytes = pdfService.generateTicketPdf(ticketDetails);
            String htmlBody = emailTemplateService.generateBookingSuccessEmail(ticketDetails);
            emailService.sendEmailWithAttachment(
                    ticketDetails.emailId(),
                    "🎉 Booking Confirmed - Your Tickets are Ready!",
                    htmlBody,
                    "ticket.pdf",
                    pdfBytes
            );
            log.info("Successfully sent ticket to: {}", ticketDetails.emailId());
        } catch (Exception e) {
            log.error("Failed to send email for booking {}: {}", bookingSuccessEvent.bookingId(), e.getMessage());
            throw new RetryableNotificationException(
                "Failed to send email",
                "EMAIL_SERVICE",
                e
            );
        }
    }

    /**
     * Recovery method for booking success notification
     * Called when all retry attempts are exhausted
     * Stores the failed notification in DLQ
     */
    @Recover
    public void recoverBookingSuccess(RetryableNotificationException e, BookingSuccessEvent bookingSuccessEvent) {
        log.error("All retry attempts exhausted for booking success notification, booking {}: {}",
                bookingSuccessEvent.bookingId(), e.getMessage());

        dlqService.storeFailedSuccessNotification(
                bookingSuccessEvent.bookingId(),
                bookingSuccessEvent.userId(),
                bookingSuccessEvent.showId(),
                bookingSuccessEvent.totalAmount(),
                e.getFailureReason(),
                e.getMessage()
        );

        log.warn("Booking success notification stored in DLQ for booking {}", bookingSuccessEvent.bookingId());
    }

    /**
     * Recovery method for non-retryable errors
     * Stores in DLQ with FAILED status (no retry needed)
     * This provides visibility and monitoring for permanent failures like 404, 403
     * Called immediately when a non-retryable error occurs
     */
    @Recover
    public void recoverBookingSuccessNonRetryable(NonRetryableNotificationException e, BookingSuccessEvent bookingSuccessEvent) {
        log.error("Non-retryable error for booking success notification, booking {}: {}. Storing in DLQ as FAILED.",
                bookingSuccessEvent.bookingId(), e.getMessage());

        // Store in DLQ with FAILED status (won't be retried by scheduler)
        dlqService.storeFailedNotificationAsPermanentFailure(
                NotificationType.BOOKING_SUCCESS,
                bookingSuccessEvent.bookingId(),
                bookingSuccessEvent.userId(),
                bookingSuccessEvent.showId(),
                bookingSuccessEvent.totalAmount(),
                null, // no reason for success events
                e.getFailureReason(),
                e.getMessage()
        );

        log.warn("Non-retryable notification stored in DLQ as FAILED for booking {}", bookingSuccessEvent.bookingId());
    }

    /**
     * Process booking failure notification with retry mechanism
     * Retries 3 times with exponential backoff (1s, 2s, 4s) for retryable errors
     * Excludes 4xx client errors (404, 403) from retry as they won't succeed
     * If all retries fail, stores in Dead Letter Queue
     */
    @Async("notificationExecutor")
    @Retryable(
        retryFor = {RetryableNotificationException.class},
        noRetryFor = {NonRetryableNotificationException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void processBookingFailure(BookingFailedEvent bookingFailedEvent) {
        log.info("Processing failed booking for user: {}", bookingFailedEvent.userId());
        String userEmail;

        try {
            UserContext.setUserId(bookingFailedEvent.userId());

            // Fetch user email with proper error handling
            userEmail = fetchUserEmail(bookingFailedEvent.userId());

            String htmlBody = emailTemplateService.generateBookingFailureEmail(
                    "Valued Customer",
                    bookingFailedEvent.bookingId(),
                    bookingFailedEvent.reason()
            );

            emailService.sendHtmlEmail(userEmail, "❌ Booking Unsuccessful - Please Try Again", htmlBody);
            log.info("Successfully sent failure notification to: {}", userEmail);

        } catch (RetryableNotificationException e) {
            log.error("Retryable error processing booking failure notification for booking {}: {}",
                    bookingFailedEvent.bookingId(), e.getMessage());
            throw e; // Re-throw to trigger retry
        } catch (NonRetryableNotificationException e) {
            log.error("Non-retryable error processing booking failure notification for booking {}: {}",
                    bookingFailedEvent.bookingId(), e.getMessage());
            throw e; // Re-throw but won't retry
        } catch (Exception e) {
            log.error("Failed to send failure notification for booking {}: {}",
                    bookingFailedEvent.bookingId(), e.getMessage());
            throw new RetryableNotificationException(
                "Failed to send failure notification",
                "EMAIL_SERVICE",
                e
            );
        } finally {
            UserContext.clear();
        }
    }

    /**
     * Recovery method for booking failure notification
     * Called when all retry attempts are exhausted
     * Stores the failed notification in DLQ
     */
    @Recover
    public void recoverBookingFailure(RetryableNotificationException e, BookingFailedEvent bookingFailedEvent) {
        log.error("All retry attempts exhausted for booking failure notification, booking {}: {}",
                bookingFailedEvent.bookingId(), e.getMessage());

        dlqService.storeFailedFailureNotification(
                bookingFailedEvent.bookingId(),
                bookingFailedEvent.userId(),
                bookingFailedEvent.showId(),
                bookingFailedEvent.totalAmount(),
                bookingFailedEvent.reason(),
                e.getFailureReason(),
                e.getMessage()
        );

        log.warn("Booking failure notification stored in DLQ for booking {}", bookingFailedEvent.bookingId());
    }

    /**
     * Recovery method for non-retryable errors
     * Stores in DLQ with FAILED status (no retry needed)
     * This provides visibility and monitoring for permanent failures like 404, 403
     */
    @Recover
    public void recoverBookingFailureNonRetryable(NonRetryableNotificationException e, BookingFailedEvent bookingFailedEvent) {
        log.error("Non-retryable error for booking failure notification, booking {}: {}. Storing in DLQ as FAILED.",
                bookingFailedEvent.bookingId(), e.getMessage());

        // Store in DLQ with FAILED status (won't be retried by scheduler)
        dlqService.storeFailedNotificationAsPermanentFailure(
                NotificationType.BOOKING_FAILED,
                bookingFailedEvent.bookingId(),
                bookingFailedEvent.userId(),
                bookingFailedEvent.showId(),
                bookingFailedEvent.totalAmount(),
                bookingFailedEvent.reason(),
                e.getFailureReason(),
                e.getMessage()
        );

        log.warn("Non-retryable notification stored in DLQ as FAILED for booking {}", bookingFailedEvent.bookingId());
    }

    /**
     * Fetch user email with proper error handling
     * Distinguishes between retryable (5xx, network) and non-retryable (4xx) errors
     */
    private String fetchUserEmail(Long userId) {
        try {
            return userService.getEmailById(userId);
        } catch (HttpClientErrorException e) {
            // 4xx errors - client errors, don't retry
            log.error("Client error fetching user email for user {}: {} {}",
                    userId, e.getStatusCode(), e.getMessage());
            throw new NonRetryableNotificationException(
                "User not found or forbidden: " + e.getStatusCode(),
                "USER_SERVICE"
            );
        } catch (HttpServerErrorException e) {
            // 5xx errors - server errors, retry
            log.error("Server error fetching user email for user {}: {} {}",
                    userId, e.getStatusCode(), e.getMessage());
            throw new RetryableNotificationException(
                "User service unavailable: " + e.getStatusCode(),
                "USER_SERVICE",
                e
            );
        } catch (ResourceAccessException e) {
            // Network/timeout errors, retry
            log.error("Network error fetching user email for user {}: {}", userId, e.getMessage());
            throw new RetryableNotificationException(
                "Network error connecting to user service",
                "USER_SERVICE",
                e
            );
        } catch (RestClientException e) {
            // Other REST errors, retry
            log.error("REST error fetching user email for user {}: {}", userId, e.getMessage());
            throw new RetryableNotificationException(
                "Error calling user service",
                "USER_SERVICE",
                e
            );
        }
    }

    /*
    Fetch ticket details with error handling
     */
    private TicketDetails fetchTicketDetails(BookingSuccessEvent bookingSuccessEvent) {
        TicketDetails ticketDetails;
        try {
            // Fetch user email, show details and seat details in parallel
            CompletableFuture<String> userEmailFuture = CompletableFuture.supplyAsync(() -> fetchUserEmail(bookingSuccessEvent.userId()),notificationExecutor);
            CompletableFuture<ShowDetails> showDetailsFuture = CompletableFuture.supplyAsync(() -> fetchShowDetails(bookingSuccessEvent.showId()), notificationExecutor);
            CompletableFuture<List<SeatDetails>> seatDetailsFuture = CompletableFuture.supplyAsync(() -> fetchSeatDetails(bookingSuccessEvent.bookingId()), notificationExecutor);
            CompletableFuture.allOf(userEmailFuture, showDetailsFuture, seatDetailsFuture).join();

            // Fetch user email with proper error handling
            String userEmail = userEmailFuture.join();

            // Fetch show details with proper error handling
            ShowDetails showDetails = showDetailsFuture.join();

            // Fetch seat details with proper error handling
            List<SeatDetails> seatDetails = seatDetailsFuture.join();

            ticketDetails = new TicketDetails(
                    bookingSuccessEvent.bookingId(),
                    bookingSuccessEvent.userId(),
                    userEmail,
                    showDetails,
                    seatDetails
            );
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if((cause instanceof RetryableNotificationException retryableEx)){
                throw retryableEx;
            } else if ((cause instanceof  NonRetryableNotificationException nonRetryableEx)){
                throw nonRetryableEx;
            } else {
                throw new RetryableNotificationException(
                        "Asynchronous operation failed",
                        "SYSTEM_ERROR",
                        e
                );
            }
        }
        return ticketDetails;
    }

    /**
     * Fetch show details with proper error handling
     */
    private ShowDetails fetchShowDetails(Long showId) {
        try {
            return theaterService.getShowDetails(showId);
        } catch (HttpClientErrorException e) {
            log.error("Client error fetching show details for show {}: {} {}",
                    showId, e.getStatusCode(), e.getMessage());
            throw new NonRetryableNotificationException(
                "Show not found or forbidden: " + e.getStatusCode(),
                "THEATER_SERVICE"
            );
        } catch (HttpServerErrorException e) {
            log.error("Server error fetching show details for show {}: {} {}",
                    showId, e.getStatusCode(), e.getMessage());
            throw new RetryableNotificationException(
                "Theater service unavailable: " + e.getStatusCode(),
                "THEATER_SERVICE",
                e
            );
        } catch (ResourceAccessException e) {
            log.error("Network error fetching show details for show {}: {}", showId, e.getMessage());
            throw new RetryableNotificationException(
                "Network error connecting to theater service",
                "THEATER_SERVICE",
                e
            );
        } catch (RestClientException e) {
            log.error("REST error fetching show details for show {}: {}", showId, e.getMessage());
            throw new RetryableNotificationException(
                "Error calling theater service",
                "THEATER_SERVICE",
                e
            );
        }
    }

    /**
     * Fetch seat details with proper error handling
     */
    private List<SeatDetails> fetchSeatDetails(Long bookingId) {
        try {
            return bookingService.getSeatDetails(bookingId);
        } catch (HttpClientErrorException e) {
            log.error("Client error fetching seat details for booking {}: {} {}",
                    bookingId, e.getStatusCode(), e.getMessage());
            throw new NonRetryableNotificationException(
                "Booking not found or forbidden: " + e.getStatusCode(),
                "BOOKING_SERVICE"
            );
        } catch (HttpServerErrorException e) {
            log.error("Server error fetching seat details for booking {}: {} {}",
                    bookingId, e.getStatusCode(), e.getMessage());
            throw new RetryableNotificationException(
                "Booking service unavailable: " + e.getStatusCode(),
                "BOOKING_SERVICE",
                e
            );
        } catch (ResourceAccessException e) {
            log.error("Network error fetching seat details for booking {}: {}", bookingId, e.getMessage());
            throw new RetryableNotificationException(
                "Network error connecting to booking service",
                "BOOKING_SERVICE",
                e
            );
        } catch (RestClientException e) {
            log.error("REST error fetching seat details for booking {}: {}", bookingId, e.getMessage());
            throw new RetryableNotificationException(
                "Error calling booking service",
                "BOOKING_SERVICE",
                e
            );
        }
    }
}
