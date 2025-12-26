package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

/**
 * Represents a simple filter condition comparing a property or expression to a value or another field.
 *
 * <p>A SimpleCondition consists of:
 * <ul>
 *   <li>{@code propertyName} - the property/field to filter on (supports nested paths like "address.city")</li>
 *   <li>{@code leftExpression} - alternatively, a {@link PropertyExpression} for function-based filtering</li>
 *   <li>{@code operator} - the comparison operator (defaults to {@link Operator#EQUALS})</li>
 *   <li>{@code value} - the value to compare against, or a {@link FieldReference} for field-to-field comparisons</li>
 * </ul>
 *
 * <h2>Field-to-Field Comparison</h2>
 * <p>To compare one field against another field (instead of a literal value), pass a
 * {@link FieldReference} as the value:
 * <pre>{@code
 * // Find records where startDate < endDate
 * SimpleCondition.builder()
 *     .propertyName("startDate")
 *     .operator(Operator.LESSER_THAN)
 *     .value(FieldReference.of("endDate"))
 *     .build();
 *
 * // Or using Querity factory methods
 * Querity.filterByField("startDate", Operator.LESSER_THAN, Querity.field("endDate"))
 * }</pre>
 *
 * <h2>Function-based Filtering</h2>
 * <p>To filter using a function expression on the left side:
 * <pre>{@code
 * // Find records where UPPER(lastName) = "SKYWALKER"
 * SimpleCondition.builder()
 *     .leftExpression(Querity.upper(Querity.property("lastName")))
 *     .operator(Operator.EQUALS)
 *     .value("SKYWALKER")
 *     .build();
 *
 * // Or using Querity factory methods
 * Querity.filterBy(Querity.upper(Querity.property("lastName")), Operator.EQUALS, "SKYWALKER")
 * }</pre>
 *
 * <p>Use {@link #isFieldReference()} to check if this condition compares against another field,
 * and {@link #getFieldReference()} to retrieve the field reference.
 *
 * @see FieldReference
 * @see PropertyExpression
 * @see FunctionCall
 * @see Querity#filterBy(String, Object)
 * @see Querity#filterByField(String, Operator, FieldReference)
 */
@Getter
@EqualsAndHashCode
@ToString
public class SimpleCondition implements Condition {

  private static final Set<Operator> FIELD_REFERENCE_UNSUPPORTED_OPERATORS = Set.of(
      Operator.STARTS_WITH,
      Operator.ENDS_WITH,
      Operator.CONTAINS,
      Operator.IS_NULL,
      Operator.IS_NOT_NULL,
      Operator.IN,
      Operator.NOT_IN
  );

  /**
   * The property name for simple property-based conditions.
   * Either this or {@code leftExpression} must be set, but not both.
   */
  private final String propertyName;

  /**
   * The left-side expression for function-based conditions.
   * Either this or {@code propertyName} must be set, but not both.
   */
  private final PropertyExpression leftExpression;

  @NonNull
  private Operator operator = Operator.EQUALS;
  private final Object value;

  @Builder(toBuilder = true)
  @Jacksonized
  public SimpleCondition(String propertyName, PropertyExpression leftExpression, Operator operator, Object value) {
    // Validate that exactly one of propertyName or leftExpression is set
    if (propertyName == null && leftExpression == null) {
      throw new IllegalArgumentException("Either propertyName or leftExpression must be set");
    }
    if (propertyName != null && leftExpression != null) {
      throw new IllegalArgumentException("Cannot set both propertyName and leftExpression");
    }

    this.propertyName = propertyName;
    this.leftExpression = leftExpression;
    if (operator != null)
      this.operator = operator;
    this.value = value;
    validate(this.operator, this.value);
  }

  /**
   * Check if this condition uses a function expression on the left side.
   *
   * @return true if this condition has a left expression
   */
  @JsonIgnore
  public boolean hasLeftExpression() {
    return leftExpression != null;
  }

  /**
   * Get the effective left-side expression.
   * <p>If a leftExpression is set, returns it. Otherwise, wraps the propertyName
   * in a PropertyReference.
   *
   * @return the left-side expression for this condition
   */
  @JsonIgnore
  public PropertyExpression getEffectiveLeftExpression() {
    return leftExpression != null ? leftExpression : PropertyReference.of(propertyName);
  }

  /**
   * Check if the value is a reference to another field.
   *
   * @return true if this condition compares against another field
   */
  public boolean isFieldReference() {
    return value instanceof FieldReference;
  }

  /**
   * Get the value as a FieldReference.
   *
   * @return the FieldReference, or null if the value is not a field reference
   */
  public FieldReference getFieldReference() {
    return isFieldReference() ? (FieldReference) value : null;
  }

  private void validate(Operator operator, Object value) {
    if (operator.getRequiredValuesCount() != getValuesCount(value))
      throw new IllegalArgumentException(
          String.format("The operator %s requires %d value(s)", operator, operator.getRequiredValuesCount()));

    if (value instanceof FieldReference && FIELD_REFERENCE_UNSUPPORTED_OPERATORS.contains(operator))
      throw new IllegalArgumentException(
          String.format("The operator %s does not support field-to-field comparison", operator));
  }

  private int getValuesCount(Object value) {
    return value == null ? 0 : 1;
  }
}
