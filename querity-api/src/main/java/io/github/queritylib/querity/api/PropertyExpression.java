package io.github.queritylib.querity.api;

/**
 * Marker interface for property expressions that can be used in conditions, sorts, and selects.
 *
 * <p>A PropertyExpression represents either:
 * <ul>
 *   <li>A simple property reference (e.g., "lastName", "address.city") via {@link PropertyReference}</li>
 *   <li>A function call (e.g., "UPPER(lastName)", "LENGTH(name)") via {@link FunctionCall}</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * // Filter using a function
 * Querity.filterBy(Querity.upper(Querity.property("lastName")), Operator.EQUALS, "SKYWALKER")
 *
 * // Sort by a function result
 * Querity.sortBy(Querity.length(Querity.property("lastName")), Direction.ASC)
 *
 * // Select with functions
 * Querity.selectBy(Querity.upper(Querity.property("firstName")), Querity.property("lastName"))
 * }</pre>
 *
 * @see PropertyReference
 * @see FunctionCall
 */
public non-sealed interface PropertyExpression extends FunctionArgument {

    /**
     * Returns a string representation of this expression for logging and debugging purposes.
     *
     * @return a human-readable representation of the expression
     */
    String toExpressionString();
}
