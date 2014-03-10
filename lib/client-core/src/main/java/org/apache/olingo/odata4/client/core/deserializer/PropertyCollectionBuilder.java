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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.olingo.odata4.client.api.deserializer.AnnotationProperty;
import org.apache.olingo.odata4.client.api.deserializer.ComplexValue;
import org.apache.olingo.odata4.client.api.deserializer.Entity;
import org.apache.olingo.odata4.client.api.deserializer.NavigationProperty;
import org.apache.olingo.odata4.client.api.deserializer.Property;
import org.apache.olingo.odata4.client.api.deserializer.StructuralProperty;
import org.apache.olingo.odata4.client.api.deserializer.Value;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyCollectionBuilder extends PropertyCollection {

  private static final Logger LOG = LoggerFactory.getLogger(PropertyCollectionBuilder.class);

  private JsonParser parser;

  private EntitySetImpl enclosingEntitySet;

  private PropertyCollectionBuilder next = null;

  public PropertyCollectionBuilder(final JsonParser parser) {
    this.parser = parser;
  }

  private PropertyCollectionBuilder() {
  }

  public PropertyCollectionBuilder(final JsonParser jp, final EntitySetImpl entitySet) {
    this(jp);
    enclosingEntitySet = entitySet;
  }

  public Entity buildEntity() {
    final Entity entity = new EntityImpl(annotationProperties, navigationProperties, structuralProperties);
    resetProperties();
    return entity;
  }

  public ComplexValue buildComplexValue() {
    final ComplexValue value = new ComplexValueImpl(annotationProperties, navigationProperties, structuralProperties);
    resetProperties();
    return value;
  }

  private void resetProperties() {
    annotationProperties = new HashMap<String, AnnotationProperty>();
    navigationProperties = new HashMap<String, NavigationProperty>();
    structuralProperties = new HashMap<String, StructuralProperty>();
  }

  public boolean hasNext() throws JsonParseException, IOException {
    if (parser.isClosed()) {
      return false;
    }
    next = parseNextObject(parser, this);
    return (next != null);
  }

  public boolean parseNext() {
    try {
      if (hasNext()) {
        if (next != null) {
          return true;
        }

        if (next == null) {
          parser.close();
          return false;
        }
        return true;
      }
    } catch (JsonParseException e) {
      LOG.error("While parsing", e);
    } catch (IOException e) {
      LOG.error("While parsing", e);
    }
    return false;

  }

  /**
   *
   * @param jp
   * @param builder
   * @return
   * @throws IOException
   * @throws JsonParseException
   */
  private PropertyCollectionBuilder parseNextObject(final JsonParser jp, final PropertyCollectionBuilder builder)
          throws JsonParseException, IOException {

    boolean endReached = readToStartObjectOrEnd(jp);
    if (endReached) {
      return null;
    }

    //
    String currentFieldName = null;
    List<Value> values = null;

    while (jp.nextToken() != null) {
      final JsonToken token = jp.getCurrentToken();
      switch (token) {
        case START_OBJECT:
          if (currentFieldName != null) {
            final ComplexValue cvp = parseNextObject(jp, new PropertyCollectionBuilder()).buildComplexValue();
            if (values == null) {
              builder.addProperty(new StructuralPropertyImpl(currentFieldName, cvp));
            } else {
              values.add(cvp);
            }
          }
          break;
        case END_OBJECT:
          return builder;
        case START_ARRAY:
          values = new ArrayList<Value>();
          break;
        case END_ARRAY:
          if (values != null) {
            builder.addProperty(new StructuralPropertyImpl(currentFieldName, values));
            values = null;
          }
          break;
        case FIELD_NAME:
          currentFieldName = jp.getCurrentName();
          break;
        case NOT_AVAILABLE:
          break;
        case VALUE_EMBEDDED_OBJECT:
          break;
        case VALUE_NULL:
          Property nullProperty = createProperty(jp.getCurrentName(), null);
          builder.addProperty(nullProperty);
          break;
        case VALUE_FALSE:
        case VALUE_NUMBER_FLOAT:
        case VALUE_NUMBER_INT:
        case VALUE_STRING:
        case VALUE_TRUE:
          if (values == null) {
            Property property = createProperty(jp.getCurrentName(), jp.getValueAsString());
            builder.addProperty(property);
          } else {
            PrimitiveValue value = new PrimitiveValue(jp.getValueAsString());
            values.add(value);
          }
          break;
        default:
          break;
      }
    }

    return null;
  }

  private boolean readToStartObjectOrEnd(final JsonParser jp) throws IOException, JsonParseException {
    final JsonToken endToken = JsonToken.START_OBJECT;
    JsonToken token = jp.getCurrentToken() == null ? jp.nextToken() : jp.getCurrentToken();
    while (token != null && token != endToken) {
      if (enclosingEntitySet != null) {
        switch (token) {
          case VALUE_FALSE:
          case VALUE_NUMBER_FLOAT:
          case VALUE_NUMBER_INT:
          case VALUE_TRUE:
          case VALUE_STRING:
            enclosingEntitySet.addAnnotation(jp.getCurrentName(), jp.getValueAsString());
            break;

          default:
            break;
        }
      }
      //
      token = jp.nextToken();
    }

    return token == null;
  }

  private Property createProperty(final String name, final String value) {
    if (name.contains("@")) {
      return new NavigationPropertyImpl(name, value);
    } else if (name.contains(".")) {
      return new AnnotationPropertyImpl(name, value);
    } else {
      return new StructuralPropertyImpl(name, new PrimitiveValue(value));
    }
  }
}
