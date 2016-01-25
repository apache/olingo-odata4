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
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.server.api.uri.queryoption.QueryOption;
import org.junit.Test;

public class UriDecoderTest {

  @Test
  public void split() throws Exception {
    assertEquals(Arrays.asList(""), UriDecoder.splitAndDecodePath(""));
    assertEquals(Arrays.asList("", ""), UriDecoder.splitAndDecodePath("/"));
    assertEquals(Arrays.asList("a"), UriDecoder.splitAndDecodePath("a"));
    assertEquals(Arrays.asList("a", ""), UriDecoder.splitAndDecodePath("a/"));
    assertEquals(Arrays.asList("", "a"), UriDecoder.splitAndDecodePath("/a"));
    assertEquals(Arrays.asList("a", "b"), UriDecoder.splitAndDecodePath("a/b"));
    assertEquals(Arrays.asList("", "a", "b"), UriDecoder.splitAndDecodePath("/a/b"));
    assertEquals(Arrays.asList("", "a", "", "", "b", ""), UriDecoder.splitAndDecodePath("/a///b/"));
  }

  @Test
  public void path() throws Exception {
    assertEquals(Arrays.asList("a", "entitySet('/')", "bcd"),
        UriDecoder.splitAndDecodePath("a/entitySet('%2F')/b%63d"));
  }

  @Test
  public void options() throws Exception {
    checkOption("", "", "");

    checkOption("a", "a", "");
    checkOption("a=b", "a", "b");
    checkOption("=", "", "");
    checkOption("=b", "", "b");

    checkOption("a&c", "a", "");
    checkOption("a&c", "c", "");

    checkOption("a=b&c", "a", "b");
    checkOption("a=b&c", "c", "");

    checkOption("a=b&c=d", "a", "b");
    checkOption("a=b&c=d", "c", "d");

    checkOption("=&=", "", "");
    assertEquals(2, UriDecoder.splitAndDecodeOptions("=&=").size());
    assertEquals(13, UriDecoder.splitAndDecodeOptions("&&&&&&&&&&&&").size());

    checkOption("=&c=d", "", "");
    checkOption("=&c=d", "c", "d");

    checkOption("a%62c=d%65f", "abc", "def");
    checkOption("a='%26%3D'", "a", "'&='");
  }

  @Test(expected = UriParserSyntaxException.class)
  public void wrongPercentEncoding() throws Exception {
    UriDecoder.splitAndDecodePath("%wrong");
  }

  private void checkOption(final String query, final String name, final String value)
      throws UriParserSyntaxException {
    final List<QueryOption> options = UriDecoder.splitAndDecodeOptions(query);
    for (final QueryOption option : options) {
      if (option.getName().equals(name)) {
        assertEquals(value, option.getText());
        return;
      }
    }
    fail("Option " + name + " not found!");
  }
}
