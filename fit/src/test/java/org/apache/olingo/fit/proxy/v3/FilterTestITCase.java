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

import org.apache.olingo.ext.proxy.api.Sort;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.Person;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Car;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types
    .CarCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types
    .EmployeeCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Order;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types
    .SpecialEmployeeCollection;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

//CHECKSTYLE:OFF (Maven checkstyle)
//CHECKSTYLE:ON (Maven checkstyle)

public class FilterTestITCase extends AbstractTestITCase {

  @Test
  public void filterOrderby() {
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.Car cars =
            container.getCar();

    CarCollection result = cars.filter(service.getClient().getFilterFactory().lt("VIN", 16)).execute();

    // 1. check that filtered entity set looks as expected
    assertEquals(5, result.size());

    // 2. extract VIN values - sorted ASC by default
    final List<Integer> vinsASC = new ArrayList<Integer>(5);
    for (Car car : result) {
      assertTrue(car.getVIN() < 16);
      vinsASC.add(car.getVIN());
    }

    // 3. add orderby clause to filter above
    result = cars.orderBy(new Sort("VIN", Sort.Direction.DESC)).execute();
    assertNotNull(result);
    assertEquals(5, result.size());

    // 4. extract again VIN value - now they were required to be sorted DESC
    final List<Integer> vinsDESC = new ArrayList<Integer>(5);
    for (Car car : result) {
      assertTrue(car.getVIN() < 16);
      vinsDESC.add(car.getVIN());
    }

    // 5. reverse vinsASC and expect to be equal to vinsDESC
    Collections.reverse(vinsASC);
    assertEquals(vinsASC, vinsDESC);
  }

  @Test
  public void single() {
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.Car cars =
            container.getCar();

    assertEquals(1, cars.filter(service.getClient().getFilterFactory().eq("VIN", 16)).execute().size());
  }

  @Test
  public void derived() {
    final Person person = container.getPerson();
    final EmployeeCollection employee = person.execute(EmployeeCollection.class);
    final SpecialEmployeeCollection specialEmployee = person.execute(SpecialEmployeeCollection.class);

    assertFalse(employee.isEmpty());
    assertFalse(specialEmployee.isEmpty());

    assertTrue(person.execute().size() > employee.size() + specialEmployee.size());
  }

  @Test
  public void loadWithSelect() {
    final Order order = container.getOrder().getByKey(-9);
    assertNull(order.getCustomerId());
    assertNull(order.getOrderId());

    order.select("OrderId");
    order.load();

    assertNull(order.getCustomerId());
    assertNotNull(order.getOrderId());

    order.clearQueryOptions();
    order.load();
    assertNotNull(order.getCustomerId());
    assertNotNull(order.getOrderId());
  }

  @Test
  public void loadWithSelectAndExpand() {
    final Customer customer = container.getCustomer().getByKey(-10);
    customer.expand("Info");
    customer.select("Info", "CustomerId");

    customer.load();
    assertEquals(11, customer.getInfo().getCustomerInfoId(), 0);
  }
}
