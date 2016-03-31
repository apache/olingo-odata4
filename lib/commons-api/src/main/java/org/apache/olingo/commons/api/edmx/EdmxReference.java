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
package org.apache.olingo.commons.api.edmx;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.provider.CsdlAnnotatable;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;

/**
 * POJO for Edmx Reference.
 */
public class EdmxReference implements CsdlAnnotatable{

  private final URI uri;
  private final List<EdmxReferenceInclude> edmxIncludes;
  private final List<EdmxReferenceIncludeAnnotation> edmxIncludeAnnotations;
  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();
  /**
   * Create reference with given uri
   *
   * @param uri of reference
   */
  public EdmxReference(final URI uri) {
    this.uri = uri;
    edmxIncludes = new ArrayList<EdmxReferenceInclude>();
    edmxIncludeAnnotations = new ArrayList<EdmxReferenceIncludeAnnotation>();
  }

  /**
   * Get URI for the reference
   * @return uri for the reference
   */
  public URI getUri() {
    return uri;
  }

  /**
   * edmx:Include elements that specify the schemas to include from the target document
   *
   * @return list of {@link EdmxReferenceInclude} in reference or null if none specified
   */
  public List<EdmxReferenceInclude> getIncludes() {
    return Collections.unmodifiableList(edmxIncludes);
  }

  /**
   * Add include element to current list.
   *
   * @param include to be added
   * @return this EdmxReference object
   */
  public EdmxReference addInclude(final EdmxReferenceInclude include) {
    edmxIncludes.add(include);
    return this;
  }

  /**
   * edmx:IncludeAnnotations elements that specify the annotations to include from the target document.
   *
   * @return List of {@link EdmxReferenceIncludeAnnotation} or null if none specified
   */
  public List<EdmxReferenceIncludeAnnotation> getIncludeAnnotations() {
    return Collections.unmodifiableList(edmxIncludeAnnotations);
  }

  /**
   * Add include annotation element to current list.
   *
   * @param includeAnnotation to be added
   * @return this EdmxReference object
   */
  public EdmxReference addIncludeAnnotation(final EdmxReferenceIncludeAnnotation includeAnnotation) {
    edmxIncludeAnnotations.add(includeAnnotation);
    return this;
  }
  
  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }

  /**
   * Sets annotations.
   *
   * @param annotations the annotations
   * @return the annotations
   */
  public EdmxReference setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }  
}
