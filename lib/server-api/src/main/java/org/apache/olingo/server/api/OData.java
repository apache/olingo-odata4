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
package org.apache.olingo.server.api;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.serializer.FixedFormatSerializer;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;

import java.util.List;

/**
 * Root object for serving factory tasks and support loose coupling of implementation (core) from the API.
 * This is not a singleton (static variables) to avoid issues with synchronization, OSGi, hot deployment and so on.
 * Each thread (request) should keep its own instance.
 */
public abstract class OData {

  private static final String IMPLEMENTATION = "org.apache.olingo.server.core.ODataImpl";

  public static OData newInstance() {
    try {
      final Class<?> clazz = Class.forName(OData.IMPLEMENTATION);

      /*
       * We explicitly do not use the singleton pattern to keep the server state free
       * and avoid class loading issues also during hot deployment.
       */
      final Object object = clazz.newInstance();

      return (OData) object;

    } catch (final Exception e) {
      throw new ODataRuntimeException(e);
    }
  }

  /**
   * Creates a new serializer object for rendering content in the specified format.
   * Serializers are used in Processor implementations.
   *
   * @param format any format supported by Olingo (XML, JSON ...)
   */
  public abstract ODataSerializer createSerializer(ODataFormat format) throws SerializerException;

  /**
   * Creates a new serializer object for rendering content in a fixed format, e.g., for binary output.
   * Serializers are used in Processor implementations.
   */
  public abstract FixedFormatSerializer createFixedFormatSerializer() throws SerializerException;

  /**
   * Creates a new ODataHttpHandler for handling OData requests in an HTTP context.
   *
   * @param serviceMetadata - metadata object required to handle an OData request
   */
  public abstract ODataHttpHandler createHandler(ServiceMetadata serviceMetadata);

  /**
   * Creates a metadata object for this service.
   *
   * @param edmProvider a custom or default implementation for creating metadata
   * @param references list of edmx references
   */
  public abstract ServiceMetadata createServiceMetadata(EdmProvider edmProvider, List<EdmxReference> references);
}
