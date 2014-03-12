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
package org.apache.olingo.server.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.server.api.ODataSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class ODataJsonSerializer implements ODataSerializer {

  private static final Logger log = LoggerFactory.getLogger(ODataJsonSerializer.class);

  private static final String DEFAULT_CHARSET = "UTF-8";

  public static final String ODATA_CONTEXT = "@odata.context";
  public static final String METADATA = "$metadata";
  public static final String VALUE = "value";
  public static final String NAME = "name";
  public static final String URL = "url";
  public static final String KIND = "kind";

  public static final String FUNCTION_IMPORT = "FunctionImport";
  public static final String SINGLETON = "Singleton";
  public static final String SERVICE_DOCUMENT = "ServiceDocument";

  @Override
  public InputStream metadata(Edm edm) {
    throw new ODataRuntimeException("Metadata in JSON format not supported!");
  }

  @Override
  public InputStream serviceDocument(Edm edm, String serviceRoot) {
    CircleStreamBuffer buffer;
    BufferedWriter writer;
    JsonFactory factory;
    JsonGenerator gen = null;

    try {
      buffer = new CircleStreamBuffer();
      writer = new BufferedWriter(new OutputStreamWriter(buffer.getOutputStream(), DEFAULT_CHARSET));
      factory = new JsonFactory();
      gen = factory.createGenerator(writer);

      gen.setPrettyPrinter(new DefaultPrettyPrinter());

      gen.writeStartObject();

      Object metadataUri = serviceRoot + "/" + METADATA;
      gen.writeObjectField(ODATA_CONTEXT, metadataUri);
      gen.writeArrayFieldStart(VALUE);

      writeEntitySets(gen, edm);
      writeFunctionImports(gen, edm);
      writeSingletons(gen, edm);

      gen.close();

//      writer.flush();
//      buffer.closeWrite();

      return buffer.getInputStream();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new ODataRuntimeException(e);
    } finally {
      if (gen != null) {
        try {
          gen.close();
        } catch (IOException e) {
          throw new ODataRuntimeException(e);
        }
      }
    }
  }

  private void writeEntitySets(JsonGenerator gen, Edm edm) throws JsonGenerationException, IOException {
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

  private void writeFunctionImports(JsonGenerator gen, Edm edm) throws JsonGenerationException, IOException {
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

  private void writeSingletons(JsonGenerator gen, Edm edm) throws JsonGenerationException, IOException {
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
