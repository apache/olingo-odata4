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
import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
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
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.utils.AbstractUtilities;
import org.apache.olingo.fit.utils.Accept;
import org.apache.olingo.fit.utils.Commons;
import org.apache.olingo.fit.utils.ConstantKey;
import org.apache.olingo.fit.utils.Constants;
import org.apache.olingo.fit.utils.FSManager;
import org.springframework.stereotype.Service;

@Service
@Path("/V30/ActionOverloading.svc")
public class V3ActionOverloading extends V3Services {

  public V3ActionOverloading() throws Exception {
    super(new Metadata(FSManager.instance(ODataServiceVersion.V30).readRes(
        "actionOverloading" + StringUtils.capitalize(Constants.get(ODataServiceVersion.V30, ConstantKey.METADATA)),
        Accept.XML), ODataServiceVersion.V30));
  }

  private Response replaceServiceName(final Response response) {
    try {
      final String content = IOUtils.toString((InputStream) response.getEntity(), Constants.ENCODING).
          replaceAll("Static\\.svc", "ActionOverloading.svc");

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
  @Path("/$metadata")
  @Produces(MediaType.APPLICATION_XML)
  @Override
  public Response getMetadata() {
    return super.getMetadata("actionOverloading"
        + StringUtils.capitalize(Constants.get(ODataServiceVersion.V30, ConstantKey.METADATA)));
  }

  @POST
  @Path("/RetrieveProduct")
  public Response unboundRetrieveProduct(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType) {

    final Accept acceptType;
    if (StringUtils.isNotBlank(format)) {
      acceptType = Accept.valueOf(format.toUpperCase());
    } else {
      acceptType = Accept.parse(accept, ODataServiceVersion.V30);
    }
    if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
      throw new UnsupportedMediaTypeException("Unsupported media type");
    }

    try {
      final InputStream result = FSManager.instance(ODataServiceVersion.V30).
          readFile("actionOverloadingRetrieveProduct", acceptType);
      return replaceServiceName(xml.createResponse(result, null, acceptType));
    } catch (Exception e) {
      return replaceServiceName(xml.createFaultResponse(accept, e));
    }
  }

  @GET
  @Path("/Product({entityId})")
  @Override
  public Response getProduct(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entityId") final String entityId,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    final Map.Entry<Accept, AbstractUtilities> utils = super.getUtilities(accept, format);

    if (utils.getKey() == Accept.XML || utils.getKey() == Accept.TEXT) {
      throw new UnsupportedMediaTypeException("Unsupported media type");
    }

    final Map.Entry<String, InputStream> entityInfo = utils.getValue().readEntity("Product", entityId, utils.getKey());

    InputStream entity = entityInfo.getValue();
    try {
      if (utils.getKey() == Accept.JSON_FULLMETA || utils.getKey() == Accept.ATOM) {
        entity = utils.getValue().addOperation(entity, "RetrieveProduct", "#DefaultContainer.RetrieveProduct",
            uriInfo.getAbsolutePath().toASCIIString()
                + "/RetrieveProduct");
      }

      return replaceServiceName(utils.getValue().createResponse(
          entity, Commons.getETag(entityInfo.getKey(), ODataServiceVersion.V30), utils.getKey()));
    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return replaceServiceName(xml.createFaultResponse(accept, e));
    }
  }

  @POST
  @Path("/Product({entityId})/{path:.*RetrieveProduct}")
  public Response productBoundRetrieveProduct(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType) {

    return unboundRetrieveProduct(accept, format, contentType);
  }

  @GET
  @Path("/OrderLine(OrderId={orderId},ProductId={productId})")
  public Response getOrderLine(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("orderId") final String orderId,
      @PathParam("productId") final String productId,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    final Map.Entry<Accept, AbstractUtilities> utils = super.getUtilities(accept, format);

    if (utils.getKey() == Accept.XML || utils.getKey() == Accept.TEXT) {
      throw new UnsupportedMediaTypeException("Unsupported media type");
    }

    final Map.Entry<String, InputStream> entityInfo = utils.getValue().
        readEntity("OrderLine", orderId + " " + productId, utils.getKey());

    InputStream entity = entityInfo.getValue();
    try {
      if (utils.getKey() == Accept.JSON_FULLMETA || utils.getKey() == Accept.ATOM) {
        entity = utils.getValue().addOperation(entity, "RetrieveProduct", "#DefaultContainer.RetrieveProduct",
            uriInfo.getAbsolutePath().toASCIIString()
                + "/RetrieveProduct");
      }

      return replaceServiceName(utils.getValue().createResponse(
          entity, Commons.getETag(entityInfo.getKey(), ODataServiceVersion.V30), utils.getKey()));
    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return replaceServiceName(xml.createFaultResponse(accept, e));
    }
  }

  @POST
  @Path("/OrderLine(OrderId={orderId},ProductId={productId})/{path:.*RetrieveProduct}")
  public Response orderLineBoundRetrieveProduct(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType) {

    return unboundRetrieveProduct(accept, format, contentType);
  }

}
