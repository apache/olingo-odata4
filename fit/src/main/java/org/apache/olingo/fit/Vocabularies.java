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
package org.apache.olingo.fit;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.utils.Accept;
import org.apache.olingo.fit.utils.ConstantKey;
import org.apache.olingo.fit.utils.Constants;
import org.apache.olingo.fit.utils.FSManager;
import org.apache.olingo.fit.utils.XMLUtilities;
import org.springframework.stereotype.Service;

@Service
@Path("/V40/Vocabularies.svc")
public class Vocabularies {

  private final XMLUtilities xml;

  public Vocabularies() throws IOException {
    Metadata metadata = new Metadata(FSManager.instance()
        .readRes("vocabularies-" + Constants.get(ConstantKey.METADATA), Accept.XML));
    xml = new XMLUtilities(metadata);
  }

  @GET
  @Path("/$metadata")
  @Produces(MediaType.APPLICATION_XML)
  public Response getMetadata() {
    try {
      return xml.createResponse(
          null,
          FSManager.instance().readRes("vocabularies-" + Constants.get(ConstantKey.METADATA), Accept.XML),
              null,
              Accept.XML);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.XML.toString(), e);
    }
  }

  @GET
  @Path("/{vocabulary}")
  @Produces(MediaType.APPLICATION_XML)
  public Response getVocabulary(@PathParam("vocabulary") final String vocabulary) {
    try {
      return xml.createResponse(
          null,
          FSManager.instance().readFile(vocabulary, null),
          null,
          Accept.XML);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.XML.toString(), e);
    }
  }
}
