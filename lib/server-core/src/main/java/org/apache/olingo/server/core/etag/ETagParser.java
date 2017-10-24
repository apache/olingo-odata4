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
package org.apache.olingo.server.core.etag;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Parses the values of HTTP header fields that contain a list of entity tags or a
 * single star character, i.e., <code>If-Match</code> and <code>If-None-Match</code>.</p>
 * <p>See <a href="https://www.ietf.org/rfc/rfc7232.txt">RFC 7232</a> for details;
 * there the following grammar is defined:</p>
 * <pre>
 * If-Match = "*" / 1#entity-tag
 * If-None-Match = "*" / 1#entity-tag
 * entity-tag = [ weak ] opaque-tag
 * weak = %x57.2F ; "W/", case-sensitive
 * opaque-tag = DQUOTE *etagc DQUOTE
 * etagc = %x21 / %x23-7E / %x80-FF
 * </pre>
 * <p>Values with illegal syntax do not contribute to the result but no exception is thrown.</p>
 */
public class ETagParser {

  private static final Pattern ETAG = Pattern.compile("\\s*(,\\s*)+|((?:W/)?\"[!#-~\\x80-\\xFF]*\")");

  private ETagParser() {
    // Private constructor for utility classes
  }

  protected static Collection<String> parse(final Collection<String> values) {
    if (values == null) {
      return Collections.<String> emptySet();
    }

    Set<String> result = new HashSet<String>();
    for (final String value : values) {
      final Collection<String> part = parse(value);
      if (part.size() == 1 && "*".equals(part.iterator().next())) {
        return part;
      } else {
        result.addAll(part);
      }
    }
    return result;
  }

  private static Collection<String> parse(final String value) {
    if ("*".equals(value.trim())) {
      return Collections.singleton("*");
    } else {
      Set<String> result = new HashSet<String>();
      String separator = "";
      int start = 0;
      Matcher matcher = ETAG.matcher(value.trim());
      while (matcher.find() && matcher.start() == start) {
        start = matcher.end();
        if (matcher.group(1) != null) {
          separator = matcher.group(1);
        } else if (separator != null) {
          result.add(matcher.group(2));
          separator = null;
        } else {
          return Collections.<String> emptySet();
        }
      }
      return matcher.hitEnd() ? result : Collections.<String> emptySet();
    }
  }
}