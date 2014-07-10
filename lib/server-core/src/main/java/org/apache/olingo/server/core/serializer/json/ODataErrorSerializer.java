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
package org.apache.olingo.server.core.serializer.json;

import java.io.IOException;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.domain.ODataError;
import org.apache.olingo.commons.api.domain.ODataErrorDetail;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

public class ODataErrorSerializer {

  private static final String ERROR = "error";
  private static final String CODE = "code";
  private static final String MESSAGE = "message";
  private static final String TARGET = "target";
  private static final String DETAILS = "details";

  public void writeErrorDocument(JsonGenerator json, ODataError error) throws IOException {
    if (error == null) {
      throw new ODataRuntimeException("ODataError object MUST NOT be null!");
    }
    json.writeStartObject();
    json.writeFieldName(ERROR);

    json.writeStartObject();
    writeODataError(json, error.getCode(), error.getMessage(), error.getTarget());

    if (error.getDetails() != null) {
      json.writeArrayFieldStart(DETAILS);
      for (ODataErrorDetail detail : error.getDetails()) {
        json.writeStartObject();
        writeODataError(json, detail.getCode(), detail.getMessage(), detail.getTarget());
        json.writeEndObject();
      }
      json.writeEndArray();
    }

    json.writeEndObject();
    json.writeEndObject();
  }

  private void writeODataError(JsonGenerator json, String code, String message, String target) throws IOException,
      JsonGenerationException {
    if (code == null) {
      json.writeNullField(CODE);
    } else {
      json.writeStringField(CODE, code);
    }
    if (message == null) {
      json.writeNullField(MESSAGE);
    } else {
      json.writeStringField(MESSAGE, message);
    }

    if (target != null) {
      json.writeStringField(TARGET, target);
    }
  }
}
