/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.jpa.core.model;

import javax.persistence.metamodel.Metamodel;

import org.apache.olingo.jpa.api.ODataJPAContext;
import org.apache.olingo.jpa.api.exception.ODataJPAException;
import org.apache.olingo.jpa.api.extension.JPACsdlMetaModelExtension;
import org.apache.olingo.jpa.api.model.JPACsdlBuilder;
import org.apache.olingo.jpa.api.model.JPACsdlMetaModelAccessor;
import org.apache.olingo.jpa.api.model.JPACsdlMetaModelContext;
import org.apache.olingo.jpa.api.model.JPACsdlSchemaAccessor;

/**
 * A meta model that holds JPA Model element definition and its corresponding CSDL definition
 */
public class JPACsdlMetaModel implements JPACsdlMetaModelContext {
  private String pUnitName = null;
  private Metamodel jpaMetaModel = null;
  private JPACsdlMetaModelExtension extension = null;

  private JPACsdlMetaModel() {}

  public static JPACsdlBuilder newJPACsdlMetaModelBuilder(ODataJPAContext odataJPAContext) {
    return new JPACsdlMetaModel.JPACsdlMetaModelBuilder(odataJPAContext);
  }

  @Override
  public String getPersistenceUnit() {
    return pUnitName;
  }

  @Override
  public Metamodel getJPAMetaModel() {
    return jpaMetaModel;
  }

  @Override
  public JPACsdlMetaModelExtension getJPACsdlMetaModelExtension() {
    return extension;
  }

  protected void setPersistenceUnit(String pUnitName) {
    this.pUnitName = pUnitName;
  }

  protected void setJPAMetaModel(Metamodel metaModel) {
    this.jpaMetaModel = metaModel;
  }

  public void setJPACsdlMetaModelExtension(JPACsdlMetaModelExtension jpaCsdlMetaModelExtension) {
    extension = jpaCsdlMetaModelExtension;
  }

  private static class JPACsdlMetaModelBuilder implements JPACsdlBuilder {

    private ODataJPAContext odataJPAContext = null;

    public JPACsdlMetaModelBuilder(ODataJPAContext odataJPAContext) {
      this.odataJPAContext = odataJPAContext;
    }

    @Override
    public JPACsdlMetaModelAccessor build() throws ODataJPAException {
      JPACsdlMetaModel jpaCsdlMetaModel = new JPACsdlMetaModel();
      jpaCsdlMetaModel.setJPAMetaModel(odataJPAContext.getEntityManagerFactory().getMetamodel());
      jpaCsdlMetaModel.setPersistenceUnit(odataJPAContext.getPersistenceUnitName());
      jpaCsdlMetaModel.setJPACsdlMetaModelExtension(odataJPAContext.getJPACsdlMetaModelExtension());

      JPACsdlSchemaAccessor jpaCsdlSchemaAccessor = new JPACsdlSchema(jpaCsdlMetaModel);
      return jpaCsdlSchemaAccessor.getJPACsdlBuilder().build();
    }
  }

}
