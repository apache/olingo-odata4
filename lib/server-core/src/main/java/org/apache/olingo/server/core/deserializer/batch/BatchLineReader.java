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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;

public class BatchLineReader {
  private static final byte CR = '\r';
  private static final byte LF = '\n';
  private static final int EOF = -1;
  private static final int BUFFER_SIZE = 8192;
  private static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");
  public static final String BOUNDARY = "boundary";
  public static final String DOUBLE_DASH = "--";
  public static final String CRLF = "\r\n";
  private Charset currentCharset = DEFAULT_CHARSET;
  private String currentBoundary = null;
  private ReadState readState = new ReadState();
  private InputStream reader;
  private byte[] buffer;
  private int offset = 0;
  private int limit = 0;

  public BatchLineReader(final InputStream reader) {
    this(reader, BUFFER_SIZE);
  }

  public BatchLineReader(final InputStream reader, final int bufferSize) {
    if (bufferSize <= 0) {
      throw new IllegalArgumentException("Buffer size must be greater than zero.");
    }

    this.reader = reader;
    buffer = new byte[bufferSize];
  }

  public void close() throws IOException {
    reader.close();
  }

  public List<String> toList() throws IOException {
    final List<String> result = new ArrayList<String>();
    String currentLine = readLine();
    if (currentLine != null) {
      currentBoundary = currentLine.trim();
      result.add(currentLine);

      while ((currentLine = readLine()) != null) {
        result.add(currentLine);
      }
    }
    return result;
  }

  public List<Line> toLineList() throws IOException {
    final List<Line> result = new ArrayList<Line>();
    String currentLine = readLine();
    if (currentLine != null) {
      currentBoundary = currentLine.trim();
      int counter = 1;
      result.add(new Line(currentLine, counter++));

      while ((currentLine = readLine()) != null) {
        result.add(new Line(currentLine, counter++));
      }
    }

    return result;
  }

  private void updateCurrentCharset(final String currentLine) {
    if (currentLine != null) {
      if (currentLine.startsWith(HttpHeader.CONTENT_TYPE)) {
        final ContentType contentType = ContentType.parse(
            currentLine.substring(HttpHeader.CONTENT_TYPE.length() + 1, currentLine.length() - 2).trim());
        if (contentType != null) {
          final String charsetString = contentType.getParameter(ContentType.PARAMETER_CHARSET);
          currentCharset = charsetString == null ?
              contentType.isCompatible(ContentType.APPLICATION_JSON) || contentType.getSubtype().contains("xml") ?
                  Charset.forName("UTF-8") :
                  DEFAULT_CHARSET :
              Charset.forName(charsetString);

          final String boundary = contentType.getParameter(BOUNDARY);
          if (boundary != null) {
            currentBoundary = DOUBLE_DASH + boundary;
          }
        }
      } else if (CRLF.equals(currentLine)) {
        readState.foundLinebreak();
      } else if (isBoundary(currentLine)) {
        readState.foundBoundary();
      }
    }
  }

  private boolean isBoundary(final String currentLine) {
    return (currentBoundary + CRLF).equals(currentLine)
        || (currentBoundary + DOUBLE_DASH + CRLF).equals(currentLine);
  }

  String readLine() throws IOException {
    if (limit == EOF) {
      return null;
    }

    ByteBuffer innerBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    // EOF will be considered as line ending
    boolean foundLineEnd = false;

    while (!foundLineEnd) {
      // Is buffer refill required?
      if (limit == offset && fillBuffer() == EOF) {
        foundLineEnd = true;
      }

      if (!foundLineEnd) {
        byte currentChar = buffer[offset++];
        if (!innerBuffer.hasRemaining()) {
          innerBuffer.flip();
          ByteBuffer tmp = ByteBuffer.allocate(innerBuffer.limit() * 2);
          tmp.put(innerBuffer);
          innerBuffer = tmp;
        }
        innerBuffer.put(currentChar);

        if (currentChar == LF) {
          foundLineEnd = true;
        } else if (currentChar == CR) {
          foundLineEnd = true;

          // Check next byte. Consume \n if available
          // Is buffer refill required?
          if (limit == offset) {
            fillBuffer();
          }

          // Check if there is at least one character
          if (limit != EOF && buffer[offset] == LF) {
            innerBuffer.put(LF);
            offset++;
          }
        }
      }
    }

    if (innerBuffer.position() == 0) {
      return null;
    } else {
      final String currentLine = new String(innerBuffer.array(), 0, innerBuffer.position(),
          readState.isReadBody() ? currentCharset : DEFAULT_CHARSET);
      updateCurrentCharset(currentLine);
      return currentLine;
    }
  }

  private int fillBuffer() throws IOException {
    limit = reader.read(buffer, 0, buffer.length);
    offset = 0;

    return limit;
  }

  /**
   * Read state indicator (whether currently the <code>body</code> or <code>header</code> part is read).
   */
  private static class ReadState {
    private int state = 0;

    public void foundLinebreak() {
      state++;
    }

    public void foundBoundary() {
      state = 0;
    }

    public boolean isReadBody() {
      return state >= 2;
    }

    @Override
    public String toString() {
      return String.valueOf(state);
    }
  }
}
