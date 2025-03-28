package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.Operator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MongodbOperatorMapperTests {
  @Test
  void testAllOperatorsSupported() {
    assertThat(Operator.values())
        .allMatch(MongodbOperatorMapper.OPERATOR_CRITERIA_MAP::containsKey);
  }
}
