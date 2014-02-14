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

import static org.junit.Assert.assertNotNull;

import com.msopentech.odatajclient.engine.client.ODataV3Client;
import com.msopentech.odatajclient.engine.data.ODataError;
import org.junit.Test;

public class ErrorTest extends AbstractTest {

    @Override
    protected ODataV3Client getClient() {
        return v3Client;
    }

    private void xmlError(final String name) {
        final ODataError error = getClient().getReader().readError(getClass().getResourceAsStream(name), true);
        assertNotNull(error);
    }

    @Test
    public void error() {
        xmlError("error.xml");
    }

    @Test
    public void stacktrace() {
        xmlError("stacktrace.xml");
    }
}
