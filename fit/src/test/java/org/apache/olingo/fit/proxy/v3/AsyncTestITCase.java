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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.olingo.ext.proxy.api.AsyncCall;
import org.apache.olingo.ext.proxy.api.Filter;
//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.EmployeeCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Product;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployee;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployeeCollection;
//CHECKSTYLE:ON (Maven checkstyle)
import org.junit.Test;

public class AsyncTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveEntitySet() throws InterruptedException, ExecutionException {
    final Future<ProductCollection> futureProds =
        new AsyncCall<ProductCollection>(containerFactory.getClient().getConfiguration()) {

          @Override
          public ProductCollection call() {
            return container.getProduct().getAll();
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

    final Product product = container.getProduct().get(-10);
    product.setDescription("AsyncTest#updateEntity " + random);

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

    final Future<Product> futureProd = new AsyncCall<Product>(containerFactory.getClient().getConfiguration()) {

      @Override
      public Product call() {
        return container.getProduct().get(-10);
      }
    };

    assertEquals("AsyncTest#updateEntity " + random, futureProd.get().load().getDescription());
  }

  @Test
  public void polymorphQuery() throws Exception {
    final Future<Filter<Employee, EmployeeCollection>> queryEmployee =
        new AsyncCall<Filter<Employee, EmployeeCollection>>(containerFactory.getClient().getConfiguration()) {

          @Override
          public Filter<Employee, EmployeeCollection> call() {
            return container.getPerson().createFilter(EmployeeCollection.class);
          }
        };
    assertFalse(queryEmployee.get().getResult().isEmpty());

    final Future<Filter<SpecialEmployee, SpecialEmployeeCollection>> querySpecialEmployee =
        new AsyncCall<Filter<SpecialEmployee, SpecialEmployeeCollection>>(
            containerFactory.getClient().getConfiguration()) {

          @Override
          public Filter<SpecialEmployee, SpecialEmployeeCollection> call() {
            return container.getPerson().createFilter(SpecialEmployeeCollection.class);
          }
        };
    assertFalse(querySpecialEmployee.get().getResult().isEmpty());

    assertTrue(container.getPerson().getAll().size()
    > queryEmployee.get().getResult().size() + querySpecialEmployee.get().getResult().size());
  }
}
