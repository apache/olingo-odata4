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

import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataHandler;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.OlingoExtension;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.etag.CustomETagSupport;
import org.apache.olingo.server.api.etag.PreconditionException;
import org.apache.olingo.server.api.processor.DefaultProcessor;
import org.apache.olingo.server.api.processor.ErrorProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.RepresentationType;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.apache.olingo.server.core.debug.ServerCoreDebugger;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.queryoption.FormatOptionImpl;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.core.uri.validator.UriValidator;

public class ODataHandlerImpl implements ODataHandler {

  private final OData odata;
  private final ServiceMetadata serviceMetadata;
  private final List<Processor> processors = new LinkedList<Processor>();
  private final ServerCoreDebugger debugger;

  private CustomContentTypeSupport customContentTypeSupport;
  private CustomETagSupport customETagSupport;

  private UriInfo uriInfo;
  private Exception lastThrownException;

  public ODataHandlerImpl(final OData odata, final ServiceMetadata serviceMetadata, final ServerCoreDebugger debugger) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
    this.debugger = debugger;

    register(new DefaultRedirectProcessor());
    register(new DefaultProcessor());
  }

  public ODataResponse process(final ODataRequest request) {
    ODataResponse response = new ODataResponse();
    final int responseHandle = debugger.startRuntimeMeasurement("ODataHandler", "process");
    try {
      processInternal(request, response);
    } catch (final UriValidationException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, e);
    } catch (final UriParserSemanticException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, e);
    } catch (final UriParserSyntaxException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, e);
    } catch (final UriParserException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, e);
    } catch (AcceptHeaderContentNegotiatorException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, e);
    } catch (ContentNegotiatorException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, e);
    } catch (SerializerException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, e);
    } catch (DeserializerException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, e);
    } catch (PreconditionException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, e);
    } catch (ODataHandlerException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, e);
    } catch (ODataApplicationException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
      handleException(request, response, serverError, e);
    } catch (Exception e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
      handleException(request, response, serverError, e);
    }
    debugger.stopRuntimeMeasurement(responseHandle);
    return response;
  }

  private void processInternal(final ODataRequest request, final ODataResponse response)
      throws ODataApplicationException, ODataLibraryException {
    final int measurementHandle = debugger.startRuntimeMeasurement("ODataHandler", "processInternal");

    response.setHeader(HttpHeader.ODATA_VERSION, ODataServiceVersion.V40.toString());
    
    try {
      validateODataVersion(request);
    } catch (final ODataHandlerException e) {
      debugger.stopRuntimeMeasurement(measurementHandle);
      throw e;
    }

    final int measurementUriParser = debugger.startRuntimeMeasurement("Parser", "parseUri");
    try {
      uriInfo = new Parser(serviceMetadata.getEdm(), odata)
          .parseUri(request.getRawODataPath(), request.getRawQueryPath(), null, request.getRawBaseUri());
    } catch (final ODataLibraryException e) {
      debugger.stopRuntimeMeasurement(measurementUriParser);
      debugger.stopRuntimeMeasurement(measurementHandle);
      throw e;
    }
    debugger.stopRuntimeMeasurement(measurementUriParser);

    final int measurementUriValidator = debugger.startRuntimeMeasurement("UriValidator", "validate");
    final HttpMethod method = request.getMethod();
    try {
      new UriValidator().validate(uriInfo, method);
    } catch (final UriValidationException e) {
      debugger.stopRuntimeMeasurement(measurementUriValidator);
      debugger.stopRuntimeMeasurement(measurementHandle);
      throw e;
    }
    debugger.stopRuntimeMeasurement(measurementUriValidator);

    final int measurementDispatcher = debugger.startRuntimeMeasurement("ODataDispatcher", "dispatch");
    try {
      new ODataDispatcher(uriInfo, this).dispatch(request, response);
    } finally {
      debugger.stopRuntimeMeasurement(measurementDispatcher);
      debugger.stopRuntimeMeasurement(measurementHandle);
    }
  }

  public void handleException(final ODataRequest request, final ODataResponse response,
      final ODataServerError serverError, final Exception exception) {
    final int measurementHandle = debugger.startRuntimeMeasurement("ODataHandler", "handleException");
    lastThrownException = exception;
    ErrorProcessor exceptionProcessor;
    try {
      exceptionProcessor = selectProcessor(ErrorProcessor.class);
    } catch (ODataHandlerException e) {
      // This cannot happen since there is always an ExceptionProcessor registered.
      exceptionProcessor = new DefaultProcessor();
    }
    ContentType requestedContentType;
    try {
      final FormatOption formatOption = getFormatOption(request, uriInfo);
      requestedContentType = ContentNegotiator.doContentNegotiation(formatOption, request,
          getCustomContentTypeSupport(), RepresentationType.ERROR);
    } catch (final AcceptHeaderContentNegotiatorException e) {
      requestedContentType = ContentType.JSON;
    } catch (final ContentNegotiatorException e) {
      requestedContentType = ContentType.JSON;
    }
    final int measurementError = debugger.startRuntimeMeasurement("ErrorProcessor", "processError");
    exceptionProcessor.processError(request, response, serverError, requestedContentType);
    debugger.stopRuntimeMeasurement(measurementError);
    debugger.stopRuntimeMeasurement(measurementHandle);
  }

  /**
   * Extract format option from either <code>uriInfo</code> (if not <code>NULL</code>)
   * or query from <code>request</code> (if not <code>NULL</code>).
   * If both options are <code>NULL</code>, <code>NULL</code> is returned.
   *
   * @param request request which is checked
   * @param uriInfo uriInfo which is checked
   * @return the evaluated format option or <code>NULL</code>.
   */
  private FormatOption getFormatOption(final ODataRequest request, final UriInfo uriInfo) {
    if(uriInfo == null) {
      String query = request.getRawQueryPath();
      if(query == null) {
        return null;
      }

      final String formatOption = SystemQueryOptionKind.FORMAT.toString();
      int index = query.indexOf(formatOption);
      int endIndex = query.indexOf('&', index);
      if(endIndex == -1) {
        endIndex = query.length();
      }
      String format = "";
      if (index + formatOption.length() < endIndex) {
         format = query.substring(index + formatOption.length(), endIndex);
      }
      return new FormatOptionImpl().setFormat(format);
    }
    return uriInfo.getFormatOption();
  }

  private void validateODataVersion(final ODataRequest request) throws ODataHandlerException {
    final String odataVersion = request.getHeader(HttpHeader.ODATA_VERSION);
   if (odataVersion != null && !ODataServiceVersion.isValidODataVersion(odataVersion)) {
      throw new ODataHandlerException("ODataVersion not supported: " + odataVersion,
          ODataHandlerException.MessageKeys.ODATA_VERSION_NOT_SUPPORTED, odataVersion);
    }
    
    final String maxVersion = request.getHeader(HttpHeader.ODATA_MAX_VERSION);
    if (maxVersion != null && !ODataServiceVersion.isValidMaxODataVersion(maxVersion)) {
        throw new ODataHandlerException("ODataVersion not supported: " + maxVersion,
            ODataHandlerException.MessageKeys.ODATA_VERSION_NOT_SUPPORTED, maxVersion);
      }
  }

  <T extends Processor> T selectProcessor(final Class<T> cls) throws ODataHandlerException {
    for (final Processor processor : processors) {
      if (cls.isAssignableFrom(processor.getClass())) {
        processor.init(odata, serviceMetadata);
        return cls.cast(processor);
      }
    }
    throw new ODataHandlerException("Processor: " + cls.getSimpleName() + " not registered.",
        ODataHandlerException.MessageKeys.PROCESSOR_NOT_IMPLEMENTED, cls.getSimpleName());
  }

  public void register(final Processor processor) {
    processors.add(0, processor);
  }

  @Override
  public void register(OlingoExtension extension) {
    if(extension instanceof CustomContentTypeSupport) {
      this.customContentTypeSupport = (CustomContentTypeSupport) extension;
    } else if(extension instanceof CustomETagSupport) {
      this.customETagSupport = (CustomETagSupport) extension;
    } else {
      throw new ODataRuntimeException("Got not supported exception with class name " +
          extension.getClass().getSimpleName());
    }
  }

  public CustomContentTypeSupport getCustomContentTypeSupport() {
    return customContentTypeSupport;
  }

  public CustomETagSupport getCustomETagSupport() {
    return customETagSupport;
  }

  public Exception getLastThrownException() {
    return lastThrownException;
  }

  public UriInfo getUriInfo() {
    return uriInfo;
  }
}
