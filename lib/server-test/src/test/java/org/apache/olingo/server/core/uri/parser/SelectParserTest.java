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
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException.MessageKeys;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.apache.olingo.server.tecsvc.provider.ActionProvider;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EnumTypeProvider;
import org.apache.olingo.server.tecsvc.provider.FunctionProvider;
import org.apache.olingo.server.tecsvc.provider.PropertyProvider;
import org.apache.olingo.server.tecsvc.provider.TypeDefinitionProvider;
import org.junit.Test;

/** Tests of the parts of the URI parser that parse the sytem query option $select. */
public class SelectParserTest {

  private static final Edm edm = OData.newInstance().createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  private final TestUriValidator testUri = new TestUriValidator().setEdm(edm);

  @Test
  public void select() throws Exception {
    testUri.run("ESTwoKeyNav", "$select=*")
        .isSelectItemStar(0);

    testUri.run("ESTwoKeyNav", "$select=olingo.odata.test1.*")
        .isSelectItemAllOp(0, new FullQualifiedName("olingo.odata.test1", "*"));
    testUri.run("ESTwoKeyNav", "$select=Namespace1_Alias.*")
        .isSelectItemAllOp(0, new FullQualifiedName("Namespace1_Alias", "*"));

    testUri.run("ESTwoKeyNav", "$select=PropertyString")
        .goSelectItemPath(0).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testUri.run("ESTwoKeyNav", "$select=PropertyComp")
        .goSelectItemPath(0).isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false);

    testUri.run("ESAllPrim", "$select=PropertyTimeOfDay,PropertyDate,NavPropertyETTwoPrimOne")
        .isKind(UriInfoKind.resource)
        .goSelectItemPath(0).first().isPrimitiveProperty("PropertyTimeOfDay", PropertyProvider.nameTimeOfDay, false)
        .goUpUriValidator()
        .goSelectItemPath(1).first().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false)
        .goUpUriValidator()
        .goSelectItemPath(2).first().isNavProperty("NavPropertyETTwoPrimOne", EntityTypeProvider.nameETTwoPrim, false);

    testUri.run("ESMixEnumDefCollComp",
        "$select=PropertyEnumString,PropertyDefString,CollPropertyEnumString,CollPropertyDefString")
        .isKind(UriInfoKind.resource)
        .goSelectItemPath(0).isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpUriValidator()
        .goSelectItemPath(1).isPrimitiveProperty("PropertyDefString", TypeDefinitionProvider.nameTDString, false)
        .goUpUriValidator()
        .goSelectItemPath(2).isPrimitiveProperty("CollPropertyEnumString", EnumTypeProvider.nameENString, true)
        .goUpUriValidator()
        .goSelectItemPath(3).isPrimitiveProperty("CollPropertyDefString", TypeDefinitionProvider.nameTDString, true);

    testUri.run("ESTwoKeyNav", "$select=PropertyComp/PropertyInt16")
        .goSelectItemPath(0)
        .first()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESTwoKeyNav", "$select=PropertyComp/PropertyComp")
        .goSelectItemPath(0)
        .first()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false);

    testUri.run("ESTwoKeyNav", "$select=olingo.odata.test1.ETBaseTwoKeyNav")
        .isSelectStartType(0, EntityTypeProvider.nameETBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')",
        "$select=olingo.odata.test1.ETBaseTwoKeyNav/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goUpUriValidator()
        .isSelectStartType(0, EntityTypeProvider.nameETBaseTwoKeyNav)
        .goSelectItemPath(0)
        .first()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='1')/PropertyCompNav",
        "$select=olingo.odata.test1.CTTwoBasePrimCompNav")
        .isSelectStartType(0, ComplexTypeProvider.nameCTTwoBasePrimCompNav);

    testUri.run("ESTwoKeyNav", "$select=PropertyCompNav/olingo.odata.test1.CTTwoBasePrimCompNav")
        .goSelectItemPath(0)
        .first()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTBasePrimCompNav, false)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBasePrimCompNav);

    testUri.run("ESTwoKeyNav", "$select=PropertyCompNav/Namespace1_Alias.CTTwoBasePrimCompNav/PropertyInt16")
        .goSelectItemPath(0)
        .first()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTBasePrimCompNav, false)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBasePrimCompNav)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESAllPrim", "$select=olingo.odata.test1.BAESAllPrimRTETAllPrim")
        .goSelectItemPath(0)
        .first()
        .isAction(ActionProvider.nameBAESAllPrimRTETAllPrim.getName());
    testUri.run("ESTwoKeyNav", "$select=Namespace1_Alias.BFCESTwoKeyNavRTString")
        .goSelectItemPath(0)
        .first()
        .isFunction(FunctionProvider.nameBFCESTwoKeyNavRTString.getName());
    testUri.run("ESTwoKeyNav", "$select=olingo.odata.test1.BFCESTwoKeyNavRTStringParam(ParameterComp)")
        .goSelectItemPath(0)
        .first()
        .isFunction(FunctionProvider.nameBFCESTwoKeyNavRTStringParam.getName());

    testUri.runEx("ESMixPrimCollComp", "$select=wrong")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESMixPrimCollComp", "$select=PropertyComp/wrong")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESMixPrimCollComp", "$select=PropertyComp///PropertyInt16")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESMixPrimCollComp", "$select=/PropertyInt16")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESMixPrimCollComp", "$select=PropertyInt16+")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testUri.runEx("ESTwoKeyNav", "$select=olingo.odata.test1.1")
        .isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESTwoKeyNav", "$select=unknown_namespace.*").isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESTwoKeyNav", "$select=olingo.odata.test1.ETKeyNav")
        .isExSemantic(MessageKeys.INCOMPATIBLE_TYPE_FILTER);
    testUri.runEx("ESTwoKeyNav", "$select=PropertyCompNav/olingo.odata.test1.CTTwoPrim")
        .isExSemantic(MessageKeys.INCOMPATIBLE_TYPE_FILTER);
    testUri.runEx("ESTwoKeyNav", "$select=PropertyCompNav/olingo.odata.test1.CTwrong")
        .isExSemantic(MessageKeys.UNKNOWN_TYPE);
    testUri.runEx("ESTwoKeyNav", "$select=PropertyCompNav/.")
        .isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESTwoKeyNav", "$select=PropertyCompNav/olingo.odata.test1.CTTwoBasePrimCompNav/.")
        .isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("AIRT", "$select=wrong")
        .isExSemantic(MessageKeys.ONLY_FOR_TYPED_PARTS);
    testUri.runEx("AIRT", "$select=olingo.odata.test1.BAESAllPrimRT")
        .isExSemantic(MessageKeys.ONLY_FOR_TYPED_PARTS);
    testUri.runEx("ESTwoKeyNav", "$select=olingo.odata.test1.BFwrong")
        .isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESTwoKeyNav", "$select=olingo.odata.test1.BFCESTwoKeyNavRTStringParam()")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESTwoKeyNav", "$select=Namespace1_Alias.BFCESTwoKeyNavRTStringParam(ParameterComp,...)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }
}
