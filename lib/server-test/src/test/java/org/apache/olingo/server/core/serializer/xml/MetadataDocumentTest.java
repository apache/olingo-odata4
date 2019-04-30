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
package org.apache.olingo.server.core.serializer.xml;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

public class MetadataDocumentTest {

  private static final String CORE_VOCABULARY =
      "http://docs.oasis-open.org/odata/odata/v4.0/cs02/vocabularies/Org.OData.Core.V1.xml";

  @Test
  public void writeMetadataWithTechnicalScenario() throws Exception {
    final OData odata = OData.newInstance();
    final ServiceMetadata serviceMetadata = odata.createServiceMetadata(
        new EdmTechProvider(),
        Collections.singletonList(
            new EdmxReference(URI.create(CORE_VOCABULARY))
                .addInclude(new EdmxReferenceInclude("Org.OData.Core.V1", "Core"))));

    final String metadata = IOUtils.toString(
        odata.createSerializer(ContentType.APPLICATION_XML).metadataDocument(serviceMetadata).getContent());
    assertNotNull(metadata);
    assertThat(metadata, containsString("<edmx:Reference Uri=\"" + CORE_VOCABULARY + "\">"
            + "<edmx:Include Namespace=\"Org.OData.Core.V1\" Alias=\"Core\"></edmx:Include>" + "</edmx:Reference>"));

    assertThat(metadata,
        containsString("<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"));

    assertThat(metadata,
        containsString("<Schema xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" "
            + "Namespace=\"olingo.odata.test1\" Alias=\"Namespace1_Alias\">"));

    assertThat(metadata,
        containsString("<EntityType Name=\"ETTwoPrim\">"
            + "<Key><PropertyRef Name=\"PropertyInt16\"/></Key>"
            + "<Property Name=\"PropertyInt16\" Type=\"Edm.Int16\" Nullable=\"false\"></Property>"
            + "<Property Name=\"PropertyString\" Type=\"Edm.String\"></Property>"
            + "<NavigationProperty Name=\"NavPropertyETAllPrimOne\" Type=\"Namespace1_Alias.ETAllPrim\">"
            + "</NavigationProperty>"
            + "<NavigationProperty Name=\"NavPropertyETAllPrimMany\" "
            + "Type=\"Collection(Namespace1_Alias.ETAllPrim)\"></NavigationProperty>"
            + "</EntityType>"));

    assertThat(metadata,
        containsString("<EntityType Name=\"ETBase\" BaseType=\"Namespace1_Alias.ETTwoPrim\">"
            + "<Property Name=\"AdditionalPropertyString_5\" Type=\"Edm.String\"></Property>"
            + "</EntityType>"));

      System.out.println(metadata);
      assertThat(metadata, containsString("<EntitySet Name=\"ESAllPrim\" EntityType=\"Namespace1_Alias.ETAllPrim\">"
        + "<NavigationPropertyBinding Path=\"NavPropertyETTwoPrimOne\" Target=\"ESTwoPrim\"/>"
        + "<NavigationPropertyBinding Path=\"NavPropertyETTwoPrimMany\" Target=\"ESTwoPrim\"/>"
        + "<Annotation Term=\"Core.Description\""));

    assertThat(metadata,
        containsString("<ComplexType Name=\"CTPrim\">"
            + "<Property Name=\"PropertyInt16\" Type=\"Edm.Int16\"></Property></ComplexType>"));

    assertThat(metadata,
        containsString("<ComplexType Name=\"CTBase\" BaseType=\"Namespace1_Alias.CTTwoPrim\">"
            + "<Property Name=\"AdditionalPropString\" Type=\"Edm.String\"></Property></ComplexType>"));

    assertThat(metadata, containsString("<Action Name=\"UARTCTTwoPrimParam\" IsBound=\"false\">"
        + "<Parameter Name=\"ParameterInt16\" Type=\"Edm.Int16\" Nullable=\"false\"></Parameter>"
        + "<ReturnType Type=\"Namespace1_Alias.CTTwoPrim\" Nullable=\"false\"/></Action>"));
    
    assertThat(metadata,
        containsString("<Action Name=\"BAESAllPrimRTETAllPrim\" IsBound=\"true\">"
            + "<Parameter Name=\"ParameterESAllPrim\" "
            + "Type=\"Collection(Namespace1_Alias.ETAllPrim)\" Nullable=\"false\"></Parameter>"
            + "<ReturnType Type=\"Namespace1_Alias.ETAllPrim\"/></Action>"));

    assertThat(metadata,
        containsString("<Function Name=\"UFNRTInt16\">"
            + "<ReturnType Type=\"Edm.Int16\"/></Function>"));

    assertThat(metadata,
        containsString("<Function Name=\"BFC_RTESTwoKeyNav_\" "
            + "EntitySetPath=\"BindingParam/NavPropertyETTwoKeyNavMany\" IsBound=\"true\" IsComposable=\"true\">"
            + "<Parameter Name=\"BindingParam\" Type=\"Collection(Namespace1_Alias.ETTwoKeyNav)\" "
            + "Nullable=\"false\"></Parameter>"
            + "<ReturnType Type=\"Collection(Namespace1_Alias.ETTwoKeyNav)\" Nullable=\"false\"/>"
            + "</Function>"));

    assertThat(metadata, containsString("<EntityContainer Name=\"Container\">"));

    assertThat(metadata,
        containsString("<EntitySet Name=\"ESTwoPrim\" EntityType=\"Namespace1_Alias.ETTwoPrim\">"));

    assertThat(metadata,
        containsString("<Singleton Name=\"SINav\" Type=\"Namespace1_Alias.ETTwoKeyNav\">"
            + "<NavigationPropertyBinding Path=\"NavPropertyETTwoKeyNavMany\" Target=\"ESTwoKeyNav\"/>"
            + "<NavigationPropertyBinding Path=\"NavPropertyETTwoKeyNavOne\" Target=\"ESTwoKeyNav\"/>"
            + "<NavigationPropertyBinding Path=\"NavPropertyETKeyNavOne\" Target=\"ESKeyNav\"/>"
            + "</Singleton>"));

    assertThat(metadata,
        containsString("<ActionImport Name=\"AIRTCTTwoPrimParam\" Action=\"Namespace1_Alias.UARTCTTwoPrimParam\">"
        		+ "</ActionImport>"));

    assertThat(metadata,
        containsString("<FunctionImport Name=\"FINInvisible2RTInt16\" Function=\"Namespace1_Alias.UFNRTInt16\">"
        		+ "</FunctionImport"));

    assertThat(
        metadata,
        containsString("<EntitySet Name=\"ESInvisible\" EntityType=\"Namespace1_Alias.ETAllPrim\" "
            + "IncludeInServiceDocument=\"false\">"));

    assertThat(metadata, containsString("</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>"));

    // BaseTypeCheck
    assertThat(metadata, containsString("<EntityType Name=\"ETBase\" BaseType=\"Namespace1_Alias.ETTwoPrim\">"));

    // TypeDefCheck
    assertThat(metadata,
        containsString("<Property Name=\"PropertyDefString\" Type=\"Namespace1_Alias.TDString\"></Property>"));
    assertThat(metadata,
        containsString("<Property Name=\"CollPropertyDefString\" Type=\"Collection(Namespace1_Alias.TDString)\">"
        		+ "</Property>"));
  }
}
