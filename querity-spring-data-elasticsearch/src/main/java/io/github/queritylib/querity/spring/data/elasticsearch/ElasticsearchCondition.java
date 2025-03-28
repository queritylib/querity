package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Condition;
import org.springframework.data.elasticsearch.core.query.Criteria;

import java.util.Set;

import static io.github.queritylib.querity.common.util.ConditionUtils.getConditionImplementation;
import static io.github.queritylib.querity.common.util.ReflectionUtils.findSubclasses;

abstract class ElasticsearchCondition {

  public <T> Criteria toCriteria(Class<T> entityClass) {
    return toCriteria(entityClass, false);
  }

  public abstract <T> Criteria toCriteria(Class<T> entityClass, boolean negate);

  private static final Set<Class<? extends ElasticsearchCondition>> ELASTICSEARCH_CONDITION_IMPLEMENTATIONS = findSubclasses(ElasticsearchCondition.class);

  public static ElasticsearchCondition of(Condition condition) {
    return getConditionImplementation(ELASTICSEARCH_CONDITION_IMPLEMENTATIONS, condition)
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Condition class %s is not supported by the Elasticsearch module", condition.getClass().getSimpleName())));
  }
}
