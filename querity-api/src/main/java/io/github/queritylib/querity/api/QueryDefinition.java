package io.github.queritylib.querity.api;

import java.util.List;

/**
 * Base interface defining the common structure of a query.
 *
 * <p>This interface provides the core query capabilities shared by all query types:
 * <ul>
 *   <li>Filtering - conditions to filter results</li>
 *   <li>Sorting - ordering of results</li>
 *   <li>Pagination - limiting and offsetting results</li>
 *   <li>Distinct - eliminating duplicate results</li>
 * </ul>
 *
 * @see Query for simple entity queries
 * @see AdvancedQuery for projection queries with GROUP BY, SELECT, and HAVING
 */
public interface QueryDefinition {

  /**
   * Returns the filter condition for this query.
   *
   * @return the filter condition, or null if no filter is set
   */
  Condition getFilter();

  /**
   * Returns the sort specifications for this query.
   *
   * @return list of sort specifications, never null
   */
  List<Sort> getSort();

  /**
   * Returns the pagination specification for this query.
   *
   * @return the pagination, or null if no pagination is set
   */
  Pagination getPagination();

  /**
   * Returns whether this query should return distinct results.
   *
   * @return true if distinct results are requested
   */
  boolean isDistinct();

  /**
   * Checks if this query has a filter condition.
   *
   * @return true if a non-empty filter is set
   */
  boolean hasFilter();

  /**
   * Checks if this query has sort specifications.
   *
   * @return true if at least one sort is set
   */
  boolean hasSort();

  /**
   * Checks if this query has pagination.
   *
   * @return true if pagination is set
   */
  boolean hasPagination();
}
