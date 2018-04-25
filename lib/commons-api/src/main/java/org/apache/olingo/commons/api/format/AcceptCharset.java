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
package org.apache.olingo.commons.api.format;

import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class AcceptCharset {

  private static final Pattern Q_PATTERN = Pattern.compile("\\A(?:0(?:\\.\\d{0,3})?)|(?:1(?:\\.0{0,3})?)\\Z");
  private static final Pattern CHARSET_PATTERN = Pattern.compile("([^,][\\w!#$%&'*+-._`|~;^]*)");
  private final String charset;
  private final Map<String, String> parameters;
  private final Float quality;
  private static final String UTF8_CHARSET = "utf8";
  private static final String UTF8_CHARSET1 = "utf-8";
  
  private AcceptCharset(final String charset) {
    parameters = TypeUtil.createParameterMap();
    this.charset = parse(charset, parameters);
    
    if (!(UTF8_CHARSET.equalsIgnoreCase(this.charset)) && 
        !(UTF8_CHARSET1.equalsIgnoreCase(this.charset)) && !(TypeUtil.MEDIA_TYPE_WILDCARD.equals(this.charset))) {
      if (CHARSET_PATTERN.matcher(this.charset).matches()) {
        throw new UnsupportedCharsetException("Unsupported charset in accept charset header:" + charset);
      } else {
        throw new IllegalArgumentException("Illegal charset in accept charset header:" + charset);
      }
    }
    for (Entry<String, String> param : parameters.entrySet()) {
      if (!param.getKey().equals(TypeUtil.PARAMETER_Q)) {
        throw new IllegalArgumentException("Illegal parameters in accept charset header:" + charset);
      }
    }
    final String q = parameters.get(TypeUtil.PARAMETER_Q);
    if (q == null) {
      quality = 1F;
    } else if (Q_PATTERN.matcher(q).matches()) {
        quality = Float.valueOf(q);
    } else {
      throw new IllegalArgumentException("Illegal quality parameter '" + q + "' in accept charset header:" + charset);
    }
  }

  private String parse(String acceptCharset, Map<String, String> parameters) {
    final String[] charsetAndParameters = acceptCharset.split(TypeUtil.PARAMETER_SEPARATOR, 2);
    acceptCharset = charsetAndParameters[0];
    final String params = (charsetAndParameters.length > 1 ? charsetAndParameters[1] : null);
    TypeUtil.parseParameters(params, parameters);
    return acceptCharset;
  }
  
  /**
   * Creates a list of {@link AcceptCharset} objects based on given input string.
   * @param acceptCharsets accept types, comma-separated, as specified for the HTTP header <code>Accept-Charset</code>
   * @return a list of <code>AcceptType</code> objects
   * @throws Exception 
   * @throws IllegalArgumentException if input string is not parseable
   */
  public static List<AcceptCharset> create(final String acceptCharsets) {
    if (acceptCharsets == null) {
      throw new IllegalArgumentException("Type parameter MUST NOT be null.");
    }
    List<AcceptCharset> result = new ArrayList<AcceptCharset>();
    List<Exception> exceptionList = new ArrayList<Exception>();

    String[] values = acceptCharsets.split(",");
    if (values.length == 0) {
      values = new String[1];
      values[0] = acceptCharsets;
    }
    for (String value : values) {
      try {
        result.add(new AcceptCharset(value.trim()));
      } catch (UnsupportedCharsetException e) {
        exceptionList.add(e);
      } catch (IllegalArgumentException e) {
        exceptionList.add(e);
      }
    }

    if (result.isEmpty()) {
      if (exceptionList.get(0) instanceof UnsupportedCharsetException) {
        throw new UnsupportedCharsetException(exceptionList.get(0).getMessage());
      } else if (exceptionList.get(0) instanceof IllegalArgumentException) {
        throw new IllegalArgumentException(exceptionList.get(0).getMessage());
      }
    }
    for (Exception ex : exceptionList) {
      if (ex instanceof UnsupportedCharsetException) {
        continue;
      } else {
        throw new IllegalArgumentException(ex.getMessage());
      }
    }
    sort(result);
    
    return result;
  }
  
  public String getCharset() {
    return charset;
  }

  public Map<String, String> getParameters() {
    return Collections.unmodifiableMap(parameters);
  }

  public String getParameter(final String name) {
    return parameters.get(name.toLowerCase(Locale.ROOT));
  }

  public Float getQuality() {
    return quality;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(charset);
    for (final Map.Entry<String, String> entry : parameters.entrySet()) {
      result.append(';').append(entry.getKey()).append('=').append(entry.getValue());
    }

    return result.toString();
  }
  
  /**
   * Sorts given list of Accept charsets
   * according to their quality-parameter values and their specificity
   * as defined in RFC 2616, chapters 14.2.
   * @param toSort list which is sorted and hence re-arranged
   */
  private static void sort(List<AcceptCharset> toSort) {
    Collections.sort(toSort,
        new Comparator<AcceptCharset>() {
      @Override
      public int compare(final AcceptCharset a1, final AcceptCharset a2) {
        int compare = a2.getQuality().compareTo(a1.getQuality());
        if (compare != 0) {
          return compare;
        }
        return a2.getParameters().size() - a1.getParameters().size();
      }
    });
  }
}
