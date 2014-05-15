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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.olingo.ext.proxy.api.NonUniqueResultException;
import org.apache.olingo.ext.proxy.api.Filter;
import org.apache.olingo.ext.proxy.api.Sort;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Car;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.
        CarCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.
        EmployeeCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.
        SpecialEmployee;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.
        SpecialEmployeeCollection;
import org.junit.Test;

public class FilterTestITCase extends AbstractTestITCase {

  @Test
  public void filterOrderby() {
    final Filter<Car, CarCollection> filter = container.getCar().createFilter().
            setFilter(containerFactory.getClient().getFilterFactory().lt("VIN", 16));
    CarCollection result = filter.getResult();
    assertNotNull(result);

    // 1. check that filtered entity set looks as expected
    assertEquals(5, result.size());

    // 2. extract VIN values - sorted ASC by default
    final List<Integer> vinsASC = new ArrayList<Integer>(5);
    for (Car car : result) {
      assertTrue(car.getVIN() < 16);
      vinsASC.add(car.getVIN());
    }

    // 3. add orderby clause to filter above
    result = filter.setOrderBy(new Sort("VIN", Sort.Direction.DESC)).getResult();
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
    final Filter<Car, CarCollection> filter = container.getCar().createFilter().
            setFilter(containerFactory.getClient().getFilterFactory().lt("VIN", 16));

    Exception exception = null;
    try {
      filter.getSingleResult();
      fail();
    } catch (NonUniqueResultException e) {
      exception = e;
    }
    assertNotNull(exception);

    filter.setFilter(containerFactory.getClient().getFilterFactory().eq("VIN", 16));
    final Car result = filter.getSingleResult();
    assertNotNull(result);
  }

  @Test
  public void derived() {
    final Filter<Employee, EmployeeCollection> filterEmployee =
            container.getPerson().createFilter(EmployeeCollection.class);
    assertFalse(filterEmployee.getResult().isEmpty());

    final Filter<SpecialEmployee, SpecialEmployeeCollection> filterSpecialEmployee =
            container.getPerson().createFilter(SpecialEmployeeCollection.class);
    assertFalse(filterSpecialEmployee.getResult().isEmpty());

    assertTrue(container.getPerson().getAll().size()
            > filterEmployee.getResult().size() + filterSpecialEmployee.getResult().size());
  }
}
