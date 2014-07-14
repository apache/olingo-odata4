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
package org.apache.olingo.fit.proxy.v4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.ext.proxy.api.Search;
import org.apache.olingo.ext.proxy.api.Sort;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.People;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer;
//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Person;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.PersonCollection;
//CHECKSTYLE:ON (Maven checkstyle)
import org.junit.Test;

public class FilterTestITCase extends AbstractTestITCase {

  @Test
  public void filterOrderby() {
    final People people = container.getPeople();

    PersonCollection result =
            people.filter(containerFactory.getClient().getFilterFactory().lt("PersonID", 3)).execute();

    // 1. check that result looks as expected
    assertEquals(2, result.size());

    // 2. extract PersonID values - sorted ASC by default
    final List<Integer> former = new ArrayList<Integer>(2);
    for (Person person : result) {
      final Integer personID = person.getPersonID();
      assertTrue(personID < 3);
      former.add(personID);
    }

    // 3. add orderby clause to filter above
    result = people.orderBy(new Sort("PersonID", Sort.Direction.DESC)).execute();
    assertEquals(2, result.size());

    // 4. extract again VIN value - now they were required to be sorted DESC
    final List<Integer> latter = new ArrayList<Integer>(2);
    for (Person person : result) {
      final Integer personID = person.getPersonID();
      assertTrue(personID < 3);
      latter.add(personID);
    }

    // 5. reverse latter and expect to be equal to former
    Collections.reverse(latter);
    assertEquals(former, latter);
  }

  @Test
  public void search() {
    final Search<Person, PersonCollection> search = container.getPeople().createSearch().setSearch(
            containerFactory.getClient().getSearchFactory().or(
            containerFactory.getClient().getSearchFactory().literal("Bob"),
            containerFactory.getClient().getSearchFactory().literal("Jill")));

    final PersonCollection result = search.getResult();
    assertFalse(result.isEmpty());
  }

  @Test
  public void loadWithSelect() {
    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Order order =
            container.getOrders().getByKey(8);
    assertNull(order.getOrderID());
    assertNull(order.getOrderDate());

    order.select("OrderID");
    order.load();

    assertNull(order.getOrderDate());
    assertNotNull(order.getOrderID());

    order.clear();
    order.load();
    assertNotNull(order.getOrderDate());
    assertNotNull(order.getOrderID());
  }

  @Test
  public void loadWithSelectAndExpand() {
    final Customer customer = container.getCustomers().getByKey(1);

    customer.expand("Orders");
    customer.select("Orders", "PersonID");

    customer.load();
    assertEquals(1, customer.getOrders().size());
  }
}
