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

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

public class ServiceDocumentJsonSerializer {
  public static final String ODATA_CONTEXT = "@odata.context";
  public static final String METADATA = "$metadata";
  public static final String VALUE = "value";
  public static final String NAME = "name";
  public static final String URL = "url";
  public static final String KIND = "kind";

  public static final String FUNCTION_IMPORT = "FunctionImport";
  public static final String SINGLETON = "Singleton";
  public static final String SERVICE_DOCUMENT = "ServiceDocument";

  private final Edm edm;
  private final String serviceRoot;

  public ServiceDocumentJsonSerializer(final Edm edm, final String serviceRoot) {
    this.edm = edm;
    this.serviceRoot = serviceRoot;
  }

  public void writeServiceDocument(final JsonGenerator gen) throws JsonGenerationException, IOException {
    gen.writeStartObject();

    Object metadataUri = serviceRoot + "/" + METADATA;
    gen.writeObjectField(ODATA_CONTEXT, metadataUri);
    gen.writeArrayFieldStart(VALUE);

    writeEntitySets(gen, edm);
    writeFunctionImports(gen, edm);
    writeSingletons(gen, edm);
  }

  private void writeEntitySets(final JsonGenerator gen, final Edm edm) throws JsonGenerationException, IOException {
    EdmEntityContainer container = edm.getEntityContainer(null);

    for (EdmEntitySet edmEntitySet : container.getEntitySets()) {
      if (edmEntitySet.isIncludeInServiceDocument()) {
        gen.writeStartObject();
        gen.writeObjectField(NAME, edmEntitySet.getName());
        gen.writeObjectField(URL, edmEntitySet.getName());
        gen.writeEndObject();
      }
    }
  }

  private void writeFunctionImports(final JsonGenerator gen, final Edm edm) throws JsonGenerationException,
      IOException {
    EdmEntityContainer container = edm.getEntityContainer(null);

    for (EdmFunctionImport edmFunctionImport : container.getFunctionImports()) {
      if (edmFunctionImport.isIncludeInServiceDocument()) {
        gen.writeStartObject();
        gen.writeObjectField(NAME, edmFunctionImport.getName());
        gen.writeObjectField(URL, edmFunctionImport.getName());
        gen.writeObjectField(KIND, FUNCTION_IMPORT);
        gen.writeEndObject();
      }
    }
  }

  private void writeSingletons(final JsonGenerator gen, final Edm edm) throws JsonGenerationException, IOException {
    EdmEntityContainer container = edm.getEntityContainer(null);

    for (EdmSingleton edmSingleton : container.getSingletons()) {
      gen.writeStartObject();
      gen.writeObjectField(NAME, edmSingleton.getName());
      gen.writeObjectField(URL, edmSingleton.getName());
      gen.writeObjectField(KIND, SINGLETON);
      gen.writeEndObject();
    }
  }
}
