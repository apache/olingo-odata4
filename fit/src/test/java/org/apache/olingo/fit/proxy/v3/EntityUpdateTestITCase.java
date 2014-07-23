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

package org.apache.olingo.fit.proxy.v3;

//CHECKSTYLE:OFF (Maven checkstyle)
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.ext.proxy.commons.EntityInvocationHandler;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Message;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.MessageKey;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Order;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.OrderCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Product;
import org.junit.Test;

import java.lang.reflect.Proxy;
//CHECKSTYLE:ON (Maven checkstyle)

/**
 * This is the unit test class to check entity update operations.
 */
public class EntityUpdateTestITCase extends AbstractTestITCase {

  @Test
  public void update() {
    Order order = container.getOrder().getByKey(-9).load();

    final ConcurrencyInfo ci = order.getConcurrency();
    ci.setToken("XXX");

    container.flush();

    order = container.getOrder().getByKey(-9).load();
    assertEquals("XXX", order.getConcurrency().getToken());
  }

  @Test
  public void multiKey() {
    final MessageKey key = new MessageKey();
    key.setFromUsername("1");
    key.setMessageId(-10);

    Message message = container.getMessage().getByKey(key);

    message.setBody("XXX");

    container.flush();

    message = container.getMessage().getByKey(key).load();
    assertEquals("XXX", message.getBody());
  }

  @Test
  public void patchLink() {
    Order order = container.newEntityInstance(Order.class);
    order.setOrderId(400);
    order.setCustomerId(-9);

    OrderCollection orders = container.newEntityCollection(OrderCollection.class);
    orders.add(order);

    Customer customer = container.getCustomer().getByKey(-9);
    customer.setOrders(orders);
    order.setCustomer(customer);

    container.flush();

    order = container.getOrder().getByKey(400).load();
    assertEquals(400, order.getOrderId().intValue());
    assertEquals(-9, order.getCustomerId().intValue());

    customer = container.getCustomer().getByKey(-9);

    assertEquals(2, customer.getOrders().execute().size());

    int count = 0;
    for (Order inside : customer.getOrders()) {
      if (inside.getOrderId() == 400) {
        count++;
      }
    }
    assertEquals(1, count);
    assertEquals(-9, order.getCustomer().load().getCustomerId(), 0);
  }

  @Test
  public void concurrentModification() {
    Product product = container.getProduct().getByKey(-10).load();
    final String etag = ((EntityInvocationHandler) Proxy.getInvocationHandler(product)).getETag();
    assertTrue(StringUtils.isNotBlank(etag));

    final String baseConcurrency = String.valueOf(System.currentTimeMillis());
    product.setBaseConcurrency(baseConcurrency);

    container.flush();

    product = container.getProduct().getByKey(-10).load();
    assertEquals(baseConcurrency, product.getBaseConcurrency());
  }
}
