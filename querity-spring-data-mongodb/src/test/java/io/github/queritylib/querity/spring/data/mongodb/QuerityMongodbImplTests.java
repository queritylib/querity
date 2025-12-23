package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.spring.data.mongodb.domain.Person;
import io.github.queritylib.querity.test.QuerityGenericSpringTestSuite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static io.github.queritylib.querity.api.Querity.filterByNative;
import static io.github.queritylib.querity.api.Querity.not;
import static io.github.queritylib.querity.api.Querity.sortByNative;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = QuerityMongodbTestApplication.class)
@Testcontainers
class QuerityMongodbImplTests extends QuerityGenericSpringTestSuite<Person, String> {

  public static final String MONGO_DB_DOCKER_IMAGE = "mongo:5.0.4";

  @Container
  private static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_DB_DOCKER_IMAGE));

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
  }

  @Override
  protected Class<Person> getEntityClass() {
    return Person.class;
  }

  /**
   * Overridden because sort with nulls last is not supported by MongoDB
   */
  @Override
  protected <C extends Comparable<? super C>> Comparator<C> getSortComparator(boolean reversed) {
    Comparator<C> comparator = Comparator.nullsFirst(Comparator.naturalOrder());
    if (reversed) comparator = comparator.reversed();
    return comparator;
  }

  /**
   * MongoDB $expr comparisons include null values because null is considered "less than" any value.
   */
  @Override
  protected boolean fieldToFieldComparisonIncludesNulls() {
    return true;
  }

  /**
   * MongoDB does not support function expressions in sorting without aggregation pipeline.
   */
  @Override
  protected boolean supportsFunctionExpressionsInSorting() {
    return false;
  }

  /**
   * MongoDB does not support function expressions in projections without aggregation pipeline.
   */
  @Override
  protected boolean supportsFunctionExpressionsInProjections() {
    return false;
  }

  @Test
  void givenMongodbNativeCondition_whenFindAll_thenReturnOnlyFilteredElements() {
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
  void givenNotConditionWrappingMongodbNativeCondition_whenFindAll_thenThrowIllegalArgumentException() {
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
        .isSortedAccordingTo(Comparator.nullsFirst(Comparator.naturalOrder()));
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
    // In MongoDB with desc, nulls are at the end by default
    Comparator<String> comparator = Comparator.nullsLast(Comparator.<String>reverseOrder());
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
        .comparing(Person::getLastName, Comparator.nullsFirst(Comparator.naturalOrder()))
        .thenComparing(Person::getFirstName, Comparator.nullsFirst(Comparator.naturalOrder()));
    assertThat(result).isSortedAccordingTo(comparator);
  }

  @Test
  void givenSelectWithProjection_whenFindAllProjected_thenReturnOnlySelectedFields() {
    Query query = Querity.query()
        .selectBy("firstName", "lastName")
        .build();
    List<Map<String, Object>> result = querity.findAllProjected(Person.class, query);
    assertThat(result).isNotEmpty();
    // MongoDB doesn't return null fields, so we only check firstName is present
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
