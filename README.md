# Gym Membership Management System

## Prerequisites:
- **Java**: Java 25 (developed and tested using Java 25, Spring Boot 4.0.6 requires at least Java 17)
- **Build Tool**: Maven 3.9.14 (configured via included Maven Wrapper, compatible with Maven 3.6.3+)


## Build and Run instructions
To build the project and run all tests, use ```./mvnw clean install```

To run the application, use ```./mvnw spring-boot:run```

Alternatively, use ```./mvnw clean install spring-boot:run``` to build, test and run the application at once.

Once started, the application will be available at ```http://localhost:8080```

*Note: All commands assume being in the main project directory and using included Maven wrapper. If you have Maven configured locally you can substitute ```./mvnw``` with ```mvn```.*

## Database Access
Project uses an H2 in-memory database.
- **H2 Console**: ```http://localhost:8080/h2-console```
- **JDBC URL**: ```jdbc:h2:mem:testdb```
- **Credentials**: User: ```sa``` / Password: ``` ``` (empty)

## REST API Endpoints 
Base URL: ```http://localhost:8080/api```

*Note: All endpoints communicate using JSON format.*

- ```/gyms ``` - **[POST]** - Create a new gym
- ```/gyms``` - **[GET]** - List all gyms
- ```/gyms/{id}/membership-plans``` - **[POST]** - Create a new membership plan for a given gym
- ```/gyms/{id}/membership-plans``` - **[GET]** - List all membership plans for a given gym
- ```/membership-plans/{id}/members``` - **[POST]** - Register a new member to a given membership plan
- ```/members ``` - **[GET]** - List all members along with their plan name and gym name
- ```/members/{id}/cancel``` - **[PATCH]** - Cancel a membership
- ```/gyms/revenue``` - **[GET]** - Return the revenue report

## Data Validation
The application enforces data integrity and validation rules: 
- **Request Validation**: All request fields in DTOs along with path id fields are mandatory and are also validated by length, type and correctness of data
- **Database Constraints**: All columns enforce ```NOT NULL``` constraints. Text fields are further validated using ```@NotBlank```. Other data constraints from Request Validation layer are also mirrored here

Refer to the API Documentation section for specific request formats, data types, and validation constraints.

## Sample queries
Sample queries are located in ```src/test/http/``` directory, numbered in order of recommended usage:
- ```01_create_gym_requests.http``` - sample gym creation POST requests, GET all gyms
- ```02_create_membership_plan_requests.http``` - sample membership plan creation POST requests, GET membership plans for Gym 1, 4, 2
- ```03_create_member_requests.http``` - sample member creation POST requests, GET all members
- ```04_other_requests.http``` - GET revenue report, GET all members, PATCH cancel Member 5 membership, GET revenue report, GET all members

Queries use IntelliJ HTTP Client variables (e.g., `{{gym_id_1}}`) to streamline the testing workflow. In the case of manually executing those requests ensure that all ```{{variable_name}}``` placeholders are replaced with actual IDs.

## API Documentation
API Documentation provides a detailed overview of the available endpoints, including request formats and input validation constraints.
- **OpenAPI Specification**: ```http://localhost:8080/api-docs``` (JSON format)
- **Swagger UI**: ```http://localhost:8080/swagger-ui/index.html``` (Interactive interface)