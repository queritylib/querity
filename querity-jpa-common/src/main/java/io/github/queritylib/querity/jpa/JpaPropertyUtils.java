package io.github.queritylib.querity.jpa;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class JpaPropertyUtils {
  @SuppressWarnings("java:S1452") // we don't really know what type to expect
  static Path<?> getPath(Path<?> rootPath, String propertyName, Metamodel metamodel) {
    String[] propertyPath = propertyName.split("\\.");
    return getPropertyPath(rootPath, propertyPath, metamodel);
  }

  private static <T, P> Path<P> getPropertyPath(Path<T> rootPath, String[] propertyPath, Metamodel metamodel) {
    String firstLevelProperty = propertyPath[0];
    Path<P> firstLevelPropertyPath = getPropertyPath(rootPath, firstLevelProperty, metamodel);
    if (propertyPath.length == 1)
      return firstLevelPropertyPath;
    else {
      String[] remainingPath = removeFirstElement(propertyPath);
      return getPropertyPath(firstLevelPropertyPath, remainingPath, metamodel);
    }
  }

  private static String[] removeFirstElement(String[] propertyPath) {
    return Arrays.copyOfRange(propertyPath, 1, propertyPath.length);
  }

  private static <T, P> Path<P> getPropertyPath(Path<T> rootPath, String propertyName, Metamodel metamodel) {
    Path<P> propertyPath = rootPath.get(propertyName);
    if (needsJoin(rootPath, propertyName, metamodel))
      propertyPath = getJoin((From<?, T>) rootPath, propertyName);
    return propertyPath;
  }

  private static <T> boolean needsJoin(Path<T> rootPath, String propertyName, Metamodel metamodel) {
    ManagedType<?> rootMetadata = metamodel.managedType(rootPath.getModel().getBindableJavaType());
    Attribute<?, ?> attribute = rootMetadata.getAttribute(propertyName);
    return attribute.isAssociation() || attribute.isCollection();
  }

  private static <T, P> Join<T, P> getJoin(From<?, T> from, String joinProperty) {
    return from.join(joinProperty, JoinType.LEFT);
  }
}
