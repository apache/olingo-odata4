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
package org.apache.olingo.ext.spring.config;

import java.util.List;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.EntityContainer;
import org.apache.olingo.server.api.edm.provider.EntityType;
import org.apache.olingo.server.api.edm.provider.Property;
import org.apache.olingo.server.api.edm.provider.PropertyRef;
import org.apache.olingo.server.api.edm.provider.Schema;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringOlingoNamespaceEdmProviderTest {
	@Test
	public void testLaunchSpring() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"/applicationContext-edm-provider-namespace.xml");
		try {
			EdmProvider edmProvider = (EdmProvider) context
					.getBean("edmProvider");
			Assert.assertNotNull(edmProvider);
			try {
				EntityContainer entityContainer = edmProvider.getEntityContainer();
				Assert.assertNotNull(entityContainer);
				Assert.assertNotNull(entityContainer.getEntitySets());
				Assert.assertEquals(1, entityContainer.getEntitySets().size());
				
				List<Schema> schemas = edmProvider.getSchemas();
				Assert.assertNotNull(schemas);
				Assert.assertEquals(1, schemas.size());

				Schema schema = schemas.get(0);
				Assert.assertEquals("test", schema.getNamespace());
				
				List<EntityType> entityTypes = schema.getEntityTypes();
				Assert.assertNotNull(entityTypes);
				Assert.assertEquals(1, entityTypes.size());
				
				EntityType entityType = entityTypes.get(0);
				Assert.assertEquals("sources1", entityType.getName());
				
				List<Property> properties = entityType.getProperties();
				Assert.assertNotNull(properties);
				Assert.assertEquals(6, properties.size());
				// field #0
				Property property0 = properties.get(0);
				Assert.assertEquals("field1", property0.getName());
				Assert.assertEquals("Edm.String", property0.getType().toString());
				// field #1
				Property property1 = properties.get(1);
				Assert.assertEquals("field2", property1.getName());
				Assert.assertEquals("Edm.Int32", property1.getType().toString());
				// field #2
				Property property2 = properties.get(2);
				Assert.assertEquals("field3", property2.getName());
				Assert.assertEquals("Edm.Int64", property2.getType().toString());
				// field #3
				Property property3 = properties.get(3);
				Assert.assertEquals("field4", property3.getName());
				Assert.assertEquals("Edm.Double", property3.getType().toString());
				// field #4
				Property property4 = properties.get(4);
				Assert.assertEquals("field5", property4.getName());
				Assert.assertEquals("Edm.Double", property4.getType().toString());
				// field #5
				Property property5 = properties.get(5);
				Assert.assertEquals("field6", property5.getName());
				Assert.assertEquals("Edm.Boolean", property5.getType().toString());
				
				List<PropertyRef> key = entityType.getKey();
				Assert.assertNotNull(key);
				Assert.assertEquals(1, key.size());
				PropertyRef key0 = key.get(0);
				Assert.assertEquals("field1", key0.getPropertyName());
			} catch (ODataException ex) {
				ex.printStackTrace();
			}
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}
}
