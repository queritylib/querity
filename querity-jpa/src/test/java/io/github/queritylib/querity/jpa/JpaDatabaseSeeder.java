package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.jpa.domain.DocumentType;
import io.github.queritylib.querity.jpa.domain.Person;
import io.github.queritylib.querity.test.DatabaseSeeder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
      Map<String, DocumentType> documentTypesMap = new HashMap<>();
      entities.forEach(p -> {
        p.getAddress().setPerson(p);
        p.getVisitedLocations().forEach(l -> l.setPerson(p));
        p.getOrders().forEach(o -> {
          o.setPerson(p);
          o.getItems().forEach(i -> i.setOrder(o));
        });
        if (p.getIdDocument() != null) {
          DocumentType docType = p.getIdDocument().getType();
          if (docType != null) {
            // Reuse the same DocumentType instance for identical names
            DocumentType existingDocType = documentTypesMap.get(docType.getCode());
            if (existingDocType == null) {
              documentTypesMap.put(docType.getCode(), docType);
            } else {
              p.getIdDocument().setType(existingDocType);
            }
          }
        }
        em.persist(p);
      });
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
    }
  }

}
