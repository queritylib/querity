package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Function;
import io.github.queritylib.querity.api.FunctionArgument;
import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.Literal;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.PropertyReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Maps Querity functions to JPA CriteriaBuilder expressions.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JpaFunctionMapper {

  static final Map<Function, JpaFunctionExpressionProvider> FUNCTION_MAP = new EnumMap<>(Function.class);

  static {
    // Arithmetic functions
    FUNCTION_MAP.put(Function.ABS, JpaFunctionMapper::abs);
    FUNCTION_MAP.put(Function.SQRT, JpaFunctionMapper::sqrt);
    FUNCTION_MAP.put(Function.MOD, JpaFunctionMapper::mod);

    // String functions
    FUNCTION_MAP.put(Function.CONCAT, JpaFunctionMapper::concat);
    FUNCTION_MAP.put(Function.SUBSTRING, JpaFunctionMapper::substring);
    FUNCTION_MAP.put(Function.TRIM, JpaFunctionMapper::trim);
    FUNCTION_MAP.put(Function.LTRIM, JpaFunctionMapper::ltrim);
    FUNCTION_MAP.put(Function.RTRIM, JpaFunctionMapper::rtrim);
    FUNCTION_MAP.put(Function.LOWER, JpaFunctionMapper::lower);
    FUNCTION_MAP.put(Function.UPPER, JpaFunctionMapper::upper);
    FUNCTION_MAP.put(Function.LENGTH, JpaFunctionMapper::length);
    FUNCTION_MAP.put(Function.LOCATE, JpaFunctionMapper::locate);

    // Date/Time functions
    FUNCTION_MAP.put(Function.CURRENT_DATE, JpaFunctionMapper::currentDate);
    FUNCTION_MAP.put(Function.CURRENT_TIME, JpaFunctionMapper::currentTime);
    FUNCTION_MAP.put(Function.CURRENT_TIMESTAMP, JpaFunctionMapper::currentTimestamp);

    // Conditional functions
    FUNCTION_MAP.put(Function.COALESCE, JpaFunctionMapper::coalesce);
    FUNCTION_MAP.put(Function.NULLIF, JpaFunctionMapper::nullif);

    // Aggregate functions
    FUNCTION_MAP.put(Function.COUNT, JpaFunctionMapper::count);
    FUNCTION_MAP.put(Function.SUM, JpaFunctionMapper::sum);
    FUNCTION_MAP.put(Function.AVG, JpaFunctionMapper::avg);
    FUNCTION_MAP.put(Function.MIN, JpaFunctionMapper::min);
    FUNCTION_MAP.put(Function.MAX, JpaFunctionMapper::max);
  }

  /**
   * Checks if the given function is supported in JPA.
   *
   * <p>JPA supports all Querity functions via CriteriaBuilder.
   *
   * @param function the function to check
   * @return true if the function is supported (always true for JPA)
   */
  public static boolean isSupported(Function function) {
    return FUNCTION_MAP.containsKey(function);
  }

  /**
   * Convert a PropertyExpression to a JPA Expression.
   *
   * @param expr      the property expression
   * @param root      the query root
   * @param cb        the criteria builder
   * @param metamodel the JPA metamodel
   * @return a JPA Expression
   */
  @SuppressWarnings("java:S1452")
  public static Expression<?> toExpression(PropertyExpression expr, Root<?> root,
      CriteriaBuilder cb, Metamodel metamodel) {
    if (expr instanceof PropertyReference pr) {
      return JpaPropertyUtils.getPath(root, pr.getPropertyName(), metamodel);
    } else if (expr instanceof FunctionCall fc) {
      JpaFunctionExpressionProvider provider = FUNCTION_MAP.get(fc.getFunction());
      if (provider == null) {
        throw new UnsupportedOperationException(
            "Function " + fc.getFunction() + " is not supported in JPA");
      }
      return provider.toExpression(fc.getArguments(), root, cb, metamodel);
    }
    throw new IllegalArgumentException("Unsupported expression type: " + expr.getClass());
  }

  // --- Arithmetic functions ---

  private static Expression<?> abs(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<Number> arg = castExpression(toExpressionOrLiteral(args.get(0), root, cb, metamodel));
    return cb.abs(arg);
  }

  private static Expression<?> sqrt(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<? extends Number> arg = castExpression(toExpressionOrLiteral(args.get(0), root, cb, metamodel));
    return cb.sqrt(arg);
  }

  private static Expression<?> mod(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<Integer> dividend = toIntExpression(args.get(0), root, cb, metamodel);
    Expression<Integer> divisor = toIntExpression(args.get(1), root, cb, metamodel);
    return cb.mod(dividend, divisor);
  }

  // --- String functions ---

  private static Expression<?> concat(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    if (args.size() < 2) {
      throw new IllegalArgumentException("CONCAT requires at least 2 arguments");
    }
    Expression<String> result = toStringExpression(args.get(0), root, cb, metamodel);
    for (int i = 1; i < args.size(); i++) {
      Expression<String> next = toStringExpression(args.get(i), root, cb, metamodel);
      result = cb.concat(result, next);
    }
    return result;
  }

  private static Expression<?> substring(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<String> str = toStringExpression(args.get(0), root, cb, metamodel);
    Expression<Integer> start = toIntExpression(args.get(1), root, cb, metamodel);
    Expression<Integer> length = toIntExpression(args.get(2), root, cb, metamodel);
    return cb.substring(str, start, length);
  }

  private static Expression<?> trim(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<String> str = toStringExpression(args.get(0), root, cb, metamodel);
    return cb.trim(str);
  }

  private static Expression<?> ltrim(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<String> str = toStringExpression(args.get(0), root, cb, metamodel);
    return cb.trim(CriteriaBuilder.Trimspec.LEADING, str);
  }

  private static Expression<?> rtrim(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<String> str = toStringExpression(args.get(0), root, cb, metamodel);
    return cb.trim(CriteriaBuilder.Trimspec.TRAILING, str);
  }

  private static Expression<?> lower(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<String> str = toStringExpression(args.get(0), root, cb, metamodel);
    return cb.lower(str);
  }

  private static Expression<?> upper(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<String> str = toStringExpression(args.get(0), root, cb, metamodel);
    return cb.upper(str);
  }

  private static Expression<?> length(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<String> str = toStringExpression(args.get(0), root, cb, metamodel);
    return cb.length(str);
  }

  /**
   * LOCATE function implementation.
   * Note: Querity uses LOCATE(search, string) order following SQL standard,
   * but JPA CriteriaBuilder.locate() expects (string, search) order.
   * We swap the arguments here to match JPA's expected order.
   */
  private static Expression<?> locate(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<String> search = toStringExpression(args.get(0), root, cb, metamodel);
    Expression<String> str = toStringExpression(args.get(1), root, cb, metamodel);
    return cb.locate(str, search);  // JPA order: (string, search)
  }

  // --- Date/Time functions ---

  private static Expression<?> currentDate(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    return cb.currentDate();
  }

  private static Expression<?> currentTime(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    return cb.currentTime();
  }

  private static Expression<?> currentTimestamp(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    return cb.currentTimestamp();
  }

  // --- Conditional functions ---

  private static Expression<?> coalesce(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    if (args.isEmpty()) {
      throw new IllegalArgumentException("COALESCE requires at least 1 argument");
    }
    CriteriaBuilder.Coalesce<Object> coalesce = cb.coalesce();
    for (FunctionArgument arg : args) {
      Expression<Object> expr = castExpression(toExpressionOrLiteral(arg, root, cb, metamodel));
      coalesce.value(expr);
    }
    return coalesce;
  }

  /**
   * NULLIF function implementation.
   *
   * <p>JPA CriteriaBuilder.nullif(Expression, Y) only accepts a literal as second argument.
   * When the second argument is a PropertyExpression (field reference), we cannot use the
   * standard nullif() method.
   *
   * <p>Workaround: We emulate NULLIF(a, b) using CASE WHEN:
   * {@code CASE WHEN a = b THEN NULL ELSE a END}
   *
   * <p>This produces equivalent results for most cases. Note: behavior may differ slightly
   * for NULL comparisons in some databases (NULL = NULL is typically UNKNOWN, not TRUE).
   */
  private static Expression<?> nullif(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<Object> first = castExpression(toExpressionOrLiteral(args.get(0), root, cb, metamodel));
    Object second = args.get(1);

    if (second instanceof PropertyExpression pe) {
      // PropertyExpression requires CASE WHEN workaround (see Javadoc above)
      Expression<Object> secondExpr = castExpression(toExpressionOrLiteral(pe, root, cb, metamodel));
      return cb.<Object>selectCase()
          .when(cb.equal(first, secondExpr), cb.nullLiteral(Object.class))
          .otherwise(first);
    } else {
      return cb.nullif(first, second);
    }
  }

  // --- Aggregate functions ---

  private static Expression<?> count(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<?> arg = toExpressionOrLiteral(args.get(0), root, cb, metamodel);
    return cb.count(arg);
  }

  private static Expression<?> sum(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<Number> arg = castExpression(toExpressionOrLiteral(args.get(0), root, cb, metamodel));
    return cb.sum(arg);
  }

  private static Expression<?> avg(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<Number> arg = castExpression(toExpressionOrLiteral(args.get(0), root, cb, metamodel));
    return cb.avg(arg);
  }

  private static Expression<?> min(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<Number> arg = castExpression(toExpressionOrLiteral(args.get(0), root, cb, metamodel));
    return cb.min(arg);
  }

  private static Expression<?> max(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    Expression<Number> arg = castExpression(toExpressionOrLiteral(args.get(0), root, cb, metamodel));
    return cb.max(arg);
  }

  // --- Helper methods ---

  /**
   * Helper to cast Expression<?> to a specific type, centralizing unchecked cast warnings.
   * This is necessary because JPA CriteriaBuilder methods require typed Expressions,
   * but our generic API works with Expression<?>.
   */
  @SuppressWarnings("unchecked")
  private static <T> Expression<T> castExpression(Expression<?> expr) {
    return (Expression<T>) expr;
  }

  private static Expression<?> toExpressionOrLiteral(FunctionArgument arg, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    if (arg instanceof PropertyExpression pe) {
      return toExpression(pe, root, cb, metamodel);
    } else if (arg instanceof Literal lit) {
      return cb.literal(lit.getValue());
    }
    throw new IllegalArgumentException("Unsupported argument type: " + arg.getClass());
  }

  private static Expression<String> toStringExpression(FunctionArgument arg, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    if (arg instanceof PropertyExpression pe) {
      return castExpression(toExpression(pe, root, cb, metamodel));
    } else if (arg instanceof Literal lit && lit.getValue() instanceof String s) {
      return cb.literal(s);
    }
    throw new IllegalArgumentException("Expected string expression but got: " + arg.getClass());
  }

  private static Expression<Integer> toIntExpression(FunctionArgument arg, Root<?> root, CriteriaBuilder cb, Metamodel metamodel) {
    if (arg instanceof PropertyExpression pe) {
      return castExpression(toExpression(pe, root, cb, metamodel));
    } else if (arg instanceof Literal lit && lit.getValue() instanceof Integer i) {
      return cb.literal(i);
    } else if (arg instanceof Literal lit && lit.getValue() instanceof Number n) {
      return cb.literal(n.intValue());
    }
    throw new IllegalArgumentException("Expected integer expression but got: " + arg.getClass());
  }

  @FunctionalInterface
  interface JpaFunctionExpressionProvider {
    @SuppressWarnings("java:S1452")
    Expression<?> toExpression(List<FunctionArgument> args, Root<?> root, CriteriaBuilder cb, Metamodel metamodel);
  }
}
