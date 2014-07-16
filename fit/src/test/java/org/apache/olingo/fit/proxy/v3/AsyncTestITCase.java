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
package org.apache.olingo.fit.proxy.v3;

import org.apache.olingo.ext.proxy.api.AsyncCall;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.Person;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types
    .EmployeeCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Product;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types
    .ProductCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types
    .SpecialEmployeeCollection;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//CHECKSTYLE:OFF (Maven checkstyle)
//CHECKSTYLE:ON (Maven checkstyle)

public class AsyncTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveEntitySet() throws InterruptedException, ExecutionException {
    final Future<ProductCollection> futureProds =
            new AsyncCall<ProductCollection>(service.getClient().getConfiguration()) {
      @Override
      public ProductCollection call() {
        return container.getProduct().execute();
      }
    };
    assertNotNull(futureProds);

    while (!futureProds.isDone()) {
      Thread.sleep(1000L);
    }

    final ProductCollection products = futureProds.get();
    assertNotNull(products);
    assertFalse(products.isEmpty());
    for (Product product : products) {
      assertNotNull(product);
    }
  }

  @Test
  public void updateEntity() throws InterruptedException, ExecutionException {
    final String random = UUID.randomUUID().toString();

    final Product product = container.getProduct().getByKey(-10);
    product.setDescription("AsyncTest#updateEntity " + random);

    final Future<Void> futureFlush = new AsyncCall<Void>(service.getClient().getConfiguration()) {
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

    final Future<Product> futureProd = new AsyncCall<Product>(service.getClient().getConfiguration()) {
      @Override
      public Product call() {
        return container.getProduct().getByKey(-10);
      }
    };

    assertEquals("AsyncTest#updateEntity " + random, futureProd.get().load().getDescription());
  }

  @Test
  public void polymorphQuery() throws Exception {
    final Future<Person> queryEmployee = new AsyncCall<Person>(service.getClient().getConfiguration()) {
      @Override
      public Person call() {
        return container.getPerson();
      }
    };
    assertFalse(queryEmployee.get().execute(EmployeeCollection.class).isEmpty());

    final Future<Person> querySpecialEmployee = new AsyncCall<Person>(service.getClient().getConfiguration()) {
      @Override
      public Person call() {
        return container.getPerson();
      }
    };
    assertFalse(querySpecialEmployee.get().execute(SpecialEmployeeCollection.class).isEmpty());

    assertTrue(container.getPerson().execute().size()
            > container.getPerson().execute(EmployeeCollection.class).size()
            + container.getPerson().execute(SpecialEmployeeCollection.class).size());
  }
}
