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

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public abstract class AbstractEdmNavigationProperty extends EdmElementImpl implements EdmNavigationProperty {

  private EdmEntityType typeImpl;

  private EdmNavigationProperty partnerNavigationProperty;

  public AbstractEdmNavigationProperty(final Edm edm, final String name) {
    super(edm, name);
  }

  protected abstract FullQualifiedName getTypeFQN();

  @Override
  public EdmType getType() {
    if (typeImpl == null) {
      typeImpl = edm.getEntityType(getTypeFQN());
      if (typeImpl == null) {
        throw new EdmException("Cannot find type with name: " + getTypeFQN());
      }
    }
    return typeImpl;
  }

  protected abstract String internatGetPartner();

  @Override
  public EdmNavigationProperty getPartner() {
    if (partnerNavigationProperty == null) {
      String partner = internatGetPartner();
      if (partner != null) {
        EdmStructuredType type = (EdmStructuredType) getType();
        EdmNavigationProperty property = null;
        final String[] split = partner.split("/");
        for (String element : split) {
          property = type.getNavigationProperty(element);
          if (property == null) {
            throw new EdmException("Cannot find navigation property with name: " + element
                + " at type " + type.getName());
          }
          type = (EdmStructuredType) property.getType();
        }
        partnerNavigationProperty = property;
      }
    }
    return partnerNavigationProperty;
  }

  public abstract String getReferencingPropertyName(String referencedPropertyName);
}
