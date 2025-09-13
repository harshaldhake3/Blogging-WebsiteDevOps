# System Architecture

```mermaid
flowchart LR
  subgraph Client
    Browser
  end
  Browser --> Ingress
  Ingress --> SVC[Service: devops-blog]
  SVC --> APP[Deployment: devops-blog (Spring Boot)]
  APP --> PVC[(PVC: uploads)]
  APP --> PG[Service: postgres]
  PG --> PDB[(Postgres Data)]
```
