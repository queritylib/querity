package io.github.queritylib.querity.spring.web.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.SimpleSelect;
import io.github.queritylib.querity.api.SimpleSort;
import io.github.queritylib.querity.api.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeserializerTests {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new QuerityModule());
  }

  @Nested
  class SelectDeserializerTests {
    @Test
    void givenSimpleSelectJson_whenDeserialize_thenReturnSimpleSelect() throws Exception {
      String json = "{\"propertyNames\":[\"id\",\"name\",\"email\"]}";

      Select select = objectMapper.readValue(json, Select.class);

      assertThat(select).isInstanceOf(SimpleSelect.class);
      assertThat(select.getPropertyNames()).containsExactly("id", "name", "email");
    }

    @Test
    void givenSinglePropertySelectJson_whenDeserialize_thenReturnSimpleSelect() throws Exception {
      String json = "{\"propertyNames\":[\"id\"]}";

      Select select = objectMapper.readValue(json, Select.class);

      assertThat(select).isInstanceOf(SimpleSelect.class);
      assertThat(select.getPropertyNames()).containsExactly("id");
    }

    @Test
    void givenInvalidSelectJson_whenDeserialize_thenThrowException() {
      String json = "{\"unknownField\":\"value\"}";

      assertThatThrownBy(() -> objectMapper.readValue(json, Select.class))
          .hasCauseInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  class SortDeserializerTests {
    @Test
    void givenSimpleSortJsonWithDirection_whenDeserialize_thenReturnSimpleSort() throws Exception {
      String json = "{\"propertyName\":\"lastName\",\"direction\":\"DESC\"}";

      Sort sort = objectMapper.readValue(json, Sort.class);

      assertThat(sort).isInstanceOf(SimpleSort.class);
      SimpleSort simpleSort = (SimpleSort) sort;
      assertThat(simpleSort.getPropertyName()).isEqualTo("lastName");
      assertThat(simpleSort.getDirection()).isEqualTo(SimpleSort.Direction.DESC);
    }

    @Test
    void givenSimpleSortJsonWithoutDirection_whenDeserialize_thenReturnSimpleSortWithDefaultAsc() throws Exception {
      String json = "{\"propertyName\":\"lastName\"}";

      Sort sort = objectMapper.readValue(json, Sort.class);

      assertThat(sort).isInstanceOf(SimpleSort.class);
      SimpleSort simpleSort = (SimpleSort) sort;
      assertThat(simpleSort.getPropertyName()).isEqualTo("lastName");
      assertThat(simpleSort.getDirection()).isEqualTo(SimpleSort.Direction.ASC);
    }

    @Test
    void givenInvalidSortJson_whenDeserialize_thenThrowException() {
      String json = "{\"unknownField\":\"value\"}";

      assertThatThrownBy(() -> objectMapper.readValue(json, Sort.class))
          .hasCauseInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  class QuerityModuleTests {
    @Test
    void givenQuerityModule_whenGetModuleName_thenReturnClassName() {
      QuerityModule module = new QuerityModule();

      assertThat(module.getModuleName()).isEqualTo("QuerityModule");
    }

    @Test
    void givenQuerityModule_whenGetVersion_thenReturnUnknownVersion() {
      QuerityModule module = new QuerityModule();

      assertThat(module.version()).isNotNull();
    }
  }
}
