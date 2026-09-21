# EventFlow – Event Booking Platform

EventFlow is a full-stack event booking platform where organisers create and manage events, while attendees browse events, book tickets, review their bookings, and cancel confirmed bookings.

The application uses a three-tier AWS deployment. The React frontend is hosted on Amazon S3, the Spring Boot backend runs as a Docker container on Amazon ECS Fargate, and application data is stored in Amazon RDS for MySQL. The application also prevents ticket overselling through transactional database locking.

## Live Application

| Component | URL |
|---|---|
| Frontend | http://booking-platform-frontend-312030313098.s3-website.ap-south-1.amazonaws.com |
| Backend health check | http://booking-platform-alb-1213308022.ap-south-1.elb.amazonaws.com/actuator/health |

> The frontend uses an Amazon S3 static website endpoint, which is HTTP-only. A production enhancement would use Amazon CloudFront and an ACM certificate to provide HTTPS.

## How to Use the Application

1. Open the **Frontend** URL above.
2. Register as either an **Organiser** or an **Attendee**.
3. Log in using the account you created.
4. As an organiser, create an event and monitor its ticket availability.
5. As an attendee, browse events, book tickets, view bookings, and cancel a confirmed booking when needed.

The backend automatically updates available and booked ticket counts. When a confirmed booking is cancelled, the tickets are restored.

## Features

### Organiser

- Register and log in securely
- Create events
- View events created by the logged-in organiser
- Monitor capacity, booked tickets, and available tickets

### Attendee

- Register and log in securely
- Browse upcoming events
- Book one or more tickets
- View personal booking history
- Cancel confirmed bookings
- Automatically restore tickets after cancellation

## Technology Stack

### Backend

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- JWT authentication
- Hibernate Validator
- MySQL
- Swagger/OpenAPI
- Maven
- JUnit and Mockito

### Frontend

- React
- Vite
- React Router
- Axios
- Lucide React
- CSS

## AWS Architecture

The application follows a three-tier architecture deployed in AWS:

```text
User browser
      |
      v
Amazon S3 static website (React frontend)
      |
      | REST API requests over HTTP
      v
Application Load Balancer
      |
      v
Amazon ECS Fargate (Spring Boot Docker container)
      |
      | Spring Data JPA / Hibernate
      v
Amazon RDS for MySQL
```

### AWS Services Used

| AWS service | Purpose in this project |
|---|---|
| Amazon S3 | Hosts the React production build as a static website. |
| Amazon ECR | Stores the private Docker image for the Spring Boot backend. |
| Amazon ECS with Fargate | Runs and manages the backend container without managing EC2 servers. |
| Application Load Balancer | Receives HTTP requests and routes traffic to healthy ECS tasks. |
| Amazon RDS for MySQL | Provides a managed relational database for users, events, and bookings. |
| Amazon CloudWatch | Stores ECS container logs for monitoring and troubleshooting. |
| VPC and Security Groups | Provide isolated networking and restrict service-to-service access. |

The ALB health check calls `/actuator/health`. It forwards traffic only to healthy backend tasks. The RDS instance is not publicly accessible; MySQL port `3306` is allowed only from the ECS security group.

The backend follows a layered structure:

```text
Controller
    |
Service
    |
Service Implementation
    |
Repository
    |
Database
```

DTOs are used between the API and service layers so JPA entities are not exposed directly.

## Security

The application uses stateless JWT authentication.

- Passwords are hashed using BCrypt.
- JWT tokens are generated after successful login.
- The frontend sends the token using the `Authorization` header.
- Organiser and attendee APIs are protected using role-based authorization.
- Invalid and expired tokens return a structured `401 Unauthorized` response.
- Unauthorized role access returns `403 Forbidden`.
- CORS origins are configured using an environment variable.
- RDS database access is restricted to the ECS security group.

## Concurrency and Overselling Prevention

The booking operation uses:

- `@Transactional`
- `PESSIMISTIC_WRITE` database locking
- Availability validation inside the transaction

When multiple attendees attempt to book the same event simultaneously, each request must obtain a database lock before changing ticket availability.

This ensures the number of confirmed tickets never exceeds the event capacity.

A concurrency integration test verifies this behaviour using 20 simultaneous booking attempts against an event with only 5 tickets.

## REST APIs

### Authentication

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register an organiser or attendee |
| POST | `/api/auth/login` | Public | Authenticate and receive a JWT |

### Events

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/events` | Organiser | Create an event |
| GET | `/api/events` | Authenticated | View upcoming events |
| GET | `/api/events/my-events` | Organiser | View organiser's events |

### Bookings

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/events/{eventId}/bookings` | Attendee | Book tickets |
| GET | `/api/bookings/me` | Attendee | View personal bookings |
| PATCH | `/api/bookings/{bookingId}/cancel` | Attendee | Cancel a booking |

## Environment Variables

Create local environment variables using `.env.example` as a reference.

### Backend

```env
DB_URL=jdbc:mysql://localhost:3306/booking_platform_db
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password

JWT_SECRET=your_base64_encoded_secret_key
JWT_EXPIRATION=3600000

SERVER_PORT=8080
CORS_ALLOWED_ORIGINS=http://localhost:5173
SHOW_SQL=false
```

### Frontend

Create `frontend/.env`:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

Never commit actual database passwords or JWT secrets.

## Local Setup

### Prerequisites

Install:

- Java 17 or later
- MySQL 8
- Node.js
- npm

### Database Setup

Create the application and test databases:

```sql
CREATE DATABASE booking_platform_db;
CREATE DATABASE booking_platform_test_db;
```

Spring Boot creates and updates the required tables automatically.

### Run the Backend

Configure the required backend environment variables and run:

```bash
./mvnw spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

Health endpoint:

```text
http://localhost:8080/actuator/health
```

### Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs at:

```text
http://localhost:5173
```

## Testing

Configure these test environment variables:

```env
TEST_DB_URL=jdbc:mysql://localhost:3306/booking_platform_test_db
TEST_DB_USERNAME=your_mysql_username
TEST_DB_PASSWORD=your_mysql_password
```

Run backend tests:

```bash
./mvnw test
```

The test suite includes:

- Spring application context test
- Booking service unit tests
- Insufficient-ticket validation test
- Booking cancellation test
- Concurrent booking integration test

Run frontend linting:

```bash
cd frontend
npm run lint
```

Create the frontend production build:

```bash
npm run build
```

## Standard API Responses

Successful response:

```json
{
  "statusCode": "200",
  "message": "Operation completed successfully",
  "object": {}
}
```

Error response:

```json
{
  "apiPath": "/api/resource",
  "errorCode": "400",
  "errorMessage": "Validation message",
  "errorTime": "2026-09-13T12:00:00"
}
```

## Deployment and Operations

### Backend deployment flow

```text
Spring Boot application
    → Docker image
    → Amazon ECR
    → ECS task definition
    → ECS Fargate service
    → Application Load Balancer
```

The ECS service maintains the required number of backend tasks. When a new task-definition revision is deployed, ECS starts a new task, the ALB verifies it through the health check, and the previous task is stopped gracefully after the new task is healthy.

### Frontend deployment flow

```bash
cd frontend
npm run build
aws s3 sync dist s3://booking-platform-frontend-312030313098 --delete --region ap-south-1
```

Before building for AWS, configure the frontend API URL to point to the ALB:

```env
VITE_API_BASE_URL=http://booking-platform-alb-1213308022.ap-south-1.elb.amazonaws.com/api
```

### Monitoring and troubleshooting

CloudWatch logs are configured for the ECS container. During deployment, CloudWatch can be used to investigate application startup errors, database connectivity problems, and runtime exceptions.

For example, an initial RDS connectivity issue was identified through ECS container logs. The RDS security-group rule was then updated to allow MySQL port `3306` from the ECS security group, and the service was redeployed successfully.

## Production Improvements

- Store database credentials and JWT secrets in AWS Secrets Manager instead of plain environment variables.
- Use separate security groups for the ALB and ECS tasks.
- Use CloudFront with an ACM certificate to serve the frontend over HTTPS.
- Keep the S3 bucket private and allow access only through CloudFront.
- Run multiple ECS tasks across Availability Zones and configure autoscaling.
- Enable RDS backups, Multi-AZ deployment, and CloudWatch alarms.
- Add CI/CD with GitHub Actions, AWS CodePipeline, or Jenkins.

## Author

Shreyas Gowda
