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

It supports **SQL** and **NoSQL** databases technologies, and each support is built into small modules, so you
can import the one which fits into your project.

### Features

Database support:

* any SQL database (with the JPA modules)
* MongoDB
* Elasticsearch

Query features:

* filtering
* sorting
* pagination
* projections (select specific fields)
* textual query language
* support for REST APIs
* support for DTO layer
* native database expressions (JPA)
* and more...

All with ONE SINGLE LANGUAGE!

## Documentation

Read the full documentation [here](https://queritylib.github.io/querity).

## Demo

Check out the simplest demo application using Querity at [querity-demo](https://github.com/queritylib/querity-demo).

This demo also uses the [@queritylib/react](https://github.com/queritylib/querity-react) library for the frontend. Take
a look!

## Getting Started

### Dependencies

* Java 17+
* Spring Framework 6 (optionally Spring Boot 3... makes things a lot simpler)

### Installing

All releases are published to the Maven Central repository (
see [here](https://search.maven.org/search?q=io.github.queritylib)).

Available modules:

* **querity-spring-data-jpa**: supports Spring Data JPA
* **querity-jpa**: supports plain Jakarta Persistence API (Spring not required)
* **querity-spring-data-mongodb**: supports Spring Data MongoDB
* **querity-spring-data-elasticsearch**: supports Spring Data Elasticsearch
* **querity-spring-web**: supports JSON de/serialization of Querity objects in Spring Web MVC
* **querity-parser**: enable the parsing of Querity objects from a **simple query language**

All Spring modules are "Spring Boot starters", you just need to add the dependency to your Spring Boot project and start using
it, no other configuration needed.

Maven:

```xml
<dependency>
  <groupId>io.github.queritylib</groupId>
  <artifactId>querity-spring-data-jpa</artifactId>
  <version>${querity.version}</version>
</dependency>
```

Gradle:

```groovy
implementation "io.github.queritylib:querity-spring-data-jpa:${querityVersion}"
```

### Usage

```java


@Service
public class MyService {

  @Autowired
  Querity querity;

  public Result<Person> getPeople() {
    Query query = Querity.query()
        // customize filters, pagination, sorting...
        .filter(
            not(and(
                filterBy("lastName", EQUALS, "Skywalker"),
                filterBy("firstName", EQUALS, "Luke")
            ))
        )
        .sort(sortBy("lastName"), sortBy("birthDate", DESC))
        .pagination(1, 10)
        .build();
    List<Person> items = querity.findAll(Person.class, query);
    Long totalCount = querity.count(Person.class, query.getFilter());
    return new Result<>(items, totalCount);
  }

  record Result<T>(List<T> items, Long totalCount) {
  }
}
```

In the above example, the `findAll` method returns the first of n pages with max 10 elements of all people NOT named
Luke Skywalker, sorted by last name and then birthdate descending.<br />
The `count` method returns the total filtered items count excluding pagination (the record keyword is implemented from
Java 14).

> Note the static imports to improve the readability.

#### Projections

Use `selectBy` to retrieve only specific fields instead of full entities:

```java
Query query = Querity.query()
    .selectBy("firstName", "lastName", "address.city")
    .filter(filterBy("lastName", EQUALS, "Skywalker"))
    .build();
List<Map<String, Object>> results = querity.findAllProjected(Person.class, query);
// Each map contains only: {firstName: "...", lastName: "...", city: "..."}
```

#### Native expressions (JPA only)

For advanced use cases, JPA modules support native expressions using `CriteriaBuilder`:

**Native sort with expression:**

```java
// Sort by length of lastName
OrderSpecification<Person> orderSpec = (root, cb) -> cb.asc(cb.length(root.get("lastName")));
Query query = Querity.query()
    .sort(sortByNative(orderSpec))
    .build();
List<Person> results = querity.findAll(Person.class, query);
```

**Native select with expression:**

```java
// Select concatenated full name
SelectionSpecification<Person> fullNameSpec = AliasedSelectionSpecification.of(
    (root, cb) -> cb.concat(cb.concat(root.get("firstName"), " "), root.get("lastName")),
    "fullName"
);
Query query = Querity.query()
    .select(selectByNative(fullNameSpec))
    .build();
List<Map<String, Object>> results = querity.findAllProjected(Person.class, query);
// Each map contains: {fullName: "Luke Skywalker"}
```

> Native expressions are only available for JPA. MongoDB and Elasticsearch support `sortByNative` and `filterByNative` with their respective native types (`Order`, `Criteria`), but not expression-based projections.

#### Query language

The `querity-parser` module provides a simple query language to build a `Query` object,
useful when you need the user to write and understand the query.

It is an alternative approach to the one provided by the module `querity-spring-web`, which parses JSON.

The following snippet rewrites the previous example using the query language:

```java
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.parser.QuerityParser;

//...

public List<Person> getPeople() {
  Query query = QuerityParser.parseQuery("not(and(lastName=\"Skywalker\", firstName=\"Luke\")) sort by lastName, birthDate desc page 1,10");
  return querity.findAll(Person.class, query);
}

//...
```

## Access to SNAPSHOT builds

Commits to the `main` branch are automatically built and deployed to Central Portal Snapshots Maven repository.

To use the SNAPSHOTs in your project, add the SNAPSHOTs repository as follows.

> Of course using SNAPSHOTs is not recommended, but if you feel brave you can do it to test new not-yet-released features.

Maven:

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

Gradle:

```groovy
repositories {
  // ...
  maven {
    name = 'Central Portal Snapshots'
    url = 'https://central.sonatype.com/repository/maven-snapshots/'
    mavenContent { snapshotsOnly() }
  }
  // ...
}
```

Browse the
repository [here](https://central.sonatype.com/service/rest/repository/browse/maven-snapshots/io/github/queritylib/querity-parent/)
to find the latest SNAPSHOT version.

## Development

### Running tests

Run with Maven (wrapper):

```bash
./mvnw test
```

or just run them with your favourite IDE.

### Test dataset

The test dataset is generated with [Mockaroo](https://mockaroo.com).

If you want to make changes, you don't need to do it manually, please find the
schema [here](https://mockaroo.com/ec155390).

## Authors

Contributors names and contact info

* Bruno Mendola [@brunomendola](https://github.com/brunomendola)

**PRs are welcome!**

## Version History

See [Releases](https://github.com/queritylib/querity/releases).

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details
