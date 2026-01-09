package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.SimpleGroupBy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA implementation for SimpleGroupBy.
 */
public class JpaSimpleGroupBy implements JpaGroupBy {

  private final SimpleGroupBy simpleGroupBy;

  public JpaSimpleGroupBy(SimpleGroupBy simpleGroupBy) {
    this.simpleGroupBy = simpleGroupBy;
  }

  @Override
  public List<Expression<?>> toExpressions(Metamodel metamodel, Root<?> root, CriteriaBuilder cb) {
    if (simpleGroupBy.hasExpressions()) {
      return toExpressionsFromPropertyExpressions(metamodel, root, cb);
    }
    return toExpressionsFromPropertyNames(metamodel, root);
  }

  private List<Expression<?>> toExpressionsFromPropertyNames(Metamodel metamodel, Root<?> root) {
    List<String> names = simpleGroupBy.getPropertyNames();
    if (names.isEmpty()) {
      return List.of();
    }
    List<Expression<?>> expressions = new ArrayList<>();
    for (String propertyName : names) {
      Path<?> path = JpaPropertyUtils.getPath(root, propertyName, metamodel);
      expressions.add(path);
    }
    return expressions;
  }

  private List<Expression<?>> toExpressionsFromPropertyExpressions(Metamodel metamodel, Root<?> root, CriteriaBuilder cb) {
    List<Expression<?>> expressions = new ArrayList<>();
    List<PropertyExpression> propertyExpressions = simpleGroupBy.getExpressions();

    for (PropertyExpression expr : propertyExpressions) {
      Expression<?> jpaExpr = JpaFunctionMapper.toExpression(expr, root, cb, metamodel);
      expressions.add(jpaExpr);
    }

    return expressions;
  }
}
