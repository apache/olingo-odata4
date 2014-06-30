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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.ext.proxy.EntityContainerFactory;
import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4.DefaultContainer;
import org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4.types.AccountInfo;
import org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4.types.Color;
import org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4.types.ContactDetails;
import org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4.types.Row;
import org.apache.olingo.fit.proxy.v4.opentype.microsoft.test.odata.services.opentypesservicev4.types.RowIndex;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is the unit test class to check actions overloading.
 */
public class OpenTypeTestITCase extends AbstractTestITCase {

  private static EntityContainerFactory<EdmEnabledODataClient> otcontainerFactory;

  private static DefaultContainer otcontainer;

  @BeforeClass
  public static void initContainer() {
    otcontainerFactory = EntityContainerFactory.getV4(testOpenTypeServiceRootURL);
    otcontainerFactory.getClient().getConfiguration().
        setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    otcontainer = otcontainerFactory.getEntityContainer(DefaultContainer.class);
    assertNotNull(otcontainer);
  }

  @Test
  public void checkOpenTypeEntityTypesExist() {
    assertTrue(otcontainer.getRow().newRow().getClass().getInterfaces()[0].
        getAnnotation(EntityType.class).openType());
    assertTrue(otcontainer.getRowIndex().newRowIndex().getClass().getInterfaces()[0].
        getAnnotation(EntityType.class).openType());
    assertTrue(otcontainer.getRow().newIndexedRow().getClass().getInterfaces()[0].
        getAnnotation(EntityType.class).openType());
    otcontainerFactory.getContext().detachAll();
  }

  @Test
  public void read() {
    Row row = otcontainer.getRow().get(UUID.fromString("71f7d0dc-ede4-45eb-b421-555a2aa1e58f"));
    assertEquals(Double.class, row.getAdditionalProperty("Double").getClass());
    assertEquals("71f7d0dc-ede4-45eb-b421-555a2aa1e58f", row.getId().toString());

    row = otcontainer.getRow().get(UUID.fromString("672b8250-1e6e-4785-80cf-b94b572e42b3"));
    assertEquals(BigDecimal.class, row.getAdditionalProperty("Decimal").getClass());
  }

  @Test
  public void cud() throws ParseException {
    final Integer id = 1426;

    RowIndex rowIndex = otcontainer.getRowIndex().newRowIndex();
    rowIndex.setId(id);
    rowIndex.addAdditionalProperty("aString", "string");
    rowIndex.addAdditionalProperty("aBoolean", true);
    rowIndex.addAdditionalProperty("aDouble", 1.5D);
    rowIndex.addAdditionalProperty("aByte", Byte.MAX_VALUE);
    rowIndex.addAdditionalProperty("aDate", Calendar.getInstance());

    final ContactDetails contact = otcontainer.complexFactory().newContactDetails();
    contact.setFirstContacted("text".getBytes());

    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2001-04-05T05:05:05.001"));

    contact.setLastContacted(cal);

    cal = Calendar.getInstance();
    cal.clear();
    cal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2001-04-05T05:05:04.001"));
    contact.setContacted(cal);

    contact.setGUID(UUID.randomUUID());
    contact.setPreferedContactTime(cal);
    contact.setByte(Short.valueOf("24"));
    contact.setSignedByte(Byte.MAX_VALUE);
    contact.setDouble(Double.valueOf(Double.MAX_VALUE));
    contact.setSingle(Float.MAX_VALUE);
    contact.setShort(Short.MAX_VALUE);
    contact.setInt(Integer.MAX_VALUE);
    rowIndex.addAdditionalProperty("aContact", contact);
    rowIndex.addAdditionalProperty("aColor", Color.Green);

    final AccountInfo ai = otcontainer.complexFactory().newAccountInfo();
    ai.setFirstName("Fabio");
    ai.setLastName("Martelli");
    ai.addAdditionalProperty("email", "fabio.martelli@tirasa.net");
    rowIndex.addAdditionalProperty("info", ai);

    otcontainer.flush();

    rowIndex = otcontainer.getRowIndex().get(id);
    assertEquals(String.class, rowIndex.getAdditionalProperty("aString").getClass());
    assertEquals(Boolean.class, rowIndex.getAdditionalProperty("aBoolean").getClass());
    assertEquals(Double.class, rowIndex.getAdditionalProperty("aDouble").getClass());
    assertEquals(Byte.class, rowIndex.getAdditionalProperty("aByte").getClass());
    assertEquals(Byte.MAX_VALUE, rowIndex.getAdditionalProperty("aByte"));
    assertTrue(Calendar.class.isAssignableFrom(rowIndex.getAdditionalProperty("aDate").getClass()));
    assertEquals(ContactDetails.class, rowIndex.getAdditionalProperty("aContact").getClass().getInterfaces()[0]);
    assertEquals(Color.class, rowIndex.getAdditionalProperty("aColor").getClass());
    assertEquals(Color.Green, rowIndex.getAdditionalProperty("aColor"));
    assertEquals("Fabio", AccountInfo.class.cast(rowIndex.getAdditionalProperty("info")).getFirstName());
    assertEquals("Martelli", AccountInfo.class.cast(rowIndex.getAdditionalProperty("info")).getLastName());
    assertEquals("fabio.martelli@tirasa.net", AccountInfo.class.cast(rowIndex.getAdditionalProperty("info")).
        getAdditionalProperty("email"));

    otcontainerFactory.getContext().detachAll();

    otcontainer.getRowIndex().delete(id);
    otcontainer.flush();

    assertNull(otcontainer.getRowIndex().get(id));
  }
}
