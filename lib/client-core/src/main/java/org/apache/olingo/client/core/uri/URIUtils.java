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
package org.apache.olingo.client.core.uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.xml.datatype.Duration;

import org.apache.commons.codec.binary.Hex;
import org.apache.olingo.commons.core.Encoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.http.HttpClientFactory;
import org.apache.olingo.client.api.http.WrappingHttpClientFactory;
import org.apache.olingo.client.api.uri.SegmentType;
import org.apache.olingo.client.core.http.BasicAuthHttpClientFactory;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.core.edm.primitivetype.EdmBinary;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDateTimeOffset;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDecimal;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDouble;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDuration;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt64;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.commons.core.edm.primitivetype.EdmSingle;

/**
 * URI utilities.
 */
public final class URIUtils {

  /**
   * Logger.
   */

  private static final Pattern ENUM_VALUE = Pattern.compile("(.+\\.)?.+'.+'");
  private static final String URI_OPTIONS = "/$";

  private URIUtils() {
    // Empty private constructor for static utility classes
  }

  /**
   * Build URI starting from the given base and href.
   * <br/>
   * If href is absolute or base is null then base will be ignored.
   *
   * @param base URI prefix.
   * @param href URI suffix.
   * @return built URI.
   */
  public static URI getURI(final String base, final String href) {
    if (href == null) {
      throw new IllegalArgumentException("Null link provided");
    }

    URI uri = URI.create(href);

    if (!uri.isAbsolute() && base != null) {
      uri = URI.create(base + "/" + href);
    }

    return uri.normalize();
  }

  /**
   * Build URI starting from the given base and href.
   * <br/>
   * If href is absolute or base is null then base will be ignored.
   *
   * @param base URI prefix.
   * @param href URI suffix.
   * @return built URI.
   */
  public static URI getURI(final URI base, final URI href) {
    if (href == null) {
      throw new IllegalArgumentException("Null link provided");
    }
    return getURI(base, href.toASCIIString());
  }

  /**
   * Build URI starting from the given base and href.
   * <br/>
   * If href is absolute or base is null then base will be ignored.
   *
   * @param base URI prefix.
   * @param href URI suffix.
   * @return built URI.
   */
  public static URI getURI(final URI base, final String href) {
    if (href == null) {
      throw new IllegalArgumentException("Null link provided");
    }

    URI uri = URI.create(href);

    if (!uri.isAbsolute() && base != null) {
      uri = URI.create(base.toASCIIString() + "/" + href);
    }

    return uri.normalize();
  }

  private static String timestamp(final Timestamp timestamp)
      throws UnsupportedEncodingException, EdmPrimitiveTypeException {

    return Encoder.encode(EdmDateTimeOffset.getInstance().
        valueToString(timestamp, null, null, Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null));
  }

  private static String calendar(final Calendar calendar)
      throws UnsupportedEncodingException, EdmPrimitiveTypeException {

    return Encoder.encode(EdmDateTimeOffset.getInstance().
        valueToString(calendar, null, null, Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null));
  }

  private static String duration(final Duration duration)
      throws UnsupportedEncodingException, EdmPrimitiveTypeException {

    return EdmDuration.getInstance().toUriLiteral(Encoder.encode(EdmDuration.getInstance().
        valueToString(duration, null, null,
            Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null)));
  }

  private static String quoteString(final String string, final boolean singleQuoteEscape)
      throws UnsupportedEncodingException {

    return ENUM_VALUE.matcher(string).matches()
        ? string
        : singleQuoteEscape
            ? "'" + string + "'"
            : "\"" + string + "\"";
  }

  /**
   * Turns primitive values into their respective URI representation.
   *
   * @param obj primitive value
   * @return URI representation
   */
  public static String escape(final Object obj) {
    return escape(obj, true);
  }

  private static String escape(final Object obj, final boolean singleQuoteEscape) {
    String value;

    try {
      if (obj == null) {
        value = Constants.ATTR_NULL;
      } else if (obj instanceof Collection) {
        final StringBuilder buffer = new StringBuilder("[");
        for (@SuppressWarnings("unchecked")
        final Iterator<Object> itor = ((Collection<Object>) obj).iterator(); itor.hasNext();) {
          buffer.append(escape(itor.next(), false));
          if (itor.hasNext()) {
            buffer.append(',');
          }
        }
        buffer.append(']');

        value = buffer.toString();
      } else if (obj instanceof Map) {
        final StringBuilder buffer = new StringBuilder("{");
        for (@SuppressWarnings("unchecked")
        final Iterator<Map.Entry<String, Object>> itor =
            ((Map<String, Object>) obj).entrySet().iterator(); itor.hasNext();) {

          final Map.Entry<String, Object> entry = itor.next();
          buffer.append("\"").append(entry.getKey()).append("\"");
          buffer.append(':').append(escape(entry.getValue(), false));

          if (itor.hasNext()) {
            buffer.append(',');
          }
        }
        buffer.append('}');

        value = buffer.toString();
      } else {
        value =
            (obj instanceof ParameterAlias)
                ? "@" + ((ParameterAlias) obj).getAlias()
                : (obj instanceof Boolean)
                    ? BooleanUtils.toStringTrueFalse((Boolean) obj)
                    : (obj instanceof UUID)
                        ? obj.toString()

                        : (obj instanceof byte[])
                            ? EdmBinary.getInstance().toUriLiteral(Hex.encodeHexString((byte[]) obj))
                            : (obj instanceof Timestamp)
                                ? timestamp((Timestamp) obj)
                                : (obj instanceof Calendar)
                                    ? calendar((Calendar) obj)
                                    : (obj instanceof Duration)
                                        ? duration((Duration) obj)
                                        : (obj instanceof BigDecimal)
                                            ? EdmDecimal.getInstance().valueToString(obj, null, null,
                                                Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null)
                                            : (obj instanceof Double)
                                                ? EdmDouble.getInstance().valueToString(obj, null, null,
                                                    Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null)
                                                : (obj instanceof Float)
                                                    ? EdmSingle.getInstance().valueToString(obj, null, null,
                                                        Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null)
                                                    : (obj instanceof Long)
                                                        ? EdmInt64.getInstance().valueToString(obj, null, null,
                                                            Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null)
                                                        : (obj instanceof Geospatial)
                                                            ? Encoder.encode(EdmPrimitiveTypeFactory.getInstance(
                                                                ((Geospatial) obj).getEdmPrimitiveTypeKind()).
                                                                valueToString(obj, null, null,
                                                                    Constants.DEFAULT_PRECISION,
                                                                    Constants.DEFAULT_SCALE, null))
                                                            : (obj instanceof String)
                                                                ? quoteString((String) obj, singleQuoteEscape)
                                                                : obj.toString();
      }
    } catch (final EdmPrimitiveTypeException e) {
      value = obj.toString();
    } catch (final UnsupportedEncodingException e) {
      value = obj.toString();
    }

    return value;
  }

  public static boolean shouldUseRepeatableHttpBodyEntry(final ODataClient client) {
    // returns true for authentication request in case of http401 which needs retry so requires being repeatable.
    HttpClientFactory httpclientFactory = client.getConfiguration().getHttpClientFactory();
    if (httpclientFactory instanceof BasicAuthHttpClientFactory) {
      return true;
    } else if (httpclientFactory instanceof WrappingHttpClientFactory) {
      WrappingHttpClientFactory tmp = (WrappingHttpClientFactory) httpclientFactory;
      if (tmp.getWrappedHttpClientFactory() instanceof BasicAuthHttpClientFactory) {
        return true;
      }
    }

    return false;
  }

  public static HttpEntity buildInputStreamEntity(final ODataClient client, final InputStream input) {
    AbstractHttpEntity entity;
    boolean useChunked = client.getConfiguration().isUseChuncked();

    if (shouldUseRepeatableHttpBodyEntry(client) || !useChunked) {
      byte[] bytes = new byte[0];
      try {
        bytes = IOUtils.toByteArray(input);
        IOUtils.closeQuietly(input);
      } catch (IOException e) {
        throw new ODataRuntimeException("While reading input for not chunked encoding", e);
      }

      entity = new ByteArrayEntity(bytes);
    } else {
      entity = new InputStreamEntity(input, -1);
    }

    if (!useChunked && entity.getContentLength() < 0) {
      useChunked = true;
    }
    // both entities can be sent in chunked way or not
    entity.setChunked(useChunked);

    return entity;
  }

  public static URI addValueSegment(final URI uri) {
    final URI res;
    if (uri.getPath().endsWith(SegmentType.VALUE.getValue())) {
      res = uri;
    } else {
      try {
        res = new URIBuilder(uri).setPath(uri.getPath() + "/" + SegmentType.VALUE.getValue()).build();
      } catch (URISyntaxException e) {
        throw new IllegalArgumentException(e);
      }
    }

    return res;
  }

  public static URI buildFunctionInvokeURI(final URI uri, final Map<String, ClientValue> parameters) {
    final String rawQuery = uri.getRawQuery();
    String baseURI = null;
    String uriOption = "";
    String pathSegments = null;
    // Check if Query contains /$ and extract options like /$count, /$value and /$ref
    if (uri.toASCIIString().indexOf(URI_OPTIONS) != -1) {
      uriOption = uri.toASCIIString().substring(uri.toASCIIString().indexOf(URI_OPTIONS), 
          (rawQuery == null ? uri.toASCIIString().length() : uri.toASCIIString().indexOf(rawQuery) - 1));
    }
    if (rawQuery != null) {
      baseURI = StringUtils.substringBefore(uri.toASCIIString(), uriOption + "?" + rawQuery);
    } else if (uriOption.length() > 0) {
      baseURI = StringUtils.substringBefore(uri.toASCIIString(), uriOption);
    } else {
      baseURI = StringUtils.substringBefore(uri.toASCIIString(), null);
    }
    if (baseURI.endsWith("()")) {
      baseURI = baseURI.substring(0, baseURI.length() - 2);
    } else {
      /**
       * If FunctionName is followed by a Navigation segment or Actions, 
       * then get the substring till function name so that parameters can be appended to it.
       */
      int bracIndex = baseURI.indexOf("()");
      if (bracIndex != -1) {
        pathSegments = baseURI.substring(bracIndex + 2);
        baseURI = baseURI.substring(0, bracIndex);
      }
    }
    final StringBuilder inlineParams = new StringBuilder();
    for (Map.Entry<String, ClientValue> param : parameters.entrySet()) {
      inlineParams.append(param.getKey()).append("=");

      Object value = null;
      if (param.getValue().isPrimitive()) {
        value = param.getValue().asPrimitive().toValue();
      } else if (param.getValue().isComplex()) {
        value = param.getValue().asComplex().asJavaMap();
      } else if (param.getValue().isCollection()) {
        value = param.getValue().asCollection().asJavaCollection();
      } else if (param.getValue().isEnum()) {
        value = param.getValue().asEnum().toString();
      }

      inlineParams.append(URIUtils.escape(value)).append(',');
    }

    if (inlineParams.length() > 0) {
      inlineParams.deleteCharAt(inlineParams.length() - 1);
    }

    return URI.create(baseURI + "(" + Encoder.encode(inlineParams.toString()) + ")"
        + (pathSegments == null ? StringUtils.EMPTY : pathSegments)
        + (!uriOption.equals(StringUtils.EMPTY) ? "/" + Encoder.encode(uriOption.substring(1)) : StringUtils.EMPTY)
        + (StringUtils.isNotBlank(rawQuery) ? "?" + rawQuery : StringUtils.EMPTY));
  }
}
