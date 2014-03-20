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
package org.apache.olingo.server.core.edm.provider;

import java.util.Iterator;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.Target;
import org.apache.olingo.commons.core.edm.AbstractEdmBindingTarget;
import org.apache.olingo.server.api.edm.provider.BindingTarget;
import org.apache.olingo.server.api.edm.provider.NavigationPropertyBinding;

public abstract class EdmBindingTargetImpl extends AbstractEdmBindingTarget {

  private final BindingTarget target;

  public EdmBindingTargetImpl(final Edm edm, final EdmEntityContainer container, final BindingTarget target) {
    super(edm, container, target.getName(), target.getType());
    this.target = target;
  }

  @Override
  public EdmBindingTarget getRelatedBindingTarget(final String path) {
    EdmBindingTarget bindingTarget = null;

    final List<NavigationPropertyBinding> navigationPropertyBindings = target.getNavigationPropertyBindings();
    if (navigationPropertyBindings != null) {
      boolean found = false;
      for (final Iterator<NavigationPropertyBinding> itor = navigationPropertyBindings.iterator(); itor.hasNext()
          && !found;) {

        final NavigationPropertyBinding binding = itor.next();
        if (binding.getPath().equals(path)) {
          final Target providerTarget = binding.getTarget();
          final EdmEntityContainer entityContainer = edm.getEntityContainer(providerTarget.getEntityContainer());
          if (entityContainer == null) {
            throw new EdmException("Cant find entity container with name: " + providerTarget.getEntityContainer());
          }
          final String targetName = providerTarget.getTargetName();
          bindingTarget = entityContainer.getEntitySet(targetName);
          if (bindingTarget == null) {
            bindingTarget = entityContainer.getSingleton(targetName);
            if (bindingTarget == null) {
              throw new EdmException("Cant find target with name: " + targetName);
            }

            found = true;
          } else {
            found = true;
          }
        }
      }
    }

    return bindingTarget;
  }
}
