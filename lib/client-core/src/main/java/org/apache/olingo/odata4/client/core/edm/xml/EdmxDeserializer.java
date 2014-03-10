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
package org.apache.olingo.odata4.client.core.edm.xml;

import org.apache.olingo.odata4.client.core.op.impl.AbstractEdmDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import org.apache.olingo.odata4.client.core.edm.xml.v4.ReferenceImpl;
import org.apache.olingo.odata4.commons.api.edm.constants.ODataServiceVersion;

@SuppressWarnings("rawtypes")
public class EdmxDeserializer extends AbstractEdmDeserializer<AbstractEdmx> {

  @Override
  protected AbstractEdmx doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final AbstractEdmx edmx = ODataServiceVersion.V30 == client.getServiceVersion()
            ? new org.apache.olingo.odata4.client.core.edm.xml.v3.EdmxImpl()
            : new org.apache.olingo.odata4.client.core.edm.xml.v4.EdmxImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Version".equals(jp.getCurrentName())) {
          edmx.setVersion(jp.nextTextValue());
        } else if ("DataServices".equals(jp.getCurrentName())) {
          jp.nextToken();
          if (edmx instanceof org.apache.olingo.odata4.client.core.edm.xml.v3.EdmxImpl) {
            ((org.apache.olingo.odata4.client.core.edm.xml.v3.EdmxImpl) edmx).
                    setDataServices(jp.readValueAs(
                                    org.apache.olingo.odata4.client.core.edm.xml.v3.DataServicesImpl.class));
          } else {
            ((org.apache.olingo.odata4.client.core.edm.xml.v4.EdmxImpl) edmx).
                    setDataServices(jp.readValueAs(
                                    org.apache.olingo.odata4.client.core.edm.xml.v4.DataServicesImpl.class));
          }
        } else if ("Reference".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.odata4.client.core.edm.xml.v4.EdmxImpl) edmx).getReferences().
                  add(jp.readValueAs( ReferenceImpl.class));
        }
      }
    }

    return edmx;
  }
}
