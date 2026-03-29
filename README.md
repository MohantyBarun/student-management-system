# 🎓 Student Management System

A full-stack Student Management System built with Java Spring Boot (REST API) and React (coming soon).

## 🛠️ Tech Stack
- Java 21
- Spring Boot 3.4.4
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

## ✅ Features
- Create student with input validation
- Global exception handling
- DTO pattern
- Audit fields (createdAt, updatedAt)
- Duplicate email detection
- Constructor injection
- Service interface pattern

## 🚀 Setup & Run

### Prerequisites
- Java 21
- PostgreSQL
- Maven

### Environment Variables
Set the following before running:
```
DB_URL=jdbc:postgresql://localhost:5432/yourdbname
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### Run
```bash
mvn clean install
mvn spring-boot:run
```

## 📌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /students/save | Create a new student |

### Request Body
```json
{
    "name": "John Doe",
    "email": "john@gmail.com",
    "age": 22
}
```

### Response
```
"Student created successfully with id: 1"
```

## 🔮 Upcoming Features
- Complete CRUD operations
- JWT Authentication
- React Frontend
- Deploy to cloud