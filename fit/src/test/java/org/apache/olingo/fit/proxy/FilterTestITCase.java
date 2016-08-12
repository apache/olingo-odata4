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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.ext.proxy.api.Search;
import org.apache.olingo.ext.proxy.api.Sort;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.People;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Employee;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.EmployeeCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Person;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PersonCollection;
import org.junit.Test;

// CHECKSTYLE:ON (Maven checkstyle)

public class FilterTestITCase extends AbstractTestITCase {

  @Test
  public void testFilterWithEntityType() {
    final People people = container.getPeople();
    final EmployeeCollection response = people.filter(service.getClient().getFilterFactory().lt("PersonID", 4))
        .execute(EmployeeCollection.class);

    assertEquals(1, response.size());

    for (final Employee employee : response) {
      assertEquals(Integer.valueOf(3), employee.getPersonID());
    }
  }

  @Test
  public void filterOrderby() {
    final People people = container.getPeople();

    PersonCollection result =
        people.filter(service.getClient().getFilterFactory().lt("PersonID", 3)).execute();

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
        service.getClient().getSearchFactory().or(
            service.getClient().getSearchFactory().literal("Bob"),
            service.getClient().getSearchFactory().literal("Jill")));

    final PersonCollection result = search.getResult();
    assertFalse(result.isEmpty());
  }
}
