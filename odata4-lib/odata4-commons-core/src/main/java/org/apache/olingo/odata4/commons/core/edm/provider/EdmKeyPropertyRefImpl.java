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
package org.apache.olingo.odata4.commons.core.edm.provider;

import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.odata4.commons.api.edm.EdmProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmStructuralType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.odata4.commons.api.edm.provider.PropertyRef;

public class EdmKeyPropertyRefImpl implements EdmKeyPropertyRef {

  private PropertyRef ref;
  private EdmEntityType edmEntityType;
  private EdmProperty property;

  public EdmKeyPropertyRefImpl(final EdmEntityType edmEntityType, final PropertyRef ref) {
    this.edmEntityType = edmEntityType;
    this.ref = ref;
  }

  @Override
  public String getKeyPropertyName() {
    return ref.getPropertyName();
  }

  @Override
  public String getAlias() {
    return ref.getAlias();
  }

  @Override
  public String getPath() {
    return ref.getPath();
  }

  @Override
  public EdmProperty getProperty() {
    if (property == null) {
      if (ref.getAlias() == null) {
        property = (EdmProperty) edmEntityType.getProperty(ref.getPropertyName());
        if (property == null) {
          throw new EdmException("Invalid key property ref specified. Can´t find property with name: "
              + ref.getPropertyName());
        }
      } else {
        String[] splitPath = ref.getPath().split("/");
        EdmStructuralType structType = edmEntityType;
        for (int i = 0; i < splitPath.length; i++) {
          property = (EdmProperty) structType.getProperty(splitPath[i]);
          if (property == null) {
            throw new EdmException("Invalid key property ref specified. Can´t find property with name: "
                + splitPath[i]);
          }
          EdmType childType = property.getType();
          if (childType.getKind() == EdmTypeKind.COMPLEX) {
            structType = (EdmStructuralType) childType;
          } else {
            if (i + 1 != splitPath.length) {
              throw new EdmException("Invalid path: " + ref.getPath() + " Must end after: " + splitPath[i]);
            }
          }
        }
      }
    }

    return property;
  }

}
