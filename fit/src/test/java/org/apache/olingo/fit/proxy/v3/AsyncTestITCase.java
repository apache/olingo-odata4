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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.olingo.commons.api.ODataRuntimeException;
import org.junit.Test;

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.EmployeeCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Product;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployeeCollection;
//CHECKSTYLE:ON (Maven checkstyle)

public class AsyncTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveEntitySet() throws InterruptedException, ExecutionException {
    final Future<ProductCollection> futureProds = container.getProduct().executeAsync();
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

    final Future<List<ODataRuntimeException>> futureFlush = container.flushAsync();
    assertNotNull(futureFlush);

    while (!futureFlush.isDone()) {
      Thread.sleep(1000L);
    }

    final Future<Product> futureProd = container.getProduct().getByKey(-10).loadAsync();
    assertEquals("AsyncTest#updateEntity " + random, futureProd.get().load().getDescription());
  }

  @Test
  public void polymorphQuery() throws Exception {
    final Future<EmployeeCollection> queryEmployee =
            container.getPerson().executeAsync(EmployeeCollection.class);
    assertFalse(queryEmployee.get().isEmpty());

    final Future<SpecialEmployeeCollection> querySpecialEmployee =
            container.getPerson().executeAsync(SpecialEmployeeCollection.class);
    assertFalse(querySpecialEmployee.get().isEmpty());

    assertTrue(container.getPerson().execute().size()
            > container.getPerson().execute(EmployeeCollection.class).size()
            + container.getPerson().execute(SpecialEmployeeCollection.class).size());
  }
}
