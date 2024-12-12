# Project - A Simplified Payment Gateway (Java)

## Description

This is the Java implementation of the [Payment Gateway challenge](https://github.com/cko-recruitment/).

## Table of Contents

- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Build and Run](#build-and-run)
- [Usage](#usage)
  - [REST Interface](#rest-api-interface-overview)
- [Architecture Overview and Design Patterns](#architecture-overview-and-design-patterns)
  - [Project Structure](#project-structure)
  - [Design Patterns](#design-patterns)
- [Testing](#testing)
  - [Testing Strategy](#testing-strategy)
  - [Testing Structure](#testing-structure)
  - [Postman Testing](#postman-testing)
- [FAQ](#faq)
- [References](#references)

## Getting Started

### Prerequisites

- Java (version 17)
- Gradle (version 7.3 or higher)
- Docker

### Build and Run

Clone the repository:

```bash
git clone https://github.com/impedansa/payment-gateway-challenge-java.git
```

Build the project using Gradle:
```bash
./gradlew build
```

Start the bank simulator:
```bash
docker-compose up
```

Run the project using Gradle:
```bash
./gradlew bootRun
```

## Usage

### REST API Interface Overview
The project provides a simple way to process and retrieve payment transactions.
It includes the following endpoints for managing payment operations:

**1. Get Payment by ID**  
Endpoint:
```
GET /payment/{id}
```

**Description:** Retrieves details of a processed payment using a unique payment ID.  
**Request Parameters:**
`id` (Path Parameter, UUID) - The unique identifier of the payment to be retrieved.  
**Response:** Returns the payment details.
- Example response:  
  **HTTP 200 OK**
```
{
  "id": "a1b2c3d4-e5f6-7890-1234-56789abcdef0",
  "status": "Authorized",
  "cardNumberLastFour": 8877,
  "expiryMonth": 4,
  "expiryYear": 2025,
  "currency": "GBP",
  "amount": 100
}
```

**2. Process Payment**  
Endpoint:
```
POST /payment
```

**Description:** Processes a new payment transaction with the provided payment details.  
**Request Body:**
- Example Request:
```
{
  "card_number": "2222405343248877",
  "expiry_month": 4,
  "expiry_year": 2025,
  "currency": "GBP",
  "amount": 100,
  "cvv": "123"
}
```
**Response:** Returns the payment processing result.
- Example response:  
  **HTTP 200 OK**:
```
{
  "id": "a1b2c3d4-e5f6-7890-1234-56789abcdef0",
  "status": "Authorized",
  "cardNumberLastFour": 8877,
  "expiryMonth": 4,
  "expiryYear": 2025,
  "currency": "GBP",
  "amount": 100
}
```

The project includes Swagger UI for interactive API documentation and testing.
You can access it at http://localhost:8090/swagger-ui/index.html.
This provides a detailed overview of all available endpoints, their request/response structures, and allows you to test the API directly from the browser.

## Architecture Overview and Design Patterns

This project follows a Layered Architecture (N-Tier Architecture), with clear separation of concerns between the controller, service, repository, and other components.
This architecture is common in modern Spring Boot applications, as it ensures separation of concerns, scalability, modularity, and ease of maintenance.

### Project Structure

* **src/main/java/com/checkout/payment/gateway/actuator**: Contains classes dedicated to implementing custom health and information checks for the application, enhancing observability via Spring Boot Actuator.
* **src/main/java/com/checkout/payment/gateway/configuration**: Contains key classes responsible for setting up and managing various aspects of the application.
* **src/main/java/com/checkout/payment/gateway/controller**: Contains the REST API controller responsible for handling incoming HTTP requests and processing them through the service layer.
* **src/main/java/com/checkout/payment/gateway/enums**: Contains enum classes that define a set of constants used throughout the application.
* **src/main/java/com/checkout/payment/gateway/exception**: Contains custom exception classes and global exception handling logic for managing errors throughout the application.
* **src/main/java/com/checkout/payment/gateway/model**: Contains the domain models and response/request objects used to represent the structure of data in the application. These classes define the format for the input and output data, ensuring that the data passed through the system follows a consistent structure.
* **src/main/java/com/checkout/payment/gateway/repository**: Contains the class responsible for the in-memory storage of payment transactions.
* **src/main/java/com/checkout/payment/gateway/service**: Contains the core business logic of the application. The service orchestrates the payment processing flow, including validation, communication with external systems, and storing payment data.
* **src/main/java/com/checkout/payment/gateway/util**: Contains helper classes and utilities that provide common functionality. It encapsulates the logic for making HTTP requests with retries, centralizing error handling and retry logic.
* **src/main/java/com/checkout/payment/gateway/validator**: Contains classes responsible for validating the data used in the payment processing workflow. These validators ensure that the incoming data adheres to the required format and business rules before being processed further.
* **src/main/resources**: Contains a configuration file that defines application settings and external dependencies.
* **postman-resources**: Contains supplementary Postman resources to aid in testing and understanding the application.
* **src/test/java/com/checkout/payment/gateway/controller**: Contains unit and integration tests for the controller layer of the Payment Gateway application.
* **src/test/resources**: Contains the test resources required for the Payment Gateway application's parameterized tests, specifically for verifying the payment processing workflow.

### Design Patterns

* **Service Layer Pattern:** Encapsulates the business logic and transactions. The PaymentGatewayService coordinates the payment processing logic.
* **Repository Pattern:** Abstracts data access. The PaymentsRepository provides methods for storing and retrieving payment data.
* **Controller-View Separation:** The controller layer (via @RestController) handles the HTTP communication, while the service layer handles business logic and data manipulation.
* **Singleton Pattern:** Many of the components (e.g., PaymentGatewayService, PaymentsRepository) are managed by Spring as singleton beans, ensuring shared instances across the application.
* **Decorator Pattern:** The HttpUtils class uses retry logic (@Retryable) to add retry functionality to the HTTP request process without changing the underlying HTTP code.
* **Builder Pattern:** Used for constructing complex objects in a flexible and readable way. The @Builder annotation is applied to several model classes (e.g., BankPaymentRequest, PostPaymentResponse) to enable fluent object creation, improve readability, and ensure immutability.
* **Dependency Injection**: Constructor injection is employed throughout the project for better dependency management and testing. For example, `PaymentGatewayController` injects `PaymentGatewayService` via its constructor, ensuring clear dependencies and promoting immutability. In some cases, `@AllArgsConstructor` is used to generate constructors for dependency injection automatically, simplifying the code and maintaining consistency with the constructor injection approach.

## Testing
The testing framework for this project is designed to ensure robust and reliable validation of the application's functionality. It leverages Spring Boot's testing capabilities, parameterized testing, and JSON-based data-driven tests to simulate a wide variety of real-world scenarios.  
You can run the tests using the following Gradle command:

```bash
./gradlew test
```

### Testing Strategy

The testing approach focuses on the following objectives:
* **Comprehensive Coverage:** Validates both successful and error-prone payment processing scenarios.
* **Reproducibility:** Ensures consistent behavior under different input conditions by using JSON-based test cases.
* **Isolation:** Tests are independent of external services by using `MockRestServiceServer` to simulate responses from third-party systems.
* **Parameterization:** Employs parameterized tests to streamline the execution of multiple scenarios with minimal boilerplate code.

### Testing Structure

* **Parameterized Tests:**
  The `processPayments` test in `PaymentGatewayControllerTest` is a parameterized test that dynamically loads multiple test cases from a JSON file (`payment-request.json`).  
  The test dynamically validates the application's behavior against the provided expectations.  
  Each test case encapsulates:
  * A descriptive title for logging (`testTitle`).
  * Input data, including the payment request payload and expected bank responses.
  * Expected application behavior, such as response status, output, or exceptions.
* **Unit and Integration Testing:**
  The test suite combines unit and integration testing principles to validate different layers of the application.  
  Examples include:
  * Unit Testing: Isolated tests for specific methods like `whenPaymentWithIdDoesNotExistThen404IsReturned`, which verifies repository interactions.
  * Integration Testing: End-to-end validation of controller, service, and repository layers via mock HTTP requests and responses.

This testing structure ensures:
* **Scalability:** Adding new test cases is as simple as appending a JSON object to the test data file.
* **Maintainability:** Centralized test data reduces duplication and ensures consistency across tests.
* **Transparency:** Parameterized tests with descriptive titles provide clear insights into test objectives and results.
* **Reliability:** Mocking and data-driven testing ensure reproducibility and accuracy even when external systems are unavailable or unreliable.

### Postman Testing

The Postman collection included in this project covers a range of endpoints for testing the `Payment Gateway Service` and the `Bank Simulator Service`.
The collection and associated environment are designed to simplify testing by allowing dynamic configurations using environment variables.
Postman collections are available in the `postman-resources` folder.
You can import the provided Postman collection and environment to easily perform API testing:
* Postman Collection: `postman-resources/payment-gateway-service.postman_collection.json`
* Postman Environment: `postman-resources/checkout-env.postman_environment.json`

**Endpoints Covered:**
* **Payment Gateway Service:**
  * **GET** `/payment/{payment-id}`: Retrieve a payment by its ID.
  * **POST** `/payment (Authorized)`: Process a payment with valid details resulting in authorization.
  * **POST** `/payment (Declined)`: Process a payment that is expected to be declined.
  * **POST** `/payment (Rejected)`: Process a payment that is expected to be rejected due to validation issues.
  * **GET** `/actuator/info`: Retrieve service metadata.
  * **GET** `/actuator/health`: Check the service's health status.
* **Bank Simulator Service:**
  * **POST** `/payments` (True): Simulate a successful bank transaction.
  * **POST** `/payments` (False): Simulate a failed bank transaction.  
    **Features:**
  * **Environment Variables:**
    Variables like `payment-id`, `payment-gateway-service-url`, and `bank-simulator-service-url` allow for dynamic URL and data configurations without modifying the collection directly.
  * **Automation with Scripts:**
    Test scripts in some endpoints automatically update environment variables, such as saving the `payment-id` from responses for reuse in subsequent requests.

## FAQ

**1. Why are reference data types used instead of primitive types in the model classes?**  
Using Non-primitive Data Types provides flexibility and future-proofing for several reasons:
* **Nullability Support:** Reference types allow null values, essential for representing missing or optional data, especially when fields might be omitted.
* **Contract Evolution:** They accommodate changes in contracts where fields may become optional, ensuring models handle such cases gracefully.
* **Framework Compatibility:** Frameworks like Hibernate and Jackson often handle reference types better for serialization, deserialization, and database interactions involving nullable fields.
* **Default Value Avoidance:** Primitive types default to values like 0 (int), which can lead to ambiguity. Reference types make the absence of data explicit.
* **Type Consistency:** Reference types work seamlessly with collections and APIs that require boxed types, ensuring consistent behavior.

**2. Why are unit and integration tests combined in one class?**  
Combining both unit tests and integration tests in a single class can be a deliberate choice, especially when testing scenarios that involve closely interwoven components or layers.
* **Holistic Coverage for Specific Features:**
  The `PaymentGatewayControllerTest` class focuses on end-to-end scenarios for processing payments. Combining unit and integration tests ensures that both the business logic and the underlying integrations (e.g., REST API, database interactions) are verified within the same context.
  This approach avoids scattering the tests across multiple classes, simplifying maintenance for a feature with tightly coupled logic.
* **Simplified Test Setup:**
  By sharing the test setup (e.g., MockMvc, MockRestServiceServer, paymentsRepository), the testing class reduces redundancy and avoids duplicating common configurations for individual unit and integration test cases.
* **Logical Grouping of Scenarios:**
  Keeping related unit and integration tests together makes it easier to see the progression from isolated unit tests (focused on controller logic) to integration scenarios (validating behavior with external dependencies like REST APIs and databases).
* **Testing Complex Interactions:**
  Payment processing involves a mix of controller logic and integration with external services (e.g., bank simulator service). Some scenarios require testing a single layer (e.g., validating input and responses) while others necessitate testing end-to-end workflows.
  This structure accommodates both types without unnecessary separation.
* **Improved Traceability and Context:**
  Grouping tests by feature (rather than by test type) improves traceability. When a test fails, it’s easier to determine whether the failure is due to logic or integration issues because the context remains consistent.
* **Streamlined Parameterized Testing:**
  Parameterized tests can span unit and integration scenarios. For example, the parameterized processPayments test simultaneously validates controller-level logic, input validation, and interaction with the mocked bank API, bridging the gap between unit and integration testing.
* **Project-Specific Design Philosophy:**
  In projects where integration between components is critical (e.g., financial transactions), it’s practical to prioritize testing real workflows over artificially segregating unit and integration tests. This structure reflects a pragmatic balance tailored to the requirements of payment processing.

**3. Why is `RestTemplate` used instead of `WebClient`?**  
In this project, I opted to use `RestTemplate` instead of `WebClient` primarily due to the nature of the application's design and requirements:
* **Non-Reactive Programming Model:** The project uses a traditional, non-reactive programming approach. Since we are not leveraging a reactive stack (e.g., `Spring WebFlux`) and the application flow doesn't require asynchronous or non-blocking processing, `RestTemplate` aligns better with the existing architecture.
* **No Database or Reactive Pipelines:** Reactive programming is most effective when there are asynchronous data flows involving databases, message queues, or external services. In this assignment, there is no database involved, and we mock the bank endpoint rather than connecting to an actual asynchronous service, reducing the need for a reactive client like `WebClient`.
* **Simplicity and Familiarity:** `RestTemplate` is widely understood and simpler to implement for straightforward, blocking HTTP calls. This makes it a practical choice for this assignment, where simplicity and clarity take precedence over adopting a fully reactive stack.
* **Mocking External Services:** In this project, we use tools like `MockRestServiceServer` to mock bank interactions during testing. While `WebClient` also supports mocking, the existing `RestTemplate` approach integrates seamlessly with these testing mechanisms.

In a Real-World Scenario: If the application were designed with a reactive architecture—for example, if the entire flow, including database access and external service calls, was reactive—using `WebClient` would indeed make more sense. It would allow us to take full advantage of non-blocking I/O, improve scalability, and align better with modern application design patterns.

**4. Why was `GetPaymentResponse` deleted and only `PostPaymentResponse` is used?**  
For simplicity and to reduce complexity in this implementation, `PostPaymentResponse` is reused for both the `/payment` and `/payment/{id}` endpoints. However, I am fully aware that **best practices recommend having separate models for each endpoint**.
This approach ensures clear separation of concerns, maintains flexibility for future changes, and avoids potential issues with overloaded or ambiguous response structures. If requirements for the two endpoints diverge in the future, separate models will be introduced to adhere to these best practices.  
Here are the pros and cons of using the same model class for both endpoints:

* **Pros**
  * **Simplifies the Codebase:**
    Reusing `PostPaymentResponse` reduces the number of classes, making the codebase simpler and easier to manage.
    It minimizes duplication and potential inconsistencies that could arise if separate but similar classes like `GetPaymentResponse` and `PostPaymentResponse` were maintained.
  * **Consistency in Response Structure:** Both endpoints return the same data structure, ensuring a consistent API response format. This can make the API easier to understand and use for clients.
  * **Reduced Development Effort:** Maintaining a single response class reduces the effort needed for updates or bug fixes, as you only need to modify one class instead of multiple.
  * **Easier Testing:** Test cases for the response structure can be shared between the endpoints, reducing redundancy and improving test coverage efficiency.
* **Cons**
  * **Potential for Overloading the Response Class:** The response for `/payment` (POST) might have slightly different requirements or fields compared to `/payment/{id}` (GET). Using the same class might lead to unused fields or ambiguous data, which could confuse consumers of the API.
  * **Less Flexibility:** If the two endpoints need to evolve independently (e.g., `/payment/{id}` starts returning additional fields like payment history or audit details), a single class might become less suitable, requiring modifications that could affect both endpoints unintentionally.
  * **Violation of Single Responsibility Principle:** A single class serving multiple purposes could blur its responsibility, making it harder to refactor or extend in the future.
  * **Client-Side Parsing Issues:** API clients consuming the endpoints might expect the responses to differ (e.g., POST responses for creation might include fewer details than GET responses for retrieval). Reusing the same response class could lead to misinterpretation of data.

**4. Why was Swagger documentation generated using annotations and not a YAML configuration file?**
For this project, Swagger documentation was generated using annotations because:
* **Small Project Scope:** The project has a limited number of endpoints, and using annotations was a faster and simpler solution.
* **Time Efficiency:** Creating a YAML file for such a small project would have been time-consuming without providing significant benefits.
  Annotations ensured the documentation was tightly coupled with the implementation, minimizing effort and avoiding the risk of outdated or inconsistent documentation. In a larger, more complex project, YAML would likely be the preferred choice for its flexibility and separation of concerns.

Here are some of the pros and cons for both approaches:
* **Annotations**
  * **Pros**
    * **Integration with Code:** Annotations are placed directly in the codebase, making the documentation always in sync with the actual implementation.
    * **Reduced Maintenance:** As the API evolves, changes in annotations update the Swagger documentation automatically.
    * **Quick to Implement:** For small projects or projects with a limited number of endpoints, adding annotations is straightforward and less time-consuming compared to creating and maintaining a separate YAML file.
    * **Developer-Friendly:** No need for external tools or familiarity with OpenAPI YAML/JSON syntax; developers work within the same environment.
    * **IDE Support:** IDEs provide autocomplete and error-checking for annotations, reducing the chance of mistakes.
  * **Cons**
    * **Cluttered Code:** Adding annotations can make the code more verbose and harder to read.
    * **Limited Customization:** Complex API designs might be harder to represent with annotations, and additional configurations may become cumbersome.
    * **Portability Issues:** Documentation tied to code may not be as portable as a standalone YAML file when sharing with external teams or third parties.
* **YAML File**
  * **Pros**
    * **Separation of Concerns:** The API documentation resides separately from the codebase, keeping the code cleaner and easier to read.
    * **Greater Flexibility:** YAML files support extensive customization and are better suited for describing complex APIs.
    * **Standardized Format:** YAML files can be shared or reused easily across different projects or tools without requiring access to the source code.
    * **Enhanced Collaboration:** Non-developers (e.g., product managers, API designers) can contribute to the documentation without needing to modify the code.
  * **Cons**
    * **Higher Maintenance Effort:** Changes to the code must be manually synced with the YAML file, increasing the risk of inconsistencies.
    * **Steeper Learning Curve:** Teams need to be familiar with the OpenAPI YAML/JSON syntax, which might require additional training or tools.
    * **Time-Consuming:** For small projects or simple APIs, creating and maintaining a YAML file might be overkill and add unnecessary overhead.

## References

* https://medium.com/devdomain/spring-boots-autowired-vs-constructor-injection-a-detailed-guide-1b19970d828e
* https://medium.com/hprog99/transitioning-from-resttemplate-to-webclient-in-spring-boot-a-detailed-guide-4febd21063ba#:~:text=As%20of%20Spring%20Framework%205,effectively%20transition%20with%20practical%20examples.
* https://medium.com/@bubu.tripathy/resttemplate-tips-for-efficient-and-secure-communication-with-external-apis-3f323ac5b83f
* https://www.linkedin.com/pulse/integrating-swagger-spring-boot-yaml-vs-annotations-k%C4%81sh%C4%81n-asim-gssve/
* https://medium.com/javarevisited/spring-resttemplate-request-response-logging-f021be66c2c0
* https://symflower.com/en/company/blog/2023/best-practices-for-spring-boot-testing/
* https://medium.com/@techphile/parameterized-tests-using-json-file-as-input-source-516a8eda8a87