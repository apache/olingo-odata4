/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.edm.xml.v3;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.olingo.client.core.op.impl.AbstractEdmDeserializer;

public class FunctionImportDeserializer extends AbstractEdmDeserializer<FunctionImportImpl> {

  @Override
  protected FunctionImportImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final FunctionImportImpl funcImp = new FunctionImportImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Name".equals(jp.getCurrentName())) {
          funcImp.setName(jp.nextTextValue());
        } else if ("ReturnType".equals(jp.getCurrentName())) {
          funcImp.setReturnType(jp.nextTextValue());
        } else if ("EntitySet".equals(jp.getCurrentName())) {
          funcImp.setEntitySet(jp.nextTextValue());
        } else if ("EntitySetPath".equals(jp.getCurrentName())) {
          funcImp.setEntitySetPath(jp.nextTextValue());
        } else if ("IsComposable".equals(jp.getCurrentName())) {
          funcImp.setComposable(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("IsSideEffecting".equals(jp.getCurrentName())) {
          funcImp.setSideEffecting(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("IsBindable".equals(jp.getCurrentName())) {
          funcImp.setBindable(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("IsAlwaysBindable".equals(jp.getCurrentName())) {
          funcImp.setAlwaysBindable(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("HttpMethod".equals(jp.getCurrentName())) {
          funcImp.setHttpMethod(jp.nextTextValue());
        } else if ("Parameter".equals(jp.getCurrentName())) {
          jp.nextToken();
          funcImp.getParameters().add(jp.readValueAs( ParameterImpl.class));
        }
      }
    }

    return funcImp;
  }
}
