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

import java.util.Collection;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.debug.DebugResponseHelper;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.FixedFormatDeserializer;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.etag.ETagHelper;
import org.apache.olingo.server.api.etag.ServiceMetadataETagSupport;
import org.apache.olingo.server.api.prefer.Preferences;
import org.apache.olingo.server.api.serializer.EdmAssistedSerializer;
import org.apache.olingo.server.api.serializer.EdmDeltaSerializer;
import org.apache.olingo.server.api.serializer.FixedFormatSerializer;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriHelper;

/**
 * Root object for serving factory tasks and support loose coupling of implementation (core) from the API.
 * This is not a singleton (static variables) to avoid issues with synchronization, OSGi, hot deployment and so on.
 * Each thread (request) should keep its own instance.
 */
public abstract class OData {

  private static final String IMPLEMENTATION = "org.apache.olingo.server.core.ODataImpl";

  /**
   * Use this method to create a new OData instance. Each thread/request should keep its own instance.
   * @return a new OData instance
   */
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
   * @param contentType any format supported by Olingo (XML, JSON ...)
   */
  public abstract ODataSerializer createSerializer(ContentType contentType) throws SerializerException;
 
  /**
   * Creates a new serializer object for rendering content in the specified format.
   * Serializers are used in Processor implementations.
   *
   * @param contentType any format supported by Olingo (XML, JSON ...)
   * @param versions any v4 version supported by Olingo (4.0, 4.01 ...)
   */
  public abstract ODataSerializer createSerializer(ContentType contentType, 
      final List<String> versions) throws SerializerException;

  /**
   * Creates a new serializer object for rendering content in a fixed format, e.g., for binary output or multipart/mixed
   * outpu.
   * Serializers are used in Processor implementations.
   */
  public abstract FixedFormatSerializer createFixedFormatSerializer();

  /**
   * Creates a new deserializer object for reading content in a fixed format, e.g., for binary input.
   * Deserializers are used in Processor implementations.
   */
  public abstract FixedFormatDeserializer createFixedFormatDeserializer();

  /**
   * Creates a new ODataHttpHandler for handling OData requests in an HTTP context.
   *
   * @param serviceMetadata - metadata object required to handle an OData request
   */
  public abstract ODataHttpHandler createHandler(ServiceMetadata serviceMetadata);

  /**
   * Creates a new ODataHandler for handling OData requests.
   *
   * @param serviceMetadata - metadata object required to handle an OData request
   */
  public abstract ODataHandler createRawHandler(ServiceMetadata serviceMetadata);

  /**
   * Creates a metadata object for this service.
   *
   * @param edmProvider a custom or default implementation for creating metadata
   * @param references list of edmx references
   * @return a service metadata implementation
   */
  public abstract ServiceMetadata createServiceMetadata(CsdlEdmProvider edmProvider, List<EdmxReference> references);

  /**
   * Creates a metadata object for this service.
   *
   * @param edmProvider a custom or default implementation for creating metadata
   * @param references list of edmx references
   * @param serviceMetadataETagSupport
   * @return a service metadata implementation
   */
  public abstract ServiceMetadata createServiceMetadata(CsdlEdmProvider edmProvider, List<EdmxReference> references,
      ServiceMetadataETagSupport serviceMetadataETagSupport);

  /**
   * Creates a new URI helper object for performing URI-related tasks.
   * It can be used in Processor implementations.
   */
  public abstract UriHelper createUriHelper();

  /**
   * Creates a new deserializer object for reading content in the specified format.
   * Deserializers are used in Processor implementations.
   *
   * @param contentType any content type supported by Olingo (XML, JSON ...)
   */
  public abstract ODataDeserializer createDeserializer(ContentType contentType) throws DeserializerException;

  /**
   * Creates a new deserializer object for reading content in the specified format.
   * Deserializers are used in Processor implementations.
   *
   * @param contentType any content type supported by Olingo (XML, JSON ...)
   * @param metadata ServiceMetada of the service
   */
  public abstract ODataDeserializer createDeserializer(ContentType contentType,
      ServiceMetadata metadata) throws DeserializerException;
  
  /**
  * Creates a new deserializer object for reading content in the specified format.
  * Deserializers are used in Processor implementations.
    *
    * @param contentType any content type supported by Olingo (XML, JSON ...)
    * @param service version
   */
  public abstract ODataDeserializer createDeserializer(ContentType contentType, 
      final List<String> versions) throws DeserializerException;

  /**
   * Creates a new deserializer object for reading content in the specified format.
   * Deserializers are used in Processor implementations.
   *
   * @param contentType any content type supported by Olingo (XML, JSON ...)
   * @param metadata ServiceMetada of the service
   * @param service version
   */
  public abstract ODataDeserializer createDeserializer(ContentType contentType,
      ServiceMetadata metadata, final List<String> versions) throws DeserializerException;
  
  /**
   * Creates a primitive-type instance.
   * @param kind the kind of the primitive type
   * @return an {@link EdmPrimitiveType} instance for the type kind
   */
  public abstract EdmPrimitiveType createPrimitiveTypeInstance(EdmPrimitiveTypeKind kind);

  /**
   * Creates a new ETag helper object for performing ETag-related tasks.
   * It can be used in Processor implementations.
   */
  public abstract ETagHelper createETagHelper();

  /**
   * Creates a new Preferences object out of Prefer HTTP request headers.
   * It can be used in Processor implementations.
   */
  public abstract Preferences createPreferences(Collection<String> preferHeaders);

  /**
   * Creates a DebugResponseHelper for the given debugFormat.
   * If the format is not supported no exception is thrown.
   * Instead we give back the implementation for the JSON format.
   * @param debugFormat format to be used
   * @return a debug-response helper
   */
  public abstract DebugResponseHelper createDebugResponseHelper(String debugFormat);

  /**
   * Creates a new serializer object capable of working without EDM information
   * for rendering content in the specified format.
   * @param contentType a content type supported by Olingo
   */
  public abstract EdmAssistedSerializer createEdmAssistedSerializer(final ContentType contentType)
      throws SerializerException;
  
  /**
   * Creates a new serializer object capable of working without EDM information
   * for rendering delta content in the specified format.
   * @param contentType a content type supported by Olingo
   * @param version versions supported by Olingo
   */
  public abstract EdmDeltaSerializer createEdmDeltaSerializer(final ContentType contentType,
      final List<String> versions) throws SerializerException;
}
