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
 *   <li>Both property names and expressions combined</li>
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
 * <h2>Mixed Selection (property names + expressions)</h2>
 * <pre>{@code
 * SimpleSelect select = SimpleSelect.builder()
 *     .propertyName("id")
 *     .propertyName("name")
 *     .expression(upper(prop("category")).as("upperCategory"))
 *     .build();
 * }</pre>
 *
 * @see PropertyExpression
 * @see FunctionCall
 */
@Builder(toBuilder = true)
@Jacksonized
@EqualsAndHashCode
@ToString
public class SimpleSelect implements Select {

  /**
   * Simple property names for basic projections.
   * <p>Can be combined with {@code expressions}.
   */
  @Singular
  private List<String> propertyNames;

  /**
   * Expressions for function-based projections.
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
   * Check if this select uses property names.
   *
   * @return true if property names are set
   */
  @JsonIgnore
  public boolean hasPropertyNames() {
    return propertyNames != null && !propertyNames.isEmpty();
  }

  /**
   * Get all selections as PropertyExpressions.
   * <p>Combines both propertyNames (converted to PropertyReferences) and expressions
   * into a single list.
   *
   * <p><strong>Ordering:</strong> PropertyNames come first (in their original order),
   * followed by expressions (in their original order). This ordering is deterministic
   * but does not preserve interleaved insertion order when mixing the builder methods
   * {@code propertyName()} and {@code expression()}.
   *
   * @return list of PropertyExpression for all selections
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
   * Get the alias names for all selections.
   * <p>For expressions with aliases, returns the alias. For property names or
   * expressions without aliases, returns the property name or a generated name.
   *
   * @return list of alias names
   */
  @JsonIgnore
  public List<String> getAliasNames() {
    List<String> result = new ArrayList<>();
    
    // Add property names directly as aliases
    if (propertyNames != null) {
      result.addAll(propertyNames);
    }
    
    // Add expression aliases
    if (expressions != null) {
      for (PropertyExpression expr : expressions) {
        result.add(getExpressionAlias(expr));
      }
    }
    
    return List.copyOf(result);
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

  /**
   * Custom builder to validate that at least one of propertyNames or expressions is set.
   */
  public static class SimpleSelectBuilder {
    public SimpleSelect build() {
      boolean hasPropertyNames = propertyNames != null && !propertyNames.isEmpty();
      boolean hasExpressions = expressions != null && !expressions.isEmpty();

      if (!hasPropertyNames && !hasExpressions) {
        throw new IllegalArgumentException("Either propertyNames or expressions must be set");
      }
      return new SimpleSelect(propertyNames, expressions);
    }
  }
}
