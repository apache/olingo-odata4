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
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class BufferedReaderIncludingLineEndings extends Reader {
  private static final char CR = '\r';
  private static final char LF = '\n';
  private static final int EOF = -1;
  private static final int BUFFER_SIZE = 8192;
  private Reader reader;
  private char[] buffer;
  private int offset = 0;
  private int limit = 0;

  public BufferedReaderIncludingLineEndings(final Reader reader) {
    this(reader, BUFFER_SIZE);
  }

  public BufferedReaderIncludingLineEndings(final Reader reader, final int bufferSize) {
    if (bufferSize <= 0) {
      throw new IllegalArgumentException("Buffer size must be greater than zero.");
    }

    this.reader = reader;
    buffer = new char[bufferSize];
  }

  @Override
  public int read(final char[] charBuffer, final int bufferOffset, final int length) throws IOException {
    if ((bufferOffset + length) > charBuffer.length) {
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
          charBuffer[currentOutputOffset++] = buffer[offset++];
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

    final StringBuilder stringBuffer = new StringBuilder();
    boolean foundLineEnd = false; // EOF will be considered as line ending

    while (!foundLineEnd) {
      // Is buffer refill required?
      if (limit == offset) {
        if (fillBuffer() == EOF) {
          foundLineEnd = true;
        }
      }

      if (!foundLineEnd) {
        char currentChar = buffer[offset++];
        stringBuffer.append(currentChar);

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
          if (limit != EOF && buffer[offset] == LF) {
            stringBuffer.append(LF);
            offset++;
          }
        }
      }
    }

    return (stringBuffer.length() == 0) ? null : stringBuffer.toString();
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  @Override
  public boolean ready() throws IOException {
    // Not EOF and buffer refill is not required
    return !isEOF() && !(limit == offset);
  }

  @Override
  public void reset() throws IOException {
    throw new IOException("Reset is not supported");
  }

  @Override
  public void mark(final int readAheadLimit) throws IOException {
    throw new IOException("Mark is not supported");
  }

  @Override
  public boolean markSupported() {
    return false;
  }

  @Override
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
}
