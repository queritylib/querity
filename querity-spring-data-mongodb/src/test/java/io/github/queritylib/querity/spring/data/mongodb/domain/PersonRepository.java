package io.github.queritylib.querity.spring.data.mongodb.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository
    extends io.github.queritylib.querity.test.domain.PersonRepository<Person, String>, MongoRepository<Person, String> {
}
