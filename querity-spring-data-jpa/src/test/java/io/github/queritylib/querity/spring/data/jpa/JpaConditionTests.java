package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static io.github.queritylib.querity.common.util.ReflectionUtils.findClassWithConstructorArgumentOfType;
import static io.github.queritylib.querity.common.util.ReflectionUtils.findSubclasses;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JpaConditionTests {
  @Test
  void testAllConditionClassesImplemented() {
    Set<Class<? extends Condition>> conditionClasses = findSubclasses(Condition.class);

    Set<Class<? extends JpaCondition>> implementationClasses = findSubclasses(JpaCondition.class);

    assertThat(conditionClasses)
        .map(clazz -> findClassWithConstructorArgumentOfType(implementationClasses, clazz))
        .allMatch(Optional::isPresent);
  }

  @Test
  void givenNotSupportedCondition_whenOf_theThrowIllegalArgumentException() {
    Condition condition = new MyCondition();
    assertThrows(IllegalArgumentException.class, () -> JpaCondition.of(condition),
        "Condition class MyCondition is not supported by the JPA module");
  }

  private static class MyCondition implements Condition {
  }
}
