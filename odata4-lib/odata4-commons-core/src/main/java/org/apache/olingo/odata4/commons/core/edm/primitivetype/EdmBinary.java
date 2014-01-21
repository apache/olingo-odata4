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
package org.apache.olingo.odata4.commons.core.edm.primitivetype;

import org.apache.commons.codec.binary.Base64;
import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Binary.
 */
public class EdmBinary extends SingletonPrimitiveType {

  private static final EdmBinary instance = new EdmBinary();
  {
    uriPrefix = "binary'";
    uriSuffix = "'";
  }

  public static EdmBinary getInstance() {
    return instance;
  }

  @Override
  public Class<?> getDefaultType() {
    return byte[].class;
  }

  @Override
  public boolean validate(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) {
    return value == null ?
        isNullable == null || isNullable :
        Base64.isBase64(value) && validateMaxLength(value, maxLength);
  }

  private static boolean validateMaxLength(final String value, final Integer maxLength) {
    return maxLength == null ? true :
        // Every three bytes are represented as four base-64 characters.
        // Additionally, there could be up to two padding "=" characters
        // if the number of bytes is not a multiple of three.
        maxLength >= value.length() * 3 / 4 - (value.endsWith("==") ? 2 : value.endsWith("=") ? 1 : 0);
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    if (!Base64.isBase64(value)) {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
    }
    if (!validateMaxLength(value, maxLength)) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_FACETS_NOT_MATCHED.addContent(value, facets)");
    }

    final byte[] result = Base64.decodeBase64(value);

    if (returnType.isAssignableFrom(byte[].class)) {
      return returnType.cast(result);
    } else if (returnType.isAssignableFrom(Byte[].class)) {
      Byte[] byteArray = new Byte[result.length];
      for (int i = 0; i < result.length; i++) {
        byteArray[i] = result[i];
      }
      return returnType.cast(byteArray);
    } else {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType)");
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
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }

    if (maxLength != null && byteArrayValue.length > maxLength) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_FACETS_NOT_MATCHED.addContent(value, facets)");
    }

    return Base64.encodeBase64URLSafeString(byteArrayValue);
  }
}
