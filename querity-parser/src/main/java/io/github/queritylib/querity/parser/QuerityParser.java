package io.github.queritylib.querity.parser;

import io.github.queritylib.querity.api.QueryDefinition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuerityParser {
  /**
   * Parses a query string and returns the appropriate query type.
   * 
   * @param query the query string to parse
   * @return a {@link io.github.queritylib.querity.api.Query} for simple queries,
   *         or an {@link io.github.queritylib.querity.api.AdvancedQuery} if the query
   *         contains SELECT, GROUP BY, or HAVING clauses
   */
  public static QueryDefinition parseQuery(String query) {
    CharStream input = CharStreams.fromString(query);
    QueryLexer lexer = new QueryLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    QueryParser parser = new QueryParser(tokens);

    ParseTree tree = parser.query();
    QueryVisitor visitor = new QueryVisitor();
    return (QueryDefinition) visitor.visit(tree);
  }
}
