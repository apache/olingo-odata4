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
package org.apache.olingo.commons.core.serialization;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

import com.fasterxml.jackson.core.JsonGenerator;

public class JsonEntitySetSerializer extends JsonSerializer {

  public JsonEntitySetSerializer(final ODataServiceVersion version, final boolean serverMode) {
    super(version, serverMode);
  }

  protected void doSerialize(final EntitySet entitySet, final JsonGenerator jgen)
      throws IOException, EdmPrimitiveTypeException {
    doContainerSerialize(new ResWrap<EntitySet>((URI) null, null, entitySet), jgen);
  }

  protected void doContainerSerialize(final ResWrap<EntitySet> container, final JsonGenerator jgen)
      throws IOException, EdmPrimitiveTypeException {

    final EntitySet entitySet = container.getPayload();

    jgen.writeStartObject();

    if (serverMode) {
      if (container.getContextURL() != null) {
        jgen.writeStringField(version.compareTo(ODataServiceVersion.V40) >= 0
            ? Constants.JSON_CONTEXT : Constants.JSON_METADATA,
            container.getContextURL().getURI().toASCIIString());
      }

      if (version.compareTo(ODataServiceVersion.V40) >= 0 && StringUtils.isNotBlank(container.getMetadataETag())) {
        jgen.writeStringField(
            Constants.JSON_METADATA_ETAG,
            container.getMetadataETag());
      }
    }

    if (entitySet.getId() != null) {
      jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_ID), entitySet.getId().toASCIIString());
    }
    jgen.writeNumberField(version.getJSONMap().get(ODataServiceVersion.JSON_COUNT),
        entitySet.getCount() == null ? entitySet.getEntities().size() : entitySet.getCount());
    if (serverMode) {
      if (entitySet.getNext() != null) {
        jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_NEXT_LINK),
            entitySet.getNext().toASCIIString());
      }
      if (entitySet.getDeltaLink() != null) {
        jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_DELTA_LINK),
            entitySet.getDeltaLink().toASCIIString());
      }
    }

    for (Annotation annotation : entitySet.getAnnotations()) {
      valuable(jgen, annotation, "@" + annotation.getTerm());
    }

    jgen.writeArrayFieldStart(Constants.VALUE);
    final JsonEntitySerializer entitySerializer = new JsonEntitySerializer(version, serverMode);
    for (Entity entity : entitySet.getEntities()) {
      entitySerializer.doSerialize(entity, jgen);
    }
    jgen.writeEndArray();

    jgen.writeEndObject();
  }
}
