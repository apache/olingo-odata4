package org.apache.olingo.client.core.domain;

/*
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Assert;
import org.junit.Test;

public class ClientEntitySetIteratorTest {
  
    @Test
    public void testGetEntitySetIterator1() throws IOException, URISyntaxException {
        String str = "{ \"@odata.context\": \"http://bdh-df5.wdf.sap.corp:8080/CloudDataIntegration/providers"
            + "/CLOUD_DATA_INTEGRATION_TEST%3ABUG_CURLY_BRACKETS/$metadata#BUG_CURLY_BRACKETSResult/$delta\", "
            + "\"value\": [ "
            + "{ \"@odata.id\": \"BUG_CURLY_BRACKETSResult(1)\", \"ID\": 1, \"TEXT\": \"ABC\", \"TEXT2\": \"DEF\" }, "
            + "{ \"@odata.id\": \"BUG_CURLY_BRACKETSResult(2)\", \"ID\": 2, "
            + "\"TEXT\": "
            + "\"QN6 1311 &amp;&amp;&amp;AmpersandCheck&amp;&amp;&amp; "
            + "~!@#$%^&amp;*()_+=-[];',./?><\\\":}{| @AlmikaPhone\", "
            + "\"TEXT2\": \"QN6 1311 &&&AmpersandCheck&&& ~!@#$%^&*()_+=-[];',./?><\\\":}{| @AlmikaPhone\" }, "
            + "{ \"@odata.id\": "
            + "\"BUG_CURLY_BRACKETSResult(3)\", \"ID\": 3, \"TEXT\": \"}XYZ\", \"TEXT2\": \"LMN\" } ] }";
        
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        ODataClient oDataClient = ODataClientFactory.getClient();
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> entitySetIterator = 
            new ClientEntitySetIterator<ClientEntitySet, ClientEntity>(
            oDataClient, stream, ContentType.parse(ContentType.JSON.toString()));

        ArrayList<ClientEntity> entities = new ArrayList<ClientEntity>();
        while (entitySetIterator.hasNext()) {
            ClientEntity next = entitySetIterator.next();
            entities.add(next);
        }

        Assert.assertEquals(3, entities.size());

        Assert.assertEquals("1", entities.get(0).getProperty("ID").getPrimitiveValue().toString());
        Assert.assertEquals("ABC",
                entities.get(0).getProperty("TEXT").getPrimitiveValue().toString());
        Assert.assertEquals("2", entities.get(1).getProperty("ID").getPrimitiveValue().toString());
        Assert.assertEquals(
            "QN6 1311 &amp;&amp;&amp;AmpersandCheck&amp;&amp;&amp; ~!@#$%^&amp;*()_+=-[];',./?><\":}{| @AlmikaPhone",
                entities.get(1).getProperty("TEXT").getPrimitiveValue().toString());
        Assert.assertEquals("3", entities.get(2).getProperty("ID").getPrimitiveValue().toString());
        Assert.assertEquals("}XYZ",
                entities.get(2).getProperty("TEXT").getPrimitiveValue().toString());
    }

    @Test
    public void testGetEntitySetIteratorWithInnerNav() throws IOException, URISyntaxException {
        String str = "{\"@odata.context\":\"$metadata#Cubes(Name)\","
            + "\"value\":[{\"@odata.etag\":\"W/\\\"c24af675e00a3f95ef63f223fb9c2cc8d6455459\\\"\","
            + "\"Name\":\"}Capabilities\","
            + "\"NavProp\":{\"PropertyInt\":1}}]}";
        
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        ODataClient oDataClient = ODataClientFactory.getClient();
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> entitySetIterator = 
            new ClientEntitySetIterator<ClientEntitySet, ClientEntity>(
            oDataClient, stream, ContentType.parse(ContentType.JSON.toString()));

        ArrayList<ClientEntity> entities = new ArrayList<ClientEntity>();
        while (entitySetIterator.hasNext()) {
            ClientEntity next = entitySetIterator.next();
            entities.add(next);
        }

        Assert.assertEquals(1, entities.size());
        Assert.assertNotNull(entities.get(0).getProperty("NavProp"));
        Assert.assertEquals("}Capabilities", entities.get(0).getProperty("Name").getPrimitiveValue().toString());
    }
    
    @Test
    public void testGetEntitySetIteratorWithInnerNavArray() throws IOException, URISyntaxException {
        String str = "{\"@odata.context\":\"$metadata#Cubes(Name)\","
            + "\"value\":[{\"@odata.etag\":\"W/\\\"c24af675e00a3f95ef63f223fb9c2cc8d6455459\\\"\","
            + "\"Name\":\"}Capabilities\","
            + "\"NavProp\":[{\"PropertyInt1\":1},{\"PropertyInt2\":2}]},"
            + "{\"@odata.etag\":\"W/\\\"c24af675e00a3f95ef63f223fb9c2cc8d6455459\\\"\",\"Name\":\"ABC()}\","
            + "\"NavProp\":[{\"PropertyInt1\":3}]}]}";
        
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        ODataClient oDataClient = ODataClientFactory.getClient();
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> entitySetIterator = 
            new ClientEntitySetIterator<ClientEntitySet, ClientEntity>(
            oDataClient, stream, ContentType.parse(ContentType.JSON.toString()));

        ArrayList<ClientEntity> entities = new ArrayList<ClientEntity>();
        while (entitySetIterator.hasNext()) {
            ClientEntity next = entitySetIterator.next();
            entities.add(next);
        }

        Assert.assertEquals(2, entities.size());
        Assert.assertNotNull(entities.get(0).getProperty("NavProp"));
        Assert.assertTrue(entities.get(0).getProperty("NavProp").hasCollectionValue());
        Assert.assertEquals("}Capabilities", entities.get(0).getProperty("Name").getPrimitiveValue().toString());
    }
    @Test
    public void testGetEntitySetIterator3() throws IOException, URISyntaxException {
        String str = "{\"@odata.context\":\"$metadata#Cubes(Name)\","
            + "\"@odata.metadataEtag\": \"W/\\\"582997db-15b9-4a23-a8b0-c91bf45b4194\\\"\","
            + "\"value\":[{\"PropertyInt16\": 0,\"PropertyString\": \"\"}]}";
        
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        ODataClient oDataClient = ODataClientFactory.getClient();
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> entitySetIterator = 
            new ClientEntitySetIterator<ClientEntitySet, ClientEntity>(
            oDataClient, stream, ContentType.parse(ContentType.JSON.toString()));

        ArrayList<ClientEntity> entities = new ArrayList<ClientEntity>();
        while (entitySetIterator.hasNext()) {
            ClientEntity next = entitySetIterator.next();
            entities.add(next);
        }

        Assert.assertEquals(1, entities.size());

        Assert.assertEquals("", entities.get(0).getProperty("PropertyString").getPrimitiveValue().toString());
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void testEntitySetIteratorRemoveMethod() throws IOException, URISyntaxException {
        String str = "{\"@odata.context\":\"$metadata#Cubes(Name)\","
            + "\"@odata.metadataEtag\": \"W/\\\"582997db-15b9-4a23-a8b0-c91bf45b4194\\\"\","
            + "\"value\":[{\"PropertyInt16\": 0,\"PropertyString\": \"\"}]}";
        
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        ODataClient oDataClient = ODataClientFactory.getClient();
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> entitySetIterator = 
            new ClientEntitySetIterator<ClientEntitySet, ClientEntity>(
            oDataClient, stream, ContentType.parse(ContentType.JSON.toString()));

        entitySetIterator.remove();
    }
    
    @Test(expected=IllegalStateException.class)
    public void testEntitySetIteratorGetNextMethod() throws IOException, URISyntaxException {
        String str = "{\"@odata.context\":\"$metadata#Cubes(Name)\","
            + "\"@odata.metadataEtag\": \"W/\\\"582997db-15b9-4a23-a8b0-c91bf45b4194\\\"\","
            + "\"value\":[{\"PropertyInt16\": 0,\"PropertyString\": \"\"}]}";
        
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        ODataClient oDataClient = ODataClientFactory.getClient();
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> entitySetIterator = 
            new ClientEntitySetIterator<ClientEntitySet, ClientEntity>(
            oDataClient, stream, ContentType.parse(ContentType.JSON.toString()));

        entitySetIterator.getNext();
    }
    
    @Test
    public void testEntitySetIteratorNextLink() throws IOException, URISyntaxException {
        String str = "{\"@odata.context\":\"$metadata#Cubes(Name)\","
            + "\"@odata.metadataEtag\": \"W/\\\"582997db-15b9-4a23-a8b0-c91bf45b4194\\\"\","
            + "\"@odata.nextLink\":\"http://localhost:8082/odata-server-tecsvc/odata.svc/"
            + "ESServerSidePaging?%24skiptoken=1%2A10\","
            + "\"value\":[{\"PropertyInt16\": 0,\"PropertyString\": \"\"}]}";
        
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        ODataClient oDataClient = ODataClientFactory.getClient();
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> entitySetIterator = 
            new ClientEntitySetIterator<ClientEntitySet, ClientEntity>(
            oDataClient, stream, ContentType.parse(ContentType.JSON.toString()));
        
        ArrayList<ClientEntity> entities = new ArrayList<ClientEntity>();
        while (entitySetIterator.hasNext()) {
            ClientEntity next = entitySetIterator.next();
            entities.add(next);
        }

        Assert.assertEquals(1, entities.size());
        Assert.assertNotNull(entitySetIterator.getNext());
        Assert.assertEquals("http://localhost:8082/odata-server-tecsvc/"
            + "odata.svc/ESServerSidePaging?%24skiptoken=1%2A10", entitySetIterator.getNext().toString());
    }
}
