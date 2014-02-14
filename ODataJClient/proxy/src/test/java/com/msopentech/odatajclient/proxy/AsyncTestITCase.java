/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.proxy;

import static com.msopentech.odatajclient.proxy.AbstractTest.container;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.msopentech.odatajclient.proxy.api.AsyncCall;
import com.msopentech.odatajclient.proxy.api.Query;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.EmployeeCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Product;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployee;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployeeCollection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Test;

public class AsyncTestITCase extends AbstractTest {

    @Test
    public void retrieveEntitySet() throws InterruptedException, ExecutionException {
        final Future<ProductCollection> futureProds =
                new AsyncCall<ProductCollection>(containerFactory.getConfiguration()) {

                    @Override
                    public ProductCollection call() {
                        return container.getProduct().getAll();
                    }
                };
        assertNotNull(futureProds);

        while (!futureProds.isDone()) {
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

        final Future<Void> futureFlush = new AsyncCall<Void>(containerFactory.getConfiguration()) {

            @Override
            public Void call() {
                container.flush();
                return null;
            }
        };
        assertNotNull(futureFlush);

        while (!futureFlush.isDone()) {
        }

        final Future<Product> futureProd = new AsyncCall<Product>(containerFactory.getConfiguration()) {

            @Override
            public Product call() {
                return container.getProduct().get(-10);
            }
        };

        assertEquals("AsyncTest#updateEntity " + random, futureProd.get().getDescription());
    }

    @Test
    public void polymorphQuery() throws Exception {
        final Future<Query<Employee, EmployeeCollection>> queryEmployee =
                new AsyncCall<Query<Employee, EmployeeCollection>>(containerFactory.getConfiguration()) {

                    @Override
                    public Query<Employee, EmployeeCollection> call() {
                        return container.getPerson().createQuery(EmployeeCollection.class);
                    }
                };
        assertFalse(queryEmployee.get().getResult().isEmpty());

        final Future<Query<SpecialEmployee, SpecialEmployeeCollection>> querySpecialEmployee =
                new AsyncCall<Query<SpecialEmployee, SpecialEmployeeCollection>>(containerFactory.getConfiguration()) {

                    @Override
                    public Query<SpecialEmployee, SpecialEmployeeCollection> call() {
                        return container.getPerson().createQuery(SpecialEmployeeCollection.class);
                    }
                };
        assertFalse(querySpecialEmployee.get().getResult().isEmpty());

        assertTrue(container.getPerson().getAll().size()
                > queryEmployee.get().getResult().size() + querySpecialEmployee.get().getResult().size());
    }
}
