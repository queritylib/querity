package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.NotCondition;
import lombok.experimental.Delegate;
import org.springframework.data.elasticsearch.core.query.Criteria;

class ElasticsearchNotCondition extends ElasticsearchCondition {
  @Delegate
  private final NotCondition notCondition;

  public ElasticsearchNotCondition(NotCondition notCondition) {
    this.notCondition = notCondition;
  }

  @Override
  public <T> Criteria toCriteria(Class<T> entityClass, boolean negate) {
    return ElasticsearchCondition.of(getCondition()).toCriteria(entityClass, !negate);
  }
}
