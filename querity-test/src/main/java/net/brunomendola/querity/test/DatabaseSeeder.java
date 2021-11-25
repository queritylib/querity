package net.brunomendola.querity.test;

import lombok.Getter;
import net.brunomendola.querity.test.util.JsonUtils;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.List;

import static net.brunomendola.querity.test.Constants.TEST_DATA_PATH;

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
