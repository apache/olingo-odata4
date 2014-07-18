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

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ComputerDetail;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.CustomerCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.EmployeeCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Product;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
//CHECKSTYLE:ON (Maven checkstyle)

public class InvokeTestITCase extends AbstractTestITCase {

  @Test
  public void getWithNoParams() {
    // 1. primitive result
    final String string = container.operations().getPrimitiveString();
    assertEquals("Foo", string);

    // 2. complex collection result
    final Collection<ContactDetails> details = container.operations().entityProjectionReturnsCollectionOfComplexTypes();
    assertFalse(details.isEmpty());
    for (ContactDetails detail : details) {
      assertNotNull(detail);
    }
    assertNotNull(details.iterator().next());
  }

  @Test
  public void getWithParam() {
    // 1. primitive result
    assertEquals(155, container.operations().getArgumentPlusOne(154), 0);

    // 2. entity collection result
    final CustomerCollection customers = container.operations().getSpecificCustomer(StringUtils.EMPTY);
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

    Employee employee = service.newEntity(Employee.class);
    employee.setPersonId(id);
    employee.setName("sample employee from proxy");
    employee.setManagersPersonId(-9918);
    employee.setSalary(2147483647);
    employee.setTitle("CEO");

    container.getPerson().add(employee);
    container.flush();

    employee = container.getPerson().getByKey(id, Employee.class).load();
    assertNotNull(employee);
    assertEquals(id, employee.getPersonId());

    try {
      // 1. invoke action bound to the employee just created
      employee.operations().sack();

      // 2. check that invoked action has effectively run
      employee = container.getPerson().getByKey(id, Employee.class).load();
      assertEquals(0, employee.getSalary(), 0);
      assertTrue(employee.getTitle().endsWith("[Sacked]"));
    } catch (Exception e) {
      fail("Should never get here");
    } finally {
      // 3. remove the test employee
      container.getPerson().delete(employee.getPersonId());
      container.flush();
    }
  }

  @Test
  public void entityCollectionBoundPostWithParam() {
    EmployeeCollection employees = container.getPerson().execute(EmployeeCollection.class);
    assertFalse(employees.isEmpty());
    final Map<Integer, Integer> preSalaries = new HashMap<Integer, Integer>(employees.size());
    for (Employee employee : employees) {
      preSalaries.put(employee.getPersonId(), employee.getSalary());
    }
    assertFalse(preSalaries.isEmpty());

    employees.operations().increaseSalaries(1);

    employees = container.getPerson().execute(EmployeeCollection.class);
    assertFalse(employees.isEmpty());
    for (Employee employee : employees) {
      assertTrue(preSalaries.get(employee.getPersonId()) < employee.getSalary());
    }
  }

  @Test
  public void changeProductDimensions() {
    // 0. create a product
    final Integer id = 101;

    Product product = service.newEntity(Product.class);
    product.setProductId(id);
    product.setDescription("New product");

    final Dimensions origDimensions = service.newComplex(Dimensions.class);
    origDimensions.setDepth(BigDecimal.ZERO);
    origDimensions.setHeight(BigDecimal.ZERO);
    origDimensions.setWidth(BigDecimal.ZERO);
    product.setDimensions(origDimensions);

    container.getProduct().add(product);
    container.flush();

    product = container.getProduct().getByKey(id).load();
    assertNotNull(product);
    assertEquals(id, product.getProductId());
    assertEquals(BigDecimal.ZERO, product.getDimensions().getDepth());
    assertEquals(BigDecimal.ZERO, product.getDimensions().getHeight());
    assertEquals(BigDecimal.ZERO, product.getDimensions().getWidth());

    try {
      // 1. invoke action bound to the product just created
      final Dimensions newDimensions = service.newComplex(Dimensions.class);
      newDimensions.setDepth(BigDecimal.ONE);
      newDimensions.setHeight(BigDecimal.ONE);
      newDimensions.setWidth(BigDecimal.ONE);

      product.operations().changeProductDimensions(newDimensions);

      // 2. check that invoked action has effectively run
      product = container.getProduct().getByKey(id).load();
      assertEquals(BigDecimal.ONE, product.getDimensions().getDepth());
      assertEquals(BigDecimal.ONE, product.getDimensions().getHeight());
      assertEquals(BigDecimal.ONE, product.getDimensions().getWidth());
    } catch (Exception e) {
      fail("Should never get here");
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

    final Calendar purchaseDate = Calendar.getInstance();
    purchaseDate.clear();
    purchaseDate.set(Calendar.YEAR, 1);
    purchaseDate.set(Calendar.MONTH, 0);
    purchaseDate.set(Calendar.DAY_OF_MONTH, 1);

    ComputerDetail computerDetail = service.newEntity(ComputerDetail.class);
    computerDetail.setComputerDetailId(id);
    computerDetail.setSpecificationsBag(Collections.singleton("First spec"));
    computerDetail.setPurchaseDate(new Timestamp(purchaseDate.getTimeInMillis()));

    container.getComputerDetail().add(computerDetail);
    container.flush();

    computerDetail = container.getComputerDetail().getByKey(id).load();
    assertNotNull(computerDetail);
    assertEquals(id, computerDetail.getComputerDetailId());
    assertEquals(1, computerDetail.getSpecificationsBag().size());
    assertTrue(computerDetail.getSpecificationsBag().contains("First spec"));
    assertEquals(purchaseDate.getTimeInMillis(), computerDetail.getPurchaseDate().getTime());

    try {
      // 1. invoke action bound to the computer detail just created
      computerDetail.operations().resetComputerDetailsSpecifications(
              Collections.singleton("Second spec"), new Timestamp(Calendar.getInstance().getTimeInMillis()));

      // 2. check that invoked action has effectively run
      computerDetail = container.getComputerDetail().getByKey(id).load();
      assertNotNull(computerDetail);
      assertEquals(id, computerDetail.getComputerDetailId());
      assertEquals(1, computerDetail.getSpecificationsBag().size());
      assertTrue(computerDetail.getSpecificationsBag().contains("Second spec"));
      assertNotEquals(purchaseDate.getTimeInMillis(), computerDetail.getPurchaseDate());
    } catch (Exception e) {
      fail("Should never get here");
    } finally {
      // 3. remove the test product
      container.getComputerDetail().delete(computerDetail.getComputerDetailId());
      container.flush();
    }
  }
}
