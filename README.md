# EventFlow – Event Booking Platform

EventFlow is a full-stack event booking platform where organisers can create and monitor events, while attendees can browse events, book tickets, view their bookings, and cancel confirmed bookings.

The application prevents ticket overselling through transactional database locking.

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

## Architecture

The application follows a three-tier architecture:

```text
React frontend
      |
      | REST API over HTTP
      v
Spring Boot backend
      |
      | Spring Data JPA / Hibernate
      v
MySQL database
```

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

## Planned AWS Deployment

The production architecture is designed to use:

- AWS Amplify for the React frontend
- Amazon ECS for the Spring Boot backend
- Application Load Balancer in front of ECS
- Amazon RDS for MySQL
- Amazon ECR for the backend Docker image
- AWS Secrets Manager or ECS secrets for credentials
- CloudWatch for application logs and monitoring

## Author

Shreyas Gowda