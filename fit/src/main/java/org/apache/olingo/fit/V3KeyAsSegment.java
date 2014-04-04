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
package org.apache.olingo.fit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.status;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.fit.methods.MERGE;
import org.apache.olingo.fit.methods.PATCH;
import org.springframework.stereotype.Service;

@Service
@Path("/V30/KeyAsSegment.svc")
public class V3KeyAsSegment {

  private final V3Services services;

  public V3KeyAsSegment() throws Exception {
    this.services = new V3Services();
  }

  private Response replaceServiceName(final Response response) {
    try {
      final String content = IOUtils.toString((InputStream) response.getEntity(), "UTF-8").
              replaceAll("Static\\.svc", "KeyAsSegment.svc");

      final Response.ResponseBuilder builder = status(response.getStatus());
      for (String headerName : response.getHeaders().keySet()) {
        for (Object headerValue : response.getHeaders().get(headerName)) {
          builder.header(headerName, headerValue);
        }
      }

      final InputStream toBeStreamedBack = IOUtils.toInputStream(content, "UTF-8");
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      IOUtils.copy(toBeStreamedBack, baos);
      IOUtils.closeQuietly(toBeStreamedBack);

      builder.header("Content-Length", baos.size());
      builder.entity(new ByteArrayInputStream(baos.toByteArray()));

      return builder.build();
    } catch (Exception e) {
      return response;
    }
  }

  @GET
  @Path("/{entitySetName}/{entityId}")
  public Response getEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
          @QueryParam("$expand") @DefaultValue(StringUtils.EMPTY) String expand,
          @QueryParam("$select") @DefaultValue(StringUtils.EMPTY) String select) {

    return replaceServiceName(services.getEntityInternal(
            accept, entitySetName, entityId, format, expand, select, true));
  }

  @DELETE
  @Path("/{entitySetName}/{entityId}")
  public Response removeEntity(
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId) {

    return replaceServiceName(services.removeEntity(entitySetName, entityId));
  }

  @MERGE
  @Path("/{entitySetName}/{entityId}")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  public Response mergeEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @HeaderParam("If-Match") @DefaultValue(StringUtils.EMPTY) String ifMatch,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          final String changes) {

    return replaceServiceName(services.patchEntity(accept, prefer, ifMatch, entitySetName, entityId, changes));
  }

  @PATCH
  @Path("/{entitySetName}/{entityId}")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  public Response patchEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @HeaderParam("If-Match") @DefaultValue(StringUtils.EMPTY) String ifMatch,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          final String changes) {

    return replaceServiceName(services.patchEntity(accept, prefer, ifMatch, entitySetName, entityId, changes));
  }

  @PUT
  @Path("/{entitySetName}/{entityId}")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  public Response putNewEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          final String entity) {

    return replaceServiceName(services.replaceEntity(accept, prefer, entitySetName, entityId, entity));
  }

  @POST
  @Path("/{entitySetName}")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM})
  public Response postNewEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          @PathParam("entitySetName") String entitySetName,
          final String entity) {

    return replaceServiceName(services.postNewEntity(accept, prefer, entitySetName, entity));
  }
}
