package io.github.queritylib.querity.spring.data.elasticsearch;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class QuerityElasticsearchImpl implements Querity {

  private final ElasticsearchOperations elasticsearchOperations;

  public QuerityElasticsearchImpl(ElasticsearchOperations elasticsearchOperations) {
    this.elasticsearchOperations = elasticsearchOperations;
  }

  @Override
  public <T> List<T> findAll(Class<T> entityClass, Query query) {
    if (query != null && query.hasSelect()) {
      throw new IllegalArgumentException(
          "findAll() does not support projections. Use findAllProjected() instead.");
    }
    org.springframework.data.elasticsearch.core.query.Query q = getElasticsearchQuery(entityClass, query);
    try {
      SearchHits<T> hits = elasticsearchOperations.search(q, entityClass);
      return hits.stream().map(SearchHit::getContent).toList();
    } catch (UncategorizedElasticsearchException e) {
      log.error(((ElasticsearchException) e.getCause()).response().error().rootCause().get(0).reason());
      throw e;
    }
  }

  @Override
  public <T> Long count(Class<T> entityClass, Condition condition) {
    Query query = Querity.wrapConditionInQuery(condition);
    org.springframework.data.elasticsearch.core.query.Query q = getElasticsearchQuery(entityClass, query);
    return elasticsearchOperations.count(q, entityClass);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> findAllProjected(Class<?> entityClass, Query query) {
    org.springframework.data.elasticsearch.core.query.Query q = getElasticsearchQueryFactory(entityClass, query).getElasticsearchProjectedQuery();
    try {
      SearchHits<Map> hits = elasticsearchOperations.search(q, Map.class, elasticsearchOperations.getIndexCoordinatesFor(entityClass));
      return hits.stream()
          .map(SearchHit::getContent)
          .map(this::sanitizeMap)
          .toList();
    } catch (UncategorizedElasticsearchException e) {
      log.error(((ElasticsearchException) e.getCause()).response().error().rootCause().get(0).reason());
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> sanitizeMap(Map<?, ?> source) {
    Map<String, Object> result = new LinkedHashMap<>();
    source.forEach((key, value) -> {
      if (key instanceof String strKey && !"_class".equals(strKey)) {
        result.put(strKey, value);
      }
    });
    return result;
  }

  private <T> org.springframework.data.elasticsearch.core.query.Query getElasticsearchQuery(Class<T> entityClass, Query query) {
    return getElasticsearchQueryFactory(entityClass, query).getElasticsearchQuery();
  }

  protected static <T> ElasticsearchQueryFactory<T> getElasticsearchQueryFactory(Class<T> entityClass, Query query) {
    return new ElasticsearchQueryFactory<>(entityClass, query);
  }
}
