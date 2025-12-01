# Order-Inventory Microservices Assignment

This repo contains two Spring Boot services:
- inventory-service/
- order-service/

order-inventory-microservices-assignment/
│
├── inventory-service/
│   ├── src/
│   ├── pom.xml
│   └── README.md (optional)
│
├── order-service/
│   ├── src/
│   ├── pom.xml
│   └── README.md (optional)
│
├── .gitignore
└── README.md   <-- (this file)

Layer	Tech Used:
Language	Java 17
Framework	Spring Boot 3.x
API Communication	REST (Spring Web)
Persistence	Spring Data JPA, H2 DB
Build Tool	Maven
Design Pattern	Factory Pattern (Inventory Handler)
Testing	JUnit 5, Mockito, Spring Boot Test

cd inventory-service
mvn spring-boot:run
Tomcat started on port(s): 8082


cd order-service
mvn spring-boot:run
Tomcat started on port(s): 8081


The Order Service communicates with Inventory Service using RestTemplate:

Check availability
Deduct stock
Validate request

This means you must start Inventory Service BEFORE placing orders.


API Documentation:
Inventory Service APIs (port 8081):
Request : GET http://localhost:8081/inventory/{sku} ;
Response: [
  {
    "id": 3,
    "sku": "SKU-APPLE-001",
    "quantity": 50,
    "expiryDate": "2025-12-01"
  }
]


POST – Update inventory after an order
Request: POST http://localhost:8081/inventory/update
Content-Type: application/json
{
  "sku": "SKU-APPLE-001",
  "batches": [
    { "batchId": null, "quantityChange": -5 }
  ]
}


POST – Place a new order
POST http://localhost:8080/order
Content-Type: application/json
{
  "orderId": "ORD-1001",
  "items": [
    { "sku": "SKU-APPLE-001", "quantity": 3 }
  ]
}
Response:
Order placed: ORD-1001

To run all tests:

mvn test


To run tests for only one service:

cd inventory-service
mvn test

cd order-service
mvn test


Build:
cd inventory-service
mvn clean package

cd order-service
mvn clean package


