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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import org.apache.olingo.fit.utils.XHTTPMethodInterceptor;
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
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.methods.MERGE;
import org.apache.olingo.fit.methods.PATCH;
import org.apache.olingo.fit.utils.AbstractUtilities;
import org.apache.olingo.fit.utils.Accept;
import org.apache.olingo.fit.utils.Commons;
import org.apache.olingo.fit.utils.ConstantKey;
import org.apache.olingo.fit.utils.Constants;
import org.apache.olingo.fit.utils.FSManager;
import org.apache.olingo.fit.utils.LinkInfo;
import org.springframework.stereotype.Service;

@Service
@Path("/V30/Static.svc")
@InInterceptors(classes = XHTTPMethodInterceptor.class)
public class V3Services extends AbstractServices {

  public V3Services() throws Exception {
    super(ODataServiceVersion.V30, Commons.getMetadata(ODataServiceVersion.V30));
  }

  protected V3Services(final Metadata metadata) throws Exception {
    super(ODataServiceVersion.V30, metadata);
  }

  @GET
  @Path("/InStreamErrorGetCustomer")
  public Response instreamErrorGetCustomer(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

    final Accept acceptType;
    if (StringUtils.isNotBlank(format)) {
      acceptType = Accept.valueOf(format.toUpperCase());
    } else {
      acceptType = Accept.parse(accept, version);
    }

    try {
      final InputStream error = FSManager.instance(version).readFile("InStreamErrorGetCustomer", acceptType);

      return Response.ok(error).
              header(Constants.get(version, ConstantKey.ODATA_SERVICE_VERSION), version + ";").
              header("Content-Type", acceptType.toString(version)).
              build();
    } catch (Exception e) {
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      return xml.createFaultResponse(accept, e);
    }
  }

  /**
   * Provide sample large metadata.
   *
   * @return metadata.
   */
  @GET
  @Path("/large/$metadata")
  @Produces(MediaType.APPLICATION_XML)
  public Response getLargeMetadata() {
    return getMetadata("large" + StringUtils.capitalize(Constants.get(version, ConstantKey.METADATA)));
  }

  @Override
  protected void setInlineCount(final EntitySet feed, final String count) {
    if ("allpages".equals(count)) {
      feed.setCount(feed.getEntities().size());
    }
  }

  @Override
  public InputStream exploreMultipart(
          final List<Attachment> attachments, final String boundary, final boolean contineOnError)
          throws IOException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    Response res = null;
    boolean goon = true;
    for (int i = 0; i < attachments.size() && goon; i++) {
      try {
        final Attachment obj = attachments.get(i);
        bos.write(("--" + boundary).getBytes());
        bos.write(Constants.CRLF);

        final Object content = obj.getDataHandler().getContent();
        if (content instanceof MimeMultipart) {
          final Map<String, String> references = new HashMap<String, String>();

          final String cboundary = "changeset_" + UUID.randomUUID().toString();
          bos.write(("Content-Type: multipart/mixed;boundary=" + cboundary).getBytes());
          bos.write(Constants.CRLF);
          bos.write(Constants.CRLF);

          final ByteArrayOutputStream chbos = new ByteArrayOutputStream();
          String lastContebtID = null;
          try {
            for (int j = 0; j < ((MimeMultipart) content).getCount(); j++) {
              final MimeBodyPart part = (MimeBodyPart) ((MimeMultipart) content).getBodyPart(j);
              lastContebtID = part.getContentID();
              addChangesetItemIntro(chbos, lastContebtID, cboundary);

              res = bodyPartRequest(new MimeBodyPart(part.getInputStream()), references);
              if (res.getStatus() >= 400) {
                throw new Exception("Failure processing changeset");
              }

              addSingleBatchResponse(res, lastContebtID, chbos);
              references.put("$" + lastContebtID, res.getHeaderString("Location"));
            }

            bos.write(chbos.toByteArray());
            IOUtils.closeQuietly(chbos);

            bos.write(("--" + cboundary + "--").getBytes());
            bos.write(Constants.CRLF);
          } catch (Exception e) {
            LOG.warn("While processing changeset", e);
            IOUtils.closeQuietly(chbos);

            addChangesetItemIntro(bos, lastContebtID, cboundary);

            if (res == null || res.getStatus() < 400) {
              addErrorBatchResponse(e, "1", bos);
            } else {
              addSingleBatchResponse(res, lastContebtID, bos);
            }

            goon = contineOnError;
          }
        } else {
          addItemIntro(bos);

          res = bodyPartRequest(new MimeBodyPart(obj.getDataHandler().getInputStream()));

          if (res.getStatus() >= 400) {
            goon = contineOnError;
            throw new Exception("Failure processing changeset");
          }

          addSingleBatchResponse(res, bos);
        }

      } catch (Exception e) {
        if (res == null || res.getStatus() < 400) {
          addErrorBatchResponse(e, bos);
        } else {
          addSingleBatchResponse(res, bos);
        }
      }
    }

    bos.write(("--" + boundary + "--").getBytes());

    return new ByteArrayInputStream(bos.toByteArray());
  }

  @GET
  @Path("/Login({entityId})")
  public Response getLogin(
          @Context UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @PathParam("entityId") String entityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
          @QueryParam("$expand") @DefaultValue(StringUtils.EMPTY) String expand,
          @QueryParam("$select") @DefaultValue(StringUtils.EMPTY) String select) {

    return super.getEntityInternal(uriInfo.getRequestUri().toASCIIString(), accept,
            "Login", StringUtils.remove(entityId, "'"), format, expand, select, false);
  }

  @POST
  @Path("/Login")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM})
  public Response postLogin(
          @Context UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) String prefer,
          final String entity) {

    if ("{\"odata.type\":\"Microsoft.Test.OData.Services.AstoriaDefaultService.Login\"}".equals(entity)) {
      return xml.createFaultResponse(accept, new BadRequestException());
    }

    return super.postNewEntity(uriInfo, accept, contentType, prefer, "Login", entity);
  }

  /**
   * Retrieve links sample.
   *
   * @param accept Accept header.
   * @param entitySetName Entity set name.
   * @param entityId entity id.
   * @param linkName link name.
   * @param format format query option.
   * @return links.
   */
  @GET
  @Path("/{entitySetName}({entityId})/$links/{linkName}")
  public Response getLinks(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("linkName") String linkName,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      if (acceptType == Accept.ATOM) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final LinkInfo links = xml.readLinks(entitySetName, entityId, linkName, acceptType);

      return xml.createResponse(
              links.getLinks(),
              links.getEtag(),
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/{entitySetName}({entityId})/$links/{linkName}")
  public Response postLink(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("linkName") String linkName,
          String link,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      if (acceptType == Accept.ATOM) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final Accept content;
      if (StringUtils.isNotBlank(contentType)) {
        content = Accept.parse(contentType, version);
      } else {
        content = acceptType;
      }

      final AbstractUtilities utils = getUtilities(acceptType);

      final List<String> links;
      if (content == Accept.XML || content == Accept.TEXT || content == Accept.ATOM) {
        links = xml.extractLinkURIs(IOUtils.toInputStream(link, Constants.ENCODING)).getValue();
      } else {
        links = json.extractLinkURIs(IOUtils.toInputStream(link, Constants.ENCODING)).getValue();
      }

      utils.putLinksInMemory(
              Commons.getEntityBasePath(entitySetName, entityId),
              entitySetName,
              entityId,
              linkName,
              links);

      return xml.createResponse(null, null, null, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @MERGE
  @Path("/{entitySetName}({entityId})/$links/{linkName}")
  public Response mergeLink(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("linkName") String linkName,
          String link,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    return putLink(accept, contentType, entitySetName, entityId, linkName, link, format);
  }

  @PATCH
  @Path("/{entitySetName}({entityId})/$links/{linkName}")
  public Response patchLink(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("linkName") String linkName,
          String link,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    return putLink(accept, contentType, entitySetName, entityId, linkName, link, format);
  }

  @PUT
  @Path("/{entitySetName}({entityId})/$links/{linkName}")
  public Response putLink(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("linkName") String linkName,
          String link,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      if (acceptType == Accept.ATOM) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final Accept content;
      if (StringUtils.isNotBlank(contentType)) {
        content = Accept.parse(contentType, version);
      } else {
        content = acceptType;
      }

      final AbstractUtilities utils = getUtilities(acceptType);

      final List<String> links;
      if (content == Accept.XML || content == Accept.TEXT || content == Accept.ATOM) {
        links = xml.extractLinkURIs(IOUtils.toInputStream(link, Constants.ENCODING)).getValue();
      } else {
        links = json.extractLinkURIs(IOUtils.toInputStream(link, Constants.ENCODING)).getValue();
      }

      utils.putLinksInMemory(
              Commons.getEntityBasePath(entitySetName, entityId),
              entitySetName,
              linkName,
              links);

      return xml.createResponse(null, null, null, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @DELETE
  @Path("/{entitySetName}({entityId})/$links/{linkName}({linkId})")
  public Response deleteLink(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("linkName") String linkName,
          @PathParam("linkId") String linkId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {
    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      if (acceptType == Accept.ATOM) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final AbstractUtilities utils = getUtilities(acceptType);

      final Map.Entry<String, List<String>> currents = json.extractLinkURIs(utils.readLinks(
              entitySetName, entityId, linkName, Accept.JSON_FULLMETA).getLinks());

      final Map.Entry<String, List<String>> toBeRemoved = json.extractLinkURIs(utils.readLinks(
              entitySetName, entityId, linkName + "(" + linkId + ")", Accept.JSON_FULLMETA).getLinks());

      final List<String> remains = currents.getValue();
      remains.removeAll(toBeRemoved.getValue());

      utils.putLinksInMemory(
              Commons.getEntityBasePath(entitySetName, entityId),
              entitySetName,
              linkName,
              remains);

      return xml.createResponse(null, null, null, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }
}
