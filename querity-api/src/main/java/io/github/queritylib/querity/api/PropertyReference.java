package io.github.queritylib.querity.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a simple reference to a property/field name.
 *
 * <p>This is a {@link PropertyExpression} that wraps a property name string,
 * allowing property names to be used alongside function calls in a uniform way.
 *
 * <p>Example:
 * <pre>{@code
 * // Create a property reference
 * PropertyReference ref = PropertyReference.of("lastName");
 *
 * // Or use the factory method
 * PropertyExpression expr = Querity.property("address.city");
 * }</pre>
 *
 * @see PropertyExpression
 * @see FunctionCall
 */
@Value
@Builder
@Jacksonized
public class PropertyReference implements PropertyExpression {

    /**
     * The property name, which may include nested paths (e.g., "address.city").
     */
    @NonNull
    String propertyName;

    /**
     * Optional alias for this property reference when used in projections.
     */
    String alias;

    /**
     * Creates a PropertyReference for the given property name.
     *
     * @param propertyName the property name (supports nested paths like "address.city")
     * @return a new PropertyReference
     */
    public static PropertyReference of(String propertyName) {
        return PropertyReference.builder()
                .propertyName(propertyName)
                .build();
    }

    /**
     * Creates a copy of this PropertyReference with the given alias.
     *
     * @param alias the alias for this property reference
     * @return a new PropertyReference with the alias set
     */
    public PropertyReference as(String alias) {
        return PropertyReference.builder()
                .propertyName(this.propertyName)
                .alias(alias)
                .build();
    }

    /**
     * Checks if this property reference has an alias.
     *
     * @return true if an alias is set
     */
    public boolean hasAlias() {
        return alias != null && !alias.isEmpty();
    }

    @Override
    public String toExpressionString() {
        return propertyName;
    }
}
