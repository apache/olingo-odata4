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
package org.apache.olingo.commons.core.edm.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmAnnotationsTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.Annotation;
import org.apache.olingo.commons.api.edm.provider.Annotations;

public class EdmAnnotationsImpl implements EdmAnnotations {

  private final Edm edm;
  private final EdmSchema schema;
  private final Annotations annotationGroup;
  private EdmAnnotationsTarget target;
  private List<EdmAnnotation> annotations;

  public EdmAnnotationsImpl(final Edm edm, final EdmSchema schema, final Annotations annotationGroup) {
    this.edm = edm;
    this.schema = schema;
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

      final EdmEntityContainer baseEntityContainer = schema.getEntityContainer(base);
      
      target = baseEntityContainer == null? null: baseEntityContainer.getActionImport(path);
      if (target == null) {
        target = getTarget(edm.getComplexType(base), path);
        if (target == null) {
          target = baseEntityContainer;
          if (target == null) {
            target = baseEntityContainer == null? null: baseEntityContainer.getEntitySet(path);
            if (target == null) {
              target = getTarget(edm.getEntityType(base), path);
              if (target == null) {
                target = getTarget(edm.getEnumType(base), path);
                if (target == null) {
                  target = baseEntityContainer == null? null: baseEntityContainer.getFunctionImport(path);
                  if (target == null) {
                    target = baseEntityContainer == null? null: baseEntityContainer.getSingleton(path);
                    if (target == null) {
                      target = edm.getTerm(base);
                      if (target == null) {
                        target = edm.getTypeDefinition(base);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
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
      annotations = new ArrayList<EdmAnnotation>();
      for (Annotation annotation : annotationGroup.getAnnotations()) {
        annotations.add(new EdmAnnotationImpl(edm, annotation));
      }
    }
    return annotations;
  }

}
