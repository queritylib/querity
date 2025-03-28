package io.github.queritylib.querity.common.util;

import io.github.queritylib.querity.common.valueextractor.PropertyValueExtractorFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.github.queritylib.querity.common.util.ReflectionUtils.getAccessibleField;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertyUtils {

  public static <T> Class<?> getPropertyType(Class<T> beanClass, String propertyPath) {
    return getPropertyType(beanClass, Arrays.asList(propertyPath.split("\\.")));
  }

  private static <T> Class<?> getPropertyType(Class<T> beanClass, List<String> propertyPath) {
    String fieldName = propertyPath.get(0);
    Class<?> fieldType = getFieldType(beanClass, fieldName);
    if (propertyPath.size() == 1) {
      return fieldType;
    } else {
      return getPropertyType(fieldType, propertyPath.subList(1, propertyPath.size()));
    }
  }

  private static <T> Class<?> getFieldType(Class<T> beanClass, String fieldName) {
    Field field = getAccessibleField(beanClass, fieldName)
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Property %s not found in class %s", fieldName, beanClass.getSimpleName())));
    Class<?> fieldType = field.getType();
    if (isCollectionType(fieldType)) fieldType = getGenericType(field, 0);
    return fieldType;
  }

  private static boolean isCollectionType(Class<?> fieldType) {
    return Collection.class.isAssignableFrom(fieldType);
  }

  private static Class<?> getGenericType(Field field, int index) {
    return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[index];
  }

  public static <T> Object getActualPropertyValue(Class<T> beanClass, String propertyPath, Object value) {
    if (value == null) return null;
    Class<?> propertyType = getPropertyType(beanClass, propertyPath);
    return PropertyValueExtractorFactory.getPropertyValueExtractor(propertyType).extractValue(propertyType, value);
  }
}
