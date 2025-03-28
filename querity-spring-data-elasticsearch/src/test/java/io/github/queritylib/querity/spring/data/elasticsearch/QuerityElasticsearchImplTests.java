package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.spring.data.elasticsearch.domain.Person;
import io.github.queritylib.querity.test.QuerityGenericSpringTestSuite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Comparator;
import java.util.List;

import static io.github.queritylib.querity.api.Querity.filterByNative;
import static io.github.queritylib.querity.api.Querity.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = QuerityElasticsearchTestApplication.class)
@Testcontainers
class QuerityElasticsearchImplTests extends QuerityGenericSpringTestSuite<Person, String> {

  private static final String ELASTICSEARCH_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:8.16.5";

  @Container
  private static final ElasticsearchContainer ELASTICSEARCH_CONTAINER = new ElasticsearchContainer(DockerImageName.parse(ELASTICSEARCH_IMAGE))
      .withEnv("xpack.security.enabled", "false");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.elasticsearch.uris", ELASTICSEARCH_CONTAINER::getHttpHostAddress);
  }

  @Override
  protected Class<Person> getEntityClass() {
    return Person.class;
  }

  /**
   * Overridden because sort behaves differently in Elasticsearch regarding null values
   */
  @Override
  protected <C extends Comparable<? super C>> Comparator<C> getSortComparator(boolean reversed) {
    Comparator<C> comparator = Comparator.naturalOrder();
    if (reversed) comparator = comparator.reversed();
    return Comparator.nullsLast(comparator);
  }

  @Test
  void givenElasticsearchNativeCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Criteria criteria = Criteria.where("lastName").is(entity1.getLastName());
    Query query = Querity.query()
        .filter(filterByNative(criteria))
        .build();
    List<Person> result = querity.findAll(Person.class, query);
    assertThat(result)
        .isNotEmpty()
        .containsExactlyInAnyOrderElementsOf(entities.stream()
            .filter(p -> entity1.getLastName().equals(p.getLastName()))
            .toList());
  }

  @Test
  void givenNotConditionWrappingElasticsearchNativeCondition_whenFilterAll_thenThrowIllegalArgumentException() {
    Criteria criteria = Criteria.where("lastName").is(entity1.getLastName());
    Query query = Querity.query()
        .filter(not(filterByNative(criteria)))
        .build();
    assertThrows(IllegalArgumentException.class,
        () -> querity.findAll(Person.class, query),
        "Not conditions wrapping native conditions is not supported; just write a negative native condition.");
  }
}
