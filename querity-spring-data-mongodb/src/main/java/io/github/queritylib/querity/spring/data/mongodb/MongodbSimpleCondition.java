package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.SimpleCondition;
import lombok.experimental.Delegate;
import org.springframework.data.mongodb.core.query.Criteria;

class MongodbSimpleCondition extends MongodbCondition {
  @Delegate
  private final SimpleCondition condition;

  MongodbSimpleCondition(SimpleCondition condition) {
    this.condition = condition;
  }

  @Override
  public <T> Criteria toCriteria(Class<T> entityClass, boolean negate) {
    return MongodbOperatorMapper.getCriteria(entityClass, condition, negate);
  }
}
