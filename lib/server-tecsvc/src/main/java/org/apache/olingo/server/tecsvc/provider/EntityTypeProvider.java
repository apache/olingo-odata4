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

import java.util.Arrays;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlReferentialConstraint;

public class EntityTypeProvider {

  public static final FullQualifiedName nameETAllKey = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETAllKey");
  public static final FullQualifiedName nameETAllNullable = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETAllNullable");
  public static final FullQualifiedName nameETDeriveCollComp = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETDeriveCollComp");
  public static final FullQualifiedName nameETAllPrim = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETAllPrim");
  public static final FullQualifiedName nameETAllPrimDefaultValues = new FullQualifiedName(SchemaProvider.NAMESPACE, 
         "ETAllPrimDefaultValues");
  public static final FullQualifiedName nameETBase = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETBase");
  public static final FullQualifiedName nameETBaseTwoKeyNav = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETBaseTwoKeyNav");
  public static final FullQualifiedName nameETBaseTwoKeyTwoPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "ETBaseTwoKeyTwoPrim");
  public static final FullQualifiedName nameETCollAllPrim = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETCollAllPrim");
  public static final FullQualifiedName nameETCompAllPrim = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETCompAllPrim");
  public static final FullQualifiedName nameETCompCollAllPrim = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETCompCollAllPrim");
  public static final FullQualifiedName nameETCompCollComp = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETCompCollComp");
  public static final FullQualifiedName nameETCompComp = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETCompComp");
  public static final FullQualifiedName nameETCompMixPrimCollComp =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "ETCompMixPrimCollComp");
  public static final FullQualifiedName nameETFourKeyAlias = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETFourKeyAlias");
  public static final FullQualifiedName nameETKeyNav = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETKeyNav");
  public static final FullQualifiedName nameETKeyPrimNav = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETKeyPrimNav");
  public static final FullQualifiedName nameETKeyPrimNavDerived = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETKeyPrimNavDerived");
  public static final FullQualifiedName nameETKeyNavCont = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETKeyNavCont");
  public static final FullQualifiedName nameETTwoKeyNavCont = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETTwoKeyNavCont");
  public static final FullQualifiedName nameETMedia = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETMedia");
  public static final FullQualifiedName nameETMixPrimCollComp = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETMixPrimCollComp");
  public static final FullQualifiedName nameETServerSidePaging =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "ETServerSidePaging");
  public static final FullQualifiedName nameETStreamServerSidePaging =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "ETStreamServerSidePaging");
  public static final FullQualifiedName nameETTwoBase = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETTwoBase");
  public static final FullQualifiedName nameETTwoBaseTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "ETTwoBaseTwoKeyNav");
  public static final FullQualifiedName nameETTwoBaseTwoKeyTwoPrim =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "ETTwoBaseTwoKeyTwoPrim");
  public static final FullQualifiedName nameETTwoKeyNav =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "ETTwoKeyNav");
  public static final FullQualifiedName nameETTwoKeyTwoPrim = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETTwoKeyTwoPrim");
  public static final FullQualifiedName nameETTwoPrim = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETTwoPrim");

  public static final FullQualifiedName nameETMixEnumDefCollComp = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETMixEnumDefCollComp");
  public static final FullQualifiedName nameETStream = new FullQualifiedName(SchemaProvider.NAMESPACE,
      "ETWithStream");
  public static final FullQualifiedName nameETDelta =
      new FullQualifiedName(SchemaProvider.NAMESPACE, "ETDelta"); 
  
  public static final FullQualifiedName nameETPeople = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETPeople");
  
 public static final FullQualifiedName nameETCont = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETCont");
  
  public static final FullQualifiedName nameETBaseCont = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETBaseCont");
  
  public static final FullQualifiedName nameETTwoCont = new FullQualifiedName(SchemaProvider.NAMESPACE, "ETTwoCont");
  
  public static final FullQualifiedName nameETStreamOnComplexProp = 
      new FullQualifiedName(SchemaProvider.NAMESPACE, "ETStreamOnComplexProp");
  
  public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
    if(entityTypeName.equals(nameETAllPrimDefaultValues)){        
        return new CsdlEntityType()
          .setName("ETAllPrimDefaultValues")
          .setKey(Arrays.asList(
              new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable_WithDefaultValue, 
              PropertyProvider.propertyString_NotNullable_WithDefaultValue,
              PropertyProvider.propertyBoolean_NotNullable_WithDefaultValue, 
              PropertyProvider.propertyByte_NotNullable_WithDefaultValue, 
              PropertyProvider.propertySByte_NotNullable_WithDefaultValue, 
              PropertyProvider.propertyInt32_NotNullable_WithDefaultValue, 
              PropertyProvider.propertyInt64_NotNullable_WithDefaultValue, 
              PropertyProvider.propertySingle_NotNullable_WithDefaultValue, 
              PropertyProvider.propertyDouble_NotNullable_WithDefaultValue, 
              PropertyProvider.propertyDecimal_Scale_NotNullable_WithDefaultValue,
              PropertyProvider.propertyBinary_NotNullable_WithDefaultValue, 
              PropertyProvider.propertyDate_NotNullable_WithDefaultValue, 
              PropertyProvider.propertyDateTimeOffset_NotNullable_WithDefaultValue, 
              PropertyProvider.propertyDuration_NotNullable_WithDefaultValue, 
              PropertyProvider.propertyGuid_NotNullable_WithDefaultValue, 
              PropertyProvider.propertyTimeOfDay_NotNullable_WithDefaultValue,
              PropertyProvider.propertyEnumString_ENString_NonNullable_WithDefaultValue, 
              PropertyProvider.propertyTypeDefinition_TDString_NonNullable_WithDefaultValue
              ));
        
    }else if (entityTypeName.equals(nameETAllPrim)) {
      return new CsdlEntityType()
          .setName("ETAllPrim")
          .setKey(Arrays.asList(
              new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString,
              PropertyProvider.propertyBoolean, PropertyProvider.propertyByte, PropertyProvider.propertySByte,
              PropertyProvider.propertyInt32, PropertyProvider.propertyInt64,
              PropertyProvider.propertySingle, PropertyProvider.propertyDouble, PropertyProvider.propertyDecimal_Scale,
              PropertyProvider.propertyBinary, PropertyProvider.propertyDate, PropertyProvider.propertyDateTimeOffset,
              PropertyProvider.propertyDuration, PropertyProvider.propertyGuid, PropertyProvider.propertyTimeOfDay
              ))
          .setNavigationProperties(Arrays.asList(PropertyProvider.navPropertyETTwoPrimOne_ETTwoPrim,
              PropertyProvider.collectionNavPropertyETTwoPrimMany_ETTwoPrim));

    } else if (entityTypeName.equals(nameETCollAllPrim)) {
      return new CsdlEntityType()
          .setName("ETCollAllPrim")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))

          .setProperties(
              Arrays.asList(
                  PropertyProvider.propertyInt16_NotNullable,
                  PropertyProvider.collPropertyString_NotNullable,
                  PropertyProvider.collPropertyBoolean, PropertyProvider.collPropertyByte,
                  PropertyProvider.collPropertySByte,
                  PropertyProvider.collPropertyInt16_NotNullable,
                  PropertyProvider.collPropertyInt32, PropertyProvider.collPropertyInt64,
                  PropertyProvider.collPropertySingle, PropertyProvider.collPropertyDouble,
                  PropertyProvider.collPropertyDecimal, PropertyProvider.collPropertyBinary,
                  PropertyProvider.collPropertyDate_NotNullable,
                  PropertyProvider.collPropertyDateTimeOffset_NotNullable,
                  PropertyProvider.collPropertyDuration_NotNullable,
                  PropertyProvider.collPropertyGuid, PropertyProvider.collPropertyTimeOfDay
                  ));

    } else if (entityTypeName.equals(nameETTwoPrim)) {
      return new CsdlEntityType()
          .setName("ETTwoPrim")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString))
          .setNavigationProperties(
              Arrays.asList(PropertyProvider.navPropertyETAllPrimOne_ETAllPrim,
                  PropertyProvider.collectionNavPropertyETAllPrimMany_ETAllPrim));

    } else if (entityTypeName.equals(nameETMixPrimCollComp)) {
      return new CsdlEntityType()
          .setName("ETMixPrimCollComp")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.collPropertyString,
              PropertyProvider.propertyComp_CTTwoPrim, PropertyProvider.collPropertyComp_CTTwoPrim));

    } else if (entityTypeName.equals(nameETDeriveCollComp)) {
      return new CsdlEntityType()
          .setName("ETDeriveCollComp")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(
             PropertyProvider.propertyInt16_NotNullable,
             PropertyProvider.propertyComp_CTTwoPrim_Ano,
             PropertyProvider.collPropertyComp_CTTwoPrim_Ano));

    } else if (entityTypeName.equals(nameETTwoKeyTwoPrim)) {
      return new CsdlEntityType()
          .setName("ETTwoKeyTwoPrim")
          .setKey(Arrays.asList(
              new CsdlPropertyRef().setName("PropertyInt16"),
              new CsdlPropertyRef().setName("PropertyString")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString_NotNullable));

    } else if (entityTypeName.equals(nameETBaseTwoKeyTwoPrim)) {
      return new CsdlEntityType()
          .setName("ETBaseTwoKeyTwoPrim")
          .setBaseType(nameETTwoKeyTwoPrim);

    } else if (entityTypeName.equals(nameETTwoBaseTwoKeyTwoPrim)) {
      return new CsdlEntityType()
          .setName("ETTwoBaseTwoKeyTwoPrim")
          .setBaseType(nameETTwoKeyTwoPrim);

    } else if (entityTypeName.equals(nameETBase)) {
      return new CsdlEntityType()
          .setName("ETBase")
          .setBaseType(nameETTwoPrim)
          .setProperties(Arrays.asList(new CsdlProperty()
              .setName("AdditionalPropertyString_5")
              .setType(PropertyProvider.nameString)));

    } else if (entityTypeName.equals(nameETTwoBase)) {
      return new CsdlEntityType()
          .setName("ETTwoBase")
          .setBaseType(nameETBase)
          .setProperties(Arrays.asList(new CsdlProperty()
              .setName("AdditionalPropertyString_6")
              .setType(PropertyProvider.nameString))
          );

    } else if (entityTypeName.equals(nameETAllKey)) {
      return new CsdlEntityType()
          .setName("ETAllKey")
          .setKey(Arrays.asList(
              new CsdlPropertyRef().setName("PropertyString"),
              new CsdlPropertyRef().setName("PropertyBoolean"),
              new CsdlPropertyRef().setName("PropertyByte"),
              new CsdlPropertyRef().setName("PropertySByte"),
              new CsdlPropertyRef().setName("PropertyInt16"),
              new CsdlPropertyRef().setName("PropertyInt32"),
              new CsdlPropertyRef().setName("PropertyInt64"),
              new CsdlPropertyRef().setName("PropertyDecimal"),
              new CsdlPropertyRef().setName("PropertyDate"),
              new CsdlPropertyRef().setName("PropertyDateTimeOffset"),
              new CsdlPropertyRef().setName("PropertyDuration"),
              new CsdlPropertyRef().setName("PropertyGuid"),
              new CsdlPropertyRef().setName("PropertyTimeOfDay")))
          .setProperties(
              Arrays.asList(
                  PropertyProvider.propertyString_NotNullable, PropertyProvider.propertyBoolean_NotNullable,
                  PropertyProvider.propertyByte_NotNullable, PropertyProvider.propertySByte_NotNullable,
                  PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyInt32_NotNullable,
                  PropertyProvider.propertyInt64_NotNullable,
                  PropertyProvider.propertyDecimal_NotNullable, PropertyProvider.propertyDate_NotNullable,
                  PropertyProvider.propertyDateTimeOffset_NotNullable,
                  PropertyProvider.propertyDuration_NotNullable, PropertyProvider.propertyGuid_NotNullable,
                  PropertyProvider.propertyTimeOfDay_NotNullable));

    } else if (entityTypeName.equals(nameETCompAllPrim)) {
      return new CsdlEntityType()
          .setName("ETCompAllPrim")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(
              Arrays.asList(PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyComp_CTAllPrim));

    } else if (entityTypeName.equals(nameETCompCollAllPrim)) {
      return new CsdlEntityType()
          .setName("ETCompCollAllPrim")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))

          .setProperties(
              Arrays.asList(PropertyProvider.propertyInt16_NotNullable,
                  PropertyProvider.propertyComp_CTCollAllPrim));

    } else if (entityTypeName.equals(nameETCompComp)) {
      return new CsdlEntityType()
          .setName("ETCompComp")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(
              Arrays.asList(PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyComp_CTCompComp));

    } else if (entityTypeName.equals(nameETCompCollComp)) {
      return new CsdlEntityType()
          .setName("ETCompCollComp")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable,
              PropertyProvider.propertyComp_CTCompCollComp));

    } else if (entityTypeName.equals(nameETMedia)) {
      return new CsdlEntityType()
          .setName("ETMedia")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16_NotNullable))
          .setHasStream(true);

    } else if (entityTypeName.equals(nameETServerSidePaging)) {
      return new CsdlEntityType()
          .setName(nameETServerSidePaging.getName())
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(PropertyProvider.propertyInt16_NotNullable,
              PropertyProvider.propertyString_NotNullable));

    }else if (entityTypeName.equals(nameETStreamServerSidePaging)) {
      return new CsdlEntityType()
          .setName(nameETStreamServerSidePaging.getName())
          .setKey(Arrays.asList(
              new CsdlPropertyRef()
                  .setName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable,
              PropertyProvider.propertyStream));     
    } else if (entityTypeName.equals(nameETAllNullable)) {
      return new CsdlEntityType()
          .setName("ETAllNullable")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyKey")))
          .setProperties(
              Arrays.asList(
                  new CsdlProperty()
                      .setName("PropertyKey").setType(PropertyProvider.nameInt16).setNullable(false),
                  PropertyProvider.propertyInt16_ExplicitNullable, PropertyProvider.propertyString_ExplicitNullable,
                  PropertyProvider.propertyBoolean_ExplicitNullable, PropertyProvider.propertyByte_ExplicitNullable,
                  PropertyProvider.propertySByte_ExplicitNullable, PropertyProvider.propertyInt32_ExplicitNullable,
                  PropertyProvider.propertyInt64_ExplicitNullable, PropertyProvider.propertySingle_ExplicitNullable,
                  PropertyProvider.propertyDouble_ExplicitNullable, PropertyProvider.propertyDecimal_ExplicitNullable,
                  PropertyProvider.propertyBinary_ExplicitNullable, PropertyProvider.propertyDate_ExplicitNullable,
                  PropertyProvider.propertyDateTimeOffset_ExplicitNullable,
                  PropertyProvider.propertyDuration_ExplicitNullable, PropertyProvider.propertyGuid_ExplicitNullable,
                  PropertyProvider.propertyTimeOfDay_ExplicitNullable,
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
                  PropertyProvider.collPropertyTimeOfDay_ExplicitNullable));

    } else if (entityTypeName.equals(nameETKeyNav)) {
      return new CsdlEntityType()
          .setName("ETKeyNav")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(
              Arrays.asList(
                  PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString_NotNullable,
                  PropertyProvider.propertyCompNav_CTNavFiveProp,
                  PropertyProvider.propertyCompAllPrim_CTAllPrim, PropertyProvider.propertyCompTwoPrim_CTTwoPrim,
                  PropertyProvider.collPropertyString, PropertyProvider.collPropertyInt16,
                  PropertyProvider.collPropertyComp_CTPrimComp,
                  new CsdlProperty()
                      .setName("PropertyCompCompNav").setType(ComplexTypeProvider.nameCTCompNav)
                  ))
          .setNavigationProperties(
              Arrays.asList(
                  PropertyProvider.navPropertyETTwoKeyNavOne_ETTwoKeyNav_NotNullable,
                  PropertyProvider.collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav_WithPartnerERKeyNavOne,
                  PropertyProvider.navPropertyETKeyNavOne_ETKeyNav,
                  PropertyProvider.collectionNavPropertyETKeyNavMany_ETKeyNav,
                  PropertyProvider.navPropertyETMediaOne_ETMedia,
                  PropertyProvider.collectionNavPropertyETMediaMany_ETMedia));
    } else if (entityTypeName.equals(nameETKeyPrimNav)) {
      return new CsdlEntityType()
          .setName("ETKeyPrimNav")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString_ExplicitNullable))
          .setNavigationProperties(
              Arrays.asList(
                  PropertyProvider.navPropertyETKeyPrimNavOne_ETKeyPrimNav));
    } else if (entityTypeName.equals(nameETKeyPrimNavDerived)) {
      return new CsdlEntityType()
      .setName("ETKeyPrimNavDerived")
      .setBaseType(nameETKeyPrimNav)
      .setProperties(Arrays.asList(PropertyProvider.propertyBoolean_NotNullable));
    } else if (entityTypeName.equals(nameETKeyNavCont)) {
      return new CsdlEntityType()
          .setName("ETKeyNavCont")
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString_NotNullable,
              PropertyProvider.propertyCompNavCont))
          .setNavigationProperties(Arrays.asList(
              PropertyProvider.navPropertyETTwoKeyNavOneCT_ETTwoKeyNav,
              PropertyProvider.collectionNavPropertyETTwoKeyNavMany_CT_ETTwoKeyNav,
              PropertyProvider.navPropertyETTwoKeyNavContOne_ETTwoKeyNav,
              PropertyProvider.collectionNavPropertyETTwoKeyNavContMany_ETTwoKeyNav,
              PropertyProvider.navPropertyETTwoKeyNavContOne_ETCont,
              PropertyProvider.collectionNavPropertyETTwoKeyNavContMany_ETCont,
              PropertyProvider.collectionNavPropertyETTwoKeyNavContMany_ETBaseCont
              ));

    } else if (entityTypeName.equals(nameETTwoKeyNavCont)) {
      return new CsdlEntityType()
      .setName("ETTwoKeyNavCont")
      .setKey(Arrays.asList(
          new CsdlPropertyRef().setName("PropertyInt16"),
          new CsdlPropertyRef().setName("PropertyString")))
      .setProperties(Arrays.asList(
          PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString_NotNullable))
      .setNavigationProperties(Arrays.asList(
          PropertyProvider.navPropertyETTwoKeyNavContOneCT_ETTwoKeyNav,
          PropertyProvider.collectionNavPropertyETTwoKeyNavContMany_CT_ETTwoKeyNav
          ));
    } else if (entityTypeName.equals(nameETTwoKeyNav)) {
      return new CsdlEntityType()
          .setName("ETTwoKeyNav")
          .setKey(Arrays.asList(
              new CsdlPropertyRef().setName("PropertyInt16"),
              new CsdlPropertyRef().setName("PropertyString")))
          .setProperties(
              Arrays.asList(
                  PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString_NotNullable,
                  PropertyProvider.propertyComp_CTPrimComp_NotNullable,
                  new CsdlProperty().setName("PropertyCompNav").setType(ComplexTypeProvider.nameCTBasePrimCompNav)
                      .setNullable(false),
                  PropertyProvider.collPropertyComp_CTPrimComp,
                  new CsdlProperty().setName("CollPropertyCompNav").setType(ComplexTypeProvider.nameCTNavFiveProp)
                      .setCollection(true),
                  PropertyProvider.collPropertyString, PropertyProvider.propertyCompTwoPrim_CTTwoPrim
                  ))
          .setNavigationProperties(Arrays.asList(
              new CsdlNavigationProperty()
                  .setName("NavPropertyETKeyNavOne")
                  .setType(nameETKeyNav)
                  .setReferentialConstraints(Arrays.asList(
                      new CsdlReferentialConstraint()
                          .setProperty("PropertyInt16")
                          .setReferencedProperty("PropertyInt16"))),
              PropertyProvider.collectionNavPropertyETKeyNavMany_ETKeyNav,
              PropertyProvider.navPropertyETTwoKeyNavOne_ETTwoKeyNav,
              PropertyProvider.collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              PropertyProvider.navPropertySINav));

    } else if (entityTypeName.equals(nameETBaseTwoKeyNav)) {
      return new CsdlEntityType()
          .setName("ETBaseTwoKeyNav")
          .setBaseType(nameETTwoKeyNav)
          .setProperties(Arrays.asList(PropertyProvider.propertyDate_ExplicitNullable))
          .setNavigationProperties(Arrays.asList(
              new CsdlNavigationProperty()
                  .setName("NavPropertyETBaseTwoKeyNavOne")
                  .setType(nameETBaseTwoKeyNav),
              new CsdlNavigationProperty()
                  .setName("NavPropertyETTwoBaseTwoKeyNavOne")
                  .setType(nameETTwoBaseTwoKeyNav)));

    } else if (entityTypeName.equals(nameETTwoBaseTwoKeyNav)) {
      return new CsdlEntityType()
          .setName("ETTwoBaseTwoKeyNav")
          .setBaseType(nameETBaseTwoKeyNav)
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(PropertyProvider.propertyGuid_ExplicitNullable))
          .setNavigationProperties(Arrays.asList(
              new CsdlNavigationProperty()
                  .setName("NavPropertyETBaseTwoKeyNavMany")
                  .setType(nameETBaseTwoKeyNav)
                  .setCollection(true)
              ));

    } else if (entityTypeName.equals(nameETFourKeyAlias)) {
      return new CsdlEntityType()
          .setName("ETFourKeyAlias")
          .setKey(
              Arrays.asList(
                  new CsdlPropertyRef().setName("PropertyInt16"),
                  new CsdlPropertyRef().setName("PropertyComp/PropertyInt16").setAlias("KeyAlias1"),
                  new CsdlPropertyRef().setName("PropertyComp/PropertyString").setAlias("KeyAlias2"),
                  new CsdlPropertyRef().setName("PropertyCompComp/PropertyComp/PropertyString").setAlias("KeyAlias3")))
          .setProperties(
              Arrays.asList(PropertyProvider.propertyInt16_NotNullable,
                  PropertyProvider.propertyComp_CTTwoPrim_NotNullable,
                  PropertyProvider.propertyCompComp_CTCompComp_NotNullable));
    } else if (entityTypeName.equals(nameETCompMixPrimCollComp)) {
      return new CsdlEntityType()
          .setName("ETCompMixPrimCollComp")
          .setKey(Arrays.asList(
              new CsdlPropertyRef()
                  .setName("PropertyInt16")))
          .setProperties(
              Arrays.asList(PropertyProvider.propertyInt16_NotNullable,
                  PropertyProvider.propertyMixedPrimCollComp_CTMixPrimCollComp));

    } else if (entityTypeName.equals(nameETMixEnumDefCollComp)) {
      return new CsdlEntityType()
          .setName(nameETMixEnumDefCollComp.getName())
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyEnumString"),
              new CsdlPropertyRef().setName("PropertyDefString")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyEnumString_ENString_NonNullable,
              PropertyProvider.collPropertyEnumString_ENString,
              PropertyProvider.propertyTypeDefinition_TDString_NonNullable,
              PropertyProvider.collPropertyTypeDefinition_TDString,
              PropertyProvider.propertyComp_CTMixEnumTypeDefColl,
              PropertyProvider.propertyCompColl_CTMixEnumTypeDefColl));
    } else if (entityTypeName.equals(nameETStream)) {
      return new CsdlEntityType()
          .setName(nameETStream.getName())
          .setKey(Arrays.asList(
              new CsdlPropertyRef()
                  .setName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable,
              PropertyProvider.propertyStream));      
    } else if (entityTypeName.equals(nameETPeople)) {
        return new CsdlEntityType()
          .setName(nameETPeople.getName())
          .setKey(Arrays.asList(new CsdlPropertyRef().setName("id")))
          .setProperties(Arrays.asList(
            PropertyProvider.propertyId,
            PropertyProvider.propertyName))
          .setNavigationProperties(Arrays.asList(PropertyProvider.navPropertyFriends));
    } else if (entityTypeName.equals(nameETDelta)) {
      return new CsdlEntityType()
          .setName("ETDelta")
          .setBaseType(nameETTwoPrim)
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString))
          .setNavigationProperties(
              Arrays.asList(PropertyProvider.navPropertyETAllPrimOne_ETAllPrim,
                  PropertyProvider.collectionNavPropertyETAllPrimMany_ETAllPrim));
    } else if (entityTypeName.equals(nameETCont)) {
      return new CsdlEntityType()
          .setName("ETCont").setBaseType(nameETBaseCont)
          .setProperties(Arrays.asList(
              PropertyProvider.propertyBoolean, PropertyProvider.propertyByte, PropertyProvider.propertySByte
              ))
          .setNavigationProperties(Arrays.asList(PropertyProvider.navPropertyETCont_ETTwoPrim,
              PropertyProvider.collectionNavPropertyETContMany_ETTwoPrim));
    } else if (entityTypeName.equals(nameETBaseCont)) {
      return new CsdlEntityType()
          .setName("ETBaseCont")
          .setKey(Arrays.asList(
              new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString,
              PropertyProvider.propertyInt32, PropertyProvider.propertyInt64,
              PropertyProvider.propertySingle, PropertyProvider.propertyDouble, PropertyProvider.propertyDecimal_Scale,
              PropertyProvider.propertyBinary, PropertyProvider.propertyDate, PropertyProvider.propertyDateTimeOffset,
              PropertyProvider.propertyDuration, PropertyProvider.propertyGuid, PropertyProvider.propertyTimeOfDay
              ))
          .setNavigationProperties(Arrays.asList(PropertyProvider.navPropertyETBaseCont_ETTwoPrim,
              PropertyProvider.collectionNavPropertyETBaseContMany_ETTwoPrim,
              PropertyProvider.collectionNavPropertyETBaseContMany_ETTwoCont,
              PropertyProvider.navPropertyETBaseCont_ETTwoCont));
    } else if (entityTypeName.equals(nameETTwoCont)) {
      return new CsdlEntityType()
          .setName("ETTwoCont")
          .setKey(Arrays.asList(
              new CsdlPropertyRef().setName("PropertyInt16"),
              new CsdlPropertyRef().setName("PropertyString")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, PropertyProvider.propertyString_NotNullable,
              PropertyProvider.propertyInt32, PropertyProvider.propertyInt64,
              PropertyProvider.propertySingle, PropertyProvider.propertyDouble, PropertyProvider.propertyDecimal_Scale,
              PropertyProvider.propertyBinary, PropertyProvider.propertyDate, PropertyProvider.propertyDateTimeOffset,
              PropertyProvider.propertyDuration, PropertyProvider.propertyGuid, PropertyProvider.propertyTimeOfDay
              ));
    } else if (entityTypeName.equals(nameETStreamOnComplexProp)) {
      return new CsdlEntityType()
          .setName("ETStreamOnComplexProp")
          .setKey(Arrays.asList(
              new CsdlPropertyRef().setName("PropertyInt16")))
          .setProperties(Arrays.asList(
              PropertyProvider.propertyInt16_NotNullable, 
              PropertyProvider.propertyInt32, PropertyProvider.propertyEntityStream,
              PropertyProvider.propertyCompWithStream_CTWithStreamProp
              ));
    }
    return null;
  }
}
