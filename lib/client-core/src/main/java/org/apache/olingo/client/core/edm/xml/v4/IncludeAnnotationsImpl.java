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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.olingo.client.api.edm.xml.v4.IncludeAnnotations;
import org.apache.olingo.client.core.edm.xml.AbstractEdmItem;

public class IncludeAnnotationsImpl extends AbstractEdmItem implements IncludeAnnotations {

  private static final long serialVersionUID = -5600031479702563436L;

  @JsonProperty(value = "TermNamespace", required = true)
  private String termNamespace;

  @JsonProperty(value = "Qualifier")
  private String qualifier;

  @JsonProperty(value = "TargetNamespace")
  private String targetNamespace;

  @Override
  public String getTermNamespace() {
    return termNamespace;
  }

  public void setTermNamespace(final String termNamespace) {
    this.termNamespace = termNamespace;
  }

  @Override
  public String getQualifier() {
    return qualifier;
  }

  public void setQualifier(final String qualifier) {
    this.qualifier = qualifier;
  }

  @Override
  public String getTargetNamespace() {
    return targetNamespace;
  }

  public void setTargeyNamespace(final String targeyNamespace) {
    this.targetNamespace = targeyNamespace;
  }

}
