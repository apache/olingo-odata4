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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.msopentech.odatajclient.proxy.api.NonUniqueResultException;
import com.msopentech.odatajclient.proxy.api.Query;
import com.msopentech.odatajclient.proxy.api.Sort;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Car;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.CarCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.EmployeeCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployee;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployeeCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class QueryTestITCase extends AbstractTest {

    @Test
    public void filterOrderby() {
        final Query<Car, CarCollection> query = container.getCar().createQuery().setFilter("VIN lt 16");
        CarCollection result = query.getResult();
        assertNotNull(result);

        // 1. check that filtered entity set looks as expected
        assertEquals(5, result.size());

        // 2. extract VIN values - sorted ASC by default
        final List<Integer> vinsASC = new ArrayList<Integer>(5);
        for (Car car : result) {
            assertTrue(car.getVIN() < 16);
            vinsASC.add(car.getVIN());
        }

        // 3. add orderby clause to filter above
        result = query.setOrderBy(new Sort("VIN", Sort.Direction.DESC)).getResult();
        assertNotNull(result);
        assertEquals(5, result.size());

        // 4. extract again VIN value - now they were required to be sorted DESC
        final List<Integer> vinsDESC = new ArrayList<Integer>(5);
        for (Car car : result) {
            assertTrue(car.getVIN() < 16);
            vinsDESC.add(car.getVIN());
        }

        // 5. reverse vinsASC and expect to be equal to vinsDESC
        Collections.reverse(vinsASC);
        assertEquals(vinsASC, vinsDESC);
    }

    @Test
    public void single() {
        final Query<Car, CarCollection> query = container.getCar().createQuery().setFilter("VIN lt 16");

        Exception exception = null;
        try {
            query.getSingleResult();
            fail();
        } catch (NonUniqueResultException e) {
            exception = e;
        }
        assertNotNull(exception);

        query.setFilter(containerFactory.getFilterFactory().eq("VIN", 16));
        final Car result = query.getSingleResult();
        assertNotNull(result);
    }

    @Test
    public void polymorph() {
        final Query<Employee, EmployeeCollection> queryEmployee =
                container.getPerson().createQuery(EmployeeCollection.class);
        assertFalse(queryEmployee.getResult().isEmpty());

        final Query<SpecialEmployee, SpecialEmployeeCollection> querySpecialEmployee =
                container.getPerson().createQuery(SpecialEmployeeCollection.class);
        assertFalse(querySpecialEmployee.getResult().isEmpty());

        assertTrue(container.getPerson().getAll().size()
                > queryEmployee.getResult().size() + querySpecialEmployee.getResult().size());
    }
}
