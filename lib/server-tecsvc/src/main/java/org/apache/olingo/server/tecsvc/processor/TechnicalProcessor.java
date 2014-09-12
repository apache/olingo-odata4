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
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.ODataSerializerOptions;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.tecsvc.data.DataProvider;

import java.util.List;
import java.util.Locale;

public class TechnicalProcessor implements EntityCollectionProcessor, EntityProcessor {

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
  public void readCollection(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
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
        ODataSerializer serializer = odata.createSerializer(ODataFormat.fromContentType(requestedContentType));
        response.setContent(serializer.entitySet(edmEntitySet, entitySet,
            ODataSerializerOptions.with()
                .contextURL(getContextUrl(edmEntitySet, false))
                .count(uriInfo.getCountOption())
                .expand(uriInfo.getExpandOption()).select(uriInfo.getSelectOption())
                .build()));
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
      }
    } catch (final DataProvider.DataProviderException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final ODataTranslatedException e) {
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
        ODataSerializer serializer = odata.createSerializer(ODataFormat.fromContentType(requestedContentType));
        response.setContent(serializer.entity(edmEntitySet, entity,
            ODataSerializerOptions.with()
                .contextURL(getContextUrl(edmEntitySet, true))
                .count(uriInfo.getCountOption())
                .expand(uriInfo.getExpandOption())
                .select(uriInfo.getSelectOption())
                .build()));
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
      }
    } catch (final DataProvider.DataProviderException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final ODataTranslatedException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    } catch (final ODataApplicationException e) {
      response.setStatusCode(e.getStatusCode());
    }
  }

  private EntitySet readEntitySetInternal(final EdmEntitySet edmEntitySet) throws DataProvider.DataProviderException {
    EntitySet entitySet = dataProvider.readAll(edmEntitySet);
    // TODO: set count and next link
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
    if (resourcePaths.size() != 1) {
      throw new ODataApplicationException("Invalid resource path.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
    if (!(resourcePaths.get(0) instanceof UriResourceEntitySet)) {
      throw new ODataApplicationException("Invalid resource type.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
    final UriResourceEntitySet uriResource = (UriResourceEntitySet) resourcePaths.get(0);
    if (uriResource.getTypeFilterOnCollection() != null || uriResource.getTypeFilterOnEntry() != null) {
      throw new ODataApplicationException("Type filters are not supported.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
    return uriResource.getEntitySet();
  }

  private ContextURL getContextUrl(final EdmEntitySet entitySet, final boolean isSingleEntity) {
    return ContextURL.with().entitySet(entitySet).suffix(isSingleEntity ? Suffix.ENTITY : null).build();
  }
}
