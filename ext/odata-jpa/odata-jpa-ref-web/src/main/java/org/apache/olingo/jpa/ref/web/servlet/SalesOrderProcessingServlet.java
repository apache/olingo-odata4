package org.apache.olingo.jpa.ref.web.servlet;

import org.apache.olingo.jpa.api.ODataJPAContext;
import org.apache.olingo.jpa.api.ODataJPAServlet;
import org.apache.olingo.jpa.ref.factory.JPAEntityManagerFactory;

public class SalesOrderProcessingServlet extends ODataJPAServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  protected void initializeODataJPAContext(ODataJPAContext odataJPAContext) {
    odataJPAContext.setPersistenceUnitName(getPersistenceUnitName());
    odataJPAContext.setEntityManagerFactory(JPAEntityManagerFactory.getEntityManagerFactory(getPersistenceUnitName()));
  }

}
