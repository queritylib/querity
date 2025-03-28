package io.github.queritylib.querity.parser;

import io.github.queritylib.querity.api.Query;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuerityParser {
  public static Query parseQuery(String query) {
    CharStream input = CharStreams.fromString(query);
    QueryLexer lexer = new QueryLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    QueryParser parser = new QueryParser(tokens);

    ParseTree tree = parser.query();
    QueryVisitor visitor = new QueryVisitor();
    return (Query) visitor.visit(tree);
  }
}
