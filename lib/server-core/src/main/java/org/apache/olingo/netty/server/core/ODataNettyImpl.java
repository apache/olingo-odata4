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
package org.apache.olingo.netty.server.core;

import java.util.Collection;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.netty.server.api.ODataNetty;
import org.apache.olingo.netty.server.api.ODataNettyHandler;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHandler;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
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

public class ODataNettyImpl extends ODataNetty {
  
  private static OData odata;
  private static final String IMPLEMENTATION = "org.apache.olingo.server.core.ODataImpl";

  static {
    try {
      final Class<?> clazz = Class.forName(IMPLEMENTATION);

      /*
       * We explicitly do not use the singleton pattern to keep the server state free
       * and avoid class loading issues also during hot deployment.
       */
      final Object object = clazz.newInstance();
      odata = (OData) object;
    } catch (Exception e) {
      throw new ODataRuntimeException(e);
    }
  }
  
  @Override
  public ODataNettyHandler createNettyHandler(ServiceMetadata serviceMetadata) {
    return new ODataNettyHandlerImpl(this, serviceMetadata);
  }

  @Override
  public ODataSerializer createSerializer(ContentType contentType) throws SerializerException {
    return odata.createSerializer(contentType);
  }
  
  @Override
  public ODataSerializer createSerializer(final ContentType contentType, 
      final List<String> versions) throws SerializerException {
    return odata.createSerializer(contentType, versions);
  }

  @Override
  public FixedFormatSerializer createFixedFormatSerializer() {
    return odata.createFixedFormatSerializer();
  }

  @Override
  public FixedFormatDeserializer createFixedFormatDeserializer() {
    return odata.createFixedFormatDeserializer();
  }

  @Override
  public ODataHttpHandler createHandler(ServiceMetadata serviceMetadata) {
    return odata.createHandler(serviceMetadata);
  }

  @Override
  public ODataHandler createRawHandler(ServiceMetadata serviceMetadata) {
    return odata.createRawHandler(serviceMetadata);
  }

  @Override
  public ServiceMetadata createServiceMetadata(CsdlEdmProvider edmProvider, List<EdmxReference> references) {
    return odata.createServiceMetadata(edmProvider, references);
  }

  @Override
  public ServiceMetadata createServiceMetadata(CsdlEdmProvider edmProvider, List<EdmxReference> references,
      ServiceMetadataETagSupport serviceMetadataETagSupport) {
    return odata.createServiceMetadata(edmProvider, references, serviceMetadataETagSupport);
  }

  @Override
  public UriHelper createUriHelper() {
    return odata.createUriHelper();
  }

  @Override
  public ODataDeserializer createDeserializer(ContentType contentType) throws DeserializerException {
    return odata.createDeserializer(contentType);
  }

  @Override
  public ODataDeserializer createDeserializer(ContentType contentType, ServiceMetadata metadata)
      throws DeserializerException {
    return odata.createDeserializer(contentType);
  }

  @Override
  public EdmPrimitiveType createPrimitiveTypeInstance(EdmPrimitiveTypeKind kind) {
    return odata.createPrimitiveTypeInstance(kind);
  }

  @Override
  public ETagHelper createETagHelper() {
    return odata.createETagHelper();
  }

  @Override
  public Preferences createPreferences(Collection<String> preferHeaders) {
    return odata.createPreferences(preferHeaders);
  }

  @Override
  public DebugResponseHelper createDebugResponseHelper(String debugFormat) {
    return odata.createDebugResponseHelper(debugFormat);
  }

  @Override
  public EdmAssistedSerializer createEdmAssistedSerializer(ContentType contentType) throws SerializerException {
    return odata.createEdmAssistedSerializer(contentType);
  }
  
  @Override
  public EdmAssistedSerializer createEdmAssistedSerializer(ContentType contentType, 
		  List<String> versions) throws SerializerException {
    return odata.createEdmAssistedSerializer(contentType, versions);
  }

  @Override
  public EdmDeltaSerializer createEdmDeltaSerializer(ContentType contentType, List<String> versions)
      throws SerializerException {
    return odata.createEdmDeltaSerializer(contentType, versions);
  }

  @Override
  public ODataDeserializer createDeserializer(ContentType contentType, List<String> versions)
      throws DeserializerException {
    return odata.createDeserializer(contentType, versions);
  }

  @Override
  public ODataDeserializer createDeserializer(ContentType contentType, ServiceMetadata metadata, List<String> versions)
      throws DeserializerException {
    return odata.createDeserializer(contentType, metadata, versions);
  }
  
}
