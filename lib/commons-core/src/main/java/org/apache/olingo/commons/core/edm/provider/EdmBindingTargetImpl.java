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
package org.apache.olingo.commons.core.edm.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.Target;
import org.apache.olingo.commons.api.edm.provider.BindingTarget;
import org.apache.olingo.commons.api.edm.provider.NavigationPropertyBinding;

public abstract class EdmBindingTargetImpl extends EdmNamedImpl implements EdmBindingTarget {

  private final BindingTarget target;
  private final EdmAnnotationHelperImpl helper;
  private final EdmEntityContainer container;

  private List<EdmNavigationPropertyBinding> navigationPropertyBindings;

  public EdmBindingTargetImpl(final Edm edm, final EdmEntityContainer container, final BindingTarget target) {
    super(edm, target.getName());
    this.container = container;
    this.target = target;
    this.helper = new EdmAnnotationHelperImpl(edm, target);
  }

  @Override
  public List<EdmNavigationPropertyBinding> getNavigationPropertyBindings() {
    if (navigationPropertyBindings == null) {
      List<NavigationPropertyBinding> providerBindings = target.getNavigationPropertyBindings();
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
  public EdmEntityContainer getEntityContainer() {
    return container;
  }

  @Override
  public EdmEntityType getEntityType() {
    final EdmEntityType entityType = edm.getEntityType(target.getTypeFQN());
    if (entityType == null) {
      throw new EdmException("Can´t find entity type: " + target.getTypeFQN() + " for entity set or singleton: "
          + getName());
    }
    return entityType;
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return container.getFullQualifiedName();
  }

  @Override
  public String getAnnotationsTargetPath() {
    return getName();
  }

  @Override
  public EdmBindingTarget getRelatedBindingTarget(final String path) {
    if (path == null) {
      return null;
    }
    EdmBindingTarget bindingTarget = null;
    boolean found = false;
    for (final Iterator<EdmNavigationPropertyBinding> itor = getNavigationPropertyBindings().iterator(); itor.hasNext()
        && !found;) {

      final EdmNavigationPropertyBinding binding = itor.next();
      if (path.startsWith(binding.getPath())) {
        final Target edmTarget = new Target.Builder(binding.getTarget(), container).build();

        final EdmEntityContainer entityContainer = edm.getEntityContainer(edmTarget.getEntityContainer());
        if (entityContainer == null) {
          throw new EdmException("Cannot find entity container with name: " + edmTarget.getEntityContainer());
        }
        try {
          bindingTarget = entityContainer.getEntitySet(edmTarget.getTargetName());

          if (bindingTarget == null) {
            throw new EdmException("Cannot find EntitySet " + edmTarget.getTargetName());
          }
        } catch (EdmException e) {
          // try with singletons ...
          bindingTarget = entityContainer.getSingleton(edmTarget.getTargetName());

          if (bindingTarget == null) {
            throw new EdmException("Cannot find Singleton " + edmTarget.getTargetName());
          }
        } finally {
          found = bindingTarget != null;
        }
      }
    }

    return bindingTarget;
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
