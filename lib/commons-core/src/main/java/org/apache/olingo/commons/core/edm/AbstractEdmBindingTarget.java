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
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlBindingTarget;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;

public abstract class AbstractEdmBindingTarget extends AbstractEdmNamed implements EdmBindingTarget {

  private final CsdlBindingTarget target;
  private final EdmEntityContainer container;

  private List<EdmNavigationPropertyBinding> navigationPropertyBindings;

  public AbstractEdmBindingTarget(final Edm edm, final EdmEntityContainer container, final CsdlBindingTarget target) {
    super(edm, target.getName(), target);
    this.container = container;
    this.target = target;
  }

  @Override
  public List<EdmNavigationPropertyBinding> getNavigationPropertyBindings() {
    if (navigationPropertyBindings == null) {
      List<CsdlNavigationPropertyBinding> providerBindings = target.getNavigationPropertyBindings();
      final List<EdmNavigationPropertyBinding> navigationPropertyBindingsLocal =
          new ArrayList<EdmNavigationPropertyBinding>();
      if (providerBindings != null) {
        for (CsdlNavigationPropertyBinding binding : providerBindings) {
          navigationPropertyBindingsLocal.add(new EdmNavigationPropertyBindingImpl(binding.getPath(),
              binding.getTarget()));
        }
        navigationPropertyBindings = Collections.unmodifiableList(navigationPropertyBindingsLocal);
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
  public EdmEntityType getEntityTypeWithAnnotations() {
    final EdmEntityType entityType = ((AbstractEdm)edm).
        getEntityTypeWithAnnotations(target.getTypeFQN(), true);
    if (entityType == null) {
      throw new EdmException("Can´t find entity type: " + target.getTypeFQN() + " for entity set or singleton: "
          + getName());
    }
    return entityType;
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
      if (binding.getPath() == null || binding.getTarget() == null) {
        throw new EdmException("Path or Target in navigation property binding must not be null!");
      }
      if (path.equals(binding.getPath())) {
        final Target edmTarget = new Target(binding.getTarget(), container);

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
            throw new EdmException("Cannot find Singleton " + edmTarget.getTargetName(), e);
          }
        } finally {
          found = bindingTarget != null;
        }
      }
    }

    return bindingTarget;
  }

  @Override
  public String getTitle() {
    return target.getTitle();
  }

  @Override
  public EdmMapping getMapping() {
    return target.getMapping();
  }
}
