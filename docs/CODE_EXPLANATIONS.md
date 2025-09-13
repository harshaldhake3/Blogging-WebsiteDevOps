# Code Explanations

- **Domain**: `User`, `Post`, `Attachment` JPA entities.
- **Repository**: Spring Data JPA repositories for CRUD.
- **Service**: `PostService` handles business logic and file storage under `app.upload.dir`.
- **Web**: Controllers for Home, Post CRUD, and Admin operations.
- **Security**: `SecurityConfig` sets URL access, login page, and password encoding (BCrypt).
- **Templates**: Thymeleaf pages styled with Bootstrap 5.
- **Profiles**: `application.yml` includes default (H2) and `prod` (Postgres).
- **Observability**: Spring Boot Actuator for health/metrics.
