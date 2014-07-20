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
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.commons.EntityInvocationHandler;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.CustomerInfo;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Login;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Order;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.OrderCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
//CHECKSTYLE:ON (Maven checkstyle)

/**
 * This is the unit test class to check entity retrieve operations.
 */
public class ContextTestITCase extends AbstractTestITCase {

  @Test
  public void attachDetachNewEntity() {
    final Customer customer1 = service.newEntityInstance(Customer.class);
    final Customer customer2 = service.newEntityInstance(Customer.class);

    final EntityInvocationHandler source1 =
            (EntityInvocationHandler) Proxy.getInvocationHandler(customer1);
    final EntityInvocationHandler source2 =
            (EntityInvocationHandler) Proxy.getInvocationHandler(customer2);

    container.getCustomer().add(customer1);
    container.getCustomer().add(customer2);

    assertTrue(service.getContext().entityContext().isAttached(source1));
    assertTrue(service.getContext().entityContext().isAttached(source2));

    service.getContext().entityContext().detach(source1);
    assertFalse(service.getContext().entityContext().isAttached(source1));
    assertTrue(service.getContext().entityContext().isAttached(source2));

    service.getContext().entityContext().detach(source2);
    assertFalse(service.getContext().entityContext().isAttached(source1));
    assertFalse(service.getContext().entityContext().isAttached(source2));
  }

  @Test
  public void attachDetachExistingEntity() {
    final Customer customer1 = container.getCustomer().getByKey(-10);
    final Customer customer2 = container.getCustomer().getByKey(-9);
    final Customer customer3 = container.getCustomer().getByKey(-10);

    final EntityInvocationHandler source1 =
            (EntityInvocationHandler) Proxy.getInvocationHandler(customer1);
    final EntityInvocationHandler source2 =
            (EntityInvocationHandler) Proxy.getInvocationHandler(customer2);
    final EntityInvocationHandler source3 =
            (EntityInvocationHandler) Proxy.getInvocationHandler(customer3);

    assertFalse(service.getContext().entityContext().isAttached(source1));
    assertFalse(service.getContext().entityContext().isAttached(source2));
    assertFalse(service.getContext().entityContext().isAttached(source3));

    service.getContext().entityContext().attach(source1);
    assertTrue(service.getContext().entityContext().isAttached(source1));
    assertFalse(service.getContext().entityContext().isAttached(source2));
    assertTrue(service.getContext().entityContext().isAttached(source3));

    service.getContext().entityContext().attach(source2);
    assertTrue(service.getContext().entityContext().isAttached(source1));
    assertTrue(service.getContext().entityContext().isAttached(source2));
    assertTrue(service.getContext().entityContext().isAttached(source3));

    try {
      service.getContext().entityContext().attach(source3);
      fail();
    } catch (IllegalStateException ignore) {
      // ignore
    }

    service.getContext().entityContext().detach(source1);
    assertFalse(service.getContext().entityContext().isAttached(source1));
    assertTrue(service.getContext().entityContext().isAttached(source2));
    assertFalse(service.getContext().entityContext().isAttached(source3));

    service.getContext().entityContext().detach(source2);
    assertFalse(service.getContext().entityContext().isAttached(source1));
    assertFalse(service.getContext().entityContext().isAttached(source2));
    assertFalse(service.getContext().entityContext().isAttached(source3));
  }

  @Test
  public void linkTargetExisting() {
    final Customer customer = service.newEntityInstance(Customer.class);
    final CustomerInfo customerInfo = container.getCustomerInfo().getByKey(11);

    customer.setInfo(customerInfo);
    assertNotNull(customer.getInfo());

    container.getCustomer().add(customer);

    final EntityInvocationHandler source = (EntityInvocationHandler) Proxy.getInvocationHandler(customer);
    final EntityInvocationHandler target = (EntityInvocationHandler) Proxy.getInvocationHandler(customerInfo);

    assertTrue(service.getContext().entityContext().isAttached(source));
    assertEquals(AttachedEntityStatus.NEW, service.getContext().entityContext().getStatus(source));
    assertFalse(service.getContext().entityContext().isAttached(target));

    checkUnidirectional("Info", source, "Customer", target, false);

    service.getContext().entityContext().detachAll();

    assertFalse(service.getContext().entityContext().isAttached(source));
    assertFalse(service.getContext().entityContext().isAttached(target));
  }

  @Test
  public void linkSourceExisting() {
    final Customer customer = container.getCustomer().getByKey(-10);

    final CustomerInfo customerInfo = service.newEntityInstance(CustomerInfo.class);

    customer.setInfo(customerInfo);
    assertNotNull(customer.getInfo());

    final EntityInvocationHandler source = (EntityInvocationHandler) Proxy.getInvocationHandler(customer);
    final EntityInvocationHandler target = (EntityInvocationHandler) Proxy.getInvocationHandler(customerInfo);

    assertTrue(service.getContext().entityContext().isAttached(source));
    assertEquals(AttachedEntityStatus.CHANGED, service.getContext().entityContext().getStatus(source));
    assertFalse(service.getContext().entityContext().isAttached(target));

    checkUnidirectional("Info", source, "Customer", target, false);

    service.getContext().entityContext().detachAll();

    assertFalse(service.getContext().entityContext().isAttached(source));
    assertFalse(service.getContext().entityContext().isAttached(target));
  }

  @Test
  public void linkBothExisting() {
    final Customer customer = container.getCustomer().getByKey(-10);
    final CustomerInfo customerInfo = container.getCustomerInfo().getByKey(12);

    customer.setInfo(customerInfo);
    assertNotNull(customer.getInfo());

    final EntityInvocationHandler source = (EntityInvocationHandler) Proxy.getInvocationHandler(customer);
    final EntityInvocationHandler target = (EntityInvocationHandler) Proxy.getInvocationHandler(customerInfo);

    assertTrue(service.getContext().entityContext().isAttached(source));
    assertEquals(AttachedEntityStatus.CHANGED, service.getContext().entityContext().getStatus(source));
    assertFalse(service.getContext().entityContext().isAttached(target));

    checkUnidirectional("Info", source, "Customer", target, false);

    service.getContext().entityContext().detachAll();

    assertFalse(service.getContext().entityContext().isAttached(source));
    assertFalse(service.getContext().entityContext().isAttached(target));
  }

  @Test
  public void linkEntitySet() {
    final Customer customer = service.newEntityInstance(Customer.class);

    final OrderCollection toBeLinked = service.newEntityCollection(OrderCollection.class);
    toBeLinked.add(service.newEntityInstance(Order.class));
    toBeLinked.add(service.newEntityInstance(Order.class));
    toBeLinked.add(service.newEntityInstance(Order.class));

    customer.setOrders(toBeLinked);
    assertNotNull(customer.getOrders());
    assertEquals(3, customer.getOrders().size());

    final EntityInvocationHandler source = (EntityInvocationHandler) Proxy.getInvocationHandler(customer);

    container.getCustomer().add(customer);

    assertTrue(service.getContext().entityContext().isAttached(source));
    assertEquals(AttachedEntityStatus.NEW, service.getContext().entityContext().getStatus(source));
    assertEquals(3, ((Collection) (source.getLinkChanges().entrySet().iterator().next().getValue())).size());

    for (Order order : toBeLinked) {
      final EntityInvocationHandler target = (EntityInvocationHandler) Proxy.getInvocationHandler(order);
      container.getOrder().add(order);

      assertTrue(service.getContext().entityContext().isAttached(target));
      assertEquals(AttachedEntityStatus.NEW, service.getContext().entityContext().getStatus(target));
      checkUnidirectional("Orders", source, "Customer", target, true);
    }

    service.getContext().entityContext().detachAll();

    assertFalse(service.getContext().entityContext().isAttached(source));

    for (Order order : toBeLinked) {
      assertFalse(service.getContext().entityContext().
              isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(order)));
    }
  }

  @Test
  public void addProperty() {
    final Customer customer = service.newEntityInstance(Customer.class);
    customer.setCustomerId(100);

    final ContactDetails cd = service.newComplex(ContactDetails.class);
    customer.setPrimaryContactInfo(cd);

    cd.setAlternativeNames(Arrays.asList("alternative1", "alternative2"));

    final ContactDetails bcd = service.newComplex(ContactDetails.class);
    customer.setBackupContactInfo(Collections.<ContactDetails>singletonList(bcd));

    bcd.setAlternativeNames(Arrays.asList("alternative3", "alternative4"));

    assertEquals(Integer.valueOf(100), customer.getCustomerId());
    assertNotNull(customer.getPrimaryContactInfo().getAlternativeNames());
    assertEquals(2, customer.getPrimaryContactInfo().getAlternativeNames().size());
    assertTrue(customer.getPrimaryContactInfo().getAlternativeNames().contains("alternative1"));
    assertEquals(2, customer.getBackupContactInfo().iterator().next().getAlternativeNames().size());
    assertTrue(customer.getBackupContactInfo().iterator().next().getAlternativeNames().contains("alternative4"));

    container.getCustomer().add(customer);

    final EntityInvocationHandler source = (EntityInvocationHandler) Proxy.getInvocationHandler(customer);

    assertTrue(service.getContext().entityContext().isAttached(source));
    assertEquals(AttachedEntityStatus.NEW, service.getContext().entityContext().getStatus(source));

    service.getContext().entityContext().detachAll();

    assertFalse(service.getContext().entityContext().isAttached(source));
  }

  @Test
  public void readEntityInTheContext() {
    CustomerInfo customerInfo = container.getCustomerInfo().getByKey(16).load();
    customerInfo.setInformation("some other info ...");

    assertEquals("some other info ...", customerInfo.getInformation());

    customerInfo = container.getCustomerInfo().getByKey(16);
    assertEquals("some other info ...", customerInfo.getInformation());

    service.getContext().entityContext().detachAll();
    customerInfo = container.getCustomerInfo().getByKey(16);
    assertNotEquals("some other info ...", customerInfo.getInformation());
  }

  @Test
  public void readAllWithEntityInTheContext() {
    CustomerInfo customerInfo = container.getCustomerInfo().getByKey(16).load();
    customerInfo.setInformation("some other info ...");

    assertEquals("some other info ...", customerInfo.getInformation());

    boolean found = false;
    for (CustomerInfo info : container.getCustomerInfo().execute()) {
      if (info.getCustomerInfoId() == 16) {
        assertEquals("some other info ...", customerInfo.getInformation());
        found = true;
      }
    }
    assertTrue(found);

    service.getContext().entityContext().detachAll();

    found = false;
    for (CustomerInfo info : container.getCustomerInfo().execute()) {
      if (info.getCustomerInfoId() == 16) {
        assertNotEquals("some other info ...", info.getInformation());
        found = true;
      }
    }
    assertTrue(found);
  }

  @Test
  public void checkContextInCaseOfErrors() {
    service.getContext().entityContext().detachAll();

    final Login login = service.newEntityInstance(Login.class);

    final EntityInvocationHandler handler = (EntityInvocationHandler) Proxy.getInvocationHandler(login);
    assertFalse(service.getContext().entityContext().isAttached(handler));

    container.flush(); // Login will be ignored because not added to an entity set.

    container.getLogin().add(login); // now has been added

    assertTrue(service.getContext().entityContext().isAttached(handler));

    try {
      container.flush();
      fail();
    } catch (Exception e) {
      // ignore
    }

    assertTrue(service.getContext().entityContext().isAttached(handler));

    login.setCustomerId(-10);
    login.setUsername("customer");

    container.flush();
    assertFalse(service.getContext().entityContext().isAttached(handler));
    assertNotNull(container.getLogin().getByKey("customer"));

    container.getLogin().delete(login.getUsername());
    assertTrue(service.getContext().entityContext().isAttached(handler));

    container.flush();
    assertFalse(service.getContext().entityContext().isAttached(handler));

    try {
      container.getLogin().getByKey("customer").load();
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void flushTest() {
    Customer customer = service.newEntityInstance(Customer.class);
    customer.setCustomerId(300);
    customer.setName("samplename");

    final List<Integer> keys = new ArrayList<Integer>();
    keys.add(-200);
    keys.add(-201);
    keys.add(-202);

    final OrderCollection toBeLinked = service.newEntityCollection(OrderCollection.class);
    for (Integer key : keys) {
      final Order order = service.newEntityInstance(Order.class);
      order.setOrderId(key);
      order.setCustomerId(300);
      order.setCustomer(customer);
      toBeLinked.add(order);
    }

    customer.setOrders(toBeLinked);

    final CustomerInfo customerInfo = container.getCustomerInfo().getByKey(16);
    customerInfo.setInformation("some new info ...");
    customer.setInfo(customerInfo);

    final ContactDetails cd = service.newComplex(ContactDetails.class);
    cd.setAlternativeNames(Arrays.asList("alternative1", "alternative2"));
    cd.setEmailBag(Collections.<String>singleton("myemail@mydomain.org"));
    cd.setMobilePhoneBag(Collections.<Phone>emptySet());

    final ContactDetails bcd = service.newComplex(ContactDetails.class);
    bcd.setAlternativeNames(Arrays.asList("alternative3", "alternative4"));
    bcd.setEmailBag(Collections.<String>emptySet());
    bcd.setMobilePhoneBag(Collections.<Phone>emptySet());

    customer.setPrimaryContactInfo(cd);
    customer.setBackupContactInfo(Collections.<ContactDetails>singletonList(bcd));

    container.getCustomer().add(customer);

    assertTrue(service.getContext().entityContext().
            isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customerInfo)));
    assertTrue(service.getContext().entityContext().
            isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customer)));
    for (Order linked : toBeLinked) {
      assertFalse(service.getContext().entityContext().
              isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(linked)));
    }

    container.flush();

    assertFalse(service.getContext().entityContext().
            isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customerInfo)));
    assertFalse(service.getContext().entityContext().
            isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customer)));
    for (Order linked : toBeLinked) {
      assertFalse(service.getContext().entityContext().
              isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(linked)));
    }

    assertEquals("some new info ...", container.getCustomerInfo().getByKey(16).load().getInformation());

    container.getOrder().delete(toBeLinked);
    container.getCustomer().delete(customer.getCustomerId());

    assertTrue(service.getContext().entityContext().
            isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customer)));
    for (Order linked : toBeLinked) {
      assertTrue(service.getContext().entityContext().
              isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(linked)));
    }

    container.flush();

    assertFalse(service.getContext().entityContext().
            isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customer)));
    for (Order linked : toBeLinked) {
      assertFalse(service.getContext().entityContext().
              isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(linked)));
    }
  }

  private void checkUnlink(
          final String sourceName,
          final EntityInvocationHandler source) {

    boolean found = false;
    for (Map.Entry<NavigationProperty, Object> property : source.getLinkChanges().entrySet()) {
      if (property.getKey().name().equals(sourceName)) {
        found = true;
      }
    }
    assertFalse(found);
  }

  private void checkLink(
          final String sourceName,
          final EntityInvocationHandler source,
          final EntityInvocationHandler target,
          final boolean isCollection) {

    boolean found = false;
    for (Map.Entry<NavigationProperty, Object> property : source.getLinkChanges().entrySet()) {
      if (property.getKey().name().equals(sourceName)) {
        if (isCollection) {
          found = false;
          for (Object proxy : (Collection) property.getValue()) {
            if (target.equals((EntityInvocationHandler) Proxy.getInvocationHandler(proxy))) {
              found = true;
            }
          }
        } else {
          found = target.equals(
                  (EntityInvocationHandler) Proxy.getInvocationHandler(property.getValue()));
        }
      }
    }
    assertTrue(found);
  }

  private void checkUnidirectional(
          final String sourceName,
          final EntityInvocationHandler source,
          final String targetName,
          final EntityInvocationHandler target,
          final boolean isCollection) {

    checkLink(sourceName, source, target, isCollection);
    checkUnlink(targetName, target);
  }
}
