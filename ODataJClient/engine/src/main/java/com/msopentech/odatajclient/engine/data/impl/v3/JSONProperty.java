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

import com.msopentech.odatajclient.engine.data.impl.JSONPropertyDeserializer;
import com.msopentech.odatajclient.engine.data.impl.JSONPropertySerializer;
import com.msopentech.odatajclient.engine.data.impl.AbstractPayloadObject;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import org.w3c.dom.Element;

/**
 * A single property (primitive, complex or collection) represented via JSON.
 */
@JsonSerialize(using = JSONPropertySerializer.class)
@JsonDeserialize(using = JSONPropertyDeserializer.class)
public class JSONProperty extends AbstractPayloadObject {

    private static final long serialVersionUID = 553414431536637434L;

    private URI metadata;

    private Element content;

    /**
     * Gets metadata URI.
     *
     * @return metadata URI.
     */
    public URI getMetadata() {
        return metadata;
    }

    /**
     * Sets metadata URI.
     *
     * @param metadata metadata URI.
     */
    public void setMetadata(final URI metadata) {
        this.metadata = metadata;
    }

    /**
     * Gets content.
     *
     * @return content as DOM element.
     */
    public Element getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content content as DOM element.
     */
    public void setContent(final Element content) {
        this.content = content;
    }
}
