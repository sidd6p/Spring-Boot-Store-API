# Spring Boot Store API
Welcome! 👋

This is a Spring Boot REST API project that I've built while learning Java Spring Boot following [Code with Mosh Spring Boot tutorials](https://members.codewithmosh.com/courses). 
> The primary focus is on mastering JPA, Spring Data, and Java core concepts rather than creating a fully functional e-commerce store. The project demonstrates modern Spring Boot development practices with database integration and clean architecture patterns.

## Table of Contents
- [What's Inside](#whats-inside)
- [Learning Focus](#learning-focus)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Features](#api-features)
- [Database Schema](#database-schema)
- [Configuration](#configuration)
- [Testing](#testing)
- [Contributing](#want-to-contribute)
- [Learning Resources](#learning-resources)
- [License](#license)
- [Contact](#contact)

## What's Inside
- **JPA & Spring Data JPA** - Entity relationships, repositories, and database operations
- **Java Core Concepts** - Dependency injection, annotations, and Spring Boot fundamentals
- **Database Modeling** - Entity relationships and database design patterns
- **Spring Boot Architecture** - Service layer, repository pattern, and configuration
- **Docker Integration** - Containerized MySQL setup for development
- **Educational Design Patterns** - Examples of common software design patterns

## Learning Focus
This project emphasizes:
- **Entity Relationships** - One-to-One, One-to-Many, Many-to-Many mappings
- **Spring Data JPA** - Repository interfaces, custom queries, and database operations
- **Dependency Injection** - Spring's IoC container and bean management
- **Configuration Management** - YAML configuration and environment variables
- **Database Integration** - MySQL setup, schema validation, and data persistence

*Note: This is a learning project focused on backend concepts rather than a production-ready e-commerce API.*

## Tech Stack
- **Java 21** - Modern Java features
- **Spring Boot 3.5.3** - Latest Spring Boot framework
- **Spring Data JPA** - Database abstraction layer
- **MySQL** - Primary database
- **H2 Database** - In-memory database for testing
- **Lombok** - Reduces boilerplate code
- **Docker** - Containerization
- **Maven** - Dependency management

## Project Structure
```
📦 Spring-Boot-Store-API
├── src/main/java/com/github/sidd6p/store/
│   ├── StoreApplication.java         # Main application entry point
│   ├── Appconfig.java               # Application configuration
│   ├── HomeController.java          # Basic home controller
│   ├── entities/                    # Database Models
│   │   ├── User.java                # User entity with profile relationships
│   │   ├── Product.java             # Product catalog management
│   │   ├── Category.java            # Product categorization
│   │   ├── Tag.java                 # Product tagging system
│   │   ├── Address.java             # User address management
│   │   └── Profile.java             # User profile information
│   ├── repositories/                # Spring Data JPA repositories
│   │   ├── UserRepository.java
│   │   ├── ProductRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── AddressRepository.java
│   │   └── ProfileRepository.java
│   ├── services/                    # Service layer
│   │   ├── UserServices.java
│   │   └── ProductServices.java
│   ├── payement/                    # Payment service design patterns (educational)
│   │   ├── PaymentService.java
│   │   ├── StripePaymentService.java
│   │   └── PayPalPayementService.java
│   ├── notification/                # Notification system patterns (educational)
│   │   ├── NotificationService.java
│   │   ├── NotificationManager.java
│   │   ├── EmailNotificatonService.java
│   │   └── SMSNotificationService.java
│   └── order/                       # Order management patterns (educational)
│       └── OrderManager.java
├── src/main/resources/
│   ├── application.yaml             # Application configuration
│   └── static/
│       └── index.html               # Welcome page
├── Docker Setup
│   ├── docker-compose.yaml          # Multi-container orchestration
│   └── docker/mysql/                # MySQL initialization scripts
│       └── docker-entrypoint-initdb.d/
│           ├── 01_create_tables.sql # Database table definitions
│           ├── 02_insert_data.sql   # Sample data insertion
│           └── 03_create_procedures.sql # Stored procedures
├── learning/                        # Course materials
│   └── Database Integration with Spring Data JPA.pdf
└── Configuration Files
    ├── pom.xml                      # Maven dependencies
    ├── mvnw & mvnw.cmd              # Maven wrapper scripts
    └── README.md                    # This file
```

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- Docker and Docker Compose
- MySQL (or use Docker setup)

### 🐳 Quick Start with Docker
1. Clone the repository:
```bash
git clone https://github.com/sidd6p/Spring-Boot-Store-API.git
cd Spring-Boot-Store-API
```

2. Start the services with Docker:
```bash
docker-compose up -d
```

3. Set environment variables:
```bash
export MYSQL_USER=your_username
export MYSQL_PASSWORD=your_password
export MYSQL_ROOT_PASSWORD=your_root_password
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

### 🔧 Manual Setup
1. Install MySQL and create a database named `store_api`

2. Update `application.yaml` with your database credentials

3. Build and run:
```bash
./mvnw clean install
./mvnw spring-boot:run
```

## 📋 API Features

### 👥 User Management
- User registration and profile management
- Address management for users
- User-address and user-profile relationships

### 🛍️ Product Catalog
- Product CRUD operations
- Category-based organization
- Tag-based product classification
- Product search and filtering capabilities

### 📚 Educational Design Patterns
*Note: The following are educational examples to demonstrate design patterns and are not integrated with the actual store API:*
- **Payment System Examples** - Demonstrates strategy pattern with multiple payment gateways
- **Notification System Examples** - Shows observer pattern and service abstraction
- **Order Management Examples** - Illustrates command pattern and business logic organization

## 🗃️ Database Schema
The application uses MySQL with the following key relationships:
- Users have multiple Addresses and one Profile
- Products belong to Categories and can have multiple Tags
- Database schema is managed through JPA annotations and validated on startup

## ⚙️ Configuration
Key configuration options in `application.yaml`:
- Database connection settings
- Payment gateway configurations
- Notification service settings
- JPA/Hibernate properties

## 🧪 Testing
Run tests with:
```bash
./mvnw test
```

## 🤝 Want to Contribute?
Contributions are welcome! Whether you're fixing a bug, adding a new feature, or improving documentation, I'd love your help.

1. Fork the repo
2. Create a branch for your changes
3. Commit with a clear message
4. Open a pull request

If this project helps you learn Spring Boot, consider giving it a ⭐ — it really helps!

## 📚 Learning Resources
This project is based on learning from:
- [Code with Mosh Spring Boot tutorials](https://members.codewithmosh.com/courses)
- [FreeCodeCamp](https://www.freecodecamp.org/)
- Spring Boot official documentation

## 📜 License
This project uses the Apache License 2.0. See the LICENSE file for details.

## 📬 Contact
- **GitHub**: [@sidd6p](https://github.com/sidd6p)
- **Email**: siddpurwar@gmail.com

Thanks for checking out this Spring Boot learning project! 🚀
