package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.jpa.domain.DocumentType;
import io.github.queritylib.querity.jpa.domain.Person;
import io.github.queritylib.querity.test.DatabaseSeeder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SpringJpaDatabaseSeeder extends DatabaseSeeder<Person> {

  private final PersonRepository personRepository;

  public SpringJpaDatabaseSeeder(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  @Override
  protected Class<Person> getEntityClass() {
    return Person.class;
  }

  @Override
  protected void saveEntities(List<Person> entities) {
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
    });
    personRepository.saveAll(entities);
    personRepository.flush();
  }

}
