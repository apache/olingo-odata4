/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.proxy;

import static org.junit.Assert.assertEquals;

import com.msopentech.odatajclient.engine.data.ODataDuration;
import com.msopentech.odatajclient.proxy.api.EntityContainerFactory;
import com.msopentech.odatajclient.proxy.primitivekeysservice.microsoft.test.odata.services.primitivekeysservice.TestContext;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.Test;

/**
 * This is the unit test class to check basic feed operations.
 */
public class PrimitiveKeysTestITCase extends AbstractTest {

    @Test
    public void readPrimitiveKeys() {
        final EntityContainerFactory factory = EntityContainerFactory.getV3Instance(testPrimitiveKeysServiceRootURL);
        final TestContext pkcontainer = factory.getEntityContainer(TestContext.class);

        // commented as per #115
        //assertEquals(new byte[] {Byte.valueOf("2"), Byte.valueOf("3"), Byte.valueOf("4")}, 
        //        container.getEdmBinarySet().get(
        //        new byte[] {Byte.valueOf("2"), Byte.valueOf("3"), Byte.valueOf("4")}).getId());

        assertEquals(Boolean.TRUE, pkcontainer.getEdmBooleanSet().get(Boolean.TRUE).getId());
        assertEquals(Integer.valueOf(255), pkcontainer.getEdmByteSet().get(255).getId());
        assertEquals(new BigDecimal("79228162514264337593543950335"),
                pkcontainer.getEdmDecimalSet().get(new BigDecimal("79228162514264337593543950335")).getId());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"),
                pkcontainer.getEdmGuidSet().get(UUID.fromString("00000000-0000-0000-0000-000000000000")).getId());
        assertEquals(Integer.valueOf(-2147483648),
                pkcontainer.getEdmInt32Set().get(Integer.valueOf(-2147483648)).getId());
        assertEquals(Long.valueOf(9223372036854775807L),
                pkcontainer.getEdmInt64Set().get(Long.valueOf(9223372036854775807L)).getId());
        assertEquals("$", pkcontainer.getEdmStringSet().get("$").getId());
        assertEquals(new ODataDuration("-P10675199DT2H48M5.4775808S"),
                pkcontainer.getEdmTimeSet().get(new ODataDuration("-P10675199DT2H48M5.4775808S")).getId());
        assertEquals(32767, pkcontainer.getEdmInt16Set().get(Short.valueOf("32767")).getId().shortValue());
        assertEquals(Double.valueOf(1.7976931348623157E+308D),
                pkcontainer.getEdmDoubleSet().get(1.7976931348623157E+308D).getId());
        assertEquals(Float.valueOf(3.40282347E+38F),
                pkcontainer.getEdmSingleSet().get(Float.valueOf(3.40282347E+38F)).getId());

        // commented as per #115
        //assertEquals(
        //        ODataTimestamp.parse(EdmSimpleType.DATE_TIME.pattern(), "0001-01-01T00:00:00"),
        //        container.getEdmDateTimeSet().get(
        //        ODataTimestamp.parse(EdmSimpleType.DATE_TIME.pattern(), "0001-01-01T00:00:00")));
        //assertEquals(
        //        ODataTimestamp.parse(EdmSimpleType.DATE_TIME_OFFSET.pattern(), "2013-08-14T13:33:46.1045905+02:00"),
        //        container.getEdmDateTimeOffsetSet().get(
        //        ODataTimestamp.parse(EdmSimpleType.DATE_TIME_OFFSET.pattern(), "2013-08-14T13:33:46.1045905+02:00")));
    }
}
