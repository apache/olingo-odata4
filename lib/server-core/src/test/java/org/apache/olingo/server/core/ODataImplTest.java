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
package org.apache.olingo.server.core;

import static org.junit.Assert.assertNotNull;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.junit.Test;

public class ODataImplTest {
	
	private final OData odata = OData.newInstance();
	
	@Test(expected=SerializerException.class)
	public void testJsonSerializerForOdataMetadataNone() throws SerializerException {
		odata.createSerializer(ContentType.JSON_NO_METADATA);
	}
	
	@Test(expected=SerializerException.class)
	public void testJsonSerializerForODataMetadataFull() throws SerializerException {
		odata.createSerializer(ContentType.JSON_FULL_METADATA);
	}
	
	@Test
	public void testCreateJsonSerializerForODataMetadataMinimal() throws SerializerException {
		final ODataSerializer serializer = odata.createSerializer(ContentType.JSON);
		
		assertNotNull(serializer);
	}
	
	@Test
	public void testCreateJsonDeserialierForODataMetadataMinimal() throws DeserializerException {
		final ODataDeserializer deserializer = odata.createDeserializer(ContentType.JSON);
		
		assertNotNull(deserializer);
	}
}
