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
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.CustomerCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Person;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PersonComposableInvoker;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductCollectionComposableInvoker;
// CHECKSTYLE:ON (Maven checkstyle)
import org.junit.Test;

public class AsyncTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveEntitySet() throws InterruptedException, ExecutionException {
    final Future<CustomerCollection> futureCustomers = container.getCustomers().executeAsync();
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
  public void updateEntity() throws Exception {
    final String randomFirstName = RandomStringUtils.random(10, "abcedfghijklmnopqrstuvwxyz");

    final Person person = container.getPeople().getByKey(1);
    person.setFirstName(randomFirstName);

    final Future<Void> futureFlush = container.flushAsync();
    assertNotNull(futureFlush);

    while (!futureFlush.isDone()) {
      Thread.sleep(1000L);
    }

    final Future<? extends Person> futurePerson = container.getPeople().getByKey(1).loadAsync();
    assertEquals(randomFirstName, futurePerson.get().getFirstName());
  }

  @Test
  public void invoke() throws Exception {
    final ProductCollectionComposableInvoker invoker1 = container.operations().getAllProducts();

    final Future<ProductCollection> future1 = invoker1.operations().
        discount(10).
        filter("Name eq XXXX").
        select("Name", "ProductDetail").
        expand("ProductDetail").
        orderBy("Name").skip(3).top(5).executeAsync();
    while (!future1.isDone()) {
      Thread.sleep(1000L);
    }
    assertFalse(future1.get().isEmpty());

    final PersonComposableInvoker invoker2 = container.operations().getPerson2("London");

    final Future<Person> future2 = invoker2.select("Name").
        expand("Order").executeAsync();
    while (!future2.isDone()) {
      Thread.sleep(1000L);
    }
    assertNotNull(future2.get());
  }
}
