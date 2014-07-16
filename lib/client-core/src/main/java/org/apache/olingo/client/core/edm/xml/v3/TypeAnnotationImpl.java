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
package org.apache.olingo.client.core.edm.xml.v3;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.olingo.client.api.edm.xml.v3.PropertyValue;
import org.apache.olingo.client.api.edm.xml.v3.TypeAnnotation;
import org.apache.olingo.client.core.edm.xml.AbstractEdmItem;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = TypeAnnotationDeserializer.class)
public class TypeAnnotationImpl extends AbstractEdmItem implements TypeAnnotation {

  private static final long serialVersionUID = -7585489230017331877L;

  private String term;

  private String qualifier;

  private List<PropertyValue> propertyValues = new ArrayList<PropertyValue>();

  @Override
  public String getTerm() {
    return term;
  }

  public void setTerm(final String term) {
    this.term = term;
  }

  @Override
  public String getQualifier() {
    return qualifier;
  }

  public void setQualifier(final String qualifier) {
    this.qualifier = qualifier;
  }

  @Override
  public List<PropertyValue> getPropertyValues() {
    return propertyValues;
  }

}
