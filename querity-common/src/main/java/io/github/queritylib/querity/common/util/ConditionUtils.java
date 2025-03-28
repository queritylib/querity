package io.github.queritylib.querity.common.util;

import io.github.queritylib.querity.api.Condition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.Set;

import static io.github.queritylib.querity.common.util.ReflectionUtils.constructInstanceWithArgument;
import static io.github.queritylib.querity.common.util.ReflectionUtils.findClassWithConstructorArgumentOfType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConditionUtils {

  public static <T> Optional<T> getConditionImplementation(Set<Class<? extends T>> implementationClasses, Condition condition) {
    return findClassWithConstructorArgumentOfType(implementationClasses, condition.getClass())
        .map(clazz -> constructInstanceWithArgument(clazz, condition));
  }
}
