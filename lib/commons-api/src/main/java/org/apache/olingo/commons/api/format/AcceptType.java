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
package org.apache.olingo.commons.api.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
 * Once created a {@link AcceptType} is <b>IMMUTABLE</b>.
 */
public class AcceptType {

  public static final AcceptType WILDCARD = create(TypeUtil.MEDIA_TYPE_WILDCARD, TypeUtil.MEDIA_TYPE_WILDCARD,
      createParameterMap(), 1F);

  private final String type;
  private final String subtype;
  private final Map<String, String> parameters;
  private final Float quality;

  private AcceptType(final String type, final String subtype, final Map<String, String> parameters,
      final Float quality) {
    this.type = type;
    this.subtype = subtype;
    this.parameters = createParameterMap();
    this.parameters.putAll(parameters);
    this.quality = quality;
  }

  private static TreeMap<String, String> createParameterMap() {
    return new TreeMap<String, String>(new Comparator<String>() {
      @Override
      public int compare(final String o1, final String o2) {
        return o1.compareToIgnoreCase(o2);
      }
    });
  }

  private AcceptType(final String type) {
    if (type == null) {
      throw new IllegalArgumentException("Type parameter MUST NOT be null.");
    }
    List<String> typeSubtype = new ArrayList<String>();
    parameters = createParameterMap();
    parse(type, typeSubtype, parameters);
    this.type = typeSubtype.get(0);
    subtype = typeSubtype.get(1);
    if (TypeUtil.MEDIA_TYPE_WILDCARD.equals(this.type) && !TypeUtil.MEDIA_TYPE_WILDCARD.equals(subtype)) {
      throw new IllegalArgumentException("Illegal combination of WILDCARD type with NONE WILDCARD subtype.");
    }
    final String q = parameters.get(TypeUtil.PARAMETER_Q);
    if (q == null) {
      quality = 1F;
    } else {
      try {
        quality = Float.valueOf(q);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Illegal quality parameter.", e);
      }
    }
  }

  private static void
      parse(final String format, final List<String> typeSubtype, final Map<String, String> parameters) {
    final String[] typesAndParameters = format.split(TypeUtil.PARAMETER_SEPARATOR, 2);
    final String types = typesAndParameters[0];
    final String params = (typesAndParameters.length > 1 ? typesAndParameters[1] : null);

    String[] tokens = types.split(TypeUtil.TYPE_SUBTYPE_SEPARATOR);
    if (tokens.length == 1) {
      typeSubtype.add(tokens[0]);
      typeSubtype.add(TypeUtil.MEDIA_TYPE_WILDCARD);
    } else if (tokens.length == 2) {
      if (tokens[0] == null || tokens[0].isEmpty()) {
        throw new IllegalArgumentException("No type found in format '" + format + "'.");
      } else if (tokens[1] == null || tokens[1].isEmpty()) {
        throw new IllegalArgumentException("No subtype found in format '" + format + "'.");
      } else {
        typeSubtype.add(tokens[0]);
        typeSubtype.add(tokens[1]);
      }
    } else {
      throw new IllegalArgumentException("Too many '" + TypeUtil.TYPE_SUBTYPE_SEPARATOR + "' in format '" + format
          + "'.");
    }

    TypeUtil.parseParameters(params, parameters);
  }

  /**
   * Creates an accept type.
   * @param type
   * @param subtype
   * @param parameters
   * @param quality
   * @return a new <code>AcceptType</code> object
   */
  public static AcceptType create(final String type, final String subtype, final Map<String, String> parameters,
      final Float quality) {
    return new AcceptType(type, subtype, parameters, quality);
  }

  public static AcceptType create(final ContentType contentType) {
    return create(contentType.getType(), contentType.getSubtype(), contentType.getParameters(), 1F);
  }

  /**
   * Create an {@link AcceptType} based on given input string (<code>format</code>).
   * @param format
   * @return a new <code>AcceptType</code> object
   * @throws IllegalArgumentException if input string is not parseable
   */
  public static List<AcceptType> create(final String format) {
    List<AcceptType> result = new ArrayList<AcceptType>();

    String[] values = format.split(",");
    for (String value : values) {
      result.add(new AcceptType(value.trim()));
    }

    return result;
  }

  /**
   * Parses the given input string (<code>format</code>) and returns created {@link AcceptType} if input was valid or
   * return <code>NULL</code> if input was not parseable.
   * @param format
   * @return a new <code>ContentType</code> object
   */
  public static List<AcceptType> parse(final String format) {
    try {
      return AcceptType.create(format);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  public String getType() {
    return type;
  }

  public String getSubtype() {
    return subtype;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public Float getQuality() {
    return quality;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(type).append('/').append(subtype);
    for (final String key : parameters.keySet()) {
      result.append(';').append(key).append('=').append(parameters.get(key));
    }
    if (quality < 1F) {
      result.append(';').append(TypeUtil.PARAMETER_Q).append('=').append(quality);
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
   * @param contentType
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
    for (final String key : parameters.keySet()) {
      if (compareParameters.containsKey(key)) {
        if (!parameters.get(key).equalsIgnoreCase(compareParameters.get(key))) {
          return false;
        }
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Create a list of {@link AcceptType} based on given input strings (<code>contentTypes</code>).
   * 
   * If one of the given strings can not be parsed an exception is thrown (hence no list is returned with the parseable
   * strings).
   * @param acceptTypeStrings a list of strings
   * @return a list of new <code>AcceptType</code> objects
   * @throws IllegalArgumentException if one of the given input string is not parseable this exceptions is thrown
   */
  public static List<AcceptType> create(final List<String> acceptTypeStrings) {
    List<AcceptType> acceptTypes = new ArrayList<AcceptType>(acceptTypeStrings.size());
    for (String contentTypeString : acceptTypeStrings) {
      acceptTypes.addAll(create(contentTypeString));
    }
    return acceptTypes;
  }

  /**
   * Sort given list of Accept types
   * according to their quality-parameter values and their specificity
   * as defined in RFC 7231, chapters 3.1.1.1, 5.3.1, and 5.3.2.
   * @param toSort list which is sorted and hence re-arranged
   */
  public static void sort(final List<AcceptType> toSort) {
    Collections.sort(toSort,
        new Comparator<AcceptType>() {
          @Override
          public int compare(final AcceptType a1, final AcceptType a2) {
            int compare = a1.getQuality().compareTo(a2.getQuality());
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
