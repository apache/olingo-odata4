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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Internally used {@link AcceptType} for OData library.
 *
 * See RFC 7231, chapter 5.3.2:
 * <pre>
 * Accept = #( media-range [ accept-params ] )
 * media-range = ( "&#42;/&#42;"
 * / ( type "/" "&#42;" )
 * / ( type "/" subtype )
 * ) *( OWS ";" OWS parameter )
 * accept-params = weight *( accept-ext )
 * accept-ext = OWS ";" OWS token [ "=" ( token / quoted-string ) ]
 * weight = OWS ";" OWS "q=" qvalue
 * qvalue = ( "0" [ "." 0*3DIGIT ] ) / ( "1" [ "." 0*3("0") ] )
 * </pre>
 *
 * Once created an {@link AcceptType} is <b>IMMUTABLE</b>.
 */
public final class AcceptType {

  private static final Pattern Q_PATTERN = Pattern.compile("\\A(?:0(?:\\.\\d{0,3})?)|(?:1(?:\\.0{0,3})?)\\Z");

  private final String type;
  private final String subtype;
  private final Map<String, String> parameters;
  private final Float quality;

  private AcceptType(final String type, final String subtype, final Map<String, String> parameters,
      final Float quality) {
    this.type = type;
    this.subtype = subtype;
    this.parameters = TypeUtil.createParameterMap();
    this.parameters.putAll(parameters);
    this.quality = quality;
  }

  private AcceptType(final String type) {
    List<String> typeSubtype = new ArrayList<String>();
    parameters = TypeUtil.createParameterMap();

    parse(type, typeSubtype, parameters);
    this.type = typeSubtype.get(0);
    subtype = typeSubtype.get(1);

    if (TypeUtil.MEDIA_TYPE_WILDCARD.equals(this.type) && !TypeUtil.MEDIA_TYPE_WILDCARD.equals(subtype)) {
      throw new IllegalArgumentException("Illegal combination of WILDCARD type with NONE WILDCARD "
          + "subtype in accept header:" + type);
    }

    final String q = parameters.get(TypeUtil.PARAMETER_Q);
    if (q == null) {
      quality = 1F;
    } else if (Q_PATTERN.matcher(q).matches()) {
        quality = Float.valueOf(q);
    } else {
      throw new IllegalArgumentException("Illegal quality parameter '" + q + "' in accept header:" + type);
    }
  }

  private static void parse(final String format, final List<String> typeSubtype,
      final Map<String, String> parameters) {

    final String[] typesAndParameters = format.split(TypeUtil.PARAMETER_SEPARATOR, 2);
    final String types = typesAndParameters[0];
    final String params = (typesAndParameters.length > 1 ? typesAndParameters[1] : null);

    String[] tokens = types.split(TypeUtil.TYPE_SUBTYPE_SEPARATOR);
    if (tokens.length == 2) {
      if (tokens[0] == null || tokens[0].isEmpty()) {
        throw new IllegalArgumentException("No type found in format: '" + format + "'.");
      } else if (tokens[1] == null || tokens[1].isEmpty()) {
        throw new IllegalArgumentException("No subtype found in format: '" + format + "'.");
      } else {
        typeSubtype.add(tokens[0]);
        typeSubtype.add(tokens[1]);
      }
    } else {
      throw new IllegalArgumentException("Not exactly one '" + TypeUtil.TYPE_SUBTYPE_SEPARATOR +
          " at the beginning or at the end in format: " + format);
    }

    TypeUtil.parseParameters(params, parameters);
  }

  /**
   * Creates a list of {@link AcceptType} objects based on given input string.
   * @param acceptTypes accept types, comma-separated, as specified for the HTTP header <code>Accept</code>
   * @return a list of <code>AcceptType</code> objects
   * @throws IllegalArgumentException if input string is not parseable
   */
  public static List<AcceptType> create(final String acceptTypes) {
    if (acceptTypes == null) {
      throw new IllegalArgumentException("Type parameter MUST NOT be null.");
    }
    List<AcceptType> result = new ArrayList<AcceptType>();
    List<IllegalArgumentException> exceptionList = new ArrayList<IllegalArgumentException>();

    String[] values = acceptTypes.split(",");
    for (String value : values) {
      try {
        result.add(new AcceptType(value.trim()));
      } catch (IllegalArgumentException e) {
        exceptionList.add(e);
      }
    }

    if (!exceptionList.isEmpty() || result.isEmpty()) {
      throw exceptionList.get(0);
    }
    
    sort(result);

    return result;
  }

  /**
   * Creates a list of {@link AcceptType} objects based on given content type.
   * @param contentType the content type
   * @return an immutable one-element list of <code>AcceptType</code> objects that matches only the given content type
   */
  public static List<AcceptType> fromContentType(final ContentType contentType) {
    return Collections.singletonList(new AcceptType(
        contentType.getType(), contentType.getSubtype(), contentType.getParameters(), 1F));
  }

  public String getType() {
    return type;
  }

  public String getSubtype() {
    return subtype;
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
    result.append(type).append('/').append(subtype);
    for (final Map.Entry<String, String> entry : parameters.entrySet()) {
      result.append(';').append(entry.getKey()).append('=').append(entry.getValue());
    }

    return result.toString();
  }

  /**
   * <p>Determines whether this accept type matches a given content type.</p>
   * <p>A match is defined as fulfilling all of the following conditions:
   * <ul>
   * <li>the type must be '*' or equal to the content-type's type,</li>
   * <li>the subtype must be '*' or equal to the content-type's subtype,</li>
   * <li>all parameters must have the same value as in the content-type's parameter map.</li>
   * </ul></p>
   * @param contentType content type against which is matched
   * @return whether this accept type matches the given content type
   */
  public boolean matches(final ContentType contentType) {
    if (type.equals(TypeUtil.MEDIA_TYPE_WILDCARD)) {
      return true;
    }
    if (!type.equalsIgnoreCase(contentType.getType())) {
      return false;
    }
    if (subtype.equals(TypeUtil.MEDIA_TYPE_WILDCARD)) {
      return true;
    }
    if (!subtype.equalsIgnoreCase(contentType.getSubtype())) {
      return false;
    }
    Map<String, String> compareParameters = contentType.getParameters();
    for (final Map.Entry<String, String> entry : parameters.entrySet()) {
      if (entry.getKey().equalsIgnoreCase(ContentType.PARAMETER_CHARSET) && 
          compareParameters.containsKey(entry.getKey())) {
        continue;
      } else {
        if (compareParameters.containsKey(entry.getKey()) || TypeUtil.PARAMETER_Q.equalsIgnoreCase(entry.getKey())) {
          String compare = compareParameters.get(entry.getKey());
          if (!entry.getValue().equalsIgnoreCase(compare) && !TypeUtil.PARAMETER_Q.equalsIgnoreCase(entry.getKey())) {
            return false;
          }
        } else {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Sorts given list of Accept types
   * according to their quality-parameter values and their specificity
   * as defined in RFC 7231, chapters 3.1.1.1, 5.3.1, and 5.3.2.
   * @param toSort list which is sorted and hence re-arranged
   */
  private static void sort(List<AcceptType> toSort) {
    Collections.sort(toSort,
        new Comparator<AcceptType>() {
      @Override
      public int compare(final AcceptType a1, final AcceptType a2) {
        int compare = a2.getQuality().compareTo(a1.getQuality());
        if (compare != 0) {
          return compare;
        }
        compare = (a1.getType().equals(TypeUtil.MEDIA_TYPE_WILDCARD) ? 1 : 0)
            - (a2.getType().equals(TypeUtil.MEDIA_TYPE_WILDCARD) ? 1 : 0);
        if (compare != 0) {
          return compare;
        }
        compare = (a1.getSubtype().equals(TypeUtil.MEDIA_TYPE_WILDCARD) ? 1 : 0)
            - (a2.getSubtype().equals(TypeUtil.MEDIA_TYPE_WILDCARD) ? 1 : 0);
        if (compare != 0) {
          return compare;
        }
        return a2.getParameters().size() - a1.getParameters().size();
      }
    });
  }
}
