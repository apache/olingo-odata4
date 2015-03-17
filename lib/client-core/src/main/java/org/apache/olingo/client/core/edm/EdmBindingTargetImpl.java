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
package org.apache.olingo.client.core.edm;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.BindingTarget;
import org.apache.olingo.commons.api.edm.provider.NavigationPropertyBinding;
import org.apache.olingo.commons.core.edm.AbstractEdmBindingTarget;
import org.apache.olingo.commons.core.edm.EdmAnnotationHelper;
import org.apache.olingo.commons.core.edm.EdmNavigationPropertyBindingImpl;

public abstract class EdmBindingTargetImpl extends AbstractEdmBindingTarget {

  private final BindingTarget target;

  private final EdmAnnotationHelper helper;

  private List<EdmNavigationPropertyBinding> navigationPropertyBindings;

  public EdmBindingTargetImpl(final Edm edm, final EdmEntityContainer container,
          final String name, final FullQualifiedName type, final BindingTarget target) {

    super(edm, container, name, type);
    this.target = target;
    this.helper = new EdmAnnotationHelperImpl(edm, target);
  }

  @Override
  public List<EdmNavigationPropertyBinding> getNavigationPropertyBindings() {
    if (navigationPropertyBindings == null) {
      List<? extends NavigationPropertyBinding> providerBindings = target.getNavigationPropertyBindings();
      navigationPropertyBindings = new ArrayList<EdmNavigationPropertyBinding>();
      if (providerBindings != null) {
        for (NavigationPropertyBinding binding : providerBindings) {
          navigationPropertyBindings.add(new EdmNavigationPropertyBindingImpl(binding.getPath(), binding.getTarget()));
        }
      }
    }
    return navigationPropertyBindings;
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return helper.getAnnotation(term);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return helper.getAnnotations();
  }
}
