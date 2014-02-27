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
import org.apache.olingo.odata4.client.api.edm.xml.ComplexType;
import org.apache.olingo.odata4.client.api.edm.xml.EntityType;
import org.apache.olingo.odata4.client.api.edm.xml.EnumType;
import org.apache.olingo.odata4.client.api.edm.xml.Schema;
import org.apache.olingo.odata4.client.core.op.impl.SchemaDeserializer;

@JsonDeserialize(using = SchemaDeserializer.class)
public abstract class AbstractSchema extends AbstractEdmItem implements Schema {

  private static final long serialVersionUID = -1356392748971378455L;

  private String namespace;

  private String alias;

  @Override
  public String getNamespace() {
    return namespace;
  }

  @Override
  public void setNamespace(final String namespace) {
    this.namespace = namespace;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(final String alias) {
    this.alias = alias;
  }

  @Override
  public EnumType getEnumType(final String name) {
    return getOneByName(name, getEnumTypes());
  }

  @Override
  public ComplexType getComplexType(final String name) {
    return getOneByName(name, getComplexTypes());
  }

  @Override
  public EntityType getEntityType(final String name) {
    return getOneByName(name, getEntityTypes());
  }
}
