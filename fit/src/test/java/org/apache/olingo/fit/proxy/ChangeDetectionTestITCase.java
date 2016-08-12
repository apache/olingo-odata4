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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Proxy;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang3.RandomUtils;
import org.apache.olingo.ext.proxy.api.ComplexType;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.commons.ComplexInvocationHandler;
import org.apache.olingo.ext.proxy.commons.EntityCollectionInvocationHandler;
import org.apache.olingo.ext.proxy.commons.EntityInvocationHandler;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.InMemoryEntities;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Account;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Order;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.OrderCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PaymentInstrument;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PaymentInstrumentCollection;
// CHECKSTYLE:ON (Maven checkstyle)
import org.junit.Test;

public class ChangeDetectionTestITCase extends AbstractTestITCase {

  @Test
  public void entityUnchangedOnGetProperty() {
    final Customer customer = getContainer().getCustomers().getByKey(1).load();
    assertFalse(isChanged(customer));

    customer.getLastName();

    assertFalse(isChanged(customer));
  }

  @Test
  public void entityChangedOnSetProperty() {
    final Customer customer = getContainer().getCustomers().getByKey(1).load();
    assertFalse(isChanged(customer));

    customer.setLastName("Test");

    assertTrue(isChanged(customer));

    getContainer().flush();
    assertFalse(isChanged(customer));
  }

  @Test
  public void entityUnchangedOnGetComplexProperty() {
    final Customer customer = getContainer().getCustomers().getByKey(1).load();
    assertFalse(isChanged(customer));

    final Address homeAddress = customer.getHomeAddress();
    assertFalse(isChanged(customer));

    homeAddress.getCity();
    assertFalse(isChanged(homeAddress));
    assertFalse(isChanged(customer));
  }

  @Test
  public void entityChangedOnSetComplexProperty() {
    final Customer customer = getContainer().getCustomers().getByKey(2).load();
    assertFalse(isChanged(customer));

    final Address newAdress = getContainer().newComplexInstance(Address.class);
    customer.setHomeAddress(newAdress);

    assertTrue(isChanged(customer));

    getContainer().flush();
    assertFalse(isChanged(customer));
  }

  @Test
  public void entityChangedOnSetPropertyOfComplexProperty() {
    final Customer customer = getContainer().getCustomers().getByKey(1).load();
    assertFalse(isChanged(customer));

    final Address homeAddress = customer.getHomeAddress();
    homeAddress.setCity("Test");

    assertTrue(isChanged(customer));

    getContainer().flush();
    assertFalse(isChanged(customer));
  }

  @Test
  public void entityUnchangedOnGetNavigationProperty() {
    final Customer customer = getContainer().getCustomers().getByKey(1).load();
    assertFalse(isChanged(customer));

    customer.getOrders();

    assertFalse(isChanged(customer));
  }

  @Test
  public void entityChangedOnAddNavigationProperty() {
    final Account account = getContainer().getAccounts().getByKey(101).load();
    assertFalse(isChanged(account));

    final PaymentInstrumentCollection instruments = account.getMyPaymentInstruments().execute();
    assertFalse(isChanged(account));

    final PaymentInstrument instrument = getContainer().newEntityInstance(PaymentInstrument.class);
    final int id = RandomUtils.nextInt(101999, 105000);
    instrument.setPaymentInstrumentID(id);
    instrument.setFriendlyName("New one");
    instrument.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    instruments.add(instrument);

    assertTrue(isChanged(instrument));
    assertFalse(isChanged(account));

    getContainer().flush();
    assertFalse(isChanged(instrument));
  }

  @Test
  public void entityCollectionUnchangedOnGet() {
    final Customer customer = getContainer().getCustomers().getByKey(1).load();
    assertFalse(isChanged(customer));

    final OrderCollection orders = customer.getOrders().execute();
    assertFalse(isChanged(customer));

    for (Order order : orders) {
      assertFalse(isChanged(order));
      order.getOrderDate();
      assertFalse(isChanged(order));
    }

    assertFalse(isChanged(customer));
  }

  protected InMemoryEntities getContainer() {
    return container;
  }

  protected boolean isChanged(final EntityType<?> entity) {
    EntityInvocationHandler invocationHandler = getInvocationHandler(entity);
    return invocationHandler.isChanged();
  }

  protected boolean isChanged(final ComplexType<?> complex) {
    ComplexInvocationHandler invocationHandler = getInvocationHandler(complex);
    return invocationHandler.isChanged();
  }

  protected EntityInvocationHandler getInvocationHandler(final EntityType<?> entity) {
    return (EntityInvocationHandler) Proxy.getInvocationHandler(entity);
  }

  protected ComplexInvocationHandler getInvocationHandler(final ComplexType<?> complex) {
    return (ComplexInvocationHandler) Proxy.getInvocationHandler(complex);
  }

  protected EntityCollectionInvocationHandler<?> getInvocationHandler(
      final EntityCollection<?, ?, ?> complex) {
    return (EntityCollectionInvocationHandler<?>) Proxy.getInvocationHandler(complex);
  }
}
