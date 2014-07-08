/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.server.tecsvc.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Random;

/**
 *  
 */
public class StringHelper {

  public static class Stream {
    private final byte[] data;

    private Stream(final byte[] data) {
      this.data = data;
    }

    public Stream(final String content, final String charset) throws UnsupportedEncodingException {
      this(content.getBytes(charset));
    }

    public InputStream asStream() {
      return new ByteArrayInputStream(data);
    }

    public byte[] asArray() {
      return data;
    }

    public String asString() {
      return asString("UTF-8");
    }

    public String asString(final String charsetName) {
      return new String(data, Charset.forName(charsetName));
    }

    public Stream print(final OutputStream out) throws IOException {
      out.write(data);
      return this;
    }

    public Stream print() throws IOException {
      return print(System.out);
    }

    /**
     * Number of lines separated by line breaks (<code>CRLF</code>).
     * A content string like <code>text\r\nmoreText</code> will result in
     * a line count of <code>2</code>.
     * 
     * @return lines count
     */
    public int linesCount() {
      return StringHelper.countLines(asString(), "\r\n");
    }
  }

  public static Stream toStream(final InputStream stream) throws IOException {
    byte[] result = new byte[0];
    byte[] tmp = new byte[8192];
    int readCount = stream.read(tmp);
    while (readCount >= 0) {
      byte[] innerTmp = new byte[result.length + readCount];
      System.arraycopy(result, 0, innerTmp, 0, result.length);
      System.arraycopy(tmp, 0, innerTmp, result.length, readCount);
      result = innerTmp;
      readCount = stream.read(tmp);
    }
    stream.close();
    return new Stream(result);
  }

  public static Stream toStream(final String content) {
    try {
      return new Stream(content, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF-8 should be supported on each system.");
    }
  }

  public static String inputStreamToString(final InputStream in, final boolean preserveLineBreaks) throws IOException {
    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
    final StringBuilder stringBuilder = new StringBuilder();
    String line = null;

    while ((line = bufferedReader.readLine()) != null) {
      stringBuilder.append(line);
      if (preserveLineBreaks) {
        stringBuilder.append("\n");
      }
    }

    bufferedReader.close();

    final String result = stringBuilder.toString();

    return result;
  }

  public static int countLines(final String content) {
    return countLines(content, "\r\n");
  }

  public static int countLines(final String content, final String lineBreak) {
    if (content == null) {
      return -1;
    }

    int lastPos = content.indexOf(lineBreak);
    int count = 1;

    while (lastPos >= 0) {
      lastPos = content.indexOf(lineBreak, lastPos + 1);
      count++;
    }
    return count;
  }

  public static String inputStreamToString(final InputStream in) throws IOException {
    return inputStreamToString(in, false);
  }

  /**
   * Encapsulate given content in an {@link InputStream} with charset <code>UTF-8</code>.
   * 
   * @param content to encapsulate content
   * @return content as stream
   */
  public static InputStream encapsulate(final String content) {
    try {
      return encapsulate(content, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // we know that UTF-8 is supported
      throw new TestUtilRuntimeException("UTF-8 MUST be supported.", e);
    }
  }

  /**
   * Encapsulate given content in an {@link InputStream} with given charset.
   * 
   * @param content to encapsulate content
   * @param charset to be used charset
   * @return content as stream
   * @throws UnsupportedEncodingException if charset is not supported
   */
  public static InputStream encapsulate(final String content, final String charset)
      throws UnsupportedEncodingException {
    return new ByteArrayInputStream(content.getBytes(charset));
  }

  /**
   * Generate a string with given length containing random upper case characters ([A-Z]).
   * 
   * @param len length of to generated string
   * @return random upper case characters ([A-Z]).
   */
  public static InputStream generateDataStream(final int len) {
    return encapsulate(generateData(len));
  }

  /**
   * Generates a string with given length containing random upper case characters ([A-Z]).
   * @param len length of the generated string
   * @return random upper case characters ([A-Z])
   */
  public static String generateData(final int len) {
    Random random = new Random();
    StringBuilder b = new StringBuilder(len);
    for (int j = 0; j < len; j++) {
      final char c = (char) ('A' + random.nextInt('Z' - 'A' + 1));
      b.append(c);
    }
    return b.toString();
  }

  private static class TestUtilRuntimeException extends RuntimeException {
    public TestUtilRuntimeException(String message, Throwable cause) {
    }
  }
}
