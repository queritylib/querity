package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.Pagination;
import io.github.queritylib.querity.api.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

@Slf4j
public class MongodbQueryFactory<T> {
  private final Class<T> entityClass;
  private final Query query;

  MongodbQueryFactory(Class<T> entityClass, Query query) {
    this.entityClass = entityClass;
    this.query = query;
  }

  org.springframework.data.mongodb.core.query.Query getMongodbQuery() {
    if (query != null && query.isDistinct()) {
      log.debug("Distinct queries are not supported in MongoDB, ignoring the distinct flag");
    }
    org.springframework.data.mongodb.core.query.Query q = initMongodbQuery();
    q = applyPaginationAndSorting(q);
    return q;
  }

  private org.springframework.data.mongodb.core.query.Query initMongodbQuery() {
    return query == null || !query.hasFilter() ?
        new org.springframework.data.mongodb.core.query.Query() :
        new org.springframework.data.mongodb.core.query.Query(getMongodbCriteria());
  }

  private Criteria getMongodbCriteria() {
    return MongodbCondition.of(query.getFilter()).toCriteria(entityClass);
  }

  private org.springframework.data.mongodb.core.query.Query applyPaginationAndSorting(org.springframework.data.mongodb.core.query.Query q) {
    return query != null && query.hasPagination() ?
        q.with(getMongodbPageRequest()) :
        q.with(getMongodbSort());
  }

  private PageRequest getMongodbPageRequest() {
    Pagination pagination = query.getPagination();
    return PageRequest.of(
        pagination.getPage() - 1,
        pagination.getPageSize(),
        getMongodbSort());
  }

  private org.springframework.data.domain.Sort getMongodbSort() {
    return query == null || !query.hasSort() ?
        org.springframework.data.domain.Sort.unsorted() :
        org.springframework.data.domain.Sort.by(getMongoDbSortOrder());
  }

  private List<Sort.Order> getMongoDbSortOrder() {
    return query.getSort().stream()
        .map(MongodbSort::of)
        .map(MongodbSort::toMongoSortOrder)
        .toList();
  }
}
