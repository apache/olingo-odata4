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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityIterator;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.core.serializer.json.ODataJsonStreamSerializer;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.apache.olingo.server.core.serializer.utils.ResultHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class StreamSerializerResult implements SerializerResult {
  private InputStream content;

  private static class StreamInputStream extends InputStream {
    private String head;
    private String tail;
    private int tailCount = 0;
    private int headCount = 0;
    private int entityCount = 0;
    private InputStream inputStream = null;
    private ODataJsonStreamSerializer jsonSerializer;
    private EntityIterator coll;
    private ServiceMetadata metadata;
    private EdmEntityType entityType;
    private EntitySerializerOptions options;

    public StreamInputStream(EntityIterator coll, EdmEntityType entityType, String head,
                             ODataJsonStreamSerializer jsonSerializer, ServiceMetadata metadata,
                             EntitySerializerOptions options, String tail) {
      this.coll = coll;
      this.entityType = entityType;
      this.head = head;
      this.jsonSerializer = jsonSerializer;
      this.metadata = metadata;
      this.options = options;
      this.tail = tail;
    }

    @Override
    public int read() throws IOException {
      if (headCount < head.length()) {
        return head.charAt(headCount++);
      }
      if (inputStream == null && coll.hasNext()) {
        try {
          inputStream = serEntity(coll.next());
          entityCount++;
          if (entityCount > 1) {
            return (int) ',';
          }
        } catch (SerializerException e) {
          inputStream = null;
          return read();
        }
      }
      if (inputStream != null) {
        int read = inputStream.read();
        if (read == -1) {
          inputStream = null;
          return read();
        }
        return read;
      }
      if (tailCount < tail.length()) {
        return tail.charAt(tailCount++);
      }
      return -1;
    }

    private InputStream serEntity(Entity entity) throws SerializerException {
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
        return buffer.getInputStream();
      } catch (final IOException e) {
        return new ByteArrayInputStream(("ERROR" + e.getMessage()).getBytes());
//      } catch (SerializerException e) {
//        return new ByteArrayInputStream(("ERROR" + e.getMessage()).getBytes());
      }
    }
  }

  @Override
  public InputStream getContent() {
    return content;
  }

  @Override
  public ReadableByteChannel getChannel() {
    return Channels.newChannel(getContent());
  }

  @Override
  public void writeContent(WritableByteChannel channel) {
    ResultHelper.copy(getChannel(), channel);
  }

  @Override
  public boolean isNioSupported() {
    return true;
  }

  private StreamSerializerResult(InputStream content) {
    this.content = content;
  }

  public static SerializerResultBuilder with(EntityIterator coll, EdmEntityType entityType,
                                             ODataJsonStreamSerializer jsonSerializer,
                                             ServiceMetadata metadata, EntitySerializerOptions options) {
    return new SerializerResultBuilder(coll, entityType, jsonSerializer, metadata, options);
  }

  public static class SerializerResultBuilder {
    private ODataJsonStreamSerializer jsonSerializer;
    private EntityIterator coll;
    private ServiceMetadata metadata;
    private EdmEntityType entityType;
    private EntitySerializerOptions options;
    private String head;
    private String tail;

    public SerializerResultBuilder(EntityIterator coll, EdmEntityType entityType,
                                   ODataJsonStreamSerializer jsonSerializer, ServiceMetadata metadata,
                                   EntitySerializerOptions options) {
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
      InputStream input = new StreamInputStream(coll, entityType, head, jsonSerializer, metadata, options, tail);
      return new StreamSerializerResult(input);
    }
  }
}
