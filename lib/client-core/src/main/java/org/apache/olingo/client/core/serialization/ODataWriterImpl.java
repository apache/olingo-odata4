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
package org.apache.olingo.client.core.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.serialization.ODataWriter;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataSerializerException;

public class ODataWriterImpl implements ODataWriter {

  protected final CommonODataClient<?> client;

  public ODataWriterImpl(final CommonODataClient<?> client) {
    this.client = client;
  }

  @Override
  public InputStream writeEntities(final Collection<ODataEntity> entities, final ODataFormat format)
      throws ODataSerializerException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    OutputStreamWriter writer;
    try {
      writer = new OutputStreamWriter(output, Constants.UTF8);
    } catch (final UnsupportedEncodingException e) {
      writer = null;
    }
    try {
      for (ODataEntity entity : entities) {
        client.getSerializer(format).write(writer, client.getBinder().getEntity(entity));
      }

      return new ByteArrayInputStream(output.toByteArray());
    } finally {
      IOUtils.closeQuietly(output);
    }
  }

  @Override
  public InputStream writeEntity(final ODataEntity entity, final ODataFormat format)
      throws ODataSerializerException {
    return writeEntities(Collections.<ODataEntity>singleton(entity), format);
  }

  @Override
  public InputStream writeProperty(final ODataProperty property, final ODataFormat format)
      throws ODataSerializerException {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    OutputStreamWriter writer;
    try {
      writer = new OutputStreamWriter(output, Constants.UTF8);
    } catch (final UnsupportedEncodingException e) {
      writer = null;
    }
    try {
      client.getSerializer(format).write(writer, client.getBinder().getProperty(property));

      return new ByteArrayInputStream(output.toByteArray());
    } finally {
      IOUtils.closeQuietly(output);
    }
  }

  @Override
  public InputStream writeLink(final ODataLink link, final ODataFormat format) throws ODataSerializerException {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    OutputStreamWriter writer;
    try {
      writer = new OutputStreamWriter(output, Constants.UTF8);
    } catch (final UnsupportedEncodingException e) {
      writer = null;
    }
    try {
      client.getSerializer(format).write(writer, client.getBinder().getLink(link));

      return new ByteArrayInputStream(output.toByteArray());
    } finally {
      IOUtils.closeQuietly(output);
    }
  }

  @Override
  public InputStream writeReference(ResWrap<URI> reference, ODataFormat format) throws ODataSerializerException {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    OutputStreamWriter writer = null;
    
    try {
      writer = new OutputStreamWriter(output, Constants.UTF8);
    } catch (final UnsupportedEncodingException e) {
    }
    
    try {
      client.getSerializer(format).write(writer, reference);

      return new ByteArrayInputStream(output.toByteArray());
    } finally {
      IOUtils.closeQuietly(output);
    }
  }
}
