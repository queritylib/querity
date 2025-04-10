---
layout: default
---

**Querity** is a powerful and extensible **query builder for Java applications**, designed to simplify database queries across different technologies.

With Querity, you can effortlessly build **REST APIs** that support **filtering, sorting, and pagination** — perfect for displaying data in frontend applications (see [Demo](#demo)).

* Users benefit from a simple textual query language to interact with data.
* Developers can leverage a fluent Java API to construct complex queries with ease.

Querity supports both **SQL and NoSQL** databases through modular components — import only what you need for your project. Currently, it works with any **SQL database** (via JPA), **MongoDB**, and **Elasticsearch**.

# Why choose Querity?

✔ **Learn once, use everywhere** – a unified approach to querying data.

✔ **Database-agnostic queries** – filtering, sorting, and pagination work across different databases.

✔ **Fluent Java API** – construct powerful queries with clean, readable code.

✔ **Simple textual query language** – ideal for REST API integration.

✔ **Consistent API design** – expose the same REST endpoints across all projects.

✔ **Easy database switching** – migrate between databases without rewriting business logic.

# Requirements

* Java 17+
* For the Spring modules:
  * Spring Framework 6 (optionally Spring Boot 3... makes things a lot simpler)

# Installing

All releases are published to the Maven Central repository
(see [here](https://search.maven.org/search?q=io.github.queritylib)).

Install one of the modules in your project as follows (see [Available modules](#available-modules)).

Maven:

```xml

<dependency>
  <groupId>io.github.queritylib</groupId>
  <artifactId>querity-spring-data-jpa</artifactId>
  <version>{{ site.querity_version }}</version>
</dependency>
```

Gradle:

```groovy
implementation "io.github.queritylib:querity-spring-data-jpa:{{ site.querity_version }}"
```

See [Releases](https://github.com/queritylib/querity/releases) to check the latest version and see the changelogs.

All the Spring-related modules are "Spring Boot starters": if you use Spring Boot you just need to add the dependency to your project and
start using it, no other configuration needed.

## Without Spring Boot autoconfiguration

You can use Querity without Spring Boot, but you need to configure the `Querity` bean in your Spring configuration.

Example with JPA:

```java
import io.github.queritylib.querity.api.Querity;

@Configuration
public class QuerityConfiguration {
  @Bean
  Querity querity(EntityManager entityManager) {
    return new QuerityJpaImpl(entityManager);
  }
}
```

You can also disable the autoconfiguration of the Spring Boot starter by adding the following property to your `application.properties`:

```properties
querity.autoconfigure.enabled=false
```

> This is useful if you want to import multiple Querity modules (e.g. JPA and Elasticsearch) and configure multiple Querity beans in your application.

# Demo

Check out the simplest demo application using Querity at [querity-demo](https://github.com/queritylib/querity-demo).

# Available modules

Currently, Querity supports the following technologies with its modules:

> The datasource configuration is not managed by Querity and is delegated to the underlying application.

## querity-spring-data-jpa

Supports [Spring Data JPA](https://spring.io/projects/spring-data-jpa) and any SQL database with a compatible JDBC driver.

## querity-jpa

Supports plain [Jakarta Persistence API](https://jakarta.ee/specifications/persistence/) and any SQL database with a compatible JDBC driver.

This module is not Spring-specific, so it's not a Spring Boot starter: you will need to apply all the configurations manually.

You basically need to instantiate a `QuerityJpaImpl` object and pass it the JPA `EntityManager` you want to use.

## querity-spring-data-mongodb

Supports [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb).

## querity-spring-data-elasticsearch

Supports [Spring Data Elasticsearch](https://spring.io/projects/spring-data-elasticsearch).

> Remember to map the fields you want to query as "keyword" in your Elasticsearch index.

## querity-spring-web

Supports JSON serialization and deserialization of Querity objects in [Spring Web MVC](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html).

With this module, you can pass a JSON `Query` or `Condition` object as request param in your Spring `@RestController` and it will be automatically deserialized into a Querity object.

For example, your API calls will look like this:

```
curl 'http://localhost:8080/people?q={"filter":{"and":[{"propertyName":"lastName","operator":"EQUALS","value":"Skywalker"},{"propertyName":"lastName","operator":"EQUALS","value":"Luke"}]}}'
```

This is an alternative approach to the one provided by the module `querity-parser`.

See [Support for Spring MVC and REST APIs](#support-for-spring-mvc-and-rest-apis) for more details.

## querity-parser

Enables the parsing of Querity objects from a **simple textual query language**.

For example, your API calls will look like this:

```
curl 'http://localhost:8080/people?q=and(lastName="Skywalker",firstName="Luke")'
```

This is an alternative approach to the one provided by the module `querity-spring-web`.

See [Query language](#query-language) for more details.

# Quick start

In your Spring Boot project, add the dependency as shown in [Installing](#installing) and create a Spring service as
follows:

```java
import static io.github.queritylib.querity.api.Querity.*;
import static io.github.queritylib.querity.api.Operator.*;
import static io.github.queritylib.querity.api.Sort.Direction.*;

@Service
public class MyService {

  @Autowired
  Querity querity;

  public Result<Person> getPeople() {
    Query query = Querity.query()
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

> Notice the static imports to improve the readability.

# Features

Use the static method `Querity.query().build()` to build an empty query (see the following chapters to build a more
complex query).

You can `@Autowire` the `Querity` service to run queries against the database configured in your application.

Having the `Querity` service, you can use the following instance methods:

* `Querity.findAll(entityClass, query)` to run the query and retrieve the results;
* `Querity.count(entityClass, condition)` to just count the elements filtered by the condition.

## Filters

Use `Querity.query().filter(condition).build()` to build a query with filters.

You can build filters which contains **conditions**, and they can be simple or nested conditions.

### Conditions

#### Simple conditions

Use `Querity.filterBy` to build a simple condition with a property name, an operator and a value (if needed by the
operator, e.g. IS_NULL does not need a value).

```
Query query = Querity.query()
    .filter(filterBy("lastName", EQUALS, "Skywalker"))
    .build();
```

Supports **nested properties** with dot notation, also with one-to-many/many-to-many relationships.

E.g. `address.city` (one-to-one), `visitedPlaces.country` (one-to-many).

##### Operators

* EQUALS
* NOT_EQUALS
* STARTS_WITH (case-insensitive where supported*)
* ENDS_WITH (case-insensitive where supported*)
* CONTAINS (case-insensitive where supported*)
* GREATER_THAN
* GREATER_THAN_EQUALS
* LESSER_THAN
* LESSER_THAN_EQUALS
* IS_NULL
* IS_NOT_NULL
* IN
* NOT_IN

> \* Operators STARTS_WITH, ENDS_WITH, CONTAINS are case-insensitive only if the underlying database supports case-insensitive matching and proper configurations are applied.
>
>    E.g. JPA supports case-insensitive matching by default,
> while Elasticsearch requires a specific configuration (see [Elasticsearch documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-analyzers.html) and [Spring Data Elasticsearch Documentation](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/object-mapping.html#elasticsearch.mapping.meta-model.annotations)).

#### AND conditions

Use `Querity.and` and add more conditions to wrap them in a logical AND.

```
Query query = Querity.query()
    .filter(
        and(
            filterBy("firstName", EQUALS, "Luke"),
            filterBy("lastName", EQUALS, "Skywalker")
        )
    ).build();
```

You can also nest more levels of complex conditions.

#### OR conditions

Use `Querity.or` and add more conditions to wrap them in a logical OR.

```
Query query = Querity.query()
    .filter(
        or(
            filterBy("lastName", EQUALS, "Skywalker"),
            filterBy("lastName", EQUALS, "Kenobi")
        )
    ).build();
```

You can also nest more levels of complex conditions.

#### NOT conditions

Use `Querity.not` and specify a condition to wrap it in a logical NOT.

You can wrap simple conditions:

```
Query query = Querity.query()
    .filter(not(filterBy("lastName", EQUALS, "Skywalker")))
    .build();
```

or complex conditions:

```
Query query = Querity.query()
    .filter(
        not(and(
            filterBy("firstName", EQUALS, "Luke"),
            filterBy("lastName", EQUALS, "Skywalker")
        ))
    ).build();
```

#### Native conditions

Use `Querity.filterByNative` and specify a native condition to use a database-specific condition in your query.

This could be useful if you really want to add very complex query conditions that cannot be built with the Querity APIs.

> Native conditions are supported only with Java API, not REST.

Example with Spring Data JPA / Jakarta Persistence:

```
Specification<Person> specification = (root, cq, cb) -> cb.equal(root.get("lastName"), "Skywalker");
Query query = Querity.query()
    .filter(filterByNative(specification))
    .build();
```

> In the module implementing the Spring Data JPA support, the Spring Data JPA's `Specification` class is used.
>
> In the module implementing the Jakarta Persistence support, there is a class named `Specification` that does the same
> (since the base library doesn't provide anything similar).

Example with Spring Data MongoDB / Elasticsearch:

```
Criteria criteria = Criteria.where("lastName").is("Skywalker");
Query query = Querity.query()
    .filter(filterByNative(criteria))
    .build();
```


## Sorting

Use `Querity.query().sort(...).build()` to build a query with sorting.

Use `Querity.sortBy` to build a sort criteria.

```
Query query = Querity.query()
    .sort(sortBy("lastName"), sortBy("birthDate", DESC))
    .build();
```

## Pagination

Use `Querity.query().pagination(page, pageSize).build()` to build a query with pagination.

```
Query query = Querity.query()
    .pagination(1, 5)
    .build();
```

## Distinct results

Use `Querity.query().distinct(true).build()` to build a query with distinct results.

You should set this flag to `true` when you are filtering by some nested properties that may produce duplicate results in SQL databases.

```
Query query = Querity.query()
    .distinct(true)
    .filter(filterBy("orders.items.quantity", GREATER_THAN, 8))
    .pagination(1, 5)
    .build();
```

> Because of limitations of SQL databases, when the distinct flag is set to `true`, you cannot sort by nested properties.

> The distinct flag is meaningless in NoSQL databases and will be ignored.

## Modify an existing Query

Query objects are immutable, so you can't modify them directly (there are no "setters").

You can build a new query by copying the existing one and changing the parts you need.

Use `Query.toBuilder()` to copy an existing Query into a new QueryBuilder, then you can make changes before calling `build()`.

```
Query query = originalQuery.toBuilder()
    .sort(sortBy("lastName"))
    .build();
```

# Support for Spring MVC and REST APIs

Querity objects need some configuration to be correctly deserialized when they are received by a
Spring `@RestController` as a JSON payload.

This includes:

* registering property editors in Spring MVC to recognize the `Querity` and `Condition` objects as valid request parameters,
* configuring the Jackson module `QuerityModule` to correctly deserialize the Querity objects from JSON,
* configuring the aspect `QuerityPreprocessorAspect` to use the preprocessors in the Spring controllers (see [Preprocessors](#preprocessors) below).

These configurations are automatically done by importing the `querity-spring-web` module (see [Installing](#installing)).

> You can disable the autoconfiguration of the Spring Boot starter by adding the following property to your `application.properties`:
>
> `querity.web.autoconfigure.enabled=false`
>
> Then you will have to apply the needed configurations manually.

After that, you'll be able to use a `Query` or `Condition` as a controller parameter and build REST APIs like this:

```java
import io.github.queritylib.querity.api.Query;

@RestController
public class MyRestController {

  @Autowired
  MyService service;

  @GetMapping(value = "/people", produces = MediaType.APPLICATION_JSON_VALUE)
  Result<Person> getPeople(@RequestParam(required = false) Query q) {
    return service.getPeople(q);
  }
}
```

Then the above REST API could be invoked like this:

```bash
curl 'http://localhost:8080/people?q={"filter":{"and":[{"propertyName":"lastName","operator":"EQUALS","value":"Skywalker"},{"propertyName":"lastName","operator":"EQUALS","value":"Luke"}]}}'
```

## Query language

The `querity-parser` module provides a simple query language to build a `Query` object,
useful when you need the user to write and understand the query.

It is an alternative approach to the one provided by the module `querity-spring-web`, which parses JSON.

To enable the query language, import the `querity-parser` module (see [Installing](#installing)).

The following snippet rewrites the previous example using the support for the query language:

```java
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.parser.QuerityParser;

@RestController
public class MyRestController {

  @Autowired
  MyService service;

  @GetMapping(value = "/people", produces = MediaType.APPLICATION_JSON_VALUE)
  Result<Person> getPeople(@RequestParam(required = false) String q) {
    Query query = QuerityParser.parseQuery(q);
    return service.getPeople(query);
  }
}
```

Then the above REST API could be invoked like this:

```bash
curl 'http://localhost:8080/people?q=and(lastName="Skywalker",firstName="Luke")'
```

_Much simpler than JSON, isn't it?_

### Query language syntax

The query language supports the following grammar (ANTLR v4 format):

```
DISTINCT    : 'distinct';
AND         : 'and';
OR          : 'or';
NOT         : 'not';
SORT        : 'sort by';
ASC         : 'asc';
DESC        : 'desc';
PAGINATION  : 'page';
NEQ         : '!=';
LTE         : '<=';
GTE         : '>=';
EQ          : '=';
LT          : '<';
GT          : '>';
STARTS_WITH : 'starts with';
ENDS_WITH   : 'ends with';
CONTAINS    : 'contains';
IS_NULL     : 'is null';
IS_NOT_NULL : 'is not null';
IN          : 'in';
NOT_IN      : 'not in';
LPAREN      : '(';
RPAREN      : ')';
COMMA       : ',';

INT_VALUE     : [0-9]+;
DECIMAL_VALUE : [0-9]+'.'[0-9]+;
BOOLEAN_VALUE : 'true' | 'false';
PROPERTY      : [a-zA-Z_][a-zA-Z0-9_.]*;
STRING_VALUE  : '"' (~["\\] | '\\' .)* '"';

query            : DISTINCT? (condition)? (SORT sortFields)? (PAGINATION paginationParams)? ;
condition        : simpleCondition | conditionWrapper | notCondition;
operator         : NEQ | LTE | GTE | EQ | LT | GT | STARTS_WITH | ENDS_WITH | CONTAINS | IS_NULL | IS_NOT_NULL | IN | NOT_IN ;
conditionWrapper : (AND | OR) LPAREN condition (COMMA condition)* RPAREN ;
notCondition     : NOT LPAREN condition RPAREN ;
simpleValue      : INT_VALUE | DECIMAL_VALUE | BOOLEAN_VALUE | STRING_VALUE;
arrayValue       : LPAREN simpleValue (COMMA simpleValue)* RPAREN ;
simpleCondition  : PROPERTY operator (simpleValue | arrayValue)? ;
direction        : ASC | DESC ;
sortField        : PROPERTY (direction)? ;
sortFields       : sortField (COMMA sortField)* ;
paginationParams : INT_VALUE COMMA INT_VALUE ;
```

Some examples of valid queries:

```text
lastName="Skywalker"
lastName!="Skywalker"
lastName starts with "Sky"
lastName ends with "walker"
lastName contains "wal"
and(firstName="Luke", lastName="Skywalker")
age>30
age<30
height>=1.80
height<=1.80
and(lastName="Skywalker", age>30)
and(or(firstName="Luke", firstName="Anakin"), lastName="Skywalker") sort by age desc
and(not(firstName="Luke"), lastName="Skywalker")
lastName="Skywalker" page 2,10
lastName is null
lastName is not null
lastName in ("Skywalker", "Solo")
lastName not in ("Skywalker", "Solo")
deleted=false
address.city="Rome"
distinct and(orders.totalPrice>1000,currency="EUR")
sort by lastName asc, age desc page 1,10
```

> Notice that string values must always be enclosed in double quotes.

## Support for DTO layer

You may not want to expose database entities to the clients of your REST APIs.

If you implemented a DTO layer, with DTO objects mapping properties from your entities, the queries which would come to your REST APIs will have the DTO property names, not the entity ones. This is a problem, because some queries would not work, looking for non-existing properties on the entities.

> Note: it would work without any help only if properties in the DTO have the same name and structure of the properties in the entity

Querity has a "preprocessing layer" which you can use to map the DTO property names in your Query to the entity property names.

### Preprocessors

Use the `@WithPreprocessor("beanName")` annotation to annotate `Query` or `Condition` parameters in your Spring RestControllers.

The `beanName` argument must refer to an existing Spring bean which implements `QueryPreprocessor`.

When entering your controller method, the `Query` or `Condition` object would already be preprocessed.

#### PropertyNameMappingPreprocessor

`PropertyNameMappingPreprocessor` is a `QueryProcessor` abstraction to convert property names.

Querity already has a simple mapper, `SimplePropertyNameMapper`, which simply does a property name conversion by looking into a Map you must provide.

To use `PropertyNameMappingPreprocessor` with `SimplePropertyNameMapper`, instantiate a bean into your Spring configuration:

```java
@SpringBootApplication
public class MyApplication {
  public static void main(String[] args) {
    SpringApplication.run(MyApplication.class, args);
  }

  @Bean
  public QueryPreprocessor preprocessor1() {
    return new PropertyNameMappingPreprocessor(
        SimplePropertyNameMapper.builder()
            .recursive(true) // default is true
            .mapping("prop1", "prop2") // customize your mappings here
            .build());
  }
}
```

and use it to annotate the parameter in your RestController:

```java
@RestController
public class MyRestController {

  @Autowired
  MyService service;

  @GetMapping(value = "/people", produces = MediaType.APPLICATION_JSON_VALUE)
  Result<Person> getPeople(@RequestParam(required = false) @WithPreprocessor("preprocessor1") Query q) {
    return service.getPeople(q);
  }
}
```

The mappings in `SimplePropertyNameMapper` are resolved recursively by default.

So if you mapped `prop1` to `prop2`, then all the fields nested in `prop1` will be automatically mapped to equal-named fields under `prop2`.

You can switch off the recursive mapping by setting the `recursive` flag to `false` in the builder.

# React Components

Querity provides a library of **React components** designed to simplify the creation of user interfaces for Querity-based REST APIs.

These components are available as the [@queritylib/react](https://www.npmjs.com/package/@queritylib/react) package on npm.

- **Live Demo**: See these components in action in the [querity-demo](#demo) project.
- **Documentation**: Explore the [documentation](https://www.npmjs.com/package/@queritylib/react) to start integrating Querity React Components into your React application.

Get started today and build powerful, dynamic UIs with ease!
