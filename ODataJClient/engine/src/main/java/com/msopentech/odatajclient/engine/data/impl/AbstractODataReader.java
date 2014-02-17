/**
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
package com.msopentech.odatajclient.engine.data.impl;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataEntitySetIterator;
import com.msopentech.odatajclient.engine.data.ODataError;
import com.msopentech.odatajclient.engine.data.ODataLinkCollection;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataReader;
import com.msopentech.odatajclient.engine.data.ODataServiceDocument;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.data.ResourceFactory;
import com.msopentech.odatajclient.engine.metadata.AbstractEdmMetadata;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.format.ODataValueFormat;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractODataReader implements ODataReader {

    private static final long serialVersionUID = -1988865870981207079L;

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractODataReader.class);

    protected final ODataClient client;

    protected AbstractODataReader(final ODataClient client) {
        this.client = client;
    }

    @Override
    public ODataEntitySet readEntitySet(final InputStream input, final ODataPubFormat format) {
        return client.getBinder().getODataEntitySet(
                client.getDeserializer().toFeed(input, ResourceFactory.feedClassForFormat(format)));
    }

    @Override
    public ODataEntity readEntity(final InputStream input, final ODataPubFormat format) {
        return client.getBinder().getODataEntity(
                client.getDeserializer().toEntry(input, ResourceFactory.entryClassForFormat(format)));
    }

    @Override
    public ODataProperty readProperty(final InputStream input, final ODataFormat format) {
        final Element property = client.getDeserializer().toPropertyDOM(input, format);

        // The ODataProperty object is used either for actual entity properties and for invoke result (when return type
        // is neither an entity nor a collection of entities).
        // Such formats are mostly the same except for collections: an entity property looks like
        //     <aproperty m:type="Collection(AType)">
        //       <element>....</element>
        //     </aproperty>
        //
        // while an invoke result with returnType="Collection(AnotherType)" looks like
        //     <functionImportName>
        //       <element m:type="AnotherType">...</element>
        //     <functionImportName>
        //
        // The code below is meant for "normalizing" the latter into
        //     <functionImportName m:type="Collection(AnotherType)">
        //       <element m:type="AnotherType">...</element>
        //     <functionImportName>
        final String type = property.getAttribute(ODataConstants.ATTR_M_TYPE);
        final NodeList elements = property.getElementsByTagName(ODataConstants.ELEM_ELEMENT);
        if (StringUtils.isBlank(type) && elements != null && elements.getLength() > 0) {
            final Node elementType = elements.item(0).getAttributes().getNamedItem(ODataConstants.ATTR_M_TYPE);
            if (elementType != null) {
                property.setAttribute(ODataConstants.ATTR_M_TYPE, "Collection(" + elementType.getTextContent() + ")");
            }
        }

        return client.getBinder().getProperty(property);
    }

    @Override
    public ODataLinkCollection readLinks(final InputStream input, final ODataFormat format) {
        return client.getBinder().getLinkCollection(
                client.getDeserializer().toLinkCollection(input, format));
    }

    @Override
    public ODataError readError(final InputStream inputStream, final boolean isXML) {
        return client.getDeserializer().toODataError(inputStream, isXML);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(final InputStream src, final String format, final Class<T> reference) {
        Object res;

        try {
            if (ODataEntitySetIterator.class.isAssignableFrom(reference)) {
                res = new ODataEntitySetIterator(client, src, ODataPubFormat.fromString(format));
            } else if (ODataEntitySet.class.isAssignableFrom(reference)) {
                res = readEntitySet(src, ODataPubFormat.fromString(format));
            } else if (ODataEntity.class.isAssignableFrom(reference)) {
                res = readEntity(src, ODataPubFormat.fromString(format));
            } else if (ODataProperty.class.isAssignableFrom(reference)) {
                res = readProperty(src, ODataFormat.fromString(format));
            } else if (ODataLinkCollection.class.isAssignableFrom(reference)) {
                res = readLinks(src, ODataFormat.fromString(format));
            } else if (ODataValue.class.isAssignableFrom(reference)) {
                res = client.getPrimitiveValueBuilder().
                        setType(ODataValueFormat.fromString(format) == ODataValueFormat.TEXT
                                ? EdmSimpleType.String : EdmSimpleType.Stream).
                        setText(IOUtils.toString(src)).
                        build();
            } else if (AbstractEdmMetadata.class.isAssignableFrom(reference)) {
                res = readMetadata(src);
            } else if (ODataServiceDocument.class.isAssignableFrom(reference)) {
                res = readServiceDocument(src, ODataFormat.fromString(format));
            } else if (ODataError.class.isAssignableFrom(reference)) {
                res = readError(src, !format.toString().contains("json"));
            } else {
                throw new IllegalArgumentException("Invalid reference type " + reference);
            }
        } catch (Exception e) {
            LOG.warn("Cast error", e);
            res = null;
        } finally {
            if (!ODataEntitySetIterator.class.isAssignableFrom(reference)) {
                IOUtils.closeQuietly(src);
            }
        }

        return (T) res;
    }
}
