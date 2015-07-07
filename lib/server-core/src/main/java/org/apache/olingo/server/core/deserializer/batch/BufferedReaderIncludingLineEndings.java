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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BufferedReaderIncludingLineEndings {
  private static final byte CR = '\r';
  private static final byte LF = '\n';
  private static final int EOF = -1;
  private static final int BUFFER_SIZE = 8192;
  public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
  private InputStream reader;
  private byte[] buffer;
  private int offset = 0;
  private int limit = 0;

  public BufferedReaderIncludingLineEndings(final InputStream reader) {
    this(reader, BUFFER_SIZE);
  }

  public BufferedReaderIncludingLineEndings(final InputStream reader, final int bufferSize) {
    if (bufferSize <= 0) {
      throw new IllegalArgumentException("Buffer size must be greater than zero.");
    }

    this.reader = reader;
    buffer = new byte[bufferSize];
  }

  public int read(final byte[] byteBuffer, final int bufferOffset, final int length) throws IOException {
    if ((bufferOffset + length) > byteBuffer.length) {
      throw new IndexOutOfBoundsException("Buffer is too small");
    }

    if (length < 0 || bufferOffset < 0) {
      throw new IndexOutOfBoundsException("Offset and length must be grater than zero");
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

  public List<String> toList() throws IOException {
    final List<String> result = new ArrayList<String>();
    String currentLine;

    while ((currentLine = readLine()) != null) {
      result.add(currentLine);
    }

    return result;
  }

  private Charset currentCharset = DEFAULT_CHARSET;

  private void updateCurrentCharset(String currentLine) {
    if(currentLine != null) {
      if(currentLine.startsWith("Content-Type:") && currentLine.contains(ContentType.PARAMETER_CHARSET)) {
        currentLine = currentLine.substring(13, currentLine.length()-2).trim();
        ContentType t = ContentType.parse(currentLine);
        if(t != null) {
          String charsetString = t.getParameter(ContentType.PARAMETER_CHARSET);
          currentCharset = Charset.forName(charsetString);
        }
      } else if(isEndBoundary(currentLine)) {
        currentCharset = Charset.forName("us-ascii");
      }
    }
  }

  private boolean isEndBoundary(String currentLine) {
    return false;
  }

  public List<Line> toLineList() throws IOException {
    final List<Line> result = new ArrayList<Line>();
    String currentLine;
    int counter = 1;

    while ((currentLine = readLine()) != null) {
      result.add(new Line(currentLine, counter++));
    }

    return result;
  }

  public String readLine() throws IOException {
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

        if (currentChar == LF) {
          foundLineEnd = true;
        } else if (currentChar == CR) {
          foundLineEnd = true;

          // Check next char. Consume \n if available
          // Is buffer refill required?
          if (limit == offset) {
            fillBuffer();
          }

          // Check if there is at least one character
          if (limit != EOF && this.buffer[offset] == LF) {
            buffer.put(LF);
            offset++;
          }
        }
      }
    }

    if(buffer.position() == 0) {
      return null;
    } else {
      String currentLine = new String(buffer.array(), 0, buffer.position(), getCurrentCharset());
      updateCurrentCharset(currentLine);
      return currentLine;
    }
  }

  public void close() throws IOException {
    reader.close();
  }

  public long skip(final long n) throws IOException {
    if (n == 0) {
      return 0;
    } else if (n < 0) {
      throw new IllegalArgumentException("skip value is negative");
    } else {
      long charactersToSkip = n;
      long charactersSkiped = 0;

      while (charactersToSkip != 0) {
        // Is buffer refill required?
        if (limit == offset) {
          fillBuffer();

          if (isEOF()) {
            charactersToSkip = 0;
          }
        }

        // Check if more characters are available
        if (!isEOF()) {
          int skipChars = (int) Math.min(limit - offset, charactersToSkip);

          charactersSkiped += skipChars;
          charactersToSkip -= skipChars;
          offset += skipChars;
        }
      }

      return charactersSkiped;
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
}
