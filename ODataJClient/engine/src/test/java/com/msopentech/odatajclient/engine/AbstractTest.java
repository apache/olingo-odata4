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
package com.msopentech.odatajclient.engine;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.ODataClientFactory;
import com.msopentech.odatajclient.engine.client.ODataV3Client;
import com.msopentech.odatajclient.engine.client.ODataV4Client;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import java.util.Locale;
import org.junit.BeforeClass;

public abstract class AbstractTest {

    protected static ODataV3Client v3Client;

    protected static ODataV4Client v4Client;

    protected abstract ODataClient getClient();

    /**
     * This is needed for correct number handling (Double, for example).
     */
    @BeforeClass
    public static void setEnglishLocale() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @BeforeClass
    public static void setClientInstances() {
        v3Client = ODataClientFactory.getV3();
        v4Client = ODataClientFactory.getV4();
    }

    protected String getSuffix(final ODataPubFormat format) {
        return format == ODataPubFormat.ATOM ? "xml" : "json";
    }

    protected String getSuffix(final ODataFormat format) {
        return format == ODataFormat.XML ? "xml" : "json";
    }
}
