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
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.CollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.data.JefDataProvider;
import org.apache.olingo.server.tecsvc.provider.ContainerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class TechnicalProcessor implements CollectionProcessor, EntityProcessor {


  private static final Logger LOG = LoggerFactory.getLogger(TechnicalProcessor.class);

  private OData odata;
  private Edm edm;
  private DataProvider dataProvider;

  public TechnicalProcessor(final DataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  @Override
  public void init(OData odata, Edm edm) {
    this.odata = odata;
    this.edm = edm;
    if(dataProvider == null) {
      this.dataProvider = new JefDataProvider(edm);
    }
  }

  @Override
  public void readCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
    long time = System.nanoTime();

    LOG.info((System.nanoTime() - time) / 1000 + " microseconds");
    time = System.nanoTime();
    ODataSerializer serializer = odata.createSerializer(ODataFormat.JSON);
    EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    ContextURL contextUrl = getContextUrl(request, edmEntitySet.getEntityType());
    try {
      EntitySet entitySet = readEntitySetInternal(edmEntitySet, contextUrl);
      if(entitySet == null) {
        response.setStatusCode(HttpStatusCode.NOT_FOUND.getStatusCode());
      } else {
        response.setContent(serializer.entitySet(edmEntitySet, entitySet, contextUrl));
        LOG.info("Finished in " + (System.nanoTime() - time) / 1000 + " microseconds");

        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader("Content-Type", ContentType.APPLICATION_JSON.toContentTypeString());
      }
    } catch (DataProvider.DataProviderException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  @Override
  public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
    long time = System.nanoTime();

    LOG.info((System.nanoTime() - time) / 1000 + " microseconds");
    time = System.nanoTime();
    ODataSerializer serializer = odata.createSerializer(ODataFormat.JSON);
    EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    try {
      Entity entity = readEntityInternal(uriInfo, edmEntitySet);
      if(entity == null) {
        response.setStatusCode(HttpStatusCode.NOT_FOUND.getStatusCode());
      } else {
        response.setContent(serializer.entity(edmEntitySet.getEntityType(), entity,
                getContextUrl(request, edmEntitySet.getEntityType())));
        LOG.info("Finished in " + (System.nanoTime() - time) / 1000 + " microseconds");

        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader("Content-Type", ContentType.APPLICATION_JSON.toContentTypeString());
      }
    } catch (DataProvider.DataProviderException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  private Entity readEntityInternal(UriInfo uriInfo, EdmEntitySet entitySet)
          throws DataProvider.DataProviderException {
    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    if(!resourcePaths.isEmpty()) {
      UriResource res = resourcePaths.get(resourcePaths.size()-1);
      if(res.getKind() == UriResourceKind.entitySet) {
        UriResourceEntitySet resourceEntitySet = (UriResourceEntitySet) res;
        String key = resourceEntitySet.getKeyPredicates().get(0).getText();
        return dataProvider.read(entitySet.getName(), key);
      }
    }
    throw new RuntimeException("Invalid resource paths.. " + resourcePaths);
  }

  private ContextURL getContextUrl(ODataRequest request, EdmEntityType entityType) {
    return ContextURL.getInstance(URI.create(request.getRawBaseUri() + "/" + entityType.getName()));
  }

  private EntitySet readEntitySetInternal(EdmEntitySet edmEntitySet, ContextURL contextUrl)
          throws DataProvider.DataProviderException {
    EntitySet entitySet = dataProvider.readAll(edmEntitySet.getName());
    try {
      entitySet.setNext(new URI(contextUrl.getServiceRoot().toASCIIString() + "/" +
              edmEntitySet.getEntityType().getName()));
    } catch (URISyntaxException e) {
      throw new RuntimeException("Invalid uri syntax.", e);
    }
    return entitySet;
  }

  private EdmEntitySet getEdmEntitySet(UriInfo uriInfo) {
    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    if(resourcePaths.isEmpty()) {
      throw new RuntimeException("Invalid resource path.");
    }
    String entitySetName = resourcePaths.get(resourcePaths.size()-1).toString();
    return edm.getEntityContainer(ContainerProvider.nameContainer).getEntitySet(entitySetName);
  }
}
