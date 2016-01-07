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

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.ex.ODataError;
import org.apache.olingo.commons.api.ex.ODataErrorDetail;
import org.apache.olingo.server.api.serializer.SerializerException;

import com.fasterxml.jackson.core.JsonGenerator;

public class ODataErrorSerializer {

  public void writeErrorDocument(final JsonGenerator json, final ODataError error)
      throws IOException, SerializerException {
    if (error == null) {
      throw new SerializerException("ODataError object MUST NOT be null!",
          SerializerException.MessageKeys.NULL_INPUT);
    }
    json.writeStartObject();
    json.writeFieldName(Constants.JSON_ERROR);

    json.writeStartObject();
    writeODataError(json, error.getCode(), error.getMessage(), error.getTarget());

    if (error.getDetails() != null) {
      json.writeArrayFieldStart(Constants.ERROR_DETAILS);
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

  private void writeODataError(final JsonGenerator json, final String code, final String message, final String target)
      throws IOException {
    json.writeFieldName(Constants.ERROR_CODE);
    if (code == null) {
      json.writeNull();
    } else {
      json.writeString(code);
    }

    json.writeFieldName(Constants.ERROR_MESSAGE);
    if (message == null) {
      json.writeNull();
    } else {
      json.writeString(message);
    }

    if (target != null) {
      json.writeStringField(Constants.ERROR_TARGET, target);
    }
  }
}
