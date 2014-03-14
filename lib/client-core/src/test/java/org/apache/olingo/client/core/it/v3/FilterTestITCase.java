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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.apache.olingo.client.api.domain.ODataEntitySet;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.junit.Test;

public class FilterTestITCase extends AbstractV3TestITCase {

  private void filterQueryTest(final String entity, final String filter, final int expected) {
    final URIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment(entity).filter(filter);
    final ODataEntitySet entitySet = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build()).
            execute().getBody();
    assertNotNull(entitySet);
    assertEquals(expected, entitySet.getEntities().size());
  }

  @Test
  public void withId() {
    filterQueryTest("Customer", "CustomerId eq -10", 1);
  }

  @Test
  public void logical() {
    filterQueryTest("Customer", "CustomerId gt -10", 2);
    filterQueryTest("Customer", "CustomerId lt -10", 0);
    filterQueryTest("Customer", "not endswith(Name,'Chandan')", 2);
    filterQueryTest("Car", "VIN le 18 and VIN gt 12", 6);
  }

  @Test
  public void arithmetic() {
    filterQueryTest("Car", "VIN add 5 lt 11", 0);
    filterQueryTest("Car", "VIN div 2 le 8", 7);
    filterQueryTest("Car", "VIN mul 2 le 30", 5);
    filterQueryTest("Person", "PersonId sub 2 lt -10", 2);
  }

  @Test
  public void stringOperations() {
    filterQueryTest("Product", "length(Description) eq 7", 1);
    filterQueryTest("Product", "length(Description) eq 7", 1);
    filterQueryTest("Product", "substringof('kdcuklu', Description) eq true", 1);
    filterQueryTest("Product", "startswith(Description, 'k') eq true", 2);
    filterQueryTest("Product", "startswith(Description, 'k') eq true", 2);
    filterQueryTest("Product", "indexof(Description, 'k') eq 0", 2);
    filterQueryTest("Product", "toupper(Description) eq 'KDCUKLU'", 1);
    filterQueryTest("Product", "concat(Description, ', newname') eq 'kdcuklu, newname'", 1);
  }

  @Test
  public void math() {
    filterQueryTest("Product", "round(Dimensions/Width) eq 7338", 1);
    filterQueryTest("Product", "round(Dimensions/Width) eq 7338", 1);
    filterQueryTest("Product", "floor(Dimensions/Width) eq 7337", 1);
    filterQueryTest("Product", "ceiling(Dimensions/Width) eq 7338", 1);
  }

  @Test
  public void date() {
    filterQueryTest("ComputerDetail", "day(PurchaseDate) eq 15", 1);
    filterQueryTest("ComputerDetail", "month(PurchaseDate) eq 12", 2);
    filterQueryTest("ComputerDetail", "hour(PurchaseDate) eq 1", 1);
    filterQueryTest("ComputerDetail", "minute(PurchaseDate) eq 33", 1);
    filterQueryTest("ComputerDetail", "second(PurchaseDate) eq 35", 1);
    filterQueryTest("ComputerDetail", "year(PurchaseDate) eq 2020", 1);
  }

  @Test
  public void isOfTest() {
    filterQueryTest("Customer", "isof(Name,'Edm.String') eq true", 2);
  }
}
