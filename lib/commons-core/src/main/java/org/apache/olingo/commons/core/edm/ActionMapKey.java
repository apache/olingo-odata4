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

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class ActionMapKey {

  private final FullQualifiedName actionName;

  private final FullQualifiedName bindingParameterTypeName;

  private final Boolean isBindingParameterCollection;

  public ActionMapKey(final FullQualifiedName actionName, final FullQualifiedName bindingParameterTypeName,
      final Boolean isBindingParameterCollection) {

    if (actionName == null || bindingParameterTypeName == null || isBindingParameterCollection == null) {
      throw new EdmException("Action name, binding parameter type and binding parameter collection "
          + "must not be null for bound actions");
    }
    this.actionName = actionName;
    this.bindingParameterTypeName = bindingParameterTypeName;
    this.isBindingParameterCollection = isBindingParameterCollection;
  }

  @Override
  public int hashCode() {
    final String forHash = actionName.toString()
        + bindingParameterTypeName.toString()
        + isBindingParameterCollection.toString();
    return forHash.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || !(obj instanceof ActionMapKey)) {
      return false;
    }
    final ActionMapKey other = (ActionMapKey) obj;
    if (actionName.equals(other.actionName) && bindingParameterTypeName.equals(other.bindingParameterTypeName)
        && isBindingParameterCollection.equals(other.isBindingParameterCollection)) {
      return true;
    }
    return false;
  }
}
