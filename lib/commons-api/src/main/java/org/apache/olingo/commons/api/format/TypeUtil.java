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

import java.util.Locale;
import java.util.Map;

class TypeUtil {

  static final String MEDIA_TYPE_WILDCARD = "*";
  static final String PARAMETER_Q = "q";

  static final char WHITESPACE_CHAR = ' ';
  static final String PARAMETER_SEPARATOR = ";";
  static final String PARAMETER_KEY_VALUE_SEPARATOR = "=";
  static final String TYPE_SUBTYPE_SEPARATOR = "/";
  static final String TYPE_SUBTYPE_WILDCARD = "*";

  /**
   * Valid input are <code>;</code> separated <code>key=value</code> pairs
   * without spaces between key and value.
   * <p>
   * See RFC 7231:
   * The type, subtype, and parameter name tokens are case-insensitive.
   * Parameter values might or might not be case-sensitive, depending on
   * the semantics of the parameter name. The presence or absence of a
   * parameter might be significant to the processing of a media-type,
   * depending on its definition within the media type registry.
   * </p>
   * 
   * @param parameters
   * @param parameterMap
   */
  static void parseParameters(final String parameters, final Map<String, String> parameterMap) {
    if (parameters != null) {
      String[] splittedParameters = parameters.split(TypeUtil.PARAMETER_SEPARATOR);
      for (String parameter : splittedParameters) {
        String[] keyValue = parameter.split(TypeUtil.PARAMETER_KEY_VALUE_SEPARATOR);
        String key = keyValue[0].trim().toLowerCase(Locale.ENGLISH);
        String value = keyValue.length > 1 ? keyValue[1] : null;
        if (value != null && Character.isWhitespace(value.charAt(0))) {
          throw new IllegalArgumentException(
              "Value of parameter '" + key + "' starts with whitespace ('" + parameters + "').");
        }
        parameterMap.put(key, value);
      }
    }
  }

}
