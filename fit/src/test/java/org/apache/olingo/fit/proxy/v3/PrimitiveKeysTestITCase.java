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
package org.apache.olingo.fit.proxy.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.UUID;
import org.apache.olingo.client.api.v3.EdmEnabledODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.fit.proxy.v3.primitivekeys.Service;
import org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice.TestContext;
import org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice.types.EdmBoolean;
import org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice.types.EdmByte;
import org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice.types.EdmDecimal;
import org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice.types.EdmDouble;
import org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice.types.EdmGuid;
import org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice.types.EdmInt16;
import org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice.types.EdmInt32;
import org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice.types.EdmInt64;
import org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice.types.EdmSingle;
import org.apache.olingo.fit.proxy.v3.primitivekeys.microsoft.test.odata.services.primitivekeysservice.types.EdmString;
import org.junit.Test;

/**
 * This is the unit test class to check basic feed operations.
 */
public class PrimitiveKeysTestITCase extends AbstractTestITCase {

  @Test
  public void readPrimitiveKeys() {
    final Service<EdmEnabledODataClient> testContainerFactory =
            Service.getV3(testPrimitiveKeysServiceRootURL);
    testContainerFactory.getClient().getConfiguration().
            setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    final TestContext testContainer = testContainerFactory.getEntityContainer(TestContext.class);
    assertNotNull(testContainer);

    final EdmBoolean edmBooleanSet = testContainer.getEdmBooleanSet().getByKey(Boolean.TRUE).load();
    assertEquals(Boolean.TRUE, edmBooleanSet.getId());

    final EdmByte edmByteSet = testContainer.getEdmByteSet().getByKey(Short.valueOf("255")).load();
    assertEquals(Short.valueOf("255"), edmByteSet.getId());

    final EdmDecimal edmDecimalSet =
            testContainer.getEdmDecimalSet().getByKey(new BigDecimal("79228162514264337593543950335")).load();
    assertEquals(new BigDecimal("79228162514264337593543950335"), edmDecimalSet.getId());

    final EdmDouble edmDoubleSet = testContainer.getEdmDoubleSet().getByKey(1.7976931348623157E+308D).load();
    assertEquals(1.7976931348623157E+308D, edmDoubleSet.getId(), 0);

    final EdmSingle edmSingleSet = testContainer.getEdmSingleSet().getByKey(3.4028235E+38F).load();
    assertEquals(3.4028235E+38F, edmSingleSet.getId(), 0);

    final EdmGuid edmGuidSet =
            testContainer.getEdmGuidSet().getByKey(UUID.fromString("00000000-0000-0000-0000-000000000000")).load();
    assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), edmGuidSet.getId());

    final EdmInt16 edmInt16Set = testContainer.getEdmInt16Set().getByKey(Short.valueOf("32767")).load();
    assertEquals(Short.valueOf("32767"), edmInt16Set.getId(), 0);

    final EdmInt32 edmInt32Set = testContainer.getEdmInt32Set().getByKey(-2147483648).load();
    assertEquals(-2147483648, edmInt32Set.getId(), 0);

    final EdmInt64 edmInt64Set = testContainer.getEdmInt64Set().getByKey(9223372036854775807L).load();
    assertEquals(9223372036854775807L, edmInt64Set.getId(), 0);

    final EdmString edmStringSet = testContainer.getEdmStringSet().getByKey("$").load();
    assertEquals("$", edmStringSet.getId());
  }
}
