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

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.server.api.edmx.EdmxReferenceIncludeAnnotation;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetadataDocumentJsonSerializerTest {

    private static ODataSerializer serializer;

    @BeforeClass
    public static void init() throws SerializerException {
        serializer = OData.newInstance().createSerializer(ODataFormat.JSON);
    }

    @Test
    public void writeMetadataWithEmptyMockedEdm() throws Exception {
        final Edm edm = mock(Edm.class);
        ServiceMetadata metadata = mock(ServiceMetadata.class);
        when(metadata.getEdm()).thenReturn(edm);
        String resultString = IOUtils.toString(serializer.metadataDocument(metadata).getContent());
        String expectedString = "{\n" +
                "  \"$schema\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#\"\n" +
                "}";

        assertEquals(expectedString, resultString);
    }

    @Test
    public void writeMetadataWithEmptySchema() throws Exception {
        EdmSchema schema = mock(EdmSchema.class);
        when(schema.getNamespace()).thenReturn("MyNamespace");
        Edm edm = mock(Edm.class);
        when(edm.getSchemas()).thenReturn(Arrays.asList(schema));
        ServiceMetadata serviceMetadata = mock(ServiceMetadata.class);
        when(serviceMetadata.getEdm()).thenReturn(edm);
        InputStream metadata = serializer.metadataDocument(serviceMetadata).getContent();
        String resultString = IOUtils.toString(metadata);
        String expectedString = "{\n" +
                "  \"$schema\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#\",\n" +
                "  \"definitions\" : { },\n" +
                "  \"schemas\" : {\n" +
                "    \"MyNamespace\" : {\n" +
                "      \"alias\" : null\n" +
                "    }\n" +
                "  }\n" +
                "}";
        assertNotNull(metadata);
        assertEquals(expectedString, resultString);
    }

    @Test
    public void writeEdmxWithLocalTestEdm() throws Exception {
        List<EdmxReference> edmxReferences = new ArrayList<EdmxReference>();
        EdmxReference reference = new EdmxReference(URI.create("http://example.com"));
        edmxReferences.add(reference);

        EdmxReference referenceWithInclude = new EdmxReference(
                URI.create("http://localhost/odata/odata/v4.0/referenceWithInclude"));
        EdmxReferenceInclude include = new EdmxReferenceInclude("Org.OData.Core.V1", "Core");
        referenceWithInclude.addInclude(include);
        edmxReferences.add(referenceWithInclude);

        EdmxReference referenceWithTwoIncludes = new EdmxReference(
                URI.create("http://localhost/odata/odata/v4.0/referenceWithTwoIncludes"));
        referenceWithTwoIncludes.addInclude(new EdmxReferenceInclude("Org.OData.Core.2", "Core2"));
        referenceWithTwoIncludes.addInclude(new EdmxReferenceInclude("Org.OData.Core.3", "Core3"));
        edmxReferences.add(referenceWithTwoIncludes);

        EdmxReference referenceWithIncludeAnnos = new EdmxReference(
                URI.create("http://localhost/odata/odata/v4.0/referenceWithIncludeAnnos"));
        referenceWithIncludeAnnos.addIncludeAnnotation(
                new EdmxReferenceIncludeAnnotation("TermNs.2", "Q.2", "TargetNS.2"));
        referenceWithIncludeAnnos.addIncludeAnnotation(
                new EdmxReferenceIncludeAnnotation("TermNs.3", "Q.3", "TargetNS.3"));
        edmxReferences.add(referenceWithIncludeAnnos);

        EdmxReference referenceWithAll = new EdmxReference(
                URI.create("http://localhost/odata/odata/v4.0/referenceWithAll"));
        referenceWithAll.addInclude(new EdmxReferenceInclude("ReferenceWithAll.1", "Core1"));
        referenceWithAll.addInclude(new EdmxReferenceInclude("ReferenceWithAll.2", "Core2"));
        referenceWithAll.addIncludeAnnotation(
                new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermNs.4", "Q.4", "TargetNS.4"));
        referenceWithAll.addIncludeAnnotation(
                new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermNs.5", "Q.5", "TargetNS.5"));
        edmxReferences.add(referenceWithAll);

        EdmxReference referenceWithAllAndNull = new EdmxReference(
                URI.create("http://localhost/odata/odata/v4.0/referenceWithAllAndNull"));
        referenceWithAllAndNull.addInclude(new EdmxReferenceInclude("referenceWithAllAndNull.1"));
        referenceWithAllAndNull.addInclude(new EdmxReferenceInclude("referenceWithAllAndNull.2", null));
        referenceWithAllAndNull.addIncludeAnnotation(
                new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermNs.4"));
        referenceWithAllAndNull.addIncludeAnnotation(
                new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermAndNullNs.5", "Q.5", null));
        referenceWithAllAndNull.addIncludeAnnotation(
                new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermAndNullNs.6", null, "TargetNS"));
        referenceWithAllAndNull.addIncludeAnnotation(
                new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermAndNullNs.7", null, null));
        edmxReferences.add(referenceWithAllAndNull);

        ServiceMetadata serviceMetadata = mock(ServiceMetadata.class);
        final Edm edm = mock(Edm.class);
        when(serviceMetadata.getEdm()).thenReturn(edm);
        when(serviceMetadata.getReferences()).thenReturn(edmxReferences);

        InputStream metadata = serializer.metadataDocument(serviceMetadata).getContent();
        assertNotNull(metadata);
        final String resultString = IOUtils.toString(metadata);
        String expectedString = "{\n" +
                "  \"$schema\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#\",\n" +
                "  \"references\" : {\n" +
                "    \"http://example.com\" : { },\n" +
                "    \"http://localhost/odata/odata/v4.0/referenceWithInclude\" : {\n" +
                "      \"includes\" : {\n" +
                "        \"Org.OData.Core.V1\" : {\n" +
                "          \"alias\" : \"Core\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"http://localhost/odata/odata/v4.0/referenceWithTwoIncludes\" : {\n" +
                "      \"includes\" : {\n" +
                "        \"Org.OData.Core.2\" : {\n" +
                "          \"alias\" : \"Core2\"\n" +
                "        },\n" +
                "        \"Org.OData.Core.3\" : {\n" +
                "          \"alias\" : \"Core3\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"http://localhost/odata/odata/v4.0/referenceWithIncludeAnnos\" : {\n" +
                "      \"includeAnnotations\" : [ {\n" +
                "        \"termNamespace\" : \"TermNs.2\",\n" +
                "        \"qualifier\" : \"Q.2\",\n" +
                "        \"targetNamespace\" : \"TargetNS.2\"\n" +
                "      }, {\n" +
                "        \"termNamespace\" : \"TermNs.3\",\n" +
                "        \"qualifier\" : \"Q.3\",\n" +
                "        \"targetNamespace\" : \"TargetNS.3\"\n" +
                "      } ]\n" +
                "    },\n" +
                "    \"http://localhost/odata/odata/v4.0/referenceWithAll\" : {\n" +
                "      \"includes\" : {\n" +
                "        \"ReferenceWithAll.1\" : {\n" +
                "          \"alias\" : \"Core1\"\n" +
                "        },\n" +
                "        \"ReferenceWithAll.2\" : {\n" +
                "          \"alias\" : \"Core2\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"includeAnnotations\" : [ {\n" +
                "        \"termNamespace\" : \"ReferenceWithAllTermNs.4\",\n" +
                "        \"qualifier\" : \"Q.4\",\n" +
                "        \"targetNamespace\" : \"TargetNS.4\"\n" +
                "      }, {\n" +
                "        \"termNamespace\" : \"ReferenceWithAllTermNs.5\",\n" +
                "        \"qualifier\" : \"Q.5\",\n" +
                "        \"targetNamespace\" : \"TargetNS.5\"\n" +
                "      } ]\n" +
                "    },\n" +
                "    \"http://localhost/odata/odata/v4.0/referenceWithAllAndNull\" : {\n" +
                "      \"includes\" : {\n" +
                "        \"referenceWithAllAndNull.1\" : { },\n" +
                "        \"referenceWithAllAndNull.2\" : { }\n" +
                "      },\n" +
                "      \"includeAnnotations\" : [ {\n" +
                "        \"termNamespace\" : \"ReferenceWithAllTermNs.4\"\n" +
                "      }, {\n" +
                "        \"termNamespace\" : \"ReferenceWithAllTermAndNullNs.5\",\n" +
                "        \"qualifier\" : \"Q.5\"\n" +
                "      }, {\n" +
                "        \"termNamespace\" : \"ReferenceWithAllTermAndNullNs.6\",\n" +
                "        \"targetNamespace\" : \"TargetNS\"\n" +
                "      }, {\n" +
                "        \"termNamespace\" : \"ReferenceWithAllTermAndNullNs.7\"\n" +
                "      } ]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertEquals(expectedString, resultString);
    }
}
