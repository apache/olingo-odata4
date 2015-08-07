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
package org.apache.olingo.server.core.uri.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class RawUriTest {

  private RawUri runRawParser(final String path, final String query, final int skipSegments)
      throws UriParserSyntaxException {
    return UriDecoder.decodeUri(path, query, null, skipSegments);
  }

  @Test
  public void testOption() throws Exception {
    RawUri rawUri;
    rawUri = runRawParser("", "", 0);
    checkOptionCount(rawUri, 0);

    rawUri = runRawParser("", "a", 0);
    checkOption(rawUri, 0, "a", "");

    rawUri = runRawParser("", "a=b", 0);
    checkOption(rawUri, 0, "a", "b");

    rawUri = runRawParser("", "=", 0);
    checkOption(rawUri, 0, "", "");

    rawUri = runRawParser("", "=b", 0);
    checkOption(rawUri, 0, "", "b");

    rawUri = runRawParser("", "a&c", 0);
    checkOption(rawUri, 0, "a", "");
    checkOption(rawUri, 1, "c", "");

    rawUri = runRawParser("", "a=b&c", 0);
    checkOption(rawUri, 0, "a", "b");
    checkOption(rawUri, 1, "c", "");

    rawUri = runRawParser("", "a=b&c=d", 0);
    checkOption(rawUri, 0, "a", "b");
    checkOption(rawUri, 1, "c", "d");

    rawUri = runRawParser("", "=&=", 0);
    checkOption(rawUri, 0, "", "");
    checkOption(rawUri, 1, "", "");

    rawUri = runRawParser("", "=&c=d", 0);
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

    rawUri = runRawParser("", null, 0);
    checkPath(rawUri, "", Collections.<String> emptyList());

    rawUri = runRawParser("/", null, 0);
    checkPath(rawUri, "/", Collections.<String> emptyList());

    rawUri = runRawParser("/entitySet", null, 0);
    checkPath(rawUri, "/entitySet", Arrays.asList("entitySet"));

    rawUri = runRawParser("//entitySet", null, 0);
    checkPath(rawUri, "//entitySet", Arrays.asList("entitySet"));

    rawUri = runRawParser("entitySet", null, 0);
    checkPath(rawUri, "entitySet", Arrays.asList("entitySet"));

    rawUri = runRawParser("/nonServiceSegment/entitySet", null, 0);
    checkPath(rawUri, "/nonServiceSegment/entitySet", Arrays.asList("nonServiceSegment", "entitySet"));

    rawUri = runRawParser("/nonServiceSegment/entitySet", null, 1);
    checkPath(rawUri, "/nonServiceSegment/entitySet", Arrays.asList("entitySet"));

    rawUri = runRawParser("nonServiceSegment/entitySet", null, 0);
    checkPath(rawUri, "nonServiceSegment/entitySet", Arrays.asList("nonServiceSegment", "entitySet"));

    rawUri = runRawParser("nonServiceSegment/entitySet", null, 1);
    checkPath(rawUri, "nonServiceSegment/entitySet", Arrays.asList("entitySet"));

    rawUri = runRawParser("non//Service/Segment///entitySet/", null, 3);
    checkPath(rawUri, "non//Service/Segment///entitySet/", Arrays.asList("entitySet"));

    rawUri = runRawParser("/a", "abc=xx+yz", 0);
    checkPath(rawUri, "/a", Arrays.asList("a"));
  }

  @Test
  public void testSplit() {
    assertTrue(UriDecoder.splitSkipEmpty("", '/').isEmpty());
    assertTrue(UriDecoder.splitSkipEmpty("/", '/').isEmpty());
    assertEquals(Arrays.asList("a"), UriDecoder.splitSkipEmpty("a", '/'));
    assertEquals(Arrays.asList("a"), UriDecoder.splitSkipEmpty("a/", '/'));
    assertEquals(Arrays.asList("a"), UriDecoder.splitSkipEmpty("/a", '/'));
    assertEquals(Arrays.asList("a", "a"), UriDecoder.splitSkipEmpty("a/a", '/'));
    assertEquals(Arrays.asList("a", "a"), UriDecoder.splitSkipEmpty("/a/a", '/'));
  }

  private void checkPath(final RawUri rawUri, final String path, final List<String> list) {
    assertEquals(path, rawUri.path);

    assertEquals(list.size(), rawUri.pathSegmentListDecoded.size());

    for (int i = 0; i < list.size(); i++) {
      assertEquals(list.get(i), rawUri.pathSegmentListDecoded.get(i));
    }
  }

  @Test(expected = UriParserSyntaxException.class)
  public void wrongPercentEncoding() throws Exception {
    runRawParser("%wrong", null, 0);
  }
}
