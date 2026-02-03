# AGENTS.md - AI Coding Agent Instructions

This document provides instructions for AI coding agents working on the Querity project.

## Project Overview

Querity is an open-source, extensible Java query builder for SQL and NoSQL databases. It is modular, supporting JPA,
MongoDB, and Elasticsearch via dedicated modules.

**Tech Stack:**

- Java 17+
- Spring Framework 6 / Spring Boot 3
- Maven (with wrapper scripts)
- JUnit 5 + AssertJ for testing
- Lombok for boilerplate reduction
- Jackson for JSON serialization

## Build Commands

```bash
# Build and run all tests (from querity/ directory)
./mvnw verify

# Compile only (skip tests)
./mvnw compile -DskipTests

# Run all tests
./mvnw test

# Run tests for a specific module
./mvnw test -pl querity-api
./mvnw test -pl querity-jpa-common
./mvnw test -pl querity-spring-data-jpa

# Run a single test class
./mvnw test -pl querity-api -Dtest=ConditionTests

# Run a single test method
./mvnw test -pl querity-api -Dtest=ConditionTests#givenNoOperator_whenBuildSimpleCondition_thenReturnEqualsCondition

# Clean build
./mvnw clean verify
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Project Structure

```
querity/
├── querity-api/           # Core API definitions (Query, Condition, Operator, etc.)
├── querity-common/        # Shared utilities and abstractions
├── querity-jpa/           # Plain Jakarta Persistence API support
├── querity-jpa-common/    # Common JPA logic shared between JPA modules
├── querity-jpa-common-test/ # JPA test utilities
├── querity-spring-data-jpa/       # Spring Data JPA integration
├── querity-spring-data-mongodb/   # Spring Data MongoDB integration
├── querity-spring-data-elasticsearch/ # Spring Data Elasticsearch integration
├── querity-spring-web/    # Spring Web MVC support (JSON de/serialization)
├── querity-parser/        # Query language parser (textual query syntax)
└── querity-test/          # Test utilities and shared test datasets
```

## Code Style Guidelines

### Formatting

- **Indentation:** 2 spaces (no tabs)
- **Line endings:** LF (Unix style)
- **Charset:** UTF-8
- **Trailing whitespace:** Remove
- **Final newline:** Insert

### Imports

- Use specific imports, avoid wildcards in production code
- Static imports are encouraged for test readability and fluent API usage:
  ```java
  import static io.github.queritylib.querity.api.Operator.*;
  import static io.github.queritylib.querity.api.Querity.*;
  import static org.assertj.core.api.Assertions.assertThat;
  ```
- Order: java.*, jakarta.*, third-party, project classes

### Naming Conventions

- **Classes:** PascalCase (e.g., `PropertyValueExtractor`, `JpaOperatorMapper`)
- **Interfaces:** PascalCase, no `I` prefix (e.g., `Querity`, `Condition`)
- **Methods:** camelCase (e.g., `filterBy`, `getPropertyName`)
- **Constants:** UPPER_SNAKE_CASE (e.g., `PROPERTY_LAST_NAME`)
- **Test classes:** `{ClassName}Tests` (e.g., `ConditionTests`, `QueryTests`)
- **Test methods:** `given{Precondition}_when{Action}_then{ExpectedResult}`

### Lombok Usage

- Use `@Builder`, `@Getter`, `@EqualsAndHashCode`, `@ToString` for DTOs/value objects
- Use `@NoArgsConstructor(access = AccessLevel.PRIVATE)` for utility classes
- Use `@Jacksonized` with `@Builder` for JSON deserialization support
- Lombok config: `lombok.addLombokGeneratedAnnotation = true` (for code coverage)

### Types and Generics

- Prefer interfaces over concrete types in method signatures
- Use bounded wildcards appropriately (`? extends`, `? super`)
- Suppress warnings explicitly when unchecked casts are necessary:
  ```java
  @SuppressWarnings({"unchecked", "rawtypes"})
  ```

### Error Handling

- Throw `IllegalArgumentException` for invalid method arguments
- Throw `UnsupportedOperationException` for unimplemented features
- Use descriptive error messages explaining what went wrong
- Validate inputs in builder `build()` methods

### Documentation

- Use Javadoc for public API methods with `@param`, `@return`, `@see`
- Include code examples in Javadoc using `{@code}` or `<pre>{@code ... }</pre>`
- Document backend-specific behavior (JPA vs MongoDB vs Elasticsearch)

## Testing Guidelines

### Test Structure

- Use JUnit 5 with AssertJ assertions
- Use `@Nested` classes to group related tests
- Follow the given-when-then naming pattern for test methods

### Integration Tests

- A generic test suite exists in `querity-test/src/main/java/.../QuerityGenericTestSuite.java`
- Database-specific tests extend this suite for consistent behavior across backends
- Avoid database-specific test implementations; use the generic suite when possible

### Test Examples

```java
@Test
void givenFilterByIdEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy("id", EQUALS, entity1.getId()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).containsExactly(entity1);
}
```

### Coverage Requirements

- JaCoCo enforces 80% line coverage per class
- Excluded from coverage: generated ANTLR parser classes (`QueryLexer`, `QueryParser*`)

## API Design Patterns

### Fluent Builder Pattern

```java
Query query = Querity.query()
    .filter(filterBy("status", EQUALS, "ACTIVE"))
    .sort(sortBy("lastName", DESC))
    .pagination(1, 10)
    .build();
```

### Static Factory Methods

The `Querity` interface provides static factory methods:

- `filterBy()`, `filterByField()`, `filterByNative()`
- `sortBy()`, `sortByNative()`
- `selectBy()`, `selectByNative()`
- `and()`, `or()`, `not()`
- `prop()`, `lit()` for function expressions
- `upper()`, `lower()`, `length()`, `concat()`, etc. for functions

### Native Expression Support

JPA modules support native expressions using functional interfaces:

```java
OrderSpecification<Person> spec = (root, cb) -> cb.asc(cb.length(root.get("name")));
Query query = Querity.query().sort(sortByNative(spec)).build();
```

## Module Dependencies

- All modules depend on `querity-api`
- `querity-common` provides shared utilities used by implementations
- `querity-test` provides test fixtures used by implementation tests
- `querity-jpa-common-test` provides JPA-specific test utilities

## SonarCloud Integration

- Project is analyzed on SonarCloud: https://sonarcloud.io/summary/new_code?id=queritylib_querity
- Excluded from analysis: `querity-test/**`, `querity-jpa-common-test/**`

## GitHub Copilot Instructions

(From `.github/copilot-instructions.md`)

- Use Maven Wrapper for building: `./mvnw` (Linux) or `.\mvnw.cmd` (Windows)
- Aim for complete test coverage using unit and integration tests
- Use the generic integration test suite (`QuerityGenericTestSuite`) for consistent behavior
- Avoid database-specific test implementations; extend the generic suite instead
