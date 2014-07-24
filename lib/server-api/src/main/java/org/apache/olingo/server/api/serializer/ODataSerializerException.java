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
package org.apache.olingo.server.api.serializer;

import org.apache.olingo.server.api.ODataTranslatedException;

public class ODataSerializerException extends ODataTranslatedException {

  private static final long serialVersionUID = 5358683245923127425L;

  // MessageKeys
  public static final String NOT_IMPLEMENTED = "ODataSerializerException.NOT_IMPLEMENTED";
  public static final String JSON_METADATA = "ODataSerializerException.JSON_METADATA";
  public static final String IO_EXCEPTION = "ODataSerializerException.IO_EXCEPTION";
  public static final String NO_CONTEXT_URL = "ODataSerializerException.NO_CONTEXT_URL";
  /** parameter: property name */
  public static final String UNSUPPORTED_PROPERTY_TYPE = "ODataSerializerException.UNSUPPORTED_PROPERTY_TYPE";
  /** parameter: property name */
  public static final String INCONSISTENT_PROPERTY_TYPE = "ODataSerializerException.INCONSISTENT_PROPERTY_TYPE";
  /** parameter: property name */
  public static final String MISSING_PROPERTY = "ODataSerializerException.MISSING_PROPERTY";
  /** parameters: property name, property value */
  public static final String WRONG_PROPERTY_VALUE = "ODataSerializerException.WRONG_PROPERTY_VALUE";

  public ODataSerializerException(final String developmentMessage,
      final String messageKey, final String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  public ODataSerializerException(final String developmentMessage, final Throwable cause,
      final String messageKey, final String... parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }
}
