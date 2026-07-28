# Zorvyn Finance Backend

A RESTful backend service for managing financial records with role-based access control. Built with Spring Boot, it provides endpoints for creating, viewing, and deleting financial transactions, along with a dashboard summary for analytics.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0.5 |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL |
| Validation | Spring Boot Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build Tool | Maven |
| Utilities | Lombok |

---

## Project Structure

```
src/main/java/com/zorvyn/finance/
├── FinanceBackendApplication.java   # Application entry point
├── DatabaseSeeder.java              # Seeds mock users and records on startup
├── controller/
│   └── FinanceController.java       # REST API endpoints
├── service/
│   └── FinancialRecordService.java  # Business logic and access control
├── repository/
│   ├── FinancialRecordRepository.java
│   └── UserRepository.java
├── model/
│   ├── User.java                    # User entity
│   ├── FinancialRecord.java         # Financial record entity
│   ├── Role.java                    # Enum: ADMIN, ANALYST, VIEWER
│   └── TransactionType.java        # Enum: INCOME, EXPENSE
├── dto/
│   └── DashboardSummaryDTO.java     # Dashboard response shape
└── exception/
    ├── GlobalExceptionHandler.java  # Centralized error handling
    ├── ResourceNotFoundException.java
    └── UnauthorizedAccessException.java
```

---

## Prerequisites

- Java 25
- Maven 3.6+
- MySQL 8.0+

---

## Getting Started

### 1. Configure the Database

The application connects to a local MySQL instance. Update credentials in `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/zorvyn_finance?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=12345
```

The database `zorvyn_finance` is created automatically on first run.

### 2. Build and Run

```bash
./mvnw spring-boot:run
```

Or on Windows:

```cmd
mvnw.cmd spring-boot:run
```

The server starts on **http://localhost:8080**.

### 3. Database Seeding

On first startup, if the database is empty, the `DatabaseSeeder` automatically creates three users and sample financial records:

| User ID | Username | Role | Password |
|---|---|---|---|
| 1 | admin_user | ADMIN | pass123 |
| 2 | analyst_user | ANALYST | pass123 |
| 3 | viewer_user | VIEWER | pass123 |

Seed records include a software contract income and two expense entries for cloud infrastructure and software licenses.

---

## Authentication Model

This service uses a **header-based mock authentication** pattern. Pass the user's database ID in the `X-User-Id` request header. Full JWT/session authentication is outside the scope of this service.

```
X-User-Id: 1
```

---

## Role-Based Access Control

| Operation | ADMIN | ANALYST | VIEWER |
|---|:---:|:---:|:---:|
| Create record | ✅ | ❌ | ❌ |
| View all records | ✅ | ✅ | ❌ |
| Delete record | ✅ | ❌ | ❌ |
| View dashboard | ✅ | ✅ | ✅ |

---

## API Reference

All endpoints are prefixed with `/api/finance`.

### Create a Financial Record
```
POST /api/finance/records
Header: X-User-Id: {adminUserId}
```

**Request body:**
```json
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Software Contract",
  "date": "2026-07-28",
  "description": "Payment from client A"
}
```

**Response:** `201 Created` with the saved record.  
**Access:** ADMIN only.

---

### Get All Records
```
GET /api/finance/records
Header: X-User-Id: {userId}
```

**Response:** `200 OK` with a list of all financial records.  
**Access:** ADMIN and ANALYST only. VIEWER receives `403 Forbidden`.

---

### Delete a Record
```
DELETE /api/finance/records/{id}
Header: X-User-Id: {adminUserId}
```

**Response:** `204 No Content`.  
**Access:** ADMIN only.

---

### Get Dashboard Summary
```
GET /api/finance/dashboard
Header: X-User-Id: {userId}
```

**Response:** `200 OK`
```json
{
  "totalIncome": 5000.00,
  "totalExpense": 1500.50,
  "netBalance": 3499.50,
  "categoryWiseTotals": {
    "Software Contract": 5000.00,
    "Cloud Infrastructure": 1200.50,
    "Software Licenses": 300.00
  }
}
```

**Access:** All roles (ADMIN, ANALYST, VIEWER).

---

## Error Responses

All errors follow a consistent JSON structure:

```json
{
  "timestamp": "2026-07-28T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with ID: 99",
  "path": "/api/finance/records"
}
```

| HTTP Status | Condition |
|---|---|
| `403 Forbidden` | User lacks permission for the operation |
| `404 Not Found` | Requested user or record does not exist |
| `500 Internal Server Error` | Unexpected server-side error |

---

## API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:

```
http://localhost:8080/v3/api-docs
```

---

## Running Tests

```bash
./mvnw test
```
