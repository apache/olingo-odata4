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
package org.apache.olingo.server.core.uri.testutil;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;

/**
 * Implement the EdmTechProvider and
 * <li>adds a entity type <b>ETabc with</b> properties a,b,c,d,e,f</li>
 * <li>adds a complex type <b>CTabc</b> with properties a,b,c,d,e,f</li>
 * <li>adds a <b>abc</b> entity set of type <b>ETabc</b></li>
 */
public class EdmTechTestProvider extends EdmTechProvider {

  private static final FullQualifiedName nameInt16 = EdmPrimitiveTypeKind.Int16.getFullQualifiedName();
  public static final String NAMESPACE = "olingo.odata.test1";
  public static final FullQualifiedName nameContainer = new FullQualifiedName(NAMESPACE, "Container");

  CsdlProperty propertyAInt16 = new CsdlProperty().setName("a").setType(nameInt16);
  CsdlProperty propertyBInt16 = new CsdlProperty().setName("b").setType(nameInt16);
  CsdlProperty propertyCInt16 = new CsdlProperty().setName("c").setType(nameInt16);
  CsdlProperty propertyDInt16 = new CsdlProperty().setName("d").setType(nameInt16);
  CsdlProperty propertyEInt16 = new CsdlProperty().setName("e").setType(nameInt16);
  CsdlProperty propertyFInt16 = new CsdlProperty().setName("f").setType(nameInt16);

  public static final FullQualifiedName nameCTabc = new FullQualifiedName(NAMESPACE, "CTabc");
  public static final FullQualifiedName nameETabc = new FullQualifiedName(NAMESPACE, "ETabc");

  @Override
  public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
    if (complexTypeName.equals(nameCTabc)) {
      return new CsdlComplexType()
      .setName("CTabc")
      .setProperties(Arrays.asList(
          propertyAInt16, propertyBInt16, propertyCInt16,
          propertyDInt16, propertyEInt16, propertyFInt16
          ));

    }

    return super.getComplexType(complexTypeName);
  }

  @Override
  public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String name) throws ODataException {
    if (nameContainer.equals(entityContainer)) {
      if (name.equals("ESabc")) {
        return new CsdlEntitySet()
        .setName("ESabc")
        .setType(nameETabc);
      }
    }

    return super.getEntitySet(entityContainer, name);
  }

  @Override
  public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
    List<CsdlPropertyRef> oneKeyPropertyInt16 = Arrays.asList(new CsdlPropertyRef().setName("a"));

    if (entityTypeName.equals(nameETabc)) {
      return new CsdlEntityType()
      .setName("ETabc")
      .setProperties(Arrays.asList(
          propertyAInt16, propertyBInt16, propertyCInt16,
          propertyDInt16, propertyEInt16, propertyFInt16))
          .setKey(oneKeyPropertyInt16);
    }

    return super.getEntityType(entityTypeName);
  }

}
