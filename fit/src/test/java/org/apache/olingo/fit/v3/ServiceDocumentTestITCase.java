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
package org.apache.olingo.fit.v3;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

public class ServiceDocumentTestITCase extends AbstractTestITCase {

  private void retrieveServiceDocument(final ODataFormat format) {
    final ODataServiceDocumentRequest req =
            client.getRetrieveRequestFactory().getServiceDocumentRequest(testStaticServiceRootURL);
    req.setFormat(format);

    final ODataRetrieveResponse<ODataServiceDocument> res = req.execute();
    assertEquals(200, res.getStatusCode());

    final ODataServiceDocument serviceDocument = res.getBody();
    assertEquals(24, serviceDocument.getEntitySetTitles().size());

    assertEquals(URI.create(testStaticServiceRootURL + "/ComputerDetail"),
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
