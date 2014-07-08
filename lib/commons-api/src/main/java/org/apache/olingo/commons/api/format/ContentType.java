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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Internally used {@link ContentType} for OData library.
 * 
 * For more details on format and content of a {@link ContentType} see
 * <em>Media Type</em> format as defined in
 * <a href="http://www.ietf.org/rfc/rfc7231.txt">RFC 7231</a>, chapter 3.1.1.1.
 * <pre>
 * media-type = type "/" subtype *( OWS ";" OWS parameter )
 * type = token
 * subtype = token
 * OWS = *( SP / HTAB ) ; optional whitespace
 * </pre>
 * 
 * Once created a {@link ContentType} is <b>IMMUTABLE</b>.
 */
public class ContentType {

  public static final ContentType APPLICATION_XML = create("application", "xml");
  public static final ContentType APPLICATION_XML_CS_UTF_8 = create(APPLICATION_XML, TypeUtil.PARAMETER_CHARSET,
      TypeUtil.CHARSET_UTF_8);
  public static final ContentType APPLICATION_ATOM_XML = create("application", "atom+xml");
  public static final ContentType APPLICATION_ATOM_XML_CS_UTF_8 = create(APPLICATION_ATOM_XML,
      TypeUtil.PARAMETER_CHARSET, TypeUtil.CHARSET_UTF_8);
  public static final ContentType APPLICATION_ATOM_XML_ENTRY =
      create(APPLICATION_ATOM_XML, TypeUtil.PARAMETER_TYPE, "entry");
  public static final ContentType APPLICATION_ATOM_XML_ENTRY_CS_UTF_8 = create(APPLICATION_ATOM_XML_ENTRY,
      TypeUtil.PARAMETER_CHARSET, TypeUtil.CHARSET_UTF_8);
  public static final ContentType APPLICATION_ATOM_XML_FEED =
      create(APPLICATION_ATOM_XML, TypeUtil.PARAMETER_TYPE, "feed");
  public static final ContentType APPLICATION_ATOM_XML_FEED_CS_UTF_8 = create(APPLICATION_ATOM_XML_FEED,
      TypeUtil.PARAMETER_CHARSET, TypeUtil.CHARSET_UTF_8);
  public static final ContentType APPLICATION_ATOM_SVC = create("application", "atomsvc+xml");
  public static final ContentType APPLICATION_ATOM_SVC_CS_UTF_8 = create(APPLICATION_ATOM_SVC,

      TypeUtil.PARAMETER_CHARSET, TypeUtil.CHARSET_UTF_8);
  public static final ContentType APPLICATION_JSON = create("application", "json");
  public static final ContentType APPLICATION_JSON_CS_UTF_8 = create(APPLICATION_JSON,
      TypeUtil.PARAMETER_CHARSET, TypeUtil.CHARSET_UTF_8);

  public static final ContentType APPLICATION_JSON_MIN = create("application/json;odata.metadata=minimal");
  public static final ContentType APPLICATION_JSON_MIN_CS_UTF_8 =
      create("application/json;odata.metadata=minimal;charset=UTF-8");

  public static final ContentType APPLICATION_OCTET_STREAM = create("application", "octet-stream");
  public static final ContentType TEXT_PLAIN = create("text", "plain");
  public static final ContentType TEXT_PLAIN_CS_UTF_8 =
      create(TEXT_PLAIN, TypeUtil.PARAMETER_CHARSET, TypeUtil.CHARSET_UTF_8);
  public static final ContentType MULTIPART_MIXED = create("multipart", "mixed");

  public static final ContentType APPLICATION_XHTML_XML = create("application", "xhtml+xml");
  public static final ContentType APPLICATION_SVG_XML = create("application", "svg+xml");
  public static final ContentType APPLICATION_FORM_URLENCODED = create("application", "x-www-form-urlencoded");
  public static final ContentType MULTIPART_FORM_DATA = create("multipart", "form-data");
  public static final ContentType TEXT_XML = create("text", "xml");
  public static final ContentType TEXT_HTML = create("text", "html");

  private final String type;
  private final String subtype;
  private final Map<String, String> parameters;

  /**
   * Creates a content type from type, subtype, and parameters.
   * @param type
   * @param subtype
   * @param parameters
   */
  private ContentType(final String type, final String subtype, final Map<String, String> parameters) {
    this.type = validateType(type);
    this.subtype = validateType(subtype);

    if (parameters == null) {
      this.parameters = Collections.emptyMap();
    } else {
      this.parameters = new TreeMap<String, String>(new Comparator<String>() {
        @Override
        public int compare(final String o1, final String o2) {
          return o1.compareToIgnoreCase(o2);
        }
      });
      this.parameters.putAll(parameters);
    }
  }

  private String validateType(final String type) {
    if (type == null || type.isEmpty() || "*".equals(type)) {
      throw new IllegalArgumentException("Illegal type '" + type + "'.");
    }
    int len = type.length();
    for (int i = 0; i < len; i++) {
      if (type.charAt(i) == TypeUtil.WHITESPACE_CHAR) {
        throw new IllegalArgumentException("Illegal whitespace found for type '" + type + "'.");
      }
    }
    return type;
  }

  /**
   * Validates if given <code>format</code> is parseable and can be used as input for {@link #create(String)} method.
   * @param format to be validated string
   * @return <code>true</code> if format is parseable otherwise <code>false</code>
   */
  public static boolean isParseable(final String format) {
    try {
      return ContentType.create(format) != null;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Creates a content type from type and subtype.
   * @param type
   * @param subtype
   * @return a new <code>ContentType</code> object
   */
  public static ContentType create(final String type, final String subtype) {
    return new ContentType(type, subtype, null);
  }

  /**
   * 
   * @param contentType
   * @param parameterKey
   * @param parameterValue
   * @return a new <code>ContentType</code> object
   */
  public static ContentType create(final ContentType contentType,
      final String parameterKey, final String parameterValue) {
    ContentType newContentType = new ContentType(contentType.type, contentType.subtype, contentType.parameters);
    newContentType.parameters.put(parameterKey, parameterValue);
    return newContentType;
  }

  /**
   * Creates a {@link ContentType} based on given input string (<code>format</code>).
   * Supported format is <code>Media Type</code> format as defined in RFC 7231, chapter 3.1.1.1.
   * @param format a string in format as defined in RFC 7231, chapter 3.1.1.1
   * @return a new <code>ContentType</code> object
   * @throws IllegalArgumentException if input string is not parseable
   */
  public static ContentType create(final String format) {
    if (format == null) {
      throw new IllegalArgumentException("Parameter format MUST NOT be NULL.");
    }
    List<String> typeSubtype = new ArrayList<String>();
    Map<String, String> parameters = new HashMap<String, String>();
    parse(format, typeSubtype, parameters);
    return new ContentType(typeSubtype.get(0), typeSubtype.get(1), parameters);
  }

  /**
   * Parses the given input string (<code>format</code>) and returns created {@link ContentType} if input was valid or
   * return <code>NULL</code> if input was not parseable.
   * 
   * For the definition of the supported format see {@link #create(String)}.
   * 
   * @param format a string in format as defined in RFC 7231, chapter 3.1.1.1
   * @return a new <code>ContentType</code> object
   */
  public static ContentType parse(final String format) {
    try {
      return ContentType.create(format);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private static void
      parse(final String format, final List<String> typeSubtype, final Map<String, String> parameters) {
    final String[] typesAndParameters = format.split(TypeUtil.PARAMETER_SEPARATOR, 2);
    final String types = typesAndParameters[0];
    final String params = (typesAndParameters.length > 1 ? typesAndParameters[1] : null);

    if (types.contains(TypeUtil.TYPE_SUBTYPE_SEPARATOR)) {
      String[] tokens = types.split(TypeUtil.TYPE_SUBTYPE_SEPARATOR);
      if (tokens.length == 2) {
        if (tokens[0] == null || tokens[0].isEmpty()) {
          throw new IllegalArgumentException("No type found in format '" + format + "'.");
        } else if (tokens[1] == null || tokens[1].isEmpty()) {
          throw new IllegalArgumentException("No subtype found in format '" + format + "'.");
        } else {
          typeSubtype.add(tokens[0]);
          typeSubtype.add(tokens[1]);
        }
      } else {
        throw new IllegalArgumentException(
            "Too many '" + TypeUtil.TYPE_SUBTYPE_SEPARATOR + "' in format '" + format + "'.");
      }
    } else {
      throw new IllegalArgumentException("No separator '" + TypeUtil.TYPE_SUBTYPE_SEPARATOR
          + "' was found in format '" + format + "'.");
    }

    TypeUtil.parseParameters(params, parameters);
  }

  public String getType() {
    return type;
  }

  public String getSubtype() {
    return subtype;
  }

  /**
   * 
   * @return parameters of this {@link ContentType} as unmodifiable map.
   */
  public Map<String, String> getParameters() {
    return Collections.unmodifiableMap(parameters);
  }

  @Override
  public int hashCode() {
    return 1;
  }

  /**
   * {@link ContentType}s are equal
   * <ul>
   * <li>if <code>type</code>, <code>subtype</code> and all <code>parameters</code> have the same value.</li>
   * <li>if <code>type</code> and/or <code>subtype</code> is set to "*" (in such a case the <code>parameters</code> are
   * ignored).</li>
   * </ul>
   * 
   * @return <code>true</code> if both instances are equal (see definition above), otherwise <code>false</code>.
   */
  @Override
  public boolean equals(final Object obj) {
    // NULL validation is done in method 'isEqualWithoutParameters(obj)'
    Boolean compatible = isEqualWithoutParameters(obj);

    if (compatible == null) {
      ContentType other = (ContentType) obj;

      // parameter checks
      if (parameters == null) {
        if (other.parameters != null) {
          return false;
        }
      } else if (parameters.size() == other.parameters.size()) {
        Iterator<Entry<String, String>> entries = parameters.entrySet().iterator();
        Iterator<Entry<String, String>> otherEntries = other.parameters.entrySet().iterator();
        while (entries.hasNext()) {
          Entry<String, String> e = entries.next();
          Entry<String, String> oe = otherEntries.next();

          if (!areEqual(e.getKey(), oe.getKey())) {
            return false;
          }
          if (!areEqual(e.getValue(), oe.getValue())) {
            return false;
          }
        }
      } else {
        return false;
      }
      return true;
    } else {
      // all tests run
      return compatible.booleanValue();
    }
  }

  /**
   * {@link ContentType}s are <b>compatible</b>
   * <ul>
   * <li>if <code>type</code>, <code>subtype</code> have the same value.</li>
   * <li>if <code>type</code> and/or <code>subtype</code> is set to "*"</li>
   * </ul>
   * The set <code>parameters</code> are <b>always</b> ignored (for compare with parameters see {@link #equals(Object)}
   * ).
   * 
   * @return <code>true</code> if both instances are equal (see definition above), otherwise <code>false</code>.
   */
  public boolean isCompatible(final ContentType obj) {
    Boolean compatible = isEqualWithoutParameters(obj);
    if (compatible == null) {
      return true;
    }
    return compatible.booleanValue();
  }

  /**
   * Check equal without parameters.
   * It is possible that no decision about <code>equal/none equal</code> can be determined a <code>NULL</code> is
   * returned.
   * 
   * @param obj to checked object
   * @return <code>true</code> if both instances are equal (see definition above), otherwise <code>false</code>
   * or <code>NULL</code> if no decision about <code>equal/none equal</code> could be determined.
   */
  private Boolean isEqualWithoutParameters(final Object obj) {
    // basic checks
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    ContentType other = (ContentType) obj;

    // subtype checks
    if (subtype == null) {
      if (other.subtype != null) {
        return false;
      }
    } else if (!subtype.equals(other.subtype)) {
      return false;
    }

    // type checks
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      return false;
    }

    return null;
  }

  /**
   * Check whether both string are equal ignoring the case of the strings.
   * 
   * @param first first string
   * @param second second string
   * @return <code>true</code> if both strings are equal (by ignoring the case), otherwise <code>false</code> is
   * returned
   */
  private static boolean areEqual(final String first, final String second) {
    return first == null && second == null || first.equalsIgnoreCase(second);
  }

  /**
   * Get {@link ContentType} as string as defined in RFC 7231
   * (http://www.ietf.org/rfc/rfc7231.txt, chapter 3.1.1.1: Media Type)
   * @return string representation of <code>ContentType</code> object
   */
  public String toContentTypeString() {
    StringBuilder sb = new StringBuilder();

    sb.append(type).append(TypeUtil.TYPE_SUBTYPE_SEPARATOR).append(subtype);

    for (String key : parameters.keySet()) {
      sb.append(";").append(key).append("=").append(parameters.get(key));
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return toContentTypeString();
  }
}
