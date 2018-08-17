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

import java.util.Collection;
import java.util.List;

import org.apache.olingo.commons.api.constants.Constantsv00;
import org.apache.olingo.commons.api.constants.Constantsv01;
import org.apache.olingo.commons.api.IConstants;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
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
import org.apache.olingo.server.core.debug.DebugResponseHelperImpl;
import org.apache.olingo.server.core.debug.ServerCoreDebugger;
import org.apache.olingo.server.core.deserializer.FixedFormatDeserializerImpl;
import org.apache.olingo.server.core.deserializer.json.ODataJsonDeserializer;
import org.apache.olingo.server.core.deserializer.xml.ODataXmlDeserializer;
import org.apache.olingo.server.core.etag.ETagHelperImpl;
import org.apache.olingo.server.core.prefer.PreferencesImpl;
import org.apache.olingo.server.core.serializer.FixedFormatSerializerImpl;
import org.apache.olingo.server.core.serializer.json.EdmAssistedJsonSerializer;
import org.apache.olingo.server.core.serializer.json.ODataJsonSerializer;
import org.apache.olingo.server.core.serializer.json.JsonDeltaSerializer;
import org.apache.olingo.server.core.serializer.json.JsonDeltaSerializerWithNavigations;
import org.apache.olingo.server.core.serializer.xml.ODataXmlSerializer;
import org.apache.olingo.server.core.uri.UriHelperImpl;

public class ODataImpl extends OData {

  @Override
  public ODataSerializer createSerializer(final ContentType contentType) throws SerializerException {
    ODataSerializer serializer = null;

    if (contentType.isCompatible(ContentType.APPLICATION_JSON)) {
      final String metadata = contentType.getParameter(ContentType.PARAMETER_ODATA_METADATA);
      if (metadata == null
          || ContentType.VALUE_ODATA_METADATA_MINIMAL.equalsIgnoreCase(metadata)
          || ContentType.VALUE_ODATA_METADATA_NONE.equalsIgnoreCase(metadata)
          || ContentType.VALUE_ODATA_METADATA_FULL.equalsIgnoreCase(metadata)) {
        serializer = new ODataJsonSerializer(contentType, new Constantsv00());
      }
    } else if (contentType.isCompatible(ContentType.APPLICATION_XML)
        || contentType.isCompatible(ContentType.APPLICATION_ATOM_XML)) {
      serializer = new ODataXmlSerializer();
    }

    if (serializer == null) {
      throw new SerializerException("Unsupported format: " + contentType.toContentTypeString(),
          SerializerException.MessageKeys.UNSUPPORTED_FORMAT, contentType.toContentTypeString());
    } else {
      return serializer;
    }
  }
  
  @Override
  public ODataSerializer createSerializer(final ContentType contentType, 
      final List<String> versions) throws SerializerException {
    ODataSerializer serializer = null;
    IConstants constants = new Constantsv00();
    if(versions!=null && versions.size()>0 && getMaxVersion(versions)>4){
      constants = new Constantsv01() ;
    }
    if (contentType.isCompatible(ContentType.APPLICATION_JSON)) {
      final String metadata = contentType.getParameter(ContentType.PARAMETER_ODATA_METADATA);
      if (metadata == null
          || ContentType.VALUE_ODATA_METADATA_MINIMAL.equalsIgnoreCase(metadata)
          || ContentType.VALUE_ODATA_METADATA_NONE.equalsIgnoreCase(metadata)
          || ContentType.VALUE_ODATA_METADATA_FULL.equalsIgnoreCase(metadata)) {
        serializer = new ODataJsonSerializer(contentType, constants);
      }
    } else if (contentType.isCompatible(ContentType.APPLICATION_XML)
        || contentType.isCompatible(ContentType.APPLICATION_ATOM_XML)) {
      serializer = new ODataXmlSerializer();
    }

    if (serializer == null) {
      throw new SerializerException("Unsupported format: " + contentType.toContentTypeString(),
          SerializerException.MessageKeys.UNSUPPORTED_FORMAT, contentType.toContentTypeString());
    } else {
      return serializer;
    }
  }

  @Override
  public FixedFormatSerializer createFixedFormatSerializer() {
    return new FixedFormatSerializerImpl();
  }

  @Override
  public EdmAssistedSerializer createEdmAssistedSerializer(final ContentType contentType) throws SerializerException {
    if (contentType.isCompatible(ContentType.APPLICATION_JSON)) {
      return new EdmAssistedJsonSerializer(contentType);
    }
    throw new SerializerException("Unsupported format: " + contentType.toContentTypeString(),
        SerializerException.MessageKeys.UNSUPPORTED_FORMAT, contentType.toContentTypeString());
  }
  
  
  @Override
  public EdmDeltaSerializer createEdmDeltaSerializer(final ContentType contentType, final List<String> versions)
      throws SerializerException {
    if (contentType.isCompatible(ContentType.APPLICATION_JSON)) {
      if(versions!=null && !versions.isEmpty()){
       return getMaxVersion(versions)>4 ?  new JsonDeltaSerializerWithNavigations(contentType):
         new JsonDeltaSerializer(contentType);
      }
      return new JsonDeltaSerializerWithNavigations(contentType);
    }
    throw new SerializerException("Unsupported format: " + contentType.toContentTypeString(),
        SerializerException.MessageKeys.UNSUPPORTED_FORMAT, contentType.toContentTypeString());
  }

  private float getMaxVersion(List<String> versions) {
    Float versionValue [] = new Float [versions.size()];
    int i=0;
    Float max=new Float(0);
    for(String version:versions){
     Float ver = Float.valueOf(version);
     versionValue[i++] = ver;
     max = max > ver ? max : ver ;
   }
    return max;
  }

  @Override
  public ODataHttpHandler createHandler(final ServiceMetadata serviceMetadata) {
    return new ODataHttpHandlerImpl(this, serviceMetadata);
  }

  @Override
  public ODataHandler createRawHandler(ServiceMetadata serviceMetadata) {
    return new ODataHandlerImpl(this, serviceMetadata, new ServerCoreDebugger(this));
  }

  @Override
  public ServiceMetadata createServiceMetadata(final CsdlEdmProvider edmProvider,
      final List<EdmxReference> references) {
    return createServiceMetadata(edmProvider, references, null);
  }

  @Override
  public ServiceMetadata createServiceMetadata(final CsdlEdmProvider edmProvider,
      final List<EdmxReference> references, final ServiceMetadataETagSupport serviceMetadataETagSupport) {
    return new ServiceMetadataImpl(edmProvider, references, serviceMetadataETagSupport);
  }

  @Override
  public FixedFormatDeserializer createFixedFormatDeserializer() {
    return new FixedFormatDeserializerImpl();
  }

  @Override
  public UriHelper createUriHelper() {
    return new UriHelperImpl();
  }

  @Override
  public ODataDeserializer createDeserializer(final ContentType contentType) throws DeserializerException {
    if (contentType.isCompatible(ContentType.JSON)) {
      return new ODataJsonDeserializer(contentType);
    } else if (contentType.isCompatible(ContentType.APPLICATION_XML)
        || contentType.isCompatible(ContentType.APPLICATION_ATOM_XML)) {
      return new ODataXmlDeserializer();
    } else {
      throw new DeserializerException("Unsupported format: " + contentType.toContentTypeString(),
          DeserializerException.MessageKeys.UNSUPPORTED_FORMAT, contentType.toContentTypeString());
    }
  }  
  @Override
  public ODataDeserializer createDeserializer(final ContentType contentType,
      ServiceMetadata metadata) throws DeserializerException {
    if (contentType.isCompatible(ContentType.JSON)) {
      return new ODataJsonDeserializer(contentType, metadata);
    } else if (contentType.isCompatible(ContentType.APPLICATION_XML)
        || contentType.isCompatible(ContentType.APPLICATION_ATOM_XML)) {
      return new ODataXmlDeserializer(metadata);
    } else {
      throw new DeserializerException("Unsupported format: " + contentType.toContentTypeString(),
          DeserializerException.MessageKeys.UNSUPPORTED_FORMAT, contentType.toContentTypeString());
    }
  }

  @Override
  public EdmPrimitiveType createPrimitiveTypeInstance(final EdmPrimitiveTypeKind kind) {
    return EdmPrimitiveTypeFactory.getInstance(kind);
  }

  @Override
  public ETagHelper createETagHelper() {
    return new ETagHelperImpl();
  }

  @Override
  public Preferences createPreferences(final Collection<String> preferHeaders) {
    return new PreferencesImpl(preferHeaders);
  }

  @Override
  public DebugResponseHelper createDebugResponseHelper(final String debugFormat) {
    // TODO: What should we do with invalid formats?
    // TODO: Support more debug formats
    return new DebugResponseHelperImpl(debugFormat);
  }

  @Override
  public ODataDeserializer createDeserializer(ContentType contentType, List<String> versions)
      throws DeserializerException {
    IConstants constants = new Constantsv00();
    if(versions!=null && versions.size()>0 && getMaxVersion(versions)>4){
      constants = new Constantsv01() ;
    }
    if (contentType.isCompatible(ContentType.JSON)) {
      return new ODataJsonDeserializer(contentType, constants);
    } else if (contentType.isCompatible(ContentType.APPLICATION_XML)
        || contentType.isCompatible(ContentType.APPLICATION_ATOM_XML)) {
      return new ODataXmlDeserializer();
    } else {
      throw new DeserializerException("Unsupported format: " + contentType.toContentTypeString(),
          DeserializerException.MessageKeys.UNSUPPORTED_FORMAT, contentType.toContentTypeString());
    }
  
  }

  @Override
  public ODataDeserializer createDeserializer(ContentType contentType, ServiceMetadata metadata, List<String> versions)
      throws DeserializerException {
    IConstants constants = new Constantsv00();
    if(versions!=null && versions.size()>0 && getMaxVersion(versions)>4){
      constants = new Constantsv01() ;
    }
    if (contentType.isCompatible(ContentType.JSON)) {
      return new ODataJsonDeserializer(contentType, metadata, constants);
    } else if (contentType.isCompatible(ContentType.APPLICATION_XML)
        || contentType.isCompatible(ContentType.APPLICATION_ATOM_XML)) {
      return new ODataXmlDeserializer(metadata);
    } else {
      throw new DeserializerException("Unsupported format: " + contentType.toContentTypeString(),
          DeserializerException.MessageKeys.UNSUPPORTED_FORMAT, contentType.toContentTypeString());
    }
  }
}
