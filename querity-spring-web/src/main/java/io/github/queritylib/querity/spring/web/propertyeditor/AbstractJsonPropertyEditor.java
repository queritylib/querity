package io.github.queritylib.querity.spring.web.propertyeditor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;

public abstract class AbstractJsonPropertyEditor<T> extends PropertyEditorSupport {
  private final ObjectMapper objectMapper;

  protected AbstractJsonPropertyEditor(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    if (!StringUtils.hasText(text)) {
      setValue(null);
    } else {
      T value;
      try {
        value = objectMapper.readValue(text, getPropertyClass());
      } catch (JsonProcessingException e) {
        throw new IllegalArgumentException(e);
      }
      setValue(value);
    }
  }

  @SuppressWarnings("unchecked")
  private Class<T> getPropertyClass() {
    return (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), AbstractJsonPropertyEditor.class);
  }
}
