package io.github.queritylib.querity.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a function call in the Querity query language.
 *
 * <p>A FunctionCall consists of:
 * <ul>
 *   <li>{@code function} - the function to invoke (from {@link Function} enum)</li>
 *   <li>{@code arguments} - the arguments to pass to the function</li>
 * </ul>
 *
 * <p>Arguments can be:
 * <ul>
 *   <li>{@link PropertyReference} - a reference to a property/field</li>
 *   <li>{@link FunctionCall} - a nested function call</li>
 *   <li>Literal values (String, Number, etc.)</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * // Simple function call
 * FunctionCall upperName = Querity.upper(Querity.property("lastName"));
 *
 * // Nested function calls
 * FunctionCall upperTrimmedName = Querity.upper(Querity.trim(Querity.property("lastName")));
 *
 * // Function with multiple arguments
 * FunctionCall fullName = Querity.concat(Querity.property("firstName"), " ", Querity.property("lastName"));
 *
 * // Using in a condition
 * Query query = Querity.query()
 *     .filter(Querity.filterBy(upperName, Operator.EQUALS, "SKYWALKER"))
 *     .build();
 * }</pre>
 *
 * @see Function
 * @see PropertyExpression
 */
@Getter
@EqualsAndHashCode
@ToString
public class FunctionCall implements PropertyExpression {

    /**
     * The function to invoke.
     */
    @NonNull
    private final Function function;

    /**
     * The arguments to pass to the function.
     * <p>Each argument can be a {@link PropertyReference}, another {@link FunctionCall},
     * or a literal value.
     */
    @NonNull
    private final List<FunctionArgument> arguments;

    /**
     * Optional alias for this function call when used in projections.
     */
    private final String alias;

    @Builder
    @Jacksonized
    public FunctionCall(@NonNull Function function, @Singular List<FunctionArgument> arguments, String alias) {
        this.function = function;
        this.arguments = arguments != null ? arguments : Collections.emptyList();
        this.alias = alias;
        validate();
    }

    private void validate() {
        if (function.isNullary() && !arguments.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("Function %s does not accept any arguments, but %d were provided",
                            function, arguments.size()));
        }

        if (!function.isVariadic() && !function.isNullary() && arguments.size() != function.getArgumentCount()) {
            throw new IllegalArgumentException(
                    String.format("Function %s requires %d argument(s), but %d were provided",
                            function, function.getArgumentCount(), arguments.size()));
        }

        if (function.isVariadic()) {
            int minArgs = function.getMinimumArguments();
            if (arguments.size() < minArgs) {
                throw new IllegalArgumentException(
                        String.format("Function %s requires at least %d argument(s), but %d were provided",
                                function, minArgs, arguments.size()));
            }
        }
    }

    /**
     * Creates a FunctionCall with the given function and arguments.
     *
     * @param function the function to invoke
     * @param arguments the arguments to pass
     * @return a new FunctionCall
     */
    public static FunctionCall of(Function function, FunctionArgument... arguments) {
        return FunctionCall.builder()
                .function(function)
                .arguments(List.of(arguments))
                .build();
    }

    /**
     * Creates a copy of this FunctionCall with the given alias.
     *
     * @param alias the alias for this function call
     * @return a new FunctionCall with the alias set
     */
    public FunctionCall as(String alias) {
        return FunctionCall.builder()
                .function(this.function)
                .arguments(this.arguments)
                .alias(alias)
                .build();
    }

    /**
     * Checks if this function call has an alias.
     *
     * @return true if an alias is set
     */
    public boolean hasAlias() {
        return alias != null && !alias.isEmpty();
    }

    @Override
    public String toExpressionString() {
        if (function.isNullary()) {
            return function.name();
        }
        String argsString = arguments.stream()
                .map(this::argumentToString)
                .collect(Collectors.joining(", "));
        String expression = function.name() + "(" + argsString + ")";
        return hasAlias() ? expression + " AS " + alias : expression;
    }

    private String argumentToString(FunctionArgument arg) {
        if (arg instanceof PropertyExpression pe) {
            return pe.toExpressionString();
        } else if (arg instanceof Literal lit) {
            return lit.toString();
        }
        return String.valueOf(arg);
    }
}
