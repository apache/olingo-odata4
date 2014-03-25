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

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;
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

  public static final Property collPropertyBoolean = new Property()
      .setName("CollPropertyBoolean")
      .setType(nameBoolean)
      .setCollection(true);

  public static final Property collPropertyByte = new Property()
      .setName("CollPropertyByte")
      .setType(nameByte)
      .setCollection(true);

  public static final Property collPropertyDate = new Property()
      .setName("CollPropertyDate")
      .setType(nameDate)
      .setCollection(true);

  public static final Property collPropertyDateTimeOffset = new Property()
      .setName("CollPropertyDateTimeOffset")
      .setType(nameDateTimeOffset)
      .setCollection(true);

  public static final Property collPropertyDecimal = new Property()
      .setName("CollPropertyDecimal")
      .setType(nameDecimal)
      .setCollection(true);

  public static final Property collPropertyDouble = new Property()
      .setName("CollPropertyDouble")
      .setType(nameDouble)
      .setCollection(true);

  public static final Property collPropertyDuration = new Property()
      .setName("CollPropertyDuration")
      .setType(nameDuration)
      .setCollection(true);
  public static final Property collPropertyGuid = new Property()
      .setName("CollPropertyGuid")
      .setType(nameGuid)
      .setCollection(true);
  public static final Property collPropertyInt16 = new Property()
      .setName("CollPropertyInt16")
      .setType(nameInt16)
      .setCollection(true);
  public static final Property collPropertyInt32 = new Property()
      .setName("CollPropertyInt32")
      .setType(nameInt32)
      .setCollection(true);
  public static final Property collPropertyInt64 = new Property()
      .setName("CollPropertyInt64")
      .setType(nameInt64)
      .setCollection(true);

  public static final Property collPropertySByte = new Property()
      .setName("CollPropertySByte")
      .setType(nameSByte)
      .setCollection(true);

  public static final Property collPropertySingle = new Property()
      .setName("CollPropertySingle")
      .setType(nameSingle)
      .setCollection(true);

  public static final Property collPropertyString = new Property()
      .setName("CollPropertyString")
      .setType(nameString)
      .setCollection(true);

  public static final Property collPropertyTimeOfDay = new Property()
      .setName("CollPropertyTimeOfDay")
      .setType(nameTimeOfDay)
      .setCollection(true);

  public static final Property propertyBinary = new Property()
      .setName("PropertyBinary")
      .setType(nameBinary);
  public static final Property propertyBoolean = new Property()
      .setName("PropertyBoolean")
      .setType(nameBoolean);
  public static final Property propertyByte = new Property()
      .setName("PropertyByte")
      .setType(nameByte);

  public static final Property propertyDate = new Property()
      .setName("PropertyDate")
      .setType(nameDate);

  public static final Property propertyDateTimeOffset = new Property()
      .setName("PropertyDateTimeOffset")
      .setType(nameDateTimeOffset);

  public static final Property propertyDecimal = new Property()
      .setName("PropertyDecimal")
      .setType(nameDecimal);

  public static final Property propertyDouble = new Property()
      .setName("PropertyDouble")
      .setType(nameDouble);

  public static final Property propertyDuration = new Property()
      .setName("PropertyDuration")
      .setType(nameDuration);

  public static final Property propertyGuid = new Property()
      .setName("PropertyGuid")
      .setType(nameGuid);

  public static final Property propertyInt16 = new Property()
      .setName("PropertyInt16")
      .setType(nameInt16);

  public static final Property propertyInt16_NotNullable = new Property()
      .setName("PropertyInt16")
      .setType(nameInt16)
      .setNullable(false);
  public static final Property propertyInt32 = new Property()
      .setName("PropertyInt32")
      .setType(nameInt32);

  public static final Property propertyInt64 = new Property()
      .setName("PropertyInt64")
      .setType(nameInt64);

  public static final Property propertySByte = new Property()
      .setName("PropertySByte")
      .setType(nameSByte);

  public static final Property propertySingle = new Property()
      .setName("PropertySingle")
      .setType(nameSingle);

  public static final Property propertyString = new Property()
      .setName("PropertyString")
      .setType(nameString);

  public static final Property propertyString_NotNullable = new Property()
      .setName("PropertyString")
      .setType(nameString);

  public static final Property propertyTimeOfDay = new Property().setName("PropertyTimeOfDay")
      .setType(nameTimeOfDay);

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
      .setType(ComplexTypeProvider.nameCTPrimComp);

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
      .setType(ComplexTypeProvider.nameCTPrimEnum);

  public static final Property propertyComplexTwoPrim_CTTwoPrim = new Property()
      .setName("PropertyComplexTwoPrim")
      .setType(ComplexTypeProvider.nameCTTwoPrim);

  public static final Property propertyMixedPrimCollComp_CTMixPrimCollComp = new Property()
      .setName("PropertyMixedPrimCollComp")
      .setType(ComplexTypeProvider.nameCTMixPrimCollComp)
      .setCollection(true);

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

  public static final NavigationProperty navPropertyETKeyNavOne_ETKeyNav = new NavigationProperty()
      .setName("NavPropertyETKeyNavOne")
      .setType(EntityTypeProvider.nameETKeyNav);
  public static final NavigationProperty navPropertyETMediaOne_ETMedia = new NavigationProperty()
      .setName("NavPropertyETMediaOne")
      .setType(EntityTypeProvider.nameETMedia);

  public static final NavigationProperty navPropertyETKeyPrimNavOne = new NavigationProperty()
      .setName("NavPropertyETKeyPrimNavOne")
      .setType(EntityTypeProvider.nameETKeyPrimNav);

  public static final NavigationProperty navPropertyETTwoKeyNavOne_ETTwoKeyNav = new NavigationProperty()
      .setName("NavPropertyETTwoKeyNavOne")
      .setType(EntityTypeProvider.nameETTwoKeyNav);

  // EnumProperties --------------------------------------------------------------------------------------------------
  public static final Property propertyEnumString_ENString = new Property()
      .setName("PropertyEnumString")
      .setType(EnumTypeProvider.nameENString);

  // TypeDefinition Properties ---------------------------------------------------------------------------------------

}
