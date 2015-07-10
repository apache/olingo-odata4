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

package org.apache.olingo.server.core.serializer.json;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

public class MetadataDocumentJsonTest {


    private static final String CORE_VOCABULARY =
            "http://docs.oasis-open.org/odata/odata/v4.0/cs02/vocabularies/Org.OData.Core.V1.xml";

    @Test
    public void writeMetadataWithTechnicalScenario() throws Exception {
        final OData odata = OData.newInstance();
        final List<EdmxReference> references = getEdmxReferences();
        final ServiceMetadata serviceMetadata = odata.createServiceMetadata(
                new EdmTechProvider(references), references);

        final String metadata = IOUtils.toString(
                odata.createSerializer(ODataFormat.JSON).metadataDocument(serviceMetadata).getContent());
        assertNotNull(metadata);

        String expectedString;

        expectedString="\"$schema\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#\"";
        assertThat(metadata, containsString(expectedString));


        expectedString="\"olingo.odata.test1.ENString\":{\"enum\":[\"String1\",\"String2\",\"String3\"]," +
                "\"String1@odata.value\":\"1\",\"String2@odata.value\":\"2\",\"String3@odata.value\":\"4\"}";
        assertThat(metadata, containsString(expectedString));


        expectedString="\"olingo.odata.test1.TDString\":{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/" +
                "v4.0/edm.json#/definitions/Edm.String\",\"maxLength\":15}";
        assertThat(metadata, containsString(expectedString));


        expectedString="\"PropertyDecimal\":{\"anyOf\":[{\"type\":\"number\",\"multipleOf\":1.0E-10},{\"type\":null}]}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"CollPropertyDouble\":{\"type\":\"array\",\"items\":{\"anyOf\":[{\"$ref\":" +
                "\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/definitions/Edm.Double\"}," +
                "{\"type\":null}]}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"CollPropertyDecimal\":{\"type\":\"array\",\"items\":{\"anyOf\":" +
                "[{\"type\":\"number\"},{\"type\":null}]}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"CollPropertyBinary\":{\"type\":\"array\",\"items\":{\"anyOf\":[{\"$ref\":" +
                "\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/definitions/Edm.Binary\"}," +
                "{\"type\":null}]}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"olingo.odata.test1.ETAllPrim\":{\"type\":\"object\",\"keys\":[{\"name\":\"PropertyInt16\"}]" +
                ",\"properties\":{\"PropertyInt16\":{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/" +
                "v4.0/edm.json#/definitions/Edm.Int16\"},\"PropertyString\":{\"type\":[\"string\",null]}," +
                "\"PropertyBoolean\":{\"type\":[\"boolean\",null]},\"PropertyByte\":{\"anyOf\":" +
                "[{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/definitions/Edm.Byte\"}" +
                ",{\"type\":null}]},\"PropertySByte\":{\"anyOf\":[{\"$ref\":\"http://docs.oasis-open.org/odata/" +
                "odata-json-csdl/v4.0/edm.json#/definitions/Edm.SByte\"},{\"type\":null}]}," +
                "\"PropertyInt32\":{\"anyOf\":[{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/" +
                "v4.0/edm.json#/definitions/Edm.Int32\"},{\"type\":null}]},\"PropertyInt64\":" +
                "{\"anyOf\":[{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/" +
                "edm.json#/definitions/Edm.Int64\"},{\"type\":null}]},\"PropertySingle\":" +
                "{\"anyOf\":[{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/Edm.Single\"},{\"type\":null}]},\"PropertyDouble\":" +
                "{\"anyOf\":[{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/Edm.Double\"},{\"type\":null}]},\"PropertyDecimal\":{\"anyOf\":" +
                "[{\"type\":\"number\",\"multipleOf\":1.0E-10},{\"type\":null}]},\"PropertyBinary\":" +
                "{\"anyOf\":[{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/Edm.Binary\"},{\"type\":null}]},\"PropertyDate\":{\"anyOf\":" +
                "[{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/definitions/" +
                "Edm.Date\"},{\"type\":null}]},\"PropertyDateTimeOffset\":{\"anyOf\":" +
                "[{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/definitions/" +
                "Edm.DateTimeOffset\"},{\"pattern\":\"^[^.]*$\"}]},\"PropertyDuration\":{\"anyOf\":[{\"$ref\":" +
                "\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/definitions/Edm.Duration\"}," +
                "{\"pattern\":\"^[^.]*$\"}]},\"PropertyGuid\":{\"anyOf\":[{\"$ref\":\"http://docs.oasis-open.org/" +
                "odata/odata-json-csdl/v4.0/edm.json#/definitions/Edm.Guid\"},{\"type\":null}]}," +
                "\"PropertyTimeOfDay\":{\"anyOf\":[{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/" +
                "v4.0/edm.json#/definitions/Edm.TimeOfDay\"},{\"pattern\":\"^[^.]*$\"}]},\"NavPropertyETTwoPrimOne\"" +
                ":{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/definitions/" +
                "olingo.odata.test1.ETTwoPrim\",\"relationship\":{\"referentialConstraints\":{}}}," +
                "\"NavPropertyETTwoPrimMany\":{\"type\":\"array\",\"items\":{\"anyOf\":[{\"$ref\":" +
                "\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/definitions/" +
                "olingo.odata.test1.ETTwoPrim\"},{\"type\":null}]},\"relationship\":{\"referentialConstraints\":{}}}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"functionImports\":{\"FINRTInt16\":{\"function\":\"Namespace1_Alias.UFNRTInt16\"," +
                "\"includeInServiceDocument\":true},\"FINInvisibleRTInt16\":{\"function\":" +
                "\"Namespace1_Alias.UFNRTInt16\"},\"FINInvisible2RTInt16\":{\"function\":" +
                "\"Namespace1_Alias.UFNRTInt16\"},\"FICRTETKeyNav\":{\"function\":" +
                "\"Namespace1_Alias.UFCRTETKeyNav\"},\"FICRTESTwoKeyNav\":{\"function\":" +
                "\"Namespace1_Alias.UFCRTETTwoKeyNav\",\"entitySet\":\"Namespace1_Alias.ESTwoKeyNav\"," +
                "\"includeInServiceDocument\":true},\"FICRTETTwoKeyNavParam\":{\"function\":" +
                "\"Namespace1_Alias.UFCRTETTwoKeyNavParam\",\"includeInServiceDocument\":true}," +
                "\"FICRTStringTwoParam\":{\"function\":\"Namespace1_Alias.UFCRTStringTwoParam\"," +
                "\"includeInServiceDocument\":true},\"FICRTCollStringTwoParam\":{\"function\":" +
                "\"Namespace1_Alias.UFCRTCollStringTwoParam\",\"includeInServiceDocument\":true}," +
                "\"FICRTCTAllPrimTwoParam\":{\"function\":\"Namespace1_Alias.UFCRTCTAllPrimTwoParam\"," +
                "\"includeInServiceDocument\":true},\"FICRTESMixPrimCollCompTwoParam\":{\"function\":" +
                "\"Namespace1_Alias.UFCRTESMixPrimCollCompTwoParam\",\"includeInServiceDocument\":true}," +
                "\"FICRTCollETMixPrimCollCompTwoParam\":{\"function\":" +
                "\"Namespace1_Alias.UFCRTCollETMixPrimCollCompTwoParam\",\"includeInServiceDocument\":true}," +
                "\"FINRTCollETMixPrimCollCompTwoParam\":{\"function\":" +
                "\"Namespace1_Alias.UFNRTCollETMixPrimCollCompTwoParam\",\"includeInServiceDocument\":true}," +
                "\"FICRTCollCTTwoPrim\":{\"function\":\"Namespace1_Alias.UFCRTCollCTTwoPrim\"," +
                "\"includeInServiceDocument\":true},\"FICRTESMedia\":{\"function\":\"Namespace1_Alias.UFCRTETMedia\"" +
                ",\"entitySet\":\"Namespace1_Alias.ESMedia\",\"includeInServiceDocument\":true}," +
                "\"FICRTCollESMedia\":{\"function\":\"Namespace1_Alias.UFCRTCollETMedia\"," +
                "\"entitySet\":\"Namespace1_Alias.ESMedia\",\"includeInServiceDocument\":true}," +
                "\"FICRTCTTwoPrimParam\":{\"function\":\"Namespace1_Alias.UFCRTCTTwoPrimParam\"," +
                "\"includeInServiceDocument\":true},\"FICRTCTTwoPrim\":{\"function\":" +
                "\"Namespace1_Alias.UFCRTCTTwoPrim\",\"includeInServiceDocument\":true}," +
                "\"FICRTCollString\":{\"function\":\"Namespace1_Alias.UFCRTCollString\"," +
                "\"includeInServiceDocument\":true},\"FICRTString\":{\"function\":\"Namespace1_Alias.UFCRTString\"," +
                "\"includeInServiceDocument\":true},\"FICRTCollESTwoKeyNavParam\":{\"function\":" +
                "\"Namespace1_Alias.UFCRTCollETTwoKeyNavParam\",\"entitySet\":\"Namespace1_Alias.ESTwoKeyNav\"," +
                "\"includeInServiceDocument\":true},\"FICRTCollCTTwoPrimParam\":{\"function\":" +
                "\"Namespace1_Alias.UFCRTCollCTTwoPrimParam\",\"includeInServiceDocument\":true}," +
                "\"FINRTCollCTNavFiveProp\":{\"function\":\"Namespace1_Alias.UFNRTCollCTNavFiveProp\"," +
                "\"includeInServiceDocument\":true},\"FICRTCollESKeyNavContParam\":{\"function\":" +
                "\"Namespace1_Alias.UFCRTCollETKeyNavContParam\",\"entitySet\":" +
                "\"Namespace1_Alias.ESKeyNavCont\",\"includeInServiceDocument\":true}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"actionImports\":{\"AIRTString\":{\"action\":\"Namespace1_Alias.UARTString\"}," +
                "\"AIRTCollStringTwoParam\":{\"action\":\"Namespace1_Alias.UARTCollStringTwoParam\"}," +
                "\"AIRTCTTwoPrimParam\":{\"action\":\"Namespace1_Alias.UARTCTTwoPrimParam\"}," +
                "\"AIRTCollCTTwoPrimParam\":{\"action\":\"Namespace1_Alias.UARTCollCTTwoPrimParam\"}," +
                "\"AIRTETTwoKeyTwoPrimParam\":{\"action\":\"Namespace1_Alias.UARTETTwoKeyTwoPrimParam\"}," +
                "\"AIRTCollETKeyNavParam\":{\"action\":\"Namespace1_Alias.UARTCollETKeyNavParam\"}," +
                "\"AIRTESAllPrimParam\":{\"action\":\"Namespace1_Alias.UARTETAllPrimParam\"}," +
                "\"AIRTCollESAllPrimParam\":{\"action\":\"Namespace1_Alias.UARTCollETAllPrimParam\"}," +
                "\"AIRT\":{\"action\":\"Namespace1_Alias.UART\"},\"AIRTParam\":{\"action\":" +
                "\"Namespace1_Alias.UARTParam\"},\"AIRTTwoParam\":{\"action\":\"Namespace1_Alias.UARTTwoParam\"}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"singletons\":{\"SI\":{\"type\":\"Namespace1_Alias.ETTwoPrim\"},\"SINav\":{\"type\":" +
                "\"Namespace1_Alias.ETTwoKeyNav\",\"navigationPropertyBindings\":{\"NavPropertyETTwoKeyNavMany\":" +
                "{\"target\":\"ESTwoKeyNav\"},\"NavPropertyETTwoKeyNavOne\":{\"target\":\"ESTwoKeyNav\"}," +
                "\"NavPropertyETKeyNavOne\":{\"target\":\"ESKeyNav\"}}},\"SIMedia\":{\"type\":" +
                "\"Namespace1_Alias.ETMedia\"}}}}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="{\"name\":\"UFCRTETKeyNav\",\"isComposable\":true,\"parameters\":{},\"returnType\":{\"type\"" +
                ":\"Namespace1_Alias.ETKeyNav\",\"nullable\":false}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="{\"name\":\"BAETTwoKeyNavRTETTwoKeyNav\",\"isBound\":true,\"parameters\":" +
                "{\"ParameterETTwoKeyNav\":{\"type\":\"Namespace1_Alias.ETTwoKeyNav\",\"nullable\":false}}," +
                "\"returnType\":{\"type\":\"Namespace1_Alias.ETTwoKeyNav\"}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"ESAllPrim\":{\"entityType\":\"Namespace1_Alias.ETAllPrim\",\"navigationPropertyBindings\"" +
                ":{\"NavPropertyETTwoPrimOne\":{\"target\":\"ESTwoPrim\"},\"NavPropertyETTwoPrimMany\"" +
                ":{\"target\":\"ESTwoPrim\"}}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"references\":{\"http://docs.oasis-open.org/odata/odata/v4.0/cs02/vocabularies/" +
                "Org.OData.Core.V1.xml\":{\"includes\":{\"Org.OData.Core.V1\":{\"alias\":\"Core\"}}}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"olingo.odata.test1.ETMedia\":{\"type\":\"object\",\"hasStream\":true,\"keys\":" +
                "[{\"name\":\"PropertyInt16\"}],\"properties\":{\"PropertyInt16\":{\"$ref\":\"" +
                "http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/definitions/Edm.Int16\"}}}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"PropertyDecimal\":{\"anyOf\":[{\"type\":\"number\",\"multipleOf\":1.0E-5," +
                "\"minimum\":-999999.99999,\"maximum\":999999.99999},{\"type\":null}]}";
        assertThat(metadata, containsString(expectedString));

        expectedString=",\"PropertyTimeOfDay\":{\"anyOf\":[{\"$ref\":\"http://docs.oasis-open.org/odata/" +
                "odata-json-csdl/v4.0/edm.json#/definitions/Edm.TimeOfDay\"},{\"pattern\":\"^[^.]*$\"}]}";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"schemas\"";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"entityContainer\":{\"name\":\"Container\"";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"definitions\"";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"actions\":[{";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"functions\":[{";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"actionImports\":{";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"functionImports\":{";
        assertThat(metadata, containsString(expectedString));

        expectedString="\"entitySets\":{";
        assertThat(metadata, containsString(expectedString));
    }


    private List<EdmxReference> getEdmxReferences() {
        EdmxReference reference = new EdmxReference(URI.create(CORE_VOCABULARY));
        reference.addInclude(new EdmxReferenceInclude("Org.OData.Core.V1", "Core"));
        return Arrays.asList(reference);
    }
}
