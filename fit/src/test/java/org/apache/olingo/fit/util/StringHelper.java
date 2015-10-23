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
 ******************************************************************************/
package org.apache.olingo.fit.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Random;

/**
 *  
 */
public class StringHelper {

  public static final String DEFAULT_ENCODING = "UTF-8";

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

    public int byteLength() {
      return data == null ? -1 : data.length;
    }

    public String asString() {
      return asString(DEFAULT_ENCODING);
    }

    public String asString(final String charsetName) {
      return new String(data, Charset.forName(charsetName));
    }

    public String asStringWithLineSeparation(String separator) throws IOException {
      BufferedReader br = new BufferedReader(new StringReader(asString()));
      StringBuilder sb = new StringBuilder(br.readLine());
      String line = br.readLine();
      while (line != null) {
        sb.append(separator).append(line);
        line = br.readLine();
      }
      return sb.toString();
    }

    public InputStream asStreamWithLineSeparation(String separator) throws IOException {
      String asString = asStringWithLineSeparation(separator);
      return new ByteArrayInputStream(asString.getBytes(DEFAULT_ENCODING));
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
    return toStream(content, DEFAULT_ENCODING);
  }

  public static Stream toStream(final String content, final String charset) {
    try {
      return new Stream(content, charset);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF-8 should be supported on each system.");
    }
  }

  /**
   * Read the input stream into an string with default encoding (see StringHelper.DEFAULT_ENCODING).
   *
   * @param in stream which is read
   * @return content of stream as string
   * @throws IOException
   */
  public static String asString(final InputStream in) throws IOException {
    return toStream(in).asString();
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

  /**
   * Encapsulate given content in an {@link InputStream} with charset <code>UTF-8</code>.
   *
   * @param content to encapsulate content
   * @return content as stream
   */
  public static InputStream encapsulate(final String content) {
    try {
      return encapsulate(content, DEFAULT_ENCODING);
    } catch (UnsupportedEncodingException e) {
      // we know that UTF-8 is supported
      throw new RuntimeException("UTF-8 MUST be supported.", e);
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
}
