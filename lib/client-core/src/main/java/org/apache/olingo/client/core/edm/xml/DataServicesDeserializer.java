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
package org.apache.olingo.client.core.edm.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

import java.io.IOException;

public class DataServicesDeserializer extends AbstractEdmDeserializer<AbstractDataServices> {

  @Override
  protected AbstractDataServices doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final AbstractDataServices dataServices = ODataServiceVersion.V30 == version
            ? new org.apache.olingo.client.core.edm.xml.v3.DataServicesImpl()
            : new org.apache.olingo.client.core.edm.xml.v4.DataServicesImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("DataServiceVersion".equals(jp.getCurrentName())) {
          dataServices.setDataServiceVersion(jp.nextTextValue());
        } else if ("MaxDataServiceVersion".equals(jp.getCurrentName())) {
          dataServices.setMaxDataServiceVersion(jp.nextTextValue());
        } else if ("Schema".equals(jp.getCurrentName())) {
          jp.nextToken();
          if (dataServices instanceof org.apache.olingo.client.core.edm.xml.v3.DataServicesImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.DataServicesImpl) dataServices).
                    getSchemas().add(jp.readValueAs(
                                    org.apache.olingo.client.core.edm.xml.v3.SchemaImpl.class));

          } else {
            ((org.apache.olingo.client.core.edm.xml.v4.DataServicesImpl) dataServices).
                    getSchemas().add(jp.readValueAs(
                                    org.apache.olingo.client.core.edm.xml.v4.SchemaImpl.class));
          }
        }
      }
    }

    return dataServices;
  }
}
