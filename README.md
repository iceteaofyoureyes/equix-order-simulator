# Order Simulator

## General Information
Order Simulator is a Java Spring Boot application designed to simulate order placement, management, and execution for financial trading systems. It provides RESTful APIs for creating, cancelling, retrieving, and simulating the execution of orders. The project is structured for extensibility and maintainability.

## Features
- Place new orders with validation
- Cancel existing orders
- Retrieve all or specific orders
- Simulate order execution
- **Order Matching Engine:** Automatically matches buy and sell orders based on price and time priority, simulating real-world trading behavior. The engine processes incoming orders, matches them against the order book, and updates order statuses accordingly.

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
- You must provide the username and password as configured in your `application.yml` when using Swagger UI or any API client (configured as: admin/Admin@123).
- In Swagger UI, click the "Authorize" button and enter your credentials to access secured endpoints.

Use Swagger UI to:
- Explore available endpoints (**NOTICE: In the context of testing environment Create Order API only accept the following Symbols: FPT, VIC, VCB, VNINDEX. Other Symbols won't be accepted**)
- Try out API requests and responses
- View request/response models and error codes

## Configuring Auto Matching Job

You can enable or disable the automatic order matching engine via the `application.yml` configuration file. Add the following property:

```yaml
order-matching:
  auto-enabled: true  # Set to false to disable automatic order matching
```

- When `auto-enabled` is set to `true`, the matching engine will automatically process and match orders as they are placed.
- When set to `false`, orders will be queued but not automatically matched until triggered manually (if supported).

## Architecture & Order Matching Flow

### High-Level Flow
1. **Order Placement:**
   - Users submit buy/sell orders via the REST API.
   - Orders are validated and persisted.
   - New orders are added to a matching queue for processing.
2. **Order Matching Engine:**
   - The `OrderMatchingEngine` component continuously processes orders from the queue.
   - Incoming orders are matched against the order book (best price/time priority).
   - If a match is found, trades are executed and order statuses are updated.
   - Unmatched orders are added to the order book for future matching.
3. **Order Book Management:**
   - The `OrderBook` maintains buy and sell price levels for each symbol.
   - The engine updates the order book as orders are matched, filled, or cancelled.
4. **Order Status & Query:**
   - Users can query order status, execution results, and order book state via the API.

### Key Code Files
- `OrderMatchingEngine.java`: Core matching logic for buy/sell orders.
- `OrderBook.java`: In-memory structure for managing price levels and orders per symbol.
- `OrderRepositoryImpl.java`: Handles persistence, order book management, and queueing.
- `MatchingQueueConfig.java`: Spring configuration for the order matching queue.

## Running Tests

To run unit tests:
```bash
mvn test
```
Test reports will be available in the `target/surefire-reports` directory.

## Logging

Application logs are written to the `logs/` directory. Logback is used for flexible logging configuration.
