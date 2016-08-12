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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.fit.proxy.staticservice.Service;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.InMemoryEntities;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Person;
import org.junit.Test;

public class KeyAsSegmentTestITCase extends AbstractTestITCase {

  private Service<EdmEnabledODataClient> ecf;

  private InMemoryEntities ime;

  protected Service<EdmEnabledODataClient> getService() {
    if (ecf == null) {
      ecf = Service.getV4(testKeyAsSegmentServiceRootURL);
      ecf.getClient().getConfiguration().setKeyAsSegment(true);
      ecf.getClient().getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    }
    return ecf;
  }

  protected InMemoryEntities getContainer() {
    if (ime == null) {
      ime = getService().getEntityContainer(InMemoryEntities.class);
    }
    return ime;
  }

  @Test
  public void read() {
    assertNotNull(getContainer().getAccounts().getByKey(101));
  }

  @Test
  public void createAndDelete() {
    createPatchAndDeleteOrder(getContainer(), getService());
  }

  @Test
  public void update() {
    Person person = getContainer().getPeople().getByKey(5);
    person.setMiddleName("middleN");

    container.flush();

    person = getContainer().getPeople().getByKey(5);
    assertEquals("middleN", person.getMiddleName());
  }
}
