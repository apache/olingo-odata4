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
package org.apache.olingo.odata4.client.core.deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.olingo.odata4.client.api.deserializer.ConsumerException;
import org.apache.olingo.odata4.client.api.deserializer.Entity;
import org.apache.olingo.odata4.client.api.deserializer.EntitySet;
import org.apache.olingo.odata4.client.api.deserializer.Property;
import org.apache.olingo.odata4.client.api.deserializer.Reader;
import org.apache.olingo.odata4.client.api.deserializer.StructuralProperty;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

public class JsonReader implements Reader {

  @Override
  public EntitySet readEntitySet(final InputStream in) throws ConsumerException {

    JsonFactory jsonFactory = new JsonFactory();
    // or, for data binding, org.codehaus.jackson.mapper.MappingJsonFactory
    try {
      JsonParser jp = jsonFactory.createParser(in);
      EntitySetBuilder entitySet = new EntitySetBuilder(jp);
      return entitySet.buildEntitySet();
    } catch (JsonParseException e) {
      throw new ConsumerException("JSON Parsing failed.", e);
    } catch (IOException e) {
      throw new ConsumerException("JSON Parsing failed.", e);
    }
  }

  @Override
  public Entity readEntity(final InputStream in) throws ConsumerException {
    Entity entity = null;

    JsonFactory jsonFactory = new JsonFactory();
    // or, for data binding, org.codehaus.jackson.mapper.MappingJsonFactory
    try {
      JsonParser jp = jsonFactory.createParser(in);
      PropertyCollectionBuilder builder = new PropertyCollectionBuilder(jp);
      builder.parseNext();
      entity = builder.buildEntity();
    } catch (JsonParseException e) {
      throw new ConsumerException("JSON Parsing failed.", e);
    } catch (IOException e) {
      throw new ConsumerException("JSON Parsing failed.", e);
    }

    return entity;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.olingo.core.consumer.Reader#parseProperty(java.io.InputStream)
   */
  @Override
  public Property readProperty(final InputStream in) throws ConsumerException {
    Entity entity = readEntity(in);

    Map<String, StructuralProperty> properties = entity.getStructuralProperties();
    if (properties.size() == 1) {
      return properties.values().iterator().next();
    }
    return null;
  }
}
