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

package org.apache.olingo.client.core.v4;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonMetadataTest extends AbstractTest {


    @Override
    protected ODataClient getClient() {
        return v4Client;
    }

    @Test
    public void parse() {
        final Edm edm = getClient().getReader().
                readMetadata(getClass().getResourceAsStream("metadata.json"), ODataFormat.JSON);
        assertNotNull(edm);

        assertNotNull(edm.getEnumType(new FullQualifiedName("namespace", "ENString")));
        assertNotNull(edm.getEntityType(new FullQualifiedName("namespace", "ETAbstractBase")));
        assertNotNull(edm.getEntityContainer(new FullQualifiedName("namespace","container"))
                .getEntitySet("ESAllPrim"));
        assertEquals(edm.getEntityType(new FullQualifiedName("namespace", "ETAbstractBase")),
                edm.getEntityContainer(new FullQualifiedName("namespace","container"))
                        .getEntitySet("ESAllPrim").getEntityType());

    }
}
