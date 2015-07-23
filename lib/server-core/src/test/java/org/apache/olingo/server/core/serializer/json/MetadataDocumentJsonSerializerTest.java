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
import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.server.api.edmx.EdmxReferenceIncludeAnnotation;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.ServiceMetadataImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        String expectedString = "{\"$schema\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#\",\"" +
                "odata-version\":\"4.0\"}";
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
        String expectedString = "{\"$schema\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#\",\"" +
                "odata-version\":\"4.0\",\"definitions\":{},\"schemas\":{\"MyNamespace\":{\"alias\":null}}}";
        assertNotNull(metadata);
        assertEquals(expectedString, resultString);
    }

    @Test
    public void referencesEdmTest() throws Exception {
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
        assertTrue(resultString.contains("\"http://example.com\":{}"));
        assertTrue(resultString.contains("\"http://localhost/odata/odata/v4.0/" +
                "referenceWithInclude\":{\"includes\":{\"Org.OData.Core.V1\":" +
                "{\"alias\":\"Core\"}}}"));
        assertTrue(resultString.contains("\"http://localhost/odata/odata/v4.0/" +
                "referenceWithTwoIncludes\":{\"includes\":{\"Org.OData.Core.2\":" +
                "{\"alias\":\"Core2\"},\"Org.OData.Core.3\":{\"alias\":\"Core3\"}}}"));
        assertTrue(resultString.contains("\"http://localhost/odata/odata/v4.0/" +
                "referenceWithIncludeAnnos\":{\"includeAnnotations\":[{\"termNamespace\":" +
                "\"TermNs.2\",\"qualifier\":\"Q.2\",\"targetNamespace\":\"TargetNS.2\"}," +
                "{\"termNamespace\":\"TermNs.3\",\"qualifier\":\"Q.3\",\"" +
                "targetNamespace\":\"TargetNS.3\"}]}"));
        assertTrue(resultString.contains("\"http://localhost/odata/odata/v4.0/referenceWithAll\":" +
                "{\"includes\":{\"ReferenceWithAll.1\":{\"alias\":\"Core1\"}," +
                "\"ReferenceWithAll.2\":{\"alias\":\"Core2\"}},\"" +
                "includeAnnotations\":[{\"termNamespace\":\"ReferenceWithAllTermNs.4\"," +
                "\"qualifier\":\"Q.4\",\"targetNamespace\":\"TargetNS.4\"}," +
                "{\"termNamespace\":\"ReferenceWithAllTermNs.5\"," +
                "\"qualifier\":\"Q.5\",\"targetNamespace\":\"TargetNS.5\"}]}"));
        assertTrue(resultString.contains("\"http://localhost/odata/odata/v4.0/" +
                "referenceWithAllAndNull\":{\"includes\":{\"referenceWithAllAndNull.1\":{}," +
                "\"referenceWithAllAndNull.2\":{}},\"includeAnnotations\":" +
                "[{\"termNamespace\":\"ReferenceWithAllTermNs.4\"},{\"termNamespace\":" +
                "\"ReferenceWithAllTermAndNullNs.5\",\"qualifier\":\"Q.5\"}," +
                "{\"termNamespace\":\"ReferenceWithAllTermAndNullNs.6\"," +
                "\"targetNamespace\":\"TargetNS\"},{\"termNamespace\":" +
                "\"ReferenceWithAllTermAndNullNs.7\"}]}}"));
        assertTrue(resultString.contains("\"references\""));
    }

    @Test
    public void metadataDocumentJsonTest() throws Exception {
        CsdlEdmProvider provider = new LocalProvider();
        ServiceMetadata serviceMetadata = new ServiceMetadataImpl(provider, Collections.<EdmxReference>emptyList());
        InputStream metadataStream = serializer.metadataDocument(serviceMetadata).getContent();
        String metadata = IOUtils.toString(metadataStream);
        assertNotNull(metadata);
        assertTrue(metadata.contains("\"namespace.ENString\":{\"enum\":[\"String1\"]," +
                "\"String1@odata.value\":\"1\"}"));
        assertTrue(metadata.contains("\"namespace.ETAbstract\":{\"type\":\"object\"," +
                "\"abstract\":true,\"properties\":{\"PropertyString\":" +
                "{\"type\":[\"string\",null]}}}"));
        assertTrue(metadata.contains("\"namespace.ETAbstractBase\":{\"type\":\"object\"," +
                "\"allOf\":[{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/" +
                "edm.json#/definitions/namespace.ETAbstract\"}]," +
                "\"keys\":[{\"name\":\"PropertyInt16\"}],\"properties\":" +
                "{\"PropertyInt16\":{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/" +
                "v4.0/edm.json#/definitions/Edm.Int16\"}}}"));
        assertTrue(metadata.contains("\"namespace.CTTwoPrim\":{\"type\":\"object\"," +
                "\"properties\":{\"PropertyInt16\":{\"$ref\":\"http://docs.oasis-open.org/odata/" +
                "odata-json-csdl/v4.0/edm.json#/definitions/Edm.Int16\"}," +
                "\"PropertyString\":{\"type\":[\"string\",null]}}}"));
        assertTrue(metadata.contains("\"namespace.CTTwoPrimBase\":{\"type\":\"object\"," +
                "\"allOf\":[{\"$ref\":\"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/" +
                "edm.json#/definitions/namespace.CTTwoPrim\"}],\"properties\":{}}"));
        assertTrue(metadata.contains("\"actions\":[{\"name\":\"UARTPrimParam\",\"isBound\"" +
                ":false,\"parameters\":{\"ParameterInt16\":{\"type\":\"Edm.Int16\"}}," +
                "\"returnType\":{\"type\":\"Edm.String\"}}]"));
        assertTrue(metadata.contains("\"functions\":[{\"name\":\"UFNRTInt16\",\"parameters\":{}," +
                "\"returnType\":{\"type\":\"Edm.Int16\"}}]"));
        assertTrue(metadata.contains("\"entitySets\":{\"ESAllPrim\":{\"entityType\":" +
                "\"Alias.ETAbstractBase\"}}"));
        assertTrue(metadata.contains("\"actionImports\":{\"AIRTPrimParam\":" +
                "{\"action\":\"Alias.UARTPrimParam\"}}"));
        assertTrue(metadata.contains("\"functionImports\":{\"FINRTInt16\":{\"function\":" +
                "\"Alias.UFNRTInt16\",\"includeInServiceDocument\":true}}"));
        assertTrue(metadata.contains("\"singletons\":{\"SI\":{\"type\":" +
                "\"Alias.ETAbstractBase\"}}"));
    }

    private class LocalProvider extends CsdlAbstractEdmProvider {

        private final static String nameSpace = "namespace";
        private final FullQualifiedName nameETAbstract = new FullQualifiedName(nameSpace, "ETAbstract");
        private final FullQualifiedName nameETAbstractBase = new FullQualifiedName(nameSpace, "ETAbstractBase");
        private final FullQualifiedName nameInt16 = EdmPrimitiveTypeKind.Int16.getFullQualifiedName();
        private final FullQualifiedName nameString = EdmPrimitiveTypeKind.String.getFullQualifiedName();
        private final FullQualifiedName nameUARTPrimParam = new FullQualifiedName(nameSpace, "UARTPrimParam");
        private final CsdlProperty propertyInt16_NotNullable = new CsdlProperty()
                .setName("PropertyInt16")
                .setType(nameInt16)
                .setNullable(false);
        private final CsdlProperty propertyString = new CsdlProperty()
                .setName("PropertyString")
                .setType(nameString);
        private final FullQualifiedName nameCTTwoPrim = new FullQualifiedName(nameSpace, "CTTwoPrim");
        private final FullQualifiedName nameCTTwoPrimBase = new FullQualifiedName(nameSpace, "CTTwoPrimBase");
        private final FullQualifiedName nameUFNRTInt16 = new FullQualifiedName(nameSpace, "UFNRTInt16");
        private final FullQualifiedName nameContainer = new FullQualifiedName(nameSpace, "container");
        private final FullQualifiedName nameENString = new FullQualifiedName(nameSpace, "ENString");

        @Override
        public List<CsdlAliasInfo> getAliasInfos() throws ODataException {
            return Arrays.asList(
                    new CsdlAliasInfo().setAlias("Alias").setNamespace(nameSpace)
            );
        }

        @Override
        public CsdlEnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
            return new CsdlEnumType()
                    .setName("ENString")
                    .setFlags(true)
                    .setUnderlyingType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName())
                    .setMembers(Arrays.asList(
                            new CsdlEnumMember().setName("String1").setValue("1")));
        }

        @Override
        public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
            if (entityTypeName.equals(nameETAbstract)) {
                return new CsdlEntityType()
                        .setName("ETAbstract")
                        .setAbstract(true)
                        .setProperties(Arrays.asList(propertyString));

            } else if (entityTypeName.equals(nameETAbstractBase)) {
                return new CsdlEntityType()
                        .setName("ETAbstractBase")
                        .setBaseType(nameETAbstract)
                        .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16")))
                        .setProperties(Arrays.asList(
                                propertyInt16_NotNullable));
            }
            return null;
        }

        @Override
        public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
            if (complexTypeName.equals(nameCTTwoPrim)) {
                return new CsdlComplexType()
                        .setName("CTTwoPrim")
                        .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString));

            }
            if (complexTypeName.equals(nameCTTwoPrimBase)) {
                return new CsdlComplexType()
                        .setName("CTTwoPrimBase")
                        .setBaseType(nameCTTwoPrim)
                        .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString));

            }
            return null;

        }

        @Override
        public List<CsdlAction> getActions(final FullQualifiedName actionName) throws ODataException {
            if (actionName.equals(nameUARTPrimParam)) {
                return Arrays.asList(
                        new CsdlAction().setName("UARTPrimParam")
                                .setParameters(Arrays.asList(
                                        new CsdlParameter().setName("ParameterInt16").setType(nameInt16)))

                                .setReturnType(new CsdlReturnType().setType(nameString))
                );

            }
            return null;
        }

        @Override
        public List<CsdlFunction> getFunctions(final FullQualifiedName functionName) throws ODataException {
            if (functionName.equals(nameUFNRTInt16)) {
                return Arrays.asList(
                        new CsdlFunction()
                                .setName("UFNRTInt16")
                                .setParameters(new ArrayList<CsdlParameter>())
                                .setReturnType(
                                        new CsdlReturnType().setType(nameInt16))
                );

            }
            return null;
        }

        @Override
        public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
                throws ODataException {
            if (entitySetName.equals("ESAllPrim")) {
                return new CsdlEntitySet()
                        .setName("ESAllPrim")
                        .setType(nameETAbstractBase);

            }
            return null;
        }

        @Override
        public CsdlSingleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
                throws ODataException {
            if (singletonName.equals("SI")) {
                return new CsdlSingleton()
                        .setName("SI")
                        .setType(nameETAbstractBase);

            }
            return null;
        }

        @Override
        public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
                throws ODataException {
            if (entityContainer.equals(nameContainer)) {
                if (actionImportName.equals("AIRTPrimParam")) {
                    return new CsdlActionImport()
                            .setName("AIRTPrimParam")
                            .setAction(nameUARTPrimParam);

                }
            }
            return null;
        }

        @Override
        public CsdlFunctionImport getFunctionImport(final FullQualifiedName entityContainer,
                                                    final String functionImportName)
                throws ODataException {
            if (entityContainer.equals(nameContainer)) {
                if (functionImportName.equals("FINRTInt16")) {
                    return new CsdlFunctionImport()
                            .setName("FINRTInt16")
                            .setFunction(nameUFNRTInt16)
                            .setIncludeInServiceDocument(true);

                }
            }
            return null;
        }

        @Override
        public List<CsdlSchema> getSchemas() throws ODataException {
            List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
            CsdlSchema schema = new CsdlSchema();
            schema.setNamespace(nameSpace);
            schema.setAlias("Alias");
            schemas.add(schema);
            // EnumTypes
            List<CsdlEnumType> enumTypes = new ArrayList<CsdlEnumType>();
            schema.setEnumTypes(enumTypes);
            enumTypes.add(getEnumType(nameENString));
            // EntityTypes
            List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
            schema.setEntityTypes(entityTypes);

            entityTypes.add(getEntityType(nameETAbstract));
            entityTypes.add(getEntityType(nameETAbstractBase));

            // ComplexTypes
            List<CsdlComplexType> complexType = new ArrayList<CsdlComplexType>();
            schema.setComplexTypes(complexType);
            complexType.add(getComplexType(nameCTTwoPrim));
            complexType.add(getComplexType(nameCTTwoPrimBase));

            // TypeDefinitions

            // Actions
            List<CsdlAction> actions = new ArrayList<CsdlAction>();
            schema.setActions(actions);
            actions.addAll(getActions(nameUARTPrimParam));

            // Functions
            List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
            schema.setFunctions(functions);

            functions.addAll(getFunctions(nameUFNRTInt16));

            // EntityContainer
            schema.setEntityContainer(getEntityContainer());

            return schemas;
        }

        @Override
        public CsdlEntityContainer getEntityContainer() throws ODataException {
            CsdlEntityContainer container = new CsdlEntityContainer();
            container.setName("container");

            // EntitySets
            List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
            container.setEntitySets(entitySets);
            entitySets.add(getEntitySet(nameContainer, "ESAllPrim"));

            // Singletons
            List<CsdlSingleton> singletons = new ArrayList<CsdlSingleton>();
            container.setSingletons(singletons);
            singletons.add(getSingleton(nameContainer, "SI"));

            // ActionImports
            List<CsdlActionImport> actionImports = new ArrayList<CsdlActionImport>();
            container.setActionImports(actionImports);
            actionImports.add(getActionImport(nameContainer, "AIRTPrimParam"));

            // FunctionImports
            List<CsdlFunctionImport> functionImports = new ArrayList<CsdlFunctionImport>();
            container.setFunctionImports(functionImports);
            functionImports.add(getFunctionImport(nameContainer, "FINRTInt16"));

            return container;
        }
    }
}