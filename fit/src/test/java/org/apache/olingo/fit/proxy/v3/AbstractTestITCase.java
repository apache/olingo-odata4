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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.v3.EdmEnabledODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.proxy.v3.staticservice.Service;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.DefaultContainer;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Aliases;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetailsCollection;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.PhoneCollection;

import org.apache.olingo.fit.server.TomcatTestServer;
import org.apache.olingo.server.tecsvc.TechnicalServlet;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//CHECKSTYLE:ON (Maven checkstyle)

public abstract class AbstractTestITCase extends AbstractBaseTestITCase {

  protected static String testStaticServiceRootURL;

  protected static String testPrimitiveKeysServiceRootURL;

  protected static String testKeyAsSegmentServiceRootURL;

  protected static String testActionOverloadingServiceRootURL;

  protected static String testOpenTypeServiceRootURL;

  protected static String testLargeModelServiceRootURL;

  protected static AbstractService<EdmEnabledODataClient> service;

  protected static DefaultContainer container;

  @BeforeClass
  public static void setUpODataServiceRoot() throws IOException {
    testStaticServiceRootURL = "http://localhost:9080/stub/StaticService/V30/Static.svc";
    testPrimitiveKeysServiceRootURL = "http://localhost:9080/stub/StaticService/V30/PrimitiveKeys.svc";
    testKeyAsSegmentServiceRootURL = "http://localhost:9080/stub/StaticService/V30/KeyAsSegment.svc";
    testActionOverloadingServiceRootURL = "http://localhost:9080/stub/StaticService/V30/ActionOverloading.svc";
    testOpenTypeServiceRootURL = "http://localhost:9080/stub/StaticService/V30/OpenType.svc";
    testLargeModelServiceRootURL = "http://localhost:9080/stub/StaticService/V30/Static.svc/large";

    service = Service.getV3(testStaticServiceRootURL);
    service.getClient().getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    container = service.getEntityContainer(DefaultContainer.class);
    assertNotNull(container);
    service.getContext().detachAll();
  }

  protected Customer getSampleCustomerProfile(
          final Integer id,
          final String sampleName,
          final DefaultContainer container) {

    final Customer customer = container.newEntityInstance(Customer.class);

    // add name attribute
    customer.setName(sampleName);

    // add key attribute
    customer.setCustomerId(id);

    final ContactDetails cd = container.newComplexInstance(ContactDetails.class); // PrimaryContactInfo

    PrimitiveCollection<String> value = container.newPrimitiveCollection(String.class);
    value.add("alternative1");
    value.add("alternative2");
    cd.setAlternativeNames(value);

    value = container.newPrimitiveCollection(String.class);
    value.add("myname@mydomain.org");
    cd.setEmailBag(value);

    cd.setMobilePhoneBag(container.newComplexCollection(PhoneCollection.class)); // empty
    customer.setPrimaryContactInfo(cd);

    final Aliases aliases = container.newComplexInstance(Aliases.class);

    value = container.newPrimitiveCollection(String.class);
    value.add("myAlternativeName");
    aliases.setAlternativeNames(value);
    cd.setContactAlias(aliases);

    final ContactDetails bcd = container.newComplexInstance(ContactDetails.class); // BackupContactInfo;
    value = container.newPrimitiveCollection(String.class);
    value.add("alternative3");
    value.add("alternative4");
    bcd.setAlternativeNames(value);
    bcd.setEmailBag(container.newPrimitiveCollection(String.class)); // empty
    bcd.setMobilePhoneBag(container.newComplexCollection(PhoneCollection.class)); // empty

    final ContactDetailsCollection bci = container.newComplexCollection(ContactDetailsCollection.class);
    bci.add(bcd);
    customer.setBackupContactInfo(bci);

    container.getCustomer().add(customer);
    return customer;
  }

  protected void checkSampleCustomerProfile(
          final Customer customer,
          final Integer id,
          final String sampleName) {

    assertEquals(id, customer.getCustomerId());
    assertNotNull(customer.getPrimaryContactInfo());
    assertFalse(customer.getBackupContactInfo().isEmpty());

    final ContactDetails cd = customer.getPrimaryContactInfo();
    final ContactDetails bcd = customer.getBackupContactInfo().iterator().next();

    assertTrue(cd.getAlternativeNames().contains("alternative1"));
    assertTrue(cd.getAlternativeNames().contains("alternative2"));
    assertEquals("myname@mydomain.org", cd.getEmailBag().iterator().next());
    assertEquals("myAlternativeName", cd.getContactAlias().getAlternativeNames().iterator().next());
    assertTrue(cd.getMobilePhoneBag().isEmpty());

    assertTrue(bcd.getAlternativeNames().contains("alternative3"));
    assertTrue(bcd.getAlternativeNames().contains("alternative4"));
    assertTrue(bcd.getEmailBag().isEmpty());
    assertTrue(bcd.getMobilePhoneBag().isEmpty());
  }

  protected Customer readCustomer(final DefaultContainer container, final int id) {
    final Customer customer = container.getCustomer().getByKey(id).load();
    assertNotNull(customer);
    assertEquals(Integer.valueOf(id), customer.getCustomerId());

    return customer;
  }

  @Override
  protected CommonODataClient getClient() {
    throw new RuntimeException("This method should not be used from proxy tests.");
  }
}
