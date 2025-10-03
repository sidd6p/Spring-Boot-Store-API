# Spring Boot Store API

This is a Spring Boot REST API project that I've built while learning Java Spring Boot
following [Code with Mosh Spring Boot tutorials](https://members.codewithmosh.com/courses).
> The primary focus is on mastering JPA, Spring Data, and Java core concepts rather than creating a fully functional
> e-commerce store. The project demonstrates modern Spring Boot development practices with database integration and
> clean
> architecture patterns.

![img.png](Doc/img.png)

## Table of Contents

- [Learning Focus](#learning-focus)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Learning Resources](#learning-resources)
- [Contact](#contact)

## Learning Focus

This project emphasizes modern Java and Spring Boot development:
*Note: This is a learning project focused on mastering Java fundamentals and Spring Boot best practices.*


### Java Core Concepts
- **Object-Oriented Programming** - Interfaces, abstract classes, inheritance, and polymorphism
- **Custom Annotations** - Creating and implementing custom validators (`@LowerCase`)
- **Collections & Streams** - Working with lists, maps, and stream operations
- **Exception Handling** - Custom exceptions and global error handling patterns
- **Generics** - Type-safe collections and method implementations

### Spring Boot Framework
- **RESTful API Design** - Controllers, request/response handling, and HTTP methods
- **Spring Security** - JWT authentication, custom filters, stateless sessions, and role-based access control
- **Dependency Injection** - IoC container, bean lifecycle, and component scanning
- **Configuration Management** - Profile-based configuration (dev/prod), externalized properties
- **Validation Framework** - Bean validation with custom validators
- **DTO Pattern & MapStruct** - Object mapping and separation of concerns
- **Design Patterns** - Strategy pattern (notifications), Gateway pattern (payments), Builder pattern
- **Global Exception Handling** - Centralized error handling with `@ControllerAdvice`
- **JPA & Database** - Spring Data JPA interfaces for database operations



## Tech Stack

- **Java 17** - Modern Java features
- **Spring Boot 3.5.3** - Latest Spring Boot framework
- **Spring Data JPA** - Database abstraction layer
- **MySQL** - Primary database
- **H2 Database** - In-memory database for testing
- **Lombok** - Reduces boilerplate code
- **Docker** - Containerization
- **Maven** - Dependency management

## Getting Started

### Prerequisites

- Docker and Docker Compose

### Quick Start with Docker Compose

1. **Clone the repository:**

```bash
git clone https://github.com/sidd6p/Spring-Boot-Store-API.git
cd Spring-Boot-Store-API
```

2. **Create `.env` file in the root directory:**

```env
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_USER=storeuser
MYSQL_PASSWORD=storepassword
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key_here
STRIPE_WEBHOOK_SECRET=whsec_your_stripe_webhook_secret_here
```

3. **Start everything with Docker Compose:**

```bash
docker-compose up -d --build
```

This will:
- Start MySQL database with initialization scripts
- Build and start the Spring Boot application
- Expose the API at `http://localhost:8080`

4. **Stop the application:**

```bash
docker-compose down
```

## API Documentation

### Swagger UI

Once the application is running, you can access the interactive API documentation through Swagger UI:
`http://localhost:8080/swagger-ui/index.html`

### Actuator Endpoints

`http://localhost:8080/actuator/info`

### Postman Collection

A complete Postman collection is included in the repository for testing all API
endpoints: [Spring-Boot-Store-API.postman_collection.json](./Spring-Boot-Store-API.postman_collection.json)

To use the Postman collection:

1. Import the collection file into Postman
2. Set up environment variables as needed
3. Test all endpoints with pre-configured requests

## Learning Resources

This project is based on learning from:

- [Code with Mosh Spring Boot tutorials](https://members.codewithmosh.com/courses)
- [FreeCodeCamp](https://www.freecodecamp.org/)
- Spring Boot official documentation

## Contact

- **GitHub**: [@sidd6p](https://github.com/sidd6p)
- **Email**: siddpurwar@gmail.com
