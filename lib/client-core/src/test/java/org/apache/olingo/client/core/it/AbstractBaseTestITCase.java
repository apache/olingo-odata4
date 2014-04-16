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
package org.apache.olingo.client.core.it;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.core.data.AtomEntryImpl;
import org.apache.olingo.commons.core.data.JSONEntryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBaseTestITCase {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractBaseTestITCase.class);

  @SuppressWarnings("rawtypes")
  protected abstract CommonODataClient getClient();

  protected void debugEntry(final Entry entry, final String message) {
    if (LOG.isDebugEnabled()) {
      final StringWriter writer = new StringWriter();
      getClient().getSerializer().entry(entry, writer);
      writer.flush();
      LOG.debug(message + "\n{}", writer.toString());
    }
  }

  protected void debugFeed(final Feed feed, final String message) {
    if (LOG.isDebugEnabled()) {
      final StringWriter writer = new StringWriter();
      getClient().getSerializer().feed(feed, writer);
      writer.flush();
      LOG.debug(message + "\n{}", writer.toString());
    }
  }

  protected void debugODataProperty(final CommonODataProperty property, final String message) {
    LOG.debug(message + "\n{}", property.toString());
  }

  protected void debugODataValue(final ODataValue value, final String message) {
    LOG.debug(message + "\n{}", value.toString());
  }

  protected void debugODataEntity(final CommonODataEntity entity, final String message) {
    if (LOG.isDebugEnabled()) {
      StringWriter writer = new StringWriter();
      getClient().getSerializer().entry(getClient().getBinder().getEntry(entity, AtomEntryImpl.class), writer);
      writer.flush();
      LOG.debug(message + " (Atom)\n{}", writer.toString());

      writer = new StringWriter();
      getClient().getSerializer().entry(getClient().getBinder().getEntry(entity, JSONEntryImpl.class), writer);
      writer.flush();
      LOG.debug(message + " (JSON)\n{}", writer.toString());
    }
  }

  protected void debugInputStream(final InputStream input, final String message) {
    if (LOG.isDebugEnabled()) {
      try {
        LOG.debug(message + "\n{}", IOUtils.toString(input));
      } catch (IOException e) {
        LOG.error("Error writing stream", e);
      } finally {
        IOUtils.closeQuietly(input);
      }
    }
  }

}
