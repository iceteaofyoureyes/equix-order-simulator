# Order Simulator

## General Information
Order Simulator is a Java Spring Boot application designed to simulate order placement, management, and execution for financial trading systems. It provides RESTful APIs for creating, cancelling, retrieving, and simulating the execution of orders. The project is structured for extensibility and maintainability.

## Features
- Place new orders with validation
- Cancel existing orders
- Retrieve all or specific orders
- Simulate order execution

## Technology Stack
- Java 17+
- Spring Boot
- Spring Security
- Spring Validation
- Maven
- JUnit 5 & Mockito (for testing)
- Swagger/OpenAPI (for API documentation)

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build and Run
1. **Clone the repository:**
   ```bash
   git clone https://github.com/iceteaofyoureyes/equix-order-simulator.git
   cd order-simulator
   ```
2. **Configure application settings:**
   Edit `src/main/resources/application.yml` to set up your environment, including server port, logging, and security credentials (username/password for basic authentication).
3. **Build the project:**
   ```bash
   mvn clean install
   ```
4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
   Or use the provided wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on http://localhost:8085 (http://localhost:8080 by default) with context path /api/v1 as configured in `application.yml`.

## API Documentation (Swagger UI)

Once the application is running, you can access the Swagger UI for interactive API documentation and testing:

- http://localhost:8085/api/v1/swagger-ui.html (default http://localhost:8080/swagger-ui.html)
- http://localhost:8085/api/v1/swagger-ui/index.html (default http://localhost:8080/swagger-ui/index.html)

**Authentication:**
- The API is protected with Spring Basic Authentication.
- You must provide the username and password as configured in your `application.yml` when using Swagger UI or any API client.
- In Swagger UI, click the "Authorize" button and enter your credentials to access secured endpoints.

Use Swagger UI to:
- Explore available endpoints
- Try out API requests and responses
- View request/response models and error codes

## Running Tests

To run unit tests:
```bash
mvn test
```
Test reports will be available in the `target/surefire-reports` directory.

## Logging

Application logs are written to the `logs/` directory. Logback is used for flexible logging configuration.
