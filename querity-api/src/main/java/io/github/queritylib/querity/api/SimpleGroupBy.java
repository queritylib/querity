package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a GROUP BY clause with property names and/or expressions.
 *
 * <p>A SimpleGroupBy can contain:
 * <ul>
 *   <li>Simple property names (e.g., "category", "address.city")</li>
 *   <li>{@link PropertyExpression} for function-based grouping (e.g., UPPER(category), YEAR(orderDate))</li>
 *   <li>Both property names and expressions combined</li>
 * </ul>
 *
 * <h2>Simple Property Grouping</h2>
 * <pre>{@code
 * AdvancedQuery query = Querity.advancedQuery()
 *     .select(selectBy(
 *         prop("category"),
 *         sum(prop("amount")).as("totalAmount")
 *     ))
 *     .groupBy("category")
 *     .build();
 * }</pre>
 *
 * <h2>Multiple Property Grouping</h2>
 * <pre>{@code
 * AdvancedQuery query = Querity.advancedQuery()
 *     .select(selectBy(
 *         prop("category"),
 *         prop("region"),
 *         count(prop("id")).as("orderCount")
 *     ))
 *     .groupBy("category", "region")
 *     .build();
 * }</pre>
 *
 * <h2>Expression-based Grouping</h2>
 * <pre>{@code
 * AdvancedQuery query = Querity.advancedQuery()
 *     .select(selectBy(
 *         upper(prop("category")).as("upperCategory"),
 *         sum(prop("amount")).as("totalAmount")
 *     ))
 *     .groupByExpressions(upper(prop("category")))
 *     .build();
 * }</pre>
 *
 * <h2>Mixed Grouping (property names + expressions)</h2>
 * <pre>{@code
 * SimpleGroupBy groupBy = SimpleGroupBy.builder()
 *     .propertyName("category")
 *     .propertyName("status")
 *     .expression(upper(prop("region")))
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
   * <p>Can be combined with {@code expressions}.
   */
  @Singular
  private List<String> propertyNames;

  /**
   * Expressions for function-based grouping.
   * <p>Can be combined with {@code propertyNames}.
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
   * Check if this group by uses property names.
   *
   * @return true if property names are set
   */
  @JsonIgnore
  public boolean hasPropertyNames() {
    return propertyNames != null && !propertyNames.isEmpty();
  }

  /**
   * Get all grouping criteria as PropertyExpressions.
   * <p>Combines both propertyNames (converted to PropertyReferences) and expressions
   * into a single list. PropertyNames come first, followed by expressions.
   *
   * @return list of PropertyExpression for all grouping criteria
   */
  @JsonIgnore
  public List<PropertyExpression> getEffectiveExpressions() {
    List<PropertyExpression> result = new ArrayList<>();
    
    // Add property names as PropertyReferences
    if (propertyNames != null) {
      for (String name : propertyNames) {
        result.add(PropertyReference.of(name));
      }
    }
    
    // Add expressions
    if (expressions != null) {
      result.addAll(expressions);
    }
    
    return List.copyOf(result);
  }

  /**
   * Custom builder to validate that at least one of propertyNames or expressions is set.
   */
  public static class SimpleGroupByBuilder {
    public SimpleGroupBy build() {
      boolean hasPropertyNames = propertyNames != null && !propertyNames.isEmpty();
      boolean hasExpressions = expressions != null && !expressions.isEmpty();

      if (!hasPropertyNames && !hasExpressions) {
        throw new IllegalArgumentException("Either propertyNames or expressions must be set");
      }
      return new SimpleGroupBy(propertyNames, expressions);
    }
  }
}
