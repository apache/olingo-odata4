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

import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataLibraryException.MessageKey;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.etag.PreconditionException;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.junit.Test;

public class ExceptionHelperTest {

  private static final String DEV_MSG = "devMsg";

  @Test
  public void withRuntimeException() {
    try {
      throw new NullPointerException();
    } catch (NullPointerException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
      assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), serverError.getStatusCode());
      assertEquals("OData Library: An exception without message text was thrown.", serverError.getMessage());
      assertEquals(e, serverError.getException());
    }
  }

  @Test
  public void withRuntimeExceptionAndText() {
    try {
      throw new NullPointerException("Text");
    } catch (NullPointerException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
      assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), serverError.getStatusCode());
      assertEquals("Text", serverError.getMessage());
      assertEquals(e, serverError.getException());
    }
  }

  @Test
  public void uriValidatorExceptionMustLeadTo400() {
    for (MessageKey key : UriValidationException.MessageKeys.values()) {
      try {
        throw new UriValidationException(DEV_MSG, key);
      } catch (UriValidationException e) {
        ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
        assertEquals("FailedKey: " + e.getMessageKey().getKey(), HttpStatusCode.BAD_REQUEST.getStatusCode(),
            serverError.getStatusCode());
      }
    }
  }

  @Test
  public void deserializerExceptionMustLeadTo400() {
    for (MessageKey key : DeserializerException.MessageKeys.values()) {
      try {
        throw new DeserializerException(DEV_MSG, key);
      } catch (DeserializerException e) {
        ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
        assertEquals("FailedKey: " + e.getMessageKey().getKey(), HttpStatusCode.BAD_REQUEST.getStatusCode(),
            serverError.getStatusCode());
      }
    }
  }

  @Test
  public void serializerExceptionMustLeadTo400() {
    for (MessageKey key : SerializerException.MessageKeys.values()) {
      try {
        throw new SerializerException(DEV_MSG, key);
      } catch (SerializerException e) {
        ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
        assertEquals("FailedKey: " + e.getMessageKey().getKey(), HttpStatusCode.BAD_REQUEST.getStatusCode(),
            serverError.getStatusCode());
      }
    }
  }

  @Test
  public void contentNegotiatorExceptionMustLeadTo406() {
    for (MessageKey key : ContentNegotiatorException.MessageKeys.values()) {
      try {
        throw new ContentNegotiatorException(DEV_MSG, key);
      } catch (ContentNegotiatorException e) {
        ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
        assertEquals("FailedKey: " + e.getMessageKey().getKey(), HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(),
            serverError.getStatusCode());
      }
    }
  }

  @Test
  public void preconditionRequiredTesting() {
    for (MessageKey key : PreconditionException.MessageKeys.values()) {
      try {
        throw new PreconditionException(DEV_MSG, key);
      } catch (PreconditionException e) {
        if (e.getMessageKey().equals(PreconditionException.MessageKeys.FAILED)) {
          ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
          assertEquals("FailedKey: " + e.getMessageKey().getKey(), HttpStatusCode.PRECONDITION_FAILED.getStatusCode(),
              serverError.getStatusCode());
        } else if (e.getMessageKey().equals(PreconditionException.MessageKeys.MISSING_HEADER)) {
          ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
          assertEquals("FailedKey: " + e.getMessageKey().getKey(), HttpStatusCode.PRECONDITION_REQUIRED.getStatusCode(),
              serverError.getStatusCode());
        } else if (e.getMessageKey().equals(PreconditionException.MessageKeys.INVALID_URI)) {
          ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
          assertEquals("FailedKey: " + e.getMessageKey().getKey(), HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
              serverError.getStatusCode());
        } else {
          fail("Unexpected message key for: " + e.getClass().getName());
        }
      }
    }
  }
}
