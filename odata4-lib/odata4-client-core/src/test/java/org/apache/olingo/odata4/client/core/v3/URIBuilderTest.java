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
package org.apache.olingo.odata4.client.core.v3;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.olingo.odata4.client.api.ODataClient;
import org.apache.olingo.odata4.client.api.uri.URIBuilder;
import org.apache.olingo.odata4.client.core.AbstractTest;
import org.junit.Test;

public class URIBuilderTest extends AbstractTest {

  private static final String BASE_URI = "http://host/service";

  @Override
  protected ODataClient getClient() {
    return v3Client;
  }

  @Test
  public void metadata() throws URISyntaxException {
    final URI uri = getClient().getURIBuilder(BASE_URI).appendMetadataSegment().build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(BASE_URI + "/$metadata").build(), uri);
  }

  @Test
  public void entity() throws URISyntaxException {
    final URI uri = getClient().getURIBuilder(BASE_URI).appendEntitySetSegment("AnEntitySet").
            appendKeySegment(11).build();

    assertEquals(new org.apache.http.client.utils.URIBuilder(BASE_URI + "/AnEntitySet(11)").build(), uri);

    final Map<String, Object> multiKey = new HashMap<String, Object>();
    multiKey.put("OrderId", -10);
    multiKey.put("ProductId", -10);
    URIBuilder uriBuilder = getClient().getURIBuilder(BASE_URI).
            appendEntityTypeSegment("OrderLine").appendKeySegment(multiKey).
            appendStructuralSegment("Quantity").
            appendValueSegment();

    assertEquals(new org.apache.http.client.utils.URIBuilder(
            BASE_URI + "/OrderLine(OrderId=-10,ProductId=-10)/Quantity/$value").build(), uriBuilder.build());

    uriBuilder = getClient().getURIBuilder(BASE_URI).
            appendEntityTypeSegment("Customer").appendKeySegment(-10).select("CustomerId,Name,Orders").expand("Orders");
    assertEquals(new org.apache.http.client.utils.URIBuilder(
            BASE_URI + "/Customer(-10)").addParameter("$expand", "Orders").
            addParameter("$select", "CustomerId,Name,Orders").build(),
            uriBuilder.build());

    uriBuilder = getClient().getURIBuilder(BASE_URI).
            appendNavigationLinkSegment("Customer").appendKeySegment(-10).appendLinksSegment("Orders");
    assertEquals(new org.apache.http.client.utils.URIBuilder(BASE_URI + "/Customer(-10)/$links/Orders").build(),
            uriBuilder.build());
  }

  @Test
  public void filter() throws URISyntaxException {
    final URIBuilder uriBuilder = getClient().getURIBuilder(BASE_URI).appendEntitySetSegment("AnEntitySet").
            filter(getClient().getFilterFactory().lt("VIN", 16));

    assertEquals(new org.apache.http.client.utils.URIBuilder(BASE_URI + "/AnEntitySet").
            addParameter("$filter", "(VIN lt 16)").build(),
            uriBuilder.build());
  }

}
