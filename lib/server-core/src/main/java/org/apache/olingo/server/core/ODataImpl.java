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
package org.apache.olingo.server.core;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.serializer.ODataFormat;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.server.core.serializer.ODataJsonSerializer;
import org.apache.olingo.server.core.serializer.ODataXmlSerializerImpl;

public class ODataImpl extends OData {

  @Override
  public ODataSerializer createSerializer(final ODataFormat format) {
    ODataSerializer serializer;
    switch (format) {
    case JSON:
      serializer = new ODataJsonSerializer();
      break;
    case XML:
      serializer = new ODataXmlSerializerImpl();
      break;
    default:
      throw new ODataRuntimeException("Unsupported format: " + format);
    }

    return serializer;
  }

  @Override
  public ODataHttpHandler createHandler(final Edm edm) {
    return new ODataHttpHandlerImpl(this, edm);
  }

  @Override
  public Edm createEdm(final EdmProvider edmProvider) {
    return new EdmProviderImpl(edmProvider);
  }

}
