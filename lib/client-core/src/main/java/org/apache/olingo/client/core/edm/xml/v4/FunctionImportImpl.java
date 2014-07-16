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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.olingo.client.api.edm.xml.v4.Annotation;
import org.apache.olingo.client.api.edm.xml.v4.FunctionImport;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = FunctionImportDeserializer.class)
public class FunctionImportImpl implements FunctionImport {

  private static final long serialVersionUID = 3023813358471000019L;

  private String name;

  private String function;

  private String entitySet;

  private boolean includeInServiceDocument = false;

  private final List<Annotation> annotations = new ArrayList<Annotation>();

  @Override
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String getFunction() {
    return function;
  }

  public void setFunction(final String function) {
    this.function = function;
  }

  @Override
  public String getEntitySet() {
    return entitySet;
  }

  @Override
  public void setEntitySet(final String entitySet) {
    this.entitySet = entitySet;
  }

  @Override
  public boolean isIncludeInServiceDocument() {
    return includeInServiceDocument;
  }

  public void setIncludeInServiceDocument(final boolean includeInServiceDocument) {
    this.includeInServiceDocument = includeInServiceDocument;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

}
