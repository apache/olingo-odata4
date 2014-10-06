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
package org.apache.olingo.server.tecsvc.processor;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.EntitySetProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.PropertyProcessor;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.ODataSerializerOptions;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.apache.olingo.server.tecsvc.data.DataProvider;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

/**
 * Technical Processor which provides current implemented processor functionality.
 */
public class TechnicalProcessor implements EntitySetProcessor, EntityProcessor, PropertyProcessor {

  private OData odata;
  private DataProvider dataProvider;

  public TechnicalProcessor(final DataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  @Override
  public void init(final OData odata, final Edm edm) {
    this.odata = odata;
  }

  @Override
  public void readEntitySet(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) {
    if (!validateOptions(uriInfo.asUriInfoResource())) {
      response.setStatusCode(HttpStatusCode.NOT_IMPLEMENTED.getStatusCode());
      return;
    }
    try {
      final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
      final EntitySet entitySet = readEntitySetInternal(edmEntitySet);
      if (entitySet == null) {
        response.setStatusCode(HttpStatusCode.NOT_FOUND.getStatusCode());
      } else {
        final ODataFormat format = ODataFormat.fromContentType(requestedContentType);
        ODataSerializer serializer = odata.createSerializer(format);
        final ExpandOption expand = uriInfo.getExpandOption();
        final SelectOption select = uriInfo.getSelectOption();
        response.setContent(serializer.entitySet(edmEntitySet, entitySet,
            ODataSerializerOptions.with()
                .contextURL(format == ODataFormat.JSON_NO_METADATA ? null :
                    getContextUrl(serializer, edmEntitySet, false, expand, select, null))
                .count(uriInfo.getCountOption())
                .expand(expand).select(select)
                .build()));
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
      }
    } catch (final DataProvider.DataProviderException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final SerializerException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final ODataApplicationException e) {
      response.setStatusCode(e.getStatusCode());
    }
  }

  @Override
  public void readEntity(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) {
    if (!validateOptions(uriInfo.asUriInfoResource())) {
      response.setStatusCode(HttpStatusCode.NOT_IMPLEMENTED.getStatusCode());
      return;
    }
    try {
      final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
      final Entity entity = readEntityInternal(uriInfo.asUriInfoResource(), edmEntitySet);
      if (entity == null) {
        response.setStatusCode(HttpStatusCode.NOT_FOUND.getStatusCode());
      } else {
        final ODataFormat format = ODataFormat.fromContentType(requestedContentType);
        ODataSerializer serializer = odata.createSerializer(format);
        final ExpandOption expand = uriInfo.getExpandOption();
        final SelectOption select = uriInfo.getSelectOption();
        response.setContent(serializer.entity(edmEntitySet, entity,
            ODataSerializerOptions.with()
                .contextURL(format == ODataFormat.JSON_NO_METADATA ? null :
                    getContextUrl(serializer, edmEntitySet, true, expand, select, null))
                .count(uriInfo.getCountOption())
                .expand(expand).select(select)
                .build()));
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
      }
    } catch (final DataProvider.DataProviderException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final SerializerException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final ODataApplicationException e) {
      response.setStatusCode(e.getStatusCode());
    }
  }

  @Override
  public void countEntitySet(ODataRequest request, ODataResponse response, UriInfo uriInfo) {
    try {
      EntitySet entitySet = null;
      final UriInfoResource uriResource = uriInfo.asUriInfoResource();
      final List<UriResource> resourceParts = uriResource.getUriResourceParts();
      if (isCount(resourceParts)) {
        int pos = resourceParts.size() - 2;
        if (pos >= 0) {
          final UriResourceEntitySet ur =
              (UriResourceEntitySet) uriResource.getUriResourceParts().get(pos);
          entitySet = readEntitySetInternal(ur.getEntitySet(), true);
        }
      }

      if (entitySet == null) {
        response.setStatusCode(HttpStatusCode.NOT_FOUND.getStatusCode());
      } else {
        Integer count = entitySet.getCount();
        response.setContent(new ByteArrayInputStream(count.toString().getBytes()));
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, "text/plain");
      }
    } catch (final DataProvider.DataProviderException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  private boolean isCount(List<UriResource> resourceParts) {
    if (resourceParts.isEmpty()) {
      return false;
    }
    UriResource part = resourceParts.get(resourceParts.size() - 1);
    return SystemQueryOptionKind.COUNT.toString().equals(part.toString());
  }

  private EntitySet readEntitySetInternal(final EdmEntitySet edmEntitySet) throws DataProvider.DataProviderException {
    return readEntitySetInternal(edmEntitySet, false);
  }

  private EntitySet readEntitySetInternal(final EdmEntitySet edmEntitySet,
      boolean withCount) throws DataProvider.DataProviderException {
    EntitySet entitySet = dataProvider.readAll(edmEntitySet);
    // TODO: set count (correctly) and next link
    if (withCount && entitySet.getCount() == null) {
      entitySet.setCount(entitySet.getEntities().size());
    }
    //
    return entitySet;
  }

  private Entity readEntityInternal(final UriInfoResource uriInfo, final EdmEntitySet entitySet)
      throws DataProvider.DataProviderException {
    final UriResourceEntitySet resourceEntitySet = (UriResourceEntitySet) uriInfo.getUriResourceParts().get(0);
    return dataProvider.read(entitySet, resourceEntitySet.getKeyPredicates());
  }

  private boolean validateOptions(final UriInfoResource uriInfo) {
    return uriInfo.getCountOption() == null
        && uriInfo.getCustomQueryOptions().isEmpty()
        && uriInfo.getFilterOption() == null
        && uriInfo.getIdOption() == null
        && uriInfo.getOrderByOption() == null
        && uriInfo.getSearchOption() == null
        && uriInfo.getSkipOption() == null
        && uriInfo.getSkipTokenOption() == null
        && uriInfo.getTopOption() == null;
  }

  private EdmEntitySet getEdmEntitySet(final UriInfoResource uriInfo) throws ODataApplicationException {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    // first must be entity set
    if (!(resourcePaths.get(0) instanceof UriResourceEntitySet)) {
      throw new ODataApplicationException("Invalid resource type.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    List<UriResource> subResPaths = resourcePaths.subList(1, resourcePaths.size());
    for (UriResource subResPath : subResPaths) {
      UriResourceKind kind = subResPath.getKind();
      if(kind != UriResourceKind.primitiveProperty
              && kind != UriResourceKind.complexProperty
              && kind != UriResourceKind.count
              && kind != UriResourceKind.value) {
        throw new ODataApplicationException("Invalid resource type.",
                HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
      }
    }

    //
    final UriResourceEntitySet uriResource = (UriResourceEntitySet) resourcePaths.get(0);
    if (uriResource.getTypeFilterOnCollection() != null || uriResource.getTypeFilterOnEntry() != null) {
      throw new ODataApplicationException("Type filters are not supported.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
    return uriResource.getEntitySet();
  }

  private ContextURL getContextUrl(final ODataSerializer serializer,
        final EdmEntitySet entitySet, final boolean isSingleEntity,
        final ExpandOption expand, final SelectOption select, final String navOrPropertyPath)
      throws SerializerException {

    return ContextURL.with().entitySet(entitySet)
        .selectList(serializer.buildContextURLSelectList(entitySet, expand, select))
        .suffix(isSingleEntity ? Suffix.ENTITY : null)
        .navOrPropertyPath(navOrPropertyPath)
        .build();
  }

  @Override
  public void readProperty(ODataRequest request,
                           ODataResponse response, UriInfo uriInfo, ContentType contentType) {

    if (!validateOptions(uriInfo.asUriInfoResource())) {
      response.setStatusCode(HttpStatusCode.NOT_IMPLEMENTED.getStatusCode());
      return;
    }
    try {
      final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
      final Entity entity = readEntityInternal(uriInfo.asUriInfoResource(), edmEntitySet);
      if (entity == null) {
        response.setStatusCode(HttpStatusCode.NOT_FOUND.getStatusCode());
      } else {
        UriResourceProperty uriProperty = (UriResourceProperty) uriInfo
            .getUriResourceParts().get(uriInfo.getUriResourceParts().size() - 1);
        EdmProperty edmProperty = uriProperty.getProperty();
        Property property = entity.getProperty(edmProperty.getName());
        if (property == null) {
          response.setStatusCode(HttpStatusCode.NOT_FOUND.getStatusCode());
        } else {
          if (property.getValue() == null) {
            response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
          } else {
            final ODataFormat format = ODataFormat.fromContentType(contentType);
            ODataSerializer serializer = odata.createSerializer(format);
            response.setContent(serializer.entityProperty(edmProperty, property,
                ODataSerializerOptions.with()
                    .contextURL(format == ODataFormat.JSON_NO_METADATA ? null :
                        getContextUrl(serializer, edmEntitySet, true, null, null, edmProperty.getName()))
                    .build()));
            response.setStatusCode(HttpStatusCode.OK.getStatusCode());
            response.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
          }
        }
      }
    } catch (final DataProvider.DataProviderException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final SerializerException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final ODataApplicationException e) {
      response.setStatusCode(e.getStatusCode());
    }
  }

  @Override
  public void readPropertyValue(ODataRequest request, ODataResponse response,
                                UriInfo uriInfo, ContentType contentType) {

    if (!validateOptions(uriInfo.asUriInfoResource())) {
      response.setStatusCode(HttpStatusCode.NOT_IMPLEMENTED.getStatusCode());
      return;
    }
    try {
      final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
      final Entity entity = readEntityInternal(uriInfo.asUriInfoResource(), edmEntitySet);
      if (entity == null) {
        response.setStatusCode(HttpStatusCode.NOT_FOUND.getStatusCode());
      } else {
        UriResourceProperty uriProperty = (UriResourceProperty) uriInfo
                .getUriResourceParts().get(uriInfo.getUriResourceParts().size() - 2);
        EdmProperty edmProperty = uriProperty.getProperty();
        Property property = entity.getProperty(edmProperty.getName());
        if (property == null || property.getValue() == null) {
          response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
        } else {
          final EdmPrimitiveType type = (EdmPrimitiveType) edmProperty.getType();
          final String value = type.valueToString(property.getValue(),
                  edmProperty.isNullable(), edmProperty.getMaxLength(),
                  edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode());
          response.setContent(new ByteArrayInputStream(value.getBytes("UTF-8")));
          response.setStatusCode(HttpStatusCode.OK.getStatusCode());
          response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.TEXT_PLAIN.toContentTypeString());
        }
      }
    } catch (final DataProvider.DataProviderException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final EdmPrimitiveTypeException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final UnsupportedEncodingException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final ODataApplicationException e) {
      response.setStatusCode(e.getStatusCode());
    }
  }
}
