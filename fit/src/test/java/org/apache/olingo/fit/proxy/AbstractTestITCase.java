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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.proxy.staticservice.Service;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.InMemoryEntities;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Order;
import org.junit.BeforeClass;

public abstract class AbstractTestITCase extends AbstractBaseTestITCase {

  protected static String testStaticServiceRootURL;

  protected static String testDemoServiceRootURL;

  protected static String testKeyAsSegmentServiceRootURL;

  protected static String testActionOverloadingServiceRootURL;

  protected static String testOpenTypeServiceRootURL;

  protected static String testLargeModelServiceRootURL;

  protected static String testAuthServiceRootURL;

  protected static Service<EdmEnabledODataClient> service;

  protected static InMemoryEntities container;

  @BeforeClass
  public static void setUpODataServiceRoot() throws IOException {
    testStaticServiceRootURL = "http://localhost:9080/stub/StaticService/V40/Static.svc";
    testDemoServiceRootURL = "http://localhost:9080/stub/StaticService/V40/Demo.svc";
    testKeyAsSegmentServiceRootURL = "http://localhost:9080/stub/StaticService/V40/KeyAsSegment.svc";
    testActionOverloadingServiceRootURL = "http://localhost:9080/stub/StaticService/V40/ActionOverloading.svc";
    testOpenTypeServiceRootURL = "http://localhost:9080/stub/StaticService/V40/OpenType.svc";
    testLargeModelServiceRootURL = "http://localhost:9080/stub/StaticService/V40/Static.svc/large";
    testAuthServiceRootURL = "http://localhost:9080/stub/DefaultService.svc/V40/Static.svc";

    service = Service.getV4(testStaticServiceRootURL);
    service.getClient().getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    container = service.getEntityContainer(InMemoryEntities.class);
    assertNotNull(container);
    service.getContext().detachAll();
  }

  protected Customer readCustomer(final InMemoryEntities container, final int id) {
    final Customer customer = container.getCustomers().getByKey(id).load();
    assertNotNull(customer);
    assertEquals(id, customer.getPersonID(), 0);

    return customer;
  }

  protected void createPatchAndDeleteOrder(
      final InMemoryEntities container, final AbstractService<EdmEnabledODataClient> service) {

    // Create order ....
    final Order order = container.newEntityInstance(Order.class);
    order.setOrderID(105);

    final Calendar orderDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    orderDate.clear();
    orderDate.set(2011, 3, 4, 16, 3, 57);
    order.setOrderDate(new Timestamp(orderDate.getTimeInMillis()));

    order.setShelfLife(BigDecimal.ZERO);

    final PrimitiveCollection<BigDecimal> value = container.newPrimitiveCollection(BigDecimal.class);
    value.add(BigDecimal.TEN.negate());
    value.add(BigDecimal.TEN);
    order.setOrderShelfLifes(value);

    container.getOrders().add(order);
    container.flush();

    // Patch order ... (test for OLINGO-353)
    order.setShelfLife(BigDecimal.TEN);
    container.flush();

    Order actual = container.getOrders().getByKey(105).load();
    assertEquals(105, actual.getOrderID(), 0);
    assertEquals(orderDate.getTimeInMillis(), actual.getOrderDate().getTime());
    assertEquals(BigDecimal.TEN, actual.getShelfLife());
    assertEquals(2, actual.getOrderShelfLifes().size());

    // Delete order ...
    container.getOrders().delete(105);
    actual = container.getOrders().getByKey(105);
    assertNull(actual);

    service.getContext().detachAll();
    actual = container.getOrders().getByKey(105);
    assertNotNull(actual);

    container.getOrders().delete(105);
    actual = container.getOrders().getByKey(105);
    assertNull(actual);

    container.flush();

    service.getContext().detachAll();
    try {
      container.getOrders().getByKey(105).load();
      fail();
    } catch (IllegalArgumentException e) {
      // Expected Exception
    }
  }

  @Override
  protected ODataClient getClient() {
    throw new RuntimeException("This method should not be used from proxy tests.");
  }
}
