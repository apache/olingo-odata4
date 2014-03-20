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
package org.apache.olingo.server.core.serializer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.core.serializer.json.ServiceDocumentJsonSerializer;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class ODataJsonSerializer implements ODataSerializer {

  private static final Logger log = LoggerFactory.getLogger(ODataJsonSerializer.class);

  @Override
  public InputStream serviceDocument(final Edm edm, final String serviceRoot) {
    CircleStreamBuffer buffer;
    BufferedWriter writer;
    JsonFactory factory;
    JsonGenerator gen = null;

    // TODO: move stream initialization into separate method
    try {
      buffer = new CircleStreamBuffer();
      writer = new BufferedWriter(new OutputStreamWriter(buffer.getOutputStream(), DEFAULT_CHARSET));
      factory = new JsonFactory();

      gen = factory.createGenerator(writer);
      gen.setPrettyPrinter(new DefaultPrettyPrinter());

      ServiceDocumentJsonSerializer serializer = new ServiceDocumentJsonSerializer(edm, serviceRoot);
      serializer.writeServiceDocument(gen);

      gen.close();

      // TODO: Check correct stream handling
      // writer.flush();
      // buffer.closeWrite();

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

  @Override
  public InputStream metadataDocument(final Edm edm) {
    throw new ODataRuntimeException("Metadata in JSON format not supported!");
  }
}
