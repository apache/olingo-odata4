package org.apache.olingo.commons.api.edm;

public class EdmException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public EdmException(final Exception e) {
    super(e);
  }

  public EdmException(final String msg) {
    super(msg);
  }

}
