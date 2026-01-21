package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a simple query with filter, sort, and pagination capabilities.
 *
 * <p>Use this class for queries that return entities via {@link Querity#findAll(Class, Query)}.
 * For queries that require projections, grouping, or aggregations, use {@link AdvancedQuery}.
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * Query query = Querity.query()
 *     .filter(filterBy("status", EQUALS, "ACTIVE"))
 *     .sort(sortBy("lastName", ASC))
 *     .pagination(0, 20)
 *     .build();
 *
 * List<Person> people = querity.findAll(Person.class, query);
 * }</pre>
 *
 * @see AdvancedQuery for projection queries with GROUP BY, SELECT, and HAVING
 * @see QueryDefinition for the common query interface
 */
@Builder(toBuilder = true)
@Jacksonized
@Getter
@EqualsAndHashCode(of = {"filter", "pagination", "sort", "distinct"})
@ToString(of = {"filter", "pagination", "sort", "distinct"})
public class Query implements QueryDefinition {
  private final Condition filter;
  private final Pagination pagination;
  @NonNull
  private final Sort[] sort;
  @NonNull
  @JsonIgnore
  private List<QueryPreprocessor> preprocessors;
  @NonNull
  @JsonIgnore
  private List<QueryCustomizer<?>> customizers;
  private boolean distinct;

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

  @Override
  public List<Sort> getSort() {
    return Arrays.asList(sort);
  }

  public static class QueryBuilder {
    @SuppressWarnings("java:S1068")
    private Pagination pagination;
    @SuppressWarnings({"java:S1068", "java:S1450"})
    private Sort[] sort = new Sort[0];
    private List<QueryPreprocessor> preprocessors = new ArrayList<>();
    private List<QueryCustomizer<?>> customizers = new ArrayList<>();

    public QueryBuilder withPreprocessor(QueryPreprocessor preprocessor) {
      if (preprocessor == null) {
        throw new IllegalArgumentException("Preprocessor cannot be null");
      }
      this.preprocessors.add(preprocessor);
      return this;
    }

    public QueryBuilder customize(QueryCustomizer<?>... customizers) {
      if (customizers == null) {
        throw new IllegalArgumentException("Customizers cannot be null");
      }
      this.customizers.addAll(Arrays.asList(customizers));
      return this;
    }

    public QueryBuilder sort(Sort... sort) {
      this.sort = sort;
      return this;
    }

    public QueryBuilder pagination(Pagination pagination) {
      this.pagination = pagination;
      return this;
    }

    public QueryBuilder pagination(Integer page, Integer pageSize) {
      this.pagination = Querity.paged(page, pageSize);
      return this;
    }

    public Query build() {
      return new Query(filter, pagination, sort, List.copyOf(preprocessors), List.copyOf(customizers), distinct);
    }
  }

  public Query preprocess() {
    Query result = this;
    for (QueryPreprocessor preprocessor : preprocessors) {
      result = preprocessor.preprocess(result);
    }
    return result;
  }
}
