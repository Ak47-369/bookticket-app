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

