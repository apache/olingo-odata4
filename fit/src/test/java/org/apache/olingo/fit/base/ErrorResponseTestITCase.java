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
package org.apache.olingo.fit.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Map;

import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.commons.api.ex.ODataError;
import org.apache.olingo.commons.api.ex.ODataErrorDetail;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class ErrorResponseTestITCase extends AbstractTestITCase {

  @Test
  public void jsonError() {
    final URI readURI = getClient().newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(32).
        build();

    try {
      read(ContentType.JSON, readURI);
      fail("should have got exception");
    } catch (Exception ex) {
      final ODataError err = ((ODataClientErrorException) ex).getODataError();

      // verify details
      final ODataErrorDetail detail = err.getDetails().get(0);
      assertEquals("Code should be correct", "301", detail.getCode());
      assertEquals("Target should be correct", "$search", detail.getTarget());
      assertEquals("Message should be correct", "$search query option not supported", detail.getMessage());

      // verify inner error dictionary
      final Map<String, String> innerErr = err.getInnerError();
      assertEquals("innerError dictionary size should be correct", 2, innerErr.size());
      assertEquals("innerError['context'] should be correct",
          "{\"key1\":\"for debug deployment only\"}", innerErr.get("context"));
      assertEquals("innerError['trace'] should be correct",
          "[\"callmethod1 etc\",\"callmethod2 etc\"]", innerErr.get("trace"));
    }
  }
}
