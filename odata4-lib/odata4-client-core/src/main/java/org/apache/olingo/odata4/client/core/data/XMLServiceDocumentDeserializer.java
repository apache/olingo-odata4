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
package org.apache.olingo.odata4.client.core.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import java.io.IOException;
import java.net.URI;
import org.apache.olingo.odata4.client.api.data.ServiceDocument;
import org.apache.olingo.odata4.commons.api.edm.constants.ODataServiceVersion;

public class XMLServiceDocumentDeserializer extends ODataJacksonDeserializer<ServiceDocument> {

  private String getTitle(final JsonParser jp) throws IOException {
    String title = jp.nextTextValue();
    if (title == null) {
      jp.nextToken();
      jp.nextToken();
      jp.nextToken();
      title = jp.nextTextValue();
    }
    return title;
  }

  private ServiceDocumentItemImpl deserializeElement(final JsonParser jp, final String elementName)
          throws IOException {

    final ServiceDocumentItemImpl element = new ServiceDocumentItemImpl();
    for (; jp.getCurrentToken() != JsonToken.END_OBJECT
            || !elementName.equals(((FromXmlParser) jp).getStaxReader().getLocalName()); jp.nextToken()) {

      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("href".equals(jp.getCurrentName())) {
          element.setHref(jp.nextTextValue());
        } else if ("name".equals(jp.getCurrentName())) {
          element.setName(jp.nextTextValue());
        } else if ("title".equals(jp.getCurrentName())) {
          element.setTitle(getTitle(jp));
        }
      }
    }

    return element;
  }

  @Override
  protected ServiceDocument doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final AbstractServiceDocument sdoc = ODataServiceVersion.V30 == client.getServiceVersion()
            ? new org.apache.olingo.odata4.client.core.data.v3.XMLServiceDocumentImpl()
            : new org.apache.olingo.odata4.client.core.data.v4.XMLServiceDocumentImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT
            || !"service".equals(((FromXmlParser) jp).getStaxReader().getLocalName()); jp.nextToken()) {

      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("base".equals(jp.getCurrentName())) {
          if (sdoc instanceof org.apache.olingo.odata4.client.core.data.v3.XMLServiceDocumentImpl) {
            ((org.apache.olingo.odata4.client.core.data.v3.XMLServiceDocumentImpl) sdoc).
                    setBaseURI(URI.create(jp.nextTextValue()));
          } else {
            ((org.apache.olingo.odata4.client.core.data.v4.XMLServiceDocumentImpl) sdoc).
                    setBaseURI(URI.create(jp.nextTextValue()));
          }
        } else if ("context".equals(jp.getCurrentName())) {
          ((org.apache.olingo.odata4.client.core.data.v4.XMLServiceDocumentImpl) sdoc).
                  setMetadataContext(jp.nextTextValue());
        } else if ("metadata-etag".equals(jp.getCurrentName())) {
          ((org.apache.olingo.odata4.client.core.data.v4.XMLServiceDocumentImpl) sdoc).
                  setMetadataETag(jp.nextTextValue());
        } else if ("workspace".equals(jp.getCurrentName())) {
          jp.nextToken();
          jp.nextToken();
          if ("title".equals(jp.getCurrentName())) {
            sdoc.setTitle(getTitle(jp));
          }
        } else if ("collection".equals(jp.getCurrentName())) {
          jp.nextToken();
          sdoc.getEntitySets().add(deserializeElement(jp, "collection"));
        } else if ("function-import".equals(jp.getCurrentName())) {
          jp.nextToken();
          sdoc.getFunctionImports().add(deserializeElement(jp, "function-import"));
        } else if ("singleton".equals(jp.getCurrentName())) {
          jp.nextToken();
          sdoc.getSingletons().add(deserializeElement(jp, "singleton"));
        } else if ("service-document".equals(jp.getCurrentName())) {
          jp.nextToken();
          sdoc.getRelatedServiceDocuments().add(deserializeElement(jp, "service-document"));
        }
      }
    }

    return sdoc;
  }

}
