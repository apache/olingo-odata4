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
package org.apache.olingo.fit.metadata;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.fail;

public class MetadataTest {

    @Test
    public void testExternalEntity() throws IOException {
        TestHttpServer server = new TestHttpServer("secret");
        try {
            String xml = String.format(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                            + "<!DOCTYPE oops [\n"
                            + "     <!ENTITY foo SYSTEM \"%s\" >\n"
                            + "]>\n"
                            + "<oops>&foo;</oops>",
                    server.url());

            new Metadata(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            server.close();
        }

        if (server.accepted()) {
            fail("Oops! The server has been reached!");
        }
    }

    @Test
    public void testExternalSchema() throws IOException {
        TestHttpServer server = new TestHttpServer("secret");
        try {
            String xml = String.format(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                            + "<!DOCTYPE oops SYSTEM “%s”>\n"
                            + "<oops>&foo;</oops>",
                    server.url());

            new Metadata(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            server.close();
        }

        if (server.accepted()) {
            fail("Oops! The server has been reached!");
        }
    }

    @Test
    public void testExternalEntityParameter() throws IOException {
        TestHttpServer server = new TestHttpServer("secret");
        try {
            String xml = String.format(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                            + "<!DOCTYPE oops [\n"
                            + "     <!ENTITY %% sp SYSTEM \"%s\">\n"
                            + "%%sp;"
                            + "]>\n"
                            + "<oops></oops>",
                    server.url());

            new Metadata(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            server.close();
        }

        if (server.accepted()) {
            fail("Oops! The server has been reached!");
        }
    }

    @Test
    public void billionLaughs() {
        String xml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE lolz [\n" +
                " <!ENTITY lol \"lol\">\n" +
                " <!ELEMENT lolz (#PCDATA)>\n" +
                " <!ENTITY lol1 \"&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;\">\n" +
                " <!ENTITY lol2 \"&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;\">\n" +
                " <!ENTITY lol3 \"&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;\">\n" +
                " <!ENTITY lol4 \"&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;\">\n" +
                " <!ENTITY lol5 \"&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;\">\n" +
                " <!ENTITY lol6 \"&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;\">\n" +
                " <!ENTITY lol7 \"&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;\">\n" +
                " <!ENTITY lol8 \"&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;\">\n" +
                " <!ENTITY lol9 \"&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;\">\n" +
                "]>\n" +
                "<lolz>&lol9;</lolz>";

        new Metadata(new ByteArrayInputStream(xml.getBytes()));
    }

    @Test
    public void testExternalXInclude() throws IOException {
        TestHttpServer server = new TestHttpServer("secret");
        try {
            String xml = String.format(
                    "<root xmlns:xi=\"http://www.w3.org/2001/XInclude\">\n" +
                            "  <xi:include href=\"%s\" parse=\"text\" />\n" +
                            "</root>",
                    server.url());

            new Metadata(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            server.close();
        }

        if (server.accepted()) {
            fail("Oops! The server has been reached!");
        }
    }

    @Test
    public void testExternalSchemaLocation() throws IOException {
        TestHttpServer server = new TestHttpServer("secret");
        try {
            String xml = String.format(
                    "<ead xmlns=\"urn:isbn:1-931666-22-9\"\n" +
                            "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                            "     xsi:schemaLocation=\"urn:isbn:1-931666-22-9 %s\">\n" +
                            "</ead>",
                    server.url());

            new Metadata(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            server.close();
        }

        if (server.accepted()) {
            fail("Oops! The server has been reached!");
        }
    }

}
