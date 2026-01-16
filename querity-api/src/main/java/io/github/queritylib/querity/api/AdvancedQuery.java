package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an advanced query with projection, grouping, and aggregation capabilities.
 *
 * <p>Use this class for queries that require:
 * <ul>
 *   <li>Projections - selecting specific fields or computed values</li>
 *   <li>Grouping - GROUP BY clauses</li>
 *   <li>Aggregations - COUNT, SUM, AVG, etc. with HAVING filters</li>
 * </ul>
 *
 * <p>Use with {@link Querity#findAllProjected(Class, AdvancedQuery)} which returns
 * {@code List<Map<String, Object>>} instead of entities.
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * AdvancedQuery query = Querity.advancedQuery()
 *     .select(
 *         prop("category"),
 *         sum(prop("amount")).as("totalAmount"),
 *         count(prop("id")).as("orderCount")
 *     )
 *     .filter(filterBy("status", EQUALS, "COMPLETED"))
 *     .groupBy("category")
 *     .having(filterBy(count(prop("id")), GREATER_THAN, 10))
 *     .sort(sortBy("totalAmount", DESC))
 *     .build();
 *
 * List<Map<String, Object>> report = querity.findAllProjected(Order.class, query);
 * }</pre>
 *
 * @see Query for simple entity queries
 * @see QueryDefinition for the common query interface
 */
@Builder(toBuilder = true)
@Jacksonized
@Getter
@EqualsAndHashCode(of = {"filter", "pagination", "sort", "distinct", "select", "groupBy", "having"})
@ToString(of = {"filter", "pagination", "sort", "distinct", "select", "groupBy", "having"})
public class AdvancedQuery implements QueryDefinition {
  private final Condition filter;
  private final Pagination pagination;
  @NonNull
  private final Sort[] sort;
  private boolean distinct;
  private final Select select;
  @Setter(AccessLevel.NONE)
  private final GroupBy groupBy;
  private final Condition having;
  @NonNull
  @JsonIgnore
  private List<AdvancedQueryPreprocessor> preprocessors;

  @Override
  public boolean hasFilter() {
    return filter != null && !filter.isEmpty();
  }

  @Override
  public boolean hasPagination() {
    return pagination != null;
  }

  @Override
  public boolean hasSort() {
    return sort.length > 0;
  }

  public boolean hasSelect() {
    return select != null;
  }

  public boolean hasGroupBy() {
    return groupBy != null;
  }

  public boolean hasHaving() {
    return having != null && !having.isEmpty();
  }

  @Override
  public List<Sort> getSort() {
    return Arrays.asList(sort);
  }

  /**
   * Apply all registered preprocessors to this query.
   *
   * @return the preprocessed query
   */
  public AdvancedQuery preprocess() {
    AdvancedQuery result = this;
    for (AdvancedQueryPreprocessor preprocessor : preprocessors) {
      result = preprocessor.preprocess(result);
    }
    return result;
  }

  public static class AdvancedQueryBuilder {
    @SuppressWarnings("java:S1068")
    private Pagination pagination;
    @SuppressWarnings({"java:S1068", "java:S1450"})
    private Sort[] sort = new Sort[0];
    @SuppressWarnings("java:S1068")
    private Select select;
    @SuppressWarnings("java:S1068")
    private GroupBy groupBy;
    @SuppressWarnings("java:S1068")
    private Condition having;
    private List<AdvancedQueryPreprocessor> preprocessors = new ArrayList<>();

    /**
     * Adds a preprocessor to be applied when {@link AdvancedQuery#preprocess()} is called.
     *
     * @param preprocessor the preprocessor to add
     * @return this builder
     * @throws IllegalArgumentException if preprocessor is null
     */
    public AdvancedQueryBuilder withPreprocessor(AdvancedQueryPreprocessor preprocessor) {
      if (preprocessor == null) {
        throw new IllegalArgumentException("Preprocessor cannot be null");
      }
      this.preprocessors.add(preprocessor);
      return this;
    }

    public AdvancedQueryBuilder sort(Sort... sort) {
      this.sort = sort;
      return this;
    }

    public AdvancedQueryBuilder pagination(Pagination pagination) {
      this.pagination = pagination;
      return this;
    }

    public AdvancedQueryBuilder pagination(Integer page, Integer pageSize) {
      this.pagination = Querity.paged(page, pageSize);
      return this;
    }

    @JsonSetter("select")
    public AdvancedQueryBuilder select(Select select) {
      this.select = select;
      return this;
    }

    public AdvancedQueryBuilder selectBy(String... propertyNames) {
      this.select = Querity.selectBy(propertyNames);
      return this;
    }

    /**
     * Sets the SELECT clause using expressions.
     * <p>Use this method for function-based projections:
     * <pre>{@code
     * advancedQuery()
     *     .select(prop("category"), sum(prop("amount")).as("total"))
     *     .build();
     * }</pre>
     *
     * @param expressions the expressions to select
     * @return this builder
     */
    @JsonIgnore
    public AdvancedQueryBuilder select(PropertyExpression... expressions) {
      this.select = Querity.selectBy(expressions);
      return this;
    }

    /**
     * Sets the GROUP BY clause directly using a GroupBy object.
     * <p>This method is typically used internally or for deserialization.
     * For fluent API usage, prefer {@link #groupBy(String...)} or {@link #groupByExpressions(PropertyExpression...)}.
     *
     * @param groupBy the GroupBy clause
     * @return this builder
     */
    @JsonSetter("groupBy")
    public AdvancedQueryBuilder groupBy(GroupBy groupBy) {
      this.groupBy = groupBy;
      return this;
    }

    /**
     * Sets the GROUP BY clause using property names.
     * <p>This is the simplest and most concise way to group by columns:
     * <pre>{@code
     * advancedQuery().groupBy("category", "region").build();
     * }</pre>
     *
     * @param propertyNames the property names to group by
     * @return this builder
     * @see #groupByExpressions(PropertyExpression...) for function-based grouping
     */
    @JsonIgnore
    public AdvancedQueryBuilder groupBy(String... propertyNames) {
      this.groupBy = Querity.groupBy(propertyNames);
      return this;
    }

    /**
     * Sets the GROUP BY clause using expressions.
     * <p>Use this method when you need function-based grouping:
     * <pre>{@code
     * advancedQuery()
     *     .groupByExpressions(upper(prop("category")), lower(prop("region")))
     *     .build();
     * }</pre>
     *
     * @param expressions the expressions to group by
     * @return this builder
     * @see #groupBy(String...) for simple property-based grouping
     */
    public AdvancedQueryBuilder groupByExpressions(PropertyExpression... expressions) {
      this.groupBy = Querity.groupBy(expressions);
      return this;
    }

    public AdvancedQueryBuilder having(Condition having) {
      this.having = having;
      return this;
    }

    /**
     * Builds the query with semantic validation.
     *
     * @return the built AdvancedQuery
     * @throws IllegalStateException if HAVING is specified without GROUP BY
     */
    public AdvancedQuery build() {
      // Semantic validation: HAVING requires GROUP BY
      if (having != null && !having.isEmpty() && groupBy == null) {
        throw new IllegalStateException("HAVING clause requires a GROUP BY clause");
      }
      return new AdvancedQuery(filter, pagination, sort, distinct, select, groupBy, having, List.copyOf(preprocessors));
    }
  }
}
