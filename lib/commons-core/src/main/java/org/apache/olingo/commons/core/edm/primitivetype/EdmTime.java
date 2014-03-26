/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.core.edm.primitivetype;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Time.
 */
public final class EdmTime extends SingletonPrimitiveType {

  private static final EdmTime INSTANCE = new EdmTime();

  public static EdmTime getInstance() {
    return INSTANCE;
  }

  {
    uriPrefix = "time'";
    uriSuffix = "'";
  }

  @Override
  public Class<?> getDefaultType() {
    return Duration.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
          final Boolean isNullable, final Integer maxLength, final Integer precision,
          final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {

    Duration duration = null;
    try {
      final DatatypeFactory dtFactory = DatatypeFactory.newInstance();
      duration = dtFactory.newDuration(value);
    } catch (Exception e) {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)", e);
    }

    if (returnType.isAssignableFrom(Duration.class)) {
      return returnType.cast(duration);
    } else {
      throw new EdmPrimitiveTypeException(
              "EdmPrimitiveTypeException.LITERAL_UNCONVERTIBLE_TO_VALUE_TYPE.addContent(value, returnType)");
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
          final Boolean isNullable, final Integer maxLength, final Integer precision,
          final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (value instanceof Duration) {
      final Duration duration = (Duration) value;
      return duration.toString();
    } else {
      throw new EdmPrimitiveTypeException(
              "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }

  }
}
