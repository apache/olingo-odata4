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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.LinkImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.methods.PATCH;
import org.apache.olingo.fit.rest.ResolvingReferencesInterceptor;
import org.apache.olingo.fit.rest.XHTTPMethodInterceptor;
import org.apache.olingo.fit.utils.AbstractUtilities;
import org.apache.olingo.fit.utils.Accept;
import org.apache.olingo.fit.utils.Commons;
import org.apache.olingo.fit.utils.ConstantKey;
import org.apache.olingo.fit.utils.Constants;
import org.apache.olingo.fit.utils.FSManager;
import org.apache.olingo.fit.utils.LinkInfo;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.ws.rs.BadRequestException;

@Service
@Path("/V40/Static.svc")
@InInterceptors(classes = {XHTTPMethodInterceptor.class, ResolvingReferencesInterceptor.class})
public class V4Services extends AbstractServices {

  /**
   * CR/LF.
   */
  protected static final byte[] CRLF = {13, 10};

  protected static final Pattern RELENTITY_SELECT_PATTERN = Pattern.compile("^.*\\(\\$select=.*\\)$");

  protected static final Pattern CROSSJOIN_PATTERN = Pattern.compile(
          "^\\$crossjoin\\(.*\\)\\?\\$filter=\\([a-zA-Z/]+ eq [a-zA-Z/]+\\)$");

  private final Map<String, String> providedAsync = new HashMap<String, String>();

  public V4Services() throws IOException {
    super(ODataServiceVersion.V40, Commons.getMetadata(ODataServiceVersion.V40));
  }

  protected V4Services(final Metadata metadata) throws IOException {
    super(ODataServiceVersion.V40, metadata);
  }

  @GET
  @Path("/redirect/{name}({id})")
  public Response conformanceRedirect(
          @Context final UriInfo uriInfo,
          @PathParam("name") final String name,
          @PathParam("id") final String id) {
    return Response.temporaryRedirect(
            URI.create(uriInfo.getRequestUri().toASCIIString().replace("/redirect", ""))).build();
  }

  @GET
  @Path("/$crossjoin({elements:.*})")
  public Response crossjoin(
          @PathParam("elements") final String elements,
          @QueryParam("$filter") final String filter) {

    try {
      if (CROSSJOIN_PATTERN.matcher("$crossjoin(" + elements + ")?$filter=" + filter).matches()) {
        final InputStream feed = FSManager.instance(version).readFile("crossjoin", Accept.JSON);

        return xml.createResponse(feed, null, Accept.JSON_FULLMETA);
      } else {
        throw new IOException("Unexpected crossjoin pattern");
      }
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.JSON.toString(version), e);
    }
  }

  @GET
  @Path("/relatedEntitySelect/{path:.*}")
  public Response relatedEntitySelect(
          @PathParam("path") final String path,
          @QueryParam("$expand") final String expand) {

    if (RELENTITY_SELECT_PATTERN.matcher(expand).matches()) {
      return xml.createResponse(null, null, Accept.JSON_FULLMETA);
    } else {
      return xml.createFaultResponse(Accept.JSON.toString(version), new Exception("Unexpected expand pattern"));
    }
  }

  @DELETE
  @Path("/monitor/{name}")
  public Response removeMonitor(@PathParam("name") final String name) {
    providedAsync.remove(name);
    return xml.createResponse(null, null, null, Status.NO_CONTENT);
  }

  @GET
  @Path("/monitor/{name}")
  public Response async(@PathParam("name") final String name) {
    try {
      if (!providedAsync.containsKey(name)) {
        throw new NotFoundException();
      }
      final InputStream res = IOUtils.toInputStream(providedAsync.get(name), Constants.ENCODING);
      providedAsync.remove(name);
      return xml.createMonitorResponse(res);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.JSON_FULLMETA.toString(), e);
    }
  }
  
  @PUT
  @Path("/People(1)/Parent")
  @SuppressWarnings("unused")
  public Response changeSingleValuedNavigationPropertyReference(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      final String content) {

    try {
        final Accept contentTypeValue = Accept.parse(contentType, version);
        assert contentTypeValue == Accept.JSON;
        
        ResWrap<Entity> entity = jsonDeserializer.toEntity(IOUtils.toInputStream(content, Constants.ENCODING));
        
        return Response.noContent().type(MediaType.APPLICATION_JSON).build();
      }catch (Exception e) {
        LOG.error("While update single property reference", e);
        return xml.createFaultResponse(accept, e);
      }
  }
  
  @POST
  @Path("/async/$batch")
  public Response async(
          @Context final UriInfo uriInfo,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
          final @Multipart MultipartBody attachment) {

    try {
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      bos.write("HTTP/1.1 200 Ok".getBytes());
      bos.write(CRLF);
      bos.write("OData-Version: 4.0".getBytes());
      bos.write(CRLF);
      bos.write(("Content-Type: " + ContentType.APPLICATION_OCTET_STREAM + ";boundary=" + BOUNDARY).getBytes());
      bos.write(CRLF);
      bos.write(CRLF);

      bos.write(("--" + BOUNDARY).getBytes());
      bos.write(CRLF);
      bos.write("Content-Type: application/http".getBytes());
      bos.write(CRLF);
      bos.write("Content-Transfer-Encoding: binary".getBytes());
      bos.write(CRLF);
      bos.write(CRLF);

      bos.write("HTTP/1.1 202 Accepted".getBytes());
      bos.write(CRLF);
      bos.write("Location: http://service-root/async-monitor".getBytes());
      bos.write(CRLF);
      bos.write("Retry-After: 10".getBytes());
      bos.write(CRLF);
      bos.write(CRLF);
      bos.write(("--" + BOUNDARY + "--").getBytes());
      bos.write(CRLF);

      final UUID uuid = UUID.randomUUID();
      providedAsync.put(uuid.toString(), bos.toString(Constants.ENCODING.toString()));

      bos.flush();
      bos.close();

      return xml.createAsyncResponse(
              uriInfo.getRequestUri().toASCIIString().replace("async/$batch", "") + "monitor/" + uuid.toString());
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.JSON.toString(), e);
    }
  }

  @GET
  @Path("/async/{name}")
  public Response async(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("name") final String name) {

    try {
      final Accept acceptType = Accept.parse(accept, version);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final String basePath = name + File.separatorChar;
      final StringBuilder path = new StringBuilder(basePath);

      path.append(metadata.getEntitySet(name).isSingleton()
              ? Constants.get(version, ConstantKey.ENTITY)
              : Constants.get(version, ConstantKey.FEED));

      final InputStream feed = FSManager.instance(version).readFile(path.toString(), acceptType);

      final StringBuilder builder = new StringBuilder();
      builder.append("HTTP/1.1 200 Ok").append(new String(CRLF));
      builder.append("Content-Type: ").append(accept).append(new String(CRLF)).append(new String(CRLF));
      builder.append(IOUtils.toString(feed));
      IOUtils.closeQuietly(feed);

      final UUID uuid = UUID.randomUUID();
      providedAsync.put(uuid.toString(), builder.toString());

      return xml.createAsyncResponse(
              uriInfo.getRequestUri().toASCIIString().replaceAll("async/" + name, "") + "monitor/" + uuid.toString());
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @Override
  protected void setInlineCount(final EntitySet entitySet, final String count) {
    if ("true".equals(count)) {
      entitySet.setCount(entitySet.getEntities().size());
    }
  }

  @Override
  public InputStream exploreMultipart(
          final List<Attachment> attachments, final String boundary, final boolean continueOnError)
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
          final ByteArrayOutputStream chbos = new ByteArrayOutputStream();
          String lastContebtID = null;
          try {
            final Map<String, String> references = new HashMap<String, String>();

            final String cboundary = "changeset_" + UUID.randomUUID().toString();
            chbos.write(("Content-Type: multipart/mixed;boundary=" + cboundary).getBytes());
            chbos.write(Constants.CRLF);
            chbos.write(Constants.CRLF);

            for (int j = 0; j < ((MimeMultipart) content).getCount(); j++) {
              final MimeBodyPart part = (MimeBodyPart) ((MimeMultipart) content).getBodyPart(j);
              lastContebtID = part.getContentID();
              addChangesetItemIntro(chbos, lastContebtID, cboundary);

              res = bodyPartRequest(new MimeBodyPart(part.getInputStream()), references);
              if (!continueOnError && (res == null || res.getStatus() >= 400)) {
                throw new Exception("Failure processing changeset");
              }

              addSingleBatchResponse(res, lastContebtID, chbos);
              references.put("$" + lastContebtID, res.getHeaderString("Location"));
            }

            chbos.write(("--" + cboundary + "--").getBytes());
            chbos.write(Constants.CRLF);

            bos.write(chbos.toByteArray());
            IOUtils.closeQuietly(chbos);
          } catch (Exception e) {
            LOG.warn("While processing changeset", e);
            IOUtils.closeQuietly(chbos);

            addItemIntro(bos, lastContebtID);

            if (res == null || res.getStatus() < 400) {
              addErrorBatchResponse(e, "1", bos);
            } else {
              addSingleBatchResponse(res, lastContebtID, bos);
            }

            goon = continueOnError;
          }
        } else {
          addItemIntro(bos);

          res = bodyPartRequest(new MimeBodyPart(obj.getDataHandler().getInputStream()));

          if (res.getStatus() >= 400) {
            goon = continueOnError;
            throw new Exception("Failure processing batch item");
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
  @Path("/People/{type:.*}")
  public Response getPeople(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("type") final String type,
          @QueryParam("$top") @DefaultValue(StringUtils.EMPTY) final String top,
          @QueryParam("$skip") @DefaultValue(StringUtils.EMPTY) final String skip,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          @QueryParam("$inlinecount") @DefaultValue(StringUtils.EMPTY) final String count,
          @QueryParam("$filter") @DefaultValue(StringUtils.EMPTY) final String filter,
          @QueryParam("$search") @DefaultValue(StringUtils.EMPTY) final String search,
          @QueryParam("$orderby") @DefaultValue(StringUtils.EMPTY) final String orderby,
          @QueryParam("$skiptoken") @DefaultValue(StringUtils.EMPTY) final String skiptoken) {

    return StringUtils.isBlank(filter) && StringUtils.isBlank(search)
            ? NumberUtils.isNumber(type)
            ? super.getEntityInternal(
                    uriInfo.getRequestUri().toASCIIString(), accept, "People", type, format, null, null)
            : super.getEntitySet(accept, "People", type)
            : super.getEntitySet(uriInfo, accept, "People", top, skip, format, count, filter, orderby, skiptoken);
  }

  @GET
  @Path("/Boss")
  public Response getSingletonBoss(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    return getEntityInternal(
            uriInfo.getRequestUri().toASCIIString(), accept, "Boss", StringUtils.EMPTY, format, null, null);
  }

  @GET
  @Path("/Company")
  public Response getSingletonCompany(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    return getEntityInternal(
            uriInfo.getRequestUri().toASCIIString(), accept, "Company", StringUtils.EMPTY, format, null, null);
  }

  @PATCH
  @Path("/Company")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  public Response patchSingletonCompany(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
          @HeaderParam("If-Match") @DefaultValue(StringUtils.EMPTY) final String ifMatch,
          final String changes) {

    return super.patchEntity(uriInfo, accept, contentType, prefer, ifMatch, "Company", StringUtils.EMPTY, changes);
  }

  @GET
  @Path("/Customers")
  public Response getCustomers(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
          @QueryParam("$deltatoken") @DefaultValue(StringUtils.EMPTY) final String deltatoken) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final InputStream output;
      if (StringUtils.isBlank(deltatoken)) {
        final InputStream input = (InputStream) getEntitySet(
                uriInfo, accept, "Customers", null, null, format, null, null, null, null).getEntity();
        final EntitySet entitySet = xml.readEntitySet(acceptType, input);

        boolean trackChanges = prefer.contains("odata.track-changes");
        if (trackChanges) {
          entitySet.setDeltaLink(URI.create("Customers?$deltatoken=8015"));
        }

        output = xml.writeEntitySet(acceptType, new ResWrap<EntitySet>(
                URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX) + "Customers"),
                null,
                entitySet));
      } else {
        output = FSManager.instance(version).readFile("delta", acceptType);
      }

      final Response response = xml.createResponse(
              null,
              output,
              null,
              acceptType);
      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }
      return response;
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/Company/Microsoft.Test.OData.Services.ODataWCFService.GetEmployeesCount{paren:[\\(\\)]*}")
  public Response functionGetEmployeesCount(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final Property property = new PropertyImpl();
      property.setType("Edm.Int32");
      property.setValue(ValueType.PRIMITIVE, 2);
      final ResWrap<Property> container = new ResWrap<Property>(
              URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX) + property.getType()), null,
              property);

      return xml.createResponse(
              null,
              xml.writeProperty(acceptType, container),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Company/Microsoft.Test.OData.Services.ODataWCFService.IncreaseRevenue{paren:[\\(\\)]*}")
  public Response actionIncreaseRevenue(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String param) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final Accept contentTypeValue = Accept.parse(contentType, version);
      final Entity entry = xml.readEntity(contentTypeValue, IOUtils.toInputStream(param, Constants.ENCODING));

      return xml.createResponse(
              null,
              xml.writeProperty(acceptType, entry.getProperty("IncreaseValue")),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/Products({entityId})/Microsoft.Test.OData.Services.ODataWCFService.GetProductDetails({param:.*})")
  public Response functionGetProductDetails(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("entityId") final String entityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final EntityImpl entry = new EntityImpl();
      entry.setType("Microsoft.Test.OData.Services.ODataWCFService.ProductDetail");
      final Property productId = new PropertyImpl();
      productId.setName("ProductID");
      productId.setType("Edm.Int32");
      productId.setValue(ValueType.PRIMITIVE, Integer.valueOf(entityId));
      entry.getProperties().add(productId);
      final Property productDetailId = new PropertyImpl();
      productDetailId.setName("ProductDetailID");
      productDetailId.setType("Edm.Int32");
      productDetailId.setValue(ValueType.PRIMITIVE, 2);
      entry.getProperties().add(productDetailId);

      final Link link = new LinkImpl();
      link.setRel("edit");
      link.setHref(URI.create(
              Constants.get(version, ConstantKey.DEFAULT_SERVICE_URL)
              + "ProductDetails(ProductID=6,ProductDetailID=1)").toASCIIString());
      entry.setEditLink(link);

      final EntitySetImpl feed = new EntitySetImpl();
      feed.getEntities().add(entry);

      final ResWrap<EntitySet> container = new ResWrap<EntitySet>(
              URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX) + "ProductDetail"), null,
              feed);

      return xml.createResponse(
              null,
              xml.writeEntitySet(acceptType, container),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Products({entityId})/Microsoft.Test.OData.Services.ODataWCFService.AddAccessRight{paren:[\\(\\)]*}")
  public Response actionAddAccessRight(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String param) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final Accept contentTypeValue = Accept.parse(contentType, version);
      final Entity entry = xml.readEntity(contentTypeValue, IOUtils.toInputStream(param, Constants.ENCODING));

      assert 1 == entry.getProperties().size();
      assert entry.getProperty("accessRight") != null;

      final Property property = entry.getProperty("accessRight");
      property.setType("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel");

      final ResWrap<Property> result = new ResWrap<Property>(
              URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX) + property.getType()),
              null, property);

      return xml.createResponse(
              null,
              xml.writeProperty(acceptType, result),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Customers({personId})/Microsoft.Test.OData.Services.ODataWCFService.ResetAddress{paren:[\\(\\)]*}")
  public Response actionResetAddress(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("personId") final String personId,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String param) {

    try {
      final Accept contentTypeValue = Accept.parse(contentType, version);
      final Entity entry = xml.readEntity(contentTypeValue, IOUtils.toInputStream(param, Constants.ENCODING));

      assert 2 == entry.getProperties().size();
      assert entry.getProperty("addresses") != null;
      assert entry.getProperty("index") != null;

      return getEntityInternal(
              uriInfo.getRequestUri().toASCIIString(), accept, "Customers", personId, format, null, null);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/ProductDetails(ProductID={productId},ProductDetailID={productDetailId})"
          + "/Microsoft.Test.OData.Services.ODataWCFService.GetRelatedProduct{paren:[\\(\\)]*}")
  public Response functionGetRelatedProduct(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("productId") final String productId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    return getEntityInternal(
            uriInfo.getRequestUri().toASCIIString(), accept, "Products", productId, format, null, null);
  }

  @POST
  @Path("/Accounts({entityId})/Microsoft.Test.OData.Services.ODataWCFService.RefreshDefaultPI{paren:[\\(\\)]*}")
  public Response actionRefreshDefaultPI(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @PathParam("entityId") final String entityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String param) {

    try {
      final Accept contentTypeValue = Accept.parse(contentType, version);
      final Entity entry = xml.readEntity(contentTypeValue, IOUtils.toInputStream(param, Constants.ENCODING));

      assert 1 == entry.getProperties().size();
      assert entry.getProperty("newDate") != null;

      return functionGetDefaultPI(accept, entityId, format);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/Accounts({entityId})/Microsoft.Test.OData.Services.ODataWCFService.GetDefaultPI{paren:[\\(\\)]*}")
  public Response functionGetDefaultPI(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("entityId") final String entityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    return getContainedEntity(accept, entityId, "MyPaymentInstruments", entityId + "901", format);
  }

  @GET
  @Path("/Accounts({entityId})/Microsoft.Test.OData.Services.ODataWCFService.GetAccountInfo{paren:[\\(\\)]*}")
  public Response functionGetAccountInfo(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("entityId") final String entityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    return getPath(accept, "Accounts", entityId, "AccountInfo", format);
  }

  @GET
  @Path("/Accounts({entityId})/MyGiftCard/Microsoft.Test.OData.Services.ODataWCFService.GetActualAmount({param:.*})")
  public Response functionGetActualAmount(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("entityId") final String entityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final Property property = new PropertyImpl();
      property.setType("Edm.Double");
      property.setValue(ValueType.PRIMITIVE, 41.79);

      final ResWrap<Property> container = new ResWrap<Property>((URI) null, null, property);

      return xml.createResponse(
              null,
              xml.writeProperty(acceptType, container),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  /**
   * Retrieve entity reference sample.
   *
   * @param accept Accept header.
   * @param path path.
   * @param format format query option.
   * @return entity reference or feed of entity reference.
   */
  @GET
  @Path("/{path:.*}/$ref")
  public Response getEntityReference(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("path") final String path,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    try {
      final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

      if (utils.getKey() == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final String filename = Base64.encodeBase64String(path.getBytes("UTF-8"));

      return utils.getValue().createResponse(
              FSManager.instance(version).readFile(Constants.get(version, ConstantKey.REF)
                      + File.separatorChar + filename, utils.getKey()),
              null,
              utils.getKey());
    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/People")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM})
  public Response postPeople(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
          final String entity) {

    if ("{\"@odata.type\":\"#Microsoft.Test.OData.Services.ODataWCFService.Person\"}".equals(entity)) {
      return xml.createFaultResponse(accept, new BadRequestException());
    }

    return super.postNewEntity(uriInfo, accept, contentType, prefer, "People", entity);
  }

  @Override
  public Response patchEntity(
          final UriInfo uriInfo,
          final String accept,
          final String contentType,
          final String prefer,
          final String ifMatch,
          final String entitySetName,
          final String entityId,
          final String changes) {

    final Response response =
            getEntityInternal(uriInfo.getRequestUri().toASCIIString(),
                    accept, entitySetName, entityId, accept, StringUtils.EMPTY, StringUtils.EMPTY);
    return response.getStatus() >= 400
            ? super.postNewEntity(uriInfo, accept, contentType, prefer, entitySetName, changes)
            : super.patchEntity(uriInfo, accept, contentType, prefer, ifMatch, entitySetName, entityId, changes);
  }

  @Override
  public Response replaceEntity(
          final UriInfo uriInfo,
          final String accept,
          final String contentType,
          final String prefer,
          final String entitySetName,
          final String entityId,
          final String entity) {

    try {
      getEntityInternal(uriInfo.getRequestUri().toASCIIString(),
              accept, entitySetName, entityId, accept, StringUtils.EMPTY, StringUtils.EMPTY);
      return super.replaceEntity(uriInfo, accept, contentType, prefer, entitySetName, entityId, entity);
    } catch (NotFoundException e) {
      return postNewEntity(uriInfo, accept, contentType, prefer, entitySetName, entityId);
    }
  }

  private StringBuilder containedPath(final String entityId, final String containedEntitySetName) {
    return new StringBuilder("Accounts").append(File.separatorChar).
            append(entityId).append(File.separatorChar).
            append("links").append(File.separatorChar).
            append(containedEntitySetName);
  }

  @GET
  @Path("/Accounts({entityId})/{containedEntitySetName}({containedEntityId})")
  public Response getContainedEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("entityId") final String entityId,
          @PathParam("containedEntitySetName") final String containedEntitySetName,
          @PathParam("containedEntityId") final String containedEntityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final StringBuilder containedPath = containedPath(entityId, containedEntitySetName);
      if (StringUtils.isNotBlank(containedEntityId)) {
        containedPath.append('(').append(containedEntityId).append(')');
      }
      final InputStream entry = FSManager.instance(version).readFile(containedPath.toString(), Accept.ATOM);

      final ResWrap<Entity> container = atomDeserializer.toEntity(entry);

      return xml.createResponse(
              null,
              xml.writeEntity(acceptType, container),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Accounts({entityId})/{containedEntitySetName:.*}")
  public Response postContainedEntity(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @PathParam("entityId") final String entityId,
          @PathParam("containedEntitySetName") final String containedEntitySetName,
          final String entity) {

    try {
      final Accept acceptType = Accept.parse(accept, version);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final AbstractUtilities utils = getUtilities(acceptType);

      // 1. parse the entry (from Atom or JSON)
      final ResWrap<Entity> entryContainer;
      final Entity entry;
      final Accept contentTypeValue = Accept.parse(contentType, version);
      if (Accept.ATOM == contentTypeValue) {
        entryContainer = atomDeserializer.toEntity(IOUtils.toInputStream(entity, Constants.ENCODING));
        entry = entryContainer.getPayload();
      } else {
        final ResWrap<Entity> jcontainer = jsonDeserializer.toEntity(
                IOUtils.toInputStream(entity, Constants.ENCODING));
        entry = jcontainer.getPayload();

        entryContainer = new ResWrap<Entity>(
                jcontainer.getContextURL(),
                jcontainer.getMetadataETag(),
                entry);
      }

      final EdmTypeInfo contained = new EdmTypeInfo.Builder().setTypeExpression(metadata.
              getNavigationProperties("Accounts").get(containedEntitySetName).getType()).build();
      final String entityKey = getUtilities(contentTypeValue).
              getDefaultEntryKey(contained.getFullQualifiedName().getName(), entry);

      // 2. Store the new entity
      final String atomEntryRelativePath = containedPath(entityId, containedEntitySetName).
              append('(').append(entityKey).append(')').toString();
      FSManager.instance(version).putInMemory(
              utils.writeEntity(Accept.ATOM, entryContainer),
              FSManager.instance(version).getAbsolutePath(atomEntryRelativePath, Accept.ATOM));

      // 3. Update the contained entity set
      final String atomFeedRelativePath = containedPath(entityId, containedEntitySetName).toString();
      final InputStream feedIS = FSManager.instance(version).readFile(atomFeedRelativePath, Accept.ATOM);
      final ResWrap<EntitySet> feedContainer = atomDeserializer.toEntitySet(feedIS);
      feedContainer.getPayload().getEntities().add(entry);

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);
      atomSerializer.write(writer, feedContainer);
      writer.flush();
      writer.close();

      FSManager.instance(version).putInMemory(
              new ByteArrayInputStream(content.toByteArray()),
              FSManager.instance(version).getAbsolutePath(atomFeedRelativePath, Accept.ATOM));

      // Finally, return
      return utils.createResponse(
              uriInfo.getRequestUri().toASCIIString() + "(" + entityKey + ")",
              utils.writeEntity(acceptType, entryContainer),
              null,
              acceptType,
              Response.Status.CREATED);
    } catch (Exception e) {
      LOG.error("While creating new contained entity", e);
      return xml.createFaultResponse(accept, e);
    }
  }

  @PATCH
  @Path("/{entitySetName}({entityId})/{containedEntitySetName}({containedEntityId})")
  public Response patchContainedEntity(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @PathParam("entitySetName") final String entitySetName,
          @PathParam("entityId") final String entityId,
          @PathParam("containedEntitySetName") final String containedEntitySetName,
          @PathParam("containedEntityId") final String containedEntityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String changes) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final Accept contentTypeValue;
      if (StringUtils.isBlank(contentType)) {
        throw new IllegalArgumentException();
      }
      contentTypeValue = Accept.parse(contentType, version);

      final LinkInfo links = xml.readLinks(
              entitySetName, entityId, containedEntitySetName + "(" + containedEntityId + ")", Accept.ATOM);

      ResWrap<Entity> container = atomDeserializer.toEntity(links.getLinks());
      final Entity original = container.getPayload();

      final Entity entryChanges;
      if (Accept.ATOM == contentTypeValue) {
        container = atomDeserializer.toEntity(IOUtils.toInputStream(changes, Constants.ENCODING));
        entryChanges = container.getPayload();
      } else {
        final String entityType = metadata.getEntitySet(entitySetName).getType();
        final String containedType = metadata.getEntityOrComplexType(entityType).
                getNavigationProperty(containedEntitySetName).getType();
        final EdmTypeInfo typeInfo = new EdmTypeInfo.Builder().setTypeExpression(containedType).build();

        final ResWrap<Entity> jsonContainer = jsonDeserializer.toEntity(
                IOUtils.toInputStream(changes, Constants.ENCODING));
        jsonContainer.getPayload().setType(typeInfo.getFullQualifiedName().toString());
        entryChanges = jsonContainer.getPayload();
      }

      for (Property property : entryChanges.getProperties()) {
        final Property old = original.getProperty(property.getName());
        if (old != null) {
          original.getProperties().remove(old);
        }
        original.getProperties().add(property);
      }

      FSManager.instance(version).putInMemory(new ResWrap<Entity>((URI) null, null, original),
              xml.getLinksBasePath(entitySetName, entityId) + containedEntitySetName + "(" + containedEntityId + ")");

      return xml.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @DELETE
  @Path("/Accounts({entityId})/{containedEntitySetName}({containedEntityId})")
  public Response removeContainedEntity(
          @PathParam("entityId") final String entityId,
          @PathParam("containedEntitySetName") final String containedEntitySetName,
          @PathParam("containedEntityId") final String containedEntityId) {

    try {
      // 1. Fetch the contained entity to be removed
      final InputStream entry = FSManager.instance(version).
              readFile(containedPath(entityId, containedEntitySetName).
                      append('(').append(containedEntityId).append(')').toString(), Accept.ATOM);
      final ResWrap<Entity> container = atomDeserializer.toEntity(entry);

      // 2. Remove the contained entity
      final String atomEntryRelativePath = containedPath(entityId, containedEntitySetName).
              append('(').append(containedEntityId).append(')').toString();
      FSManager.instance(version).deleteFile(atomEntryRelativePath);

      // 3. Update the contained entity set
      final String atomFeedRelativePath = containedPath(entityId, containedEntitySetName).toString();
      final InputStream feedIS = FSManager.instance(version).readFile(atomFeedRelativePath, Accept.ATOM);
      final ResWrap<EntitySet> feedContainer = atomDeserializer.toEntitySet(feedIS);
      feedContainer.getPayload().getEntities().remove(container.getPayload());

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);
      atomSerializer.write(writer, feedContainer);
      writer.flush();
      writer.close();

      FSManager.instance(version).putInMemory(
              new ByteArrayInputStream(content.toByteArray()),
              FSManager.instance(version).getAbsolutePath(atomFeedRelativePath, Accept.ATOM));

      return xml.createResponse(null, null, null, null, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.XML.toString(version), e);
    }
  }

  @GET
  @Path("/Accounts({entityId})/{containedEntitySetName:.*}")
  public Response getContainedEntitySet(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("entityId") final String entityId,
          @PathParam("containedEntitySetName") String containedEntitySetName,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    if ("MyGiftCard".equals(containedEntitySetName)) {
      return getContainedEntity(accept, entityId, containedEntitySetName, null, format);
    }

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      String derivedType = null;
      if (containedEntitySetName.contains("/")) {
        final String[] parts = containedEntitySetName.split("/");
        containedEntitySetName = parts[0];
        derivedType = parts[1];
      }

      final InputStream feed = FSManager.instance(version).
              readFile(containedPath(entityId, containedEntitySetName).toString(), Accept.ATOM);

      final ResWrap<EntitySet> container = atomDeserializer.toEntitySet(feed);

      if (derivedType != null) {
        final List<Entity> nonMatching = new ArrayList<Entity>();
        for (Entity entity : container.getPayload().getEntities()) {
          if (!derivedType.equals(entity.getType())) {
            nonMatching.add(entity);
          }
        }
        container.getPayload().getEntities().removeAll(nonMatching);
      }

      return xml.createResponse(
              null,
              xml.writeEntitySet(acceptType, container),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/GetDefaultColor()")
  public Response functionGetDefaultColor(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final PropertyImpl property = new PropertyImpl();
      property.setType("Microsoft.Test.OData.Services.ODataWCFService.Color");
      property.setValue(ValueType.ENUM, "Red");
      final ResWrap<Property> container = new ResWrap<Property>(
              URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX) + property.getType()), null,
              property);

      return xml.createResponse(
              null,
              xml.writeProperty(acceptType, container),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/GetPerson2({param:.*})")
  public Response functionGetPerson2(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    return getEntityInternal(
            uriInfo.getRequestUri().toASCIIString(), accept, "Customers", "1", format, null, null);
  }

  @GET
  @Path("/GetPerson2({param:.*})/Emails")
  public Response functionGetPerson2Emails(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    return getPath(accept, "Customers", "1", "Emails", format);
  }

  @GET
  @Path("/GetPerson2({param:.*})/HomeAddress")
  public Response functionGetPerson2HomeAddress(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    return getPath(accept, "Customers", "1", "HomeAddress", format);
  }

  @GET
  @Path("/GetPerson2({param:.*})/Parent")
  public Response functionGetPerson2Parent(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    return getEntityInternal(
            uriInfo.getRequestUri().toASCIIString(), accept, "Customers", "2", format, null, null);
  }

  @GET
  @Path("/GetPerson({param:.*})")
  public Response functionGetPerson(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    return getEntityInternal(
            uriInfo.getRequestUri().toASCIIString(), accept, "Customers", "1", format, null, null);
  }

  @GET
  @Path("/GetAllProducts()")
  public Response functionGetAllProducts(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    return getEntitySet(uriInfo, accept, "Products", null, null, format, null, null, null, null);
  }

  @GET
  @Path("/GetProductsByAccessLevel({param:.*})")
  public Response functionGetProductsByAccessLevel(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final PropertyImpl property = new PropertyImpl();
      property.setType("Collection(String)");
      final List<String> value = Arrays.asList("Cheetos", "Mushrooms", "Apple", "Car", "Computer");
      property.setValue(ValueType.COLLECTION_PRIMITIVE, value);
      final ResWrap<Property> container = new ResWrap<Property>(
              URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX) + property.getType()), null,
              property);

      return xml.createResponse(
              null,
              xml.writeProperty(acceptType, container),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/GetBossEmails({param:.*})")
  public Response functionGetBossEmails(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final PropertyImpl property = new PropertyImpl();
      property.setType("Collection(Edm.String)");
      property.setValue(ValueType.COLLECTION_PRIMITIVE,
              Arrays.asList("first@olingo.apache.org", "second@olingo.apache.org"));
      final ResWrap<Property> container = new ResWrap<Property>(
              URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX) + property.getType()), null,
              property);

      return xml.createResponse(null, xml.writeProperty(acceptType, container), null, acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Discount()")
  public Response actionDiscount(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String param) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final Accept contentTypeValue = Accept.parse(contentType, version);
      Property property;
      if (contentTypeValue == Accept.ATOM) {
        final ResWrap<Property> paramContainer = atomDeserializer.toProperty(
                IOUtils.toInputStream(param, Constants.ENCODING));
        property = paramContainer.getPayload();
      } else {
        final ResWrap<Property> paramContainer = jsonDeserializer.toProperty(
                IOUtils.toInputStream(param, Constants.ENCODING));
        property = paramContainer.getPayload();
      }

      assert property.isComplex();
      assert 1 == property.asComplex().getValue().size();
      assert "Edm.Int32".equals(property.asComplex().getValue().get(0).getType());
      assert property.asComplex().getValue().get(0).isPrimitive();
      assert "percentage".equals(property.asComplex().getValue().get(0).getName());

      return xml.createResponse(null, null, null, acceptType, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/GetAllProducts()/Discount")
  public Response actionBoundDiscount() {
    try {
      final String basePath = "Products" + File.separatorChar + "feed";

      final InputStream feed = FSManager.instance(version).readFile(basePath, Accept.JSON_FULLMETA);
      return xml.createResponse(null, feed, Commons.getETag(basePath, version), Accept.JSON_FULLMETA);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.JSON_FULLMETA.toString(version), e);
    }
  }

  @POST
  @Path("/ResetBossAddress()")
  public Response actionResetBossAddress(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String param) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final Accept contentTypeValue = Accept.parse(contentType, version);
      final Entity entity = xml.readEntity(contentTypeValue, IOUtils.toInputStream(param, Constants.ENCODING));

      assert "Microsoft.Test.OData.Services.ODataWCFService.Address".equals(entity.getType());
      assert entity.getProperty("address").isComplex();

      final ResWrap<Property> result = new ResWrap<Property>(
              URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX)
                      + "Microsoft.Test.OData.Services.ODataWCFService.Address"),
              null,
              entity.getProperty("address"));

      return xml.createResponse(
              null,
              xml.writeProperty(acceptType, result),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/ResetBossEmail()")
  public Response actionResetBossEmail(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String param) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final Accept contentTypeValue = Accept.parse(contentType, version);
      final Entity entry = xml.readEntity(contentTypeValue, IOUtils.toInputStream(param, Constants.ENCODING));

      assert 1 == entry.getProperties().size();
      assert "Collection(Edm.String)".equals(entry.getProperty("emails").getType());
      assert entry.getProperty("emails").isCollection();

      return xml.createResponse(
              null,
              xml.writeProperty(acceptType, entry.getProperty("emails")),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Products({productId})/Categories/$ref")
  public Response createLinked(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String entity) {

    return xml.createResponse(null, null, null, Status.NO_CONTENT);
  }

  @POST
  @Path("/Customers(1)/Orders/$ref")
  public Response linkOrderViaRef(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String entity) {

    return xml.createResponse(null, null, null, Status.NO_CONTENT);
  }

  @DELETE
  @Path("/Products({productId})/Categories({categoryId})/$ref")
  public Response deleteLinked(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String entity) {

    return xml.createResponse(null, null, null, Status.NO_CONTENT);
  }

  @GET
  @Path("/Company/VipCustomer")
  public Response getVipCustomer(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          @QueryParam("$expand") @DefaultValue(StringUtils.EMPTY) final String expand,
          @QueryParam("$select") @DefaultValue(StringUtils.EMPTY) final String select) {

    return super.getEntityInternal(
            uriInfo.getRequestUri().toASCIIString(), accept, "VipCustomer", "1", format, expand, select);
  }
}
