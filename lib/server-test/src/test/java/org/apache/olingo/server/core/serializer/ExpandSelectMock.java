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

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceComplexProperty;
import org.apache.olingo.server.api.uri.UriResourceFunction;
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
        Mockito.when(element.getSegmentValue()).thenReturn(((EdmNavigationProperty) edmElement).getName());
        Mockito.when(element.getType()).thenReturn(((EdmNavigationProperty) edmElement).getType());
        elements.add(element);
      } else {
        final EdmProperty property = (EdmProperty) edmElement;
        UriResourceProperty element = Mockito.mock(UriResourceProperty.class);
        Mockito.when(element.getProperty()).thenReturn(property);
        Mockito.when(element.getSegmentValue()).thenReturn(property.getName());
        elements.add(element);
        type = property.isPrimitive() ? null : (EdmStructuredType) property.getType();
        Mockito.when(element.getType()).thenReturn(type);
      }
    }
    UriInfoResource resource = Mockito.mock(UriInfoResource.class);
    Mockito.when(resource.getUriResourceParts()).thenReturn(elements);
    return resource;
  }

  private static UriInfoResource mockResourceOnDerivedComplexTypes(final EdmEntitySet edmEntitySet,
      final String name, final EdmType derivedType, final String pathSegmentAfterCast) {
    EdmStructuredType type = edmEntitySet.getEntityType();
    List<UriResource> elements = new ArrayList<UriResource>();
    mockComplexPropertyWithTypeFilter(name, derivedType, type, elements);
    
    mockPropertyOnDerivedType(derivedType, pathSegmentAfterCast, elements);
    
    UriInfoResource resource = Mockito.mock(UriInfoResource.class);
    Mockito.when(resource.getUriResourceParts()).thenReturn(elements);
    return resource;
  }
  
  private static UriInfoResource mockResourceOnComplexTypesWithNav(final EdmEntitySet edmEntitySet,
      final String name, final String navProperty) {
    EdmStructuredType type = edmEntitySet.getEntityType();
    List<UriResource> elements = new ArrayList<UriResource>();
    final EdmElement edmElement = type.getProperty(name);
    final EdmProperty property = (EdmProperty) edmElement;
    UriResourceComplexProperty element = Mockito.mock(UriResourceComplexProperty.class);
    Mockito.when(element.getProperty()).thenReturn(property);
    elements.add(element);
    
    mockNavPropertyOnEdmType(navProperty, elements, property);
    
    UriInfoResource resource = Mockito.mock(UriInfoResource.class);
    Mockito.when(resource.getUriResourceParts()).thenReturn(elements);
    return resource;
  }

  public static SelectItem mockSelectItemHavingAction(final EdmEntitySet edmEntitySet, 
      final EdmAction action) {
    final UriInfoResource resource = mockResourceOnAction(
        edmEntitySet, action);
    SelectItem selectItem = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem.getResourcePath()).thenReturn(resource);
    return selectItem;
  }
  
  public static SelectItem mockSelectItemHavingFunction(final EdmEntitySet edmEntitySet, 
      final EdmFunction function) {
    final UriInfoResource resource = mockResourceOnFunction(
        edmEntitySet, function);
    SelectItem selectItem = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem.getResourcePath()).thenReturn(resource);
    return selectItem;
  }
  
  private static UriInfoResource mockResourceOnAction(
      EdmEntitySet edmEntitySet, EdmAction action) {
    List<UriResource> elements = new ArrayList<UriResource>();
    UriResourceAction element = Mockito.mock(UriResourceAction.class);
    Mockito.when(element.getAction()).thenReturn(action);
    elements.add(element);
    
    UriInfoResource resource = Mockito.mock(UriInfoResource.class);
    Mockito.when(resource.getUriResourceParts()).thenReturn(elements);
    return resource;
  }
  
  private static UriInfoResource mockResourceOnFunction(EdmEntitySet edmEntitySet, EdmFunction function) {
    UriResourceFunction element = Mockito.mock(UriResourceFunction.class);
    Mockito.when(element.getFunction()).thenReturn(function);
    List<UriResource> elements = new ArrayList<UriResource>();
    elements.add(element);
    
    UriInfoResource resource = Mockito.mock(UriInfoResource.class);
    Mockito.when(resource.getUriResourceParts()).thenReturn(elements);
    return resource;
  }

  /**
   * @param navProperty
   * @param elements
   * @param property
   */
  private static void mockNavPropertyOnEdmType(final String navProperty, List<UriResource> elements,
      final EdmProperty property) {
    final EdmElement edmElement1 = ((EdmStructuredType) property.getType()).getProperty(navProperty);
    UriResourceNavigation element1 = Mockito.mock(UriResourceNavigation.class);
    Mockito.when(element1.getProperty()).thenReturn((EdmNavigationProperty) edmElement1);
    elements.add(element1);
  }
  
  private static UriInfoResource mockResourceOnDerivedEntityTypes(
      final String name, final EdmType derivedType) {
    EdmStructuredType type = (EdmStructuredType) derivedType;
    List<UriResource> elements = new ArrayList<UriResource>();
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
    }
    UriInfoResource resource = Mockito.mock(UriInfoResource.class);
    Mockito.when(resource.getUriResourceParts()).thenReturn(elements);
    return resource;
  }
  
  private static UriInfoResource mockResourceOnDerivedEntityAndComplexTypes(
      final String name, final EdmType derivedEntityType, final EdmType derivedComplexType, 
      final String pathSegment) {
    EdmStructuredType type = (EdmStructuredType) derivedEntityType;
    List<UriResource> elements = new ArrayList<UriResource>();
    mockComplexPropertyWithTypeFilter(name, derivedComplexType, type, elements);
    
    final EdmElement edmElement1 = ((EdmStructuredType) derivedComplexType).getProperty(pathSegment);
    UriResourceNavigation element1 = Mockito.mock(UriResourceNavigation.class);
    Mockito.when(element1.getProperty()).thenReturn((EdmNavigationProperty) edmElement1);
    elements.add(element1);
    
    UriInfoResource resource = Mockito.mock(UriInfoResource.class);
    Mockito.when(resource.getUriResourceParts()).thenReturn(elements);
    return resource;
  }

  /**
   * @param name
   * @param derivedComplexType
   * @param type
   * @param elements
   */
  private static void mockComplexPropertyWithTypeFilter(final String name, final EdmType derivedComplexType,
      EdmStructuredType type, List<UriResource> elements) {
    final EdmElement edmElement = type.getProperty(name);
    final EdmProperty property = (EdmProperty) edmElement;
    UriResourceComplexProperty element = Mockito.mock(UriResourceComplexProperty.class);
    Mockito.when(element.getProperty()).thenReturn(property);
    Mockito.when(element.getComplexTypeFilter()).thenReturn((EdmComplexType) derivedComplexType);
    elements.add(element);
  }
  
  private static UriInfoResource mockResourceMultiLevelOnDerivedComplexTypes(final EdmEntitySet edmEntitySet, 
      final String pathSegmentBeforeCast,
      final String name, final EdmType derivedType, final String pathSegmentAfterCast) {
    EdmStructuredType type = edmEntitySet.getEntityType();
    List<UriResource> elements = new ArrayList<UriResource>();
    final EdmElement edmElement = type.getProperty(name);
    final EdmProperty property = (EdmProperty) edmElement;
    UriResourceComplexProperty element = Mockito.mock(UriResourceComplexProperty.class);
    Mockito.when(element.getProperty()).thenReturn(property);
    elements.add(element);
    
    if (pathSegmentBeforeCast != null) {
      mockComplexPropertyWithTypeFilter(pathSegmentBeforeCast, (EdmComplexType) derivedType, 
          (EdmStructuredType) edmElement.getType(), elements);
    }
    
    mockPropertyOnDerivedType(derivedType, pathSegmentAfterCast, elements);
    
    UriInfoResource resource = Mockito.mock(UriInfoResource.class);
    Mockito.when(resource.getUriResourceParts()).thenReturn(elements);
    return resource;
  }

  /**
   * @param derivedType
   * @param pathSegment
   * @param elements
   */
  private static void mockPropertyOnDerivedType(final EdmType derivedType, final String pathSegment,
      List<UriResource> elements) {
    if (pathSegment != null) {
      final EdmElement edmElement1 = ((EdmStructuredType) derivedType).getProperty(pathSegment);
      final EdmProperty property1 = (EdmProperty) edmElement1;
      UriResourceProperty element1 = Mockito.mock(UriResourceProperty.class);
      Mockito.when(element1.getProperty()).thenReturn(property1);
      elements.add(element1);
    }
  }
  
  public static SelectItem mockSelectItem(final EdmEntitySet edmEntitySet, final String... names) {
    final UriInfoResource resource = mockResource(edmEntitySet, names);
    SelectItem selectItem = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem.getResourcePath()).thenReturn(resource);
    return selectItem;
  }
  
  public static SelectItem mockSelectItemOnDerivedComplexTypes(final EdmEntitySet edmEntitySet, final String name, 
      final EdmType type, final String pathSegmentAfterCast) {
    final UriInfoResource resource = mockResourceOnDerivedComplexTypes(edmEntitySet,  
        name, type, pathSegmentAfterCast);
    SelectItem selectItem = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem.getResourcePath()).thenReturn(resource);
    return selectItem;
  }
  
  public static SelectItem mockSelectItemOnDerivedEntityTypes(final String name, final EdmType type) {
    final UriInfoResource resource = mockResourceOnDerivedEntityTypes(name, type);
    SelectItem selectItem = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem.getResourcePath()).thenReturn(resource);
    return selectItem;
  }
  
  public static SelectItem mockSelectItemOnDerivedEntityAndComplexTypes(
      final String name, final EdmType entityType, final EdmType complexType, final String pathSegment) {
    final UriInfoResource resource = mockResourceOnDerivedEntityAndComplexTypes(
        name, entityType, complexType, pathSegment);
    SelectItem selectItem = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem.getResourcePath()).thenReturn(resource);
    return selectItem;
  }
  
  public static SelectItem mockSelectItemMultiLevelOnDerivedComplexTypes(
      final EdmEntitySet edmEntitySet, final String name, 
      final String pathSegmentBeforeCast, final EdmType type, final String pathSegmentAfterCast) {
    final UriInfoResource resource = mockResourceMultiLevelOnDerivedComplexTypes(
        edmEntitySet, pathSegmentBeforeCast, name, type, pathSegmentAfterCast);
    SelectItem selectItem = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem.getResourcePath()).thenReturn(resource);
    return selectItem;
  }
  
  public static SelectItem mockSelectItemOnComplexTypesWithNav (
      final EdmEntitySet edmEntitySet, final String name, final String navProperty) {
    final UriInfoResource resource = mockResourceOnComplexTypesWithNav(
        edmEntitySet, name, navProperty);
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
  
  /**
   * @param resource
   * @return
   */
  public static SelectItem mockSelectItemForColComplexProperty(final UriInfoResource resource) {
    SelectItem selectItem = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem.getResourcePath()).thenReturn(resource);
    return selectItem;
  }

  /**
   * @param propertyWithinCT
   * @return
   */
  public static UriInfoResource mockComplexTypeResource(final EdmProperty propertyWithinCT) {
    final UriInfoResource resource = Mockito.mock(UriInfoResource.class);
    final List<UriResource> elements = new ArrayList<UriResource>();
    final UriResourceProperty element = Mockito.mock(UriResourceProperty.class);
    Mockito.when(element.getProperty()).thenReturn(propertyWithinCT);
    elements.add(element);
    Mockito.when(resource.getUriResourceParts()).thenReturn(elements);
    return resource;
  }
}
