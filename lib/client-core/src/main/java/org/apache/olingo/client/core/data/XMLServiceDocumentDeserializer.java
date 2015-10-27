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
package org.apache.olingo.client.core.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.core.serialization.JsonDeserializer;
import org.apache.olingo.client.core.uri.URIUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

public class XMLServiceDocumentDeserializer extends JsonDeserializer {

  public XMLServiceDocumentDeserializer(final boolean serverMode) {
    super(serverMode);
  }

  private String getName(final JsonParser jp) throws IOException {
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
          element.setUrl(jp.nextTextValue());
        } else if ("name".equals(jp.getCurrentName())) {
          element.setName(jp.nextTextValue());
        } else if ("title".equals(jp.getCurrentName())) {
          element.setTitle(getName(jp));
        }
      }
    }

    if (element.getName() == null) {
      element.setName(element.getTitle());
    }
    return element;
  }

  protected ResWrap<ServiceDocument> doDeserialize(final JsonParser jp) throws IOException {

    ServiceDocumentImpl sdoc = new ServiceDocumentImpl();

    URI contextURL = null;
    String metadataETag = null;
    String base = null;

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT
        || !"service".equals(((FromXmlParser) jp).getStaxReader().getLocalName()); jp.nextToken()) {

      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("base".equals(jp.getCurrentName())) {
          base = jp.nextTextValue();
        } else if ("context".equals(jp.getCurrentName())) {
          contextURL = URI.create(jp.nextTextValue());
        } else if ("metadata-etag".equals(jp.getCurrentName())) {
          metadataETag = jp.nextTextValue();
        } else if ("workspace".equals(jp.getCurrentName())) {
          jp.nextToken();
          jp.nextToken();
          if ("title".equals(jp.getCurrentName())) {
            sdoc.setTitle(getName(jp));
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

    sdoc.setMetadata((contextURL == null
        ? URIUtils.getURI(base, "$metadata")
        : URIUtils.getURI(base, contextURL.toASCIIString())).toASCIIString());

    return new ResWrap<ServiceDocument>(
        contextURL == null ? null : URIUtils.getURI(sdoc.getBaseURI(), contextURL),
        metadataETag, sdoc);
  }

  public ResWrap<ServiceDocument> toServiceDocument(InputStream input) throws ODataDeserializerException {
    try {
      JsonParser parser = new XmlFactory().createParser(input);
      return doDeserialize(parser);
    } catch (final IOException e) {
      throw new ODataDeserializerException(e);
    }
  }
}
