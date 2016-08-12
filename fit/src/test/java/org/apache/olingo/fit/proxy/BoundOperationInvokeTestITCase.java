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

// CHECKSTYLE:OFF (Maven checkstyle)
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.AccessLevel;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.AccountInfoComposableInvoker;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.AddressCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.HomeAddress;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PaymentInstrument;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Person;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductComposableInvoker;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductDetailCollectionComposableInvoker;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductDetailKey;
import org.junit.Test;
// CHECKSTYLE:ON (Maven checkstyle)

public class BoundOperationInvokeTestITCase extends AbstractTestITCase {

  @Test
  public void getEmployeesCount() {
    assertNotNull(container.getCompany().load().operations().getEmployeesCount());
  }

  @Test
  public void getProductDetails() {
    final ProductDetailCollectionComposableInvoker result =
        container.getProducts().getByKey(5).operations().getProductDetails(1);
    assertEquals(1, result.execute().size());
  }

  @Test
  public void getRelatedProduct() {
    final ProductDetailKey key = new ProductDetailKey();
    key.setProductID(6);
    key.setProductDetailID(1);

    final ProductComposableInvoker product =
        container.getProductDetails().getByKey(key).operations().getRelatedProduct();
    assertEquals(6, product.execute().getProductID(), 0);
  }

  @Test
  public void getDefaultPI() {
    final PaymentInstrument pi = container.getAccounts().getByKey(101).operations().getDefaultPI().execute();
    assertEquals(101901, pi.getPaymentInstrumentID(), 0);
  }

  @Test
  public void getAccountInfo() {
    final AccountInfoComposableInvoker accountInfo =
        container.getAccounts().getByKey(101).operations().getAccountInfo();
    assertNotNull(accountInfo.execute());
  }

  @Test
  public void getActualAmount() {
    final Double amount =
        container.getAccounts().getByKey(101).getMyGiftCard().operations().getActualAmount(1.1).execute();
    assertEquals(41.79, amount, 0);
  }

  @Test
  public void increaseRevenue() {
    final Long result = container.getCompany().load().operations().increaseRevenue(12L).execute();
    assertNotNull(result);
  }

  @Test
  public void addAccessRight() {
    final AccessLevel accessLevel =
        container.getProducts().getByKey(5).operations().addAccessRight(AccessLevel.Execute).execute();
    assertNotNull(accessLevel);
  }

  @Test
  public void resetAddress() {
    final Address address = container.newComplexInstance(HomeAddress.class);
    address.setStreet("Via Le Mani Dal Naso, 123");
    address.setPostalCode("Tollo");
    address.setCity("66010");

    final AddressCollection ac = container.newComplexCollection(AddressCollection.class);
    ac.add(address);

    final Person person = container.getCustomers().getByKey(2).operations().resetAddress(ac, 0).execute();
    assertEquals(2, person.getPersonID(), 0);
  }

  @Test
  public void refreshDefaultPI() {
    final PaymentInstrument pi = container.getAccounts().getByKey(101).operations().
        refreshDefaultPI(new Timestamp(Calendar.getInstance().getTimeInMillis())).execute();
    assertEquals(101901, pi.getPaymentInstrumentID(), 0);
  }
}
