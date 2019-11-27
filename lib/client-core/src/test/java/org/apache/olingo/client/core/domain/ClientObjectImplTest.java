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
package org.apache.olingo.client.core.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.olingo.client.api.domain.ClientDeletedEntity.Reason;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientInlineEntity;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientLinkType;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.ODataClientImpl;
import org.apache.olingo.client.core.domain.ClientPrimitiveValueImpl.BuilderImpl;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.junit.Test;

public class ClientObjectImplTest {

  @Test
  public void testFactory() throws URISyntaxException {
    
    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    ClientObjectFactory factory = client.getObjectFactory();
    assertNotNull(factory);
    URI uri = new URI("test");
    assertNotNull(factory.newEntitySet(uri));
    FullQualifiedName typeName = new FullQualifiedName("name.test");
    ClientEntity entity = new ClientEntityImpl(typeName );
    assertNotNull(factory.newDeepInsertEntity("test", entity));
    assertNotNull(factory.newEntity(typeName));
    assertNotNull(factory.newSingleton(typeName));
    assertNotNull(factory.newMediaEditLink("media", 
        uri, "image", "W/1"));
    assertNotNull(factory.newDelta(uri));
    assertNotNull(factory.newDelta());
  }
  
  @Test
  public void testAnnotation(){
    ClientValue val = new ClientCollectionValueImpl<ClientValue>("test");
    ClientAnnotationImpl annotation = new ClientAnnotationImpl("term", val);
    assertFalse(annotation.hasNullValue());
    assertNull(annotation.getPrimitiveValue());
    assertTrue(annotation.hasCollectionValue());
    assertFalse(annotation.hasComplexValue());
  }
  
  @Test
  public void testCollection(){
    ClientCollectionValueImpl val = new ClientCollectionValueImpl<ClientValue>("test");
    assertNull(val.asEnum());
    ClientCollectionValueImpl val2 = new ClientCollectionValueImpl<ClientValue>("test");
    assertTrue(val.equals(val2));
    assertNotNull(val.hashCode());
    assertNotNull(val.toString());
    val.add(val2);
    assertEquals(1, val.asJavaCollection().size());
  }
  
  @Test
  public void testComplex() throws URISyntaxException{
    ClientComplexValueImpl val = new ClientComplexValueImpl("test");
    ClientEntity entity = new ClientEntityImpl(new FullQualifiedName("name.test"));
    ClientLink link = new ClientInlineEntity(new URI("test"), ClientLinkType.ASSOCIATION,
        "title", entity );
    assertTrue(val.addLink(link ));
    assertNull(val.asEnum());
    assertTrue(val.removeLink(link));
    ClientComplexValueImpl val2 = new ClientComplexValueImpl("test");
    assertTrue(val.equals(val2));
    assertNotNull(val.hashCode());
    assertNotNull(val.toString());
  }  
  
  @Test
  public void testDeletedEntity() throws URISyntaxException {
    ClientDeletedEntityImpl val = new ClientDeletedEntityImpl();
    assertNotNull(val);
    val.setId(new URI("Id"));
    assertNotNull(val.getId());
    val.setReason(Reason.changed);
    assertNotNull(val.getReason());
    assertNotNull(val.hashCode());
    assertNotNull(val.toString());
    ClientDeletedEntityImpl val2 = new ClientDeletedEntityImpl();
    assertFalse(val.equals(val2));
  }
  
  @Test
  public void testDelta() throws URISyntaxException {
    ClientDeltaImpl val = new ClientDeltaImpl();
    ClientDeltaImpl val2 = new ClientDeltaImpl(new URI("Id"));
    assertNotNull(val);
    assertNotNull(val.getAddedLinks());
    assertNotNull(val.getDeletedLinks());
    assertNotNull(val.getDeletedEntities());
    assertNotNull(val.getAnnotations());
    assertNull(val.getContextURL());
    assertNull(val.getDeltaLink());
    assertNotNull(val.getEntities());
    assertNull(val.getLink());
    assertNull(val.getName());
    assertNull(val.getNext());
    assertNotNull(val.getOperations());
    assertNotNull(val.hashCode());
    assertNotNull(val.toString());
    assertFalse(val.equals(val2));
  }
  
  @Test
  public void testDeltaLink() throws URISyntaxException {
    ClientDeltaLinkImpl val = new ClientDeltaLinkImpl();
    ClientDeltaLinkImpl val2 = new ClientDeltaLinkImpl();
    assertNotNull(val);
    URI uri = new URI("test");
    val.setSource(uri );
    assertNotNull(val.getSource());
    val.setRelationship("Nav");
    assertNotNull(val.getRelationship());
    val.setTarget(uri);
    assertNotNull(val.getTarget());
    assertNotNull(val.getAnnotations());
    assertNull(val.getLink());
    assertNull(val.getName());
    assertNotNull(val.hashCode());
    assertNotNull(val.toString());
    assertFalse(val.equals(val2));
  }
  
  @Test
  public void testClientEntity() throws URISyntaxException {
    FullQualifiedName name = new FullQualifiedName("test.name");
    ClientEntityImpl val = new ClientEntityImpl(name );
    URI uri = new URI("test");
    assertNotNull(val);
    val.setId(new URI("Id"));
    assertNotNull(val.getId());
    assertNull(val.getETag());
    ClientEntity entity = new ClientEntityImpl(name);
    ClientLink link = new ClientInlineEntity(uri, ClientLinkType.ASSOCIATION,
        "title", entity);
    assertTrue(val.addLink(link ));
    assertNull(val.getLink());
    assertNotNull(val.getAssociationLink("title"));
    assertTrue(val.removeLink(link ));
    assertFalse(val.isReadOnly());
    assertNotNull(val.hashCode());
    assertNotNull(val.toString());
    ClientDeletedEntityImpl val2 = new ClientDeletedEntityImpl();
    assertFalse(val.equals(val2));
  }
  
  @Test
  public void testClientEntitySet() throws URISyntaxException {
    ClientEntitySetImpl val = new ClientEntitySetImpl();
    URI uri = new URI("test");
    ClientEntitySetImpl val2 = new ClientEntitySetImpl(uri);
    assertNotNull(val);
    val.setDeltaLink(uri);
    assertNotNull(val.getOperations());
    assertNull(val.getOperation("test"));
    assertNotNull(val.hashCode());
    assertNotNull(val.toString());
    assertFalse(val.equals(val2));
  }  
  
  @Test
  public void testClientEnumValue() {
    ClientEnumValueImpl val = new ClientEnumValueImpl("type", "value");
    ClientEnumValueImpl val2 = new ClientEnumValueImpl("type", "value");
    assertNotNull(val.hashCode());
    assertNotNull(val.toString());
    assertTrue(val.equals(val2));
  }
  
  @Test
  public void testClientPrimitiveValue() {
    ClientPrimitiveValueImpl val = new ClientPrimitiveValueImpl();
    ClientPrimitiveValueImpl val2 = new ClientPrimitiveValueImpl();
    BuilderImpl builder = new BuilderImpl();
    builder.setType(EdmPrimitiveTypeKind.Binary);
    assertNotNull(builder.buildBoolean(true));
    assertNotNull(builder);
    byte[] byteArray = new byte[2];
    assertNotNull(builder.buildBinary(byteArray));
    Short shortValue = new Short("1");
    assertNotNull(builder.buildInt16(shortValue));
    assertNotNull(builder.buildInt32(new Integer("1")));
    assertNotNull(builder.buildSingle(new Float("1")));
    assertNotNull(builder.buildDouble(new Double("1")));
    assertNotNull(builder.buildGuid(new UUID(1,1)));
    assertNotNull(builder.buildDecimal(new BigDecimal("1")));
    assertNotNull(builder.buildDuration(new BigDecimal("1")));
    assertNotNull(val.hashCode());
    assertNotNull(val.toString());
    assertTrue(val.equals(val2));
  }
  
  @Test
  public void testClientProperty() {
    ClientValue value = new ClientCollectionValueImpl<ClientValue>("type");
    ClientPropertyImpl val = new ClientPropertyImpl("type", value );
    ClientPropertyImpl val2 = new ClientPropertyImpl("type", value);
    assertNull(val.getOperation("type"));
    assertNotNull(val.getOperations());
    assertNotNull(val.hashCode());
    assertNotNull(val.toString());
    assertTrue(val.equals(val2));
  }
  
  @Test
  public void testClientValuable() {
    ClientValue value = new ClientCollectionValueImpl<ClientValue>("type");
    ClientValuableImpl val = new ClientValuableImpl(value);
    ClientValuableImpl val2 = new ClientValuableImpl(value);
    assertNotNull(val.hashCode());
    assertNotNull(val.toString());
    assertTrue(val.equals(val2));
  }
}
