package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.NativeSortWrapper;
import io.github.queritylib.querity.api.SimpleSort;
import io.github.queritylib.querity.api.Sort;

abstract class ElasticsearchSort {

  public abstract org.springframework.data.domain.Sort.Order toElasticsearchSortOrder();

  @SuppressWarnings("unchecked")
  public static ElasticsearchSort of(Sort sort) {
    if (sort instanceof SimpleSort simpleSort) {
      return new ElasticsearchSimpleSort(simpleSort);
    } else if (sort instanceof NativeSortWrapper) {
      return new ElasticsearchNativeSortWrapper((NativeSortWrapper<org.springframework.data.domain.Sort.Order>) sort);
    }
    throw new IllegalArgumentException(
        String.format("Sort class %s is not supported by the Elasticsearch module", sort.getClass().getSimpleName()));
  }
}

