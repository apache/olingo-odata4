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
package org.apache.olingo.client.core.v4;

import java.util.Calendar;
import static org.junit.Assert.assertEquals;

import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class PrimitiveValueTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v4Client;
  }

  @Test
  public void manageTimeOfDay() throws EdmPrimitiveTypeException {
    final Calendar expected = Calendar.getInstance();
    expected.clear();
    expected.set(2013, 0, 10, 21, 45, 17);

    final ODataValue value = getClient().getPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.TimeOfDay).setValue(expected).build();
    assertEquals(EdmPrimitiveTypeKind.TimeOfDay, value.asPrimitive().getTypeKind());

    final Calendar actual = value.asPrimitive().toCastValue(Calendar.class);
    assertEquals(expected.get(Calendar.HOUR), actual.get(Calendar.HOUR));
    assertEquals(expected.get(Calendar.MINUTE), actual.get(Calendar.MINUTE));
    assertEquals(expected.get(Calendar.SECOND), actual.get(Calendar.SECOND));
    
    assertEquals("21:45:17", value.asPrimitive().toString());
  }

  @Test
  public void manageDate() throws EdmPrimitiveTypeException {
    final Calendar expected = Calendar.getInstance();
    expected.clear();
    expected.set(2013, 0, 10);

    final ODataValue value = getClient().getPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Date).setValue(expected).build();
    assertEquals(EdmPrimitiveTypeKind.Date, value.asPrimitive().getTypeKind());

    final Calendar actual = value.asPrimitive().toCastValue(Calendar.class);
    assertEquals(expected.get(Calendar.YEAR), actual.get(Calendar.YEAR));
    assertEquals(expected.get(Calendar.MONTH), actual.get(Calendar.MONTH));
    assertEquals(expected.get(Calendar.DATE), actual.get(Calendar.DATE));

    assertEquals("2013-01-10", value.asPrimitive().toString());
  }
}
