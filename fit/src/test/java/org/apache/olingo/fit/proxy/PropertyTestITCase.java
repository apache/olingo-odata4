/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.proxy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.apache.olingo.ext.proxy.api.ODataFlushException;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.StoredPI;
import org.junit.Test;

/**
 * This is the unit test class to check actions overloading.
 */
public class PropertyTestITCase extends AbstractTestITCase {

  @Test
  public void nullNullableProperty() {
    final Customer customer = container.getCustomers().getByKey(1);
    customer.setFirstName(null);
    container.flush();

    assertNull(container.getCustomers().getByKey(1).getFirstName());
  }

  @Test
  public void nullNonNullableProperty() {
    final StoredPI storedPI = container.getStoredPIs().getByKey(1000);
    storedPI.setPIName(null);

    try {
      container.flush();
      fail();
    } catch (ODataFlushException e) {
      assertNotNull(e);
    }
    service.getContext().detachAll();
  }
}
