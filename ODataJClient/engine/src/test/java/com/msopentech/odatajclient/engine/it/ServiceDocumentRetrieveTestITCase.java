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
package com.msopentech.odatajclient.engine.it;

import static org.junit.Assert.*;
import java.net.URI;
import org.junit.Test;

import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataServiceDocumentRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataServiceDocument;
import com.msopentech.odatajclient.engine.format.ODataFormat;

public class ServiceDocumentRetrieveTestITCase extends AbstractTestITCase {
    // retrieve service document

    private void retrieveServiceDocumentTest(final ODataFormat reqFormat, final String acceptFormat) {
        final ODataServiceDocumentRequest req = client.getRetrieveRequestFactory().getServiceDocumentRequest(
                testDefaultServiceRootURL);
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        final ODataRetrieveResponse<ODataServiceDocument> res = req.execute();
        assertEquals(200, res.getStatusCode());
        final ODataServiceDocument serviceDocument = res.getBody();
        assertEquals(24, serviceDocument.getEntitySetTitles().size());
        assertEquals(URI.create(testDefaultServiceRootURL + "/Customer"),
                serviceDocument.getEntitySetURI("Customer"));
    }
    //with json header

    @Test
    public void jsonTest() {
        retrieveServiceDocumentTest(ODataFormat.JSON, "application/json");
    }
    //with json header no metadata

    @Test
    public void jsonNoMetadataTest() {
        retrieveServiceDocumentTest(ODataFormat.JSON_NO_METADATA, "application/json");
    }
    //with xml header

    @Test
    public void xmlTest() {
        retrieveServiceDocumentTest(ODataFormat.XML, "application/xml");
    }
    //unsupported media type. 415 error

    @Test(expected = ODataClientErrorException.class)
    public void atomAcceptTest() {
        retrieveServiceDocumentTest(ODataFormat.XML, "application/atom+xml");
    }
    // test with xml format and null accept header

    @Test
    public void nullAcceptTest() {
        retrieveServiceDocumentTest(ODataFormat.XML, null);
    }
    // null format test

    @Test
    public void nullServiceFormatTest() {
        retrieveServiceDocumentTest(null, "application/xml");
    }
}
