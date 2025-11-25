package com.bookticket.notification_service.service;

import brevoApi.TransactionalEmailsApi;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailAttachment;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class EmailService {

    private final TransactionalEmailsApi brevoApi;
    private final String senderEmail;
    private final String senderName;

    public EmailService(TransactionalEmailsApi brevoApi,
                        @Value("${spring.mail.username}") String senderEmail,
                        @Value("${brevo.sender.name:BookTicket}") String senderName) {
        this.brevoApi = brevoApi;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    public void sendEmailWithAttachment(String to, String subject, String body, String attachmentName, byte[] attachment) {
        try {
            SendSmtpEmailSender sender = new SendSmtpEmailSender().email(senderEmail).name(senderName);
            SendSmtpEmailTo recipient = new SendSmtpEmailTo().email(to);
            List<SendSmtpEmailTo> toList = Collections.singletonList(recipient);

            SendSmtpEmailAttachment emailAttachment = new SendSmtpEmailAttachment()
                    .content(attachment)
                    .name(attachmentName);
            List<SendSmtpEmailAttachment> attachmentList = Collections.singletonList(emailAttachment);

            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail()
                    .sender(sender)
                    .to(toList)
                    .subject(subject)
                    .htmlContent(body)
                    .attachment(attachmentList);

            brevoApi.sendTransacEmail(sendSmtpEmail);
            log.info("Email with attachment sent to: {} via Brevo", to);
        } catch (Exception e) {
            log.error("Failed to send email with attachment to: {} via Brevo. Error: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email with attachment: " + e.getMessage(), e);
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            SendSmtpEmailSender sender = new SendSmtpEmailSender().email(senderEmail).name(senderName);
            SendSmtpEmailTo recipient = new SendSmtpEmailTo().email(to);
            List<SendSmtpEmailTo> toList = Collections.singletonList(recipient);

            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail()
                    .sender(sender)
                    .to(toList)
                    .subject(subject)
                    .htmlContent(htmlBody);

            brevoApi.sendTransacEmail(sendSmtpEmail);
            log.info("HTML email sent to: {} via Brevo", to);
        } catch (Exception e) {
            log.error("Failed to send HTML email to: {} via Brevo. Error: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send HTML email: " + e.getMessage(), e);
        }
    }
}
