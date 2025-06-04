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
package org.apache.olingo.client.core.uri;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExampleUriGeneratorTest {
    /**
     * This test demonstrates the normal behavior of encapsulating a string with single quotes
     * <p>
     * The test succeeds
     */
    @Test
    public void testHappyPath() {
        String uri = ExampleUriGenerator.filterPersonByName("henk");

        // Expected: https://example.com/Person?$filter=(name eq 'henk')
        assertEquals("https://example.com/Person?%24filter=%28name%20eq%20'henk'%29", uri);
    }


    /**
     * This test demonstrates that some String literals are not encapsulated (quoted) correctly. This happens because
     * {@link org.apache.olingo.client.core.uri.URIUtils#quoteString(String, boolean)} incorrectly determines the string
     * to be an enum using a poorly designed regex, causing it to not encapsulate the string with single quotes. This
     * can be abused to circumvent filters as illustrated in the example below.
     * <p>
     * The test <b>fails</b>
     */
    @Test
    public void testODataInjection() {
        String uri = ExampleUriGenerator.filterPersonByName("' or name ne '");

        // Expected: https://example.com/Person?$filter=(name eq ''' or name ne ''')
        assertEquals("https://example.com/Person?%24filter=%28name%20eq%20'''%20or%20name%20ne%20'''%29", uri);

        // Actual:  https://example.com/Person?%24filter=%28name%20eq%20''%20or%20name%20ne%20''%29
        //          https://example.com/Person?$filter=(name eq '' or name ne '')
    }

    static final class ExampleUriGenerator {
        private static final ODataClient client = ODataClientFactory.getClient();

        private ExampleUriGenerator() {
        }

        static String filterPersonByName(String name) {
            // OLingo does not escape string literals so we do it ourselves
            String escapedName = escape(name);

            String filter = client.getFilterFactory() //
                .eq("name", escapedName) //
                .build();

            return client.newURIBuilder("https://example.com/") //
                .appendEntitySetSegment("Person") //
                .filter(filter) //
                .build().toString();
        }

        //CHECKSTYLE:OFF
        /**
         * Escapes a string literal by representing a single quote in a string literal as two consecutive single quotes,
         * as per the <a href="https://docs.oasis-open.org/odata/odata/v4.01/os/part2-url-conventions/odata-v4.01-os-part2-url-conventions.html#sec_URLSyntax">OData 4.01 URL Conventions</a>
         *
         * @param value the value to escape
         * @return the escaped value
         */
        //CHECKSTYLE:ON
        private static String escape(String value) {
            return value.replaceAll("'", "''");
        }
    }

}
