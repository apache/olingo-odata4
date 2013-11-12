package org.apache.olingo.producer.core.uri;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.apache.olingo.producer.core.uri.antlr.UriLexer;

public class ErrorHandler<T> extends BaseErrorListener {
  @Override
  public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
      final int line, final int charPositionInLine, final String msg, final RecognitionException e) {
    System.err.println("-");
    // check also http://stackoverflow.com/questions/14747952/ll-exact-ambig-detection-interpetation
    List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
    Collections.reverse(stack);
    System.err.println("rule stack: " + stack);
    if (e != null && e.getOffendingToken() != null) {

      // String lexerTokenName =TestSuiteLexer.tokenNames[e.getOffendingToken().getType()];
      String lexerTokenName = "";
      try {
        lexerTokenName = UriLexer.tokenNames[e.getOffendingToken().getType()];
      } catch (ArrayIndexOutOfBoundsException es) {
        lexerTokenName = "token error";
      }
      System.err.println("line " + line + ":" + charPositionInLine + " at " +
          offendingSymbol + "/" + lexerTokenName + ": " + msg);
    } else {
      System.err.println("line " + line + ":" + charPositionInLine + " at " + offendingSymbol + ": " + msg);
    }
  }
}
