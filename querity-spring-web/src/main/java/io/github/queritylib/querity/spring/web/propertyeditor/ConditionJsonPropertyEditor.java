package io.github.queritylib.querity.spring.web.propertyeditor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.queritylib.querity.api.Condition;

public class ConditionJsonPropertyEditor extends AbstractJsonPropertyEditor<Condition> {
  public ConditionJsonPropertyEditor(ObjectMapper objectMapper) {
    super(objectMapper);
  }
}
