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

// sync 20.1.2014
import java.util.Arrays;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriResourceKind;
import org.apache.olingo.odata4.producer.core.testutil.EdmTechProvider;
import org.apache.olingo.odata4.producer.core.testutil.EdmTechTestProvider;
import org.apache.olingo.odata4.producer.core.testutil.FilterValidator;
import org.apache.olingo.odata4.producer.core.testutil.UriResourceValidator;
import org.apache.olingo.odata4.producer.core.testutil.UriValidator;
import org.junit.Test;

public class TestByAbapResourcePath {
  Edm edm = null;
  UriValidator testUri = null;
  UriResourceValidator testRes = null;
  FilterValidator testFilter = null;

  public TestByAbapResourcePath() {
    edm = new EdmProviderImpl(new EdmTechTestProvider());
    testUri = new UriValidator().setEdm(edm);
    testRes = new UriResourceValidator().setEdm(edm);
    testFilter = new FilterValidator().setEdm(edm);
  }

  @Test
  public void runAction_VarReturnType() {

    testUri.run("AIRTPrimParam").isKind(UriInfoKind.resource).goPath()
        .first()
        .isActionImport("AIRTPrimParam")
        .isAction("UARTPrimParam")
        .isType(EdmTechProvider.nameString, false);

    testUri.run("AIRTPrimCollParam").isKind(UriInfoKind.resource).goPath()
        .first()
        .isActionImport("AIRTPrimCollParam")
        .isAction("UARTPrimCollParam")
        .isType(EdmTechProvider.nameString, true);

    testUri.run("AIRTCompParam").isKind(UriInfoKind.resource).goPath()
        .first()
        .isActionImport("AIRTCompParam")
        .isAction("UARTCompParam")
        .isType(EdmTechProvider.nameCTTwoPrim, false);

    testUri.run("AIRTCompCollParam").isKind(UriInfoKind.resource).goPath()
        .first()
        .isActionImport("AIRTCompCollParam")
        .isAction("UARTCompCollParam")
        .isType(EdmTechProvider.nameCTTwoPrim, true);

    testUri.run("AIRTETParam").isKind(UriInfoKind.resource).goPath()
        .first()
        .isActionImport("AIRTETParam")
        .isAction("UARTETParam")
        .isType(EdmTechProvider.nameETTwoKeyTwoPrim, false);

    // TODO add error test
    // testUri.run("AIRTPrimParam/invalidElement").isKind(UriInfoKind.resource).goPath().
    // isUriPathInfoKind(UriResourceKind.action);
    // testUri.run("InvalidAction");
  }

//DONE

  @Test
  public void runBfuncBnCpropCastRtEs() {

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESBaseTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESBaseTwoKeyNav");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESBaseTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESBaseTwoKeyNav")
        .isType(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);

  }

  @Test
  public void runBfuncBnCpropCollRtEs() {
    testUri.run("ESKeyNav(PropertyInt16=1)/CollPropertyComplex/com.sap.odata.test1.BFCCollCTPrimCompRTESAllPrim()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true)
        .n()
        .isFunction("BFCCollCTPrimCompRTESAllPrim");

    testUri
        .run("ESKeyNav(PropertyInt16=1)/CollPropertyComplex/com.sap.odata.test1.BFCCollCTPrimCompRTESAllPrim()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true)
        .n()
        .isFunction("BFCCollCTPrimCompRTESAllPrim")
        .isType(EdmTechProvider.nameETAllPrim, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void runBfuncBnCpropRtEs() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNav");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);

  }

  @Test
  public void runBfuncBnEntityRtEs() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isFunction("BFCETTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void runBfuncBnEntityCastRtEs() {
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESTwoKeyNav");

    testUri
        .run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='(''2'')')"
            + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'(''2'')'")
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void runBfuncBnEsCastRtEs() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/com.sap.odata.test1.BFCESBaseTwoKeyNavRTESBaseTwoKey()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCESBaseTwoKeyNavRTESBaseTwoKey");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/com.sap.odata.test1.BFCESBaseTwoKeyNavRTESBaseTwoKey()"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCESBaseTwoKeyNavRTESBaseTwoKey")
        .isType(EdmTechProvider.nameETBaseTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav"
        + "/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBaseTwoKeyNav);
  }

  @Test
  public void runBfuncBnEsRtCprop() {
    testUri.run("ESAllPrim/com.sap.odata.test1.BFCESAllPrimRTCTAllPrim()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .n()
        .isFunction("BFCESAllPrimRTCTAllPrim")
        .isType(EdmTechProvider.nameCTAllPrim);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCTTwoPrim()/com.sap.odata.test1.CTBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCTTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim, false)
        .isTypeFilterOnEntry(EdmTechProvider.nameCTBase);
  }

  @Test
  public void runBfuncBnEsRtCpropColl() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollCTTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim, true);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollCTTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void runBfuncBnEsRtEntityPpNp() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()/NavPropertyETKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNav("NavPropertyETKeyNavOne")
        .isType(EdmTechProvider.nameETKeyNav);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()/NavPropertyETKeyNavOne/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNav("NavPropertyETKeyNavOne")
        .isType(EdmTechProvider.nameETKeyNav, false)
        .n()
        .isUriPathInfoKind(UriResourceKind.ref);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/NavPropertyETMediaOne/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isNav("NavPropertyETMediaOne")
        .isType(EdmTechProvider.nameETMedia, false)
        .n()
        .isValue();

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNav("NavPropertyETTwoKeyNavOne")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNav("NavPropertyETTwoKeyNavOne")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNav("NavPropertyETTwoKeyNavOne")
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp)
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTAllPrim);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNav("NavPropertyETTwoKeyNavOne")
        .n()
        .isSimple("PropertyString")
        .isType(EdmTechProvider.nameString);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isSimple("PropertyString")
        .isType(EdmTechProvider.nameString);
  }

  @Test
  public void runBfuncBnEsRtEntyPpNpCast() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()"
        + "/NavPropertyETTwoKeyNavOne/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNav("NavPropertyETTwoKeyNavOne")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri
        .run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()(PropertyInt16=1,PropertyString='2')"
            + "/NavPropertyETTwoKeyNavOne/com.sap.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isNav("NavPropertyETTwoKeyNavOne")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBaseTwoKeyNav);

  }

  @Test
  public void runBfuncBnEsRtEntityPpCp() {

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTNavFiveProp);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComplex/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTNavFiveProp)
        .n()
        .isSimple("PropertyInt16")
        .isType(EdmTechProvider.nameInt16);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComplex/PropertyInt16/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTNavFiveProp)
        .n()
        .isSimple("PropertyInt16")
        .isType(EdmTechProvider.nameInt16, false)
        .n()
        .isValue();

  }

  @Test
  public void runBfuncBnEsRtEntyPpCpCast() {

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isComplex("PropertyComplexTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isTypeFilter(EdmTechProvider.nameCTTwoBase);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isComplex("PropertyComplexTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isTypeFilter(EdmTechProvider.nameCTTwoBase);
  }

  @Test
  public void runBfuncBnEsRtEntityPpSp() {
    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isSimple("PropertyInt16")
        .isType(EdmTechProvider.nameInt16);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyInt16/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isSimple("PropertyInt16")
        .isType(EdmTechProvider.nameInt16)
        .n()
        .isValue();

  }

  @Test
  public void runBfuncBnEsRtEs() {

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isParameter(0, "ParameterString", "'2'")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isParameter(0, "ParameterString", "'3'")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .n()
        .isCount();

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()(PropertyInt16=1,PropertyString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");

  }

  @Test
  public void runBfuncBnEsRtEsBa() {

    testUri.run("ESKeyNav(PropertyInt16=1)/CollPropertyComplex"
        + "/com.sap.odata.test1.BFCCollCTPrimCompRTESAllPrim()/com.sap.odata.test1.BAESAllPrimRTETAllPrim")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp)
        .n()
        .isFunction("BFCCollCTPrimCompRTESAllPrim")
        .n()
        .isAction("BAESAllPrimRTETAllPrim");

  }

  @Test
  public void runBfuncBnEsRtPrim() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTString()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTString");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTString()/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTString")
        .isType(EdmTechProvider.nameString)
        .n()
        .isValue();
  }

  @Test
  public void runbfuncBnEsRtPrimColl() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollString()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollString")
        .isType(EdmTechProvider.nameString, true);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollString()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollString")
        .isType(EdmTechProvider.nameString, true)
        .n()
        .isCount();
  }

  @Test
  public void runBfuncBnPpropCollRtEs() {
    testUri.run("ESKeyNav(1)/CollPropertyString/com.sap.odata.test1.BFCCollStringRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isSimple("CollPropertyString")
        .n()
        .isFunction("BFCCollStringRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true);

    testUri.run("ESKeyNav(1)/CollPropertyString/com.sap.odata.test1.BFCCollStringRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isSimple("CollPropertyString")
        .n()
        .isFunction("BFCCollStringRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .n()
        .isCount();
  }

  @Test
  public void runBfuncBnPpropRtEs() {

    testUri.run("ESKeyNav(1)/PropertyString/com.sap.odata.test1.BFCStringRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isSimple("PropertyString")
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true);

    testUri.run("ESKeyNav(1)/PropertyString/com.sap.odata.test1.BFCStringRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isSimple("PropertyString")
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .n()
        .isCount();

    testUri.run("ESKeyNav(1)/PropertyString/com.sap.odata.test1.BFCStringRTESTwoKeyNav()/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isSimple("PropertyString")
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .n()
        .isRef();
  }

  @Test
  public void runBfuncBnSingleRtEs() {

    testUri.run("SINav/com.sap.odata.test1.BFCSINavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .n()
        .isFunction("BFCSINavRTESTwoKeyNav");
  }

  @Test
  public void runBfuncBnSingleCastRtEs() {
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/com.sap.odata.test1.BFCETBaseTwoKeyNavRTESBaseTwoKey()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESBaseTwoKey");
  }

  @Test
  public void runBactionEntity() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.BAETTwoKeyNavRTETTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isAction("BAETTwoKeyNavRTETTwoKeyNav");

    testUri.run("ESKeyNav(PropertyInt16=1)/com.sap.odata.test1.BAETTwoKeyNavRTETTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isAction("BAETTwoKeyNavRTETTwoKeyNav");
  }

  @Test
  public void runBactionEntity_set() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BAESTwoKeyNavRTESTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isAction("BAESTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void runBactionEntityCast() {

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/com.sap.odata.test1.BAETBaseTwoKeyNavRTETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isAction("BAETBaseTwoKeyNavRTETBaseTwoKeyNav");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav/com.sap.odata.test1.BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBaseTwoKeyNav)
        .n()
        .isAction("BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav");
  }

  @Test
  public void runCrossjoin() {
    testUri.run("$crossjoin(ESKeyNav)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESKeyNav"));

    testUri.run("$crossjoin(ESKeyNav, ESTwoKeyNav)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESKeyNav", "ESTwoKeyNav"));
  }

  @Test
  public void runCrossjoinError() {
    // testUri.run("$crossjoin");
    // testUri.run("$crossjoin/error");
    // testUri.run("$crossjoin()");
    // testUri.run("$crossjoin(ESKeyNav, ESTwoKeyNav)/invalid");
    // testUri.run("$crossjoin(invalidEntitySet)");
  }

  @Test
  public void runEntityId() {
    testUri.run("$entity?$id=ESKeyNav(1)")
        .isKind(UriInfoKind.entityId)
        .isID("ESKeyNav(1)");
    testUri.run("$entity/com.sap.odata.test1.ETKeyNav?$id=ESKeyNav(1)")
        .isKind(UriInfoKind.entityId)
        .isEntityType(EdmTechProvider.nameETKeyNav)
        .isID("ESKeyNav(1)");
  }

  @Test
  public void runEntityIdError() {
    // entity_id_error

    // testUri.run("$entity");
    // testUri.run("$entity?$idfalse=ESKeyNav(1)");
    // testUri.run("$entity/com.sap.odata.test1.invalidType?$id=ESKeyNav(1)");
    // testUri.run("$entity/invalid?$id=ESKeyNav(1)");
  }

  @Test
  public void runEsName() {
    testUri.run("ESAllPrim")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isType(EdmTechProvider.nameETAllPrim, true);

    testUri.run("ESAllPrim/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isType(EdmTechProvider.nameETAllPrim, true)
        .n()
        .isCount();
  }

  @Test
  public void runEsNameError() {

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
    // +"/com.sap.odata.test1.ETTwoBaseTwoKeyTwoPrim");
    // testUri.run("ETBaseTwoKeyTwoPrim/com.sap.odata.test1.ETBaseTwoKeyTwoPrim(1)/com.sap.odata.test1.ETAllKey");
    // testUri.run("ETBaseTwoKeyTwoPrim(1)/com.sap.odata.test1.ETBaseTwoKeyTwoPrim('1')/com.sap.odata.test1.ETAllKey");
    // testUri.run("ETBaseTwoKeyTwoPrim(1)/com.sap.odata.test1.ETBaseTwoKeyTwoPrim"
    // +"/com.sap.odata.test1.ETTwoBaseTwoKeyTwoPrim");
    // testUri.run("ETBaseTwoKeyTwoPrim/com.sap.odata.test1.ETBaseTwoKeyTwoPrim"
    // +"/com.sap.odata.test1.ETTwoBaseTwoKeyTwoPrim(1)");
    // testUri.run("ETBaseTwoKeyTwoPrim/com.sap.odata.test1.ETAllKey");
    // testUri.run("ETBaseTwoKeyTwoPrim()");
    // testUri.run("ESAllNullable(1)/CollPropertyString/$value");
    // testUri.run("ETMixPrimCollComp(1)/ComplexProperty/$value");
  }

  @Test
  public void runEsNameCast() {
    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim, true)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase(-32768)/com.sap.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETTwoBase(-32768)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768");

    testUri.run("ESTwoPrim/Namespace1_Alias.ETTwoBase(-32768)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768");

  }

  @Test
  public void runEsNamePpSpCast() {

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isSimple("PropertyDate");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComplex/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isSimple("PropertyInt16");
  }

  @Test
  public void runEsNameKey() {
    testUri.run("ESCollAllPrim(1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESCollAllPrim");

    testUri.run("ESCollAllPrim(PropertyInt16=1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESCollAllPrim");

    testUri.run("ESFourKeyAlias(PropertyInt16=1,KeyAlias1=2,KeyAlias2='3',KeyAlias3='4')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESFourKeyAlias")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "KeyAlias1", "2")
        .isKeyPredicate(2, "KeyAlias2", "'3'")
        .isKeyPredicate(3, "KeyAlias3", "'4'");

    testUri.run("ESCollAllPrim(null)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESCollAllPrim");
  }

  @Test
  public void runEsNameParaKeys() {
    testUri.run("ESAllKey(PropertyString='O''Neil',PropertyBoolean=true,PropertyByte=255,"
        + "PropertySByte=-128,PropertyInt16=-32768,PropertyInt32=-2147483648,"
        + "PropertyInt64=-9223372036854775808,PropertyDecimal=0.1,PropertyDate=2013-09-25,"
        + "PropertyDateTimeOffset=2002-10-10T12:00:00-05:00,"
        + "PropertyDuration=duration'P10DT5H34M21.123456789012S',"
        + "PropertyGuid=12345678-1234-1234-1234-123456789012,"
        + "PropertyTimeOfDay=12:34:55.123456789012)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllKey")
        .isKeyPredicate(0, "PropertyString", "'O''Neil'")
        .isKeyPredicate(1, "PropertyBoolean", "true")
        .isKeyPredicate(2, "PropertyByte", "255")
        .isKeyPredicate(3, "PropertySByte", "-128")
        .isKeyPredicate(4, "PropertyInt16", "-32768")
        .isKeyPredicate(5, "PropertyInt32", "-2147483648")
        .isKeyPredicate(6, "PropertyInt64", "-9223372036854775808")
        .isKeyPredicate(7, "PropertyDecimal", "0.1")
        .isKeyPredicate(8, "PropertyDate", "2013-09-25")
        .isKeyPredicate(9, "PropertyDateTimeOffset", "2002-10-10T12:00:00-05:00")
        .isKeyPredicate(10, "PropertyDuration", "duration'P10DT5H34M21.123456789012S'")
        .isKeyPredicate(11, "PropertyGuid", "12345678-1234-1234-1234-123456789012")
        .isKeyPredicate(12, "PropertyTimeOfDay", "12:34:55.123456789012");
  }

  @Test
  public void runEsNameKeyCast() {
    // testUri.run("xESTwoPrim(1)/com.sap.odata.test1.ETBase(1)");
    // testUri.run("xESTwoPrim/com.sap.odata.test1.ETBase(1)/com.sap.odata.test1.ETTwoBase(1)");
    // testUri.run("xESBase/com.sap.odata.test1.ETTwoPrim(1)");

    testUri.run("ESTwoPrim(1)/com.sap.odata.test1.ETBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBase);

    testUri.run("ESTwoPrim(1)/com.sap.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase(1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETTwoBase(1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase(1)/com.sap.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBase);
  }

  @Test
  public void runEsNameParaKeysCast() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");
  }

  @Test
  public void run_EsNamePpCp() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplex("PropertyComplex");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComplex/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isComplex("PropertyComplex");
  }

  @Test
  public void runEsNamePpCpColl() {
    testUri.run("ESMixPrimCollComp(5)/CollPropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESMixPrimCollComp")
        .isKeyPredicate(0, "PropertyInt16", "5")
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTTwoPrim, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/CollPropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isNav("NavPropertyETTwoKeyNavOne")
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/CollPropertyComplex/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isNav("NavPropertyETTwoKeyNavOne")
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true)
        .n()
        .isCount();
  }

  @Test
  public void runEsNamePpCpCast() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex");

    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplex/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isComplex("PropertyComplex");

    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")

        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplexTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isTypeFilter(EdmTechProvider.nameCTBase);

    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplexTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isTypeFilter(EdmTechProvider.nameCTTwoBase);
  }

  @Test
  public void runNsNamePpNp() {
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany");

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETKeyNavMany");

    testUri.run("ESKeyNav(PropertyInt16=1)/NavPropertyETKeyNavMany(PropertyInt16=2)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETKeyNavMany");

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETKeyNavMany")
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isSimple("PropertyInt16");

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETKeyNavMany")
        .n()
        .isComplex("PropertyComplex");

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/NavPropertyETKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2");

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETKeyNavMany(4)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, false);

    testUri.run("ESKeyNav(1)/PropertyComplex/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isNav("NavPropertyETTwoKeyNavOne")
        .isType(EdmTechProvider.nameETTwoKeyNav, false);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='(3)')"
        + "/PropertyComplex/PropertyComplex/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isSimple("PropertyInt16");

    testUri.run("ESKeyNav(1)/NavPropertyETMediaMany(2)/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETMediaMany")
        .isType(EdmTechProvider.nameETMedia, false)
        .n()
        .isValue();

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETKeyNavOne/NavPropertyETMediaOne/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .n()
        .isNav("NavPropertyETKeyNavOne")
        .n()
        .isNav("NavPropertyETMediaOne")
        .n()
        .isValue();

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETKeyNavOne/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .n()
        .isNav("NavPropertyETKeyNavOne")
        .n()
        .isRef();
  }

  @Test
  public void runEsNamePpNpCast() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNav("NavPropertyETKeyNavMany");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETKeyNavMany(3)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNav("NavPropertyETKeyNavMany");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETTwoBaseTwoKeyNav(PropertyInt16=3,PropertyString='4')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBaseTwoKeyNav);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=4,PropertyString='5')"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav/NavPropertyETBaseTwoKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "4")
        .isKeyPredicate(1, "PropertyString", "'5'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBaseTwoKeyNav)
        .n()
        .isNav("NavPropertyETBaseTwoKeyNavMany");

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/"
        + "NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=4,PropertyString='5')/"
        + "NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "4")
        .isKeyPredicate(1, "PropertyString", "'5'")
        .n()
        .isNav("NavPropertyETKeyNavMany");

  }

  @Test
  public void runEsNamePpNpRc() {
    // testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany('2')");
    // testUri.run("ESKeyNav(PropertyInt16=1)/NavPropertyETTwoKeyNavMany(PropertyString='2')");

  }

  @Test
  public void runEsNamePpSp() {
    testUri.run("ESAllPrim(1)/PropertyByte")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isSimple("PropertyByte");

    testUri.run("ESAllPrim(1)/PropertyByte/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isSimple("PropertyByte")
        .n()
        .isValue();

    testUri.run("ESMixPrimCollComp(1)/PropertyComplex/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESMixPrimCollComp")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isSimple("PropertyString");
  }

  @Test
  public void runEsNamePpSpColl() {
    testUri.run("ESCollAllPrim(1)/CollPropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESCollAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isSimple("CollPropertyString")
        .isType(EdmTechProvider.nameString, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/CollPropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .n()
        .isSimple("CollPropertyString")
        .isType(EdmTechProvider.nameString, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/CollPropertyString/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .n()
        .isSimple("CollPropertyString")
        .isType(EdmTechProvider.nameString, true)
        .n()
        .isCount();

  }

  @Test
  public void runEsNameRef() {
    testUri.run("ESAllPrim/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .n()
        .isRef();

    testUri.run("ESAllPrim(-32768)/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "-32768")
        .n()
        .isRef();
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .n()
        .isRef();
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .n()
        .isRef();
  }

  @Test
  public void runFunctionImpBf() {

    testUri.run("FICRTString()/com.sap.odata.test1.BFCStringRTESTwoKeyNav()");
  }

  @Test
  public void runFunctionImpCastBf() {

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTETTwoKeyNav");

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTETTwoKeyNav");
  }

  @Test
  public void runFunctionImpEntity() {

    testUri.run("FICRTETKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETKeyNav")
        .isFunction("UFCRTETKeyNav")
        .isType(EdmTechProvider.nameETKeyNav);

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=2,PropertyString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");

    testUri.run("FICRTETMedia()/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETMedia")
        .isFunction("UFCRTETMedia")
        .n()
        .isValue();

    testUri.run("FICRTETKeyNav()/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETKeyNav")
        .isFunction("UFCRTETKeyNav")
        .n()
        .isRef();
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .n()
        .isRef();

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=2,PropertyString='3')"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");
  }

  @Test
  public void runFunctionImpEs() {
    /**/
    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESMixPrimCollCompTwoParam")
        .isFunction("UFCRTESMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EdmTechProvider.nameETMixPrimCollComp);

    testUri.run("FINRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FINRTESMixPrimCollCompTwoParam")
        .isFunction("UFNRTESMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EdmTechProvider.nameETMixPrimCollComp);

    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESMixPrimCollCompTwoParam")
        .isFunction("UFCRTESMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EdmTechProvider.nameETMixPrimCollComp)
        .n()
        .isCount();
  }

  @Test
  public void runFunctionImpError() {
    /*
     * testUri.run("FICRTCollCTTwoPrimParam()");
     * testUri.run("FICRTCollCTTwoPrimParam(invalidParam=2)");
     * testUri.run("FICRTCollCTTwoPrimParam(ParameterInt16='1',ParameterString='2')");
     */
  }

  @Test
  public void runFunctionImpEsAlias() {

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@parameterAlias)?@parameterAlias=1");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@parameterAlias)/$count?@parameterAlias=1");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@invalidAlias)?@validAlias=1");
  }

  @Test
  public void runFunctionImpEsCast() {

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isCount();

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBaseTwoKeyNav);

  }

  @Test
  public void runSingletonEntityValue() {
    testUri.run("SIMedia/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SIMedia")
        .n().isValue();
  }

  @Test
  public void runSingletonPpNpCast() {
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNav("NavPropertyETKeyNavMany");

    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany(1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNav("NavPropertyETKeyNavMany")
        .isKeyPredicate(0, "PropertyInt16", "1");

  }

  @Test
  public void runSingletonPpCpCast() {
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex");

    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplex/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isComplex("PropertyComplex");

    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplexTwoPrim/com.sap.odata.test1.CTBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplexTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isTypeFilter(EdmTechProvider.nameCTBase);

  }

  @Test
  public void runSingletonPpSpCast() {
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isSimple("PropertyInt16");

    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/CollPropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isSimple("CollPropertyString")
        .isType(EdmTechProvider.nameString, true);

  }

  @Test
  public void runSingletonEntityPpNp() {
    testUri.run("SINav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isNav("NavPropertyETKeyNavMany");

    testUri.run("SINav/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");

  }

  @Test
  public void runSingletonEntityPpCp() {
    testUri.run("SINav/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("PropertyComplex");

    testUri.run("SINav/PropertyComplex/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isComplex("PropertyComplex");

  }

  @Test
  public void runSingletonEntityPpCpColl() {
    testUri.run("SINav/CollPropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true);

    testUri.run("SINav/CollPropertyComplex/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true)
        .n()
        .isCount();
  }

  @Test
  public void runSingletonEntityPpSp() {
    testUri.run("SINav/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isSimple("PropertyString");
  }

  @Test
  public void runSingletonEntityPpSpColl() {
    testUri.run("SINav/CollPropertyString")

        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isSimple("CollPropertyString")
        .isType(EdmTechProvider.nameString, true);
    testUri.run("SINav/CollPropertyString/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isSimple("CollPropertyString")
        .isType(EdmTechProvider.nameString, true)
        .n()
        .isCount();
  }

  //@Test
  public void runExpand() {

    testUri.run("ESKeyNav(1)?$expand=*")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegStar(0);

    testUri.run("ESKeyNav(1)?$expand=*/$ref")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegStar(0)
        .isSegRef(1);

    testUri.run("ESKeyNav(1)?$expand=*/$ref,NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegStar(0).isSegRef(1)
        .n()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany");

    testUri.run("ESKeyNav(1)?$expand=*($levels=3)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegStar(0)
        .isLevels("3");

    testUri.run("ESKeyNav(1)?$expand=*($levels=max)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegStar(0)
        .isLevels("max");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef();

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne/$ref")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavOne")
        .isType(EdmTechProvider.nameETKeyNav, false)
        .n().isRef();

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($filter=PropertyInt16 eq 1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator().isFilterSerialized("<PropertyInt16 eq 1>");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($orderby=PropertyInt16)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator();// .isFilterSerialized(""); TODO check order BY

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($skip=1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isTopText("1");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($count=true)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isCountText("1");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($skip=1;$top=3)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("3");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($skip=1%3b$top=3)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")

        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("3");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$count")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isCount();

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne/$count")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavOne")
        .isType(EdmTechProvider.nameETKeyNav, false)
        .n().isCount();

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$count($filter=PropertyInt16 gt 1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isCount()
        .goUpExpandValidator()
        .isFilterSerialized("<PropertyInt16 gt 1>");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($filter=PropertyInt16 eq 1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isFilterSerialized("<PropertyInt16 eq 1>");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($orderby=PropertyInt16)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator();// TODO check orderby

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($skip=1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("1");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isTopText("1");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($count=true)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isCountText("1");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($select=PropertyString)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSelectText("PropertyString");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($expand=NavPropertyETTwoKeyNavOne)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goExpand()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETTwoKeyNavOne");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($expand=NavPropertyETKeyNavMany)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goExpand()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne($levels=5)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavOne")
        .isType(EdmTechProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .isLevelsText("5");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($select=PropertyString)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSelectText("PropertyString");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne($levels=max)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavOne")
        .isType(EdmTechProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .isLevelsText("max");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($skip=1;$top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("2");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($skip=1%3b$top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("max")
        .isTopText("2");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='Hugo')?$expand=NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'Hugo'")
        .goExpand()
        .first()
        .goPath().first()
        .isIt().n()
        .isNav("NavPropertyETKeyNavMany")
        .isType(EdmTechProvider.nameETKeyNav, true);

    testUri.run("ESTwoKeyNav?"
        + "$expand=com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNav("NavPropertyETKeyNavMany");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='Hugo')?"
        + "$expand=com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'Hugo'")
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNav("NavPropertyETKeyNavMany");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')?"
        + "$expand=com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETTwoKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNav("NavPropertyETTwoKeyNavMany");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')?$expand=com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNav("NavPropertyETTwoKeyNavMany")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav?$expand=com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplexNav/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplexNav")
        .isType(EdmTechProvider.nameCTBasePrimCompNav)
        .n()
        .isNav("NavPropertyETTwoKeyNavOne");

    testUri.run("ESTwoKeyNav?$expand=com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplexNav"
        + "/com.sap.odata.test1.CTTwoBasePrimCompNav/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplexNav")
        .isType(EdmTechProvider.nameCTBasePrimCompNav)
        .n()
        .isNav("NavPropertyETTwoKeyNavOne");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref,NavPropertyETTwoKeyNavMany($skip=2;$top=1)")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .n().isNav("NavPropertyETKeyNavMany")
        .n().isRef()
        .goUpExpandValidator()
        .n()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .n().isNav("NavPropertyETTwoKeyNavMany")
        .goUpExpandValidator()
        .isSkipText("2")
        .isTopText("1");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')?$expand=com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETTwoBaseTwoKeyNav($select=PropertyString)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n().isNav("NavPropertyETTwoKeyNavMany")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBaseTwoKeyNav)
        .goUpExpandValidator(); // TODO check select

    testUri.run("ESKeyNav?$expand=NavPropertyETKeyNavOne($expand=NavPropertyETKeyNavMany("
        + "$expand=NavPropertyETKeyNavOne))")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETKeyNav)
        .n().isNav("NavPropertyETKeyNavOne")
        .goUpExpandValidator()
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETKeyNav)
        .n().isNav("NavPropertyETKeyNavMany")
        .goUpExpandValidator()
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETKeyNav)
        .n().isNav("NavPropertyETKeyNavOne");

    testUri.run("ESKeyNav?$expand=NavPropertyETKeyNavOne($select=PropertyInt16)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETKeyNav)
        .n().isNav("NavPropertyETKeyNavOne")
        .goUpExpandValidator();
    // .isSelectText("PropertyInt16") //TODO check select

    testUri.run("ESKeyNav?$expand=NavPropertyETKeyNavOne($select=PropertyComplex/PropertyInt16)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETKeyNav)
        .n().isNav("NavPropertyETKeyNavOne")
        .goUpExpandValidator();
    // .isSelectText("PropertyInt16")//TODO check select
  }

  private void isSkipText(String string) {
    // TODO Auto-generated method stub

  }

  @Test
  public void runTop() {
    // top
    testUri.run("ESKeyNav?$top=1")
        .isKind(UriInfoKind.resource).goPath()
        .isEntitySet("ESKeyNav")
        .isTopText("1");

    testUri.run("ESKeyNav?$top=0")
        .isKind(UriInfoKind.resource).goPath()
        .isEntitySet("ESKeyNav")
        .isTopText("0");

    testUri.run("ESKeyNav?$top=-3")
        .isKind(UriInfoKind.resource).goPath()
        .isEntitySet("ESKeyNav")
        .isTopText("-3");
  }

  @Test
  public void runFormat() {
    // format
    testUri.run("ESKeyNav(1)?$format=atom")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("atom");
    testUri.run("ESKeyNav(1)?$format=json")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("json");
    testUri.run("ESKeyNav(1)?$format=xml")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("xml");
    testUri.run("ESKeyNav(1)?$format=IANA_content_type/must_contain_a_slash")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("IANA_content_type/must_contain_a_slash");
    testUri.run("ESKeyNav(1)?$format=Test_all_valid_signsSpecified_for_format_signs%26-._~$@%27/Aa123%26-._~$@%27")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("Test_all_valid_signsSpecified_for_format_signs%26-._~$@%27/Aa123%26-._~$@%27");
  }

  @Test
  public void runCount() {
    // count
    testUri.run("ESAllPrim?$count=true")
        .isKind(UriInfoKind.resource).goPath()
        .isInlineCountText("true");
    testUri.run("ESAllPrim?$count=false")
        .isKind(UriInfoKind.resource).goPath()
        .isInlineCountText("false");
    // testUri.run("ESAllPrim?$count=foo");
  }

  @Test
  public void skip() {
    // skip
    testUri.run("ESAllPrim?$skip=3")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipText("3");
    testUri.run("ESAllPrim?$skip=0")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipText("0");
    testUri.run("ESAllPrim?$skip=-3")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipText("-3");
  }

  @Test
  public void skiptoken() {

    testUri.run("ESAllPrim?$skiptoken=foo")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipTokenText("foo");
  }

  @Test
  public void misc() {

    testUri.run("");

    testUri.run("$all")
        .isKind(UriInfoKind.all);

    testUri.run("$metadata")
        .isKind(UriInfoKind.metadata);

    testUri.run("$batch")
        .isKind(UriInfoKind.batch);

    testUri.run("$crossjoin(ESKeyNav)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESKeyNav"));

    testUri.run("ESKeyNav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav");
    testUri.run("ESKeyNav(1)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1");

    testUri.run("SINav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isSingleton("SINav");

    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath()
        .isFunctionImport("FICRTESMixPrimCollCompTwoParam")
        .isType(EdmTechProvider.nameETMixPrimCollComp)
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'");

    testUri.run("FICRTETKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTETKeyNav")
        .isType(EdmTechProvider.nameETKeyNav);

    testUri.run("FICRTCollCTTwoPrim()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollCTTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim);

    testUri.run("FICRTCTAllPrimTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCTAllPrimTwoParam")
        .isType(EdmTechProvider.nameCTAllPrim)
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'");

    testUri.run("FICRTCollStringTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollStringTwoParam")
        .isType(EdmTechProvider.nameString)
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'");

    testUri.run("FICRTStringTwoParam(ParameterInt16=1)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTStringTwoParam")
        .isFunction("UFCRTStringTwoParam")
        .isType(EdmTechProvider.nameString)
        .isParameter(0, "ParameterInt16", "1");

    testUri.run("FICRTStringTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTStringTwoParam")
        .isFunction("UFCRTStringTwoParam")
        .isType(EdmTechProvider.nameString)
        .isParameter(0, "ParameterInt16", "1");

    testUri.run("AIRTETParam")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isActionImport("AIRTETParam");

    testUri.run("AIRTPrimParam")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isActionImport("AIRTPrimParam");

    testUri.run("ESKeyNav/$count")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .n().isCount();

    testUri.run("ESKeyNav/$ref")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .n().isRef();

    testUri.run("ESKeyNav/$count")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .n().isCount();

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFCESTwoKeyNavRTESTwoKeyNav");

    testUri.run("ESAllPrim/com.sap.odata.test1.BAESAllPrimRTETAllPrim")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESAllPrim")
        .n().isAction("BAESAllPrimRTETAllPrim");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFCESTwoKeyNavRTESTwoKeyNav");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav/$count")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isCount();

    testUri.run("ESTwoKeyNav/$ref")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isRef();

    testUri.run("ESKeyNav(1)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1");

    testUri.run("ESKeyNav(1)/$ref")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isRef();

    testUri.run("ESMedia(1)/$value")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESMedia")
        .n()
        .isValue();

    testUri.run("ESAllPrim/com.sap.odata.test1.BAESAllPrimRTETAllPrim")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESAllPrim")
        .n().isAction("BAESAllPrimRTETAllPrim");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFCESTwoKeyNavRTESTwoKeyNav");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/$ref")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n().isRef();

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/$value")
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n().isValue();

  }

  @Test
  public void testSpecial() {
    // testFilter.runOnETKeyNav("any()");

  }

  //@Test
  public void TestFilter() {
/*
    testFilter.runOnETTwoKeyNav("PropertyString")
        .is("<$it/PropertyString>")
        .isType(EdmTechProvider.nameString);

    testFilter.runOnETTwoKeyNav("PropertyComplex/PropertyInt16")
        .is("<$it/PropertyComplex/PropertyInt16>")
        .isType(EdmTechProvider.nameInt16);

    testFilter.runOnETTwoKeyNav("PropertyComplex/PropertyComplex/PropertyDate")
        .is("<$it/PropertyComplex/PropertyComplex/PropertyDate>")
        .isType(EdmTechProvider.nameDate);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne")
        .is("<$it/NavPropertyETTwoKeyNavOne>")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyString")
        .is("<$it/NavPropertyETTwoKeyNavOne/PropertyString>")
        .isType(EdmTechProvider.nameString);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex")
        .is("<$it/NavPropertyETTwoKeyNavOne/PropertyComplex>")
        .isType(EdmTechProvider.nameCTPrimComp);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyComplex")
        .is("<$it/NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyComplex>")
        .isType(EdmTechProvider.nameCTAllPrim);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyInt16")
        .is("<$it/NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyInt16>")
        .isType(EdmTechProvider.nameInt16);
    
    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyInt16 eq 1")
        .is("<<$it/NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyInt16> eq <1>>")
        .root().left()
        .isType(EdmTechProvider.nameInt16)
        .root().right()
        .isLiteral("1");
       
    // testFilter
    // .runOnETTwoKeyNav(
    // "NavPropertyETKeyNavMany(1)/NavPropertyETTwoKeyNavMany(PropertyString='2')/PropertyString eq 'SomeString'");
    testFilter.runOnETTwoKeyNav("com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate eq 2013-11-12")
        .is("<<$it/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate> eq <2013-11-12>>")
        .root().left()
        .isType(EdmTechProvider.nameDate)
        .root().right()
        .isLiteral("2013-11-12");
    
    testFilter.runOnCTTwoPrim("com.sap.odata.test1.CTBase/AdditionalPropString eq 'SomeString'")
        .is("<<$it/com.sap.odata.test1.CTBase/AdditionalPropString> eq <'SomeString'>>")
        .root().left()
        .isType(EdmTechProvider.nameString)
        .root().right()
        .isLiteral("'SomeString'");
    
    testFilter
        .runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate eq 2013-11-12")
        .is("<<$it/NavPropertyETTwoKeyNavOne/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate> eq <2013-11-12>>")
        .root().left()
        .isType(EdmTechProvider.nameDate)
        .root().right()
        .isLiteral("2013-11-12");
        */
    testFilter
        .runOnETTwoKeyNav("PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase/AdditionalPropString eq 'SomeString'")
        .is("<<$it/PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase/AdditionalPropString> eq <'SomeString'>>")
        .root().left()
        .isType(EdmTechProvider.nameString)
        .root().right()
        .isLiteral("'SomeString'");

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

    testFilter.runOnETAllPrim("PropertySByte add PropertySByte")
        .is("<<$it/PropertySByte> add <$it/PropertySByte>>")
        .root().left()
        .isType(EdmTechProvider.nameByte)
        .root().right()
        .isType(EdmTechProvider.nameByte);
    
    /**/
    testFilter.runOnETAllPrim("PropertyByte add PropertyByte")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt16 add PropertyInt16")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt32 add PropertyInt32")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt64 add PropertyInt64")
        .is("");
    testFilter.runOnETAllPrim("PropertySingle add PropertySingle")
        .is("");
    testFilter.runOnETAllPrim("PropertyDouble add PropertyDouble")
        .is("");
    testFilter.runOnETAllPrim("PropertyDecimal add PropertyDecimal")
        .is("");
    testFilter.runOnETAllPrim("PropertySByte add PropertyDecimal")
        .is("");
    testFilter.runOnETAllPrim("PropertySByte add PropertyInt32")
        .is("");
    testFilter.runOnETAllPrim("PropertySByte add PropertyInt64")
        .is("");
    testFilter.runOnETAllPrim("PropertyDateTimeOffset add PropertyDuration")
        .is("");
    testFilter.runOnETAllPrim("PropertyDuration add PropertyDuration")
        .is("");
    testFilter.runOnETAllPrim("PropertyDate add PropertyDuration")
        .is("");
    testFilter.runOnETAllPrim("PropertySByte sub PropertySByte")
        .is("");
    testFilter.runOnETAllPrim("PropertyByte sub PropertyByte")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt16 sub PropertyInt16")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt32 sub PropertyInt32")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt64 sub PropertyInt64")
        .is("");
    testFilter.runOnETAllPrim("PropertySingle sub PropertySingle")
        .is("");
    testFilter.runOnETAllPrim("PropertyDouble sub PropertyDouble")
        .is("");
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyDecimal")
        .is("");
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyInt32")
        .is("");
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyInt64")
        .is("");
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyByte")
        .is("");
    testFilter.runOnETAllPrim("PropertyDateTimeOffset sub PropertyDuration")
        .is("");
    testFilter.runOnETAllPrim("PropertyDuration sub PropertyDuration")
        .is("");
    testFilter.runOnETAllPrim("PropertyDateTimeOffset sub PropertyDateTimeOffset")
        .is("");
    testFilter.runOnETAllPrim("PropertyDate sub PropertyDuration")
        .is("");
    testFilter.runOnETAllPrim("PropertyDate sub PropertyDate")
        .is("");
    testFilter.runOnETAllPrim("PropertySByte mul PropertySByte")
        .is("");
    testFilter.runOnETAllPrim("PropertyByte mul PropertyByte")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt16 mul PropertyInt16")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt32 mul PropertyInt32")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyInt64")
        .is("");
    testFilter.runOnETAllPrim("PropertySingle mul PropertySingle")
        .is("");
    testFilter.runOnETAllPrim("PropertyDouble mul PropertyDouble")
        .is("");
    testFilter.runOnETAllPrim("PropertyDecimal mul PropertyDecimal")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyInt32")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertySByte")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyDecimal")
        .is("");
    testFilter.runOnETAllPrim("PropertySByte div PropertySByte")
        .is("");
    testFilter.runOnETAllPrim("PropertyByte div PropertyByte")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt16 div PropertyInt16")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt32 div PropertyInt32")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt64 div PropertyInt64")
        .is("");
    testFilter.runOnETAllPrim("PropertySingle div PropertySingle")
        .is("");
    testFilter.runOnETAllPrim("PropertyDouble div PropertyDouble")
        .is("");
    testFilter.runOnETAllPrim("PropertyDecimal div PropertyDecimal")
        .is("");
    testFilter.runOnETAllPrim("PropertyByte div PropertyInt32")
        .is("");
    testFilter.runOnETAllPrim("PropertyByte div PropertyDecimal")
        .is("");
    testFilter.runOnETAllPrim("PropertyByte div PropertySByte")
        .is("");
    // testFilter.runOnETAllPrim("PropertyByte div 0");
    // testFilter.runOnETAllPrim("0 div 0");
    testFilter.runOnETAllPrim("PropertySByte mod PropertySByte")
        .is("");
    testFilter.runOnETAllPrim("PropertyByte mod PropertyByte")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt16 mod PropertyInt16")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt32 mod PropertyInt32")
        .is("");
    testFilter.runOnETAllPrim("PropertyInt64 mod PropertyInt64")
        .is("");
    testFilter.runOnETAllPrim("PropertySingle mod PropertySingle")
        .is("");
    testFilter.runOnETAllPrim("PropertyDouble mod PropertyDouble")
        .is("");
    testFilter.runOnETAllPrim("PropertyDecimal mod PropertyDecimal")
        .is("");

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
    // testFilter.runOnETKeyNav("any()");

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
