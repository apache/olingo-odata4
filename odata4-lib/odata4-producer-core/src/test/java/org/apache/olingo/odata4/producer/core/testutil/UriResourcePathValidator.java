/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.producer.core.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriPathInfoKind;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.UriParserImpl;
import org.apache.olingo.odata4.producer.core.uri.UriPathInfoImpl;

public class UriResourcePathValidator {
  UriInfoImpl uriInfo = null;
  UriPathInfoImpl uriPathInfo = null; // last
  private Edm edm;

  public UriResourcePathValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  public UriResourcePathValidator run(final String uri) {
    uriInfo = parseUri(uri);
    last();
    return this;
  }

  public UriResourcePathValidator isUriPathInfoKind(final UriPathInfoKind infoType) {

    assertNotNull(uriPathInfo);
    assertEquals(infoType, uriPathInfo.getKind());
    return this;
  }

  private UriInfoImpl parseUri(final String uri) {
    UriParserImpl reader = new UriParserImpl();
    UriInfoImpl uriInfo = reader.readUri(uri, edm);
    return uriInfo;
  }

  public UriResourcePathValidator last() {
    // TODO implement
    // uriPathInfo = uriInfo.getLastUriPathInfo();
    return this;
  }

  public UriResourcePathValidator at(final int index) {
    try {
      // uriPathInfo = uriInfo.getUriPathInfo(index);
    } catch (IndexOutOfBoundsException ex) {
      uriPathInfo = null;
    }
    return this;
  }

  public UriResourcePathValidator first() {
    try {
      // uriPathInfo = uriInfo.getUriPathInfo(0);
    } catch (IndexOutOfBoundsException ex) {
      uriPathInfo = null;
    }
    return this;
  }

  public void isKind(final UriInfoKind batch) {

    assertEquals(batch, uriInfo.getKind());
  }

}
