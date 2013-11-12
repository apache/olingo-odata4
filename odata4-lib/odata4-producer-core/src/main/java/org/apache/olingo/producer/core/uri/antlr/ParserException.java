package org.apache.olingo.producer.core.uri.antlr;

public class ParserException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ParserException() {
    super();
  }

  public ParserException(final String msg) {
    super(msg);
  }

  public ParserException(final String msg, final Throwable e) {
    super(msg, e);
  }

  public ParserException(final Throwable e) {
    super(e);
  }

}
