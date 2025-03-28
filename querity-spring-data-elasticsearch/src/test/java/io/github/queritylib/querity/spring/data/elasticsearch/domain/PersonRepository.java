package io.github.queritylib.querity.spring.data.elasticsearch.domain;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository
    extends io.github.queritylib.querity.test.domain.PersonRepository<Person, String>, ElasticsearchRepository<Person, String> {
}
