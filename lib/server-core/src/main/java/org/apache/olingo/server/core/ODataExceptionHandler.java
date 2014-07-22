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

import java.io.InputStream;
import java.util.Locale;

import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.serializer.ODataSerializer;

public class ODataExceptionHandler {

  public Locale requestedLocale;
  public ODataFormat requestedFormat = ODataFormat.JSON;

  public void handle(ODataResponse resp, Exception e) {
    if (resp.getStatusCode() == 0) {
      resp.setStatusCode(500);
    }
    ODataServerError error = new ODataServerError();
    if (e instanceof ODataTranslatedException) {
      error.setMessage(((ODataTranslatedException) e).getTranslatedMessage(requestedLocale).getMessage());
    } else {
      error.setMessage(e.getMessage());
    }

    ODataSerializer serializer = OData.newInstance().createSerializer(requestedFormat);
    InputStream errorStream = serializer.error(error);
    resp.setContent(errorStream);
    // Set header
  }
}
