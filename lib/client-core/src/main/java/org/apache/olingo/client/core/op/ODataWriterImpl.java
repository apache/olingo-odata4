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
package org.apache.olingo.client.core.op;

import org.apache.olingo.commons.core.op.ResourceFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.client.api.op.ODataWriter;

public class ODataWriterImpl implements ODataWriter {

  private static final long serialVersionUID = 3265794768412314485L;

  protected final CommonODataClient client;

  public ODataWriterImpl(final CommonODataClient client) {
    this.client = client;
  }

  @Override
  public InputStream writeEntities(final Collection<CommonODataEntity> entities, final ODataPubFormat format) {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      for (CommonODataEntity entity : entities) {
        client.getSerializer().entity(client.getBinder().getEntity(
                entity, ResourceFactory.entityClassForFormat(format == ODataPubFormat.ATOM)), output);
      }

      return new ByteArrayInputStream(output.toByteArray());
    } finally {
      IOUtils.closeQuietly(output);
    }
  }

  @Override
  public InputStream writeEntity(final CommonODataEntity entity, final ODataPubFormat format) {
    return writeEntities(Collections.<CommonODataEntity>singleton(entity), format);
  }

  @Override
  public InputStream writeProperty(final CommonODataProperty property, final ODataFormat format) {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      client.getSerializer().property(client.getBinder().getProperty(
              property, ResourceFactory.entityClassForFormat(format == ODataFormat.XML)), output);

      return new ByteArrayInputStream(output.toByteArray());
    } finally {
      IOUtils.closeQuietly(output);
    }
  }

  @Override
  public InputStream writeLink(final ODataLink link, final ODataFormat format) {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      client.getSerializer().link(client.getBinder().getLink(link, format == ODataFormat.XML), format, output);

      return new ByteArrayInputStream(output.toByteArray());
    } finally {
      IOUtils.closeQuietly(output);
    }
  }
}
