[![LinkedIn][linkedin-shield]][linkedin-url]

<br />
<div align="center">
<h3 align="center">Inventory Service</h3>

  <p align="center">
    Event-driven microservice responsible for stock reservation within the Order Fulfillment Platform.
    <br />
    <br />
    <a href="https://github.com/order-fulfillment-platform">View Organization</a>
  </p>
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about">About</a></li>
    <li><a href="#built-with">Built With</a></li>
    <li><a href="#architecture">Architecture</a></li>
    <li><a href="#api">API</a></li>
    <li><a href="#events">Events</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

## About

The Inventory Service is responsible for managing product stock and processing stock reservations within the Order Fulfillment Platform. It consumes `ORDER_CREATED` events from Kafka, verifies product availability, reserves stock and publishes the result as a domain event using the **Outbox Pattern**.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Built With

[![Spring Boot][springboot-shield]][springboot-url]
[![Apache Kafka][kafka-shield]][kafka-url]
[![PostgreSQL][postgres-shield]][postgres-url]
[![Docker][docker-shield]][docker-url]
[![Java][java-shield]][java-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Architecture

### Event Flow
```
[Kafka] order.created
        │
        ▼
OrderCreatedConsumer
        │
        ▼
InventoryService
  → check stock availability for each item
  → if available: decrease stock, save reservations
  → if unavailable: skip
        │
        ▼
[Transaction]
  INSERT reservations (if available)
  INSERT outbox_events
        │
        ▼
@Scheduled every 5s
  → publish STOCK_RESERVED or STOCK_REJECTED to Kafka
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## API

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/v1/products | Create a new product |
| GET | /api/v1/products/{id} | Get product by ID |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Events

### Consumed

| Topic | Event | Description |
|---|---|---|
| order.created | ORDER_CREATED | Triggers stock reservation process |

### Published

| Topic | Event | Description |
|---|---|---|
| stock.reserved | STOCK_RESERVED | Stock successfully reserved for all items |
| stock.rejected | STOCK_REJECTED | One or more items are out of stock |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker

### Run with Docker Compose

Start the full platform from the [infrastructure](https://github.com/order-fulfillment-platform/infrastructure) repository:
```bash
docker-compose up -d --build
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contact

Eros Burelli — [LinkedIn](https://www.linkedin.com/in/eros-burelli-a458b1145/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS -->
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/eros-burelli-a458b1145/
[springboot-shield]: https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white
[springboot-url]: https://spring.io/projects/spring-boot
[kafka-shield]: https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white
[kafka-url]: https://kafka.apache.org/
[postgres-shield]: https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white
[postgres-url]: https://www.postgresql.org/
[docker-shield]: https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white
[docker-url]: https://www.docker.com/
[java-shield]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[java-url]: https://www.java.com/