package io.github.queritylib.querity.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing supported functions in the Querity query language.
 *
 * <p>Functions can be used in:
 * <ul>
 *   <li>Filter conditions: {@code LENGTH(name) > 5}</li>
 *   <li>Projections/selects: {@code select UPPER(firstName), COUNT(id)}</li>
 *   <li>Sorting: {@code sort by LENGTH(lastName)}</li>
 * </ul>
 *
 * <p><b>Backend support:</b> Not all backends support all functions.
 * JPA supports most functions. MongoDB supports most functions via aggregation pipeline.
 * Elasticsearch has limited support and will throw {@link UnsupportedOperationException}
 * for unsupported functions.
 *
 * @see FunctionCall
 */
@Getter
@RequiredArgsConstructor
public enum Function {

    // Arithmetic functions
    /**
     * Absolute value. Returns the absolute value of a numeric argument.
     * <p>Usage: {@code ABS(amount)} or {@code ABS(-5)}
     */
    ABS(1, FunctionCategory.ARITHMETIC),

    /**
     * Square root. Returns the square root of a numeric argument.
     * <p>Usage: {@code SQRT(value)}
     */
    SQRT(1, FunctionCategory.ARITHMETIC),

    /**
     * Modulo. Returns the remainder of division.
     * <p>Usage: {@code MOD(dividend, divisor)}
     */
    MOD(2, FunctionCategory.ARITHMETIC),

    // String functions
    /**
     * Concatenation. Joins two or more strings together.
     * <p>Usage: {@code CONCAT(firstName, lastName)} or {@code CONCAT(a, b, c, ...)}
     */
    CONCAT(-1, FunctionCategory.STRING),  // -1 = variadic (2+ arguments)

    /**
     * Substring extraction. Extracts a portion of a string.
     * <p>Usage: {@code SUBSTRING(str, start, length)}
     * <p>Note: start is 1-based in most SQL implementations.
     */
    SUBSTRING(3, FunctionCategory.STRING),

    /**
     * Trim whitespace. Removes leading and trailing whitespace from a string.
     * <p>Usage: {@code TRIM(str)}
     */
    TRIM(1, FunctionCategory.STRING),

    /**
     * Left trim. Removes leading whitespace from a string.
     * <p>Usage: {@code LTRIM(str)}
     */
    LTRIM(1, FunctionCategory.STRING),

    /**
     * Right trim. Removes trailing whitespace from a string.
     * <p>Usage: {@code RTRIM(str)}
     */
    RTRIM(1, FunctionCategory.STRING),

    /**
     * Lowercase conversion. Converts a string to lowercase.
     * <p>Usage: {@code LOWER(str)}
     */
    LOWER(1, FunctionCategory.STRING),

    /**
     * Uppercase conversion. Converts a string to uppercase.
     * <p>Usage: {@code UPPER(str)}
     */
    UPPER(1, FunctionCategory.STRING),

    /**
     * String length. Returns the length of a string.
     * <p>Usage: {@code LENGTH(str)}
     */
    LENGTH(1, FunctionCategory.STRING),

    /**
     * Locate substring. Returns the position of a substring within a string.
     * <p>Usage: {@code LOCATE(search, str)}
     * <p>Returns 0 if not found, 1-based position otherwise.
     *
     * <p><b>Note on argument order:</b> Querity follows the SQL standard convention
     * with {@code LOCATE(search, string)}. Internally, JPA CriteriaBuilder.locate()
     * uses reversed argument order {@code locate(string, search)}, which is handled
     * automatically by the JPA mapper.
     */
    LOCATE(2, FunctionCategory.STRING),

    // Date/Time functions
    /**
     * Current date. Returns the current date (without time component).
     * <p>Usage: {@code CURRENT_DATE}
     */
    CURRENT_DATE(0, FunctionCategory.DATE_TIME),

    /**
     * Current time. Returns the current time (without date component).
     * <p>Usage: {@code CURRENT_TIME}
     */
    CURRENT_TIME(0, FunctionCategory.DATE_TIME),

    /**
     * Current timestamp. Returns the current date and time.
     * <p>Usage: {@code CURRENT_TIMESTAMP}
     */
    CURRENT_TIMESTAMP(0, FunctionCategory.DATE_TIME),

    // Logical/Conditional functions
    /**
     * Coalesce. Returns the first non-null argument.
     * <p>Usage: {@code COALESCE(value1, value2, ...)}
     */
    COALESCE(-1, FunctionCategory.CONDITIONAL),  // -1 = variadic (1+ arguments)

    /**
     * Null if equal. Returns null if the two arguments are equal, otherwise returns the first argument.
     * <p>Usage: {@code NULLIF(value1, value2)}
     */
    NULLIF(2, FunctionCategory.CONDITIONAL),

    // Aggregate functions
    /**
     * Count. Counts the number of non-null values.
     * <p>Usage: {@code COUNT(field)} or {@code COUNT(*)}
     * <p><b>Note:</b> Aggregate functions can only be used in projections.
     */
    COUNT(1, FunctionCategory.AGGREGATE),

    /**
     * Sum. Returns the sum of numeric values.
     * <p>Usage: {@code SUM(amount)}
     * <p><b>Note:</b> Aggregate functions can only be used in projections.
     */
    SUM(1, FunctionCategory.AGGREGATE),

    /**
     * Average. Returns the average of numeric values.
     * <p>Usage: {@code AVG(amount)}
     * <p><b>Note:</b> Aggregate functions can only be used in projections.
     */
    AVG(1, FunctionCategory.AGGREGATE),

    /**
     * Minimum. Returns the minimum value.
     * <p>Usage: {@code MIN(amount)}
     * <p><b>Note:</b> Aggregate functions can only be used in projections.
     */
    MIN(1, FunctionCategory.AGGREGATE),

    /**
     * Maximum. Returns the maximum value.
     * <p>Usage: {@code MAX(amount)}
     * <p><b>Note:</b> Aggregate functions can only be used in projections.
     */
    MAX(1, FunctionCategory.AGGREGATE);

    /**
     * The number of arguments this function expects.
     * <ul>
     *   <li>0 = nullary function (no arguments, e.g., CURRENT_DATE)</li>
     *   <li>1+ = fixed argument count</li>
     *   <li>-1 = variadic (variable number of arguments)</li>
     * </ul>
     */
    private final int argumentCount;

    /**
     * The category this function belongs to.
     */
    private final FunctionCategory category;

    /**
     * Checks if this function is an aggregate function.
     * <p>Aggregate functions can only be used in projections, not in filters or sorts.
     *
     * @return true if this is an aggregate function
     */
    public boolean isAggregate() {
        return category == FunctionCategory.AGGREGATE;
    }

    /**
     * Checks if this function accepts a variable number of arguments.
     *
     * @return true if this function is variadic
     */
    public boolean isVariadic() {
        return argumentCount == -1;
    }

    /**
     * Checks if this function requires no arguments (nullary function).
     *
     * @return true if this function takes no arguments
     */
    public boolean isNullary() {
        return argumentCount == 0;
    }

    /**
     * Returns the minimum number of arguments required for this function.
     * <p>For fixed-arity functions, this equals the argument count.
     * For variadic functions, this returns the minimum required (e.g., 2 for CONCAT, 1 for COALESCE).
     *
     * @return the minimum number of arguments required
     */
    public int getMinimumArguments() {
        if (!isVariadic()) {
            return argumentCount;
        }
        // Variadic functions: CONCAT needs at least 2, others need at least 1
        return this == CONCAT ? 2 : 1;
    }

    /**
     * Categories of functions.
     */
    public enum FunctionCategory {
        ARITHMETIC,
        STRING,
        DATE_TIME,
        CONDITIONAL,
        AGGREGATE
    }
}
