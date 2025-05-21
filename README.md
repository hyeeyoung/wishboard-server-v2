# wishboard-server-v2

<!-- Optional: Add a logo/image here if available -->
<!-- <img width="150" alt="app_logo" src="path_to_logo.svg"> -->

<br>

**A backend server for managing wishlists.** <!-- Brief slogan/description -->

<!-- Optional: Timeline / Team, if applicable -->
<!-- Timeline: YYYY.MM.DD ~ -->
<!-- TEAM: Project Team / Your Name -->

<br>

## Contents
- [About wishboard-server-v2](#about-wishboard-server-v2)
- [Features](#features)
- [Development Environment](#development-environment)
- [API Documentation](#api-documentation)
- [Setup and Installation](#setup-and-installation)
- [Configuration](#configuration)
- [ERD](#erd)
- [Architecture](#architecture)
- [Key Dependencies](#key-dependencies)
- [Directory Structure](#directory-structure)
<!-- - [Contributors](#contributors) --> <!-- Placeholder, can be added if info is available -->


<br>

## About wishboard-server-v2
This project is the backend server for the Wishboard application, a platform for users to create and manage their wishlists. It handles user authentication, data storage, and provides APIs for the frontend application.

<br>

## Features
| Category         | Detailed Function                                  | Status    |
| ---------------- | -------------------------------------------------- | --------- |
| `Authentication` | User registration and login                        | ✔         |
| `Authentication` | Secure password handling                           | ✔         |
| `Wishlists`      | CRUD operations for wishlists                      | ✔         |
| `Items`          | CRUD operations for items in wishlists             | ✔         |
| `Items`          | Image uploading for items (via AWS S3)             | ✔         |
| `API`            | RESTful APIs for client-server communication       | ✔         |
| `API`            | API documentation via Swagger UI                   | ✔         |
| `Performance`    | Caching with Redis and Caffeine                    | ✔         |
| `Monitoring`     | Error tracking with Sentry                         | ✔         |
<!-- Add more features as they are developed -->

<br>

## Development Environment
- **Core Stack:**
  - Java 21
  - Spring Boot 3.3.3
  - Gradle
- **Database & Cache:**
  - MySQL
  - Redis
- **Cloud Services:**
  - AWS S3 (for file storage)
- **API & Documentation:**
  - Swagger (OpenAPI)
- **DevOps & Monitoring:**
  - Docker
  - Sentry
  - Spring Boot Actuator
  - Micrometer (Prometheus)

<br>

## API Documentation
API documentation is generated using Swagger (OpenAPI).
Once the application is running, it can typically be accessed at:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
(Adjust the port if your application runs on a different one.)

<br>

## Setup and Installation

### Prerequisites
- Java JDK 21 or later
- Gradle (the wrapper `./gradlew` is included)
- MySQL server
- Redis server
- AWS S3 bucket and credentials

### Building the project
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd wishboard-server-v2
   ```
2. Configure the application (see [Configuration](#configuration) section below).
3. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```

### Running the application
- **Using Gradle:**
  ```bash
  ./gradlew bootRun
  ```
- **Using Docker:**
  The project includes a `docker-compose.yml`.
  ```bash
  docker-compose up -d
  ```
  Ensure environment variables for configuration are set, possibly in a `.env` file or directly in `docker-compose.yml`.

<br>

## Configuration
Application settings (database connections, AWS credentials, Redis host/port, etc.) are managed via Spring Boot's property files:
- `src/main/resources/application.properties` or
- `src/main/resources/application.yml`

You may need to create/copy one from a template or environment-specific example.
Environment variables can also be used to override these properties, which is common in containerized deployments.

<br>

## ERD
<!-- Placeholder for ERD image or description -->
<!-- e.g., <img width="800" alt="ERD" src="path_to_erd_image.png"> -->
Details about the database schema and entity relationships will be documented here.

<br>

## Architecture
<!-- Placeholder for Architecture diagram or description -->
<!-- e.g., <img width="800" alt="Architecture Diagram" src="path_to_architecture_diagram.png"> -->
An overview of the system architecture, components, and their interactions will be provided here.

<br>

## Key Dependencies
- **Spring Boot Starters:** `web`, `security`, `validation`, `data-jpa`, `data-redis`, `cache`, `actuator`
- **Database:** `mysql-connector-j`, `querydsl-jpa`
- **AWS:** `spring-cloud-starter-aws` (for S3)
- **API & Docs:** `springdoc-openapi-starter-webmvc-ui`
- **Authentication:** `jjwt` (JSON Web Token)
- **Utilities:** `lombok`, `modelmapper`
- **Monitoring:** `sentry-spring-boot-starter-jakarta`, `micrometer-registry-prometheus`
- **Email:** `spring-boot-starter-mail`

*(Refer to `build.gradle` for a complete list of dependencies.)*

<br>

## Directory Structure
<!-- Placeholder for Directory Structure -->
A high-level overview of the project's directory structure:
```
wishboard-server-v2/
├── build.gradle
├── gradlew
├── docker-compose.yml
├── src/
│   ├── main/
│   │   ├── java/com/wishboard/server/  # Main application code
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── domain/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── service/
│   │   │   └── repository/
│   │   └── resources/
│   │       ├── application.properties  # Or application.yml
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/com/wishboard/server/  # Unit and integration tests
...
```
*(This is a simplified representation. Refer to the `ls()` command output for actual structure if needed for more detail, or generate using a tool like 'tree')*

<br>