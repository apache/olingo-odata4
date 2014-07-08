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
package org.apache.olingo.server.core.serializer.json;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.core.serialization.JsonDeserializer;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 */
public class JsonDataProvider {
  @Test
  public void testMe() throws Exception {
    FileInputStream fis = new FileInputStream("/tmp/ESAllPrim.json");
    ResWrap<EntitySet> wrapper = new JsonDeserializer(ODataServiceVersion.V40, true).toEntitySet(fis);
    EntitySet es = wrapper.getPayload();
    for (Entity entity : es.getEntities()) {
      System.out.println(entity);
    }

    ODataJsonSerializer serializer = new ODataJsonSerializer();
  }
}
