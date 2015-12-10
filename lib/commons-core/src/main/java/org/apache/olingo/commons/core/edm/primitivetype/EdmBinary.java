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
package org.apache.olingo.commons.core.edm.primitivetype;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Binary.
 */
public class EdmBinary extends SingletonPrimitiveType {

  private static final Charset UTF_8 = Charset.forName("UTF-8");

  /**
   * Byte used to pad output.
   *
   * <b>NOTE</b>: this is provided here from Commons Codec for Android compatibility.
   */
  private static final byte PAD_DEFAULT = '=';

  /**
   * This array is a lookup table that translates Unicode characters drawn from the "Base64 Alphabet" (as specified in
   * Table 1 of RFC 2045) into their 6-bit positive integer equivalents. Characters that are not in the Base64 alphabet
   * but fall within the bounds of the array are translated to -1.
   *
   * Note: '+' and '-' both decode to 62. '/' and '_' both decode to 63. This means decoder seamlessly handles both
   * URL_SAFE and STANDARD base64. (The encoder, on the other hand, needs to know ahead of time what to emit).
   *
   * Thanks to "commons" project in ws.apache.org for this code.
   * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
   *
   * <b>NOTE</b>: this is provided here from Commons Codec for Android compatibility.
   */
  private static final byte[] DECODE_TABLE = {
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54,
      55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
      5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
      24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34,
      35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51
  };

  private static final EdmBinary INSTANCE = new EdmBinary();

  {
    uriPrefix = "binary'";
    uriSuffix = "'";
  }

  public static EdmBinary getInstance() {
    return INSTANCE;
  }

  @Override
  public Class<?> getDefaultType() {
    return byte[].class;
  }

  /**
   * Checks if a byte value is whitespace or not. Whitespace is taken to mean: space, tab, CR, LF
   * <br/>
   * <b>NOTE</b>: this method is provided here from Commons Codec for Android compatibility.
   *
   * @param byteToCheck the byte to check
   * @return true if byte is whitespace, false otherwise
   */
  private static boolean isWhiteSpace(final byte byteToCheck) {
    switch (byteToCheck) {
    case ' ':
    case '\n':
    case '\r':
    case '\t':
      return true;
    default:
      return false;
    }
  }

  /**
   * Returns whether or not the <code>octet</code> is in the base 64 alphabet.
   * <br/>
   * <b>NOTE</b>: this method is provided here from Commons Codec for Android compatibility.
   *
   * @param octet The value to test
   * @return {@code true} if the value is defined in the the base 64 alphabet, {@code false} otherwise.
   * @since 1.4
   */
  private static boolean isBase64(final byte octet) {
    return octet == PAD_DEFAULT || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1);
  }

  /**
   * Tests a given byte array to see if it contains only valid characters within the Base64 alphabet. Currently the
   * method treats whitespace as valid.
   * <br/>
   * <b>NOTE</b>: this method is provided here from Commons Codec for Android compatibility.
   *
   * @param arrayOctet byte array to test
   * @return {@code true} if all bytes are valid characters in the Base64 alphabet or if the byte array is empty;
   * {@code false}, otherwise
   */
  private static boolean isBase64(final byte[] arrayOctet) {
    for (int i = 0; i < arrayOctet.length; i++) {
      if (!isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean validate(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) {

    return value == null ?
        isNullable == null || isNullable :
        isBase64(value.getBytes(UTF_8)) && validateMaxLength(value, maxLength);
  }

  private static boolean validateMaxLength(final String value, final Integer maxLength) {
    return maxLength == null ? true :
        // Every three bytes are represented as four base-64 characters.
        // Additionally, there could be up to two padding "=" characters
        // if the number of bytes is not a multiple of three,
        // and there could be line feeds, possibly with carriage returns.
        maxLength >= (value.length() - lineEndingsLength(value)) * 3 / 4
            - (value.endsWith("==") ? 2 : value.endsWith("=") ? 1 : 0);
  }

  private static int lineEndingsLength(final String value) {
    int result = 0;
    int index = 0;
    while ((index = value.indexOf('\n', index)) >= 0) {
      result += index > 0 && value.charAt(index - 1) == '\r' ? 2 : 1;
      index++;
    }
    return result;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {

    if (value == null || !isBase64(value.getBytes(UTF_8))) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
    }
    if (!validateMaxLength(value, maxLength)) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' does not match the facets' constraints.");
    }

    final byte[] result = Base64.decodeBase64(value.getBytes(UTF_8));

    if (returnType.isAssignableFrom(byte[].class)) {
      return returnType.cast(result);
    } else if (returnType.isAssignableFrom(Byte[].class)) {
      final Byte[] byteArray = new Byte[result.length];
      for (int i = 0; i < result.length; i++) {
        byteArray[i] = result[i];
      }
      return returnType.cast(byteArray);
    } else {
      throw new EdmPrimitiveTypeException("The value type " + returnType + " is not supported.");
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    byte[] byteArrayValue;
    if (value instanceof byte[]) {
      byteArrayValue = (byte[]) value;
    } else if (value instanceof Byte[]) {
      final int length = ((Byte[]) value).length;
      byteArrayValue = new byte[length];
      for (int i = 0; i < length; i++) {
        byteArrayValue[i] = ((Byte[]) value)[i].byteValue();
      }
    } else {
      throw new EdmPrimitiveTypeException("The value type " + value.getClass() + " is not supported.");
    }

    if (maxLength != null && byteArrayValue.length > maxLength) {
      throw new EdmPrimitiveTypeException("The value '" + value + "' does not match the facets' constraints.");
    }

    return new String(Base64.encodeBase64(byteArrayValue, false), UTF_8);
  }
}
