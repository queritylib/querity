package net.brunomendola.querity.test;

import lombok.SneakyThrows;
import net.brunomendola.querity.api.*;
import net.brunomendola.querity.test.domain.Person;
import net.brunomendola.querity.test.domain.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.GenericTypeResolver;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public abstract class QuerityGenericSpringTestSuite<T extends Person<?>> {
  public final List<T> entities = getEntities();

  @Autowired
  PersonRepository<T, ?> repository;

  @Autowired
  Querity querity;

  @BeforeEach
  void setUp() {
    repository.saveAll(entities);
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void givenEmptyFilter_whenFilterAll_thenReturnAllTheElements() {
    Query query = Query.builder()
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(6);
    assertThat(result).isEqualTo(entities);
  }

  @Test
  void givenFilterWithStringEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("lastName").operator(Operator.EQUALS).value("Skywalker").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(2);
    assertThat(result).isEqualTo(entities.stream().filter(p -> "Skywalker".equals(p.getLastName())).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithIntegerEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("children").operator(Operator.EQUALS).value("2").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(1);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getChildren() != null && p.getChildren().equals(2))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBigDecimalEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("height").operator(Operator.EQUALS).value("1.72").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(1);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getHeight().compareTo(new BigDecimal("1.72")) == 0)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBigDecimalGreaterThanCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("height").operator(Operator.GREATER_THAN).value("1.72").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(4);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getHeight().compareTo(new BigDecimal("1.72")) > 0)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBigDecimalGreaterThanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("height").operator(Operator.GREATER_THAN_EQUALS).value("1.72").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(5);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getHeight().compareTo(new BigDecimal("1.72")) >= 0)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBigDecimalLesserThanCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("height").operator(Operator.LESSER_THAN).value("1.72").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(1);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getHeight().compareTo(new BigDecimal("1.72")) < 0)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithBigDecimalLesserThanEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("height").operator(Operator.LESSER_THAN_EQUALS).value("1.72").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(2);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getHeight().compareTo(new BigDecimal("1.72")) <= 0)
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringNotEqualsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("lastName").operator(Operator.NOT_EQUALS).value("Skywalker").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(4);
    assertThat(result).isEqualTo(entities.stream().filter(p -> !"Skywalker".equals(p.getLastName())).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringStartsWithCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("lastName").operator(Operator.STARTS_WITH).value("Sky").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(2);
    assertThat(result).isEqualTo(entities.stream().filter(p -> p.getLastName() != null && p.getLastName().startsWith("Sky")).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringEndsWithCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("lastName").operator(Operator.ENDS_WITH).value("walker").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(2);
    assertThat(result).isEqualTo(entities.stream().filter(p -> p.getLastName() != null && p.getLastName().endsWith("walker")).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringContainsCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("lastName").operator(Operator.CONTAINS).value("walk").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(2);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> p.getLastName() != null && p.getLastName().contains("walk"))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringIsNullCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("lastName").operator(Operator.IS_NULL).build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(1);
    assertThat(result).isEqualTo(entities.stream().filter(p -> p.getLastName() == null).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringIsNotNullCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("lastName").operator(Operator.IS_NOT_NULL).build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(5);
    assertThat(result).isEqualTo(entities.stream().filter(p -> p.getLastName() != null).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithTwoStringEqualsConditionsWithAndLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(ConditionsWrapper.builder()
            .conditions(Arrays.asList(
                SimpleCondition.builder().propertyName("lastName").operator(Operator.EQUALS).value("Skywalker").build(),
                SimpleCondition.builder().propertyName("firstName").operator(Operator.EQUALS).value("Luke").build()
            ))
            .build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(1);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> "Skywalker".equals(p.getLastName()) && "Luke".equals(p.getFirstName()))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithTwoStringEqualsConditionsWithOrLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(ConditionsWrapper.builder()
            .logic(LogicOperator.OR)
            .conditions(Arrays.asList(
                SimpleCondition.builder().propertyName("lastName").operator(Operator.EQUALS).value("Skywalker").build(),
                SimpleCondition.builder().propertyName("lastName").operator(Operator.EQUALS).value("Kenobi").build()
            ))
            .build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(3);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> "Skywalker".equals(p.getLastName()) || "Kenobi".equals(p.getLastName()))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNestedConditions_whenFindAll_thenReturnListOfEntity() {
    Query query = Query.builder()
        .filter(ConditionsWrapper.builder()
            .conditions(Arrays.asList(
                SimpleCondition.builder().propertyName("lastName").operator(Operator.EQUALS).value("Skywalker").build(),
                ConditionsWrapper.builder()
                    .logic(LogicOperator.OR)
                    .conditions(Arrays.asList(
                        SimpleCondition.builder().propertyName("firstName").operator(Operator.EQUALS).value("Anakin").build(),
                        SimpleCondition.builder().propertyName("firstName").operator(Operator.EQUALS).value("Luke").build()
                    ))
                    .build()
            ))
            .build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(2);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> "Skywalker".equals(p.getLastName()) && ("Anakin".equals(p.getFirstName()) || "Luke".equals(p.getFirstName())))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithStringEqualsConditionOnNestedField_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(SimpleCondition.builder().propertyName("address.city").operator(Operator.EQUALS).value("Tatooine").build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(1);
    assertThat(result).isEqualTo(entities.stream().filter(p -> "Tatooine".equals(p.getAddress().getCity())).collect(Collectors.toList()));
  }

  @Test
  void givenPagination_whenFilterAll_thenReturnThePageElements() {
    Query query = Query.builder()
        .pagination(Pagination.builder().page(2).pageSize(3).build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(3);
    assertThat(result).isEqualTo(entities.stream().skip(3).limit(3).collect(Collectors.toList()));
  }

  @Test
  void givenSort_whenFilterAll_thenReturnSortedElements() {
    Query query = Query.builder()
        .sort(Arrays.asList(Sort.builder().propertyName("lastName").build(), Sort.builder().propertyName("firstName").build()))
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    Comparator<T> comparator = Comparator
        .comparing((T p) -> p.getLastName(), getSortComparator())
        .thenComparing((T p) -> p.getFirstName());
    assertThat(result).hasSize(6);
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
    Query query = Query.builder()
        .filter(NotCondition.builder().condition(SimpleCondition.builder().propertyName("lastName").operator(Operator.EQUALS).value("Skywalker").build()).build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(4);
    assertThat(result).isEqualTo(entities.stream().filter(p -> !("Skywalker".equals(p.getLastName()))).collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNotConditionWithTwoStringEqualsConditionsWithAndLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(NotCondition.builder().condition(ConditionsWrapper.builder()
            .conditions(Arrays.asList(
                SimpleCondition.builder().propertyName("lastName").operator(Operator.EQUALS).value("Skywalker").build(),
                SimpleCondition.builder().propertyName("firstName").operator(Operator.EQUALS).value("Luke").build()
            ))
            .build()).build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(5);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> !("Skywalker".equals(p.getLastName()) && "Luke".equals(p.getFirstName())))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNotConditionWithTwoStringEqualsConditionsWithOrLogic_whenFilterAll_thenReturnOnlyFilteredElements() {
    Query query = Query.builder()
        .filter(NotCondition.builder().condition(ConditionsWrapper.builder()
            .logic(LogicOperator.OR)
            .conditions(Arrays.asList(
                SimpleCondition.builder().propertyName("lastName").operator(Operator.EQUALS).value("Skywalker").build(),
                SimpleCondition.builder().propertyName("lastName").operator(Operator.EQUALS).value("Kenobi").build()
            ))
            .build()).build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(3);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> !("Skywalker".equals(p.getLastName()) || "Kenobi".equals(p.getLastName())))
        .collect(Collectors.toList()));
  }

  @Test
  void givenFilterWithNotConditionWithNestedConditions_whenFindAll_thenReturnListOfEntity() {
    Query query = Query.builder()
        .filter(NotCondition.builder().condition(ConditionsWrapper.builder()
            .conditions(Arrays.asList(
                SimpleCondition.builder().propertyName("lastName").operator(Operator.EQUALS).value("Skywalker").build(),
                ConditionsWrapper.builder()
                    .logic(LogicOperator.OR)
                    .conditions(Arrays.asList(
                        SimpleCondition.builder().propertyName("firstName").operator(Operator.EQUALS).value("Anakin").build(),
                        SimpleCondition.builder().propertyName("firstName").operator(Operator.EQUALS).value("Luke").build()
                    ))
                    .build()
            ))
            .build()).build())
        .build();
    List<T> result = querity.findAll(getEntityClass(), query);
    assertThat(result).hasSize(4);
    assertThat(result).isEqualTo(entities.stream()
        .filter(p -> !("Skywalker".equals(p.getLastName()) && ("Anakin".equals(p.getFirstName()) || "Luke".equals(p.getFirstName()))))
        .collect(Collectors.toList()));
  }

  @SneakyThrows
  private List<T> getEntities() {
    List<T> entities = CsvUtils.readCsv("/querity/test-data.csv", getEntityClass());
    postImportEntities(entities);
    return entities;
  }

  protected void postImportEntities(List<T> entities) {
    // do nothing by default
  }

  @SuppressWarnings("unchecked")
  private Class<T> getEntityClass() {
    return (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), QuerityGenericSpringTestSuite.class);
  }
}
