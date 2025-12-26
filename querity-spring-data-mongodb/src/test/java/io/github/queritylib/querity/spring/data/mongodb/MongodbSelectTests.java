package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.Function;
import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.PropertyReference;
import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.SimpleSelect;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Field;

import java.util.List;

import static io.github.queritylib.querity.api.Querity.selectBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MongodbSelectTests {

  @Test
  void givenSimpleSelect_whenOf_thenReturnMongodbSimpleSelect() {
    Select simpleSelect = selectBy("id", "name");

    MongodbSelect mongodbSelect = MongodbSelect.of(simpleSelect);

    assertThat(mongodbSelect).isInstanceOf(MongodbSimpleSelect.class);
  }

  @Test
  void givenSimpleSelectWithIdField_whenApplyProjection_thenMapIdToMongoDbId() {
    MongodbSelect mongodbSelect = MongodbSelect.of(selectBy("id", "firstName", "lastName"));
    Field field = mock(Field.class);

    mongodbSelect.applyProjection(field);

    // 'id' should be mapped to MongoDB's '_id'
    verify(field).include("_id");
    verify(field).include("firstName");
    verify(field).include("lastName");
  }

  @Test
  void givenSimpleSelectWithoutIdField_whenApplyProjection_thenIncludeFieldsAsIs() {
    MongodbSelect mongodbSelect = MongodbSelect.of(selectBy("firstName", "email"));
    Field field = mock(Field.class);

    mongodbSelect.applyProjection(field);

    verify(field).include("firstName");
    verify(field).include("email");
  }

  @Test
  void givenSimpleSelectWithNestedField_whenApplyProjection_thenIncludeNestedField() {
    MongodbSelect mongodbSelect = MongodbSelect.of(selectBy("address.city", "address.street"));
    Field field = mock(Field.class);

    mongodbSelect.applyProjection(field);

    verify(field).include("address.city");
    verify(field).include("address.street");
  }

  @Test
  void givenUnsupportedSelect_whenOf_thenThrowIllegalArgumentException() {
    Select unsupportedSelect = new UnsupportedSelect();

    assertThatThrownBy(() -> MongodbSelect.of(unsupportedSelect))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported select type");
  }

  @Test
  void givenSimpleSelectWithExpressions_whenApplyProjection_thenIncludeFields() {
    SimpleSelect simpleSelect = SimpleSelect.ofExpressions(
        PropertyReference.of("firstName"),
        PropertyReference.of("lastName")
    );
    MongodbSelect mongodbSelect = MongodbSelect.of(simpleSelect);
    Field field = mock(Field.class);

    mongodbSelect.applyProjection(field);

    verify(field).include("firstName");
    verify(field).include("lastName");
  }

  @Test
  void givenSimpleSelectWithFunctionExpression_whenApplyProjection_thenThrowUnsupportedOperationException() {
    FunctionCall upperName = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));
    SimpleSelect simpleSelect = SimpleSelect.ofExpressions(upperName);
    MongodbSelect mongodbSelect = MongodbSelect.of(simpleSelect);
    Field field = mock(Field.class);

    assertThatThrownBy(() -> mongodbSelect.applyProjection(field))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("aggregation pipeline");
  }

  private static class UnsupportedSelect implements Select {
    @Override
    public List<String> getPropertyNames() {
      return List.of();
    }
  }
}
