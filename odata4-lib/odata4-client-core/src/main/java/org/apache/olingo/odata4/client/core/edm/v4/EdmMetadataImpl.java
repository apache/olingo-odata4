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
package org.apache.olingo.odata4.client.core.edm.v4;

import java.io.InputStream;
import java.util.List;
import org.apache.olingo.odata4.client.api.ODataClient;
import org.apache.olingo.odata4.client.core.edm.AbstractEdmMetadata;
import org.apache.olingo.odata4.client.core.edm.xml.v4.EdmxImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v4.ReferenceImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v4.SchemaImpl;

public class EdmMetadataImpl extends AbstractEdmMetadata {

  private static final long serialVersionUID = -7765327879691528010L;

  public EdmMetadataImpl(final ODataClient client, final InputStream inputStream) {
    super(client, inputStream);
  }

  @Override
  public SchemaImpl getSchema(final int index) {
    return (SchemaImpl) super.getSchema(index);
  }

  @Override
  public SchemaImpl getSchema(final String key) {
    return (SchemaImpl) super.getSchema(key);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<SchemaImpl> getSchemas() {
    return (List<SchemaImpl>) super.getSchemas();
  }

  public List<ReferenceImpl> getReferences() {
    return ((EdmxImpl) this.edmx).getReferences();
  }
}
