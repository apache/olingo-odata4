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
package org.apache.olingo.server.core.serializer.json;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.core.edm.EdmComplexTypeImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;

public class ComplexTypeHelper {

  public static EdmComplexType createType() throws ODataException {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    FullQualifiedName baseName = new FullQualifiedName("namespace", "BaseTypeName");
    CsdlComplexType baseComplexType = new CsdlComplexType();
    List<CsdlProperty> baseProperties = new ArrayList<CsdlProperty>();
    baseProperties.add(new CsdlProperty().setName("prop1").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    List<CsdlNavigationProperty> baseNavigationProperties = new ArrayList<CsdlNavigationProperty>();
    baseNavigationProperties.add(new CsdlNavigationProperty().setName("nav1"));
    baseComplexType.setName("BaseTypeName").setAbstract(false).setOpenType(false).setProperties(baseProperties)
        .setNavigationProperties(baseNavigationProperties);
    when(provider.getComplexType(baseName)).thenReturn(baseComplexType);

    FullQualifiedName name = new FullQualifiedName("namespace", "typeName");
    CsdlComplexType complexType = new CsdlComplexType().setBaseType(baseName);
    List<CsdlProperty> properties = new ArrayList<CsdlProperty>();
    properties.add(new CsdlProperty().setName("prop2").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    List<CsdlNavigationProperty> navigationProperties = new ArrayList<CsdlNavigationProperty>();
    navigationProperties.add(new CsdlNavigationProperty().setName("nav2"));
    complexType.setName("BaseTypeName").setAbstract(false).setOpenType(false).setProperties(properties)
        .setNavigationProperties(navigationProperties);
    when(provider.getComplexType(name)).thenReturn(complexType);

    return new EdmComplexTypeImpl(edm, name, complexType);
  }
}
