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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

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
 * OWS = *( SP / HTAB )  ; optional whitespace
 * </pre>
 *
 * Especially for <code>Accept</code> Header as defined in
 * RFC 7231, chapter 5.3.2:
 * <pre>
 * Accept = #( media-range [ accept-params ] )
 * media-range = ( "&#42;/&#42;"
 *               / ( type "/" "&#42;" )
 *               / ( type "/" subtype )
 *               ) *( OWS ";" OWS parameter )
 * accept-params  = weight *( accept-ext )
 * accept-ext = OWS ";" OWS token [ "=" ( token / quoted-string ) ]
 * weight = OWS ";" OWS "q=" qvalue
 * qvalue = ( "0" [ "." 0*3DIGIT ] ) / ( "1" [ "." 0*3("0") ] )
 * </pre>
 *
 * Once created a {@link ContentType} is <b>IMMUTABLE</b>.
 */
public class ContentType {

  private static final Comparator<String> Q_PARAMETER_COMPARATOR = new Comparator<String>() {
    @Override
    public int compare(final String o1, final String o2) {
      Float f1 = parseQParameterValue(o1);
      Float f2 = parseQParameterValue(o2);
      return f2.compareTo(f1);
    }
  };

  private static final char WHITESPACE_CHAR = ' ';
  private static final String PARAMETER_SEPARATOR = ";";
  private static final String PARAMETER_KEY_VALUE_SEPARATOR = "=";
  private static final String TYPE_SUBTYPE_SEPARATOR = "/";
  private static final String MEDIA_TYPE_WILDCARD = "*";

  public static final String PARAMETER_Q = "q";
  public static final String PARAMETER_TYPE = "type";
  public static final String PARAMETER_CHARSET = "charset";
  public static final String CHARSET_UTF_8 = "UTF-8";

  private static final Pattern Q_PARAMETER_VALUE_PATTERN = Pattern.compile("1|0|1\\.0{1,3}|0\\.\\d{1,3}");

  public static final ContentType WILDCARD = create(MEDIA_TYPE_WILDCARD, MEDIA_TYPE_WILDCARD);

  public static final ContentType APPLICATION_XHTML_XML = create("application", "xhtml+xml");
  public static final ContentType APPLICATION_SVG_XML = create("application", "svg+xml");
  public static final ContentType APPLICATION_FORM_URLENCODED = create("application", "x-www-form-urlencoded");
  public static final ContentType MULTIPART_FORM_DATA = create("multipart", "form-data");
  public static final ContentType TEXT_XML = create("text", "xml");
  public static final ContentType TEXT_HTML = create("text", "html");

  public static final ContentType APPLICATION_XML = create("application", "xml");
  public static final ContentType APPLICATION_XML_CS_UTF_8 = create(APPLICATION_XML, PARAMETER_CHARSET,
          CHARSET_UTF_8);
  public static final ContentType APPLICATION_ATOM_XML = create("application", "atom+xml");
  public static final ContentType APPLICATION_ATOM_XML_CS_UTF_8 = create(APPLICATION_ATOM_XML,
          PARAMETER_CHARSET, CHARSET_UTF_8);
  public static final ContentType APPLICATION_ATOM_XML_ENTRY = create(APPLICATION_ATOM_XML, PARAMETER_TYPE, "entry");
  public static final ContentType APPLICATION_ATOM_XML_ENTRY_CS_UTF_8 = create(APPLICATION_ATOM_XML_ENTRY,
          PARAMETER_CHARSET, CHARSET_UTF_8);
  public static final ContentType APPLICATION_ATOM_XML_FEED = create(APPLICATION_ATOM_XML, PARAMETER_TYPE, "feed");
  public static final ContentType APPLICATION_ATOM_XML_FEED_CS_UTF_8 = ContentType.create(APPLICATION_ATOM_XML_FEED,
          PARAMETER_CHARSET, CHARSET_UTF_8);
  public static final ContentType APPLICATION_ATOM_SVC = create("application", "atomsvc+xml");
  public static final ContentType APPLICATION_ATOM_SVC_CS_UTF_8 = create(APPLICATION_ATOM_SVC,
          PARAMETER_CHARSET, CHARSET_UTF_8);
  public static final ContentType APPLICATION_JSON = create("application", "json");
  public static final ContentType APPLICATION_JSON_CS_UTF_8 = create(APPLICATION_JSON,
      PARAMETER_CHARSET, CHARSET_UTF_8);
  public static final ContentType APPLICATION_OCTET_STREAM = create("application", "octet-stream");
  public static final ContentType TEXT_PLAIN = create("text", "plain");
  public static final ContentType TEXT_PLAIN_CS_UTF_8 = ContentType
          .create(TEXT_PLAIN, PARAMETER_CHARSET, CHARSET_UTF_8);
  public static final ContentType MULTIPART_MIXED = create("multipart", "mixed");

  private final String type;
  private final String subtype;
  private final Map<String, String> parameters;

  private ContentType(final String type) {
    if (type == null) {
      throw new IllegalArgumentException("Type parameter MUST NOT be null.");
    }
    this.type = validateType(type);
    subtype = null;
    parameters = Collections.emptyMap();
  }

  /**
   * Creates a content type from type, subtype, and parameters.
  * @param type
  * @param subtype
  * @param parameters
  */
  private ContentType(final String type, final String subtype, final Map<String, String> parameters) {
    if ((type == null || MEDIA_TYPE_WILDCARD.equals(type)) && !MEDIA_TYPE_WILDCARD.equals(subtype)) {
      throw new IllegalArgumentException("Illegal combination of WILDCARD type with NONE WILDCARD subtype.");
    }
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
      this.parameters.remove(PARAMETER_Q);
    }
  }

  private String validateType(final String type) {
    if (type == null || type.isEmpty()) {
      return MEDIA_TYPE_WILDCARD;
    }
    int len = type.length();
    for (int i = 0; i < len; i++) {
      if (type.charAt(i) == WHITESPACE_CHAR) {
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
    ContentType ct = new ContentType(contentType.type, contentType.subtype, contentType.parameters);
    ct.parameters.put(parameterKey, parameterValue);
    return ct;
  }

  /**
   * Create a {@link ContentType} based on given input string (<code>format</code>).
   * Supported format is <code>Media Type</code> format as defined in RFC 7231, chapter 3.1.1.1.
   * @param format a string in format as defined in RFC 7231, chapter 3.1.1.1
   * @return a new <code>ContentType</code> object
   * @throws IllegalArgumentException if input string is not parseable
   */
  public static ContentType create(final String format) {
    if (format == null) {
      throw new IllegalArgumentException("Parameter format MUST NOT be NULL.");
    }

    // split 'types' and 'parameters'
    String[] typesAndParameters = format.split(PARAMETER_SEPARATOR, 2);
    String types = typesAndParameters[0];
    String parameters = (typesAndParameters.length > 1 ? typesAndParameters[1] : null);
    //
    Map<String, String> parametersMap = parseParameters(parameters);
    //
    if (types.contains(TYPE_SUBTYPE_SEPARATOR)) {
      String[] tokens = types.split(TYPE_SUBTYPE_SEPARATOR);
      if (tokens.length == 2) {
        if (tokens[0] == null || tokens[0].isEmpty()) {
          throw new IllegalArgumentException("No type found in format '" + format + "'.");
        } else if (tokens[1] == null || tokens[1].isEmpty()) {
          throw new IllegalArgumentException("No subtype found in format '" + format + "'.");
        } else {
          return new ContentType(tokens[0], tokens[1], parametersMap);
        }
      } else {
        throw new IllegalArgumentException("Too many '" + TYPE_SUBTYPE_SEPARATOR + "' in format '" + format + "'.");
      }
    } else if (MEDIA_TYPE_WILDCARD.equals(types)) {
      return ContentType.WILDCARD;
    } else {
      throw new IllegalArgumentException("No separator '" + TYPE_SUBTYPE_SEPARATOR + "' was found in format '" + format
              + "'.");
    }
  }

  /**
   * Create a list of {@link ContentType} based on given input strings (<code>contentTypes</code>).
   *
   * Supported format is <code>Media Type</code> format as defined in RFC 7231, chapter 3.1.1.1.
   * If one of the given strings can not be parsed an exception is thrown (hence no list is returned with the parseable
   * strings).
   * @param contentTypeStrings a list of strings in format as defined in <code>RFC 2616 section 3.7</code>
   * @return a list of new <code>ContentType</code> object
   * @throws IllegalArgumentException if one of the given input string is not parseable this exceptions is thrown
   */
  public static List<ContentType> create(final List<String> contentTypeStrings) {
    List<ContentType> contentTypes = new ArrayList<ContentType>(contentTypeStrings.size());
    for (String contentTypeString : contentTypeStrings) {
      contentTypes.add(create(contentTypeString));
    }
    return contentTypes;
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

  /**
   * Sort given list (which must contains content-type formatted string) for their {@value #PARAMETER_Q} value
   * as defined in RFC 7231, chapter 3.1.1.1, and RFC 7231, chapter 5.3.1.
   *
   * <b>Attention:</b> For invalid values a {@value #PARAMETER_Q} value from <code>-1</code> is used for sorting.
   *
   * @param toSort list which is sorted and hence re-arranged
   */
  public static void sortForQParameter(final List<String> toSort) {
    Collections.sort(toSort, ContentType.Q_PARAMETER_COMPARATOR);
  }

  /**
   * Valid input are <code>;</code> separated <code>key=value</code> pairs
   * without spaces between key and value.
   * <b>Attention:</b> <code>q</code> parameter is validated but not added to result map
   *
   * <p>
   * See RFC 7231:
   * The type, subtype, and parameter name tokens are case-insensitive.
   * Parameter values might or might not be case-sensitive, depending on
   * the semantics of the parameter name.  The presence or absence of a
   * parameter might be significant to the processing of a media-type,
   * depending on its definition within the media type registry.
   * </p>
   *
   * @param parameters
   * @return Map with keys mapped to values
   */
  private static Map<String, String> parseParameters(final String parameters) {
    Map<String, String> parameterMap = new HashMap<String, String>();
    if (parameters != null) {
      String[] splittedParameters = parameters.split(PARAMETER_SEPARATOR);
      for (String parameter : splittedParameters) {
        String[] keyValue = parameter.split(PARAMETER_KEY_VALUE_SEPARATOR);
        String key = keyValue[0].trim().toLowerCase(Locale.ENGLISH);
        String value = keyValue.length > 1 ? keyValue[1] : null;
        if (value != null && Character.isWhitespace(value.charAt(0))) {
          throw new IllegalArgumentException("Value of parameter '" + key + "' starts with whitespace ('" + parameters
                  + "').");
        }
        if (PARAMETER_Q.equals(key.toLowerCase(Locale.ENGLISH))) {
          // q parameter is only validated but not added
          if (!Q_PARAMETER_VALUE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Value of 'q' parameter is not valid (q='" + value + "').");
          }
        } else {
          parameterMap.put(key, value);
        }
      }
    }
    return parameterMap;
  }

  /**
   * Parse value of {@value #PARAMETER_Q} <code>parameter</code> out of content type/parameters.
   * If no {@value #PARAMETER_Q} <code>parameter</code> is in <code>content type/parameters</code> parameter found
   * <code>1</code> is returned.
   * If {@value #PARAMETER_Q} <code>parameter</code> is invalid <code>-1</code> is returned.
   *
   * @param contentType parameter which is parsed for {@value #PARAMETER_Q} <code>parameter</code> value
   * @return value of {@value #PARAMETER_Q} <code>parameter</code> or <code>1</code> or <code>-1</code>
   */
  private static Float parseQParameterValue(final String contentType) {
    if (contentType != null) {
      String[] splittedParameters = contentType.split(PARAMETER_SEPARATOR);
      for (String parameter : splittedParameters) {
        String[] keyValue = parameter.split(PARAMETER_KEY_VALUE_SEPARATOR);
        String key = keyValue[0].trim().toLowerCase(Locale.ENGLISH);
        if (PARAMETER_Q.equalsIgnoreCase(key)) {
          String value = keyValue.length > 1 ? keyValue[1] : null;
          if (Q_PARAMETER_VALUE_PATTERN.matcher(value).matches()) {
            return Float.valueOf(value);
          }
          return Float.valueOf(-1);
        }
      }
    }
    return Float.valueOf(1);
  }

  /**
   * Check if parameter with key value is an allowed parameter.
   * @param key
   * @return
   */
  private static boolean isParameterAllowed(final String key) {
    return key != null && !PARAMETER_Q.equals(key.toLowerCase(Locale.ENGLISH));
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
      if (other.subtype == null) {
        return false;
      } else if (!subtype.equals(MEDIA_TYPE_WILDCARD) && !other.subtype.equals(MEDIA_TYPE_WILDCARD)) {
        return false;
      }
    }

    // type checks
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      if (!type.equals(MEDIA_TYPE_WILDCARD) && !other.type.equals(MEDIA_TYPE_WILDCARD)) {
        return false;
      }
    }

    // if wildcards are set, content types are defined as 'equal'
    if (countWildcards() > 0 || other.countWildcards() > 0) {
      return true;
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
    if (first == null) {
      if (second != null) {
        return false;
      }
    } else if (!first.equalsIgnoreCase(second)) {
      return false;
    }
    return true;
  }

  /**
   * Get {@link ContentType} as string as defined in RFC 7231 (http://www.ietf.org/rfc/rfc7231.txt, chapter 3.1.1.1:
   * Media Type)
   * @return string representation of <code>ContentType</code> object
   */
  public String toContentTypeString() {
    StringBuilder sb = new StringBuilder();

    sb.append(type).append(TYPE_SUBTYPE_SEPARATOR).append(subtype);

    for (String key : parameters.keySet()) {
      if (isParameterAllowed(key)) {
        String value = parameters.get(key);
        sb.append(";").append(key).append("=").append(value);
      }
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return toContentTypeString();
  }

  /**
   * Find best match between this {@link ContentType} and the {@link ContentType} in the list.
   * If a match (this {@link ContentType} is equal to a {@link ContentType} in list) is found either this or the
   * {@link ContentType} from the list is returned based on which {@link ContentType} has less "**" characters set
   * (checked with {@link #compareWildcardCounts(ContentType)}.
   * If no match (none {@link ContentType} in list is equal to this {@link ContentType}) is found <code>NULL</code> is
   * returned.
   *
   * @param toMatchContentTypes list of {@link ContentType}s which are matches against this {@link ContentType}
   * @return best matched content type in list or <code>NULL</code> if none content type match to this content type
   * instance
   */
  public ContentType match(final List<ContentType> toMatchContentTypes) {
    for (ContentType supportedContentType : toMatchContentTypes) {
      if (equals(supportedContentType)) {
        if (compareWildcardCounts(supportedContentType) < 0) {
          return this;
        } else {
          return supportedContentType;
        }
      }
    }
    return null;
  }

  /**
   * Find best match between this {@link ContentType} and the {@link ContentType} in the list ignoring all set
   * parameters.
   * If a match (this {@link ContentType} is equal to a {@link ContentType} in list) is found either this or the
   * {@link ContentType} from the list is returned based on which {@link ContentType} has less "**" characters set
   * (checked with {@link #compareWildcardCounts(ContentType)}.
   * If no match (none {@link ContentType} in list is equal to this {@link ContentType}) is found <code>NULL</code> is
   * returned.
   *
   * @param toMatchContentTypes list of {@link ContentType}s which are matches against this {@link ContentType}
   * @return best matched content type in list or <code>NULL</code> if none content type match to this content type
   * instance
   */
  public ContentType matchCompatible(final List<ContentType> toMatchContentTypes) {
    for (ContentType supportedContentType : toMatchContentTypes) {
      if (isCompatible(supportedContentType)) {
        if (compareWildcardCounts(supportedContentType) < 0) {
          return this;
        } else {
          return supportedContentType;
        }
      }
    }
    return null;
  }

  /**
   * Check if a valid compatible match for this {@link ContentType} exists in given list.
   * Compatible in this case means that <b>all set parameters are ignored</b>.
   * For more detail what a valid match is see {@link #matchCompatible(List)}.
   *
   * @param toMatchContentTypes list of {@link ContentType}s which are matches against this {@link ContentType}
   * @return <code>true</code> if a compatible content type was found in given list
   * or <code>false</code> if none compatible content type match was found
   */
  public boolean hasCompatible(final List<ContentType> toMatchContentTypes) {
    return matchCompatible(toMatchContentTypes) != null;
  }

  /**
   * Check if a valid match for this {@link ContentType} exists in given list.
   * For more detail what a valid match is see {@link #match(List)}.
   *
   * @param toMatchContentTypes list of {@link ContentType}s which are matches against this {@link ContentType}
   * @return <code>true</code> if a matching content type was found in given list
   * or <code>false</code> if none matching content type match was found
   */
  public boolean hasMatch(final List<ContentType> toMatchContentTypes) {
    return match(toMatchContentTypes) != null;
  }

  /**
   * Compare wildcards counts/weights of both {@link ContentType}.
   *
   * The smaller {@link ContentType} has lesser weighted wildcards then the bigger {@link ContentType}.
   * As result this method returns this object weighted wildcards minus the given parameter object weighted wildcards.
   *
   * A type wildcard is weighted with <code>2</code> and a subtype wildcard is weighted with <code>1</code>.
   *
   * @param otherContentType {@link ContentType} to be compared to
   * @return this object weighted wildcards minus the given parameter object weighted wildcards.
   */
  public int compareWildcardCounts(final ContentType otherContentType) {
    return countWildcards() - otherContentType.countWildcards();
  }

  private int countWildcards() {
    int count = 0;
    if (MEDIA_TYPE_WILDCARD.equals(type)) {
      count += 2;
    }
    if (MEDIA_TYPE_WILDCARD.equals(subtype)) {
      count++;
    }
    return count;
  }

  /**
   *
   * @return <code>true</code> if <code>type</code> or <code>subtype</code> of this instance is a "*".
   */
  public boolean hasWildcard() {
    return (MEDIA_TYPE_WILDCARD.equals(type) || MEDIA_TYPE_WILDCARD.equals(subtype));
  }

  /**
   *
   * @return <code>true</code> if both <code>type</code> and <code>subtype</code> of this instance are a "*".
   */
  public boolean isWildcard() {
    return (MEDIA_TYPE_WILDCARD.equals(type) && MEDIA_TYPE_WILDCARD.equals(subtype));
  }

  public static List<ContentType> convert(final List<String> types) {
    List<ContentType> results = new ArrayList<ContentType>();
    for (String contentType : types) {
      results.add(ContentType.create(contentType));
    }
    return results;
  }

  /**
   * Check if a valid match for given content type formated string (<code>toMatch</code>) exists in given list.
   * Therefore the given content type formated string (<code>toMatch</code>) is converted into a {@link ContentType}
   * with a simple {@link #create(String)} call (during which an exception can occur).
   *
   * For more detail in general see {@link #hasMatch(List)} and for what a valid match is see {@link #match(List)}.
   *
   * @param toMatch content type formated string (<code>toMatch</code>) for which is checked if a match exists in given
   * list
   * @param matchExamples list of {@link ContentType}s which are matches against content type formated string
   * (<code>toMatch</code>)
   * @return <code>true</code> if a matching content type was found in given list
   * or <code>false</code> if none matching content type match was found
   */
  public static boolean match(final String toMatch, final ContentType... matchExamples) {
    ContentType toMatchContentType = ContentType.create(toMatch);

    return toMatchContentType.hasMatch(Arrays.asList(matchExamples));
  }
}
