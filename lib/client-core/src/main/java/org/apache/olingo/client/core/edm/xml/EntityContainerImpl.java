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
import org.apache.olingo.commons.api.edm.provider.EntityContainer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = EntityContainerImpl.EntityContainerDeserializer.class)
public class EntityContainerImpl extends EntityContainer {

  private static final long serialVersionUID = 5631432527646955795L;

  static class EntityContainerDeserializer extends AbstractEdmDeserializer<EntityContainerImpl> {

    @Override
    protected EntityContainerImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {

      final EntityContainerImpl entityContainer = new EntityContainerImpl();

      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Name".equals(jp.getCurrentName())) {
            entityContainer.setName(jp.nextTextValue());
          } else if ("Extends".equals(jp.getCurrentName())) {
            entityContainer.setExtendsContainer(jp.nextTextValue());
          } else if ("EntitySet".equals(jp.getCurrentName())) {
            jp.nextToken();
            entityContainer.getEntitySets().add(jp.readValueAs(EntitySetImpl.class));
          } else if ("Singleton".equals(jp.getCurrentName())) {
            jp.nextToken();
            entityContainer.getSingletons().add(jp.readValueAs(SingletonImpl.class));
          } else if ("ActionImport".equals(jp.getCurrentName())) {
            jp.nextToken();
            entityContainer.getActionImports().add(jp.readValueAs(ActionImportImpl.class));
          } else if ("FunctionImport".equals(jp.getCurrentName())) {
            jp.nextToken();
            entityContainer.getFunctionImports().add(jp.readValueAs(FunctionImportImpl.class));
          } else if ("Annotation".equals(jp.getCurrentName())) {
            jp.nextToken();
            entityContainer.getAnnotations().add(jp.readValueAs(AnnotationImpl.class));
          }
        }
      }

      return entityContainer;
    }
  }
}
