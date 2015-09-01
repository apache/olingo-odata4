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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.fit.methods.PATCH;
import org.apache.olingo.fit.utils.Constants;
import org.springframework.stereotype.Service;

@Service
@Path("/V40/KeyAsSegment.svc")
public class KeyAsSegment extends Services {

  public KeyAsSegment() throws IOException {
    super();
  }

  private Response replaceServiceName(final Response response) {
    try {
      final String content = IOUtils.toString((InputStream) response.getEntity(), Constants.ENCODING).
          replaceAll("Static\\.svc", "KeyAsSegment.svc");

      final Response.ResponseBuilder builder = Response.status(response.getStatus());
      for (String headerName : response.getHeaders().keySet()) {
        for (Object headerValue : response.getHeaders().get(headerName)) {
          builder.header(headerName, headerValue);
        }
      }

      final InputStream toBeStreamedBack = IOUtils.toInputStream(content, Constants.ENCODING);
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
  @Override
  public Response getEntity(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      @QueryParam("$expand") @DefaultValue(StringUtils.EMPTY) final String expand,
      @QueryParam("$select") @DefaultValue(StringUtils.EMPTY) final String select) {

    return replaceServiceName(super.getEntityInternal(uriInfo.getRequestUri().toASCIIString(),
        accept, entitySetName, entityId, format, expand, select));
  }

  @DELETE
  @Path("/{entitySetName}/{entityId}")
  @Override
  public Response removeEntity(
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId) {

    return replaceServiceName(super.removeEntity(entitySetName, entityId));
  }

  @PATCH
  @Path("/{entitySetName}/{entityId}")
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  @Override
  public Response patchEntity(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @HeaderParam("If-Match") @DefaultValue(StringUtils.EMPTY) final String ifMatch,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      final String changes) {

    return replaceServiceName(super.patchEntity(uriInfo, accept, contentType, prefer, ifMatch, entitySetName, entityId,
        changes));
  }

  @PUT
  @Path("/{entitySetName}/{entityId}")
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  public Response putNewEntity(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      final String entity) {

    return replaceServiceName(super
        .replaceEntity(uriInfo, accept, contentType, prefer, entitySetName, entityId, entity));
  }

  @POST
  @Path("/{entitySetName}")
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM })
  @Override
  public Response postNewEntity(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @PathParam("entitySetName") final String entitySetName,
      final String entity) {

    return replaceServiceName(super.postNewEntity(uriInfo, accept, contentType, prefer, entitySetName, entity));
  }
}
