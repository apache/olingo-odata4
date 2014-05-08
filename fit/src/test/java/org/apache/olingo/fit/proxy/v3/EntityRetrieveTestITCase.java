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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Proxy;
import java.sql.Timestamp;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.Geospatial.Type;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.ext.proxy.commons.EntityTypeInvocationHandler;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        DefaultContainer;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.AllSpatialTypes;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.ComputerDetail;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.ConcurrencyInfo;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.Contractor;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.ContractorCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.CustomerInfo;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.Message;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.MessageKey;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.Order;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.OrderCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.Product;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.SpecialEmployee;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.SpecialEmployeeCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.Customer;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.Employee;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.EmployeeCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.Person;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.
        types.PersonCollection;
import org.junit.Test;

/**
 * This is the unit test class to check entity retrieve operations.
 */
public class EntityRetrieveTestITCase extends AbstractTest {

  protected DefaultContainer getContainer() {
    return container;
  }

  @Test
  public void exists() {
    assertTrue(getContainer().getCar().exists(15));
    assertFalse(getContainer().getComputerDetail().exists(-11));
  }

  @Test
  public void get() {
    readCustomer(getContainer(), -10);
  }

  @Test
  public void getAll() {
    final PersonCollection all = getContainer().getPerson().getAll();
    assertNotNull(all);
    assertFalse(all.isEmpty());
    for (Person person : all) {
      assertNotNull(person);
    }

    final EmployeeCollection employees = getContainer().getPerson().getAll(EmployeeCollection.class);
    assertNotNull(employees);
    assertFalse(employees.isEmpty());
    for (Employee employee : employees) {
      assertNotNull(employee);
    }

    final SpecialEmployeeCollection specialEmployees = 
            getContainer().getPerson().getAll(SpecialEmployeeCollection.class);
    assertNotNull(specialEmployees);
    assertFalse(specialEmployees.isEmpty());
    for (SpecialEmployee employee : specialEmployees) {
      assertNotNull(employee);
    }

    final ContractorCollection contractors = getContainer().getPerson().getAll(ContractorCollection.class);
    assertNotNull(contractors);
    assertFalse(contractors.isEmpty());
    for (Contractor contractor : contractors) {
      assertNotNull(contractor);
    }

    assertTrue(employees.size() > specialEmployees.size());
    assertTrue(all.size() > employees.size() + contractors.size());
  }

  @Test
  public void navigate() {
    final Order order = getContainer().getOrder().get(-9);
    assertNotNull(order);
    assertEquals(Integer.valueOf(-9), order.getOrderId());

    final ConcurrencyInfo concurrency = order.getConcurrency();
    assertNotNull(concurrency);
    assertEquals(Timestamp.valueOf("2012-02-12 11:32:50.005072026"), concurrency.getQueriedDateTime());
    assertEquals(Integer.valueOf(78), order.getCustomerId());
  }

  @Test
  public void withGeospatial() {
    final AllSpatialTypes allSpatialTypes = getContainer().getAllGeoTypesSet().get(-10);
    assertNotNull(allSpatialTypes);
    assertEquals(Integer.valueOf(-10), allSpatialTypes.getId());

    final MultiLineString geogMultiLine = allSpatialTypes.getGeogMultiLine();
    assertNotNull(geogMultiLine);
    assertEquals(Type.MULTILINESTRING, geogMultiLine.getType());
    assertEquals(Geospatial.Dimension.GEOGRAPHY, geogMultiLine.getDimension());
    assertFalse(geogMultiLine.isEmpty());

    final Point geogPoint = allSpatialTypes.getGeogPoint();
    assertNotNull(geogPoint);
    assertEquals(Type.POINT, geogPoint.getType());
    assertEquals(Geospatial.Dimension.GEOGRAPHY, geogPoint.getDimension());
    assertEquals(52.8606, geogPoint.getX(), 0);
    assertEquals(173.334, geogPoint.getY(), 0);
  }

  @Test
  public void withInlineEntry() {
    final Customer customer = readCustomer(getContainer(), -10);
    final CustomerInfo customerInfo = customer.getInfo();
    assertNotNull(customerInfo);
    assertEquals(Integer.valueOf(11), customerInfo.getCustomerInfoId());
  }

  @Test
  public void withInlineFeed() {
    final Customer customer = readCustomer(getContainer(), -10);
    final OrderCollection orders = customer.getOrders();
    assertFalse(orders.isEmpty());
  }

  @Test
  public void withActions() {
    final ComputerDetail computerDetail = getContainer().getComputerDetail().get(-10);
    assertEquals(Integer.valueOf(-10), computerDetail.getComputerDetailId());

    try {
      assertNotNull(
              computerDetail.operations().getClass().getMethod(
              "resetComputerDetailsSpecifications", Collection.class, Timestamp.class));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void multiKey() {
    final MessageKey messageKey = new MessageKey();
    messageKey.setFromUsername("1");
    messageKey.setMessageId(-10);

    final Message message = getContainer().getMessage().get(messageKey);
    assertNotNull(message);
    assertEquals("1", message.getFromUsername());
  }

  @Test
  public void checkForETag() {
    Product product = getContainer().getProduct().get(-10);
    assertTrue(StringUtils.isNotBlank(
            ((EntityTypeInvocationHandler) Proxy.getInvocationHandler(product)).getETag()));
  }
}
