package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static io.github.queritylib.querity.common.util.ReflectionUtils.findClassWithConstructorArgumentOfType;
import static io.github.queritylib.querity.common.util.ReflectionUtils.findSubclasses;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MongodbConditionTests {
  @Test
  void testAllConditionClassesImplemented() {
    Set<Class<? extends Condition>> conditionClasses = findSubclasses(Condition.class);

    Set<Class<? extends MongodbCondition>> implementationClasses = findSubclasses(MongodbCondition.class);

    assertThat(conditionClasses)
        .map(clazz -> findClassWithConstructorArgumentOfType(implementationClasses, clazz))
        .allMatch(Optional::isPresent);
  }

  @Test
  void givenNotSupportedCondition_whenOf_theThrowIllegalArgumentException() {
    Condition condition = new MyCondition();
    assertThrows(IllegalArgumentException.class, () -> MongodbCondition.of(condition),
        "Condition class MyCondition is not supported by the MongoDB module");
  }

  private static class MyCondition implements Condition {
  }
}
