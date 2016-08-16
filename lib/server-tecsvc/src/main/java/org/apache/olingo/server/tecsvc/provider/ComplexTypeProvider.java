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
package org.apache.olingo.server.tecsvc.provider;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;

public class ComplexTypeProvider {

  public static final FullQualifiedName nameCTAllPrim = new FullQualifiedName(SchemaProvider.NAMESPACE, "CTAllPrim");
  public static final FullQualifiedName nameCTBase = new FullQualifiedName(SchemaProvider.NAMESPACE, "CTBase");
  public static final FullQualifiedName nameCTBasePrimCompNav = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "CTBasePrimCompNav");
  public static final FullQualifiedName nameCTCollAllPrim = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "CTCollAllPrim");
  public static final FullQualifiedName nameCTCompCollComp = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "CTCompCollComp");
  public static final FullQualifiedName nameCTCompComp = new FullQualifiedName(SchemaProvider.NAMESPACE, "CTCompComp");
  public static final FullQualifiedName nameCTCompNav = new FullQualifiedName(SchemaProvider.NAMESPACE, "CTCompNav");

  public static final FullQualifiedName nameCTMixPrimCollComp = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "CTMixPrimCollComp");
  public static final FullQualifiedName nameCTNavFiveProp = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "CTNavFiveProp");
  public static final FullQualifiedName nameCTPrim = new FullQualifiedName(SchemaProvider.NAMESPACE, "CTPrim");
  public static final FullQualifiedName nameCTPrimComp = new FullQualifiedName(SchemaProvider.NAMESPACE, "CTPrimComp");
  public static final FullQualifiedName nameCTTwoBase = new FullQualifiedName(SchemaProvider.NAMESPACE, "CTTwoBase");
  public static final FullQualifiedName nameCTTwoBasePrimCompNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "CTTwoBasePrimCompNav");
  public static final FullQualifiedName nameCTTwoPrim = new FullQualifiedName(SchemaProvider.NAMESPACE, "CTTwoPrim");
  public static final FullQualifiedName nameCTMixEnumDef = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "CTMixEnumDef");
  public static final FullQualifiedName nameCTNavCont = new FullQualifiedName(SchemaProvider.NAMESPACE, "CTNavCont");

  public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {

    if (complexTypeName.equals(nameCTPrim)) {
      return new CsdlComplexType()
          .setName("CTPrim")
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16));

    } else if (complexTypeName.equals(nameCTAllPrim)) {
      return new CsdlComplexType()
          .setName("CTAllPrim")
          .setProperties(
              Arrays.asList(PropertyProvider.propertyString, PropertyProvider.propertyBinary,
                  PropertyProvider.propertyBoolean, PropertyProvider.propertyByte, PropertyProvider.propertyDate,
                  PropertyProvider.propertyDateTimeOffset_Precision, PropertyProvider.propertyDecimal_Scale_Precision,
                  PropertyProvider.propertySingle, PropertyProvider.propertyDouble, PropertyProvider.propertyDuration,
                  PropertyProvider.propertyGuid, PropertyProvider.propertyInt16, PropertyProvider.propertyInt32,
                  PropertyProvider.propertyInt64, PropertyProvider.propertySByte,
                  PropertyProvider.propertyTimeOfDay_Precision));
    } else if (complexTypeName.equals(nameCTCollAllPrim)) {
      return new CsdlComplexType()
          .setName("CTCollAllPrim")
          .setProperties(
              Arrays.asList(
                  PropertyProvider.collPropertyString, PropertyProvider.collPropertyBoolean,
                  PropertyProvider.collPropertyByte, PropertyProvider.collPropertySByte,
                  PropertyProvider.collPropertyInt16, PropertyProvider.collPropertyInt32,
                  PropertyProvider.collPropertyInt64, PropertyProvider.collPropertySingle,
                  PropertyProvider.collPropertyDouble, PropertyProvider.collPropertyDecimal,
                  PropertyProvider.collPropertyBinary, PropertyProvider.collPropertyDate,
                  PropertyProvider.collPropertyDateTimeOffset, PropertyProvider.collPropertyDuration,
                  PropertyProvider.collPropertyGuid, PropertyProvider.collPropertyTimeOfDay
                  ));

    } else if (complexTypeName.equals(nameCTTwoPrim)) {
      return new CsdlComplexType()
          .setName("CTTwoPrim")
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16_NotNullable,
              PropertyProvider.propertyString_NotNullable));

    } else if (complexTypeName.equals(nameCTCompNav)) {
      return new CsdlComplexType()
          .setName("CTCompNav")
          .setProperties(Arrays.asList(PropertyProvider.propertyString,
              PropertyProvider.propertyCompNav_CTNavFiveProp));

    } else if (complexTypeName.equals(nameCTMixPrimCollComp)) {
      return new CsdlComplexType()
          .setName("CTMixPrimCollComp")
          .setProperties(
              Arrays.asList(PropertyProvider.propertyInt16, PropertyProvider.collPropertyString,
                  PropertyProvider.propertyComp_CTTwoPrim, PropertyProvider.collPropertyComp_CTTwoPrim));

    } else if (complexTypeName.equals(nameCTBase)) {
      return new CsdlComplexType()
          .setName("CTBase")
          .setBaseType(nameCTTwoPrim)
          .setProperties(Arrays.asList(
              new CsdlProperty()
                  .setName("AdditionalPropString")
                  .setType(PropertyProvider.nameString)));

    } else if (complexTypeName.equals(nameCTTwoBase)) {
      return new CsdlComplexType()
          .setName("CTTwoBase")
          .setBaseType(nameCTBase);

    } else if (complexTypeName.equals(nameCTCompComp)) {
      return new CsdlComplexType()
          .setName("CTCompComp")
          .setProperties(Arrays.asList(PropertyProvider.propertyComp_CTTwoPrim));
      
    } else if (complexTypeName.equals(nameCTCompCollComp)) {
      return new CsdlComplexType()
          .setName("CTCompCollComp")
          .setProperties(Arrays.asList(PropertyProvider.collPropertyComp_CTTwoPrim));

    } else if (complexTypeName.equals(nameCTPrimComp)) {
      return new CsdlComplexType()
          .setName("CTPrimComp")
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16, PropertyProvider.propertyComp_CTAllPrim));

    } else if (complexTypeName.equals(nameCTNavFiveProp)) {
      return new CsdlComplexType()
          .setName("CTNavFiveProp")
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16))
          .setNavigationProperties((Arrays.asList(
              PropertyProvider.collectionNavPropertyETTwoKeyNavOne_ETTwoKeyNav,
              PropertyProvider.collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              new CsdlNavigationProperty()
                  .setName("NavPropertyETMediaOne")
                  .setType(EntityTypeProvider.nameETMedia),
              new CsdlNavigationProperty()
                  .setName("NavPropertyETMediaMany")
                  .setType(EntityTypeProvider.nameETMedia).setCollection(true)
              )));

    } else if (complexTypeName.equals(nameCTNavCont)) {
      return new CsdlComplexType()
          .setName("CTNavCont")
          .setProperties(new ArrayList<CsdlProperty>())
          .setNavigationProperties(Arrays.asList(
              PropertyProvider.navPropertyETKeyNavOne_CT_ETeyNav,
              PropertyProvider.collectionNavPropertyETKeyNavMany_CT_ETKeyNav,
              PropertyProvider.navPropertyETTwoKeyNavOne_CT_ETTwoKeyNav,
              PropertyProvider.collectionNavPropertyETTwoKeyNavMany_CT_ETTwoKeyNav
              ));

    } else if (complexTypeName.equals(nameCTBasePrimCompNav)) {
      return new CsdlComplexType()
          .setName("CTBasePrimCompNav")
          .setBaseType(nameCTPrimComp)
          .setNavigationProperties(Arrays.asList(
              PropertyProvider.collectionNavPropertyETTwoKeyNavOne_ETTwoKeyNav,
              PropertyProvider.collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              PropertyProvider.navPropertyETKeyNavOne_ETKeyNav,
              PropertyProvider.collectionNavPropertyETKeyNavMany_ETKeyNav));

    } else if (complexTypeName.equals(nameCTTwoBasePrimCompNav)) {
      return new CsdlComplexType()
          .setName("CTTwoBasePrimCompNav")
          .setBaseType(nameCTBasePrimCompNav);

    } else if (complexTypeName.equals(nameCTMixEnumDef)) {
      return new CsdlComplexType()
          .setName(nameCTMixEnumDef.getName())
          .setProperties(Arrays.asList(
              PropertyProvider.propertyEnumString_ENString,
              PropertyProvider.collPropertyEnumString_ENString,
              PropertyProvider.propertyTypeDefinition_TDString,
              PropertyProvider.collPropertyTypeDefinition_TDString));
    }

    return null;
  }
}
