package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static io.github.queritylib.querity.common.util.ReflectionUtils.findClassWithConstructorArgumentOfType;
import static io.github.queritylib.querity.common.util.ReflectionUtils.findSubclasses;
import static org.assertj.core.api.Assertions.assertThat;

public class JpaConditionTests {
  @Test
  void testAllConditionClassesImplemented() {
    Set<Class<? extends Condition>> conditionClasses = findSubclasses(Condition.class);

    Set<Class<? extends JpaCondition>> implementationClasses = findSubclasses(JpaCondition.class);

    assertThat(conditionClasses)
        .map(clazz -> findClassWithConstructorArgumentOfType(implementationClasses, clazz))
        .allMatch(Optional::isPresent);
  }
}
