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
package org.apache.olingo.server.core.uri.parser;

import java.util.Collections;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException.MessageKeys;
import org.apache.olingo.server.core.uri.testutil.ResourceValidator;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.tecsvc.provider.ActionProvider;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;
import org.apache.olingo.server.tecsvc.provider.ContainerProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EnumTypeProvider;
import org.apache.olingo.server.tecsvc.provider.PropertyProvider;
import org.apache.olingo.server.tecsvc.provider.TypeDefinitionProvider;
import org.junit.Test;

/** Tests of the parts of the URI parser that parse the resource path without query options. */
public class ResourcePathParserTest {

  private static final Edm edm = OData.newInstance().createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  private final TestUriValidator testUri = new TestUriValidator().setEdm(edm);
  private final ResourceValidator testRes = new ResourceValidator().setEdm(edm);

  @Test
  public void esName() throws Exception {
    testRes.run("ESAllPrim")
        .isEntitySet("ESAllPrim")
        .isType(EntityTypeProvider.nameETAllPrim, true);

    testRes.run("ESAllPrim/$count")
        .isEntitySet("ESAllPrim")
        .isType(EntityTypeProvider.nameETAllPrim, true)
        .n()
        .isCount();
  }

  @Test
  public void esNameError() {
    testUri.runEx("ESAllPrim/$count/$ref").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("ESAllPrim/$ref/$count").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("ESAllPrim/$ref/invalid").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("ESAllPrim/$count/invalid").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("ESAllPrim/PropertyString").isExSemantic(MessageKeys.PROPERTY_AFTER_COLLECTION);
    testUri.runEx("ESAllPrim(1)/whatever").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESAllPrim('1')").isExSemantic(MessageKeys.INVALID_KEY_VALUE);
    testUri.runEx("ESAllPrim(PropertyInt16)").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESAllPrim(PropertyInt16=)").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESAllPrim(PropertyInt16=1,Invalid='1')").isExSemantic(MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES);

    testUri.runEx("ESBase/olingo.odata.test1.ETBase/PropertyInt16")
        .isExSemantic(MessageKeys.PROPERTY_AFTER_COLLECTION);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyTwoPrim")
        .isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETBaseTwoKeyTwoPrim(1)/olingo.odata.test1.ETAllKey")
        .isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim(1)/olingo.odata.test1.ETBaseTwoKeyTwoPrim('1')/olingo.odata.test1.ETAllKey")
        .isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim(1)/olingo.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyTwoPrim")
        .isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyTwoPrim(1)")
        .isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETAllKey").isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);
    testUri.runEx("ETBaseTwoKeyTwoPrim()").isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);
    testUri.runEx("ESAllNullable(1)/CollPropertyString/$value").isExSemantic(MessageKeys.ONLY_FOR_TYPED_PARTS);

    testUri.runEx("ETMixPrimCollComp(1)/ComplexProperty/$value").isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);
  }

  @Test
  public void esNameCast() throws Exception {
    testRes.run("ESTwoPrim/olingo.odata.test1.ETBase")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBase);

    testRes.run("ESTwoPrim/olingo.odata.test1.ETBase(-32768)/olingo.odata.test1.ETTwoBase")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBase);

    testRes.run("ESTwoPrim/olingo.odata.test1.ETTwoBase(-32768)")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768");

    testRes.run("ESTwoPrim/Namespace1_Alias.ETTwoBase(-32768)")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768");

    testRes.run("ESTwoPrim/Namespace1_Alias.ETTwoBase(PropertyInt16=-32768)")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768");

    testUri.runEx("ESAllPrim/namespace.Invalid").isExSemantic(MessageKeys.UNKNOWN_TYPE);
  }

  @Test
  public void esNamePpSpCast() throws Exception {

    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false);

    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComp/PropertyInt16")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testRes.run("ESTwoPrim/Namespace1_Alias.ETBase(PropertyInt16=1)/AdditionalPropertyString_5")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBase)
        .isTypeFilterOnEntry(null)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("AdditionalPropertyString_5", PropertyProvider.nameString, false);
  }

  @Test
  public void esNameKey() throws Exception {
    testRes.run("ESKeyNav(1)")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1");

    testRes.run("ESCollAllPrim(PropertyInt16=1)")
        .isEntitySet("ESCollAllPrim");

    testRes.run("ESTwoKeyTwoPrim(PropertyInt16=1,PropertyString='ABC')")
        .isEntitySet("ESTwoKeyTwoPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'ABC'");

    testRes.run("ESFourKeyAlias(PropertyInt16=1,KeyAlias1=2,KeyAlias2='3',KeyAlias3='4')")
        .isEntitySet("ESFourKeyAlias")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "KeyAlias1", "2")
        .isKeyPredicate(2, "KeyAlias2", "'3'")
        .isKeyPredicate(3, "KeyAlias3", "'4'");

    testUri.runEx("ESTwoPrim('wrong')").isExSemantic(MessageKeys.INVALID_KEY_VALUE);
    testUri.runEx("ESTwoPrim(PropertyInt16='wrong')").isExSemantic(MessageKeys.INVALID_KEY_VALUE);
  }

  @Test
  public void esNameParaKeys() throws Exception {
    testRes.run("ESAllKey(PropertyString='O''Neil',PropertyBoolean=true,PropertyByte=255,"
        + "PropertySByte=-128,PropertyInt16=-32768,PropertyInt32=-2147483648,"
        + "PropertyInt64=-9223372036854775808,PropertyDecimal=1,PropertyDate=2013-09-25,"
        + "PropertyDateTimeOffset=2002-10-10T12:00:00-05:00,"
        + "PropertyDuration=duration'P50903316DT2H25M4S',"
        + "PropertyGuid=12345678-1234-1234-1234-123456789012,"
        + "PropertyTimeOfDay=12:34:55)")
        .isEntitySet("ESAllKey")
        .isKeyPredicate(0, "PropertyString", "'O''Neil'")
        .isKeyPredicate(1, "PropertyBoolean", "true")
        .isKeyPredicate(2, "PropertyByte", "255")
        .isKeyPredicate(3, "PropertySByte", "-128")
        .isKeyPredicate(4, "PropertyInt16", "-32768")
        .isKeyPredicate(5, "PropertyInt32", "-2147483648")
        .isKeyPredicate(6, "PropertyInt64", "-9223372036854775808")
        .isKeyPredicate(7, "PropertyDecimal", "1")
        .isKeyPredicate(8, "PropertyDate", "2013-09-25")
        .isKeyPredicate(9, "PropertyDateTimeOffset", "2002-10-10T12:00:00-05:00")
        .isKeyPredicate(10, "PropertyDuration", "duration'P50903316DT2H25M4S'")
        .isKeyPredicate(11, "PropertyGuid", "12345678-1234-1234-1234-123456789012")
        .isKeyPredicate(12, "PropertyTimeOfDay", "12:34:55");
  }

  @Test
  public void esNameKeyCast() throws Exception {
    testRes.run("ESTwoPrim(1)/olingo.odata.test1.ETBase")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBase);

    testRes.run("ESTwoPrim(1)/olingo.odata.test1.ETTwoBase")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBase);

    testRes.run("ESTwoPrim/olingo.odata.test1.ETBase(1)")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBase);

    testRes.run("ESTwoPrim/olingo.odata.test1.ETTwoBase(1)")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBase);

    testRes.run("ESTwoPrim/olingo.odata.test1.ETBase(1)/olingo.odata.test1.ETTwoBase")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBase);

    testRes.run("ESTwoPrim/olingo.odata.test1.ETTwoBase")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBase);

    // Keys cannot be specified twice.
    testUri.runEx("ESTwoPrim(1)/olingo.odata.test1.ETBase(1)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);
    testUri.runEx("ESTwoPrim/olingo.odata.test1.ETBase(1)/olingo.odata.test1.ETTwoBase(1)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testUri.runEx("ESAllPrim(0)/namespace.Invalid").isExSemantic(MessageKeys.UNKNOWN_TYPE);
    testUri.runEx("ESBase/olingo.odata.test1.ETTwoPrim(1)").isExSemantic(MessageKeys.INCOMPATIBLE_TYPE_FILTER);
  }

  @Test
  public void esNameParaKeysCast() throws Exception {
    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav);

    testRes.run("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");

    testRes.run("ESTwoPrim(PropertyInt16=1)/olingo.odata.test1.ETBase/AdditionalPropertyString_5")
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(null)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("AdditionalPropertyString_5", PropertyProvider.nameString, false);
  }

  @Test
  public void esNamePpCp() throws Exception {
    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComp")
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false);

    testRes.run("ESCompAllPrim(1)/PropertyComp/PropertyString")
        .isEntitySet("ESCompAllPrim").isKeyPredicate(0, "PropertyInt16", "1")
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComp/PropertyComp")
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false);

    testRes.run("ESMixEnumDefCollComp(PropertyEnumString=olingo.odata.test1.ENString'String1',"
        + "PropertyDefString='key1')/PropertyEnumString")
        .isEntitySet("ESMixEnumDefCollComp")
        .isKeyPredicate(0, "PropertyEnumString", "olingo.odata.test1.ENString'String1'")
        .isKeyPredicate(1, "PropertyDefString", "'key1'")
        .n()
        .isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false);

    testRes.run("ESMixEnumDefCollComp(PropertyEnumString=olingo.odata.test1.ENString'String1',"
        + "PropertyDefString='key1')/PropertyDefString")
        .isEntitySet("ESMixEnumDefCollComp")
        .isKeyPredicate(0, "PropertyEnumString", "olingo.odata.test1.ENString'String1'")
        .isKeyPredicate(1, "PropertyDefString", "'key1'")
        .n()
        .isPrimitiveProperty("PropertyDefString", TypeDefinitionProvider.nameTDString, false);
  }

  @Test
  public void esNamePpCpColl() throws Exception {
    testRes.run("ESMixPrimCollComp(5)/CollPropertyComp")
        .isEntitySet("ESMixPrimCollComp")
        .isKeyPredicate(0, "PropertyInt16", "5")
        .n()
        .isComplexProperty("CollPropertyComp", ComplexTypeProvider.nameCTTwoPrim, true);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/CollPropertyComp")
        .isEntitySet("ESKeyNav")
        .n().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isComplexProperty("CollPropertyComp", ComplexTypeProvider.nameCTPrimComp, true);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/CollPropertyComp/$count")
        .isEntitySet("ESKeyNav")
        .n().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isComplexProperty("CollPropertyComp", ComplexTypeProvider.nameCTPrimComp, true)
        .n().isCount();

    testRes.run("ESMixEnumDefCollComp(PropertyEnumString=olingo.odata.test1.ENString'String1',"
        + "PropertyDefString='key1')/CollPropertyEnumString")
        .isEntitySet("ESMixEnumDefCollComp")
        .isKeyPredicate(0, "PropertyEnumString", "olingo.odata.test1.ENString'String1'")
        .isKeyPredicate(1, "PropertyDefString", "'key1'")
        .n()
        .isPrimitiveProperty("CollPropertyEnumString", EnumTypeProvider.nameENString, true);

    testRes.run("ESMixEnumDefCollComp(PropertyEnumString=olingo.odata.test1.ENString'String1',"
        + "PropertyDefString='key1')/CollPropertyDefString")
        .isEntitySet("ESMixEnumDefCollComp")
        .isKeyPredicate(0, "PropertyEnumString", "olingo.odata.test1.ENString'String1'")
        .isKeyPredicate(1, "PropertyDefString", "'key1'")
        .n()
        .isPrimitiveProperty("CollPropertyDefString", TypeDefinitionProvider.nameTDString, true);
  }

  @Test
  public void esNamePpCpCast() throws Exception {
    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav/PropertyComp")
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false);

    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComp/PropertyComp")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false);

    testRes
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyCompTwoPrim/olingo.odata.test1.CTBase")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplexProperty("PropertyCompTwoPrim", ComplexTypeProvider.nameCTTwoPrim, false)
        .isTypeFilter(ComplexTypeProvider.nameCTBase);

    testRes
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyCompTwoPrim/olingo.odata.test1.CTTwoBase")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplexProperty("PropertyCompTwoPrim", ComplexTypeProvider.nameCTTwoPrim, false)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBase);
  }

  @Test
  public void esNamePpNp() throws Exception {
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne")
        .isEntitySet("ESKeyNav").isKeyPredicate(0, "PropertyInt16", "1")
        .n().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany")
        .isEntitySet("ESKeyNav").isKeyPredicate(0, "PropertyInt16", "1")
        .n().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true);

    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2");

    testRes.run("ESKeyNav(PropertyInt16=1)/NavPropertyETKeyNavMany(PropertyInt16=2)")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2");

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/PropertyString")
        .isEntitySet("ESKeyNav").isKeyPredicate(0, "PropertyInt16", "1")
        .n().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/PropertyInt16")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavMany(1)")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'")
        .n().isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1");

    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/PropertyCompNav")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTNavFiveProp, false);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne")
        .isEntitySet("ESKeyNav").isKeyPredicate(0, "PropertyInt16", "1")
        .n().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false);

    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/NavPropertyETKeyNavOne")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETKeyNavMany(4)")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "4");

    testRes.run("ESKeyNav(1)/PropertyCompNav/NavPropertyETTwoKeyNavOne")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n().isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTNavFiveProp, false)
        .n().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='(3)')"
        + "/PropertyComp/PropertyComp/PropertyInt16")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'(3)'")
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany/$count")
        .isEntitySet("ESKeyNav").isKeyPredicate(0, "PropertyInt16", "1")
        .n().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .n().isCount();

    testRes.run("ESKeyNav(1)/NavPropertyETMediaMany(2)/$value")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETMediaMany", EntityTypeProvider.nameETMedia, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isValue();

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavOne/PropertyString")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'")
        .n().isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavMany(1)/PropertyString")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'")
        .n().isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETKeyNavOne/NavPropertyETMediaOne/$value")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n()
        .isNavProperty("NavPropertyETMediaOne", EntityTypeProvider.nameETMedia, false)
        .n()
        .isValue();

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETKeyNavOne/$ref")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n()
        .isRef();
  }

  @Test
  public void esNamePpNpCast() throws Exception {
    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETKeyNavMany")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETKeyNavMany(3)")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "3");

    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETTwoKeyNavMany/olingo.odata.test1.ETTwoBaseTwoKeyNav(PropertyInt16=3,PropertyString='4')")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "3")
        .isKeyPredicate(1, "PropertyString", "'4'")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETTwoKeyNavMany/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=4,PropertyString='5')"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav/NavPropertyETBaseTwoKeyNavMany")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "4")
        .isKeyPredicate(1, "PropertyString", "'5'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETBaseTwoKeyNavMany", EntityTypeProvider.nameETBaseTwoKeyNav, true);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/"
        + "NavPropertyETTwoKeyNavMany/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=4,PropertyString='5')/"
        + "NavPropertyETKeyNavMany")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "4")
        .isKeyPredicate(1, "PropertyString", "'5'")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);
  }

  @Test
  public void navigationWithMoreThanOneKey() throws Exception {
    testUri.runEx("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')"
        + "(PropertyInt16=1,PropertyString='2')")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void referentialConstraints() throws Exception {
    // checks for using referential constraints to fill missing keys
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany('2')")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicateRef(0, "PropertyInt16", "PropertyInt16")
        .isKeyPredicate(1, "PropertyString", "'2'");

    testRes.run("ESKeyNav(PropertyInt16=1)/NavPropertyETTwoKeyNavMany(PropertyString='2')")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicateRef(0, "PropertyInt16", "PropertyInt16")
        .isKeyPredicate(1, "PropertyString", "'2'");
  }

  @Test
  public void esNamePpSp() throws Exception {
    testRes.run("ESAllPrim(1)/PropertyByte")
        .isEntitySet("ESAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyByte", PropertyProvider.nameByte, false);

    testRes.run("ESAllPrim(1)/PropertyByte/$value")
        .isEntitySet("ESAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyByte", PropertyProvider.nameByte, false)
        .n()
        .isValue();

    testRes.run("ESMixPrimCollComp(1)/PropertyComp/PropertyString")
        .isEntitySet("ESMixPrimCollComp")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTTwoPrim, false)
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);
  }

  @Test
  public void esNamePpSpColl() throws Exception {
    testRes.run("ESCollAllPrim(1)/CollPropertyString")
        .isEntitySet("ESCollAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/CollPropertyString")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true);

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/CollPropertyString/$count")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .n()
        .isCount();
  }

  @Test
  public void esNameRef() throws Exception {
    testRes.run("ESAllPrim/$ref")
        .isEntitySet("ESAllPrim")
        .n().isRef();

    testRes.run("ESAllPrim(-32768)/$ref")
        .isEntitySet("ESAllPrim").isKeyPredicate(0, "PropertyInt16", "-32768")
        .n().isRef();

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/$ref")
        .isEntitySet("ESKeyNav").isKeyPredicate(0, "PropertyInt16", "1")
        .n().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isRef();

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany/$ref")
        .isEntitySet("ESKeyNav").isKeyPredicate(0, "PropertyInt16", "1")
        .n().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .n().isRef();

    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')/$ref")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isRef();
  }

  @Test
  public void singletonEntityValue() throws Exception {
    testRes.run("SIMedia/$value")
        .isSingleton("SIMedia")
        .n().isValue();
  }

  @Test
  public void singletonPpNpCast() throws Exception {
    testRes.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testRes.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany(1)")
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1");
  }

  @Test
  public void singletonPpCpCast() throws Exception {
    testRes.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/PropertyComp")
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false);

    testRes.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/PropertyComp/PropertyComp")
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false);

    testRes.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/PropertyCompTwoPrim/olingo.odata.test1.CTBase")
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplexProperty("PropertyCompTwoPrim", ComplexTypeProvider.nameCTTwoPrim, false)
        .isTypeFilter(ComplexTypeProvider.nameCTBase);
  }

  @Test
  public void singletonPpSpCast() throws Exception {
    testRes.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/PropertyInt16")
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testRes.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/CollPropertyString")
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .isType(PropertyProvider.nameString, true);
  }

  @Test
  public void singletonEntityPpNp() throws Exception {
    testRes.run("SINav/NavPropertyETKeyNavMany")
        .isSingleton("SINav")
        .n().isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testRes.run("SINav/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')")
        .isSingleton("SINav")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");
  }

  @Test
  public void singletonEntityPpCp() throws Exception {
    testRes.run("SINav/PropertyComp")
        .isSingleton("SINav")
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false);

    testRes.run("SINav/PropertyComp/PropertyComp")
        .isSingleton("SINav")
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false);

    testRes.run("SINav/PropertyComp/PropertyInt16")
        .isSingleton("SINav").isType(EntityTypeProvider.nameETTwoKeyNav)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
  }

  @Test
  public void singletonEntityPpCpColl() throws Exception {
    testRes.run("SINav/CollPropertyComp")
        .isSingleton("SINav")
        .n().isComplexProperty("CollPropertyComp", ComplexTypeProvider.nameCTPrimComp, true);

    testRes.run("SINav/CollPropertyComp/$count")
        .isSingleton("SINav")
        .n().isComplexProperty("CollPropertyComp", ComplexTypeProvider.nameCTPrimComp, true)
        .n().isCount();
  }

  @Test
  public void singletonEntityPpSp() throws Exception {
    testRes.run("SINav/PropertyString")
        .isSingleton("SINav")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);
  }

  @Test
  public void singletonEntityPpSpColl() throws Exception {
    testRes.run("SINav/CollPropertyString")
        .isSingleton("SINav")
        .n().isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true);

    testRes.run("SINav/CollPropertyString/$count")
        .isSingleton("SINav")
        .n().isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .n().isCount();
  }

  @Test
  public void valueOnNonMediaEntity() throws Exception {
    testUri.runEx("ESAllPrim/$value").isExSemantic(UriParserSemanticException.MessageKeys.ONLY_FOR_TYPED_PARTS);
    testUri.runEx("ESAllPrim(1)/NavPropertyETTwoPrimMany/$value").isExSemantic(
        UriParserSemanticException.MessageKeys.ONLY_FOR_TYPED_PARTS);
    testUri.runEx("FICRTCollESMedia()/$value")
        .isExSemantic(UriParserSemanticException.MessageKeys.ONLY_FOR_TYPED_PARTS);

    testUri.runEx("ESAllPrim(1)/$value").isExSemantic(UriParserSemanticException.MessageKeys.NOT_A_MEDIA_RESOURCE);
    testUri.runEx("ESAllPrim(1)/NavPropertyETTwoPrimOne/$value").isExSemantic(
        UriParserSemanticException.MessageKeys.NOT_A_MEDIA_RESOURCE);
    testUri.runEx("FICRTETKeyNav()/$value").isExSemantic(UriParserSemanticException.MessageKeys.NOT_A_MEDIA_RESOURCE);
  }

  @Test
  public void functionBound_varReturnType() {
    // returning primitive
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTString()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(PropertyProvider.nameString, false);

    // returning collection of primitive
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollString()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(PropertyProvider.nameString, true);

    // returning single complex
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCTTwoPrim()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(ComplexTypeProvider.nameCTTwoPrim, false);

    // returning collection of complex
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(ComplexTypeProvider.nameCTTwoPrim, true);

    // returning single entity
    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='ABC')"
        + "/olingo.odata.test1.ETBaseTwoKeyNav/olingo.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false);

    // returning collection of entity (aka entitySet)
    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='ABC')/olingo.odata.test1.BFCSINavRTESTwoKeyNav()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true);
  }

  @Test
  public void functionBound_varOverloading() throws Exception {
    // on ESTwoKeyNav
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    // with string parameter
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_(ParameterString='ABC')")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isParameter(0, "ParameterString", "'ABC'");

    // with string parameter
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav);
  }

  @Test
  public void boundFuncBnCpropCastRtEs() throws Exception {

    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESBaseTwoKeyNav()")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isFunction("BFCCTPrimCompRTESBaseTwoKeyNav");

    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESBaseTwoKeyNav()/$count")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESBaseTwoKeyNav")
        .isType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void boundFuncBnCpropCollRtEs() throws Exception {
    testRes.run("ESKeyNav(PropertyInt16=1)/CollPropertyComp/olingo.odata.test1.BFCCollCTPrimCompRTESAllPrim()")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplexProperty("CollPropertyComp", ComplexTypeProvider.nameCTPrimComp, true)
        .n()
        .isFunction("BFCCollCTPrimCompRTESAllPrim");

    testRes
        .run("ESKeyNav(PropertyInt16=1)/CollPropertyComp/olingo.odata.test1.BFCCollCTPrimCompRTESAllPrim()/$count")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplexProperty("CollPropertyComp", ComplexTypeProvider.nameCTPrimComp, true)
        .n()
        .isFunction("BFCCollCTPrimCompRTESAllPrim")
        .isType(EntityTypeProvider.nameETAllPrim, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void boundFuncBnCpropRtEs() throws Exception {
    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESTwoKeyNav()")
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNav");

    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESTwoKeyNav()/$count")
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void boundFuncBnEntityRtEs() throws Exception {
    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()")
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isFunction("BFCETTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void boundFuncBnEntityCastRtEs() throws Exception {
    testRes
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
            + "/olingo.odata.test1.BFCETBaseTwoKeyNavRTESTwoKeyNav()")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESTwoKeyNav");

    testRes
        .run("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='(''2'')')"
            + "/olingo.odata.test1.BFCETBaseTwoKeyNavRTESTwoKeyNav()")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'(''2'')'")
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void boundFuncBnEsCastRtEs() throws Exception {
    testRes.run("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/olingo.odata.test1.BFCESBaseTwoKeyNavRTESBaseTwoKey()")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCESBaseTwoKeyNavRTESBaseTwoKey");

    testRes.run("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/olingo.odata.test1.BFCESBaseTwoKeyNavRTESBaseTwoKey()"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCESBaseTwoKeyNavRTESBaseTwoKey")
        .isType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

    testRes.run("ESTwoKeyNav"
        + "/olingo.odata.test1.BFC_RTESTwoKeyNav_()"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBaseTwoKeyNav);
  }

  @Test
  public void boundFuncBnEsRtCprop() throws Exception {
    testRes.run("ESAllPrim/olingo.odata.test1.BFNESAllPrimRTCTAllPrim()")
        .isEntitySet("ESAllPrim")
        .n()
        .isFunction("BFNESAllPrimRTCTAllPrim")
        .isType(ComplexTypeProvider.nameCTAllPrim);

    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCTTwoPrim()/olingo.odata.test1.CTBase")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCTTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim, false)
        .isTypeFilterOnEntry(ComplexTypeProvider.nameCTBase);
  }

  @Test
  public void boundFuncBnEsRtCpropColl() throws Exception {
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollCTTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim, true);

    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()/$count")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollCTTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void boundFuncBnEsRtEntityPpNp() throws Exception {
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()/NavPropertyETKeyNavOne")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false);

    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()/NavPropertyETKeyNavOne/$ref")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n()
        .isUriPathInfoKind(UriResourceKind.ref);

    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/NavPropertyETMediaOne/$value")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isNavProperty("NavPropertyETMediaOne", EntityTypeProvider.nameETMedia, false)
        .n()
        .isValue();

    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyComp")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false);

    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyComp/PropertyComp")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false);

    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyString")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/PropertyString")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testUri.runEx("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(WrongParameter='1')")
        .isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString=wrong)")
        .isExSemantic(MessageKeys.INVALID_KEY_VALUE);
  }

  @Test
  public void boundFuncBnEsRtEntyPpNpCast() throws Exception {
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()"
        + "/NavPropertyETTwoKeyNavOne/olingo.odata.test1.ETBaseTwoKeyNav")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav);

    testRes
        .run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()(PropertyInt16=1,PropertyString='2')"
            + "/NavPropertyETTwoKeyNavOne/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBaseTwoKeyNav);
  }

  @Test
  public void boundFuncBnEsRtEntityPpCp() throws Exception {
    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyCompNav")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTNavFiveProp, false);

    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyCompNav/PropertyInt16")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTNavFiveProp, false)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyCompNav/PropertyInt16/$value")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTNavFiveProp, false)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .n()
        .isValue();
  }

  @Test
  public void boundFuncBnEsRtEntyPpCpCast() throws Exception {

    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/PropertyCompTwoPrim/olingo.odata.test1.CTTwoBase")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isComplexProperty("PropertyCompTwoPrim", ComplexTypeProvider.nameCTTwoPrim, false)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBase);

    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/PropertyCompTwoPrim/olingo.odata.test1.CTTwoBase")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isComplexProperty("PropertyCompTwoPrim", ComplexTypeProvider.nameCTTwoPrim, false)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBase);
  }

  @Test
  public void boundFuncBnEsRtEntityPpSp() throws Exception {
    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyInt16")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testRes.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyInt16/$value")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .n()
        .isValue();
  }

  @Test
  public void boundFuncBnEsRtEs() throws Exception {

    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_(ParameterString='2')")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isParameter(0, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testRes.run("ESKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()")
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_(ParameterString='3')")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isParameter(0, "ParameterString", "'3'")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()/$count")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .n()
        .isCount();

    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()(PropertyInt16=1,PropertyString='2')")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");
  }

  @Test
  public void boundFuncBnEsRtEsBa() throws Exception {
    testRes.run("ESKeyNav(PropertyInt16=1)/CollPropertyComp"
        + "/olingo.odata.test1.BFCCollCTPrimCompRTESAllPrim()/olingo.odata.test1.BAESAllPrimRTETAllPrim")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n().isComplexProperty("CollPropertyComp", ComplexTypeProvider.nameCTPrimComp, true)
        .n().isFunction("BFCCollCTPrimCompRTESAllPrim")
        .n().isAction("BAESAllPrimRTETAllPrim");
  }

  @Test
  public void boundFuncBnEsRtPrim() throws Exception {
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTString()")
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFCESTwoKeyNavRTString");

    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTString()/$value")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTString")
        .isType(PropertyProvider.nameString)
        .n()
        .isValue();
  }

  @Test
  public void boundFuncBnEsRtPrimColl() throws Exception {
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollString()")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollString")
        .isType(PropertyProvider.nameString, true);

    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollString()/$count")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollString")
        .isType(PropertyProvider.nameString, true)
        .n()
        .isCount();
  }

  @Test
  public void boundFuncBnPpropCollRtEs() throws Exception {
    testRes.run("ESKeyNav(1)/CollPropertyString/olingo.odata.test1.BFCCollStringRTESTwoKeyNav()")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .n()
        .isFunction("BFCCollStringRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true);

    testRes.run("ESKeyNav(1)/CollPropertyString/olingo.odata.test1.BFCCollStringRTESTwoKeyNav()/$count")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .n()
        .isFunction("BFCCollStringRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isCount();
  }

  @Test
  public void boundFuncBnPpropRtEs() throws Exception {

    testRes.run("ESKeyNav(1)/PropertyString/olingo.odata.test1.BFCStringRTESTwoKeyNav()")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true);

    testRes.run("ESKeyNav(1)/PropertyString/olingo.odata.test1.BFCStringRTESTwoKeyNav()/$count")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isCount();

    testRes.run("ESKeyNav(1)/PropertyString/olingo.odata.test1.BFCStringRTESTwoKeyNav()/$ref")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isRef();
  }

  @Test
  public void boundFuncBnSingleRtEs() throws Exception {
    testRes.run("SINav/olingo.odata.test1.BFCSINavRTESTwoKeyNav()")
        .isSingleton("SINav").isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isFunction("BFCSINavRTESTwoKeyNav");
  }

  @Test
  public void boundFuncBnSingleCastRtEs() throws Exception {
    testRes.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/olingo.odata.test1.BFCETBaseTwoKeyNavRTESBaseTwoKey()")
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESBaseTwoKey");
  }

  @Test
  public void actionBound_on_EntityEntry() throws Exception {

    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.BA_RTETTwoKeyNav")
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isAction("BA_RTETTwoKeyNav");

    testRes.run("ESKeyNav(PropertyInt16=1)/olingo.odata.test1.BA_RTETTwoKeyNav")
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isAction("BA_RTETTwoKeyNav");
  }

  @Test
  public void actionBound_on_EntityCollection() throws Exception {
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BAESTwoKeyNavRTESTwoKeyNav")
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isAction("BAESTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void functionBound_on_var_Types() throws Exception {

    // on primitive
    testRes.run("ESAllPrim(1)/PropertyString/olingo.odata.test1.BFCStringRTESTwoKeyNav()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETAllPrim, false)
        .n()
        .isUriPathInfoKind(UriResourceKind.primitiveProperty)
        .isType(PropertyProvider.nameString);

    // on collection of primitive
    testRes.run("ESCollAllPrim(1)/CollPropertyString/olingo.odata.test1.BFCCollStringRTESTwoKeyNav()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETCollAllPrim, false)
        .n()
        .isUriPathInfoKind(UriResourceKind.primitiveProperty)
        .isType(PropertyProvider.nameString);

    // on complex
    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='ABC')"
        + "/PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESTwoKeyNav()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .n()
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    // on collection of complex
    testRes.run("ESKeyNav(1)/CollPropertyComp/olingo.odata.test1.BFCCollCTPrimCompRTESAllPrim()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .n()
        .isComplexProperty("CollPropertyComp", ComplexTypeProvider.nameCTPrimComp, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETAllPrim);

    // on entity
    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='ABC')"
        + "/olingo.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    // on collection of entity
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n().isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav);
  }

  @Test
  public void actionBound_on_EntityCast() throws Exception {

    testRes.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/olingo.odata.test1.BAETBaseTwoKeyNavRTETBaseTwoKeyNav")
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isAction("BAETBaseTwoKeyNavRTETBaseTwoKeyNav");

    testRes.run("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav/olingo.odata.test1.BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav")
        .isEntitySet("ESTwoKeyNav")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBaseTwoKeyNav)
        .n()
        .isAction("BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav");
  }

  @Test
  public void functionImport_VarReturning() {
    // returning primitive
    testRes.run("FINRTInt16()")
        .isFunctionImport("FINRTInt16")
        .isFunction("UFNRTInt16")
        .isType(PropertyProvider.nameInt16, false);

    // returning collection of primitive
    testRes.run("FICRTCollStringTwoParam(ParameterString='ABC',ParameterInt16=1)")
        .isFunctionImport("FICRTCollStringTwoParam")
        .isFunction("UFCRTCollStringTwoParam")
        .isType(PropertyProvider.nameString, true);

    // returning single complex
    testRes.run("FICRTCTAllPrimTwoParam(ParameterString='ABC',ParameterInt16=1)")
        .isFunctionImport("FICRTCTAllPrimTwoParam")
        .isFunction("UFCRTCTAllPrimTwoParam")
        .isType(ComplexTypeProvider.nameCTAllPrim, false);

    // returning collection of complex
    testRes.run("FICRTCollCTTwoPrim()")
        .isFunctionImport("FICRTCollCTTwoPrim")
        .isFunction("UFCRTCollCTTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim, true);

    // returning single entity
    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)")
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false);

    // returning collection of entity (aka entitySet)
    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)")
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true);
  }

  @Test
  public void functionsWithKeyPredicates() throws Exception {
    testRes.run("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)")
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterString", "'1'")
        .isParameter(1, "ParameterInt16", "1");

    testRes.run("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt16=0)")
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterString", "'1'")
        .isParameter(1, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "0");

    testRes.run("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)(0)")
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterString", "'1'")
        .isParameter(1, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "0");

    testUri.runEx("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt16 eq 0)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);

    // PropertyInt32 does not exist
    testUri.runEx("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt32=0)")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);

    testUri.runEx("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)"
        + "(PropertyInt16=0,PropertyInt16=1)")
        .isExValidation(UriValidationException.MessageKeys.DOUBLE_KEY_PROPERTY);

    testRes.run("FICRTCollCTTwoPrimTwoParam(ParameterString='1',ParameterInt16=1)")
        .isFunctionImport("FICRTCollCTTwoPrimTwoParam")
        .isFunction("UFCRTCollCTTwoPrimTwoParam")
        .isParameter(0, "ParameterString", "'1'")
        .isParameter(1, "ParameterInt16", "1");

    testUri.runEx("FICRTCollCTTwoPrimTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt16=1)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testUri.runEx("FICRTCollCTTwoPrimTwoParam(ParameterString='1',ParameterInt16=1)(1)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testUri.runEx("FICRTCollCTTwoPrimTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt32=1)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testUri.runEx("FICRTCollCTTwoPrimTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt32=1,PropertyInt16=2)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)")
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1");

    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyString='1')")
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'");

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16 eq 1)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1)")
        .isExSemantic(MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES);

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyInt32=1,PropertyString='1')")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)()")
        .isExSemantic(MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES);

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyInt32=1)")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,Unkown=1)")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);

    testRes.run("FICRTCollCTTwoPrim()")
        .isFunctionImport("FICRTCollCTTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim);

    testRes.run("FICRTCollStringTwoParam(ParameterInt16=1,ParameterString='2')")
        .isFunctionImport("FICRTCollStringTwoParam")
        .isType(PropertyProvider.nameString)
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'");

    testRes.run("FICRTStringTwoParam(ParameterInt16=1)")
        .isFunctionImport("FICRTStringTwoParam")
        .isFunction("UFCRTStringTwoParam")
        .isType(PropertyProvider.nameString)
        .isParameter(0, "ParameterInt16", "1");

    testRes.run("FICRTStringTwoParam(ParameterInt16=1,ParameterString='2')")
        .isFunctionImport("FICRTStringTwoParam")
        .isFunction("UFCRTStringTwoParam")
        .isType(PropertyProvider.nameString)
        .isParameter(0, "ParameterInt16", "1");

    testRes.run("FICRTCollString()")
        .isFunctionImport("FICRTCollString")
        .isFunction("UFCRTCollString");

    testRes.run("FICRTString()")
        .isFunctionImport("FICRTString")
        .isFunction("UFCRTString");

    testUri.runEx("FICRTCollString()(0)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testUri.runEx("FICRTString()(0)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);
  }

  @Test
  public void nonComposableFunctions() throws Exception {
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$skip=1");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$top=1");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')",
        "$filter=PropertyInt16 eq 1");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$skip=1");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$count=true");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$skiptoken=5");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$search=test");

    testRes.run("ESAllPrim/olingo.odata.test1.BFNESAllPrimRTCTAllPrim()")
        .isEntitySet("ESAllPrim")
        .n()
        .isFunction("BFNESAllPrimRTCTAllPrim");

    testUri.runEx("ESAllPrim/olingo.odata.test1.BFNESAllPrimRTCTAllPrim()/PropertyString")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);

    testUri.runEx("FINRTByteNineParam(ParameterEnum=Namespace1_Alias.ENString'String1',"
        + "CollParameterETTwoPrim=@collComp,ParameterComp=@comp,ParameterDef='key1',"
        + "ParameterETTwoPrim=@comp,CollParameterDef=@collDef,CollParameterByte=@collByte,"
        + "CollParameterComp=@collComp,CollParameterEnum=@collEnum)/$value?@comp={\"PropertyInt16\":1}"
        + "&@collByte=[1]&@collEnum=[\"String1,String1\"]&@collDef=[\"Test\"]&@collComp=[{\"PropertyInt16\":11}]")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
  }

  @Test
  public void functionImpBf() throws Exception {
    testRes.run("FICRTString()/olingo.odata.test1.BFCStringRTESTwoKeyNav()")
        .isFunctionImport("FICRTString").isFunction("UFCRTString")
        .n().isFunction("BFCStringRTESTwoKeyNav");
  }

  @Test
  public void functionImpCastBf() throws Exception {

    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/olingo.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTETTwoKeyNav");

    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/olingo.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTETTwoKeyNav");

    testRes.run("FICRTCollCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=null)")
        .isFunctionImport("FICRTCollCTTwoPrimTwoParam")
        .isFunction("UFCRTCollCTTwoPrimTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", null);
  }

  @Test
  public void functionImpEntity() throws Exception {

    testRes.run("FICRTETKeyNav()")
        .isFunctionImport("FICRTETKeyNav")
        .isFunction("UFCRTETKeyNav")
        .isType(EntityTypeProvider.nameETKeyNav);

    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)")
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1");

    testRes.run("FICRTESMedia(ParameterInt16=1)/$value")
        .isFunctionImport("FICRTESMedia")
        .isFunction("UFCRTETMedia")
        .n()
        .isValue();

    testRes.run("FICRTETKeyNav()/$ref")
        .isFunctionImport("FICRTETKeyNav")
        .isFunction("UFCRTETKeyNav")
        .n()
        .isRef();

    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/$ref")
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .n()
        .isRef();

    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav")
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav);

    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav")
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav);

    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')")
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");
  }

  @Test
  public void functionImpEs() throws Exception {
    testRes.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETMixPrimCollComp);

    testRes.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETMixPrimCollComp);

    testRes.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')/$count")
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETMixPrimCollComp)
        .n()
        .isCount();

    testRes.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')")
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'1'");

    testRes.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')(0)")
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'1'");
  }

  @Test
  public void functionImpError() {
    testUri.runEx("FICRTCollCTTwoPrimTwoParam")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("FICRTCollCTTwoPrimTwoParam()").isExSemantic(MessageKeys.FUNCTION_NOT_FOUND);
    testUri.runEx("FICRTCollCTTwoPrimTwoParam(invalidParam=2)").isExSemantic(MessageKeys.FUNCTION_NOT_FOUND);
  }

  @Test
  public void functionImportCast() throws Exception {
    testRes.run("FICRTCollCTTwoPrimTwoParam(ParameterInt16=1,ParameterString='2')/olingo.odata.test1.CTBase")
        .isFunctionImport("FICRTCollCTTwoPrimTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isTypeFilterOnCollection(ComplexTypeProvider.nameCTBase);
  }

  @Test
  public void functionImpEsCast() throws Exception {

    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav")
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav);

    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav/$count")
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isCount();

    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')")
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");

    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBaseTwoKeyNav);
  }

  @Test
  public void functionImportChain() {
    // test chain; returning single complex
    testRes.run("FICRTCTAllPrimTwoParam(ParameterString='ABC',ParameterInt16=1)/PropertyInt16")
    .at(0)
    .isFunctionImport("FICRTCTAllPrimTwoParam")
    .isFunction("UFCRTCTAllPrimTwoParam")
    .isType(ComplexTypeProvider.nameCTAllPrim, false)
    .isParameter(0, "ParameterString", "'ABC'")
    .isParameter(1, "ParameterInt16", "1")
    .at(1)
    .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    // test chains; returning single entity
    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/PropertyInt16")
    .at(0)
    .isFunctionImport("FICRTETTwoKeyNavParam")
    .isFunction("UFCRTETTwoKeyNavParam")
    .isType(EntityTypeProvider.nameETTwoKeyNav, false)
    .isParameter(0, "ParameterInt16", "1")
    .at(1)
    .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    // test chains; returning collection of entity (aka entitySet)
    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyString='ABC')")
    .at(0)
    .isFunctionImport("FICRTCollESTwoKeyNavParam")
    .isFunction("UFCRTCollETTwoKeyNavParam")
    .isType(EntityTypeProvider.nameETTwoKeyNav, false)
    .isParameter(0, "ParameterInt16", "1")
    .isKeyPredicate(0, "PropertyInt16", "1")
    .isKeyPredicate(1, "PropertyString", "'ABC'");

    // test chains; returning collection of entity (aka entitySet)
    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyString='ABC')/PropertyInt16")
    .at(0)
    .isFunctionImport("FICRTCollESTwoKeyNavParam")
    .isFunction("UFCRTCollETTwoKeyNavParam")
    .isType(EntityTypeProvider.nameETTwoKeyNav, false)
    .isParameter(0, "ParameterInt16", "1")
    .isKeyPredicate(0, "PropertyInt16", "1")
    .isKeyPredicate(1, "PropertyString", "'ABC'")
    .at(1)
    .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
  }

  @Test
  public void actionImport_VarReturnType() {
    testRes.run(ContainerProvider.AIRT_STRING)
        .isActionImport(ContainerProvider.AIRT_STRING)
        .isAction(ActionProvider.nameUARTString.getName())
        .isType(PropertyProvider.nameString, false);

    testRes.run(ContainerProvider.AIRT_COLL_STRING_TWO_PARAM)
        .isActionImport(ContainerProvider.AIRT_COLL_STRING_TWO_PARAM)
        .isAction(ActionProvider.nameUARTCollStringTwoParam.getName())
        .isType(PropertyProvider.nameString, true);

    testRes.run(ContainerProvider.AIRTCT_TWO_PRIM_PARAM)
        .isActionImport(ContainerProvider.AIRTCT_TWO_PRIM_PARAM)
        .isAction(ActionProvider.nameUARTCTTwoPrimParam.getName())
        .isType(ComplexTypeProvider.nameCTTwoPrim, false);

    testRes.run(ContainerProvider.AIRT_COLL_CT_TWO_PRIM_PARAM)
        .isActionImport(ContainerProvider.AIRT_COLL_CT_TWO_PRIM_PARAM)
        .isAction(ActionProvider.nameUARTCollCTTwoPrimParam.getName())
        .isType(ComplexTypeProvider.nameCTTwoPrim, true);

    testRes.run(ContainerProvider.AIRTET_TWO_KEY_TWO_PRIM_PARAM)
        .isActionImport(ContainerProvider.AIRTET_TWO_KEY_TWO_PRIM_PARAM)
        .isAction(ActionProvider.nameUARTETTwoKeyTwoPrimParam.getName())
        .isType(EntityTypeProvider.nameETTwoKeyTwoPrim, false);

    testUri.runEx(ContainerProvider.AIRT_STRING + "/invalidElement")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
    testUri.runEx(ContainerProvider.AIRTCT_TWO_PRIM_PARAM + "/PropertyInt16")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
  }
}
