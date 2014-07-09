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
import java.util.List;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.server.api.serializer.ODataError;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

public class ODataErrorSerializer {

  private static final String ERROR = "error";
  private static final String CODE = "code";
  private static final String MESSAGE = "message";
  private static final String TARGET = "target";
  private static final String DETAILS = "details";

  public void writeErrorDocument(JsonGenerator json, ODataError error, List<ODataError> details) throws IOException {
    if (error == null) {
      throw new ODataRuntimeException("ODataError object MUST NOT be null!");
    }
    json.writeStartObject();
    json.writeFieldName(ERROR);

    json.writeStartObject();
    writeODataError(json, error);
    
    if(details != null){
      json.writeArrayFieldStart(DETAILS);
      for(ODataError detailedError : details){
        json.writeStartObject();
        writeODataError(json, detailedError);
        json.writeEndObject();
      }
      json.writeEndArray();
    }

    json.writeEndObject();
    json.writeEndObject();
  }

  private void writeODataError(JsonGenerator json, ODataError error) throws IOException, JsonGenerationException {
    if (error.getCode() == null) {
      json.writeNullField(CODE);
    } else {
      json.writeStringField(CODE, error.getCode());
    }
    if (error.getMessage() == null) {
      json.writeNullField(MESSAGE);
    } else {
      json.writeStringField(MESSAGE, error.getMessage());
    }

    if (error.getTarget() != null) {
      json.writeStringField(TARGET, error.getTarget());
    }
  }
}
