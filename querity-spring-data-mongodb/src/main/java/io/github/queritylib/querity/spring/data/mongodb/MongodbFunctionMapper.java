package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.Function;
import io.github.queritylib.querity.api.FunctionArgument;
import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.Literal;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.PropertyReference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Maps Querity functions to MongoDB aggregation expressions.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MongodbFunctionMapper {

  static final Map<Function, MongodbFunctionExpressionProvider> FUNCTION_MAP = new EnumMap<>(Function.class);

  static {
    // Arithmetic functions
    FUNCTION_MAP.put(Function.ABS, MongodbFunctionMapper::abs);
    FUNCTION_MAP.put(Function.SQRT, MongodbFunctionMapper::sqrt);
    FUNCTION_MAP.put(Function.MOD, MongodbFunctionMapper::mod);

    // String functions
    FUNCTION_MAP.put(Function.CONCAT, MongodbFunctionMapper::concat);
    FUNCTION_MAP.put(Function.SUBSTRING, MongodbFunctionMapper::substring);
    FUNCTION_MAP.put(Function.TRIM, MongodbFunctionMapper::trim);
    FUNCTION_MAP.put(Function.LTRIM, MongodbFunctionMapper::ltrim);
    FUNCTION_MAP.put(Function.RTRIM, MongodbFunctionMapper::rtrim);
    FUNCTION_MAP.put(Function.LOWER, MongodbFunctionMapper::lower);
    FUNCTION_MAP.put(Function.UPPER, MongodbFunctionMapper::upper);
    FUNCTION_MAP.put(Function.LENGTH, MongodbFunctionMapper::length);
    FUNCTION_MAP.put(Function.LOCATE, MongodbFunctionMapper::locate);

    // Date/Time functions
    FUNCTION_MAP.put(Function.CURRENT_DATE, MongodbFunctionMapper::currentDate);
    FUNCTION_MAP.put(Function.CURRENT_TIME, MongodbFunctionMapper::currentTime);
    FUNCTION_MAP.put(Function.CURRENT_TIMESTAMP, MongodbFunctionMapper::currentTimestamp);

    // Conditional functions
    FUNCTION_MAP.put(Function.COALESCE, MongodbFunctionMapper::coalesce);
    FUNCTION_MAP.put(Function.NULLIF, MongodbFunctionMapper::nullif);

    // Aggregate functions
    FUNCTION_MAP.put(Function.COUNT, MongodbFunctionMapper::count);
    FUNCTION_MAP.put(Function.SUM, MongodbFunctionMapper::sum);
    FUNCTION_MAP.put(Function.AVG, MongodbFunctionMapper::avg);
    FUNCTION_MAP.put(Function.MIN, MongodbFunctionMapper::min);
    FUNCTION_MAP.put(Function.MAX, MongodbFunctionMapper::max);
  }

  /**
   * Checks if the given function is supported in MongoDB.
   *
   * <p>Note: MongoDB supports all functions in filter expressions via $expr.
   * However, sorting and projections with function expressions are NOT supported
   * and will throw UnsupportedOperationException at runtime.
   *
   * @param function the function to check
   * @return true if the function is supported in filters
   */
  public static boolean isSupported(Function function) {
    return FUNCTION_MAP.containsKey(function);
  }

  /**
   * Convert a PropertyExpression to a MongoDB aggregation expression.
   *
   * @param expr the property expression
   * @return a MongoDB aggregation expression (can be a String field reference or a Document)
   */
  public static Object toExpression(PropertyExpression expr) {
    if (expr instanceof PropertyReference pr) {
      return "$" + mapFieldName(pr.getPropertyName());
    } else if (expr instanceof FunctionCall fc) {
      MongodbFunctionExpressionProvider provider = FUNCTION_MAP.get(fc.getFunction());
      if (provider == null) {
        throw new UnsupportedOperationException(
            "Function " + fc.getFunction() + " is not supported in MongoDB");
      }
      return provider.toExpression(fc.getArguments());
    }
    throw new IllegalArgumentException("Unsupported expression type: " + expr.getClass());
  }

  /**
   * Get the field name for a PropertyExpression (for simple projections/sorts).
   * Throws if the expression is a function (which requires aggregation pipeline).
   */
  public static String getFieldName(PropertyExpression expr) {
    if (expr instanceof PropertyReference pr) {
      return mapFieldName(pr.getPropertyName());
    }
    throw new UnsupportedOperationException(
        "Function expressions in sorts require aggregation pipeline which is not supported. " +
        "Use simple property names for sorting.");
  }

  /**
   * Check if an expression contains functions.
   */
  public static boolean containsFunction(PropertyExpression expr) {
    return expr instanceof FunctionCall;
  }

  private static String mapFieldName(String fieldName) {
    return "id".equals(fieldName) ? "_id" : fieldName;
  }

  // --- Arithmetic functions ---

  private static Object abs(List<FunctionArgument> args) {
    return new Document("$abs", toExpressionOrLiteral(args.get(0)));
  }

  private static Object sqrt(List<FunctionArgument> args) {
    return new Document("$sqrt", toExpressionOrLiteral(args.get(0)));
  }

  private static Object mod(List<FunctionArgument> args) {
    return new Document("$mod", List.of(
        toExpressionOrLiteral(args.get(0)),
        toExpressionOrLiteral(args.get(1))
    ));
  }

  // --- String functions ---

  private static Object concat(List<FunctionArgument> args) {
    List<Object> mongoArgs = args.stream()
        .map(MongodbFunctionMapper::toExpressionOrLiteral)
        .toList();
    return new Document("$concat", mongoArgs);
  }

  private static Object substring(List<FunctionArgument> args) {
    // MongoDB $substr takes: string, start (0-based), length
    // Querity uses 1-based start like SQL, so we subtract 1
    Object str = toExpressionOrLiteral(args.get(0));
    FunctionArgument start = args.get(1);
    FunctionArgument len = args.get(2);

    // Convert 1-based to 0-based
    Object startExpr;
    if (start instanceof Literal lit && lit.getValue() instanceof Number n) {
      startExpr = n.intValue() - 1;
    } else {
      startExpr = new Document("$subtract", List.of(toExpressionOrLiteral(start), 1));
    }

    return new Document("$substr", List.of(str, startExpr, toExpressionOrLiteral(len)));
  }

  private static Object trim(List<FunctionArgument> args) {
    return new Document("$trim", new Document("input", toExpressionOrLiteral(args.get(0))));
  }

  private static Object ltrim(List<FunctionArgument> args) {
    return new Document("$ltrim", new Document("input", toExpressionOrLiteral(args.get(0))));
  }

  private static Object rtrim(List<FunctionArgument> args) {
    return new Document("$rtrim", new Document("input", toExpressionOrLiteral(args.get(0))));
  }

  private static Object lower(List<FunctionArgument> args) {
    return new Document("$toLower", toExpressionOrLiteral(args.get(0)));
  }

  private static Object upper(List<FunctionArgument> args) {
    return new Document("$toUpper", toExpressionOrLiteral(args.get(0)));
  }

  private static Object length(List<FunctionArgument> args) {
    // Handle null values: return null if input is null, otherwise apply $strLenCP
    Object input = toExpressionOrLiteral(args.get(0));
    // Use Arrays.asList to allow null values
    return new Document("$cond", java.util.Arrays.asList(
        new Document("$eq", java.util.Arrays.asList(new Document("$ifNull", java.util.Arrays.asList(input, null)), null)),
        null,
        new Document("$strLenCP", input)
    ));
  }

  private static Object locate(List<FunctionArgument> args) {
    // MongoDB $indexOfCP returns 0-based index, -1 if not found
    // SQL LOCATE returns 1-based index, 0 if not found
    // We need to add 1 to the result (with -1 becoming 0)
    Object search = toExpressionOrLiteral(args.get(0));
    Object str = toExpressionOrLiteral(args.get(1));

    Document indexOf = new Document("$indexOfCP", List.of(str, search));
    // Add 1 to convert from 0-based to 1-based
    return new Document("$add", List.of(indexOf, 1));
  }

  // --- Date/Time functions ---

  private static Object currentDate(List<FunctionArgument> args) {
    // Use $dateTrunc to get just the date part
    return new Document("$dateTrunc", new Document("date", "$$NOW").append("unit", "day"));
  }

  private static Object currentTime(List<FunctionArgument> args) {
    // MongoDB doesn't have a direct current_time, use $$NOW
    return "$$NOW";
  }

  private static Object currentTimestamp(List<FunctionArgument> args) {
    return "$$NOW";
  }

  // --- Conditional functions ---

  private static Object coalesce(List<FunctionArgument> args) {
    // MongoDB $ifNull only takes 2 arguments, so we need to nest them for multiple args
    if (args.size() == 1) {
      return toExpressionOrLiteral(args.get(0));
    }

    // Build nested $ifNull: {$ifNull: [arg1, {$ifNull: [arg2, arg3]}]}
    Object result = toExpressionOrLiteral(args.get(args.size() - 1));
    for (int i = args.size() - 2; i >= 0; i--) {
      result = new Document("$ifNull", List.of(toExpressionOrLiteral(args.get(i)), result));
    }
    return result;
  }

  private static Object nullif(List<FunctionArgument> args) {
    // NULLIF(a, b) returns NULL if a = b, otherwise returns a
    // In MongoDB: {$cond: [{$eq: [a, b]}, null, a]}
    Object first = toExpressionOrLiteral(args.get(0));
    Object second = toExpressionOrLiteral(args.get(1));

    return new Document("$cond", Arrays.asList(
        new Document("$eq", List.of(first, second)),
        null,
        first
    ));
  }

  // --- Aggregate functions ---

  private static Object count(List<FunctionArgument> args) {
    // For aggregation context, $sum with 1 counts documents
    // For projection, we use $size if it's an array, otherwise just return the expression
    return new Document("$sum", 1);
  }

  private static Object sum(List<FunctionArgument> args) {
    return new Document("$sum", toExpressionOrLiteral(args.get(0)));
  }

  private static Object avg(List<FunctionArgument> args) {
    return new Document("$avg", toExpressionOrLiteral(args.get(0)));
  }

  private static Object min(List<FunctionArgument> args) {
    return new Document("$min", toExpressionOrLiteral(args.get(0)));
  }

  private static Object max(List<FunctionArgument> args) {
    return new Document("$max", toExpressionOrLiteral(args.get(0)));
  }

  // --- Helper methods ---

  private static Object toExpressionOrLiteral(FunctionArgument arg) {
    if (arg instanceof PropertyExpression pe) {
      return toExpression(pe);
    } else if (arg instanceof Literal lit) {
      return lit.getValue();
    }
    throw new IllegalArgumentException("Unsupported argument type: " + arg.getClass());
  }

  @FunctionalInterface
  interface MongodbFunctionExpressionProvider {
    Object toExpression(List<FunctionArgument> args);
  }
}
