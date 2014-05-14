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
package org.apache.olingo.fit.proxy.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Company;
import org.junit.Test;

public class SingletonTestITCase extends AbstractTestITCase {

  @Test
  public void read() {
    assertNotNull(container.getCompany().get(0));
    entityContext.detachAll();
    assertNotNull(container.getCompany().iterator().next());
    entityContext.detachAll();
    assertEquals(1, container.getCompany().count(), 0);
    entityContext.detachAll();
  }

  @Test
  public void update() {
    final Company company = container.getCompany().get(0);
    company.setRevenue(132520L);

    container.flush();

    assertEquals(132520L, container.getCompany().get(0).getRevenue(), 0);
  }
}
