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
package org.apache.olingo.netty.server.api;

import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;

public abstract class ODataNetty extends OData {

  private static final String IMPLEMENTATION = "org.apache.olingo.netty.server.core.ODataNettyImpl";

  /**
   * Use this method to create a new OData instance. Each thread/request should keep its own instance.
   * @return a new OData instance
   */
  public static ODataNetty newInstance() {
    try {
      final Class<?> clazz = Class.forName(ODataNetty.IMPLEMENTATION);

      
       /* We explicitly do not use the singleton pattern to keep the server state free
       * and avoid class loading issues also during hot deployment.*/
       
      final Object object = clazz.newInstance();

      return (ODataNetty) object;

    } catch (final Exception e) {
      throw new ODataRuntimeException(e);
    }
  }
  /**
   * Creates a new ODataNettyHandler for handling OData requests in an HTTP context.
   *
   * @param serviceMetadata - metadata object required to handle an OData request
   */
  public abstract ODataNettyHandler createNettyHandler(ServiceMetadata serviceMetadata);

}
