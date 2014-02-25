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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.olingo.odata4.client.api.edm.v4.Include;
import org.apache.olingo.odata4.client.core.edm.AbstractEdmItem;

public class IncludeImpl extends AbstractEdmItem implements Include {

  private static final long serialVersionUID = -5600031479702563436L;

  @JsonProperty(value = "Namespace", required = true)
  private String namespace;

  @JsonProperty(value = "Alias")
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

}
