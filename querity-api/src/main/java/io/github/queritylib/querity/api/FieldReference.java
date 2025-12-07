package io.github.queritylib.querity.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a reference to another field in the same entity.
 * Used in SimpleCondition to compare one field against another field
 * instead of comparing against a literal value.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Compare startDate < endDate
 * SimpleCondition.builder()
 *     .propertyName("startDate")
 *     .operator(Operator.LESSER_THAN)
 *     .value(FieldReference.of("endDate"))
 *     .build();
 * }</pre>
 */
@Getter
@EqualsAndHashCode
@ToString
@Builder
@Jacksonized
public class FieldReference {
    @NonNull
    private final String fieldName;

    public static FieldReference of(String fieldName) {
        return new FieldReference(fieldName);
    }
}
