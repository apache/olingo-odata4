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

import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;

public class OData4Impl extends ODataImpl {

  public static OData newInstance() {
    try {
      final Class<?> clazz = Class.forName(OData4Impl.class.getName());
      final Object object = clazz.newInstance();
      return (OData) object;
    } catch (final Exception e) {
      throw new ODataRuntimeException(e);
    }
  }

  private OData4Impl() {
  }

  @Override
  public ODataHttpHandler createHandler(final ServiceMetadata serviceMetadata) {
    return new OData4HttpHandler(this, serviceMetadata);
  }
}
