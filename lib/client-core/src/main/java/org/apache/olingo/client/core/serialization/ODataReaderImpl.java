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
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.domain.ODataEntitySetIterator;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.serialization.ODataReader;
import org.apache.olingo.client.core.edm.EdmClientImpl;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataError;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
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
    return readMetadata(client.getDeserializer(ODataFormat.XML).toMetadata(input).getSchemaByNsOrAlias());
  }

  @Override
  public Edm readMetadata(final Map<String, Schema> xmlSchemas) {
    return new EdmClientImpl(xmlSchemas);
  }

  @Override
  public ODataServiceDocument readServiceDocument(final InputStream input, final ODataFormat format)
      throws ODataDeserializerException {
    return client.getBinder().getODataServiceDocument(
        client.getDeserializer(format).toServiceDocument(input).getPayload());
  }

  @Override
  public ODataError readError(final InputStream inputStream, final ODataFormat format)
      throws ODataDeserializerException {
    return client.getDeserializer(format).toError(inputStream);
  }

  @Override
  public <T> ResWrap<T> read(final InputStream src, final String format, final Class<T> reference)
      throws ODataDeserializerException {
    ResWrap<T> res;

    try {
      if (ODataEntitySetIterator.class.isAssignableFrom(reference)) {
        res = new ResWrap<T>(
            (URI) null,
            null,
            reference.cast(new ODataEntitySetIterator<ODataEntitySet, ODataEntity>(
                client, src, ODataFormat.fromString(format))));
      } else if (ODataEntitySet.class.isAssignableFrom(reference)) {
        final ResWrap<EntitySet> resource = client.getDeserializer(ODataFormat.fromString(format))
            .toEntitySet(src);
        res = new ResWrap<T>(
            resource.getContextURL(),
            resource.getMetadataETag(),
            reference.cast(client.getBinder().getODataEntitySet(resource)));
      } else if (ODataEntity.class.isAssignableFrom(reference)) {
        final ResWrap<Entity> container = client.getDeserializer(ODataFormat.fromString(format)).toEntity(src);
        res = new ResWrap<T>(
            container.getContextURL(),
            container.getMetadataETag(),
            reference.cast(client.getBinder().getODataEntity(container)));
      } else if (ODataProperty.class.isAssignableFrom(reference)) {
        final ResWrap<Property> container = client.getDeserializer(ODataFormat.fromString(format)).toProperty(src);
        res = new ResWrap<T>(
            container.getContextURL(),
            container.getMetadataETag(),
            reference.cast(client.getBinder().getODataProperty(container)));
      } else if (ODataValue.class.isAssignableFrom(reference)) {
        res = new ResWrap<T>(
            (URI) null,
            null,
            reference.cast(client.getObjectFactory().newPrimitiveValueBuilder().
                setType(ODataFormat.fromString(format) == ODataFormat.TEXT_PLAIN
                    ? EdmPrimitiveTypeKind.String : EdmPrimitiveTypeKind.Stream).
                setValue(IOUtils.toString(src)) // TODO: set correct value
                .build()));
      } else if (XMLMetadata.class.isAssignableFrom(reference)) {
        res = new ResWrap<T>(
            (URI) null,
            null,
            reference.cast(readMetadata(src)));
      } else if (ODataServiceDocument.class.isAssignableFrom(reference)) {
        final ResWrap<ServiceDocument> resource =
            client.getDeserializer(ODataFormat.fromString(format)).toServiceDocument(src);
        res = new ResWrap<T>(
            resource.getContextURL(),
            resource.getMetadataETag(),
            reference.cast(client.getBinder().getODataServiceDocument(resource.getPayload())));
      } else if (ODataError.class.isAssignableFrom(reference)) {
        res = new ResWrap<T>(
            (URI) null,
            null,
            reference.cast(readError(src, ODataFormat.fromString(format))));
      } else {
        throw new IllegalArgumentException("Invalid reference type " + reference);
      }
    } catch (Exception e) {
      LOG.warn("Cast error", e);
      res = null;
    } finally {
      if (!ODataEntitySetIterator.class.isAssignableFrom(reference)) {
        IOUtils.closeQuietly(src);
      }
    }

    return res;
  }

  @Override
  public ODataEntitySet readEntitySet(final InputStream input, final ODataFormat format)
      throws ODataDeserializerException {
    return client.getBinder().getODataEntitySet(client.getDeserializer(format).toEntitySet(input));
  }

  @Override
  public ODataEntity readEntity(final InputStream input, final ODataFormat format)
      throws ODataDeserializerException {
    return client.getBinder().getODataEntity(client.getDeserializer(format).toEntity(input));
  }

  @Override
  public ODataProperty readProperty(final InputStream input, final ODataFormat format)
      throws ODataDeserializerException {
    return client.getBinder().getODataProperty(client.getDeserializer(format).toProperty(input));
  }
}
