package io.github.queritylib.querity.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtils {
  public static <T> Set<Class<? extends T>> findSubclasses(Class<T> baseClass) {
    return new Reflections(baseClass.getPackage().getName())
        .getSubTypesOf(baseClass).stream()
        .filter(ReflectionUtils::isConcreteClass)
        .collect(Collectors.toSet());
  }

  private static boolean isConcreteClass(Class<?> clazz) {
    return !Modifier.isAbstract(clazz.getModifiers());
  }

  @SuppressWarnings("java:S3011")
  public static <T> Optional<Field> getAccessibleField(Class<T> beanClass, String fieldName) {
    Optional<Field> field = getField(beanClass, fieldName);
    field.ifPresent(f -> f.setAccessible(true));
    return field;
  }

  private static <T> Optional<Field> getField(Class<T> beanClass, String fieldName) {
    Class<?> currentClass = beanClass;
    do {
      Optional<Field> field = Arrays.stream(currentClass.getDeclaredFields())
          .filter(e -> e.getName().equals(fieldName))
          .findFirst();
      if (field.isPresent()) {
        return field;
      } else {
        currentClass = currentClass.getSuperclass();
      }
    } while (currentClass != null);
    return Optional.empty();
  }

  public static <T, A> Optional<Class<? extends T>> findClassWithConstructorArgumentOfType(Set<Class<? extends T>> allClasses,
                                                                                           Class<? extends A> constructorArgumentType) {
    return allClasses.stream()
        .filter(clazz -> Arrays.stream(clazz.getDeclaredConstructors())
            .anyMatch(constructor -> constructor.getParameterCount() == 1 &&
                constructor.getParameterTypes()[0].equals(constructorArgumentType)))
        .findAny();
  }

  @SneakyThrows
  @SuppressWarnings("java:S3011") // we want to have this generic constructor but also package-private classes
  public static <T, A> T constructInstanceWithArgument(Class<T> clazz, A argument) {
    Constructor<T> constructor = clazz.getDeclaredConstructor(argument.getClass());
    constructor.setAccessible(true);
    return constructor.newInstance(argument);
  }
}
