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

import static org.junit.Assert.assertEquals;

import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataServiceDocumentRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataServiceDocument;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import java.net.URI;
import org.junit.Test;

public class ServiceDocumentTestITCase extends AbstractTestITCase {

    private void retrieveServiceDocument(final ODataFormat format) {
        final ODataServiceDocumentRequest req =
                client.getRetrieveRequestFactory().getServiceDocumentRequest(testDefaultServiceRootURL);
        req.setFormat(format);

        final ODataRetrieveResponse<ODataServiceDocument> res = req.execute();
        assertEquals(200, res.getStatusCode());

        final ODataServiceDocument serviceDocument = res.getBody();
        assertEquals(24, serviceDocument.getEntitySetTitles().size());

        assertEquals(URI.create(testDefaultServiceRootURL + "/ComputerDetail"),
                serviceDocument.getEntitySetURI("ComputerDetail"));
    }

    @Test
    public void retrieveServiceDocumentAsXML() {
        retrieveServiceDocument(ODataFormat.XML);
    }

    @Test
    public void retrieveServiceDocumentAsJSON() {
        retrieveServiceDocument(ODataFormat.JSON);
    }
}
