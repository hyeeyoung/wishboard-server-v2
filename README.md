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
- [Development Environment](#development-environment)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [ERD](#erd)
- [Architecture](#architecture)
- [Directory Structure](#directory-structure)

<!-- - [Contributors](#contributors) --> <!-- Placeholder, can be added if info is available -->


<br>

## About wishboard-server-v2

This project is the backend server for the Wishboard application, a platform for users to create and manage their
wishlists. It handles user authentication, data storage, and provides APIs for the frontend application.
The wishboard application is available
in [google store](https://play.google.com/store/apps/details?id=com.hyeeyoung.wishboard&hl=ko)
and [app store](https://apps.apple.com/kr/app/%EC%9C%84%EC%8B%9C%EB%B3%B4%EB%93%9C-wish-board/id6443808936).

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

## Configuration

Application settings (database connections, AWS credentials, Redis host/port, etc.) are managed via Spring Boot's
property files:

- `src/main/resources/application.yml`

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
│   │   │   ├── auth/
│   │   │   ├── cart/
│   │   │   ├── common/
│   │   │   ├── config/
│   │   │   ├── folder/
│   │   │   ├── image/
│   │   │   ├── item/
│   │   │   ├── notifications/
│   │   │   ├── image/
│   │   │   ├── user/
│   │   │   └── version/
│   │   └── resources/
│   │       ├── messages/
│   │       ├── sql/
│   │       ├── logback-spring.xml
│   │       └── application.yaml
│   └── test/
│       └── java/com/wishboard/server/  # Unit and integration tests
...
```

*(This is a simplified representation. Refer to the `ls()` command output for actual structure if needed for more
detail, or generate using a tool like 'tree')*

<br>
