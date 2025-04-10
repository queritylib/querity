package io.github.queritylib.querity.parser;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static io.github.queritylib.querity.api.Operator.*;
import static io.github.queritylib.querity.api.Querity.*;
import static io.github.queritylib.querity.api.Sort.Direction.ASC;
import static io.github.queritylib.querity.api.Sort.Direction.DESC;
import static org.assertj.core.api.Assertions.assertThat;

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
            Querity.query().sort(sortBy("lastName", ASC), sortBy("age", DESC)).pagination(1, 10).build())
    );
  }

  @ParameterizedTest
  @MethodSource("provideArguments")
  void testParseQuery(String query, Query expected) {
    Query actual = QuerityParser.parseQuery(query);
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }
}
