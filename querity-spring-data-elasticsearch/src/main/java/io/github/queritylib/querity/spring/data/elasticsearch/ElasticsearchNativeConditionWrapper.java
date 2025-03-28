package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.NativeConditionWrapper;
import lombok.experimental.Delegate;
import org.springframework.data.elasticsearch.core.query.Criteria;

class ElasticsearchNativeConditionWrapper extends ElasticsearchCondition {
  @Delegate
  private final NativeConditionWrapper<Criteria> nativeConditionWrapper;

  ElasticsearchNativeConditionWrapper(NativeConditionWrapper<Criteria> nativeConditionWrapper) {
    this.nativeConditionWrapper = nativeConditionWrapper;
  }

  @Override
  public <T> Criteria toCriteria(Class<T> entityClass, boolean negate) {
    if (negate)
      throw new IllegalArgumentException("Not conditions wrapping native conditions is not supported; just write a negative native condition.");
    return getNativeCondition();
  }
}
