package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.jpa.AliasedSelectionSpecification;
import io.github.queritylib.querity.jpa.SelectionSpecification;
import io.github.queritylib.querity.jpa.domain.Person;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.github.queritylib.querity.api.Querity.selectByNative;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestPropertySource(properties = {
    "spring.datasource.driver-class-name=org.postgresql.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SpringPostgresqlQuerityJpaImplTests extends QuerityJpaImplTests {

  public static final String POSTGRES_DOCKER_IMAGE = "postgres:14.1";

  @Container
  private static final PostgreSQLContainer POSTGRESQL_CONTAINER = new PostgreSQLContainer(DockerImageName.parse(POSTGRES_DOCKER_IMAGE));

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
  }

  /**
   * Overridden because sort behaves differently in PostgreSQL regarding accented strings
   */
  @Override
  protected <C> Comparator<C> getStringComparator(Function<C, String> extractValueFunction, boolean reversed) {
    return Comparator.comparing(
        (C c) -> StringUtils.lowerCase(
            StringUtils.stripAccents(
                extractValueFunction.apply(c))),
        getSortComparator(reversed));
  }

  @Test
  void givenNativeSelectWithConcatExpression_whenFindAllProjected_thenReturnConcatenatedValues() {
    // Use coalesce to handle null values in concatenation
    SelectionSpecification<Person> fullNameSpec = AliasedSelectionSpecification.of(
        (root, cb) -> cb.concat(
            cb.concat(cb.coalesce(root.get("firstName"), ""), " "),
            cb.coalesce(root.get("lastName"), "")),
        "fullName"
    );
    Query query = Querity.query()
        .filter(Querity.filterBy("firstName", io.github.queritylib.querity.api.Operator.IS_NOT_NULL))
        .filter(Querity.filterBy("lastName", io.github.queritylib.querity.api.Operator.IS_NOT_NULL))
        .select(selectByNative(fullNameSpec))
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result).isNotEmpty();
    assertThat(result).allSatisfy(map -> {
      assertThat(map).containsKey("fullName");
      assertThat(map.get("fullName")).isInstanceOf(String.class);
    });
    // Verify the concatenation is correct for entity1
    String expectedFullName = entity1.getFirstName() + " " + entity1.getLastName();
    assertThat(result).anyMatch(map -> expectedFullName.equals(map.get("fullName")));
  }

  @Test
  void givenNativeSelectWithMultipleExpressionsAndFilter_whenFindAllProjected_thenReturnFilteredResults() {
    SelectionSpecification<Person> fullNameSpec = AliasedSelectionSpecification.of(
        (root, cb) -> cb.concat(
            cb.concat(cb.coalesce(root.get("firstName"), ""), " "),
            cb.coalesce(root.get("lastName"), "")),
        "fullName"
    );
    SelectionSpecification<Person> idSpec = AliasedSelectionSpecification.of(
        (root, cb) -> root.get("id"),
        "id"
    );
    Query query = Querity.query()
        .filter(Querity.filterBy("lastName", entity1.getLastName()))
        .select(selectByNative(idSpec, fullNameSpec))
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result).isNotEmpty();
    assertThat(result).allSatisfy(map -> {
      assertThat(map).containsKey("id");
      assertThat(map).containsKey("fullName");
      String fullName = (String) map.get("fullName");
      assertThat(fullName).endsWith(entity1.getLastName());
    });
  }
}
