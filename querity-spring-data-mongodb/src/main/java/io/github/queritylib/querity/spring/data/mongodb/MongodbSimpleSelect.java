package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.SimpleSelect;
import lombok.experimental.Delegate;
import org.springframework.data.mongodb.core.query.Field;

class MongodbSimpleSelect extends MongodbSelect {
  @Delegate
  private final SimpleSelect simpleSelect;

  public MongodbSimpleSelect(SimpleSelect simpleSelect) {
    this.simpleSelect = simpleSelect;
  }

  @Override
  public void applyProjection(Field field) {
    if (simpleSelect.hasExpressions()) {
      // Check if any expression contains functions
      for (PropertyExpression expr : simpleSelect.getExpressions()) {
        if (MongodbFunctionMapper.containsFunction(expr)) {
          throw new UnsupportedOperationException(
              "Function expressions in projections require aggregation pipeline " +
              "which is not supported in MongoDB simple queries. " +
              "Use simple property names for projections.");
        }
        // For PropertyReference, get the field name
        String fieldName = MongodbFunctionMapper.getFieldName(expr);
        field.include(fieldName);
      }
    } else {
      getPropertyNames().stream()
          .map(MongodbSimpleSelect::mapFieldName)
          .forEach(field::include);
    }
  }

  private static String mapFieldName(String fieldName) {
    // Map 'id' to MongoDB's '_id'
    return "id".equals(fieldName) ? "_id" : fieldName;
  }
}
