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
package org.apache.olingo.client.core.uri;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import javax.xml.datatype.Duration;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.Constants;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDateTime;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDateTimeOffset;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDecimal;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDouble;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt64;
import org.apache.olingo.commons.core.edm.primitivetype.EdmSingle;
import org.apache.olingo.commons.core.edm.primitivetype.EdmTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URI utilities.
 */
public final class URIUtils {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(URIUtils.class);

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

  /**
   * Gets function import URI segment.
   *
   * @param entityContainer entity container.
   * @param functionImport function import.
   * @return URI segment.
   */
  public static String rootFunctionImportURISegment(
          final EdmEntityContainer entityContainer, final EdmFunctionImport functionImport) {

    final StringBuilder result = new StringBuilder();
    // TODO: https://issues.apache.org/jira/browse/OLINGO-209
    // if (!entityContainer.isDefaultEntityContainer()) {
    //  result.append(entityContainer.getName()).append('.');
    // }
    result.append(functionImport.getName());

    return result.toString();
  }

  /**
   * Turns primitive values into their respective URI representation.
   *
   * @param obj primitive value
   * @return URI representation
   */
  public static String escape(final Object obj) {
    String value;

    try {
      value = (obj instanceof UUID)
              ? "guid'" + obj.toString() + "'"
              : (obj instanceof byte[])
              ? "X'" + Hex.encodeHexString((byte[]) obj) + "'"
              : (obj instanceof Timestamp)
              ? "datetime'" + URLEncoder.encode(EdmDateTime.getInstance().
                      valueToString(obj, null, null, null, null, null), Constants.UTF8) + "'"
              : (obj instanceof Calendar)
              ? "datetimeoffset'" + URLEncoder.encode(EdmDateTimeOffset.getInstance().
                      valueToString(obj, null, null, null, null, null), Constants.UTF8) + "'"
              : (obj instanceof Duration)
              ? "time'" + URLEncoder.encode(EdmTime.getInstance().
                      valueToString(obj, null, null, null, null, null), Constants.UTF8) + "'"
              : (obj instanceof BigDecimal)
              ? EdmDecimal.getInstance().valueToString(obj, null, null,
                      Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null) + "M"
              : (obj instanceof Double)
              ? EdmDouble.getInstance().valueToString(obj, null, null,
                      Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null) + "D"
              : (obj instanceof Float)
              ? EdmSingle.getInstance().valueToString(obj, null, null,
                      Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null) + "f"
              : (obj instanceof Long)
              ? EdmInt64.getInstance().valueToString(obj, null, null,
                      Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null) + "L"
              : (obj instanceof String)
              ? "'" + URLEncoder.encode((String) obj, Constants.UTF8) + "'"
              : obj.toString();
    } catch (Exception e) {
      LOG.warn("While escaping '{}', using toString()", obj, e);
      value = obj.toString();
    }

    return value;
  }

  public static InputStreamEntity buildInputStreamEntity(final CommonODataClient client, final InputStream input) {
    InputStreamEntity entity;
    if (client.getConfiguration().isUseChuncked()) {
      entity = new InputStreamEntity(input, -1);
    } else {
      byte[] bytes = new byte[0];
      try {
        bytes = IOUtils.toByteArray(input);
      } catch (IOException e) {
        LOG.error("While reading input for not chunked encoding", e);
      }

      entity = new InputStreamEntity(new ByteArrayInputStream(bytes), bytes.length);
    }
    entity.setChunked(client.getConfiguration().isUseChuncked());

    return entity;
  }
}
