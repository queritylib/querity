package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.NativeSortWrapper;
import lombok.experimental.Delegate;

class MongodbNativeSortWrapper extends MongodbSort {
  @Delegate
  private final NativeSortWrapper<org.springframework.data.domain.Sort.Order> nativeSortWrapper;

  MongodbNativeSortWrapper(NativeSortWrapper<org.springframework.data.domain.Sort.Order> nativeSortWrapper) {
    this.nativeSortWrapper = nativeSortWrapper;
  }

  @Override
  public org.springframework.data.domain.Sort.Order toMongoSortOrder() {
    return getNativeSort();
  }
}

