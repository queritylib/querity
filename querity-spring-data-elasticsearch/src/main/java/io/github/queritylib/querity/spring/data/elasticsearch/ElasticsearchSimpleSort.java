package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.SimpleSort;
import lombok.experimental.Delegate;

class ElasticsearchSimpleSort extends ElasticsearchSort {
  @Delegate
  private final SimpleSort simpleSort;

  public ElasticsearchSimpleSort(SimpleSort simpleSort) {
    this.simpleSort = simpleSort;
  }

  @Override
  public org.springframework.data.domain.Sort.Order toElasticsearchSortOrder() {
    return new org.springframework.data.domain.Sort.Order(
        getDirection().equals(SimpleSort.Direction.ASC) ?
            org.springframework.data.domain.Sort.Direction.ASC :
            org.springframework.data.domain.Sort.Direction.DESC,
        getPropertyName(),
        org.springframework.data.domain.Sort.NullHandling.NULLS_LAST
    );
  }
}

