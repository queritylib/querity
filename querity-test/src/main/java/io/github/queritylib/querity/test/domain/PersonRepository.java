package io.github.queritylib.querity.test.domain;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface PersonRepository<T extends Person<K, ?, ?, ?>, K extends Comparable<K>> extends PagingAndSortingRepository<T, K> {
}
