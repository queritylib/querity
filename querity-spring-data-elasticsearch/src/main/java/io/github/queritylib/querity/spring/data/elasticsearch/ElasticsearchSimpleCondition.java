package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.SimpleCondition;
import lombok.experimental.Delegate;
import org.springframework.data.elasticsearch.core.query.Criteria;

class ElasticsearchSimpleCondition extends ElasticsearchCondition {
  @Delegate
  private final SimpleCondition condition;

  ElasticsearchSimpleCondition(SimpleCondition condition) {
    this.condition = condition;
  }

  @Override
  public <T> Criteria toCriteria(Class<T> entityClass, boolean negate) {
    return ElasticsearchOperatorMapper.getCriteria(entityClass, condition, negate);
  }
}
