/**
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

    private void retrieveServiceDocumentTest(final ODataFormat reqFormat, final String acceptFormat) {
        final ODataServiceDocumentRequest req =
                client.getRetrieveRequestFactory().getServiceDocumentRequest(testStaticServiceRootURL);
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        final ODataRetrieveResponse<ODataServiceDocument> res = req.execute();
        assertEquals(200, res.getStatusCode());
        final ODataServiceDocument serviceDocument = res.getBody();
        assertEquals(24, serviceDocument.getEntitySetTitles().size());
        assertEquals(URI.create(testStaticServiceRootURL + "/Customer"), serviceDocument.getEntitySetURI("Customer"));
    }

    @Test
    public void jsonTest() {
        retrieveServiceDocumentTest(ODataFormat.JSON, "application/json");
    }

    @Test
    public void jsonNoMetadataTest() {
        retrieveServiceDocumentTest(ODataFormat.JSON_NO_METADATA, "application/json");
    }

    @Test
    public void xmlTest() {
        retrieveServiceDocumentTest(ODataFormat.XML, "application/xml");
    }

    @Test(expected = ODataClientErrorException.class)
    public void atomAcceptTest() {
        retrieveServiceDocumentTest(ODataFormat.XML, "application/atom+xml");
    }

    @Test
    public void nullAcceptTest() {
        retrieveServiceDocumentTest(ODataFormat.XML, null);
    }

    @Test
    public void nullServiceFormatTest() {
        retrieveServiceDocumentTest(null, "application/xml");
    }
}
