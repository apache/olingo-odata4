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
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.ext.proxy.EntityContainerFactory;
import org.apache.olingo.ext.proxy.context.EntityContext;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.InMemoryEntities;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Order;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTestITCase {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractTestITCase.class);

  protected static final String TEST_PRODUCT_TYPE = "Microsoft.Test.OData.Services.AstoriaDefaultService.Product";

  protected static String testStaticServiceRootURL;

  protected static String testKeyAsSegmentServiceRootURL;

  protected static String testActionOverloadingServiceRootURL;

  protected static String testOpenTypeServiceRootURL;

  protected static String testLargeModelServiceRootURL;

  protected static String testAuthServiceRootURL;

  protected final EntityContext entityContext = EntityContainerFactory.getContext().entityContext();

  protected static EntityContainerFactory containerFactory;

  protected static InMemoryEntities container;

  @BeforeClass
  public static void setUpODataServiceRoot() throws IOException {
    testStaticServiceRootURL = "http://localhost:9080/stub/StaticService/V40/Static.svc";
    testKeyAsSegmentServiceRootURL = "http://localhost:9080/stub/StaticService/V40/KeyAsSegment.svc";
    testActionOverloadingServiceRootURL = "http://localhost:9080/stub/StaticService/V40/ActionOverloading.svc";
    testOpenTypeServiceRootURL = "http://localhost:9080/stub/StaticService/V40/OpenType.svc";
    testLargeModelServiceRootURL = "http://localhost:9080/stub/StaticService/V40/Static.svc/large";
    testAuthServiceRootURL = "http://localhost:9080/stub/DefaultService.svc";

    containerFactory = EntityContainerFactory.getV4(testStaticServiceRootURL);
    containerFactory.getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    container = containerFactory.getEntityContainer(InMemoryEntities.class);
    assertNotNull(container);
    EntityContainerFactory.getContext().detachAll();
  }

  protected Customer readCustomer(final InMemoryEntities container, int id) {
    final Customer customer = container.getCustomers().get(id);
    assertNotNull(customer);
    assertEquals(id, customer.getPersonID(), 0);

    return customer;
  }

  protected void createAndDeleteOrder(final InMemoryEntities container) {
    final Order order = container.getOrders().newOrder();
    order.setOrderID(105);

    final Calendar orderDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    orderDate.clear();
    orderDate.set(2011, 3, 4, 16, 3, 57);
    order.setOrderDate(orderDate);

    order.setShelfLife(BigDecimal.TEN);
    order.setOrderShelfLifes(Arrays.asList(new BigDecimal[] {BigDecimal.TEN.negate(), BigDecimal.TEN}));

    container.flush();

    Order actual = container.getOrders().get(105);
    assertEquals(105, actual.getOrderID(), 0);
    assertEquals(orderDate.getTimeInMillis(), actual.getOrderDate().getTimeInMillis());
    assertEquals(BigDecimal.TEN, actual.getShelfLife());
    assertEquals(2, actual.getOrderShelfLifes().size());

    container.getOrders().delete(105);
    actual = container.getOrders().get(105);
    assertNull(actual);

    entityContext.detachAll();
    actual = container.getOrders().get(105);
    assertNotNull(actual);

    container.getOrders().delete(105);
    actual = container.getOrders().get(105);
    assertNull(actual);

    container.flush();

    entityContext.detachAll();
    actual = container.getOrders().get(105);
    assertNull(actual);
  }
}
