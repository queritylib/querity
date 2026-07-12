package io.github.queritylib.querity.spring.data.mongodb;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class QuerityMongodbImplUnitTests {

  @Test
  void givenDocumentWithIdAndClassKeys_whenDocumentToMap_thenMapIdAndExcludeClassKey() throws Exception {
    MongoTemplate mockTemplate = mock(MongoTemplate.class);
    QuerityMongodbImpl querity = new QuerityMongodbImpl(mockTemplate);

    Document document = new Document("_id", "123")
        .append("_class", "io.github.queritylib.querity.spring.data.mongodb.domain.Person")
        .append("firstName", "John");

    Method documentToMap = QuerityMongodbImpl.class.getDeclaredMethod("documentToMap", Document.class);
    documentToMap.setAccessible(true);

    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) documentToMap.invoke(querity, document);

    assertThat(result)
        .containsEntry("id", "123")
        .containsEntry("firstName", "John")
        .doesNotContainKey("_id")
        .doesNotContainKey("_class");
  }
}
