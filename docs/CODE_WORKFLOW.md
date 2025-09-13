# Code Workflow

1. **Branching**: `main` (stable), `develop` (integration), feature branches `feature/*`.
2. **Versioning**: Semantic Versioning (MAJOR.MINOR.PATCH); current: v1.0.0.
3. **CI pipeline** (suggested):
   - Build with Maven, run unit tests.
   - Package JAR and build Docker image `devops-blog:v1.0.0`.
   - Push to registry.
   - Apply K8s with `kubectl apply -f k8s/` (with proper image reference).
4. **Release**:
   - Tag repo with version.
   - Update `docs/RELEASE_NOTES.md`.
