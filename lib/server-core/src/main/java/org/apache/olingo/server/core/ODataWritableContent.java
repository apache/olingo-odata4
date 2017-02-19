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
import java.util.Iterator;

import org.apache.olingo.commons.api.data.ComplexIterator;
import org.apache.olingo.commons.api.data.EntityIterator;
import org.apache.olingo.commons.api.data.PrimitiveIterator;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.ODataContent;
import org.apache.olingo.server.api.ODataContentWriteErrorCallback;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
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

  public static abstract class StreamContent<T extends Iterator,
      P extends EdmType, O> implements ODataContent {
    protected T iterator;
    protected P type;
    protected O options;
    protected ServiceMetadata metadata;
    ODataContentWriteErrorCallback errorCallback;

    StreamContent(T iterator, ServiceMetadata metadata,
                  P edmType, O options) {
      this.iterator = iterator;
      this.type = edmType;
      this.metadata = metadata;
      this.options = options;
      if(options != null) {
        if(options instanceof ComplexSerializerOptions) {
          this.errorCallback = ((ComplexSerializerOptions) options).getODataContentWriteErrorCallback();
        } else if(options instanceof EntityCollectionSerializerOptions) {
          this.errorCallback = ((EntityCollectionSerializerOptions) options).getODataContentWriteErrorCallback();
        } else if(options instanceof PrimitiveSerializerOptions) {
          this.errorCallback = ((PrimitiveSerializerOptions) options).getODataContentWriteErrorCallback();
        }
      }
    }

    protected abstract void writeInternal(OutputStream outputStream) throws SerializerException;

    public void write(OutputStream stream) {
      write(Channels.newChannel(stream));
    }

    @Override
    public void write(WritableByteChannel writeChannel) {
      writeCollection(Channels.newOutputStream(writeChannel));
    }

    void writeCollection(OutputStream out) {
      try {
        writeInternal(out);
      } catch (SerializerException e) {
        if (errorCallback != null) {
          final WriteErrorContext errorContext = new WriteErrorContext(e);
          errorCallback.handleError(errorContext, Channels.newChannel(out));
        }
      }
    }
  }

  private static class StreamContentForJson
      extends StreamContent<EntityIterator, EdmEntityType, EntityCollectionSerializerOptions> {
    private ODataJsonSerializer jsonSerializer;

    StreamContentForJson(EntityIterator iterator, EdmEntityType entityType,
                         ODataJsonSerializer jsonSerializer, ServiceMetadata metadata,
                         EntityCollectionSerializerOptions options) {
      super(iterator, metadata, entityType, options);

      this.jsonSerializer = jsonSerializer;
    }

    protected void writeInternal(OutputStream outputStream) throws SerializerException {
      try {
        jsonSerializer.entityCollectionIntoStream(metadata, type, iterator, options, outputStream);
        outputStream.flush();
      } catch (final IOException e) {
        throw new ODataRuntimeException("Failed entity serialization", e);
      }
    }
  }

  public static class ComplexStreamContentForJson
      extends StreamContent<ComplexIterator, EdmComplexType, ComplexSerializerOptions> {
    private final ODataJsonSerializer jsonSerializer;

    public ComplexStreamContentForJson(ComplexIterator iterator, EdmComplexType edmComplexType,
                                       ODataJsonSerializer jsonSerializer, ServiceMetadata serviceMetadata,
                                       ComplexSerializerOptions options) {
      super(iterator, serviceMetadata, edmComplexType, options);

      this.jsonSerializer = jsonSerializer;
    }

    @Override
    protected void writeInternal(OutputStream outputStream) throws SerializerException {
      try {
        jsonSerializer.complexCollectionIntoStream(metadata, type, iterator, options, outputStream);
        outputStream.flush();
      } catch (final IOException e) {
        throw new ODataRuntimeException("Failed complex serialization", e);
      }
    }
  }

  public static class PrimitiveStreamContentForJson
      extends StreamContent<PrimitiveIterator, EdmPrimitiveType, PrimitiveSerializerOptions> {
    private final ODataJsonSerializer jsonSerializer;

    public PrimitiveStreamContentForJson(
        PrimitiveIterator iterator,
        EdmPrimitiveType primitiveType,
        ODataJsonSerializer jsonSerializer,
        ServiceMetadata serviceMetadata,
        PrimitiveSerializerOptions options) {

      super(iterator, serviceMetadata, primitiveType, options);

      this.jsonSerializer = jsonSerializer;
    }

    protected void writeInternal(OutputStream outputStream) throws SerializerException {
      try {
        jsonSerializer.primitiveCollectionIntoStream(metadata, type, iterator, options, outputStream);
        outputStream.flush();
      } catch (final IOException e) {
        throw new ODataRuntimeException("Failed complex serialization", e);
      }
    }
  }


  private static class StreamContentForXml
      extends StreamContent<EntityIterator, EdmEntityType, EntityCollectionSerializerOptions> {
    private ODataXmlSerializer xmlSerializer;

    StreamContentForXml(EntityIterator iterator, EdmEntityType entityType,
                        ODataXmlSerializer xmlSerializer, ServiceMetadata metadata,
                        EntityCollectionSerializerOptions options) {
      super(iterator, metadata, entityType, options);

      this.xmlSerializer = xmlSerializer;
    }

    @Override
    protected void writeInternal(OutputStream outputStream) throws SerializerException {
      try {
        xmlSerializer.entityCollectionIntoStream(metadata, type, iterator, options, outputStream);
        outputStream.flush();
      } catch (final IOException e) {
        throw new ODataRuntimeException("Failed entity serialization", e);
      }
    }
  }

  public static class ComplexStreamContentForXml
      extends StreamContent<ComplexIterator, EdmComplexType, ComplexSerializerOptions> {
    private final ODataXmlSerializer xmlSerializer;

    public ComplexStreamContentForXml(ComplexIterator iterator, EdmComplexType edmComplexType,
                                         ODataXmlSerializer xmlSerializer, ServiceMetadata serviceMetadata,
                                         ComplexSerializerOptions options) {
      super(iterator, serviceMetadata, edmComplexType, options);

      this.xmlSerializer = xmlSerializer;
    }

    @Override
    protected void writeInternal(OutputStream outputStream) throws SerializerException {
      try {
        xmlSerializer.complexCollectionIntoStream(metadata, type, iterator, options, outputStream);
        outputStream.flush();
      } catch (final IOException e) {
        throw new ODataRuntimeException("Failed complex serialization", e);
      }
    }
  }

  public static class PrimitiveStreamContentForXml
      extends StreamContent<PrimitiveIterator, EdmPrimitiveType, PrimitiveSerializerOptions> {

    private final ODataXmlSerializer xmlSerializer;

    public PrimitiveStreamContentForXml(
        PrimitiveIterator iterator,
        EdmPrimitiveType primitiveType,
        ODataXmlSerializer xmlSerializer,
        ServiceMetadata serviceMetadata,
        PrimitiveSerializerOptions options) {

      super(iterator, serviceMetadata, primitiveType, options);

      this.xmlSerializer = xmlSerializer;
    }

    @Override
    protected void writeInternal(OutputStream outputStream) throws SerializerException {
      try {
        xmlSerializer.primitiveCollectionIntoStream(metadata, type, iterator, options, outputStream);
        outputStream.flush();
      } catch (final IOException e) {
        throw new ODataRuntimeException("Failed complex serialization", e);
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

  public static class ODataWritableContentBuilder {
    private ODataSerializer serializer;
    private EntityIterator entities;
    private ServiceMetadata metadata;
    private EdmEntityType entityType;
    private EntityCollectionSerializerOptions options;

    ODataWritableContentBuilder(EntityIterator entities, EdmEntityType entityType,
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
      } else if (serializer instanceof ODataXmlSerializer) {
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
