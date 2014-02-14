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
package com.msopentech.odatajclient.engine.data.impl.v3;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.msopentech.odatajclient.engine.data.impl.AbstractServiceDocument;
import com.msopentech.odatajclient.engine.data.impl.XMLServiceDocumentDeserializer;
import java.net.URI;

/**
 * Service document, represented via XML.
 */
@JsonDeserialize(using = XMLServiceDocumentDeserializer.class)
public class XMLServiceDocument extends AbstractServiceDocument {

    private URI baseURI;

    @Override
    public URI getBaseURI() {
        return this.baseURI;
    }

    /**
     * Sets base URI.
     *
     * @param baseURI base URI.
     */
    public void setBaseURI(final URI baseURI) {
        this.baseURI = baseURI;
    }

}
