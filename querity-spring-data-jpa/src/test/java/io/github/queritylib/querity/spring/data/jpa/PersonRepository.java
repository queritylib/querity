package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.jpa.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
