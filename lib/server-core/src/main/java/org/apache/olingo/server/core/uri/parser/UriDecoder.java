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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.commons.core.Decoder;
import org.apache.olingo.server.api.uri.queryoption.QueryOption;
import org.apache.olingo.server.core.uri.queryoption.CustomQueryOptionImpl;

public class UriDecoder {

  /** Splits the path string at '/' characters and percent-decodes the resulting path segments. */
  protected static List<String> splitAndDecodePath(final String path) throws UriParserSyntaxException {
    List<String> pathSegmentsDecoded = new ArrayList<String>();
    for (final String segment : splitSkipEmpty(path, '/')) {
      pathSegmentsDecoded.add(decode(segment));
    }
    return pathSegmentsDecoded;
  }

  /**
   * Splits the query-option string at '&' characters, the resulting parts at '=' characters,
   * and separately percent-decodes names and values of the resulting name-value pairs.
   */
  protected static List<QueryOption> splitAndDecodeOptions(final String queryOptionString)
      throws UriParserSyntaxException {
    if (queryOptionString == null || queryOptionString.isEmpty()) {
      return Collections.emptyList();
    }

    List<QueryOption> queryOptions = new ArrayList<QueryOption>();
    for (final String option : splitSkipEmpty(queryOptionString, '&')) {
      final List<String> pair = splitFirst(option, '=');
      queryOptions.add(new CustomQueryOptionImpl()
          .setName(decode(pair.get(0)))
          .setText(decode(pair.get(1))));
    }
    return queryOptions;
  }

  private static List<String> splitFirst(final String input, final char c) {
    int pos = input.indexOf(c);
    if (pos >= 0) {
      return Arrays.asList(input.substring(0, pos), input.substring(pos + 1));
    } else {
      return Arrays.asList(input, "");
    }
  }

  /**
   * Splits the input string at the given character and drops all empty elements.
   * @param input string to split
   * @param c character at which to split
   * @return list of elements (can be empty)
   */
  private static List<String> splitSkipEmpty(final String input, final char c) {
    if (input.isEmpty() || input.length() == 1 && input.charAt(0) == c) {
      return Collections.emptyList();
    }

    List<String> list = new LinkedList<String>();

    int start = 0;
    int end;

    while ((end = input.indexOf(c, start)) >= 0) {
      if (start != end) {
        list.add(input.substring(start, end));
      }
      start = end + 1;
    }

    if (input.charAt(input.length() - 1) != c) {
      list.add(input.substring(start));
    }

    return list;
  }

  private static String decode(final String encoded) throws UriParserSyntaxException {
    try {
      return Decoder.decode(encoded);
    } catch (final IllegalArgumentException e) {
      throw new UriParserSyntaxException("Wrong percent encoding!", e, UriParserSyntaxException.MessageKeys.SYNTAX);
    }
  }
}
