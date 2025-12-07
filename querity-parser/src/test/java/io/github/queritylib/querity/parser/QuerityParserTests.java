package io.github.queritylib.querity.parser;

import io.github.queritylib.querity.api.FieldReference;
import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static io.github.queritylib.querity.api.Operator.*;
import static io.github.queritylib.querity.api.Querity.*;
import static io.github.queritylib.querity.api.SimpleSort.Direction.ASC;
import static io.github.queritylib.querity.api.SimpleSort.Direction.DESC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuerityParserTests {

  public static Stream<Arguments> provideArguments() {
    return Stream.of(
        Arguments.of("", Querity.query().build()),
        Arguments.of("lastName=\"Skywalker\"",
            Querity.query().filter(filterBy("lastName", "Skywalker")).build()),
        Arguments.of("lastName!=\"Skywalker\"",
            Querity.query().filter(filterBy("lastName", NOT_EQUALS, "Skywalker")).build()),
        Arguments.of("lastName starts with \"Sky\"",
            Querity.query().filter(filterBy("lastName", STARTS_WITH, "Sky")).build()),
        Arguments.of("lastName ends with \"walker\"",
            Querity.query().filter(filterBy("lastName", ENDS_WITH, "walker")).build()),
        Arguments.of("lastName contains \"wal\"",
            Querity.query().filter(filterBy("lastName", CONTAINS, "wal")).build()),
        Arguments.of("and(firstName=\"Luke\", lastName=\"Skywalker\")",
            Querity.query().filter(and(filterBy("firstName", "Luke"), filterBy("lastName", "Skywalker"))).build()),
        Arguments.of("age>30",
            Querity.query().filter(filterBy("age", GREATER_THAN, 30)).build()),
        Arguments.of("age<30",
            Querity.query().filter(filterBy("age", LESSER_THAN, 30)).build()),
        Arguments.of("height>=1.80",
            Querity.query().filter(filterBy("height", GREATER_THAN_EQUALS, new BigDecimal("1.80"))).build()),
        Arguments.of("height<=1.80",
            Querity.query().filter(filterBy("height", LESSER_THAN_EQUALS, new BigDecimal("1.80"))).build()),
        Arguments.of("and(lastName=\"Skywalker\", age>30)",
            Querity.query().filter(and(filterBy("lastName", "Skywalker"), filterBy("age", GREATER_THAN, 30))).build()),
        Arguments.of("and(or(firstName=\"Luke\", firstName=\"Anakin\"), lastName=\"Skywalker\") sort by age desc",
            Querity.query().filter(and(or(filterBy("firstName", "Luke"), filterBy("firstName", "Anakin")), filterBy("lastName", "Skywalker"))).sort(sortBy("age", DESC)).build()),
        Arguments.of("and(not(firstName=\"Luke\"), lastName=\"Skywalker\")",
            Querity.query().filter(and(not(filterBy("firstName", "Luke")), filterBy("lastName", "Skywalker"))).build()),
        Arguments.of("lastName=\"Skywalker\" page 2,10",
            Querity.query().filter(filterBy("lastName", "Skywalker")).pagination(2, 10).build()),
        Arguments.of("lastName is null",
            Querity.query().filter(filterBy("lastName", IS_NULL)).build()),
        Arguments.of("lastName is not null",
            Querity.query().filter(filterBy("lastName", IS_NOT_NULL)).build()),
        Arguments.of("lastName in (\"Skywalker\", \"Solo\")",
            Querity.query().filter(filterBy("lastName", IN, new Object[]{"Skywalker", "Solo"})).build()),
        Arguments.of("lastName not in (\"Skywalker\", \"Solo\")",
            Querity.query().filter(filterBy("lastName", NOT_IN, new Object[]{"Skywalker", "Solo"})).build()),
        Arguments.of("deleted=false",
            Querity.query().filter(filterBy("deleted", Boolean.FALSE)).build()),
        Arguments.of("address.city=\"Rome\"",
            Querity.query().filter(filterBy("address.city", "Rome")).build()),
        Arguments.of("distinct orders.rows.quantity>10",
            Querity.query().distinct(true).filter(filterBy("orders.rows.quantity", GREATER_THAN, 10)).build()),
        Arguments.of("sort by lastName asc, age desc page 1,10",
            Querity.query().sort(sortBy("lastName", ASC), sortBy("age", DESC)).pagination(1, 10).build()),
        // Select tests
        Arguments.of("select id, firstName, lastName",
            Querity.query().selectBy("id", "firstName", "lastName").build()),
        Arguments.of("select id",
            Querity.query().selectBy("id").build()),
        Arguments.of("select id, name lastName=\"Skywalker\"",
            Querity.query().selectBy("id", "name").filter(filterBy("lastName", "Skywalker")).build()),
        Arguments.of("select id, name lastName=\"Skywalker\" sort by name asc",
            Querity.query().selectBy("id", "name").filter(filterBy("lastName", "Skywalker")).sort(sortBy("name", ASC)).build()),
        Arguments.of("select id, name lastName=\"Skywalker\" sort by name asc page 1,10",
            Querity.query().selectBy("id", "name").filter(filterBy("lastName", "Skywalker")).sort(sortBy("name", ASC)).pagination(1, 10).build()),
        Arguments.of("distinct select id, firstName age>20",
            Querity.query().distinct(true).selectBy("id", "firstName").filter(filterBy("age", GREATER_THAN, 20)).build()),
        Arguments.of("select address.city, address.street",
            Querity.query().selectBy("address.city", "address.street").build()),
        // Field-to-field comparison tests
        Arguments.of("startDate<$endDate",
            Querity.query().filter(filterByField("startDate", LESSER_THAN, field("endDate"))).build()),
        Arguments.of("startDate<=$endDate",
            Querity.query().filter(filterByField("startDate", LESSER_THAN_EQUALS, field("endDate"))).build()),
        Arguments.of("price>$minPrice",
            Querity.query().filter(filterByField("price", GREATER_THAN, field("minPrice"))).build()),
        Arguments.of("price>=$minPrice",
            Querity.query().filter(filterByField("price", GREATER_THAN_EQUALS, field("minPrice"))).build()),
        Arguments.of("field1=$field2",
            Querity.query().filter(filterByField("field1", EQUALS, field("field2"))).build()),
        Arguments.of("field1!=$field2",
            Querity.query().filter(filterByField("field1", NOT_EQUALS, field("field2"))).build()),
        Arguments.of("nested.startDate<$nested.endDate",
            Querity.query().filter(filterByField("nested.startDate", LESSER_THAN, field("nested.endDate"))).build()),
        Arguments.of("and(startDate<$endDate, price>$minPrice)",
            Querity.query().filter(and(
                filterByField("startDate", LESSER_THAN, field("endDate")),
                filterByField("price", GREATER_THAN, field("minPrice"))
            )).build()),
        Arguments.of("or(field1=$field2, field3!=$field4)",
            Querity.query().filter(or(
                filterByField("field1", EQUALS, field("field2")),
                filterByField("field3", NOT_EQUALS, field("field4"))
            )).build()),
        Arguments.of("not(startDate>$endDate)",
            Querity.query().filter(not(filterByField("startDate", GREATER_THAN, field("endDate")))).build()),
        Arguments.of("and(startDate<$endDate, status=\"ACTIVE\")",
            Querity.query().filter(and(
                filterByField("startDate", LESSER_THAN, field("endDate")),
                filterBy("status", "ACTIVE")
            )).build()),
        Arguments.of("startDate<$endDate sort by startDate asc",
            Querity.query().filter(filterByField("startDate", LESSER_THAN, field("endDate"))).sort(sortBy("startDate", ASC)).build()),
        Arguments.of("startDate<$endDate page 1,10",
            Querity.query().filter(filterByField("startDate", LESSER_THAN, field("endDate"))).pagination(1, 10).build()),
        Arguments.of("select id, startDate startDate<$endDate",
            Querity.query().selectBy("id", "startDate").filter(filterByField("startDate", LESSER_THAN, field("endDate"))).build()),
        // Additional tests for better coverage
        Arguments.of("deleted=true",
            Querity.query().filter(filterBy("deleted", Boolean.TRUE)).build()),
        Arguments.of("or(firstName=\"Luke\", firstName=\"Leia\")",
            Querity.query().filter(or(filterBy("firstName", "Luke"), filterBy("firstName", "Leia"))).build()),
        Arguments.of("not(deleted=true)",
            Querity.query().filter(not(filterBy("deleted", Boolean.TRUE))).build()),
        Arguments.of("age in (20, 30, 40)",
            Querity.query().filter(filterBy("age", IN, new Object[]{20, 30, 40})).build()),
        Arguments.of("price in (10.5, 20.5)",
            Querity.query().filter(filterBy("price", IN, new Object[]{new BigDecimal("10.5"), new BigDecimal("20.5")})).build()),
        Arguments.of("sort by lastName",
            Querity.query().sort(sortBy("lastName", ASC)).build()),
        Arguments.of("sort by lastName asc",
            Querity.query().sort(sortBy("lastName", ASC)).build()),
        Arguments.of("page 1,20",
            Querity.query().pagination(1, 20).build()),
        Arguments.of("page 0,50",
            Querity.query().pagination(0, 50).build()),
        Arguments.of("select id sort by id desc",
            Querity.query().selectBy("id").sort(sortBy("id", DESC)).build()),
        Arguments.of("select id page 1,5",
            Querity.query().selectBy("id").pagination(1, 5).build())
    );
  }

  @ParameterizedTest
  @MethodSource("provideArguments")
  void testParseQuery(String query, Query expected) {
    Query actual = QuerityParser.parseQuery(query);
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void givenInvalidQuery_whenParse_thenThrowException() {
    assertThatThrownBy(() -> QuerityParser.parseQuery("invalid query syntax !!!"))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  void givenNullQuery_whenParse_thenThrowException() {
    assertThatThrownBy(() -> QuerityParser.parseQuery(null))
        .isInstanceOf(NullPointerException.class);
  }
}
