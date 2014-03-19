/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.op.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.olingo.client.api.Constants;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.data.Entry;
import org.apache.olingo.client.api.data.Feed;
import org.apache.olingo.client.api.data.Link;
import org.apache.olingo.client.api.data.Property;
import org.apache.olingo.client.api.format.ODataFormat;
import org.apache.olingo.client.api.op.ODataSerializer;
import org.apache.olingo.client.core.data.AtomEntryImpl;
import org.apache.olingo.client.core.data.AtomFeedImpl;
import org.apache.olingo.client.core.data.AtomPropertyImpl;
import org.apache.olingo.client.core.data.AtomSerializer;
import org.apache.olingo.client.core.data.JSONEntryImpl;
import org.apache.olingo.client.core.data.JSONFeedImpl;
import org.apache.olingo.client.core.data.JSONPropertyImpl;

public abstract class AbstractODataSerializer extends AbstractJacksonTool implements ODataSerializer {

  private static final long serialVersionUID = -357777648541325363L;

  private final AtomSerializer atomSerializer;

  public AbstractODataSerializer(final ODataClient client) {
    super(client);

    this.atomSerializer = new AtomSerializer(client.getServiceVersion());
  }

  @Override
  public void feed(final Feed obj, final OutputStream out) {
    feed(obj, new OutputStreamWriter(out));
  }

  @Override
  public void feed(final Feed obj, final Writer writer) {
    if (obj instanceof AtomFeedImpl) {
      atom((AtomFeedImpl) obj, writer);
    } else {
      json((JSONFeedImpl) obj, writer);
    }
  }

  @Override
  public void entry(final Entry obj, final OutputStream out) {
    entry(obj, new OutputStreamWriter(out));
  }

  @Override
  public void entry(final Entry obj, final Writer writer) {
    if (obj instanceof AtomEntryImpl) {
      atom((AtomEntryImpl) obj, writer);
    } else {
      json((JSONEntryImpl) obj, writer);
    }
  }

  @Override
  public void property(final Property obj, final OutputStream out) {
    property(obj, new OutputStreamWriter(out));
  }

  @Override
  public void property(final Property obj, final Writer writer) {
    if (obj instanceof AtomPropertyImpl) {
      atom((AtomPropertyImpl) obj, writer);
    } else {
      json((JSONPropertyImpl) obj, writer);
    }
  }

  @Override
  public void link(final Link link, final ODataFormat format, final OutputStream out) {
    link(link, format, new OutputStreamWriter(out));
  }

  @Override
  public void link(final Link link, final ODataFormat format, final Writer writer) {
    if (format == ODataFormat.XML) {
      atom(link, writer);
    } else {
      jsonLink(link, writer);
    }
  }

  /*
   * ------------------ Protected methods ------------------
   */
  protected <T> void atom(final T obj, final Writer writer) {
    try {
      atomSerializer.write(writer, obj);
    } catch (Exception e) {
      throw new IllegalArgumentException("While serializing Atom object", e);
    }
  }

  protected <T> void json(final T obj, final Writer writer) {
    try {
      getObjectMapper().writeValue(writer, obj);
    } catch (IOException e) {
      throw new IllegalArgumentException("While serializing JSON object", e);
    }
  }

  protected void jsonLink(final Link link, final Writer writer) {
    final ObjectMapper mapper = getObjectMapper();
    final ObjectNode uri = mapper.createObjectNode();
    uri.put(Constants.JSON_URL, link.getHref());

    try {
      mapper.writeValue(writer, uri);
    } catch (Exception e) {
      throw new IllegalArgumentException("While serializing JSON link", e);
    }
  }
}
