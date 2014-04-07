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
package org.apache.olingo.client.core.it.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Test;

/**
 * This is the unit test class to check basic entity operations.
 */
public class ErrorTestITCase extends AbstractTestITCase {

  private class ErrorGeneratingRequest
          extends AbstractODataBasicRequest<ODataEntityCreateResponse, ODataPubFormat> {

    public ErrorGeneratingRequest(final HttpMethod method, final URI uri) {
      super(client, ODataPubFormat.class, method, uri);
    }

    @Override
    protected InputStream getPayload() {
      return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public ODataEntityCreateResponse execute() {
      final HttpResponse res = doExecute();
      return new ErrorResponseImpl(client, httpClient, res);
    }

    private class ErrorResponseImpl extends AbstractODataResponse implements ODataEntityCreateResponse {

      private final CommonODataClient odataClient;

      public ErrorResponseImpl(
              final CommonODataClient odataClient, final HttpClient client, final HttpResponse res) {

        super(client, res);
        this.odataClient = odataClient;
      }

      @Override
      public CommonODataEntity getBody() {
        return odataClient.getObjectFactory().newEntity("Invalid");
      }
    }
  }

  private void stacktraceError(final ODataPubFormat format) {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL);
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
    stacktraceError(ODataPubFormat.ATOM);
  }

  @Test
  public void jsonStacktraceError() {
    stacktraceError(ODataPubFormat.JSON);
  }

  private void notfoundError(final ODataPubFormat format) {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL);
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
    notfoundError(ODataPubFormat.ATOM);
  }

  @Test
  public void jsonNotfoundError() {
    notfoundError(ODataPubFormat.JSON);
  }

  private void instreamError(final ODataPubFormat format) {
    final Edm metadata =
            client.getRetrieveRequestFactory().getMetadataRequest(testStaticServiceRootURL).execute().getBody();
    assertNotNull(metadata);

    final EdmEntityContainer container = metadata.getSchemas().get(0).getEntityContainer();
    final EdmFunctionImport funcImp = container.getFunctionImport("InStreamErrorGetCustomer");
    final URIBuilder builder = client.getURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment(URIUtils.operationImportURISegment(container, funcImp));
    final ODataInvokeRequest<ODataEntitySet> req =
            client.getInvokeRequestFactory().getInvokeRequest(builder.build(), funcImp.getUnboundFunction(null));
    req.setFormat(format);

    final ODataInvokeResponse<ODataEntitySet> res = req.execute();
    res.getBody();
    fail("Shouldn't get here");
  }

  @Test(expected = IllegalArgumentException.class)
  public void atomInstreamError() {
    instreamError(ODataPubFormat.ATOM);
  }

  @Test(expected = IllegalArgumentException.class)
  public void jsonInstreamError() {
    instreamError(ODataPubFormat.JSON);
  }
}
