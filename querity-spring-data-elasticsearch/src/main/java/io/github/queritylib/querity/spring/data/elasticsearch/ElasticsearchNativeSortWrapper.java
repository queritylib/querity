package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.NativeSortWrapper;
import lombok.experimental.Delegate;

class ElasticsearchNativeSortWrapper extends ElasticsearchSort {
  @Delegate
  private final NativeSortWrapper<org.springframework.data.domain.Sort.Order> nativeSortWrapper;

  ElasticsearchNativeSortWrapper(NativeSortWrapper<org.springframework.data.domain.Sort.Order> nativeSortWrapper) {
    this.nativeSortWrapper = nativeSortWrapper;
  }

  @Override
  public org.springframework.data.domain.Sort.Order toElasticsearchSortOrder() {
    return getNativeSort();
  }
}

