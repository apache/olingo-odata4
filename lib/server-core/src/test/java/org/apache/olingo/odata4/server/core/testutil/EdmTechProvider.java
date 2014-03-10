/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.server.core.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata4.commons.api.ODataException;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;
import org.apache.olingo.odata4.server.api.edm.provider.Action;
import org.apache.olingo.odata4.server.api.edm.provider.ActionImport;
import org.apache.olingo.odata4.server.api.edm.provider.AliasInfo;
import org.apache.olingo.odata4.server.api.edm.provider.ComplexType;
import org.apache.olingo.odata4.server.api.edm.provider.EdmProvider;
import org.apache.olingo.odata4.server.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.odata4.server.api.edm.provider.EntitySet;
import org.apache.olingo.odata4.server.api.edm.provider.EntityType;
import org.apache.olingo.odata4.server.api.edm.provider.EnumMember;
import org.apache.olingo.odata4.server.api.edm.provider.EnumType;
import org.apache.olingo.odata4.server.api.edm.provider.Function;
import org.apache.olingo.odata4.server.api.edm.provider.FunctionImport;
import org.apache.olingo.odata4.server.api.edm.provider.NavigationProperty;
import org.apache.olingo.odata4.server.api.edm.provider.NavigationPropertyBinding;
import org.apache.olingo.odata4.server.api.edm.provider.Parameter;
import org.apache.olingo.odata4.server.api.edm.provider.Property;
import org.apache.olingo.odata4.server.api.edm.provider.PropertyRef;
import org.apache.olingo.odata4.server.api.edm.provider.ReferentialConstraint;
import org.apache.olingo.odata4.server.api.edm.provider.ReturnType;
import org.apache.olingo.odata4.server.api.edm.provider.Singleton;
import org.apache.olingo.odata4.commons.api.edm.Target;

public class EdmTechProvider extends EdmProvider {

  public static final String nameSpace = "com.sap.odata.test1";

  public static final FullQualifiedName nameBAESAllPrimRTETAllPrim =
      new FullQualifiedName(nameSpace, "BAESAllPrimRTETAllPrim");

  public static final FullQualifiedName nameBAESTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BAESTwoKeyNavRTESTwoKeyNav");

  public static final FullQualifiedName nameBAETBaseTwoKeyNavRTETBaseTwoKeyNav =
      new FullQualifiedName(nameSpace, "BAETBaseTwoKeyNavRTETBaseTwoKeyNav");

  public static final FullQualifiedName nameBAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav =
      new FullQualifiedName(nameSpace, "BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav");

  public static final FullQualifiedName nameBAETTwoKeyNavRTETTwoKeyNav =
      new FullQualifiedName(nameSpace, "BAETTwoKeyNavRTETTwoKeyNav");

  public static final FullQualifiedName nameBFCCollCTPrimCompRTESAllPrim =
      new FullQualifiedName(nameSpace, "BFCCollCTPrimCompRTESAllPrim");

  public static final FullQualifiedName nameBFCCollStringRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCCollStringRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCCTPrimCompRTESBaseTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCCTPrimCompRTESBaseTwoKeyNav");

  public static final FullQualifiedName nameBFCCTPrimCompRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCCTPrimCompRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCCTPrimCompRTESTwoKeyNavParam =
      new FullQualifiedName(nameSpace, "BFCCTPrimCompRTESTwoKeyNavParam");

  public static final FullQualifiedName nameBFCCTPrimCompRTETTwoKeyNavParam =
      new FullQualifiedName(nameSpace, "BFCCTPrimCompRTETTwoKeyNavParam");

  public static final FullQualifiedName nameBFCESAllPrimRTCTAllPrim =
      new FullQualifiedName(nameSpace, "BFCESAllPrimRTCTAllPrim");

  public static final FullQualifiedName nameBFCESBaseTwoKeyNavRTESBaseTwoKey =
      new FullQualifiedName(nameSpace, "BFCESBaseTwoKeyNavRTESBaseTwoKey");

  public static final FullQualifiedName nameBFCESKeyNavRTETKeyNav =
      new FullQualifiedName(nameSpace, "BFCESKeyNavRTETKeyNav");

  public static final FullQualifiedName nameBFCESKeyNavRTETKeyNavParam =
      new FullQualifiedName(nameSpace, "BFCESKeyNavRTETKeyNavParam");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCollCTTwoPrim =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTCollCTTwoPrim");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCollString =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTCollString");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTCTTwoPrim =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTCTTwoPrim");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTString =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTString");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTStringParam =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTStringParam");

  public static final FullQualifiedName nameBFCESTwoKeyNavRTTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTTwoKeyNav");

  public static final FullQualifiedName nameBFCETBaseTwoKeyNavRTESBaseTwoKey =
      new FullQualifiedName(nameSpace, "BFCETBaseTwoKeyNavRTESBaseTwoKey");

  public static final FullQualifiedName nameBFCETBaseTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCETBaseTwoKeyNavRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCETBaseTwoKeyNavRTETTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCETBaseTwoKeyNavRTETTwoKeyNav");

  public static final FullQualifiedName nameBFCETKeyNavRTETKeyNav =
      new FullQualifiedName(nameSpace, "BFCETKeyNavRTETKeyNav");

  public static final FullQualifiedName nameBFCETTwoKeyNavRTCTTwoPrim =
      new FullQualifiedName(nameSpace, "BFCETTwoKeyNavRTCTTwoPrim");

  public static final FullQualifiedName nameBFCETTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCETTwoKeyNavRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCETTwoKeyNavRTETTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCETTwoKeyNavRTETTwoKeyNav");

  public static final FullQualifiedName nameBFCSINavRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCSINavRTESTwoKeyNav");

  public static final FullQualifiedName nameBFCStringRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCStringRTESTwoKeyNav");

  public static final FullQualifiedName nameBFESTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFESTwoKeyNavRTESTwoKeyNav");

  public static final FullQualifiedName nameBinary = EdmPrimitiveTypeKind.Binary.getFullQualifiedName();
  public static final FullQualifiedName nameBoolean = EdmPrimitiveTypeKind.Boolean.getFullQualifiedName();
  public static final FullQualifiedName nameByte = EdmPrimitiveTypeKind.Byte.getFullQualifiedName();
  public static final FullQualifiedName nameContainer = new FullQualifiedName(nameSpace, "Container");
  public static final FullQualifiedName nameCTAllPrim = new FullQualifiedName(nameSpace, "CTAllPrim");
  public static final FullQualifiedName nameCTBase = new FullQualifiedName(nameSpace, "CTBase");
  public static final FullQualifiedName nameCTBasePrimCompNav = new FullQualifiedName(nameSpace, "CTBasePrimCompNav");
  public static final FullQualifiedName nameCTCollAllPrim = new FullQualifiedName(nameSpace, "CTCollAllPrim");
  public static final FullQualifiedName nameCTCompCollComp = new FullQualifiedName(nameSpace, "CTCompCollComp");
  public static final FullQualifiedName nameCTCompComp = new FullQualifiedName(nameSpace, "CTCompComp");
  public static final FullQualifiedName nameCTCompNav = new FullQualifiedName(nameSpace, "CTCompNav");

  public static final FullQualifiedName nameCTMixPrimCollComp = new FullQualifiedName(nameSpace, "CTMixPrimCollComp");
  public static final FullQualifiedName nameCTNavFiveProp = new FullQualifiedName(nameSpace, "CTNavFiveProp");
  public static final FullQualifiedName nameCTPrim = new FullQualifiedName(nameSpace, "CTPrim");
  public static final FullQualifiedName nameCTPrimComp = new FullQualifiedName(nameSpace, "CTPrimComp");
  public static final FullQualifiedName nameCTPrimEnum = new FullQualifiedName(nameSpace, "CTPrimEnum");
  public static final FullQualifiedName nameCTTwoBase = new FullQualifiedName(nameSpace, "CTTwoBase");
  public static final FullQualifiedName nameCTTwoBasePrimCompNav =
      new FullQualifiedName(nameSpace, "CTTwoBasePrimCompNav");
  public static final FullQualifiedName nameCTTwoPrim = new FullQualifiedName(nameSpace, "CTTwoPrim");
  public static final FullQualifiedName nameDate = EdmPrimitiveTypeKind.Date.getFullQualifiedName();
  public static final FullQualifiedName nameDateTimeOffset =
      EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName();

  public static final FullQualifiedName nameDecimal = EdmPrimitiveTypeKind.Decimal.getFullQualifiedName();
  public static final FullQualifiedName nameDouble = EdmPrimitiveTypeKind.Double.getFullQualifiedName();
  public static final FullQualifiedName nameDuration = EdmPrimitiveTypeKind.Duration.getFullQualifiedName();
  public static final FullQualifiedName nameENString = new FullQualifiedName(nameSpace, "ENString");
  public static final FullQualifiedName nameETAllKey = new FullQualifiedName(nameSpace, "ETAllKey");
  public static final FullQualifiedName nameETAllNullable = new FullQualifiedName(nameSpace, "ETAllNullable");
  public static final FullQualifiedName nameETAllPrim = new FullQualifiedName(nameSpace, "ETAllPrim");
  public static final FullQualifiedName nameETBase = new FullQualifiedName(nameSpace, "ETBase");
  public static final FullQualifiedName nameETBaseTwoKeyNav = new FullQualifiedName(nameSpace, "ETBaseTwoKeyNav");
  public static final FullQualifiedName nameETBaseTwoKeyTwoPrim =
      new FullQualifiedName(nameSpace, "ETBaseTwoKeyTwoPrim");
  public static final FullQualifiedName nameETCollAllPrim = new FullQualifiedName(nameSpace, "ETCollAllPrim");
  public static final FullQualifiedName nameETCompAllPrim = new FullQualifiedName(nameSpace, "ETCompAllPrim");
  public static final FullQualifiedName nameETCompCollAllPrim = new FullQualifiedName(nameSpace, "ETCompCollAllPrim");
  public static final FullQualifiedName nameETCompCollComp = new FullQualifiedName(nameSpace, "ETCompCollComp");
  public static final FullQualifiedName nameETCompComp = new FullQualifiedName(nameSpace, "ETCompComp");
  public static final FullQualifiedName nameETCompMixPrimCollComp =
      new FullQualifiedName(nameSpace, "ETCompMixPrimCollComp");
  public static final FullQualifiedName nameETFourKeyAlias = new FullQualifiedName(nameSpace, "ETFourKeyAlias");
  public static final FullQualifiedName nameETKeyNav = new FullQualifiedName(nameSpace, "ETKeyNav");
  public static final FullQualifiedName nameETKeyPrimNav = new FullQualifiedName(nameSpace, "ETKeyPrimNav");
  public static final FullQualifiedName nameETKeyTwoKeyComp = new FullQualifiedName(nameSpace, "ETKeyTwoKeyComp");
  public static final FullQualifiedName nameETMedia = new FullQualifiedName(nameSpace, "ETMedia");
  public static final FullQualifiedName nameETMixPrimCollComp = new FullQualifiedName(nameSpace, "ETMixPrimCollComp");
  public static final FullQualifiedName nameETServerSidePaging =
      new FullQualifiedName(nameSpace, "ETServerSidePaging");
  public static final FullQualifiedName nameETTwoBase = new FullQualifiedName(nameSpace, "ETTwoBase");
  public static final FullQualifiedName nameETTwoBaseTwoKeyNav =
      new FullQualifiedName(nameSpace, "ETTwoBaseTwoKeyNav");
  public static final FullQualifiedName nameETTwoBaseTwoKeyTwoPrim =
      new FullQualifiedName(nameSpace, "ETTwoBaseTwoKeyTwoPrim");
  public static final FullQualifiedName nameETTwoKeyNav = new FullQualifiedName(nameSpace, "ETTwoKeyNav");
  public static final FullQualifiedName nameETTwoKeyTwoPrim = new FullQualifiedName(nameSpace, "ETTwoKeyTwoPrim");
  public static final FullQualifiedName nameETTwoPrim = new FullQualifiedName(nameSpace, "ETTwoPrim");
  public static final FullQualifiedName nameGuid = EdmPrimitiveTypeKind.Guid.getFullQualifiedName();
  public static final FullQualifiedName nameInt16 = EdmPrimitiveTypeKind.Int16.getFullQualifiedName();
  public static final FullQualifiedName nameInt32 = EdmPrimitiveTypeKind.Int32.getFullQualifiedName();
  public static final FullQualifiedName nameInt64 = EdmPrimitiveTypeKind.Int64.getFullQualifiedName();

  public static final FullQualifiedName nameSByte = EdmPrimitiveTypeKind.SByte.getFullQualifiedName();
  public static final FullQualifiedName nameSingle = EdmPrimitiveTypeKind.Single.getFullQualifiedName();

  public static final FullQualifiedName nameString = EdmPrimitiveTypeKind.String.getFullQualifiedName();
  public static final FullQualifiedName nameTimeOfDay = EdmPrimitiveTypeKind.TimeOfDay.getFullQualifiedName();
  public static final FullQualifiedName nameUARTCompCollParam = new FullQualifiedName(nameSpace, "UARTCompCollParam");
  public static final FullQualifiedName nameUARTCompParam = new FullQualifiedName(nameSpace, "UARTCompParam");
  public static final FullQualifiedName nameUARTETCollAllPrimParam =
      new FullQualifiedName(nameSpace, "UARTETCollAllPrimParam");

  public static final FullQualifiedName nameUARTETParam = new FullQualifiedName(nameSpace, "UARTETParam");
  public static final FullQualifiedName nameUARTPrimParam = new FullQualifiedName(nameSpace, "UARTPrimParam");
  public static final FullQualifiedName nameUARTPrimCollParam = new FullQualifiedName(nameSpace, "UARTPrimCollParam");
  public static final FullQualifiedName nameUFCRTCollCTTwoPrim =
      new FullQualifiedName(nameSpace, "UFCRTCollCTTwoPrim");
  public static final FullQualifiedName nameUFCRTCollCTTwoPrimParam =
      new FullQualifiedName(nameSpace, "UFCRTCollCTTwoPrimParam");
  public static final FullQualifiedName nameUFCRTCollString = new FullQualifiedName(nameSpace, "UFCRTCollString");
  public static final FullQualifiedName nameUFCRTCollStringTwoParam =
      new FullQualifiedName(nameSpace, "UFCRTCollStringTwoParam");
  public static final FullQualifiedName nameUFCRTCTAllPrimTwoParam =
      new FullQualifiedName(nameSpace, "UFCRTCTAllPrimTwoParam");
  public static final FullQualifiedName nameUFCRTCTTwoPrim = new FullQualifiedName(nameSpace, "UFCRTCTTwoPrim");
  public static final FullQualifiedName nameUFCRTCTTwoPrimParam =
      new FullQualifiedName(nameSpace, "UFCRTCTTwoPrimParam");
  public static final FullQualifiedName nameUFCRTESMixPrimCollCompTwoParam =
      new FullQualifiedName(nameSpace, "UFCRTESMixPrimCollCompTwoParam");
  public static final FullQualifiedName nameUFCRTESTwoKeyNavParam =
      new FullQualifiedName(nameSpace, "UFCRTESTwoKeyNavParam");
  public static final FullQualifiedName nameUFCRTETAllPrimTwoParam =
      new FullQualifiedName(nameSpace, "UFCRTETAllPrimTwoParam");
  public static final FullQualifiedName nameUFCRTETKeyNav = new FullQualifiedName(nameSpace, "UFCRTETKeyNav");
  public static final FullQualifiedName nameUFCRTETMedia = new FullQualifiedName(nameSpace, "UFCRTETMedia");

  public static final FullQualifiedName nameUFCRTETTwoKeyNavParam =
      new FullQualifiedName(nameSpace, "UFCRTETTwoKeyNavParam");

  public static final FullQualifiedName nameUFCRTETTwoKeyNavParamCTTwoPrim =
      new FullQualifiedName(nameSpace, "UFCRTETTwoKeyNavParamCTTwoPrim");

  public static final FullQualifiedName nameUFCRTString = new FullQualifiedName(nameSpace, "UFCRTString");

  public static final FullQualifiedName nameUFCRTStringTwoParam =
      new FullQualifiedName(nameSpace, "UFCRTStringTwoParam");

  public static final FullQualifiedName nameUFNRTESMixPrimCollCompTwoParam =
      new FullQualifiedName(nameSpace, "UFNRTESMixPrimCollCompTwoParam");
  public static final FullQualifiedName nameUFNRTInt16 =
      new FullQualifiedName(nameSpace, "UFNRTInt16");

  NavigationProperty collectionNavPropertyETKeyNavMany_ETKeyNav = new NavigationProperty()
      .setName("NavPropertyETKeyNavMany")
      .setType(nameETKeyNav)
      .setCollection(true);

  NavigationProperty collectionNavPropertyETMediaMany_ETMedia = new NavigationProperty()
      .setName("NavPropertyETMediaMany")
      .setType(nameETMedia)
      .setCollection(true);

  NavigationProperty collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav = new NavigationProperty()
      .setName("NavPropertyETTwoKeyNavMany")
      .setType(nameETTwoKeyNav)
      .setCollection(true)
      .setPartner("NavPropertyETKeyNavOne");

  NavigationProperty collectionNavPropertyETTwoKeyNavOne_ETTwoKeyNav = new NavigationProperty()
      .setName("NavPropertyETTwoKeyNavOne")
      .setType(nameETTwoKeyNav);

  Property collPropertyBinary = new Property()
      .setName("CollPropertyBinary")
      .setType(nameBinary)
      .setCollection(true);

  Property collPropertyBoolean = new Property()
      .setName("CollPropertyBoolean")
      .setType(nameBoolean)
      .setCollection(true);

  Property collPropertyByte = new Property()
      .setName("CollPropertyByte")
      .setType(nameByte)
      .setCollection(true);

  Property collPropertyComplex_CTPrimComp = new Property()
      .setName("CollPropertyComplex")
      .setType(nameCTPrimComp)
      .setCollection(true);

  Property collPropertyComplex_CTTwoPrim = new Property()
      .setName("CollPropertyComplex")
      .setType(nameCTTwoPrim)
      .setCollection(true);

  Property collPropertyDate = new Property()
      .setName("CollPropertyDate")
      .setType(nameDate)
      .setCollection(true);

  Property collPropertyDateTimeOffset = new Property()
      .setName("CollPropertyDateTimeOffset")
      .setType(nameDateTimeOffset)
      .setCollection(true);

  Property collPropertyDecimal = new Property()
      .setName("CollPropertyDecimal")
      .setType(nameDecimal)
      .setCollection(true);

  Property collPropertyDouble = new Property()
      .setName("CollPropertyDouble")
      .setType(nameDouble)
      .setCollection(true);

  Property collPropertyDuration = new Property()
      .setName("CollPropertyDuration")
      .setType(nameDuration)
      .setCollection(true);
  Property collPropertyGuid = new Property()
      .setName("CollPropertyGuid")
      .setType(nameGuid)
      .setCollection(true);
  Property collPropertyInt16 = new Property()
      .setName("CollPropertyInt16")
      .setType(nameInt16)
      .setCollection(true);
  Property collPropertyInt32 = new Property()
      .setName("CollPropertyInt32")
      .setType(nameInt32)
      .setCollection(true);
  Property collPropertyInt64 = new Property()
      .setName("CollPropertyInt64")
      .setType(nameInt64)
      .setCollection(true);

  Property collPropertySByte = new Property()
      .setName("CollPropertySByte")
      .setType(nameSByte)
      .setCollection(true);

  Property collPropertySingle = new Property()
      .setName("CollPropertySingle")
      .setType(nameSingle)
      .setCollection(true);
  Property collPropertyString = new Property()
      .setName("CollPropertyString")
      .setType(nameString)
      .setCollection(true);
  Property collPropertyTimeOfDay = new Property()
      .setName("CollPropertyTimeOfDay")
      .setType(nameTimeOfDay)
      .setCollection(true);
  EntityContainerInfo entityContainerInfoTest1 =
      new EntityContainerInfo().setContainerName(nameContainer);
  NavigationProperty navPropertyETKeyNavOne_ETKeyNav = new NavigationProperty()
      .setName("NavPropertyETKeyNavOne")
      .setType(nameETKeyNav);
  NavigationProperty navPropertyETMediaOne_ETMedia = new NavigationProperty()
      .setName("NavPropertyETMediaOne")
      .setType(nameETMedia);

  /*
   * TODO add propertyStream
   * Property propertyStream = new Property()
   * .setName("PropertyStream")
   * .setType(EdmStream.getFullQualifiedName());
   */

  NavigationProperty navPropertyETKeyPrimNavOne = new NavigationProperty()
      .setName("NavPropertyETKeyPrimNavOne")
      .setType(nameETKeyPrimNav);

  NavigationProperty navPropertyETTwoKeyNavOne_ETTwoKeyNav = new NavigationProperty()
      .setName("NavPropertyETTwoKeyNavOne")
      .setType(nameETTwoKeyNav);
  Property propertyBinary = new Property()
      .setName("PropertyBinary")
      .setType(nameBinary);
  Property propertyBoolean = new Property()
      .setName("PropertyBoolean")
      .setType(nameBoolean);
  Property propertyByte = new Property()
      .setName("PropertyByte")
      .setType(nameByte);
  Property propertyComplex_CTAllPrim = new Property()
      .setName("PropertyComplex")
      .setType(nameCTAllPrim);
  Property propertyComplex_CTCollAllPrim = new Property()
      .setName("PropertyComplex")
      .setType(nameCTCollAllPrim);
  Property propertyComplex_CTCompCollComp = new Property()
      .setName("PropertyComplex")
      .setType(nameCTCompCollComp);
  Property propertyComplex_CTCompComp = new Property()
      .setName("PropertyComplex")
      .setType(nameCTCompComp);
  Property propertyComplex_CTNavFiveProp = new Property()
      .setName("PropertyComplex")
      .setType(nameCTNavFiveProp);
  Property propertyComplex_CTPrimComp_NotNullable = new Property()
      .setName("PropertyComplex")
      .setType(nameCTPrimComp);
  Property propertyComplex_CTTwoPrim = new Property()
      .setName("PropertyComplex")
      .setType(nameCTTwoPrim);
  Property propertyComplexAllPrim_CTAllPrim = new Property()
      .setName("PropertyComplexAllPrim")
      .setType(nameCTAllPrim);
  Property propertyComplexComplex_CTCompComp = new Property()
      .setName("PropertyComplexComplex")
      .setType(nameCTCompComp);
  Property propertyComplexEnum_CTPrimEnum_NotNullable = new Property()
      .setName("PropertyComplexEnum")
      .setType(nameCTPrimEnum);
  Property propertyComplexTwoPrim_CTTwoPrim = new Property()
      .setName("PropertyComplexTwoPrim")
      .setType(nameCTTwoPrim);
  Property propertyDate = new Property()
      .setName("PropertyDate")
      .setType(nameDate);

  Property propertyDateTimeOffset = new Property()
      .setName("PropertyDateTimeOffset")
      .setType(nameDateTimeOffset);

  Property propertyDecimal = new Property()
      .setName("PropertyDecimal")
      .setType(nameDecimal);

  Property propertyDouble = new Property()
      .setName("PropertyDouble")
      .setType(nameDouble);

  Property propertyDuration = new Property()
      .setName("PropertyDuration")
      .setType(nameDuration);

  Property propertyEnumString_ENString = new Property()
      .setName("PropertyEnumString")
      .setType(nameENString);

  Property propertyGuid = new Property()
      .setName("PropertyGuid")
      .setType(nameGuid);

  Property propertyInt16 = new Property()
      .setName("PropertyInt16")
      .setType(nameInt16);

  Property propertyInt16_NotNullable = new Property()
      .setName("PropertyInt16")
      .setType(nameInt16);
  Property propertyInt32 = new Property()
      .setName("PropertyInt32")
      .setType(nameInt32);

  Property propertyInt64 = new Property()
      .setName("PropertyInt64")
      .setType(nameInt64);

  Property propertyMixedPrimCollComp_CTMixPrimCollComp = new Property()
      .setName("PropertyMixedPrimCollComp")
      .setType(nameCTMixPrimCollComp)
      .setCollection(true);

  Property propertySByte = new Property()
      .setName("PropertySByte")
      .setType(nameSByte);

  Property propertySingle = new Property()
      .setName("PropertySingle")
      .setType(nameSingle);

  Property propertyString = new Property()
      .setName("PropertyString")
      .setType(nameString);

  Property propertyString_NotNullable = new Property()
      .setName("PropertyString")
      .setType(nameString);

  Property propertyTimeOfDay = new Property().setName("PropertyTimeOfDay")
      .setType(nameTimeOfDay);

  @Override
  public ActionImport getActionImport(final FullQualifiedName entityContainer, final String name) throws ODataException
  {
    if (entityContainer.equals(nameContainer)) {
      if (name.equals("AIRTPrimParam")) {
        return new ActionImport()
            .setName("AIRTPrimParam")
            .setAction(nameUARTPrimParam);

      } else if (name.equals("AIRTPrimCollParam")) {
        return new ActionImport()
            .setName("AIRTPrimCollParam")
            .setAction(nameUARTPrimCollParam);

      } else if (name.equals("AIRTCompParam")) {
        return new ActionImport()
            .setName("AIRTCompParam")
            .setAction(nameUARTCompParam);

      } else if (name.equals("AIRTCompCollParam")) {
        return new ActionImport()
            .setName("AIRTCompCollParam")
            .setAction(nameUARTCompCollParam);

      } else if (name.equals("AIRTETParam")) {
        return new ActionImport()
            .setName("AIRTETParam")
            .setAction(nameUARTETParam);

      } else if (name.equals("AIRTETCollAllPrimParam")) {
        return new ActionImport()
            .setName("AIRTETCollAllPrimParam")
            .setAction(nameUARTETCollAllPrimParam);
      }
    }

    return null;
  }

  @Override
  public List<Action> getActions(final FullQualifiedName actionName) throws ODataException {
    if (actionName.equals(nameUARTPrimParam)) {
      return Arrays.asList(
          new Action().setName("UARTPrimParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))

              .setReturnType(new ReturnType().setType(nameString))
          );

    } else if (actionName.equals(nameUARTPrimCollParam)) {
      return Arrays.asList(
          new Action().setName("UARTPrimCollParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))

              .setReturnType(
                  new ReturnType().setType(nameString).setCollection(true))
          );

    } else if (actionName.equals(nameUARTCompParam)) {
      return Arrays.asList(
          new Action().setName("UARTCompParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))

              .setReturnType(
                  new ReturnType().setType(nameCTTwoPrim))
          );

    } else if (actionName.equals(nameUARTCompCollParam)) {
      return Arrays.asList(
          new Action().setName("UARTCompCollParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))

              .setReturnType(
                  new ReturnType().setType(nameCTTwoPrim).setCollection(true))
          );

    } else if (actionName.equals(nameUARTETParam)) {
      return Arrays.asList(
          new Action().setName("UARTCompCollParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyTwoPrim))
          );

    } else if (actionName.equals(nameUARTETCollAllPrimParam)) {
      return Arrays.asList(
          new Action().setName("UARTETCollAllPrimParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))

              .setReturnType(
                  new ReturnType().setType(nameETCollAllPrim).setCollection(true))
          );

    } else if (actionName.equals(nameBAETTwoKeyNavRTETTwoKeyNav)) {
      return Arrays.asList(
          new Action().setName("BAETTwoKeyNavRTETTwoKeyNav")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterETTwoKeyNav").setType(nameETTwoKeyNav)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav))
          ,
          new Action().setName("BAETTwoKeyNavRTETTwoKeyNav")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterETKeyNav").setType(nameETKeyNav)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav))
          );

    } else if (actionName.equals(nameBAESAllPrimRTETAllPrim)) {
      return Arrays.asList(
          new Action().setName("BAESAllPrimRTETAllPrim")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterESAllPrim").setType(nameETAllPrim).setCollection(true)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(nameETAllPrim))
          );

    } else if (actionName.equals(nameBAESTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Action().setName("BAESTwoKeyNavRTESTwoKeyNav")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterETTwoKeyNav").setType(nameETTwoKeyNav).setCollection(true)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true))
          );

    } else if (actionName.equals(nameBAETBaseTwoKeyNavRTETBaseTwoKeyNav)) {
      return Arrays.asList(
          new Action().setName("BAETBaseTwoKeyNavRTETBaseTwoKeyNav")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterETTwoKeyNav").setType(nameETBaseTwoKeyNav)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav))
          );

    } else if (actionName.equals(nameBAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav)) {
      return Arrays.asList(
          new Action().setName("BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterETTwoBaseTwoKeyNav").setType(nameETTwoBaseTwoKeyNav)))
              .setBound(true)
              .setReturnType(
                  new ReturnType().setType(nameETBaseTwoKeyNav))
          );
    }

    return null;
  }

  @Override
  public List<AliasInfo> getAliasInfos() throws ODataException {
    return Arrays.asList(
        new AliasInfo().setAlias("Namespace1_Alias").setNamespace(nameSpace)
        );
  }

  @Override
  public ComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {

    if (complexTypeName.equals(nameCTPrim)) {
      return new ComplexType()
          .setName("CTPrim")
          .setProperties(Arrays.asList(propertyInt16));

    } else if (complexTypeName.equals(nameCTAllPrim)) {
      return new ComplexType()
          .setName("CTAllPrim")
          .setProperties(Arrays.asList(propertyString, propertyBinary, propertyBoolean,
              propertyByte, propertyDate, propertyDateTimeOffset,
              propertyDecimal, propertySingle, propertyDouble,
              propertyDuration, propertyGuid, propertyInt16,
              propertyInt32, propertyInt64, propertySByte,
              propertyTimeOfDay/* TODO add propertyStream */));

    } else if (complexTypeName.equals(nameCTCollAllPrim)) {
      return new ComplexType()
          .setName("CTCollAllPrim")
          .setProperties(Arrays.asList(
              collPropertyString,
              collPropertyBoolean, collPropertyByte,
              collPropertySByte,
              collPropertyInt16, collPropertyInt32, collPropertyInt64,
              collPropertySingle, collPropertyDouble,
              collPropertyDecimal, collPropertyBinary,
              collPropertyDate, collPropertyDateTimeOffset,
              collPropertyDuration, collPropertyGuid,
              collPropertyTimeOfDay /* TODO add collectionPropertyStream */));

    } else if (complexTypeName.equals(nameCTTwoPrim)) {
      return new ComplexType()
          .setName("CTTwoPrim")
          .setProperties(Arrays.asList(propertyInt16, propertyString));

    } else if (complexTypeName.equals(nameCTCompNav)) {
      return new ComplexType()
          .setName("CTCompNav")
          .setProperties(Arrays.asList(propertyInt16, propertyComplex_CTNavFiveProp));

    } else if (complexTypeName.equals(nameCTMixPrimCollComp)) {
      return new ComplexType()
          .setName("CTMixPrimCollComp")
          .setProperties(Arrays.asList(
              propertyInt16, collPropertyString, propertyComplex_CTTwoPrim, collPropertyComplex_CTTwoPrim));

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
          .setBaseType(nameCTBase)
          .setProperties(Arrays.asList(
              new Property()
                  .setName("AdditionalPropString")
                  .setType(new FullQualifiedName("Edm", "String"))));

    } else if (complexTypeName.equals(nameCTCompComp)) {
      return new ComplexType()
          .setName("CTCompComp")
          .setProperties(Arrays.asList(propertyComplex_CTTwoPrim));

    } else if (complexTypeName.equals(nameCTCompCollComp)) {
      return new ComplexType()
          .setName("CTCompComp")
          .setProperties(Arrays.asList(collPropertyComplex_CTTwoPrim));

    } else if (complexTypeName.equals(nameCTPrimComp)) {
      return new ComplexType()
          .setName("CTPrimComp")
          .setProperties(Arrays.asList(propertyInt16, propertyComplex_CTAllPrim));

    } else if (complexTypeName.equals(nameCTNavFiveProp)) {
      return new ComplexType()
          .setName("CTNavFiveProp")
          .setProperties(Arrays.asList(propertyInt16))
          .setNavigationProperties((Arrays.asList(
              collectionNavPropertyETTwoKeyNavOne_ETTwoKeyNav,
              collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              new NavigationProperty()
                  .setName("NavPropertyETMediaOne")
                  .setType(nameETMedia),
              new NavigationProperty()
                  .setName("NavPropertyETMediaMany")
                  .setType(nameETMedia).setCollection(true)
              )));

    } else if (complexTypeName.equals(nameCTBasePrimCompNav)) {
      return new ComplexType()
          .setName("CTBasePrimCompNav")
          .setBaseType(nameCTPrimComp)
          .setNavigationProperties(Arrays.asList(
              collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              collectionNavPropertyETTwoKeyNavOne_ETTwoKeyNav));

    } else if (complexTypeName.equals(nameCTPrimEnum)) {
      return new ComplexType()
          .setName("CTPrimEnum")
          .setProperties(Arrays.asList(propertyInt16, propertyEnumString_ENString));

    } else if (complexTypeName.equals(nameCTTwoBasePrimCompNav)) {
      return new ComplexType()
          .setName("CTTwoBasePrimCompNav")
          .setBaseType(nameCTBasePrimCompNav);

    }

    return null;
  }

  @Override
  public EntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName) throws ODataException {
    if (entityContainerName == null) {
      return entityContainerInfoTest1;
    } else if (entityContainerName.equals(nameContainer)) {
      return entityContainerInfoTest1;
    }

    return null;
  }

  @Override
  public EntitySet getEntitySet(final FullQualifiedName entityContainer, final String name) throws ODataException {
    if (entityContainer == nameContainer) {
      if (name.equals("ESAllPrim")) {
        return new EntitySet()
            .setName("ESAllPrim")
            .setType(nameETAllPrim);

      } else if (name.equals("ESCollAllPrim")) {
        return new EntitySet()
            .setName("ESCollAllPrim")
            .setType(nameETCollAllPrim);

      } else if (name.equals("ESTwoPrim")) {
        return new EntitySet()
            .setName("ESTwoPrim")
            .setType(nameETTwoPrim);

      } else if (name.equals("ESMixPrimCollComp")) {
        return new EntitySet()
            .setName("ESMixPrimCollComp")
            .setType(nameETMixPrimCollComp);

      } else if (name.equals("ESBase")) {
        return new EntitySet()
            .setName("ESBase")
            .setType(nameETBase);

      } else if (name.equals("ESTwoBase")) {
        return new EntitySet()
            .setName("ESTwoBase")
            .setType(nameETTwoBase);

      } else if (name.equals("ESTwoKeyTwoPrim")) {
        return new EntitySet()
            .setName("ESTwoKeyTwoPrim")
            .setType(nameETTwoKeyTwoPrim);

      } else if (name.equals("ESBaseTwoKeyTwoPrim")) {
        return new EntitySet()
            .setName("ESBaseTwoKeyTwoPrim")
            .setType(nameETBaseTwoKeyTwoPrim);

      } else if (name.equals("ESTwoBaseTwoKeyTwoPrim")) {
        return new EntitySet()
            .setName("ESTwoBaseTwoKeyTwoPrim")
            .setType(nameETTwoBaseTwoKeyTwoPrim);

      } else if (name.equals("ESAllKey")) {
        return new EntitySet()
            .setName("ESAllKey")
            .setType(nameETAllKey);

      } else if (name.equals("ESCompAllPrim")) {
        return new EntitySet()
            .setName("ESCompAllPrim")
            .setType(nameETCompAllPrim);

      } else if (name.equals("ESCompCollAllPrim")) {
        return new EntitySet()
            .setName("ESCompCollAllPrim")
            .setType(nameETCompCollAllPrim);

      } else if (name.equals("ESCompComp")) {
        return new EntitySet()
            .setName("ESCompComp")
            .setType(nameETCompComp);

      } else if (name.equals("ESCompCollComp")) {
        return new EntitySet()
            .setName("ESCompCollComp")
            .setType(nameETCompCollComp);

      } else if (name.equals("ESMedia")) {
        return new EntitySet()
            .setName("ESMedia")
            .setType(nameETMedia)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("ESKeyTwoKeyComp")) {
        return new EntitySet()
            .setName("ESKeyTwoKeyComp")
            .setType(nameETKeyTwoKeyComp);
      } else if (name.equals("ESInvisible")) {
        return new EntitySet()
            .setName("ESInvisible")
            .setType(nameETAllPrim);
      } else if (name.equals("ESServerSidePaging")) {
        return new EntitySet()
            .setName("ESServerSidePaging")
            .setType(nameETServerSidePaging);
      } else if (name.equals("ESAllNullable")) {
        return new EntitySet()
            .setName("ESAllNullable")
            .setType(nameETAllNullable);
      } else if (name.equals("ESKeyNav")) {
        return new EntitySet()
            .setName("ESKeyNav")
            .setType(nameETKeyNav);
      } else if (name.equals("ESTwoKeyNav")) {
        return new EntitySet()
            .setName("ESTwoKeyNav")
            .setType(nameETTwoKeyNav);
      } else if (name.equals("ESBaseTwoKeyNav")) {
        return new EntitySet()
            .setName("ESBaseTwoKeyNav")
            .setType(nameETBaseTwoKeyNav);
      } else if (name.equals("ESCompMixPrimCollComp")) {
        return new EntitySet()
            .setName("ESCompMixPrimCollComp")
            .setType(nameETCompMixPrimCollComp);
      } else if (name.equals("ESFourKeyAlias")) {
        return new EntitySet()
            .setName("ESFourKeyAlias")
            .setType(nameETFourKeyAlias);
      }
    }

    return null;
  }

  @Override
  public EntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {

    if (entityTypeName.equals(nameETAllPrim)) {
      return new EntityType()
          .setName("ETAllPrim")
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(
              propertyInt16_NotNullable, propertyString,
              propertyBoolean, propertyByte, propertySByte,
              propertyInt32, propertyInt64,
              propertySingle, propertyDouble, propertyDecimal,
              propertyBinary, propertyDate, propertyDateTimeOffset,
              propertyDuration, propertyGuid,
              propertyTimeOfDay /* TODO add propertyStream */));

    } else if (entityTypeName.equals(nameETCollAllPrim)) {
      return new EntityType()
          .setName("ETCollAllPrim")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))

          .setProperties(Arrays.asList(
              propertyInt16_NotNullable,
              collPropertyString, collPropertyBoolean,
              collPropertyByte, collPropertySByte,
              collPropertyInt16, collPropertyInt32, collPropertyInt64,
              collPropertySingle, collPropertyDouble, collPropertyDecimal,
              collPropertyBinary, collPropertyDate, collPropertyDateTimeOffset,
              collPropertyDuration, collPropertyGuid, collPropertyTimeOfDay /* TODO add propertyStream */));

    } else if (entityTypeName.equals(nameETTwoPrim)) {
      return new EntityType()
          .setName("ETTwoPrim")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))

          .setProperties(Arrays.asList(
              propertyInt16_NotNullable, propertyString));

    } else if (entityTypeName.equals(nameETMixPrimCollComp)) {
      return new EntityType()
          .setName("ETMixPrimCollComp")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))

          .setProperties(Arrays.asList(
              propertyInt16_NotNullable, collPropertyString,
              propertyComplex_CTTwoPrim, collPropertyComplex_CTTwoPrim));

    } else if (entityTypeName.equals(nameETTwoKeyTwoPrim)) {
      return new EntityType()
          .setName("ETTwoKeyTwoPrim")
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyInt16"),
              new PropertyRef().setPropertyName("PropertyString")))
          .setProperties(Arrays.asList(
              propertyInt16_NotNullable, propertyString));

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
              .setType(nameString)));

    } else if (entityTypeName.equals(nameETTwoBase)) {
      return new EntityType()
          .setName("ETTwoBase")
          .setBaseType(nameETBase)
          .setProperties(Arrays.asList(new Property()
              .setName("AdditionalPropertyString_6")
              .setType(nameString))
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
          .setProperties(Arrays.asList(
              propertyString, propertyBoolean,
              propertyByte, propertySByte,
              propertyInt16, propertyInt32, propertyInt64,
              propertyDecimal, propertyDate,
              propertySingle, propertyDouble, propertyDateTimeOffset,
              propertyDuration, propertyGuid,
              propertyTimeOfDay /* TODO add propertyStream */));

    } else if (entityTypeName.equals(nameETCompAllPrim)) {
      return new EntityType()
          .setName("ETCompAllPrim")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyComplex_CTAllPrim));

    } else if (entityTypeName.equals(nameETCompCollAllPrim)) {
      return new EntityType()
          .setName("ETCompAllPrim")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))

          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyComplex_CTCollAllPrim));

    } else if (entityTypeName.equals(nameETCompComp)) {
      return new EntityType()
          .setName("ETCompComp")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyComplex_CTCompComp));

    } else if (entityTypeName.equals(nameETCompCollComp)) {
      return new EntityType()
          .setName("ETCompCollComp")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyComplex_CTCompCollComp));

    } else if (entityTypeName.equals(nameETMedia)) {
      return new EntityType()
          .setName("ETCompCollComp")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(propertyInt16_NotNullable))
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
          .setProperties(Arrays.asList(
              propertyInt16_NotNullable, propertyComplex_CTTwoPrim, propertyComplexComplex_CTCompComp));

    } else if (entityTypeName.equals(nameETServerSidePaging)) {
      return new EntityType()
          .setName("ETKeyTwoKeyComp")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString));

    } else if (entityTypeName.equals(nameETAllNullable)) {
      return new EntityType()
          .setName("ETAllNullable")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyKey")))
          .setProperties(Arrays.asList(
              new Property()
                  .setName("PropertyKey").setType(nameInt16),
              propertyInt16,
              propertyString, propertyBoolean,
              propertyByte, propertySByte,
              propertyInt32, propertyInt64,
              propertySingle, propertyDouble,
              propertyDecimal, propertyBinary, propertyDate,
              propertyDateTimeOffset,
              propertyDuration, propertyGuid, propertyTimeOfDay /* TODO add propertyStream */,
              collPropertyString, collPropertyBoolean,
              collPropertyByte, collPropertySByte,
              collPropertyInt16,
              collPropertyInt32, collPropertyInt64,
              collPropertySingle, collPropertyDouble,
              collPropertyDecimal, collPropertyBinary, collPropertyDate,
              collPropertyDateTimeOffset,
              collPropertyDuration, collPropertyGuid, collPropertyTimeOfDay /* TODO add propertyStream */));

    } else if (entityTypeName.equals(nameETKeyNav)) {
      return new EntityType()
          .setName("ETKeyNav")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(
              propertyInt16_NotNullable, propertyString_NotNullable, propertyComplex_CTNavFiveProp,
              propertyComplexAllPrim_CTAllPrim, propertyComplexTwoPrim_CTTwoPrim,
              collPropertyString, collPropertyInt16, collPropertyComplex_CTPrimComp,
              new Property()
                  .setName("PropertyComplexComplex").setType(nameCTCompNav)
              ))
          .setNavigationProperties(Arrays.asList(
              navPropertyETTwoKeyNavOne_ETTwoKeyNav, collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              navPropertyETKeyNavOne_ETKeyNav, collectionNavPropertyETKeyNavMany_ETKeyNav,
              navPropertyETMediaOne_ETMedia, collectionNavPropertyETMediaMany_ETMedia
              ));
    } else if (entityTypeName.equals(nameETKeyPrimNav)) {
      return new EntityType()
          .setName("ETKeyNav")
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(
              propertyInt16_NotNullable, propertyString_NotNullable))
          .setNavigationProperties(Arrays.asList(
              navPropertyETTwoKeyNavOne_ETTwoKeyNav, collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              navPropertyETKeyNavOne_ETKeyNav, collectionNavPropertyETKeyNavMany_ETKeyNav,
              navPropertyETMediaOne_ETMedia, collectionNavPropertyETMediaMany_ETMedia
              ));

    } else if (entityTypeName.equals(nameETTwoKeyNav)) {
      return new EntityType()
          .setName("ETTwoKeyNav")
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyInt16"),
              new PropertyRef().setPropertyName("PropertyString")))
          .setProperties(Arrays.asList(
              propertyInt16, propertyString, propertyComplex_CTPrimComp_NotNullable,
              new Property().setName("PropertyComplexNav").setType(nameCTBasePrimCompNav),
              propertyComplexEnum_CTPrimEnum_NotNullable,
              collPropertyComplex_CTPrimComp,
              new Property().setName("CollPropertyComplexNav").setType(nameCTNavFiveProp).setCollection(true),
              collPropertyString, propertyComplexTwoPrim_CTTwoPrim,
              propertyEnumString_ENString
              ))
          .setNavigationProperties(Arrays.asList(
              new NavigationProperty()
                  .setName("NavPropertyETKeyNavOne")
                  .setType(nameETKeyNav)
                  .setReferentialConstraints(Arrays.asList(
                      new ReferentialConstraint()
                          .setProperty("PropertyInt16")
                          .setReferencedProperty("PropertyInt16"))),
              collectionNavPropertyETKeyNavMany_ETKeyNav,
              navPropertyETTwoKeyNavOne_ETTwoKeyNav,
              collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav));

    } else if (entityTypeName.equals(nameETBaseTwoKeyNav)) {
      return new EntityType()
          .setName("ETBaseTwoKeyNav")
          .setBaseType(nameETTwoKeyNav)
          .setProperties(Arrays.asList(propertyDate))
          .setNavigationProperties(Arrays.asList(
              new NavigationProperty()
                  .setName("NavPropertyETBaseTwoKeyNav")
                  .setType(nameETBaseTwoKeyNav),
              new NavigationProperty()
                  .setName("NavPropertyETTwoBaseTwoKeyNav")
                  .setType(nameETTwoBaseTwoKeyNav)))
          .setHasStream(true);

    } else if (entityTypeName.equals(nameETTwoBaseTwoKeyNav)) {
      return new EntityType()
          .setName("ETTwoBaseTwoKeyNav")
          .setBaseType(nameETBaseTwoKeyNav)
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")))
          .setProperties(Arrays.asList(propertyGuid))
          .setNavigationProperties(Arrays.asList(
              new NavigationProperty()
                  .setName("NavPropertyETBaseTwoKeyNavOne")
                  .setType(nameETBaseTwoKeyNav),
              new NavigationProperty()
                  .setName("NavPropertyETBaseTwoKeyNavMany")
                  .setType(nameETBaseTwoKeyNav)
                  .setCollection(true)
              ));

    } else if (entityTypeName.equals(nameETFourKeyAlias)) {
      return new EntityType()
          .setName("ETFourKeyAlias")
          .setKey(Arrays.asList(
              new PropertyRef()
                  .setPropertyName("PropertyInt16"),
              new PropertyRef()
                  .setPropertyName("PropertyComplex/PropertyInt16").setAlias("KeyAlias1"),
              new PropertyRef()
                  .setPropertyName("PropertyComplex/PropertyString").setAlias("KeyAlias2"),
              new PropertyRef()
                  .setPropertyName("PropertyComplexComplex/PropertyComplex/PropertyString")
                  .setAlias("KeyAlias3")))
          .setProperties(Arrays.asList(propertyGuid, propertyComplex_CTTwoPrim, propertyComplexComplex_CTCompComp));
    }

    return null;
  }

  @Override
  public EnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
    if (enumTypeName.equals(nameENString)) {
      return new EnumType()
          .setName("ENString")
          .setMembers(Arrays.asList(
              new EnumMember().setName("String1").setValue("1"),
              new EnumMember().setName("String2").setValue("2"),
              new EnumMember().setName("String3").setValue("3")));
    }

    return null;
  }

  @Override
  public FunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String name)
      throws ODataException {

    if (entityContainer.equals(nameContainer)) {
      if (name.equals("FINRTInt16")) {
        return new FunctionImport()
            .setName("FINRTInt16")
            .setFunction(nameUFNRTInt16)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FINInvisibleRTInt16")) {
        return new FunctionImport()
            .setName("FINInvisibleRTInt16")
            .setFunction(nameUFNRTInt16);

      } else if (name.equals("FINInvisible2RTInt16")) {
        return new FunctionImport()
            .setName("FINInvisible2RTInt16")
            .setFunction(nameUFNRTInt16);

      } else if (name.equals("FICRTETKeyNav")) {
        return new FunctionImport()
            .setName("FICRTETKeyNav")
            .setFunction(nameUFCRTETKeyNav);

      } else if (name.equals("FICRTETTwoKeyNavParam")) {
        return new FunctionImport()
            .setName("FICRTETTwoKeyNavParam")
            .setFunction(nameUFCRTETTwoKeyNavParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTStringTwoParam")) {
        return new FunctionImport()
            .setName("FICRTStringTwoParam")
            .setFunction(nameUFCRTStringTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollStringTwoParam")) {
        return new FunctionImport()
            .setName("FICRTCollStringTwoParam")
            .setFunction(nameUFCRTCollStringTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCTAllPrimTwoParam")) {
        return new FunctionImport()
            .setName("FICRTCTAllPrimTwoParam")
            .setFunction(nameUFCRTCTAllPrimTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTESMixPrimCollCompTwoParam")) {
        return new FunctionImport()
            .setName("FICRTESMixPrimCollCompTwoParam")
            .setFunction(nameUFCRTESMixPrimCollCompTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FINRTESMixPrimCollCompTwoParam")) {
        return new FunctionImport()
            .setName("FINRTESMixPrimCollCompTwoParam")
            .setFunction(nameUFNRTESMixPrimCollCompTwoParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollCTTwoPrim")) {
        return new FunctionImport()
            .setName("FICRTCollCTTwoPrim")
            .setFunction(nameUFCRTCollCTTwoPrim)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTETMedia")) {
        return new FunctionImport()
            .setName("FICRTETMedia")
            .setFunction(nameUFCRTETMedia)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCTTwoPrimParam")) {
        return new FunctionImport()
            .setName("FICRTCTTwoPrimParam")
            .setFunction(nameUFCRTCTTwoPrimParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCTTwoPrim")) {
        return new FunctionImport()
            .setName("FICRTCTTwoPrim")
            .setFunction(nameUFCRTCTTwoPrim)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollString")) {
        return new FunctionImport()
            .setName("FICRTCollString")
            .setFunction(nameUFCRTCollString)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTString")) {
        return new FunctionImport()
            .setName("FICRTString")
            .setFunction(nameUFCRTString)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTESTwoKeyNavParam")) {
        return new FunctionImport()
            .setName("FICRTESTwoKeyNavParam")
            .setFunction(nameUFCRTESTwoKeyNavParam)
            .setIncludeInServiceDocument(true);

      } else if (name.equals("FICRTCollCTTwoPrimParam")) {
        return new FunctionImport()
            .setName("FICRTCollCTTwoPrimParam")
            .setFunction(nameUFCRTCollCTTwoPrimParam)
            .setIncludeInServiceDocument(true);

      }
    }

    return null;
  }

  @Override
  public List<Function> getFunctions(final FullQualifiedName functionName) throws ODataException {

    if (functionName.equals(nameUFNRTInt16)) {
      return Arrays.asList(
          new Function()
              .setName("UFNRTInt16")
              .setParameters(new ArrayList<Parameter>())
              .setReturnType(
                  new ReturnType().setType(nameString))
          );

    } else if (functionName.equals(nameUFCRTETKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTETKeyNav")
              .setParameters(new ArrayList<Parameter>())
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETKeyNav))
          );

    } else if (functionName.equals(nameUFCRTETTwoKeyNavParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTETTwoKeyNavParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav)
              )
          );

    } else if (functionName.equals(nameUFCRTETTwoKeyNavParamCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTETTwoKeyNavParamCTTwoPrim")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterCTTwoPrim").setType(nameCTTwoPrim)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav)
              )
          );

    } else if (functionName.equals(nameUFCRTStringTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTStringTwoParam")
              .setParameters(Arrays.asList(
                  new Parameter()
                      .setName("ParameterInt16")
                      .setType(nameInt16)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameString)),
          new Function()
              .setName("UFCRTStringTwoParam")
              .setParameters(Arrays.asList(
                  new Parameter()
                      .setName("ParameterString")
                      .setType(nameString),
                  new Parameter()
                      .setName("ParameterInt16")
                      .setType(nameInt16)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameString))

          );

    } else if (functionName.equals(nameUFCRTESTwoKeyNavParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTESTwoKeyNavParam")
              .setParameters(Arrays.asList(
                  new Parameter()
                      .setName("ParameterInt16")
                      .setType(nameInt16)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true))
          );

    } else if (functionName.equals(nameUFCRTString)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTString")

              .setComposable(true)
              .setParameters(new ArrayList<Parameter>())
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameString)
              )
          );

    } else if (functionName.equals(nameUFCRTCollStringTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCollStringTwoParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterString").setType(nameString),
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameString).setCollection(true))
          );

    } else if (functionName.equals(nameUFCRTCollString)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCollString")
              .setParameters(new ArrayList<Parameter>())
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameString).setCollection(true))
          );

    } else if (functionName.equals(nameUFCRTCTAllPrimTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCTAllPrimTwoParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterString").setType(nameString),
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameCTAllPrim))
          );

    } else if (functionName.equals(nameUFCRTCTTwoPrimParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCTTwoPrimParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterString").setType(nameString),
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameCTTwoPrim))
          );
    } else if (functionName.equals(nameUFCRTCollCTTwoPrimParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCollCTTwoPrimParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterString").setType(nameString),
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameCTTwoPrim).setCollection(true))
          );

    } else if (functionName.equals(nameUFCRTCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCTTwoPrim")
              .setParameters(new ArrayList<Parameter>())
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameCTTwoPrim))
          );

    } else if (functionName.equals(nameUFCRTCollCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCTTwoPrim")

              .setParameters(new ArrayList<Parameter>())
              .setReturnType(
                  new ReturnType().setType(nameCTTwoPrim).setCollection(true))
          );

    } else if (functionName.equals(nameUFCRTETMedia)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTETMedia")
              .setParameters(new ArrayList<Parameter>())
              .setReturnType(
                  new ReturnType().setType(nameETMedia))
          );

    } else if (functionName.equals(nameUFCRTString)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTString")
              .setParameters(new ArrayList<Parameter>())
              .setReturnType(new ReturnType()
                  .setType(nameString)
              )
          );

    } else if (functionName.equals(nameUFCRTCollCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCollCTTwoPrim")
              .setComposable(true)
              .setParameters(new ArrayList<Parameter>())
              .setReturnType(
                  new ReturnType().setType(nameCTTwoPrim).setCollection(true))
          );

    } else if (functionName.equals(nameUFNRTESMixPrimCollCompTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFNRTESMixPrimCollCompTwoParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterString").setType(nameString),
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))
              .setComposable(false)
              .setReturnType(
                  new ReturnType().setType(nameETMixPrimCollComp).setCollection(true))
          );

    } else if (functionName.equals(nameUFCRTETAllPrimTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTETAllPrimTwoParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterString").setType(nameString),
                  new Parameter().setName("ParameterInt16").setType(nameInt16)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETAllPrim))
          );

    } else if (functionName.equals(nameUFCRTESMixPrimCollCompTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTESMixPrimCollCompTwoParam")
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterString").setType(nameString),
                  new Parameter().setName("ParameterInt16").setType(nameInt16)
                  ))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETMixPrimCollComp).setCollection(true))
          );

    } else if (functionName.equals(nameBFCESTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav).setCollection(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true)),

          new Function()
              .setName("BFCESTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav).setCollection(true),
                  new Parameter().setName("ParameterString").setType(nameString).setCollection(false)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true)),
          new Function()
              .setName("BFCESTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETKeyNav).setCollection(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true)),
          new Function()
              .setName("BFCESTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(
                  Arrays.asList(new Parameter().setName("BindingParam").setType(nameETKeyNav).setCollection(true),
                      new Parameter().setName("ParameterString").setType(nameString).setCollection(false)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true))
          );

    } else if (functionName.equals(nameBFCStringRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function().setName("BFCStringRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameString)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true))
          );

    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTETTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETBaseTwoKeyNavRTETTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETBaseTwoKeyNav)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav)
              )
          );

    } else if (functionName.equals(nameBFCESBaseTwoKeyNavRTESBaseTwoKey)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESBaseTwoKeyNavRTESBaseTwoKey")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETBaseTwoKeyNav)
                      .setCollection(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETBaseTwoKeyNav).setCollection(true))
          );

    } else if (functionName.equals(nameBFCESAllPrimRTCTAllPrim)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESAllPrimRTCTAllPrim")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETAllPrim).setCollection(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameCTAllPrim))
          );

    } else if (functionName.equals(nameBFCESTwoKeyNavRTCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTCTTwoPrim")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav).setCollection(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameCTTwoPrim))
          );

    } else if (functionName.equals(nameBFCESTwoKeyNavRTCollCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTCollCTTwoPrim")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav).setCollection(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameCTTwoPrim).setCollection(true))
          );

    } else if (functionName.equals(nameBFCESTwoKeyNavRTString)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTString")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav).setCollection(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameString))
          );

    } else if (functionName.equals(nameBFCESTwoKeyNavRTCollString)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTCollString")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav).setCollection(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameString).setCollection(true))
          );

    } else if (functionName.equals(nameBFCETTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true))
          );

    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETBaseTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETBaseTwoKeyNav)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true))
          );

    } else if (functionName.equals(nameBFCSINavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCSINavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true))
          );

    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTESBaseTwoKey)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETBaseTwoKeyNavRTESBaseTwoKey")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETBaseTwoKeyNav)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETBaseTwoKeyNav).setCollection(true))
          );

    } else if (functionName.equals(nameBFCCollStringRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCCollStringRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameString).setCollection(true)))
              .setComposable(true)
              .setReturnType(new ReturnType().setType(nameETTwoKeyNav).setCollection(true))
          );

    } else if (functionName.equals(nameBFCCTPrimCompRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCCTPrimCompRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameCTPrimComp)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true))
          );

    } else if (functionName.equals(nameBFCCTPrimCompRTESBaseTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCCTPrimCompRTESBaseTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameCTPrimComp)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETBaseTwoKeyNav).setCollection(true))
          );

    } else if (functionName.equals(nameBFCCollCTPrimCompRTESAllPrim)) {
      return Arrays.asList(
          new Function()
              .setName("BFCCollCTPrimCompRTESAllPrim")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameCTPrimComp).setCollection(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETAllPrim).setCollection(true))
          );

    } else if (functionName.equals(nameBFCESTwoKeyNavRTTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav).setCollection(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav))
          );

    } else if (functionName.equals(nameBFCESKeyNavRTETKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESKeyNavRTETKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETKeyNav).setCollection(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETKeyNav))
          );

    } else if (functionName.equals(nameBFCETKeyNavRTETKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETKeyNavRTETKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETKeyNav)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETKeyNav))
          );
    } else if (functionName.equals(nameBFESTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFESTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav).setCollection(true)))
              .setComposable(true)
              .setReturnType(new ReturnType().setType(nameETTwoKeyNav).setCollection(true))

          );

    } else if (functionName.equals(nameBFCETTwoKeyNavRTETTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETTwoKeyNavRTETTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav))
          );

    } else if (functionName.equals(nameBFCETTwoKeyNavRTCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETTwoKeyNavRTCTTwoPrim")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameCTTwoPrim))
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTStringParam)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTStringParam")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETTwoKeyNav).setCollection(true),
                  new Parameter().setName("ParameterComplex").setType(nameCTTwoPrim)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameString))
          );

    } else if (functionName.equals(nameBFCESKeyNavRTETKeyNavParam)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESKeyNavRTETKeyNavParam")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameETKeyNav).setCollection(true),
                  new Parameter().setName("ParameterString").setType(nameString)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETKeyNav))
          );
    } else if (functionName.equals(nameBFCCTPrimCompRTETTwoKeyNavParam)) {
      return Arrays.asList(
          new Function()
              .setName("BFCCTPrimCompRTETTwoKeyNavParam")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameCTPrimComp),
                  new Parameter().setName("ParameterString").setType(nameString)))
              .setComposable(true)
              .setReturnType(new ReturnType()
                  .setType(nameETTwoKeyNav)
              )
          );
    } else if (functionName.equals(nameBAETTwoKeyNavRTETTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BAETTwoKeyNavRTETTwoKeyNav")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameInt16).setCollection(true),
                  new Parameter().setName("ParameterString").setType(nameString).setNullable(true)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameCTTwoPrim).setCollection(true))
          );
    } else if (functionName.equals(nameBFCCTPrimCompRTESTwoKeyNavParam)) {
      return Arrays.asList(
          new Function()
              .setName("BFCCTPrimCompRTESTwoKeyNavParam")
              .setBound(true)
              .setParameters(Arrays.asList(
                  new Parameter().setName("BindingParam").setType(nameCTPrimComp),
                  new Parameter().setName("ParameterString").setType(nameString)))
              .setComposable(true)
              .setReturnType(
                  new ReturnType().setType(nameETTwoKeyNav).setCollection(true))
          );
    }

    return null;
  }

  @Override
  public Singleton getSingleton(final FullQualifiedName entityContainer, final String name) throws ODataException {
    if (entityContainer.equals(nameContainer)) {

      if (name.equals("SI")) {
        return new Singleton()
            .setName("SI")
            .setType(nameETTwoPrim);

      } else if (name.equals("SINav")) {
        return new Singleton()
            .setName("SINav")
            .setType(nameETTwoKeyNav)
            .setNavigationPropertyBindings(Arrays.asList(
                new NavigationPropertyBinding()
                    .setPath("NavPropertyETTwoKeyNavMany")
                    .setTarget(new Target().setTargetName("ESTwoKeyNav"))));

      } else if (name.equals("SIMedia")) {
        return new Singleton()
            .setName("SIMedia")
            .setType(nameETMedia);
      }
    }
    return null;
  };
}
