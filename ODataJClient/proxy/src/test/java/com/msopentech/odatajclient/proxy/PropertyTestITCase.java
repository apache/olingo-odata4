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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.msopentech.odatajclient.proxy.api.EntityContainerFactory;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Driver;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Order;
import org.junit.Test;

/**
 * This is the unit test class to check actions overloading.
 */
public class PropertyTestITCase extends AbstractTest {

    @Test
    public void nullNullableProperty() {
        Order order = container.getOrder().get(-8);
        order.setCustomerId(null);

        container.flush();

        order = container.getOrder().get(-8);
        assertNull(order.getCustomerId());
    }

    @Test
    public void nullNonNullableProperty() {
        Driver driver = container.getDriver().get("1");
        driver.setBirthDate(null);

        try {
            container.flush();
            fail();
        } catch (IllegalStateException e) {
            // ignore and detach all
            EntityContainerFactory.getContext().detachAll();
        }
    }
}
