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
package org.apache.olingo.client.core.edm.xml.v4;

import java.util.List;

import org.apache.olingo.client.api.edm.xml.v4.Edmx;
import org.apache.olingo.client.api.edm.xml.v4.Reference;
import org.apache.olingo.client.api.edm.xml.v4.Schema;
import org.apache.olingo.client.api.edm.xml.v4.XMLMetadata;
import org.apache.olingo.client.core.edm.xml.AbstractXMLMetadata;

public class XMLMetadataImpl extends AbstractXMLMetadata implements XMLMetadata {

  private static final long serialVersionUID = -7765327879691528010L;

  public XMLMetadataImpl(final EdmxImpl edmx) {
    super(edmx);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Schema> getSchemas() {
    return (List<Schema>) super.getSchemas();
  }

  @Override
  public Schema getSchema(final int index) {
    return (Schema) super.getSchema(index);
  }

  @Override
  public Schema getSchema(final String key) {
    return (Schema) super.getSchema(key);
  }

  public List<Reference> getReferences() {
    return ((Edmx) this.edmx).getReferences();
  }
}
