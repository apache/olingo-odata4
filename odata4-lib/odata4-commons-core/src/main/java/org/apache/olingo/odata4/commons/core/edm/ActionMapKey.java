/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.commons.core.edm;

import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;

public class ActionMapKey {
  private final FullQualifiedName actionName;
  private final FullQualifiedName bindingParameterTypeName;
  private final Boolean isBindingParameterCollection;

  public ActionMapKey(final FullQualifiedName actionName, final FullQualifiedName bindingParameterTypeName,
      final Boolean isBindingParameterCollection) {
    this.actionName = actionName;
    this.bindingParameterTypeName = bindingParameterTypeName;
    this.isBindingParameterCollection = isBindingParameterCollection;
  }

  @Override
  public int hashCode() {
    String forHash = actionName.toString();

    if (bindingParameterTypeName != null) {
      forHash = forHash + bindingParameterTypeName.toString();
    } else {
      forHash = forHash + "TypeNull";
    }

    if (isBindingParameterCollection != null) {
      forHash = forHash + isBindingParameterCollection.toString();
    } else {
      forHash = forHash + "CollectionNull";
    }

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

    if (actionName.equals(other.actionName)) {
      if ((bindingParameterTypeName == null && other.bindingParameterTypeName == null)
          || (bindingParameterTypeName != null && bindingParameterTypeName.equals(other.bindingParameterTypeName))) {
        if ((isBindingParameterCollection == null && other.isBindingParameterCollection == null)
            || (isBindingParameterCollection != null && isBindingParameterCollection
                .equals(other.isBindingParameterCollection))) {
          return true;
        }
      }
    }
    return false;
  }
}
