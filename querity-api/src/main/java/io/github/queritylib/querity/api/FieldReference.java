package io.github.queritylib.querity.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a reference to another field in the same entity.
 * Used in SimpleCondition to compare one field against another field
 * instead of comparing against a literal value.
 *
 * <p>This enables queries like "find all records where startDate is before endDate"
 * or "find all products where salePrice is less than originalPrice".
 *
 * <h2>Supported Operators</h2>
 * <ul>
 *   <li>{@link Operator#EQUALS} - field equals another field</li>
 *   <li>{@link Operator#NOT_EQUALS} - field does not equal another field</li>
 *   <li>{@link Operator#GREATER_THAN} - field is greater than another field</li>
 *   <li>{@link Operator#GREATER_THAN_EQUALS} - field is greater than or equal to another field</li>
 *   <li>{@link Operator#LESSER_THAN} - field is less than another field</li>
 *   <li>{@link Operator#LESSER_THAN_EQUALS} - field is less than or equal to another field</li>
 * </ul>
 *
 * <h2>Unsupported Operators</h2>
 * <p>The following operators do not support field references:
 * {@code STARTS_WITH}, {@code ENDS_WITH}, {@code CONTAINS}, {@code IS_NULL},
 * {@code IS_NOT_NULL}, {@code IN}, {@code NOT_IN}.
 *
 * <h2>Backend Support</h2>
 * <ul>
 *   <li><b>JPA/Hibernate</b>: Fully supported using CriteriaBuilder path comparisons</li>
 *   <li><b>MongoDB</b>: Supported using {@code $expr} with comparison operators</li>
 *   <li><b>Elasticsearch</b>: Not supported (throws UnsupportedOperationException)</li>
 * </ul>
 *
 * <h2>Nested Fields</h2>
 * <p>Nested field paths are supported using dot notation:
 * <pre>{@code
 * // Compare address.city with billingAddress.city
 * Querity.filterByField("address.city", Operator.EQUALS, Querity.field("billingAddress.city"))
 * }</pre>
 *
 * <h2>Query Language Syntax</h2>
 * <p>In the Querity query language, field references use the {@code $} prefix:
 * <pre>{@code
 * startDate < $endDate
 * firstName = $lastName
 * address.city != $billingAddress.city
 * }</pre>
 *
 * <h2>JSON API</h2>
 * <p>In JSON, use the {@code fieldRef} property instead of {@code value}:
 * <pre>{@code
 * {
 *   "propertyName": "startDate",
 *   "operator": "LESSER_THAN",
 *   "fieldRef": "endDate"
 * }
 * }</pre>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // Using the builder
 * SimpleCondition.builder()
 *     .propertyName("startDate")
 *     .operator(Operator.LESSER_THAN)
 *     .value(FieldReference.of("endDate"))
 *     .build();
 *
 * // Using the Querity factory methods (recommended)
 * Querity.filterByField("startDate", Operator.LESSER_THAN, Querity.field("endDate"))
 *
 * // In a full query
 * Query query = Querity.query()
 *     .filter(Querity.filterByField("salePrice", Operator.LESSER_THAN, Querity.field("originalPrice")))
 *     .build();
 * }</pre>
 *
 * @see SimpleCondition#isFieldReference()
 * @see Querity#field(String)
 * @see Querity#filterByField(String, Operator, FieldReference)
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
