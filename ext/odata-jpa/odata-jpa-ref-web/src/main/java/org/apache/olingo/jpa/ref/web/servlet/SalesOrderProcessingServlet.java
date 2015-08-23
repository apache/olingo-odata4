package org.apache.olingo.jpa.ref.web.servlet;

import org.apache.olingo.jpa.api.ODataJPAContext;
import org.apache.olingo.jpa.api.ODataJPAService;
import org.apache.olingo.jpa.api.ODataJPAServlet;
import org.apache.olingo.jpa.api.factory.ODataJPAFactory;

public class SalesOrderProcessingServlet extends ODataJPAServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private ODataJPAService odataJPAService = null;
  private ODataJPAContext odataJPAContext = null;

  @Override
  protected void initializeODataJPAContext(ODataJPAContext odataJPAContext) {
    odataJPAContext.setPersistenceUnitName();
  }

}
