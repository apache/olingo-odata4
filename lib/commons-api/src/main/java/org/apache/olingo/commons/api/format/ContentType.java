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
 * parameter = token "=" ( token / quoted-string )
 * </pre>
 *
 * Once created a {@link ContentType} is <b>IMMUTABLE</b>.
 */
public final class ContentType {

  private static final String APPLICATION = "application";
  private static final String TEXT = "text";
  private static final String MULTIPART = "multipart";

  public static final ContentType APPLICATION_XML = new ContentType(APPLICATION, "xml", null);
  public static final ContentType APPLICATION_ATOM_XML = new ContentType(APPLICATION, "atom+xml", null);
  public static final ContentType APPLICATION_ATOM_XML_ENTRY = create(APPLICATION_ATOM_XML, "type=entry");
  public static final ContentType APPLICATION_ATOM_XML_FEED = create(APPLICATION_ATOM_XML, "type=feed");
  public static final ContentType APPLICATION_ATOM_SVC = new ContentType(APPLICATION, "atomsvc+xml", null);

  public static final ContentType APPLICATION_JSON = new ContentType(APPLICATION, "json", null);

  public static final ContentType APPLICATION_OCTET_STREAM = new ContentType(APPLICATION, "octet-stream", null);

  public static final ContentType APPLICATION_XHTML_XML = new ContentType(APPLICATION, "xhtml+xml", null);
  public static final ContentType TEXT_HTML = new ContentType(TEXT, "html", null);
  public static final ContentType TEXT_XML = new ContentType(TEXT, "xml", null);
  public static final ContentType TEXT_PLAIN = new ContentType(TEXT, "plain", null);

  public static final ContentType APPLICATION_SVG_XML = new ContentType(APPLICATION, "svg+xml", null);

  public static final ContentType APPLICATION_FORM_URLENCODED =
      new ContentType(APPLICATION, "x-www-form-urlencoded", null);

  public static final ContentType MULTIPART_MIXED = new ContentType(MULTIPART, "mixed", null);

  public static final ContentType MULTIPART_FORM_DATA = new ContentType(MULTIPART, "form-data", null);

  public static final String PARAMETER_CHARSET_UTF8 = "charset=utf-8";

  private final String type;
  private final String subtype;
  private final Map<String, String> parameters;

  /**
   * Creates a content type from type, subtype, and parameters.
   *
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
    if (type.indexOf(TypeUtil.WHITESPACE_CHAR) >= 0) {
      throw new IllegalArgumentException("Illegal whitespace found for type '" + type + "'.");
    }
    return type;
  }

  /**
   * Validates if given <code>format</code> is parseable and can be used as input for {@link #create(String)} method.
   *
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
   * Creates a content type from format and key-value pairs for parameters.
   *
   * @param format for example "application/json"
   * @param parameters for example "a=b", "c=d"
   * @return a new <code>ContentType</code> object
   */
  public static ContentType create(final String format, final String... parameters) {
    ContentType ct = parse(format);

    for (String p : parameters) {
      final String[] keyvalue = TypeUtil.parseParameter(p);
      ct.parameters.put(keyvalue[0], keyvalue[1]);
    }

    return ct;
  }

  /**
   * Creates a content type from an existing content type and additional key-value pairs for parameters.
   *
   * @param contentType for example "application/json"
   * @param parameters for example "a=b", "c=d"
   * @return a new <code>ContentType</code> object
   */
  public static ContentType create(final ContentType contentType, final String... parameters) {
    ContentType ct = new ContentType(contentType.type, contentType.subtype, contentType.parameters);

    for (String p : parameters) {
      final String[] keyvalue = TypeUtil.parseParameter(p);
      ct.parameters.put(keyvalue[0], keyvalue[1]);
    }

    return ct;
  }

  /**
   * Creates a {@link ContentType} based on given input string (<code>format</code>). Supported format is
   * <code>Media Type</code> format as defined in RFC 7231, chapter 3.1.1.1.
   *
   * @param format a string in format as defined in RFC 7231, chapter 3.1.1.1
   * @return a new <code>ContentType</code> object
   * @throws IllegalArgumentException if input string is not parseable
   */
  public static ContentType create(final String format) {
    if (format == null) {
      throw new IllegalArgumentException("Parameter format MUST NOT be NULL.");
    }
    final List<String> typeSubtype = new ArrayList<String>();
    final Map<String, String> parameters = new HashMap<String, String>();
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

  private static void parse(final String format, List<String> typeSubtype, Map<String, String> parameters) {
    final String[] typesAndParameters = format.split(TypeUtil.PARAMETER_SEPARATOR, 2);
    final String types = typesAndParameters[0];
    final String params = (typesAndParameters.length > 1 ? typesAndParameters[1] : null);

    if (types.contains(TypeUtil.TYPE_SUBTYPE_SEPARATOR)) {
      final String[] tokens = types.split(TypeUtil.TYPE_SUBTYPE_SEPARATOR);
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
   * {@link ContentType}s are equal if <code>type</code>, <code>subtype</code>, and all <code>parameters</code>
   * have the same value.
   */
  @Override
  public boolean equals(final Object obj) {
    // basic checks
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    final ContentType other = (ContentType) obj;

    // type/subtype checks
    if (!isCompatible(other)) {
      return false;
    }

    // parameter checks
    if (parameters.size() == other.parameters.size()) {
      final Iterator<Entry<String, String>> entries = parameters.entrySet().iterator();
      final Iterator<Entry<String, String>> otherEntries = other.parameters.entrySet().iterator();
      while (entries.hasNext()) {
        final Entry<String, String> e = entries.next();
        final Entry<String, String> oe = otherEntries.next();
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
  }

  /**
   * <p>{@link ContentType}s are <b>compatible</b>
   * if <code>type</code> and <code>subtype</code> have the same value.</p>
   * <p>The set <code>parameters</code> are <b>always</b> ignored
   * (for compare with parameters see {@link #equals(Object)}).</p>
   * @return <code>true</code> if both instances are compatible (see definition above), otherwise <code>false</code>.
   */
  public boolean isCompatible(final ContentType other) {
    return type.equalsIgnoreCase(other.type) && subtype.equalsIgnoreCase(other.subtype);
  }

  /**
   * Checks whether both strings are equal ignoring the case of the strings.
   *
   * @param first first string
   * @param second second string
   * @return <code>true</code> if both strings are equal (ignoring the case), otherwise <code>false</code>
   */
  private static boolean areEqual(final String first, final String second) {
    return first == null && second == null || first.equalsIgnoreCase(second);
  }

  /**
   * Gets {@link ContentType} as string as defined in
   * <a href="http://www.ietf.org/rfc/rfc7231.txt">RFC 7231</a>, chapter 3.1.1.1:
   * Media Type.
   *
   * @return string representation of <code>ContentType</code> object
   */
  public String toContentTypeString() {
    final StringBuilder sb = new StringBuilder();

    sb.append(type).append(TypeUtil.TYPE_SUBTYPE_SEPARATOR).append(subtype);

    for (String key : parameters.keySet()) {
      sb.append(TypeUtil.PARAMETER_SEPARATOR).append(key)
          .append(TypeUtil.PARAMETER_KEY_VALUE_SEPARATOR).append(parameters.get(key));
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return toContentTypeString();
  }
}
