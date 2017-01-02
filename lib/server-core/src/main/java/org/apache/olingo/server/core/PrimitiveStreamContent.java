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

import java.io.OutputStream;
import java.nio.channels.Channels;

import org.apache.olingo.commons.api.data.PrimitiveIterator;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.server.api.ODataContentWriteErrorCallback;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerStreamResult;
import org.apache.olingo.server.core.serializer.SerializerStreamResultImpl;
import org.apache.olingo.server.core.serializer.json.ODataJsonSerializer;
import org.apache.olingo.server.core.serializer.xml.ODataXmlSerializer;

public abstract class PrimitiveStreamContent extends CollectionWritableContent {
  protected PrimitiveIterator iterator;
  protected ServiceMetadata metadata;
  protected EdmPrimitiveType primitiveType;
  protected PrimitiveSerializerOptions options;

  protected PrimitiveStreamContent(
          PrimitiveIterator iterator,
          ServiceMetadata metadata,
          EdmPrimitiveType primitiveType,
          PrimitiveSerializerOptions options) {
    this.iterator = iterator;
    this.primitiveType = primitiveType;
    this.metadata = metadata;
    this.options = options;
  }

  protected abstract void writePrimitive(OutputStream out) throws SerializerException;

  @Override
  protected void writeCollection(OutputStream out) {
    try {
      writePrimitive(out);
    } catch (SerializerException e) {
      final ODataContentWriteErrorCallback errorCallback = options.getODataContentWriteErrorCallback();
      if (errorCallback != null) {
        final WriteErrorContext errorContext = new WriteErrorContext(e);
        errorCallback.handleError(errorContext, Channels.newChannel(out));
      }
    }
  }

  public static SerializerStreamResult PrimitiveStreamContentForJson(
          PrimitiveIterator iterator,
          EdmPrimitiveType primitiveType,
          ODataJsonSerializer jsonSerializer,
          ServiceMetadata serviceMetadata,
          PrimitiveSerializerOptions options) {

    return SerializerStreamResultImpl.with()
            .content(new PrimitiveStreamContentForJson(iterator,
                                                       primitiveType,
                                                       jsonSerializer,
                                                       serviceMetadata,
                                                       options)).build();
  }

  public static SerializerStreamResult PrimitiveStreamContentForXml(
          PrimitiveIterator iterator,
          EdmPrimitiveType primitiveType,
          ODataXmlSerializer xmlSerializer,
          ServiceMetadata serviceMetadata,
          PrimitiveSerializerOptions options) {

    return SerializerStreamResultImpl.with()
            .content(new PrimitiveStreamContentForXml(iterator,
                                                      primitiveType,
                                                      xmlSerializer,
                                                      serviceMetadata,
                                                      options)).build();
  }
}
