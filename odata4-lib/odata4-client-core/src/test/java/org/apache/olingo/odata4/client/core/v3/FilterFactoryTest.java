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

import org.apache.olingo.odata4.client.api.ODataClient;
import org.apache.olingo.odata4.client.api.uri.filter.URIFilter;
import org.apache.olingo.odata4.client.core.AbstractTest;
import org.apache.olingo.odata4.client.core.uri.filter.FilterArgFactory;
import org.junit.Test;

public class FilterFactoryTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v3Client;
  }

  @Test
  public void simple() {
    final URIFilter filter = getClient().getFilterFactory().lt("VIN", 16);
    assertEquals("(VIN lt 16)", filter.build());
  }

  @Test
  public void and() {
    final URIFilter filter = getClient().getFilterFactory().and(
            getClient().getFilterFactory().lt("VIN", 16),
            getClient().getFilterFactory().gt("VIN", 12));

    assertEquals("((VIN lt 16) and (VIN gt 12))", filter.build());
  }

  @Test
  public void not() {
    final URIFilter filter = getClient().getFilterFactory().not(
            getClient().getFilterFactory().or(
                    getClient().getFilterFactory().ge("VIN", 16),
                    getClient().getFilterFactory().le("VIN", 12)));

    assertEquals("not (((VIN ge 16) or (VIN le 12)))", filter.build());
  }

  @Test
  public void operator() {
    URIFilter filter = getClient().getFilterFactory().eq(
            FilterArgFactory.add(FilterArgFactory.property("VIN"), FilterArgFactory.literal(1)),
            FilterArgFactory.literal(16));

    assertEquals("((VIN add 1) eq 16)", filter.build());

    filter = getClient().getFilterFactory().eq(
            FilterArgFactory.add(FilterArgFactory.literal(1), FilterArgFactory.property("VIN")),
            FilterArgFactory.literal(16));

    assertEquals("((1 add VIN) eq 16)", filter.build());

    filter = getClient().getFilterFactory().eq(
            FilterArgFactory.literal(16),
            FilterArgFactory.add(FilterArgFactory.literal(1), FilterArgFactory.property("VIN")));

    assertEquals("(16 eq (1 add VIN))", filter.build());
  }

  @Test
  public void function() {
    final URIFilter filter = getClient().getFilterFactory().match(
            FilterArgFactory.startswith(FilterArgFactory.property("Description"), FilterArgFactory.literal("cen")));

    assertEquals("startswith(Description,'cen')", filter.build());
  }

  @Test
  public void composed() {
    final URIFilter filter = getClient().getFilterFactory().gt(
            FilterArgFactory.length(FilterArgFactory.property("Description")),
            FilterArgFactory.add(FilterArgFactory.property("VIN"), FilterArgFactory.literal(10)));

    assertEquals("(length(Description) gt (VIN add 10))", filter.build());
  }

  @Test
  public void propertyPath() {
    URIFilter filter = getClient().getFilterFactory().eq(
            FilterArgFactory.indexof(
                    FilterArgFactory.property("PrimaryContactInfo/HomePhone/PhoneNumber"),
                    FilterArgFactory.literal("ODataJClient")),
            FilterArgFactory.literal(1));

    assertEquals("(indexof(PrimaryContactInfo/HomePhone/PhoneNumber,'ODataJClient') eq 1)", filter.build());

    filter = getClient().getFilterFactory().ne(
            FilterArgFactory.indexof(
                    FilterArgFactory.property("PrimaryContactInfo/HomePhone/PhoneNumber"),
                    FilterArgFactory.literal("lccvussrv")),
            FilterArgFactory.literal(-1));

    assertEquals("(indexof(PrimaryContactInfo/HomePhone/PhoneNumber,'lccvussrv') ne -1)", filter.build());
  }

  @Test
  public void datetime() {
    final URIFilter filter = getClient().getFilterFactory().eq(
            FilterArgFactory.month(FilterArgFactory.property("PurchaseDate")),
            FilterArgFactory.literal(12));

    assertEquals("(month(PurchaseDate) eq 12)", filter.build());
  }

  @Test
  public void isof() {
    final URIFilter filter = getClient().getFilterFactory().match(
            FilterArgFactory.isof(
                    FilterArgFactory.literal("Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee")));

    assertEquals("isof('Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee')", filter.build());
  }

}
