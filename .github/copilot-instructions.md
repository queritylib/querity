# Copilot Project Instructions

This document provides essential information for GitHub Copilot to understand the Querity project structure and tech
stack.

## Project Overview

Querity is an open-source, extensible Java query builder for SQL and NoSQL databases. It is modular, supporting various
database technologies via dedicated modules.

## Tech Stack

- **Language:** Java (minimum version 17)
- **Frameworks:**
    - Spring Framework 6
    - Spring Boot 3 (optional, recommended for simplicity)
- **Build Tool:** Maven (with wrapper scripts)
- **Testing:** JUnit (standard for Java projects)
- **Code Quality:** SonarCloud integration
- **Other:** Lombok (for code generation)

## Main Modules

- **querity-api:** Core API definitions
- **querity-common:** Shared utilities and abstractions
- **querity-jpa:** Jakarta Persistence API support (plain JPA)
- **querity-jpa-common:** Common JPA logic
- **querity-jpa-common-test:** JPA test utilities
- **querity-parser:** Query language parser
- **querity-spring-data-jpa:** Spring Data JPA integration
- **querity-spring-data-mongodb:** Spring Data MongoDB integration
- **querity-spring-data-elasticsearch:** Spring Data Elasticsearch integration
- **querity-spring-web:** Spring Web MVC support (JSON serialization/deserialization)
- **querity-test:** Test utilities and datasets

## Usage Patterns

- Queries are built using a fluent API or a simple query language (via `querity-parser`).
- Integration with Spring and JPA is modular; import only the modules needed for your project.
- All Spring modules are "Spring Boot starters" for easy integration.

## Directory Structure

- Each module is a Maven subproject with its own `pom.xml` and `src/main`/`src/test` directories.
- The root project contains a parent `pom.xml` with common configurations and dependencies, and Maven Wrapper scripts (
  `mvnw`, `mvnw.cmd`).

## Development Notes

- Use Maven Wrapper for building and testing the project (`./mvnw` on Linux or `.\mvnw.cmd` on Windows).

## Testing

- You should aim for complete test coverage in each module using unit tests and integration tests.
- A generic integration test suite is provided in
  `querity-test/src/main/java/io/github/queritylib/querity/test/QuerityGenericTestSuite.java`, this file gets extended
  in other modules to ensure consistent behavior across different database implementations, specifically:
    - `querity-jpa/src/test/java/io/github/queritylib/querity/jpa/QuerityJpaImplTests.java`
      abstract class for plain JPA (there is actually one concrete classes for H2)
    - `querity-spring-data-jpa/src/test/java/io/github/queritylib/querity/spring/data/jpa/QuerityJpaImplTests.java`
      abstract class for Spring Data JPA (there are actually two concrete classes: one for H2 and one for PostgreSQL)
    -
    `querity-spring-data-mongodb/src/test/java/io/github/queritylib/querity/spring/data/mongodb/QuerityMongodbImplTests.java`
    for Spring Data MongoDB
    -
    `querity-spring-data-elasticsearch/src/test/java/io/github/queritylib/querity/spring/data/elasticsearch/QuerityElasticsearchImplTests.java`
    for Spring Data Elasticsearch
- You should avoid implementing integration tests specific for a single database technology; instead, use the generic
  test suite wherever possible.

## References

- [Querity Documentation](https://queritylib.github.io/querity)

---
This file is intended for Copilot and other automation tools to understand the structure, tech stack, and integration
points of the Querity project.
