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
package org.apache.olingo.server.core.batch.transformator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.api.batch.BatchException.MessageKeys;
import org.apache.olingo.server.core.batch.parser.BatchParserCommon;
import org.apache.olingo.server.core.batch.parser.BufferedReaderIncludingLineEndings.Line;
import org.apache.olingo.server.core.batch.parser.Header;
import org.apache.olingo.server.core.batch.parser.HeaderField;

public class BatchTransformatorCommon {

  public static void validateContentType(final Header headers, final Pattern pattern) throws BatchException {
    List<String> contentTypes = headers.getHeaders(HttpHeader.CONTENT_TYPE);

    if (contentTypes.size() == 0) {
      throw new BatchException("Missing content type", MessageKeys.MISSING_CONTENT_TYPE, headers.getLineNumber());
    }
    if (!headers.isHeaderMatching(HttpHeader.CONTENT_TYPE, pattern)) {

      throw new BatchException("Invalid content type", MessageKeys.INVALID_CONTENT_TYPE,
          HttpContentType.MULTIPART_MIXED + " or " + HttpContentType.APPLICATION_HTTP);
    }
  }

  public static void validateContentTransferEncoding(Header headers) throws BatchException {
    final HeaderField contentTransferField = headers.getHeaderField(BatchParserCommon.HTTP_CONTENT_TRANSFER_ENCODING);

    if (contentTransferField != null) {
      final List<String> contentTransferValues = contentTransferField.getValues();
      if (contentTransferValues.size() == 1) {
        String encoding = contentTransferValues.get(0);

        if (!BatchParserCommon.BINARY_ENCODING.equalsIgnoreCase(encoding)) {
          throw new BatchException("Invalid content transfer encoding", MessageKeys.INVALID_CONTENT_TRANSFER_ENCODING,
              headers.getLineNumber());
        }
      } else {
        throw new BatchException("Invalid header", MessageKeys.INVALID_HEADER, headers.getLineNumber());
      }
    } else {
      throw new BatchException("Missing mandatory content transfer encoding",
          MessageKeys.MISSING_CONTENT_TRANSFER_ENCODING,
          headers.getLineNumber());
    }
  }

  public static int getContentLength(Header headers) throws BatchException {
    final HeaderField contentLengthField = headers.getHeaderField(HttpHeader.CONTENT_LENGTH);

    if (contentLengthField != null && contentLengthField.getValues().size() == 1) {
      final List<String> contentLengthValues = contentLengthField.getValues();

      try {
        int contentLength = Integer.parseInt(contentLengthValues.get(0));

        if (contentLength < 0) {
          throw new BatchException("Invalid content length", MessageKeys.INVALID_CONTENT_LENGTH, contentLengthField
              .getLineNumber());
        }

        return contentLength;
      } catch (NumberFormatException e) {
        throw new BatchException("Invalid header", MessageKeys.INVALID_HEADER, contentLengthField.getLineNumber());
      }
    }

    return -1;
  }

  public static class HttpRequestStatusLine {
    private static final Pattern PATTERN_RELATIVE_URI = Pattern.compile("([^/][^?]*)(?:\\?(.*))?");
    private static final Pattern PATTERN_ABSOLUTE_URI_WITH_HOST = Pattern.compile("(/[^?]*)(?:\\?(.*))?");
    private static final Pattern PATTERN_ABSOLUTE_URI = Pattern.compile("(http[s]?://[^?]*)(?:\\?(.*))?");

    private static final Set<String> HTTP_BATCH_METHODS = new HashSet<String>(Arrays.asList(new String[] { "GET" }));
    private static final Set<String> HTTP_CHANGE_SET_METHODS = new HashSet<String>(Arrays.asList(new String[] { "POST",
        "PUT", "DELETE", "MERGE", "PATCH" }));
    private static final String HTTP_VERSION = "HTTP/1.1";

    final private Line statusLine;
    final String requestBaseUri;

    private HttpMethod method;
    private String httpVersion;
    private String rawServiceResolutionUri;
    private String rawQueryPath;
    private String rawODataPath;
    private String rawBaseUri;
    private Header header;
    private String rawRequestUri;

    public HttpRequestStatusLine(final Line httpStatusLine, final String baseUri, final String serviceResolutionUri,
        final Header requestHeader)
        throws BatchException {
      statusLine = httpStatusLine;
      requestBaseUri = baseUri;
      header = requestHeader;
      rawServiceResolutionUri = serviceResolutionUri;
      
      parse();
    }

    private void parse() throws BatchException {
      final String[] parts = statusLine.toString().split(" ");

      if (parts.length == 3) {
        method = parseMethod(parts[0]);
        parseRawPaths(parts[1]);
        httpVersion = parseHttpVersion(parts[2]);
      } else {
        throw new BatchException("Invalid status line", MessageKeys.INVALID_STATUS_LINE, statusLine.getLineNumber());
      }
    }

    private HttpMethod parseMethod(final String method) throws BatchException {
      try {
        return HttpMethod.valueOf(method.trim());
      } catch (IllegalArgumentException e) {
        throw new BatchException("Illegal http method", MessageKeys.INVALID_METHOD, statusLine.getLineNumber());
      }
    }

    private void parseRawPaths(final String rawUrl) throws BatchException {
      final Matcher absoluteUriMatcher = PATTERN_ABSOLUTE_URI.matcher(rawUrl);
      final Matcher absoluteUriWtithHostMatcher = PATTERN_ABSOLUTE_URI_WITH_HOST.matcher(rawUrl);
      final Matcher relativeUriMatcher = PATTERN_RELATIVE_URI.matcher(rawUrl);

      if (absoluteUriMatcher.matches()) {
        buildUri(absoluteUriMatcher.group(1), absoluteUriMatcher.group(2));

      } else if (absoluteUriWtithHostMatcher.matches()) {
        final List<String> hostHeader = header.getHeaders(HttpHeader.HOST);
        if (hostHeader.size() == 1) {
          buildUri(hostHeader.get(0) + absoluteUriWtithHostMatcher.group(1), absoluteUriWtithHostMatcher.group(2));
        } else {
          throw new BatchException("Exactly one host header is required", MessageKeys.MISSING_MANDATORY_HEADER,
              statusLine.getLineNumber());
        }

      } else if (relativeUriMatcher.matches()) {
        buildUri(requestBaseUri + "/" + relativeUriMatcher.group(1), relativeUriMatcher.group(2));

      } else {
        throw new BatchException("Invalid uri", MessageKeys.INVALID_URI, statusLine.getLineNumber());
      }
    }

    private void buildUri(final String resourceUri, final String queryOptions) throws BatchException {
      if(!resourceUri.startsWith(requestBaseUri)) {
        throw new BatchException("Host do not match", MessageKeys.INVALID_URI, statusLine.getLineNumber());
      }
      
      final int oDataPathIndex = resourceUri.indexOf(requestBaseUri);

      rawBaseUri = requestBaseUri;
      rawODataPath = resourceUri.substring(oDataPathIndex + requestBaseUri.length());
      rawServiceResolutionUri = "";
      rawRequestUri = requestBaseUri + rawODataPath;

      if (queryOptions != null) {
        rawRequestUri += "?" + queryOptions;
        rawQueryPath = queryOptions;
      } else {
        rawQueryPath = "";
      }
    }

    private String parseHttpVersion(final String httpVersion) throws BatchException {
      if (!HTTP_VERSION.equals(httpVersion.trim())) {
        throw new BatchException("Invalid http version", MessageKeys.INVALID_HTTP_VERSION, statusLine.getLineNumber());
      } else {
        return HTTP_VERSION;
      }
    }

    public void validateHttpMethod(boolean isChangeSet) throws BatchException {
      Set<String> validMethods = (isChangeSet) ? HTTP_CHANGE_SET_METHODS : HTTP_BATCH_METHODS;

      if (!validMethods.contains(getMethod().toString())) {
        if (isChangeSet) {
          throw new BatchException("Invalid change set method", MessageKeys.INVALID_CHANGESET_METHOD, statusLine
              .getLineNumber());
        } else {
          throw new BatchException("Invalid query operation method", MessageKeys.INVALID_QUERY_OPERATION_METHOD,
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

    public String getRawBaseUri() {
      return rawBaseUri;
    }

    public String getRawODataPath() {
      return rawODataPath;
    }

    public String getRawQueryPath() {
      return rawQueryPath;
    }

    public String getRawRequestUri() {
      return rawRequestUri;
    }

    public String getRawServiceResolutionUri() {
      return rawServiceResolutionUri;
    }
  }
}
