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
package org.apache.olingo.commons.core.edm;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;

public class EdmAnnotationsImpl extends AbstractEdmAnnotatable implements EdmAnnotations {

  private final CsdlAnnotations annotationGroup;

  public EdmAnnotationsImpl(final Edm edm, final CsdlAnnotations annotationGroup) {
    super(edm, annotationGroup);
    this.annotationGroup = annotationGroup;
  }

  @Override
  public String getQualifier() {
    return annotationGroup.getQualifier();
  }

  @Override
  public String getTargetPath() {
    return annotationGroup.getTarget();
  }
}
