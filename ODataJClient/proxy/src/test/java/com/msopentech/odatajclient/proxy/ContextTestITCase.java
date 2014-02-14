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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.msopentech.odatajclient.proxy.api.annotations.NavigationProperty;
import com.msopentech.odatajclient.proxy.api.context.AttachedEntityStatus;
import com.msopentech.odatajclient.proxy.api.impl.EntityTypeInvocationHandler;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.CustomerInfo;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Login;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Order;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.OrderCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * This is the unit test class to check entity retrieve operations.
 */
public class ContextTestITCase extends AbstractTest {

    @Test
    public void attachDetachNewEntity() {
        final Customer customer1 = container.getCustomer().newCustomer();
        final Customer customer2 = container.getCustomer().newCustomer();

        final EntityTypeInvocationHandler source1 =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer1);
        final EntityTypeInvocationHandler source2 =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer2);

        assertTrue(entityContext.isAttached(source1));
        assertTrue(entityContext.isAttached(source2));

        entityContext.detach(source1);
        assertFalse(entityContext.isAttached(source1));
        assertTrue(entityContext.isAttached(source2));

        entityContext.detach(source2);
        assertFalse(entityContext.isAttached(source1));
        assertFalse(entityContext.isAttached(source2));
    }

    @Test
    public void attachDetachExistingEntity() {
        final Customer customer1 = container.getCustomer().get(-10);
        final Customer customer2 = container.getCustomer().get(-9);
        final Customer customer3 = container.getCustomer().get(-10);

        final EntityTypeInvocationHandler source1 =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer1);
        final EntityTypeInvocationHandler source2 =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer2);
        final EntityTypeInvocationHandler source3 =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer3);

        assertFalse(entityContext.isAttached(source1));
        assertFalse(entityContext.isAttached(source2));
        assertFalse(entityContext.isAttached(source3));

        entityContext.attach(source1);
        assertTrue(entityContext.isAttached(source1));
        assertFalse(entityContext.isAttached(source2));
        assertTrue(entityContext.isAttached(source3));

        entityContext.attach(source2);
        assertTrue(entityContext.isAttached(source1));
        assertTrue(entityContext.isAttached(source2));
        assertTrue(entityContext.isAttached(source3));

        try {
            entityContext.attach(source3);
            fail();
        } catch (IllegalStateException ignore) {
            // ignore
        }

        entityContext.detach(source1);
        assertFalse(entityContext.isAttached(source1));
        assertTrue(entityContext.isAttached(source2));
        assertFalse(entityContext.isAttached(source3));

        entityContext.detach(source2);
        assertFalse(entityContext.isAttached(source1));
        assertFalse(entityContext.isAttached(source2));
        assertFalse(entityContext.isAttached(source3));
    }

    @Test
    public void linkTargetExisting() {
        final Customer customer = container.getCustomer().newCustomer();
        final CustomerInfo customerInfo = container.getCustomerInfo().get(11);

        customer.setInfo(customerInfo);

        assertNotNull(customer.getInfo());

        final EntityTypeInvocationHandler source =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer);
        final EntityTypeInvocationHandler target =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customerInfo);

        assertTrue(entityContext.isAttached(source));
        assertEquals(AttachedEntityStatus.NEW, entityContext.getStatus(source));
        assertTrue(entityContext.isAttached(target));
        assertEquals(AttachedEntityStatus.LINKED, entityContext.getStatus(target));

        checkUnidirectional("Info", source, "Customer", target, false);

        entityContext.detachAll();

        assertFalse(entityContext.isAttached(source));
        assertFalse(entityContext.isAttached(target));
    }

    @Test
    public void linkSourceExisting() {
        final Customer customer = container.getCustomer().get(-10);;
        final CustomerInfo customerInfo = container.getCustomerInfo().newCustomerInfo();

        customer.setInfo(customerInfo);

        assertNotNull(customer.getInfo());

        final EntityTypeInvocationHandler source =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer);
        final EntityTypeInvocationHandler target =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customerInfo);

        assertTrue(entityContext.isAttached(source));
        assertEquals(AttachedEntityStatus.CHANGED, entityContext.getStatus(source));
        assertTrue(entityContext.isAttached(target));
        assertEquals(AttachedEntityStatus.NEW, entityContext.getStatus(target));

        checkUnidirectional("Info", source, "Customer", target, false);

        entityContext.detachAll();

        assertFalse(entityContext.isAttached(source));
        assertFalse(entityContext.isAttached(target));
    }

    @Test
    public void linkBothExisting() {
        final Customer customer = container.getCustomer().get(-10);
        final CustomerInfo customerInfo = container.getCustomerInfo().get(12);

        customer.setInfo(customerInfo);

        assertNotNull(customer.getInfo());

        final EntityTypeInvocationHandler source =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer);
        final EntityTypeInvocationHandler target =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customerInfo);

        assertTrue(entityContext.isAttached(source));
        assertEquals(AttachedEntityStatus.CHANGED, entityContext.getStatus(source));
        assertTrue(entityContext.isAttached(target));
        assertEquals(AttachedEntityStatus.LINKED, entityContext.getStatus(target));

        checkUnidirectional("Info", source, "Customer", target, false);

        entityContext.detachAll();

        assertFalse(entityContext.isAttached(source));
        assertFalse(entityContext.isAttached(target));
    }

    @Test
    public void linkEntitySet() {;
        final Customer customer = container.getCustomer().newCustomer();

        final OrderCollection toBeLinked = container.getOrder().newOrderCollection();
        toBeLinked.add(container.getOrder().newOrder());
        toBeLinked.add(container.getOrder().newOrder());
        toBeLinked.add(container.getOrder().newOrder());

        customer.setOrders(toBeLinked);
        assertNotNull(customer.getOrders());
        assertEquals(3, customer.getOrders().size());

        final EntityTypeInvocationHandler source =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer);

        assertTrue(entityContext.isAttached(source));
        assertEquals(AttachedEntityStatus.NEW, entityContext.getStatus(source));
        assertEquals(3, ((Collection) (source.getLinkChanges().entrySet().iterator().next().getValue())).size());

        for (Order order : toBeLinked) {
            final EntityTypeInvocationHandler target =
                    (EntityTypeInvocationHandler) Proxy.getInvocationHandler(order);

            assertTrue(entityContext.isAttached(target));
            assertEquals(AttachedEntityStatus.NEW, entityContext.getStatus(target));
            checkUnidirectional("Orders", source, "Customer", target, true);
        }

        entityContext.detachAll();

        assertFalse(entityContext.isAttached(source));

        for (Order order : toBeLinked) {
            assertFalse(entityContext.isAttached((EntityTypeInvocationHandler) Proxy.getInvocationHandler(order)));
        }
    }

    @Test
    public void addProperty() {
        final Customer customer = container.getCustomer().newCustomer();
        customer.setCustomerId(100);

        final ContactDetails cd = new ContactDetails();
        customer.setPrimaryContactInfo(cd);

        cd.setAlternativeNames(Arrays.asList("alternative1", "alternative2"));

        final ContactDetails bcd = new ContactDetails();
        customer.setBackupContactInfo(Collections.<ContactDetails>singletonList(bcd));

        bcd.setAlternativeNames(Arrays.asList("alternative3", "alternative4"));

        assertEquals(Integer.valueOf(100), customer.getCustomerId());
        assertNotNull(customer.getPrimaryContactInfo().getAlternativeNames());
        assertEquals(2, customer.getPrimaryContactInfo().getAlternativeNames().size());
        assertTrue(customer.getPrimaryContactInfo().getAlternativeNames().contains("alternative1"));
        assertEquals(2, customer.getBackupContactInfo().iterator().next().getAlternativeNames().size());
        assertTrue(customer.getBackupContactInfo().iterator().next().getAlternativeNames().contains("alternative4"));

        final EntityTypeInvocationHandler source = (EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer);

        assertTrue(entityContext.isAttached(source));
        assertEquals(AttachedEntityStatus.NEW, entityContext.getStatus(source));

        entityContext.detachAll();

        assertFalse(entityContext.isAttached(source));
    }

    @Test
    public void readEntityInTheContext() {
        CustomerInfo customerInfo = container.getCustomerInfo().get(16);
        customerInfo.setInformation("some other info ...");

        assertEquals("some other info ...", customerInfo.getInformation());

        customerInfo = container.getCustomerInfo().get(16);
        assertEquals("some other info ...", customerInfo.getInformation());

        entityContext.detachAll();
        customerInfo = container.getCustomerInfo().get(16);
        assertNotEquals("some other info ...", customerInfo.getInformation());
    }

    @Test
    public void readAllWithEntityInTheContext() {
        CustomerInfo customerInfo = container.getCustomerInfo().get(16);
        customerInfo.setInformation("some other info ...");

        assertEquals("some other info ...", customerInfo.getInformation());

        boolean found = false;
        for (CustomerInfo info : container.getCustomerInfo().getAll()) {
            if (info.getCustomerInfoId() == 16) {
                assertEquals("some other info ...", customerInfo.getInformation());
                found = true;
            }
        }
        assertTrue(found);

        entityContext.detachAll();

        found = false;
        for (CustomerInfo info : container.getCustomerInfo().getAll()) {
            if (info.getCustomerInfoId() == 16) {
                assertNotEquals("some other info ...", info.getInformation());
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void checkContextInCaseOfErrors() {
        final Login login = container.getLogin().newLogin();

        final EntityTypeInvocationHandler handler = (EntityTypeInvocationHandler) Proxy.getInvocationHandler(login);

        assertTrue(entityContext.isAttached(handler));

        try {
            container.flush();
            fail();
        } catch (Exception e) {
            // ignore
        }

        assertTrue(entityContext.isAttached(handler));

        login.setCustomerId(-10);
        login.setUsername("customer");

        container.flush();
        assertFalse(entityContext.isAttached(handler));
        assertNotNull(container.getLogin().get("customer"));

        container.getLogin().delete(login.getUsername());
        assertTrue(entityContext.isAttached(handler));

        container.flush();
        assertFalse(entityContext.isAttached(handler));
        assertNull(container.getLogin().get("customer"));
    }

    @Test
    public void flushTest() {
        Customer customer = container.getCustomer().newCustomer();
        customer.setCustomerId(300);
        customer.setName("samplename");

        final List<Integer> keys = new ArrayList<Integer>();
        keys.add(-200);
        keys.add(-201);
        keys.add(-202);

        final OrderCollection toBeLinked = container.getOrder().newOrderCollection();
        for (Integer key : keys) {
            final Order order = container.getOrder().newOrder();
            order.setOrderId(key);
            order.setCustomerId(300);
            order.setCustomer(customer);
            toBeLinked.add(order);
        }

        customer.setOrders(toBeLinked);

        final CustomerInfo customerInfo = container.getCustomerInfo().get(16);
        customerInfo.setInformation("some new info ...");
        customer.setInfo(customerInfo);

        final ContactDetails cd = new ContactDetails();
        cd.setAlternativeNames(Arrays.asList("alternative1", "alternative2"));
        cd.setEmailBag(Collections.<String>singleton("myemail@mydomain.org"));
        cd.setMobilePhoneBag(Collections.<Phone>emptySet());

        final ContactDetails bcd = new ContactDetails();
        bcd.setAlternativeNames(Arrays.asList("alternative3", "alternative4"));
        bcd.setEmailBag(Collections.<String>emptySet());
        bcd.setMobilePhoneBag(Collections.<Phone>emptySet());

        customer.setPrimaryContactInfo(cd);
        customer.setBackupContactInfo(Collections.<ContactDetails>singletonList(bcd));

        assertTrue(entityContext.isAttached((EntityTypeInvocationHandler) Proxy.getInvocationHandler(customerInfo)));
        assertTrue(entityContext.isAttached((EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer)));
        for (Order linked : toBeLinked) {
            assertTrue(entityContext.isAttached((EntityTypeInvocationHandler) Proxy.getInvocationHandler(linked)));
        }

        container.flush();

        assertFalse(entityContext.isAttached((EntityTypeInvocationHandler) Proxy.getInvocationHandler(customerInfo)));
        assertFalse(entityContext.isAttached((EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer)));
        for (Order linked : toBeLinked) {
            assertFalse(entityContext.isAttached((EntityTypeInvocationHandler) Proxy.getInvocationHandler(linked)));
        }

        assertEquals("some new info ...", container.getCustomerInfo().get(16).getInformation());

        container.getOrder().delete(toBeLinked);
        container.getCustomer().delete(customer.getCustomerId());

        assertTrue(entityContext.isAttached((EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer)));
        for (Order linked : toBeLinked) {
            assertTrue(entityContext.isAttached((EntityTypeInvocationHandler) Proxy.getInvocationHandler(linked)));
        }

        container.flush();

        assertFalse(entityContext.isAttached((EntityTypeInvocationHandler) Proxy.getInvocationHandler(customer)));
        for (Order linked : toBeLinked) {
            assertFalse(entityContext.isAttached((EntityTypeInvocationHandler) Proxy.getInvocationHandler(linked)));
        }
    }

    private void checkUnlink(
            final String sourceName,
            final EntityTypeInvocationHandler source) {
        boolean found = false;
        for (Map.Entry<NavigationProperty, Object> property : source.getLinkChanges().entrySet()) {
            if (property.getKey().name().equals(sourceName)) {
                found = true;
            }
        }
        assertFalse(found);
    }

    private void checkLink(
            final String sourceName,
            final EntityTypeInvocationHandler source,
            final EntityTypeInvocationHandler target,
            final boolean isCollection) {
        boolean found = false;
        for (Map.Entry<NavigationProperty, Object> property : source.getLinkChanges().entrySet()) {
            if (property.getKey().name().equals(sourceName)) {
                if (isCollection) {
                    found = false;
                    for (Object proxy : (Collection) property.getValue()) {
                        if (target.equals((EntityTypeInvocationHandler) Proxy.getInvocationHandler(proxy))) {
                            found = true;
                        }
                    }
                } else {
                    found = target.equals(
                            (EntityTypeInvocationHandler) Proxy.getInvocationHandler(property.getValue()));
                }
            }
        }
        assertTrue(found);
    }

    private void checkUnidirectional(
            final String sourceName,
            final EntityTypeInvocationHandler source,
            final String targetName,
            final EntityTypeInvocationHandler target,
            final boolean isCollection) {
        checkLink(sourceName, source, target, isCollection);
        checkUnlink(targetName, target);
    }
}
