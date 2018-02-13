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
package org.apache.olingo.client.core.serialization;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.api.serialization.ODataReader;
import org.apache.olingo.client.core.edm.ClientCsdlEdmProvider;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataError;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODataReaderImpl implements ODataReader {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(ODataReaderImpl.class);

  protected final ODataClient client;

  public ODataReaderImpl(final ODataClient client) {
    this.client = client;
  }

  @Override
  public Edm readMetadata(final InputStream input) {
    return readMetadata(client.getDeserializer(ContentType.APPLICATION_XML).toMetadata(input).getSchemaByNsOrAlias());
  }

  @Override
  public Edm readMetadata(final Map<String, CsdlSchema> xmlSchemas) {
    ClientCsdlEdmProvider prov = new ClientCsdlEdmProvider(xmlSchemas);
    return new EdmProviderImpl(prov);
  }

  @Override
  public ClientServiceDocument readServiceDocument(final InputStream input, final ContentType contentType)
      throws ODataDeserializerException {
    return client.getBinder().getODataServiceDocument(
        client.getDeserializer(contentType).toServiceDocument(input).getPayload());
  }

  @Override
  public ODataError readError(final InputStream inputStream, final ContentType contentType)
      throws ODataDeserializerException {
    return client.getDeserializer(contentType).toError(inputStream);
  }

  @Override
  public <T> ResWrap<T> read(final InputStream src, final String format, final Class<T> reference)
      throws ODataDeserializerException {
    ResWrap<T> res;

    try {
      if (ClientEntitySetIterator.class.isAssignableFrom(reference)) {
        res = new ResWrap<T>(
            (URI) null,
            null,
            reference.cast(new ClientEntitySetIterator<ClientEntitySet, ClientEntity>(
                client, src, ContentType.parse(format))));
      } else if (ClientEntitySet.class.isAssignableFrom(reference)) {
        final ResWrap<EntityCollection> resource = client.getDeserializer(ContentType.parse(format))
            .toEntitySet(src);
        res = new ResWrap<T>(
            resource.getContextURL(),
            resource.getMetadataETag(),
            reference.cast(client.getBinder().getODataEntitySet(resource)));
      } else if (ClientEntity.class.isAssignableFrom(reference)) {
        final ResWrap<Entity> container = client.getDeserializer(ContentType.parse(format)).toEntity(src);
        res = new ResWrap<T>(
            container.getContextURL(),
            container.getMetadataETag(),
            reference.cast(client.getBinder().getODataEntity(container)));
      } else if (ClientProperty.class.isAssignableFrom(reference)) {
        final ResWrap<Property> container = client.getDeserializer(ContentType.parse(format)).toProperty(src);
        res = new ResWrap<T>(
            container.getContextURL(),
            container.getMetadataETag(),
            reference.cast(client.getBinder().getODataProperty(container)));
      } else if (ClientValue.class.isAssignableFrom(reference)) {
        res = new ResWrap<T>(
            (URI) null,
            null,
            reference.cast(client.getObjectFactory().newPrimitiveValueBuilder().
                setType(ContentType.parse(format).equals(ContentType.TEXT_PLAIN)
                    ? EdmPrimitiveTypeKind.String : EdmPrimitiveTypeKind.Stream).
                setValue(IOUtils.toString(src)) // TODO: set correct value
                .build()));
      } else if (XMLMetadata.class.isAssignableFrom(reference)) {
        res = new ResWrap<T>(
            (URI) null,
            null,
            reference.cast(readMetadata(src)));
      } else if (ClientServiceDocument.class.isAssignableFrom(reference)) {
        final ResWrap<ServiceDocument> resource =
            client.getDeserializer(ContentType.parse(format)).toServiceDocument(src);
        res = new ResWrap<T>(
            resource.getContextURL(),
            resource.getMetadataETag(),
            reference.cast(client.getBinder().getODataServiceDocument(resource.getPayload())));
      } else if (ODataError.class.isAssignableFrom(reference)) {
        res = new ResWrap<T>(
            (URI) null,
            null,
            reference.cast(readError(src, ContentType.parse(format))));
      } else {
        throw new IllegalArgumentException("Invalid reference type " + reference);
      }
    } catch (Exception e) {
      LOG.warn("Cast error", e);
      res = null;
    } finally {
      if (!ClientEntitySetIterator.class.isAssignableFrom(reference)) {
        IOUtils.closeQuietly(src);
      }
    }

    return res;
  }

  @Override
  public ClientEntitySet readEntitySet(final InputStream input, final ContentType contentType)
      throws ODataDeserializerException {
    return client.getBinder().getODataEntitySet(client.getDeserializer(contentType).toEntitySet(input));
  }

  @Override
  public ClientEntity readEntity(final InputStream input, final ContentType contentType)
      throws ODataDeserializerException {
    return client.getBinder().getODataEntity(client.getDeserializer(contentType).toEntity(input));
  }

  @Override
  public ClientProperty readProperty(final InputStream input, final ContentType contentType)
      throws ODataDeserializerException {
    return client.getBinder().getODataProperty(client.getDeserializer(contentType).toProperty(input));
  }

  @Override
  public Edm readMetadata(InputStream input, List<InputStream> termDefinitions) {
    return readMetadata(client.getDeserializer(ContentType.APPLICATION_XML).toMetadata(input).getSchemaByNsOrAlias(),
        client.getDeserializer(ContentType.APPLICATION_XML).fetchTermDefinitionSchema(termDefinitions));
  }

  @Override
  public Edm readMetadata(Map<String, CsdlSchema> xmlSchemas, List<CsdlSchema> termDefinitionSchema) {
    ClientCsdlEdmProvider prov = new ClientCsdlEdmProvider(xmlSchemas);
    return new EdmProviderImpl(prov, termDefinitionSchema);
  }
  
  @Override
  public Edm readMetadata(XMLMetadata metadata, List<InputStream> termDefinitions) {
    return readMetadata(metadata.getSchemaByNsOrAlias(),
        client.getDeserializer(ContentType.APPLICATION_XML).fetchTermDefinitionSchema(termDefinitions));
  }
}
