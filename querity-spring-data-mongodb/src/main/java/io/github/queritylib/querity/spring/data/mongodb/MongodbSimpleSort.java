package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.SimpleSort;
import lombok.experimental.Delegate;

class MongodbSimpleSort extends MongodbSort {
  @Delegate
  private final SimpleSort simpleSort;

  public MongodbSimpleSort(SimpleSort simpleSort) {
    this.simpleSort = simpleSort;
  }

  @Override
  public org.springframework.data.domain.Sort.Order toMongoSortOrder() {
    String fieldName;

    if (simpleSort.hasExpression()) {
      PropertyExpression expr = simpleSort.getExpression();
      // MongodbFunctionMapper.getFieldName throws UnsupportedOperationException for functions
      fieldName = MongodbFunctionMapper.getFieldName(expr);
    } else {
      fieldName = mapFieldName(getPropertyName());
    }

    return new org.springframework.data.domain.Sort.Order(
        getDirection().equals(SimpleSort.Direction.ASC) ?
            org.springframework.data.domain.Sort.Direction.ASC :
            org.springframework.data.domain.Sort.Direction.DESC,
        fieldName,
        org.springframework.data.domain.Sort.NullHandling.NULLS_FIRST
    );
  }

  private static String mapFieldName(String fieldName) {
    return "id".equals(fieldName) ? "_id" : fieldName;
  }
}

