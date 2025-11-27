# 📘 BookTicket App — Microservices Ticket Booking Platform

A scalable, production-grade **microservices-based** ticket booking ecosystem built using **Spring Boot**, Spring Cloud patterns, centralized configuration, service registry, API gateway, and independent domain services.

This repository acts as a **monorepo aggregator** for all microservices.

---

## 🌐 Quick Project Overview

- Microservices architecture — 9 independently deployable services  
- Centralized configuration + Eureka service registry  
- API Gateway for routing & cross-cutting concerns  
- Render deployment (free tier: services sleep after 15 minutes)  
- Postman master collection for end-to-end testing  
- CI workflow that auto-syncs READMEs from all microservice repos  

---

## 🚀 Quick Links

| Resource | Link |
|---------|------|
| **Monorepo Repository** | https://github.com/Ak47-369/bookticket-app |
| **Postman Master Collection** | [![Postman Collection](https://img.shields.io/badge/Postman-Import-orange)](REPLACE_WITH_POSTMAN_COLLECTION_COPY_URL) |
| **Architecture Diagram** | *(Optional — add if available)* |

> 📝 Replace the placeholder above with your real Postman share link.

---

## 🧭 How Reviewers Should Test (Recommended Order)

1. **Cold-start services on Render** (free tier sleeps quickly)  
2. Run the **Starter Postman Flow**  
3. Execute the **full master Postman collection**  
4. Optionally run locally using Docker or IntelliJ

---

## 🔔 Starter Postman Flow — Cold Start Render

Before testing, run these **wake-up endpoints** (included in Postman under “Starter”):

Wait 20–60 seconds for services to warm up.

---

# 📁 Microservices Documentation  
Each microservice has its own independent repository. Click a toggle to expand the docs.

> 🔄 These sections are automatically populated via GitHub Actions.

---

## **🔹 Config Server**
<details>
<summary><strong>Click to view Config Server documentation</strong></summary>

### 📦 Repository  
👉 https://github.com/Ak47-369/bookticket-config-server  

---

<!-- INJECT: CONFIG_SERVER -->

</details>

---

## **🔹 Service Registry (Eureka)**
<details>
<summary><strong>Click to view Service Registry documentation</strong></summary>

### 📦 Repository  
👉 https://github.com/Ak47-369/bookticket-eureka-server  

---

<!-- INJECT: EUREKA_SERVER -->

</details>

---

## **🔹 API Gateway**
<details>
<summary><strong>Click to view API Gateway documentation</strong></summary>

### 📦 Repository  
👉 https://github.com/Ak47-369/bookticket-api-gateway  

---

<!-- INJECT: API_GATEWAY -->

</details>

---

## **🔹 User Service**
<details>
<summary><strong>Click to view User Service documentation</strong></summary>

### 📦 Repository  
👉 https://github.com/Ak47-369/bookticket-user-service  

---

<!-- INJECT: USER_SERVICE -->

</details>

---

## **🔹 Movie Service**
<details>
<summary><strong>Click to view Movie Service documentation</strong></summary>

### 📦 Repository  
👉 https://github.com/Ak47-369/bookticket-movie-service  

---

<!-- INJECT: MOVIE_SERVICE -->

</details>

---

## **🔹 Theater Service**
<details>
<summary><strong>Click to view Theater Service documentation</strong></summary>

### 📦 Repository  
👉 https://github.com/Ak47-369/bookticket-theater-service  

---

<!-- INJECT: THEATER_SERVICE -->

</details>

---

## **🔹 Booking Service**
<details>
<summary><strong>Click to view Booking Service documentation</strong></summary>

### 📦 Repository  
👉 https://github.com/Ak47-369/bookticket-booking-service  

---

<!-- INJECT: BOOKING_SERVICE -->

</details>

---

## **🔹 Payment Service**
<details>
<summary><strong>Click to view Payment Service documentation</strong></summary>

### 📦 Repository  
👉 https://github.com/Ak47-369/bookticket-payment-service  

---

<!-- INJECT: PAYMENT_SERVICE -->

</details>

---

## **🔹 Notification Service**
<details>
<summary><strong>Click to view Notification Service documentation</strong></summary>

### 📦 Repository  
👉 https://github.com/Ak47-369/bookticket-notification-service  

---

<!-- INJECT: NOTIFICATION_SERVICE -->

</details>

---

## ⚙️ Notes About Render Free Tier

- Services sleep after ~15 minutes  
- Expect slow cold-starts, especially for Config Server & Eureka  
- Use the **Starter Postman Flow** before running main collection  

---

## 📬 Contact / Author

**Amit Kumar**  
GitHub: https://github.com/Ak47-369  
LinkedIn: *(Add your URL)*


---

# 📌 Microservices Documentation

## 🔹 Config Server

# BookTicket :: Platform :: Config Server

## Overview

The **Config Server** is a foundational component of the BookTicket microservices ecosystem. It provides centralized, externalized configuration management for all services. By acting as a single source of truth, it ensures that all microservices run with consistent and correct properties across different deployment environments.

This service uses Spring Cloud Config Server.

## Core Responsibilities

-   **Centralized Configuration:** Serves configuration properties to all other microservices from a central location.
-   **Environment Abstraction:** Manages different configuration profiles (e.g., `dev`, `prod`) for each service.
-   **Versioning and Auditing:** Leverages a Git backend to provide a complete history of all configuration changes.

## Architecture


<img width="1119" height="784" alt="Config-Server-Architecture" src="https://github.com/user-attachments/assets/2809fa3c-ad5d-44b0-81ad-4f59fe0826af" />


### How It Works

1.  **Backend Storage:** All configuration files (`.yaml`) are stored in a dedicated Git repository. This provides version control, history, and an auditable trail for every configuration change.
2.  **Client Bootstrap:** When a microservice (e.g., `user-service`) starts up, it makes a request to the Config Server, identifying itself by its application name and active profile (e.g., `user-service`, `prod`).
3.  **Serving Configuration:** The Config Server reads the appropriate files from the Git repository (`application.yaml` and `user-service-prod.yaml`), combines the properties, and sends them back to the client service.
4.  **Standalone Operation:** The Config Server is a foundational service and does **not** register with the Eureka Service Registry. This is a deliberate design choice to prevent circular dependencies during application startup.

## Configuration

The server is configured in its `application.yaml` file. Key properties include:

-   `spring.cloud.config.server.git.uri`: The URI of the Git repository containing the configuration files.
-   `server.port`: The port on which the Config Server listens (default: `8888`).
-   `eureka.client.enabled: false`: Explicitly disables Eureka client behavior.
-   `spring.cloud.config.enabled: false`: Prevents the server from trying to connect to itself for configuration.

## Endpoints

The Config Server exposes a REST API for clients to fetch their configuration. You can inspect the configuration for a service using the following URL structure:

`/{application}/{profile}`

-   **Example:** To see the production configuration for the `api-gateway`, you would access:
    `https://config-server-f2t3.onrender.com/api-gateway/prod`

---

## 🔹 Service Registry

# BookTicket :: Platform :: Eureka Server (Service Registry)

## Overview

The **Eureka Server** is the service registry for the BookTicket microservices ecosystem. It functions as a dynamic "phone book," allowing services to locate and communicate with each other without hardcoding network locations. This is a critical component for enabling resilience, scalability, and client-side load balancing in a distributed system.

This service is an implementation of **Spring Cloud Netflix Eureka Server**.

## Core Responsibilities

-   **Service Registration:** Listens for new microservice instances to come online and registers their network location (IP address and port).
-   **Service Discovery:** Provides an API for other services to query and discover the network locations of the services they need to communicate with.
-   **Health Monitoring:** Tracks the health of each registered service instance via periodic heartbeats. If an instance fails to send a heartbeat, it is removed from the registry.

## Architecture
<img width="1953" height="780" alt="Eureka-Server" src="https://github.com/user-attachments/assets/49fa5c72-78de-4a44-9545-40643154c361" />


### How It Works

1.  **Registration:** When a microservice (e.g., `user-service`) starts, its embedded Eureka client sends a registration request to the Eureka Server, providing its service name, IP address, and port.
2.  **Heartbeats:** The client then sends a heartbeat to the server every 30 seconds to confirm it is still alive and healthy. If the server doesn't receive a heartbeat for 90 seconds, it removes the instance from its registry.
3.  **Discovery:** When another service (e.g., the `api-gateway`) needs to communicate with the `user-service`, it asks the Eureka Server for a list of all healthy instances of `user-service`.
4.  **Client-Side Load Balancing:** The API Gateway's built-in load balancer receives this list of IP addresses and intelligently chooses one to forward the request to. If the request fails, it can try another instance from the list.
5.  **Resilience:** All clients maintain a local cache of the Eureka registry. If the Eureka Server becomes temporarily unavailable, clients can continue to function using their cached information.

## Configuration

The Eureka Server is configured via properties fetched from the **Config Server**. Key properties in its `eureka-server-prod.yaml` file include:

-   `eureka.client.register-with-eureka: false`: Prevents the server from trying to register with itself.
-   `eureka.client.fetch-registry: false`: Prevents the server from trying to fetch a registry from a peer. In a multi-node cluster, this would be `true`.
-   `server.port`: The port on which the Eureka Server UI and API are available (default: `8761`).

## Eureka Dashboard

The Eureka Server provides a web dashboard to visualize the status of all registered microservices. It is accessible at the root URL of the service:

-   **URL:** `https://service-registry-a48d.onrender.com/`

---

## 🔹 API Gateway

# BookTicket :: Platform :: API Gateway

## Overview

The **API Gateway** is the primary entry point for all external traffic into the BookTicket microservices ecosystem. It serves as a reverse proxy, routing client requests to the appropriate internal services. It is also responsible for handling critical cross-cutting concerns, providing a secure, reliable, and unified interface for our frontend applications.

This service is built using **Spring Cloud Gateway**.

## Core Responsibilities

-   **Request Routing:** Intelligently routes incoming HTTP requests to the correct downstream microservice based on path predicates.
-   **Service Discovery Integration:** Dynamically discovers service locations by integrating with the Eureka Service Registry, enabling resilience and scalability.
-   **Centralized Authentication:** Validates JWTs for all secured endpoints, enriching requests with user context before forwarding them.
-   **Rate Limiting:** Protects the system from abuse by enforcing rate limits on incoming requests using a Redis-backed token bucket algorithm.
-   **Unified API Documentation:** Aggregates OpenAPI (Swagger) documentation from all downstream services into a single, convenient UI.

## Architecture & Request Flow

<img width="500" height="1200" alt="API-Gateway-Flow" src="https://github.com/user-attachments/assets/8f7c6825-292a-4169-a3dd-b6ef3a8b59a7" />


### How It Works

1.  **Dynamic Routing:** The gateway uses route definitions from the `api-gateway-prod.yaml` file (managed by the Config Server). It uses the `lb://` scheme to look up service locations from Eureka, allowing for dynamic scaling and resilience.
2.  **Global Filters:** All incoming requests pass through a chain of `GlobalFilter` implementations:
    -   **`RateLimitingFilter`:** Checks if the client (identified by IP or user ID) has exceeded their allowed request rate.
    -   **`AuthenticationFilter`:** For protected routes, this filter validates the JWT from the `Authorization` header. If valid, it adds `X-User-ID`, `X-User-Name`, and `X-User-Roles` headers to the request for downstream services to consume.
3.  **Load Balancing:** When routing to a service, the gateway's built-in Spring Cloud LoadBalancer chooses a healthy instance from the list provided by Eureka.

## Key Dependencies

-   **Spring Cloud Gateway:** The core non-blocking, reactive gateway framework.
-   **Eureka Discovery Client:** For integrating with the service registry.
-   **Spring Boot Starter Data Redis Reactive:** Used for the Redis-backed rate limiting implementation.
-   **JJWT (Java JWT):** For parsing and validating JSON Web Tokens.
-   **SpringDoc OpenAPI WebFlux UI:** To aggregate and display Swagger documentation from all microservices.

## Configuration

The gateway's configuration, including its routes, is managed by the **Config Server**. Key properties are defined in `api-gateway-prod.yaml`.

-   **Routes:** Defined under `spring.cloud.gateway.routes`, mapping URL paths to service IDs (e.g., `lb://user-service`).
-   **Documentation:** The `springdoc.swagger-ui.urls` property is used to define the list of microservices to include in the combined documentation view.

---

## 🔹 User Service

# BookTicket :: Services :: User Service

## Overview

The **User Service** is a core microservice responsible for managing all aspects of user identity, authentication, and authorization within the BookTicket platform. It serves as the single source of truth for user data.

## Core Responsibilities

-   **User Registration:** Handles the creation of new user accounts.
-   **Authentication:** Manages user login and, upon successful authentication, generates and signs JSON Web Tokens (JWTs).
-   **Password Management:** Securely stores user passwords using strong hashing algorithms (BCrypt).
-   **User Profile Management:** Provides endpoints for users to view and manage their own profile information.

## Architecture
<img width="1473" height="1642" alt="User Service-2025-11-26-191334" src="https://github.com/user-attachments/assets/b3322aed-a237-40f5-af9a-15f2cbfe6c57" />


### How It Works

1.  **Data Storage:** User information is stored in a **PostgreSQL** database to ensure data integrity and consistency. Passwords are never stored in plain text; they are hashed using the **BCrypt** algorithm provided by Spring Security.
2.  **Authentication Flow:**
    -   A user sends their credentials to the `/api/v1/auth/login` endpoint, which is routed through the API Gateway to the User Service.
    -   The User Service validates the credentials against the hashed password in the database.
    -   If valid, it generates a signed **JWT** containing the user's ID, username, and roles. This token is returned to the client.
3.  **Stateless Authorization:** For all subsequent requests, the client includes the JWT in the `Authorization` header. The API Gateway validates this token without needing to contact the User Service, enabling a highly scalable and stateless security model.
4.  **Service Discovery:** The User Service registers itself with the **Eureka Service Registry**, allowing other services like the API Gateway to discover its location dynamically.

## Key Dependencies

-   **Spring Boot Starter Web:** For building the REST APIs.
-   **Spring Boot Starter Data JPA:** For database interaction using PostgreSQL.
-   **Spring Boot Starter Security:** For authentication, authorization, and password hashing.
-   **Eureka Discovery Client:** To register with the service registry.
-   **Java JWT (jjwt):** For creating and signing JSON Web Tokens.
-   **SpringDoc OpenAPI:** For generating API documentation.

## API Endpoints

The service's endpoints are exposed through the API Gateway.

### Public Authentication Endpoints
These endpoints are used for user registration and login and are publicly accessible.

-   `POST /api/v1/auth/register`: Creates a new user account.
-   `POST /api/v1/auth/login`: Authenticates a user with their email and password and returns a JWT upon success.

### Secured User Endpoints
These endpoints require a valid JWT in the `Authorization` header for access.

-   `GET /api/v1/users/me`: Retrieves the profile information for the currently authenticated user.
-   `GET /api/v1/users/{id}`: Retrieves the public profile information for any user by their ID.
-   `GET /api/v1/users`: Fetches a list of all users. (Requires `ADMIN` role).
-   `DELETE /api/v1/users/{id}`: Deletes a user account. (Requires `ADMIN` role).

---

## 🔹 Movie Service

# BookTicket :: Services :: Movie Service

## Overview

The **Movie Service** is a core business microservice that acts as the central catalog for all movie-related information on the BookTicket platform. It manages movie details, genres, cast, posters, and other descriptive data.

## Core Responsibilities

-   **Movie Catalog Management:** Provides CRUD (Create, Read, Update, Delete) operations for the movie catalog.
-   **Data Source:** Acts as the single source of truth for all descriptive information about movies.
-   **Search and Query:** Exposes endpoints to allow clients to search and filter the movie catalog.

## Architecture
<img width="1656" height="1052" alt="Movie-Service" src="https://github.com/user-attachments/assets/1d65e4db-99c0-47b6-9f01-1ef518a53ce6" />


### How It Works

1.  **Data Storage:** The service uses a **MongoDB** database to store movie information. The flexible, document-based nature of MongoDB is a natural fit for the rich and sometimes varied structure of movie data, allowing for easy updates to the data model without rigid schema migrations.
2.  **Authorization:** The service is protected by Spring Security. While authentication is handled by the API Gateway, the Movie Service performs its own authorization. It inspects trusted headers (e.g., `X-User-Roles`) passed from the gateway to determine if a user has the required permissions (e.g., `ADMIN`) to perform sensitive operations like adding or deleting a movie.
3.  **Service Discovery:** The Movie Service registers itself with the **Eureka Service Registry**, making it discoverable by other components in the system, primarily the API Gateway.

## Key Dependencies

-   **Spring Boot Starter Web:** For building the REST APIs.
-   **Spring Boot Starter Data MongoDB:** For interacting with the MongoDB database.
-   **Spring Boot Starter Security:** For handling role-based authorization.
-   **Eureka Discovery Client:** To register with the service registry.
-   **SpringDoc OpenAPI:** For generating API documentation.

## API Endpoints

The service's endpoints are exposed through the API Gateway under the `/api/v1/movies/**` path. All endpoints require a valid JWT for authentication.

-   `POST /api/v1/movies`: Adds a new movie to the catalog. This is a protected endpoint that requires the user to have either an `ADMIN` or `THEATER_OWNER` role.
-   `GET /api/v1/movies`: Retrieves a list of all movies available in the system.
-   `GET /api/v1/movies/{id}`: Fetches detailed information for a single movie by its unique ID.
-   `GET /api/v1/movies/now-playing`: A search endpoint that finds all movies currently playing in a specific city, based on show data from the Theater Service. This is a key endpoint for the user-facing application.

---

## 🔹 Theater Service

# BookTicket :: Services :: Theater Service

## Overview

The **Theater Service** is the operational core of the BookTicket platform, responsible for managing the physical locations, schedules, and inventory for movie screenings. It bridges the gap between the movie catalog and the actual booking process.

## Core Responsibilities

-   **Theater Management:** Manages information about theater locations, including names, addresses, and the screens they contain.
-   **Screen Management:** Manages the details of individual screens within a theater, including their seating layouts.
-   **Showtime Scheduling:** Manages the schedule of which movies are playing on which screens at what times.
-   **Seat Inventory:** Tracks the availability of seats for each specific show, which is critical for the booking process.

## Architecture

<img width="1266" height="1010" alt="Theater Service" src="https://github.com/user-attachments/assets/9b908de1-d5c3-4c25-97c7-a5146fb7c359" />


### How It Works

1.  **Data Storage:** The service uses a **PostgreSQL** database. The highly relational nature of theaters, screens, shows, and seats makes a traditional RDBMS the ideal choice to enforce data integrity and handle transactional operations like seat management.
2.  **Decoupled Domain:** The Theater Service is intentionally decoupled from the `Movie Service`. It does not store any movie metadata (like titles or posters). Instead, it references movies only by their unique `movieId`. This follows the Single Source of Truth principle.
3.  **Client-Side Composition:** A client application looking to display showtimes for a movie will first call the `Movie Service` to get movie details, and then call the `Theater Service` with the `movieId` to get a list of showtimes.
4.  **Service Discovery:** The Theater Service registers itself with the **Eureka Service Registry**, allowing other services like the API Gateway and Booking Service to discover and communicate with it dynamically.

## Key Dependencies

-   **Spring Boot Starter Web:** For building the REST APIs.
-   **Spring Boot Starter Data JPA:** For database interaction using PostgreSQL.
-   **Spring Boot Starter Security:** For handling role-based authorization on its endpoints.
-   **Eureka Discovery Client:** To register with the service registry.
-   **SpringDoc OpenAPI:** For generating API documentation.

## API Endpoints

The service's endpoints are exposed through the API Gateway. They provide comprehensive access to theater, screen, and show information.

### Theater Endpoints
-   `POST /api/v1/theaters`: Adds a new theater to the system. (Requires `ADMIN` role).
-   `GET /api/v1/theaters`: Retrieves a list of all theaters, with optional filtering by `city`.
-   `GET /api/v1/theaters/{id}`: Fetches detailed information for a single theater by its ID.
-   `GET /api/v1/theaters/{id}/shows`: Retrieves all shows currently scheduled for a specific theater.

### Screen Endpoints
-   `POST /api/v1/screens`: Adds a new screen to a specified theater. (Requires `ADMIN` or `THEATER_OWNER` role).
-   `GET /api/v1/screens/{id}`: Fetches detailed information for a single screen, including its seating arrangement.

### Show Endpoints
-   `POST /api/v1/shows`: Schedules a new show for a movie on a specific screen. (Requires `ADMIN` or `THEATER_OWNER` role).
-   `GET /api/v1/shows`: A powerful search endpoint to find shows. Can be filtered by `movieId`, `city`, and `showDate`.
-   `GET /api/v1/shows/{id}`: Fetches detailed information for a single show, including the availability of all its seats.

### Internal Endpoints
*(These are intended for service-to-service communication and are not exposed on the API Gateway)*
-   `POST /api/v1/internal/shows/verify-seats`: Used by the `Booking Service` to verify that a list of seats for a show are valid and available before proceeding with a booking.
-   `POST /api/v1/internal/shows/lock-seats`: Used by the `Booking Service` to mark seats as temporarily locked during the booking process.
-   `POST /api/v1/internal/shows/book-seats`: Used by the `Booking Service` to permanently mark seats as booked after a successful payment.

---

## 🔹 Booking Service

# BookTicket :: Services :: Booking Service

## Overview

The **Booking Service** is the transactional engine of the BookTicket platform. It orchestrates the complex, multi-step process of reserving seats and creating a confirmed booking. It acts as a central coordinator, interacting with multiple other services to ensure a booking is processed reliably and consistently.

## Core Responsibilities

-   **Booking Orchestration:** Manages the end-to-end lifecycle of a booking, from `PENDING` to `CONFIRMED` or `FAILED`.
-   **Seat Reservation:** Implements a concurrency-safe mechanism to temporarily lock seats while a user completes payment.
-   **Payment Initiation:** Coordinates with the `Payment Service` to create and process payments.
-   **Asynchronous Notifications:** Publishes events to a message queue (Kafka) to trigger downstream processes, such as sending confirmation emails, without blocking the core booking flow.

## Architecture & Saga Orchestration

The booking process is a classic example of a **Saga pattern**, orchestrated by this service.
<img width="3146" height="1998" alt="Booking Service" src="https://github.com/user-attachments/assets/cc6c8a91-8a4c-423c-bdb7-67c7f0f1265e" />


### How It Works

1.  **Seat Locking:** To prevent double-bookings, the service uses a **distributed lock in Redis**. When a user selects seats, a lock with a short TTL (e.g., 5 minutes) is acquired. This provides a high-performance, scalable solution for managing concurrency.
2.  **Payment Orchestration:** The service makes a synchronous call to the `Payment Service` to create a payment intent. The result of the payment is communicated back asynchronously via a webhook or message queue.
3.  **Asynchronous Events:** Upon successful confirmation, the service publishes a `BookingConfirmationEvent` to an **Apache Kafka** topic. This decouples the booking process from downstream actions like sending notifications. If the `Notification Service` is temporarily unavailable, the booking can still be successfully confirmed, and the notification will be sent later.
4.  **Data Storage:** Booking records are stored in a **PostgreSQL** database to ensure the highest level of transactional integrity and data consistency.

## Key Dependencies

-   **Spring Boot Starter Data JPA:** For database interaction with PostgreSQL.
-   **Spring Boot Starter Data Redis Reactive:** For implementing distributed locking.
-   **Spring Kafka:** For publishing asynchronous events.
-   **Spring Retry:** Used to handle transient failures when communicating with other services.
-   **Eureka Discovery Client:** To discover and communicate with other services like `Payment Service` and `Theater Service`.

## API Endpoints

The service's endpoints are exposed through the API Gateway and are secured, requiring a valid JWT.

-   `POST /api/v1/bookings`: The primary endpoint to create a new booking. The request body should contain the `showId` and a list of `seatIds`. This initiates the seat-locking and payment-creation process.
-   `GET /api/v1/bookings/{bookingId}/verify-payment`: Poll the payment status from Payment Service, and update the booking status accordingly.
<!-- -   `GET /api/v1/bookings/verify`: The endpoint the user is redirected back to after completing the payment flow on Stripe. It takes `bookingId` and `sessionId` as query parameters to verify and finalize the booking. -->
-   `GET /api/v1/bookings/{id}`: Fetches the complete details of a specific booking by its ID.
-   `GET /api/v1/bookings/{id}/seats`: Fetches the specific seat details (number, type, price) associated with a particular booking.

---

## 🔹 Payment Service

# BookTicket :: Services :: Payment Service

## Overview

The **Payment Service** is a specialized microservice that acts as a secure facade and anti-corruption layer for all interactions with our third-party payment provider, Stripe. Its sole responsibility is to handle payment intent creation, provide a mechanism to verify payment status, and maintain a local, auditable record of every transaction.

## Core Responsibilities

-   **Payment Intent Creation:** Provides an internal API for other services (like the Booking Service) to request the creation of a new Stripe Checkout Session.
-   **Encapsulation of Stripe SDK:** It is the only service in the system that contains the Stripe SDK and API keys, creating a strong security boundary.
-   **Payment Status Verification:** Exposes an endpoint that allows the `Booking Service` to poll for the status of a payment after the user has been redirected to Stripe.
-   **Transactional Record Keeping:** Persists a `Payment` entity for every transaction attempt, providing a local audit trail and decoupling the system from relying solely on Stripe's API for historical data.

## Architecture & Communication Flow

This service is part of a **synchronous polling** model for payment verification, which is a robust pattern for confirming payment status when a webhook is not used.
<img width="3425" height="2302" alt="PaymentService" src="https://github.com/user-attachments/assets/4a5f5946-2a76-4106-92d2-11b429099bd8" />


### How It Works

1.  **Isolation:** This service is the only component that communicates directly with Stripe. This isolates sensitive API keys and decouples the rest of our platform from a specific payment provider implementation.
2.  **Transactional Records:** When a payment is initiated, a `Payment` record is immediately created in the service's own **PostgreSQL** database with a `PENDING` status. This provides an immediate, local audit trail for every attempted transaction.
3.  **Polling for Verification:**
    -   Payment creation is a **synchronous** REST call from the `Booking Service`.
    -   After the user is redirected back from Stripe, the `Booking Service` initiates a **polling sequence**. It repeatedly calls the `GET /api/v1/internal/payments/checkout/verify/{sessionId}` endpoint on this service.
    -   The Payment Service calls the Stripe API to get the latest status. It then updates its local `Payment` record to `COMPLETED` or `FAILED` and returns the final status to the Booking Service.
4.  **Security:** All interactions are internal (service-to-service), preventing direct external access to the payment creation logic.

## Key Dependencies

-   **Spring Boot Starter Web:** For building the REST APIs.
-   **Spring Boot Starter Data JPA:** For database interaction with PostgreSQL.
-   **Stripe Java SDK (`stripe-java`):** The official library for interacting with the Stripe API.
-   **Eureka Discovery Client:** To register with the service registry.

## API Endpoints

All endpoints are for internal, service-to-service communication and are not exposed on the public API Gateway.

-   `POST /api/v1/internal/payments/checkout/create`: Creates a new Stripe Checkout Session and a corresponding `Payment` record.
-   `GET /api/v1/internal/payments/checkout/verify/{sessionId}`: Verifies the current status of a Checkout Session with Stripe and updates the local `Payment` record.
-   `GET /api/v1/internal/payments/status/{transactionId}`: Retrieves the last known status of a payment from the service's local database.
  
### Admin-Only DLQ Endpoints
These endpoints are exposed through the API Gateway and require the `ADMIN` role for access. They are used to monitor and manage booking events that have failed to be published to Kafka.

-   `GET /api/v1/admin/booking-dlq/pending`: Retrieves a list of all booking events in the DLQ that are currently in `PENDING` status, awaiting an automatic retry.
-   `GET /api/v1/admin/booking-dlq/failed`: Retrieves a list of booking events that have exhausted all retry attempts and are now in a terminal `FAILED` state.
-   `POST /api/v1/admin/booking-dlq/{eventId}/mark-processed`: An administrative action to manually mark a failed event as `PROCESSED`, removing it from the queue.
-   `GET /api/v1/admin/booking-dlq/stats`: Provides high-level statistics for the DLQ, including the total counts of pending and failed events.
-   `GET /api/v1/admin/booking-dlq/booking/{bookingId}`: Fetches a detailed list and status summary of all failed events associated with a specific booking ID.

---

## 🔹 Notification Service

# BookTicket :: Services :: Notification Service

## Overview

The **Notification Service** is an asynchronous, event-driven microservice responsible for handling all user-facing communications. It is designed to be highly reliable and decoupled from the core business logic, ensuring that notifications are sent without impacting critical user flows.

## Core Responsibilities

-   **Asynchronous Event Consumption:** Listens to Apache Kafka topics for events published by other services (e.g., `BookingConfirmationEvent`).
-   **Email Generation & Sending:** Uses email templates (Thymeleaf) and a third-party API (Brevo) to construct and send rich HTML emails.
-   **PDF & QR Code Generation:** Generates PDF e-tickets, complete with a unique QR code for validation, and attaches them to confirmation emails.
-   **Resilient Error Handling:** Implements a robust retry and Dead Letter Queue (DLQ) mechanism to ensure that failed notifications are never lost and can be reprocessed.

## Architecture & Asynchronous Flow
<img width="800" height="1000" alt="Notification-Service" src="https://github.com/user-attachments/assets/a352ed91-4834-425f-9050-481b4e0af9df" />



### How It Works

1.  **Event-Driven:** The service is completely decoupled from upstream services. It does not expose a REST API for sending notifications. Instead, it subscribes to Kafka topics and reacts to business events.
2.  **Third-Party API for Email:** To ensure high deliverability and bypass cloud provider SMTP blocks, the service uses the **Brevo** (formerly Sendinblue) HTTP API to send emails. This is a modern, reliable approach for cloud-native applications.
3.  **Dead Letter Queue (DLQ):** If a notification fails to send after several retries (e.g., due to an external API outage), the message is not lost. It is forwarded to a dedicated DLQ topic in Kafka. A scheduled job periodically attempts to re-process messages from the DLQ, providing a high degree of fault tolerance.
4.  **Template-Based Content:** Email content is generated using **Thymeleaf** templates, allowing the design and layout of emails to be managed independently of the application logic.

## Key Dependencies

-   **Spring Kafka:** For consuming messages from Apache Kafka topics.
-   **Brevo Java SDK:** The official library for interacting with the Brevo email API.
-   **Spring Boot Starter Thymeleaf:** For processing HTML email templates.
-   **OpenPDF & ZXing:** For generating PDF tickets and QR codes.
-   **Spring Retry:** To handle transient failures when sending emails.
-   **Eureka Discovery Client:** To register with the service registry.

## Configuration

The service is configured via properties from the **Config Server**. Key properties include:

-   `kafka.bootstrap-servers`: The address of the Kafka cluster.
-   `brevo.api.key`: The API key for authenticating with the Brevo service.
-   `spring.mail.username`: The "from" address used in outgoing emails.
-   `management.health.mail.enabled: false`: Disables the default mail health check to prevent timeouts in a cloud environment.

## API Endpoints

This service is primarily event-driven and does not expose public endpoints for creating notifications. However, it does expose administrative endpoints for observability and management of the Dead Letter Queue (DLQ).

## API Endpoints

This service is primarily event-driven and does not expose public endpoints for creating notifications. However, it does expose administrative endpoints for observability and management of the Dead Letter Queue (DLQ).

### Admin-Only DLQ Endpoints
These endpoints are exposed through the API Gateway and require the `ADMIN` role for access. They are crucial for monitoring and managing the health of the notification system.

-   `GET /api/v1/admin/notification-dlq/pending`: Retrieves a list of all notifications in the DLQ that are currently in `PENDING` status, awaiting an automatic retry from the scheduled job.
-   `GET /api/v1/admin/notification-dlq/failed`: Retrieves a list of notifications that have exhausted all retry attempts and are now in a terminal `FAILED` state, requiring manual investigation.
-   `POST /api/v1/admin/notification-dlq/{notificationId}/mark-processed`: An administrative action to manually mark a failed notification as `PROCESSED`. This is used to clear an item from the queue after the issue has been handled externally (e.g., the email was sent manually).
-   `GET /api/v1/admin/notification-dlq/stats`: Provides high-level statistics for the DLQ, including the total counts of pending and failed notifications.
-   `GET /api/v1/admin/notification-dlq/booking/{bookingId}`: Fetches a detailed list and status summary of all failed notifications associated with a specific booking ID. This is very useful for debugging a specific user's issue.

---

