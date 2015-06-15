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
package org.apache.olingo.ext.spring.integration;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServlet;

import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SpringOlingoNamespaceIntegrationTest {
	private static Server server;

	private static void configureAndStartupEmbeddedWebServer(Class<? extends HttpServlet> odataServletClass) throws Exception {
		server = startupEmbeddedWebServer();
		ServletHandler handler = (ServletHandler) server.getHandler();
        handler.addServletWithMapping(odataServletClass, "/odata.svc/*");
        server.start();
	}

	private static Server startupEmbeddedWebServer() throws Exception {
		Server server = new Server(8080);
		ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
		//server.start();
        
		return server;
	}
	
	private static void shutdownEmbeddedWebServer() throws Exception {
		if (server!=null) {
			server.stop();
	        server.join();
	        server.destroy();
		}
	}

	@BeforeClass
	public static void setUp() throws Exception {
		configureAndStartupEmbeddedWebServer(OlingoSpringTestServlet.class);
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		shutdownEmbeddedWebServer();
	}

	//@Test
	public void testServiceDocument() {
		String serviceRoot = "http://localhost:8080/odata.svc";

		ODataClient client = ODataClientFactory.getV4();
		ODataServiceDocumentRequest req =
	        client.getRetrieveRequestFactory().getServiceDocumentRequest(serviceRoot);
		req.setContentType("application/json;odata.metadata=minimal");
		req.setAccept("application/json;odata.metadata=minimal");
		ODataRetrieveResponse<ODataServiceDocument> res = req.execute();
		
		ODataServiceDocument serviceDocument = res.getBody();

		Collection<String> entitySetNames = serviceDocument.getEntitySetNames();
		Assert.assertEquals(1, entitySetNames.size());
		String entitySetName = entitySetNames.iterator().next();
		Assert.assertEquals("sources1", entitySetName);

		Map<String,URI> entitySets = serviceDocument.getEntitySets();
		Assert.assertEquals(1, entitySets.size());
		Entry<String, URI> entitySet = entitySets.entrySet().iterator().next();
		Assert.assertEquals("sources1", entitySet.getKey());
		Assert.assertEquals("http://localhost:8080/odata.svc/sources1", entitySet.getValue().toString());

		Map<String,URI> singletons = serviceDocument.getSingletons();
		Assert.assertEquals(0, singletons.size());

		Map<String,URI> functionImports = serviceDocument.getFunctionImports();
		Assert.assertEquals(0, functionImports.size());
	}

	@Test
	public void testMetadata() throws InterruptedException {
        //Thread.sleep(60000);
		String serviceRoot = "http://localhost:8080/odata.svc";

		ODataClient client = ODataClientFactory.getV4();
		EdmMetadataRequest request =
	        client.getRetrieveRequestFactory().getMetadataRequest(serviceRoot);
		ODataRetrieveResponse<Edm> response = request.execute();
		
		Edm edm = response.getBody();
		
		List<EdmSchema> schemas = edm.getSchemas();
		for (EdmSchema schema : schemas) {
			System.out.println(schema.getNamespace());
			System.out.println(">> schema.getComplexTypes() size = "+schema.getComplexTypes().size());
			for (EdmComplexType complexType : schema.getComplexTypes()) {
				System.out.println("- complex type = "+complexType);
			}
			System.out.println(">> schema.getEntityTypes() size = "+schema.getEntityTypes().size());
			for (EdmEntityType entityType : schema.getEntityTypes()) {
				System.out.println("- entity type = "+entityType);
				System.out.println("    >> qn = "+entityType.getFullQualifiedName());
			}
		}
		
		//edm.get
		EdmEntityType customerType = edm.getEntityType(
	            new FullQualifiedName("NorthwindModel", "Order"));
		List<String> propertyNames = customerType.getPropertyNames();
		for (String propertyName : propertyNames) {
			System.out.println(" - propertyName = "+propertyName);
			EdmProperty property = customerType.getStructuralProperty(propertyName);
			FullQualifiedName typeName = property.getType().getFullQualifiedName();
			System.out.println("   - type name = "+typeName);
		}
	}
}
