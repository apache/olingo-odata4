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
package com.msopentech.odatajclient.engine.it;

import com.msopentech.odatajclient.engine.client.http.AbstractBasicAuthHttpClientFactory;
import com.msopentech.odatajclient.engine.client.http.DefaultHttpClientFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class AuthEntityRetrieveTestITCase extends EntityRetrieveTestITCase {

    @BeforeClass
    public static void enableBasicAuth() {
        client.getConfiguration().setHttpClientFactory(new AbstractBasicAuthHttpClientFactory() {

            @Override
            protected String getUsername() {
                return "odatajclient";
            }

            @Override
            protected String getPassword() {
                return "odatajclient";
            }
        });
    }

    @AfterClass
    public static void disableBasicAuth() {
        client.getConfiguration().setHttpClientFactory(new DefaultHttpClientFactory());
    }

    @Override
    protected String getServiceRoot() {
        return testAuthServiceRootURL;
    }
}
