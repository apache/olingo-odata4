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
package org.apache.olingo.server.core.serializer.json;

import java.io.IOException;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.SerializerException;

import com.fasterxml.jackson.core.JsonGenerator;

public class ServiceDocumentJsonSerializer {
  public static final String KIND = "kind";

  public static final String FUNCTION_IMPORT = "FunctionImport";
  public static final String SINGLETON = "Singleton";
  public static final String SERVICE_DOCUMENT = "ServiceDocument";

  private final ServiceMetadata metadata;
  private final String serviceRoot;
  private final boolean isODataMetadataNone;

  public ServiceDocumentJsonSerializer(final ServiceMetadata metadata, final String serviceRoot,
      final boolean isODataMetadataNone) throws SerializerException {
    if (metadata == null || metadata.getEdm() == null) {
      throw new SerializerException("Service Metadata and EDM must not be null for a service.",
          SerializerException.MessageKeys.NULL_METADATA_OR_EDM);
    }
    this.metadata = metadata;
    this.serviceRoot = serviceRoot;
    this.isODataMetadataNone = isODataMetadataNone;
  }

  public void writeServiceDocument(final JsonGenerator gen) throws IOException {
    gen.writeStartObject();

    if (!isODataMetadataNone) {
      final String metadataUri =
          (serviceRoot == null ? "" :
              serviceRoot.endsWith("/") ? serviceRoot : (serviceRoot + "/"))
              + Constants.METADATA;
      gen.writeObjectField(Constants.JSON_CONTEXT, metadataUri);

      if (metadata != null
          && metadata.getServiceMetadataETagSupport() != null
          && metadata.getServiceMetadataETagSupport().getMetadataETag() != null) {
        gen.writeStringField(Constants.JSON_METADATA_ETAG,
            metadata.getServiceMetadataETagSupport().getMetadataETag());
      }
    }

    gen.writeArrayFieldStart(Constants.VALUE);
    if(metadata != null){
      final EdmEntityContainer container = metadata.getEdm().getEntityContainer();
      if (container != null) {
        writeEntitySets(gen, container);
        writeFunctionImports(gen, container);
        writeSingletons(gen, container);
      }
    }
  }

  private void writeEntitySets(final JsonGenerator gen, final EdmEntityContainer container) throws IOException {
    for (EdmEntitySet edmEntitySet : container.getEntitySets()) {
      if (edmEntitySet.isIncludeInServiceDocument()) {
        writeElement(gen, null, edmEntitySet.getName(), edmEntitySet.getName(), edmEntitySet.getTitle());
      }
    }
  }

  private void writeFunctionImports(final JsonGenerator gen, final EdmEntityContainer container) throws IOException {
    for (EdmFunctionImport edmFI : container.getFunctionImports()) {
      if (edmFI.isIncludeInServiceDocument()) {
        writeElement(gen, FUNCTION_IMPORT, edmFI.getName(), edmFI.getName(), edmFI.getTitle());
      }
    }
  }

  private void writeSingletons(final JsonGenerator gen, final EdmEntityContainer container) throws IOException {
    for (EdmSingleton edmSingleton : container.getSingletons()) {
      writeElement(gen, SINGLETON, edmSingleton.getName(), edmSingleton.getName(), edmSingleton.getTitle());
    }
  }

  private void writeElement(final JsonGenerator gen, final String kind, final String reference, final String name,
      final String title)
      throws IOException {
    gen.writeStartObject();
    gen.writeObjectField(Constants.JSON_NAME, name);
    if (title != null) {
      gen.writeObjectField(Constants.JSON_TITLE, title);
    }
    gen.writeObjectField(Constants.JSON_URL, reference);
    if (kind != null) {
      gen.writeObjectField(KIND, kind);
    }
    gen.writeEndObject();
  }
}
