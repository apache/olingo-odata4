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
package org.apache.olingo.odata4.client.core.edm.xml;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.olingo.odata4.client.api.edm.xml.EntitySet;
import org.apache.olingo.odata4.client.core.op.impl.EntitySetDeserializer;

@JsonDeserialize(using = EntitySetDeserializer.class)
public abstract class AbstractEntitySet extends AbstractEdmItem implements EntitySet {

  private static final long serialVersionUID = -6577263439520376420L;

  private String name;

  private String entityType;

  @Override
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(final String entityType) {
    this.entityType = entityType;
  }
}
