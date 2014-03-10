/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.edm.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.edm.xml.Edmx;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;

/**
 * Entry point for access information about EDM metadata.
 */
public abstract class AbstractXMLMetadata extends AbstractEdmItem implements XMLMetadata {

  private static final long serialVersionUID = -1214173426671503187L;

  protected final Edmx edmx;

  protected final Map<String, Schema> schemaByNsOrAlias;

  public AbstractXMLMetadata(final Edmx edmx) {
    this.edmx = edmx;

    this.schemaByNsOrAlias = new HashMap<String, Schema>();
    for (Schema schema : edmx.getDataServices().getSchemas()) {
      this.schemaByNsOrAlias.put(schema.getNamespace(), schema);
      if (StringUtils.isNotBlank(schema.getAlias())) {
        this.schemaByNsOrAlias.put(schema.getAlias(), schema);
      }
    }
  }

  /**
   * Checks whether the given key is a valid namespace or alias in the EdM metadata document.
   *
   * @param key namespace or alias
   * @return true if key is valid namespace or alias
   */
  @Override
  public boolean isNsOrAlias(final String key) {
    return this.schemaByNsOrAlias.keySet().contains(key);
  }

  /**
   * Returns the Schema at the specified position in the EdM metadata document.
   *
   * @param index index of the Schema to return
   * @return the Schema at the specified position in the EdM metadata document
   */
  @Override
  public Schema getSchema(final int index) {
    return this.edmx.getDataServices().getSchemas().get(index);
  }

  /**
   * Returns the Schema with the specified key (namespace or alias) in the EdM metadata document.
   *
   * @param key namespace or alias
   * @return the Schema with the specified key in the EdM metadata document
   */
  @Override
  public Schema getSchema(final String key) {
    return this.schemaByNsOrAlias.get(key);
  }

  /**
   * Returns all Schema objects defined in the EdM metadata document.
   *
   * @return all Schema objects defined in the EdM metadata document
   */
  @Override
  public List<? extends Schema> getSchemas() {
    return this.edmx.getDataServices().getSchemas();
  }

}
