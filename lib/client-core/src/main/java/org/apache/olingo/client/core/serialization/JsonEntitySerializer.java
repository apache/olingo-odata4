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
package org.apache.olingo.client.core.serialization;

import java.io.IOException;

import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Operation;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Writes out JSON string from an entity.
 */
public class JsonEntitySerializer extends JsonSerializer {

  public JsonEntitySerializer(final boolean serverMode, final ContentType contentType) {
    super(serverMode, contentType);
  }

  protected void doSerialize(final Entity entity, final JsonGenerator jgen)
      throws IOException, EdmPrimitiveTypeException {

    doContainerSerialize(new ResWrap<Entity>(null, null, entity), jgen);
  }

  protected void doContainerSerialize(final ResWrap<Entity> container, final JsonGenerator jgen)
      throws IOException, EdmPrimitiveTypeException {

    final Entity entity = container.getPayload();

    jgen.writeStartObject();

    if (serverMode && !isODataMetadataNone) {
      if (container.getContextURL() != null) {
        jgen.writeStringField(Constants.JSON_CONTEXT, container.getContextURL().toASCIIString());
      }
      if (container.getMetadataETag() != null) {
        jgen.writeStringField(Constants.JSON_METADATA_ETAG, container.getMetadataETag());
      }

      if (entity.getETag() != null) {
        jgen.writeStringField(Constants.JSON_ETAG, entity.getETag());
      }
    }

    if (entity.getType() != null && isODataMetadataFull) {
      jgen.writeStringField(Constants.JSON_TYPE,
          new EdmTypeInfo.Builder().setTypeExpression(entity.getType()).build().external());
    }

    if (entity.getId() != null && isODataMetadataFull) {
      jgen.writeStringField(Constants.JSON_ID, entity.getId().toASCIIString());
    }

    for (Annotation annotation : entity.getAnnotations()) {
      valuable(jgen, annotation, "@" + annotation.getTerm());
    }

    for (Property property : entity.getProperties()) {
      valuable(jgen, property, property.getName());
    }

    if (serverMode && entity.getEditLink() != null && 
        entity.getEditLink().getHref() != null && isODataMetadataFull) {
      jgen.writeStringField(Constants.JSON_EDIT_LINK, entity.getEditLink().getHref());

      if (entity.isMediaEntity() && isODataMetadataFull) {
        jgen.writeStringField(Constants.JSON_MEDIA_READ_LINK,
            entity.getEditLink().getHref() + "/$value");
      }
    }

    if (!isODataMetadataNone) {
      links(entity, jgen);
    }

    if (isODataMetadataFull) {
      for (Link link : entity.getMediaEditLinks()) {
        if (link.getTitle() == null) {
          jgen.writeStringField(Constants.JSON_MEDIA_EDIT_LINK, link.getHref());
        }

        if (link.getInlineEntity() != null) {
          jgen.writeObjectField(link.getTitle(), link.getInlineEntity());
        }
        if (link.getInlineEntitySet() != null) {
          jgen.writeArrayFieldStart(link.getTitle());
          for (Entity subEntry : link.getInlineEntitySet().getEntities()) {
            jgen.writeObject(subEntry);
          }
          jgen.writeEndArray();
        }
      }
    }

    if (serverMode && isODataMetadataFull) {
      for (Operation operation : entity.getOperations()) {
        final String anchor = operation.getMetadataAnchor();
        final int index = anchor.lastIndexOf('#');
        jgen.writeObjectFieldStart('#' + anchor.substring(index < 0 ? 0 : (index + 1)));
        jgen.writeStringField(Constants.ATTR_TITLE, operation.getTitle());
        jgen.writeStringField(Constants.ATTR_TARGET, operation.getTarget().toASCIIString());
        jgen.writeEndObject();
      }
    }

    jgen.writeEndObject();
  }
}
