package io.github.queritylib.querity.test;

import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.test.domain.Order;
import io.github.queritylib.querity.test.domain.OrderItem;
import io.github.queritylib.querity.test.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class QuerityGenericSpringTestSuite<T extends Person<K, ?, ?, ? extends Order<? extends OrderItem>>, K extends Comparable<K>> extends QuerityGenericTestSuite<T, K> {

  @Autowired
  protected Querity querity;
  @Autowired
  private DatabaseSeeder<T> databaseSeeder;

  @Override
  protected DatabaseSeeder<T> getDatabaseSeeder() {
    return databaseSeeder;
  }

  @Override
  protected Querity getQuerity() {
    return querity;
  }
}
