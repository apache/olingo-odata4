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

import org.apache.olingo.client.api.v3.EdmEnabledODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.ext.proxy.Service;
import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.fit.proxy.v3.opentype.microsoft.test.odata.services.opentypesservicev3.DefaultContainer;
import org.apache.olingo.fit.proxy.v3.opentype.microsoft.test.odata.services.opentypesservicev3.types.ContactDetails;
import org.apache.olingo.fit.proxy.v3.opentype.microsoft.test.odata.services.opentypesservicev3.types.Row;
import org.apache.olingo.fit.proxy.v3.opentype.microsoft.test.odata.services.opentypesservicev3.types.RowIndex;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4.types.IndexedRow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This is the unit test class to check actions overloading.
 */
public class OpenTypeTestITCase extends AbstractTestITCase {

  private static Service<EdmEnabledODataClient> otservice;

  private static DefaultContainer otcontainer;

  @BeforeClass
  public static void initContainer() {
    otservice = Service.getV3(testOpenTypeServiceRootURL);
    otservice.getClient().getConfiguration().
            setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    otcontainer = otservice.getEntityContainer(DefaultContainer.class);
    assertNotNull(otcontainer);
  }

  @Test
  public void checkOpenTypeEntityTypesExist() {
    assertTrue(otservice.newEntityInstance(Row.class).getClass().getInterfaces()[0].
            getAnnotation(EntityType.class).openType());
    assertTrue(otservice.newEntityInstance(RowIndex.class).getClass().getInterfaces()[0].
            getAnnotation(EntityType.class).openType());
    assertTrue(otservice.newEntityInstance(IndexedRow.class).getClass().getInterfaces()[0].
            getAnnotation(EntityType.class).openType());
    otservice.getContext().detachAll();
  }

  @Test
  public void read() {
    Row row = otcontainer.getRow().getByKey(UUID.fromString("71f7d0dc-ede4-45eb-b421-555a2aa1e58f")).load();
    assertEquals(Double.class, row.getAdditionalProperty("Double").getClass());
    assertEquals("71f7d0dc-ede4-45eb-b421-555a2aa1e58f", row.getId().toString());

    row = otcontainer.getRow().getByKey(UUID.fromString("672b8250-1e6e-4785-80cf-b94b572e42b3")).load();
    assertEquals(BigDecimal.class, row.getAdditionalProperty("Decimal").getClass());
  }

  @Test
  public void cud() throws ParseException {
    final Integer id = 1426;

    RowIndex rowIndex = otservice.newEntityInstance(RowIndex.class);
    rowIndex.setId(id);
    rowIndex.addAdditionalProperty("aString", "string");
    rowIndex.addAdditionalProperty("aBoolean", true);
    rowIndex.addAdditionalProperty("aDouble", 1.5D);
    rowIndex.addAdditionalProperty("aByte", Byte.MAX_VALUE);
    rowIndex.addAdditionalProperty("aDate", Calendar.getInstance());

    final ContactDetails contact = otservice.newComplexInstance(ContactDetails.class);
    contact.setFirstContacted("text".getBytes());

    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2001-04-05T05:05:05.001"));

    contact.setLastContacted(new Timestamp(cal.getTimeInMillis()));

    cal = Calendar.getInstance();
    cal.clear();
    cal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2001-04-05T05:05:04.001"));
    contact.setContacted(new Timestamp(cal.getTimeInMillis()));

    contact.setGUID(UUID.randomUUID());
    contact.setPreferedContactTime(BigDecimal.ONE);
    contact.setByte(Short.valueOf("24"));
    contact.setSignedByte(Byte.MAX_VALUE);
    contact.setDouble(Double.valueOf(Double.MAX_VALUE));
    contact.setSingle(Float.MAX_VALUE);
    contact.setShort(Short.MAX_VALUE);
    contact.setInt(Integer.MAX_VALUE);
    rowIndex.addAdditionalProperty("aContact", contact);

    otcontainer.getRowIndex().add(rowIndex);
    otcontainer.flush();

    rowIndex = otcontainer.getRowIndex().getByKey(id).load();
    assertEquals(String.class, rowIndex.getAdditionalProperty("aString").getClass());
    assertEquals(Boolean.class, rowIndex.getAdditionalProperty("aBoolean").getClass());
    assertEquals(Double.class, rowIndex.getAdditionalProperty("aDouble").getClass());
    assertEquals(Byte.class, rowIndex.getAdditionalProperty("aByte").getClass());
    assertEquals(Byte.MAX_VALUE, rowIndex.getAdditionalProperty("aByte"));
    assertTrue(Timestamp.class.isAssignableFrom(rowIndex.getAdditionalProperty("aDate").getClass()));
    assertEquals(ContactDetails.class, rowIndex.getAdditionalProperty("aContact").getClass().getInterfaces()[0]);

    otservice.getContext().detachAll();

    otcontainer.getRowIndex().delete(id);
    otcontainer.flush();

    try {
      otcontainer.getRowIndex().getByKey(id).load();
    } catch (IllegalArgumentException e) {
    }
  }
}
