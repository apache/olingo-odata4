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
package org.apache.olingo.fit.v4;

import org.apache.commons.lang3.RandomUtils;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

import java.net.URI;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EntityCreateTestITCase extends AbstractTestITCase {

  @Test
  public void atomCreateAndDelete() {
    createAndDeleteOrder(testStaticServiceRootURL, ODataFormat.ATOM, 1000);
  }

  @Test
  public void jsonCreateAndDelete() {
    createAndDeleteOrder(testStaticServiceRootURL, ODataFormat.JSON, 1001);
    createAndDeleteOrder(testStaticServiceRootURL, ODataFormat.JSON_NO_METADATA, 1001);
    createAndDeleteOrder(testStaticServiceRootURL, ODataFormat.JSON_FULL_METADATA, 1001);
  }


  private void onContained(final ODataFormat format) {
    final URI uri = getClient().newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Accounts").
        appendKeySegment(101).appendNavigationSegment("MyPaymentInstruments").build();

    // 1. read contained collection before any operation
    ODataEntitySet instruments = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri).execute().getBody();
    assertNotNull(instruments);
    final int sizeBefore = instruments.getCount();

    // 2. instantiate an ODataEntity of the same type as the collection above
    final ODataEntity instrument = getClient().getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument"));

    int id = RandomUtils.nextInt(101999, 105000);
    instrument.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("PaymentInstrumentID",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(id)));
    instrument.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("FriendlyName",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("New one")));
    instrument.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("CreatedDate",
        getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.DateTimeOffset).setValue(Calendar.getInstance()).build()));

    // 3. create it as contained entity
    final ODataEntityCreateRequest<ODataEntity> req = getClient().getCUDRequestFactory().
        getEntityCreateRequest(uri, instrument);
    req.setFormat(format);

    final ODataEntityCreateResponse<ODataEntity> res = req.execute();
    assertEquals(201, res.getStatusCode());

    // 4. verify that the contained collection effectively grew
    instruments = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri).execute().getBody();
    assertNotNull(instruments);
    final int sizeAfter = instruments.getCount();
    assertEquals(sizeBefore + 1, sizeAfter);

    // 5. remove the contained entity created above
    final ODataDeleteResponse deleteRes = getClient().getCUDRequestFactory().
        getDeleteRequest(getClient().newURIBuilder(uri.toASCIIString()).appendKeySegment(id).build()).execute();
    assertEquals(204, deleteRes.getStatusCode());

    // 6. verify that the contained collection effectively reduced
    instruments = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri).execute().getBody();
    assertNotNull(instruments);
    final int sizeEnd = instruments.getCount();
    assertEquals(sizeBefore, sizeEnd);
  }

  @Test
  public void atomOnContained() {
    onContained(ODataFormat.ATOM);
  }

  @Test
  public void jsonOnContained() {
    onContained(ODataFormat.JSON);
  }

  private void deepInsert(final ODataFormat format, final int productId, final int productDetailId)
      throws EdmPrimitiveTypeException {

    final ODataEntity product = getClient().getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Product"));
    product.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("ProductID",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(productId)));
    product.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("Name",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("Latte")));
    product.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("QuantityPerUnit",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("100g Bag")));
    product.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("UnitPrice",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildSingle(3.24f)));
    product.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("QuantityInStock",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(100)));
    product.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("Discontinued",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildBoolean(false)));
    product.getProperties().add(getClient().getObjectFactory().newEnumProperty("UserAccess",
        getClient().getObjectFactory().
            newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "Execute")));
    product.getProperties().add(getClient().getObjectFactory().newEnumProperty("SkinColor",
        getClient().getObjectFactory().
            newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.Color", "Blue")));
    product.getProperties().add(getClient().getObjectFactory().newCollectionProperty("CoverColors",
        getClient().getObjectFactory().
            newCollectionValue("Microsoft.Test.OData.Services.ODataWCFService.Color")));
    product.getProperty("CoverColors").getCollectionValue().add(getClient().getObjectFactory().
        newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.Color", "Green"));
    product.getProperty("CoverColors").getCollectionValue().add(getClient().getObjectFactory().
        newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.Color", "Red"));

    final ODataEntity detail = getClient().getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.ProductDetail"));
    detail.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("ProductID",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(productId)));
    detail.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("ProductDetailID",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(productDetailId)));
    detail.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("ProductName",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("LatteHQ")));
    detail.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("Description",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("High-Quality Milk")));

    final ODataEntitySet details = getClient().getObjectFactory().newEntitySet();
    details.getEntities().add(detail);

    final ODataInlineEntitySet inlineDetails = getClient().getObjectFactory().
        newDeepInsertEntitySet("Details", details);
    product.addLink(inlineDetails);

    final ODataEntityCreateRequest<ODataEntity> req = getClient().getCUDRequestFactory().getEntityCreateRequest(
        getClient().newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Products").build(), product);
    req.setFormat(format);
    final ODataEntityCreateResponse<ODataEntity> res = req.execute();
    assertEquals(201, res.getStatusCode());

    final ODataEntity createdProduct = res.getBody();
    assertEquals(productId,
        createdProduct.getProperty("ProductID").getPrimitiveValue().toCastValue(Integer.class), 0);

    final ODataLink createdLink = createdProduct.getNavigationLink("Details");
    assertNotNull(createdLink);

    final ODataEntitySet createdProductDetails =
        getClient().getRetrieveRequestFactory().getEntitySetRequest(createdLink.getLink()).execute().getBody();
    assertNotNull(createdProductDetails);
    assertEquals(productDetailId, createdProductDetails.getEntities().iterator().next().
        getProperty("ProductDetailID").getPrimitiveValue().toCastValue(Integer.class), 0);
  }

  @Test
  public void atomDeepInsert() throws EdmPrimitiveTypeException {
    deepInsert(ODataFormat.ATOM, 10, 10);
  }

  @Test
  public void jsonDeepInsert() throws EdmPrimitiveTypeException {
    deepInsert(ODataFormat.JSON_FULL_METADATA, 11, 11);
  }
}
