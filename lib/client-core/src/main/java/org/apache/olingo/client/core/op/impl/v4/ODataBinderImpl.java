/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.op.impl.v4;

import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.data.ServiceDocumentItem;
import org.apache.olingo.client.api.domain.ODataServiceDocument;
import org.apache.olingo.client.api.op.v4.ODataBinder;
import org.apache.olingo.client.api.utils.URIUtils;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.op.impl.AbstractODataBinder;

public class ODataBinderImpl extends AbstractODataBinder implements ODataBinder {

  private static final long serialVersionUID = -6371110655960799393L;

  public ODataBinderImpl(final ODataClient client) {
    super(client);
  }

  @Override
  public ODataServiceDocument getODataServiceDocument(final ServiceDocument resource) {
    final ODataServiceDocument serviceDocument = super.getODataServiceDocument(resource);

    serviceDocument.setMetadataContext(URIUtils.getURI(resource.getBaseURI(), resource.getMetadataContext()));
    serviceDocument.setMetadataETag(resource.getMetadataETag());

    for (ServiceDocumentItem functionImport : resource.getFunctionImports()) {
      serviceDocument.getFunctionImports().put(functionImport.getTitle(),
              URIUtils.getURI(resource.getBaseURI(), functionImport.getHref()));
    }
    for (ServiceDocumentItem singleton : resource.getSingletons()) {
      serviceDocument.getSingletons().put(singleton.getTitle(),
              URIUtils.getURI(resource.getBaseURI(), singleton.getHref()));
    }
    for (ServiceDocumentItem sdoc : resource.getRelatedServiceDocuments()) {
      serviceDocument.getRelatedServiceDocuments().put(sdoc.getTitle(),
              URIUtils.getURI(resource.getBaseURI(), sdoc.getHref()));
    }

    return serviceDocument;
  }
}
