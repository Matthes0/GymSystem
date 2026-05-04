# Gym Membership Management System

***

## Prerequisites:
- **Java**: Java 25 (developed and tested using Java 25, Spring Boot 4.0.6 requires at least Java 17)
- **Build Tool**: Maven 3.9.14 (configured via included Maven Wrapper, compatible with Maven 3.6.3+)

***

## Build and Run instructions
To build the project and run all tests, use ```./mvnw clean install```

To run the application, use ```./mvnw spring-boot:run```

Alternatively, use ```./mvnw clean install spring-boot:run``` to build, test and run the application at once.

Once started, the application will be available at ```http://localhost:8080```

*Note: All commands assume being in the main project directory and using included Maven wrapper. If you have Maven configured locally you can substitute ```./mvnw``` with ```mvn```.*

***

## Database Access
Project uses an H2 in-memory database.
- **H2 Console**: ```http://localhost:8080/h2-console```
- **JDBC URL**: ```jdbc:h2:mem:testdb```
- **Credentials**: User: ```sa``` / Password: ``` ``` (empty)

***

## REST API Endpoints 
Base URL: ```http://localhost:8080/api```
- /gyms [GET] List all gyms
- /gyms [POST] Create a new gym
- /gyms/revenue [GET] Return the revenue report 
- /members [GET] List all members with plan name, gym name and status
- /members/{id}/cancel [PATCH] Cancel a membership
- /membership-plans/{id}/members [POST] Register a new member to a given membership plan
- /gyms/{id}/membership-plans [GET] List all membership plans for a given gym
- /gyms/{id}/membership-plans [POST] Create a new membership plan for a given gym

***

## API Documentation
- **OpenAPI Specification**: ```http://localhost:8080/api-docs``` (JSON format)

- **Swagger UI**: ```http://localhost:8080/swagger-ui/index.html``` (Interactive interface)