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

import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Driver;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Order;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * This is the unit test class to check actions overloading.
 */
public class PropertyTestITCase extends AbstractTestITCase {

  @Test
  public void nullNullableProperty() {
    Order order = container.getOrder().getByKey(-8);
    order.setCustomerId(null);
    container.flush();

    assertNull(container.getOrder().getByKey(-8).getCustomerId());
  }

  @Test
  public void nullNonNullableProperty() {
    Driver driver = container.getDriver().getByKey("2");
    driver.setBirthDate(null);

    try {
      container.flush();
      fail();
    } catch (IllegalStateException e) {
      // ignore and detach all
      service.getContext().detachAll();
    }
  }
}
