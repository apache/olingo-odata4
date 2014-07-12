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
package org.apache.olingo.client.core;

import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.Configuration;
import org.apache.olingo.client.api.communication.header.ODataPreferences;
import org.apache.olingo.client.api.communication.request.cud.CommonUpdateType;
import org.apache.olingo.client.api.serialization.ODataWriter;
import org.apache.olingo.client.core.serialization.ODataWriterImpl;

public abstract class AbstractODataClient<UT extends CommonUpdateType> implements CommonODataClient<UT> {

  protected final Configuration configuration = new ConfigurationImpl();

  private final ODataWriter writer = new ODataWriterImpl(this);

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public ODataPreferences newPreferences() {
    return new ODataPreferences(getServiceVersion());
  }

  @Override
  public ODataWriter getWriter() {
    return writer;
  }

}
