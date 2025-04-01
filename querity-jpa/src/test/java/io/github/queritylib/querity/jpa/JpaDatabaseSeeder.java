package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.jpa.domain.Person;
import io.github.queritylib.querity.test.DatabaseSeeder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class JpaDatabaseSeeder extends DatabaseSeeder<Person> {

  private final EntityManager em;

  public JpaDatabaseSeeder(EntityManager em) {
    this.em = em;
  }

  @Override
  protected Class<Person> getEntityClass() {
    return Person.class;
  }

  @Override
  protected void saveEntities(List<Person> entities) {
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    try {
      entities.forEach(p -> {
        p.getAddress().setPerson(p);
        p.getVisitedLocations().forEach(l -> l.setPerson(p));
        p.getOrders().forEach(o -> {
          o.setPerson(p);
          o.getItems().forEach(i -> i.setOrder(o));
        });
        em.persist(p);
      });
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
    }
  }

}
