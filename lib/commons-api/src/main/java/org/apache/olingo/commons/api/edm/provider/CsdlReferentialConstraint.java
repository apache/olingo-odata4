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
package org.apache.olingo.commons.api.edm.provider;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Csdl referential constraint.
 */
public class CsdlReferentialConstraint extends CsdlAbstractEdmItem implements CsdlAnnotatable {

  private String property;

  private String referencedProperty;

  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  /**
   * Gets property.
   *
   * @return the property
   */
  public String getProperty() {
    return property;
  }

  /**
   * Sets property.
   *
   * @param property the property
   * @return the property
   */
  public CsdlReferentialConstraint setProperty(final String property) {
    this.property = property;
    return this;
  }

  /**
   * Gets referenced property.
   *
   * @return the referenced property
   */
  public String getReferencedProperty() {
    return referencedProperty;
  }

  /**
   * Sets referenced property.
   *
   * @param referencedProperty the referenced property
   * @return the referenced property
   */
  public CsdlReferentialConstraint setReferencedProperty(final String referencedProperty) {
    this.referencedProperty = referencedProperty;
    return this;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }
  
  /**
   * Sets a list of annotations
   * @param annotations list of annotations
   * @return this instance
   */
  public CsdlReferentialConstraint setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
}
