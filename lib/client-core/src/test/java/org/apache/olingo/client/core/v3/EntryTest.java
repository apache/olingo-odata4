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
package org.apache.olingo.client.core.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import org.apache.olingo.client.api.ODataConstants;
import org.apache.olingo.client.api.ODataV3Client;
import org.apache.olingo.client.api.data.Entry;
import org.apache.olingo.client.api.format.ODataPubFormat;
import org.apache.olingo.client.api.utils.XMLUtils;
import org.apache.olingo.client.core.AbstractTest;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class EntryTest extends AbstractTest {

  @Override
  protected ODataV3Client getClient() {
    return v3Client;
  }

  private void read(final ODataPubFormat format) throws IOException {
    InputStream input = getClass().getResourceAsStream("Car_16." + getSuffix(format));

    Entry entry = getClient().getDeserializer().toEntry(input, format);
    assertNotNull(entry);

    assertEquals("Car(16)", entry.getEditLink().getHref());
    assertEquals(2, entry.getMediaEditLinks().size());

    // this is a media entry
    assertNull(entry.getContent());

    final Element vin = XMLUtils.getChildElements(entry.getMediaEntryProperties(), "d:VIN").get(0);
    assertNotNull(vin);
    assertEquals("16", vin.getTextContent());

    input.close();

    // ---------------------------------------------
    input = getClass().getResourceAsStream("Customer_-10." + getSuffix(format));

    entry = getClient().getDeserializer().toEntry(input, format);
    assertNotNull(entry);

    input.close();

    assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer", entry.getType());
    assertNotNull(entry.getBaseURI());

    final Element content = entry.getContent();
    assertEquals(ODataConstants.ELEM_PROPERTIES, content.getNodeName());

    boolean entered = false;
    boolean checked = false;
    for (int i = 0; i < content.getChildNodes().getLength(); i++) {
      entered = true;

      final Node property = content.getChildNodes().item(i);
      if ("PrimaryContactInfo".equals(XMLUtils.getSimpleName(property))) {
        checked = true;

        assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails",
                ((Element) property).getAttribute(ODataConstants.ATTR_M_TYPE));
      }
    }
    assertTrue(entered);
    assertTrue(checked);
  }

  @Test
  public void atom() throws IOException {
    read(ODataPubFormat.ATOM);
  }

  @Test
  public void json() throws IOException {
    read(ODataPubFormat.JSON_FULL_METADATA);
  }
}
