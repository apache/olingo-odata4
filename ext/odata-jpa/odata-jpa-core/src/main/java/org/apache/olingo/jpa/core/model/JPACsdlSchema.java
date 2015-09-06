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

import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.jpa.api.exception.ODataJPAException;
import org.apache.olingo.jpa.api.model.JPACsdlBuilder;
import org.apache.olingo.jpa.api.model.JPACsdlMetaModelAccessor;
import org.apache.olingo.jpa.api.model.JPACsdlMetaModelContext;
import org.apache.olingo.jpa.api.model.JPACsdlSchemaAccessor;

public class JPACsdlSchema implements JPACsdlSchemaAccessor {
  private CsdlSchema csdlSchema;
  private JPACsdlBuilder builder = null;
  private JPACsdlMetaModelContext context = null;

  public JPACsdlSchema(JPACsdlMetaModelContext context) {
    this.context = context;
  }

  @Override
  public CsdlSchema getCsdlSchema() {
    return this.csdlSchema;
  }

  @Override
  public JPACsdlBuilder getJPACsdlBuilder() {
    if (builder == null) {
      builder = new JPACsdlSchemaBuilder();
    }
    return builder;
  }

  private class JPACsdlSchemaBuilder implements JPACsdlBuilder {

    @Override
    public JPACsdlMetaModelAccessor build() throws ODataJPAException {
      csdlSchema = new CsdlSchema();
      csdlSchema.setNamespace(JPACsdlNameBuilder.buildSchemaNamespace(context));
      return JPACsdlSchema.this;
    }

  }
}
