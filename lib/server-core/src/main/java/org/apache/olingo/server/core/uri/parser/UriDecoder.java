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

public class UriDecoder {

  public static RawUri decodeUri(final String path, final String query, final String fragment,
      final int skipSegments) throws UriParserSyntaxException {
    RawUri rawUri = new RawUri();

    rawUri.path = path;
    rawUri.queryOptionString = query;
    rawUri.fragment = fragment;

    rawUri.pathSegmentList = splitPath(path, skipSegments);
    rawUri.queryOptionList = splitOptions(query);
    decode(rawUri);

    return rawUri;
  }

  private static void decode(final RawUri rawUri) throws UriParserSyntaxException {
    rawUri.pathSegmentListDecoded = new ArrayList<String>();
    for (String segment : rawUri.pathSegmentList) {
      rawUri.pathSegmentListDecoded.add(decode(segment));
    }

    rawUri.queryOptionListDecoded = new ArrayList<RawUri.QueryOption>();
    for (RawUri.QueryOption optionPair : rawUri.queryOptionList) {
      rawUri.queryOptionListDecoded.add(new RawUri.QueryOption(
          decode(optionPair.name),
          decode(optionPair.value)));
    }
  }

  private static List<RawUri.QueryOption> splitOptions(final String queryOptionString) {
    if (queryOptionString == null) {
      return Collections.emptyList();
    }

    List<RawUri.QueryOption> queryOptionList = new ArrayList<RawUri.QueryOption>();
    for (String option : splitSkipEmpty(queryOptionString, '&')) {
      final List<String> pair = splitFirst(option, '=');
      queryOptionList.add(new RawUri.QueryOption(pair.get(0), pair.get(1)));
    }
    return queryOptionList;
  }

  private static List<String> splitFirst(final String input, final char c) {
    int pos = input.indexOf(c, 0);
    if (pos >= 0) {
      return Arrays.asList(input.substring(0, pos), input.substring(pos + 1));
    } else {
      return Arrays.asList(input, "");
    }
  }

  private static List<String> splitPath(final String path, final int skipSegments) {
    List<String> list = splitSkipEmpty(path, '/');

    return skipSegments > 0 ? list.subList(skipSegments, list.size()) : list;
  }

  /**
   * Split the input string at given character and drop all empty elements.
   *
   * @param input string to split
   * @param c character at which to split
   * @return list of elements (can be empty)
   */
  static List<String> splitSkipEmpty(final String input, final char c) {
    if(input.isEmpty() || input.length() == 1 && input.charAt(0) == c) {
      return Collections.emptyList();
    }

    List<String> list = new LinkedList<String>();

    int start = 0;
    int end;

    while ((end = input.indexOf(c, start)) >= 0) {
      if(start != end) {
        list.add(input.substring(start, end));
      }
      start = end + 1;
    }

    if(input.charAt(input.length()-1) != c) {
      list.add(input.substring(start));
    }

    return list;
  }

  public static String decode(final String encoded) throws UriParserSyntaxException {
    try {
      return Decoder.decode(encoded);
    } catch (final IllegalArgumentException e) {
      throw new UriParserSyntaxException("Wrong percent encoding!", e, UriParserSyntaxException.MessageKeys.SYNTAX);
    }
  }
}
