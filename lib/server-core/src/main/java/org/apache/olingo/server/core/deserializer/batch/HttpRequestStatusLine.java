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
package org.apache.olingo.server.core.deserializer.batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.batch.exception.BatchDeserializerException;
import org.apache.olingo.server.api.batch.exception.BatchDeserializerException.MessageKeys;

public class HttpRequestStatusLine {
  private static final Pattern PATTERN_RELATIVE_URI = Pattern.compile("([^/][^?]*)(?:\\?(.*))?");
  private static final Pattern PATTERN_ABSOLUTE_URI_WITH_HOST = Pattern.compile("(/[^?]*)(?:\\?(.*))?");
  private static final Pattern PATTERN_ABSOLUTE_URI = Pattern.compile("(http[s]?://[^?]*)(?:\\?(.*))?");

  private static final Set<String> HTTP_BATCH_METHODS = new HashSet<String>(Arrays.asList(new String[] { "GET" }));
  private static final Set<String> HTTP_CHANGE_SET_METHODS = new HashSet<String>(Arrays.asList(new String[] { "POST",
      "PUT", "DELETE", "PATCH" }));
  private static final String HTTP_VERSION = "HTTP/1.1";

  final private Line statusLine;
  final String requestBaseUri;

  private HttpMethod method;
  private String httpVersion;
  private Header header;
  private ODataURI uri;

  public HttpRequestStatusLine(final Line httpStatusLine, final String baseUri, final String serviceResolutionUri,
      final Header requestHeader)
          throws BatchDeserializerException {
    statusLine = httpStatusLine;
    requestBaseUri = baseUri;
    header = requestHeader;

    parse();
  }

  private void parse() throws BatchDeserializerException {
    final String[] parts = statusLine.toString().split(" ");

    if (parts.length == 3) {
      method = parseMethod(parts[0]);
      uri = new ODataURI(parts[1], requestBaseUri, statusLine.getLineNumber(), header.getHeaders(HttpHeader.HOST));
      httpVersion = parseHttpVersion(parts[2]);
    } else {
      throw new BatchDeserializerException("Invalid status line", MessageKeys.INVALID_STATUS_LINE, statusLine
          .getLineNumber());
    }
  }

  private HttpMethod parseMethod(final String method) throws BatchDeserializerException {
    try {
      return HttpMethod.valueOf(method.trim());
    } catch (IllegalArgumentException e) {
      throw new BatchDeserializerException("Illegal http method", MessageKeys.INVALID_METHOD, statusLine
          .getLineNumber());
    }
  }

  private String parseHttpVersion(final String httpVersion) throws BatchDeserializerException {
    if (!HTTP_VERSION.equals(httpVersion.trim())) {
      throw new BatchDeserializerException("Invalid http version", MessageKeys.INVALID_HTTP_VERSION, statusLine
          .getLineNumber());
    } else {
      return HTTP_VERSION;
    }
  }

  public void validateHttpMethod(final boolean isChangeSet) throws BatchDeserializerException {
    Set<String> validMethods = (isChangeSet) ? HTTP_CHANGE_SET_METHODS : HTTP_BATCH_METHODS;

    if (!validMethods.contains(getMethod().toString())) {
      if (isChangeSet) {
        throw new BatchDeserializerException("Invalid change set method", MessageKeys.INVALID_CHANGESET_METHOD,
            statusLine
            .getLineNumber());
      } else {
        throw new BatchDeserializerException("Invalid query operation method",
            MessageKeys.INVALID_QUERY_OPERATION_METHOD,
            statusLine.getLineNumber());
      }
    }
  }

  public HttpMethod getMethod() {
    return method;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public int getLineNumber() {
    return statusLine.getLineNumber();
  }

  public ODataURI getUri() {
    return uri;
  }

  public static class ODataURI {
    private String rawServiceResolutionUri;
    private String rawQueryPath;
    private String rawODataPath;
    private String rawBaseUri;
    private String rawRequestUri;
    private final String requestBaseUri;
    private final int lineNumber;

    public ODataURI(final String rawUri, final String requestBaseUri) throws BatchDeserializerException {
      this(rawUri, requestBaseUri, 0, new ArrayList<String>());
    }

    public ODataURI(final String rawUri, final String requestBaseUri, final int lineNumber,
        final List<String> hostHeader)
        throws BatchDeserializerException {
      this.lineNumber = lineNumber;
      this.requestBaseUri = requestBaseUri;

      final Matcher absoluteUriMatcher = PATTERN_ABSOLUTE_URI.matcher(rawUri);
      final Matcher absoluteUriWtithHostMatcher = PATTERN_ABSOLUTE_URI_WITH_HOST.matcher(rawUri);
      final Matcher relativeUriMatcher = PATTERN_RELATIVE_URI.matcher(rawUri);

      if (absoluteUriMatcher.matches()) {
        buildUri(absoluteUriMatcher.group(1), absoluteUriMatcher.group(2));

      } else if (absoluteUriWtithHostMatcher.matches()) {
        if (hostHeader != null && hostHeader.size() == 1) {
          buildUri(hostHeader.get(0) + absoluteUriWtithHostMatcher.group(1), absoluteUriWtithHostMatcher.group(2));
        } else {
          throw new BatchDeserializerException("Exactly one host header is required",
              MessageKeys.MISSING_MANDATORY_HEADER,
              lineNumber);
        }

      } else if (relativeUriMatcher.matches()) {
        buildUri(requestBaseUri + "/" + relativeUriMatcher.group(1), relativeUriMatcher.group(2));

      } else {
        throw new BatchDeserializerException("Invalid uri", MessageKeys.INVALID_URI, lineNumber);
      }
    }

    private void buildUri(final String resourceUri, final String queryOptions) throws BatchDeserializerException {
      if (!resourceUri.startsWith(requestBaseUri)) {
        throw new BatchDeserializerException("Host do not match", MessageKeys.INVALID_URI, lineNumber);
      }

      final int oDataPathIndex = resourceUri.indexOf(requestBaseUri);

      rawBaseUri = requestBaseUri;
      rawODataPath = resourceUri.substring(oDataPathIndex + requestBaseUri.length());
      rawRequestUri = requestBaseUri + rawODataPath;

      if (queryOptions != null) {
        rawRequestUri += "?" + queryOptions;
        rawQueryPath = queryOptions;
      } else {
        rawQueryPath = "";
      }
    }

    public String getRawServiceResolutionUri() {
      return rawServiceResolutionUri;
    }

    public void setRawServiceResolutionUri(final String rawServiceResolutionUri) {
      this.rawServiceResolutionUri = rawServiceResolutionUri;
    }

    public String getRawQueryPath() {
      return rawQueryPath;
    }

    public void setRawQueryPath(final String rawQueryPath) {
      this.rawQueryPath = rawQueryPath;
    }

    public String getRawODataPath() {
      return rawODataPath;
    }

    public void setRawODataPath(final String rawODataPath) {
      this.rawODataPath = rawODataPath;
    }

    public String getRawBaseUri() {
      return rawBaseUri;
    }

    public void setRawBaseUri(final String rawBaseUri) {
      this.rawBaseUri = rawBaseUri;
    }

    public String getRawRequestUri() {
      return rawRequestUri;
    }

    public void setRawRequestUri(final String rawRequestUri) {
      this.rawRequestUri = rawRequestUri;
    }

    public String getRequestBaseUri() {
      return requestBaseUri;
    }
  }
}