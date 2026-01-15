package io.github.queritylib.querity.spring.web.propertyeditor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.queritylib.querity.api.AdvancedQuery;

public class AdvancedQueryJsonPropertyEditor extends AbstractJsonPropertyEditor<AdvancedQuery> {
  public AdvancedQueryJsonPropertyEditor(ObjectMapper objectMapper) {
    super(objectMapper);
  }
}
