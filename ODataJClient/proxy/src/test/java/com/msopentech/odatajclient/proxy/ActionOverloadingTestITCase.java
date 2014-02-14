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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.DefaultContainer;
import com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee;
import com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.EmployeeCollection;
import com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.OrderLineKey;
import com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployee;
import com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployeeCollection;
import com.msopentech.odatajclient.proxy.api.EntityContainerFactory;
import org.junit.Test;

/**
 * This is the unit test class to check actions overloading.
 */
public class ActionOverloadingTestITCase extends AbstractTest {

    @Test
    public void retrieveProduct() {
        final DefaultContainer aocontainer = EntityContainerFactory.getV3Instance(testActionOverloadingServiceRootURL).
                getEntityContainer(DefaultContainer.class);

        int res = aocontainer.retrieveProduct();
        assertEquals(-10, res);

        EntityContainerFactory.getContext().detachAll();

        res = aocontainer.getProduct().get(-10).retrieveProduct();
        assertEquals(-10, res);

        EntityContainerFactory.getContext().detachAll();

        final OrderLineKey key = new OrderLineKey();
        key.setOrderId(-10);
        key.setProductId(-10);

        res = aocontainer.getOrderLine().get(key).retrieveProduct();
        assertEquals(-10, res);
    }

    @Test
    public void increaseSalaries() {
        final DefaultContainer aocontainer = EntityContainerFactory.getV3Instance(testActionOverloadingServiceRootURL).
                getEntityContainer(DefaultContainer.class);

        EmployeeCollection ecoll = aocontainer.getPerson().getAll(EmployeeCollection.class);
        assertFalse(ecoll.isEmpty());

        Employee empl = ecoll.iterator().next();
        assertNotNull(empl);

        int key = empl.getPersonId();
        int salary = empl.getSalary();

        ecoll.increaseSalaries(5);

        EntityContainerFactory.getContext().detachAll();

        empl = aocontainer.getPerson().get(key, Employee.class);
        assertEquals(salary + 5, empl.getSalary().intValue());

        SpecialEmployeeCollection secoll = aocontainer.getPerson().getAll(SpecialEmployeeCollection.class);
        assertFalse(secoll.isEmpty());

        SpecialEmployee sempl = secoll.iterator().next();
        assertNotNull(sempl);

        key = sempl.getPersonId();
        salary = sempl.getSalary();

        secoll.increaseSalaries(5);

        EntityContainerFactory.getContext().detachAll();

        sempl = aocontainer.getPerson().get(key, SpecialEmployee.class);
        assertEquals(salary + 5, sempl.getSalary().intValue());
    }
}
