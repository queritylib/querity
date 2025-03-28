package io.github.queritylib.querity.spring.data.jpa;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.query.sqm.tree.domain.SqmPluralValuedSimplePath;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class JpaPropertyUtils {
  @SuppressWarnings("java:S1452") // we don't really know what type to expect
  static Path<?> getPath(Path<?> rootPath, String propertyName) {
    String[] propertyPath = propertyName.split("\\.");
    return getPropertyPath(rootPath, propertyPath);
  }

  private static <T, P> Path<P> getPropertyPath(Path<T> rootPath, String[] propertyPath) {
    String firstLevelProperty = propertyPath[0];
    Path<P> firstLevelPropertyPath = getPropertyPath(rootPath, firstLevelProperty);
    if (propertyPath.length == 1)
      return firstLevelPropertyPath;
    else {
      String[] remainingPath = removeFirstElement(propertyPath);
      return getPropertyPath(firstLevelPropertyPath, remainingPath);
    }
  }

  private static String[] removeFirstElement(String[] propertyPath) {
    return Arrays.copyOfRange(propertyPath, 1, propertyPath.length);
  }

  private static <T, P> Path<P> getPropertyPath(Path<T> rootPath, String propertyName) {
    Path<P> propertyPath = rootPath.get(propertyName);
    if (isCollectionPath(propertyPath))
      propertyPath = getJoin((From<?, T>) rootPath, propertyName);
    return propertyPath;
  }

  private static <P> boolean isCollectionPath(Path<P> propertyPath) {
    return SqmPluralValuedSimplePath.class.isAssignableFrom(propertyPath.getClass());
  }

  private static <T, P> Join<T, P> getJoin(From<?, T> from, String joinProperty) {
    return from.join(joinProperty, JoinType.LEFT);
  }
}
