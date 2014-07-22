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

import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.ODataTranslatedException.ODataErrorMessage;

public class ODataExceptionHelper {

  public static ODataServerError createServerErrorObject(Exception e, int statusCode) {
    ODataServerError serverError = basicServerError(e);
    serverError.setStatusCode(statusCode);
    serverError.setLocale(Locale.ENGLISH);
    return serverError;
  }

  public static ODataServerError createServerErrorObject(ODataTranslatedException e, int statusCode,
      Locale requestedLocale) {
    ODataServerError serverError = basicServerError(e);
    ODataErrorMessage translatedMessage = e.getTranslatedMessage(requestedLocale);
    serverError.setMessage(translatedMessage.getMessage());
    serverError.setLocale(translatedMessage.getLocale());
    serverError.setStatusCode(statusCode);
    return serverError;
  }

  public static ODataServerError createServerErrorObject(ODataApplicationException e) {
    ODataServerError serverError = basicServerError(e);
    serverError.setStatusCode(e.getStatusCode());
    serverError.setLocale(e.getLocale());
    serverError.setCode(e.getODataErrorCode());
    return serverError;
  }
  
  private static ODataServerError basicServerError(Exception e) {
    ODataServerError serverError = new ODataServerError().setException(e).setMessage(e.getMessage());
    return serverError;
  }
}
