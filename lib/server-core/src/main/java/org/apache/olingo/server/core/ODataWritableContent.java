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
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityIterator;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.ODataContent;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.WriteContentErrorCallback;
import org.apache.olingo.server.api.WriteContentErrorContext;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerStreamResult;
import org.apache.olingo.server.core.serializer.SerializerStreamResultImpl;
import org.apache.olingo.server.core.serializer.json.ODataJsonSerializer;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class ODataWritableContent implements ODataContent {
  private StreamChannel channel;

  private static class StreamChannel implements ReadableByteChannel {
    private static final Charset DEFAULT = Charset.forName("UTF-8");
    private ByteBuffer head;
    private ByteBuffer tail;
    private ODataJsonSerializer jsonSerializer;
    private EntityIterator coll;
    private ServiceMetadata metadata;
    private EdmEntityType entityType;
    private EntityCollectionSerializerOptions options;

    public StreamChannel(EntityIterator coll, EdmEntityType entityType, String head,
                         ODataJsonSerializer jsonSerializer, ServiceMetadata metadata,
                         EntityCollectionSerializerOptions options, String tail) {
      this.coll = coll;
      this.entityType = entityType;
      this.head = head == null ? ByteBuffer.allocate(0) : ByteBuffer.wrap(head.getBytes(DEFAULT));
      this.jsonSerializer = jsonSerializer;
      this.metadata = metadata;
      this.options = options;
      this.tail = tail == null ? ByteBuffer.allocate(0) : ByteBuffer.wrap(tail.getBytes(DEFAULT));
    }

//    public boolean write(OutputStream out) throws IOException {
//      if(head.hasRemaining()) {
//        out.write(head.array());
//        head.flip();
//        return true;
//      }
//      if (coll.hasNext()) {
//        try {
//          writeEntity(coll.next(), out);
//          if(coll.hasNext()) {
//            out.write(",".getBytes(DEFAULT));
//          }
//          return true;
//        } catch (SerializerException e) {
//          final WriteContentErrorCallback errorCallback = options.getWriteContentErrorCallback();
//          if(errorCallback != null) {
//            final ErrorContext errorContext = new ErrorContext(e).setParameter("Sample", "Some exception happened.");
//            errorCallback.handleError(errorContext, Channels.newChannel(out));
//          }
//        }
//      } else if(tail.hasRemaining()) {
//        out.write(tail.array());
//        tail.flip();
//        return true;
//      }
//      return false;
//    }

    public void write(OutputStream out) {
      try {
        writeEntity(coll, out);
      } catch (SerializerException e) {
        final WriteContentErrorCallback errorCallback = options.getWriteContentErrorCallback();
        if(errorCallback != null) {
          final ErrorContext errorContext = new ErrorContext(e).setParameter("Sample", "Some exception happened.");
          errorCallback.handleError(errorContext, Channels.newChannel(out));
        }
      }
    }


    private void writeEntity(EntityIterator entity, OutputStream outputStream) throws SerializerException {
      try {
        jsonSerializer.entityCollectionIntoStream(metadata, entityType, entity, options, outputStream);
        outputStream.flush();
      } catch (final IOException e) {
        throw new ODataRuntimeException("Failed entity serialization");
      }
    }

    @Override
    public int read(ByteBuffer dest) throws IOException {
      ByteBuffer buffer = getCurrentBuffer();
      if (buffer != null && buffer.hasRemaining()) {
        int r = buffer.remaining();
        if(r <= dest.remaining()) {
          dest.put(buffer);
        } else {
          r = dest.remaining();
          byte[] buf = new byte[dest.remaining()];
          buffer.get(buf);
          dest.put(buf);
        }
        return r;
      }
      return -1;
    }

    ByteBuffer currentBuffer;

    private ByteBuffer getCurrentBuffer() {
      if(currentBuffer == null) {
        currentBuffer = head;
      }
      if(!currentBuffer.hasRemaining()) {
        if (coll.hasNext()) {
          try {
            // FIXME: mibo_160108: Inefficient buffer handling, replace
            currentBuffer = serEntity(coll.next());
            if(coll.hasNext()) {
              ByteBuffer b = ByteBuffer.allocate(currentBuffer.position() + 1);
              currentBuffer.flip();
              b.put(currentBuffer).put(",".getBytes(DEFAULT));
              currentBuffer = b;
            }
            currentBuffer.flip();
          } catch (SerializerException e) {
            return getCurrentBuffer();
          }
        } else if(tail.hasRemaining()) {
          currentBuffer = tail;
        } else {
          return null;
        }
      }
      return currentBuffer;
    }

    private ByteBuffer serEntity(Entity entity) throws SerializerException {
      try {
        CircleStreamBuffer buffer = new CircleStreamBuffer();
        OutputStream outputStream = buffer.getOutputStream();
        JsonGenerator json = new JsonFactory().createGenerator(outputStream);
        jsonSerializer.writeEntity(metadata, entityType, entity, null,
            options == null ? null : options.getExpand(),
            options == null ? null : options.getSelect(),
            options != null && options.getWriteOnlyReferences(),
            json);

        json.close();
        outputStream.close();
        return buffer.getBuffer();
      } catch (final IOException e) {
        return ByteBuffer.wrap(("ERROR" + e.getMessage()).getBytes());
      }
    }


    @Override
    public boolean isOpen() {
      return false;
    }

    @Override
    public void close() throws IOException {

    }
  }

  public ReadableByteChannel getChannel() {
    return this.channel;
  }

  public boolean isWriteSupported() {
    return true;
  }

  @Override
  public void write(WritableByteChannel writeChannel) {
    this.channel.write(Channels.newOutputStream(writeChannel));
  }

  @Override
  public void write(OutputStream stream) {
    write(Channels.newChannel(stream));
  }

  private ODataWritableContent(StreamChannel channel) {
    this.channel = channel;
  }

  public static ODataWritableContentBuilder with(EntityIterator coll, EdmEntityType entityType,
                                             ODataJsonSerializer jsonSerializer,
                                             ServiceMetadata metadata, EntityCollectionSerializerOptions options) {
    return new ODataWritableContentBuilder(coll, entityType, jsonSerializer, metadata, options);
  }

  public static class ErrorContext implements WriteContentErrorContext {
    private ODataLibraryException exception;
    final private Map<String, Object> parameters = new HashMap<String, Object>();

    public ErrorContext(ODataLibraryException exception) {
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

    public ErrorContext setParameter(String name, Object value) {
      parameters.put(name, value);
      return this;
    }

//    @Override
    public Object getParameter(String name) {
      return parameters.get(name);
    }
  }

  public static class ODataWritableContentBuilder {
    private ODataJsonSerializer jsonSerializer;
    private EntityIterator entities;
    private ServiceMetadata metadata;
    private EdmEntityType entityType;
    private EntityCollectionSerializerOptions options;
    private String head;
    private String tail;

    public ODataWritableContentBuilder(EntityIterator entities, EdmEntityType entityType,
                                   ODataJsonSerializer jsonSerializer,
                                   ServiceMetadata metadata, EntityCollectionSerializerOptions options) {
      this.entities = entities;
      this.entityType = entityType;
      this.jsonSerializer = jsonSerializer;
      this.metadata = metadata;
      this.options = options;
    }

    public ODataWritableContentBuilder addHead(String head) {
      this.head = head;
      return this;
    }

    public ODataWritableContentBuilder addTail(String tail) {
      this.tail = tail;
      return this;
    }

    public ODataContent buildContent() {
      StreamChannel input = new StreamChannel(entities, entityType, head, jsonSerializer, metadata, options, tail);
      return new ODataWritableContent(input);
    }
    public SerializerStreamResult build() {
      return SerializerStreamResultImpl.with().content(buildContent()).build();
    }
  }
}
