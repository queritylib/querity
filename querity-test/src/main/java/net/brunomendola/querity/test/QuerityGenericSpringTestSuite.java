package net.brunomendola.querity.test;

import net.brunomendola.querity.api.Querity;
import net.brunomendola.querity.api.Query;
import net.brunomendola.querity.test.domain.Person;
import net.brunomendola.querity.test.domain.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.brunomendola.querity.api.Operator.*;
import static net.brunomendola.querity.api.Querity.*;
import static net.brunomendola.querity.api.Sort.Direction.DESC;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class QuerityGenericSpringTestSuite<T extends Person<K, ?, ?>, K extends Comparable<K>> {

  public static final String PROPERTY_ID = "id";
  public static final String PROPERTY_LAST_NAME = "lastName";
  public static final String PROPERTY_FIRST_NAME = "firstName";
  public static final String PROPERTY_BIRTH_DATE = "birthDate";
  public static final String PROPERTY_HEIGHT = "height";
  public static final String PROPERTY_CHILDREN = "children";
  public static final String PROPERTY_MARRIED = "married";
  public static final String PROPERTY_ADDRESS_CITY = "address.city";

  @Autowired
  private DatabaseSeeder<T> databaseSeeder;

  private List<T> entities;
  private T entity1;
  private T entity2;

  @Autowired
  PersonRepository<T, K> repository;

  @Autowired
  Querity querity;

  @BeforeEach
  void setUp() {
    this.entities = databaseSeeder.getEntities();
    assertThat(this.entities).isNotEmpty();
    this.entity1 = getEntityFromList(20);
    this.entity2 = getEntityFromList(30);
  }

  @Test
  void givenNullQuery_whenFilterAll_thenReturnAllTheElements() {
    List<T> result = querity.findAll(getEntityClass(), null);
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(entities.size());
  }

  @Test
  void givenEmptyFilter_whenFilterAll_thenReturnAllTheElements() {
    Query query = Querity.query()
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities);
  }

  @Test
  void givenFilterWithStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> entity1.getLastName().equals(p.getLastName()))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithIntegerEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_CHILDREN, EQUALS, entity1.getChildren()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getChildren() != null && p.getChildren().equals(entity1.getChildren()))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithIntegerAsStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_CHILDREN, EQUALS, entity1.getChildren()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getChildren() != null && p.getChildren().equals(entity1.getChildren()))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithDateEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_BIRTH_DATE, EQUALS, entity1.getBirthDate()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getBirthDate().isEqual(entity1.getBirthDate()))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithDateAsStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_BIRTH_DATE, EQUALS, formatDate(entity1.getBirthDate())))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getBirthDate().isEqual(entity1.getBirthDate()))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBooleanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_MARRIED, EQUALS, true))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(Person::isMarried)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBooleanAsStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_MARRIED, EQUALS, Boolean.TRUE.toString()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(Person::isMarried)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithIntegerEqualsConditionAsString_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_CHILDREN, EQUALS, entity1.getChildren().toString()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getChildren() != null && p.getChildren().equals(entity1.getChildren()))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBigDecimalEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_HEIGHT, EQUALS, entity1.getHeight()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getHeight().compareTo(entity1.getHeight()) == 0)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBigDecimalEqualsConditionAsString_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_HEIGHT, EQUALS, entity1.getHeight().toString()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getHeight().compareTo(entity1.getHeight()) == 0)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBigDecimalGreaterThanCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_HEIGHT, GREATER_THAN, entity1.getHeight().toString()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getHeight().compareTo(entity1.getHeight()) > 0)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNotBigDecimalGreaterThanCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(not(filterBy(PROPERTY_HEIGHT, GREATER_THAN, entity1.getHeight().toString())))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) > 0))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBigDecimalGreaterThanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_HEIGHT, GREATER_THAN_EQUALS, entity1.getHeight().toString()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getHeight().compareTo(entity1.getHeight()) >= 0)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNotBigDecimalGreaterThanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(not(filterBy(PROPERTY_HEIGHT, GREATER_THAN_EQUALS, entity1.getHeight().toString())))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) >= 0))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBigDecimalLesserThanCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_HEIGHT, LESSER_THAN, entity1.getHeight().toString()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getHeight().compareTo(entity1.getHeight()) < 0)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNotBigDecimalLesserThanCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(not(filterBy(PROPERTY_HEIGHT, LESSER_THAN, entity1.getHeight().toString())))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) < 0))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBigDecimalLesserThanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_HEIGHT, LESSER_THAN_EQUALS, entity1.getHeight().toString()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getHeight().compareTo(entity1.getHeight()) <= 0)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNotBigDecimalLesserThanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(not(filterBy(PROPERTY_HEIGHT, LESSER_THAN_EQUALS, entity1.getHeight().toString())))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> !(p.getHeight().compareTo(entity1.getHeight()) <= 0))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringNotEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_LAST_NAME, NOT_EQUALS, entity1.getLastName()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream().filter(p -> !entity1.getLastName().equals(p.getLastName())).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNotStringNotEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(not(filterBy(PROPERTY_LAST_NAME, NOT_EQUALS, entity1.getLastName())))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> entity1.getLastName().equals(p.getLastName()))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringStartsWithCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    String prefix = entity1.getLastName().substring(0, 3);
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_LAST_NAME, STARTS_WITH, prefix))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getLastName() != null && p.getLastName().startsWith(prefix))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringEndsWithCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    String suffix = entity1.getLastName().substring(3);
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_LAST_NAME, ENDS_WITH, suffix))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getLastName() != null && p.getLastName().endsWith(suffix))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringContainsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    String substring = entity1.getLastName().substring(1, 3);
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_LAST_NAME, CONTAINS, substring))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getLastName() != null && p.getLastName().contains(substring))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringNotContainsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    String substring = entity1.getLastName().substring(1, 3);
    Query query = Querity.query()
        .filter(not(filterBy(PROPERTY_LAST_NAME, CONTAINS, substring)))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getLastName() == null || !p.getLastName().contains(substring))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringIsNullCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_LAST_NAME, IS_NULL))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream().filter(p -> p.getLastName() == null).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringIsNotNullCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_LAST_NAME, IS_NOT_NULL))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream().filter(p -> p.getLastName() != null).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNotStringIsNotNullCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(not(filterBy(PROPERTY_LAST_NAME, IS_NOT_NULL)))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream().filter(p -> p.getLastName() == null).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithTwoStringEqualsConditionsWithAndLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(and(
            filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
            filterBy(PROPERTY_FIRST_NAME, EQUALS, entity1.getFirstName())
        ))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> entity1.getLastName().equals(p.getLastName()) && entity1.getFirstName().equals(p.getFirstName()))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithTwoStringEqualsConditionsWithOrLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(or(
            filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
            filterBy(PROPERTY_LAST_NAME, EQUALS, entity2.getLastName())
        ))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> entity1.getLastName().equals(p.getLastName()) || entity2.getLastName().equals(p.getLastName()))
        .collect(Collectors.toList()));
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
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> entity1.getLastName().equals(p.getLastName()) &&
            (entity1.getFirstName().equals(p.getFirstName()) || entity2.getFirstName().equals(p.getFirstName())))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringEqualsConditionOnNestedField_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(filterBy(PROPERTY_ADDRESS_CITY, EQUALS, entity1.getAddress().getCity()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> entity1.getAddress().getCity().equals(p.getAddress().getCity()))
        .collect(Collectors.toList()));
  }

  @Test
  void givenPagination_whenFilterAll_thenReturnThePageElements() {
    Query query = Querity.query()
        .pagination(2, 3)
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream().skip(3).limit(3).collect(Collectors.toList()));
  }

  @Test
  void givenSortByFieldAscending_whenFilterAll_thenReturnSortedElements() {
    Query query = Querity.query()
        .sort(sortBy(PROPERTY_LAST_NAME), sortBy(PROPERTY_ID))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    Comparator<T> comparator = Comparator
        .comparing((T p) -> p.getLastName(), getSortComparator())
        .thenComparing((T p) -> p.getId());
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(entities.size());
    assertThat(result).isEqualTo(entities.stream().sorted(comparator).collect(Collectors.toList()));
  }

  @Test
  void givenSortByDateField_whenFilterAll_thenReturnSortedElements() {
    Query query = Querity.query()
        .sort(sortBy(PROPERTY_BIRTH_DATE), sortBy(PROPERTY_ID))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    Comparator<T> comparator = Comparator
        .comparing((T p) -> p.getBirthDate())
        .thenComparing((T p) -> p.getId());
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream().sorted(comparator).collect(Collectors.toList()));
  }

  @Test
  void givenSortByFieldDescending_whenFilterAll_thenReturnSortedElements() {
    Query query = Querity.query()
        .sort(sortBy(PROPERTY_LAST_NAME, DESC), sortBy(PROPERTY_ID))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    Comparator<T> comparator = Comparator
        .comparing((T p) -> p.getLastName(), getSortComparator()).reversed()
        .thenComparing((T p) -> p.getId());
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(entities.size());
    assertThat(result).isEqualTo(entities.stream().sorted(comparator).collect(Collectors.toList()));
  }

  @Test
  void givenSortByNestedField_whenFilterAll_thenReturnSortedElements() {
    Query query = Querity.query()
        .sort(sortBy(PROPERTY_ADDRESS_CITY), sortBy(PROPERTY_ID))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    Comparator<T> comparator = Comparator
        .comparing((T p) -> p.getAddress().getCity())
        .thenComparing((T p) -> p.getId());
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(entities.size());
    assertThat(result).isEqualTo(entities.stream().sorted(comparator).collect(Collectors.toList()));
  }

  @Test
  void givenSortByMultipleFields_whenFilterAll_thenReturnSortedElements() {
    Query query = Querity.query()
        .sort(sortBy(PROPERTY_LAST_NAME), sortBy(PROPERTY_FIRST_NAME))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    Comparator<T> comparator = Comparator
        .comparing((T p) -> p.getLastName(), getSortComparator())
        .thenComparing((T p) -> p.getFirstName());
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream().sorted(comparator).collect(Collectors.toList()));
  }

  /**
   * Override this method if the database doesn't support handling null values in sorting
   */
  protected <C extends Comparable<? super C>> Comparator<C> getSortComparator() {
    return Comparator.nullsLast(Comparator.naturalOrder());
  }

  @Test
  void givenFilterWithNotConditionWithStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(not(filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName())))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream().filter(p -> !(entity1.getLastName().equals(p.getLastName()))).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithTwoNestedNotConditionsWithStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(not(not(filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()))))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream().filter(p -> entity1.getLastName().equals(p.getLastName())).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNotConditionWithTwoStringEqualsConditionsWithAndLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(not(and(
            filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
            filterBy(PROPERTY_FIRST_NAME, EQUALS, entity1.getFirstName())
        )))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> !(entity1.getLastName().equals(p.getLastName()) && entity1.getFirstName().equals(p.getFirstName())))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNotConditionWithTwoStringEqualsConditionsWithOrLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Querity.query()
        .filter(not(or(
            filterBy(PROPERTY_LAST_NAME, EQUALS, entity1.getLastName()),
            filterBy(PROPERTY_LAST_NAME, EQUALS, entity2.getLastName())
        )))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).isNotEmpty();
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> !(entity1.getLastName().equals(p.getLastName()) || entity2.getLastName().equals(p.getLastName())))
        .collect(Collectors.toList()));
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
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> !(entity1.getLastName().equals(p.getLastName()) && (entity1.getFirstName().equals(p.getFirstName()) || entity2.getFirstName().equals(p.getFirstName()))))
        .collect(Collectors.toList()));
  }

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

  protected abstract Class<T> getEntityClass();

  private T getEntityFromList(int skip) {
    return entities.stream()
        .filter(e -> e.getLastName() != null)
        .filter(e -> e.getLastName().length() >= 5)
        .filter(e -> e.getChildren() > 0)
        .skip(skip).limit(1).findAny()
        .orElseThrow(() -> new IllegalStateException("No entities found"));
  }

  private static String formatDate(LocalDate birthDate) {
    return birthDate.format(DateTimeFormatter.ISO_DATE);
  }
}
