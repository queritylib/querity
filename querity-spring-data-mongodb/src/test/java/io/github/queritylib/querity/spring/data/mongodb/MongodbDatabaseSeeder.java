package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.spring.data.mongodb.domain.Person;
import io.github.queritylib.querity.spring.data.mongodb.domain.PersonRepository;
import io.github.queritylib.querity.test.DatabaseSeeder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongodbDatabaseSeeder extends DatabaseSeeder<Person> {

  private final PersonRepository personRepository;

  public MongodbDatabaseSeeder(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  @Override
  protected Class<Person> getEntityClass() {
    return Person.class;
  }

  @Override
  protected void saveEntities(List<Person> entities) {
    personRepository.saveAll(entities);
  }

}
