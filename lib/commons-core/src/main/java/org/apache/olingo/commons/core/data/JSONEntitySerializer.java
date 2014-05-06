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
package org.apache.olingo.commons.core.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

/**
 * Writes out JSON string from an entity.
 */
public class JSONEntitySerializer extends AbstractJsonSerializer<JSONEntityImpl> {

  @Override
  protected void doSerialize(final JSONEntityImpl entity, final JsonGenerator jgen, final SerializerProvider provider)
          throws IOException, JsonProcessingException {

    doContainerSerialize(new ResWrap<JSONEntityImpl>((URI) null, null, entity), jgen, provider);
  }

  @Override
  protected void doContainerSerialize(
          final ResWrap<JSONEntityImpl> container, final JsonGenerator jgen, final SerializerProvider provider)
          throws IOException, JsonProcessingException {

    final Entity entity = container.getPayload();

    jgen.writeStartObject();

    if (serverMode) {
      if (container.getContextURL() != null) {
        jgen.writeStringField(version.compareTo(ODataServiceVersion.V40) >= 0
                ? Constants.JSON_CONTEXT : Constants.JSON_METADATA,
                container.getContextURL().getURI().toASCIIString());
      }
      if (version.compareTo(ODataServiceVersion.V40) >= 0 && StringUtils.isNotBlank(container.getMetadataETag())) {
        jgen.writeStringField(Constants.JSON_METADATA_ETAG, container.getMetadataETag());
      }

      if (StringUtils.isNotBlank(entity.getETag())) {
        jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_ETAG), entity.getETag());
      }
    }

    if (StringUtils.isNotBlank(entity.getType())) {
      jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_TYPE),
              new EdmTypeInfo.Builder().setTypeExpression(entity.getType()).build().external(version));
    }

    if (entity.getId() != null) {
      jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_ID), entity.getId());
    }

    for (Annotation annotation : entity.getAnnotations()) {
      valuable(jgen, annotation, "@" + annotation.getTerm());
    }

    for (Property property : entity.getProperties()) {
      valuable(jgen, property, property.getName());
    }

    if (serverMode && entity.getEditLink() != null && StringUtils.isNotBlank(entity.getEditLink().getHref())) {
      final URI link = URI.create(entity.getEditLink().getHref());
      final String editLink = link.isAbsolute() ? link.toASCIIString()
              : URI.create(entity.getBaseURI() + "/" + link.toASCIIString()).normalize().toASCIIString();

      jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_EDIT_LINK), editLink);
    }

    links(entity, jgen);

    for (Link link : entity.getMediaEditLinks()) {
      if (link.getTitle() == null) {
        jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_MEDIAEDIT_LINK), link.getHref());
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

    if (serverMode) {
      for (ODataOperation operation : entity.getOperations()) {
        jgen.writeObjectFieldStart("#" + StringUtils.substringAfterLast(operation.getMetadataAnchor(), "#"));
        jgen.writeStringField(Constants.ATTR_TITLE, operation.getTitle());
        jgen.writeStringField(Constants.ATTR_TARGET, operation.getTarget().toASCIIString());
        jgen.writeEndObject();
      }
    }

    jgen.writeEndObject();
  }
}
