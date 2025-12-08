package io.github.queritylib.querity.test;

import io.github.queritylib.querity.api.FieldReference;
import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.test.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.github.queritylib.querity.api.Operator.*;
import static io.github.queritylib.querity.api.Querity.*;
import static io.github.queritylib.querity.api.SimpleSort.Direction.DESC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public abstract class QuerityGenericTestSuite<T extends Person<K, ?, ?, ? extends Order<? extends OrderItem>>, K extends Comparable<K>> {
  protected DatabaseSeeder<T> databaseSeeder;
  protected Querity querity;

  protected abstract DatabaseSeeder<T> getDatabaseSeeder();

  protected abstract Querity getQuerity();

  public static final String PROPERTY_ID = "id";
  public static final String PROPERTY_LAST_NAME = "lastName";
  public static final String PROPERTY_FIRST_NAME = "firstName";
  public static final String PROPERTY_BIRTH_DATE = "birthDate";
  public static final String PROPERTY_HEIGHT = "height";
  public static final String PROPERTY_CHILDREN = "children";
  public static final String PROPERTY_MARRIED = "married";
  public static final String PROPERTY_ADDRESS_CITY = "address.city";
  public static final String PROPERTY_VISITED_LOCATIONS_COUNTRY = "visitedLocations.country";
  public static final String PROPERTY_VISITED_LOCATIONS_CITIES = "visitedLocations.cities";
  public static final String PROPERTY_FAVOURITE_PRODUCT_CATEGORY = "favouriteProductCategory";
  public static final String PROPERTY_ORDERS_EXTERNAL_ID = "orders.externalId";

  protected List<T> entities;
  protected T entity1;
  protected T entity2;

  @BeforeEach
  void setUp() {
    this.databaseSeeder = getDatabaseSeeder();
    this.querity = getQuerity();
    this.entities = databaseSeeder.getEntities();
    assertThat(this.entities).isNotEmpty();
    this.entity1 = getEntityFromList(20);
    this.entity2 = getEntityFromList(30);
  }

  @Nested
  class FilteringTests {

    @Test
    void givenNullQuery_whenFindAll_thenReturnAllTheElements() {
      List<T> result = querity.findAll(getEntityClass(), null);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities);
    }

    @Test
    void givenEmptyFilter_whenFindAll_thenReturnAllTheElements() {
      Query query = Querity.query()
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities);
    }

    @Test
    void givenFilterByIdEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_ID, EQUALS, entity1.getId()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).hasSize(1);
      assertThat(result).containsExactly(entity1);
    }

    @Test
    void givenFilterWithStringEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> entity1.getLastName().equals(p.getLastName()))
          .toList());
    }

    @Test
    void givenFilterWithIntegerEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_CHILDREN, EQUALS, entity1.getChildren()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getChildren() != null && p.getChildren().equals(entity1.getChildren()))
          .toList());
    }

    @Test
    void givenFilterWithIntegerAsStringEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_CHILDREN, EQUALS, entity1.getChildren().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getChildren() != null && p.getChildren().equals(entity1.getChildren()))
          .toList());
    }

    @Test
    void givenFilterWithDateEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_BIRTH_DATE, EQUALS, entity1.getBirthDate()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getBirthDate() != null && p.getBirthDate().isEqual(entity1.getBirthDate()))
          .toList());
    }

    @Test
    void givenFilterWithDateAsStringEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_BIRTH_DATE, EQUALS, formatDate(entity1.getBirthDate())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getBirthDate() != null && p.getBirthDate().isEqual(entity1.getBirthDate()))
          .toList());
    }

    @Test
    void givenFilterWithBooleanEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_MARRIED, EQUALS, true))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(Person::isMarried)
          .toList());
    }

    @Test
    void givenFilterWithBooleanAsStringEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_MARRIED, EQUALS, Boolean.TRUE.toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(Person::isMarried)
          .toList());
    }

    @Test
    void givenFilterWithUUIDEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      UUID anOrderExternalId = entity1.getOrders().get(0).getExternalId();
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_ORDERS_EXTERNAL_ID, EQUALS, anOrderExternalId))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getOrders().stream()
              .anyMatch(l -> l.getExternalId().equals(anOrderExternalId)))
          .toList());
    }

    @Test
    void givenFilterWithUUIDAsStringEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      UUID anOrderExternalId = entity1.getOrders().get(0).getExternalId();
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_ORDERS_EXTERNAL_ID, EQUALS, anOrderExternalId.toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getOrders().stream()
              .anyMatch(l -> l.getExternalId().equals(anOrderExternalId)))
          .toList());
    }

    @Test
    void givenFilterWithBigDecimalEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, EQUALS, entity1.getHeight()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) == 0)
          .toList());
    }

    @Test
    void givenFilterWithBigDecimalEqualsConditionAsString_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, EQUALS, entity1.getHeight().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) == 0)
          .toList());
    }

    @Test
    void givenFilterWithBigDecimalGreaterThanCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, GREATER_THAN, entity1.getHeight().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) > 0)
          .toList());
    }

    @Test
    void givenFilterWithNotBigDecimalGreaterThanCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_HEIGHT, GREATER_THAN, entity1.getHeight().toString())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) > 0))
          .toList());
    }

    @Test
    void givenFilterWithBigDecimalGreaterThanEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, GREATER_THAN_EQUALS, entity1.getHeight().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) >= 0)
          .toList());
    }

    @Test
    void givenFilterWithNotBigDecimalGreaterThanEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_HEIGHT, GREATER_THAN_EQUALS, entity1.getHeight().toString())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) >= 0))
          .toList());
    }

    @Test
    void givenFilterWithBigDecimalLesserThanCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, LESSER_THAN, entity1.getHeight().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) < 0)
          .toList());
    }

    @Test
    void givenFilterWithNotBigDecimalLesserThanCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_HEIGHT, LESSER_THAN, entity1.getHeight().toString())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) < 0))
          .toList());
    }

    @Test
    void givenFilterWithBigDecimalLesserThanEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_HEIGHT, LESSER_THAN_EQUALS, entity1.getHeight().toString()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getHeight().compareTo(entity1.getHeight()) <= 0)
          .toList());
    }

    @Test
    void givenFilterWithNotBigDecimalLesserThanEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_HEIGHT, LESSER_THAN_EQUALS, entity1.getHeight().toString())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) <= 0))
          .toList());
    }

    @Test
    void givenFilterWithStringNotEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, NOT_EQUALS, entity1.getLastName()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream().filter(p -> !entity1.getLastName().equals(p.getLastName())).toList());
    }

    @Test
    void givenFilterWithNotStringNotEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_LAST_NAME, NOT_EQUALS, entity1.getLastName())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> entity1.getLastName().equals(p.getLastName()))
          .toList());
    }

    @Test
    void givenFilterWithStringStartsWithCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      String prefix = entity1.getLastName().substring(0, 3);
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, STARTS_WITH, prefix))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getLastName() != null && p.getLastName().startsWith(prefix))
          .toList());
    }

    @Test
    void givenFilterWithStringEndsWithCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      String suffix = entity1.getLastName().substring(3);
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, ENDS_WITH, suffix))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getLastName() != null && p.getLastName().endsWith(suffix))
          .toList());
    }

    @Test
    void givenFilterWithStringContainsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      String substring = entity1.getLastName().substring(1, 3);
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, CONTAINS, substring))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getLastName() != null && p.getLastName().contains(substring))
          .toList());
    }

    @Test
    void givenFilterWithStringNotContainsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      String substring = entity1.getLastName().substring(1, 3);
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_LAST_NAME, CONTAINS, substring)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getLastName() == null || !p.getLastName().contains(substring))
          .toList());
    }

    @Test
    void givenFilterWithStringIsNullCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, IS_NULL))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream().filter(p -> p.getLastName() == null).toList());
    }

    @Test
    void givenFilterWithStringIsNotNullCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, IS_NOT_NULL))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream().filter(p -> p.getLastName() != null).toList());
    }

    @Test
    void givenFilterWithNotStringIsNotNullCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_LAST_NAME, IS_NOT_NULL)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream().filter(p -> p.getLastName() == null).toList());
    }

    @Test
    void givenFilterWithInListCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, IN, List.of(entity1.getLastName(), entity2.getLastName())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getLastName() != null && List.of(entity1.getLastName(), entity2.getLastName()).contains(p.getLastName()))
          .toList());
    }

    @Test
    void givenFilterWithInArrayCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, IN, new String[]{entity1.getLastName(), entity2.getLastName()}))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getLastName() != null && List.of(entity1.getLastName(), entity2.getLastName()).contains(p.getLastName()))
          .toList());
    }

    @Test
    void givenFilterWithInConditionWithStringValue_whenFindAll_thenThrowIllegalArgumentException() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, IN, entity1.getLastName()))
          .build();
      assertThatIllegalArgumentException()
          .isThrownBy(() -> querity.findAll(getEntityClass(), query))
          .withMessage("Value must be an array");
    }

    @Test
    void givenFilterWithNotInListCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, NOT_IN, List.of(entity1.getLastName(), entity2.getLastName())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getLastName() == null || !List.of(entity1.getLastName(), entity2.getLastName()).contains(p.getLastName()))
          .toList());
    }

    @Test
    void givenFilterWithNotInArrayCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, NOT_IN, new String[]{entity1.getLastName(), entity2.getLastName()}))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getLastName() == null || !List.of(entity1.getLastName(), entity2.getLastName()).contains(p.getLastName()))
          .toList());
    }

    @Test
    void givenFilterWithNotInConditionWithStringValue_whenFindAll_thenThrowIllegalArgumentException() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, NOT_IN, entity1.getLastName()))
          .build();
      assertThatIllegalArgumentException()
          .isThrownBy(() -> querity.findAll(getEntityClass(), query))
          .withMessage("Value must be an array");
    }

    @Test
    void givenFilterWithTwoStringEqualsConditionsWithAndLogic_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(and(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              filterBy(PROPERTY_FIRST_NAME, EQUALS, entity1.getFirstName())
          ))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> entity1.getLastName().equals(p.getLastName()) && entity1.getFirstName().equals(p.getFirstName()))
          .toList());
    }

    @Test
    void givenFilterWithTwoStringEqualsConditionsWithOrLogic_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(or(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity2.getLastName())
          ))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> entity1.getLastName().equals(p.getLastName()) || entity2.getLastName().equals(p.getLastName()))
          .toList());
    }

    @Test
    void givenFilterWithNestedConditions_whenFindAll_thenReturnListOfEntity() {
      Query query = Querity.query()
          .filter(and(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              or(
                  filterBy(PROPERTY_FIRST_NAME, EQUALS, entity1.getFirstName()),
                  filterBy(PROPERTY_FIRST_NAME, EQUALS, entity2.getFirstName())
              ))
          )
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> entity1.getLastName().equals(p.getLastName()) &&
                       (entity1.getFirstName().equals(p.getFirstName()) || entity2.getFirstName().equals(p.getFirstName())))
          .toList());
    }

    @Test
    void givenFilterWithStringEqualsConditionOnNestedField_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_ADDRESS_CITY, EQUALS, entity1.getAddress().getCity()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> entity1.getAddress().getCity().equals(p.getAddress().getCity()))
          .toList());
    }

    @Test
    void givenFilterWithStringEqualsConditionOnNestedCollectionItemField_whenFindAll_thenReturnOnlyFilteredElements() {
      String visitedCountry = entity1.getVisitedLocations().get(0).getCountry();
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_VISITED_LOCATIONS_COUNTRY, EQUALS, visitedCountry))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getVisitedLocations().stream()
              .map(Location::getCountry)
              .anyMatch(visitedCountry::equals))
          .toList());
    }

    @Test
    void givenFilterWithStringEqualsConditionOnNestedCollectionItemStringListField_whenFindAll_thenReturnOnlyFilteredElements() {
      String visitedCity = entity1.getVisitedLocations().get(0).getCities().get(0);
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_VISITED_LOCATIONS_CITIES, EQUALS, visitedCity))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getVisitedLocations().stream()
              .anyMatch(l -> l.getCities().contains(visitedCity)))
          .toList());
    }

    @Test
    void givenFilterWithTwoStringEqualsConditionOnNestedCollectionItemFieldsWithAndLogic_whenFindAll_thenReturnOnlyFilteredElements() {
      String visitedCountry = entity1.getVisitedLocations().get(0).getCountry();
      String visitedCity = entity1.getVisitedLocations().get(0).getCities().get(0);
      Query query = Querity.query()
          .filter(and(
              filterBy(PROPERTY_VISITED_LOCATIONS_COUNTRY, EQUALS, visitedCountry),
              filterBy(PROPERTY_VISITED_LOCATIONS_CITIES, EQUALS, visitedCity))
          )
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getVisitedLocations().stream()
              .anyMatch(l -> visitedCountry.equals(l.getCountry()) &&
                             l.getCities().contains(visitedCity)))
          .toList());
    }

    @Test
    void givenFilterWithStringEqualsConditionOnDoubleNestedCollectionItemField_whenFindAll_thenReturnOnlyFilteredElements() {
      String sku = entity1.getOrders().get(0).getItems().get(0).getSku();
      Query query = Querity.query()
          .filter(filterBy("orders.items.sku", EQUALS, sku))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(findByOrderContainingItemMatching(i -> i.getSku().equals(sku)));
    }

    /**
     * This test should allow to catch issues with duplicated rows from SQL queries when filtering by nested collection fields.
     * We cannot spot those issues without pagination, because JPA automatically removes duplicates.
     * But when pagination is applied, if the query produced duplicated rows, the pagination will return fewer elements than expected.
     * In those cases, the distinct flag should be set to true to remove duplicates.
     */
    @Test
    void givenFilterWithNumberGreaterThanConditionOnDoubleNestedCollectionItemFieldAndDistinctAndSortAndPagination_whenFindAll_thenReturnOnlyFilteredElements() {
      int quantity = 8;
      Query query = Querity.query()
          .distinct(true)
          .filter(filterBy("orders.items.quantity", GREATER_THAN, quantity))
          .sort(sortBy(PROPERTY_ID))
          .pagination(1, 10)
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(findByOrderContainingItemMatching(i -> i.getQuantity() > quantity).stream()
          .sorted(Comparator.comparing(T::getId))
          .skip(0).limit(10)
          .toList());
    }

    @Test
    void givenFilterWithEnumEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      ProductCategory category = entity1.getFavouriteProductCategory();
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_FAVOURITE_PRODUCT_CATEGORY, EQUALS, category.name()))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream().filter(e -> e.getFavouriteProductCategory().equals(category)).toList());
    }

    @Test
    void givenFilterWithNotConditionWithStringEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName())))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream().filter(p -> !(entity1.getLastName().equals(p.getLastName()))).toList());
    }

    @Test
    void givenFilterWithTwoNestedNotConditionsWithStringEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(not(filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()))))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream().filter(p -> entity1.getLastName().equals(p.getLastName())).toList());
    }

    @Test
    void givenFilterWithNotConditionWithTwoStringEqualsConditionsWithAndLogic_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(and(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              filterBy(PROPERTY_FIRST_NAME, EQUALS, entity1.getFirstName())
          )))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> !(entity1.getLastName().equals(p.getLastName()) && entity1.getFirstName().equals(p.getFirstName())))
          .toList());
    }

    @Test
    void givenFilterWithNotConditionWithTwoStringEqualsConditionsWithOrLogic_whenFindAll_thenReturnOnlyFilteredElements() {
      Query query = Querity.query()
          .filter(not(or(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity2.getLastName())
          )))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> !(entity1.getLastName().equals(p.getLastName()) || entity2.getLastName().equals(p.getLastName())))
          .toList());
    }

    @Test
    void givenFilterWithNotConditionWithNestedConditions_whenFindAll_thenReturnListOfEntity() {
      Query query = Querity.query()
          .filter(not(and(
              filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
              or(
                  filterBy(PROPERTY_FIRST_NAME, EQUALS, entity1.getFirstName()),
                  filterBy(PROPERTY_FIRST_NAME, EQUALS, entity2.getFirstName())
              ))
          ))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> !(entity1.getLastName().equals(p.getLastName()) && (entity1.getFirstName().equals(p.getFirstName()) || entity2.getFirstName().equals(p.getFirstName()))))
          .toList());
    }

    private String formatDate(LocalDate birthDate) {
      return birthDate.format(DateTimeFormatter.ISO_DATE);
    }
  }

  @Nested
  class PaginationTests {

    @Test
    void givenPagination_whenFindAll_thenReturnThePageElements() {
      Query query = Querity.query()
          .sort(sortBy(PROPERTY_ID))
          .pagination(2, 3)
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).containsExactlyElementsOf(entities.stream()
          .sorted(Comparator.comparing(T::getId))
          .skip(3).limit(3)
          .toList());
    }
  }

  @Nested
  class SortingTests {

    @Test
    void givenSortByFieldAscending_whenFindAll_thenReturnSortedElements() {
      Query query = Querity.query()
          .sort(sortBy(PROPERTY_LAST_NAME), sortBy(PROPERTY_ID))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      Comparator<T> comparator = getStringComparator((T p) -> p.getLastName())
          .thenComparing((T p) -> p.getId());
      assertThat(result).isNotEmpty();
      assertThat(result).hasSize(entities.size());
      assertThat(result).isEqualTo(entities.stream().sorted(comparator).toList());
    }

    @Test
    void givenSortByDateField_whenFindAll_thenReturnSortedElements() {
      Query query = Querity.query()
          .sort(sortBy(PROPERTY_BIRTH_DATE), sortBy(PROPERTY_ID))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      Comparator<T> comparator = Comparator
          .comparing((T p) -> p.getBirthDate(), getSortComparator())
          .thenComparing((T p) -> p.getId());
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().sorted(comparator).toList());
    }

    @Test
    void givenSortByFieldDescending_whenFindAll_thenReturnSortedElements() {
      Query query = Querity.query()
          .sort(sortBy(PROPERTY_LAST_NAME, DESC), sortBy(PROPERTY_ID))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      Comparator<T> comparator = getStringComparator((T p) -> p.getLastName(), true)
          .thenComparing((T p) -> p.getId());
      assertThat(result).isNotEmpty();
      assertThat(result).hasSize(entities.size());
      assertThat(result).isEqualTo(entities.stream().sorted(comparator).toList());
    }

    @Test
    void givenSortByNestedField_whenFindAll_thenReturnSortedElements() {
      Query query = Querity.query()
          .sort(sortBy(PROPERTY_ADDRESS_CITY), sortBy(PROPERTY_ID))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      Comparator<T> comparator = getStringComparator((T p) -> p.getAddress().getCity())
          .thenComparing((T p) -> p.getId());
      assertThat(result).isNotEmpty();
      assertThat(result).hasSize(entities.size());
      assertThat(result).isEqualTo(entities.stream().sorted(comparator).toList());
    }

    /**
     * This test highlights the need for a tuple query in JPA,
     * as SQL databases don't allow to sort by a field that is not selected in the query,
     * and that's the case of nested fields when using DISTINCT.
     */
    @Test
    void givenSortByNestedFieldWithDistinct_whenFindAll_thenReturnSortedElements() {
      Query query = Querity.query()
          .distinct(true)
          .sort(sortBy(PROPERTY_ADDRESS_CITY), sortBy(PROPERTY_ID))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      Comparator<T> comparator = getStringComparator((T t) -> t.getAddress().getCity()).thenComparing(T::getId);
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .sorted(comparator)
          .toList());
    }

    @Test
    void givenSortByMultipleFields_whenFindAll_thenReturnSortedElements() {
      Query query = Querity.query()
          .sort(sortBy(PROPERTY_LAST_NAME), sortBy(PROPERTY_FIRST_NAME))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      Comparator<T> comparator = getStringComparator((T p) -> p.getLastName())
          .thenComparing((T p) -> p.getFirstName());
      assertThat(result).isNotEmpty();
      assertThat(result).isEqualTo(entities.stream().sorted(comparator).toList());
    }
  }

  @Nested
  class CountTests {

    @Test
    void givenNullQuery_whenCount_thenReturnAllTheElementsCount() {
      Long count = querity.count(getEntityClass(), null);
      assertThat(count).isEqualTo(entities.size());
    }

    @Test
    void givenEmptyFilter_whenCount_thenReturnAllTheElementsCount() {
      Long count = querity.count(getEntityClass(), and());
      assertThat(count).isEqualTo(entities.size());
    }

    @Test
    void givenFilterWithStringEqualsCondition_whenCount_thenReturnOnlyFilteredElementsCount() {
      Long count = querity.count(getEntityClass(), filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()));
      assertThat(count).isEqualTo(entities.stream().filter(e -> entity1.getLastName().equals(e.getLastName())).count());
    }

    @Test
    void givenFilterWithNumberGreaterThanConditionOnDoubleNestedCollectionItemField_whenCount_thenReturnOnlyFilteredElementsCount() {
      int quantity = 8;
      Long count = querity.count(getEntityClass(), filterBy("orders.items.quantity", GREATER_THAN, quantity));
      assertThat(count).isEqualTo(findByOrderContainingItemMatching(i -> i.getQuantity() > quantity).size());
    }

  }

  /**
   * Override this method to disable field-to-field comparison tests
   * for implementations that do not support this feature (e.g., Elasticsearch).
   *
   * @return true if field-to-field comparison is supported
   */
  protected boolean supportsFieldToFieldComparison() {
    return true;
  }

  /**
   * Override this method to indicate that the database includes records with null fields
   * in field-to-field comparisons. MongoDB, for example, includes records where one field
   * is null when comparing with $gt/$lt because null is considered "less than" any value.
   *
   * @return true if the database includes null values in field-to-field comparisons
   */
  protected boolean fieldToFieldComparisonIncludesNulls() {
    return false;
  }

  @Nested
  class FieldToFieldComparisonTests {

    @Test
    void givenFieldToFieldEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      // Find persons where firstName equals lastName (unlikely but testable)
      Query query = Querity.query()
          .filter(filterByField(PROPERTY_FIRST_NAME, EQUALS, field(PROPERTY_LAST_NAME)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getFirstName() != null && p.getLastName() != null && p.getFirstName().equals(p.getLastName()))
          .toList());
    }

    @Test
    void givenFieldToFieldNotEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      // Find persons where firstName does not equal lastName
      Query query = Querity.query()
          .filter(filterByField(PROPERTY_FIRST_NAME, NOT_EQUALS, field(PROPERTY_LAST_NAME)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getFirstName() == null || p.getLastName() == null || !p.getFirstName().equals(p.getLastName()))
          .toList());
    }

    @Test
    void givenFieldToFieldGreaterThanCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      // Find persons where firstName > lastName (lexicographically)
      Query query = Querity.query()
          .filter(filterByField(PROPERTY_FIRST_NAME, GREATER_THAN, field(PROPERTY_LAST_NAME)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      List<T> expected = entities.stream()
          .filter(p -> {
            if (fieldToFieldComparisonIncludesNulls()) {
              // MongoDB: null is less than any value, so firstName > null is true for non-null firstName
              return p.getFirstName() != null &&
                     (p.getLastName() == null || p.getFirstName().compareTo(p.getLastName()) > 0);
            } else {
              return p.getFirstName() != null && p.getLastName() != null &&
                     p.getFirstName().compareTo(p.getLastName()) > 0;
            }
          })
          .toList();
      assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void givenFieldToFieldGreaterThanEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      Query query = Querity.query()
          .filter(filterByField(PROPERTY_FIRST_NAME, GREATER_THAN_EQUALS, field(PROPERTY_LAST_NAME)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      List<T> expected = entities.stream()
          .filter(p -> {
            if (fieldToFieldComparisonIncludesNulls()) {
              return p.getFirstName() != null &&
                     (p.getLastName() == null || p.getFirstName().compareTo(p.getLastName()) >= 0);
            } else {
              return p.getFirstName() != null && p.getLastName() != null &&
                     p.getFirstName().compareTo(p.getLastName()) >= 0;
            }
          })
          .toList();
      assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void givenFieldToFieldLesserThanCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      // Find persons where firstName < lastName (lexicographically)
      Query query = Querity.query()
          .filter(filterByField(PROPERTY_FIRST_NAME, LESSER_THAN, field(PROPERTY_LAST_NAME)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getFirstName() != null && p.getLastName() != null && p.getFirstName().compareTo(p.getLastName()) < 0)
          .toList());
    }

    @Test
    void givenFieldToFieldLesserThanEqualsCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      Query query = Querity.query()
          .filter(filterByField(PROPERTY_FIRST_NAME, LESSER_THAN_EQUALS, field(PROPERTY_LAST_NAME)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getFirstName() != null && p.getLastName() != null && p.getFirstName().compareTo(p.getLastName()) <= 0)
          .toList());
    }

    @Test
    void givenFieldToFieldConditionWithAndLogic_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      // Find persons where firstName < lastName AND married = true
      Query query = Querity.query()
          .filter(and(
              filterByField(PROPERTY_FIRST_NAME, LESSER_THAN, field(PROPERTY_LAST_NAME)),
              filterBy(PROPERTY_MARRIED, EQUALS, true)
          ))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getFirstName() != null && p.getLastName() != null &&
                       p.getFirstName().compareTo(p.getLastName()) < 0 && p.isMarried())
          .toList());
    }

    @Test
    void givenFieldToFieldConditionWithOrLogic_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      // Find persons where firstName > lastName OR firstName < lastName (i.e., firstName != lastName)
      Query query = Querity.query()
          .filter(or(
              filterByField(PROPERTY_FIRST_NAME, GREATER_THAN, field(PROPERTY_LAST_NAME)),
              filterByField(PROPERTY_FIRST_NAME, LESSER_THAN, field(PROPERTY_LAST_NAME))
          ))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      List<T> expected = entities.stream()
          .filter(p -> {
            if (fieldToFieldComparisonIncludesNulls()) {
              // MongoDB: firstName > null is true, firstName < null is false
              // So we get all where lastName is null (firstName > null) or firstName != lastName
              return p.getFirstName() != null &&
                     (p.getLastName() == null || !p.getFirstName().equals(p.getLastName()));
            } else {
              return p.getFirstName() != null && p.getLastName() != null &&
                     !p.getFirstName().equals(p.getLastName());
            }
          })
          .toList();
      assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void givenNotFieldToFieldCondition_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      // Find persons where NOT (firstName > lastName)
      // Note: In SQL, NOT (NULL > x) is also NULL (not TRUE), so we expect only non-null comparisons
      Query query = Querity.query()
          .filter(not(filterByField(PROPERTY_FIRST_NAME, GREATER_THAN, field(PROPERTY_LAST_NAME))))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      // SQL semantics: NOT of comparison with NULL is still NULL, so records with NULL fields are excluded
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getFirstName() != null && p.getLastName() != null &&
                       !(p.getFirstName().compareTo(p.getLastName()) > 0))
          .toList());
    }

    @Test
    void givenFieldToFieldConditionWithPagination_whenFindAll_thenReturnPagedResults() {
      if (!supportsFieldToFieldComparison()) return;
      // Find persons where firstName < lastName with pagination
      Query query = Querity.query()
          .filter(filterByField(PROPERTY_FIRST_NAME, LESSER_THAN, field(PROPERTY_LAST_NAME)))
          .sort(sortBy(PROPERTY_ID))
          .pagination(1, 5)
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      List<T> expected = entities.stream()
          .filter(p -> p.getFirstName() != null && p.getLastName() != null &&
                       p.getFirstName().compareTo(p.getLastName()) < 0)
          .sorted(Comparator.comparing(T::getId))
          .skip(0).limit(5)
          .toList();
      assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void givenFieldToFieldConditionWithSort_whenFindAll_thenReturnSortedResults() {
      if (!supportsFieldToFieldComparison()) return;
      // Find persons where firstName < lastName sorted by lastName
      Query query = Querity.query()
          .filter(filterByField(PROPERTY_FIRST_NAME, LESSER_THAN, field(PROPERTY_LAST_NAME)))
          .sort(sortBy(PROPERTY_LAST_NAME), sortBy(PROPERTY_ID))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      Comparator<T> comparator = getStringComparator((T p) -> p.getLastName())
          .thenComparing(T::getId);
      List<T> expected = entities.stream()
          .filter(p -> p.getFirstName() != null && p.getLastName() != null &&
                       p.getFirstName().compareTo(p.getLastName()) < 0)
          .sorted(comparator)
          .toList();
      assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void givenNestedFieldToFieldComparison_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      // Compare nested field address.city with top-level field lastName
      // This tests that nested field paths work correctly in field references
      Query query = Querity.query()
          .filter(filterByField(PROPERTY_ADDRESS_CITY, EQUALS, field(PROPERTY_LAST_NAME)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      // Expect records where address.city equals lastName (unlikely but tests the feature)
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getAddress() != null && p.getAddress().getCity() != null &&
                       p.getLastName() != null && p.getAddress().getCity().equals(p.getLastName()))
          .toList());
    }

    @Test
    void givenNestedFieldInFieldReference_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      // Compare top-level field firstName with nested field address.city using EQUALS
      // Using EQUALS is simpler because equality with null is consistent: null == null is true, null == value is false
      Query query = Querity.query()
          .filter(filterByField(PROPERTY_FIRST_NAME, EQUALS, field(PROPERTY_ADDRESS_CITY)))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      // Expect records where firstName exactly equals address.city (unlikely but tests the feature)
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getFirstName() != null && p.getAddress() != null &&
                       p.getAddress().getCity() != null &&
                       p.getFirstName().equals(p.getAddress().getCity()))
          .toList());
    }

    @Test
    void givenNotConditionWithNestedFieldReference_whenFindAll_thenReturnOnlyFilteredElements() {
      if (!supportsFieldToFieldComparison()) return;
      // NOT (firstName = address.city) - should return records where firstName != address.city
      Query query = Querity.query()
          .filter(not(filterByField(PROPERTY_FIRST_NAME, EQUALS, field(PROPERTY_ADDRESS_CITY))))
          .build();
      List<T> result = querity.findAll(getEntityClass(), query);
      // NOT EQUALS returns records where the comparison is false (not null, not equal)
      // Records with null values are typically excluded because NOT(NULL) is NULL (unknown), not TRUE
      assertThat(result).containsExactlyInAnyOrderElementsOf(entities.stream()
          .filter(p -> p.getFirstName() != null && p.getAddress() != null &&
                       p.getAddress().getCity() != null &&
                       !p.getFirstName().equals(p.getAddress().getCity()))
          .toList());
    }
  }

  @Nested
  class SelectTests {

    @Test
    void givenSelectByTwoFields_whenFindAllProjected_thenReturnOnlySelectedFields() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, IS_NOT_NULL))
          .selectBy(PROPERTY_FIRST_NAME, PROPERTY_LAST_NAME)
          .build();
      List<Map<String, Object>> result = querity.findAllProjected(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      long expectedCount = entities.stream().filter(e -> e.getLastName() != null).count();
      assertThat(result).hasSize((int) expectedCount);
      assertThat(result).allSatisfy(map -> {
        assertThat(map).containsKey("firstName");
        assertThat(map).containsKey("lastName");
      });
    }

    @Test
    void givenSelectByWithFilter_whenFindAllProjected_thenReturnFilteredAndProjectedResults() {
      Query query = Querity.query()
          .filter(filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()))
          .selectBy(PROPERTY_FIRST_NAME, PROPERTY_LAST_NAME)
          .build();
      List<Map<String, Object>> result = querity.findAllProjected(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).allSatisfy(map -> {
        assertThat(map.get("lastName")).isEqualTo(entity1.getLastName());
      });
    }

    @Test
    void givenSelectByNestedField_whenFindAllProjected_thenReturnNestedFieldValues() {
      Query query = Querity.query()
          .selectBy(PROPERTY_FIRST_NAME, PROPERTY_ADDRESS_CITY)
          .build();
      List<Map<String, Object>> result = querity.findAllProjected(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).allSatisfy(map -> {
        assertThat(map).containsKey("firstName");
        // Nested field "address.city" may be returned as "city" (JPA) or nested as "address.city" (Elasticsearch)
        boolean hasCityFlat = map.containsKey("city");
        boolean hasCityNested = map.containsKey("address") && map.get("address") instanceof Map;
        assertThat(hasCityFlat || hasCityNested)
            .as("Expected 'city' key or nested 'address.city' structure")
            .isTrue();
      });
    }

    @Test
    void givenSelectByWithPagination_whenFindAllProjected_thenReturnPaginatedProjectedResults() {
      Query query = Querity.query()
          .selectBy(PROPERTY_ID, PROPERTY_FIRST_NAME)
          .sort(sortBy(PROPERTY_ID))
          .pagination(2, 3)
          .build();
      List<Map<String, Object>> result = querity.findAllProjected(getEntityClass(), query);
      assertThat(result).isNotEmpty();
      assertThat(result).hasSize(3);
    }

  }

  private List<T> findByOrderContainingItemMatching(Predicate<OrderItem> matchPredicate) {
    return entities.stream()
        .filter(p -> p.getOrders().stream()
            .map(Order::getItems)
            .flatMap(Collection::stream)
            .anyMatch(matchPredicate))
        .toList();
  }

  protected abstract Class<T> getEntityClass();

  private T getEntityFromList(int skip) {
    return entities.stream()
        .filter(e -> e.getLastName() != null)
        .filter(e -> e.getLastName().length() >= 5) // needed to test startsWith/endsWith/contains operators
        .filter(e -> e.getBirthDate() != null)
        .filter(e -> e.getChildren() > 0)
        .filter(e -> !e.getVisitedLocations().isEmpty())
        .filter(e -> !e.getVisitedLocations().get(0).getCities().isEmpty())
        .filter(e -> !e.getOrders().isEmpty())
        .skip(skip).limit(1).findAny()
        .orElseThrow(() -> new IllegalStateException("No entities found"));
  }

  /**
   * Override this method if the database sorts the strings differently
   *
   * @param extractValueFunction the function to extract a sort value from the object
   * @param <C>                  the object type
   * @return a Comparator for the object of type C
   */
  protected <C> Comparator<C> getStringComparator(Function<C, String> extractValueFunction) {
    return getStringComparator(extractValueFunction, false);
  }

  /**
   * Override this method if the database sorts the strings differently
   *
   * @param <C>                  the object type
   * @param extractValueFunction the function to extract a sort value from the object
   * @param reversed             reverse the sorting order
   * @return a Comparator for the object of type C
   */
  protected <C> Comparator<C> getStringComparator(Function<C, String> extractValueFunction, boolean reversed) {
    return Comparator.comparing(extractValueFunction, getSortComparator(reversed));
  }

  /**
   * Override this method if the database doesn't support handling null values in sorting
   *
   * @param <C> the object type
   * @return a Comparator for the object of type C
   */
  protected <C extends Comparable<? super C>> Comparator<C> getSortComparator() {
    return getSortComparator(false);
  }

  /**
   * Override this method if the database doesn't support handling null values in sorting
   *
   * @param <C>      the object type
   * @param reversed reverse the sorting order
   * @return a Comparator for the object of type C
   */
  protected <C extends Comparable<? super C>> Comparator<C> getSortComparator(boolean reversed) {
    Comparator<C> comparator = Comparator.nullsLast(Comparator.naturalOrder());
    if (reversed) comparator = comparator.reversed();
    return comparator;
  }
}
