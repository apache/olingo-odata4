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

// CHECKSTYLE:OFF (Maven checkstyle)
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.lang3.RandomUtils;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.InMemoryEntities;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.AccessLevel;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Color;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Employee;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.HomeAddress;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Order;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.OrderCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.OrderDetail;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.OrderDetailKey;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PaymentInstrument;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PaymentInstrumentCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Product;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductDetail;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductDetailCollection;
import org.junit.Assert;
import org.junit.Test;
// CHECKSTYLE:ON (Maven checkstyle)

/**
 * This is the unit test class to check entity create operations.
 */
public class EntityCreateTestITCase extends AbstractTestITCase {

  protected AbstractService<EdmEnabledODataClient> getService() {
    return service;
  }

  protected InMemoryEntities getContainer() {
    return container;
  }

  @Test
  public void createAndDelete() {
    createPatchAndDeleteOrder(getContainer(), getService());
  }

  @Test
  public void createEmployee() {
    final Integer id = 101;

    final Employee employee = getContainer().newEntityInstance(Employee.class);
    employee.setPersonID(id);
    employee.setFirstName("Fabio");
    employee.setLastName("Martelli");

    PrimitiveCollection<String> value = getContainer().newPrimitiveCollection(String.class);
    value.add("fabio.martelli@tirasa.net");
    employee.setEmails(value);

    final Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    date.clear();
    date.set(2011, 3, 4, 9, 0, 0);
    employee.setDateHired(new Timestamp(date.getTimeInMillis()));
    final Address homeAddress = getContainer().newComplexInstance(HomeAddress.class);
    homeAddress.setCity("Pescara");
    homeAddress.setPostalCode("65100");
    homeAddress.setStreet("viale Gabriele D'Annunzio 256");
    employee.setHomeAddress(homeAddress);

    value = getContainer().newPrimitiveCollection(String.class);
    value.add("3204725072");
    value.add("08569930");
    employee.setNumbers(value);

    getContainer().getPeople().add(employee);

    getContainer().flush();

    Employee actual = getContainer().getPeople().getByKey(id, Employee.class).load();
    assertNotNull(actual);
    assertEquals(id, actual.getPersonID());
    Assert.assertEquals(homeAddress.getCity(), actual.getHomeAddress().getCity());

    getService().getContext().detachAll();
    actual = getContainer().getPeople().getByKey(id, Employee.class).load();
    assertNotNull(actual);
    assertEquals(id, actual.getPersonID());
    Assert.assertEquals(homeAddress.getCity(), actual.getHomeAddress().getCity());

    getContainer().getPeople().delete(actual.getPersonID());
    getContainer().flush();

    try {
      getContainer().getPeople().getByKey(id, Employee.class).load();
      fail();
    } catch (IllegalArgumentException e) {
      // Expected
    }

    getService().getContext().detachAll();
    try {
      getContainer().getPeople().getByKey(id, Employee.class).load();
      fail();
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void createWithNavigation() {
    final Integer id = 101;

    final Customer customer = getContainer().newEntityInstance(Customer.class);
    customer.setPersonID(id);
    customer.setPersonID(id);
    customer.setFirstName("Fabio");
    customer.setLastName("Martelli");
    customer.setCity("Pescara");

    PrimitiveCollection<String> value = getContainer().newPrimitiveCollection(String.class);
    value.add("fabio.martelli@tirasa.net");
    customer.setEmails(value);

    Address homeAddress = getContainer().newComplexInstance(HomeAddress.class);
    homeAddress.setCity("Pescara");
    homeAddress.setPostalCode("65100");
    homeAddress.setStreet("viale Gabriele D'Annunzio 256");
    customer.setHomeAddress(homeAddress);

    value = getContainer().newPrimitiveCollection(String.class);
    value.add("3204725072");
    value.add("08569930");
    customer.setNumbers(value);

    final OrderCollection orders = getContainer().newEntityCollection(OrderCollection.class);
    orders.add(getContainer().getOrders().getByKey(8));
    customer.setOrders(orders);

    getContainer().getCustomers().add(customer);
    getContainer().flush();

    Customer actual = readCustomer(getContainer(), id);
    Assert.assertEquals(homeAddress.getCity(), actual.getHomeAddress().getCity());
    Assert.assertEquals(1, actual.getOrders().execute().size());
    Assert.assertEquals(8, actual.getOrders().iterator().next().getOrderID(), 0);

    getContainer().getCustomers().delete(actual.getPersonID());
    getContainer().flush();

    try {
      getContainer().getCustomers().getByKey(id).load();
      fail();
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void createWithBackNavigation() {
    final Integer id = 102;

    // -------------------------------
    // Create a new order
    // -------------------------------
    Order order = getContainer().newEntityInstance(Order.class);
    order.setOrderID(id);

    final Calendar orderDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    orderDate.clear();
    orderDate.set(2011, 3, 4, 16, 3, 57);
    order.setOrderDate(new Timestamp(orderDate.getTimeInMillis()));

    order.setShelfLife(BigDecimal.TEN);

    PrimitiveCollection<BigDecimal> osl = getContainer().newPrimitiveCollection(BigDecimal.class);
    osl.add(BigDecimal.TEN.negate());
    osl.add(BigDecimal.TEN);
    order.setOrderShelfLifes(osl);
    // -------------------------------

    // -------------------------------
    // Create a new customer
    // -------------------------------
    final Customer customer = getContainer().newEntityInstance(Customer.class);
    customer.setPersonID(id);
    customer.setPersonID(id);
    customer.setFirstName("Fabio");
    customer.setLastName("Martelli");
    customer.setCity("Pescara");

    PrimitiveCollection<String> value = getContainer().newPrimitiveCollection(String.class);
    value.add("fabio.martelli@tirasa.net");
    customer.setEmails(value);

    final Address homeAddress = getContainer().newComplexInstance(HomeAddress.class);
    homeAddress.setCity("Pescara");
    homeAddress.setPostalCode("65100");
    homeAddress.setStreet("viale Gabriele D'Annunzio 256");
    customer.setHomeAddress(homeAddress);

    value = getContainer().newPrimitiveCollection(String.class);
    value.add("3204725072");
    value.add("08569930");
    customer.setNumbers(value);

    final OrderCollection orders = getContainer().newEntityCollection(OrderCollection.class);
    orders.add(order);
    customer.setOrders(orders);
    // -------------------------------

    // -------------------------------
    // Link customer to order
    // -------------------------------
    order.setCustomerForOrder(customer);
    // -------------------------------

    getContainer().getOrders().add(order);
    getContainer().flush();

    assertEquals(id, order.getOrderID());
    assertEquals(id, customer.getPersonID());

    Customer actual = readCustomer(getContainer(), id);
    Assert.assertEquals(homeAddress.getCity(), actual.getHomeAddress().getCity());
    Assert.assertEquals(1, actual.getOrders().execute().size());
    Assert.assertEquals(id, actual.getOrders().iterator().next().getOrderID());

    order = getContainer().getOrders().getByKey(id);
    assertNotNull(order);
    Assert.assertEquals(id, order.getCustomerForOrder().load().getPersonID());

    getContainer().getOrders().delete(actual.getOrders());
    getContainer().flush();

    try {
      getContainer().getOrders().getByKey(id).load();
      fail();
    } catch (IllegalArgumentException e) {
      // Expected
    }

    actual = readCustomer(getContainer(), id);
    assertTrue(actual.getOrders().isEmpty());

    getContainer().getCustomers().delete(actual.getPersonID());
    getContainer().flush();

    try {
      getContainer().getCustomers().getByKey(id).load();
      fail();
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void multiKey() {
    OrderDetail details = getContainer().newEntityInstance(OrderDetail.class);
    details.setOrderID(8);
    details.setProductID(1);
    details.setQuantity(100);
    details.setUnitPrice(5f);

    getContainer().getOrderDetails().add(details);
    getContainer().flush();

    OrderDetailKey key = new OrderDetailKey();
    key.setOrderID(8);
    key.setProductID(1);

    details = getContainer().getOrderDetails().getByKey(key).load();
    assertNotNull(details);
    assertEquals(Integer.valueOf(100), details.getQuantity());
    assertEquals(8, details.getOrderID(), 0);
    assertEquals(1, details.getProductID(), 0);
    assertEquals(5f, details.getUnitPrice(), 0);

    getContainer().getOrderDetails().delete(key);
    getContainer().flush();

    try {
      getContainer().getOrderDetails().getByKey(key).load();
      fail();
    } catch (IllegalArgumentException e) {
      // Expected
    }
  }

  @Test
  public void deepInsert() {
    Product product = getContainer().newEntityInstance(Product.class);
    product.setProductID(12);
    product.setName("Latte");
    product.setQuantityPerUnit("100g Bag");
    product.setUnitPrice(3.24f);
    product.setQuantityInStock(100);
    product.setDiscontinued(false);
    product.setUserAccess(AccessLevel.Execute);
    product.setSkinColor(Color.Blue);

    PrimitiveCollection<Color> value = getContainer().newPrimitiveCollection(Color.class);
    value.add(Color.Red);
    value.add(Color.Green);
    product.setCoverColors(value);

    final ProductDetail detail = getContainer().newEntityInstance(ProductDetail.class);
    detail.setProductID(product.getProductID());
    detail.setProductDetailID(12);
    detail.setProductName("LatteHQ");
    detail.setDescription("High-Quality Milk");

    final ProductDetailCollection detailCollection = getContainer().newEntityCollection(ProductDetailCollection.class);
    detailCollection.add(detail);

    product.setDetails(detailCollection);

    getContainer().getProducts().add(product);
    getContainer().flush();

    product = getContainer().getProducts().getByKey(12).load();
    assertEquals("Latte", product.getName());
    Assert.assertEquals(12, product.getDetails().execute().iterator().next().getProductDetailID(), 0);
  }

  @Test
  public void contained() {
    PaymentInstrumentCollection instruments =
        getContainer().getAccounts().getByKey(101).getMyPaymentInstruments().execute();
    final int sizeBefore = instruments.size();

    final PaymentInstrument instrument = getContainer().newEntityInstance(PaymentInstrument.class);
    instruments.add(instrument);

    final int id = RandomUtils.nextInt(101999, 105000);
    instrument.setPaymentInstrumentID(id);
    instrument.setFriendlyName("New one");
    instrument.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));

    getContainer().flush();

    instruments = getContainer().getAccounts().getByKey(101).getMyPaymentInstruments().execute();
    final int sizeAfter = instruments.size();
    assertEquals(sizeBefore + 1, sizeAfter);

    getContainer().getAccounts().getByKey(101).getMyPaymentInstruments().delete(id);

    getContainer().flush();

    instruments = getContainer().getAccounts().getByKey(101).getMyPaymentInstruments().execute();
    final int sizeEnd = instruments.size();
    assertEquals(sizeBefore, sizeEnd);
  }
}
