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
import org.apache.olingo.odata4.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.provider.NavigationProperty;

public class EdmNavigationPropertyImpl extends EdmElementImpl implements EdmNavigationProperty {

  private final NavigationProperty navigationProperty;
  private EdmEntityType typeImpl;

  public EdmNavigationPropertyImpl(final EdmProviderImpl edm, final NavigationProperty navigationProperty) {
    super(edm, navigationProperty.getName());
    this.navigationProperty = navigationProperty;
  }

  @Override
  public EdmType getType() {
    if (typeImpl == null) {
      typeImpl = edm.getEntityType(navigationProperty.getType());
      if (typeImpl == null) {
        throw new EdmException("Cannot find type with name: " + navigationProperty.getType());
      }
    }
    return typeImpl;
  }

  @Override
  public boolean isCollection() {
    return navigationProperty.isCollection();
  }

  @Override
  public Boolean isNullable() {
    return navigationProperty.getNullable();
  }

}
