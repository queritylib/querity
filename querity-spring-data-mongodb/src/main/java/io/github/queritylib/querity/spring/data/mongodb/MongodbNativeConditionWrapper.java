package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.NativeConditionWrapper;
import lombok.experimental.Delegate;
import org.springframework.data.mongodb.core.query.Criteria;

class MongodbNativeConditionWrapper extends MongodbCondition {
  @Delegate
  private final NativeConditionWrapper<Criteria> nativeConditionWrapper;

  MongodbNativeConditionWrapper(NativeConditionWrapper<Criteria> nativeConditionWrapper) {
    this.nativeConditionWrapper = nativeConditionWrapper;
  }

  @Override
  public <T> Criteria toCriteria(Class<T> entityClass, boolean negate) {
    if (negate)
      throw new IllegalArgumentException("Not conditions wrapping native conditions is not supported; just write a negative native condition.");
    return getNativeCondition();
  }
}
