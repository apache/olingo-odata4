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

package org.apache.olingo.fit.proxy.v4;

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.ext.proxy.Service;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.InMemoryEntities;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Order;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.PersonCollection;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
//CHECKSTYLE:ON (Maven checkstyle)

/**
 * This is the unit test class to check entity create operations.
 */
public class APIBasicDesignTestITCase extends AbstractTestITCase {

  protected Service<EdmEnabledODataClient> getService() {
    return service;
  }

  protected InMemoryEntities getContainer() {
    return container;
  }

  @Test
  public void readAndCheckForPrimitive() {
    final Customer customer = getContainer().getCustomers().getByKey(1);
    assertNotNull(customer);
    assertNull(customer.getPersonID());

    assertEquals(1, customer.load().getPersonID(), 0);
  }

  @Test
  public void readWholeEntitySet() {
    PersonCollection person = getContainer().getPeople().execute();
    assertEquals(5, person.size(), 0);

    int pageCount = 1;
    while (person.hasNextPage()) {
      pageCount++;
      assertFalse(person.nextPage().execute().isEmpty());
    }

    assertEquals(2, pageCount);
  }

  @Test
  public void loadWithSelect() {
    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Order order =
            getContainer().getOrders().getByKey(8);
    assertNull(order.getOrderID());
    assertNull(order.getOrderDate());

    order.select("OrderID");
    order.load();

    assertNull(order.getOrderDate());
    assertNotNull(order.getOrderID());

    order.clearQueryOptions();
    order.load();
    assertNotNull(order.getOrderDate());
    assertNotNull(order.getOrderID());
  }

  @Test
  public void loadWithSelectAndExpand() {
    final Customer customer = getContainer().getCustomers().getByKey(1);

    customer.expand("Orders");
    customer.select("Orders", "PersonID");

    customer.load();
    assertEquals(1, customer.getOrders().size());
  }

  @Test
  public void createDelete() {
    // Create order ....
    final Order order = getService().newEntity(Order.class);
    order.setOrderID(1105);

    final Calendar orderDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    orderDate.clear();
    orderDate.set(2011, 3, 4, 16, 3, 57);
    order.setOrderDate(new Timestamp(orderDate.getTimeInMillis()));

    order.setShelfLife(BigDecimal.ZERO);
    order.setOrderShelfLifes(Arrays.asList(new BigDecimal[] {BigDecimal.TEN.negate(), BigDecimal.TEN}));

    getContainer().getOrders().add(order);
    getContainer().flush();

    Order actual = getContainer().getOrders().getByKey(1105);
    assertNull(actual.getOrderID());

    actual.load();
    assertEquals(1105, actual.getOrderID(), 0);
    assertEquals(orderDate.getTimeInMillis(), actual.getOrderDate().getTime());
    assertEquals(BigDecimal.ZERO, actual.getShelfLife());
    assertEquals(2, actual.getOrderShelfLifes().size());

    service.getContext().detachAll();

    // Delete order ...
    getContainer().getOrders().delete(getContainer().getOrders().getByKey(1105));
    actual = getContainer().getOrders().getByKey(1105);
    assertNull(actual);

    getContainer().flush();

    service.getContext().detachAll();
    try {
      getContainer().getOrders().getByKey(105).load();
      fail();
    } catch (IllegalArgumentException e) {
    }
  }
}
