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

import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

/**
 * An Edm target element. It contains a target as a String name as well as the {@link FullQualifiedName} of the entity
 * container it is contained in.
 */
public class Target {

  private String targetName;
  private FullQualifiedName entityContainer;

  public Target(final String target, final EdmEntityContainer defaultContainer) {
    final String[] bindingTargetParts = target.split("/");
    if (bindingTargetParts.length == 1) {
      entityContainer = defaultContainer.getFullQualifiedName();
      targetName = bindingTargetParts[0];
    } else {
      entityContainer = new FullQualifiedName(bindingTargetParts[0]);
      targetName = bindingTargetParts[1];
    }
  }

  /**
   * @return name of the target as a String
   */
  public String getTargetName() {
    return targetName;
  }

  /**
   * @return {@link FullQualifiedName} of the entity container this target is contained in.
   */
  public FullQualifiedName getEntityContainer() {
    return entityContainer;
  }

  @Override
  public String toString() {
    if (entityContainer == null) {
      return targetName;
    }
    return entityContainer.getFullQualifiedNameAsString() + "/" + targetName;
  }

}
