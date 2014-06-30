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
package org.apache.olingo.fit.proxy.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.commons.EntityInvocationHandler;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.CustomerInfo;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Login;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Order;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.OrderCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone;
//CHECKSTYLE:ON (Maven checkstyle)
import org.junit.Test;

/**
 * This is the unit test class to check entity retrieve operations.
 */
public class ContextTestITCase extends AbstractTestITCase {

  @Test
  public void attachDetachNewEntity() {
    final Customer customer1 = container.getCustomer().newCustomer();
    final Customer customer2 = container.getCustomer().newCustomer();

    final EntityInvocationHandler source1 =
        (EntityInvocationHandler) Proxy.getInvocationHandler(customer1);
    final EntityInvocationHandler source2 =
        (EntityInvocationHandler) Proxy.getInvocationHandler(customer2);

    assertTrue(containerFactory.getContext().entityContext().isAttached(source1));
    assertTrue(containerFactory.getContext().entityContext().isAttached(source2));

    containerFactory.getContext().entityContext().detach(source1);
    assertFalse(containerFactory.getContext().entityContext().isAttached(source1));
    assertTrue(containerFactory.getContext().entityContext().isAttached(source2));

    containerFactory.getContext().entityContext().detach(source2);
    assertFalse(containerFactory.getContext().entityContext().isAttached(source1));
    assertFalse(containerFactory.getContext().entityContext().isAttached(source2));
  }

  @Test
  public void attachDetachExistingEntity() {
    final Customer customer1 = container.getCustomer().get(-10);
    final Customer customer2 = container.getCustomer().get(-9);
    final Customer customer3 = container.getCustomer().get(-10);

    final EntityInvocationHandler source1 =
        (EntityInvocationHandler) Proxy.getInvocationHandler(customer1);
    final EntityInvocationHandler source2 =
        (EntityInvocationHandler) Proxy.getInvocationHandler(customer2);
    final EntityInvocationHandler source3 =
        (EntityInvocationHandler) Proxy.getInvocationHandler(customer3);

    assertFalse(containerFactory.getContext().entityContext().isAttached(source1));
    assertFalse(containerFactory.getContext().entityContext().isAttached(source2));
    assertFalse(containerFactory.getContext().entityContext().isAttached(source3));

    containerFactory.getContext().entityContext().attach(source1);
    assertTrue(containerFactory.getContext().entityContext().isAttached(source1));
    assertFalse(containerFactory.getContext().entityContext().isAttached(source2));
    assertTrue(containerFactory.getContext().entityContext().isAttached(source3));

    containerFactory.getContext().entityContext().attach(source2);
    assertTrue(containerFactory.getContext().entityContext().isAttached(source1));
    assertTrue(containerFactory.getContext().entityContext().isAttached(source2));
    assertTrue(containerFactory.getContext().entityContext().isAttached(source3));

    try {
      containerFactory.getContext().entityContext().attach(source3);
      fail();
    } catch (IllegalStateException ignore) {
      // ignore
    }

    containerFactory.getContext().entityContext().detach(source1);
    assertFalse(containerFactory.getContext().entityContext().isAttached(source1));
    assertTrue(containerFactory.getContext().entityContext().isAttached(source2));
    assertFalse(containerFactory.getContext().entityContext().isAttached(source3));

    containerFactory.getContext().entityContext().detach(source2);
    assertFalse(containerFactory.getContext().entityContext().isAttached(source1));
    assertFalse(containerFactory.getContext().entityContext().isAttached(source2));
    assertFalse(containerFactory.getContext().entityContext().isAttached(source3));
  }

  @Test
  public void linkTargetExisting() {
    final Customer customer = container.getCustomer().newCustomer();
    final CustomerInfo customerInfo = container.getCustomerInfo().get(11);

    customer.setInfo(customerInfo);

    assertNotNull(customer.getInfo());

    final EntityInvocationHandler source =
        (EntityInvocationHandler) Proxy.getInvocationHandler(customer);
    final EntityInvocationHandler target =
        (EntityInvocationHandler) Proxy.getInvocationHandler(customerInfo);

    assertTrue(containerFactory.getContext().entityContext().isAttached(source));
    assertEquals(AttachedEntityStatus.NEW, containerFactory.getContext().entityContext().getStatus(source));
    assertTrue(containerFactory.getContext().entityContext().isAttached(target));
    assertEquals(AttachedEntityStatus.LINKED, containerFactory.getContext().entityContext().getStatus(target));

    checkUnidirectional("Info", source, "Customer", target, false);

    containerFactory.getContext().entityContext().detachAll();

    assertFalse(containerFactory.getContext().entityContext().isAttached(source));
    assertFalse(containerFactory.getContext().entityContext().isAttached(target));
  }

  @Test
  public void linkSourceExisting() {
    final Customer customer = container.getCustomer().get(-10);
    ;
    final CustomerInfo customerInfo = container.getCustomerInfo().newCustomerInfo();

    customer.setInfo(customerInfo);

    assertNotNull(customer.getInfo());

    final EntityInvocationHandler source =
        (EntityInvocationHandler) Proxy.getInvocationHandler(customer);
    final EntityInvocationHandler target =
        (EntityInvocationHandler) Proxy.getInvocationHandler(customerInfo);

    assertTrue(containerFactory.getContext().entityContext().isAttached(source));
    assertEquals(AttachedEntityStatus.CHANGED, containerFactory.getContext().entityContext().getStatus(source));
    assertTrue(containerFactory.getContext().entityContext().isAttached(target));
    assertEquals(AttachedEntityStatus.NEW, containerFactory.getContext().entityContext().getStatus(target));

    checkUnidirectional("Info", source, "Customer", target, false);

    containerFactory.getContext().entityContext().detachAll();

    assertFalse(containerFactory.getContext().entityContext().isAttached(source));
    assertFalse(containerFactory.getContext().entityContext().isAttached(target));
  }

  @Test
  public void linkBothExisting() {
    final Customer customer = container.getCustomer().get(-10);
    final CustomerInfo customerInfo = container.getCustomerInfo().get(12);

    customer.setInfo(customerInfo);

    assertNotNull(customer.getInfo());

    final EntityInvocationHandler source =
        (EntityInvocationHandler) Proxy.getInvocationHandler(customer);
    final EntityInvocationHandler target =
        (EntityInvocationHandler) Proxy.getInvocationHandler(customerInfo);

    assertTrue(containerFactory.getContext().entityContext().isAttached(source));
    assertEquals(AttachedEntityStatus.CHANGED, containerFactory.getContext().entityContext().getStatus(source));
    assertTrue(containerFactory.getContext().entityContext().isAttached(target));
    assertEquals(AttachedEntityStatus.LINKED, containerFactory.getContext().entityContext().getStatus(target));

    checkUnidirectional("Info", source, "Customer", target, false);

    containerFactory.getContext().entityContext().detachAll();

    assertFalse(containerFactory.getContext().entityContext().isAttached(source));
    assertFalse(containerFactory.getContext().entityContext().isAttached(target));
  }

  @Test
  public void linkEntitySet() {
    final Customer customer = container.getCustomer().newCustomer();

    final OrderCollection toBeLinked = container.getOrder().newOrderCollection();
    toBeLinked.add(container.getOrder().newOrder());
    toBeLinked.add(container.getOrder().newOrder());
    toBeLinked.add(container.getOrder().newOrder());

    customer.setOrders(toBeLinked);
    assertNotNull(customer.getOrders());
    assertEquals(3, customer.getOrders().size());

    final EntityInvocationHandler source = (EntityInvocationHandler) Proxy.getInvocationHandler(customer);

    assertTrue(containerFactory.getContext().entityContext().isAttached(source));
    assertEquals(AttachedEntityStatus.NEW, containerFactory.getContext().entityContext().getStatus(source));
    assertEquals(3, ((Collection) (source.getLinkChanges().entrySet().iterator().next().getValue())).size());

    for (Order order : toBeLinked) {
      final EntityInvocationHandler target = (EntityInvocationHandler) Proxy.getInvocationHandler(order);

      assertTrue(containerFactory.getContext().entityContext().isAttached(target));
      assertEquals(AttachedEntityStatus.NEW, containerFactory.getContext().entityContext().getStatus(target));
      checkUnidirectional("Orders", source, "Customer", target, true);
    }

    containerFactory.getContext().entityContext().detachAll();

    assertFalse(containerFactory.getContext().entityContext().isAttached(source));

    for (Order order : toBeLinked) {
      assertFalse(containerFactory.getContext().entityContext().
          isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(order)));
    }
  }

  @Test
  public void addProperty() {
    final Customer customer = container.getCustomer().newCustomer();
    customer.setCustomerId(100);

    final ContactDetails cd = customer.factory().newPrimaryContactInfo();
    customer.setPrimaryContactInfo(cd);

    cd.setAlternativeNames(Arrays.asList("alternative1", "alternative2"));

    final ContactDetails bcd = customer.factory().newBackupContactInfo();
    customer.setBackupContactInfo(Collections.<ContactDetails> singletonList(bcd));

    bcd.setAlternativeNames(Arrays.asList("alternative3", "alternative4"));

    assertEquals(Integer.valueOf(100), customer.getCustomerId());
    assertNotNull(customer.getPrimaryContactInfo().getAlternativeNames());
    assertEquals(2, customer.getPrimaryContactInfo().getAlternativeNames().size());
    assertTrue(customer.getPrimaryContactInfo().getAlternativeNames().contains("alternative1"));
    assertEquals(2, customer.getBackupContactInfo().iterator().next().getAlternativeNames().size());
    assertTrue(customer.getBackupContactInfo().iterator().next().getAlternativeNames().contains("alternative4"));

    final EntityInvocationHandler source = (EntityInvocationHandler) Proxy.getInvocationHandler(customer);

    assertTrue(containerFactory.getContext().entityContext().isAttached(source));
    assertEquals(AttachedEntityStatus.NEW, containerFactory.getContext().entityContext().getStatus(source));

    containerFactory.getContext().entityContext().detachAll();

    assertFalse(containerFactory.getContext().entityContext().isAttached(source));
  }

  @Test
  public void readEntityInTheContext() {
    CustomerInfo customerInfo = container.getCustomerInfo().get(16);
    customerInfo.setInformation("some other info ...");

    assertEquals("some other info ...", customerInfo.getInformation());

    customerInfo = container.getCustomerInfo().get(16);
    assertEquals("some other info ...", customerInfo.getInformation());

    containerFactory.getContext().entityContext().detachAll();
    customerInfo = container.getCustomerInfo().get(16);
    assertNotEquals("some other info ...", customerInfo.getInformation());
  }

  @Test
  public void readAllWithEntityInTheContext() {
    CustomerInfo customerInfo = container.getCustomerInfo().get(16);
    customerInfo.setInformation("some other info ...");

    assertEquals("some other info ...", customerInfo.getInformation());

    boolean found = false;
    for (CustomerInfo info : container.getCustomerInfo().getAll()) {
      if (info.getCustomerInfoId() == 16) {
        assertEquals("some other info ...", customerInfo.getInformation());
        found = true;
      }
    }
    assertTrue(found);

    containerFactory.getContext().entityContext().detachAll();

    found = false;
    for (CustomerInfo info : container.getCustomerInfo().getAll()) {
      if (info.getCustomerInfoId() == 16) {
        assertNotEquals("some other info ...", info.getInformation());
        found = true;
      }
    }
    assertTrue(found);
  }

  @Test
  public void checkContextInCaseOfErrors() {
    final Login login = container.getLogin().newLogin();

    final EntityInvocationHandler handler = (EntityInvocationHandler) Proxy.getInvocationHandler(login);

    assertTrue(containerFactory.getContext().entityContext().isAttached(handler));

    try {
      container.flush();
      fail();
    } catch (Exception e) {
      // ignore
    }

    assertTrue(containerFactory.getContext().entityContext().isAttached(handler));

    login.setCustomerId(-10);
    login.setUsername("customer");

    container.flush();
    assertFalse(containerFactory.getContext().entityContext().isAttached(handler));
    assertNotNull(container.getLogin().get("customer"));

    container.getLogin().delete(login.getUsername());
    assertTrue(containerFactory.getContext().entityContext().isAttached(handler));

    container.flush();
    assertFalse(containerFactory.getContext().entityContext().isAttached(handler));
    assertNull(container.getLogin().get("customer"));
  }

  @Test
  public void flushTest() {
    Customer customer = container.getCustomer().newCustomer();
    customer.setCustomerId(300);
    customer.setName("samplename");

    final List<Integer> keys = new ArrayList<Integer>();
    keys.add(-200);
    keys.add(-201);
    keys.add(-202);

    final OrderCollection toBeLinked = container.getOrder().newOrderCollection();
    for (Integer key : keys) {
      final Order order = container.getOrder().newOrder();
      order.setOrderId(key);
      order.setCustomerId(300);
      order.setCustomer(customer);
      toBeLinked.add(order);
    }

    customer.setOrders(toBeLinked);

    final CustomerInfo customerInfo = container.getCustomerInfo().get(16);
    customerInfo.setInformation("some new info ...");
    customer.setInfo(customerInfo);

    final ContactDetails cd = customer.factory().newPrimaryContactInfo();
    cd.setAlternativeNames(Arrays.asList("alternative1", "alternative2"));
    cd.setEmailBag(Collections.<String> singleton("myemail@mydomain.org"));
    cd.setMobilePhoneBag(Collections.<Phone> emptySet());

    final ContactDetails bcd = customer.factory().newBackupContactInfo();
    bcd.setAlternativeNames(Arrays.asList("alternative3", "alternative4"));
    bcd.setEmailBag(Collections.<String> emptySet());
    bcd.setMobilePhoneBag(Collections.<Phone> emptySet());

    customer.setPrimaryContactInfo(cd);
    customer.setBackupContactInfo(Collections.<ContactDetails> singletonList(bcd));

    assertTrue(containerFactory.getContext().entityContext().
        isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customerInfo)));
    assertTrue(containerFactory.getContext().entityContext().
        isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customer)));
    for (Order linked : toBeLinked) {
      assertTrue(containerFactory.getContext().entityContext().
          isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(linked)));
    }

    container.flush();

    assertFalse(containerFactory.getContext().entityContext().
        isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customerInfo)));
    assertFalse(containerFactory.getContext().entityContext().
        isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customer)));
    for (Order linked : toBeLinked) {
      assertFalse(containerFactory.getContext().entityContext().
          isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(linked)));
    }

    assertEquals("some new info ...", container.getCustomerInfo().get(16).getInformation());

    container.getOrder().delete(toBeLinked);
    container.getCustomer().delete(customer.getCustomerId());

    assertTrue(containerFactory.getContext().entityContext().
        isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customer)));
    for (Order linked : toBeLinked) {
      assertTrue(containerFactory.getContext().entityContext().
          isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(linked)));
    }

    container.flush();

    assertFalse(containerFactory.getContext().entityContext().
        isAttached((EntityInvocationHandler) Proxy.getInvocationHandler(customer)));
    for (Order linked : toBeLinked) {
      assertFalse(containerFactory.getContext().entityContext().
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
