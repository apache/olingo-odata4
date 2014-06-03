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

import java.io.ByteArrayInputStream;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.EntitySetProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.tecsvc.data.DataProvider;

public class TechnicalProcessor implements EntitySetProcessor, EntityProcessor {

  private OData odata;
  private Edm edm;
  private final DataProvider dataProvider;

  public TechnicalProcessor(DataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  @Override
  public void init(OData odata, Edm edm) {
    this.odata = odata;
    this.edm = edm;
  }

  @Override
  public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
    response.setContent(new ByteArrayInputStream("Entity".getBytes()));
    response.setStatusCode(200);
  }

  @Override
  public void readEntitySet(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
    response.setContent(new ByteArrayInputStream("EntitySet".getBytes()));
    response.setStatusCode(200);
  }
}
