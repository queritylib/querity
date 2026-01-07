package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a selection of properties or expressions for projection queries.
 *
 * <p>A SimpleSelect can contain:
 * <ul>
 *   <li>Simple property names (e.g., "firstName", "address.city")</li>
 *   <li>{@link PropertyExpression} for function-based projections (e.g., UPPER(name), COUNT(id))</li>
 * </ul>
 *
 * <h2>Simple Property Selection</h2>
 * <pre>{@code
 * // Select specific fields
 * Query query = Querity.query()
 *     .select(Querity.selectBy("firstName", "lastName", "address.city"))
 *     .build();
 * }</pre>
 *
 * <h2>Function-based Selection</h2>
 * <pre>{@code
 * // Select with functions
 * Query query = Querity.query()
 *     .select(Querity.selectBy(
 *         Querity.upper(Querity.property("firstName")),
 *         Querity.length(Querity.property("lastName")),
 *         Querity.property("email")
 *     ))
 *     .build();
 * }</pre>
 *
 * <h2>Aggregation Functions</h2>
 * <pre>{@code
 * // Select with aggregations
 * Query query = Querity.query()
 *     .select(Querity.selectBy(
 *         Querity.count(Querity.property("id")).as("totalCount"),
 *         Querity.sum(Querity.property("amount")).as("totalAmount")
 *     ))
 *     .build();
 * }</pre>
 *
 * @see PropertyExpression
 * @see FunctionCall
 */
@Builder(toBuilder = true)
@Jacksonized
@Getter
@EqualsAndHashCode
@ToString
public class SimpleSelect implements Select {

  /**
   * Simple property names for basic projections.
   * Either this or {@code expressions} can be used.
   */
  @Singular
  private List<String> propertyNames;

  /**
   * Expressions for function-based projections.
   * Either this or {@code propertyNames} can be used.
   */
  @Singular
  private List<PropertyExpression> expressions;

  /**
   * Creates a SimpleSelect with the given property names.
   *
   * @param propertyNames the property names to select
   * @return a new SimpleSelect
   */
  public static SimpleSelect of(String... propertyNames) {
    return SimpleSelect.builder()
        .propertyNames(Arrays.asList(propertyNames))
        .build();
  }

  /**
   * Creates a SimpleSelect with the given expressions.
   *
   * @param expressions the expressions to select
   * @return a new SimpleSelect
   */
  public static SimpleSelect ofExpressions(PropertyExpression... expressions) {
    return SimpleSelect.builder()
        .expressions(Arrays.asList(expressions))
        .build();
  }

  /**
   * Check if this select uses expressions.
   *
   * @return true if expressions are set
   */
  @JsonIgnore
  public boolean hasExpressions() {
    return expressions != null && !expressions.isEmpty();
  }

  /**
   * Get all selections as PropertyExpressions.
   * <p>If expressions are set, returns them. Otherwise, converts propertyNames
   * to PropertyReferences.
   *
   * @return list of PropertyExpression for all selections
   */
  @JsonIgnore
  public List<PropertyExpression> getEffectiveExpressions() {
    if (hasExpressions()) {
      return expressions;
    }
    if (propertyNames != null) {
      return propertyNames.stream()
          .map(name -> (PropertyExpression) PropertyReference.of(name))
          .toList();
    }
    return new ArrayList<>();
  }

  /**
   * Get the alias names for all selections.
   * <p>For expressions with aliases, returns the alias. For property names or
   * expressions without aliases, returns the property name or a generated name.
   *
   * @return list of alias names
   */
  @JsonIgnore
  public List<String> getAliasNames() {
    if (hasExpressions()) {
      return expressions.stream()
          .map(this::getExpressionAlias)
          .toList();
    }
    return propertyNames != null ? new ArrayList<>(propertyNames) : new ArrayList<>();
  }

  private String getExpressionAlias(PropertyExpression expr) {
    if (expr instanceof FunctionCall fc && fc.hasAlias()) {
      return fc.getAlias();
    }
    if (expr instanceof PropertyReference pr) {
      if (pr.hasAlias()) {
        return pr.getAlias();
      }
      return pr.getPropertyName();
    }
    // For function calls without alias, generate a name
    return expr.toExpressionString().replaceAll("[^a-zA-Z0-9]", "_");
  }
}
