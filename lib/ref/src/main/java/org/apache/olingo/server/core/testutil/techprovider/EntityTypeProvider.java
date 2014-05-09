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
import org.apache.olingo.server.api.edm.provider.EntityType;
import org.apache.olingo.server.api.edm.provider.NavigationProperty;
import org.apache.olingo.server.api.edm.provider.Property;
import org.apache.olingo.server.api.edm.provider.PropertyRef;
import org.apache.olingo.server.api.edm.provider.ReferentialConstraint;

public class EntityTypeProvider {

  public static final FullQualifiedName nameETAllKey = new FullQualifiedName(SchemaProvider.nameSpace, "ETAllKey");
  public static final FullQualifiedName nameETAllNullable = new FullQualifiedName(SchemaProvider.nameSpace,
      "ETAllNullable");
  public static final FullQualifiedName nameETAllPrim = new FullQualifiedName(SchemaProvider.nameSpace, "ETAllPrim");
  public static final FullQualifiedName nameETBase = new FullQualifiedName(SchemaProvider.nameSpace, "ETBase");
  public static final FullQualifiedName nameETBaseTwoKeyNav = new FullQualifiedName(SchemaProvider.nameSpace,
      "ETBaseTwoKeyNav");
  public static final FullQualifiedName nameETBaseTwoKeyTwoPrim =
      new FullQualifiedName(SchemaProvider.nameSpace, "ETBaseTwoKeyTwoPrim");
  public static final FullQualifiedName nameETCollAllPrim = new FullQualifiedName(SchemaProvider.nameSpace,
      "ETCollAllPrim");
  public static final FullQualifiedName nameETCompAllPrim = new FullQualifiedName(SchemaProvider.nameSpace,
      "ETCompAllPrim");
  public static final FullQualifiedName nameETCompCollAllPrim = new FullQualifiedName(SchemaProvider.nameSpace,
      "ETCompCollAllPrim");
  public static final FullQualifiedName nameETCompCollComp = new FullQualifiedName(SchemaProvider.nameSpace,
      "ETCompCollComp");
  public static final FullQualifiedName nameETCompComp = new FullQualifiedName(SchemaProvider.nameSpace, "ETCompComp");
  public static final FullQualifiedName nameETCompMixPrimCollComp =
      new FullQualifiedName(SchemaProvider.nameSpace, "ETCompMixPrimCollComp");
  public static final FullQualifiedName nameETFourKeyAlias = new FullQualifiedName(SchemaProvider.nameSpace,
      "ETFourKeyAlias");
  public static final FullQualifiedName nameETKeyNav = new FullQualifiedName(SchemaProvider.nameSpace, "ETKeyNav");
  public static final FullQualifiedName nameETKeyPrimNav = new FullQualifiedName(SchemaProvider.nameSpace,
      "ETKeyPrimNav");
  public static final FullQualifiedName nameETKeyTwoKeyComp = new FullQualifiedName(SchemaProvider.nameSpace,
      "ETKeyTwoKeyComp");
  public static final FullQualifiedName nameETMedia = new FullQualifiedName(SchemaProvider.nameSpace, "ETMedia");
  public static final FullQualifiedName nameETMixPrimCollComp = new FullQualifiedName(SchemaProvider.nameSpace,
      "ETMixPrimCollComp");
  public static final FullQualifiedName nameETServerSidePaging =
      new FullQualifiedName(SchemaProvider.nameSpace, "ETServerSidePaging");
  public static final FullQualifiedName nameETTwoBase = new FullQualifiedName(SchemaProvider.nameSpace, "ETTwoBase");
  public static final FullQualifiedName nameETTwoBaseTwoKeyNav =
      new FullQualifiedName(SchemaProvider.nameSpace, "ETTwoBaseTwoKeyNav");
  public static final FullQualifiedName nameETTwoBaseTwoKeyTwoPrim =
      new FullQualifiedName(SchemaProvider.nameSpace, "ETTwoBaseTwoKeyTwoPrim");
  public static final FullQualifiedName nameETTwoKeyNav =
      new FullQualifiedName(SchemaProvider.nameSpace, "ETTwoKeyNav");
  public static final FullQualifiedName nameETTwoKeyTwoPrim = new FullQualifiedName(SchemaProvider.nameSpace,
      "ETTwoKeyTwoPrim");
  public static final FullQualifiedName nameETTwoPrim = new FullQualifiedName(SchemaProvider.nameSpace, "ETTwoPrim");

  public EntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
    if (entityTypeName.equals(nameETAllPrim)) {
      return new EntityType()
          .setName("ETAllPrim")
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString,
              PropertyProvider.propertyBoolean, PropertyProvider.propertyByte, PropertyProvider.propertySByte,
              PropertyProvider.propertyInt32, PropertyProvider.propertyInt64,
              PropertyProvider.propertySingle, PropertyProvider.propertyDouble, PropertyProvider.propertyDecimal,
              PropertyProvider.propertyBinary, PropertyProvider.propertyDate, PropertyProvider.propertyDateTimeOffset,
              PropertyProvider.propertyDuration, PropertyProvider.propertyGuid, PropertyProvider.propertyTimeOfDay
              /* TODO add propertyStream */))
          .setNavigationProperties(Arrays.asList(PropertyProvider.navPropertyETTwoPrimOne_ETTwoPrim,
              PropertyProvider.collectionNavPropertyETTwoPrimMany_ETTwoPrim));

    } else if (entityTypeName.equals(nameETCollAllPrim)) {
      return new EntityType()
          .setName("ETCollAllPrim")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))

          .setProperties(
              Arrays.asList(
                  PropertyProvider.propertyInt16_NotNullable, PropertyProvider.collPropertyString,
                  PropertyProvider.collPropertyBoolean, PropertyProvider.collPropertyByte,
                  PropertyProvider.collPropertySByte, PropertyProvider.collPropertyInt16,
                  PropertyProvider.collPropertyInt32, PropertyProvider.collPropertyInt64,
                  PropertyProvider.collPropertySingle, PropertyProvider.collPropertyDouble,
                  PropertyProvider.collPropertyDecimal, PropertyProvider.collPropertyBinary,
                  PropertyProvider.collPropertyDate, PropertyProvider.collPropertyDateTimeOffset,
                  PropertyProvider.collPropertyDuration, PropertyProvider.collPropertyGuid,
                  PropertyProvider.collPropertyTimeOfDay /* TODO add propertyStream */));

    } else if (entityTypeName.equals(nameETTwoPrim)) {
      return new EntityType()
          .setName("ETTwoPrim")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString))
          .setNavigationProperties(
              Arrays.asList(PropertyProvider.navPropertyETAllPrimOne_ETAllPrim,
                  PropertyProvider.collectionNavPropertyETAllPrimMany_ETAllPrim));

    } else if (entityTypeName.equals(nameETMixPrimCollComp)) {
      return new EntityType()
          .setName("ETMixPrimCollComp")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.collPropertyString,
              PropertyProvider.propertyComplex_CTTwoPrim, PropertyProvider.collPropertyComplex_CTTwoPrim));

    } else if (entityTypeName.equals(nameETTwoKeyTwoPrim)) {
      return new EntityType()
          .setName("ETTwoKeyTwoPrim")
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyInt16"),
              new PropertyRef().setPropertyName("PropertyString")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString));

    } else if (entityTypeName.equals(nameETBaseTwoKeyTwoPrim)) {
      return new EntityType()
          .setName("ETBaseTwoKeyTwoPrim")
          .setBaseType(nameETTwoKeyTwoPrim);

    } else if (entityTypeName.equals(nameETTwoBaseTwoKeyTwoPrim)) {
      return new EntityType()
          .setName("ETTwoBaseTwoKeyTwoPrim")
          .setBaseType(nameETTwoKeyTwoPrim);

    } else if (entityTypeName.equals(nameETBase)) {
      return new EntityType()
          .setName("ETBase")
          .setBaseType(nameETTwoPrim)
          .setProperties(Arrays.asList(new Property()
              .setName("AdditionalPropertyString_5")
              .setType(PropertyProvider.nameString)));

    } else if (entityTypeName.equals(nameETTwoBase)) {
      return new EntityType()
          .setName("ETTwoBase")
          .setBaseType(nameETBase)
          .setProperties(Arrays.asList(new Property()
              .setName("AdditionalPropertyString_6")
              .setType(PropertyProvider.nameString))
          );

    } else if (entityTypeName.equals(nameETAllKey)) {
      return new EntityType()
          .setName("ETAllKey")
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyString"),
              new PropertyRef().setPropertyName("PropertyBoolean"),
              new PropertyRef().setPropertyName("PropertyByte"),
              new PropertyRef().setPropertyName("PropertySByte"),
              new PropertyRef().setPropertyName("PropertyInt16"),
              new PropertyRef().setPropertyName("PropertyInt32"),
              new PropertyRef().setPropertyName("PropertyInt64"),
              new PropertyRef().setPropertyName("PropertyDecimal"),
              new PropertyRef().setPropertyName("PropertyDate"),
              new PropertyRef().setPropertyName("PropertyDateTimeOffset"),
              new PropertyRef().setPropertyName("PropertyDuration"),
              new PropertyRef().setPropertyName("PropertyGuid"),
              new PropertyRef().setPropertyName("PropertyTimeOfDay")))
          .setProperties(
              Arrays.asList(
                  PropertyProvider.propertyString_NotNullable, PropertyProvider.propertyBoolean_NotNullable,
                  PropertyProvider.propertyByte_NotNullable, PropertyProvider.propertySByte_NotNullable,
                  PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyInt32_NotNullable,
                  PropertyProvider.propertyInt64_NotNullable,
                  PropertyProvider.propertyDecimal_NotNullable, PropertyProvider.propertyDate_NotNullable,
                  PropertyProvider.propertyDateTimeOffset_NotNullable,
                  PropertyProvider.propertyDuration_NotNullable, PropertyProvider.propertyGuid_NotNullable,
                  PropertyProvider.propertyTimeOfDay_NotNullable /* TODO add propertyStream */));

    } else if (entityTypeName.equals(nameETCompAllPrim)) {
      return new EntityType()
          .setName("ETCompAllPrim")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(
              Arrays.asList(PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyComplex_CTAllPrim));

    } else if (entityTypeName.equals(nameETCompCollAllPrim)) {
      return new EntityType()
          .setName("ETCompCollAllPrim")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))

          .setProperties(
              Arrays.asList(PropertyProvider.propertyInt16_NotNullable,
                  PropertyProvider.propertyComplex_CTCollAllPrim));

    } else if (entityTypeName.equals(nameETCompComp)) {
      return new EntityType()
          .setName("ETCompComp")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(
              Arrays.asList(PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyComplex_CTCompComp));

    } else if (entityTypeName.equals(nameETCompCollComp)) {
      return new EntityType()
          .setName("ETCompCollComp")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(
              Arrays
                  .asList(PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyComplex_CTCompCollComp));

    } else if (entityTypeName.equals(nameETMedia)) {
      return new EntityType()
          .setName("ETMedia")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16_NotNullable))
          .setHasStream(true);

    } else if (entityTypeName.equals(nameETKeyTwoKeyComp)) {
      return new EntityType()
          .setName("ETKeyTwoKeyComp")
          .setKey(Arrays.asList(
              new PropertyRef()
                  .setPropertyName("PropertyInt16"),
              new PropertyRef()
                  .setPropertyName("PropertyComplex/PropertyInt16")
                  .setAlias("KeyAlias1"),
              new PropertyRef()
                  .setPropertyName("PropertyComplex/PropertyString")
                  .setAlias("KeyAlias2"),
              new PropertyRef()
                  .setPropertyName("PropertyComplexComplex/PropertyComplex/PropertyString")
                  .setAlias("KeyAlias3")))
          .setProperties(
              Arrays.asList(
                  PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyComplex_CTTwoPrim,
                  PropertyProvider.propertyComplexComplex_CTCompComp));

    } else if (entityTypeName.equals(nameETServerSidePaging)) {
      return new EntityType()
          .setName(nameETServerSidePaging.getName())
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16_NotNullable,
              PropertyProvider.propertyString_NotNullable));

    } else if (entityTypeName.equals(nameETAllNullable)) {
      return new EntityType()
          .setName("ETAllNullable")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyKey")))
          .setProperties(
              Arrays.asList(
                  new Property()
                      .setName("PropertyKey").setType(PropertyProvider.nameInt16).setNullable(false),
                  PropertyProvider.propertyInt16_ExplicitNullable, PropertyProvider.propertyString_ExplicitNullable,
                  PropertyProvider.propertyBoolean_ExplicitNullable, PropertyProvider.propertyByte_ExplicitNullable,
                  PropertyProvider.propertySByte_ExplicitNullable, PropertyProvider.propertyInt32_ExplicitNullable,
                  PropertyProvider.propertyInt64_ExplicitNullable, PropertyProvider.propertySingle_ExplicitNullable,
                  PropertyProvider.propertyDouble_ExplicitNullable, PropertyProvider.propertyDecimal_ExplicitNullable,
                  PropertyProvider.propertyBinary_ExplicitNullable, PropertyProvider.propertyDate_ExplicitNullable,
                  PropertyProvider.propertyDateTimeOffset_ExplicitNullable,
                  PropertyProvider.propertyDuration_ExplicitNullable, PropertyProvider.propertyGuid_ExplicitNullable,
                  PropertyProvider.propertyTimeOfDay_ExplicitNullable /* TODO add propertyStream */,
                  PropertyProvider.collPropertyString_ExplicitNullable,
                  PropertyProvider.collPropertyBoolean_ExplicitNullable,
                  PropertyProvider.collPropertyByte_ExplicitNullable,
                  PropertyProvider.collPropertySByte_ExplicitNullable,
                  PropertyProvider.collPropertyInt16_ExplicitNullable,
                  PropertyProvider.collPropertyInt32_ExplicitNullable,
                  PropertyProvider.collPropertyInt64_ExplicitNullable,
                  PropertyProvider.collPropertySingle_ExplicitNullable,
                  PropertyProvider.collPropertyDouble_ExplicitNullable,
                  PropertyProvider.collPropertyDecimal_ExplicitNullable,
                  PropertyProvider.collPropertyBinary_ExplicitNullable,
                  PropertyProvider.collPropertyDate_ExplicitNullable,
                  PropertyProvider.collPropertyDateTimeOffset_ExplicitNullable,
                  PropertyProvider.collPropertyDuration_ExplicitNullable,
                  PropertyProvider.collPropertyGuid_ExplicitNullable,
                  PropertyProvider.collPropertyTimeOfDay_ExplicitNullable /* TODO add propertyStream */));

    } else if (entityTypeName.equals(nameETKeyNav)) {
      return new EntityType()
          .setName("ETKeyNav")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(
              Arrays.asList(
                  PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString_NotNullable,
                  PropertyProvider.propertyComplex_CTNavFiveProp,
                  PropertyProvider.propertyComplexAllPrim_CTAllPrim, PropertyProvider.propertyComplexTwoPrim_CTTwoPrim,
                  PropertyProvider.collPropertyString, PropertyProvider.collPropertyInt16,
                  PropertyProvider.collPropertyComplex_CTPrimComp,
                  new Property()
                      .setName("PropertyComplexComplex").setType(ComplexTypeProvider.nameCTCompNav)
                  ))
          .setNavigationProperties(
              Arrays.asList(
                  PropertyProvider.navPropertyETTwoKeyNavOne_ETTwoKeyNav_NotNullable,
                  PropertyProvider.collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
                  PropertyProvider.navPropertyETKeyNavOne_ETKeyNav,
                  PropertyProvider.collectionNavPropertyETKeyNavMany_ETKeyNav,
                  PropertyProvider.navPropertyETMediaOne_ETMedia,
                  PropertyProvider.collectionNavPropertyETMediaMany_ETMedia
                  ));
    } else if (entityTypeName.equals(nameETKeyPrimNav)) {
      return new EntityType()
          .setName("ETKeyPrimNav")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString_ExplicitNullable))
          .setNavigationProperties(
              Arrays.asList(
                  PropertyProvider.navPropertyETKeyPrimNavOne_ETKeyPrimNav));

    } else if (entityTypeName.equals(nameETTwoKeyNav)) {
      return new EntityType()
          .setName("ETTwoKeyNav")
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyInt16"),
              new PropertyRef().setPropertyName("PropertyString")))
          .setProperties(
              Arrays.asList(
                  PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString_NotNullable,
                  PropertyProvider.propertyComplex_CTPrimComp_NotNullable,
                  new Property().setName("PropertyComplexNav").setType(ComplexTypeProvider.nameCTBasePrimCompNav)
                      .setNullable(false),
                  PropertyProvider.propertyComplexEnum_CTPrimEnum_NotNullable,
                  PropertyProvider.collPropertyComplex_CTPrimComp,
                  new Property().setName("CollPropertyComplexNav").setType(ComplexTypeProvider.nameCTNavFiveProp)
                      .setCollection(true),
                  PropertyProvider.collPropertyString, PropertyProvider.propertyComplexTwoPrim_CTTwoPrim,
                  PropertyProvider.propertyEnumString_ENString
                  ))
          .setNavigationProperties(Arrays.asList(
              new NavigationProperty()
                  .setName("NavPropertyETKeyNavOne")
                  .setType(nameETKeyNav)
                  .setReferentialConstraints(Arrays.asList(
                      new ReferentialConstraint()
                          .setProperty("PropertyInt16")
                          .setReferencedProperty("PropertyInt16"))),
              PropertyProvider.collectionNavPropertyETKeyNavMany_ETKeyNav,
              PropertyProvider.navPropertyETTwoKeyNavOne_ETTwoKeyNav,
              PropertyProvider.collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav));

    } else if (entityTypeName.equals(nameETBaseTwoKeyNav)) {
      return new EntityType()
          .setName("ETBaseTwoKeyNav")
          .setBaseType(nameETTwoKeyNav)
          .setProperties(Arrays.asList(PropertyProvider.propertyDate_ExplicitNullable))
          .setNavigationProperties(Arrays.asList(
              new NavigationProperty()
                  .setName("NavPropertyETBaseTwoKeyNavOne")
                  .setType(nameETBaseTwoKeyNav),
              new NavigationProperty()
                  .setName("NavPropertyETTwoBaseTwoKeyNavOne")
                  .setType(nameETTwoBaseTwoKeyNav)));

    } else if (entityTypeName.equals(nameETTwoBaseTwoKeyNav)) {
      return new EntityType()
          .setName("ETTwoBaseTwoKeyNav")
          .setBaseType(nameETBaseTwoKeyNav)
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(PropertyProvider.propertyGuid_ExplicitNullable))
          .setNavigationProperties(Arrays.asList(
              new NavigationProperty()
                  .setName("NavPropertyETBaseTwoKeyNavMany")
                  .setType(nameETBaseTwoKeyNav)
                  .setCollection(true)
              ));

    } else if (entityTypeName.equals(nameETFourKeyAlias)) {
      return new EntityType()
          .setName("ETFourKeyAlias")
          .setKey(
              Arrays.asList(
                  new PropertyRef().setPropertyName("PropertyInt16"),
                  new PropertyRef().setPath("PropertyComplex/PropertyInt16").setPropertyName("PropertyInt16").setAlias(
                      "KeyAlias1"),
                  new PropertyRef().setPath("PropertyComplex/PropertyString").setPropertyName("PropertyString")
                      .setAlias("KeyAlias2"),
                  new PropertyRef().setPath("PropertyComplexComplex/PropertyComplex/PropertyString").setPropertyName(
                      "PropertyString").setAlias("KeyAlias3"))).setProperties(
              Arrays.asList(PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyComplex_CTTwoPrim,
                  PropertyProvider.propertyComplexComplex_CTCompComp));
    } else if (entityTypeName.equals(nameETCompMixPrimCollComp)) {
      return new EntityType()
          .setName("ETCompMixPrimCollComp")
          .setKey(Arrays.asList(
              new PropertyRef()
                  .setPropertyName("PropertyInt16")))
          .setProperties(
              Arrays.asList(PropertyProvider.propertyInt16_NotNullable,
                  PropertyProvider.propertyMixedPrimCollComp_CTMixPrimCollComp));
    }

    return null;
  }
}
