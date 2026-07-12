package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.AdvancedQuery;
import io.github.queritylib.querity.jpa.domain.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = QueritySpringJpaTestApplication.class)
@TestPropertySource(properties = {
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true"
})
class JpaAdvancedQueryFactoryTests {

  @Autowired
  EntityManager entityManager;

  @Test
  void givenNullQuery_whenGetJpaProjectionQuery_thenReturnQueryWithoutProjections() {
    JpaAdvancedQueryFactory<Person> factory = new JpaAdvancedQueryFactory<>(Person.class, null, entityManager);

    TypedQuery<Tuple> result = factory.getJpaProjectionQuery();

    assertThat(result).isNotNull();
  }

  @Test
  void givenNullQuery_whenGetProjectedResults_thenThrowIllegalStateException() {
    JpaAdvancedQueryFactory<Person> factory = new JpaAdvancedQueryFactory<>(Person.class, null, entityManager);

    assertThatThrownBy(factory::getProjectedResults)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("SELECT clause");
  }

  @Test
  void givenQueryWithoutSelect_whenGetProjectedResults_thenThrowIllegalStateException() {
    AdvancedQuery query = AdvancedQuery.builder().build();
    JpaAdvancedQueryFactory<Person> factory = new JpaAdvancedQueryFactory<>(Person.class, query, entityManager);

    assertThatThrownBy(factory::getProjectedResults)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("SELECT clause");
  }
}
