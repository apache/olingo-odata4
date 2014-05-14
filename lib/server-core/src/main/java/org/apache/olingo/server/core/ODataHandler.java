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

import java.io.InputStream;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.ODataServer;
import org.apache.olingo.server.api.serializer.ODataFormat;
import org.apache.olingo.server.api.serializer.ODataSerializer;

public class ODataHandler {

  private ODataServer server;
  private Edm edm;

  public ODataHandler(ODataServer server, Edm edm) {
    this.server = server;
    this.edm = edm;
  }

  public ODataResponse process(ODataRequest odRequest) {
    ODataResponse response = new ODataResponse();

    ODataSerializer serializer = server.createSerializer(ODataFormat.JSON);
    InputStream responseEntity = serializer.serviceDocument(edm, "http//:root");
  
    response.setStatusCode(200);
    response.setHeader("Content-Type", "application/json");
    response.setContent(responseEntity);
    
    return response;
  }

}

