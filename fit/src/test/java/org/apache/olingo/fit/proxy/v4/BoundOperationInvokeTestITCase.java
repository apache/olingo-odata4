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
package org.apache.olingo.fit.proxy.v4;

import java.sql.Timestamp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Collections;

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.AccessLevel;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.AccountInfo;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.PaymentInstrument;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Person;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Product;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.ProductDetailCollection;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.ProductDetailKey;
//CHECKSTYLE:ON (Maven checkstyle)
import org.junit.Test;

public class BoundOperationInvokeTestITCase extends AbstractTestITCase {

  @Test
  public void getEmployeesCount() {
    assertNotNull(container.getCompany().get().operations().getEmployeesCount());
  }

  @Test
  public void getProductDetails() {
    final ProductDetailCollection result = container.getProducts().get(5).operations().getProductDetails(1);
    assertEquals(1, result.size());
  }

  @Test
  public void getRelatedProduct() {
    final ProductDetailKey key = new ProductDetailKey();
    key.setProductID(6);
    key.setProductDetailID(1);

    final Product product = container.getProductDetails().get(key).operations().getRelatedProduct();
    assertEquals(6, product.getProductID(), 0);
  }

  @Test
  public void getDefaultPI() {
    final PaymentInstrument pi = container.getAccounts().get(101).operations().getDefaultPI();
    assertEquals(101901, pi.getPaymentInstrumentID(), 0);
  }

  @Test
  public void getAccountInfo() {
    final AccountInfo accountInfo = container.getAccounts().get(101).operations().getAccountInfo();
    assertNotNull(accountInfo);
  }

  @Test
  public void getActualAmount() {
    final Double amount = container.getAccounts().get(101).getMyGiftCard().operations().getActualAmount(1.1);
    assertEquals(41.79, amount, 0);
  }

  @Test
  public void increaseRevenue() {
    final Long result = container.getCompany().get().operations().increaseRevenue(12L);
    assertNotNull(result);
  }

  @Test
  public void addAccessRight() {
    final AccessLevel accessLevel = container.getProducts().get(5).operations().addAccessRight(AccessLevel.Execute);
    assertNotNull(accessLevel);
  }

  @Test
  public void resetAddress() {
    final Customer customer = container.getCustomers().get(2);

    final Address address = customer.factory().newHomeAddress();
    address.setStreet("Via Le Mani Dal Naso, 123");
    address.setPostalCode("Tollo");
    address.setCity("66010");

    final Person person = container.getCustomers().get(2).operations().
        resetAddress(Collections.singletonList(address), 0);
    assertEquals(2, person.getPersonID(), 0);
  }

  @Test
  public void refreshDefaultPI() {
    final PaymentInstrument pi = container.getAccounts().get(101).operations().
            refreshDefaultPI(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    assertEquals(101901, pi.getPaymentInstrumentID(), 0);
  }
}
