package io.github.queritylib.querity.api;

/**
 * Represents a valid argument for function calls in Querity.
 *
 * <p>This sealed interface restricts what can be passed to functions:
 * <ul>
 *   <li>{@link PropertyExpression} - property references and nested function calls</li>
 *   <li>{@link Literal} - literal values (strings, numbers, booleans)</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * import static io.github.queritylib.querity.api.Querity.*;
 *
 * // Property references
 * upper(prop("lastName"))
 *
 * // Mix of properties and literals
 * concat(prop("firstName"), lit(" "), prop("lastName"))
 * coalesce(prop("nickname"), lit("Anonymous"))
 * }</pre>
 *
 * @see PropertyExpression
 * @see Literal
 * @see Querity#prop(String)
 * @see Querity#lit(String)
 */
public sealed interface FunctionArgument permits PropertyExpression, Literal {
}
