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
package org.apache.olingo.server.core.debug;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataResponse;
import org.junit.Test;

public class DebugTabBodyTest extends AbstractDebugTabTest {

  @Test
  public void nullResponseMustNotLeadToException() throws Exception {
    DebugTabBody tab = new DebugTabBody(null);

    assertEquals("null", createJson(tab));
    assertEquals("<pre class=\"code\">\nODataLibrary: No body.\n</pre>\n", createHtml(tab));
  }

  @Test
  public void json() throws Exception {
    ODataResponse response = new ODataResponse();
    response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.JSON_NO_METADATA.toContentTypeString());
    response.setContent(IOUtils.toInputStream("{\"property\": true}"));
    assertEquals("\"{\\\"property\\\": true}\"", createJson(new DebugTabBody(response)));

    response.setContent(IOUtils.toInputStream("{\"property\": false}"));
    assertEquals("<pre class=\"code json\">\n{\"property\": false}\n</pre>\n", createHtml(new DebugTabBody(response)));
  }

  @Test
  public void xml() throws Exception {
    ODataResponse response = new ODataResponse();
    response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.APPLICATION_XML.toContentTypeString());
    response.setContent(IOUtils.toInputStream("<?xml version='1.1'?>\n<a xmlns=\"b\" />\n"));
    assertEquals("\"<?xml version='1.1'?>\\n<a xmlns=\\\"b\\\" />\\n\"", createJson(new DebugTabBody(response)));

    response.setContent(IOUtils.toInputStream("<?xml version='1.1'?>\n<c xmlns=\"d\" />\n"));
    assertEquals("<pre class=\"code xml\">\n&lt;?xml version='1.1'?&gt;\n&lt;c xmlns=\"d\" /&gt;\n\n</pre>\n",
        createHtml(new DebugTabBody(response)));
  }

  @Test
  public void text() throws Exception {
    ODataResponse response = new ODataResponse();
    response.setContent(IOUtils.toInputStream("testText\n12"));
    assertEquals("\"testText\\n12\"", createJson(new DebugTabBody(response)));

    response.setContent(IOUtils.toInputStream("testText\n34"));
    assertEquals("<pre class=\"code\">\ntestText\n34\n</pre>\n", createHtml(new DebugTabBody(response)));
  }

  @Test
  public void image() throws Exception {
    ODataResponse response = new ODataResponse();
    response.setHeader(HttpHeader.CONTENT_TYPE, "image/png");
    response.setContent(new ByteArrayInputStream(new byte[] { -1, -2, -3, -4 }));
    assertEquals("\"//79/A==\"", createJson(new DebugTabBody(response)));

    response.setContent(new ByteArrayInputStream(new byte[] { -5, -6, -7, -8 }));
    assertEquals("<img src=\"data:image/png;base64,+/r5+A==\" />\n", createHtml(new DebugTabBody(response)));
  }

  @Test
  public void streamError() throws Exception {
    ODataResponse response = new ODataResponse();
    InputStream input = new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException("test");
      }
    };
    response.setContent(input);
    assertEquals("\"Could not parse Body for Debug Output\"", createJson(new DebugTabBody(response)));
    assertEquals("<pre class=\"code\">\nCould not parse Body for Debug Output\n</pre>\n",
        createHtml(new DebugTabBody(response)));
  }
}
