/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.server.core.uri;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata4.server.core.uri.parser.RawUri;
import org.apache.olingo.odata4.server.core.uri.parser.UriDecoder;
import org.apache.olingo.odata4.server.core.uri.parser.UriParserSyntaxException;
import org.junit.Test;

public class RawUriTest {

  private RawUri runRawParser(final String uri, final int scipSegments) throws UriParserSyntaxException {
    return UriDecoder.decodeUri(uri, scipSegments);
  }

  @Test
  public void testOption() throws Exception {
    RawUri rawUri;
    rawUri = runRawParser("?", 0);
    checkOptionCount(rawUri, 0);

    rawUri = runRawParser("?a", 0);
    checkOption(rawUri, 0, "a", "");

    rawUri = runRawParser("?a=b", 0);
    checkOption(rawUri, 0, "a", "b");

    rawUri = runRawParser("?=", 0);
    checkOption(rawUri, 0, "", "");

    rawUri = runRawParser("?=b", 0);
    checkOption(rawUri, 0, "", "b");

    rawUri = runRawParser("?a&c", 0);
    checkOption(rawUri, 0, "a", "");
    checkOption(rawUri, 1, "c", "");

    rawUri = runRawParser("?a=b&c", 0);
    checkOption(rawUri, 0, "a", "b");
    checkOption(rawUri, 1, "c", "");

    rawUri = runRawParser("?a=b&c=d", 0);
    checkOption(rawUri, 0, "a", "b");
    checkOption(rawUri, 1, "c", "d");

    rawUri = runRawParser("?=&=", 0);
    checkOption(rawUri, 0, "", "");
    checkOption(rawUri, 1, "", "");

    rawUri = runRawParser("?=&c=d", 0);
    checkOption(rawUri, 0, "", "");
    checkOption(rawUri, 1, "c", "d");
  }

  private void checkOption(final RawUri rawUri, final int index, final String name, final String value) {
    RawUri.QueryOption option = rawUri.queryOptionListDecoded.get(index);

    assertEquals(name, option.name);
    assertEquals(value, option.value);

  }

  private void checkOptionCount(final RawUri rawUri, final int count) {
    assertEquals(count, rawUri.queryOptionListDecoded.size());
  }

  @Test
  public void testPath() throws Exception {
    RawUri rawUri;

    rawUri = runRawParser("http://test.org", 0);
    checkPath(rawUri, "", new ArrayList<String>());

    rawUri = runRawParser("http://test.org/", 0);
    checkPath(rawUri, "/", Arrays.asList(""));

    rawUri = runRawParser("http://test.org/entitySet", 0);
    checkPath(rawUri, "/entitySet", Arrays.asList("entitySet"));

    rawUri = runRawParser("http://test.org/nonServiceSegment/entitySet", 0);
    checkPath(rawUri, "/nonServiceSegment/entitySet", Arrays.asList("nonServiceSegment", "entitySet"));

    rawUri = runRawParser("http://test.org/nonServiceSegment/entitySet", 1);
    checkPath(rawUri, "/nonServiceSegment/entitySet", Arrays.asList("entitySet"));

    rawUri = runRawParser("", 0);
    checkPath(rawUri, "", new ArrayList<String>());

    rawUri = runRawParser("/", 0);
    checkPath(rawUri, "/", Arrays.asList(""));

    rawUri = runRawParser("/entitySet", 0);
    checkPath(rawUri, "/entitySet", Arrays.asList("entitySet"));

    rawUri = runRawParser("entitySet", 0);
    checkPath(rawUri, "entitySet", Arrays.asList("entitySet"));

    rawUri = runRawParser("nonServiceSegment/entitySet", 0);
    checkPath(rawUri, "nonServiceSegment/entitySet", Arrays.asList("nonServiceSegment", "entitySet"));

    rawUri = runRawParser("nonServiceSegment/entitySet", 1);
    checkPath(rawUri, "nonServiceSegment/entitySet", Arrays.asList("entitySet"));
  }

  
    @Test
    public void testSplitt() {
    UriDecoder.splitt("", '/');
    UriDecoder.splitt("/", '/');
    UriDecoder.splitt("a", '/');
    UriDecoder.splitt("a/", '/');
    UriDecoder.splitt("/a", '/');
    UriDecoder.splitt("a/a", '/');
    }
   

  private void checkPath(final RawUri rawUri, final String path, final List<String> list) {
    assertEquals(path, rawUri.path);

    assertEquals(list.size(), rawUri.pathSegmentListDecoded.size());

    int i = 0;
    while (i < list.size()) {
      assertEquals(list.get(i), rawUri.pathSegmentListDecoded.get(i));
      i++;
    }
  }

}
