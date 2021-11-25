package net.brunomendola.querity.spring.data.jpa;

import net.brunomendola.querity.spring.data.jpa.domain.Person;
import net.brunomendola.querity.spring.data.jpa.domain.PersonRepository;
import net.brunomendola.querity.test.DatabaseSeeder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JpaDatabaseSeeder extends DatabaseSeeder<Person> {

  private final PersonRepository personRepository;

  public JpaDatabaseSeeder(PersonRepository personRepository) {
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
    });
    personRepository.saveAll(entities);
    personRepository.flush();
  }

}
