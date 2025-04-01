package io.github.queritylib.querity.spring.data.jpa;

import org.apache.commons.lang3.StringUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Comparator;
import java.util.function.Function;

@Testcontainers
@TestPropertySource(properties = {
    "spring.datasource.driver-class-name=org.postgresql.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SpringPostgresqlQuerityJpaImplTests extends QuerityJpaImplTests {

  public static final String POSTGRES_DOCKER_IMAGE = "postgres:14.1";

  @Container
  private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_DOCKER_IMAGE));

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
}
