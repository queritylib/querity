package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.Querity;
import io.github.queritylib.querity.api.Query;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuerityMongodbImpl implements Querity {

  private final MongoTemplate mongoTemplate;

  public QuerityMongodbImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public <T> List<T> findAll(Class<T> entityClass, Query query) {
    if (query != null && query.hasSelect()) {
      throw new IllegalArgumentException(
          "findAll() does not support projections. Use findAllProjected() instead.");
    }
    org.springframework.data.mongodb.core.query.Query q = getMongodbQuery(entityClass, query);
    return mongoTemplate.find(q, entityClass);
  }

  @Override
  public <T> Long count(Class<T> entityClass, Condition condition) {
    Query query = Querity.wrapConditionInQuery(condition);
    org.springframework.data.mongodb.core.query.Query q = getMongodbQuery(entityClass, query);
    return mongoTemplate.count(q, entityClass);
  }

  @Override
  public List<Map<String, Object>> findAllProjected(Class<?> entityClass, Query query) {
    org.springframework.data.mongodb.core.query.Query q = getMongodbQueryFactory(entityClass, query).getMongodbProjectedQuery();
    return mongoTemplate.find(q, Document.class, mongoTemplate.getCollectionName(entityClass)).stream()
        .map(this::documentToMap)
        .toList();
  }

  private Map<String, Object> documentToMap(Document document) {
    Map<String, Object> map = new LinkedHashMap<>();
    document.forEach((key, value) -> {
      if ("_id".equals(key)) {
        map.put("id", value);
      } else if (!"_class".equals(key)) {
        map.put(key, value);
      }
    });
    return map;
  }

  private <T> org.springframework.data.mongodb.core.query.Query getMongodbQuery(Class<T> entityClass, Query query) {
    return getMongodbQueryFactory(entityClass, query).getMongodbQuery();
  }

  protected static <T> MongodbQueryFactory<T> getMongodbQueryFactory(Class<T> entityClass, Query query) {
    return new MongodbQueryFactory<>(entityClass, query);
  }
}
