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

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import com.msopentech.odatajclient.proxy.api.impl.EntityTypeInvocationHandler;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Message;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.MessageKey;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Order;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.OrderCollection;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Product;
import java.lang.reflect.Proxy;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * This is the unit test class to check entity update operations.
 */
public class EntityUpdateTestITCase extends AbstractTest {

    @Test
    public void update() {
        Order order = container.getOrder().get(-9);

        final ConcurrencyInfo ci = order.getConcurrency();
        ci.setToken("XXX");

        container.flush();

        order = container.getOrder().get(-9);
        assertEquals("XXX", order.getConcurrency().getToken());
    }

    @Test
    public void multiKey() {
        final MessageKey key = new MessageKey();
        key.setFromUsername("1");
        key.setMessageId(-10);

        Message message = container.getMessage().get(key);
        assertNotNull(message);

        message.setBody("XXX");

        container.flush();

        message = container.getMessage().get(key);
        assertNotNull(message);
        assertEquals("XXX", message.getBody());
    }

    @Test
    public void patchLink() {
        Order order = container.getOrder().newOrder();
        order.setOrderId(400);
        order.setCustomerId(-9);

        OrderCollection orders = container.getOrder().newOrderCollection();
        orders.add(order);

        Customer customer = container.getCustomer().get(-9);
        customer.setOrders(orders);
        order.setCustomer(customer);

        container.flush();

        order = container.getOrder().get(400);
        assertEquals(400, order.getOrderId().intValue());
        assertEquals(-9, order.getCustomerId().intValue());

        customer = container.getCustomer().get(-9);

        assertEquals(2, customer.getOrders().size());

        int count = 0;
        for (Order inside : customer.getOrders()) {
            if (inside.getOrderId() == 400) {
                count++;
            }
        }
        assertEquals(1, count);
        assertEquals(-9, order.getCustomer().getCustomerId().intValue());
    }

    @Test
    public void concurrentModification() {
        Product product = container.getProduct().get(-10);
        final String etag = ((EntityTypeInvocationHandler) Proxy.getInvocationHandler(product)).getETag();
        assertTrue(StringUtils.isNotBlank(etag));

        final String baseConcurrency = String.valueOf(System.currentTimeMillis());
        product.setBaseConcurrency(baseConcurrency);

        container.flush();

        product = container.getProduct().get(-10);
        assertEquals(baseConcurrency, product.getBaseConcurrency());
        assertNotEquals(etag, ((EntityTypeInvocationHandler) Proxy.getInvocationHandler(product)).getETag());
    }
}
