package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.jpa.domain.Person;
import io.github.queritylib.querity.test.DatabaseSeeder;
import org.springframework.stereotype.Component;

import java.util.List;

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
    entities.forEach(p -> {
      p.getAddress().setPerson(p);
      p.getVisitedLocations().forEach(l -> l.setPerson(p));
      p.getOrders().forEach(o -> {
        o.setPerson(p);
        o.getItems().forEach(i -> i.setOrder(o));
      });
    });
    personRepository.saveAll(entities);
    personRepository.flush();
  }

}
