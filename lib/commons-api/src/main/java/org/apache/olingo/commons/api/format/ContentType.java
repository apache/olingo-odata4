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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

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

  public static final String PARAMETER_CHARSET = "charset";
  public static final String PARAMETER_IEEE754_COMPATIBLE = "IEEE754Compatible";
  public static final String PARAMETER_ODATA_METADATA = "odata.metadata";

  public static final String VALUE_ODATA_METADATA_NONE = "none";
  public static final String VALUE_ODATA_METADATA_MINIMAL = "minimal";
  public static final String VALUE_ODATA_METADATA_FULL = "full";

  public static final ContentType APPLICATION_JSON = new ContentType(APPLICATION, "json", null);
  public static final ContentType JSON = create(ContentType.APPLICATION_JSON,
      PARAMETER_ODATA_METADATA, VALUE_ODATA_METADATA_MINIMAL);
  public static final ContentType JSON_NO_METADATA = create(ContentType.APPLICATION_JSON,
      PARAMETER_ODATA_METADATA, VALUE_ODATA_METADATA_NONE);
  public static final ContentType JSON_FULL_METADATA = create(ContentType.APPLICATION_JSON,
      PARAMETER_ODATA_METADATA, VALUE_ODATA_METADATA_FULL);

  public static final ContentType APPLICATION_XML = new ContentType(APPLICATION, "xml", null);
  public static final ContentType APPLICATION_ATOM_XML = new ContentType(APPLICATION, "atom+xml", null);
  public static final ContentType APPLICATION_ATOM_XML_ENTRY = create(APPLICATION_ATOM_XML, "type", "entry");
  public static final ContentType APPLICATION_ATOM_XML_ENTRY_UTF8 = create(APPLICATION_ATOM_XML_ENTRY,
      PARAMETER_CHARSET, "utf-8");
  public static final ContentType APPLICATION_ATOM_XML_FEED = create(APPLICATION_ATOM_XML, "type", "feed");
  public static final ContentType APPLICATION_ATOM_XML_FEED_UTF8 = create(APPLICATION_ATOM_XML_FEED,
      PARAMETER_CHARSET, "utf-8");
  public static final ContentType APPLICATION_ATOM_SVC = new ContentType(APPLICATION, "atomsvc+xml", null);

  public static final ContentType APPLICATION_OCTET_STREAM = new ContentType(APPLICATION, "octet-stream", null);

  public static final ContentType APPLICATION_XHTML_XML = new ContentType(APPLICATION, "xhtml+xml", null);
  public static final ContentType TEXT_HTML = new ContentType(TEXT, "html", null);
  public static final ContentType TEXT_XML = new ContentType(TEXT, "xml", null);
  public static final ContentType TEXT_PLAIN = new ContentType(TEXT, "plain", null);

  public static final ContentType APPLICATION_SVG_XML = new ContentType(APPLICATION, "svg+xml", null);

  public static final ContentType APPLICATION_FORM_URLENCODED =
      new ContentType(APPLICATION, "x-www-form-urlencoded", null);

  public static final ContentType APPLICATION_HTTP = new ContentType(APPLICATION, "http", null);

  public static final ContentType MULTIPART_MIXED = new ContentType(MULTIPART, "mixed", null);
  public static final ContentType MULTIPART_FORM_DATA = new ContentType(MULTIPART, "form-data", null);

  private final String type;
  private final String subtype;
  private final Map<String, String> parameters;

  /**
   * Creates a content type from type, subtype, and parameters.
   * @param type       type
   * @param subtype    subtype
   * @param parameters parameters as map from names to values
   */
  private ContentType(final String type, final String subtype, final Map<String, String> parameters) {
    this.type = validateType(type);
    this.subtype = validateType(subtype);

    if (parameters == null) {
      this.parameters = Collections.emptyMap();
    } else {
      this.parameters = TypeUtil.createParameterMap();
      this.parameters.putAll(parameters);
    }
  }

  private String validateType(final String type) throws IllegalArgumentException {
    if (type == null || type.isEmpty() || "*".equals(type)) {
      throw new IllegalArgumentException("Illegal type '" + type + "'.");
    }
    if (type.indexOf(TypeUtil.WHITESPACE_CHAR) >= 0) {
      throw new IllegalArgumentException("Illegal whitespace found for type '" + type + "'.");
    }
    return type;
  }

  /**
   * Creates a content type from an existing content type and an additional parameter as key-value pair.
   * @param contentType    an existing content type
   * @param parameterName  the name of the additional parameter
   * @param parameterValue the value of the additional parameter
   * @return a new {@link ContentType} object
   */
  public static ContentType create(final ContentType contentType,
      final String parameterName, final String parameterValue) throws IllegalArgumentException {
    TypeUtil.validateParameterNameAndValue(parameterName, parameterValue);

    ContentType type = new ContentType(contentType.type, contentType.subtype, contentType.parameters);
    type.parameters.put(parameterName.toLowerCase(Locale.ROOT), parameterValue);
    return type;
  }

  /**
   * Creates a {@link ContentType} based on given input string (<code>format</code>). Supported format is
   * <code>Media Type</code> format as defined in RFC 7231, chapter 3.1.1.1.
   *
   * @param format a string in format as defined in RFC 7231, chapter 3.1.1.1
   * @return a new {@link ContentType} object
   * @throws IllegalArgumentException if input string is not parseable
   */
  public static ContentType create(final String format) throws IllegalArgumentException {
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

  private static void parse(final String format, List<String> typeSubtype, Map<String, String> parameters)
      throws IllegalArgumentException {
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

  /** Gets the type of this content type. */
  public String getType() {
    return type;
  }

  /** Gets the subtype of this content type. */
  public String getSubtype() {
    return subtype;
  }

  /**
   * Gets the parameters of this content type.
   * @return parameters of this {@link ContentType} as unmodifiable map
   */
  public Map<String, String> getParameters() {
    return Collections.unmodifiableMap(parameters);
  }

  /**
   * Returns the value of a given parameter.
   * If the parameter does not exist the method returns null.
   * @param name the name of the parameter to get (case-insensitive)
   * @return the value of the parameter or <code>null</code> if the parameter is not present
   */
  public String getParameter(final String name) {
    return parameters.get(name.toLowerCase(Locale.ROOT));
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
        if (!areEqual(e.getKey(), oe.getKey())
            || !areEqual(e.getValue(), oe.getValue())) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
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
   * @param first first string
   * @param second second string
   * @return <code>true</code> if both strings are equal (ignoring the case), otherwise <code>false</code>
   */
  private static boolean areEqual(final String first, final String second) {
    return first == null && second == null || (first != null && first.equalsIgnoreCase(second));
  }

  /**
   * Gets {@link ContentType} as string as defined in
   * <a href="http://www.ietf.org/rfc/rfc7231.txt">RFC 7231</a>, chapter 3.1.1.1: Media Type.
   * @return string representation of {@link ContentType} object
   */
  public String toContentTypeString() {
    final StringBuilder sb = new StringBuilder();

    sb.append(type).append(TypeUtil.TYPE_SUBTYPE_SEPARATOR).append(subtype);

    for (Entry<String, String> entry : parameters.entrySet()) {
      sb.append(TypeUtil.PARAMETER_SEPARATOR).append(entry.getKey())
          .append(TypeUtil.PARAMETER_KEY_VALUE_SEPARATOR).append(entry.getValue());
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return toContentTypeString();
  }
}
