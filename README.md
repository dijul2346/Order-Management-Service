# Order Management Service

This is a Spring Boot application that manages orders, utilizing Aerospike as the primary data store and Kafka for event messaging.

## Architecture Overview

The application is built using a microservices architecture style, utilizing the following core technologies:

*   **Spring Boot**: Framework for building the REST API application.
*   **Aerospike**: High-performance NoSQL database used for storing order information.
*   **Apache Kafka**: Distributed streaming platform for publishing order events (e.g., `order.created`).
*   **Docker/Podman**: Containerization for running dependency services (Aerospike, Kafka).

## Local Setup Steps

Follow these steps to get the application running locally.

### Prerequisites
*   Java 17 or higher
*   Maven (or use the provided `mvnw` wrapper)
*   Docker or Podman (with `podman-compose` or `docker-compose`)

### 1. Start Infrastructure
Start the required database and messaging services using the provided compose file.

If using **Podman**:
```bash
podman-compose up -d
```

If using **Docker**:
```bash
# You may need to rename podman-compose.yml to docker-compose.yml or specify the file
docker-compose -f podman-compose.yml up -d
```

Validates that:
*   Aerospike is running on port `3000`
*   Kafka is running on port `9092`

### 2. Run Application
Run the Spring Boot application using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

The application will start on port `8080` (default).

### 3. Test Endpoints
You can verify the application is running by checking the actuator health endpoint:
```bash
curl http://localhost:8080/actuator/health
```

## API Examples

### Create Order
**POST** `/orders`

```bash
curl -X POST http://localhost:8080/orders \
-H "Content-Type: application/json" \
-d '{
  "customerId": "CUST-001",
  "taxRate": 0.1,
  "items": [
    {
      "pricePerUnit": 50.0,
      "quantity": 2
    },
    {
      "pricePerUnit": 30.0,
      "quantity": 1
    }
  ]
}'
```

### View Order
**GET** `/orders/{orderId}`

```bash
curl http://localhost:8080/orders/a1b2c3d4-e5f6-7890-1234-567890abcdef
```

### View Customer Orders (Pagination)
**GET** `/customers/{customerId}/orders`

```bash
curl "http://localhost:8080/customers/CUST-001/orders?page=0&size=10"
```

### Delete Order
**DELETE** `/{orderId}`

```bash
curl -X DELETE http://localhost:8080/a1b2c3d4-e5f6-7890-1234-567890abcdef
```

## Event Schema

When an order is created, an event is published to Kafka.

*   **Topic**: `order.created`
*   **Payload Format**: JSON

**Example Payload:**
```json
{
  "eventType": "order.created",
  "orderId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "timestamp": "2023-10-27T10:00:00.123Z",
  "correlationId": "trace-id-123",  
  "customerId": "CUST-001",
  "subtotal": 130.0,
  "taxAmount": 13.0,
  "totalAmount": 143.0
}
```
*Note: `correlationId` is populated from the MDC context if available.*

## Configuration

Top-level configuration is managed in `src/main/resources/application.properties`.

**Key Properties:**

| Property | Default Value | Description |
| :--- | :--- | :--- |
| `spring.data.aerospike.hosts` | `localhost:3000` | Aerospike host and port |
| `spring.data.aerospike.namespace` | `test` | Aerospike namespace |
| `spring.kafka.bootstrap-servers` | `localhost:9092` | Kafka broker address |
| `spring.kafka.consumer.group-id` | `order-management-group` | Kafka consumer group ID |

**Profiles:**
*   `default`: Uses local settings above.
*   `devprod`: (Files present: `application-devprod.properties`) - Likely for higher environments