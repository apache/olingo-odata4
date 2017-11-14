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
package org.apache.olingo.netty.server.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.netty.server.api.ODataNettyHandler;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataContent;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.core.ODataExceptionHelper;
import org.apache.olingo.server.core.ODataHandlerException;
import org.apache.olingo.server.core.ODataHandlerImpl;
import org.apache.olingo.server.core.debug.ServerCoreDebugger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class ODataNettyHandlerImpl implements ODataNettyHandler {

  public static final int COPY_BUFFER_SIZE = 8192;

  private final ODataHandlerImpl handler;
  private final ServerCoreDebugger debugger;
  
  private static final String CONTEXT_PATH = "contextPath";
  private static final String SPLIT = "split";

  private int split = 0;

  public ODataNettyHandlerImpl(final OData odata, final ServiceMetadata serviceMetadata) {
    debugger = new ServerCoreDebugger(odata);
    handler = new ODataHandlerImpl(odata, serviceMetadata, debugger);
  }
  
  private ODataResponse handleException(final ODataRequest odRequest, final Exception e) {
    ODataResponse resp = new ODataResponse();
    ODataServerError serverError;
    if (e instanceof ODataHandlerException) {
      serverError = ODataExceptionHelper.createServerErrorObject((ODataHandlerException) e, null);
    } else if (e instanceof ODataLibraryException) {
      serverError = ODataExceptionHelper.createServerErrorObject((ODataLibraryException) e, null);
    } else {
      serverError = ODataExceptionHelper.createServerErrorObject(e);
    }
    handler.handleException(odRequest, resp, serverError, e);
    return resp;
  }
  
  /**
   * Convert the OData Response to Netty Response
   * @param response
   * @param odResponse
   */
  static void convertToHttp(final HttpResponse response, final ODataResponse odResponse) {
	    response.setStatus(HttpResponseStatus.valueOf(odResponse.getStatusCode()));

	    for (Entry<String, List<String>> entry : odResponse.getAllHeaders().entrySet()) {
	      for (String headerValue : entry.getValue()) {
	        ((HttpMessage)response).headers().add(entry.getKey(), headerValue);
	      }
	    }

	    if (odResponse.getContent() != null) {
	      copyContent(odResponse.getContent(), response);
	    } else if (odResponse.getODataContent() != null) {
	      writeContent(odResponse, response);
	    }
	  }
  
  /**
   * Write the odata content to netty response content
   * @param odataResponse
   * @param response
   */
  static void writeContent(final ODataResponse odataResponse, final HttpResponse response) {
    ODataContent res = odataResponse.getODataContent();
    res.write(Channels.newChannel(new ByteBufOutputStream(((HttpContent)response).content())));
  }
  
  static void copyContent(final InputStream inputStream, final HttpResponse response) {
	    copyContent(Channels.newChannel(inputStream), response);
	  }

  /** 
   * Copy OData content to netty content
   * @param input
   * @param response
   */
  static void copyContent(final ReadableByteChannel input, final HttpResponse response) {
    WritableByteChannel output = null;
    try {
      ByteBuffer inBuffer = ByteBuffer.allocate(COPY_BUFFER_SIZE);
      output = Channels.newChannel(new ByteBufOutputStream(((HttpContent)response).content()));
      while (input.read(inBuffer) > 0) {
        inBuffer.flip();
        output.write(inBuffer);
        inBuffer.clear();
      }
    } catch (IOException e) {
      throw new ODataRuntimeException("Error on reading request content", e);
    } finally {
      closeStream(input);
      closeStream(output);
    }
  }

  private static void closeStream(final Channel closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        // ignore
      }
    }
  }
  
  /**
   * Extract the information part of Netty Request and fill OData Request
   * @param odRequest
   * @param httpRequest
   * @param split
   * @param contextPath
   * @return
   * @throws ODataLibraryException
   */
  private ODataRequest fillODataRequest(final ODataRequest odRequest, final HttpRequest httpRequest,
	      final int split, final String contextPath) throws ODataLibraryException {
	    final int requestHandle = debugger.startRuntimeMeasurement("ODataHttpHandlerImpl", "fillODataRequest");
	    try {
	    	ByteBuf byteBuf = ((HttpContent)httpRequest).content();
	    	ByteBufInputStream inputStream = new ByteBufInputStream(byteBuf);
	      odRequest.setBody(inputStream);
	      
	      odRequest.setProtocol(httpRequest.protocolVersion().text());
	      odRequest.setMethod(extractMethod(httpRequest));
	      int innerHandle = debugger.startRuntimeMeasurement("ODataNettyHandlerImpl", "copyHeaders");
	      copyHeaders(odRequest, httpRequest);
	      debugger.stopRuntimeMeasurement(innerHandle);
	      innerHandle = debugger.startRuntimeMeasurement("ODataNettyHandlerImpl", "fillUriInformation");
	      fillUriInformationFromHttpRequest(odRequest, httpRequest, split, contextPath);
	      debugger.stopRuntimeMeasurement(innerHandle);

	      return odRequest;
	    } finally {
	      debugger.stopRuntimeMeasurement(requestHandle);
	    }
	  }
  
  static HttpMethod extractMethod(final HttpRequest httpRequest) throws ODataLibraryException {
    final HttpMethod httpRequestMethod;
    	try {
    	      httpRequestMethod = HttpMethod.valueOf(httpRequest.method().name());
    	    } catch (IllegalArgumentException e) {
    	      throw new ODataHandlerException("HTTP method not allowed" + 
    	    httpRequest.method().name(), e,
    	          ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, 
    	          httpRequest.method().name());
    	    }
    	try {
  	      if (httpRequestMethod == HttpMethod.POST) {
  	        String xHttpMethod = httpRequest.headers().
  	        		get(HttpHeader.X_HTTP_METHOD);
  	        String xHttpMethodOverride = httpRequest.headers().
  	        		get(HttpHeader.X_HTTP_METHOD_OVERRIDE);

  	        if (xHttpMethod == null && xHttpMethodOverride == null) {
  	          return httpRequestMethod;
  	        } else if (xHttpMethod == null) {
  	          return HttpMethod.valueOf(xHttpMethodOverride);
  	        } else if (xHttpMethodOverride == null) {
  	          return HttpMethod.valueOf(xHttpMethod);
  	        } else {
  	          if (!xHttpMethod.equalsIgnoreCase(xHttpMethodOverride)) {
  	            throw new ODataHandlerException("Ambiguous X-HTTP-Methods",
  	                ODataHandlerException.MessageKeys.AMBIGUOUS_XHTTP_METHOD, xHttpMethod, xHttpMethodOverride);
  	          }
  	          return HttpMethod.valueOf(xHttpMethod);
  	        }
  	      } else {
  	        return httpRequestMethod;
  	      }
  	    } catch (IllegalArgumentException e) {
  	      throw new ODataHandlerException("Invalid HTTP method" + 
  	    httpRequest.method().name(), e,
  	          ODataHandlerException.MessageKeys.INVALID_HTTP_METHOD, 
  	          httpRequest.method().name());
  	    }
  }

  /**
   * Fetch the uri information parsing netty request url
   * @param odRequest
   * @param httpRequest
   * @param split
   * @param contextPath
   */
  static void fillUriInformationFromHttpRequest(final ODataRequest odRequest, final HttpRequest httpRequest, 
		  final int split, final String contextPath) {
	    String rawRequestUri = httpRequest.uri();
	    if (rawRequestUri.indexOf("?") != -1) {
	    	rawRequestUri = rawRequestUri.substring(0, rawRequestUri.indexOf("?"));
	    }

	    String rawODataPath;
	    if (!"".equals(contextPath)) {
	      int beginIndex = rawRequestUri.indexOf(contextPath) + contextPath.length();
	      rawODataPath = rawRequestUri.substring(beginIndex);
	    } else {
	      rawODataPath = rawRequestUri;
	    }

	    String rawServiceResolutionUri = null;
	    if (split > 0) {
	      rawServiceResolutionUri = rawODataPath;
	      for (int i = 0; i < split; i++) {
	        int index = rawODataPath.indexOf('/', 1);
	        if (-1 == index) {
	          rawODataPath = "";
	          break;
	        } else {
	          rawODataPath = rawODataPath.substring(index);
	        }
	      }
	      int end = rawServiceResolutionUri.length() - rawODataPath.length();
	      rawServiceResolutionUri = rawServiceResolutionUri.substring(0, end);
	    }

	    String rawBaseUri = rawRequestUri.substring(0, rawRequestUri.length() - rawODataPath.length());

	    int index = httpRequest.uri().indexOf('?');
	    String queryString = null;
        if (index != -1) {
            queryString = httpRequest.uri().substring(index + 1);
        }
	    odRequest.setRawQueryPath(queryString);
	    odRequest.setRawRequestUri(rawRequestUri
	            + (queryString == null ? "" : "?" + queryString));
	    odRequest.setRawODataPath(rawODataPath);
	    odRequest.setRawBaseUri(rawBaseUri);
	    odRequest.setRawServiceResolutionUri(rawServiceResolutionUri);
	  }

  /**
   * Copy the headers part of Netty Request to OData Request
   * @param odRequest
   * @param req
   */
  static void copyHeaders(ODataRequest odRequest, final HttpRequest req) {
	  final Set<String> headers = req.headers().names();
	  Iterator<String> headerNames = headers.iterator();
	  while (headerNames.hasNext()) {
		  final String headerName = headerNames.next();
		  final List<String> headerValues = req.headers().getAll(headerName);
	      odRequest.addHeader(headerName, headerValues);
	  }
  }
  
@SuppressWarnings("unused")
@Override
public void processNettyRequest(HttpRequest request, HttpResponse response, 
		Map<String, String> requestParameters) {
	  ODataRequest odRequest = new ODataRequest();
    Exception exception = null;
    ODataResponse odResponse;
    
    final int processMethodHandle = 
    		debugger.startRuntimeMeasurement("ODataNettyHandlerImpl", "process");
    try {
      fillODataRequest(odRequest, request, 
          requestParameters.get(SPLIT) != null? Integer.parseInt(requestParameters.get(SPLIT)) : split, 
              requestParameters.get(CONTEXT_PATH));

      odResponse = process(odRequest);
      // ALL future methods after process must not throw exceptions!
    } catch (Exception e) {
      exception = e;
      odResponse = handleException(odRequest, e);
    }
    debugger.stopRuntimeMeasurement(processMethodHandle);

    convertToHttp(response, odResponse);
  }

  public ODataResponse process(ODataRequest request) {
    return handler.process(request);
  }

  @Override
  public void register(Processor processor) {
    handler.register(processor);
  }
}
