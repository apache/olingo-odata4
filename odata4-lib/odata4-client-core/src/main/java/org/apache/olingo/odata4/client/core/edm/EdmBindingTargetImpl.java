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
package org.apache.olingo.odata4.client.core.edm;

import java.util.Iterator;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.xml.BindingTarget;
import org.apache.olingo.odata4.client.api.edm.xml.v4.NavigationPropertyBinding;
import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.Target;
import org.apache.olingo.odata4.commons.core.edm.AbstractEdmBindingTarget;

public abstract class EdmBindingTargetImpl extends AbstractEdmBindingTarget {

  private final BindingTarget target;

  public EdmBindingTargetImpl(final Edm edm, final EdmEntityContainer container,
          final String name, final FullQualifiedName type, final BindingTarget target) {

    super(edm, container, name, type);
    this.target = target;
  }

  @Override
  public EdmBindingTarget getRelatedBindingTarget(final String path) {
    EdmBindingTarget bindingTarget = null;

    final List<? extends NavigationPropertyBinding> navigationPropertyBindings = target.getNavigationPropertyBindings();
    if (navigationPropertyBindings != null) {
      boolean found = false;
      for (final Iterator<? extends NavigationPropertyBinding> itor = navigationPropertyBindings.iterator();
              itor.hasNext() && !found;) {

        final NavigationPropertyBinding binding = itor.next();
        if (binding.getPath().equals(path)) {
          final Target edmTarget = new Target.Builder(binding.getTarget(), container).build();

          final EdmEntityContainer entityContainer = edm.getEntityContainer(edmTarget.getEntityContainer());
          if (entityContainer == null) {
            throw new EdmException("Cant find entity container with name: " + edmTarget.getEntityContainer());
          }
          bindingTarget = entityContainer.getEntitySet(edmTarget.getTargetName());
          if (bindingTarget == null) {
            bindingTarget = entityContainer.getSingleton(edmTarget.getTargetName());
            if (bindingTarget == null) {
              throw new EdmException("Cant find target with name: " + edmTarget.getTargetName());
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
