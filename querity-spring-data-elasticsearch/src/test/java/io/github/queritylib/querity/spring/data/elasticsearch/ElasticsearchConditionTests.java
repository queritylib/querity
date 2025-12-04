package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Condition;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static io.github.queritylib.querity.common.util.ReflectionUtils.findClassWithConstructorArgumentOfType;
import static io.github.queritylib.querity.common.util.ReflectionUtils.findSubclasses;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ElasticsearchConditionTests {
  @Test
  void testAllConditionClassesImplemented() {
    Set<Class<? extends Condition>> conditionClasses = findSubclasses(Condition.class);

    Set<Class<? extends ElasticsearchCondition>> implementationClasses = findSubclasses(ElasticsearchCondition.class);

    assertThat(conditionClasses)
        .map(clazz -> findClassWithConstructorArgumentOfType(implementationClasses, clazz))
        .allMatch(Optional::isPresent);
  }

  @Test
  void givenNotSupportedCondition_whenOf_thenThrowIllegalArgumentException() {
    Condition condition = new UnsupportedCondition();
    assertThrows(IllegalArgumentException.class, () -> ElasticsearchCondition.of(condition),
        "Condition class UnsupportedCondition is not supported by the Elasticsearch module");
  }

  private static class UnsupportedCondition implements Condition {
  }
}

