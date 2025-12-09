package io.github.queritylib.querity.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

/**
 * Represents a simple filter condition comparing a property to a value or another field.
 *
 * <p>A SimpleCondition consists of:
 * <ul>
 *   <li>{@code propertyName} - the property/field to filter on (supports nested paths like "address.city")</li>
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
 * <p>Use {@link #isFieldReference()} to check if this condition compares against another field,
 * and {@link #getFieldReference()} to retrieve the field reference.
 *
 * @see FieldReference
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

  @NonNull
  private final String propertyName;
  @NonNull
  private Operator operator = Operator.EQUALS;
  private final Object value;

  @Builder(toBuilder = true)
  @Jacksonized
  public SimpleCondition(@NonNull String propertyName, Operator operator, Object value) {
    this.propertyName = propertyName;
    if (operator != null)
      this.operator = operator;
    this.value = value;
    validate(this.operator, this.value);
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
