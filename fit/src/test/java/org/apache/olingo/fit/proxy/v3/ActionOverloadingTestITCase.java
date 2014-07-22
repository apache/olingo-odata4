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

import org.apache.olingo.client.api.v3.EdmEnabledODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.fit.proxy.v3.actionoverloading.Service;
import org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.DefaultContainer;
import org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Employee;
import org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.EmployeeCollection;
import org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.OrderLineKey;
import org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployee;
import org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployeeCollection;
//CHECKSTYLE:ON (Maven checkstyle)

/**
 * This is the unit test class to check actions overloading.
 */
public class ActionOverloadingTestITCase extends AbstractTestITCase {

  private DefaultContainer getContainer() {
    final Service<EdmEnabledODataClient> ecf =
            Service.getV3(testActionOverloadingServiceRootURL);
    ecf.getClient().getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    return ecf.getEntityContainer(DefaultContainer.class);
  }

  @Test
  public void retrieveProduct() {
    final DefaultContainer aocontainer = getContainer();

    int res = aocontainer.operations().retrieveProduct();
    assertEquals(-10, res);

    service.getContext().detachAll();

    res = aocontainer.getProduct().getByKey(-10).operations().retrieveProduct();
    assertEquals(-10, res);

    service.getContext().detachAll();

    final OrderLineKey key = new OrderLineKey();
    key.setOrderId(-10);
    key.setProductId(-10);

    res = aocontainer.getOrderLine().getByKey(key).operations().retrieveProduct();
    assertEquals(-10, res);
  }

  @Test
  public void increaseSalaries() {
    final DefaultContainer aocontainer = getContainer();

    EmployeeCollection ecoll = aocontainer.getPerson().execute(EmployeeCollection.class);
    assertFalse(ecoll.isEmpty());

    Employee empl = ecoll.iterator().next();
    assertNotNull(empl);

    empl.getPersonId();
    int salary = empl.getSalary();

    ecoll.operations().increaseSalaries(5);

    // the invoke above changed the local entities, re-read
    service.getContext().detachAll();
    ecoll = aocontainer.getPerson().execute(EmployeeCollection.class);
    empl = ecoll.iterator().next();

    assertEquals(salary + 5, empl.getSalary(), 0);

    SpecialEmployeeCollection secoll = aocontainer.getPerson().execute(SpecialEmployeeCollection.class);
    assertFalse(secoll.isEmpty());

    SpecialEmployee sempl = secoll.toArray(new SpecialEmployee[secoll.size()])[1];
    assertNotNull(sempl);

    sempl.getPersonId();
    salary = sempl.getSalary();

    secoll.operations().increaseSalaries(5);

    // the invoke above changed the local entities, re-read
    service.getContext().detachAll();
    secoll = aocontainer.getPerson().execute(SpecialEmployeeCollection.class);
    sempl = secoll.toArray(new SpecialEmployee[secoll.size()])[1];

    assertEquals(salary + 5, sempl.getSalary(), 0);
  }
}
