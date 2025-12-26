package io.github.queritylib.querity.api;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Represents a literal value for use in function arguments.
 *
 * <p>Use {@link Querity#lit(String)}, {@link Querity#lit(Number)}, or {@link Querity#lit(Boolean)}
 * to create literal values:
 * <pre>{@code
 * import static io.github.queritylib.querity.api.Querity.*;
 *
 * // String literal
 * concat(prop("firstName"), lit(" - "), prop("lastName"))
 *
 * // Number literal
 * mod(prop("value"), lit(10))
 *
 * // In coalesce
 * coalesce(prop("nickname"), lit("Anonymous"))
 * }</pre>
 *
 * @see FunctionArgument
 * @see Querity#lit(String)
 * @see Querity#lit(Number)
 * @see Querity#lit(Boolean)
 */
@Value
@Jacksonized
public class Literal implements FunctionArgument {

    /**
     * The literal value (String, Number, or Boolean).
     */
    @NonNull
    Object value;

    /**
     * Creates a Literal wrapping the given value.
     *
     * @param value the literal value
     * @return a new Literal
     */
    public static Literal of(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Literal value cannot be null");
        }
        if (!(value instanceof String || value instanceof Number || value instanceof Boolean)) {
            throw new IllegalArgumentException(
                "Literal value must be String, Number, or Boolean, but was: " + value.getClass().getName());
        }
        return new Literal(value);
    }

    @Override
    public String toString() {
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        return String.valueOf(value);
    }
}
