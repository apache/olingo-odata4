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
package org.apache.olingo.fit.tecsvc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientLinkType;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Test;
import org.junit.runners.Parameterized;

public class EntityWithStreamITCase extends AbstractParamTecSvcITCase {
  private static final ContentType CONTENT_TYPE_JSON_FULL_METADATA =
      ContentType.create(ContentType.JSON, ContentType.PARAMETER_ODATA_METADATA, 
          ContentType.VALUE_ODATA_METADATA_FULL);
  private static final String PROPERTY_INT16 = "PropertyInt16";

  @Parameterized.Parameters(name = "{0}")
  public static List<ContentType[]> parameters() {
    ContentType[] a = new ContentType[1];
    a[0] = CONTENT_TYPE_JSON_FULL_METADATA;
    ArrayList<ContentType[]> type = new ArrayList<ContentType[]>();
    type.add(a);
    return type;
  }  
  
  @Test
  public void readEntitySetWithStreamProperty() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESWithStream").build());    
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("application/json; odata.metadata=full", response.getContentType());

    final ClientEntitySet entitySet = response.getBody();
    assertNotNull(entitySet);

    assertNull(entitySet.getCount());
    assertNull(entitySet.getNext());
    assertEquals(Collections.<ClientAnnotation> emptyList(), entitySet.getAnnotations());
    assertNull(entitySet.getDeltaLink());

    final List<ClientEntity> entities = entitySet.getEntities();
    assertNotNull(entities);
    assertEquals(2, entities.size());
    
    ClientEntity entity = entities.get(0);
    assertNotNull(entity);
    ClientProperty property = entity.getProperty(PROPERTY_INT16);
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(Short.MAX_VALUE, property.getPrimitiveValue().toValue());
    
    ClientLink link = entity.getMediaEditLinks().get(0);
    assertNotNull(link);
    
    assertEquals("/readLink", link.getLink().toASCIIString());
    assertEquals(ClientLinkType.MEDIA_READ, link.getType());
    
    entity = entities.get(1);
    assertNotNull(entity);
    property = entity.getProperty(PROPERTY_INT16);
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(7, property.getPrimitiveValue().toValue());
    
    assertEquals(1, entity.getMediaEditLinks().size());
    
    link = entity.getMediaEditLinks().get(0);
    assertNotNull(link);
    assertEquals("http://mediaserver:1234/editLink", link.getLink().toASCIIString());
    assertEquals(ClientLinkType.fromString(Constants.NS_MEDIA_EDIT_LINK_REL, "image/jpeg").name(), 
        link.getType().name());
    assertEquals("eTag", link.getMediaETag());    
  } 
}
