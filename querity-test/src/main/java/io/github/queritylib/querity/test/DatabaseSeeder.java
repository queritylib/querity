package io.github.queritylib.querity.test;

import io.github.queritylib.querity.test.util.JsonUtils;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.List;

import static io.github.queritylib.querity.test.Constants.TEST_DATA_PATH;

public abstract class DatabaseSeeder<T> implements InitializingBean {

  @Getter
  private List<T> entities;

  @Override
  public void afterPropertiesSet() throws Exception {
    seedDatabase();
  }

  private void seedDatabase() throws IOException {
    this.entities = JsonUtils.readListFromJson(TEST_DATA_PATH, getEntityClass());
    saveEntities(this.entities);
  }

  protected abstract void saveEntities(List<T> entities);

  protected abstract Class<T> getEntityClass();
}
