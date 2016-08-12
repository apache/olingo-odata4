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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

// CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.EdmStreamValue;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;
import org.apache.olingo.ext.proxy.commons.AbstractCollectionInvocationHandler;
import org.apache.olingo.fit.proxy.demo.Service;
import org.apache.olingo.fit.proxy.demo.odatademo.DemoService;
import org.apache.olingo.fit.proxy.demo.odatademo.types.PersonDetail;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.InMemoryEntities;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.AccessLevel;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Account;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.AddressCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Color;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.CustomerCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.HomeAddress;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Order;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.OrderCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PaymentInstrument;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PaymentInstrumentCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Person;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PersonCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PersonComposableInvoker;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Product;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductCollectionComposableInvoker;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductDetail;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductDetailCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductDetailCollectionComposableInvoker;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Account.MyPaymentInstruments;
// CHECKSTYLE:ON (Maven checkstyle)
import org.junit.Test;

public class APIBasicDesignTestITCase extends AbstractTestITCase {

  protected AbstractService<EdmEnabledODataClient> getService() {
    return service;
  }

  protected InMemoryEntities getContainer() {
    return container;
  }

  @Test
  public void readEntitySet() {
    final OrderCollection orders = container.getOrders().execute();
    assertFalse(orders.isEmpty());

    final CustomerCollection customers = container.getCustomers().
        orderBy("PersonID").
        select("FirstName", "LastName", "Orders").
        expand("Orders").
        execute();

    assertEquals(2, customers.size());
    for (Customer customer : customers) {
      assertNotNull(customer.getFirstName());
      assertNotNull(customer.getLastName());
    }
  }
  
  @Test
  public void expandToContainedEntitySet() {
    Account account = container.getAccounts().getByKey(103).expand("MyPaymentInstruments").load();
    assertNotNull(account);
    assertNotNull(account.getAccountID());
    MyPaymentInstruments myPaymentInstruments = account.getMyPaymentInstruments();
    assertNotNull(myPaymentInstruments);
    PaymentInstrument paymentInstrument = myPaymentInstruments.iterator().next();
    assertNotNull(paymentInstrument);
    assertNotNull(paymentInstrument.getFriendlyName());

    PaymentInstrumentCollection myPaymentInstrumentCol = myPaymentInstruments.execute();
    assertNotNull(myPaymentInstrumentCol);
    assertFalse(myPaymentInstrumentCol.isEmpty());
    paymentInstrument = myPaymentInstrumentCol.iterator().next();
    assertNotNull(paymentInstrument);
    assertNotNull(paymentInstrument.getFriendlyName());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void expandToContainedEntitySetWithUnsupportedOperation() {
    Account account = container.getAccounts().getByKey(103).expand("MyPaymentInstruments").load();
    account.getMyPaymentInstruments().delete(101901);
  }

  @Test
  public void readWithReferences() {
    final Person person = container.getOrders().getByKey(8).getCustomerForOrder().refs().load();
    assertEquals("http://localhost:9080/stub/StaticService/V40/Static.svc/Customers(PersonID=1)",
        person.readEntityReferenceID());

    final OrderCollection orders = container.getCustomers().getByKey(1).getOrders().refs().execute();
    assertEquals("http://localhost:9080/stub/StaticService/V40/Static.svc/Orders(7)",
        orders.iterator().next().readEntityReferenceID());
  }

  @Test
  public void changeSingleNavigationProperty() {
    /*
     * See OData Spec 11.4.6.3
     * Alternatively, a relationship MAY be updated as part of an update to the source entity by including
     * the required binding information for the new target entity.
     *
     * => use PATCH instead of PUT
     */
    final Person person1 = container.getPeople().getByKey(1).load();
    final Person person5 = container.getPeople().getByKey(5).load();

    person1.setParent(person5);
    container.flush();
  }

  @Test
  public void addViaReference() {
    final Order order = container.getOrders().getByKey(8).load();

    final OrderCollection orders = container.newEntityCollection(OrderCollection.class);
    orders.addRef(order);

    container.getCustomers().getByKey(1).setOrders(orders);
    container.flush();
  }

  @Test
  public void readAndCheckForPrimitive() {
    final Customer customer = getContainer().getCustomers().getByKey(1);
    assertNotNull(customer);
    assertNull(customer.getPersonID());

    assertEquals(1, customer.load().getPersonID(), 0);
    service.getContext().detachAll();
  }

  @Test
  public void readAndCheckForComplex() {
    Customer customer = container.getCustomers().getByKey(1); // no http request
    Address homeAddress = customer.getHomeAddress();
    assertNotNull(homeAddress);
    assertNull(homeAddress.getCity());
    assertNull(homeAddress.getPostalCode());
    assertNull(homeAddress.getStreet());

    homeAddress.load(); // HTTP request at complex loading
    assertEquals("London", homeAddress.getCity());
    assertEquals("98052", homeAddress.getPostalCode());
    assertEquals("1 Microsoft Way", homeAddress.getStreet());

    getService().getContext().detachAll();

    homeAddress = container.getCustomers().getByKey(1).load().getHomeAddress(); // HTTP request at entity loading
    assertEquals("London", homeAddress.getCity());
    assertEquals("98052", homeAddress.getPostalCode());
    assertEquals("1 Microsoft Way", homeAddress.getStreet());

    getService().getContext().detachAll();

    customer = container.getOrders().getByKey(8).getCustomerForOrder();
    homeAddress = customer.getHomeAddress().select("City", "PostalCode").expand("SomethingElse"); // no HTTP request
    assertNotNull(homeAddress);
    assertNull(homeAddress.getCity());
    assertNull(homeAddress.getPostalCode());
    assertNull(homeAddress.getStreet());

    try {
      homeAddress.load();
      fail();
    } catch (Exception e) {
      // Generated URL
      // "<serviceroot>/Orders(8)/CustomerForOrder/HomeAddress?$select=City,PostalCode&$expand=SomethingElse"
      // curently unsupported by test service server
      homeAddress.clearQueryOptions();
    }
  }

  @Test
  public void readWholeEntitySet() {
    PersonCollection person = getContainer().getPeople().execute();
    assertEquals(5, person.size(), 0);

    int pageCount = 1;
    while (person.hasNextPage()) {
      pageCount++;
      assertFalse(person.nextPage().execute().isEmpty());
    }

    assertEquals(2, pageCount);
    service.getContext().detachAll(); // avoid influences
  }

  @Test
  public void loadWithSelect() {
    Order order =
        getContainer().getOrders().getByKey(8);
    assertNull(order.getOrderID());
    assertNull(order.getOrderDate());

    order.select("OrderID");
    order.load();

    assertNull(order.getOrderDate());
    assertNotNull(order.getOrderID());

    order.clearQueryOptions();
    order.load();
    assertNotNull(order.getOrderDate());
    assertNotNull(order.getOrderID());
    service.getContext().detachAll(); // avoid influences
  }

  @Test
  public void loadWithSelectAndExpand() {
    final Customer customer = getContainer().getCustomers().getByKey(1);

    customer.expand("Orders");
    customer.select("Orders", "PersonID");

    customer.load();
    assertEquals(1, customer.getOrders().size());
    service.getContext().detachAll(); // avoid influences
  }

  @Test
  public void navigateLinks() {
    final Customer customer = getContainer().getCustomers().getByKey(1); // No HTTP Request
    assertNotNull(customer.getCompany().load().getCompanyID()); // singleton: single request
    assertEquals(1, customer.getOrders().execute().size()); // collection: single request

    final Order order = getContainer().getOrders().getByKey(8); // No HTTP Requests
    assertNotNull(order.getCustomerForOrder().load().getPersonID()); // entity type: single request

    service.getContext().detachAll(); // avoid influences
  }

  @Test
  public void createDelete() {
    // Create order ....
    final Order order = getContainer().newEntityInstance(Order.class);
    order.setOrderID(1105);

    final Calendar orderDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    orderDate.clear();
    orderDate.set(2011, 3, 4, 16, 3, 57);
    order.setOrderDate(new Timestamp(orderDate.getTimeInMillis()));

    order.setShelfLife(BigDecimal.ZERO);

    final PrimitiveCollection<BigDecimal> osl = getContainer().newPrimitiveCollection(BigDecimal.class);
    osl.add(BigDecimal.TEN.negate());
    osl.add(BigDecimal.TEN);

    order.setOrderShelfLifes(osl);

    getContainer().getOrders().add(order);
    getContainer().flush();

    Order actual = getContainer().getOrders().getByKey(1105);
    assertNull(actual.getOrderID());

    actual.load();
    assertEquals(1105, actual.getOrderID(), 0);
    assertEquals(orderDate.getTimeInMillis(), actual.getOrderDate().getTime());
    assertEquals(BigDecimal.ZERO, actual.getShelfLife());
    assertEquals(2, actual.getOrderShelfLifes().size());

    service.getContext().detachAll();

    // (1) Delete by key (see EntityCreateTestITCase)
    getContainer().getOrders().delete(1105);
    assertNull(getContainer().getOrders().getByKey(1105));

    service.getContext().detachAll(); // detach to show the second delete case

    // (2) Delete by object (see EntityCreateTestITCase)
    getContainer().getOrders().delete(getContainer().getOrders().getByKey(1105));
    assertNull(getContainer().getOrders().getByKey(1105));

    // (3) Delete by invoking delete method on the object itself
    service.getContext().detachAll(); // detach to show the third delete case
    getContainer().getOrders().getByKey(1105).delete();
    assertNull(getContainer().getOrders().getByKey(1105));

    getContainer().flush();

    service.getContext().detachAll();
    try {
      getContainer().getOrders().getByKey(1105).load();
      fail();
    } catch (IllegalArgumentException e) {
      // Test
    }
    service.getContext().detachAll(); // avoid influences
  }

  @Test
  public void deleteSingleProperty() {
    container.getCustomers().getByKey(1).delete("City");
    container.flush();
  }

  @Test
  public void deleteComplex() {
    container.getCustomers().getByKey(1).getHomeAddress().delete();
    container.flush();
  }

  @Test
  public void deleteCollection() {
    container.getCustomers().getByKey(1).getEmails().delete();
    container.flush();
  }

  @Test
  public void deleteEdmStreamProperty() throws IOException {
    // ---------------------------------------
    // Instantiate Demo Service
    // ---------------------------------------
    final Service<EdmEnabledODataClient> dservice =
        Service.getV4(testDemoServiceRootURL);
    dservice.getClient().getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    final DemoService dcontainer = dservice.getEntityContainer(DemoService.class);
    assertNotNull(dcontainer);
    dservice.getContext().detachAll();
    // ---------------------------------------
    dcontainer.getPersonDetails().getByKey(1).delete("Photo");
    dcontainer.flush();

    dservice.getContext().detachAll(); // avoid influences
  }

  @Test
  public void updateComplexProperty() {
    Address homeAddress = container.getCustomers().getByKey(1).getHomeAddress();
    homeAddress.setCity("Pescara");
    homeAddress.setPostalCode("98052");
    container.flush();

    homeAddress = container.getCustomers().getByKey(1).getHomeAddress().load();
    assertEquals("Pescara", homeAddress.getCity());
    assertEquals("98052", homeAddress.getPostalCode());
  }

  @Test
  public void updateAndReadEdmStreamProperty() throws IOException {
    // ---------------------------------------
    // Instantiate Demo Service
    // ---------------------------------------
    final Service<EdmEnabledODataClient> dservice =
        Service.getV4(testDemoServiceRootURL);
    dservice.getClient().getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    final DemoService dcontainer = dservice.getEntityContainer(DemoService.class);
    assertNotNull(dcontainer);
    dservice.getContext().detachAll();
    // ---------------------------------------
    final String random = RandomStringUtils.random(124, "abcdefghijklmnopqrstuvwxyz");

    final PersonDetail personDetail = dcontainer.getPersonDetails().getByKey(1); // NO HTTP Request

    // 1 HTTP Request to add an Edm.Stream property value about MediaEditLink Photo
    personDetail.setPhoto(dcontainer.newEdmStreamValue("application/octet-stream", IOUtils.toInputStream(random)));

    dcontainer.flush();

    final EdmStreamValue actual = dcontainer.getPersonDetails().getByKey(1).getPhoto().load(); // 1 HTTP Request
    assertEquals(random, IOUtils.toString(actual.getStream()));

    dservice.getContext().detachAll(); // avoid influences
  }

  @Test
  public void boundOperationsAfterCreate() {
    final Product product = getContainer().newEntityInstance(Product.class);
    product.setProductID(1012);
    product.setName("Latte");
    product.setQuantityPerUnit("100g Bag");
    product.setUnitPrice(3.24f);
    product.setQuantityInStock(100);
    product.setDiscontinued(false);
    product.setUserAccess(AccessLevel.Execute);
    product.setSkinColor(Color.Blue);

    final PrimitiveCollection<Color> cc = getContainer().newPrimitiveCollection(Color.class);
    cc.add(Color.Red);
    cc.add(Color.Green);

    product.setCoverColors(cc);

    final ProductDetail detail = getContainer().newEntityInstance(ProductDetail.class);
    detail.setProductID(product.getProductID());
    detail.setProductDetailID(1012);
    detail.setProductName("LatteHQ");
    detail.setDescription("High-Quality Milk");

    final ProductDetailCollection detailCollection = getContainer().newEntityCollection(ProductDetailCollection.class);
    detailCollection.add(detail);

    product.setDetails(detailCollection);

    getContainer().getProducts().add(product);

    // The first HTTP Request to create product and the linked product detail
    getContainer().flush();

    // The second HTTP request to access a bound operation via the local object
    assertNotNull(product.operations().addAccessRight(AccessLevel.None).execute());

    // The third HTTP Request to access a bound operation via entity URL
    final ProductDetailCollectionComposableInvoker result =
        container.getProducts().getByKey(1012).operations().getProductDetails(1);
    assertEquals(1, result.execute().size());
  }

  @Test
  public void workingWithPrimitiveCollections() throws IOException {
    final PrimitiveCollection<String> emails = container.getPeople().getByKey(1).getEmails();
    assertNotNull(emails);
    assertTrue(emails.isEmpty());
    assertFalse(emails.execute().isEmpty());

    getService().getContext().detachAll();

    // container.getOrders().getByKey(1).getCustomerForOrder().getEmails().execute().isEmpty());
    // Not supported by the test service BTW generates a single request as expected:
    // <service root>/Orders(1)/CustomerForOrder/Emails
    emails.add("fabio.martelli@tirasa.net");
    container.getPeople().getByKey(1).setEmails(emails);

    container.flush();

    boolean found = false;
    for (String email : container.getPeople().getByKey(1).getEmails().execute()) {
      if (email.equals("fabio.martelli@tirasa.net")) {
        found = true;
      }
    }

    assertTrue(found);

    getService().getContext().detachAll();
  }

  @Test
  public void workingWithSingletons() {
    assertNotNull(container.getCompany().getVipCustomer().load().getPersonID());

    container.getCompany().setName("new name");
    container.flush();

    assertEquals("new name", container.getCompany().load().getName());
  }

  @Test
  public void createAndCallOperation() {
    final Product product = container.newEntityInstance(Product.class);
    product.setProductID(1001);
    container.flush();

    container.getProducts().getByKey(1000).operations().getProductDetails(1).execute();
  }

  @Test
  public void workingWithOperations() {
    // Primitive collections (available only skip and top)
    final PrimitiveCollection<String> prods1 = container.operations().
        getProductsByAccessLevel(AccessLevel.None).
        skip(2).
        top(3).execute();
    assertNotNull(prods1);
    assertFalse(prods1.isEmpty());

    // Complex/Entity collection
    final ProductCollection prods2 = container.operations().getAllProducts().
        filter("name eq XXXX").
        select("Name", "ProductDetail").
        expand("ProductDetail").
        orderBy("Name").skip(3).top(5).execute();
    assertNotNull(prods2);
    assertFalse(prods2.isEmpty());

    // Complex/Entity
    final Person person = container.operations().getPerson2("London").
        select("Name").
        expand("Order").execute();
    assertNotNull(person);

    // Primitive (no query option available)
    final Double amount = container.getAccounts().getByKey(101).getMyGiftCard().operations().
        getActualAmount(1.1).execute();
    assertNotNull(amount);

    // POST ...
    final Address address = container.newComplexInstance(HomeAddress.class);
    address.setStreet("Via Le Mani Dal Naso, 123");
    address.setPostalCode("Tollo");
    address.setCity("66010");

    final AddressCollection ac = container.newComplexCollection(AddressCollection.class);
    final Person updated = container.getCustomers().getByKey(2).operations().
        resetAddress(ac, 0).select("Name").expand("Orders").execute();
    assertNotNull(updated);
  }

  @Test
  public void workingWithComposableOperations() {
    final ProductCollectionComposableInvoker invoker1 = container.operations().getAllProducts();

    // Complex/Entity collection (available filter, select, expand, orderBy, skip and top)
    invoker1.operations().discount(10). // discount is an operation of ProductCollecton
    filter("Name eq XXXX").
    select("Name", "ProductDetail").
    expand("ProductDetail").
    orderBy("Name").skip(3).top(5).execute();

    // Complex/Entity
    final PersonComposableInvoker invoker2 = container.operations().getPerson2("London");

    // a. whole entity
    final Person person = invoker2.select("Name").expand("Order").execute();
    assertNotNull(person);
    assertEquals(1, person.getPersonID(), 0);

    // b. primitive collection property
    final PrimitiveCollection<String> emails = invoker2.getEmails().execute();
    assertNotNull(emails);
    assertFalse(emails.isEmpty());

    // c. complex property
    final Address homeAddress = invoker2.getHomeAddress().load();
    assertNotNull(homeAddress);

    // d. navigation property
    final Person parent = invoker2.getParent().load();
    assertNotNull(parent);
    assertEquals(2, parent.getPersonID(), 0);
  }

  /**
   * Java client should support the deletion based on locally created entity.
   * See also <a href="https://issues.apache.org/jira/browse/OLINGO-395">Olingo Issue 395</a>.
   */
  @Test
  public void issueOLINGO395() {
    Order order = getContainer().newEntityInstance(Order.class);
    order.setOrderID(1105);

    final Calendar orderDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    orderDate.clear();
    orderDate.set(2011, 3, 4, 16, 3, 57);
    order.setOrderDate(new Timestamp(orderDate.getTimeInMillis()));

    order.setShelfLife(BigDecimal.ZERO);

    final PrimitiveCollection<BigDecimal> osl = getContainer().newPrimitiveCollection(BigDecimal.class);
    osl.add(BigDecimal.TEN.negate());
    osl.add(BigDecimal.TEN);

    order.setOrderShelfLifes(osl);

    getContainer().getOrders().add(order);
    getContainer().getOrders().delete(order);

    getContainer().flush();

    service.getContext().detachAll();
    try {
      getContainer().getOrders().getByKey(1105).load();
      fail();
    } catch (IllegalArgumentException e) {
      // Expected Exception
    }
    service.getContext().detachAll(); // avoid influences

    order = getContainer().newEntityInstance(Order.class);
    order.setOrderID(1105);

    getContainer().getOrders().delete(order);
    getContainer().flush(); // test service doesn't fail for delete requests about unexisting objects

    service.getContext().detachAll();
    try {
      getContainer().getOrders().getByKey(1105).load();
      fail();
    } catch (IllegalArgumentException e) {
      // Expected Exception
    }
    service.getContext().detachAll(); // avoid influences
  }

  @Test
  public void issueOLINGO398() {
    AbstractCollectionInvocationHandler<?, ?> handler = AbstractCollectionInvocationHandler.class.cast(
        Proxy.getInvocationHandler(container.getCustomers().getByKey(1).getOrders().
            select("OrderID", "CustomerForOrder").
            expand("CustomerForOrder").
            top(1).
            skip(2)));

    assertEquals("http://localhost:9080/stub/StaticService/V40/Static.svc/Customers(1)/Orders?"
        + "%24select=OrderID%2CCustomerForOrder&%24expand=CustomerForOrder&%24top=1&%24skip=2",
        handler.getRequestURI().toASCIIString());

    handler = AbstractCollectionInvocationHandler.class.cast(
        Proxy.getInvocationHandler(container.getCustomers().getByKey(1).getOrders().
            select("OrderID", "CustomerForOrder").
            expand("CustomerForOrder").
            top(1).
            skip(2)));

    assertEquals("http://localhost:9080/stub/StaticService/V40/Static.svc/Customers(1)/Orders?%24"
        + "select=OrderID%2CCustomerForOrder&%24expand=CustomerForOrder&%24top=1&%24skip=2",
        handler.getRequestURI().toASCIIString());
  }
}
