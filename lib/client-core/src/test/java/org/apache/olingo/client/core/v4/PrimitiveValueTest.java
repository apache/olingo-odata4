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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.olingo.client.api.ODataV4Client;
import org.apache.olingo.client.api.domain.ODataJClientEdmPrimitiveType;
import org.apache.olingo.client.api.domain.ODataDuration;
import org.apache.olingo.client.api.domain.ODataPrimitiveValue;
import org.apache.olingo.client.api.domain.ODataTimestamp;
import org.apache.olingo.client.api.domain.ODataValue;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.client.core.ODataClientFactory;
import org.junit.Test;

public class PrimitiveValueTest extends AbstractTest {

  @Override
  protected ODataV4Client getClient() {
    return v4Client;
  }

  @Test
  public void manageTimeOfDay() {
    // OData V4 only
    final String primitive = "-P9DT51M12.5063807S";
    try {
      new ODataPrimitiveValue.Builder(ODataClientFactory.getV3()).
              setType(ODataJClientEdmPrimitiveType.TimeOfDay).setText(primitive).build();
      fail();
    } catch (IllegalArgumentException iae) {
      // ignore
    }

    final ODataValue value = getClient().getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.TimeOfDay).setText(primitive).build();
    assertEquals(ODataJClientEdmPrimitiveType.TimeOfDay.toString(), value.asPrimitive().getTypeName());
    // performed cast to improve the check
    assertEquals(primitive, value.asPrimitive().<ODataDuration>toCastValue().toString());
  }

  @Test
  public void manageDate() {
    // OData V4 only
    final String primitive = "2013-01-10";
    try {
      new ODataPrimitiveValue.Builder(ODataClientFactory.getV3()).
              setType(ODataJClientEdmPrimitiveType.Date).setText(primitive).build();
      fail();
    } catch (IllegalArgumentException iae) {
      // ignore
    }

    final ODataValue value = getClient().getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.Date).setText(primitive).build();
    assertEquals(ODataJClientEdmPrimitiveType.Date.toString(), value.asPrimitive().getTypeName());
    // performed cast to improve the check
    assertEquals(primitive, value.asPrimitive().<ODataTimestamp>toCastValue().toString());
  }
}
