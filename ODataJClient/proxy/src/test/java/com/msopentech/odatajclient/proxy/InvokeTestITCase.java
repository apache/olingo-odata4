/**
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
package com.msopentech.odatajclient.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.msopentech.odatajclient.engine.data.ODataTimestamp;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.ComputerDetail;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.CustomerCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.EmployeeCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Product;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class InvokeTestITCase extends AbstractTest {

    @Test
    public void getWithNoParams() {
        // 1. primitive result
        final String string = container.getPrimitiveString();
        assertEquals("Foo", string);

        // 2. complex collection result
        final Collection<ContactDetails> details = container.entityProjectionReturnsCollectionOfComplexTypes();
        assertFalse(details.isEmpty());
        for (ContactDetails detail : details) {
            assertNotNull(detail);
        }
        assertNotNull(details.iterator().next());
    }

    @Test
    public void getWithParam() {
        // 1. primitive result
        assertEquals(155, container.getArgumentPlusOne(154).intValue());

        // 2. entity collection result
        final CustomerCollection customers = container.getSpecificCustomer(StringUtils.EMPTY);
        assertNotNull(customers);
        assertFalse(customers.isEmpty());
        final Set<Integer> customerIds = new HashSet<Integer>(customers.size());
        for (Customer customer : customers) {
            assertNotNull(customer);
            customerIds.add(customer.getCustomerId());
        }
        assertTrue(customerIds.contains(-8));
    }

    @Test
    public void entityBoundPost() {
        // 0. create an employee
        final Integer id = 101;

        Employee employee = container.getPerson().newEmployee();
        employee.setPersonId(id);
        employee.setName("sample employee from proxy");
        employee.setManagersPersonId(-9918);
        employee.setSalary(2147483647);
        employee.setTitle("CEO");

        container.flush();

        employee = container.getPerson().get(id, Employee.class);
        assertNotNull(employee);
        assertEquals(id, employee.getPersonId());

        try {
            // 1. invoke action bound to the employee just created
            employee.sack();

            // 2. check that invoked action has effectively run
            employee = container.getPerson().get(id, Employee.class);
            assertEquals(0, employee.getSalary().intValue());
            assertTrue(employee.getTitle().endsWith("[Sacked]"));
        } finally {
            // 3. remove the test employee
            container.getPerson().delete(employee.getPersonId());
            container.flush();
        }
    }

    @Test
    public void entityCollectionBoundPostWithParam() {
        EmployeeCollection employees = container.getPerson().getAll(EmployeeCollection.class);
        assertFalse(employees.isEmpty());
        final Map<Integer, Integer> preSalaries = new HashMap<Integer, Integer>(employees.size());
        for (Employee employee : employees) {
            preSalaries.put(employee.getPersonId(), employee.getSalary());
        }
        assertFalse(preSalaries.isEmpty());

        employees.increaseSalaries(1);

        employees = container.getPerson().getAll(EmployeeCollection.class);
        assertFalse(employees.isEmpty());
        for (Employee employee : employees) {
            assertTrue(preSalaries.get(employee.getPersonId()) < employee.getSalary());
        }
    }

    @Test
    public void changeProductDimensions() {
        // 0. create a product
        final Integer id = 101;

        Product product = container.getProduct().newProduct();
        product.setProductId(id);
        product.setDescription("New product");

        final Dimensions origDimensions = new Dimensions();
        origDimensions.setDepth(BigDecimal.ZERO);
        origDimensions.setHeight(BigDecimal.ZERO);
        origDimensions.setWidth(BigDecimal.ZERO);
        product.setDimensions(origDimensions);

        container.flush();

        product = container.getProduct().get(id);
        assertNotNull(product);
        assertEquals(id, product.getProductId());
        assertEquals(BigDecimal.ZERO, product.getDimensions().getDepth());
        assertEquals(BigDecimal.ZERO, product.getDimensions().getHeight());
        assertEquals(BigDecimal.ZERO, product.getDimensions().getWidth());

        try {
            // 1. invoke action bound to the product just created
            final Dimensions newDimensions = new Dimensions();
            newDimensions.setDepth(BigDecimal.ONE);
            newDimensions.setHeight(BigDecimal.ONE);
            newDimensions.setWidth(BigDecimal.ONE);

            product.changeProductDimensions(newDimensions);

            // 2. check that invoked action has effectively run
            product = container.getProduct().get(id);
            assertEquals(BigDecimal.ONE, product.getDimensions().getDepth());
            assertEquals(BigDecimal.ONE, product.getDimensions().getHeight());
            assertEquals(BigDecimal.ONE, product.getDimensions().getWidth());
        } finally {
            // 3. remove the test product
            container.getProduct().delete(product.getProductId());
            container.flush();
        }
    }

    @Test
    public void resetComputerDetailsSpecifications() {
        // 0. create a computer detail
        final Integer id = 101;

        ComputerDetail computerDetail = container.getComputerDetail().newComputerDetail();
        computerDetail.setComputerDetailId(id);
        computerDetail.setSpecificationsBag(Collections.singleton("First spec"));

        container.flush();

        computerDetail = container.getComputerDetail().get(id);
        assertNotNull(computerDetail);
        assertEquals(id, computerDetail.getComputerDetailId());
        assertEquals(1, computerDetail.getSpecificationsBag().size());
        assertTrue(computerDetail.getSpecificationsBag().contains("First spec"));
        assertEquals(ODataTimestamp.parse(EdmSimpleType.DateTime, "0001-01-01T00:00:00"),
                computerDetail.getPurchaseDate());

        try {
            // 1. invoke action bound to the computer detail just created
            computerDetail.resetComputerDetailsSpecifications(
                    Collections.singleton("Second spec"),
                    ODataTimestamp.getInstance(EdmSimpleType.DateTime, new Timestamp(System.currentTimeMillis())));

            // 2. check that invoked action has effectively run
            computerDetail = container.getComputerDetail().get(id);
            assertNotNull(computerDetail);
            assertEquals(id, computerDetail.getComputerDetailId());
            assertEquals(1, computerDetail.getSpecificationsBag().size());
            assertTrue(computerDetail.getSpecificationsBag().contains("Second spec"));
            assertNotEquals(ODataTimestamp.parse(EdmSimpleType.DateTime, "0001-01-01T00:00:00"),
                    computerDetail.getPurchaseDate());
        } finally {
            // 3. remove the test product
            container.getComputerDetail().delete(computerDetail.getComputerDetailId());
            container.flush();
        }
    }
}
