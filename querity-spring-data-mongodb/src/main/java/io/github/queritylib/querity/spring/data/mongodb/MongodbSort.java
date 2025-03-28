package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.Sort;
import lombok.experimental.Delegate;

class MongodbSort {
  @Delegate
  private final Sort sort;

  public MongodbSort(Sort sort) {
    this.sort = sort;
  }

  public org.springframework.data.domain.Sort.Order toMongoSortOrder() {
    return new org.springframework.data.domain.Sort.Order(
        getDirection().equals(Sort.Direction.ASC) ?
            org.springframework.data.domain.Sort.Direction.ASC :
            org.springframework.data.domain.Sort.Direction.DESC,
        getPropertyName(),
        org.springframework.data.domain.Sort.NullHandling.NULLS_FIRST // looks like NULLS_LAST is not supported by MongoDB
    );
  }
}
