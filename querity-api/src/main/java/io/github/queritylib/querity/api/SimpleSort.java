package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a simple sort specification for ordering query results.
 *
 * <p>A SimpleSort can sort by either:
 * <ul>
 *   <li>A simple property name (e.g., "lastName", "address.city")</li>
 *   <li>A {@link PropertyExpression} for function-based sorting (e.g., LENGTH(name))</li>
 * </ul>
 *
 * <h2>Simple Property Sorting</h2>
 * <pre>{@code
 * // Sort by lastName ascending (default)
 * Querity.sortBy("lastName")
 *
 * // Sort by age descending
 * Querity.sortBy("age", Direction.DESC)
 * }</pre>
 *
 * <h2>Function-based Sorting</h2>
 * <pre>{@code
 * // Sort by length of lastName
 * Querity.sortBy(Querity.length(Querity.property("lastName")), Direction.ASC)
 *
 * // Sort by uppercase firstName
 * Querity.sortBy(Querity.upper(Querity.property("firstName")), Direction.DESC)
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
public class SimpleSort implements Sort {

  /**
   * The property name for simple property-based sorting.
   * Either this or {@code expression} must be set, but not both.
   */
  private String propertyName;

  /**
   * The expression for function-based sorting.
   * Either this or {@code propertyName} must be set, but not both.
   */
  private PropertyExpression expression;

  @Builder.Default
  @NonNull
  private Direction direction = Direction.ASC;

  /**
   * Check if this sort uses a function expression.
   *
   * @return true if this sort has an expression
   */
  @JsonIgnore
  public boolean hasExpression() {
    return expression != null;
  }

  /**
   * Get the effective expression for this sort.
   * <p>If an expression is set, returns it. Otherwise, wraps the propertyName
   * in a PropertyReference.
   *
   * @return the expression for this sort
   */
  @JsonIgnore
  public PropertyExpression getEffectiveExpression() {
    return expression != null ? expression : PropertyReference.of(propertyName);
  }

  public enum Direction {
    ASC, DESC
  }

  // Custom builder to validate that exactly one of propertyName or expression is set
  public static class SimpleSortBuilder {
    public SimpleSort build() {
      if (propertyName == null && expression == null) {
        throw new IllegalArgumentException("Either propertyName or expression must be set");
      }
      if (propertyName != null && expression != null) {
        throw new IllegalArgumentException("Cannot set both propertyName and expression");
      }
      // Handle the default value for direction when not explicitly set
      Direction effectiveDirection = direction$value != null ? direction$value : Direction.ASC;
      return new SimpleSort(propertyName, expression, effectiveDirection);
    }
  }
}
