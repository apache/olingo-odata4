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
package org.apache.olingo.client.api.communication.request.retrieve.v4;

import java.net.URI;
import org.apache.olingo.client.api.communication.request.retrieve.CommonRetrieveRequestFactory;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;

@SuppressWarnings("unchecked")
public interface RetrieveRequestFactory extends CommonRetrieveRequestFactory {

  @Override
  ODataEntitySetRequest<ODataEntitySet> getEntitySetRequest(URI uri);

  @Override
  ODataEntitySetIteratorRequest<ODataEntitySet, ODataEntity> getEntitySetIteratorRequest(URI uri);

  @Override
  ODataEntityRequest<ODataEntity> getEntityRequest(URI uri);

  @Override
  ODataPropertyRequest<ODataProperty> getPropertyRequest(URI uri);
}
