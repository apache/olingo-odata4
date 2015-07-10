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

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Read batch content and split it into lines.
 * This class is not thread safe.
 */
public class BatchLineReader {
  private static final byte CR_BYTE = '\r';
  private static final byte LF_BYTE = '\n';
  private static final int EOF = -1;
  private static final int BUFFER_SIZE = 8192;
  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
  private static final Charset CS_ISO_8859_1 = Charset.forName("iso-8859-1");
  private static final String BOUNDARY = "boundary";
  private static final String DOUBLE_DASH = "--";
  private static final String CR = "\r";
  private static final String LF = "\n";
  private static final String CRLF = "\r\n";
  // length of the Content-Type Header field including the ':'
  // "Content-Type:" => 13
  private static final int CONTENT_TYPE_LENGTH = 13;

  private final ReadState readState = new ReadState();
  private Charset currentCharset = DEFAULT_CHARSET;
  private String currentBoundary = null;
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
    if(currentLine != null) {
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
    if(currentLine != null) {
      currentBoundary = currentLine.trim();
      int counter = 1;
      result.add(new Line(currentLine, counter++));

      while ((currentLine = readLine()) != null) {
        result.add(new Line(currentLine, counter++));
      }
    }

    return result;
  }

  int read(final byte[] byteBuffer, final int bufferOffset, final int length) throws IOException {
    if ((bufferOffset + length) > byteBuffer.length) {
      throw new IndexOutOfBoundsException("Buffer is too small");
    }

    if (length < 0 || bufferOffset < 0) {
      throw new IndexOutOfBoundsException("Offset and length must be greater than zero");
    }

    // Check if buffer is filled. Return if EOF is reached
    // Is buffer refill required
    if (limit == offset || isEOF()) {
      fillBuffer();

      if (isEOF()) {
        return EOF;
      }
    }

    int bytesRead = 0;
    int bytesToRead = length;
    int currentOutputOffset = bufferOffset;

    while (bytesToRead != 0) {
      // Is buffer refill required?
      if (limit == offset) {
        fillBuffer();

        if (isEOF()) {
          bytesToRead = 0;
        }
      }

      if (bytesToRead > 0) {
        int readByte = Math.min(limit - offset, bytesToRead);
        bytesRead += readByte;
        bytesToRead -= readByte;

        for (int i = 0; i < readByte; i++) {
          byteBuffer[currentOutputOffset++] = buffer[offset++];
        }
      }
    }

    return bytesRead;
  }

  private void updateCurrentCharset(String currentLine) {
    if(currentLine != null) {
      if(currentLine.startsWith(HttpHeader.CONTENT_TYPE)) {
        ContentType ct = parseContentType(currentLine);
        if (ct != null) {
          String charsetString = ct.getParameter(ContentType.PARAMETER_CHARSET);
          if (charsetString != null) {
            currentCharset = Charset.forName(charsetString);
          } else {
            currentCharset = DEFAULT_CHARSET;
          }
          // boundary
          String boundary = ct.getParameter(BOUNDARY);
          if (boundary != null) {
            currentBoundary = DOUBLE_DASH + boundary;
          }
        }
      } else if(isLinebreak(currentLine)) {
        readState.foundLinebreak();
      } else if(isBoundary(currentLine)) {
        readState.foundBoundary();
      }
    }
  }

  private ContentType parseContentType(String currentLine) {
    currentLine = currentLine.substring(CONTENT_TYPE_LENGTH, currentLine.length()).trim();
    return ContentType.parse(currentLine);
  }

  private boolean isLinebreak(String currentLine) {
    if(currentLine.length() > 2) {
      return false;
    }
    return CR.equals(currentLine) || LF.equals(currentLine) || CRLF.equals(currentLine);
  }

  private boolean isBoundary(String currentLine) {
    if((currentBoundary + CRLF).equals(currentLine)) {
      return true;
    } else if((currentBoundary + DOUBLE_DASH + CRLF).equals(currentLine)) {
      return true;
    }
    return false;
  }

  String readLine() throws IOException {
    if (limit == EOF) {
      return null;
    }

    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    boolean foundLineEnd = false; // EOF will be considered as line ending

    while (!foundLineEnd) {
      // Is buffer refill required?
      if (limit == offset) {
        if (fillBuffer() == EOF) {
          foundLineEnd = true;
        }
      }

      if (!foundLineEnd) {
        byte currentChar = this.buffer[offset++];
        if(!buffer.hasRemaining()) {
          buffer.flip();
          ByteBuffer tmp = ByteBuffer.allocate(buffer.limit() *2);
          tmp.put(buffer);
          buffer = tmp;
        }
        buffer.put(currentChar);

        if (currentChar == LF_BYTE) {
          foundLineEnd = true;
        } else if (currentChar == CR_BYTE) {
          foundLineEnd = true;

          // Check next char. Consume \n if available
          // Is buffer refill required?
          if (limit == offset) {
            fillBuffer();
          }

          // Check if there is at least one character
          if (limit != EOF && this.buffer[offset] == LF_BYTE) {
            buffer.put(LF_BYTE);
            offset++;
          }
        }
      }
    }

    if(buffer.position() == 0) {
      return null;
    } else {
      String currentLine;
      if(readState.isReadBody()) {
        currentLine = new String(buffer.array(), 0, buffer.position(), getCurrentCharset());
      } else {
        currentLine = new String(buffer.array(), 0, buffer.position(), CS_ISO_8859_1);
      }
      updateCurrentCharset(currentLine);
      return currentLine;
    }
  }

  private boolean isEOF() {
    return limit == EOF;
  }

  private int fillBuffer() throws IOException {
    limit = reader.read(buffer, 0, buffer.length);
    offset = 0;

    return limit;
  }

  private Charset getCurrentCharset() {
    return currentCharset;
  }

  /**
   * Read state indicator (whether currently the <code>body</code> or <code>header</code> part is read).
   */
  private class ReadState {
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
