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

import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class ExpandWithComplexPropertyITCase extends AbstractParamTecSvcITCase {

  @Test
  public void readExpandHavingComplexProperty1() {
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESCompMixPrimCollComp").appendKeySegment(1)
            .expand("PropertyMixedPrimCollComp/PropertyComp/NavPropertyETTwoKeyNavOne($expand=NavPropertySINav),"
                + "PropertyMixedPrimCollComp/PropertyComp/NavPropertyETMediaOne")
            .build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);

    assertNotNull(entity.getProperties());
    assertEquals(2, entity.getProperties().size());
    assertNotNull(entity.getProperty("PropertyMixedPrimCollComp"));

    ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(Integer.valueOf(1), property.getPrimitiveValue().toValue());

    property = entity.getProperty("PropertyMixedPrimCollComp");
    assertNotNull(property);
    ClientComplexValue complexValue = property.getComplexValue();
    assertNotNull(complexValue);
    property = complexValue.get("PropertyComp");
    assertNotNull(property);
    complexValue = property.getComplexValue();
    assertNotNull(complexValue);
    if (isJson()) {
      property = complexValue.get("NavPropertyETTwoKeyNavOne");
      assertNotNull(property);
      assertNotNull(complexValue.get("NavPropertyETMediaOne"));
      complexValue = property.getComplexValue();
      assertNotNull(complexValue);
      assertNotNull(complexValue.get("NavPropertySINav"));
    } else {
      ClientLink etkeyNavOneLink = complexValue.getNavigationLink("NavPropertyETTwoKeyNavOne");
      assertNotNull(etkeyNavOneLink);
      ClientEntity navEntity = etkeyNavOneLink.asInlineEntity().getEntity();
      assertNotNull(navEntity.getNavigationLink("NavPropertySINav"));
      assertNotNull(navEntity.getNavigationLink("NavPropertySINav").asInlineEntity().getEntity());
      assertNotNull(complexValue.getNavigationLink("NavPropertyETMediaOne"));
    }
  }
  
  @Test
  public void readExpandHavingComplexProperty2() {
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESCompMixPrimCollComp").appendKeySegment(1)
            .expand("PropertyMixedPrimCollComp/NavPropertyETTwoKeyNavOne,"
                + "PropertyMixedPrimCollComp/PropertyComp/NavPropertyETMediaOne")
            .build());
    assertNotNull(request);
    setCookieHeader(request);
    if (isJson()) {
      request.setAccept("application/json;odata.metadata=full");
    }

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);

    assertNotNull(entity.getProperties());
    assertEquals(2, entity.getProperties().size());
    assertNotNull(entity.getProperty("PropertyMixedPrimCollComp"));

    ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(Integer.valueOf(1), property.getPrimitiveValue().toValue());

    property = entity.getProperty("PropertyMixedPrimCollComp");
    assertNotNull(property);
    ClientComplexValue complexValue = property.getComplexValue();
    assertNotNull(complexValue);
    ClientLink etkeyNavLink = complexValue.getNavigationLink("NavPropertyETTwoKeyNavOne");
    assertNotNull(etkeyNavLink);
    assertNotNull(etkeyNavLink.asInlineEntity().getEntity());
    property = complexValue.get("PropertyComp");
    assertNotNull(property);
    complexValue = property.getComplexValue();
    assertNotNull(complexValue);
    ClientLink etMediaOneNav = complexValue.getNavigationLink("NavPropertyETMediaOne");
    assertNotNull(etMediaOneNav);
    assertNotNull(etMediaOneNav.asInlineEntity().getEntity());
  }
  
  @Test
  public void readExpandHavingComplexProperty3() {
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESCompMixPrimCollComp").appendKeySegment(1)
            .expand("PropertyMixedPrimCollComp/NavPropertyETTwoKeyNavOne,"
                + "PropertyMixedPrimCollComp/PropertyComp/NavPropertyETMediaOne,"
                + "PropertyMixedPrimCollComp/PropertyComp/NavPropertyETTwoKeyNavOne")
            .build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);

    assertNotNull(entity.getProperties());
    assertEquals(2, entity.getProperties().size());
    assertNotNull(entity.getProperty("PropertyMixedPrimCollComp"));

    ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(Integer.valueOf(1), property.getPrimitiveValue().toValue());

    property = entity.getProperty("PropertyMixedPrimCollComp");
    assertNotNull(property);
    ClientComplexValue complexValue = property.getComplexValue();
    assertNotNull(complexValue);
    if (isJson()) {
      assertNotNull(complexValue.get("NavPropertyETTwoKeyNavOne"));
    } else {
      assertNotNull(complexValue.getNavigationLink("NavPropertyETTwoKeyNavOne"));
    }
    property = complexValue.get("PropertyComp");
    assertNotNull(property);
    complexValue = property.getComplexValue();
    assertNotNull(complexValue);
    if (isJson()) {
      assertNotNull(complexValue.get("NavPropertyETMediaOne"));
      assertNotNull(complexValue.get("NavPropertyETTwoKeyNavOne"));
    } else {
      assertNotNull(complexValue.getNavigationLink("NavPropertyETMediaOne"));
      assertNotNull(complexValue.getNavigationLink("NavPropertyETMediaOne").asInlineEntity().getEntity());
      assertNotNull(complexValue.getNavigationLink("NavPropertyETTwoKeyNavOne"));
      assertNotNull(complexValue.getNavigationLink("NavPropertyETTwoKeyNavOne").asInlineEntity().getEntity());
    }
  }
  
  @Test
  public void readExpandHavingCollComplexProperty1() {
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESCompMixPrimCollComp").appendKeySegment(1)
            .expand("PropertyMixedPrimCollComp/CollPropertyComp/NavPropertyETTwoKeyNavOne")
            .build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);

    assertNotNull(entity.getProperties());
    assertEquals(2, entity.getProperties().size());
    assertNotNull(entity.getProperty("PropertyMixedPrimCollComp"));

    ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(Integer.valueOf(1), property.getPrimitiveValue().toValue());

    property = entity.getProperty("PropertyMixedPrimCollComp");
    assertNotNull(property);
    ClientComplexValue complexValue = property.getComplexValue();
    assertNotNull(complexValue);
    property = complexValue.get("CollPropertyComp");
    assertNotNull(property);
    ClientCollectionValue<ClientValue> complexValues = property.getCollectionValue();
    assertNotNull(complexValues);
    for (ClientValue value : complexValues) {
      if (isJson()) {
        ClientComplexValue innerComplexValue = value.asComplex();
        assertNotNull(innerComplexValue.get("NavPropertyETTwoKeyNavOne"));
        property = innerComplexValue.get("NavPropertyETTwoKeyNavOne");
        assertEquals(8, property.getValue().asComplex().size());
      } else {
        ClientComplexValue innerComplexValue = value.asComplex();
        ClientLink link = innerComplexValue.getNavigationLink("NavPropertyETTwoKeyNavOne");
        assertNotNull(link);
        assertNotNull(link.asInlineEntity());
        assertEquals(8, link.asInlineEntity().getEntity().getProperties().size());
      }
    }
  }
  
  @Test
  public void readExpandHavingCollComplexProperty2() {
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESMixPrimCollComp").appendKeySegment(32767)
            .expand("CollPropertyComp/NavPropertyETTwoKeyNavOne,PropertyComp/NavPropertyETTwoKeyNavOne")
            .build());
    assertNotNull(request);
    setCookieHeader(request);
    
    if (isJson()) {
      request.setAccept("application/json;odata.metadata=full");
    }

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);

    assertNotNull(entity.getProperties());
    assertEquals(4, entity.getProperties().size());
    assertNotNull(entity.getProperty("CollPropertyComp"));

    ClientProperty property = entity.getProperty("CollPropertyComp");
    assertNotNull(property);
    ClientCollectionValue<ClientValue> complexValues = property.getCollectionValue();
    assertNotNull(complexValues);
    assertEquals(3, complexValues.size());
    for (ClientValue value : complexValues) {
      ClientComplexValue complexValue = value.asComplex();
      ClientLink link = complexValue.getNavigationLink("NavPropertyETTwoKeyNavOne");
      assertNotNull(link);
      assertNotNull(link.asInlineEntity());
      assertEquals(8, link.asInlineEntity().getEntity().getProperties().size());
    }
    property = entity.getProperty("PropertyComp");
    assertNotNull(property);
    ClientComplexValue complexValue = property.getComplexValue();
    assertNotNull(complexValue);
    ClientLink link = complexValue.getNavigationLink("NavPropertyETTwoKeyNavOne");
    assertNotNull(link);
    assertNotNull(link.asInlineEntity());
    assertEquals(8, link.asInlineEntity().getEntity().getProperties().size());
  }
  
  @Test
  public void readExpandHavingCollComplexPropertyWith$ref$count() {
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESCompMixPrimCollComp").appendKeySegment(1)
            .expand("PropertyMixedPrimCollComp/PropertyComp/NavPropertyETTwoKeyNavOne/$ref,"
                + "PropertyMixedPrimCollComp/PropertyComp/NavPropertyETMediaOne/$ref,"
                + "PropertyMixedPrimCollComp/NavPropertyETTwoKeyNavMany/$count")
            .build());
    assertNotNull(request);
    setCookieHeader(request);
    
    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);

    assertNotNull(entity.getProperties());
    assertEquals(2, entity.getProperties().size());

    ClientProperty property = entity.getProperty("PropertyMixedPrimCollComp");
    assertNotNull(property);
    ClientComplexValue complexValue = property.getComplexValue();
    assertNotNull(complexValue);
    ClientLink link = null;
    if (isJson()) {
      property = complexValue.get("NavPropertyETTwoKeyNavMany@odata.count");
      assertNotNull(property.getPrimitiveValue());
      assertEquals(Integer.valueOf(2), property.getPrimitiveValue().toValue());
    } else {
      link = complexValue.getNavigationLink("NavPropertyETTwoKeyNavMany");
      assertNotNull(link);
      assertNotNull(link.getLink());
      assertNotNull(link.asInlineEntitySet().getEntitySet());
      assertEquals(Integer.valueOf(2), link.asInlineEntitySet().getEntitySet().getCount());
    }
    
    property = complexValue.get("PropertyComp");
    assertNotNull(property);
    ClientComplexValue innerComplexValue = property.getComplexValue();
    assertNotNull(innerComplexValue);
    if (isJson()) {
      property = innerComplexValue.get("NavPropertyETTwoKeyNavOne");
      assertNotNull(property);
      assertEquals("odata.id", property.getComplexValue().getAnnotations().get(0).getTerm());
      assertEquals(String.valueOf("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"), 
          property.getComplexValue().getAnnotations().get(0).getPrimitiveValue().toValue());
      property = innerComplexValue.get("NavPropertyETMediaOne");
      assertNotNull(property);
      assertEquals("odata.id", property.getComplexValue().getAnnotations().get(3).getTerm());
      assertEquals(String.valueOf("ESMedia(2)"), 
          property.getComplexValue().getAnnotations().get(3).getPrimitiveValue().toValue());
    } else {
      link = innerComplexValue.getNavigationLink("NavPropertyETTwoKeyNavOne");
      assertNotNull(link);
      assertNotNull(link.getLink());
      link = innerComplexValue.getNavigationLink("NavPropertyETMediaOne");
      assertNotNull(link);
      assertNotNull(link.getLink());
    }
  }
}
