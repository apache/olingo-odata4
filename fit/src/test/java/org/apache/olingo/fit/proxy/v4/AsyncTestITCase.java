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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.olingo.ext.proxy.api.AsyncCall;
//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CustomerCollection;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Person;
//CHECKSTYLE:ON (Maven checkstyle)
import org.junit.Test;

public class AsyncTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveEntitySet() throws InterruptedException, ExecutionException {
    final Future<CustomerCollection> futureCustomers =
        new AsyncCall<CustomerCollection>(containerFactory.getClient().getConfiguration()) {

          @Override
          public CustomerCollection call() {
            return container.getCustomers().getAll();
          }
        };
    assertNotNull(futureCustomers);

    while (!futureCustomers.isDone()) {
      Thread.sleep(1000L);
    }

    final CustomerCollection customers = futureCustomers.get();
    assertNotNull(customers);
    assertFalse(customers.isEmpty());
    for (Customer customer : customers) {
      assertNotNull(customer);
    }
  }

  @Test
  public void updateEntity() throws InterruptedException {
    final String randomFirstName = RandomStringUtils.random(10, "abcedfghijklmnopqrstuvwxyz");

    Person person = container.getPeople().get(1);
    person.setFirstName(randomFirstName);

    final Future<Void> futureFlush = new AsyncCall<Void>(containerFactory.getClient().getConfiguration()) {

      @Override
      public Void call() {
        container.flush();
        return null;
      }
    };
    assertNotNull(futureFlush);

    while (!futureFlush.isDone()) {
      Thread.sleep(1000L);
    }

    new AsyncCall<Person>(containerFactory.getClient().getConfiguration()) {

      @Override
      public Person call() {
        return container.getPeople().get(1);
      }
    };

    assertEquals(randomFirstName, person.getFirstName());
  }
}
