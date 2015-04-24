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
package org.apache.olingo.client.core.edm.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.olingo.commons.api.edm.provider.annotation.DynamicAnnotationExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.LabeledElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = ClientLabeledElement.LabeledElementDeserializer.class)
class ClientLabeledElement
        extends AbstractClientAnnotatableDynamicAnnotationExpression implements LabeledElement {

  private static final long serialVersionUID = 4909387630253341824L;

  private String name;

  private DynamicAnnotationExpression value;

  @Override
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public DynamicAnnotationExpression getValue() {
    return value;
  }

  public void setValue(final DynamicAnnotationExpression value) {
    this.value = value;
  }

  static class LabeledElementDeserializer extends AbstractClientEdmDeserializer<ClientLabeledElement> {
    @Override
    protected ClientLabeledElement doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
      final ClientLabeledElement element = new ClientLabeledElement();
      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Name".equals(jp.getCurrentName())) {
            element.setName(jp.nextTextValue());
          } else if ("Annotation".equals(jp.getCurrentName())) {
            element.getAnnotations().add(jp.readValueAs(ClientAnnotation.class));
          } else {
            element.setValue(jp.readValueAs(AbstractClientDynamicAnnotationExpression.class));
          }
        }
      }
      return element;
    }
  }
}
