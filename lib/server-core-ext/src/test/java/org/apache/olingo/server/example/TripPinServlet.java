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
package org.apache.olingo.server.example;

import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.core.MetadataParser;
import org.apache.olingo.server.core.OData4Impl;

public class TripPinServlet extends HttpServlet {
  private static final long serialVersionUID = 2663595419366214401L;
  private TripPinDataModel dataModel;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
  }

  @Override
  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    OData odata = OData4Impl.newInstance();
    MetadataParser parser = new MetadataParser();
    ServiceMetadata metadata = null;

    try {
      parser.parseAnnotations(true);
      parser.useLocalCoreVocabularies(true);
      parser.implicitlyLoadCoreVocabularies(true);
      metadata = parser.buildServiceMetadata(new FileReader("src/test/resources/trippin.xml"));
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }

    ODataHttpHandler handler = odata.createHandler(metadata);

    if (this.dataModel == null) {
      try {
        this.dataModel = new TripPinDataModel(metadata);
      } catch (Exception e) {
        throw new IOException("Failed to load data for TripPin Service");
      }
    }

    handler.register(new TripPinHandler(this.dataModel));
    handler.process(request, response);
  }
}
