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
package org.apache.olingo.fit.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

/**
 * This is the unit test class to check basic entity operations.
 */
public class ErrorTestITCase extends AbstractTestITCase {

  private class ErrorGeneratingRequest
      extends AbstractODataBasicRequest<ODataEntityCreateResponse<ODataEntity>> {

    public ErrorGeneratingRequest(final HttpMethod method, final URI uri) {
      super(client, method, uri);
    }

    @Override
    public ODataFormat getDefaultFormat() {
      return odataClient.getConfiguration().getDefaultPubFormat();
    }

    @Override
    protected InputStream getPayload() {
      return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public ODataEntityCreateResponse<ODataEntity> execute() {
      final HttpResponse res = doExecute();
      return new ErrorResponseImpl(client, httpClient, res);
    }

    private class ErrorResponseImpl extends AbstractODataResponse implements ODataEntityCreateResponse<ODataEntity> {

      private final ODataClient odataClient;

      public ErrorResponseImpl(final ODataClient odataClient, final HttpClient client, final HttpResponse res) {
        super(client, res);
        this.odataClient = odataClient;
      }

      @Override
      public ODataEntity getBody() {
        return odataClient.getObjectFactory().newEntity(new FullQualifiedName("Invalid.Invalid"));
      }
    }
  }

  private void stacktraceError(final ODataFormat format) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL);
    uriBuilder.appendEntitySetSegment("Customer");

    final ErrorGeneratingRequest errorReq = new ErrorGeneratingRequest(HttpMethod.POST, uriBuilder.build());
    errorReq.setFormat(format);

    try {
      errorReq.execute();
      fail();
    } catch (ODataClientErrorException e) {
      LOG.error("ODataClientErrorException found", e);
      assertEquals(400, e.getStatusLine().getStatusCode());
      assertNotNull(e.getODataError());
    }
  }

  @Test
  public void xmlStacktraceError() {
    stacktraceError(ODataFormat.ATOM);
  }

  @Test
  public void jsonStacktraceError() {
    stacktraceError(ODataFormat.JSON);
  }

  private void notfoundError(final ODataFormat format) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL);
    uriBuilder.appendEntitySetSegment("Customer(154)");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    try {
      req.execute();
      fail();
    } catch (ODataClientErrorException e) {
      LOG.error("ODataClientErrorException found", e);
      assertEquals(404, e.getStatusLine().getStatusCode());
      assertNull(e.getCause());
      assertNotNull(e.getODataError());
    }
  }

  @Test
  public void xmlNotfoundError() {
    notfoundError(ODataFormat.ATOM);
  }

  @Test
  public void jsonNotfoundError() {
    notfoundError(ODataFormat.JSON);
  }

  private void instreamError(final ODataFormat format) {
    final URIBuilder builder = client.newURIBuilder(testStaticServiceRootURL).
        appendOperationCallSegment("InStreamErrorGetCustomer");
    final ODataInvokeRequest<ODataEntitySet> req =
        client.getInvokeRequestFactory().getFunctionInvokeRequest(builder.build(), ODataEntitySet.class);
    req.setFormat(format);

    final ODataInvokeResponse<ODataEntitySet> res = req.execute();
    res.getBody();
    fail("Shouldn't get here");
  }

  @Test(expected = IllegalArgumentException.class)
  public void atomInstreamError() {
    instreamError(ODataFormat.ATOM);
  }

  @Test(expected = IllegalArgumentException.class)
  public void jsonInstreamError() {
    instreamError(ODataFormat.JSON);
  }
}
