package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NativeGroupByWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA implementation for {@link NativeGroupByWrapper} carrying {@link GroupBySpecification}
 * groupings. Each specification is resolved against the query Root and CriteriaBuilder at
 * execution time, so groupings can use expressions the querity model cannot express
 * (e.g. a JSON-extraction over a jsonb column).
 */
public class JpaNativeGroupByWrapper implements JpaGroupBy {

  private final NativeGroupByWrapper<GroupBySpecification<?>> nativeGroupByWrapper;

  public JpaNativeGroupByWrapper(NativeGroupByWrapper<GroupBySpecification<?>> nativeGroupByWrapper) {
    this.nativeGroupByWrapper = nativeGroupByWrapper;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public List<Expression<?>> toExpressions(Metamodel metamodel, Root<?> root, CriteriaBuilder cb) {
    List<Expression<?>> expressions = new ArrayList<>();
    for (GroupBySpecification<?> spec : nativeGroupByWrapper.getNativeGroupings()) {
      expressions.add(((GroupBySpecification) spec).toExpression(root, cb));
    }
    return expressions;
  }
}
