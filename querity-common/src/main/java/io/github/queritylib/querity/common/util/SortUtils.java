package io.github.queritylib.querity.common.util;

import io.github.queritylib.querity.api.NativeSortWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static io.github.queritylib.querity.common.util.ReflectionUtils.constructInstanceWithArgument;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SortUtils {

  /**
   * Find a sort implementation that can handle the given NativeSortWrapper based on its generic type.
   * This looks for a class that has a constructor accepting a NativeSortWrapper with a matching generic type.
   */
  public static <T> Optional<T> getSortImplementation(Set<Class<? extends T>> implementationClasses, NativeSortWrapper<?> nativeSortWrapper) {
    Class<?> wrappedType = nativeSortWrapper.getNativeSort().getClass();

    return findClassWithNativeSortWrapperConstructor(implementationClasses, wrappedType)
        .map(clazz -> constructInstanceWithArgument(clazz, nativeSortWrapper));
  }

  private static <T> Optional<Class<? extends T>> findClassWithNativeSortWrapperConstructor(
      Set<Class<? extends T>> allClasses, Class<?> wrappedType) {
    return allClasses.stream()
        .filter(clazz -> hasNativeSortWrapperConstructorForType(clazz, wrappedType))
        .findAny();
  }

  private static boolean hasNativeSortWrapperConstructorForType(Class<?> clazz, Class<?> wrappedType) {
    return Arrays.stream(clazz.getDeclaredConstructors())
        .filter(constructor -> constructor.getParameterCount() == 1)
        .anyMatch(constructor -> {
          Type paramType = constructor.getGenericParameterTypes()[0];
          if (paramType instanceof ParameterizedType pt) {
            if (NativeSortWrapper.class.isAssignableFrom((Class<?>) pt.getRawType())) {
              Type[] typeArgs = pt.getActualTypeArguments();
              if (typeArgs.length == 1) {
                Class<?> expectedType = extractRawType(typeArgs[0]);
                if (expectedType != null) {
                  return expectedType.isAssignableFrom(wrappedType);
                }
              }
            }
          }
          return false;
        });
  }

  private static Class<?> extractRawType(Type type) {
    if (type instanceof Class<?> clazz) {
      return clazz;
    } else if (type instanceof ParameterizedType pt) {
      return (Class<?>) pt.getRawType();
    } else if (type instanceof WildcardType wt) {
      Type[] upperBounds = wt.getUpperBounds();
      if (upperBounds.length > 0) {
        return extractRawType(upperBounds[0]);
      }
    }
    return null;
  }
}

