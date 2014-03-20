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

import java.io.InputStream;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.data.Entry;
import org.apache.olingo.client.api.data.Error;
import org.apache.olingo.client.api.data.Feed;
import org.apache.olingo.client.api.data.LinkCollection;
import org.apache.olingo.client.api.data.Property;
import org.apache.olingo.client.api.format.ODataFormat;
import org.apache.olingo.client.api.format.ODataPubFormat;
import org.apache.olingo.client.api.op.ODataDeserializer;
import org.apache.olingo.client.core.data.AtomDeserializer;
import org.apache.olingo.client.core.data.AtomEntryImpl;
import org.apache.olingo.client.core.data.AtomFeedImpl;
import org.apache.olingo.client.core.data.AtomPropertyImpl;
import org.apache.olingo.client.core.data.JSONEntryImpl;
import org.apache.olingo.client.core.data.JSONErrorBundle;
import org.apache.olingo.client.core.data.JSONFeedImpl;
import org.apache.olingo.client.core.data.JSONLinkCollectionImpl;
import org.apache.olingo.client.core.data.JSONPropertyImpl;
import org.apache.olingo.client.core.data.XMLErrorImpl;
import org.apache.olingo.client.core.data.XMLLinkCollectionImpl;

public abstract class AbstractODataDeserializer extends AbstractJacksonTool implements ODataDeserializer {

  private static final long serialVersionUID = -4244158979195609909L;

  private final AtomDeserializer atomDeserializer;

  public AbstractODataDeserializer(final ODataClient client) {
    super(client);

    this.atomDeserializer = new AtomDeserializer(client.getServiceVersion());
  }

  @Override
  public Feed toFeed(final InputStream input, final ODataPubFormat format) {
    return format == ODataPubFormat.ATOM
            ? atom(input, AtomFeedImpl.class)
            : json(input, JSONFeedImpl.class);
  }

  @Override
  public Entry toEntry(final InputStream input, final ODataPubFormat format) {
    return format == ODataPubFormat.ATOM
            ? atom(input, AtomEntryImpl.class)
            : json(input, JSONEntryImpl.class);
  }

  @Override
  public Property toProperty(final InputStream input, final ODataFormat format) {
    return format == ODataFormat.XML
            ? atom(input, AtomPropertyImpl.class)
            : json(input, JSONPropertyImpl.class);
  }

  @Override
  public LinkCollection toLinkCollection(final InputStream input, final ODataFormat format) {
    return format == ODataFormat.XML
            ? atom(input, XMLLinkCollectionImpl.class)
            : json(input, JSONLinkCollectionImpl.class);
  }

  @Override
  public Error toError(final InputStream input, final boolean isXML) {
    return isXML
            ? xml(input, XMLErrorImpl.class)
            : json(input, JSONErrorBundle.class).getError();
  }

  /*
   * ------------------ Protected methods ------------------
   */
  protected <T> T xml(final InputStream input, final Class<T> reference) {
    try {
      return getXmlMapper().readValue(input, reference);
    } catch (Exception e) {
      throw new IllegalArgumentException("While deserializing " + reference.getName(), e);
    }
  }

  protected <T> T atom(final InputStream input, final Class<T> reference) {
    try {
      return atomDeserializer.read(input, reference);
    } catch (Exception e) {
      throw new IllegalArgumentException("While deserializing " + reference.getName(), e);
    }
  }

  protected <T> T json(final InputStream input, final Class<T> reference) {
    try {
      return getObjectMapper().readValue(input, reference);
    } catch (Exception e) {
      throw new IllegalArgumentException("While deserializing " + reference.getName(), e);
    }
  }

}
