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

package org.apache.olingo.client.core.v4;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonMetadataTest extends AbstractTest {


    @Override
    protected ODataClient getClient() {
        return v4Client;
    }

    @Test
    public void parse() {

        //container
        final Edm edm = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("metadata.json"), ODataFormat.JSON);
        assertNotNull(edm);

        assertNotNull(edm.getEnumType(new FullQualifiedName("namespace", "ENString")));
        assertNotNull(edm.getEntityType(new FullQualifiedName("namespace", "ETAbstractBase")));
        assertNotNull(edm.getEntityContainer(new FullQualifiedName("namespace", "container"))
                .getEntitySet("ESAllPrim"));
        assertEquals(edm.getEntityType(new FullQualifiedName("namespace", "ETAbstractBase")),
                edm.getEntityContainer(new FullQualifiedName("namespace", "container"))
                        .getEntitySet("ESAllPrim").getEntityType());

        //action
        final Edm edm2 = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("metadata.actions.functions.json"), ODataFormat.JSON);
        assertNotNull(edm2);
        EdmAction action = edm2.getUnboundAction(new FullQualifiedName("namespace", "UARTPrimParam"));
        assertNotNull(action);
        assertFalse(action.isBound());
        assertEquals(action.getParameterNames().size(), 1);
        assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16),
                action.getParameter("ParameterInt16").getType());
        assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String),
                action.getReturnType().getType());

        //function
        EdmFunction function = edm2.getUnboundFunctions(new FullQualifiedName("namespace","UFNRTInt16")).get(0);
        assertEquals( edm2.getUnboundFunctions(new FullQualifiedName("namespace", "UFNRTInt16")).size(), 1);
        assertNotNull(function);
        assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16),
                function.getReturnType().getType());
        assertFalse(function.isBound());


        //references
        final XMLMetadata metadata = getClient().getDeserializer(ODataFormat.JSON).
                toMetadata(getClass().getResourceAsStream("metadata.json"));
        assertNotNull(metadata);
        //references size
        assertEquals(metadata.getReferences().size(), 1);
        assertEquals(metadata.getReferences().get(0).getUri().toASCIIString(), "http://docs.oasis-open.org/odata/" +
                "odata/v4.0/cs02/vocabularies/Org.OData.Core.V1.xml");
        //references includes size
        assertEquals(metadata.getReferences().get(0).getIncludes().size(), 1);
        assertEquals(metadata.getReferences().get(0).getIncludes().get(0).getAlias(), "Core");
        assertEquals(metadata.getReferences().get(0).getIncludes().get(0).getNamespace(), "Org.OData.Core.V1");
        //references include annotations
        assertEquals(metadata.getReferences().get(0).getIncludeAnnotations().size(), 0);

        //singleton
        final Edm edm3 = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("metadata.singletons.json"), ODataFormat.JSON);
        assertNotNull(edm3);
        assertEquals(edm3.getEntityContainer(
                new FullQualifiedName("olingo.odata.test1", "container")).getSingletons().size(), 1);
        EdmSingleton singleton = edm3.getEntityContainer(
                new FullQualifiedName("olingo.odata.test1", "container")).getSingleton("SINav");
        assertNotNull(singleton);
        assertNotNull(singleton.getEntityType());
        assertEquals(singleton.getEntityType().getName(), "ETTwoKeyNav");
        assertEquals(singleton.getEntityType().getNamespace(), "olingo.odata.test1");
        assertNotNull(edm3.getEntityType(new FullQualifiedName("olingo.odata.test1", "ETTwoKeyNav")));
        assertEquals(edm3.getEntityType(new FullQualifiedName("olingo.odata.test1", "ETTwoKeyNav")),
                edm3.getEntityContainer(new FullQualifiedName("olingo.odata.test1", "container"))
                        .getEntitySet("ESTwoKeyNav").getEntityType());
        assertEquals(singleton.getNavigationPropertyBindings().size(), 3);


        //action imports
        final Edm edm4 = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("metadata.action.imports.json"), ODataFormat.JSON);
        assertNotNull(edm4);
        assertNotNull(edm4.getEntityContainer(
                new FullQualifiedName("namespace", "container")));
        assertNotNull(edm4.getEntityContainer(
                new FullQualifiedName("namespace", "container")).getActionImport("AIRTString"));
        EdmActionImport actionImport = edm4.getEntityContainer(
                new FullQualifiedName("namespace", "container")).getActionImport("AIRTString");
        assertNotNull(actionImport.getUnboundAction());
        assertEquals(actionImport.getUnboundAction().getName(), "UARTString");
        assertEquals(actionImport.getUnboundAction().getNamespace(), "namespace");

        //function imports
        final Edm edm5 = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("metadata.function.imports.json"), ODataFormat.JSON);
        assertNotNull(edm5);
        assertNotNull(edm5.getEntityContainer(
                new FullQualifiedName("namespace", "container")));
        assertNotNull(edm5.getEntityContainer(
                new FullQualifiedName("namespace", "container")).getFunctionImport("FICRTCollESTwoKeyNavParam"));
        EdmFunctionImport functionImport = edm5.getEntityContainer(
                new FullQualifiedName("namespace", "container")).getFunctionImport("FICRTCollESTwoKeyNavParam");
        assertEquals(functionImport.isIncludeInServiceDocument(), true);
        assertEquals(functionImport.getUnboundFunctions().size(), 1);
        assertEquals(functionImport.getUnboundFunctions().get(0).getName(), "UFCRTCollETTwoKeyNavParam");
        assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16),
                functionImport.getUnboundFunctions().get(0).getReturnType().getType());
        assertEquals(functionImport.getReturnedEntitySet().getName(), "ESTwoKeyNav");
        assertEquals(functionImport.getReturnedEntitySet().isIncludeInServiceDocument(), true);

    }

    @Test
    public void multipleSchemas() {

        final XMLMetadata metadata = getClient().getDeserializer(ODataFormat.JSON).
                toMetadata(getClass().getResourceAsStream("metadata.multiple.schema.json"));
        assertNotNull(metadata);
        assertEquals(metadata.getSchemas().size(), 3);

        CsdlSchema first = metadata.getSchema("namespace1");
        assertNotNull(first);
        assertEquals(first.getAlias(), "Alias1");

        CsdlSchema second = metadata.getSchema("namespace2");
        assertNotNull(second);
        assertEquals(second.getAlias(), "Alias2");

        CsdlSchema third = metadata.getSchema("namespace3");
        assertNotNull(third);
        assertEquals(third.getAlias(), "Alias3");

        assertNotNull(third.getEntityContainer());
        assertEquals(third.getEntityContainer().getName(), "container");

    }
}