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

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

public abstract class PrimitiveTypeBaseTest {

  private void expectErrorInValueToString(final EdmPrimitiveType instance,
      final Object value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode,
      final String message) {
    try {
      instance.valueToString(value, isNullable, maxLength, precision, scale, isUnicode);
      fail("Expected exception not thrown");
    } catch (final EdmPrimitiveTypeException e) {
      assertNotNull(e.getLocalizedMessage());
      assertThat(e.getLocalizedMessage(), containsString(message));
    }
  }

  private void expectErrorInValueToString(final EdmPrimitiveType instance, final Object value, final String message) {
    expectErrorInValueToString(instance, value, null, null, null, null, null, message);
  }

  protected void expectTypeErrorInValueToString(final EdmPrimitiveType instance, final Object value) {
    expectErrorInValueToString(instance, value, "value type");
  }

  protected void expectContentErrorInValueToString(final EdmPrimitiveType instance, final Object value) {
    expectErrorInValueToString(instance, value, "' is not valid.");
  }

  protected void expectFacetsErrorInValueToString(final EdmPrimitiveType instance, final Object value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) {
    expectErrorInValueToString(instance, value, isNullable, maxLength, precision, scale, isUnicode,
        "facets' constraints");
  }

  protected void expectNullErrorInValueToString(final EdmPrimitiveType instance) {
    expectErrorInValueToString(instance, null, false, null, null, null, null, "The value NULL is not allowed.");
  }

  private void expectErrorInValueOfString(final EdmPrimitiveType instance,
      final String value, final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<?> returnType,
      final String message) {

    try {
      instance.valueOfString(value, isNullable, maxLength, precision, scale, isUnicode, returnType);
      fail("Expected exception not thrown");
    } catch (final EdmPrimitiveTypeException e) {
      assertNotNull(e.getLocalizedMessage());
      assertThat(e.getLocalizedMessage(), containsString(message));
    }
  }

  protected void expectTypeErrorInValueOfString(final EdmPrimitiveType instance, final String value) {
    expectErrorInValueOfString(instance, value, null, null, null, null, null, Class.class,
        "The value type class java.lang.Class is not supported.");
  }

  protected void expectUnconvertibleErrorInValueOfString(final EdmPrimitiveType instance, final String value,
      final Class<?> type) {
    expectErrorInValueOfString(instance, value, true, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, true,
        type, "cannot be converted to");
  }

  protected void expectContentErrorInValueOfString(final EdmPrimitiveType instance, final String value) {
    expectErrorInValueOfString(instance, value, null, null, null, null, null, instance.getDefaultType(),
        "illegal content");
  }

  protected void expectFacetsErrorInValueOfString(final EdmPrimitiveType instance, final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) {
    expectErrorInValueOfString(instance, value, isNullable, maxLength, precision, scale, isUnicode,
        instance.getDefaultType(), "facets' constraints");
  }

  protected void expectNullErrorInValueOfString(final EdmPrimitiveType instance) {
    expectErrorInValueOfString(instance, null, false, null, null, null, null, instance.getDefaultType(),
        "The literal 'null' is not allowed.");
  }

  protected void expectErrorInFromUriLiteral(final EdmPrimitiveType instance, final String value) {
    try {
      instance.fromUriLiteral(value);
      fail("Expected exception not thrown");
    } catch (final EdmPrimitiveTypeException e) {
      assertNotNull(e.getLocalizedMessage());
      assertThat(e.getLocalizedMessage(), containsString("' has illegal content."));
    }
  }
}
