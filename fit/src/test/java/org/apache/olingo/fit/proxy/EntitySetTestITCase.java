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

import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Person;
import org.junit.Assert;
import org.junit.Test;

/**
 * This is the unit test class to check basic feed operations.
 */
public class EntitySetTestITCase extends AbstractTestITCase {

  @Test
  public void count() {
    assertNotNull(container.getOrders());
    Assert.assertEquals(2, container.getOrders().count().longValue());
  }

  @Test
  public void getAll() {
    int count = 0;
    for (Customer customer : container.getCustomers().execute()) {
      assertNotNull(customer);
      count++;
    }
    assertEquals(2, count);
  }

  @Test
  public void iterator() {
    int count = 0;
    for (Customer customer : container.getCustomers()) {
      assertNotNull(customer);
      count++;
    }
    assertEquals(2, count);
  }

  @Test
  public void readEntitySetWithNextLink() {
    int count = 0;
    for (Person people : container.getPeople().execute()) {
      assertNotNull(people);
      count++;
    }
    assertEquals(5, count);

    int iterating = 0;
    for (Person person : container.getPeople()) {
      assertNotNull(person);
      iterating++;
    }
    assertEquals(count + 1, iterating);
  }
}
