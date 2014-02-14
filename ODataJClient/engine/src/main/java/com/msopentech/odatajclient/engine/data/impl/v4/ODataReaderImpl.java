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
package com.msopentech.odatajclient.engine.data.impl.v4;

import com.msopentech.odatajclient.engine.client.ODataV4Client;
import com.msopentech.odatajclient.engine.data.ODataServiceDocument;
import com.msopentech.odatajclient.engine.data.impl.AbstractODataReader;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.metadata.EdmV4Metadata;
import java.io.InputStream;

public class ODataReaderImpl extends AbstractODataReader {

    private static final long serialVersionUID = -2481293269536406956L;

    public ODataReaderImpl(final ODataV4Client client) {
        super(client);
    }

    @Override
    public EdmV4Metadata readMetadata(final InputStream input) {
        return new EdmV4Metadata(client, input);
    }

    @Override
    public ODataServiceDocument readServiceDocument(final InputStream input, final ODataFormat format) {
        return ((ODataV4Client) client).getBinder().getODataServiceDocument(
                ((ODataV4Client) client).getDeserializer().toServiceDocument(input, format));
    }
}
