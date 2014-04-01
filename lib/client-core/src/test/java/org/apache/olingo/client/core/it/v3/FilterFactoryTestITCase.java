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
package org.apache.olingo.client.core.it.v3;

import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.api.uri.v3.FilterArgFactory;
import org.apache.olingo.client.api.uri.v3.FilterFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class FilterFactoryTestITCase extends AbstractTestITCase {

  private FilterFactory getFilterFactory() {
    return getClient().getFilterFactory();
  }

  private FilterArgFactory getFilterArgFactory() {
    return getFilterFactory().getArgFactory();
  }

  private void match(final String entitySet, final URIFilter filter, final int expected) {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment(entitySet).filter(filter);

    final ODataEntitySet feed = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build()).
            execute().getBody();
    assertNotNull(feed);
    assertEquals(expected, feed.getEntities().size());
  }

  @Test
  public void simple() {
    match("Car", getFilterFactory().lt("VIN", 16), 5);
  }

  @Test
  public void and() {
    final URIFilter filter =
            getFilterFactory().and(
            getFilterFactory().lt("VIN", 16),
            getFilterFactory().gt("VIN", 12));

    match("Car", filter, 3);
  }

  @Test
  public void not() {
    final URIFilter filter =
            getFilterFactory().not(
            getFilterFactory().or(
            getFilterFactory().ge("VIN", 16),
            getFilterFactory().le("VIN", 12)));

    match("Car", filter, 3);
  }

  @Test
  public void operator() {
    URIFilter filter =
            getFilterFactory().eq(
            getFilterArgFactory().add(getFilterArgFactory().property("VIN"), getFilterArgFactory().
            literal(1)),
            getFilterArgFactory().literal(16));

    match("Car", filter, 1);

    filter =
            getFilterFactory().eq(
            getFilterArgFactory().add(getFilterArgFactory().literal(1), getFilterArgFactory().
            property("VIN")),
            getFilterArgFactory().literal(16));

    match("Car", filter, 1);

    filter =
            getFilterFactory().eq(
            getFilterArgFactory().literal(16),
            getFilterArgFactory().add(getFilterArgFactory().literal(1), getFilterArgFactory().
            property("VIN")));

    match("Car", filter, 1);
  }

  @Test
  public void function() {
    final URIFilter filter =
            getFilterFactory().match(
            getFilterArgFactory().startswith(
            getFilterArgFactory().property("Description"), getFilterArgFactory().literal("cen")));

    match("Car", filter, 1);
  }

  @Test
  public void composed() {
    final URIFilter filter =
            getFilterFactory().gt(
            getFilterArgFactory().length(getFilterArgFactory().property("Description")),
            getFilterArgFactory().add(getFilterArgFactory().property("VIN"), getFilterArgFactory().literal(
            10)));

    match("Car", filter, 5);
  }

  @Test
  public void propertyPath() {
    URIFilter filter =
            getFilterFactory().eq(
            getFilterArgFactory().indexof(
            getFilterArgFactory().property("PrimaryContactInfo/HomePhone/PhoneNumber"),
            getFilterArgFactory().literal("ODataJClient")),
            getFilterArgFactory().literal(1));

    match("Customer", filter, 0);

    filter =
            getFilterFactory().ne(
            getFilterArgFactory().indexof(
            getFilterArgFactory().property("PrimaryContactInfo/HomePhone/PhoneNumber"),
            getFilterArgFactory().literal("lccvussrv")),
            getFilterArgFactory().literal(-1));

    match("Customer", filter, 2);
  }

  @Test
  public void datetime() {
    final URIFilter filter =
            getFilterFactory().eq(
            getFilterArgFactory().month(
            getFilterArgFactory().property("PurchaseDate")),
            getFilterArgFactory().literal(12));

    match("ComputerDetail", filter, 1);
  }

  @Test
  public void isof() {
    final URIFilter filter =
            getFilterFactory().match(
            getFilterArgFactory().isof(
            getFilterArgFactory().literal(
            "Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee")));

    match("Person", filter, 4);
  }
}
