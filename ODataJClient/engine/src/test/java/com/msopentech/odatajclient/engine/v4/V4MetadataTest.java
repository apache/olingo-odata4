/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.msopentech.odatajclient.engine.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.msopentech.odatajclient.engine.AbstractTest;
import com.msopentech.odatajclient.engine.client.ODataV4Client;
import com.msopentech.odatajclient.engine.metadata.EdmV4Metadata;
import com.msopentech.odatajclient.engine.metadata.EdmV4Type;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.metadata.edm.StoreGeneratedPattern;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Action;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Annotation;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Annotations;
import com.msopentech.odatajclient.engine.metadata.edm.v4.ComplexType;
import com.msopentech.odatajclient.engine.metadata.edm.v4.EntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.v4.EntitySet;
import com.msopentech.odatajclient.engine.metadata.edm.v4.EntityType;
import com.msopentech.odatajclient.engine.metadata.edm.v4.EnumType;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Function;
import com.msopentech.odatajclient.engine.metadata.edm.v4.FunctionImport;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Schema;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Singleton;
import com.msopentech.odatajclient.engine.metadata.edm.v4.annotation.Apply;
import com.msopentech.odatajclient.engine.metadata.edm.v4.annotation.Collection;
import com.msopentech.odatajclient.engine.metadata.edm.v4.annotation.ConstExprConstruct;
import com.msopentech.odatajclient.engine.metadata.edm.v4.annotation.Path;
import java.util.List;
import org.junit.Test;

public class V4MetadataTest extends AbstractTest {

    @Override
    protected ODataV4Client getClient() {
        return v4Client;
    }

    @Test
    public void parse() {
        final EdmV4Metadata metadata = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("metadata.xml"));
        assertNotNull(metadata);

        // 1. Enum
        final EnumType responseEnumType = metadata.getSchema(0).getEnumType("ResponseType");
        assertNotNull(responseEnumType);
        assertEquals(6, responseEnumType.getMembers().size());
        assertEquals(3, responseEnumType.getMember("Accepted").getValue().intValue());
        assertEquals("Accepted", responseEnumType.getMember(3).getName());

        final EdmV4Type responseType = new EdmV4Type(metadata, "Microsoft.Exchange.Services.OData.Model.ResponseType");
        assertNotNull(responseType);
        assertFalse(responseType.isCollection());
        assertFalse(responseType.isSimpleType());
        assertTrue(responseType.isEnumType());
        assertFalse(responseType.isComplexType());
        assertFalse(responseType.isEntityType());

        // 2. Complex
        final ComplexType responseStatus = metadata.getSchema(0).getComplexType("ResponseStatus");
        assertNotNull(responseStatus);
        assertTrue(responseStatus.getNavigationProperties().isEmpty());
        assertEquals(EdmSimpleType.DateTimeOffset,
                EdmSimpleType.fromValue(responseStatus.getProperty("Time").getType()));

        // 3. Entity
        final EntityType user = metadata.getSchema(0).getEntityType("User");
        assertNotNull(user);
        assertEquals("Microsoft.Exchange.Services.OData.Model.Entity", user.getBaseType());
        assertFalse(user.getProperties().isEmpty());
        assertFalse(user.getNavigationProperties().isEmpty());
        assertEquals("Microsoft.Exchange.Services.OData.Model.Folder", user.getNavigationProperty("Inbox").getType());

        // 4. Action
        final List<Action> moves = metadata.getSchema(0).getActions("Move");
        assertFalse(moves.isEmpty());
        Action move = null;
        for (Action action : moves) {
            if ("Microsoft.Exchange.Services.OData.Model.EmailMessage".equals(action.getReturnType().getType())) {
                move = action;
            }
        }
        assertNotNull(move);
        assertTrue(move.isBound());
        assertEquals("bindingParameter", move.getEntitySetPath());
        assertEquals(2, move.getParameters().size());
        assertEquals("Microsoft.Exchange.Services.OData.Model.EmailMessage", move.getParameters().get(0).getType());

        // 5. EntityContainer
        final EntityContainer container = metadata.getSchema(0).getEntityContainer();
        assertNotNull(container);
        final EntitySet users = container.getEntitySet("Users");
        assertNotNull(users);
        assertEquals(metadata.getSchema(0).getNamespace() + "." + user.getName(), users.getEntityType());
        assertEquals(user.getNavigationProperties().size(), users.getNavigationPropertyBindings().size());
    }

    @Test
    public void demo() {
        final EdmV4Metadata metadata = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("demo-metadata.xml"));
        assertNotNull(metadata);

        assertFalse(metadata.getSchema(0).getAnnotationsList().isEmpty());
        Annotations annots = metadata.getSchema(0).getAnnotationsList("ODataDemo.DemoService/Suppliers");
        assertNotNull(annots);
        assertFalse(annots.getAnnotations().isEmpty());
        assertEquals(ConstExprConstruct.Type.String,
                annots.getAnnotation("Org.OData.Publication.V1.PrivacyPolicyUrl").getConstExpr().getType());
        assertEquals("http://www.odata.org/",
                annots.getAnnotation("Org.OData.Publication.V1.PrivacyPolicyUrl").getConstExpr().getValue());
    }

    @Test
    public void multipleSchemas() {
        final EdmV4Metadata metadata = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("northwind-metadata.xml"));
        assertNotNull(metadata);

        final Schema first = metadata.getSchema("NorthwindModel");
        assertNotNull(first);

        final Schema second = metadata.getSchema("ODataWebExperimental.Northwind.Model");
        assertNotNull(second);

        assertEquals(StoreGeneratedPattern.Identity,
                first.getEntityType("Category").getProperty("CategoryID").getStoreGeneratedPattern());

        final EntityContainer entityContainer = second.getDefaultEntityContainer();
        assertNotNull(entityContainer);
        assertEquals("NorthwindEntities", entityContainer.getName());
        assertTrue(entityContainer.isLazyLoadingEnabled());
    }

    /**
     * Tests Example 85 from CSDL specification.
     */
    @Test
    public void fromdoc1() {
        final EdmV4Metadata metadata = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("fromdoc1-metadata.xml"));
        assertNotNull(metadata);

        assertFalse(metadata.getReferences().isEmpty());
        assertEquals("Org.OData.Measures.V1", metadata.getReferences().get(1).getIncludes().get(0).getNamespace());

        final EntityType product = metadata.getSchema(0).getEntityType("Product");
        assertTrue(product.isHasStream());
        assertEquals("UoM.ISOCurrency", product.getProperty("Price").getAnnotation().getTerm());
        //assertEquals("Currency", product.getProperty("Price").getAnnotation().));
        assertEquals("Products", product.getNavigationProperty("Supplier").getPartner());

        final EntityType category = metadata.getSchema(0).getEntityType("Category");
        final EdmV4Type type = new EdmV4Type(metadata, category.getNavigationProperty("Products").getType());
        assertNotNull(type);
        assertTrue(type.isCollection());
        assertFalse(type.isSimpleType());

        final ComplexType address = metadata.getSchema(0).getComplexType("Address");
        assertFalse(address.getNavigationProperty("Country").getReferentialConstraints().isEmpty());
        assertEquals("Name",
                address.getNavigationProperty("Country").getReferentialConstraints().get(0).getReferencedProperty());

        final Function productsByRating = metadata.getSchema(0).getFunctions("ProductsByRating").get(0);
        assertNotNull(productsByRating.getParameter("Rating"));
        assertEquals("Edm.Int32", productsByRating.getParameter("Rating").getType());
        assertEquals("Collection(ODataDemo.Product)", productsByRating.getReturnType().getType());

        final Singleton contoso = metadata.getSchema(0).getEntityContainer().getSingleton("Contoso");
        assertNotNull(contoso);
        assertFalse(contoso.getNavigationPropertyBindings().isEmpty());
        assertEquals("Products", contoso.getNavigationPropertyBindings().get(0).getPath());

        final FunctionImport functionImport = metadata.getSchema(0).getEntityContainer().
                getFunctionImport("ProductsByRating");
        assertNotNull(functionImport);
        assertEquals(metadata.getSchema(0).getNamespace() + "." + productsByRating.getName(),
                functionImport.getFunction());
    }

    /**
     * Tests Example 86 from CSDL specification.
     */
    @Test
    public void fromdoc2() {
        final EdmV4Metadata metadata = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("fromdoc2-metadata.xml"));
        assertNotNull(metadata);

        // Check displayName
        final Annotation displayName = metadata.getSchema(0).getAnnotationsList("ODataDemo.Supplier").
                getAnnotation("Vocabulary1.DisplayName");
        assertNotNull(displayName);
        assertNull(displayName.getConstExpr());
        assertNotNull(displayName.getDynExpr());

        assertTrue(displayName.getDynExpr() instanceof Apply);
        final Apply apply = (Apply) displayName.getDynExpr();
        assertEquals(Apply.CANONICAL_FUNCTION_CONCAT, apply.getFunction());
        assertEquals(3, apply.getParameters().size());

        final Path firstArg = new Path();
        firstArg.setValue("Name");
        assertEquals(firstArg, apply.getParameters().get(0));

        final ConstExprConstruct secondArg = new ConstExprConstruct();
        secondArg.setType(ConstExprConstruct.Type.String);
        secondArg.setValue(" in ");
        assertEquals(secondArg, apply.getParameters().get(1));

        final Path thirdArg = new Path();
        thirdArg.setValue("Address/CountryName");
        assertEquals(thirdArg, apply.getParameters().get(2));

        // Check Tags
        final Annotation tags = metadata.getSchema(0).getAnnotationsList("ODataDemo.Product").
                getAnnotation("Vocabulary1.Tags");
        assertNotNull(tags);
        assertNull(tags.getConstExpr());
        assertNotNull(tags.getDynExpr());

        assertTrue(tags.getDynExpr() instanceof Collection);
        final Collection collection = (Collection) tags.getDynExpr();
        assertEquals(1, collection.getItems().size());
        assertEquals(ConstExprConstruct.Type.String, ((ConstExprConstruct) collection.getItems().get(0)).getType());
        assertEquals("MasterData", ((ConstExprConstruct) collection.getItems().get(0)).getValue());
    }

    /**
     * Various annotation examples taken from CSDL specification.
     */
    @Test
    public void fromdoc3() {
        final EdmV4Metadata metadata = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("fromdoc3-metadata.xml"));
        assertNotNull(metadata);
    }
}
