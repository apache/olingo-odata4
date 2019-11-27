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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataLibraryException.MessageKey;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.etag.PreconditionException;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.junit.Test;

public class ExceptionHelperTest {

  private static final String DEV_MSG = "devMsg";

  @Test
  public void withRuntimeException() {
    final Exception e = new NullPointerException();
    ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
    assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), serverError.getStatusCode());
    assertEquals("OData Library: An exception without message text was thrown.", serverError.getMessage());
    assertEquals(e, serverError.getException());
  }

  @Test
  public void withRuntimeExceptionAndText() {
    final Exception e = new NullPointerException("Text");
    ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
    assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), serverError.getStatusCode());
    assertEquals("Text", serverError.getMessage());
    assertEquals(e, serverError.getException());
  }

  @Test
  public void uriValidatorExceptionMustLeadToBadRequest() {
    for (MessageKey key : UriValidationException.MessageKeys.values()) {
      final UriValidationException e = new UriValidationException(DEV_MSG, key);
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      checkStatusCode(serverError, HttpStatusCode.BAD_REQUEST, e);
    }
  }

  @Test
  public void deserializerExceptionMustLeadToBadRequest() {
    for (MessageKey key : DeserializerException.MessageKeys.values()) {
      final DeserializerException e = new DeserializerException(DEV_MSG, key);
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      checkStatusCode(serverError, HttpStatusCode.BAD_REQUEST, e);
    }
  }

  @Test
  public void serializerExceptionMustLeadToBadRequest() {
    for (MessageKey key : SerializerException.MessageKeys.values()) {
      final SerializerException e = new SerializerException(DEV_MSG, key);
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      checkStatusCode(serverError, HttpStatusCode.BAD_REQUEST, e);
    }
  }
  
  @Test
  public void libraryExceptionLeadToBadRequest() {
      ODataLibraryException e = new SerializerException(DEV_MSG, SerializerException.MessageKeys.MISSING_PROPERTY);
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      checkStatusCode(serverError, HttpStatusCode.BAD_REQUEST, e);
      e = new SerializerException(DEV_MSG, DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
      serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      checkStatusCode(serverError, HttpStatusCode.BAD_REQUEST, e);
  }

  @Test
  public void contentNegotiatorExceptionMustLeadToNotAcceptable() {
    for (MessageKey key : ContentNegotiatorException.MessageKeys.values()) {
      final ContentNegotiatorException e = new ContentNegotiatorException(DEV_MSG, key);
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      checkStatusCode(serverError, HttpStatusCode.NOT_ACCEPTABLE, e);
    }
  }

  @Test
  public void preconditionRequiredTesting() {
    for (MessageKey key : PreconditionException.MessageKeys.values()) {
      final PreconditionException e = new PreconditionException(DEV_MSG, key);
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      if (e.getMessageKey().equals(PreconditionException.MessageKeys.FAILED)) {
        checkStatusCode(serverError, HttpStatusCode.PRECONDITION_FAILED, e);
      } else if (e.getMessageKey().equals(PreconditionException.MessageKeys.MISSING_HEADER)) {
        checkStatusCode(serverError, HttpStatusCode.PRECONDITION_REQUIRED, e);
      } else if (e.getMessageKey().equals(PreconditionException.MessageKeys.INVALID_URI)) {
        checkStatusCode(serverError, HttpStatusCode.INTERNAL_SERVER_ERROR, e);
      } else {
        fail("Unexpected message key for: " + e.getClass().getName());
      }
    }
  }

  @Test
  public void httpHandlerExceptions() {
    for (MessageKey key : ODataHandlerException.MessageKeys.values()) {
      final ODataHandlerException e = new ODataHandlerException(DEV_MSG, key);
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);

      if (key.equals(ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED)
          || key.equals(ODataHandlerException.MessageKeys.PROCESSOR_NOT_IMPLEMENTED)) {
        checkStatusCode(serverError, HttpStatusCode.NOT_IMPLEMENTED, e);
      } else if (key.equals(ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED)) {
        checkStatusCode(serverError, HttpStatusCode.METHOD_NOT_ALLOWED, e);
      } else {
        checkStatusCode(serverError, HttpStatusCode.BAD_REQUEST, e);
      }
    }
  }

  @Test
  public void withNotImplementedException() {
    final UriParserSemanticException  e = new UriParserSemanticException("Exception", 
        UriParserSemanticException.MessageKeys.NOT_IMPLEMENTED, "Method");
    ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, Locale.ENGLISH);
    assertEquals(HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), serverError.getStatusCode());
    assertEquals("'Method' is not implemented!", serverError.getMessage());
    assertEquals(e, serverError.getException());
  }
  
  @Test
  public void uriParserException() {
    final UriParserException  e = new UriParserSemanticException("Exception", 
        UriParserSemanticException.MessageKeys.NOT_IMPLEMENTED, "Method");
    ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, Locale.ENGLISH);
    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), serverError.getStatusCode());
    assertEquals("'Method' is not implemented!", serverError.getMessage());
    assertEquals(e, serverError.getException());
  }
  
  @Test
  public void acceptHeaderException() {
    final AcceptHeaderContentNegotiatorException   e = new AcceptHeaderContentNegotiatorException ("Exception", 
        UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE, "Method");
    ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, Locale.ENGLISH);
    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), serverError.getStatusCode());
    assertEquals("Missing message for key 'INVALID_KEY_VALUE'!", serverError.getMessage());
    assertEquals(e, serverError.getException());
  }
  
  private void checkStatusCode(final ODataServerError serverError, final HttpStatusCode statusCode,
      final ODataLibraryException exception) {
    assertEquals("FailedKey: " + exception.getMessageKey().getKey(),
        statusCode.getStatusCode(), serverError.getStatusCode());
  }
}
