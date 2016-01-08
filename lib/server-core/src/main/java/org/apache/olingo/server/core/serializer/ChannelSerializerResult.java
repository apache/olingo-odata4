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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityStreamCollection;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.core.serializer.json.ODataJsonStreamSerializer;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class ChannelSerializerResult implements SerializerResult {
  private ReadableByteChannel channel;

  private static class StreamChannel implements ReadableByteChannel {
    private static final Charset DEFAULT = Charset.forName("UTF-8");
    private ByteBuffer head;
    private ByteBuffer tail;
    private ODataJsonStreamSerializer jsonSerializer;
    private EntityStreamCollection coll;
    private ServiceMetadata metadata;
    private EdmEntityType entityType;
    private EntitySerializerOptions options;

    public StreamChannel(EntityStreamCollection coll, EdmEntityType entityType, String head,
        ODataJsonStreamSerializer jsonSerializer, ServiceMetadata metadata,
        EntitySerializerOptions options, String tail) {
      this.coll = coll;
      this.entityType = entityType;
      this.head = ByteBuffer.wrap(head.getBytes(DEFAULT));
      this.jsonSerializer = jsonSerializer;
      this.metadata = metadata;
      this.options = options;
      this.tail = ByteBuffer.wrap(tail.getBytes(DEFAULT));
    }

    @Override
    public int read(ByteBuffer dest) throws IOException {
      ByteBuffer buffer = getCurrentBuffer();
      if (buffer != null && buffer.hasRemaining()) {
        int r = buffer.remaining();
        if(r <= dest.remaining()) {
          dest.put(buffer);
        } else {
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
      } if(!currentBuffer.hasRemaining()) {
        if (coll.hasNext()) {
          try {
            // FIXME: mibo_160108: Inefficient buffer handling, replace
            currentBuffer = serEntity(coll.nextEntity());
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

  @Override
  public InputStream getContent() {
    return Channels.newInputStream(this.channel);
  }

  @Override
  public ReadableByteChannel getChannel() {
    return this.channel;
  }

  @Override
  public boolean isNioSupported() {
    return true;
  }

  private ChannelSerializerResult(ReadableByteChannel channel) {
    this.channel = channel;
  }

  public static SerializerResultBuilder with(EntityStreamCollection coll, EdmEntityType entityType,
      ODataJsonStreamSerializer jsonSerializer, ServiceMetadata metadata, EntitySerializerOptions options) {
    return new SerializerResultBuilder(coll, entityType, jsonSerializer, metadata, options);
  }

  public static class SerializerResultBuilder {
    private ODataJsonStreamSerializer jsonSerializer;
    private EntityStreamCollection coll;
    private ServiceMetadata metadata;
    private EdmEntityType entityType;
    private EntitySerializerOptions options;
    private String head;
    private String tail;

    public SerializerResultBuilder(EntityStreamCollection coll, EdmEntityType entityType,
        ODataJsonStreamSerializer jsonSerializer, ServiceMetadata metadata, EntitySerializerOptions options) {
      this.coll = coll;
      this.entityType = entityType;
      this.jsonSerializer = jsonSerializer;
      this.metadata = metadata;
      this.options = options;
    }

    public SerializerResultBuilder addHead(String head) {
      this.head = head;
      return this;
    }

    public SerializerResultBuilder addTail(String tail) {
      this.tail = tail;
      return this;
    }

    public SerializerResult build() {
      ReadableByteChannel input = new StreamChannel(coll, entityType, head, jsonSerializer, metadata, options, tail);
      return new ChannelSerializerResult(input);
    }
  }
}
