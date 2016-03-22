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
package org.apache.olingo.client.core.uri;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.AbstractTest;
import org.junit.Test;

public class URIBuilderTest extends AbstractTest {

  private static final String SERVICE_ROOT = "http://host/service";

  @Test
  public void metadata() throws URISyntaxException {
    final URI uri = client.newURIBuilder(SERVICE_ROOT).appendMetadataSegment().build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/$metadata").build(), uri);
  }

  @Test
  public void entity() throws URISyntaxException {
    final URI uri = client.newURIBuilder(SERVICE_ROOT).appendEntitySetSegment("AnEntitySet").
        appendKeySegment(11).build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/AnEntitySet(11)").build(), uri);

    final Map<String, Object> multiKey = new LinkedHashMap<String, Object>();
    multiKey.put("OrderId", -10);
    multiKey.put("ProductId", -10);
    URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendEntitySetSegment("OrderLine").appendKeySegment(multiKey).
        appendPropertySegment("Quantity").appendValueSegment();

    assertEquals(new org.apache.http.client.utils.URIBuilder(
        SERVICE_ROOT + "/OrderLine(OrderId=-10,ProductId=-10)/Quantity/$value").build(), uriBuilder.build());

    uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendEntitySetSegment("Customer").appendKeySegment(-10).
        select("CustomerId", "Name", "Orders").expand("Orders");
    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Customer(-10)").
        addParameter("$select", "CustomerId,Name,Orders").addParameter("$expand", "Orders").build(),
        uriBuilder.build());

    uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendEntitySetSegment("Customer").appendKeySegment(-10).appendNavigationSegment("Orders").appendRefSegment();
    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Customer(-10)/Orders/$ref").build(),
        uriBuilder.build());
  }

  @Test
  public void expandWithOptions() throws URISyntaxException {
    final URI uri = client.newURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").appendKeySegment(5).
        expandWithOptions("ProductDetails", new LinkedHashMap<QueryOption, Object>() {
          private static final long serialVersionUID = 3109256773218160485L;

          {
            put(QueryOption.EXPAND, "ProductInfo");
            put(QueryOption.SELECT, "Price");
          }
        }).expand("Orders", "Customers").build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Products(5)").
        addParameter("$expand", "ProductDetails($expand=ProductInfo;$select=Price),Orders,Customers").build(), uri);
  }
  
  @Test
  public void expandWithOptionsCount() throws URISyntaxException {
    final URI uri = client.newURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").appendKeySegment(5).
        expandWithOptions("ProductDetails", false, true, new LinkedHashMap<QueryOption, Object>() {
          private static final long serialVersionUID = 3109256773218160485L;
          {
            put(QueryOption.EXPAND, "ProductInfo");
            put(QueryOption.SELECT, "Price");
          }
        }).expand("Orders", "Customers").build();
    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Products(5)").
        addParameter("$expand", "ProductDetails($expand=ProductInfo;$select=Price)/$count,Orders,Customers")
        .build(), uri);
  }  

  public void expandWithLevels() throws URISyntaxException {
    final URI uri = client.newURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").appendKeySegment(1).
        expandWithOptions("Customer", Collections.<QueryOption, Object> singletonMap(QueryOption.LEVELS, 4)).
        build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Products(1)").
        addParameter("$expand", "Customer($levels=4)").build(), uri);
  }

  @Test
  public void count() throws URISyntaxException {
    URI uri = client.newURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").count().build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Products/$count").build(), uri);

    uri = client.newURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").count(true).build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Products").
        addParameter("$count", "true").build(), uri);
  }

  @Test
  public void filter() throws URISyntaxException {
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).appendEntitySetSegment("AnEntitySet").
        filter(client.getFilterFactory().lt("VIN", 16));

    assertEquals("http://host/service/AnEntitySet?%24filter=%28VIN%20lt%2016%29", uriBuilder.build().toASCIIString());

    //    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/AnEntitySet").
    //        addParameter("$filter", "(VIN lt 16)").build(),
    //        uriBuilder.build());
  }

  @Test
  public void filterWithParameter() throws URISyntaxException {
    // http://host/service.svc/Employees?$filter=Region eq @p1&@p1='WA'
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Employees").
        filter(client.getFilterFactory().eq("Region", new ParameterAlias("p1"))).
        addParameterAlias("p1", "'WA'");

    assertEquals("http://host/service/Employees?%24filter=%28Region%20eq%20%40p1%29&%40p1='WA'", uriBuilder.build()
        .toASCIIString());

    //    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Employees").
    //        addParameter("$filter", "(Region eq @p1)").addParameter("@p1", "'WA'").build(),
    //        uriBuilder.build());
  }

  @Test
  public void expandMoreThenOnce() throws URISyntaxException {
    URI uri = client.newURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").appendKeySegment(5).
        expand("Orders", "Customers").expand("Info").build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Products(5)").
        addParameter("$expand", "Orders,Customers,Info").build(), uri);
  }

  @Test
  public void selectMoreThenOnce() throws URISyntaxException {
    URI uri = client.newURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Customers").appendKeySegment(5).
        select("Name", "Surname").expand("Info").select("Gender").build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Customers(5)").
        addParameter("$select", "Name,Surname,Gender").addParameter("$expand", "Info").build(), uri);
  }

  @Test
  public void singleton() throws URISyntaxException {
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendSingletonSegment("BestProductEverCreated");

    assertEquals(new org.apache.http.client.utils.URIBuilder(
        SERVICE_ROOT + "/BestProductEverCreated").build(), uriBuilder.build());
  }

  @Test
  public void entityId() throws URISyntaxException {
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendEntityIdSegment("Products(0)");

    assertEquals(new org.apache.http.client.utils.URIBuilder(
        SERVICE_ROOT + "/$entity").addParameter("$id", "Products(0)").build(), uriBuilder.build());
  }

  @Test
  public void boundAction() throws URISyntaxException {
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendEntitySetSegment("Categories").appendKeySegment(1).
        appendNavigationSegment("Products").
        appendActionCallSegment("Model.AllOrders");

    assertEquals(new org.apache.http.client.utils.URIBuilder(
        SERVICE_ROOT + "/Categories(1)/Products/Model.AllOrders").build(), uriBuilder.build());
  }

  @Test
  public void boundOperation() throws URISyntaxException {
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendEntitySetSegment("Categories").appendKeySegment(1).
        appendNavigationSegment("Products").
        appendOperationCallSegment("Model.AllOrders");

    assertEquals(new org.apache.http.client.utils.URIBuilder(
        SERVICE_ROOT + "/Categories(1)/Products/Model.AllOrders()").build(), uriBuilder.build());
  }

  @Test
  public void ref() throws URISyntaxException {
    URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendEntitySetSegment("Categories").appendKeySegment(1).
        appendNavigationSegment("Products").appendRefSegment();

    assertEquals(new org.apache.http.client.utils.URIBuilder(
        SERVICE_ROOT + "/Categories(1)/Products/$ref").build(), uriBuilder.build());

    uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendEntitySetSegment("Categories").appendKeySegment(1).
        appendNavigationSegment("Products").appendRefSegment().id("../../Products(0)");

    assertEquals(new org.apache.http.client.utils.URIBuilder(
        SERVICE_ROOT + "/Categories(1)/Products/$ref").addParameter("$id", "../../Products(0)").build(),
        uriBuilder.build());
  }

  @Test
  public void derived() throws URISyntaxException {
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendEntitySetSegment("Customers").appendDerivedEntityTypeSegment("Model.VipCustomer").appendKeySegment(1);

    assertEquals(new org.apache.http.client.utils.URIBuilder(
        SERVICE_ROOT + "/Customers/Model.VipCustomer(1)").build(), uriBuilder.build());
  }

  @Test
  public void crossjoin() throws URISyntaxException {
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendCrossjoinSegment("Products", "Sales");

    assertEquals(new org.apache.http.client.utils.URIBuilder(
        SERVICE_ROOT + "/$crossjoin(Products,Sales)").build(), uriBuilder.build());
  }

  @Test
  public void all() throws URISyntaxException {
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).appendAllSegment();

    assertEquals(new org.apache.http.client.utils.URIBuilder(
        SERVICE_ROOT + "/$all").build(), uriBuilder.build());
  }

  @Test
  public void search() throws URISyntaxException {
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_ROOT).
        appendEntitySetSegment("Products").search("blue OR green");

    assertEquals(new URI("http://host/service/Products?%24search=blue%20OR%20green"), uriBuilder.build());
  }
}
