/**
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
package com.msopentech.odatajclient.engine.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.RetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataDuration;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.Test;

public class PrimitiveKeysTestITCase extends AbstractTestITCase {

    private void readEntity(final String entityType, final Object key, final ODataPubFormat format) {
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(
                client.getURIBuilder(testPrimitiveKeysServiceRootURL).appendEntityTypeSegment(entityType).
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
        // commented as per #115
        //readEntity("EdmBinarySet", new byte[] {Byte.valueOf("2"), Byte.valueOf("3"), Byte.valueOf("4")}, format);
        readEntity("EdmBooleanSet", Boolean.TRUE, format);
        readEntity("EdmByteSet", 255, format);
        readEntity("EdmDecimalSet", new BigDecimal("79228162514264337593543950335"), format);
        readEntity("EdmDoubleSet", 1.7976931348623157E+308D, format);
        readEntity("EdmSingleSet", 3.40282347E+38F, format);
        readEntity("EdmGuidSet", UUID.fromString("00000000-0000-0000-0000-000000000000"), format);
        readEntity("EdmInt16Set", 32767, format);
        readEntity("EdmInt32Set", -2147483648, format);
        readEntity("EdmInt64Set", 9223372036854775807L, format);
        readEntity("EdmStringSet", "$", format);
        readEntity("EdmTimeSet", new ODataDuration("-P10675199DT2H48M5.4775808S"), format);
        // commented as per #115
        //readEntity("EdmDateTimeSet",
        //        ODataTimestamp.parse(EdmSimpleType.DATE_TIME.pattern(), "0001-01-01T00:00:00"),
        //        format);
        //readEntity("EdmDateTimeOffsetSet",
        //        ODataTimestamp.parse(EdmSimpleType.DATE_TIME_OFFSET.pattern(), "2013-08-14T13:33:46.1045905+02:00"),
        //        format);
    }

    @Test
    public void readEntityAsAtom() {
        readPrimitiveKeys(ODataPubFormat.ATOM);
    }

    @Test
    public void readEntityAsJSON() {
        readPrimitiveKeys(ODataPubFormat.JSON);
    }
}
