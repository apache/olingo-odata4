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
package org.apache.olingo.server.api;

import java.util.Locale;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.http.HttpStatusCode;

/**
 * Exception thrown by OData service implementations.
 * @see ODataException
 */
public class ODataApplicationException extends ODataException {

  private static final long serialVersionUID = 5358683245923127425L;
  private int statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode();
  private Locale locale;
  private String oDataErrorCode;

  /**
   * Exception in an OData service implementation.
   * @param msg        the text of the exception
   * @param statusCode the HTTP status code of the error response; the default is 500 - Internal Server Error
   * @param locale     a {@link Locale} to enable translation of error messages
   * @see ODataException
   * @see HttpStatusCode
   */
  public ODataApplicationException(final String msg, final int statusCode, final Locale locale) {
    super(msg);
    this.statusCode = statusCode;
    this.locale = locale;
  }

  /**
   * Exception in an OData service implementation.
   * @param msg            the text of the exception
   * @param statusCode     the HTTP status code of the error response; the default is 500 - Internal Server Error
   * @param locale         a {@link Locale} to enable translation of error messages
   * @param oDataErrorCode the error code of the exception as defined by the OData standard
   * @see ODataException
   * @see HttpStatusCode
   */
  public ODataApplicationException(final String msg, final int statusCode, final Locale locale,
      final String oDataErrorCode) {
    this(msg, statusCode, locale);
    this.oDataErrorCode = oDataErrorCode;
  }

  /**
   * Exception in an OData service implementation.
   * @param msg        the text of the exception
   * @param statusCode the HTTP status code of the error response; the default is 500 - Internal Server Error
   * @param locale     a {@link Locale} to enable translation of error messages
   * @param cause      the cause of this exception 
   * @see ODataException
   * @see HttpStatusCode
   * @see Throwable#getCause()
   */
  public ODataApplicationException(final String msg, final int statusCode, final Locale locale,
      final Throwable cause) {
    super(msg, cause);
    this.statusCode = statusCode;
    this.locale = locale;
  }

  /**
   * Exception in an OData service implementation.
   * @param msg            the text of the exception
   * @param statusCode     the HTTP status code of the error response; the default is 500 - Internal Server Error
   * @param locale         a {@link Locale} to enable translation of error messages
   * @param cause          the cause of this exception 
   * @param oDataErrorCode the error code of the exception as defined by the OData standard
   * @see ODataException
   * @see HttpStatusCode
   * @see Throwable#getCause()
   */
  public ODataApplicationException(final String msg, final int statusCode, final Locale locale, final Throwable cause,
      final String oDataErrorCode) {
    this(msg, statusCode, locale, cause);
    this.oDataErrorCode = oDataErrorCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public Locale getLocale() {
    return locale;
  }

  public String getODataErrorCode() {
    return oDataErrorCode;
  }
}
