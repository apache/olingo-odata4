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

import static org.junit.Assert.*;
import org.junit.Test;

import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.format.ODataValueFormat;
import org.apache.olingo.client.api.uri.CommonURIBuilder;

public class CountTestITCase extends AbstractTestITCase {

  @Test
  public void entityCount() {
    CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Customer").count();
    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setFormat(ODataValueFormat.TEXT);
    try {
      final ODataValue value = req.execute().getBody();
      assertTrue(10 <= Integer.parseInt(value.toString()));
    } catch (ODataClientErrorException e) {
      LOG.error("Error code: {}", e.getStatusLine().getStatusCode(), e);
    }
  }

  @Test
  public void invalidAccept() {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Customer").count();
    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setFormat(ODataValueFormat.TEXT);
    req.setAccept("application/json;odata=fullmetadata");
    try {
      final ODataValue value = req.execute().getBody();
      fail();
    } catch (ODataClientErrorException e) {
      assertEquals(415, e.getStatusLine().getStatusCode());
    }
  }
}
