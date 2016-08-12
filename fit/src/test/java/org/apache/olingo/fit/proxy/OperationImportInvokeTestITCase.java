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
package org.apache.olingo.fit.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.olingo.ext.proxy.api.PrimitiveCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.AccessLevel;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Color;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.HomeAddress;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PersonComposableInvoker;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductCollectionComposableInvoker;
// CHECKSTYLE:ON (Maven checkstyle)
import org.junit.Assert;
import org.junit.Test;

public class OperationImportInvokeTestITCase extends AbstractTestITCase {

  @Test
  public void getDefaultColor() {
    final Color color = container.operations().getDefaultColor().execute();
    assertEquals(Color.Red, color);
  }

  @Test
  public void getPerson2() {
    final PersonComposableInvoker person = container.operations().getPerson2("London");
    assertEquals(1, person.execute().getPersonID(), 0);
  }

  @Test
  public void getPerson() {
    final Address address = container.newComplexInstance(HomeAddress.class);
    address.setStreet("1 Microsoft Way");
    address.setPostalCode("98052");
    address.setCity("London");

    final PersonComposableInvoker person = container.operations().getPerson(address);
    assertEquals(1, person.execute().getPersonID(), 0);
  }

  @Test
  public void getAllProducts() {
    final ProductCollectionComposableInvoker products = container.operations().getAllProducts();
    Assert.assertEquals(5, products.execute().size());
  }

  @Test
  public void getProductsByAccessLevel() {
    final PrimitiveCollection<String> products =
        container.operations().getProductsByAccessLevel(AccessLevel.None).execute();
    assertEquals(5, products.size());
    assertTrue(products.contains("Car"));
  }

  @Test
  public void discount() {
    container.operations().discount(22).execute();
  }

  @Test
  public void resetBossAddress() {
    final Address address = container.newComplexInstance(HomeAddress.class);
    address.setStreet("Via Le Mani Dal Naso, 123");
    address.setPostalCode("Tollo");
    address.setCity("66010");

    final Address actual = container.operations().resetBossAddress(address).execute();
    assertEquals(address.getStreet(), actual.getStreet());
    assertEquals(address.getPostalCode(), actual.getPostalCode());
    assertEquals(address.getCity(), actual.getCity());
  }

  @Test
  public void bossEmails() {
    PrimitiveCollection<String> be = container.newPrimitiveCollection(String.class);
    be.add("first@olingo.apache.org");
    be.add("second@olingo.apache.org");

    final PrimitiveCollection<String> result = container.operations().resetBossEmail(be).execute();
    assertEquals(2, result.size());

    final PrimitiveCollection<String> result2 = container.operations().getBossEmails(0, 100).execute();
    assertEquals(result, result2);
  }
}
