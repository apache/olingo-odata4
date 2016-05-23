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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Stream as URI.
 */
public final class EdmStream extends SingletonPrimitiveType {

  private static final EdmStream INSTANCE = new EdmStream();

  public static EdmStream getInstance() {
    return INSTANCE;
  }

  @Override
  public Class<?> getDefaultType() {
    return URI.class;
  }

  @Override
  public boolean validate(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) {

    if (value == null) {
      return isNullable == null || isNullable;
    }

    try {
      new URI(value);
      return true;
    } catch (final URISyntaxException e) {
      return false;
    }
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {

    URI stream = null;
    try {
      stream = new URI(value);
    } catch (final URISyntaxException e) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.", e);
    }

    if (returnType.isAssignableFrom(URI.class)) {
      return returnType.cast(stream);
    } else if (returnType.isAssignableFrom(Link.class)) {
      Link link = new Link();
      link.setHref(value);
      return returnType.cast(link);
    } else {
      throw new EdmPrimitiveTypeException("The value type " + returnType + " is not supported.");
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (value instanceof URI) {
      return ((URI) value).toASCIIString();
    } else if (value instanceof Link) {
      return ((Link)value).getHref();
    } else {
      throw new EdmPrimitiveTypeException("The value type " + value.getClass() + " is not supported.");
    }
  }
}
