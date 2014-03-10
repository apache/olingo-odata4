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
package org.apache.olingo.client.core.v3;

import org.apache.olingo.client.api.ODataV3Client;
import org.apache.olingo.client.api.uri.filter.URIFilter;
import org.apache.olingo.client.api.uri.filter.V3FilterArgFactory;
import org.apache.olingo.client.api.uri.filter.V3FilterFactory;
import org.apache.olingo.client.core.AbstractTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FilterFactoryTest extends AbstractTest {

  @Override
  protected ODataV3Client getClient() {
    return v3Client;
  }

  private V3FilterFactory getFilterFactory() {
    return getClient().getFilterFactory();
  }

  private V3FilterArgFactory getFilterArgFactory() {
    return getFilterFactory().getArgFactory();
  }

  @Test
  public void simple() {
    final URIFilter filter = getFilterFactory().lt("VIN", 16);
    assertEquals("(VIN lt 16)", filter.build());
  }

  @Test
  public void _null() {
    final URIFilter filter = getFilterFactory().eq(
            getFilterArgFactory().property("NullValue"), getFilterArgFactory()._null());

    assertEquals("(NullValue eq null)", filter.build());
  }

  @Test
  public void and() {
    final URIFilter filter = getFilterFactory().and(
            getFilterFactory().lt("VIN", 16),
            getFilterFactory().gt("VIN", 12));

    assertEquals("((VIN lt 16) and (VIN gt 12))", filter.build());
  }

  @Test
  public void not() {
    final URIFilter filter = getFilterFactory().not(
            getFilterFactory().or(
                    getFilterFactory().ge("VIN", 16),
                    getFilterFactory().le("VIN", 12)));

    assertEquals("not (((VIN ge 16) or (VIN le 12)))", filter.build());
  }

  @Test
  public void operator() {
    URIFilter filter = getFilterFactory().eq(
            getFilterArgFactory().add(
                    getFilterArgFactory().property("VIN"),
                    getFilterArgFactory().literal(1)),
            getFilterArgFactory().literal(16));

    assertEquals("((VIN add 1) eq 16)", filter.build());

    filter = getFilterFactory().eq(
            getFilterArgFactory().add(
                    getFilterArgFactory().literal(1),
                    getFilterArgFactory().property("VIN")),
            getFilterArgFactory().literal(16));

    assertEquals("((1 add VIN) eq 16)", filter.build());

    filter = getFilterFactory().eq(
            getFilterArgFactory().literal(16),
            getFilterArgFactory().add(
                    getFilterArgFactory().literal(1),
                    getFilterArgFactory().property("VIN")));

    assertEquals("(16 eq (1 add VIN))", filter.build());
  }

  @Test
  public void function() {
    final URIFilter filter = getFilterFactory().match(
            getFilterArgFactory().startswith(
                    getFilterArgFactory().property("Description"),
                    getFilterArgFactory().literal("cen")));

    assertEquals("startswith(Description,'cen')", filter.build());
  }

  @Test
  public void composed() {
    final URIFilter filter = getFilterFactory().gt(
            getFilterArgFactory().length(
                    getFilterArgFactory().property("Description")),
            getFilterArgFactory().add(
                    getFilterArgFactory().property("VIN"),
                    getFilterArgFactory().literal(10)));

    assertEquals("(length(Description) gt (VIN add 10))", filter.build());
  }

  @Test
  public void propertyPath() {
    URIFilter filter = getFilterFactory().eq(
            getFilterArgFactory().indexof(
                    getFilterArgFactory().property("PrimaryContactInfo/HomePhone/PhoneNumber"),
                    getFilterArgFactory().literal("ODataJClient")),
            getFilterArgFactory().literal(1));

    assertEquals("(indexof(PrimaryContactInfo/HomePhone/PhoneNumber,'ODataJClient') eq 1)", filter.build());

    filter = getFilterFactory().ne(
            getFilterArgFactory().indexof(
                    getFilterArgFactory().property("PrimaryContactInfo/HomePhone/PhoneNumber"),
                    getFilterArgFactory().literal("lccvussrv")),
            getFilterArgFactory().literal(-1));

    assertEquals("(indexof(PrimaryContactInfo/HomePhone/PhoneNumber,'lccvussrv') ne -1)", filter.build());
  }

  @Test
  public void datetime() {
    final URIFilter filter = getFilterFactory().eq(
            getFilterArgFactory().month(
                    getFilterArgFactory().property("PurchaseDate")),
            getFilterArgFactory().literal(12));

    assertEquals("(month(PurchaseDate) eq 12)", filter.build());
  }

  @Test
  public void isof() {
    final URIFilter filter = getFilterFactory().match(
            getFilterArgFactory().isof(
                    getFilterArgFactory().literal(
                            "Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee")));

    assertEquals("isof('Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee')", filter.build());
  }

}
