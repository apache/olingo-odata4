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
package org.apache.olingo.server.core.testutil.techprovider;

import java.util.Arrays;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.edm.provider.ComplexType;
import org.apache.olingo.server.api.edm.provider.NavigationProperty;
import org.apache.olingo.server.api.edm.provider.Property;

public class ComplexTypeProvider {

  public static final FullQualifiedName nameCTAllPrim = new FullQualifiedName(SchemaProvider.nameSpace, "CTAllPrim");
  public static final FullQualifiedName nameCTBase = new FullQualifiedName(SchemaProvider.nameSpace, "CTBase");
  public static final FullQualifiedName nameCTBasePrimCompNav = new FullQualifiedName(SchemaProvider.nameSpace,
      "CTBasePrimCompNav");
  public static final FullQualifiedName nameCTCollAllPrim = new FullQualifiedName(SchemaProvider.nameSpace,
      "CTCollAllPrim");
  public static final FullQualifiedName nameCTCompCollComp = new FullQualifiedName(SchemaProvider.nameSpace,
      "CTCompCollComp");
  public static final FullQualifiedName nameCTCompComp = new FullQualifiedName(SchemaProvider.nameSpace, "CTCompComp");
  public static final FullQualifiedName nameCTCompNav = new FullQualifiedName(SchemaProvider.nameSpace, "CTCompNav");

  public static final FullQualifiedName nameCTMixPrimCollComp = new FullQualifiedName(SchemaProvider.nameSpace,
      "CTMixPrimCollComp");
  public static final FullQualifiedName nameCTNavFiveProp = new FullQualifiedName(SchemaProvider.nameSpace,
      "CTNavFiveProp");
  public static final FullQualifiedName nameCTPrim = new FullQualifiedName(SchemaProvider.nameSpace, "CTPrim");
  public static final FullQualifiedName nameCTPrimComp = new FullQualifiedName(SchemaProvider.nameSpace, "CTPrimComp");
  public static final FullQualifiedName nameCTPrimEnum = new FullQualifiedName(SchemaProvider.nameSpace, "CTPrimEnum");
  public static final FullQualifiedName nameCTTwoBase = new FullQualifiedName(SchemaProvider.nameSpace, "CTTwoBase");
  public static final FullQualifiedName nameCTTwoBasePrimCompNav =
      new FullQualifiedName(SchemaProvider.nameSpace, "CTTwoBasePrimCompNav");
  public static final FullQualifiedName nameCTTwoPrim = new FullQualifiedName(SchemaProvider.nameSpace, "CTTwoPrim");

  public ComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {

    if (complexTypeName.equals(nameCTPrim)) {
      return new ComplexType()
          .setName("CTPrim")
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16));

    } else if (complexTypeName.equals(nameCTAllPrim)) {
      return new ComplexType()
          .setName("CTAllPrim")
          .setProperties(
              Arrays.asList(PropertyProvider.propertyString, PropertyProvider.propertyBinary,
                  PropertyProvider.propertyBoolean, PropertyProvider.propertyByte, PropertyProvider.propertyDate,
                  PropertyProvider.propertyDateTimeOffset, PropertyProvider.propertyDecimal,
                  PropertyProvider.propertySingle, PropertyProvider.propertyDouble, PropertyProvider.propertyDuration,
                  PropertyProvider.propertyGuid, PropertyProvider.propertyInt16, PropertyProvider.propertyInt32,
                  PropertyProvider.propertyInt64, PropertyProvider.propertySByte, PropertyProvider.propertyTimeOfDay
                  /* TODO add propertyStream */));

    } else if (complexTypeName.equals(nameCTCollAllPrim)) {
      return new ComplexType()
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
                  /* TODO add collectionPropertyStream */));

    } else if (complexTypeName.equals(nameCTTwoPrim)) {
      return new ComplexType()
          .setName("CTTwoPrim")
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16, PropertyProvider.propertyString));

    } else if (complexTypeName.equals(nameCTCompNav)) {
      return new ComplexType()
          .setName("CTCompNav")
          .setProperties(Arrays.asList(PropertyProvider.propertyString,
              PropertyProvider.propertyComplex_CTNavFiveProp));

    } else if (complexTypeName.equals(nameCTMixPrimCollComp)) {
      return new ComplexType()
          .setName("CTMixPrimCollComp")
          .setProperties(
              Arrays.asList(PropertyProvider.propertyInt16, PropertyProvider.collPropertyString,
                  PropertyProvider.propertyComplex_CTTwoPrim, PropertyProvider.collPropertyComplex_CTTwoPrim));

    } else if (complexTypeName.equals(nameCTBase)) {
      return new ComplexType()
          .setName("CTBase")
          .setBaseType(nameCTTwoPrim)
          .setProperties(Arrays.asList(
              new Property()
                  .setName("AdditionalPropString")
                  .setType(new FullQualifiedName("Edm", "String"))));

    } else if (complexTypeName.equals(nameCTTwoBase)) {
      return new ComplexType()
          .setName("CTTwoBase")
          .setBaseType(nameCTBase);

    } else if (complexTypeName.equals(nameCTCompComp)) {
      return new ComplexType()
          .setName("CTCompComp")
          .setProperties(Arrays.asList(PropertyProvider.propertyComplex_CTTwoPrim));

    } else if (complexTypeName.equals(nameCTCompCollComp)) {
      return new ComplexType()
          .setName("CTCompCollComp")
          .setProperties(Arrays.asList(PropertyProvider.collPropertyComplex_CTTwoPrim));

    } else if (complexTypeName.equals(nameCTPrimComp)) {
      return new ComplexType()
          .setName("CTPrimComp")
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16, PropertyProvider.propertyComplex_CTAllPrim));

    } else if (complexTypeName.equals(nameCTNavFiveProp)) {
      return new ComplexType()
          .setName("CTNavFiveProp")
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16))
          .setNavigationProperties((Arrays.asList(
              PropertyProvider.collectionNavPropertyETTwoKeyNavOne_ETTwoKeyNav,
              PropertyProvider.collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              new NavigationProperty()
                  .setName("NavPropertyETMediaOne")
                  .setType(EntityTypeProvider.nameETMedia),
              new NavigationProperty()
                  .setName("NavPropertyETMediaMany")
                  .setType(EntityTypeProvider.nameETMedia).setCollection(true)
              )));

    } else if (complexTypeName.equals(nameCTBasePrimCompNav)) {
      return new ComplexType()
          .setName("CTBasePrimCompNav")
          .setBaseType(nameCTPrimComp)
          .setNavigationProperties(Arrays.asList(
              PropertyProvider.collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              PropertyProvider.collectionNavPropertyETTwoKeyNavOne_ETTwoKeyNav,
              PropertyProvider.navPropertyETKeyNavOne_ETKeyNav,
              PropertyProvider.collectionNavPropertyETKeyNavMany_ETKeyNav));

    } else if (complexTypeName.equals(nameCTPrimEnum)) {
      return new ComplexType()
          .setName("CTPrimEnum")
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16, PropertyProvider.propertyEnumString_ENString));

    } else if (complexTypeName.equals(nameCTTwoBasePrimCompNav)) {
      return new ComplexType()
          .setName("CTTwoBasePrimCompNav")
          .setBaseType(nameCTBasePrimCompNav);

    }

    return null;
  }

}
