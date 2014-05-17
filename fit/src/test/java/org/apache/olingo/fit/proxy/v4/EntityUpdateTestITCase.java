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
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.ext.proxy.commons.EntityInvocationHandler;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Order;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.OrderCollection;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.OrderDetail;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.OrderDetailKey;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.
        PaymentInstrument;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Person;
import org.junit.Test;

/**
 * This is the unit test class to check entity update operations.
 */
public class EntityUpdateTestITCase extends AbstractTestITCase {

  @Test
  public void update() {
    Person person = container.getPeople().get(1);

    final Address address = person.getHomeAddress();
    address.setCity("XXX");

    container.flush();

    person = container.getPeople().get(1);
    assertEquals("XXX", person.getHomeAddress().getCity());
  }

  @Test
  public void multiKey() {
    final OrderDetailKey orderDetailKey = new OrderDetailKey();
    orderDetailKey.setOrderID(7);
    orderDetailKey.setProductID(5);

    OrderDetail orderDetail = container.getOrderDetails().get(orderDetailKey);
    assertNotNull(orderDetail);
    assertEquals(7, orderDetail.getOrderID(), 0);
    assertEquals(5, orderDetail.getProductID(), 0);

    orderDetail.setQuantity(5);

    container.flush();

    orderDetail = container.getOrderDetails().get(orderDetailKey);
    orderDetail.setQuantity(5);
  }

  @Test
  public void patchLink() {
    Order order = container.getOrders().newOrder();
    order.setOrderID(400);

    OrderCollection orders = container.getOrders().newOrderCollection();
    orders.add(order);

    Customer customer = container.getCustomers().get(1);
    customer.setOrders(orders);
    order.setCustomerForOrder(customer);

    container.flush();

    order = container.getOrders().get(400);
    assertEquals(400, order.getOrderID().intValue());

    customer = container.getCustomers().get(1);

    assertEquals(2, customer.getOrders().size());

    int count = 0;
    for (Order inside : customer.getOrders()) {
      if (inside.getOrderID() == 400) {
        count++;
      }
    }
    assertEquals(1, count);
    assertEquals(1, order.getCustomerForOrder().getPersonID(), 0);
  }

  @Test
  public void concurrentModification() {
    Order order = container.getOrders().get(8);
    final String etag = ((EntityInvocationHandler) Proxy.getInvocationHandler(order)).getETag();
    assertTrue(StringUtils.isNotBlank(etag));

    order.setShelfLife(BigDecimal.TEN);

    container.flush();

    order = container.getOrders().get(8);
    assertEquals(BigDecimal.TEN, order.getShelfLife());
  }

  @Test
  public void contained() {
    PaymentInstrument instrument = container.getAccounts().get(101).getMyPaymentInstruments().get(101901);

    final String newName = UUID.randomUUID().toString();
    instrument.setFriendlyName(newName);

    container.flush();

    instrument = container.getAccounts().get(101).getMyPaymentInstruments().get(101901);
    assertEquals(newName, instrument.getFriendlyName());
  }
}
