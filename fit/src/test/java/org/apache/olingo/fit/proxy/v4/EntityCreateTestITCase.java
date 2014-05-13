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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Employee;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Order;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.OrderCollection;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.OrderDetail;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.OrderDetailKey;
import org.junit.Test;

/**
 * This is the unit test class to check entity create operations.
 */
public class EntityCreateTestITCase extends AbstractTestITCase {

  @Test
  public void create() {
    final Order order = container.getOrders().newOrder();
    order.setOrderID(105);

    final Calendar orderDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    orderDate.clear();
    orderDate.set(2011, 3, 4, 16, 3, 57);
    order.setOrderDate(orderDate);

    order.setShelfLife(BigDecimal.TEN);
    order.setOrderShelfLifes(Arrays.asList(new BigDecimal[] {BigDecimal.TEN.negate(), BigDecimal.TEN}));

    container.flush();

    Order actual = container.getOrders().get(105);
    assertEquals(105, actual.getOrderID(), 0);
    assertEquals(orderDate.getTimeInMillis(), actual.getOrderDate().getTimeInMillis());
    assertEquals(BigDecimal.TEN, actual.getShelfLife());
    assertEquals(2, actual.getOrderShelfLifes().size());

    container.getOrders().delete(105);
    actual = container.getOrders().get(105);
    assertNull(actual);

    entityContext.detachAll();
    actual = container.getOrders().get(105);
    assertNotNull(actual);

    container.getOrders().delete(105);
    actual = container.getOrders().get(105);
    assertNull(actual);

    container.flush();

    entityContext.detachAll();
    actual = container.getOrders().get(105);
    assertNull(actual);
  }

  @Test
  public void createEmployee() {
    final Integer id = 101;

    final Employee employee = container.getPeople().newEmployee();
    employee.setPersonID(id);
    employee.setFirstName("Fabio");
    employee.setLastName("Martelli");
    employee.setEmails(Collections.<String>singleton("fabio.martelli@tirasa.net"));
    final Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    date.clear();
    date.set(2011, 3, 4, 9, 0, 0);
    employee.setDateHired(date);
    Address homeAddress = employee.factory().newHomeAddress();
    homeAddress.setCity("Pescara");
    homeAddress.setPostalCode("65100");
    homeAddress.setStreet("viale Gabriele D'Annunzio 256");
    employee.setHomeAddress(homeAddress);
    employee.setNumbers(Arrays.asList(new String[] {"3204725072", "08569930"}));

    container.flush();

    Employee actual = container.getPeople().get(id, Employee.class);
    assertNotNull(actual);
    assertEquals(id, actual.getPersonID());
    assertEquals(homeAddress.getCity(), actual.getHomeAddress().getCity());

    entityContext.detachAll();
    actual = container.getPeople().get(id, Employee.class);
    assertNotNull(actual);
    assertEquals(id, actual.getPersonID());
    assertEquals(homeAddress.getCity(), actual.getHomeAddress().getCity());

    container.getPeople().delete(actual.getPersonID());
    container.flush();

    actual = container.getPeople().get(id, Employee.class);;
    assertNull(actual);

    entityContext.detachAll();
    actual = container.getPeople().get(id, Employee.class);
    assertNull(actual);
  }

  @Test
  public void createWithNavigation() {
    final Integer id = 101;

    final Customer customer = container.getCustomers().newCustomer();
    customer.setPersonID(id);
    customer.setPersonID(id);
    customer.setFirstName("Fabio");
    customer.setLastName("Martelli");
    customer.setCity("Pescara");
    customer.setEmails(Collections.<String>singleton("fabio.martelli@tirasa.net"));
    Address homeAddress = customer.factory().newHomeAddress();
    homeAddress.setCity("Pescara");
    homeAddress.setPostalCode("65100");
    homeAddress.setStreet("viale Gabriele D'Annunzio 256");
    customer.setHomeAddress(homeAddress);
    customer.setNumbers(Arrays.asList(new String[] {"3204725072", "08569930"}));

    final OrderCollection orders = container.getOrders().newOrderCollection();
    orders.add(container.getOrders().get(8));
    customer.setOrders(orders);

    container.flush();

    Customer actual = readCustomer(container, id);
    assertEquals(homeAddress.getCity(), actual.getHomeAddress().getCity());
    assertEquals(1, actual.getOrders().size());
    assertEquals(8, actual.getOrders().iterator().next().getOrderID(), 0);

    container.getCustomers().delete(actual.getPersonID());
    container.flush();

    actual = container.getCustomers().get(id);
    assertNull(actual);
  }

  @Test
  public void createWithBackNavigation() {
    final Integer id = 102;

    // -------------------------------
    // Create a new order
    // -------------------------------
    Order order = container.getOrders().newOrder();
    order.setOrderID(id);

    final Calendar orderDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    orderDate.clear();
    orderDate.set(2011, 3, 4, 16, 3, 57);
    order.setOrderDate(orderDate);

    order.setShelfLife(BigDecimal.TEN);
    order.setOrderShelfLifes(Arrays.asList(new BigDecimal[] {BigDecimal.TEN.negate(), BigDecimal.TEN}));
    // -------------------------------


    // -------------------------------
    // Create a new customer
    // -------------------------------
    final Customer customer = container.getCustomers().newCustomer();
    customer.setPersonID(id);
    customer.setPersonID(id);
    customer.setFirstName("Fabio");
    customer.setLastName("Martelli");
    customer.setCity("Pescara");
    customer.setEmails(Collections.<String>singleton("fabio.martelli@tirasa.net"));
    Address homeAddress = customer.factory().newHomeAddress();
    homeAddress.setCity("Pescara");
    homeAddress.setPostalCode("65100");
    homeAddress.setStreet("viale Gabriele D'Annunzio 256");
    customer.setHomeAddress(homeAddress);
    customer.setNumbers(Arrays.asList(new String[] {"3204725072", "08569930"}));

    final OrderCollection orders = container.getOrders().newOrderCollection();
    orders.add(order);
    customer.setOrders(orders);
    // -------------------------------

    // -------------------------------
    // Link customer to order
    // -------------------------------
    order.setCustomerForOrder(customer);
    // -------------------------------

    container.flush();

    assertEquals(id, order.getOrderID());
    assertEquals(id, customer.getPersonID());

    Customer actual = readCustomer(container, id);
    assertEquals(homeAddress.getCity(), actual.getHomeAddress().getCity());
    assertEquals(1, actual.getOrders().size());
    assertEquals(id, actual.getOrders().iterator().next().getOrderID());

    order = container.getOrders().get(id);
    assertNotNull(order);
    assertEquals(id, order.getCustomerForOrder().getPersonID());

    container.getOrders().delete(actual.getOrders());
    container.flush();

    order = container.getOrders().get(id);
    assertNull(order);

    actual = readCustomer(container, id);
    assertTrue(actual.getOrders().isEmpty());

    container.getCustomers().delete(actual.getPersonID());
    container.flush();

    actual = container.getCustomers().get(id);
    assertNull(actual);
  }

  @Test
  public void multiKey() {
    OrderDetail details = container.getOrderDetails().newOrderDetail();
    details.setOrderID(8);
    details.setProductID(1);
    details.setQuantity(100);
    details.setUnitPrice(5f);

    container.flush();

    OrderDetailKey key = new OrderDetailKey();
    key.setOrderID(8);
    key.setProductID(1);

    details = container.getOrderDetails().get(key);
    assertNotNull(details);
    assertEquals(Integer.valueOf(100), details.getQuantity());
    assertEquals(8, details.getOrderID(), 0);
    assertEquals(1, details.getProductID(), 0);
    assertEquals(5f, details.getUnitPrice(), 0);

    container.getOrderDetails().delete(key);
    container.flush();

    assertNull(container.getOrderDetails().get(key));
  }
}
