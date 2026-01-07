package io.github.queritylib.querity.spring.data.elasticsearch;

import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class QuerityElasticsearchImplUnitTests {

  @Test
  void givenMapWithClassKey_whenSanitizeMap_thenExcludeClassKey() throws Exception {
    ElasticsearchOperations mockOperations = mock(ElasticsearchOperations.class);
    QuerityElasticsearchImpl querity = new QuerityElasticsearchImpl(mockOperations);

    Map<String, Object> sourceMap = new HashMap<>();
    sourceMap.put("firstName", "John");
    sourceMap.put("lastName", "Doe");
    sourceMap.put("_class", "io.github.queritylib.querity.spring.data.elasticsearch.domain.Person");

    Method sanitizeMethod = QuerityElasticsearchImpl.class.getDeclaredMethod("sanitizeMap", Map.class);
    sanitizeMethod.setAccessible(true);

    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) sanitizeMethod.invoke(querity, sourceMap);

    assertThat(result)
        .containsKey("firstName")
        .containsKey("lastName")
        .doesNotContainKey("_class");
  }

  @Test
  void givenMapWithNonStringKey_whenSanitizeMap_thenExcludeNonStringKey() throws Exception {
    ElasticsearchOperations mockOperations = mock(ElasticsearchOperations.class);
    QuerityElasticsearchImpl querity = new QuerityElasticsearchImpl(mockOperations);

    Map<Object, Object> sourceMap = new HashMap<>();
    sourceMap.put("firstName", "John");
    sourceMap.put(123, "numericKey");
    sourceMap.put(null, "nullKey");

    Method sanitizeMethod = QuerityElasticsearchImpl.class.getDeclaredMethod("sanitizeMap", Map.class);
    sanitizeMethod.setAccessible(true);

    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) sanitizeMethod.invoke(querity, sourceMap);

    assertThat(result)
        .containsKey("firstName")
        .doesNotContainKey("123")
        .hasSize(1);
  }

  @Test
  void givenEmptyMap_whenSanitizeMap_thenReturnEmptyMap() throws Exception {
    ElasticsearchOperations mockOperations = mock(ElasticsearchOperations.class);
    QuerityElasticsearchImpl querity = new QuerityElasticsearchImpl(mockOperations);

    Map<String, Object> sourceMap = new HashMap<>();

    Method sanitizeMethod = QuerityElasticsearchImpl.class.getDeclaredMethod("sanitizeMap", Map.class);
    sanitizeMethod.setAccessible(true);

    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) sanitizeMethod.invoke(querity, sourceMap);

    assertThat(result).isEmpty();
  }
}
