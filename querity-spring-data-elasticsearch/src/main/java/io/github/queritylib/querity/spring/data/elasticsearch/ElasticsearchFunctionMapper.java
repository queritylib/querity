package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Function;
import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.PropertyReference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Function mapper for Elasticsearch.
 * <p>
 * Elasticsearch does not natively support SQL-like functions in queries.
 * All function expressions will throw {@link UnsupportedOperationException}.
 * <p>
 * To achieve similar functionality in Elasticsearch, consider:
 * <ul>
 *   <li>Using script queries for complex comparisons</li>
 *   <li>Denormalizing your data at index time</li>
 *   <li>Using Elasticsearch's built-in aggregations for aggregate operations</li>
 *   <li>Creating computed fields using ingest pipelines</li>
 * </ul>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElasticsearchFunctionMapper {

  /**
   * Checks if the given function is supported in Elasticsearch.
   *
   * <p>Elasticsearch does NOT support any SQL-like functions natively.
   * This method always returns false. Consider using script queries
   * or denormalizing your data at index time for similar functionality.
   *
   * @param function the function to check
   * @return always false, as Elasticsearch does not support functions
   */
  @SuppressWarnings("unused")
  public static boolean isSupported(Function function) {
    return false;
  }

  /**
   * Get the field name from a PropertyExpression.
   * <p>
   * Throws {@link UnsupportedOperationException} if the expression contains functions.
   *
   * @param expr the property expression
   * @return the field name for simple property references
   * @throws UnsupportedOperationException if the expression is a function call
   */
  public static String getFieldName(PropertyExpression expr) {
    if (expr instanceof PropertyReference pr) {
      return pr.getPropertyName();
    } else if (expr instanceof FunctionCall fc) {
      throw new UnsupportedOperationException(
          "Function " + fc.getFunction() + " is not supported in Elasticsearch. " +
          "Consider using script queries or denormalizing your data.");
    }
    throw new IllegalArgumentException("Unsupported expression type: " + expr.getClass());
  }

  /**
   * Check if an expression contains functions.
   *
   * @param expr the property expression
   * @return true if the expression is a function call
   */
  public static boolean containsFunction(PropertyExpression expr) {
    return expr instanceof FunctionCall;
  }

  /**
   * Validate that no function expressions are used.
   * <p>
   * Throws {@link UnsupportedOperationException} if any function is detected.
   *
   * @param expr the property expression to validate
   * @throws UnsupportedOperationException if the expression contains functions
   */
  public static void validateNoFunctions(PropertyExpression expr) {
    if (expr instanceof FunctionCall fc) {
      throw new UnsupportedOperationException(
          "Function " + fc.getFunction() + " is not supported in Elasticsearch. " +
          "Consider using script queries or denormalizing your data.");
    }
  }
}
