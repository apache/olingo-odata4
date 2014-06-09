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

import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.commons.api.domain.ODataError;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.api.op.ODataDeserializerException;
import org.apache.olingo.client.core.AbstractTest;
import org.junit.Test;

public class ErrorTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v3Client;
  }

  private ODataError error(final String name, final ODataPubFormat format) throws ODataDeserializerException {
    final ODataError error = getClient().getDeserializer(format).toError(
            getClass().getResourceAsStream(name + "." + getSuffix(format)));
    assertNotNull(error);
    return error;
  }

  private void simple(final ODataPubFormat format) throws ODataDeserializerException {
    final ODataError error = error("error", format);
    assertEquals("The URL representing the root of the service only supports GET requests.", error.getMessage());
  }

  @Test
  public void jsonSimple() throws Exception {
    simple(ODataPubFormat.JSON);
  }

  @Test
  public void atomSimple() throws Exception {
    simple(ODataPubFormat.ATOM);
  }

  private void stacktrace(final ODataPubFormat format) throws ODataDeserializerException {
    final ODataError error = error("stacktrace", format);
    assertEquals("Unsupported media type requested.", error.getMessage());
  }

  @Test
  public void jsonStacktrace() throws Exception {
    stacktrace(ODataPubFormat.JSON);
  }

  @Test
  public void atomStacktrace() throws Exception {
    stacktrace(ODataPubFormat.ATOM);
  }

}
