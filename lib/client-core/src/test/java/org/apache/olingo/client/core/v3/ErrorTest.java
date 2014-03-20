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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.olingo.client.api.ODataV3Client;
import org.apache.olingo.client.api.data.ODataError;
import org.apache.olingo.client.api.format.ODataPubFormat;
import org.apache.olingo.client.core.AbstractTest;
import org.junit.Test;

public class ErrorTest extends AbstractTest {

  @Override
  protected ODataV3Client getClient() {
    return v3Client;
  }

  private ODataError error(final String name, final ODataPubFormat format) {
    final ODataError error = getClient().getDeserializer().toError(
            getClass().getResourceAsStream(name + "." + getSuffix(format)), format == ODataPubFormat.ATOM);
    assertNotNull(error);
    return error;
  }

  private void simple(final ODataPubFormat format) {
    final ODataError error = error("error", format);
    assertNull(error.getInnerErrorStacktrace());
  }

  @Test
  public void jsonSimple() {
    simple(ODataPubFormat.JSON);
  }

  @Test
  public void atomSimple() {
    simple(ODataPubFormat.ATOM);
  }

  private void stacktrace(final ODataPubFormat format) {
    final ODataError error = error("stacktrace", format);
    assertNotNull(error.getInnerErrorStacktrace());
  }

  @Test
  public void jsonStacktrace() {
    stacktrace(ODataPubFormat.JSON);
  }

  @Test
  public void atomStacktrace() {
    stacktrace(ODataPubFormat.ATOM);
  }

}
