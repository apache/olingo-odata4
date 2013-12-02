/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.producer.core.testutil;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.helper.EntityContainerInfo;
import org.apache.olingo.odata4.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.provider.ActionImport;
import org.apache.olingo.odata4.commons.api.edm.provider.ComplexType;
import org.apache.olingo.odata4.commons.api.edm.provider.EdmProviderAdapter;
import org.apache.olingo.odata4.commons.api.edm.provider.EntitySet;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityType;
import org.apache.olingo.odata4.commons.api.edm.provider.EnumMember;
import org.apache.olingo.odata4.commons.api.edm.provider.EnumType;
import org.apache.olingo.odata4.commons.api.edm.provider.Function;
import org.apache.olingo.odata4.commons.api.edm.provider.FunctionImport;
import org.apache.olingo.odata4.commons.api.edm.provider.NavigationProperty;
import org.apache.olingo.odata4.commons.api.edm.provider.Parameter;
import org.apache.olingo.odata4.commons.api.edm.provider.Property;
import org.apache.olingo.odata4.commons.api.edm.provider.PropertyRef;
import org.apache.olingo.odata4.commons.api.edm.provider.ReferentialConstraint;
import org.apache.olingo.odata4.commons.api.edm.provider.ReturnType;
import org.apache.olingo.odata4.commons.api.edm.provider.Singleton;
import org.apache.olingo.odata4.commons.api.edm.provider.Action;
import org.apache.olingo.odata4.commons.api.exception.ODataException;
import org.apache.olingo.odata4.commons.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;

public class EdmTechProvider extends EdmProviderAdapter {

  private static final String nameSpace = "com.sap.odata.test1";

  private static final FullQualifiedName nameBFCCollCTPrimCompRTESAllPrim =
      new FullQualifiedName(nameSpace, "BFCCollCTPrimCompRTESAllPrim");

  private static final FullQualifiedName nameBFCCollStringRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCCollStringRTESTwoKeyNav");

  private static final FullQualifiedName nameBFCCTPrimCompRTESBaseTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCCTPrimCompRTESBaseTwoKeyNav");

  private static final FullQualifiedName nameBFCCTPrimCompRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCCTPrimCompRTESTwoKeyNav");

  private static final FullQualifiedName nameBFCCTPrimCompRTETTwoKeyNavParam =
      new FullQualifiedName(nameSpace, "BFCCTPrimCompRTETTwoKeyNavParam");

  private static final FullQualifiedName nameBFCESAllPrimRTCTAllPrim =
      new FullQualifiedName(nameSpace, "BFCESAllPrimRTCTAllPrim");

  private static final FullQualifiedName nameBFCESBaseTwoKeyNavRTESBaseTwoKey =
      new FullQualifiedName(nameSpace, "BFCESBaseTwoKeyNavRTESBaseTwoKey");

  private static final FullQualifiedName nameBFCESKeyNavRTETKeyNav = new FullQualifiedName(nameSpace,
      "BFCESKeyNavRTETKeyNav");

  private static final FullQualifiedName nameBFCESKeyNavRTETKeyNavParam =
      new FullQualifiedName(nameSpace, "BFCESKeyNavRTETKeyNavParam");

  private static final FullQualifiedName nameBFCESTwoKeyNavRTCollCTTwoPrim =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTCollCTTwoPrim");

  private static final FullQualifiedName nameBFCESTwoKeyNavRTCollString =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTCollString");

  private static final FullQualifiedName nameBFCESTwoKeyNavRTCTTwoPrim =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTCTTwoPrim");

  private static final FullQualifiedName nameBFCESTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTESTwoKeyNav");

  private static final FullQualifiedName nameBFCESTwoKeyNavRTString =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTString");

  private static final FullQualifiedName nameBFCESTwoKeyNavRTStringParam =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTStringParam");

  private static final FullQualifiedName nameBFCESTwoKeyNavRTTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCESTwoKeyNavRTTwoKeyNav");

  private static final FullQualifiedName nameBFCETBaseTwoKeyNavRTESBaseTwoKey =
      new FullQualifiedName(nameSpace, "BFCETBaseTwoKeyNavRTESBaseTwoKey");

  private static final FullQualifiedName nameBFCETBaseTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCETBaseTwoKeyNavRTESTwoKeyNav");

  private static final FullQualifiedName nameBFCETBaseTwoKeyNavRTETTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCETBaseTwoKeyNavRTETTwoKeyNav");

  private static final FullQualifiedName nameBFCETKeyNavRTETKeyNav =
      new FullQualifiedName(nameSpace, "BFCETKeyNavRTETKeyNav");

  private static final FullQualifiedName nameBFCETTwoKeyNavRTCTTwoPrim =
      new FullQualifiedName(nameSpace, "BFCETTwoKeyNavRTCTTwoPrim");

  private static final FullQualifiedName nameBFCETTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCETTwoKeyNavRTESTwoKeyNav");

  private static final FullQualifiedName nameBFCETTwoKeyNavRTETTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCETTwoKeyNavRTETTwoKeyNav");

  private static final FullQualifiedName nameBFCSINavRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCSINavRTESTwoKeyNav");

  private static final FullQualifiedName nameBFCStringRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFCStringRTESTwoKeyNav");

  private static final FullQualifiedName nameBFESTwoKeyNavRTESTwoKeyNav =
      new FullQualifiedName(nameSpace, "BFESTwoKeyNavRTESTwoKeyNav");

  private static final FullQualifiedName nameBinary = EdmPrimitiveTypeKind.Binary.getFullQualifiedName();

  private static final FullQualifiedName nameBoolean = EdmPrimitiveTypeKind.Boolean.getFullQualifiedName();

  public static final FullQualifiedName nameByte = EdmPrimitiveTypeKind.Byte.getFullQualifiedName();

  private static final FullQualifiedName nameContainer = new FullQualifiedName(nameSpace, "Container");
  public static final FullQualifiedName nameCTPrim = new FullQualifiedName(nameSpace, "CTPrim");
  public static final FullQualifiedName nameCTAllPrim = new FullQualifiedName(nameSpace, "CTAllPrim");

  private static final FullQualifiedName nameCTBase = new FullQualifiedName(nameSpace, "CTBase");

  public static final FullQualifiedName nameCTCollAllPrim =
      new FullQualifiedName(nameSpace, "CTCollAllPrim");

  public static final FullQualifiedName nameCTCompCollComp =
      new FullQualifiedName(nameSpace, "CTCompCollComp");

  public static final FullQualifiedName nameCTCompComp = new FullQualifiedName(nameSpace, "CTCompComp");

  private static final FullQualifiedName nameCTMixPrimCollComp = new FullQualifiedName(nameSpace,
      "CTMixPrimCollComp");

  public static final FullQualifiedName nameCTNavFiveProp =
      new FullQualifiedName(nameSpace, "CTNavFiveProp");

  public static final FullQualifiedName nameCTPrimComp = new FullQualifiedName(nameSpace, "CTPrimComp");

  public static final FullQualifiedName nameCTPrimEnum = new FullQualifiedName(nameSpace, "CTPrimEnum");

  private static final FullQualifiedName nameCTTwoBase = new FullQualifiedName(nameSpace, "CTTwoBase");

  public static final FullQualifiedName nameCTTwoPrim = new FullQualifiedName(nameSpace, "CTTwoPrim");

  public static final FullQualifiedName nameDate = EdmPrimitiveTypeKind.Date.getFullQualifiedName();

  public static final FullQualifiedName nameDateTimeOffset =
      EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName();

  public static final FullQualifiedName nameDecimal = EdmPrimitiveTypeKind.Decimal.getFullQualifiedName();

  public static final FullQualifiedName nameDouble = EdmPrimitiveTypeKind.Double.getFullQualifiedName();

  public static final FullQualifiedName nameDuration = EdmPrimitiveTypeKind.Duration.getFullQualifiedName();

  public static final FullQualifiedName nameENString = new FullQualifiedName(nameSpace, "ENString");

  private static final FullQualifiedName nameETAllKey = new FullQualifiedName(nameSpace, "ETAllKey");

  private static final FullQualifiedName nameETAllNullable = new FullQualifiedName(nameSpace, "ETAllNullable");

  private static final FullQualifiedName nameETAllPrim = new FullQualifiedName(nameSpace, "ETAllPrim");

  private static final FullQualifiedName nameETBase = new FullQualifiedName(nameSpace, "ETBase");

  private static final FullQualifiedName nameETBaseTwoKeyNav = new FullQualifiedName(nameSpace, "ETBaseTwoKeyNav");

  private static final FullQualifiedName nameETBaseTwoKeyTwoPrim = new FullQualifiedName(nameSpace,
      "ETBaseTwoKeyTwoPrim");

  public static final FullQualifiedName nameETCollAllPrim = new FullQualifiedName(nameSpace, "ETCollAllPrim");

  private static final FullQualifiedName nameETCompAllPrim = new FullQualifiedName(nameSpace, "ETCompAllPrim");

  private static final FullQualifiedName nameETCompCollAllPrim = new FullQualifiedName(nameSpace, "ETCompCollAllPrim");

  private static final FullQualifiedName nameETCompCollComp = new FullQualifiedName(nameSpace, "ETCompCollComp");

  private static final FullQualifiedName nameETCompComp = new FullQualifiedName(nameSpace, "ETCompComp");

  private static final FullQualifiedName nameETCompMixPrimCollComp = new FullQualifiedName(nameSpace,
      "ETCompMixPrimCollComp");

  public static final FullQualifiedName nameETKeyNav = new FullQualifiedName(nameSpace, "ETKeyNav");

  private static final FullQualifiedName nameETKeyTwoKeyComp = new FullQualifiedName(nameSpace, "ETKeyTwoKeyComp");

  private static final FullQualifiedName nameETMedia = new FullQualifiedName(nameSpace, "ETMedia");

  private static final FullQualifiedName nameETMixPrimCollComp = new FullQualifiedName(nameSpace, "ETMixPrimCollComp");

  private static final FullQualifiedName nameETServerSidePaging =
      new FullQualifiedName(nameSpace, "ETServerSidePaging");

  private static final FullQualifiedName nameETTwoBase = new FullQualifiedName(nameSpace, "ETTwoBase");

  private static final FullQualifiedName nameETTwoBaseTwoKeyTwoPrim = new FullQualifiedName(nameSpace,
      "ETTwoBaseTwoKeyTwoPrim");

  public static final FullQualifiedName nameETTwoKeyNav = new FullQualifiedName(nameSpace, "ETTwoKeyNav");

  public static final FullQualifiedName nameETTwoKeyTwoPrim = new FullQualifiedName(nameSpace, "ETTwoKeyTwoPrim");

  public static final FullQualifiedName nameETTwoPrim = new FullQualifiedName(nameSpace, "ETTwoPrim");

  public static final FullQualifiedName nameGuit = EdmPrimitiveTypeKind.Guid.getFullQualifiedName();

  public static final FullQualifiedName nameInt16 = EdmPrimitiveTypeKind.Int16.getFullQualifiedName();

  public static final FullQualifiedName nameInt32 = EdmPrimitiveTypeKind.Int32.getFullQualifiedName();

  public static final FullQualifiedName nameInt64 = EdmPrimitiveTypeKind.Int64.getFullQualifiedName();

  public static final FullQualifiedName nameSByte = EdmPrimitiveTypeKind.SByte.getFullQualifiedName();

  public static final FullQualifiedName nameSingle = EdmPrimitiveTypeKind.Single.getFullQualifiedName();

  public static final FullQualifiedName nameString = EdmPrimitiveTypeKind.String.getFullQualifiedName();

  public static final FullQualifiedName nameTimeOfDay = EdmPrimitiveTypeKind.TimeOfDay.getFullQualifiedName();

  private static final FullQualifiedName nameUARTCompCollParam = new FullQualifiedName(nameSpace, "UARTCompCollParam");

  private static final FullQualifiedName nameUARTCompParam = new FullQualifiedName(nameSpace, "UARTCompParam");

  private static final FullQualifiedName nameUARTETCollAllPrimParam = new FullQualifiedName(nameSpace,
      "UARTETCollAllPrimParam");

  private static final FullQualifiedName nameUARTETParam = new FullQualifiedName(nameSpace, "UARTETParam");

  private static final FullQualifiedName nameUARTPrimCollParam = new FullQualifiedName(nameSpace, "UARTPrimCollParam");

  private static final FullQualifiedName nameUARTPrimParam = new FullQualifiedName(nameSpace, "UARTPrimParam");

  private static final FullQualifiedName nameUFCRTCollCTTwoPrim =
      new FullQualifiedName(nameSpace, "UFCRTCollCTTwoPrim");

  private static final FullQualifiedName nameUFCRTCollString = new FullQualifiedName(nameSpace, "UFCRTCollString");

  private static final FullQualifiedName nameUFCRTCollStringTwoParam = new FullQualifiedName(nameSpace,
      "UFCRTCollStringTwoParam");

  private static final FullQualifiedName nameUFCRTCTAllPrimTwoParam = new FullQualifiedName(nameSpace,
      "UFCRTCTAllPrimTwoParam");

  private static final FullQualifiedName nameUFCRTCTTwoPrim = new FullQualifiedName(nameSpace, "UFCRTCTTwoPrim");

  private static final FullQualifiedName nameUFCRTCTTwoPrimParam = new FullQualifiedName(nameSpace,
      "UFCRTCTTwoPrimParam");

  private static final FullQualifiedName nameUFCRTESMixPrimCollCompTwoParam = new FullQualifiedName(nameSpace,
      "UFCRTESMixPrimCollCompTwoParam");

  private static final FullQualifiedName nameUFCRTESTwoKeyNavParam = new FullQualifiedName(nameSpace,
      "UFCRTESTwoKeyNavParam");

  private static final FullQualifiedName nameUFCRTETAllPrimTwoParam = new FullQualifiedName(nameSpace,
      "UFCRTETAllPrimTwoParam");

  private static final FullQualifiedName nameUFCRTETKeyNav = new FullQualifiedName(nameSpace, "UFCRTETKeyNav");

  private static final FullQualifiedName nameUFCRTETMedia = new FullQualifiedName(nameSpace, "UFCRTETMedia");

  private static final FullQualifiedName nameUFCRTETTwoKeyNavParam = new FullQualifiedName(nameSpace,
      "UFCRTETTwoKeyNavParam");

  private static final FullQualifiedName nameUFCRTETTwoKeyNavParamCTTwoPrim = new FullQualifiedName(nameSpace,
      "UFCRTETTwoKeyNavParamCTTwoPrim");

  private static final FullQualifiedName nameUFCRTString = new FullQualifiedName(nameSpace, "UFCRTString");

  private static final FullQualifiedName nameUFCRTStringTwoParam = new FullQualifiedName(nameSpace,
      "UFCRTStringTwoParam");

  private static final FullQualifiedName nameUFNRTESMixPrimCollCompTwoParam = new FullQualifiedName(nameSpace,
      "UFNRTESMixPrimCollCompTwoParam");

  private static final FullQualifiedName nameUFNRTInt16 = new FullQualifiedName(nameSpace, "UFNRTInt16");

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

  // Properties typed as collection of simple types
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
      .setType(nameGuit)
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
  NavigationProperty navPropertyETTwoKeyNavOne_ETTwoKeyNav = new NavigationProperty()
      .setName("NavPropertyETTwoKeyNavOne")
      .setType(nameETTwoKeyNav)
      .setNullable(false);

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

  /*
   * TODO add stream property
   * Property propertyStream = new Property()
   * .setName("PropertyStream")
   * .setType(EdmStream.getFullQualifiedName());
   */

  Property propertyComplex_CTCompComp = new Property()
      .setName("PropertyComplex")
      .setType(nameCTCompComp);
  Property propertyComplex_CTNavFiveProp = new Property()
      .setName("PropertyComplex")
      .setType(nameCTNavFiveProp);
  Property propertyComplex_CTPrimComp_NotNullable = new Property()
      .setName("PropertyComplex")
      .setType(nameCTPrimComp)
      .setNullable(false);
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
      .setType(nameCTPrimEnum)
      .setNullable(false);
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
      .setType(nameGuit);
  Property propertyInt16 = new Property()
      .setName("PropertyInt16")
      .setType(nameInt16);

  Property propertyInt16_NotNullable = new Property()
      .setName("PropertyInt16")
      .setType(nameInt16)
      .setNullable(false);

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
      .setType(nameString)
      .setNullable(false);
  Property propertyTimeOfDay = new Property().setName("PropertyTimeOfDay")
      .setType(nameTimeOfDay);

  @Override
  public List<Action> getActions(final FullQualifiedName actionName) throws ODataException {
    if (actionName.equals(nameUARTPrimParam)) {
      return Arrays.asList(
          new Action().setName("UARTPrimParam")
              .setBound(false)
              .setReturnType(new ReturnType().setType(nameString))
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16")
                      .setType(nameInt16)))

          );
    } else if (actionName.equals(nameUARTPrimCollParam)) {
      return Arrays.asList(
          new Action().setName("UARTPrimCollParam")
              .setBound(false)
              .setReturnType(
                  new ReturnType()
                      .setType(nameString).setCollection(true))
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16")
                      .setType(nameInt16)))
          );
    } else if (actionName.equals(nameUARTCompParam)) {
      return Arrays.asList(
          new Action().setName("UARTCompParam")
              .setBound(false)
              .setReturnType(
                  new ReturnType()
                      .setType(nameCTTwoPrim))
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16")
                      .setType(nameInt16)))
          );
    } else if (actionName.equals(nameUARTCompCollParam)) {
      return Arrays.asList(
          new Action().setName("UARTCompCollParam")
              .setBound(false)
              .setReturnType(
                  new ReturnType()
                      .setCollection(true)
                      .setType(nameCTTwoPrim))
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16")
                      .setType(nameInt16)))
          );
    } else if (actionName.equals(nameUARTETParam)) {
      return Arrays.asList(
          new Action().setName("UARTCompCollParam")
              .setBound(false)
              .setReturnType(
                  new ReturnType()
                      .setType(nameETTwoKeyTwoPrim))
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16")
                      .setType(nameInt16)))
          );
    } else if (actionName.equals(nameUARTETCollAllPrimParam)) {
      return Arrays.asList(
          new Action().setName("UARTETCollAllPrimParam")
              .setBound(false)
              .setReturnType(
                  new ReturnType()
                      .setType(nameETCollAllPrim)
                      .setCollection(true))
                      
              .setParameters(Arrays.asList(
                  new Parameter().setName("ParameterInt16")
                      .setType(nameInt16)))
          );
    }

    // complete 20131209
    return null;
  }

  @Override
  public ComplexType
      getComplexType(final FullQualifiedName
          complexTypeName) throws
          ODataException {
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
              propertyTimeOfDay/* ,TODO propertyStream */
              ));

    } else if (complexTypeName.equals(nameCTCollAllPrim)) {
      return new ComplexType()
          .setName("CTCollAllPrim")
          .setProperties(Arrays.asList(collPropertyString,
              collPropertyBoolean, collPropertyByte,
              collPropertySByte,
              collPropertyInt16, collPropertyInt32, collPropertyInt64,
              collPropertySingle, collPropertyDouble,
              collPropertyDecimal, collPropertyBinary,
              collPropertyDate, collPropertyDateTimeOffset,
              collPropertyDuration, collPropertyGuid,
              collPropertyTimeOfDay /* ,TODO collectionPropertyStream */));
    } else if (complexTypeName.equals(nameCTTwoPrim)) {
      return new ComplexType()
          .setName("CTTwoPrim")
          .setProperties(Arrays.asList(propertyInt16, propertyString));
    } else if (complexTypeName.equals(nameCTMixPrimCollComp)) {
      return new ComplexType()
          .setName("CTMixPrimCollComp")
          .setProperties(Arrays.asList(propertyInt16, collPropertyString,
              propertyComplex_CTTwoPrim, collPropertyComplex_CTTwoPrim));
    } else if (complexTypeName.equals(nameCTBase)) {
      return new ComplexType()
          .setName("CTBase")
          .setProperties(
              Arrays.asList(new Property().setName("AdditionalPropString").setType(
                  new FullQualifiedName("Edm", "String"))));
    } else if (complexTypeName.equals(nameCTTwoBase)) {
      return new ComplexType()
          .setName("CTTwoBase")
          .setProperties(
              Arrays.asList(new Property().setName("AdditionalPropString").setType(
                  new FullQualifiedName("Edm", "String"))));
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
          .setProperties(Arrays.asList(propertyInt16) /* TODO add nav prop */);
    } else if (complexTypeName.equals(nameCTPrimEnum)) {
      return new ComplexType()
          .setName("CTPrimEnum")
          .setProperties(Arrays.asList(propertyInt16, propertyEnumString_ENString) /* TODO add nav prop */);
    }
    // complete 20131205
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
            .setType(nameETAllPrim)
            .setIncludeInServiceDocument(false);
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
      }
    }

    return null;
  }

  @Override
  public EntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
    List<PropertyRef> oneKeyPropertyInt16 = Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16"));

    if (entityTypeName.equals(nameETAllPrim)) {
      return new EntityType()
          .setName("ETAllPrim")
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString,
              propertyBoolean, propertyByte, propertySByte,
              propertyInt32, propertyInt64,
              propertySingle, propertyDouble, propertyDecimal,
              propertyBinary, propertyDate, propertyDateTimeOffset,
              propertyDuration, propertyGuid,
              propertyTimeOfDay /* TODO add stream property */))
          .setKey(oneKeyPropertyInt16);

    } else if (entityTypeName.equals(nameETCollAllPrim)) {
      return new EntityType()
          .setName("ETCollAllPrim")
          .setProperties(Arrays.asList(
              propertyInt16_NotNullable,
              collPropertyString, collPropertyBoolean,
              collPropertyByte, collPropertySByte,
              collPropertyInt16, collPropertyInt32, collPropertyInt64,
              collPropertySingle, collPropertyDouble, collPropertyDecimal,
              collPropertyBinary, collPropertyDate, collPropertyDateTimeOffset,
              collPropertyDuration,
              collPropertyGuid,
              collPropertyTimeOfDay /* TODO add stream property */))
          .setKey(oneKeyPropertyInt16);
    } else if (entityTypeName.equals(nameETTwoPrim)) {
      return new EntityType()
          .setName("ETTwoPrim")
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString))
          .setKey(oneKeyPropertyInt16);
    } else if (entityTypeName.equals(nameETMixPrimCollComp)) {
      return new EntityType()
          .setName("ETMixPrimCollComp")
          .setProperties(Arrays.asList(propertyInt16_NotNullable, collPropertyString,
              propertyComplex_CTTwoPrim, collPropertyComplex_CTTwoPrim))
          .setKey(oneKeyPropertyInt16);
    } else if (entityTypeName.equals(nameETTwoKeyTwoPrim)) {
      return new EntityType()
          .setName("ETTwoKeyTwoPrim")
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString))
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyInt16"),
              new PropertyRef().setPropertyName("PropertyString")));
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
          .setProperties(Arrays.asList(new Property()
              .setName("AdditionalPropertyString_6")
              .setType(nameString))
          );
    } else if (entityTypeName.equals(nameETAllKey)) {
      return new EntityType()
          .setName("ETAllKey")
          .setProperties(Arrays.asList(
              propertyString, propertyBoolean,
              propertyByte, propertySByte,
              propertyInt16, propertyInt32, propertyInt64,
              propertyDecimal, propertyDate,
              propertySingle, propertyDouble, propertyDateTimeOffset,
              propertyDuration, propertyGuid,
              propertyTimeOfDay /* TODO add stream property */))
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
              new PropertyRef().setPropertyName("PropertyTimeOfDay")));
    } else if (entityTypeName.equals(nameETCompAllPrim)) {
      return new EntityType()
          .setName("ETCompAllPrim")
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyComplex_CTAllPrim))
          .setKey(oneKeyPropertyInt16);
    } else if (entityTypeName.equals(nameETCompCollAllPrim)) {
      return new EntityType()
          .setName("ETCompAllPrim")
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyComplex_CTCollAllPrim))
          .setKey(oneKeyPropertyInt16);
    } else if (entityTypeName.equals(nameETCompComp)) {
      return new EntityType()
          .setName("ETCompComp")
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyComplex_CTCompComp))
          .setKey(oneKeyPropertyInt16);
    } else if (entityTypeName.equals(nameETCompCollComp)) {
      return new EntityType()
          .setName("ETCompCollComp")
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyComplex_CTCompCollComp))
          .setKey(oneKeyPropertyInt16);
    } else if (entityTypeName.equals(nameETMedia)) {
      return new EntityType()
          .setName("ETCompCollComp")
          .setProperties(Arrays.asList(propertyInt16_NotNullable))
          .setKey(oneKeyPropertyInt16)
          .setHasStream(true);
    } else if (entityTypeName.equals(nameETKeyTwoKeyComp)) {
      return new EntityType()
          .setName("ETKeyTwoKeyComp")
          .setProperties(
              Arrays.asList(propertyInt16_NotNullable, propertyComplex_CTTwoPrim, propertyComplexComplex_CTCompComp))
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyInt16"),
              new PropertyRef().setPropertyName("PropertyComplex/PropertyInt16").setAlias("KeyAlias1"),
              new PropertyRef().setPropertyName("PropertyComplex/PropertyString").setAlias("KeyAlias2"),
              new PropertyRef().setPropertyName("PropertyComplexComplex/PropertyComplex/PropertyString")
                  .setAlias("KeyAlias3")));
    } else if (entityTypeName.equals(nameETServerSidePaging)) {
      return new EntityType()
          .setName("ETKeyTwoKeyComp")
          .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString))
          .setKey(oneKeyPropertyInt16);
    } else if (entityTypeName.equals(nameETAllNullable)) {
      return new EntityType()
          .setName("ETAllNullable")
          .setProperties(Arrays.asList(new Property()
              .setName("PropertyKey").setType(nameInt16).setNullable(false),
              propertyInt16,
              propertyString, propertyBoolean,
              propertyByte, propertySByte,
              propertyInt32, propertyInt64,
              propertySingle, propertyDouble,
              propertyDecimal, propertyBinary, propertyDate,
              propertyDateTimeOffset,
              propertyDuration, propertyGuid, propertyTimeOfDay /* TODO add stream */,
              collPropertyString, collPropertyBoolean,
              collPropertyByte, collPropertySByte,
              collPropertyInt16,
              collPropertyInt32, collPropertyInt64,
              collPropertySingle, collPropertyDouble,
              collPropertyDecimal, collPropertyBinary, collPropertyDate,
              collPropertyDateTimeOffset,
              collPropertyDuration, collPropertyGuid, collPropertyTimeOfDay /* TODO add stream, */
              ))
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyKey")));
    } else if (entityTypeName.equals(nameETKeyNav)) {
      return new EntityType()
          .setName("ETCollAllPrim")
          .setProperties(
              Arrays.asList(
                  propertyInt16_NotNullable, propertyString_NotNullable, propertyComplex_CTNavFiveProp,
                  propertyComplexAllPrim_CTAllPrim, propertyComplexTwoPrim_CTTwoPrim,
                  collPropertyString, collPropertyInt16, collPropertyComplex_CTPrimComp))
          .setNavigationProperties(Arrays.asList(
              navPropertyETTwoKeyNavOne_ETTwoKeyNav, collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              navPropertyETKeyNavOne_ETKeyNav, collectionNavPropertyETKeyNavMany_ETKeyNav,
              navPropertyETMediaOne_ETMedia, collectionNavPropertyETMediaMany_ETMedia
              ))
          .setKey(Arrays.asList(new PropertyRef().setPropertyName("PropertyInt16")));
    } else if (entityTypeName.equals(nameETTwoKeyNav)) {
      return new EntityType()
          .setName("ETTwoKeyNav")
          .setProperties(
              Arrays.asList(propertyInt16, propertyString, propertyComplex_CTPrimComp_NotNullable,
                  propertyComplexEnum_CTPrimEnum_NotNullable,
                  collPropertyComplex_CTPrimComp,
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
              collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              navPropertyETTwoKeyNavOne_ETTwoKeyNav
              ))
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyInt16"),
              new PropertyRef().setPropertyName("PropertyString")));
    } else if (entityTypeName.equals(nameETBaseTwoKeyNav)) {
      return new EntityType()
          .setName("ETBaseTwoKeyNav")
          .setProperties(Arrays.asList(propertyDate, propertyComplex_CTPrimComp_NotNullable))
          .setNavigationProperties(Arrays.asList(
              collectionNavPropertyETKeyNavMany_ETKeyNav,
              navPropertyETKeyNavOne_ETKeyNav,
              collectionNavPropertyETTwoKeyNavMany_ETTwoKeyNav,
              navPropertyETTwoKeyNavOne_ETTwoKeyNav
              ))
          .setHasStream(true);
    } else if (entityTypeName.equals(nameETCompMixPrimCollComp)) {
      return new EntityType()
          .setName("ETCompMixPrimCollComp")
          .setProperties(Arrays.asList(propertyInt16, propertyMixedPrimCollComp_CTMixPrimCollComp))
          .setKey(oneKeyPropertyInt16);
    }

    // complete 20131205
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
    // complete 20131205
    return null;
  }

  @Override
  public List<Function> getFunctions(final FullQualifiedName functionName) throws ODataException {
    ReturnType returnTypeString = new ReturnType()
        .setType(nameString);

    ReturnType returnTypeString_NotNullable = new ReturnType()
        .setType(nameString)
        .setNullable(false);

    ReturnType returnTypeETTwoKeyNav_NotNullable = new ReturnType()
        .setType(nameETTwoKeyNav)
        .setNullable(false);

    ReturnType returnTypeCollectionETTwoKeyNav_NotNullable = new ReturnType()
        .setType(nameETTwoKeyNav)
        .setNullable(false)
        .setCollection(true);

    if (functionName.equals(nameUFNRTInt16)) {
      return Arrays.asList(
          new Function()
              .setName("UFNRTInt16")
              .setBound(false)
              .setReturnType(returnTypeString)
          );
    } else if (functionName.equals(nameUFCRTETKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTETKeyNav")
              .setBound(true)
              .setComposable(true)
              .setReturnType(
                  new ReturnType()
                      .setType(nameETKeyNav)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameUFCRTETTwoKeyNavParam)) {
      return Arrays.asList(
          new Function().setName("UFCRTETTwoKeyNavParam")
              .setBound(false)
              .setComposable(true)
              .setParameters(Arrays.asList(
                  new Parameter()
                      .setName("ParameterInt16")
                      .setType(nameInt16)
                      .setNullable(false)))
              .setReturnType(returnTypeETTwoKeyNav_NotNullable)
          );
    } else if (functionName.equals(nameUFCRTETTwoKeyNavParamCTTwoPrim)) {
      return Arrays.asList(
          new Function().setName("UFCRTETTwoKeyNavParamCTTwoPrim")
              .setBound(false)
              .setComposable(true)
              .setParameters(Arrays.asList(
                  new Parameter()
                      .setName("ParameterCTTwoPrim")
                      .setType(nameCTTwoPrim)
                      .setNullable(false)))
              .setReturnType(returnTypeETTwoKeyNav_NotNullable)

          );
    } else if (functionName.equals(nameUFCRTStringTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTStringTwoParam")
              .setBound(false)
              .setComposable(true)
              .setParameters(Arrays.asList(
                  new Parameter()
                      .setName("ParameterString")
                      .setType(nameString)
                      .setNullable(false),
                  new Parameter()
                      .setName("ParameterInt16")
                      .setType(nameInt16)
                      .setNullable(false)))
              .setReturnType(returnTypeString_NotNullable)

          );
    } else if (functionName.equals(nameUFCRTESTwoKeyNavParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTESTwoKeyNavParam")
              .setBound(false)
              .setComposable(true)
              .setParameters(Arrays.asList(
                  new Parameter()
                      .setName("ParameterInt16")
                      .setType(nameInt16)
                      .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETTwoKeyNav)
                      .setNullable(false).setCollection(true))

          );
    } else if (functionName.equals(nameUFCRTString)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTString")
              .setBound(true)
              .setComposable(true)
              .setReturnType(returnTypeString_NotNullable)
          );
    } else if (functionName.equals(nameUFCRTCollStringTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCollStringTwoParam")
              .setBound(false)
              .setComposable(true)
              .setParameters(Arrays.asList(
                  new Parameter()
                      .setName("ParameterString")
                      .setType(nameString)
                      .setNullable(false),
                  new Parameter()
                      .setName("ParameterInt16")
                      .setType(nameInt16)
                      .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameString)
                      .setNullable(false)
                      .setCollection(true))

          );
    } else if (functionName.equals(nameUFCRTCollString)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCollString")
              .setBound(false)
              .setComposable(true)
              .setReturnType(
                  new ReturnType()
                      .setType(nameString)
                      .setNullable(false)
                      .setCollection(true))

          );
    } else if (functionName.equals(nameUFCRTCTAllPrimTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCTAllPrimTwoParam")
              .setBound(false)
              .setComposable(true)
              .setParameters(Arrays.asList(
                  new Parameter()
                      .setName("ParameterString")
                      .setType(nameString)
                      .setNullable(false),
                  new Parameter()
                      .setName("ParameterInt16")
                      .setType(nameInt16)
                      .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameCTAllPrim)
                      .setNullable(false))

          );
    } else if (functionName.equals(nameUFCRTCTTwoPrimParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCTTwoPrimParam")
              .setBound(false)
              .setComposable(true)
              .setParameters(Arrays.asList(
                  new Parameter()
                      .setName("ParameterString")
                      .setType(nameString)
                      .setNullable(false),
                  new Parameter()
                      .setName("ParameterInt16")
                      .setType(nameInt16)
                      .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameCTTwoPrim)
                      .setNullable(false))

          );
    } else if (functionName.equals(nameUFCRTCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCTTwoPrim")
              .setBound(false)
              .setComposable(true)
              .setReturnType(
                  new ReturnType()
                      .setType(nameCTTwoPrim)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameUFCRTCollCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCTTwoPrim")
              .setBound(false)
              .setReturnType(
                  new ReturnType()
                      .setType(nameCTTwoPrim)
                      .setNullable(false)
                      .setCollection(true))
          );
    } else if (functionName.equals(nameUFCRTETMedia)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTETMedia")
              .setBound(false)
              .setReturnType(
                  new ReturnType()
                      .setType(nameETMedia)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameUFCRTString)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTString")
              .setBound(false)
              .setReturnType(returnTypeString_NotNullable)
          );
    } else if (functionName.equals(nameUFCRTCollCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTCollCTTwoPrim")
              .setBound(false)
              .setComposable(true)
              .setReturnType(
                  new ReturnType()
                      .setType(nameCTTwoPrim)
                      .setNullable(false)
                      .setCollection(true))
          );
    } else if (functionName.equals(nameUFNRTESMixPrimCollCompTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFNRTESMixPrimCollCompTwoParam")
              .setBound(false)
              .setComposable(false)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("ParameterString")
                          .setType(nameString)
                          .setNullable(false),
                      new Parameter()
                          .setName("ParameterInt16")
                          .setType(nameInt16)
                          .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETMixPrimCollComp)
                      .setNullable(false)
                      .setCollection(true))
          );

    } else if (functionName.equals(nameUFCRTETAllPrimTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTETAllPrimTwoParam")
              .setBound(false)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("ParameterString")
                          .setType(nameString)
                          .setNullable(false),
                      new Parameter()
                          .setName("ParameterInt16")
                          .setType(nameInt16)
                          .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETAllPrim)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameUFCRTESMixPrimCollCompTwoParam)) {
      return Arrays.asList(
          new Function()
              .setName("UFCRTESMixPrimCollCompTwoParam")
              .setBound(false)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("ParameterString")
                          .setType(nameString)
                          .setNullable(false),
                      new Parameter()
                          .setName("ParameterInt16")
                          .setType(nameInt16)
                          .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETMixPrimCollComp)
                      .setNullable(false)
                      .setCollection(true))
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)
                          .setCollection(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETTwoKeyNav)
                      .setNullable(false)
                      .setCollection(true))
          );
    } else if (functionName.equals(nameBFCStringRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function().setName("BFCStringRTESTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameString)
                          .setNullable(false)))
              .setReturnType(returnTypeCollectionETTwoKeyNav_NotNullable)
          );
    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTETTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETBaseTwoKeyNavRTETTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETBaseTwoKeyNav)
                          .setNullable(false)))
              .setReturnType(returnTypeETTwoKeyNav_NotNullable)
          );
    } else if (functionName.equals(nameBFCESBaseTwoKeyNavRTESBaseTwoKey)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESBaseTwoKeyNavRTESBaseTwoKey")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETBaseTwoKeyNav)
                          .setNullable(false)
                          .setCollection(true)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETBaseTwoKeyNav)
                      .setNullable(false)
                      .setCollection(true))
          );
    } else if (functionName.equals(nameBFCESAllPrimRTCTAllPrim)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESAllPrimRTCTAllPrim")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETAllPrim)
                          .setNullable(false)
                          .setCollection(true)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameCTAllPrim)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTCTTwoPrim")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)
                          .setCollection(true)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameCTTwoPrim)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTCollCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTCollCTTwoPrim")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)
                          .setCollection(true)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameCTTwoPrim)
                      .setNullable(false)
                      .setCollection(true))
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTString)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTString")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)
                          .setCollection(true)))
              .setReturnType(returnTypeString_NotNullable)
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTCollString)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTCollString")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameString)
                      .setNullable(false)
                      .setCollection(true))
          );
    } else if (functionName.equals(nameBFCETTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)))
              .setReturnType(returnTypeCollectionETTwoKeyNav_NotNullable)
          );
    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETBaseTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETBaseTwoKeyNav)
                          .setNullable(false)))
              .setReturnType(returnTypeCollectionETTwoKeyNav_NotNullable)
          );
    } else if (functionName.equals(nameBFCSINavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCSINavRTESTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)))
              .setReturnType(returnTypeCollectionETTwoKeyNav_NotNullable)
          );
    } else if (functionName.equals(nameBFCETBaseTwoKeyNavRTESBaseTwoKey)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETBaseTwoKeyNavRTESBaseTwoKey")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETBaseTwoKeyNav)
                          .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETBaseTwoKeyNav)
                      .setNullable(false)
                      .setCollection(true))
          );
    } else if (functionName.equals(nameBFCCollStringRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCCollStringRTESTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameString)
                          .setNullable(false)
                          .setCollection(true)))
              .setReturnType(returnTypeETTwoKeyNav_NotNullable)
          );
    } else if (functionName.equals(nameBFCCTPrimCompRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCCTPrimCompRTESTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameCTPrimComp)
                          .setNullable(false)))
              .setReturnType(returnTypeETTwoKeyNav_NotNullable)
          );
    } else if (functionName.equals(nameBFCCTPrimCompRTESBaseTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCCTPrimCompRTESBaseTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameCTPrimComp)
                          .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETBaseTwoKeyNav)
                      .setNullable(false)
                      .setCollection(true))
          );
    } else if (functionName.equals(nameBFCCollCTPrimCompRTESAllPrim)) {
      return Arrays.asList(
          new Function()
              .setName("BFCCollCTPrimCompRTESAllPrim")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameCTPrimComp)
                          .setNullable(false)
                          .setCollection(true)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETAllPrim)
                      .setNullable(false)
                      .setCollection(true))
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETAllPrim)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameBFCESKeyNavRTETKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESKeyNavRTETKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETKeyNav)
                          .setNullable(false)
                          .setCollection(true)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETKeyNav)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameBFCETKeyNavRTETKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETKeyNavRTETKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETKeyNav)
                          .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETKeyNav)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameBFESTwoKeyNavRTESTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFESTwoKeyNavRTESTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)
                          .setCollection(true)))
              .setReturnType(returnTypeCollectionETTwoKeyNav_NotNullable)
          );
    } else if (functionName.equals(nameBFCETTwoKeyNavRTETTwoKeyNav)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETTwoKeyNavRTETTwoKeyNav")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)))
              .setReturnType(returnTypeETTwoKeyNav_NotNullable)
          );
    } else if (functionName.equals(nameBFCETTwoKeyNavRTCTTwoPrim)) {
      return Arrays.asList(
          new Function()
              .setName("BFCETTwoKeyNavRTCTTwoPrim")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameCTTwoPrim)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameBFCESTwoKeyNavRTStringParam)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESTwoKeyNavRTStringParam")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETTwoKeyNav)
                          .setNullable(false)
                          .setCollection(true),
                      new Parameter()
                          .setName("ParameterComplex")
                          .setType(nameCTTwoPrim)
                          .setNullable(false)))
              .setReturnType(returnTypeString_NotNullable)
          );
    } else if (functionName.equals(nameBFCESKeyNavRTETKeyNavParam)) {
      return Arrays.asList(
          new Function()
              .setName("BFCESKeyNavRTETKeyNavParam")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameETKeyNav)
                          .setNullable(false)
                          .setCollection(true),
                      new Parameter()
                          .setName("ParameterString")
                          .setType(nameString)
                          .setNullable(false)))
              .setReturnType(
                  new ReturnType()
                      .setType(nameETKeyNav)
                      .setNullable(false))
          );
    } else if (functionName.equals(nameBFCCTPrimCompRTETTwoKeyNavParam)) {
      return Arrays.asList(
          new Function()
              .setName("BFCCTPrimCompRTETTwoKeyNavParam")
              .setBound(true)
              .setComposable(true)
              .setParameters(
                  Arrays.asList(
                      new Parameter()
                          .setName("BindingParam")
                          .setType(nameCTPrimComp)
                          .setNullable(false)
                          .setCollection(true),
                      new Parameter()
                          .setName("ParameterString")
                          .setType(nameString)
                          .setNullable(false)))
              .setReturnType(returnTypeETTwoKeyNav_NotNullable)
          );
    }
    return null;
    // complete 20131210
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
            .setName("SI")
            .setType(nameETTwoKeyNav);
      }

    }
    return null;
  }

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
  public FunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String name)
      throws ODataException {

    if (entityContainer.equals(nameContainer)) {
      if (name.equals("FINRTInt16")) {
        return new FunctionImport()
            .setName("FINRTInt16")
            .setFunction(nameUFNRTInt16)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FINInvisibleRTInt16")) {
        return new FunctionImport()
            .setName("FINInvisibleRTInt16")
            .setFunction(nameUFNRTInt16)
            .setIncludeInServiceDocument(false);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FINInvisible2RTInt16")) {
        return new FunctionImport()
            .setName("FINInvisible2RTInt16")
            .setFunction(nameUFNRTInt16);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTETKeyNav")) {
        return new FunctionImport()
            .setName("FICRTETKeyNav")
            .setFunction(nameUFCRTETKeyNav);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTETTwoKeyNavParam")) {
        return new FunctionImport()
            .setName("FICRTETTwoKeyNavParam")
            .setFunction(nameUFCRTETTwoKeyNavParam)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTStringTwoParam")) {
        return new FunctionImport()
            .setName("FICRTStringTwoParam")
            .setFunction(nameUFCRTStringTwoParam)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTCollStringTwoParam")) {
        return new FunctionImport()
            .setName("FICRTCollStringTwoParam")
            .setFunction(nameUFCRTCollStringTwoParam)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTCTAllPrimTwoParam")) {
        return new FunctionImport()
            .setName("FICRTCTAllPrimTwoParam")
            .setFunction(nameUFCRTCTAllPrimTwoParam)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTESMixPrimCollCompTwoParam")) {
        return new FunctionImport()
            .setName("FICRTESMixPrimCollCompTwoParam")
            .setFunction(nameUFCRTESMixPrimCollCompTwoParam)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FINRTESMixPrimCollCompTwoParam")) {
        return new FunctionImport()
            .setName("FINRTESMixPrimCollCompTwoParam")
            .setFunction(nameUFNRTESMixPrimCollCompTwoParam)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTCollCTTwoPrim")) {
        return new FunctionImport()
            .setName("FICRTCollCTTwoPrim")
            .setFunction(nameUFCRTCollCTTwoPrim)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTETMedia")) {
        return new FunctionImport()
            .setName("FICRTETMedia")
            .setFunction(nameUFCRTETMedia)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTCTTwoPrimParam")) {
        return new FunctionImport()
            .setName("FICRTCTTwoPrimParam")
            .setFunction(nameUFCRTCTTwoPrimParam)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTCTTwoPrim")) {
        return new FunctionImport()
            .setName("FICRTCTTwoPrim")
            .setFunction(nameUFCRTCTTwoPrim)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTCollString")) {
        return new FunctionImport()
            .setName("FICRTCollString")
            .setFunction(nameUFCRTCollString)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTString")) {
        return new FunctionImport()
            .setName("FICRTString")
            .setFunction(nameUFCRTString)
            .setIncludeInServiceDocument(true);

      }
    } else if (entityContainer.equals(nameContainer)) {
      if (name.equals("FICRTESTwoKeyNavParam")) {
        return new FunctionImport()
            .setName("FICRTESTwoKeyNavParam")
            .setFunction(nameUFCRTESTwoKeyNavParam)
            .setIncludeInServiceDocument(true);

      }
    }
    return null;
  }

}
