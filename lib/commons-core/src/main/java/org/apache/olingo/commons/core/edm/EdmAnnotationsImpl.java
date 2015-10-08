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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmAnnotationsTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;

public class EdmAnnotationsImpl implements EdmAnnotations {

  private final Edm edm;
  private final CsdlAnnotations annotationGroup;
  private EdmAnnotationsTarget target;
  private List<EdmAnnotation> annotations;

  public EdmAnnotationsImpl(final Edm edm, final CsdlAnnotations annotationGroup) {
    this.edm = edm;
    this.annotationGroup = annotationGroup;
  }

  private EdmAnnotationsTarget getTarget(final EdmStructuredType structured, final String path) {
    EdmAnnotationsTarget _target = null;
    if (structured != null) {
      _target = path == null
          ? structured
              : structured.getStructuralProperty(path);
      if (_target == null) {
        _target = structured.getNavigationProperty(path);
      }
    }
    return _target;
  }

  private EdmAnnotationsTarget getTarget(final EdmEnumType enumType, final String path) {
    EdmAnnotationsTarget _target = null;
    if (enumType != null) {
      _target = path == null
          ? enumType
              : enumType.getMember(path);
    }
    return _target;
  }

  @Override
  public EdmAnnotationsTarget getTarget() {
    if (target == null) {
      final String[] splitted = StringUtils.split(annotationGroup.getTarget(), '/');
      final FullQualifiedName base = new FullQualifiedName(splitted[0]);
      final String path = splitted.length > 1 ? splitted[1] : null;

      final EdmEntityContainer baseEntityContainer = edm.getEntityContainer();

      EdmAnnotationsTarget localTarget = baseEntityContainer == null ? null
          : baseEntityContainer.getActionImport(path);
      if (localTarget == null) {
        localTarget = getTarget(edm.getComplexType(base), path);
        if (localTarget == null) {
          if (baseEntityContainer != null && baseEntityContainer.getFullQualifiedName().equals(base)) {
            localTarget = baseEntityContainer;
          }
          if (localTarget == null) {
            localTarget = baseEntityContainer == null ? null : baseEntityContainer.getEntitySet(path);
            if (localTarget == null) {
              localTarget = getTarget(edm.getEntityType(base), path);
              if (localTarget == null) {
                localTarget = getTarget(edm.getEnumType(base), path);
                if (localTarget == null) {
                  localTarget = baseEntityContainer == null ? null : baseEntityContainer.getFunctionImport(path);
                  if (localTarget == null) {
                    localTarget = baseEntityContainer == null ? null : baseEntityContainer.getSingleton(path);
                    if (localTarget == null) {
                      localTarget = edm.getTerm(base);
                      if (localTarget == null) {
                        localTarget = edm.getTypeDefinition(base);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      target = localTarget;
    }

    return target;
  }

  @Override
  public String getQualifier() {
    return annotationGroup.getQualifier();
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    EdmAnnotation result = null;
    for (EdmAnnotation annotation : getAnnotations()) {
      if (term.getFullQualifiedName().equals(annotation.getTerm().getFullQualifiedName())) {
        result = annotation;
      }
    }
    return result;
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    if (annotations == null) {
      List<EdmAnnotation> annotationsLocal = new ArrayList<EdmAnnotation>();
      for (CsdlAnnotation annotation : annotationGroup.getAnnotations()) {
        annotationsLocal.add(new EdmAnnotationImpl(edm, annotation));
      }

      annotations = Collections.unmodifiableList(annotationsLocal);
    }
    return annotations;
  }

  @Override
  public String getTargetPath() {
    return annotationGroup.getTarget();
  }

}
