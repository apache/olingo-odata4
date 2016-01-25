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
package org.apache.olingo.client.core.edm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.edm.xml.Edmx;
import org.apache.olingo.client.api.edm.xml.Reference;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmItem;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

/**
 * Entry point for access information about EDM metadata.
 */
public class ClientCsdlXMLMetadata extends CsdlAbstractEdmItem implements Serializable, XMLMetadata {

  private static final long serialVersionUID = 6025723060298454901L;
  protected final Edmx edmx;

  public ClientCsdlXMLMetadata(final Edmx edmx) {
    this.edmx = edmx;
  }

  @Override
  public List<CsdlSchema> getSchemas() {
    return this.edmx.getDataServices().getSchemas();
  }

  @Override
  public CsdlSchema getSchema(final int index) {
    return getSchemas().get(index);
  }

  @Override
  public CsdlSchema getSchema(final String key) {
    return getSchemaByNsOrAlias().get(key);
  }

  @Override
  public Map<String, CsdlSchema> getSchemaByNsOrAlias() {
    final Map<String, CsdlSchema> schemaByNsOrAlias = new HashMap<String, CsdlSchema>();
    for (CsdlSchema schema : getSchemas()) {
      schemaByNsOrAlias.put(schema.getNamespace(), schema);
      if (StringUtils.isNotBlank(schema.getAlias())) {
        schemaByNsOrAlias.put(schema.getAlias(), schema);
      }
    }
    return schemaByNsOrAlias;
  }

  @Override
  public List<Reference> getReferences() {
    return this.edmx.getReferences();
  }
}
