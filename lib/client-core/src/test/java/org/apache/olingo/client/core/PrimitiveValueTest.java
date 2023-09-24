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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Calendar;

import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class PrimitiveValueTest extends AbstractTest {

  @Test
  public void timeOfDay() throws EdmPrimitiveTypeException {
    final Calendar expected = Calendar.getInstance();
    expected.clear();
    expected.set(2013, 0, 10, 21, 45, 17);

    final ClientValue value = client.getObjectFactory().newPrimitiveValueBuilder()
        .setType(EdmPrimitiveTypeKind.TimeOfDay).setValue(expected).build();
    assertEquals(EdmPrimitiveTypeKind.TimeOfDay, value.asPrimitive().getTypeKind());

    final Calendar actual = value.asPrimitive().toCastValue(Calendar.class);
    assertEquals(expected.get(Calendar.HOUR), actual.get(Calendar.HOUR));
    assertEquals(expected.get(Calendar.MINUTE), actual.get(Calendar.MINUTE));
    assertEquals(expected.get(Calendar.SECOND), actual.get(Calendar.SECOND));

    assertEquals("21:45:17", value.asPrimitive().toString());
  }

  @Test
  public void Date() throws EdmPrimitiveTypeException {
    final Calendar expected = Calendar.getInstance();
    expected.clear();
    expected.set(2013, 0, 10);

    final ClientValue value = client.getObjectFactory().newPrimitiveValueBuilder()
        .setType(EdmPrimitiveTypeKind.Date).setValue(expected).build();
    assertEquals(EdmPrimitiveTypeKind.Date, value.asPrimitive().getTypeKind());

    final Calendar actual = value.asPrimitive().toCastValue(Calendar.class);
    assertEquals(expected.get(Calendar.YEAR), actual.get(Calendar.YEAR));
    assertEquals(expected.get(Calendar.MONTH), actual.get(Calendar.MONTH));
    assertEquals(expected.get(Calendar.DATE), actual.get(Calendar.DATE));

    assertEquals("2013-01-10", value.asPrimitive().toString());
  }

  @Test
  public void testBigDecimalToStringConversion() throws EdmPrimitiveTypeException {
    final ClientValue leadingZerosDecimalValue = client.getObjectFactory().newPrimitiveValueBuilder()
            .setType(EdmPrimitiveTypeKind.Decimal).setValue(new BigDecimal("0.01")).build();
    final ClientValue arbitraryPrecisionDecimalValue = client.getObjectFactory().newPrimitiveValueBuilder()
            .setType(EdmPrimitiveTypeKind.Decimal).setValue(new BigDecimal(0.01)).build();

    assertEquals("0.01", leadingZerosDecimalValue.asPrimitive().toString());
    assertEquals("0.01000000000000000020816681711721685132943093776702880859375",
        arbitraryPrecisionDecimalValue.asPrimitive().toString());
  }
}
