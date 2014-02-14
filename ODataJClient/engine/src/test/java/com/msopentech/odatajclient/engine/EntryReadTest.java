/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.msopentech.odatajclient.engine.client.ODataV3Client;
import com.msopentech.odatajclient.engine.data.Entry;
import com.msopentech.odatajclient.engine.data.ResourceFactory;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import com.msopentech.odatajclient.engine.utils.XMLUtils;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class EntryReadTest extends AbstractTest {

    @Override
    protected ODataV3Client getClient() {
        return v3Client;
    }

    private void read(final ODataPubFormat format) throws IOException {
        InputStream input = getClass().getResourceAsStream("Car_16." + getSuffix(format));

        Entry entry = getClient().getDeserializer().toEntry(input, ResourceFactory.entryClassForFormat(format));
        assertNotNull(entry);

        input.close();

        // ---------------------------------------------
        input = getClass().getResourceAsStream("Customer_-10." + getSuffix(format));

        entry = getClient().getDeserializer().toEntry(input, ResourceFactory.entryClassForFormat(format));
        assertNotNull(entry);

        input.close();

        if (ODataPubFormat.JSON_FULL_METADATA == format || ODataPubFormat.ATOM == format) {
            assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer", entry.getType());
            assertNotNull(entry.getBaseURI());
        }

        final Element content = entry.getContent();
        assertEquals(ODataConstants.ELEM_PROPERTIES, content.getNodeName());

        boolean entered = false;
        boolean checked = false;
        for (int i = 0; i < content.getChildNodes().getLength(); i++) {
            entered = true;

            final Node property = content.getChildNodes().item(i);
            if ("PrimaryContactInfo".equals(XMLUtils.getSimpleName(property))) {
                checked = true;

                if (ODataPubFormat.JSON_FULL_METADATA == format || ODataPubFormat.ATOM == format) {
                    assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails",
                            ((Element) property).getAttribute(ODataConstants.ATTR_M_TYPE));
                }
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
