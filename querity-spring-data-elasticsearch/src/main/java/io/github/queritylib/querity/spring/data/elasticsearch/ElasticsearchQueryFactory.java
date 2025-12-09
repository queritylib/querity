package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Pagination;
import io.github.queritylib.querity.api.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.Criteria;

import java.util.List;

@Slf4j
public class ElasticsearchQueryFactory<T> {
  private final Class<T> entityClass;
  private final Query query;

  ElasticsearchQueryFactory(Class<T> entityClass, Query query) {
    this.entityClass = entityClass;
    this.query = query;
  }

  org.springframework.data.elasticsearch.core.query.Query getElasticsearchQuery() {
    if (query != null && query.isDistinct()) {
      log.debug("Distinct queries are not supported in Elasticsearch, ignoring the distinct flag");
    }
    org.springframework.data.elasticsearch.core.query.Query q = initElasticsearchQuery();
    q = applyPaginationAndSorting(q);
    return q;
  }

  org.springframework.data.elasticsearch.core.query.Query getElasticsearchProjectedQuery() {
    if (query == null || !query.hasSelect()) {
      throw new IllegalArgumentException("Query must have a select clause for projection queries");
    }
    if (query.isDistinct()) {
      log.debug("Distinct queries are not supported in Elasticsearch, ignoring the distinct flag");
    }
    org.springframework.data.elasticsearch.core.query.Query q = initElasticsearchQuery();
    applyProjection(q);
    q = applyPaginationAndSorting(q);
    return q;
  }

  private void applyProjection(org.springframework.data.elasticsearch.core.query.Query q) {
    List<String> fields = ElasticsearchSelect.of(query.getSelect()).getFields();
    String[] includes = fields.toArray(new String[0]);
    q.addSourceFilter(org.springframework.data.elasticsearch.core.query.FetchSourceFilter.of(
        sourceFilterBuilder -> sourceFilterBuilder.withIncludes(includes)));
  }

  private org.springframework.data.elasticsearch.core.query.Query initElasticsearchQuery() {
    return query == null || !query.hasFilter() ?
        new org.springframework.data.elasticsearch.core.query.CriteriaQuery(new Criteria()) :
        new org.springframework.data.elasticsearch.core.query.CriteriaQuery(getElasticsearchCriteria());
  }

  private Criteria getElasticsearchCriteria() {
    return ElasticsearchCondition.of(query.getFilter()).toCriteria(entityClass);
  }

  private org.springframework.data.elasticsearch.core.query.Query applyPaginationAndSorting(org.springframework.data.elasticsearch.core.query.Query q) {
    return query != null && query.hasPagination() ?
        q.setPageable(getElasticsearchPageRequest()) :
        q.addSort(getElasticsearchSort());
  }

  private PageRequest getElasticsearchPageRequest() {
    Pagination pagination = query.getPagination();
    return PageRequest.of(
        pagination.getPage() - 1,
        pagination.getPageSize(),
        getElasticsearchSort());
  }

  private org.springframework.data.domain.Sort getElasticsearchSort() {
    return query == null || !query.hasSort() ?
        org.springframework.data.domain.Sort.unsorted() :
        org.springframework.data.domain.Sort.by(getElasticsearchSortOrder());
  }

  private List<Sort.Order> getElasticsearchSortOrder() {
    return query.getSort().stream()
        .map(ElasticsearchSort::of)
        .map(ElasticsearchSort::toElasticsearchSortOrder)
        .toList();
  }
}
