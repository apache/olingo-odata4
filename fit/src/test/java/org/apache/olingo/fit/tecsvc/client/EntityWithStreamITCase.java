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
  
  /**
   * These tests can be uncommented once client API's are fixed for V4.01
   */
  /*@Test
  public void readExpandOfStreamPropOnComplexProperty() {
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESStreamOnComplexProp").appendKeySegment(7)
            .expand("PropertyCompWithStream/PropertyStream,"
                + "PropertyEntityStream,"
                + "PropertyCompWithStream/NavPropertyETStreamOnComplexPropOne($expand=PropertyStream),"
                + "PropertyCompWithStream/NavPropertyETStreamOnComplexPropMany/$count")
            .build());
    
    assertNotNull(request);
    setCookieHeader(request);
    request.addCustomHeader("OData-Version", "4.01");
    request.setAccept("application/json;odata.metadata=full");
    
    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("application/json; odata.metadata=full", response.getContentType());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);

    assertNotNull(entity.getProperties());
    assertEquals(10, entity.getProperties().size());

    ClientProperty property = entity.getProperty("PropertyEntityStream");
    assertNotNull(property);
    assertEquals(String.valueOf("eTag"), 
        entity.getProperty("PropertyEntityStream@mediaEtag").getPrimitiveValue().toValue());
    assertEquals(String.valueOf("image/jpeg"), 
        entity.getProperty("PropertyEntityStream@mediaContentType").getPrimitiveValue().toValue());
    assertEquals(String.valueOf("\ufffdioz\ufffd\"\ufffd"), 
        entity.getProperty("PropertyEntityStream").getPrimitiveValue().toValue());
    
    property = entity.getProperty("PropertyCompWithStream");
    assertNotNull(property);
    assertEquals(String.valueOf("eTag"), 
        property.getComplexValue().get("PropertyStream@mediaEtag").getPrimitiveValue().toValue());
    assertEquals(String.valueOf("image/jpeg"), 
        property.getComplexValue().get("PropertyStream@mediaContentType").getPrimitiveValue().toValue());
    assertEquals(String.valueOf("\ufffdioz\ufffd\"\ufffd"), 
        property.getComplexValue().get("PropertyStream").getPrimitiveValue().toValue());
    ClientComplexValue complexValue = property.getComplexValue();
    assertNotNull(complexValue);
    
    assertNotNull(complexValue.get("NavPropertyETStreamOnComplexPropOne@navigationLink"));
    assertNotNull(complexValue.get("NavPropertyETStreamOnComplexPropMany@navigationLink"));
    
    property = complexValue.get("NavPropertyETStreamOnComplexPropOne");
    assertNotNull(property);
    assertNotNull(property.getComplexValue());
    assertEquals(String.valueOf("eTag"), 
        property.getComplexValue().get("PropertyStream@mediaEtag").getPrimitiveValue().toValue());
    assertEquals(String.valueOf("image/jpeg"), 
        property.getComplexValue().get("PropertyStream@mediaContentType").getPrimitiveValue().toValue());
    assertEquals(String.valueOf("\ufffdioz\ufffd\"\ufffd"), 
        property.getComplexValue().get("PropertyStream").getPrimitiveValue().toValue());
    
    property = complexValue.get("NavPropertyETStreamOnComplexPropMany@count");
    assertNotNull(property);
    assertEquals(Integer.parseInt("2"), property.getPrimitiveValue().toValue());
  }
  
  @Test
  public void readExpandOfStreamPropOnComplexPropertyWithRef() {
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESStreamOnComplexProp").appendKeySegment(7)
            .expand("PropertyCompWithStream/NavPropertyETStreamOnComplexPropMany/$ref")
            .build());
    
    assertNotNull(request);
    setCookieHeader(request);
    request.addCustomHeader("OData-Version", "4.01");
    request.setAccept("application/json;odata.metadata=full");
    
    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("application/json; odata.metadata=full", response.getContentType());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);

    assertNotNull(entity.getProperties());
    assertEquals(9, entity.getProperties().size());

    ClientProperty property = entity.getProperty("PropertyEntityStream");
    assertNull(property);
    assertEquals(String.valueOf("eTag"), 
        entity.getProperty("PropertyEntityStream@mediaEtag").getPrimitiveValue().toValue());
    assertEquals(String.valueOf("image/jpeg"), 
        entity.getProperty("PropertyEntityStream@mediaContentType").getPrimitiveValue().toValue());
    assertEquals(String.valueOf("http://mediaserver:1234/editLink"), 
        entity.getProperty("PropertyEntityStream@mediaEditLink").getPrimitiveValue().toValue());
    
    property = entity.getProperty("PropertyCompWithStream");
    assertNotNull(property);
    assertEquals(String.valueOf("eTag"), 
        property.getComplexValue().get("PropertyStream@mediaEtag").getPrimitiveValue().toValue());
    assertEquals(String.valueOf("image/jpeg"), 
        property.getComplexValue().get("PropertyStream@mediaContentType").getPrimitiveValue().toValue());
    assertEquals(String.valueOf("http://mediaserver:1234/editLink"), 
        property.getComplexValue().get("PropertyStream@mediaEditLink").getPrimitiveValue().toValue());
    ClientComplexValue complexValue = property.getComplexValue();
    assertNotNull(complexValue);
    
    assertNotNull(complexValue.get("NavPropertyETStreamOnComplexPropOne@navigationLink"));
    assertNotNull(complexValue.get("NavPropertyETStreamOnComplexPropMany@navigationLink"));
    
    property = complexValue.get("NavPropertyETStreamOnComplexPropMany");
    assertNotNull(property);
    assertEquals(2, property.getCollectionValue().size());
    for (ClientValue value : property.getCollectionValue()) {
      assertEquals("id", value.asComplex().getAnnotations().get(0).getTerm());
    }
  }*/
}
