<img alt="Querity-logo" src="https://user-images.githubusercontent.com/1853562/142502086-2a352854-2315-4fe5-b1a3-d7730a47fe36.jpeg" width="80" height="80"/> Querity
=======

[![Build](https://github.com/queritylib/querity/actions/workflows/maven.yml/badge.svg)](https://github.com/queritylib/querity/actions/workflows/maven.yml)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=queritylib_querity&metric=bugs)](https://sonarcloud.io/summary/new_code?id=queritylib_querity)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=queritylib_querity&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=queritylib_querity)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=queritylib_querity&metric=coverage)](https://sonarcloud.io/summary/new_code?id=queritylib_querity)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=queritylib_querity&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=queritylib_querity)
[![Twitter](https://img.shields.io/twitter/url/https/twitter.com/QuerityLib.svg?style=social&label=Follow%20%40QuerityLib)](https://twitter.com/QuerityLib)

Open-source Java query builder for SQL and NoSQL.

## Description

Querity is an extensible query builder to create and run database queries in your Java application.

It supports **SQL** and **NoSQL** databases, and each support is built into small modules, so you can import the one
which fits into your project.

### Features

* **Filtering** with simple and nested conditions (AND, OR, NOT)
* **Sorting** and **pagination**
* **Projections** (select specific fields)
* **Function expressions** (UPPER, LOWER, LENGTH, CONCAT, etc.)
* **GROUP BY and HAVING** clauses for aggregate queries
* **Textual query language** for REST APIs
* **Native expressions** for advanced database-specific queries
* Support for **DTO layer** with property name mapping

**Supported databases:**

* Any SQL database (via JPA)
* MongoDB
* Elasticsearch

## Documentation

**Read the full documentation at [queritylib.github.io/querity](https://queritylib.github.io/querity)**

## Demo

Check out the demo application at [querity-demo](https://github.com/queritylib/querity-demo).

This demo also uses the [@queritylib/react](https://github.com/queritylib/querity-react) library for the frontend.

## Quick Start

### Requirements

* Java 17+
* Spring Framework 6 / Spring Boot 3 (for Spring modules)

### Installation

All releases are published to [Maven Central](https://search.maven.org/search?q=io.github.queritylib).

**Available modules:**

| Module                              | Description                                        |
|-------------------------------------|----------------------------------------------------|
| `querity-spring-data-jpa`           | Spring Data JPA support                            |
| `querity-jpa`                       | Plain Jakarta Persistence API (no Spring required) |
| `querity-spring-data-mongodb`       | Spring Data MongoDB support                        |
| `querity-spring-data-elasticsearch` | Spring Data Elasticsearch support                  |
| `querity-spring-web`                | JSON de/serialization for Spring Web MVC           |
| `querity-parser`                    | Textual query language parser                      |

All Spring modules are "Spring Boot starters" - just add the dependency and start using it.

**Maven:**

```xml
<dependency>
  <groupId>io.github.queritylib</groupId>
  <artifactId>querity-spring-data-jpa</artifactId>
  <version>${querity.version}</version>
</dependency>
```

**Gradle:**

```groovy
implementation "io.github.queritylib:querity-spring-data-jpa:${querityVersion}"
```

### Basic Usage

```java
import static io.github.queritylib.querity.api.Querity.*;
import static io.github.queritylib.querity.api.Operator.*;
import static io.github.queritylib.querity.api.Sort.Direction.*;

@Service
public class MyService {

  @Autowired
  Querity querity;

  public List<Person> getPeople() {
    Query query = Querity.query()
        .filter(
          and(
                filterBy("lastName", EQUALS, "Skywalker"),
            filterBy("age", GREATER_THAN, 18)
          )
        )
        .sort(sortBy("lastName"), sortBy("birthDate", DESC))
        .pagination(1, 10)
        .build();
    return querity.findAll(Person.class, query);
  }
}
```

### Query Language

With the `querity-parser` module, you can parse queries from a simple textual syntax:

```java
Query query = QuerityParser.parseQuery(
  "and(lastName=\"Skywalker\", age>18) sort by lastName, birthDate desc page 1,10"
);
```

See the [full documentation](https://queritylib.github.io/querity) for more examples and advanced features.

## Access to SNAPSHOT builds

Commits to the `main` branch are automatically deployed to Central Portal Snapshots repository.

**Maven:**

```xml
<repositories>
  <repository>
    <name>Central Portal Snapshots</name>
    <id>central-portal-snapshots</id>
    <url>https://central.sonatype.com/repository/maven-snapshots/</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

**Gradle:**

```groovy
repositories {
  maven {
    name = 'Central Portal Snapshots'
    url = 'https://central.sonatype.com/repository/maven-snapshots/'
    mavenContent { snapshotsOnly() }
  }
}
```

## Development

### Running tests

```bash
./mvnw test
```

### Test dataset

The test dataset is generated with [Mockaroo](https://mockaroo.com). Schema
available [here](https://mockaroo.com/ec155390).

## Maintainers

* Bruno Mendola [@brunomendola](https://github.com/brunomendola)

**PRs are welcome!**

## Version History

See [Releases](https://github.com/queritylib/querity/releases).

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.
