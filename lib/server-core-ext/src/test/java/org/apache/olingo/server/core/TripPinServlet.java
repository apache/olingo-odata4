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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.core.deserializer.json.ODataJsonDeserializer;

public class TripPinServlet extends HttpServlet {
  private static final long serialVersionUID = 2663595419366214401L;
  private HashMap<String, EntitySet> entitySetMap;

  @Override
  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    OData odata = OData4Impl.newInstance();
    MetadataParser parser = new MetadataParser();
    EdmProvider edmProvider = null;

    try {
      edmProvider = parser.buildEdmProvider(new FileReader("src/test/resources/trippin.xml"));
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }

    ServiceMetadata metadata = odata.createServiceMetadata(edmProvider, Collections.EMPTY_LIST);

    ODataHttpHandler handler = odata.createHandler(metadata);

    if (this.entitySetMap == null) {
      loadData(metadata);
    }

    handler.register(new TripPinHandler(entitySetMap));
    handler.process(request, response);
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

  }

  private void loadData (ServiceMetadata metadata)  {
    if (this.entitySetMap == null) {
      this.entitySetMap = new HashMap<String, EntitySet>();
    }
    EdmEntityContainer ec = metadata.getEdm().getEntityContainer(null);
    for (EdmEntitySet edmEntitySet: ec.getEntitySets()){
      try {
        ODataJsonDeserializer deserializer = new ODataJsonDeserializer();
        String entitySetName = edmEntitySet.getName();
        EntitySet set = deserializer.entityCollection(new FileInputStream(new File("src/test/resources/"
            + entitySetName.toLowerCase() + ".json")), edmEntitySet.getEntityType());
        //TODO: the count needs to be part of deserializer
        set.setCount(set.getEntities().size());
        for (Entity entity:set.getEntities()) {
          ((EntityImpl)entity).setETag(UUID.randomUUID().toString());
          ((EntityImpl)entity).setId(new URI(TripPinHandler.buildLocation(entity, edmEntitySet)));
        }
        this.entitySetMap.put(entitySetName, set);
      } catch (FileNotFoundException e) {
        // keep going
      } catch (DeserializerException e) {
        // keep going
      } catch (URISyntaxException e) {
        //keep going
      }
    }
  }
}
