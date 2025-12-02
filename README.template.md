# BookTicket: A Microservices-Based Movie Ticket Booking Application

Welcome to BookTicket! This is a personal project I built to learn and demonstrate the core principles of building a real-world, distributed application using Java, Spring Boot, and the Spring Cloud ecosystem.

The goal of this project was to move beyond a simple monolithic application and tackle the challenges of a microservices architecture, including:
-   **Service Discovery:** How do services find each other in a dynamic environment?
-   **Centralized Configuration:** How do you manage configuration for multiple services without duplicating properties?
-   **API Gateway:** How do you create a single, secure entry point for a frontend application? 
-   **Asynchronous Communication:** How do you handle tasks like sending a confirmation email without slowing down the user's booking process?



## The Component Architecture Diagram

### System Architecture Overview
<img width="2781" height="1648" alt="Overview" src="https://github.com/user-attachments/assets/c29798a9-6dd6-484c-bb12-f3988ff093da" />


## Sequence Diagram 
### End-to-End User Flow : Booking a Ticket
<img width="3820" height="1866" alt="Sequence" src="https://github.com/user-attachments/assets/cd8198be-6431-4f4b-abf8-b48a117cc8e5" />




## 🚀 Quick Links

| Resource                      | Link                                                                                                                  |
|-------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| **Monorepo Repository**       | https://github.com/Ak47-369/bookticket-app                                                                            |
| **Swagger UI**                | https://api-gateway-mtiq.onrender.com/swagger-ui.html                                                                 |
| **Postman Starter Flow**      | [![Postman Collection](https://img.shields.io/badge/Postman-Import-orange?style=for-the-badge)](https://akydv2627-762592.postman.co/workspace/Amit-yadav's-Workspace~ad787101-1fc6-4550-8c4f-690f01ef3462/flow/6925d0e3b99f1f0014c54e64)  [![Start Services](https://img.shields.io/badge/Start%20Services-Click%20Here-brightgreen?style=for-the-badge)](https://ak47-369.github.io/bookticket-app/) |                                                                                                                      |
| **Postman Master Collection** | [![Postman Collection](https://img.shields.io/badge/Postman-Import-orange?style=for-the-badge)](REPLACE_WITH_POSTMAN_COLLECTION_COPY_URL) |

---

## How to Test the Live API

1.  Navigate to the **Swagger UI**.
2.  Use the `POST /api/v1/auth/register` endpoint under the **User-Service** to create a new account.
3.  Use the `POST /api/v1/auth/login` endpoint to log in with your new credentials and get a JWT.
4.  Click the **"Authorize"** button at the top of the page and enter your token in the format: `Bearer <your_jwt_token>`.

## 🌐 Quick Project Overview

- Microservices architecture — 9 independently deployable services
- Centralized configuration + Eureka service registry
- API Gateway for routing & cross-cutting concerns
- Render deployment (free tier: services sleep after 15 minutes)
- Postman master collection for end-to-end testing



## 🧭 How Reviewers Should Test (Recommended Order)

1. **Cold-start services on Render** (free tier sleeps quickly)
2. Run the **Starter Postman Flow**.Wait 20–60 seconds for services to warm up.
3. Execute the **full master Postman collection**



