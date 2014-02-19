/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.server.core.uri.parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.odata4.server.core.uri.UriParserSyntaxException;
import org.apache.olingo.odata4.server.core.uri.parser.RawUri.QueryOption;

public class UriDecoder {

  static Pattern uriPattern = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");

  public static RawUri decodeUri(final String uri, final int scipSegments) throws UriParserSyntaxException {
    RawUri rawUri = new RawUri();

    Matcher m = uriPattern.matcher(uri);
    if (m.matches()) {
      rawUri.sheme = m.group(2);
      rawUri.authority = m.group(4);
      rawUri.path = m.group(5);
      rawUri.queryOptionString = m.group(7);
      rawUri.fragment = m.group(9);
    }

    splittPath(rawUri, scipSegments);
    splittOptions(rawUri);
    decode(rawUri);

    return rawUri;
  }

  private static void decode(final RawUri rawUri) throws UriParserSyntaxException {
    rawUri.pathSegmentListDecoded = new ArrayList<String>();
    for (String segment : rawUri.pathSegmentList) {
      rawUri.pathSegmentListDecoded.add(decode(segment));
    }

    rawUri.queryOptionListDecoded = new ArrayList<QueryOption>();
    for (QueryOption optionPair : rawUri.queryOptionList) {
      rawUri.queryOptionListDecoded.add(new QueryOption(
          decode(optionPair.name),
          decode(optionPair.value)));
    }
  }

  private static void splittOptions(final RawUri rawUri) {
    rawUri.queryOptionList = new ArrayList<RawUri.QueryOption>();

    if (rawUri.queryOptionString == null) {
      return;
    }

    List<String> options = splitt(rawUri.queryOptionString, '&');

    for (String option : options) {
      if (option.length() != 0) {
        List<String> pair = splittFirst(option, '=');
        rawUri.queryOptionList.add(
            new RawUri.QueryOption(pair.get(0), pair.get(1)));
      }
    }
  }

  private static List<String> splittFirst(final String input, final char c) {
    int pos = input.indexOf(c, 0);
    if (pos >= 0) {
      return Arrays.asList(input.substring(0, pos), input.substring(pos + 1));
    } else {
      return Arrays.asList(input, "");
    }
  }

  public static void splittPath(final RawUri rawUri, int scipSegments) {
    List<String> list = splitt(rawUri.path, '/');

    if (list.size() > 0) {
      if (list.get(0).length() == 0) {
        scipSegments++;
      }
    }

    if (scipSegments > 0) {
      rawUri.pathSegmentList = list.subList(scipSegments, list.size());
    } else {
      rawUri.pathSegmentList = list;
    }
  }

  public static List<String> splitt(final String input, final char c) {

    List<String> list = new LinkedList<String>();

    int start = 0;
    int end = -1;

    while ((end = input.indexOf(c, start)) >= 0) {
      list.add(input.substring(start, end));
      start = end + 1;
    }

    if (end == -1) {
      list.add(input.substring(start));
    }

    return list;
  }

  public static String decode(final String encoded) throws UriParserSyntaxException {
    try {
      return URLDecoder.decode(encoded, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new UriParserSyntaxException("Error while decoding");
    }
  }

}
