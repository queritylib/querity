package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.NotCondition;
import lombok.experimental.Delegate;
import org.springframework.data.mongodb.core.query.Criteria;

class MongodbNotCondition extends MongodbCondition {
  @Delegate
  private final NotCondition notCondition;

  public MongodbNotCondition(NotCondition notCondition) {
    this.notCondition = notCondition;
  }

  @Override
  public <T> Criteria toCriteria(Class<T> entityClass, boolean negate) {
    return MongodbCondition.of(getCondition()).toCriteria(entityClass, !negate);
  }
}
