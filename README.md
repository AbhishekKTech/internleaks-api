<img width="1584" height="396" alt="internleaks-linkedin-banner github" src="https://github.com/user-attachments/assets/ec5c3e87-3f99-480d-b371-0feecbb05149" />

# ⚙️ InternLeaks - Core API & AI Engine

> **The robust Java Spring Boot backend powering the AI analysis, deep web scraping, and secure data management for InternLeaks.**

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)

This repository contains the backend engine for **InternLeaks** (`internleaks-api`). It acts as the central brain of the platform, orchestrating data between the MySQL database, the Google Gemini AI models, and the Tavily deep search API. It provides secure, JWT-authenticated RESTful APIs consumed by the Next.js frontend client.

🖥️ **Frontend Repository:** [InternLeaks Web](https://github.com/AbhishekKTech/internleaks-web)  
🌐 **Live Platform:** [internleaks.in](https://internleaks.in)

---

## ✨ Core Features & Architecture

Our backend is built with a clean, modular architecture to ensure scalability and maintainability:

- **🔐 Security & Authentication (`config` & `service`):** Custom `JwtAuthenticationFilter` and `SecurityConfiguration` ensure all endpoints are protected. Token generation and validation are handled by the dedicated `JwtService`.
- **🧠 AI Orchestration (`AiAnalysisService`):** Integrates with Google Gemini AI (`gemini-2.0-flash-lite`) with built-in retry mechanisms and fallback logic to handle rate limits gracefully.
- **🌐 Deep Web Context (`WebSearchService`):** Hooks into the Tavily Search API to scrape live internet data, reviews, and scam databases before feeding context to the AI.
- **🗄️ Data Persistence (`entity` & `repository`):** Uses Spring Data JPA to manage entities like `User`, `Company`, `Report`, and `ScamReport` in a relational MySQL database.
- **🚀 Containerized (`Dockerfile`):** Ready for seamless deployment across cloud environments using Docker.

---

## 🚀 Local Development Setup

To run the backend engine locally, follow these precise steps:

### 1. Prerequisites
- Java Development Kit (JDK) 21 installed.
- Maven installed.
- A local MySQL instance (or connection details to a cloud DB).

### 2. Installation
Clone the repository:
```bash
git clone [https://github.com/AbhishekKTech/internleaks-api.git](https://github.com/AbhishekKTech/internleaks-api.git)
cd internleaks-api
```

### 3. Environment Configuration
Navigate to `src/main/resources/application.properties` and configure your local environment variables. **Never push real API keys or passwords to the public repository.**

Properties

```
# Server Configuration
server.port=8080

# Database Configuration (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/internleaksdb?sslMode=REQUIRED
spring.datasource.username=root
spring.datasource.password=your_local_db_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# AI & Third-Party APIs
gemini.api.key=YOUR_GEMINI_API_KEY
tavily.api.key=YOUR_TAVILY_API_KEY

# JWT Configuration
jwt.secret=your_super_secret_jwt_signature_key
```

### 4. Build and Run
Compile and run the Spring Boot application using the Maven wrapper:

```
./mvnw clean install
./mvnw spring-boot:run
```

The server will start on `http://localhost:8080`.

## 🐳 Docker Setup
If you prefer running the application via Docker, you can build and run the image using the provided `Dockerfile`.

```
# Build the Docker image
docker build -t internleaks-api .

# Run the container (Make sure to pass environment variables if needed)
docker run -p 8080:8080 internleaks-api
```

## 📂 Project Structure

```
src/main/java/com/abhishekktech/internleaks/
├── config/          # JWT Filters, Security Configurations, CORS settings
├── controller/      # REST API Endpoints (Auth, Reports, Scams)
├── entity/          # Database Models (User, Company, ScamReport, etc.)
├── repository/      # Spring Data JPA Interfaces
├── service/         # Business Logic (AI Analysis, Web Search, Auth, JWT)
└── InternleaksApplication.java
```

## 🤝 Contributing Guidelines

<img width="1536" height="1024" alt="how to clone" src="https://github.com/user-attachments/assets/0fdce545-2abb-40dd-8cd7-1233f0042601" />


We welcome backend developers to help optimize our AI processing, improve API response times, and tighten security.

1. **Fork the Repository:** Click 'Fork' at the top right.
2. **Clone your Fork:** `git clone https://github.com/YOUR_USERNAME/internleaks-api.git`
3. **Create a Branch:** `git checkout -b feature/optimize-ai-call`
4. **Make Changes:** Ensure code follows standard Java conventions. Maintain the existing package structure.
5. **Test Locally:** Ensure your changes do not break existing endpoints or JWT validation.
6. **Commit & Push:** `git commit -m "refactor: improved Gemini retry logic"` and `git push origin feature/optimize-ai-call`
7. **Open a Pull Request:** Submit a PR against the `main` branch.

## 👨‍💻 Author
**Abhishek Kumar Sharma** (AbhishekKTech)

[Portfolio](https://meetabhishek.in) | [GitHub](https://github.com/AbhishekKTech) | [LinkedIn](https://www.linkedin.com/in/abhishekktech/)

## ⚠️ Disclaimer
*The InternLeaks API acts as an analytical engine. The AI-generated verdicts are based on semantic analysis and automated web scraping, and should not be considered as binding legal advice.*

