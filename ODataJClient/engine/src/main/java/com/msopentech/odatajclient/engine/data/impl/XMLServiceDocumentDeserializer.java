/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.data.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.msopentech.odatajclient.engine.data.ServiceDocument;
import com.msopentech.odatajclient.engine.data.ServiceDocumentElement;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import java.io.IOException;
import java.net.URI;

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

    private ServiceDocumentElement deserializeElement(final JsonParser jp, final String elementName)
            throws IOException {

        final ServiceDocumentElement element = new ServiceDocumentElement();
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

        final AbstractServiceDocument sdoc = ODataVersion.V3 == client.getWorkingVersion()
                ? new com.msopentech.odatajclient.engine.data.impl.v3.XMLServiceDocument()
                : new com.msopentech.odatajclient.engine.data.impl.v4.XMLServiceDocument();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT
                || !"service".equals(((FromXmlParser) jp).getStaxReader().getLocalName()); jp.nextToken()) {

            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("base".equals(jp.getCurrentName())) {
                    if (sdoc instanceof com.msopentech.odatajclient.engine.data.impl.v3.XMLServiceDocument) {
                        ((com.msopentech.odatajclient.engine.data.impl.v3.XMLServiceDocument) sdoc).
                                setBaseURI(URI.create(jp.nextTextValue()));
                    } else {
                        ((com.msopentech.odatajclient.engine.data.impl.v4.XMLServiceDocument) sdoc).
                                setBaseURI(URI.create(jp.nextTextValue()));
                    }
                } else if ("context".equals(jp.getCurrentName())) {
                    ((com.msopentech.odatajclient.engine.data.impl.v4.XMLServiceDocument) sdoc).
                            setMetadataContext(jp.nextTextValue());
                } else if ("metadata-etag".equals(jp.getCurrentName())) {
                    ((com.msopentech.odatajclient.engine.data.impl.v4.XMLServiceDocument) sdoc).
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
