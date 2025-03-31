package io.github.queritylib.querity.jakarta.jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

class JpaTestExtension implements BeforeAllCallback, AfterAllCallback {
  private static EntityManagerFactory emf;

  @Override
  public void beforeAll(ExtensionContext context) {
    emf = Persistence.createEntityManagerFactory("example");
  }

  @Override
  public void afterAll(ExtensionContext context) {
    emf.close();
  }

  public static EntityManagerFactory getEntityManagerFactory() {
    return emf;
  }
}
