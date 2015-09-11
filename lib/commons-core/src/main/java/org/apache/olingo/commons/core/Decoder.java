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
package org.apache.olingo.commons.core;

import java.io.UnsupportedEncodingException;

/**
 * Decodes a Java String containing a percent-encoded UTF-8 String value
 * into a Java String (in its internal UTF-16 encoding).
 *
 */
public class Decoder {

  /**
   * Decodes a percent-encoded UTF-8 String value into a Java String
   * (in its internal UTF-16 encoding).
   * @param value the encoded String
   * @return the Java String
   * @throws IllegalArgumentException if value contains characters not representing UTF-8 bytes
   * or ends with an unfinished percent-encoded character
   * @throws NumberFormatException if the two characters after a percent character
   * are not hexadecimal digits
   */
  public static String decode(final String value) throws IllegalArgumentException, NumberFormatException {
    if (value == null) {
      return value;
    }

    // Use a tiny finite-state machine to handle decoding on byte level.
    // There are only three states:
    // -2: normal bytes
    // -1: a byte representing the percent character has been read
    // >= 0: a byte representing the first half-byte of a percent-encoded byte has been read
    // The variable holding the state is also used to store the value of the first half-byte.
    byte[] result = new byte[value.length()];
    int position = 0;
    byte encodedPart = -2;
    for (final char c : value.toCharArray()) {
      if (c <= Byte.MAX_VALUE) {
        if (c == '%') {
          if (encodedPart == -2) {
            encodedPart = -1;
          } else {
            throw new IllegalArgumentException();
          }
        } else if (encodedPart == -1) {
          encodedPart = (byte) c;
        } else if (encodedPart >= 0) {
          final int i = Integer.parseInt(String.valueOf(new char[] { (char) encodedPart, c }), 16);
          if (i >= 0) {
            result[position++] = (byte) i;
          } else {
            throw new NumberFormatException();
          }
          encodedPart = -2;
        } else {
          result[position++] = (byte) c;
        }
      } else {
        throw new IllegalArgumentException();
      }
    }

    if (encodedPart >= 0) {
      throw new IllegalArgumentException();
    }

    try {
      return new String(result, 0, position, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
