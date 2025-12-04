package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.NativeSortWrapper;
import io.github.queritylib.querity.api.SimpleSort;
import io.github.queritylib.querity.api.Sort;

abstract class MongodbSort {

  public abstract org.springframework.data.domain.Sort.Order toMongoSortOrder();

  @SuppressWarnings("unchecked")
  public static MongodbSort of(Sort sort) {
    if (sort instanceof SimpleSort simpleSort) {
      return new MongodbSimpleSort(simpleSort);
    } else if (sort instanceof NativeSortWrapper) {
      return new MongodbNativeSortWrapper((NativeSortWrapper<org.springframework.data.domain.Sort.Order>) sort);
    }
    throw new IllegalArgumentException(
        String.format("Sort class %s is not supported by the MongoDB module", sort.getClass().getSimpleName()));
  }
}

