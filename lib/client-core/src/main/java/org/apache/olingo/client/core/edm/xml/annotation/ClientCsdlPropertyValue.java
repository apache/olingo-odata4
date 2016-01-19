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
package org.apache.olingo.client.core.edm.xml.annotation;

import java.io.IOException;
import java.io.Serializable;

import org.apache.olingo.client.core.edm.xml.AbstractClientCsdlEdmDeserializer;
import org.apache.olingo.client.core.edm.xml.ClientCsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPropertyValue;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ClientCsdlPropertyValue.PropertyValueDeserializer.class)
class ClientCsdlPropertyValue extends CsdlPropertyValue implements Serializable {

  private static final long serialVersionUID = -8437649215282645228L;

  static class PropertyValueDeserializer extends AbstractClientCsdlEdmDeserializer<ClientCsdlPropertyValue> {
    @Override
    protected ClientCsdlPropertyValue doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
        throws IOException {
      final ClientCsdlPropertyValue propValue = new ClientCsdlPropertyValue();
      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Property".equals(jp.getCurrentName())) {
            propValue.setProperty(jp.nextTextValue());
          } else if ("Annotation".equals(jp.getCurrentName())) {
            propValue.getAnnotations().add(jp.readValueAs(ClientCsdlAnnotation.class));
          } else if (isAnnotationConstExprConstruct(jp)) {
            propValue.setValue(parseAnnotationConstExprConstruct(jp));
          } else {
            propValue.setValue(jp.readValueAs(ClientCsdlDynamicExpression.class));
          }
        }
      }
      return propValue;
    }
  }
}
