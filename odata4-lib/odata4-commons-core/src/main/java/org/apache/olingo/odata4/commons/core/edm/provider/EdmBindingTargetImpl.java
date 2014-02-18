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
package org.apache.olingo.odata4.commons.core.edm.provider;

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.provider.BindingTarget;
import org.apache.olingo.odata4.commons.api.edm.provider.NavigationPropertyBinding;
import org.apache.olingo.odata4.commons.api.edm.provider.Target;

public abstract class EdmBindingTargetImpl extends EdmNamedImpl implements EdmBindingTarget {

  private BindingTarget target;
  private EdmEntityContainer container;

  public EdmBindingTargetImpl(final EdmProviderImpl edm, final EdmEntityContainer container,
      final BindingTarget target) {
    super(edm, target.getName());
    this.container = container;
    this.target = target;
  }

  @Override
  public EdmBindingTarget getRelatedBindingTarget(final String path) {
    EdmBindingTarget bindingTarget = null;
    List<NavigationPropertyBinding> navigationPropertyBindings = target.getNavigationPropertyBindings();
    if (navigationPropertyBindings != null) {
      for (NavigationPropertyBinding binding : navigationPropertyBindings) {
        if (binding.getPath().equals(path)) {
          Target providerTarget = binding.getTarget();
          EdmEntityContainer entityContainer = edm.getEntityContainer(providerTarget.getEntityContainer());
          if (entityContainer == null) {
            throw new EdmException("Cant find entity container with name: " + providerTarget.getEntityContainer());
          }
          String targetName = providerTarget.getTargetName();
          bindingTarget = entityContainer.getEntitySet(targetName);
          if (bindingTarget == null) {
            bindingTarget = entityContainer.getSingleton(targetName);
            if (bindingTarget != null) {
              break;
            } else {
              throw new EdmException("Cant find target with name: " + targetName);
            }
          } else {
            break;
          }
        }
      }
    }

    return bindingTarget;
  }

  @Override
  public EdmEntityContainer getEntityContainer() {
    return container;
  }

  @Override
  public EdmEntityType getEntityType() {
    EdmEntityType type = edm.getEntityType(target.getType());
    if (type == null) {
      throw new EdmException("Can´t find entity type : " + target.getType() + "for entity set: " + target.getName());
    }
    return type;
  }

}
