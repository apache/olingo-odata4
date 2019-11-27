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
package org.apache.olingo.client.core.communication.request.cud;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.olingo.client.api.communication.request.cud.CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.ODataClientImpl;
import org.apache.olingo.client.core.domain.ClientCollectionValueImpl;
import org.apache.olingo.client.core.domain.ClientComplexValueImpl;
import org.apache.olingo.client.core.domain.ClientEntityImpl;
import org.apache.olingo.client.core.domain.ClientPropertyImpl;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.junit.Test;

public class ODataRequestImplTest {

  @Test
  public void testdel() throws URISyntaxException{
    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("test");
    assertNotNull(client);
    CUDRequestFactory factory = client.getCUDRequestFactory();
    assertNotNull(factory);
    ODataDeleteRequestImpl del = (ODataDeleteRequestImpl) factory.getDeleteRequest(uri);
    assertNotNull(del);
    assertNotNull(del.getDefaultFormat());
    assertNull(del.getPayload());
    
    }
  
  @Test
  public void testcreate() throws URISyntaxException{
    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("test");
    assertNotNull(client);
    CUDRequestFactory factory = client.getCUDRequestFactory();
    assertNotNull(factory);
    FullQualifiedName fqn = new FullQualifiedName("test.entity");
    ClientEntity entity = new ClientEntityImpl(fqn );
    ODataEntityCreateRequestImpl create = (ODataEntityCreateRequestImpl) factory
        .getEntityCreateRequest(uri, entity);
    assertNotNull(create);
    assertNotNull(create.getDefaultFormat());
    assertNotNull(create.getPayload());
    }
  
  @Test
  public void testUpdate() throws URISyntaxException{
    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("test");
    assertNotNull(client);
    CUDRequestFactory factory = client.getCUDRequestFactory();
    assertNotNull(factory);
    FullQualifiedName fqn = new FullQualifiedName("test.entity");
    ClientEntity entity = new ClientEntityImpl(fqn );
    entity.setEditLink(uri);
    ODataEntityUpdateRequestImpl update = (ODataEntityUpdateRequestImpl) factory
        .getEntityUpdateRequest(UpdateType.PATCH, entity);
    assertNotNull(update);
    assertNotNull(update.getDefaultFormat());
    assertNotNull(update.getPayload());
    }
  
  @Test
  public void testUpdatePropColl() throws URISyntaxException{
    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("test");
    assertNotNull(client);
    CUDRequestFactory factory = client.getCUDRequestFactory();
    assertNotNull(factory);
    ClientValue value = new ClientCollectionValueImpl("properties");
    ClientProperty prop = new ClientPropertyImpl("property", value );
    ODataPropertyUpdateRequestImpl update = (ODataPropertyUpdateRequestImpl) factory
        .getPropertyCollectionValueUpdateRequest(uri, prop);
    assertNotNull(update);
    assertNotNull(update.getDefaultFormat());
    assertNotNull(update.getPayload());
    }
  
  @Test
  public void testUpdatePropComplex() throws URISyntaxException{
    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("test");
    assertNotNull(client);
    CUDRequestFactory factory = client.getCUDRequestFactory();
    assertNotNull(factory);
    ClientValue value = new ClientComplexValueImpl("complex");
    ClientProperty prop = new ClientPropertyImpl("property", value );
    ODataPropertyUpdateRequestImpl update = (ODataPropertyUpdateRequestImpl) factory
        .getPropertyComplexValueUpdateRequest(uri, UpdateType.PATCH, prop);
    assertNotNull(update);
    assertNotNull(update.getDefaultFormat());
    assertNotNull(update.getPayload());
    }
  
  @Test
  public void testUpdate2() throws URISyntaxException{
    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("test");
    assertNotNull(client);
    CUDRequestFactory factory = client.getCUDRequestFactory();
    assertNotNull(factory);
    FullQualifiedName fqn = new FullQualifiedName("test.entity");
    ClientEntity entity = new ClientEntityImpl(fqn );
    entity.setEditLink(uri);
    ODataEntityUpdateRequestImpl update = (ODataEntityUpdateRequestImpl) factory
        .getEntityUpdateRequest(uri, UpdateType.PATCH, entity);
    assertNotNull(update);
    assertNotNull(update.getDefaultFormat());
    assertNotNull(update.getPayload());
    }
  
  @Test
  public void testRef() throws URISyntaxException{
    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("test");
    assertNotNull(client);
    CUDRequestFactory factory = client.getCUDRequestFactory();
    assertNotNull(factory);
    FullQualifiedName fqn = new FullQualifiedName("test.entity");
    ClientEntity entity = new ClientEntityImpl(fqn );
    entity.setEditLink(uri);
    ODataReferenceAddingRequestImpl ref = (ODataReferenceAddingRequestImpl) factory
        .getReferenceAddingRequest(uri, uri, null);
    assertNotNull(ref);
    assertNotNull(ref.getDefaultFormat());
    assertNotNull(ref.getPayload());
    }

}
