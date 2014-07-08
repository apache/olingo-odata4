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

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.edm.provider.NavigationProperty;
import org.apache.olingo.server.api.edm.provider.Property;

public class PropertyProvider {

  // Primitive Type Names
  public static final FullQualifiedName nameBinary = EdmPrimitiveTypeKind.Binary.getFullQualifiedName();
  public static final FullQualifiedName nameBoolean = EdmPrimitiveTypeKind.Boolean.getFullQualifiedName();
  public static final FullQualifiedName nameByte = EdmPrimitiveTypeKind.Byte.getFullQualifiedName();

  public static final FullQualifiedName nameDate = EdmPrimitiveTypeKind.Date.getFullQualifiedName();
  public static final FullQualifiedName nameDateTimeOffset =
      EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName();

  public static final FullQualifiedName nameDecimal = EdmPrimitiveTypeKind.Decimal.getFullQualifiedName();
  public static final FullQualifiedName nameDouble = EdmPrimitiveTypeKind.Double.getFullQualifiedName();
  public static final FullQualifiedName nameDuration = EdmPrimitiveTypeKind.Duration.getFullQualifiedName();

  public static final FullQualifiedName nameGuid = EdmPrimitiveTypeKind.Guid.getFullQualifiedName();
  public static final FullQualifiedName nameInt16 = EdmPrimitiveTypeKind.Int16.getFullQualifiedName();
  public static final FullQualifiedName nameInt32 = EdmPrimitiveTypeKind.Int32.getFullQualifiedName();
  public static final FullQualifiedName nameInt64 = EdmPrimitiveTypeKind.Int64.getFullQualifiedName();

  public static final FullQualifiedName nameSByte = EdmPrimitiveTypeKind.SByte.getFullQualifiedName();
  public static final FullQualifiedName nameSingle = EdmPrimitiveTypeKind.Single.getFullQualifiedName();

  public static final FullQualifiedName nameString = EdmPrimitiveTypeKind.String.getFullQualifiedName();
  public static final FullQualifiedName nameTimeOfDay = EdmPrimitiveTypeKind.TimeOfDay.getFullQualifiedName();

  // Primitive Properties --------------------------------------------------------------------------------------------
  public static final Property collPropertyBinary = new Property()
      .setName("CollPropertyBinary")
      .setType(nameBinary)
      .setCollection(true);

  public static final Property collPropertyBinary_ExplicitNullable = new Property()
      .setName("CollPropertyBinary")
      .setType(nameBinary)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyBoolean = new Property()
      .setName("CollPropertyBoolean")
      .setType(nameBoolean)
      .setCollection(true);

  public static final Property collPropertyBoolean_ExplicitNullable = new Property()
      .setName("CollPropertyBoolean")
      .setType(nameBoolean)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyByte = new Property()
      .setName("CollPropertyByte")
      .setType(nameByte)
      .setCollection(true);

  public static final Property collPropertyByte_ExplicitNullable = new Property()
      .setName("CollPropertyByte")
      .setType(nameByte)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyDate = new Property()
      .setName("CollPropertyDate")
      .setType(nameDate)
      .setCollection(true);

  public static final Property collPropertyDate_ExplicitNullable = new Property()
      .setName("CollPropertyDate")
      .setType(nameDate)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyDateTimeOffset = new Property()
      .setName("CollPropertyDateTimeOffset")
      .setType(nameDateTimeOffset)
      .setCollection(true);

  public static final Property collPropertyDateTimeOffset_ExplicitNullable = new Property()
      .setName("CollPropertyDateTimeOffset")
      .setType(nameDateTimeOffset)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyDecimal = new Property()
      .setName("CollPropertyDecimal")
      .setType(nameDecimal)
      .setCollection(true);

  public static final Property collPropertyDecimal_ExplicitNullable = new Property()
      .setName("CollPropertyDecimal")
      .setType(nameDecimal)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyDouble = new Property()
      .setName("CollPropertyDouble")
      .setType(nameDouble)
      .setCollection(true);

  public static final Property collPropertyDouble_ExplicitNullable = new Property()
      .setName("CollPropertyDouble")
      .setType(nameDouble)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyDuration = new Property()
      .setName("CollPropertyDuration")
      .setType(nameDuration)
      .setCollection(true);

  public static final Property collPropertyDuration_ExplicitNullable = new Property()
      .setName("CollPropertyDuration")
      .setType(nameDuration)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyGuid = new Property()
      .setName("CollPropertyGuid")
      .setType(nameGuid)
      .setCollection(true);

  public static final Property collPropertyGuid_ExplicitNullable = new Property()
      .setName("CollPropertyGuid")
      .setType(nameGuid)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyInt16 = new Property()
      .setName("CollPropertyInt16")
      .setType(nameInt16)
      .setCollection(true);

  public static final Property collPropertyInt16_ExplicitNullable = new Property()
      .setName("CollPropertyInt16")
      .setType(nameInt16)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyInt32 = new Property()
      .setName("CollPropertyInt32")
      .setType(nameInt32)
      .setCollection(true);

  public static final Property collPropertyInt32_ExplicitNullable = new Property()
      .setName("CollPropertyInt32")
      .setType(nameInt32)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyInt64 = new Property()
      .setName("CollPropertyInt64")
      .setType(nameInt64)
      .setCollection(true);

  public static final Property collPropertyInt64_ExplicitNullable = new Property()
      .setName("CollPropertyInt64")
      .setType(nameInt64)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertySByte = new Property()
      .setName("CollPropertySByte")
      .setType(nameSByte)
      .setCollection(true);

  public static final Property collPropertySByte_ExplicitNullable = new Property()
      .setName("CollPropertySByte")
      .setType(nameSByte)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertySingle = new Property()
      .setName("CollPropertySingle")
      .setType(nameSingle)
      .setCollection(true);

  public static final Property collPropertySingle_ExplicitNullable = new Property()
      .setName("CollPropertySingle")
      .setType(nameSingle)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyString = new Property()
      .setName("CollPropertyString")
      .setType(nameString)
      .setCollection(true);

  public static final Property collPropertyString_ExplicitNullable = new Property()
      .setName("CollPropertyString")
      .setType(nameString)
      .setNullable(true)
      .setCollection(true);

  public static final Property collPropertyTimeOfDay = new Property()
      .setName("CollPropertyTimeOfDay")
      .setType(nameTimeOfDay)
      .setCollection(true);

  public static final Property collPropertyTimeOfDay_ExplicitNullable = new Property()
      .setName("CollPropertyTimeOfDay")
      .setType(nameTimeOfDay)
      .setNullable(true)
      .setCollection(true);

  public static final Property propertyBinary = new Property()
      .setName("PropertyBinary")
      .setType(nameBinary);

  public static final Property propertyBinary_NotNullable = new Property()
      .setName("PropertyBinary")
      .setType(nameBinary)
      .setNullable(false);

  public static final Property propertyBinary_ExplicitNullable = new Property()
      .setName("PropertyBinary")
      .setType(nameBinary)
      .setNullable(true);

  public static final Property propertyBoolean = new Property()
      .setName("PropertyBoolean")
      .setType(nameBoolean);

  public static final Property propertyBoolean_NotNullable = new Property()
      .setName("PropertyBoolean")
      .setType(nameBoolean)
      .setNullable(false);

  public static final Property propertyBoolean_ExplicitNullable = new Property()
      .setName("PropertyBoolean")
      .setType(nameBoolean)
      .setNullable(true);

  public static final Property propertyByte = new Property()
      .setName("PropertyByte")
      .setType(nameByte);

  public static final Property propertyByte_NotNullable = new Property()
      .setName("PropertyByte")
      .setType(nameByte)
      .setNullable(false);

  public static final Property propertyByte_ExplicitNullable = new Property()
      .setName("PropertyByte")
      .setType(nameByte)
      .setNullable(true);

  public static final Property propertyDate = new Property()
      .setName("PropertyDate")
      .setType(nameDate);

  public static final Property propertyDate_NotNullable = new Property()
      .setName("PropertyDate")
      .setType(nameDate)
      .setNullable(false);

  public static final Property propertyDate_ExplicitNullable = new Property()
      .setName("PropertyDate")
      .setType(nameDate)
      .setNullable(true);

  public static final Property propertyDateTimeOffset = new Property()
      .setName("PropertyDateTimeOffset")
      .setPrecision(20)
      .setType(nameDateTimeOffset);

  public static final Property propertyDateTimeOffset_NotNullable = new Property()
      .setName("PropertyDateTimeOffset")
      .setType(nameDateTimeOffset)
      .setNullable(false);

  public static final Property propertyDateTimeOffset_ExplicitNullable = new Property()
      .setName("PropertyDateTimeOffset")
      .setType(nameDateTimeOffset)
      .setNullable(true);

  public static final Property propertyDecimal = new Property()
      .setName("PropertyDecimal")
      .setScale(10)
      .setType(nameDecimal);

  public static final Property propertyDecimal_NotNullable = new Property()
      .setName("PropertyDecimal")
      .setType(nameDecimal)
      .setNullable(false);

  public static final Property propertyDecimal_ExplicitNullable = new Property()
      .setName("PropertyDecimal")
      .setType(nameDecimal)
      .setNullable(true);

  public static final Property propertyDouble = new Property()
      .setName("PropertyDouble")
      .setType(nameDouble);

  public static final Property propertyDouble_NotNullable = new Property()
      .setName("PropertyDouble")
      .setType(nameDouble)
      .setNullable(false);

  public static final Property propertyDouble_ExplicitNullable = new Property()
      .setName("PropertyDouble")
      .setType(nameDouble)
      .setNullable(true);

  public static final Property propertyDuration = new Property()
      .setName("PropertyDuration")
      .setType(nameDuration);

  public static final Property propertyDuration_NotNullable = new Property()
      .setName("PropertyDuration")
      .setType(nameDuration)
      .setNullable(false);

  public static final Property propertyDuration_ExplicitNullable = new Property()
      .setName("PropertyDuration")
      .setType(nameDuration)
      .setNullable(true);

  public static final Property propertyGuid = new Property()
      .setName("PropertyGuid")
      .setType(nameGuid);

  public static final Property propertyGuid_NotNullable = new Property()
      .setName("PropertyGuid")
      .setType(nameGuid)
      .setNullable(false);

  public static final Property propertyGuid_ExplicitNullable = new Property()
      .setName("PropertyGuid")
      .setType(nameGuid)
      .setNullable(true);

  public static final Property propertyInt16 = new Property()
      .setName("PropertyInt16")
      .setType(nameInt16);

  public static final Property propertyInt16_NotNullable = new Property()
      .setName("PropertyInt16")
      .setType(nameInt16)
      .setNullable(false);

  public static final Property propertyInt16_ExplicitNullable = new Property()
      .setName("PropertyInt16")
      .setType(nameInt16)
      .setNullable(true);

  public static final Property propertyInt32 = new Property()
      .setName("PropertyInt32")
      .setType(nameInt32);

  public static final Property propertyInt32_NotNullable = new Property()
      .setName("PropertyInt32")
      .setType(nameInt32)
      .setNullable(false);

  public static final Property propertyInt32_ExplicitNullable = new Property()
      .setName("PropertyInt32")
      .setType(nameInt32)
      .setNullable(true);

  public static final Property propertyInt64 = new Property()
      .setName("PropertyInt64")
      .setType(nameInt64);

  public static final Property propertyInt64_NotNullable = new Property()
      .setName("PropertyInt64")
      .setType(nameInt64)
      .setNullable(false);

  public static final Property propertyInt64_ExplicitNullable = new Property()
      .setName("PropertyInt64")
      .setType(nameInt64)
      .setNullable(true);

  public static final Property propertySByte = new Property()
      .setName("PropertySByte")
      .setType(nameSByte);

  public static final Property propertySByte_NotNullable = new Property()
      .setName("PropertySByte")
      .setType(nameSByte)
      .setNullable(false);

  public static final Property propertySByte_ExplicitNullable = new Property()
      .setName("PropertySByte")
      .setType(nameSByte)
      .setNullable(true);

  public static final Property propertySingle = new Property()
      .setName("PropertySingle")
      .setType(nameSingle);

  public static final Property propertySingle_NotNullable = new Property()
      .setName("PropertySingle")
      .setType(nameSingle)
      .setNullable(false);

  public static final Property propertySingle_ExplicitNullable = new Property()
      .setName("PropertySingle")
      .setType(nameSingle)
      .setNullable(true);

  public static final Property propertyString = new Property()
      .setName("PropertyString")
      .setType(nameString);

  public static final Property propertyString_NotNullable = new Property()
      .setName("PropertyString")
      .setType(nameString)
      .setNullable(false);

  public static final Property propertyString_ExplicitNullable = new Property()
      .setName("PropertyString")
      .setType(nameString)
      .setNullable(true);

  public static final Property propertyTimeOfDay = new Property()
      .setName("PropertyTimeOfDay")
      .setPrecision(10)
      .setType(nameTimeOfDay);

  public static final Property propertyTimeOfDay_NotNullable = new Property()
      .setName("PropertyTimeOfDay")
      .setType(nameTimeOfDay)
      .setNullable(false);

  public static final Property propertyTimeOfDay_ExplicitNullable = new Property()
      .setName("PropertyTimeOfDay")
      .setType(nameTimeOfDay)
      .setNullable(true);

  /*
   * TODO add propertyStream
   * Property propertyStream = new Property()
   * .setName("PropertyStream")
   * .setType(EdmStream.getFullQualifiedName());
   */

  // Complex Properties ----------------------------------------------------------------------------------------------
  public static final Property collPropertyComplex_CTPrimComp = new Property()
      .setName("CollPropertyComplex")
      .setType(ComplexTypeProvider.nameCTPrimComp)
      .setCollection(true);

  public static final Property collPropertyComplex_CTTwoPrim = new Property()
      .setName("CollPropertyComplex")
      .setType(ComplexTypeProvider.nameCTTwoPrim)
      .setCollection(true);

  public static final Property propertyComplex_CTAllPrim = new Property()
      .setName("PropertyComplex")
      .setType(ComplexTypeProvider.nameCTAllPrim);

  public static final Property propertyComplex_CTCollAllPrim = new Property()
      .setName("PropertyComplex")
      .setType(ComplexTypeProvider.nameCTCollAllPrim);

  public static final Property propertyComplex_CTCompCollComp = new Property()
      .setName("PropertyComplex")
      .setType(ComplexTypeProvider.nameCTCompCollComp);

  public static final Property propertyComplex_CTCompComp = new Property()
      .setName("PropertyComplex")
      .setType(ComplexTypeProvider.nameCTCompComp);

  public static final Property propertyComplex_CTNavFiveProp = new Property()
      .setName("PropertyComplex")
      .setType(ComplexTypeProvider.nameCTNavFiveProp);

  public static final Property propertyComplex_CTPrimComp_NotNullable = new Property()
      .setName("PropertyComplex")
      .setType(ComplexTypeProvider.nameCTPrimComp)
      .setNullable(false);

  public static final Property propertyComplex_CTTwoPrim = new Property()
      .setName("PropertyComplex")
      .setType(ComplexTypeProvider.nameCTTwoPrim);

  public static final Property propertyComplexAllPrim_CTAllPrim = new Property()
      .setName("PropertyComplexAllPrim")
      .setType(ComplexTypeProvider.nameCTAllPrim);

  public static final Property propertyComplexComplex_CTCompComp = new Property()
      .setName("PropertyComplexComplex")
      .setType(ComplexTypeProvider.nameCTCompComp);

  public static final Property propertyComplexEnum_CTPrimEnum_NotNullable = new Property()
      .setName("PropertyComplexEnum")
      .setType(ComplexTypeProvider.nameCTPrimEnum)
      .setNullable(false);

  public static final Property propertyComplexTwoPrim_CTTwoPrim = new Property()
      .setName("PropertyComplexTwoPrim")
      .setType(ComplexTypeProvider.nameCTTwoPrim);

  public static final Property propertyMixedPrimCollComp_CTMixPrimCollComp = new Property()
      .setName("PropertyMixedPrimCollComp")
      .setType(ComplexTypeProvider.nameCTMixPrimCollComp);

  // Navigation Properties -------------------------------------------------------------------------------------------
  public static final NavigationProperty collectionNavPropertyETKeyNavMany_ETKeyNav = new NavigationProperty()
      .setName("NavPropertyETKeyNavMany")
      .setType(EntityTypeProvider.nameETKeyNav)
      .setCollection(true);

  public static final NavigationProperty collectionNavPropertyETMediaMany_ETMedia = new NavigationProperty()
      .setName("NavPropertyETMediaMany")
      .setType(EntityTypeProvider.nameETMedia)
      .setCollection(true);

  public static final NavigationProperty collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav = new NavigationProperty()
      .setName("NavPropertyETTwoKeyNavMany")
      .setType(EntityTypeProvider.nameETTwoKeyNav)
      .setCollection(true)
      .setPartner("NavPropertyETKeyNavOne");

  public static final NavigationProperty collectionNavPropertyETTwoKeyNavOne_ETTwoKeyNav = new NavigationProperty()
      .setName("NavPropertyETTwoKeyNavOne")
      .setType(EntityTypeProvider.nameETTwoKeyNav);

  public static final NavigationProperty collectionNavPropertyETTwoPrimMany_ETTwoPrim = new NavigationProperty()
      .setName("NavPropertyETTwoPrimMany")
      .setType(EntityTypeProvider.nameETTwoPrim)
      .setCollection(true)
      .setNullable(false);

  public static final NavigationProperty collectionNavPropertyETAllPrimMany_ETAllPrim = new NavigationProperty()
      .setName("NavPropertyETAllPrimMany")
      .setType(EntityTypeProvider.nameETAllPrim)
      .setCollection(true);

  public static final NavigationProperty navPropertyETKeyNavOne_ETKeyNav = new NavigationProperty()
      .setName("NavPropertyETKeyNavOne")
      .setType(EntityTypeProvider.nameETKeyNav);

  public static final NavigationProperty navPropertyETMediaOne_ETMedia = new NavigationProperty()
      .setName("NavPropertyETMediaOne")
      .setType(EntityTypeProvider.nameETMedia);

  public static final NavigationProperty navPropertyETKeyPrimNavOne_ETKeyPrimNav = new NavigationProperty()
      .setName("NavPropertyETKeyPrimNavOne")
      .setType(EntityTypeProvider.nameETKeyPrimNav);

  public static final NavigationProperty navPropertyETTwoKeyNavOne_ETTwoKeyNav_NotNullable = new NavigationProperty()
      .setName("NavPropertyETTwoKeyNavOne")
      .setType(EntityTypeProvider.nameETTwoKeyNav)
      .setNullable(false);

  public static final NavigationProperty navPropertyETTwoKeyNavOne_ETTwoKeyNav = new NavigationProperty()
      .setName("NavPropertyETTwoKeyNavOne")
      .setType(EntityTypeProvider.nameETTwoKeyNav);

  public static final NavigationProperty navPropertyETTwoPrimOne_ETTwoPrim = new NavigationProperty()
      .setName("NavPropertyETTwoPrimOne")
      .setType(EntityTypeProvider.nameETTwoPrim)
      .setNullable(false);

  public static final NavigationProperty navPropertyETAllPrimOne_ETAllPrim = new NavigationProperty()
      .setName("NavPropertyETAllPrimOne")
      .setType(EntityTypeProvider.nameETAllPrim);

  // EnumProperties --------------------------------------------------------------------------------------------------
  public static final Property propertyEnumString_ENString = new Property()
      .setName("PropertyEnumString")
      .setType(EnumTypeProvider.nameENString);

  // TypeDefinition Properties ---------------------------------------------------------------------------------------

}
