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

import com.msopentech.odatajclient.engine.data.ODataTimestamp;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Geospatial;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Geospatial.Type;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiLineString;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Point;
import com.msopentech.odatajclient.proxy.api.impl.EntityTypeInvocationHandler;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.DefaultContainer;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.AllSpatialTypes;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.ComputerDetail;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Contractor;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.ContractorCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.CustomerInfo;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.EmployeeCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Message;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.MessageKey;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Order;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.OrderCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Person;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.PersonCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Product;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployee;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.SpecialEmployeeCollection;
import java.lang.reflect.Proxy;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * This is the unit test class to check entity retrieve operations.
 */
public class EntityRetrieveTestITCase extends AbstractTest {

    protected DefaultContainer getContainer() {
        return container;
    }

    @Test
    public void exists() {
        assertTrue(getContainer().getPerson().exists(-10));
        assertFalse(getContainer().getPerson().exists(-11));
    }

    @Test
    public void get() {
        readCustomer(getContainer(), -10);
    }

    @Test
    public void getAll() {
        final PersonCollection all = getContainer().getPerson().getAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        for (Person person : all) {
            assertNotNull(person);
        }

        final EmployeeCollection employees = getContainer().getPerson().getAll(EmployeeCollection.class);
        assertNotNull(employees);
        assertFalse(employees.isEmpty());
        for (Employee employee : employees) {
            assertNotNull(employee);
        }

        final SpecialEmployeeCollection specialEmployees = getContainer().getPerson().getAll(
                SpecialEmployeeCollection.class);
        assertNotNull(specialEmployees);
        assertFalse(specialEmployees.isEmpty());
        for (SpecialEmployee employee : specialEmployees) {
            assertNotNull(employee);
        }

        final ContractorCollection contractors = getContainer().getPerson().getAll(ContractorCollection.class);
        assertNotNull(contractors);
        assertFalse(contractors.isEmpty());
        for (Contractor contractor : contractors) {
            assertNotNull(contractor);
        }

        assertTrue(employees.size() > specialEmployees.size());
        assertTrue(all.size() > employees.size() + contractors.size());
    }

    @Test
    public void navigate() {
        final Order order = getContainer().getOrder().get(-9);
        assertNotNull(order);
        assertEquals(Integer.valueOf(-9), order.getOrderId());

        final ConcurrencyInfo concurrency = order.getConcurrency();
        assertNotNull(concurrency);
        assertEquals("2012-02-12T11:32:50.5072026", concurrency.getQueriedDateTime().toString());
        assertEquals(Integer.valueOf(78), order.getCustomerId());
    }

    @Test
    public void withGeospatial() {
        final AllSpatialTypes allSpatialTypes = getContainer().getAllGeoTypesSet().get(-10);
        assertNotNull(allSpatialTypes);
        assertEquals(Integer.valueOf(-10), allSpatialTypes.getId());

        final MultiLineString geogMultiLine = allSpatialTypes.getGeogMultiLine();
        assertNotNull(geogMultiLine);
        assertEquals(Type.MULTILINESTRING, geogMultiLine.getType());
        assertEquals(Geospatial.Dimension.GEOGRAPHY, geogMultiLine.getDimension());
        assertFalse(geogMultiLine.isEmpty());

        final Point geogPoint = allSpatialTypes.getGeogPoint();
        assertNotNull(geogPoint);
        assertEquals(Type.POINT, geogPoint.getType());
        assertEquals(Geospatial.Dimension.GEOGRAPHY, geogPoint.getDimension());
        assertEquals(52.8606, geogPoint.getY(), 0);
        assertEquals(173.334, geogPoint.getX(), 0);
    }

    @Test
    public void withInlineEntry() {
        final Customer customer = readCustomer(getContainer(), -10);
        final CustomerInfo customerInfo = customer.getInfo();
        assertNotNull(customerInfo);
        assertEquals(Integer.valueOf(11), customerInfo.getCustomerInfoId());
    }

    @Test
    public void withInlineFeed() {
        final Customer customer = readCustomer(getContainer(), -10);
        final OrderCollection orders = customer.getOrders();
        assertFalse(orders.isEmpty());
    }

    @Test
    public void withActions() {
        final ComputerDetail computerDetail = getContainer().getComputerDetail().get(-10);
        assertEquals(Integer.valueOf(-10), computerDetail.getComputerDetailId());

        try {
            assertNotNull(ComputerDetail.class.getMethod("resetComputerDetailsSpecifications",
                    Collection.class, ODataTimestamp.class));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void multiKey() {
        final MessageKey messageKey = new MessageKey();
        messageKey.setFromUsername("1");
        messageKey.setMessageId(-10);

        final Message message = getContainer().getMessage().get(messageKey);
        assertNotNull(message);
        assertEquals("1", message.getFromUsername());
    }

    @Test
    public void checkForETag() {
        Product product = getContainer().getProduct().get(-10);
        assertTrue(StringUtils.isNotBlank(
                ((EntityTypeInvocationHandler) Proxy.getInvocationHandler(product)).getETag()));
    }
}
