package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.spring.data.elasticsearch.domain.Person;
import io.github.queritylib.querity.test.QuerityGenericSpringTestSuite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static io.github.queritylib.querity.api.Querity.filterByNative;
import static io.github.queritylib.querity.api.Querity.not;
import static io.github.queritylib.querity.api.Querity.sortByNative;
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

  /**
   * Elasticsearch does not support field-to-field comparison natively.
   * This would require script queries which are not implemented.
   */
  @Override
  protected boolean supportsFieldToFieldComparison() {
    return false;
  }

  /**
   * Elasticsearch does not support function expressions in filters.
   * This would require script queries which are not implemented.
   */
  @Override
  protected boolean supportsFunctionExpressionsInFilters() {
    return false;
  }

  /**
   * Elasticsearch does not support function expressions in sorting.
   * This would require script-based sorting which is not implemented.
   */
  @Override
  protected boolean supportsFunctionExpressionsInSorting() {
    return false;
  }

  /**
   * Elasticsearch does not support function expressions in projections.
   * This would require scripted fields which are not implemented.
   */
  @Override
  protected boolean supportsFunctionExpressionsInProjections() {
    return false;
  }

  @Test
  void givenElasticsearchNativeCondition_whenFindAll_thenReturnOnlyFilteredElements() {
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
  void givenNotConditionWrappingElasticsearchNativeCondition_whenFindAll_thenThrowIllegalArgumentException() {
    Criteria criteria = Criteria.where("lastName").is(entity1.getLastName());
    Query query = Querity.query()
        .filter(not(filterByNative(criteria)))
        .build();
    assertThrows(IllegalArgumentException.class,
        () -> querity.findAll(Person.class, query),
        "Not conditions wrapping native conditions is not supported; just write a negative native condition.");
  }

  @Test
  void givenNativeSortWrapper_whenFindAll_thenReturnSortedElements() {
    Order nativeOrder = Order.asc("lastName");
    Query query = Querity.query()
        .sort(sortByNative(nativeOrder))
        .build();
    List<Person> result = querity.findAll(Person.class, query);
    assertThat(result).isNotEmpty();
    // Verify that results are sorted by lastName ascending
    assertThat(result)
        .extracting(Person::getLastName)
        .isSortedAccordingTo(Comparator.nullsLast(Comparator.naturalOrder()));
  }

  @Test
  void givenNativeSortWrapperDesc_whenFindAll_thenReturnSortedElementsDescending() {
    Order nativeOrder = Order.desc("lastName");
    Query query = Querity.query()
        .sort(sortByNative(nativeOrder))
        .build();
    List<Person> result = querity.findAll(Person.class, query);
    assertThat(result).isNotEmpty();
    // Verify that results are sorted by lastName descending
    Comparator<String> comparator = Comparator.nullsLast(Comparator.<String>naturalOrder().reversed());
    assertThat(result)
        .extracting(Person::getLastName)
        .isSortedAccordingTo(comparator);
  }

  @Test
  void givenMixedSortTypes_whenFindAll_thenReturnSortedElements() {
    Order nativeOrder = Order.asc("lastName");
    Query query = Querity.query()
        .sort(sortByNative(nativeOrder), Querity.sortBy("firstName"))
        .build();
    List<Person> result = querity.findAll(Person.class, query);
    assertThat(result).isNotEmpty();
    // Verify that results are sorted first by lastName, then by firstName
    Comparator<Person> comparator = Comparator
        .comparing(Person::getLastName, Comparator.nullsLast(Comparator.naturalOrder()))
        .thenComparing(Person::getFirstName, Comparator.nullsLast(Comparator.naturalOrder()));
    assertThat(result).isSortedAccordingTo(comparator);
  }

  @Test
  void givenSelectWithProjection_whenFindAllProjected_thenReturnOnlySelectedFields() {
    Query query = Querity.query()
        .selectBy("firstName", "lastName")
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result).isNotEmpty();
    // Elasticsearch doesn't return null fields, so we only check firstName is present
    assertThat(result).allSatisfy(map -> {
      assertThat(map).containsKey("firstName");
    });
  }

  @Test
  void givenSelectWithProjection_whenFindAll_thenThrowIllegalArgumentException() {
    Query query = Querity.query()
        .selectBy("firstName", "lastName")
        .build();
    assertThrows(IllegalArgumentException.class,
        () -> querity.findAll(Person.class, query),
        "findAll() does not support projections. Use findAllProjected() instead.");
  }

  @Test
  void givenQueryWithFilterAndProjection_whenFindAllProjected_thenReturnFilteredAndProjectedResults() {
    String lastName = ((Person) entity1).getLastName();
    Query query = Querity.query()
        .filter(Querity.filterBy("lastName", lastName))
        .selectBy("firstName", "lastName")
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result).isNotEmpty();
    assertThat(result).allSatisfy(map -> {
      assertThat(map.get("lastName")).isEqualTo(lastName);
    });
  }
}
