package io.github.queritylib.querity.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Builder(toBuilder = true)
@Jacksonized
@Getter
@EqualsAndHashCode(of = {"filter", "pagination", "sort", "distinct", "select"})
@ToString(of = {"filter", "pagination", "sort", "distinct", "select"})
public class Query {
  private final Condition filter;
  private final Pagination pagination;
  @NonNull
  private final Sort[] sort;
  @NonNull
  @JsonIgnore
  private List<QueryPreprocessor> preprocessors;
  private boolean distinct;
  private final Select select;

  public boolean hasFilter() {
    return filter != null && !filter.isEmpty();
  }

  public boolean hasPagination() {
    return pagination != null;
  }

  public boolean hasSort() {
    return Arrays.stream(sort).anyMatch(s -> true);
  }

  public boolean hasSelect() {
    return select != null;
  }

  public List<Sort> getSort() {
    return Arrays.asList(sort);
  }

  @NonNull List<QueryPreprocessor> getPreprocessors() {
    return preprocessors;
  }

  public static class QueryBuilder {
    @SuppressWarnings("java:S1068")
    private Pagination pagination;
    @SuppressWarnings({"java:S1068", "java:S1450"})
    private Sort[] sort = new Sort[0];
    private List<QueryPreprocessor> preprocessors = new ArrayList<>();
    private Select select;

    public QueryBuilder withPreprocessor(QueryPreprocessor preprocessor) {
      this.preprocessors.add(preprocessor);
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

    public QueryBuilder select(Select select) {
      this.select = select;
      return this;
    }

    public QueryBuilder selectBy(String... propertyNames) {
      this.select = Querity.selectBy(propertyNames);
      return this;
    }
  }

  public Query preprocess() {
    AtomicReference<Query> atomicQuery = new AtomicReference<>(this);
    this.getPreprocessors().forEach(p -> atomicQuery.set(p.preprocess(atomicQuery.get())));
    return atomicQuery.get();
  }
}
