package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Sort;
import lombok.experimental.Delegate;

class ElasticsearchSort {
  @Delegate
  private final Sort sort;

  public ElasticsearchSort(Sort sort) {
    this.sort = sort;
  }

  public org.springframework.data.domain.Sort.Order toElasticsearchSortOrder() {
    return new org.springframework.data.domain.Sort.Order(
        getDirection().equals(Sort.Direction.ASC) ?
            org.springframework.data.domain.Sort.Direction.ASC :
            org.springframework.data.domain.Sort.Direction.DESC,
        getPropertyName(),
        org.springframework.data.domain.Sort.NullHandling.NULLS_LAST
    );
  }
}
