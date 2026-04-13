package io.github.queritylib.querity.spring.web.propertyeditor;

import tools.jackson.databind.ObjectMapper;
import io.github.queritylib.querity.api.Query;

public class QueryJsonPropertyEditor extends AbstractJsonPropertyEditor<Query> {
  public QueryJsonPropertyEditor(ObjectMapper objectMapper) {
    super(objectMapper);
  }
}
