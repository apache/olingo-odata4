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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;

public class BatchParserCommon {

  // Multipart boundaries are defined in RFC 2046:
  //     boundary      := 0*69<bchars> bcharsnospace
  //     bchars        := bcharsnospace / " "
  //     bcharsnospace := DIGIT / ALPHA / "'" / "(" / ")" / "+" / "_" / "," / "-" / "." / "/" / ":" / "=" / "?"
  // The first alternative is for the case that only characters are used that don't need quoting.
  private static final Pattern PATTERN_BOUNDARY = Pattern.compile(
      "((?:\\w|[-.'+]){1,70})|"
          + "\"((?:\\w|[-.'+(),/:=?]|\\s){0,69}(?:\\w|[-.'+(),/:=?]))\"");
  private static final Pattern PATTERN_LAST_CRLF = Pattern.compile("(.*)\\r\\n\\s*", Pattern.DOTALL);
  // HTTP header fields are defined in RFC 7230:
  //     header-field   = field-name ":" OWS field-value OWS
  //     field-name     = token
  //     field-value    = *( field-content / obs-fold )
  //     field-content  = field-vchar [ 1*( SP / HTAB ) field-vchar ]
  //     field-vchar    = VCHAR / obs-text
  //     obs-fold       = CRLF 1*( SP / HTAB )
  //     token          = 1*tchar
  //     tchar          = "!" / "#" / "$" / "%" / "&" / "'" / "*" / "+" / "-" / "." / "^" / "_" / "`" / "|" / "~"
  //                      / DIGIT / ALPHA
  // For the field-name the specification is followed strictly,
  // but for the field-value the pattern currently accepts more than specified.
  private static final Pattern PATTERN_HEADER_LINE = Pattern.compile("((?:\\w|[!#$%\\&'*+\\-.^`|~])+):\\s?(.*)\\s*");

  public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";

  protected static final String BOUNDARY = "boundary";
  public static final String BINARY_ENCODING = "binary";

  private BatchParserCommon() { /* private constructor for helper class */ }

  public static String getBoundary(final String contentType, final int line) throws BatchDeserializerException {
    final ContentType type = parseContentType(contentType, ContentType.MULTIPART_MIXED, line);
    final String boundary = type.getParameter(BOUNDARY);
    if (boundary == null) {
      throw new BatchDeserializerException("Missing boundary.",
          BatchDeserializerException.MessageKeys.MISSING_BOUNDARY_DELIMITER, Integer.toString(line));
    }
    final Matcher matcher = PATTERN_BOUNDARY.matcher(boundary);
    if (matcher.matches()) {
      return matcher.group(1) == null ? matcher.group(2) : matcher.group(1);
    } else {
      throw new BatchDeserializerException("Invalid boundary format",
          BatchDeserializerException.MessageKeys.INVALID_BOUNDARY, Integer.toString(line));
    }
  }

  /**
   * Get the content type based on <code>contentType</code> parameter.
   * If this content type is not compatible to the expected ContentType a
   * BatchDeserializerException is thrown.
   *
   * @param contentType content type string which is parsed
   * @param expected content type to which the parsed must be compatible
   * @param line parsed line
   * @return the parsed content type or if not compatible or parseable an exception is thrown (never returns null)
   * @throws BatchDeserializerException
   */
  public static ContentType parseContentType(final String contentType, final ContentType expected, final int line)
      throws BatchDeserializerException {
    if (contentType == null) {
      throw new BatchDeserializerException("Missing content type",
          BatchDeserializerException.MessageKeys.MISSING_CONTENT_TYPE, Integer.toString(line));
    }
    ContentType type;
    try {
      type = ContentType.create(contentType);
    } catch (final IllegalArgumentException e) {
      throw new BatchDeserializerException("Invalid content type.", e,
          BatchDeserializerException.MessageKeys.INVALID_CONTENT_TYPE, Integer.toString(line));
    }
    if (type.isCompatible(expected)) {
      return type;
    } else {
      throw new BatchDeserializerException("Content type is not the expected content type",
          BatchDeserializerException.MessageKeys.UNEXPECTED_CONTENT_TYPE,
          Integer.toString(line), expected.toContentTypeString(), type.toContentTypeString());
    }
  }

  public static List<List<Line>> splitMessageByBoundary(final List<Line> message, final String boundary)
      throws BatchDeserializerException {
    final List<List<Line>> messageParts = new LinkedList<List<Line>>();
    List<Line> currentPart = new LinkedList<Line>();
    boolean isEndReached = false;

    final String quotedBoundary = Pattern.quote(boundary);
    final Pattern boundaryDelimiterPattern = Pattern.compile("--" + quotedBoundary + "--\\s*");
    final Pattern boundaryPattern = Pattern.compile("--" + quotedBoundary + "\\s*");

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

    // Remove preamble
    if (!messageParts.isEmpty()) {
      messageParts.remove(0);
    }

    if (!isEndReached) {
      final int lineNumber = (!message.isEmpty()) ? message.get(0).getLineNumber() : 0;
      throw new BatchDeserializerException("Missing close boundary delimiter",
          BatchDeserializerException.MessageKeys.MISSING_CLOSE_DELIMITER, Integer.toString(lineNumber));
    }

    return messageParts;
  }

  private static void removeEndingCRLFFromList(final List<Line> list) {
    if (!list.isEmpty()) {
      Line lastLine = list.remove(list.size() - 1);
      list.add(removeEndingCRLF(lastLine));
    }
  }

  public static Line removeEndingCRLF(final Line line) {
    Matcher matcher = PATTERN_LAST_CRLF.matcher(line.toString());
    if (matcher.matches()) {
      return new Line(matcher.group(1), line.getLineNumber());
    } else {
      return line;
    }
  }

  public static Header consumeHeaders(final List<Line> remainingMessage) {
    final int headerLineNumber = !remainingMessage.isEmpty() ? remainingMessage.get(0).getLineNumber() : 0;
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

  public static void consumeBlankLine(final List<Line> remainingMessage, final boolean isStrict)
      throws BatchDeserializerException {
    if (!remainingMessage.isEmpty() && remainingMessage.get(0).toString().matches("\\s*\r?\n\\s*")) {
      remainingMessage.remove(0);
    } else {
      if (isStrict) {
        final int lineNumber = (!remainingMessage.isEmpty()) ? remainingMessage.get(0).getLineNumber() : 0;
        throw new BatchDeserializerException("Missing blank line",
            BatchDeserializerException.MessageKeys.MISSING_BLANK_LINE, "[None]", Integer.toString(lineNumber));
      }
    }
  }

  public static InputStream convertLineListToInputStream(final List<Line> messageList, final Charset charset) {
    final String message = lineListToString(messageList);

    return new ByteArrayInputStream(message.getBytes(charset));
  }

  private static String lineListToString(final List<Line> messageList) {
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

  public static InputStream convertLineListToInputStream(final List<Line> list, final Charset charset,
      final int length) {
    final String message = trimLineListToLength(list, length);

    return new ByteArrayInputStream(message.getBytes(charset));
  }
}
