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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.olingo.client.core.edm.xml.v3.AssociationSetImpl;
import org.apache.olingo.client.core.edm.xml.v4.ActionImportImpl;
import org.apache.olingo.client.core.edm.xml.v4.AnnotationImpl;
import org.apache.olingo.client.core.edm.xml.v4.SingletonImpl;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

import java.io.IOException;

@SuppressWarnings("rawtypes")
public class EntityContainerDeserializer extends AbstractEdmDeserializer<AbstractEntityContainer> {

  @Override
  protected AbstractEntityContainer doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final AbstractEntityContainer entityContainer = ODataServiceVersion.V30 == version
            ? new org.apache.olingo.client.core.edm.xml.v3.EntityContainerImpl()
            : new org.apache.olingo.client.core.edm.xml.v4.EntityContainerImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Name".equals(jp.getCurrentName())) {
          entityContainer.setName(jp.nextTextValue());
        } else if ("Extends".equals(jp.getCurrentName())) {
          entityContainer.setExtends(jp.nextTextValue());
        } else if ("LazyLoadingEnabled".equals(jp.getCurrentName())) {
          entityContainer.setLazyLoadingEnabled(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("IsDefaultEntityContainer".equals(jp.getCurrentName())) {
          entityContainer.setDefaultEntityContainer(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("EntitySet".equals(jp.getCurrentName())) {
          jp.nextToken();
          if (entityContainer instanceof org.apache.olingo.client.core.edm.xml.v3.EntityContainerImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.EntityContainerImpl) entityContainer).
                    getEntitySets().add(jp.readValueAs(
                                    org.apache.olingo.client.core.edm.xml.v3.EntitySetImpl.class));
          } else {
            ((org.apache.olingo.client.core.edm.xml.v4.EntityContainerImpl) entityContainer).
                    getEntitySets().add(jp.readValueAs(
                                    org.apache.olingo.client.core.edm.xml.v4.EntitySetImpl.class));
          }
        } else if ("AssociationSet".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.client.core.edm.xml.v3.EntityContainerImpl) entityContainer).
                  getAssociationSets().add(jp.readValueAs(AssociationSetImpl.class));
        } else if ("Singleton".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.client.core.edm.xml.v4.EntityContainerImpl) entityContainer).
                  getSingletons().add(jp.readValueAs(SingletonImpl.class));
        } else if ("ActionImport".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.client.core.edm.xml.v4.EntityContainerImpl) entityContainer).
                  getActionImports().add(jp.readValueAs(ActionImportImpl.class));
        } else if ("FunctionImport".equals(jp.getCurrentName())) {
          jp.nextToken();
          if (entityContainer instanceof org.apache.olingo.client.core.edm.xml.v3.EntityContainerImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.EntityContainerImpl) entityContainer).
                    getFunctionImports().add(jp.readValueAs(
                                    org.apache.olingo.client.core.edm.xml.v3.FunctionImportImpl.class));
          } else {
            ((org.apache.olingo.client.core.edm.xml.v4.EntityContainerImpl) entityContainer).
                    getFunctionImports().add(jp.readValueAs(
                                    org.apache.olingo.client.core.edm.xml.v4.FunctionImportImpl.class));
          }
        } else if ("Annotation".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.client.core.edm.xml.v4.EntityContainerImpl) entityContainer).getAnnotations().
                  add(jp.readValueAs(AnnotationImpl.class));
        }
      }
    }

    return entityContainer;
  }
}
