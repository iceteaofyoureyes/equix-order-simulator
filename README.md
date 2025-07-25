# Spring Actuator Services Monitoring

This project provides a monitoring solution for Spring Boot microservices using Prometheus, Grafana, Alertmanager, and Blackbox Exporter. It leverages Spring Boot Actuator to expose application metrics and health endpoints, which are then collected and visualized for observability and alerting.

## Project Structure

```
├── docker/                  # Docker Compose and monitoring configs
├── src/                     # Spring Boot application source code
├── docs/images/             # Documentation images (dashboard screenshots)
├── pom.xml                  # Maven build file
```

## Monitoring Stack Components

- **Prometheus**: Collects metrics from Spring Boot Actuator endpoints.
- **Grafana**: Visualizes metrics and dashboards.
- **Alertmanager**: Handles alerts triggered by Prometheus rules.
- **Blackbox Exporter**: Probes external endpoints (e.g., websites) for availability and readiness, enabling monitoring of frontend (FE) sites and other HTTP/HTTPS services.

## Prerequisites

- [Docker](https://www.docker.com/products/docker-desktop) installed on your machine.
- [Docker Compose](https://docs.docker.com/compose/) (if not included with Docker Desktop).

## Setup & Running the Monitoring Stack

1. **Build and Run Your Spring Boot Application**
   - Build the application JAR:
     ```sh
     ./mvnw clean package
     ```
   - Run the application (optional, if not containerized):
     ```sh
     java -jar target/demo-0.0.1-SNAPSHOT.jar
     ```
   - Ensure your application exposes Actuator endpoints (e.g., `/actuator/prometheus`).

2. **Start Monitoring Stack with Docker Compose**
   - Navigate to the monitoring directory:
     ```sh
     cd docker/monitoring
     ```
   - Start Prometheus, Grafana, Alertmanager, and Blackbox Exporter:
     ```sh
     docker compose up -d
     ```
   - By default, services use `network_mode: host`. Adjust as needed for your environment.

3. **Access the Monitoring Tools**
   - **Prometheus**: [http://localhost:9090](http://localhost:9090)
   - **Grafana**: [http://localhost:3000](http://localhost:3000) (default admin password: `admin`)
   - **Alertmanager**: [http://localhost:9093](http://localhost:9093)
   - **Blackbox Exporter**: [http://localhost:9115](http://localhost:9115) (if port is exposed)

4. **Import Grafana Dashboards**
   - Log in to Grafana and import dashboards from `docker/monitoring/grafana-dashboard/`.

## Blackbox Exporter for Website (FE) Monitoring

The Blackbox Exporter is configured to monitor the readiness and availability of external websites, such as your frontend (FE) application. It performs HTTP/HTTPS probes and exposes metrics to Prometheus.

- **Configuration**: See `docker/monitoring/blackbox_exporter/config/blackbox.yml` for probe settings.
- **Prometheus Integration**: The Prometheus config (`prometheus.yml`) includes scrape jobs for Blackbox Exporter.
- **Grafana Dashboard**: A dedicated dashboard visualizes website status and probe results.

## Configuration

- **Prometheus**: Edit `docker/monitoring/prometheus/prometheus.yml` to add or modify scrape targets.
- **Alertmanager**: Configure alert receivers in `docker/monitoring/alertmanager/alertmanager.yml`.
- **Grafana**: Dashboards can be customized or extended as needed.
- **Blackbox Exporter**: Configure probe modules and targets in `docker/monitoring/blackbox_exporter/config/blackbox.yml`. This file defines how Blackbox Exporter probes websites (e.g., HTTP, HTTPS, ICMP) and which endpoints are monitored. Update this file to add or modify website monitoring rules as needed.

## Grafana Dashboards (Sample Screenshots)

Application Readiness Status:

![Application Readiness Status](docs/images/Application-readiness-status.png)

Services Readiness Status:

![Services Readiness Status](docs/images/services-readiness-status.png)

Website (FE) Monitoring:

![Website (FE) Monitoring](docs/images/fe-site-monitoring.png)

## Custom Health Checks

The application includes custom readiness and health checkers (e.g., for database, web services, RabbitMQ, Redis) implemented in `src/main/java/com/example/demo/monitoring/checker/`.

## License

This project is for demonstration and educational purposes.
