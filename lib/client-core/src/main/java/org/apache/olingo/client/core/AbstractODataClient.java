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
import org.apache.olingo.commons.api.domain.ODataObjectFactory;
import org.apache.olingo.client.api.op.ODataWriter;
import org.apache.olingo.commons.core.domain.ODataPrimitiveValueImpl;
import org.apache.olingo.commons.core.op.ODataObjectFactoryImpl;
import org.apache.olingo.client.core.op.ODataWriterImpl;

public abstract class AbstractODataClient implements CommonODataClient {

  private static final long serialVersionUID = 7269096702397630265L;

  private final ODataWriter writer = new ODataWriterImpl(this);

  private final ODataObjectFactory objectFactory = new ODataObjectFactoryImpl(getServiceVersion());

  @Override
  public ODataPrimitiveValueImpl.BuilderImpl getPrimitiveValueBuilder() {
    return new ODataPrimitiveValueImpl.BuilderImpl(this.getServiceVersion());
  }

  @Override
  public ODataWriter getWriter() {
    return writer;
  }

  @Override
  public ODataObjectFactory getObjectFactory() {
    return objectFactory;
  }

}
