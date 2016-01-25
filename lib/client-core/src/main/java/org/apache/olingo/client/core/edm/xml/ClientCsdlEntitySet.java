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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.io.Serializable;

@JsonDeserialize(using = ClientCsdlEntitySet.EntitySetDeserializer.class)
class ClientCsdlEntitySet extends CsdlEntitySet implements Serializable {

  private static final long serialVersionUID = -5553885465204370676L;

  static class EntitySetDeserializer extends AbstractClientCsdlEdmDeserializer<CsdlEntitySet> {
    @Override
    protected CsdlEntitySet doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {

      final ClientCsdlEntitySet entitySet = new ClientCsdlEntitySet();

      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Name".equals(jp.getCurrentName())) {
            entitySet.setName(jp.nextTextValue());
          } else if ("EntityType".equals(jp.getCurrentName())) {
            entitySet.setType(jp.nextTextValue());
          } else if ("IncludeInServiceDocument".equals(jp.getCurrentName())) {
            entitySet.setIncludeInServiceDocument(BooleanUtils.toBoolean(jp.nextTextValue()));
          } else if ("NavigationPropertyBinding".equals(jp.getCurrentName())) {
            jp.nextToken();
            entitySet.getNavigationPropertyBindings().add(jp.readValueAs(ClientCsdlNavigationPropertyBinding.class));
          } else if ("Annotation".equals(jp.getCurrentName())) {
            jp.nextToken();
            entitySet.getAnnotations().add(jp.readValueAs(ClientCsdlAnnotation.class));
          }
        }
      }

      return entitySet;
    }
  }
}
