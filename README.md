# E-Commerce Microservices Platform

A comprehensive, event-driven e-commerce platform built with Spring Boot microservices architecture. Features distributed transaction management, real-time inventory tracking, intelligent fraud detection, and seamless payment processing across multiple services.

## [API ENDPOINTS](https://ejohchisimdi-collab.github.io/E-Commerce-Microservice/)

##  Features

### Core E-Commerce Functionality
- **Multi-Service Architecture**: Six specialized microservices working in harmony
- **Event-Driven Processing**: Kafka-based asynchronous communication for reliable order processing
- **Real-time Inventory Management**: Automatic stock reservation and release with optimistic locking
- **Intelligent Payment Processing**: Multi-factor fraud detection with configurable scoring
- **Service Discovery**: Eureka-based service registration and discovery
- **API Gateway**: Centralized routing and load balancing

### Security & Authentication
- **JWT Authentication**: Secure token-based authentication across all services
- **Role-Based Access Control**: Three-tier access system (Merchant, Employee, Customer)
- **Service-to-Service Authentication**: Secure inter-service communication
- **Password Encryption**: BCrypt-based secure password storage

### Payment & Financial Features
- **Account Management**: Separate customer and merchant accounts
- **Fraud Detection System**: Multi-layered security checks including:
  - Location-based validation
  - Payment velocity monitoring
  - Failed payment tracking
  - Insufficient funds detection
- **Real-time Balance Updates**: Instant payment processing and settlement
- **Transaction Isolation**: Optimistic locking prevents race conditions

### Order Management
- **Shopping Cart System**: Persistent cart management with idempotency support
- **Order Lifecycle**: Complete order tracking from creation to completion
- **Automatic Timeout**: Failed orders auto-cancelled after 5 minutes
- **Saga Pattern**: Distributed transaction coordination with compensation

### Inventory & Products
- **Bulk Import**: CSV-based mass product import with idempotency
- **Stock Reservation**: Two-phase commit for inventory management
- **Automatic Release**: Failed payments trigger stock restoration
- **Public Product Catalog**: No authentication required for browsing

### Reliability & Resilience
- **Circuit Breakers**: Resilience4j-based fault tolerance
- **Retry Mechanisms**: Automatic retries with exponential backoff
- **Dead Letter Queues**: Failed message handling and monitoring
- **Health Checks**: Docker-based health monitoring for all services
- **Idempotency Keys**: Prevents duplicate operations across all services

##  Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          API Gateway (8084)                      │
│                    (Spring Cloud Gateway)                        │
└────────────────────────────┬────────────────────────────────────┘
                             │
                ┌────────────┼────────────┐
                │            │            │
      ┌─────────▼─────┐ ┌───▼────┐ ┌────▼──────┐
      │  User Service │ │ Product│ │  Finance  │
      │    (8080)     │ │ Service│ │  Service  │
      │               │ │ (8081) │ │  (8082)   │
      └───────┬───────┘ └────┬───┘ └─────┬─────┘
              │              │            │
              └──────────────┼────────────┘
                             │
                      ┌──────▼──────┐
                      │Order Service│
                      │   (8083)    │
                      └──────┬──────┘
                             │
                  ┌──────────▼──────────┐
                  │   Kafka Message     │
                  │      Broker         │
                  │   (Event Stream)    │
                  └──────────┬──────────┘
                             │
                    ┌────────▼────────┐
                    │ Eureka Server   │
                    │     (8761)      │
                    └─────────────────┘
```

##  Service Flow Diagram

### Order Processing Flow
```
1. Customer adds to cart
   │
   ├─► Shopping Cart Service
   │   └─► Validates product exists
   │       └─► Checks stock availability
   │           └─► Creates cart entry
   │
2. Customer places order
   │
   ├─► Order Service
   │   ├─► Validates account exists
   │   ├─► Creates pending order
   │   └─► Publishes "order-created" event
   │
3. Product Service (Kafka Listener)
   │
   ├─► Reserves inventory
   │   ├─► Success → Publishes "inventory-reserved"
   │   └─► Failure → Publishes "reservation-failed"
   │
4. Finance Service (Kafka Listener)
   │
   ├─► Processes payment
   │   ├─► Fraud detection checks
   │   ├─► Balance validation
   │   ├─► Success → Publishes "payment-succeeded"
   │   └─► Failure → Publishes "payment-failed"
   │
5. Order Service (Kafka Listener)
   │
   ├─► "payment-succeeded" → Order COMPLETED
   ├─► "payment-failed" → Order FAILED
   └─► "reservation-failed" → Order FAILED

6. Product Service (Compensation)
   │
   └─► "payment-failed" → Releases reserved stock
```

##  Tech Stack

| Category | Technology |
|----------|-----------|
| **Backend Framework** | Spring Boot 4.0.1 |
| **Security** | Spring Security + JWT |
| **Message Broker** | Apache Kafka 7.4.0 |
| **Service Discovery** | Netflix Eureka |
| **API Gateway** | Spring Cloud Gateway |
| **Database** | MySQL 8.0 |
| **ORM** | Spring Data JPA (Hibernate) |
| **Resilience** | Resilience4j |
| **Build Tool** | Maven 3.9.12 |
| **Containerization** | Docker & Docker Compose |
| **Testing** | JUnit 5, Mockito, Spring Kafka Test |
| **API Documentation** | Swagger/OpenAPI |
| **Logging** | SLF4J + Logback |

##  Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0
- Docker & Docker Compose
- Apache Kafka

### Option 1: Docker (Recommended)

1. **Clone the repository**
```bash
git clone https://github.com/ejohchisimdi-collab/E-Commerce-Microservice.git
cd E-Commerce-Microservices
```

2. **Start all services**
```bash
docker-compose up -d
```

3. **Verify services are running**
```bash
docker-compose ps
```

4. **Access services**
   - API Gateway: http://localhost:8084
   - Eureka Dashboard: http://localhost:8761
   - User Service: http://localhost:8080
   - Product Service: http://localhost:8081
   - Finance Service: http://localhost:8082
   - Order Service: http://localhost:8083

### Option 2: Local Development

1. **Clone and configure**
```bash
git clone https://github.com/ejohchisimdi-collab/E-Commerce-Microservice
cd E-Commerce-Microservices
```

2. **Start infrastructure services**
```bash
# Start MySQL
docker run -d --name ecommerce-mysql \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -p 3306:3306 \
  mysql:8.0

# Start Kafka & Zookeeper
docker-compose up -d zookeeper kafka
```

3. **Configure environment variables**
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/yourdb
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=rootpassword
export JWT_SECRET=mySecretKeyThatIsATLeast32CharactersLongForHSMACAlgorithm
export JWT_EXPIRATION=86400000
```

4. **Build all services**
```bash
mvn clean install
```

5. **Start services in order**
```bash
# 1. Eureka Server
cd eureka-server && mvn spring-boot:run &

# 2. User Service
cd user-service && mvn spring-boot:run &

# 3. Product Service
cd product-service && mvn spring-boot:run &

# 4. Finance Service
cd finance-service && mvn spring-boot:run &

# 5. Order Service
cd order-service && mvn spring-boot:run &

# 6. API Gateway
cd api-gateway && mvn spring-boot:run &
```

### Default Admin Account

On first startup, a default merchant account is created:
- **Username**: Merchant
- **Password**: Admin
- **Role**: Merchant
- **Email**: Admin@gmail.com



##  Service Details

### User Service (Port 8080)
- User registration and authentication
- JWT token generation
- Role-based access control
- User approval workflow for merchants and employees

### Product Service (Port 8081)
- Product catalog management
- Bulk CSV import with idempotency
- Inventory reservation/release
- Stock level tracking
- Public product browsing

### Finance Service (Port 8082)
- Account management (customer & merchant)
- Payment processing with fraud detection
- Balance management
- Transaction history
- Payment status tracking

### Order Service (Port 8083)
- Shopping cart management
- Order creation and tracking
- Order timeout handling (5-minute auto-cancellation)
- Order status management (PENDING → COMPLETED/FAILED)

### API Gateway (Port 8084)
- Request routing
- Load balancing
- Service discovery integration
- Lower-case service ID routing

### Eureka Server (Port 8761)
- Service registration
- Service discovery
- Health monitoring
- Dashboard interface

## 🔄 Event Flow

### Kafka Topics

| Topic | Producer | Consumer | Purpose |
|-------|----------|----------|---------|
| `order-created` | Order Service | Product Service | Triggers inventory reservation |
| `inventory-reserved` | Product Service | Finance Service | Triggers payment processing |
| `reservation-failed` | Product Service | Order Service | Marks order as failed |
| `payment-succeeded` | Finance Service | Order Service | Completes order |
| `payment-failed` | Finance Service | Order, Product Services | Triggers compensation |

### Dead Letter Queues (DLQ)
All topics have corresponding `.DLQ` topics for failed message handling:
- `order-created.DLQ`
- `inventory-reserved.DLQ`
- `reservation-failed.DLQ`
- `payment-succeeded.DLQ`
- `payment-failed.DLQ`

##  Fraud Detection

The finance service implements a multi-factor fraud scoring system:

| Check | Score | Description |
|-------|-------|-------------|
| **Different Location** | +1 | Payment from unusual location |
| **High Velocity** | +1 | 3+ payments in < 1 minute |
| **Failed History** | +1 | 3 consecutive failed payments |
| **Insufficient Funds** | Auto-Fail | Balance too low |

**Fraud Score ≥ 3 = Automatic Payment Failure**

## Testing

### Run All Tests
```bash
mvn test
```

### Test Coverage by Service

| Service | Tests | Coverage |
|---------|-------|----------|
| **User Service** | 6 | Registration, Login, Approval |
| **Product Service** | 8 | CRUD, Import, Events |
| **Finance Service** | 12 | Payments, Fraud Detection, Accounts |
| **Order Service** | 10 | Cart, Orders, Saga |
| **Total** | **36+** | Integration & Unit Tests |

### Integration Testing
```bash
 With embedded Kafka
cd order-service && mvn test -Dtest=OrderServiceIntegrationTests
cd product-service && mvn test -Dtest=EventServiceIntegrationTest
cd finance-service && mvn test -Dtest=PaymentServiceIntegrationTest
```

##  Deployment

### Docker Deployment

Each service includes a multi-stage Dockerfile:

```dockerfile
# Build stage
FROM maven:3.8.8-eclipse-temurin-17-alpine AS build
WORKDIR /build
# Copy and build

# Production stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Run application
```

### Health Checks

All services include Docker health checks:
```yaml
healthcheck:
  test: ["CMD", "wget", "--spider", "http://localhost:PORT/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 5
  start_period: 60s
```


##  Configuration

### Application Properties Template

```properties
# Database
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=${SERVICE_NAME}
spring.kafka.consumer.auto-offset-reset=earliest

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

# Circuit Breaker (Resilience4j)
resilience4j.circuitbreaker.instances.default.slidingWindowSize=5
resilience4j.circuitbreaker.instances.default.failureRateThreshold=50
resilience4j.circuitbreaker.instances.default.waitDurationInOpenState=10s
```

##  Monitoring & Observability



### Eureka Dashboard
Access the service registry at: http://localhost:8761

View:
- Registered services
- Instance status
- Health information

##  Error Handling

### Global Exception Handling
All services implement centralized exception handling:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException e);
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError2> handleValidation(MethodArgumentNotValidException e);
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception e);
}
```

### Circuit Breaker Fallbacks
Services implement fallback methods for external dependencies:

```java
@CircuitBreaker(name = "userService", fallbackMethod = "doesUserExistFallBack")
public Boolean doesUserExist(int userId) {
    // Call user service
}

public Boolean doesUserExistFallBack(int userId, Throwable t) {
    // Fallback logic
    throw new FallBackException("User profiles not available");
}
```

## 🔐 Security  Practices

1. **JWT Tokens**
   - 24-hour expiration (configurable)
   - Signed with HS256 algorithm
   - Include user ID, username, and role

2. **Password Security**
   - BCrypt encryption (cost factor 10)
   - Never log or expose passwords
   - Password complexity requirements recommended

3. **API Security**
   - All endpoints authenticated except public routes
   - Role-based authorization on sensitive operations
   - Service-to-service authentication

4. **Data Protection**
   - Sensitive data encrypted at rest
   - HTTPS required for production
   - SQL injection prevention via JPA

##  API Documentation

### Swagger UI
Each service exposes Swagger documentation:
- User Service: http://localhost:8080/swagger-ui.html
- Product Service: http://localhost:8081/swagger-ui.html
- Finance Service: http://localhost:8082/swagger-ui.html
- Order Service: http://localhost:8083/swagger-ui.html

### Example Endpoints

#### User Registration
```bash
POST http://localhost:8080/users/
Content-Type: application/json

{
  "name": "John Doe",
  "userName": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "roles": "Customer"
}
```

#### Add Product to Cart
```bash
POST http://localhost:8083/shopping-cart/
Authorization: Bearer {token}
Idempotency-Key: unique-key-123
Content-Type: application/json

{
  "userId": 1,
  "productName": "Laptop",
  "amount": 2
}
```

#### Place Order
```bash
POST http://localhost:8083/orders/
Authorization: Bearer {token}
Idempotency-Key: order-key-456
Content-Type: application/json

{
  "cartId": 1,
  "userId": 1,
  "accountId": 1,
  "location": "United_States"
}
```

# Troubleshooting

### Common Issues

1. **Services not registering with Eureka**
   - Check Eureka server is running
   - Verify network connectivity
   - Check eureka.client.service-url.defaultZone configuration

2. **Kafka connection errors**
   - Ensure Kafka and Zookeeper are running
   - Verify bootstrap-servers configuration
   - Check topic auto-creation settings

3. **Database connection issues**
   - Verify MySQL is running
   - Check connection string format
   - Ensure databases exist (auto-created by flag)

4. **JWT authentication failures**
   - Verify JWT secret matches across services
   - Check token expiration
   - Ensure proper Bearer token format

### Debug Mode
Enable debug logging:
```properties
logging.level.com.chisimdi=DEBUG
logging.level.org.springframework.web=DEBUG
```



##  License

This project is licensed under the MIT License 

##  Author

**Chisimdi Ejoh**
- LinkedIn: [www.linkedin.com/in/chisimdi-ejoh-057ba1382](https://www.linkedin.com/in/chisimdi-ejoh-057ba1382)
- GitHub: [@ejohchisimdi-collab](https://github.com/ejohchisimdi-collab)

##  Acknowledgments

- Spring Boot team for the excellent framework
- Apache Kafka for reliable event streaming
- Netflix OSS for Eureka service discovery
- The open-source community
- Microservice videos from code opinion


---

