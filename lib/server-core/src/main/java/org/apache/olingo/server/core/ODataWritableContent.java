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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import org.apache.olingo.commons.api.data.EntityIterator;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.ODataContent;
import org.apache.olingo.server.api.ODataContentWriteErrorCallback;
import org.apache.olingo.server.api.ODataContentWriteErrorContext;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerStreamResult;
import org.apache.olingo.server.core.serializer.SerializerStreamResultImpl;
import org.apache.olingo.server.core.serializer.json.ODataJsonSerializer;
import org.apache.olingo.server.core.serializer.xml.ODataXmlSerializer;

/**
 * Stream supporting implementation of the ODataContent
 * and contains the response content for the OData request.
 * <p/>
 * If an error occur during a <code>write</code> method <b>NO</b> exception
 * will be thrown but if registered the
 * org.apache.olingo.server.api.ODataContentWriteErrorCallback is called.
 */
public class ODataWritableContent implements ODataContent {
  private StreamContent streamContent;

  private static abstract class StreamContent {
    protected EntityIterator iterator;
    protected ServiceMetadata metadata;
    protected EdmEntityType entityType;
    protected EntityCollectionSerializerOptions options;

    public StreamContent(EntityIterator iterator, EdmEntityType entityType, ServiceMetadata metadata,
        EntityCollectionSerializerOptions options) {
      this.iterator = iterator;
      this.entityType = entityType;
      this.metadata = metadata;
      this.options = options;
    }

    protected abstract void writeEntity(EntityIterator entity, OutputStream outputStream) throws SerializerException;

    public void write(OutputStream out) {
      try {
        writeEntity(iterator, out);
      } catch (SerializerException e) {
        final ODataContentWriteErrorCallback errorCallback = options.getODataContentWriteErrorCallback();
        if (errorCallback != null) {
          final WriteErrorContext errorContext = new WriteErrorContext(e);
          errorCallback.handleError(errorContext, Channels.newChannel(out));
        }
      }
    }
  }

  private static class StreamContentForJson extends StreamContent {
    private ODataJsonSerializer jsonSerializer;

    public StreamContentForJson(EntityIterator iterator, EdmEntityType entityType,
        ODataJsonSerializer jsonSerializer, ServiceMetadata metadata,
        EntityCollectionSerializerOptions options) {
      super(iterator, entityType, metadata, options);

      this.jsonSerializer = jsonSerializer;
    }

    protected void writeEntity(EntityIterator entity, OutputStream outputStream) throws SerializerException {
      try {
        jsonSerializer.entityCollectionIntoStream(metadata, entityType, entity, options, outputStream);
        outputStream.flush();
      } catch (final IOException e) {
        throw new ODataRuntimeException("Failed entity serialization", e);
      }
    }
  }

  private static class StreamContentForXml extends StreamContent {
    private ODataXmlSerializer xmlSerializer;

    public StreamContentForXml(EntityIterator iterator, EdmEntityType entityType,
        ODataXmlSerializer xmlSerializer, ServiceMetadata metadata,
        EntityCollectionSerializerOptions options) {
      super(iterator, entityType, metadata, options);

      this.xmlSerializer = xmlSerializer;
    }

    protected void writeEntity(EntityIterator entity, OutputStream outputStream) throws SerializerException {
      try {
        xmlSerializer.entityCollectionIntoStream(metadata, entityType, entity, options, outputStream);
        outputStream.flush();
      } catch (final IOException e) {
        throw new ODataRuntimeException("Failed entity serialization", e);
      }
    }
  }

  @Override
  public void write(WritableByteChannel writeChannel) {
    this.streamContent.write(Channels.newOutputStream(writeChannel));
  }

  @Override
  public void write(OutputStream stream) {
    write(Channels.newChannel(stream));
  }

  private ODataWritableContent(StreamContent streamContent) {
    this.streamContent = streamContent;
  }

  public static ODataWritableContentBuilder with(EntityIterator iterator, EdmEntityType entityType,
      ODataSerializer serializer, ServiceMetadata metadata,
      EntityCollectionSerializerOptions options) {
    return new ODataWritableContentBuilder(iterator, entityType, serializer, metadata, options);
  }

  public static class WriteErrorContext implements ODataContentWriteErrorContext {
    private ODataLibraryException exception;

    public WriteErrorContext(ODataLibraryException exception) {
      this.exception = exception;
    }

    @Override
    public Exception getException() {
      return exception;
    }

    @Override
    public ODataLibraryException getODataLibraryException() {
      return exception;
    }
  }

  public static class ODataWritableContentBuilder {
    private ODataSerializer serializer;
    private EntityIterator entities;
    private ServiceMetadata metadata;
    private EdmEntityType entityType;
    private EntityCollectionSerializerOptions options;

    public ODataWritableContentBuilder(EntityIterator entities, EdmEntityType entityType,
        ODataSerializer serializer,
        ServiceMetadata metadata, EntityCollectionSerializerOptions options) {
      this.entities = entities;
      this.entityType = entityType;
      this.serializer = serializer;
      this.metadata = metadata;
      this.options = options;
    }

    public ODataContent buildContent() {
      if (serializer instanceof ODataJsonSerializer) {
        StreamContent input = new StreamContentForJson(entities, entityType,
            (ODataJsonSerializer) serializer, metadata, options);
        return new ODataWritableContent(input);
      }else if (serializer instanceof ODataXmlSerializer) {
        StreamContentForXml input = new StreamContentForXml(entities, entityType,
            (ODataXmlSerializer) serializer, metadata, options);
        return new ODataWritableContent(input);
      }
      throw new ODataRuntimeException("No suitable serializer found");
    }

    public SerializerStreamResult build() {
      return SerializerStreamResultImpl.with().content(buildContent()).build();
    }
  }
}
