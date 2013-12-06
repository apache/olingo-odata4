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
package org.apache.olingo.odata4.consumer.core.testutil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @author SAP AG
 */
public class StringHelper {

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
   * Generate a string with given length containing random upper case characters ([A-Z]).
   * 
   * @param len length of to generated string
   * @return random upper case characters ([A-Z]).
   */
  public static String generateData(final int len) {
    StringBuilder b = new StringBuilder(len);
    for (int j = 0; j < len; j++) {
      char c = (char) (Math.random() * 26 + 65);
      b.append(c);
    }
    return b.toString();
  }

}
