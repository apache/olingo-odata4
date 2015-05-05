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
package org.apache.olingo.server.core.serializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.mockito.Mockito;

public final class ExpandSelectMock {

  private static UriInfoResource mockResource(final EdmEntitySet edmEntitySet, final String... names) {
    EdmStructuredType type = edmEntitySet.getEntityType();
    List<UriResource> elements = new ArrayList<UriResource>();
    for (final String name : Arrays.asList(names)) {
      final EdmElement edmElement = type.getProperty(name);
      if (edmElement.getType().getKind() == EdmTypeKind.ENTITY) {
        UriResourceNavigation element = Mockito.mock(UriResourceNavigation.class);
        Mockito.when(element.getProperty()).thenReturn((EdmNavigationProperty) edmElement);
        elements.add(element);
      } else {
        final EdmProperty property = (EdmProperty) edmElement;
        UriResourceProperty element = Mockito.mock(UriResourceProperty.class);
        Mockito.when(element.getProperty()).thenReturn(property);
        elements.add(element);
        type = property.isPrimitive() ? null : (EdmStructuredType) property.getType();
      }
    }
    UriInfoResource resource = Mockito.mock(UriInfoResource.class);
    Mockito.when(resource.getUriResourceParts()).thenReturn(elements);
    return resource;
  }

  public static SelectItem mockSelectItem(final EdmEntitySet edmEntitySet, final String... names) {
    final UriInfoResource resource = mockResource(edmEntitySet, names);
    SelectItem selectItem = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem.getResourcePath()).thenReturn(resource);
    return selectItem;
  }

  public static SelectOption mockSelectOption(final List<SelectItem> selectItems) {
    SelectOption select = Mockito.mock(SelectOption.class);
    Mockito.when(select.getSelectItems()).thenReturn(selectItems);
    return select;
  }

  public static ExpandItem mockExpandItem(final EdmEntitySet edmEntitySet, final String... names) {
    final UriInfoResource resource = mockResource(edmEntitySet, names);
    ExpandItem expandItem = Mockito.mock(ExpandItem.class);
    Mockito.when(expandItem.getResourcePath()).thenReturn(resource);
    return expandItem;
  }

  public static ExpandOption mockExpandOption(final List<ExpandItem> expandItems) {
    ExpandOption expand = Mockito.mock(ExpandOption.class);
    Mockito.when(expand.getExpandItems()).thenReturn(expandItems);
    return expand;
  }
}
