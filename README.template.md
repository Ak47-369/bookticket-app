# BookTicket: A Comprehensive Microservices Project

Welcome to BookTicket, a complete microservices-based application for booking movie tickets. This system is built using Spring Boot and the Spring Cloud ecosystem, demonstrating a wide range of patterns and technologies used in modern, cloud-native distributed systems.

This repository is structured as a monorepo, containing all platform and business logic services.

---

## 🚀 Quick Links

| Resource                      | Link                                                                                                                   |
|-------------------------------|------------------------------------------------------------------------------------------------------------------------|
| **Monorepo Repository**       | https://github.com/Ak47-369/bookticket-app                                                                             |
| **Swagger UI**                | https://api-gateway-mtiq.onrender.com/swagger-ui.html                                                                  |
| **Postman Starter Flow**      | [![Postman Collection](https://img.shields.io/badge/Postman-Import-orange)](REPLACE_WITH_POSTMAN_COLLECTION_COPY_URL)  |                                                                                                                      |
| **Postman Master Collection** | [![Postman Collection](https://img.shields.io/badge/Postman-Import-orange)](REPLACE_WITH_POSTMAN_COLLECTION_COPY_URL)  |

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


# 📦 Microservices Documentation (Auto-injected)

Each service section below is automatically injected from the microservice repositories.

👉 *Click any microservice to expand its full documentation.*

<!-- INJECT_MICROSERVICES -->

---

## Connect with me:

<p align="left">
<a href="https://www.linkedin.com/in/amitkumar47369/" target="blank">
<img align="center" src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" height="30" />
</a>

<a href="https://github.com/Ak47-369" target="blank">
<img align="center" src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" height="30" />
</a>
</p>
