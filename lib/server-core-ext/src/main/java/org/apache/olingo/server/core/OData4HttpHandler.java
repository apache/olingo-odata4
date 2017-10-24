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
package org.apache.olingo.server.core;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.legacy.ProcessorServiceHandler;

public class OData4HttpHandler extends ODataHttpHandlerImpl {
  private ServiceHandler handler;
  private final ServiceMetadata serviceMetadata;
  private final OData odata;
  private int split = 0;
  private CustomContentTypeSupport customContentTypeSupport;


  public OData4HttpHandler(OData odata, ServiceMetadata serviceMetadata) {
    super(odata, serviceMetadata);
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
    // this is support old interfaces
    this.handler = new ProcessorServiceHandler();
    this.handler.init(odata, serviceMetadata);
  }

  @Override
  public void process(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse) {
    ODataRequest request = null;
    ODataResponse response = new ODataResponse();

    try {
      request = createODataRequest(httpRequest, this.split);
      validateODataVersion(request, response);

      ServiceDispatcher dispatcher = new ServiceDispatcher(this.odata, this.serviceMetadata,
          handler, this.customContentTypeSupport);
      dispatcher.execute(request, response);
      
    } catch (Exception e) {
      // also handle any unchecked exception thrown by service handler for proper serialization
      ErrorHandler handler = new ErrorHandler(this.odata, this.serviceMetadata,
          this.handler, ContentType.JSON);
      handler.handleException(e, request, response);
    }    
    convertToHttp(httpResponse, response);
  }


  ODataRequest createODataRequest(final HttpServletRequest httpRequest, final int split)
      throws ODataLibraryException {
    try {
      ODataRequest odRequest = new ODataRequest();

      odRequest.setBody(httpRequest.getInputStream());
      copyHeaders(odRequest, httpRequest);
      odRequest.setMethod(extractMethod(httpRequest));
      fillUriInformation(odRequest, httpRequest, split);

      return odRequest;
    } catch (final IOException e) {
      throw new SerializerException(
          "An I/O exception occurred.", e, SerializerException.MessageKeys.IO_EXCEPTION); //$NON-NLS-1$
    }
  }

  void validateODataVersion(final ODataRequest request, final ODataResponse response)
      throws ODataHandlerException {
    final String maxVersion = request.getHeader(HttpHeader.ODATA_MAX_VERSION);
    response.setHeader(HttpHeader.ODATA_VERSION, ODataServiceVersion.V40.toString());

    if (maxVersion != null 
        && ODataServiceVersion.isBiggerThan(ODataServiceVersion.V40.toString(), maxVersion)) {
      throw new ODataHandlerException("ODataVersion not supported: " + maxVersion, //$NON-NLS-1$
          ODataHandlerException.MessageKeys.ODATA_VERSION_NOT_SUPPORTED, maxVersion);
    }
  }

  @Override
  public void register(final Processor processor) {

    if (processor instanceof ServiceHandler) {
      this.handler = (ServiceHandler) processor;
      this.handler.init(this.odata, this.serviceMetadata);
    }

    if (this.handler instanceof ProcessorServiceHandler) {
      ((ProcessorServiceHandler)this.handler).register(processor);
    }
  }

  @Override
  public void register(final CustomContentTypeSupport customContentTypeSupport) {
    this.customContentTypeSupport = customContentTypeSupport;
  }

  @Override
  public void setSplit(int split) {
    this.split = split;
  }
}
