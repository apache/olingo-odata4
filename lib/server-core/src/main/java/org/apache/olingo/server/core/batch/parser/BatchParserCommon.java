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
package org.apache.olingo.server.core.batch.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.core.batch.parser.BufferedReaderIncludingLineEndings.Line;

public class BatchParserCommon {

  private static final String REG_EX_BOUNDARY =
      "([a-zA-Z0-9_\\-\\.'\\+]{1,70})|\"([a-zA-Z0-9_\\-\\.'\\+\\s\\" +
          "(\\),/:=\\?]{1,69}[a-zA-Z0-9_\\-\\.'\\+\\(\\),/:=\\?])\"";
  private static final Pattern PATTERN_LAST_CRLF = Pattern.compile("(.*)(\r\n){1}( *)", Pattern.DOTALL);
  private static final Pattern PATTERN_HEADER_LINE = Pattern.compile("([a-zA-Z\\-]+):\\s?(.*)\\s*");
  private static final String REG_EX_APPLICATION_HTTP = "application/http";
  
  public static final Pattern PATTERN_MULTIPART_BOUNDARY = Pattern.compile("multipart/mixed(.*)",
      Pattern.CASE_INSENSITIVE);
  public static final Pattern PATTERN_CONTENT_TYPE_APPLICATION_HTTP = Pattern.compile(REG_EX_APPLICATION_HTTP,
      Pattern.CASE_INSENSITIVE);
  public static final String BINARY_ENCODING = "binary";
  public static final String HTTP_CONTENT_ID = "Content-Id";
  public static final String HTTP_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
  
  public static final String HTTP_EXPECT = "Expect";
  public static final String HTTP_FROM = "From";
  public static final String HTTP_MAX_FORWARDS = "Max-Forwards";
  public static final String HTTP_RANGE = "Range";
  public static final String HTTP_TE = "TE";
  
  public static String getBoundary(final String contentType, final int line) throws BatchException {
    if (contentType.toLowerCase(Locale.ENGLISH).startsWith("multipart/mixed")) {
      final String[] parameter = contentType.split(";");

      for (final String pair : parameter) {

        final String[] attrValue = pair.split("=");
        if (attrValue.length == 2 && "boundary".equals(attrValue[0].trim().toLowerCase(Locale.ENGLISH))) {
          if (attrValue[1].matches(REG_EX_BOUNDARY)) {
            return trimQuota(attrValue[1].trim());
          } else {
            throw new BatchException("Invalid boundary format", BatchException.MessageKeys.INVALID_BOUNDARY, "" + line);
          }
        }

      }
    }
    throw new BatchException("Content type is not multipart mixed", 
        BatchException.MessageKeys.INVALID_CONTENT_TYPE, HttpContentType.MULTIPART_MIXED);
  }

  public static String removeEndingSlash(String content) {
    content = content.trim();
    int lastSlashIndex = content.lastIndexOf('/');

    return (lastSlashIndex == content.length() - 1) ? content.substring(0, content.length() - 1) : content;
  }

  private static String trimQuota(String boundary) {
    if (boundary.matches("\".*\"")) {
      boundary = boundary.replace("\"", "");
    }

    return boundary;
  }

  public static List<List<Line>> splitMessageByBoundary(final List<Line> message, final String boundary)
      throws BatchException {
    final List<List<Line>> messageParts = new LinkedList<List<Line>>();
    List<Line> currentPart = new ArrayList<Line>();
    boolean isEndReached = false;

    final String quotedBoundary = Pattern.quote(boundary);
    final Pattern boundaryDelimiterPattern = Pattern.compile("--" + quotedBoundary + "--[\\s ]*");
    final Pattern boundaryPattern = Pattern.compile("--" + quotedBoundary + "[\\s ]*");

    for (Line currentLine : message) {
      if (boundaryDelimiterPattern.matcher(currentLine.toString()).matches()) {
        removeEndingCRLFFromList(currentPart);
        messageParts.add(currentPart);
        isEndReached = true;
      } else if (boundaryPattern.matcher(currentLine.toString()).matches()) {
        removeEndingCRLFFromList(currentPart);
        messageParts.add(currentPart);
        currentPart = new LinkedList<Line>();
      } else {
        currentPart.add(currentLine);
      }

      if (isEndReached) {
        break;
      }
    }

    final int lineNumer = (message.size() > 0) ? message.get(0).getLineNumber() : 0;
    // Remove preamble
    if (messageParts.size() > 0) {
      messageParts.remove(0);
    } else {
      throw new BatchException("Missing boundary delimiter", BatchException.MessageKeys.MISSING_BOUNDARY_DELIMITER, ""
          + lineNumer);
    }

    if (!isEndReached) {
      throw new BatchException("Missing close boundary delimiter", BatchException.MessageKeys.MISSING_CLOSE_DELIMITER,
          "" + lineNumer);
    }

    if (messageParts.size() == 0) {
      throw new BatchException("No boundary delimiter found",
          BatchException.MessageKeys.MISSING_BOUNDARY_DELIMITER, boundary, "" + lineNumer);
    }

    return messageParts;
  }

  private static void removeEndingCRLFFromList(final List<Line> list) {
    if (list.size() > 0) {
      Line lastLine = list.remove(list.size() - 1);
      list.add(removeEndingCRLF(lastLine));
    }
  }

  public static Line removeEndingCRLF(final Line line) {
    Pattern pattern = PATTERN_LAST_CRLF;
    Matcher matcher = pattern.matcher(line.toString());

    if (matcher.matches()) {
      return new Line(matcher.group(1), line.getLineNumber());
    } else {
      return line;
    }
  }

  public static Header consumeHeaders(final List<Line> remainingMessage) {
    final int headerLineNumber = remainingMessage.size() != 0 ? remainingMessage.get(0).getLineNumber() : 0;
    final Header headers = new Header(headerLineNumber);
    final Iterator<Line> iter = remainingMessage.iterator();
    Line currentLine;
    boolean isHeader = true;

    while (iter.hasNext() && isHeader) {
      currentLine = iter.next();
      final Matcher headerMatcher = PATTERN_HEADER_LINE.matcher(currentLine.toString());

      if (headerMatcher.matches() && headerMatcher.groupCount() == 2) {
        iter.remove();

        String headerName = headerMatcher.group(1).trim();
        String headerValue = headerMatcher.group(2).trim();

        headers.addHeader(headerName, Header.splitValuesByComma(headerValue), currentLine.getLineNumber());
      } else {
        isHeader = false;
      }
    }

    return headers;
  }

  public static void consumeBlankLine(final List<Line> remainingMessage, final boolean isStrict) throws BatchException {
    //TODO is \r\n to strict?
    if (remainingMessage.size() > 0 && remainingMessage.get(0).toString().matches("\\s*(\r\n|\n)\\s*")) {
      remainingMessage.remove(0);
    } else {
      if (isStrict) {
        final int lineNumber = (remainingMessage.size() > 0) ? remainingMessage.get(0).getLineNumber() : 0;
        throw new BatchException("Missing blank line", BatchException.MessageKeys.MISSING_BLANK_LINE, "[None]", ""
            + lineNumber);
      }
    }
  }

  public static InputStream convertLineListToInputStream(List<Line> messageList) {
    final String message = lineListToString(messageList);

    return new ByteArrayInputStream(message.getBytes());
  }

  private static String lineListToString(List<Line> messageList) {
    final StringBuilder builder = new StringBuilder();

    for (Line currentLine : messageList) {
      builder.append(currentLine.toString());
    }

    return builder.toString();
  }
  
  public static String trimLineListToLength(final List<Line> list, final int length) {
    final String message = lineListToString(list);
    final int lastIndex = Math.min(length, message.length());

    return (lastIndex > 0) ? message.substring(0, lastIndex) : "";
  }
  
  public static InputStream convertLineListToInputStream(List<Line> list, int length) {
    final String message = trimLineListToLength(list, length);

    return new ByteArrayInputStream(message.getBytes());
  }
}
