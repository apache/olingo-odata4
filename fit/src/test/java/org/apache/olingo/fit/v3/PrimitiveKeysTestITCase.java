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
package org.apache.olingo.fit.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.UUID;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.format.ODataPubFormat;

import org.junit.Test;

public class PrimitiveKeysTestITCase extends AbstractTestITCase {

  private void readEntity(final String entityType, final Object key, final ODataPubFormat format) {
    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(
            client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment(entityType).
            appendKeySegment(key).
            build());
    req.setFormat(format);
    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());
    final ODataEntity entity = res.getBody();
    assertNotNull(entity);
    assertNotNull(entity.getProperty("Id"));
  }

  private void readPrimitiveKeys(final ODataPubFormat format) {
    readEntity("EdmBooleanSet", Boolean.TRUE, format);
    readEntity("EdmByteSet", 255, format);
    readEntity("EdmDecimalSet", new BigDecimal("79228162514264337593543950335"), format);
    readEntity("EdmDoubleSet", 1.7976931348623157E+308D, format);
    readEntity("EdmSingleSet", 3.4028235E+38F, format);
    readEntity("EdmGuidSet", UUID.fromString("00000000-0000-0000-0000-000000000000"), format);
    readEntity("EdmInt16Set", 32767, format);
    readEntity("EdmInt32Set", -2147483648, format);
    readEntity("EdmInt64Set", 9223372036854775807L, format);
    readEntity("EdmStringSet", "$", format);
  }

  @Test
  public void readEntityAsAtom() {
    readPrimitiveKeys(ODataPubFormat.ATOM);
  }

  @Test
  public void readEntityAsJSON() {
    readPrimitiveKeys(ODataPubFormat.JSON_FULL_METADATA);
  }
}
