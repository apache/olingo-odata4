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
package org.apache.olingo.fit.proxy.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Proxy;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.ext.proxy.commons.EntityInvocationHandler;
//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.InMemoryEntities;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.AccessLevel;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Company;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CustomerCollection;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Employee;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.EmployeeCollection;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Order;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.OrderCollection;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.OrderDetail;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.OrderDetailKey;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.PaymentInstrument;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Person;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.PersonCollection;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Product;
//CHECKSTYLE:ON (Maven checkstyle)
import org.junit.Test;

/**
 * This is the unit test class to check entity retrieve operations.
 */
public class EntityRetrieveTestITCase extends AbstractTestITCase {

  protected InMemoryEntities getContainer() {
    return container;
  }

  @Test
  public void exists() {
    assertTrue(getContainer().getCustomers().exists(1));
    assertFalse(getContainer().getOrders().exists(1));
  }

  @Test
  public void get() {
    readCustomer(getContainer(), 1);
  }

  @Test
  public void getAll() {
    final PersonCollection all = getContainer().getPeople().getAll();
    assertNotNull(all);
    assertFalse(all.isEmpty());
    for (Person person : all) {
      assertNotNull(person);
    }

    final EmployeeCollection employees = getContainer().getPeople().getAll(EmployeeCollection.class);
    assertNotNull(employees);
    assertFalse(employees.isEmpty());
    for (Employee employee : employees) {
      assertNotNull(employee);
    }

    final CustomerCollection customers = getContainer().getPeople().getAll(CustomerCollection.class);
    assertNotNull(customers);
    assertFalse(customers.isEmpty());
    for (Customer customer : customers) {
      assertNotNull(customer);
    }

    assertTrue(all.size() > employees.size() + customers.size());
  }

  @Test
  public void navigate() {
    final Order order = getContainer().getOrders().get(8).load();
    assertNotNull(order);
    assertEquals(8, order.getOrderID(), 0);

    final Timestamp date = order.getOrderDate();
    assertNotNull(date);
    final Calendar actual = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    actual.clear();
    actual.set(2011, 2, 4, 16, 3, 57);
    assertEquals(actual.getTimeInMillis(), date.getTime());

    final Customer customer = getContainer().getCustomers().get(1).load();
    assertNotNull(customer);
    assertEquals(1, customer.getPersonID(), 0);
    final Address address = customer.getHomeAddress();
    assertNotNull(address);
    assertEquals("98052", address.getPostalCode());
  }

  @Test
  public void withInlineEntry() {
    final Customer customer = readCustomer(getContainer(), 1);
    final Company company = customer.getCompany();
    assertEquals(0, company.getCompanyID(), 0);
  }

  @Test
  public void withInlineFeed() {
    final Customer customer = readCustomer(getContainer(), 1);
    final OrderCollection orders = customer.getOrders();
    assertEquals(1, orders.size());
    assertEquals(8, orders.iterator().next().getOrderID(), 0);
  }

  @Test
  public void withActions() {
    final Product product = getContainer().getProducts().get(5).load();
    assertEquals(5, product.getProductID(), 0);

    try {
      assertNotNull(product.operations().getClass().getMethod("addAccessRight", AccessLevel.class));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void multiKey() {
    final OrderDetailKey orderDetailKey = new OrderDetailKey();
    orderDetailKey.setOrderID(7);
    orderDetailKey.setProductID(5);

    final OrderDetail orderDetail = getContainer().getOrderDetails().get(orderDetailKey).load();
    assertNotNull(orderDetail);
    assertEquals(7, orderDetail.getOrderID(), 0);
    assertEquals(5, orderDetail.getProductID(), 0);
  }

  @Test
  public void checkForETag() {
    final Order order = getContainer().getOrders().get(8).load();
    assertTrue(StringUtils.isNotBlank(((EntityInvocationHandler) Proxy.getInvocationHandler(order)).getETag()));
  }

  @Test
  public void contained() {
    final PaymentInstrument instrument = 
            getContainer().getAccounts().get(101).getMyPaymentInstruments().get(101901).load();
    assertEquals(101901, instrument.getPaymentInstrumentID(), 0);
    assertNotNull(instrument.getCreatedDate());
  }
}
