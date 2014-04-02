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
package org.apache.olingo.client.core.uri.v3;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.client.core.uri.ParameterAlias;
import org.junit.Test;

public class URIBuilderTest extends AbstractTest {

  private static final String SERVICE_ROOT = "http://host/service";

  @Override
  protected ODataClient getClient() {
    return v3Client;
  }

  @Test
  public void metadata() throws URISyntaxException {
    final URI uri = getClient().getURIBuilder(SERVICE_ROOT).appendMetadataSegment().build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/$metadata").build(), uri);
  }

  @Test
  public void entity() throws URISyntaxException {
    final URI uri = getClient().getURIBuilder(SERVICE_ROOT).appendEntitySetSegment("AnEntitySet").
            appendKeySegment(11).build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/AnEntitySet(11)").build(), uri);

    final Map<String, Object> multiKey = new HashMap<String, Object>();
    multiKey.put("OrderId", -10);
    multiKey.put("ProductId", -10);
    URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendEntitySetSegment("OrderLine").appendKeySegment(multiKey).
            appendPropertySegment("Quantity").appendValueSegment();

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/OrderLine(OrderId=-10,ProductId=-10)/Quantity/$value").build(), uriBuilder.build());

    uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendEntitySetSegment("Customer").appendKeySegment(-10).
            select("CustomerId", "Name", "Orders").expand("Orders");
    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Customer(-10)").
            addParameter("$select", "CustomerId,Name,Orders").addParameter("$expand", "Orders").build(),
            uriBuilder.build());

    uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendEntitySetSegment("Customer").appendKeySegment(-10).appendLinksSegment("Orders");
    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Customer(-10)/$links/Orders").build(),
            uriBuilder.build());
  }

  @Test
  public void count() throws URISyntaxException {
    URI uri = getClient().getURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").count().build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Products/$count").build(), uri);

    uri = getClient().getURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").
            inlineCount(URIBuilder.InlineCount.allpages).build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Products").
            addParameter("$inlinecount", "allpages").build(), uri);
  }

  @Test
  public void filter() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).appendEntitySetSegment("AnEntitySet").
            filter(getClient().getFilterFactory().lt("VIN", 16));

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/AnEntitySet").
            addParameter("$filter", "(VIN lt 16)").build(),
            uriBuilder.build());
  }

  @Test
  public void filterWithParameter() throws URISyntaxException {
    // http://host/service.svc/Employees?$filter=Region eq @p1&@p1='WA'
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Employees").
            filter(getClient().getFilterFactory().eq("Region", new ParameterAlias("p1"))).
            addParameterAlias("p1", "'WA'");

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Employees").
            addParameter("$filter", "(Region eq @p1)").addParameter("@p1", "'WA'").build(),
            uriBuilder.build());
  }

  @Test
  public void unboundAction() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendOperationCallSegment("ProductsByCategoryId",
            Collections.<String, Object>singletonMap("categoryId", 2));

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/ProductsByCategoryId(categoryId=2)").build(), uriBuilder.build());
  }

  @Test
  public void boundAction() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendEntitySetSegment("Products").appendOperationCallSegment("MostExpensive", null);

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/Products/MostExpensive").build(), uriBuilder.build());
  }

  @Test
  public void derived() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendEntitySetSegment("Customers").appendNavigationSegment("Model").
            appendDerivedEntityTypeSegment("Namespace.VipCustomer").appendKeySegment(1);

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/Customers/Model/Namespace.VipCustomer(1)").build(), uriBuilder.build());
  }

  @Test
  public void expandMoreThenOnce() throws URISyntaxException {
    URI uri = getClient().getURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").appendKeySegment(5).
            expand("Orders", "Customers").expand("Info").build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Products(5)").
            addParameter("$expand", "Orders,Customers,Info").build(), uri);
  }

  @Test
  public void selectMoreThenOnce() throws URISyntaxException {
    URI uri = getClient().getURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Customers").appendKeySegment(5).
            select("Name", "Surname").expand("Info").select("Gender").build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Customers(5)").
            addParameter("$select", "Name,Surname,Gender").addParameter("$expand", "Info").build(), uri);
  }
}
