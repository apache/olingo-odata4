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
package org.apache.olingo.client.core.uri.v4;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.core.AbstractTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class URIBuilderTest extends AbstractTest {

  private static final String SERVICE_ROOT = "http://host/service";

  @Override
  protected ODataClient getClient() {
    return v4Client;
  }

  @Test
  public void count() throws URISyntaxException {
    URI uri = getClient().getURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").count().build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Products/$count").build(), uri);

    uri = getClient().getURIBuilder(SERVICE_ROOT).appendEntitySetSegment("Products").count(true).build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(SERVICE_ROOT + "/Products").
            addParameter("$count", "true").build(), uri);
  }

  @Test
  public void singleton() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendSingletonSegment("BestProductEverCreated");

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/BestProductEverCreated").build(), uriBuilder.build());
  }

  @Test
  public void entityId() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendEntityIdSegment("Products(0)");

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/$entity").addParameter("$id", "Products(0)").build(), uriBuilder.build());
  }

  @Test
  public void boundAction() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendEntitySetSegment("Categories").appendKeySegment(1).
            appendNavigationSegment("Products").appendNavigationSegment("Model").
            appendOperationCallSegment("AllOrders", null);

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/Categories(1)/Products/Model.AllOrders()").build(), uriBuilder.build());
  }

  @Test
  public void ref() throws URISyntaxException {
    URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendEntitySetSegment("Categories").appendKeySegment(1).
            appendNavigationSegment("Products").appendRefSegment();

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/Categories(1)/Products/$ref").build(), uriBuilder.build());

    uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendEntitySetSegment("Categories").appendKeySegment(1).
            appendNavigationSegment("Products").appendRefSegment().id("../../Products(0)");

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/Categories(1)/Products/$ref").addParameter("$id", "../../Products(0)").build(),
            uriBuilder.build());
  }

  @Test
  public void derived() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendEntitySetSegment("Customers").appendNavigationSegment("Model").
            appendDerivedEntityTypeSegment("VipCustomer").appendKeySegment(1);

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/Customers/Model.VipCustomer(1)").build(), uriBuilder.build());
  }

  @Test
  public void crossjoin() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendCrossjoinSegment("Products", "Sales");

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/$crossjoin(Products,Sales)").build(), uriBuilder.build());
  }

  @Test
  public void all() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).appendAllSegment();

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/$all").build(), uriBuilder.build());
  }

  @Test
  public void search() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(SERVICE_ROOT).
            appendEntitySetSegment("Products").search("blue OR green");

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            SERVICE_ROOT + "/Products").addParameter("$search", "blue OR green").build(), uriBuilder.build());
  }

}
