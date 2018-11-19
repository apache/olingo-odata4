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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.Decoder;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.RepresentationType;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoBatch;
import org.apache.olingo.server.api.uri.UriInfoCrossjoin;
import org.apache.olingo.server.api.uri.UriInfoEntityId;
import org.apache.olingo.server.api.uri.UriInfoMetadata;
import org.apache.olingo.server.api.uri.UriInfoService;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceComplexProperty;
import org.apache.olingo.server.api.uri.UriResourceCount;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.UriResourceRef;
import org.apache.olingo.server.api.uri.UriResourceSingleton;
import org.apache.olingo.server.api.uri.UriResourceValue;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;
import org.apache.olingo.server.core.requests.ActionRequest;
import org.apache.olingo.server.core.requests.BatchRequest;
import org.apache.olingo.server.core.requests.DataRequest;
import org.apache.olingo.server.core.requests.FunctionRequest;
import org.apache.olingo.server.core.requests.MediaRequest;
import org.apache.olingo.server.core.requests.MetadataRequest;
import org.apache.olingo.server.core.requests.OperationRequest;
import org.apache.olingo.server.core.requests.ServiceDocumentRequest;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.validator.UriValidator;

public class ServiceDispatcher extends RequestURLHierarchyVisitor {
  private final OData odata;
  protected ServiceMetadata metadata;
  protected ServiceHandler handler;
  protected CustomContentTypeSupport customContentSupport;
  private String idOption;
  protected ServiceRequest request;

  public ServiceDispatcher(OData odata, ServiceMetadata metadata, ServiceHandler handler,
      CustomContentTypeSupport customContentSupport) {
    this.odata = odata;
    this.metadata = metadata;
    this.handler = handler;
    this.customContentSupport = customContentSupport;
  }

  public void execute(ODataRequest odRequest, ODataResponse odResponse) {
    FormatOption formatOption = null;
    ODataException oDataException = null;
    try {
      String path = odRequest.getRawODataPath();      
      String query = odRequest.getRawQueryPath();      
      if(path.indexOf("$entity") != -1) {
        executeIdOption(query, odRequest, odResponse);
      } else {
        UriInfo uriInfo = new Parser(this.metadata.getEdm(), odata)
          .parseUri(path, query, null, odRequest.getRawBaseUri());
        
        formatOption = uriInfo.getFormatOption();
        
        internalExecute(uriInfo, odRequest, odResponse);
      }
      return;
    } catch(ODataLibraryException e) {
    	oDataException = e;
    } catch(ODataApplicationException e) {
    	oDataException = e;
    }
    ContentType contentType = ContentType.JSON;
    try {
      contentType = ContentNegotiator.doContentNegotiation(formatOption, 
          odRequest, this.customContentSupport, RepresentationType.ERROR);
    } catch (ContentNegotiatorException e) {
      // ignore, default to JSON
    }
    handleException(oDataException, contentType, odRequest, odResponse);
  }
  
  protected void handleException(ODataException e, ContentType contentType,
      ODataRequest odRequest, ODataResponse odResponse) {
    ErrorHandler errorHandler = new ErrorHandler(this.odata, this.metadata,
        this.handler, contentType);
    errorHandler.handleException(e, odRequest, odResponse);    
  }
  
  private void internalExecute(UriInfo uriInfo, ODataRequest odRequest,
      ODataResponse odResponse) throws ODataLibraryException,
      ODataApplicationException {

    new UriValidator().validate(uriInfo, odRequest.getMethod());

    // part1, 8.2.6
    String isolation = odRequest.getHeader(HttpHeader.ODATA_ISOLATION);
    if (isolation != null && "snapshot".equals(isolation) && !this.handler.supportsDataIsolation()) {
      odResponse.setStatusCode(HttpStatusCode.PRECONDITION_FAILED.getStatusCode());
      return;
    }
    
    visit(uriInfo);

    // this should cover for any unsupported calls until they are implemented
    if (this.request == null) {
      this.request = new ServiceRequest(this.odata, this.metadata) {
        @Override
        public ContentType getResponseContentType() throws ContentNegotiatorException {
          return ContentType.APPLICATION_JSON;
        }

        @Override
        public void execute(ServiceHandler handler, ODataResponse response)
            throws ODataLibraryException, ODataApplicationException {
          handler.anyUnsupported(getODataRequest(), response);
        }
      };
    }

    // To handle $entity?$id=http://localhost/EntitySet(key) as
    // http://localhost/EntitySet(key)
    if (this.idOption != null) {
      try {
        this.request.setODataRequest(odRequest);
        this.request = this.request.parseLink(new URI(this.idOption));
      } catch (URISyntaxException e) {
        throw new ODataHandlerException("Invalid $id value",
            ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED, this.idOption);
      }
    }

    this.request.setODataRequest(odRequest);
    this.request.setUriInfo(uriInfo);
    this.request.setCustomContentTypeSupport(this.customContentSupport);
    this.request.execute(this.handler, odResponse);
  }

  @Override
  public void visit(UriInfoMetadata info) {
    this.request = new MetadataRequest(this.odata, this.metadata);
  }

  @Override
  public void visit(UriInfoService info) {
    this.request = new ServiceDocumentRequest(this.odata, this.metadata);
  }

  @Override
  public void visit(UriResourceEntitySet info) {
    DataRequest dataRequest = new DataRequest(this.odata, this.metadata);
    dataRequest.setUriResourceEntitySet(info);
    this.request = dataRequest;
  }

  @Override
  public void visit(UriResourceCount option) {
    if (this.request instanceof DataRequest) {
      DataRequest dataRequest = (DataRequest) this.request;
      dataRequest.setCountRequest(option != null);
    } else if (this.request instanceof OperationRequest) {
      OperationRequest opRequest = (OperationRequest) this.request;
      opRequest.setCountRequest(option != null);      
    }
  }

  @Override
  public void visit(UriResourceComplexProperty info) {
    DataRequest dataRequest = (DataRequest) this.request;
    dataRequest.setUriResourceProperty(info);
  }

  @Override
  public void visit(UriResourcePrimitiveProperty info) {
    DataRequest dataRequest = (DataRequest) this.request;
    dataRequest.setUriResourceProperty(info);
  }

  @Override
  public void visit(UriResourceValue info) {
    DataRequest dataRequest = (DataRequest) this.request;
    if (dataRequest.isPropertyRequest()) {
      dataRequest.setValueRequest(info != null);
    } else {
      MediaRequest mediaRequest = new MediaRequest(this.odata, this.metadata);
      mediaRequest.setUriResourceEntitySet(dataRequest.getUriResourceEntitySet());
      this.request = mediaRequest;
    }
  }

  @Override
  public void visit(UriResourceAction info) {
    ActionRequest actionRequest = new ActionRequest(this.odata, this.metadata);
    actionRequest.setUriResourceAction(info);
    this.request = actionRequest;
  }

  @Override
  public void visit(UriResourceFunction info) {
    FunctionRequest functionRequest = new FunctionRequest(this.odata, this.metadata);
    functionRequest.setUriResourceFunction(info);
    this.request = functionRequest;
  }

  @Override
  public void visit(UriResourceNavigation info) {
    DataRequest dataRequest = (DataRequest) this.request;
    dataRequest.addUriResourceNavigation(info);
  }

  @Override
  public void visit(UriResourceRef info) {
    // this is same as data, but return is just entity references.
    DataRequest dataRequest = (DataRequest) this.request;
    dataRequest.setReferenceRequest(info != null);
  }

  @Override
  public void visit(UriInfoBatch info) {
    this.request = new BatchRequest(this.odata, this.metadata);
  }

  @Override
  public void visit(UriResourceSingleton info) {
    DataRequest dataRequest = new DataRequest(this.odata, this.metadata);
    dataRequest.setUriResourceSingleton(info);
    this.request = dataRequest;
  }

  @Override
  public void visit(UriInfoEntityId info) {
    DataRequest dataRequest = new DataRequest(this.odata, this.metadata);
    this.request = dataRequest;
    super.visit(info);
  }

  @Override
  public void visit(UriInfoCrossjoin info) {
    DataRequest dataRequest = new DataRequest(this.odata, this.metadata);
    dataRequest.setCrossJoin(info);
    this.request = dataRequest;
  }
  
  private void executeIdOption(String query, ODataRequest odRequest,
      ODataResponse odResponse) throws ODataLibraryException,
      ODataApplicationException {
    StringBuilder sb = new StringBuilder();
    StringTokenizer st = new StringTokenizer(query, "&");
    boolean first = true;
    while(st.hasMoreTokens()) {
      String token = st.nextToken();
      if (token.startsWith("$id=")) {
        URI id = URI.create(Decoder.decode(token.substring(4)));
        sb.append(id.getPath());
      } else {
        if (first) {
          sb.append("?");
        } else {
          sb.append("&");
        }
        sb.append(token);
      }
    }    
    DataRequest dataRequest = new DataRequest(this.odata, this.metadata);
    this.request = dataRequest;
    
    this.request.setODataRequest(odRequest);
    this.request = this.request.parseLink(URI.create(sb.toString()));

    this.request.setODataRequest(odRequest);
    this.request.setCustomContentTypeSupport(this.customContentSupport);
    this.request.execute(this.handler, odResponse);    
  }
}
