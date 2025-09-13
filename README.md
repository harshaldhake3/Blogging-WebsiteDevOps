# DevOps Blog — v1.0.0

A production-grade Spring Boot blogging platform focused on DevOps content.

## Features
- User auth with roles: Admin, User (Spring Security, BCrypt)
- Create/publish posts with multiple file attachments
- Admin dashboard to manage users
- Responsive UI (Bootstrap 5), server-side rendered (Thymeleaf)
- Actuator endpoints
- H2 (local dev) and Postgres (prod) profiles
- Configurable upload directory (PVC in Kubernetes)
- Dockerfile + Kubernetes manifests (Namespace, ConfigMap, Secret, PVC, Deployments, Service, Ingress)
- Seed admin endpoint: `POST /admin/seed` (creates `admin/admin123` once)

## Quickstart (Local)
```bash
# Java 17 + Maven required
mvn spring-boot:run
# seed admin user (open once in browser or curl)
curl -XPOST http://localhost:8080/admin/seed
```

Login at `/login` → `admin / admin123`

## Local Docker
```bash
docker build -t devops-blog:v1.0.0 .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod -v $(pwd)/uploads:/data/uploads --name devops-blog devops-blog:v1.0.0
```

## Kubernetes
```bash
kubectl apply -f k8s/
# build & load image into your cluster, e.g. with kind or a registry
kubectl -n devops-blog get all
```

## Folder Structure
- `src/main/java` — code (domain, repo, service, web, config)
- `src/main/resources/templates` — Thymeleaf templates
- `src/main/resources/static` — CSS/JS
- `k8s/` — manifests for namespace, config, db, app, ingress
- `docs/` — architecture, release notes, workflow

## Default Credentials
Generated via `/admin/seed`:
- admin / admin123 (Roles: ADMIN, USER)

> Change the password ASAP in production.
