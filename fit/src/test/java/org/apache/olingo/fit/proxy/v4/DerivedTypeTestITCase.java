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

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CompanyAddress;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CreditCardPI;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CreditCardPICollection;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CustomerCollection;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Person;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
//CHECKSTYLE:ON (Maven checkstyle)

public class DerivedTypeTestITCase extends AbstractTestITCase {

  @Test
  public void read() {
    final CustomerCollection customers = container.getPeople().execute(CustomerCollection.class);
    assertNotNull(customers);

    for (Customer customer : customers) {
      assertTrue(customer instanceof Customer);
    }

    final CreditCardPICollection creditCards = container.getAccounts().getByKey(101).
            getMyPaymentInstruments().execute(CreditCardPICollection.class);
    assertNotNull(creditCards);
    for (CreditCardPI creditCard : creditCards) {
      assertTrue(creditCard instanceof CreditCardPI);
    }
  }

  @Test
  public void createDelete() {
    final Customer customer = service.newEntityInstance(Customer.class);
    customer.setPersonID(976);
    customer.setFirstName("Test");
    customer.setLastName("Test");

    final Address homeAddress = service.newComplexInstance(CompanyAddress.class);
    homeAddress.setStreet("V.le Gabriele D'Annunzio");
    homeAddress.setCity("Pescara");
    homeAddress.setPostalCode("65127");
    customer.setHomeAddress(homeAddress);

    customer.setNumbers(service.newPrimitiveCollection(String.class)); // empty
    customer.setEmails(service.newPrimitiveCollection(String.class)); // empty
    customer.setCity("Pescara");

    final Calendar birthday = Calendar.getInstance();
    birthday.clear();
    birthday.set(1977, 8, 8);
    customer.setBirthday(new Timestamp(birthday.getTimeInMillis()));

    customer.setTimeBetweenLastTwoOrders(BigDecimal.valueOf(0.0000002));

    container.getPeople().add(customer);
    container.flush();

    final Person actual = container.getPeople().getByKey(976, Customer.class).load();
    assertTrue(actual instanceof Customer);
    assertTrue(actual.getHomeAddress() instanceof CompanyAddress);

    container.getPeople().delete(976);

    container.flush();
  }
}
