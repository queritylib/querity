package io.github.queritylib.querity.api;

/**
 * Interface for preprocessing AdvancedQuery objects before execution.
 *
 * <p>Preprocessors can modify, validate, or enhance queries before they are executed.
 * Common use cases include:
 * <ul>
 *   <li>Adding default filters (e.g., tenant isolation, soft delete)</li>
 *   <li>Validating query constraints</li>
 *   <li>Transforming property names</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * AdvancedQueryPreprocessor tenantFilter = query -> query.toBuilder()
 *     .filter(and(query.getFilter(), filterBy("tenantId", EQUALS, currentTenantId)))
 *     .build();
 *
 * AdvancedQuery query = Querity.advancedQuery()
 *     .select(prop("category"), sum(prop("amount")).as("total"))
 *     .withPreprocessor(tenantFilter)
 *     .build();
 * }</pre>
 *
 * @see AdvancedQuery#preprocess()
 * @see QueryPreprocessor for simple Query preprocessing
 */
public interface AdvancedQueryPreprocessor {
  /**
   * Preprocess the given query.
   *
   * @param query the query to preprocess
   * @return the preprocessed query (may be the same instance or a new one)
   */
  AdvancedQuery preprocess(AdvancedQuery query);
}
