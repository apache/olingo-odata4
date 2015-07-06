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
        assertTrue(resultString.contains("\"http://example.com\" : { }"));
        assertTrue(resultString.contains("\"http://localhost/odata/odata/v4.0/referenceWithInclude\" : {\n" +
                "      \"includes\" : {\n" +
                "        \"Org.OData.Core.V1\" : {\n" +
                "          \"alias\" : \"Core\"\n" +
                "        }\n" +
                "      }\n" +
                "    }"));
        assertTrue(resultString.contains("\"http://localhost/odata/odata/v4.0/referenceWithTwoIncludes\" : {\n" +
                "      \"includes\" : {\n" +
                "        \"Org.OData.Core.2\" : {\n" +
                "          \"alias\" : \"Core2\"\n" +
                "        },\n" +
                "        \"Org.OData.Core.3\" : {\n" +
                "          \"alias\" : \"Core3\"\n" +
                "        }\n" +
                "      }\n" +
                "    }"));
        assertTrue(resultString.contains("\"http://localhost/odata/odata/v4.0/referenceWithIncludeAnnos\" : {\n" +
                "      \"includeAnnotations\" : [ {\n" +
                "        \"termNamespace\" : \"TermNs.2\",\n" +
                "        \"qualifier\" : \"Q.2\",\n" +
                "        \"targetNamespace\" : \"TargetNS.2\"\n" +
                "      }, {\n" +
                "        \"termNamespace\" : \"TermNs.3\",\n" +
                "        \"qualifier\" : \"Q.3\",\n" +
                "        \"targetNamespace\" : \"TargetNS.3\"\n" +
                "      } ]\n" +
                "    }"));
        assertTrue(resultString.contains("\"http://localhost/odata/odata/v4.0/referenceWithAll\" : {\n" +
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
                "    }"));
        assertTrue(resultString.contains("\"http://localhost/odata/odata/v4.0/referenceWithAllAndNull\" : {\n" +
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
                "    }"));
        assertTrue(resultString.contains("\"references\""));
    }

    @Test
    public void metadataDocumentJsonTest() throws Exception {
        CsdlEdmProvider provider = new LocalProvider();
        ServiceMetadata serviceMetadata = new ServiceMetadataImpl(provider, Collections.<EdmxReference> emptyList());
        InputStream metadataStream = serializer.metadataDocument(serviceMetadata).getContent();
        String metadata = IOUtils.toString(metadataStream);
        assertNotNull(metadata);
        assertTrue(metadata.contains("\"namespace.ENString\" : {\n" +
                "      \"enum\" : [ \"String1\" ],\n" +
                "      \"String1@odata.value\" : \"1\"\n" +
                "    }"));
        assertTrue(metadata.contains("\"namespace.ETAbstract\" : {\n" +
                "      \"type\" : \"object\",\n" +
                "      \"abstract\" : true,\n" +
                "      \"properties\" : {\n" +
                "        \"PropertyString\" : {\n" +
                "          \"type\" : [ \"string\", null ]\n" +
                "        }\n" +
                "      }\n" +
                "    }"));
        assertTrue(metadata.contains("\"namespace.ETAbstractBase\" : {\n" +
                "      \"type\" : \"object\",\n" +
                "      \"allOf\" : [ {\n" +
                "        \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/namespace.ETAbstract\"\n" +
                "      } ],\n" +
                "      \"keys\" : [ {\n" +
                "        \"name\" : \"PropertyInt16\"\n" +
                "      } ],\n" +
                "      \"properties\" : {\n" +
                "        \"PropertyInt16\" : {\n" +
                "          \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/Edm.Int16\"\n" +
                "        }\n" +
                "      }\n" +
                "    }"));
        assertTrue(metadata.contains(" \"namespace.CTTwoPrim\" : {\n" +
                "      \"type\" : \"object\",\n" +
                "      \"properties\" : {\n" +
                "        \"PropertyInt16\" : {\n" +
                "          \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/Edm.Int16\"\n" +
                "        },\n" +
                "        \"PropertyString\" : {\n" +
                "          \"type\" : [ \"string\", null ]\n" +
                "        }\n" +
                "      }\n" +
                "    }"));
        assertTrue(metadata.contains("\"namespace.CTTwoPrimBase\" : {\n" +
                "      \"type\" : \"object\",\n" +
                "      \"allOf\" : [ {\n" +
                "        \"$ref\" : \"http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#/" +
                "definitions/namespace.CTTwoPrim\"\n" +
                "      } ],\n" +
                "      \"properties\" : { }\n" +
                "    }"));
        assertTrue(metadata.contains("\"actions\" : [ {\n" +
                "        \"name\" : \"UARTPrimParam\",\n" +
                "        \"isBound\" : false,\n" +
                "        \"parameters\" : {\n" +
                "          \"ParameterInt16\" : {\n" +
                "            \"type\" : \"Edm.Int16\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"returnType\" : {\n" +
                "          \"type\" : \"Edm.String\"\n" +
                "        }\n" +
                "      } ]"));
        assertTrue(metadata.contains("\"functions\" : [ {\n" +
                "        \"name\" : \"UFNRTInt16\",\n" +
                "        \"parameters\" : { },\n" +
                "        \"returnType\" : {\n" +
                "          \"type\" : \"Edm.Int16\"\n" +
                "        }\n" +
                "      } ]"));
        assertTrue(metadata.contains("\"entitySets\" : {\n" +
                "          \"ESAllPrim\" : {\n" +
                "            \"entityType\" : \"Alias.ETAbstractBase\"\n" +
                "          }\n" +
                "        }"));
        assertTrue(metadata.contains("\"actionImports\" : {\n" +
                "          \"AIRTPrimParam\" : {\n" +
                "            \"action\" : \"Alias.UARTPrimParam\"\n" +
                "          }\n" +
                "        }"));
        assertTrue(metadata.contains("\"functionImports\" : {\n" +
                "          \"FINRTInt16\" : {\n" +
                "            \"function\" : \"Alias.UFNRTInt16\",\n" +
                "            \"includeInServiceDocument\" : true\n" +
                "          }\n" +
                "        }"));
        assertTrue(metadata.contains("\"singletons\" : {\n" +
                "          \"SI\" : {\n" +
                "            \"type\" : \"Alias.ETAbstractBase\"\n" +
                "          }\n" +
                "        }"));
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