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
package org.apache.olingo.odata4.producer.core.uri.antlr;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriResourceKind;
import org.apache.olingo.odata4.producer.core.testutil.EdmTechTestProvider;
import org.apache.olingo.odata4.producer.core.testutil.FilterValidator;
import org.apache.olingo.odata4.producer.core.testutil.UriResourcePathValidator;
import org.apache.olingo.odata4.producer.core.testutil.UriValidator;
import org.junit.Test;

public class TestByAbapResourcePath {
  Edm edm = null;
  private final String PropertyBoolean = "PropertyBoolean=true";
  private final String PropertyByte = "PropertyByte=1";
  boolean test;
  private final String PropertyDate = "PropertyDate=2013-09-25";
  private final String PropertyDateTimeOffset = "PropertyDateTimeOffset=2002-10-10T12:00:00-05:00";
  private final String PropertyDecimal = "PropertyDecimal=12";
  private final String PropertyDuration = "PropertyDuration=duration'P10DT5H34M21.123456789012S'";
  private final String PropertyGuid = "PropertyGuid=12345678-1234-1234-1234-123456789012";
  private final String PropertyInt16 = "PropertyInt16=1";
  private final String PropertyInt32 = "PropertyInt32=12";
  private final String PropertyInt64 = "PropertyInt64=64";
  private final String PropertySByte = "PropertySByte=1";
  private final String PropertyString = "PropertyString='ABC'";
  private final String PropertyTimeOfDay = "PropertyTimeOfDay=12:34:55.123456789012";

  private final String allKeys = PropertyString + "," + PropertyInt16 + "," + PropertyBoolean + "," + PropertyByte
      + "," + PropertySByte + "," + PropertyInt32 + "," + PropertyInt64 + "," + PropertyDecimal + "," + PropertyDate
      + "," + PropertyDateTimeOffset + "," + PropertyDuration + "," + PropertyGuid + "," + PropertyTimeOfDay;
  FilterValidator testFilter = null;
  UriResourcePathValidator testPath = null;
  UriValidator testUri = null;

  public TestByAbapResourcePath() {
    edm = new EdmProviderImpl(new EdmTechTestProvider());

    testUri = new UriValidator().setEdm(edm);
    testPath = new UriResourcePathValidator().setEdm(edm);
    testFilter = new FilterValidator().setEdm(edm);

  }

  @Test
  public void runActionImportTests() {
    testUri.run("AIRTPrimParam").isKind(UriInfoKind.resource).goPath().isUriPathInfoKind(UriResourceKind.action);
    testUri.run("AIRTPrimCollParam").isKind(UriInfoKind.resource).goPath().isUriPathInfoKind(UriResourceKind.action);
    testUri.run("AIRTCompParam").isKind(UriInfoKind.resource).goPath().isUriPathInfoKind(UriResourceKind.action);
    testUri.run("AIRTCompCollParam").isKind(UriInfoKind.resource).goPath().isUriPathInfoKind(UriResourceKind.action);
    testUri.run("AIRTETParam").isKind(UriInfoKind.resource).goPath().isUriPathInfoKind(UriResourceKind.action);
    // testUri.run("AIRTPrimParam/invalidElement").isKind(UriInfoKind.resource).goPath().
    //isUriPathInfoKind(UriResourceKind.action);
    // testUri.run("InvalidAction");
  }

  @Test
  public void runGeht() {
    // based from 20.1.2014
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESBaseTwoKeyNav()");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESBaseTwoKeyNav()/$count");
    testUri.run("ESKeyNav(PropertyInt16=1)/CollPropertyComplex/com.sap.odata.test1.BFCCollCTPrimCompRTESAllPrim()");
    testUri
        .run("ESKeyNav(PropertyInt16=1)/CollPropertyComplex/com.sap.odata.test1.BFCCollCTPrimCompRTESAllPrim()/$count");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
            + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNav()");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
            + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNav()/$count");
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTESTwoKeyNav()");
    testUri
        .run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='(''2'')')"
            + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTESTwoKeyNav()");
    testUri
        .run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav/com.sap.odata.test1.BFCESBaseTwoKeyNavRTESBaseTwoKey()");
    testUri
        .run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav/com.sap.odata.test1.BFCESBaseTwoKeyNavRTESBaseTwoKey()"
            + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav");
    testUri
        .run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()"
            + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')"
            + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav");
    testUri.run("ESAllPrim/com.sap.odata.test1.BFCESAllPrimRTCTAllPrim()");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCTTwoPrim()/com.sap.odata.test1.CTBase");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()/$count");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()/NavPropertyETKeyNavOne");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()/NavPropertyETKeyNavOne/$ref");
    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/NavPropertyETMediaOne/$value");
    testUri
        .run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
            + "/NavPropertyETTwoKeyNavOne");
    testUri
        .run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
            + "/NavPropertyETTwoKeyNavOne/PropertyComplex");
    testUri
        .run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
            + "/NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyComplex");
    testUri
        .run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
            + "/NavPropertyETTwoKeyNavOne/PropertyString");
    testUri
        .run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
            + "/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/PropertyString");
    testUri
        .run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()"
            + "/NavPropertyETTwoKeyNavOne/com.sap.odata.test1.ETBaseTwoKeyNav");
    testUri
        .run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()(PropertyInt16=1,PropertyString='2')"
            + "/NavPropertyETTwoKeyNavOne/com.sap.odata.test1.ETTwoBaseTwoKeyNav");
    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComplex");
    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComplex/PropertyInt16");
    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComplex/PropertyInt16/$value");

    testUri
        .run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
            + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase");

    testUri
        .run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
            + "/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
            + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase");
    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyInt16");
    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyInt16/$value");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='2')");
    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()");
    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='3')");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()/$count");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()(PropertyInt16=1,PropertyString='2')");

    testUri
        .run("ESKeyNav(PropertyInt16=1)/CollPropertyComplex"
            + "/com.sap.odata.test1.BFCCollCTPrimCompRTESAllPrim()/com.sap.odata.test1.BAESAllPrimRTETAllPrim");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTString()");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTString()/$value");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollString()");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollString()/$count");
    testUri.run("ESKeyNav(1)/CollPropertyString/com.sap.odata.test1.BFCCollStringRTESTwoKeyNav()");
    testUri.run("ESKeyNav(1)/CollPropertyString/com.sap.odata.test1.BFCCollStringRTESTwoKeyNav()/$count");
    testUri.run("ESKeyNav(1)/PropertyString/com.sap.odata.test1.BFCStringRTESTwoKeyNav()");
    testUri.run("ESKeyNav(1)/PropertyString/com.sap.odata.test1.BFCStringRTESTwoKeyNav()/$count");
    testUri.run("ESKeyNav(1)/PropertyString/com.sap.odata.test1.BFCStringRTESTwoKeyNav()/$ref");
    testUri.run("SINav/com.sap.odata.test1.BFCSINavRTESTwoKeyNav()");
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/com.sap.odata.test1.BFCETBaseTwoKeyNavRTESBaseTwoKey()");
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.BAETTwoKeyNavRTETTwoKeyNav");
    testUri.run("ESKeyNav(PropertyInt16=1)/com.sap.odata.test1.BAETTwoKeyNavRTETTwoKeyNav");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BAESTwoKeyNavRTESTwoKeyNav");

    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/com.sap.odata.test1.BAETBaseTwoKeyNavRTETBaseTwoKeyNav");
    testUri
        .run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')"
            + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav/com.sap.odata.test1.BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav");
    testUri.run("$crossjoin(ESKeyNav)");
    testUri.run("$crossjoin(ESKeyNav, ESTwoKeyNav)");
    // testUri.run("$crossjoin");
    // testUri.run("$crossjoin/error");
    // testUri.run("$crossjoin()");
    // testUri.run("$crossjoin(ESKeyNav, ESTwoKeyNav)/invalid");
    // testUri.run("$crossjoin(invalidEntitySet)");
    testUri.run("$entity?$id=ESKeyNav(1)");
    testUri.run("$entity/com.sap.odata.test1.ETKeyNav?$id=ESKeyNav(1)");
    // testUri.run("$entity");
    // testUri.run("$entity?$idfalse=ESKeyNav(1)");
    // testUri.run("$entity/com.sap.odata.test1.invalidType?$id=ESKeyNav(1)");
    // testUri.run("$entity/invalid?$id=ESKeyNav(1)");
    testUri.run("ESAllPrim");
    testUri.run("ESAllPrim/$count");

    // testUri.run("ESAllPrim/$count/$ref");
    // testUri.run("ESAllPrim/$ref/$count");
    // testUri.run("ESAllPrim/$ref/invalid");
    // testUri.run("ESAllPrim/$count/invalid");
    // testUri.run("ESAllPrim(1)/whatever");
    // testUri.run("ESAllPrim(PropertyInt16='1')");
    // testUri.run("ESAllPrim(PropertyInt16)");
    // testUri.run("ESAllPrim(PropertyInt16=)");
    // testUri.run("ESAllPrim(PropertyInt16=1,Invalid='1')");
    // testUri.run("ETBaseTwoKeyTwoPrim/com.sap.odata.test1.ETBaseTwoKeyTwoPrim"
    //      +"/com.sap.odata.test1.ETTwoBaseTwoKeyTwoPrim");
    // testUri.run("ETBaseTwoKeyTwoPrim/com.sap.odata.test1.ETBaseTwoKeyTwoPrim(1)/com.sap.odata.test1.ETAllKey");
    // testUri.run("ETBaseTwoKeyTwoPrim(1)/com.sap.odata.test1.ETBaseTwoKeyTwoPrim('1')/com.sap.odata.test1.ETAllKey");
    // testUri.run("ETBaseTwoKeyTwoPrim(1)/com.sap.odata.test1.ETBaseTwoKeyTwoPrim"
    //      +"/com.sap.odata.test1.ETTwoBaseTwoKeyTwoPrim");
    // testUri.run("ETBaseTwoKeyTwoPrim/com.sap.odata.test1.ETBaseTwoKeyTwoPrim"
    //      +"/com.sap.odata.test1.ETTwoBaseTwoKeyTwoPrim(1)");
    // testUri.run("ETBaseTwoKeyTwoPrim/com.sap.odata.test1.ETAllKey");
    // testUri.run("ETBaseTwoKeyTwoPrim()");
    // testUri.run("ESAllNullable(1)/CollPropertyString/$value");
    // testUri.run("ETMixPrimCollComp(1)/ComplexProperty/$value");
    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase");
    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase(-32768)/com.sap.odata.test1.ETTwoBase");
    testUri.run("ESTwoPrim/com.sap.odata.test1.ETTwoBase(-32768)");
    testUri.run("ESTwoPrim/Namespace1_Alias.ETTwoBase(-32768)");
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplex/PropertyInt16");
    testUri.run("ESCollAllPrim(1)");
    testUri.run("ESCollAllPrim(PropertyInt16=1)");
    testUri.run("ESFourKeyAlias(PropertyInt16=1,KeyAlias1=2,KeyAlias2='3',KeyAlias3='4')");
    testUri.run("ESCollAllPrim(null)");
    testUri
        .run("ESAllKey(PropertyString='O''Neil',PropertyBoolean=true,PropertyByte=255,"
            + "PropertySByte=-128,PropertyInt16=-32768,PropertyInt32=-2147483648,"
            + "PropertyInt64=-9223372036854775808,PropertyDecimal=0.1,PropertyDate=2013-09-25,"
            + "PropertyDateTimeOffset=2002-10-10T12:00:00-05:00,"
            + "PropertyDuration=duration'P10DT5H34M21.123456789012S',"
            + "PropertyGuid=12345678-1234-1234-1234-123456789012,"
            + "PropertyTimeOfDay=12:34:55.123456789012)");
    testUri.run("ESTwoPrim(1)/com.sap.odata.test1.ETBase");
    testUri.run("ESTwoPrim(1)/com.sap.odata.test1.ETTwoBase");
    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase(1)");
    testUri.run("ESTwoPrim/com.sap.odata.test1.ETTwoBase(1)");
    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase(1)/com.sap.odata.test1.ETTwoBase");
    testUri.run("ESTwoPrim/com.sap.odata.test1.ETTwoBase");
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')");
    testUri.run("ESTwoPrim(1)/com.sap.odata.test1.ETBase(1)");
    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase(1)/com.sap.odata.test1.ETTwoBase(1)");
    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase(1)/com.sap.odata.test1.ETTwoBase(1)");

    // testUri.run("ESBase/com.sap.odata.test1.ETTwoPrim(1)");
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComplex");
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComplex/PropertyComplex");
    testUri.run("ESMixPrimCollComp(5)/CollPropertyComplex");
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/CollPropertyComplex");
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/CollPropertyComplex/$count");
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplex");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplex/PropertyComplex");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTBase");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase");
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany");
    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)");
    testUri.run("ESKeyNav(PropertyInt16=1)/NavPropertyETKeyNavMany(PropertyInt16=2)");
    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/PropertyInt16");
    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/PropertyComplex");
    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/NavPropertyETKeyNavOne");
    testUri
        .run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
            + "/NavPropertyETKeyNavMany(4)");
    testUri.run("ESKeyNav(1)/PropertyComplex/NavPropertyETTwoKeyNavOne");
    testUri
        .run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='(3)')"
            + "/PropertyComplex/PropertyComplex/PropertyInt16");
    testUri.run("ESKeyNav(1)/NavPropertyETMediaMany(2)/$value");
    testUri
        .run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
            + "/NavPropertyETKeyNavOne/NavPropertyETMediaOne/$value");
    testUri
        .run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
            + "/NavPropertyETKeyNavOne/$ref");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/NavPropertyETKeyNavMany");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/NavPropertyETKeyNavMany(3)");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETTwoBaseTwoKeyNav(PropertyInt16=3,PropertyString='4')");
    testUri
        .run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
            + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=4,PropertyString='5')"
            + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav/NavPropertyETBaseTwoKeyNavMany");

    // testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany('2')");
    // testUri.run("ESKeyNav(PropertyInt16=1)/NavPropertyETTwoKeyNavMany(PropertyString='2')");
    testUri.run("ESAllPrim(1)/PropertyByte");
    testUri.run("ESAllPrim(1)/PropertyByte/$value");
    testUri.run("ESMixPrimCollComp(1)/PropertyComplex/PropertyString");
    testUri.run("ESCollAllPrim(1)/CollPropertyString");
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/CollPropertyString");
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/CollPropertyString/$count");
    testUri.run("ESAllPrim/$ref");
    testUri.run("ESAllPrim(-32768)/$ref");
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany/$ref");
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')/$ref");
    testUri.run("FICRTString()/com.sap.odata.test1.BFCStringRTESTwoKeyNav()");
    testUri
        .run("FICRTETTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()");
    testUri
        .run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
            + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
            + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()");
    testUri.run("FICRTETKeyNav()");
    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=2,PropertyString='3')");
    testUri.run("FICRTETMedia()/$value");
    testUri.run("FICRTETKeyNav()/$ref");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)/$ref");
    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav");
    testUri
        .run("FICRTETTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=2,PropertyString='3')"
            + "/com.sap.odata.test1.ETBaseTwoKeyNav");
    testUri
        .run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
            + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')");
    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')");
    testUri.run("FINRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')");
    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')/$count");
    testUri.run("FICRTCollCTTwoPrimParam()");
    testUri.run("FICRTCollCTTwoPrimParam(invalidParam=2)");
    testUri.run("FICRTCollCTTwoPrimParam(ParameterInt16='1',ParameterString='2')");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@parameterAlias)?@parameterAlias=1");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@parameterAlias)/$count?@parameterAlias=1");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@invalidAlias)?@validAlias=1");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav/$count");
    testUri
        .run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
            + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')");
    testUri
        .run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
            + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
            + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav");
    testUri.run("SIMedia/$value");
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany");
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany(1)");
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplex");
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplex/PropertyComplex");
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplexTwoPrim/com.sap.odata.test1.CTBase");
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyInt16");
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/CollPropertyString");
    testUri.run("SINav/NavPropertyETKeyNavMany");
    testUri.run("SINav/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')");
    testUri.run("SINav/PropertyComplex");
    testUri.run("SINav/PropertyComplex/PropertyComplex");
    testUri.run("SINav/CollPropertyComplex");
    testUri.run("SINav/CollPropertyComplex/$count");
    testUri.run("SINav/PropertyString");
    testUri.run("SINav/CollPropertyString");
    testUri.run("SINav/CollPropertyString/$count");
    testUri.run("ESKeyNav(1)?$expand=*");
    testUri.run("ESKeyNav(1)?$expand=*/$ref");
    testUri.run("ESKeyNav(1)?$expand=*/$ref,NavPropertyETKeyNavMany");
    testUri.run("ESKeyNav(1)?$expand=*($levels=3)");
    testUri.run("ESKeyNav(1)?$expand=*($levels=max)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne/$ref");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($filter=PropertyInt16 eq 1)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($orderby=PropertyInt16)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($skip=1)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($top=2)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($count=true)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($skip=1;$top=3)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($skip=1%3b$top=3)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$count");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne/$count");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$count($filter=PropertyInt16 gt 1)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($filter=PropertyInt16 eq 1)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($orderby=PropertyInt16)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($skip=1)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($top=2)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($count=true)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($select=PropertyString)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($expand=NavPropertyETTwoKeyNavOne)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($expand=NavPropertyETKeyNavMany)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne($levels=5)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($select=PropertyString)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne($levels=max)");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($skip=1;$top=2)");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($skip=1%3b$top=2)");
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='Hugo')?$expand=NavPropertyETKeyNavMany");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='Hugo')?$expand=com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/NavPropertyETKeyNavMany");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')?$expand=com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/NavPropertyETTwoKeyNavMany");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')?$expand=com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETTwoBaseTwoKeyNav");
    testUri.run("ESTwoKeyNav?$expand=com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplexNav/NavPropertyETTwoKeyNavOne");
    testUri
        .run("ESTwoKeyNav?$expand=com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplexNav"
            + "/com.sap.odata.test1.CTTwoBasePrimCompNav/NavPropertyETTwoKeyNavOne");
    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref,NavPropertyETTwoKeyNavMany($skip=2;$top=1)");
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')?$expand=com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETTwoBaseTwoKeyNav($select=PropertyString)");
    testUri
        .run("ESKeyNav?$expand=NavPropertyETKeyNavOne($expand=NavPropertyETKeyNavMany("
            + "$expand=NavPropertyETKeyNavOne))");
    testUri.run("ESKeyNav?$expand=NavPropertyETKeyNavOne($select=PropertyInt16)");
    testUri.run("ESKeyNav?$expand=NavPropertyETKeyNavOne($select=PropertyComplex/PropertyInt16)");
    testUri.run("ESKeyNav?$top=1");
    testUri.run("ESKeyNav?$top=0");
    testUri.run("ESKeyNav?$top=-3");
    testUri.run("ESKeyNav(1)?$format=atom");
    testUri.run("ESKeyNav(1)?$format=json");
    testUri.run("ESKeyNav(1)?$format=xml");
    testUri.run("ESKeyNav(1)?$format=IANA_content_type/must_contain_a_slash");
    testUri.run("ESKeyNav(1)?$format=Test_all_valid_signs_specified_for_format_signs%26-._~$@%27/Aa123%26-._~$@%27");
    testUri.run("ESAllPrim?$count=true");
    testUri.run("ESAllPrim?$count=false");
    // testUri.run("ESAllPrim?$count=foo");
    testUri.run("ESAllPrim?$skip=3");
    testUri.run("ESAllPrim?$skip=0");
    testUri.run("ESAllPrim?$skip=-3");
    testUri.run("ESAllPrim?$skiptoken=foo");
    testUri.run("");

    testUri.run("$all");
    testUri.run("$metadata");
    testUri.run("$batch");
    testUri.run("$crossjoin(ESKeyNav)");
    testUri.run("ESKeyNav");
    testUri.run("ESKeyNav(1)");
    testUri.run("SINav");
    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')");
    testUri.run("FICRTETKeyNav()");
    testUri.run("FICRTCollCTTwoPrim()");
    testUri.run("FICRTCTAllPrimTwoParam(ParameterInt16=1,ParameterString='2')");
    testUri.run("FICRTCollStringTwoParam(ParameterInt16=1,ParameterString='2')");
    testUri.run("FICRTStringTwoParam(ParameterInt16=1)");
    testUri.run("FICRTStringTwoParam(ParameterInt16=1,ParameterString='2')");
    testUri.run("AIRTETParam");
    testUri.run("AIRTPrimParam");
    testUri.run("ESKeyNav/$count");
    testUri.run("ESKeyNav/$ref");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()");
    testUri.run("ESAllPrim/com.sap.odata.test1.BAESAllPrimRTETAllPrim");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav");
    testUri.run("ESTwoKeyNav/$count");
    testUri.run("ESTwoKeyNav/$ref");
    testUri.run("ESKeyNav(1)");
    testUri.run("ESKeyNav(1)/$ref");
    testUri.run("ESMedia(1)/$value");
    testUri.run("ESAllPrim/com.sap.odata.test1.BAESAllPrimRTETAllPrim");
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()");
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav");
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/$ref");
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/$value");
  }

  @Test
  public void testSpecial() {
    testFilter.runOnETKeyNav("any()");

  }

  @Test
  public void TestFilter() {

    testFilter.runOnETTwoKeyNav("PropertyString");
    testFilter.runOnETTwoKeyNav("PropertyComplex/PropertyInt16");
    testFilter.runOnETTwoKeyNav("PropertyComplex/PropertyComplex/PropertyDate");
    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne");
    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyString");
    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex");
    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyComplex");
    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyInt16");
    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyInt16 eq 1");
    // testFilter
    // .runOnETTwoKeyNav(
    // "NavPropertyETKeyNavMany(1)/NavPropertyETTwoKeyNavMany(PropertyString='2')/PropertyString eq 'SomeString'");
    testFilter.runOnETTwoKeyNav("com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate eq 2013-11-12");
    testFilter.runOnCTTwoPrim("com.sap.odata.test1.CTBase/AdditionalPropString eq 'SomeString'");
    testFilter
        .runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate eq 2013-11-12");
    testFilter
        .runOnETTwoKeyNav("PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase/AdditionalPropString eq 'SomeString'");

    /*
     * Xinvalid
     * XPropertyComplex/invalid
     * Xconcat('a','b')/invalid
     * XPropertyComplex/concat('a','b')
     * XPropertyComplexAllPrim/PropertyInt16 eq '1'
     * XPropertyComplexAllPrim/PropertyDate eq 1
     * XPropertyComplexAllPrim/PropertyString eq 1
     * XPropertyComplexAllPrim/PropertyDate eq 1
     */

    testFilter.runOnETAllPrim("PropertySByte add PropertySByte");
    testFilter.runOnETAllPrim("PropertyByte add PropertyByte");
    testFilter.runOnETAllPrim("PropertyInt16 add PropertyInt16");
    testFilter.runOnETAllPrim("PropertyInt32 add PropertyInt32");
    testFilter.runOnETAllPrim("PropertyInt64 add PropertyInt64");
    testFilter.runOnETAllPrim("PropertySingle add PropertySingle");
    testFilter.runOnETAllPrim("PropertyDouble add PropertyDouble");
    testFilter.runOnETAllPrim("PropertyDecimal add PropertyDecimal");
    testFilter.runOnETAllPrim("PropertySByte add PropertyDecimal");
    testFilter.runOnETAllPrim("PropertySByte add PropertyInt32");
    testFilter.runOnETAllPrim("PropertySByte add PropertyInt64");
    testFilter.runOnETAllPrim("PropertyDateTimeOffset add PropertyDuration");
    testFilter.runOnETAllPrim("PropertyDuration add PropertyDuration");
    testFilter.runOnETAllPrim("PropertyDate add PropertyDuration");
    testFilter.runOnETAllPrim("PropertySByte sub PropertySByte");
    testFilter.runOnETAllPrim("PropertyByte sub PropertyByte");
    testFilter.runOnETAllPrim("PropertyInt16 sub PropertyInt16");
    testFilter.runOnETAllPrim("PropertyInt32 sub PropertyInt32");
    testFilter.runOnETAllPrim("PropertyInt64 sub PropertyInt64");
    testFilter.runOnETAllPrim("PropertySingle sub PropertySingle");
    testFilter.runOnETAllPrim("PropertyDouble sub PropertyDouble");
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyDecimal");
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyInt32");
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyInt64");
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyByte");
    testFilter.runOnETAllPrim("PropertyDateTimeOffset sub PropertyDuration");
    testFilter.runOnETAllPrim("PropertyDuration sub PropertyDuration");
    testFilter.runOnETAllPrim("PropertyDateTimeOffset sub PropertyDateTimeOffset");
    testFilter.runOnETAllPrim("PropertyDate sub PropertyDuration");
    testFilter.runOnETAllPrim("PropertyDate sub PropertyDate");
    testFilter.runOnETAllPrim("PropertySByte mul PropertySByte");
    testFilter.runOnETAllPrim("PropertyByte mul PropertyByte");
    testFilter.runOnETAllPrim("PropertyInt16 mul PropertyInt16");
    testFilter.runOnETAllPrim("PropertyInt32 mul PropertyInt32");
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyInt64");
    testFilter.runOnETAllPrim("PropertySingle mul PropertySingle");
    testFilter.runOnETAllPrim("PropertyDouble mul PropertyDouble");
    testFilter.runOnETAllPrim("PropertyDecimal mul PropertyDecimal");
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyInt32");
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertySByte");
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyDecimal");
    testFilter.runOnETAllPrim("PropertySByte div PropertySByte");
    testFilter.runOnETAllPrim("PropertyByte div PropertyByte");
    testFilter.runOnETAllPrim("PropertyInt16 div PropertyInt16");
    testFilter.runOnETAllPrim("PropertyInt32 div PropertyInt32");
    testFilter.runOnETAllPrim("PropertyInt64 div PropertyInt64");
    testFilter.runOnETAllPrim("PropertySingle div PropertySingle");
    testFilter.runOnETAllPrim("PropertyDouble div PropertyDouble");
    testFilter.runOnETAllPrim("PropertyDecimal div PropertyDecimal");
    testFilter.runOnETAllPrim("PropertyByte div PropertyInt32");
    testFilter.runOnETAllPrim("PropertyByte div PropertyDecimal");
    testFilter.runOnETAllPrim("PropertyByte div PropertySByte");
    // testFilter.runOnETAllPrim("PropertyByte div 0");
    // testFilter.runOnETAllPrim("0 div 0");
    testFilter.runOnETAllPrim("PropertySByte mod PropertySByte");
    testFilter.runOnETAllPrim("PropertyByte mod PropertyByte");
    testFilter.runOnETAllPrim("PropertyInt16 mod PropertyInt16");
    testFilter.runOnETAllPrim("PropertyInt32 mod PropertyInt32");
    testFilter.runOnETAllPrim("PropertyInt64 mod PropertyInt64");
    testFilter.runOnETAllPrim("PropertySingle mod PropertySingle");
    testFilter.runOnETAllPrim("PropertyDouble mod PropertyDouble");
    testFilter.runOnETAllPrim("PropertyDecimal mod PropertyDecimal");

    // testFilter.runOnETAllPrim("XPropertyByte mod 0");
    // testFilter.runOnETAllPrim("com.sap.odata.test1.UFCRTETTwoKeyNavParamCTTwoPrim(ParameterCTTwoPrim=@ParamAlias)");
    testFilter
        .runOnETTwoKeyNav("PropertyComplex"
            + "/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNavParam"
            + "(ParameterString=PropertyComplex/PropertyComplex/PropertyString)(PropertyInt16=1,PropertyString='2')"
            + "/PropertyString eq 'SomeString'");
    testFilter
        .runOnETTwoKeyNav("PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTETTwoKeyNavParam(ParameterString=null)"
            + "/PropertyString eq 'SomeString'");
    testFilter
        .runOnETTwoKeyNav("NavPropertyETTwoKeyNavMany/com.sap.odata.test1.BFCESTwoKeyNavRTString() eq 'SomeString'");
    testFilter.runOnETTwoKeyNav("$it/com.sap.odata.test1.BFCETTwoKeyNavRTETTwoKeyNav()/PropertyString eq 'SomeString'");
//testFilter.runOnETTwoKeyNav("com.sap.odata.test1.BFCETTwoKeyNavRTETTwoKeyNav()/PropertyString eq 'SomeString'");
    testFilter
        .runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/com.sap.odata.test1.BFCETTwoKeyNavRTETTwoKeyNav()"
            + "/PropertyComplex/PropertyComplex/PropertyString eq 'Walldorf'");
    testFilter
        .runOnETTwoKeyNav("PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNavParam(ParameterString='1')"
            + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
            + "/PropertyString eq 'SomeString'");
    testFilter
        .runOnETTwoKeyNav("PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNavParam(ParameterString='a=1')"
            + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
            + "/PropertyString eq 'SomeString'");
    testFilter
        .runOnETTwoKeyNav("$it/com.sap.odata.test1.BFCETTwoKeyNavRTCTTwoPrim()/com.sap.odata.test1.CTBase"
            + "/PropertyString eq 'SomeString'");
    testFilter.runOnETTwoKeyNav("com.sap.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=1)/PropertyInt16 eq 2");
    testFilter
        .runOnETTwoKeyNav("com.sap.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=@Param1Alias)/PropertyInt16 eq 2");
    testFilter
        .runOnETTwoKeyNav("com.sap.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=1)/PropertyComplex"
            + "/PropertyComplex/PropertyString eq 'SomeString'");

    testFilter
        .runOnETTwoKeyNav(
        "com.sap.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=PropertyInt16)/PropertyComplex"
        + "/PropertyComplex/PropertyString eq 'SomeString'"
        );

    testFilter.runOnETKeyNav("indexof(PropertyString,'47') eq 5");
    testFilter.runOnETKeyNav("tolower(PropertyString) eq 'foo'");
    testFilter.runOnETKeyNav("toupper(PropertyString) eq 'FOO'");
    testFilter.runOnETKeyNav("trim(PropertyString) eq 'fooba'");
    testFilter.runOnETKeyNav("substring(PropertyString,4) eq 'foo'");
    testFilter.runOnETKeyNav("substring(PropertyString,4) eq 'foo'");
    testFilter.runOnETKeyNav("substring(PropertyString,2,4) eq 'foo'");
    testFilter.runOnETKeyNav("concat(PropertyString,PropertyComplexTwoPrim/PropertyString) eq 'foo'");
    testFilter.runOnETKeyNav("concat(PropertyString,PropertyComplexTwoPrim/PropertyString) eq 'foo'");
    testFilter.runOnETKeyNav("concat(PropertyString,'bar') eq 'foobar'");
    testFilter.runOnETKeyNav("concat(PropertyString,'bar') eq 'foobar'");
    testFilter.runOnETKeyNav("concat(PropertyString, cast(PropertyComplexAllPrim/PropertyInt16,Edm.String))");
    testFilter.runOnETKeyNav("concat(PropertyString, cast(PropertyComplexAllPrim/PropertyInt16,Edm.String))");
    testFilter.runOnETKeyNav("length(PropertyString) eq 32");

    testFilter.runOnETAllPrim("year(PropertyDate) eq 2013");
    testFilter.runOnETAllPrim("year(2013-09-25) eq 2013");
    testFilter.runOnETAllPrim("year(PropertyDateTimeOffset) eq 2013");
    testFilter.runOnETAllPrim("year(2013-09-25T12:34:56.123456789012-10:24) eq 2013");
    testFilter.runOnETAllPrim("month(PropertyDate) eq 9");
    testFilter.runOnETAllPrim("month(2013-09-25) eq 9");
    testFilter.runOnETAllPrim("month(PropertyDateTimeOffset) eq 9");
    testFilter.runOnETAllPrim("month(2013-09-25T12:34:56.123456789012-10:24) eq 9");
    testFilter.runOnETAllPrim("day(PropertyDate) eq 25");
    testFilter.runOnETAllPrim("day(2013-09-25) eq 25");
    testFilter.runOnETAllPrim("day(PropertyDateTimeOffset) eq 25");
    testFilter.runOnETAllPrim("day(2013-09-25T12:34:56.123456789012-10:24) eq 25");
    testFilter.runOnETAllPrim("hour(PropertyDateTimeOffset) eq 2");
    testFilter.runOnETAllPrim("hour(PropertyDateTimeOffset) eq 2");

    testFilter.runOnETAllPrim("hour(2013-09-25T12:34:56.123456789012-10:24) eq 2");
    testFilter.runOnETAllPrim("hour(PropertyTimeOfDay) eq 2");
    testFilter.runOnETAllPrim("hour(12:34:55.123456789012) eq 12");
    testFilter.runOnETAllPrim("minute(PropertyDateTimeOffset) eq 34");
    testFilter.runOnETAllPrim("minute(2013-09-25T12:34:56.123456789012-10:24) eq 34");
    testFilter.runOnETAllPrim("minute(PropertyTimeOfDay) eq 34");
    testFilter.runOnETAllPrim("minute(12:34:55.123456789012) eq 34");
    testFilter.runOnETAllPrim("second(PropertyDateTimeOffset) eq 56");
    testFilter.runOnETAllPrim("second(2013-09-25T12:34:56.123456789012-10:24) eq 56");
    testFilter.runOnETAllPrim("second(PropertyTimeOfDay) eq 56");

    testFilter.runOnETAllPrim("second(12:34:55.123456789012) eq 56");
    testFilter.runOnETAllPrim("fractionalseconds(PropertyDateTimeOffset) eq 123456789012");
    testFilter.runOnETAllPrim("fractionalseconds(2013-09-25T12:34:56.123456789012-10:24) eq 123456789012");
    testFilter.runOnETAllPrim("fractionalseconds(PropertyTimeOfDay) eq 123456789012");
    testFilter.runOnETAllPrim("fractionalseconds(12:34:55.123456789012) eq 123456789012");
    testFilter.runOnETAllPrim("totalseconds(PropertyDuration) eq 4711");
    testFilter.runOnETAllPrim("totalseconds(duration'P10DT5H34M21.123456789012S') eq 4711");
    testFilter.runOnETAllPrim("date(PropertyDateTimeOffset) eq 2013-09-25");
    testFilter.runOnETAllPrim("date(2013-09-25T12:34:56.123456789012-10:24) eq 2013-09-25");

    testFilter.runOnETAllPrim("time(PropertyDateTimeOffset) eq 12:34:55.123456789012");
    testFilter.runOnETAllPrim("time(2013-09-25T12:34:56.123456789012-10:24) eq 12:34:55.123456789012");
    testFilter.runOnETAllPrim("round(PropertyDouble) eq 17");
    testFilter.runOnETAllPrim("round(17.45e1) eq 17");
    testFilter.runOnETAllPrim("round(PropertyDecimal) eq 17");
    testFilter.runOnETAllPrim("round(17.45) eq 17");
    testFilter.runOnETAllPrim("floor(PropertyDouble) eq 17");
    testFilter.runOnETAllPrim("floor(17.45e1) eq 17");
    testFilter.runOnETAllPrim("floor(PropertyDecimal) eq 17");
    testFilter.runOnETAllPrim("floor(17.45) eq 17");
    testFilter.runOnETAllPrim("ceiling(PropertyDouble) eq 18");
    testFilter.runOnETAllPrim("ceiling(17.55e1) eq 18");
    testFilter.runOnETAllPrim("ceiling(PropertyDecimal) eq 18");
    testFilter.runOnETAllPrim("ceiling(17.55) eq 18");

    testFilter.runOnETAllPrim("totaloffsetminutes(PropertyDateTimeOffset) eq 4711");
    testFilter.runOnETAllPrim("totaloffsetminutes(2013-09-25T12:34:56.123456789012-10:24) eq 4711");
    testFilter.runOnETAllPrim("mindatetime()");
    testFilter.runOnETAllPrim("mindatetime() eq 2013-09-25T12:34:56.123456789012-10:24");
    testFilter.runOnETAllPrim("maxdatetime()");
    testFilter.runOnETAllPrim("maxdatetime() eq 2013-09-25T12:34:56.123456789012-10:24");
    testFilter.runOnETAllPrim("now()");
    testFilter.runOnETAllPrim("now() eq 2013-09-25T12:34:56.123456789012-10:24");

    testFilter.runOnETTwoKeyNav("$it/PropertyString eq 'SomeString'");
    testFilter.runOnCTTwoPrim("$it/PropertyString eq 'SomeString'");

    testFilter.runOnString("$it eq 'Walldorf'");
    testFilter.runOnString("endswith($it,'sap.com')");
    testFilter.runOnString("endswith($it,'sap.com') eq false");

    testFilter.runOnETTwoKeyNav("endswith($it/CollPropertyString,'sap.com')");
    testFilter
        .runOnETTwoKeyNav("PropertyComplex/PropertyComplex/PropertyInt16 eq $root"
            + "/ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyInt16");

    testFilter.runOnETKeyNav("cast(com.sap.odata.test1.ETBaseTwoKeyNav)");
    testFilter.runOnETKeyNav("cast(PropertyComplexTwoPrim,com.sap.odata.test1.CTBase)");
    testFilter.runOnETKeyNav("cast($it,com.sap.odata.test1.CTBase)");
    testFilter.runOnETKeyNav("cast($it,com.sap.odata.test1.CTBase) eq cast($it,com.sap.odata.test1.CTBase)");
    testFilter.runOnInt32("cast(Edm.Int32)");
    testFilter.runOnDateTimeOffset("cast(Edm.DateTimeOffset)");
    testFilter.runOnDuration("cast(Edm.Duration)");
    testFilter.runOnTimeOfDay("cast(Edm.TimeOfDay)");
    testFilter.runOnETKeyNav("cast(CollPropertyInt16,Edm.Int32)");

    testFilter.runOnETTwoKeyNav("cast(PropertyComplex/PropertyComplex/PropertyDateTimeOffset,Edm.DateTimeOffset)");
    testFilter.runOnETTwoKeyNav("cast(PropertyComplex/PropertyComplex/PropertyDuration,Edm.Duration)");
    testFilter.runOnETTwoKeyNav("cast(PropertyComplex/PropertyComplex/PropertyTimeOfDay,Edm.TimeOfDay)");
    testFilter.runOnETKeyNav("cast(PropertyComplexAllPrim,com.sap.odata.test1.CTTwoPrim)");
    // testFilter.runOnETKeyNav(" Xcast(PropertyComplexTwoPrim,com.sap.odata.test1.CTAllPrim)");

    testFilter.runOnETKeyNav("cast(NavPropertyETKeyNavOne,com.sap.odata.test1.ETKeyPrimNav)");
    // testFilter.runOnETKeyNav("Xcast(NavPropertyETKeyPrimNavOne,com.sap.odata.test1.ETKeyNav)");
    testFilter.runOnETKeyNav("any()");

    testFilter.runOnETKeyNav("any(d:d/PropertyInt16 eq 1)");
    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyString eq 'SomeString')");
    // testFilter.runOnETKeyNav("XNavPropertyETTwoKeyNavOne/any(d:d/PropertyString eq 'SomeString')");

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any()");
    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavOne/CollPropertyString/any(d:d eq 'SomeString')");
    testFilter
        .runOnETKeyNav(" NavPropertyETTwoKeyNavOne/com.sap.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()"
            + "/any(d:d/PropertyComplex/PropertyInt16 eq 6)");
    testFilter
        .runOnETKeyNav(" NavPropertyETTwoKeyNavMany/any(d:d/PropertyInt16 eq 1 or d/any"
            + "(e:e/CollPropertyString eq 'SomeString'))");
    testFilter
        .runOnETKeyNav(" NavPropertyETTwoKeyNavMany/any(d:d/PropertyString eq 'SomeString' and d/any"
            + "(e:e/PropertyString eq d/PropertyString))");

    testFilter.runOnETKeyNav("isof(com.sap.odata.test1.ETTwoKeyNav)");
    testFilter.runOnETKeyNav("isof(com.sap.odata.test1.ETBaseTwoKeyNav) eq true");
    testFilter
        .runOnETKeyNav("isof(com.sap.odata.test1.ETBaseTwoKeyNav) eq true and PropertyComplex/PropertyInt16 eq 1");
    testFilter.runOnETKeyNav("isof(NavPropertyETKeyNavOne, com.sap.odata.test1.ETKeyNav) eq true");
    testFilter.runOnETKeyNav("isof(PropertyComplexTwoPrim,com.sap.odata.test1.CTTwoPrim)");
    testFilter.runOnETKeyNav("isof(PropertyComplexTwoPrim,com.sap.odata.test1.CTTwoBase)");
    testFilter.runOnETKeyNav("isof(PropertyComplexTwoPrim,com.sap.odata.test1.CTTwoPrim) eq true");
    testFilter.runOnETKeyNav("isof($it,com.sap.odata.test1.CTTwoPrim)");
    testFilter.runOnETKeyNav("isof($it,com.sap.odata.test1.CTTwoBase) eq false");
    testFilter.runOnETKeyNav("isof(PropertyComplex/PropertyInt16,Edm.Int32)");

    testFilter.runOnETTwoKeyNav("isof(PropertyComplex/PropertyComplex/PropertyDateTimeOffset,Edm.DateTimeOffset)");
    testFilter.runOnETTwoKeyNav("isof(PropertyComplex/PropertyComplex/PropertyTimeOfDay,Edm.TimeOfDay)");
    testFilter.runOnETTwoKeyNav(" isof(PropertyComplex/PropertyComplex/PropertyDuration,Edm.Duration)");
    testFilter.runOnETTwoKeyNav("isof(PropertyComplex/PropertyComplex/PropertyString,Edm.String)");
    testFilter.runOnETTwoKeyNav("isof(PropertyComplex/PropertyComplex/PropertyString,Edm.Guid)");

    testFilter.runOnETTwoKeyNav("PropertyEnumString has com.sap.odata.test1.ENString'String1'");
    testFilter.runOnETTwoKeyNav("PropertyComplexEnum/PropertyEnumString has com.sap.odata.test1.ENString'String2'");
    testFilter
        .runOnETTwoKeyNav("PropertyComplexEnum/PropertyEnumString has com.sap.odata.test1.ENString'String2' eq true");
    testFilter.runOnETTwoKeyNav("PropertyEnumString has com.sap.odata.test1.ENString'String3'");
    testFilter.runOnETTwoKeyNav("PropertyEnumString has null");

    testFilter.runOnETTwoKeyNav("endswith(PropertyComplex/PropertyComplex/PropertyString,'dorf')");
    testFilter.runOnETTwoKeyNav("endswith(PropertyComplex/PropertyComplex/PropertyString,'dorf') eq true");
    testFilter.runOnETTwoKeyNav("endswith('Walldorf','dorf')");
    testFilter.runOnETTwoKeyNav("endswith('Walldorf','dorf') eq true");

    testFilter.runOnETKeyNav("startswith(PropertyComplexAllPrim/PropertyString,'Wall')");
    testFilter.runOnETKeyNav("startswith(PropertyComplexAllPrim/PropertyString,'Wall') eq true");
    testFilter.runOnETKeyNav("startswith('Walldorf','Wall')");
    testFilter.runOnETKeyNav("startswith('Walldorf','Wall') eq true");

    testFilter.runOnETTwoKeyNav("contains(PropertyComplex/PropertyComplex/PropertyString,'Wall')");
    testFilter.runOnETTwoKeyNav("contains(PropertyComplex/PropertyComplex/PropertyString,'Wall') eq true");
    testFilter.runOnETTwoKeyNav("contains('Walldorf','Wall')");
    testFilter.runOnETTwoKeyNav("contains('Walldorf','Wall') eq true");
    testFilter.runOnETAllPrim("com.sap.odata.test1.UFCRTCTTwoPrimParam(ParameterInt16=null,ParameterString=null)");
    testFilter.runOnETAllPrim("PropertyBoolean eq true");
    // testFilter.runOnETAllPrim("XPropertyBoolean eq 2");
    testFilter.runOnETAllPrim("PropertyDecimal eq 1.25");
    testFilter.runOnETAllPrim("PropertyDouble eq 1.5");
    testFilter.runOnETAllPrim("PropertySingle eq 1.5");
    testFilter.runOnETAllPrim("PropertySByte eq -128");
    testFilter.runOnETAllPrim("PropertyByte eq 255");
    testFilter.runOnETAllPrim("PropertyInt16 eq 32767");
    testFilter.runOnETAllPrim("PropertyInt32 eq 2147483647");
    testFilter.runOnETAllPrim("PropertyInt64 eq 9223372036854775807");
    testFilter.runOnETAllPrim("PropertyDate eq 2013-09-25");
    testFilter.runOnETAllPrim("PropertyDateTimeOffset eq 2013-09-25T12:34:56.123456789012-10:24");
    testFilter.runOnETAllPrim("PropertyDuration eq duration'P10DT5H34M21.123456789012S'");
    testFilter.runOnETAllPrim("PropertyGuid eq 005056A5-09B1-1ED3-89BD-FB81372CCB33");
    testFilter.runOnETAllPrim("PropertyString eq 'somestring'");

    testFilter.runOnETAllPrim("PropertyTimeOfDay eq 12:34:55.12345678901");
    testFilter.runOnETTwoKeyNav("PropertyEnumString eq com.sap.odata.test1.ENString'String1'");
    testFilter.runOnETTwoKeyNav("PropertyEnumString eq com.sap.odata.test1.ENString'String2'");

    testFilter.runOnETTwoKeyNav("PropertyComplexEnum/PropertyEnumString eq com.sap.odata.test1.ENString'String3'");
    testFilter.runOnETTwoKeyNav("PropertyComplexEnum/PropertyEnumString eq com.sap.odata.test1.ENString'String3'");
    testFilter.runOnETTwoKeyNav("PropertyComplexEnum/PropertyEnumString eq PropertyComplexEnum/PropertyEnumString");
    testFilter.runOnETTwoKeyNav("PropertyComplexEnum/PropertyEnumString eq PropertyComplexEnum/PropertyEnumString");

  }

  @Test
  public void TestOrderby() {
    /*
     * com.sap.odata.test1.UFCRTETAllPrimTwoParam(ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/
     * PropertyString eq 'SomeString' asc
     * com.sap.odata.test1.UFCRTETAllPrimTwoParam(ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/
     * PropertyString eq 'SomeString' desc, PropertyString eq '1'
     * com.sap.odata.test1.UFCRTETAllPrimTwoParam(ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/
     * PropertyString eq 'SomeString' desc, PropertyString eq '1'
     * PropertyComplex/PropertyComplex/PropertyDate eq
     * $root/ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComplex/PropertyComplex/PropertyDate
     * PropertyString
     * PropertyComplex/PropertyComplex/PropertyDate
     * PropertyComplex/PropertyComplex/PropertyDate eq 2013-11-12 desc, PropertyString eq 'SomeString' desc
     * PropertyComplex
     * PropertyComplex/PropertyComplex
     * PropertyComplex desc, PropertyComplex/PropertyInt16 eq 1
     * NavPropertyETKeyNavOne
     * NavPropertyETKeyNavOne/PropertyString
     * NavPropertyETKeyNavOne/PropertyComplex
     * PropertyComplex/PropertyComplex/PropertyInt16 eq 1
     * NavPropertyETKeyNavMany(1)/NavPropertyETTwoKeyNavMany(PropertyString='2')/PropertyString eq 'SomeString'
     * NavPropertyETKeyNavMany(1)/NavPropertyETTwoKeyNavMany(PropertyString='2')/PropertyString eq 'SomeString1' desc,
     * PropertyString eq 'SomeString2' asc
     * PropertyBoolean eq true
     * PropertyBoolean eq true desc
     * PropertyDouble eq 3.5E+38
     * PropertyDouble eq 3.5E+38 desc
     * PropertySingle eq 1.5
     * PropertySingle eq 1.5 desc
     * PropertySByte eq -128
     * PropertySByte eq -128 desc
     * PropertyByte eq 255
     * PropertyByte eq 255 desc
     * PropertyInt16 eq 32767
     * PropertyInt16 eq 32767 desc
     * PropertyInt32 eq 2147483647
     * PropertyInt32 eq 2147483647 desc
     * PropertyInt64 eq 9223372036854775807
     * PropertyInt64 eq 9223372036854775807 desc
     * PropertyBinary eq binary'0FAB7B'
     * PropertyBinary eq binary'0FAB7B' desc
     * PropertyDate eq 2013-09-25
     * PropertyDate eq 2013-09-25 desc
     * PropertyDateTimeOffset eq 2013-09-25T12:34:56.123456789012-10:24
     * PropertyDateTimeOffset eq 2013-09-25T12:34:56.123456789012-10:24 desc
     * PropertyDuration eq duration'P10DT5H34M21.123456789012S'
     * PropertyDuration eq duration'P10DT5H34M21.123456789012S' desc
     * PropertyGuid eq 005056A5-09B1-1ED3-89BD-FB81372CCB33
     * PropertyGuid eq 005056A5-09B1-1ED3-89BD-FB81372CCB33 desc
     * PropertyString eq 'somestring
     * PropertyString eq 'somestring desc
     * PropertyTimeOfDay eq 12:34:55.123456789012
     * PropertyTimeOfDay eq 12:34:55.123456789012 desc
     * PropertyEnumString eq com.sap.odata.test1.ENString'String1'
     * PropertyEnumString eq com.sap.odata.test1.ENString'String1' desc
     * XPropertyInt16 1
     * XPropertyInt16, PropertyInt32 PropertyDuration
     * XPropertyInt16 PropertyInt32, PropertyDuration desc
     * XPropertyInt16 asc, PropertyInt32 PropertyDuration desc
     */
  }

}
