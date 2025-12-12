# AWS Deployment Plan

## Architecture Overview

```mermaid
graph TB
    subgraph "Client"
        USER[End Users]
    end

    subgraph "AWS Cloud"
        subgraph "Frontend - Static Hosting"
            S3[S3 Bucket<br/>Vue.js SPA]
            CF[CloudFront CDN]
        end

        subgraph "Load Balancing"
            ALB[Application Load Balancer<br/>Port 443/80]
        end

        subgraph "Container Orchestration - ECS Cluster"
            ECS_SERVICE[ECS Service<br/>Auto Scaling]
            TASK1[Task: Spring Boot<br/>Container 1]
            TASK2[Task: Spring Boot<br/>Container 2]
            TASK3[Task: Spring Boot<br/>Container N]
        end

        subgraph "Container Registry"
            ECR[ECR Repository<br/>customer-workbench-backend]
        end

        subgraph "Database"
            RDS[(RDS PostgreSQL<br/>Multi-AZ)]
        end

        subgraph "Observability Stack - ECS"
            PROM[Prometheus<br/>Metrics Collection]
            GRAF[Grafana<br/>Dashboards]
            ELK_E[Elasticsearch<br/>Log Storage]
            ELK_L[Logstash<br/>Log Processing]
            ELK_K[Kibana<br/>Log Visualization]
        end

        subgraph "Monitoring"
            CW[CloudWatch<br/>Logs & Metrics]
        end
    end

    USER -->|HTTPS| CF
    CF --> S3
    USER -->|API Calls| ALB
    ALB --> ECS_SERVICE
    ECS_SERVICE --> TASK1
    ECS_SERVICE --> TASK2
    ECS_SERVICE --> TASK3
    TASK1 --> RDS
    TASK2 --> RDS
    TASK3 --> RDS

    ECR -.->|Pull Image| ECS_SERVICE

    TASK1 -->|Metrics| PROM
    TASK2 -->|Metrics| PROM
    TASK3 -->|Metrics| PROM
    PROM --> GRAF

    TASK1 -->|Logs| ELK_L
    TASK2 -->|Logs| ELK_L
    TASK3 -->|Logs| ELK_L
    ELK_L --> ELK_E
    ELK_E --> ELK_K

    ECS_SERVICE -->|Logs| CW
    ALB -->|Access Logs| CW

    style S3 fill:#FF9900,stroke:#232F3E,stroke-width:2px,color:#fff
    style ECR fill:#FF9900,stroke:#232F3E,stroke-width:2px,color:#fff
    style ECS_SERVICE fill:#FF9900,stroke:#232F3E,stroke-width:2px,color:#fff
    style RDS fill:#527FFF,stroke:#232F3E,stroke-width:2px,color:#fff
    style ALB fill:#8C4FFF,stroke:#232F3E,stroke-width:2px,color:#fff
```

## Deployment Steps

### 1. Containerize Spring Boot Application
- Create Dockerfile with Java 21 base image
- Build JAR with Maven
- Build Docker image

### 2. Push to ECR
- Authenticate Docker with ECR
- Create ECR repository
- Tag and push Docker image

### 3. Setup Backend Infrastructure

**RDS PostgreSQL**
- Automated backups enabled

**ECS Cluster (Fargate)**
- Create ECS cluster
- Define task: Spring Boot container, 2 vCPU, 4GB RAM
- Configure environment variables (DB credentials from Secrets Manager)
- Create ECS service with auto-scaling

**Application Load Balancer**

### 4. Setup Observability Stack

**Prometheus + Grafana (ECS)**
- Deploy Prometheus as ECS service
- Configure to scrape `/actuator/prometheus` from Spring Boot tasks
- Deploy Grafana as ECS service
- Configure dashboards for JVM metrics, request rates, latency

**ELK Stack**
- **Option A**: Use Amazon OpenSearch Service (managed)
- **Option B**: Deploy Elasticsearch, Logstash, Kibana as ECS services
- Configure Spring Boot to log to stdout → CloudWatch → Logstash → Elasticsearch
- Access logs via Kibana dashboards

**CloudWatch**
- Container logs from all ECS tasks
- ALB access logs
- RDS performance metrics
- Create alarms for high CPU, memory, error rates

### 5. Frontend Deployment

**Option A: S3 + CloudFront**
- Build Vue.js application
- Upload to S3 bucket configured for static website hosting
- Create CloudFront distribution with custom domain

**Option B: ECS (For enhanced observability)**
- Containerize Vue.js with nginx
- Deploy to same ECS cluster
- Adds full logging and metrics visibility
- More operational overhead but better monitoring

### 6. CI/CD Pipeline
- Use GitHub Actions or AWS CodePipeline
- Build → Test → Push to ECR → Update ECS service
- Run smoke tests post-deployment