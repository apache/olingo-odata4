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

import java.util.Locale;

import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataLibraryException.ODataErrorMessage;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.etag.PreconditionException;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

public class ODataExceptionHelper {

  private ODataExceptionHelper() {
    // Private Constructor
  }

  public static ODataServerError createServerErrorObject(final UriValidationException e,
      final Locale requestedLocale) {
    ODataServerError serverError = basicTranslatedError(e, requestedLocale);
    serverError.setStatusCode(HttpStatusCode.BAD_REQUEST.getStatusCode());
    return serverError;
  }

  public static ODataServerError createServerErrorObject(final UriParserSemanticException e,
      final Locale requestedLocale) {
    ODataServerError serverError = basicTranslatedError(e, requestedLocale);
    if (UriParserSemanticException.MessageKeys.RESOURCE_NOT_FOUND.equals(e.getMessageKey())
        || UriParserSemanticException.MessageKeys.PROPERTY_NOT_IN_TYPE.equals(e.getMessageKey())) {
      serverError.setStatusCode(HttpStatusCode.NOT_FOUND.getStatusCode());
    } else if (UriParserSemanticException.MessageKeys.NOT_IMPLEMENTED.equals(e.getMessageKey())) {
      serverError.setStatusCode(HttpStatusCode.NOT_IMPLEMENTED.getStatusCode());
    } else {
      serverError.setStatusCode(HttpStatusCode.BAD_REQUEST.getStatusCode());
    }
    return serverError;
  }

  public static ODataServerError createServerErrorObject(final UriParserSyntaxException e,
      final Locale requestedLocale) {
    ODataServerError serverError = basicTranslatedError(e, requestedLocale);
    serverError.setStatusCode(
        UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION_FORMAT.equals(e.getMessageKey()) ?
            HttpStatusCode.NOT_ACCEPTABLE.getStatusCode() :
              HttpStatusCode.BAD_REQUEST.getStatusCode());
    return serverError;
  }

  public static ODataServerError createServerErrorObject(final UriParserException e, final Locale requestedLocale) {
    ODataServerError serverError = basicTranslatedError(e, requestedLocale);
    serverError.setStatusCode(HttpStatusCode.BAD_REQUEST.getStatusCode());
    return serverError;
  }

  public static ODataServerError createServerErrorObject(final ContentNegotiatorException e,
      final Locale requestedLocale) {
    ODataServerError serverError = basicTranslatedError(e, requestedLocale);
    serverError.setStatusCode(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode());
    return serverError;
  }

  public static ODataServerError createServerErrorObject(final AcceptHeaderContentNegotiatorException e,
      final Locale requestedLocale) {
    ODataServerError serverError = basicTranslatedError(e, requestedLocale);
    serverError.setStatusCode(HttpStatusCode.BAD_REQUEST.getStatusCode());
    return serverError;
  }
  
  public static ODataServerError createServerErrorObject(final ODataHandlerException e, final Locale requestedLocale) {
    ODataServerError serverError = basicTranslatedError(e, requestedLocale);
    if (ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED.equals(e.getMessageKey())
        || ODataHandlerException.MessageKeys.PROCESSOR_NOT_IMPLEMENTED.equals(e.getMessageKey())) {
      serverError.setStatusCode(HttpStatusCode.NOT_IMPLEMENTED.getStatusCode());
    } else if (ODataHandlerException.MessageKeys.ODATA_VERSION_NOT_SUPPORTED.equals(e.getMessageKey())
        || ODataHandlerException.MessageKeys.INVALID_HTTP_METHOD.equals(e.getMessageKey())
        || ODataHandlerException.MessageKeys.AMBIGUOUS_XHTTP_METHOD.equals(e.getMessageKey())
        || ODataHandlerException.MessageKeys.MISSING_CONTENT_TYPE.equals(e.getMessageKey())
        || ODataHandlerException.MessageKeys.INVALID_CONTENT_TYPE.equals(e.getMessageKey())
        || ODataHandlerException.MessageKeys.UNSUPPORTED_CONTENT_TYPE.equals(e.getMessageKey())
        || ODataHandlerException.MessageKeys.INVALID_PREFER_HEADER.equals(e.getMessageKey())) {
      serverError.setStatusCode(HttpStatusCode.BAD_REQUEST.getStatusCode());
    } else if (ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED.equals(e.getMessageKey())) {
      serverError.setStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode());
    }

    return serverError;
  }

  public static ODataServerError createServerErrorObject(final SerializerException e, final Locale requestedLocale) {
    ODataServerError serverError = basicTranslatedError(e, requestedLocale);
    serverError.setStatusCode(HttpStatusCode.BAD_REQUEST.getStatusCode());
    return serverError;
  }

  public static ODataServerError createServerErrorObject(final DeserializerException e, final Locale requestedLocale) {
    return basicTranslatedError(e, requestedLocale)
        .setStatusCode(HttpStatusCode.BAD_REQUEST.getStatusCode());
  }

  public static ODataServerError createServerErrorObject(final PreconditionException e,
      final Locale requestedLocale) {
    ODataServerError serverError = basicTranslatedError(e, requestedLocale);
    if (PreconditionException.MessageKeys.MISSING_HEADER == e.getMessageKey()) {
      serverError.setStatusCode(HttpStatusCode.PRECONDITION_REQUIRED.getStatusCode());
    } else if (PreconditionException.MessageKeys.FAILED == e.getMessageKey()) {
      serverError.setStatusCode(HttpStatusCode.PRECONDITION_FAILED.getStatusCode());
    }
    return serverError;
  }

  public static ODataServerError createServerErrorObject(final ODataLibraryException e, final Locale requestedLocale) {
    ODataServerError serverError = basicTranslatedError(e, requestedLocale);
    if(e instanceof SerializerException || e instanceof DeserializerException){
      serverError.setStatusCode(HttpStatusCode.BAD_REQUEST.getStatusCode());
    }
    return serverError;
  }

  public static ODataServerError createServerErrorObject(final ODataApplicationException e) {
    ODataServerError serverError = basicServerError(e);
    serverError.setStatusCode(e.getStatusCode());
    serverError.setLocale(e.getLocale());
    serverError.setCode(e.getODataErrorCode());
    serverError.setMessage(e.getLocalizedMessage());
    return serverError;
  }

  public static ODataServerError createServerErrorObject(final Exception e) {
    ODataServerError serverError = basicServerError(e);
    serverError.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    serverError.setLocale(Locale.ENGLISH);
    return serverError;
  }

  private static ODataServerError basicServerError(final Exception e) {
    ODataServerError serverError = new ODataServerError().setException(e).setMessage(e.getMessage());
    if (serverError.getMessage() == null) {
      serverError.setMessage("OData Library: An exception without message text was thrown.");
    }
    return serverError;
  }

  private static ODataServerError basicTranslatedError(final ODataLibraryException e,
      final Locale requestedLocale) {
    ODataServerError serverError = basicServerError(e);
    ODataErrorMessage translatedMessage = e.getTranslatedMessage(requestedLocale);
    serverError.setMessage(translatedMessage.getMessage());
    serverError.setLocale(translatedMessage.getLocale());
    serverError.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    return serverError;
  }
}
