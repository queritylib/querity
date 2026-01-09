package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a GROUP BY clause with property names or expressions.
 *
 * <p>A SimpleGroupBy can contain:
 * <ul>
 *   <li>Simple property names (e.g., "category", "address.city")</li>
 *   <li>{@link PropertyExpression} for function-based grouping (e.g., UPPER(category), YEAR(orderDate))</li>
 * </ul>
 *
 * <p><strong>Important:</strong> Use either {@code propertyNames} or {@code expressions}, not both.
 * The builder will throw an {@link IllegalArgumentException} if both are set or if neither is set.
 * The factory methods {@link #of(String...)} and {@link #ofExpressions(PropertyExpression...)}
 * ensure only one is set.
 *
 * <h2>Simple Property Grouping</h2>
 * <pre>{@code
 * Query query = Querity.query()
 *     .select(selectBy(
 *         prop("category"),
 *         sum(prop("amount")).as("totalAmount")
 *     ))
 *     .groupByProperties("category")
 *     .build();
 * }</pre>
 *
 * <h2>Multiple Property Grouping</h2>
 * <pre>{@code
 * Query query = Querity.query()
 *     .select(selectBy(
 *         prop("category"),
 *         prop("region"),
 *         count(prop("id")).as("orderCount")
 *     ))
 *     .groupByProperties("category", "region")
 *     .build();
 * }</pre>
 *
 * <h2>Expression-based Grouping</h2>
 * <pre>{@code
 * Query query = Querity.query()
 *     .select(selectBy(
 *         upper(prop("category")).as("upperCategory"),
 *         sum(prop("amount")).as("totalAmount")
 *     ))
 *     .groupByExpressions(upper(prop("category")))
 *     .build();
 * }</pre>
 *
 * @see PropertyExpression
 * @see FunctionCall
 * @see GroupBy
 */
@Builder(toBuilder = true)
@Jacksonized
@EqualsAndHashCode
@ToString
public class SimpleGroupBy implements GroupBy {

  /**
   * Simple property names for basic grouping.
   * <p>Use either this or {@code expressions}, not both.
   * The builder will throw an exception if both are set.
   */
  @Singular
  private List<String> propertyNames;

  /**
   * Expressions for function-based grouping.
   * <p>Use either this or {@code propertyNames}, not both.
   * The builder will throw an exception if both are set.
   */
  @Singular
  private List<PropertyExpression> expressions;

  /**
   * Returns an immutable copy of the property names list.
   *
   * @return immutable list of property names, never null
   */
  public List<String> getPropertyNames() {
    return propertyNames == null ? List.of() : List.copyOf(propertyNames);
  }

  /**
   * Returns an immutable copy of the expressions list.
   *
   * @return immutable list of expressions, never null
   */
  public List<PropertyExpression> getExpressions() {
    return expressions == null ? List.of() : List.copyOf(expressions);
  }

  /**
   * Creates a SimpleGroupBy with the given property names.
   *
   * @param propertyNames the property names to group by
   * @return a new SimpleGroupBy
   */
  public static SimpleGroupBy of(String... propertyNames) {
    return SimpleGroupBy.builder()
        .propertyNames(Arrays.asList(propertyNames))
        .build();
  }

  /**
   * Creates a SimpleGroupBy with the given expressions.
   *
   * @param expressions the expressions to group by
   * @return a new SimpleGroupBy
   */
  public static SimpleGroupBy ofExpressions(PropertyExpression... expressions) {
    return SimpleGroupBy.builder()
        .expressions(Arrays.asList(expressions))
        .build();
  }

  /**
   * Check if this group by uses expressions.
   *
   * @return true if expressions are set
   */
  @JsonIgnore
  public boolean hasExpressions() {
    return expressions != null && !expressions.isEmpty();
  }

  /**
   * Get all grouping criteria as PropertyExpressions.
   * <p>If expressions are set, returns them. Otherwise, converts propertyNames
   * to PropertyReferences.
   *
   * @return list of PropertyExpression for all grouping criteria
   */
  @JsonIgnore
  public List<PropertyExpression> getEffectiveExpressions() {
    if (hasExpressions()) {
      return List.copyOf(expressions);
    }
    if (propertyNames != null) {
      return propertyNames.stream()
          .map(name -> (PropertyExpression) PropertyReference.of(name))
          .toList();
    }
    return List.of();
  }

  /**
   * Custom builder to validate that exactly one of propertyNames or expressions is set.
   */
  public static class SimpleGroupByBuilder {
    public SimpleGroupBy build() {
      boolean hasPropertyNames = propertyNames != null && !propertyNames.isEmpty();
      boolean hasExpressions = expressions != null && !expressions.isEmpty();

      if (!hasPropertyNames && !hasExpressions) {
        throw new IllegalArgumentException("Either propertyNames or expressions must be set");
      }
      if (hasPropertyNames && hasExpressions) {
        throw new IllegalArgumentException("Cannot set both propertyNames and expressions");
      }
      return new SimpleGroupBy(propertyNames, expressions);
    }
  }
}
