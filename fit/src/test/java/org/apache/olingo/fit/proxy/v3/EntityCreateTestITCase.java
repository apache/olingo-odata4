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
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Message;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.MessageKey;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Order;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.OrderCollection;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
//CHECKSTYLE:ON (Maven checkstyle)

/**
 * This is the unit test class to check entity create operations.
 */
public class EntityCreateTestITCase extends AbstractTestITCase {

  @Test
  public void create() {
    container.getCustomer().getByKey(-10);
    final String sampleName = "sample customer from proxy";
    final Integer id = 100;

    getSampleCustomerProfile(id, sampleName, container);
    container.flush();

    Customer actual = readCustomer(container, id);
    checkSampleCustomerProfile(actual, id, sampleName);

    container.getCustomer().delete(actual.getCustomerId());
    actual = container.getCustomer().getByKey(id);
    assertNull(actual);

    service.getContext().detachAll();
    actual = container.getCustomer().getByKey(id).load();

    container.getCustomer().delete(actual.getCustomerId());
    container.flush();

    try {
      container.getCustomer().getByKey(id).load();
      fail();
    } catch (IllegalArgumentException e) {
    }

    service.getContext().detachAll();

    try {
      container.getCustomer().getByKey(id).load();
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void createEmployee() {
    final Integer id = 101;

    final Employee employee = container.newEntityInstance(Employee.class);
    employee.setPersonId(id);
    employee.setName("sample employee from proxy");
    employee.setManagersPersonId(-9918);
    employee.setSalary(2147483647);
    employee.setTitle("CEO");

    container.getPerson().add(employee);
    container.flush();

    Employee actual = container.getPerson().getByKey(id, Employee.class).load();
    assertNotNull(actual);
    assertEquals(id, actual.getPersonId());

    service.getContext().detachAll();
    actual = container.getPerson().getByKey(id, Employee.class).load();
    assertNotNull(actual);

    container.getPerson().delete(actual.getPersonId());
    container.flush();

    try {
      container.getPerson().getByKey(id, Employee.class).load();
      fail();
    } catch (IllegalArgumentException e) {
    }

    service.getContext().detachAll();

    try {
      container.getPerson().getByKey(id, Employee.class).load();
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void createWithNavigation() {
    final String sampleName = "sample customer from proxy with navigation";
    final Integer id = 101;

    final Customer original = getSampleCustomerProfile(id, sampleName, container);
    original.setInfo(container.getCustomerInfo().getByKey(16));
    container.flush();

    Customer actual = readCustomer(container, id);
    checkSampleCustomerProfile(actual, id, sampleName);
    assertEquals(16, actual.getInfo().load().getCustomerInfoId(), 0);

    container.getCustomer().delete(actual.getCustomerId());
    container.flush();

    try {
      container.getCustomer().getByKey(id).load();
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void createWithBackNavigation() {
    final String sampleName = "sample customer from proxy with back navigation";
    final Integer id = 102;

    Order order = container.newEntityInstance(Order.class);
    order.setCustomerId(id);
    order.setOrderId(id); // same id ...

    final Customer customer = getSampleCustomerProfile(id, sampleName, container);

    final OrderCollection orders = container.newEntityCollection(OrderCollection.class);
    orders.add(order);

    customer.setOrders(orders);
    order.setCustomer(customer);
    container.flush();

    assertEquals(id, order.getOrderId());
    assertEquals(id, order.getCustomerId());

    Customer actual = readCustomer(container, id);
    checkSampleCustomerProfile(actual, id, sampleName);

    assertEquals(1, actual.getOrders().execute().size());
    assertEquals(id, actual.getOrders().iterator().next().getOrderId());
    assertEquals(id, actual.getOrders().iterator().next().getCustomerId());

    order = container.getOrder().getByKey(id);
    assertEquals(id, order.getCustomer().load().getCustomerId());

    container.getOrder().delete(actual.getOrders());
    container.flush();

    try {
      container.getOrder().getByKey(id).load();
      fail();
    } catch (IllegalArgumentException e) {
    }

    actual = readCustomer(container, id);
    assertTrue(actual.getOrders().isEmpty());

    container.getCustomer().delete(actual.getCustomerId());
    container.flush();

    try {
      container.getCustomer().getByKey(id).load();
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void multiKey() {
    Message message = container.newEntityInstance(Message.class);
    message.setMessageId(100);
    message.setFromUsername("fromusername");
    message.setToUsername("myusername");
    message.setIsRead(false);
    message.setSubject("test message");
    message.setBody("test");

    container.getMessage().add(message);
    container.flush();

    MessageKey key = new MessageKey();
    key.setFromUsername("fromusername");
    key.setMessageId(100);

    message = container.getMessage().getByKey(key).load();
    assertNotNull(message);
    assertEquals(Integer.valueOf(100), message.getMessageId());
    assertEquals("fromusername", message.getFromUsername());
    assertEquals("myusername", message.getToUsername());
    assertEquals("test message", message.getSubject());
    assertEquals("test", message.getBody());

    container.getMessage().delete(key);
    container.flush();

    try {
      container.getMessage().getByKey(key).load();
      fail();
    } catch (IllegalArgumentException e) {
    }
  }
}
