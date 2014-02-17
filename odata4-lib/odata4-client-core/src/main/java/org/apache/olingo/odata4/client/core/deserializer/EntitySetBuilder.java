/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.client.core.deserializer;

import java.io.IOException;

import org.apache.olingo.odata4.client.api.deserializer.EntitySet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class EntitySetBuilder {

  private final JsonParser parser;

  public EntitySetBuilder(final JsonParser jp) {
    parser = jp;
  }

  public EntitySet buildEntitySet() throws JsonParseException, IOException {
    return parseEntitySet(parser);
  }

  private EntitySet parseEntitySet(final JsonParser jp) throws JsonParseException, IOException {
    EntitySetImpl entitySet = new EntitySetImpl();
    boolean arrayStarted = false;

    while (jp.nextToken() != null) {
      JsonToken token = jp.getCurrentToken();
      switch (token) {
      case START_ARRAY:
        PropertyCollectionBuilder builder = new PropertyCollectionBuilder(jp, entitySet);
        entitySet.setPropertyCollectionBuilder(builder);
        arrayStarted = true;
        break;
      case START_OBJECT:
        if (arrayStarted) {
          return entitySet;
        }
        break;
      case VALUE_NUMBER_INT:
      case VALUE_STRING:
        entitySet.addAnnotation(jp.getCurrentName(), jp.getValueAsString());
      default:
        break;
      }
    }

    return entitySet;
  }
}
