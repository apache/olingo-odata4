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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Header;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.BadRequestException;
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
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.serialization.ODataDeserializer;
import org.apache.olingo.client.api.serialization.ODataSerializer;
import org.apache.olingo.client.core.serialization.AtomSerializer;
import org.apache.olingo.client.core.serialization.JsonDeserializer;
import org.apache.olingo.client.core.serialization.JsonSerializer;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.fit.metadata.EntityType;
import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.metadata.NavigationProperty;
import org.apache.olingo.fit.methods.PATCH;
import org.apache.olingo.fit.rest.ResolvingReferencesInterceptor;
import org.apache.olingo.fit.rest.XHTTPMethodInterceptor;
import org.apache.olingo.fit.serializer.FITAtomDeserializer;
import org.apache.olingo.fit.utils.AbstractUtilities;
import org.apache.olingo.fit.utils.Accept;
import org.apache.olingo.fit.utils.Commons;
import org.apache.olingo.fit.utils.ConstantKey;
import org.apache.olingo.fit.utils.Constants;
import org.apache.olingo.fit.utils.FSManager;
import org.apache.olingo.fit.utils.JSONUtilities;
import org.apache.olingo.fit.utils.LinkInfo;
import org.apache.olingo.fit.utils.XMLUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Path("/V40/Static.svc")
@InInterceptors(classes = { XHTTPMethodInterceptor.class, ResolvingReferencesInterceptor.class })
public class Services {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(Services.class);

  private static final Pattern REQUEST_PATTERN = Pattern.compile("(.*) (http://.*) HTTP/.*");
  private static final Pattern BATCH_REQUEST_REF_PATTERN = Pattern.compile("(.*) ([$]\\d+)(.*) HTTP/.*");
  private static final Pattern REF_PATTERN = Pattern.compile("([$]\\d+)");
  private static final Pattern RELENTITY_SELECT_PATTERN = Pattern.compile("^.*\\(\\$select=.*\\)$");
  private static final Pattern CROSSJOIN_PATTERN = Pattern.compile(
      "^\\$crossjoin\\(.*\\)\\?\\$filter=\\([a-zA-Z/]+ eq [a-zA-Z/]+\\)$");
  protected static final String BOUNDARY = "batch_243234_25424_ef_892u748";
  protected static final String MULTIPART_MIXED = "multipart/mixed";
  protected static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

  private final Map<String, String> providedAsync = new HashMap<String, String>();

  protected final ODataDeserializer atomDeserializer = new FITAtomDeserializer();
  protected final ODataDeserializer jsonDeserializer = new JsonDeserializer(true);
  protected final ODataSerializer atomSerializer = new AtomSerializer(true);
  protected final ODataSerializer jsonSerializer = new JsonSerializer(true, ContentType.JSON_FULL_METADATA);

  protected final Metadata metadata;
  protected final XMLUtilities xml;
  protected final JSONUtilities json;

  public Services() throws IOException {
    this(Commons.getMetadata());
  }

  protected Services(final Metadata metadata) throws IOException {
    this.metadata = metadata;
    xml = new XMLUtilities(metadata);
    json = new JSONUtilities(metadata);
  }

  /**
   * Provide sample services.
   *
   * @param accept Accept header.
   * @return OData services.
   */
  @GET
  public Response getServices(@HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept) {
    try {
      final Accept acceptType = Accept.parse(accept);

      if (acceptType == Accept.ATOM) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      return xml.createResponse(
          null,
          FSManager.instance().readFile(Constants.get(ConstantKey.SERVICES), acceptType),
          null, acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  /**
   * Provide sample getMetadata().
   *
   * @return getMetadata().
   */
  @GET
  @Path("/$metadata")
  @Produces(MediaType.APPLICATION_XML)
  public Response getMetadata() {
    return getMetadata(Constants.get(ConstantKey.METADATA));
  }

  protected Response getMetadata(final String filename) {
    try {
      return xml.createResponse(null, FSManager.instance().readRes(filename, Accept.XML), null, Accept.XML);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.XML.toString(), e);
    }
  }

  @GET
  @Path("/redirect/{name}({id})")
  public Response conformanceRedirect(@Context final UriInfo uriInfo) {
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
        final InputStream feed = FSManager.instance().readFile("crossjoin", Accept.JSON);

        return xml.createResponse(feed, null, Accept.JSON_FULLMETA);
      } else {
        throw new IOException("Unexpected crossjoin pattern");
      }
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.JSON.toString(), e);
    }
  }

  @GET
  @Path("/relatedEntitySelect/{path:.*}")
  public Response relatedEntitySelect(@QueryParam("$expand") final String expand) {

    if (RELENTITY_SELECT_PATTERN.matcher(expand).matches()) {
      return xml.createResponse(null, null, Accept.JSON_FULLMETA);
    } else {
      return xml.createFaultResponse(Accept.JSON.toString(), new Exception("Unexpected expand pattern"));
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
  public Response changeSingleValuedNavigationPropertyReference(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      final String content) {

    try {
      final Accept contentTypeValue = Accept.parse(contentType);
      assert contentTypeValue == Accept.JSON;

      jsonDeserializer.toEntity(IOUtils.toInputStream(content, Constants.ENCODING));

      return Response.noContent().type(MediaType.APPLICATION_JSON).build();
    } catch (Exception e) {
      LOG.error("While update single property reference", e);
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/$batch")
  @Consumes(MULTIPART_MIXED)
  @Produces(APPLICATION_OCTET_STREAM + ";boundary=" + BOUNDARY)
  public Response batch(
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      final @Multipart MultipartBody attachment) {
    try {
      final boolean continueOnError = prefer.contains("odata.continue-on-error");
      return xml.createBatchResponse(
          exploreMultipart(attachment.getAllAttachments(), BOUNDARY, continueOnError));
    } catch (IOException e) {
      return xml.createFaultResponse(Accept.XML.toString(), e);
    }
  }

  // ----------------------------------------------
  // just for non nullable property test into PropertyTestITCase
  // ----------------------------------------------
  @PATCH
  @Path("/Driver('2')")
  public Response patchDriver() {
    return xml.createFaultResponse(Accept.JSON_FULLMETA.toString(), new Exception("Non nullable properties"));
  }

  @GET
  @Path("/StoredPIs(1000)")
  public Response getStoredPI(@Context final UriInfo uriInfo) {
    final Entity entity = new Entity();
    entity.setType("Microsoft.Test.OData.Services.ODataWCFService.StoredPI");
    final Property id = new Property();
    id.setType("Edm.Int32");
    id.setName("StoredPIID");
    id.setValue(ValueType.PRIMITIVE, 1000);
    entity.getProperties().add(id);
    final Link edit = new Link();
    edit.setHref(uriInfo.getRequestUri().toASCIIString());
    edit.setRel("edit");
    edit.setTitle("StoredPI");
    entity.setEditLink(edit);

    final ByteArrayOutputStream content = new ByteArrayOutputStream();
    final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);
    try {
      jsonSerializer.write(writer, new ResWrap<Entity>((URI) null, null, entity));
      return xml.createResponse(new ByteArrayInputStream(content.toByteArray()), null, Accept.JSON_FULLMETA);
    } catch (Exception e) {
      LOG.error("While creating StoredPI", e);
      return xml.createFaultResponse(Accept.JSON_FULLMETA.toString(), e);
    }
  }

  @PATCH
  @Path("/StoredPIs(1000)")
  public Response patchStoredPI() {
    // just for non nullable property test into PropertyTestITCase
    return xml.createFaultResponse(Accept.JSON_FULLMETA.toString(), new Exception("Non nullable properties"));
  }

  @POST
  @Path("/async/$batch")
  public Response async(@Context final UriInfo uriInfo) {

    try {
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      bos.write("HTTP/1.1 200 Ok".getBytes());
      bos.write(Constants.CRLF);
      bos.write("OData-Version: 4.0".getBytes());
      bos.write(Constants.CRLF);
      bos.write(("Content-Type: " + ContentType.APPLICATION_OCTET_STREAM + ";boundary=" + BOUNDARY).getBytes());
      bos.write(Constants.CRLF);
      bos.write(Constants.CRLF);

      bos.write(("--" + BOUNDARY).getBytes());
      bos.write(Constants.CRLF);
      bos.write("Content-Type: application/http".getBytes());
      bos.write(Constants.CRLF);
      bos.write("Content-Transfer-Encoding: binary".getBytes());
      bos.write(Constants.CRLF);
      bos.write(Constants.CRLF);

      bos.write("HTTP/1.1 202 Accepted".getBytes());
      bos.write(Constants.CRLF);
      bos.write("Location: http://service-root/async-monitor".getBytes());
      bos.write(Constants.CRLF);
      bos.write("Retry-After: 10".getBytes());
      bos.write(Constants.CRLF);
      bos.write(Constants.CRLF);
      bos.write(("--" + BOUNDARY + "--").getBytes());
      bos.write(Constants.CRLF);

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
      final Accept acceptType = Accept.parse(accept);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final String basePath = name + File.separatorChar;
      final StringBuilder path = new StringBuilder(basePath);

      path.append(metadata.getEntitySet(name).isSingleton()
          ? Constants.get(ConstantKey.ENTITY)
          : Constants.get(ConstantKey.FEED));

      final InputStream feed = FSManager.instance().readFile(path.toString(), acceptType);

      final StringBuilder builder = new StringBuilder();
      builder.append("HTTP/1.1 200 Ok").append(new String(Constants.CRLF));
      builder.append("Content-Type: ").append(accept)
          .append(new String(Constants.CRLF))
          .append(new String(Constants.CRLF));
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

  private void setInlineCount(final EntityCollection entitySet, final String count) {
    if ("true".equals(count)) {
      entitySet.setCount(entitySet.getEntities().size());
    }
  }

  private Response bodyPartRequest(final MimeBodyPart body, final Map<String, String> references) throws Exception {
    @SuppressWarnings("unchecked")
    final Enumeration<Header> en = body.getAllHeaders();

    Header header = en.nextElement();
    final String request =
        header.getName() + (StringUtils.isNotBlank(header.getValue()) ? ":" + header.getValue() : "");

    final Matcher matcher = REQUEST_PATTERN.matcher(request);
    final Matcher matcherRef = BATCH_REQUEST_REF_PATTERN.matcher(request);

    final MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();

    while (en.hasMoreElements()) {
      header = en.nextElement();
      headers.putSingle(header.getName(), header.getValue());
    }

    final Response res;
    final String url;
    final String method;

    if (matcher.find()) {
      url = matcher.group(2);
      method = matcher.group(1);
    } else if (matcherRef.find()) {
      url = references.get(matcherRef.group(2)) + matcherRef.group(3);
      method = matcherRef.group(1);
    } else {
      url = null;
      method = null;
    }

    if (url == null) {
      res = null;
    } else {
      final WebClient client = WebClient.create(url, "odatajclient", "odatajclient", null);
      client.headers(headers);

      if ("DELETE".equals(method)) {
        res = client.delete();
      } else {
        final InputStream is = body.getDataHandler().getInputStream();
        String content = IOUtils.toString(is);
        IOUtils.closeQuietly(is);

        final Matcher refs = REF_PATTERN.matcher(content);

        while (refs.find()) {
          content = content.replace(refs.group(1), references.get(refs.group(1)));
        }

        if ("PATCH".equals(method) || "MERGE".equals(method)) {
          client.header("X-HTTP-METHOD", method);
          res = client.invoke("POST", IOUtils.toInputStream(content));
        } else {
          res = client.invoke(method, IOUtils.toInputStream(content));
        }
      }

      // When updating to CXF 3.0.1, uncomment the following line, see CXF-5865
      // client.close();
    }

    return res;
  }

  private InputStream exploreMultipart(
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
          addItemIntro(bos, null);

          res = bodyPartRequest(new MimeBodyPart(obj.getDataHandler().getInputStream()),
              Collections.<String, String> emptyMap());

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

  private void addItemIntro(final ByteArrayOutputStream bos, final String contentId) throws IOException {
    bos.write("Content-Type: application/http".getBytes());
    bos.write(Constants.CRLF);
    bos.write("Content-Transfer-Encoding: binary".getBytes());
    bos.write(Constants.CRLF);

    if (StringUtils.isNotBlank(contentId)) {
      bos.write(("Content-ID: " + contentId).getBytes());
      bos.write(Constants.CRLF);
    }

    bos.write(Constants.CRLF);
  }

  private void addChangesetItemIntro(
      final ByteArrayOutputStream bos, final String contentId, final String cboundary) throws IOException {
    bos.write(("--" + cboundary).getBytes());
    bos.write(Constants.CRLF);
    bos.write(("Content-ID: " + contentId).getBytes());
    bos.write(Constants.CRLF);
    addItemIntro(bos, null);
  }

  private void addSingleBatchResponse(
      final Response response, final ByteArrayOutputStream bos) throws IOException {
    addSingleBatchResponse(response, null, bos);
  }

  private void addSingleBatchResponse(
      final Response response, final String contentId, final ByteArrayOutputStream bos) throws IOException {
    bos.write("HTTP/1.1 ".getBytes());
    bos.write(String.valueOf(response.getStatusInfo().getStatusCode()).getBytes());
    bos.write(" ".getBytes());
    bos.write(response.getStatusInfo().getReasonPhrase().getBytes());
    bos.write(Constants.CRLF);

    for (Map.Entry<String, List<Object>> header : response.getHeaders().entrySet()) {
      final StringBuilder builder = new StringBuilder();
      for (Object value : header.getValue()) {
        if (builder.length() > 0) {
          builder.append(", ");
        }
        builder.append(value.toString());
      }
      builder.insert(0, ": ").insert(0, header.getKey());
      bos.write(builder.toString().getBytes());
      bos.write(Constants.CRLF);
    }

    if (StringUtils.isNotBlank(contentId)) {
      bos.write(("Content-ID: " + contentId).getBytes());
      bos.write(Constants.CRLF);
    }

    bos.write(Constants.CRLF);

    final Object entity = response.getEntity();
    if (entity != null) {
      bos.write(IOUtils.toByteArray((InputStream) entity));
      bos.write(Constants.CRLF);
    }

    bos.write(Constants.CRLF);
  }

  protected void addErrorBatchResponse(final Exception e, final ByteArrayOutputStream bos)
      throws IOException {
    addErrorBatchResponse(e, null, bos);
  }

  protected void addErrorBatchResponse(final Exception e, final String contentId, final ByteArrayOutputStream bos)
      throws IOException {
    addSingleBatchResponse(xml.createFaultResponse(Accept.XML.toString(), e), contentId, bos);
  }

  /**
   * Retrieve entities from the given entity set and the given type.
   *
   * @param accept Accept header.
   * @param name entity set.
   * @param type entity type.
   * @return entity set.
   */
  @GET
  @Path("/{name}/{type:[a-zA-Z].*}")
  public Response getEntitySet(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("name") final String name,
      @PathParam("type") final String type) {

    try {
      final Accept acceptType = Accept.parse(accept);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final String basePath = name + File.separatorChar;
      final StringBuilder path = new StringBuilder(name).
          append(File.separatorChar).append(type).
          append(File.separatorChar);

      path.append(metadata.getEntitySet(name).isSingleton()
          ? Constants.get(ConstantKey.ENTITY)
              : Constants.get(ConstantKey.FEED));

      final InputStream feed = FSManager.instance().readFile(path.toString(), acceptType);
      return xml.createResponse(null, feed, Commons.getETag(basePath), acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/{name}/{type:[a-zA-Z].*}")
  public Response getEntitySet(@Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("name") final String name,
      @QueryParam("$top") @DefaultValue(StringUtils.EMPTY) final String top,
      @QueryParam("$skip") @DefaultValue(StringUtils.EMPTY) final String skip,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      @QueryParam("$count") @DefaultValue(StringUtils.EMPTY) final String count,
      @QueryParam("$filter") @DefaultValue(StringUtils.EMPTY) final String filter,
      @QueryParam("$orderby") @DefaultValue(StringUtils.EMPTY) final String orderby,
      @QueryParam("$skiptoken") @DefaultValue(StringUtils.EMPTY) final String skiptoken,
      @PathParam("type") final String type) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept);
      }

      final String location = uriInfo.getRequestUri().toASCIIString();
      try {
        // search for function ...
        final InputStream func = FSManager.instance().readFile(name, acceptType);
        return xml.createResponse(location, func, null, acceptType);
      } catch (NotFoundException e) {
        if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
          throw new UnsupportedMediaTypeException("Unsupported media type");
        }

        // search for entitySet ...
        final String basePath = name + File.separatorChar;

        final StringBuilder builder = new StringBuilder();
        builder.append(basePath);

        if (type != null) {
          builder.append(type).append(File.separatorChar);
        }

        if (StringUtils.isNotBlank(orderby)) {
          builder.append(Constants.get(ConstantKey.ORDERBY)).append(File.separatorChar).
          append(orderby).append(File.separatorChar);
        }

        if (StringUtils.isNotBlank(filter)) {
          builder.append(Constants.get(ConstantKey.FILTER)).append(File.separatorChar).
          append(filter.replaceAll("/", "."));
        } else if (StringUtils.isNotBlank(skiptoken)) {
          builder.append(Constants.get(ConstantKey.SKIP_TOKEN)).append(File.separatorChar).
          append(skiptoken);
        } else {
          builder.append(metadata.getEntitySet(name).isSingleton()
              ? Constants.get(ConstantKey.ENTITY)
                  : Constants.get(ConstantKey.FEED));
        }

        final InputStream feed = FSManager.instance().readFile(builder.toString(), Accept.ATOM);

        final ResWrap<EntityCollection> container = atomDeserializer.toEntitySet(feed);

        setInlineCount(container.getPayload(), count);

        final ByteArrayOutputStream content = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);

        // -----------------------------------------------
        // Evaluate $skip and $top
        // -----------------------------------------------
        List<Entity> entries = new ArrayList<Entity>(container.getPayload().getEntities());

        if (StringUtils.isNotBlank(skip)) {
          entries = entries.subList(Integer.valueOf(skip), entries.size());
        }

        if (StringUtils.isNotBlank(top)) {
          entries = entries.subList(0, Integer.valueOf(top));
        }

        container.getPayload().getEntities().clear();
        container.getPayload().getEntities().addAll(entries);
        // -----------------------------------------------

        if (acceptType == Accept.ATOM) {
          atomSerializer.write(writer, container);
        } else {
          jsonSerializer.write(writer, container);
        }
        writer.flush();
        writer.close();

        return xml.createResponse(
            location,
            new ByteArrayInputStream(content.toByteArray()),
            Commons.getETag(basePath),
            acceptType);
      }
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  /**
   * Retrieve entity set or function execution sample.
   *
   * @param accept Accept header.
   * @param name entity set or function name.
   * @param format format query option.
   * @param count count query option.
   * @param filter filter query option.
   * @param orderby orderby query option.
   * @param skiptoken skiptoken query option.
   * @return entity set or function result.
   */
  @GET
  @Path("/{name}")
  public Response getEntitySet(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("name") final String name,
      @QueryParam("$top") @DefaultValue(StringUtils.EMPTY) final String top,
      @QueryParam("$skip") @DefaultValue(StringUtils.EMPTY) final String skip,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      @QueryParam("$count") @DefaultValue(StringUtils.EMPTY) final String count,
      @QueryParam("$filter") @DefaultValue(StringUtils.EMPTY) final String filter,
      @QueryParam("$orderby") @DefaultValue(StringUtils.EMPTY) final String orderby,
      @QueryParam("$skiptoken") @DefaultValue(StringUtils.EMPTY) final String skiptoken) {

    return getEntitySet(uriInfo, accept, name, top, skip, format, count, filter, orderby, skiptoken, null);
  }

  @GET
  @Path("/Person({entityId})")
  public Response getPerson(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entityId") final String entityId,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

    final Response internal = getEntityInternal(
        uriInfo.getRequestUri().toASCIIString(), accept, "Person", entityId, format, null, null);
    if (internal.getStatus() == 200) {
      InputStream entity = (InputStream) internal.getEntity();
      try {
        if (utils.getKey() == Accept.JSON_FULLMETA || utils.getKey() == Accept.ATOM) {
          entity = utils.getValue().addOperation(entity, "Sack", "#DefaultContainer.Sack",
              uriInfo.getAbsolutePath().toASCIIString()
              + "/Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee/Sack");
        }

        return utils.getValue().createResponse(
            uriInfo.getRequestUri().toASCIIString(),
            entity,
            internal.getHeaderString("ETag"),
            utils.getKey());
      } catch (Exception e) {
        LOG.error("Error retrieving entity", e);
        return xml.createFaultResponse(accept, e);
      }
    } else {
      return internal;
    }
  }

  @GET
  @Path("/Product({entityId})")
  public Response getProduct(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entityId") final String entityId,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

    final Response internal = getEntityInternal(
        uriInfo.getRequestUri().toASCIIString(), accept, "Product", entityId, format, null, null);
    if (internal.getStatus() == 200) {
      InputStream entity = (InputStream) internal.getEntity();
      try {
        if (utils.getKey() == Accept.JSON_FULLMETA || utils.getKey() == Accept.ATOM) {
          entity = utils.getValue().addOperation(entity,
              "ChangeProductDimensions", "#DefaultContainer.ChangeProductDimensions",
              uriInfo.getAbsolutePath().toASCIIString() + "/ChangeProductDimensions");
        }

        return utils.getValue().createResponse(
            uriInfo.getRequestUri().toASCIIString(),
            entity,
            internal.getHeaderString("ETag"),
            utils.getKey());
      } catch (Exception e) {
        LOG.error("Error retrieving entity", e);
        return xml.createFaultResponse(accept, e);
      }
    } else {
      return internal;
    }
  }

  @GET
  @Path("/ComputerDetail({entityId})")
  public Response getComputerDetail(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entityId") final String entityId,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

    final Response internal = getEntityInternal(
        uriInfo.getRequestUri().toASCIIString(), accept, "ComputerDetail", entityId, format, null, null);
    if (internal.getStatus() == 200) {
      InputStream entity = (InputStream) internal.getEntity();
      try {
        if (utils.getKey() == Accept.JSON_FULLMETA || utils.getKey() == Accept.ATOM) {
          entity = utils.getValue().addOperation(entity,
              "ResetComputerDetailsSpecifications", "#DefaultContainer.ResetComputerDetailsSpecifications",
              uriInfo.getAbsolutePath().toASCIIString() + "/ResetComputerDetailsSpecifications");
        }

        return utils.getValue().createResponse(
            uriInfo.getRequestUri().toASCIIString(),
            entity,
            internal.getHeaderString("ETag"),
            utils.getKey());
      } catch (Exception e) {
        LOG.error("Error retrieving entity", e);
        return xml.createFaultResponse(accept, e);
      }
    } else {
      return internal;
    }
  }

  /**
   * Retrieve entity sample.
   *
   * @param accept Accept header.
   * @param entitySetName Entity set name.
   * @param entityId entity id.
   * @param format format query option.
   * @param expand expand query option.
   * @param select select query option.
   * @return entity.
   */
  @GET
  @Path("/{entitySetName}({entityId})")
  public Response getEntity(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      @QueryParam("$expand") @DefaultValue(StringUtils.EMPTY) final String expand,
      @QueryParam("$select") @DefaultValue(StringUtils.EMPTY) final String select) {

    return getEntityInternal(
        uriInfo.getRequestUri().toASCIIString(), accept, entitySetName, entityId, format, expand, select);
  }

  protected Response getEntityInternal(
      final String location,
      final String accept,
      final String entitySetName,
      final String entityId,
      final String format,
      final String expand,
      final String select) {

    try {
      final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

      if (utils.getKey() == Accept.XML || utils.getKey() == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final Map.Entry<String, InputStream> entityInfo =
          utils.getValue().readEntity(entitySetName, entityId, Accept.ATOM);

      final InputStream entity = entityInfo.getValue();

      ResWrap<Entity> container = atomDeserializer.toEntity(entity);
      if (container.getContextURL() == null) {
        container = new ResWrap<Entity>(URI.create(Constants.get(ConstantKey.ODATA_METADATA_PREFIX)
            + entitySetName + Constants.get(ConstantKey.ODATA_METADATA_ENTITY_SUFFIX)),
            container.getMetadataETag(), container.getPayload());
      }
      final Entity entry = container.getPayload();

      if ((this instanceof KeyAsSegment)) {
        final Link editLink = new Link();
        editLink.setRel("edit");
        editLink.setTitle(entitySetName);
        editLink.setHref(Constants.get(ConstantKey.DEFAULT_SERVICE_URL) + entitySetName + "/" + entityId);

        entry.setEditLink(editLink);
      }

      if (StringUtils.isNotBlank(select)) {
        final List<String> properties = Arrays.asList(select.split(","));
        final Set<Property> toBeRemoved = new HashSet<Property>();

        for (Property property : entry.getProperties()) {
          if (!properties.contains(property.getName())) {
            toBeRemoved.add(property);
          }
        }

        entry.getProperties().removeAll(toBeRemoved);

        final Set<Link> linkToBeRemoved = new HashSet<Link>();

        for (Link link : entry.getNavigationLinks()) {
          if (!properties.contains(link.getTitle().replaceAll("@.*$", "")) && !properties.contains(link.getTitle())) {
            linkToBeRemoved.add(link);
          }
        }

        entry.getNavigationLinks().removeAll(linkToBeRemoved);
      }

      String tempExpand = expand;
      if (StringUtils.isNotBlank(tempExpand)) {
        tempExpand = StringUtils.substringBefore(tempExpand, "(");
        final List<String> links = Arrays.asList(tempExpand.split(","));

        final Map<Link, Link> replace = new HashMap<Link, Link>();

        for (Link link : entry.getNavigationLinks()) {
          if (links.contains(link.getTitle())) {
            // expand link
            final Link rep = new Link();
            rep.setHref(link.getHref());
            rep.setRel(link.getRel());
            rep.setTitle(link.getTitle());
            rep.setType(link.getType());
            if (link.getType().equals(Constants.get(ConstantKey.ATOM_LINK_ENTRY))) {
              // inline entry
              final Entity inline = atomDeserializer.toEntity(
                  xml.expandEntity(entitySetName, entityId, link.getTitle())).getPayload();
              rep.setInlineEntity(inline);
            } else if (link.getType().equals(Constants.get(ConstantKey.ATOM_LINK_FEED))) {
              // inline feed
              final EntityCollection inline = atomDeserializer.toEntitySet(
                  xml.expandEntity(entitySetName, entityId, link.getTitle())).getPayload();
              rep.setInlineEntitySet(inline);
            }
            replace.put(link, rep);
          }
        }

        for (Map.Entry<Link, Link> link : replace.entrySet()) {
          entry.getNavigationLinks().remove(link.getKey());
          entry.getNavigationLinks().add(link.getValue());
        }
      }

      return xml.createResponse(
          location,
          xml.writeEntity(utils.getKey(), container),
          Commons.getETag(entityInfo.getKey()),
          utils.getKey());
    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/{entitySetName}({entityId})/$value")
  public Response getMediaEntity(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId) {

    try {
      if (!accept.contains("*/*") && !accept.contains("application/octet-stream")) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final AbstractUtilities utils = getUtilities(null);
      final Map.Entry<String, InputStream> entityInfo = utils.readMediaEntity(entitySetName, entityId);
      return utils.createResponse(
          uriInfo.getRequestUri().toASCIIString(),
          entityInfo.getValue(),
          Commons.getETag(entityInfo.getKey()),
          null);

    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(accept, e);
    }
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
      @QueryParam("$count") @DefaultValue(StringUtils.EMPTY) final String count,
      @QueryParam("$filter") @DefaultValue(StringUtils.EMPTY) final String filter,
      @QueryParam("$search") @DefaultValue(StringUtils.EMPTY) final String search,
      @QueryParam("$orderby") @DefaultValue(StringUtils.EMPTY) final String orderby,
      @QueryParam("$skiptoken") @DefaultValue(StringUtils.EMPTY) final String skiptoken) {

    return StringUtils.isBlank(filter) && StringUtils.isBlank(search) ?
        NumberUtils.isNumber(type) ?
            getEntityInternal(uriInfo.getRequestUri().toASCIIString(), accept, "People", type, format, null, null) :
            getEntitySet(accept, "People", type) :
        getEntitySet(uriInfo, accept, "People", top, skip, format, count, filter, orderby, skiptoken, type);
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
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  public Response patchSingletonCompany(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @HeaderParam("If-Match") @DefaultValue(StringUtils.EMPTY) final String ifMatch,
      final String changes) {

    return patchEntityInternal(uriInfo, accept, contentType, prefer, ifMatch, "Company", StringUtils.EMPTY, changes);
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
        acceptType = Accept.parse(accept);
      }

      final InputStream output;
      if (StringUtils.isBlank(deltatoken)) {
        final InputStream input = (InputStream) getEntitySet(
            uriInfo, accept, "Customers", null, null, format, null, null, null, null).getEntity();
        final EntityCollection entitySet = xml.readEntitySet(acceptType, input);

        boolean trackChanges = prefer.contains("odata.track-changes");
        if (trackChanges) {
          entitySet.setDeltaLink(URI.create("Customers?$deltatoken=8015"));
        }

        output = xml.writeEntitySet(acceptType, new ResWrap<EntityCollection>(
            URI.create(Constants.get(ConstantKey.ODATA_METADATA_PREFIX) + "Customers"),
            null,
            entitySet));
      } else {
        output = FSManager.instance().readFile("delta", acceptType);
      }

      final Response response = xml.createResponse(
          null,
          output,
          null,
          acceptType);
      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object> singletonList(prefer));
      }
      return response;
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/{entitySetName}")
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM })
  public Response postNewEntity(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @PathParam("entitySetName") final String entitySetName,
      final String entity) {

    try {
      final Accept acceptType = Accept.parse(accept);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final ResWrap<Entity> container;

      final org.apache.olingo.fit.metadata.EntitySet entitySet = metadata.getEntitySet(entitySetName);

      final Entity entry;
      final String entityKey;
      if (xml.isMediaContent(entitySetName)) {
        entry = new Entity();
        entry.setMediaContentType(ContentType.APPLICATION_OCTET_STREAM.toContentTypeString());
        entry.setType(entitySet.getType());

        entityKey = xml.getDefaultEntryKey(entitySetName, entry);

        xml.addMediaEntityValue(entitySetName, entityKey, IOUtils.toInputStream(entity, Constants.ENCODING));

        final Pair<String, EdmPrimitiveTypeKind> id = Commons.getMediaContent().get(entitySetName);
        if (id != null) {
          final Property prop = new Property();
          prop.setName(id.getKey());
          prop.setType(id.getValue().toString());
          prop.setValue(ValueType.PRIMITIVE,
              id.getValue() == EdmPrimitiveTypeKind.Int32
              ? Integer.parseInt(entityKey)
                  : id.getValue() == EdmPrimitiveTypeKind.Guid
                  ? UUID.fromString(entityKey)
                      : entityKey);
          entry.getProperties().add(prop);
        }

        final Link editLink = new Link();
        editLink.setHref(Commons.getEntityURI(entitySetName, entityKey));
        editLink.setRel("edit");
        editLink.setTitle(entitySetName);
        entry.setEditLink(editLink);

        entry.setMediaContentSource(URI.create(editLink.getHref() + "/$value"));

        container = new ResWrap<Entity>((URI) null, null, entry);
      } else {
        final Accept contentTypeValue = Accept.parse(contentType);
        if (Accept.ATOM == contentTypeValue) {
          container = atomDeserializer.toEntity(IOUtils.toInputStream(entity, Constants.ENCODING));
        } else {
          container = jsonDeserializer.toEntity(IOUtils.toInputStream(entity, Constants.ENCODING));
        }
        entry = container.getPayload();
        updateInlineEntities(entry);

        entityKey = xml.getDefaultEntryKey(entitySetName, entry);
      }

      normalizeAtomEntry(entry, entitySetName, entityKey);

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);
      atomSerializer.write(writer, container);
      writer.flush();
      writer.close();

      final InputStream serialization =
          xml.addOrReplaceEntity(entityKey, entitySetName, new ByteArrayInputStream(content.toByteArray()), entry);

      ResWrap<Entity> result = atomDeserializer.toEntity(serialization);
      result = new ResWrap<Entity>(
          URI.create(Constants.get(ConstantKey.ODATA_METADATA_PREFIX)
              + entitySetName + Constants.get(ConstantKey.ODATA_METADATA_ENTITY_SUFFIX)),
              null, result.getPayload());

      final String path = Commons.getEntityBasePath(entitySetName, entityKey);
      FSManager.instance().putInMemory(result, path + Constants.get(ConstantKey.ENTITY));

      final String location;

      if ((this instanceof KeyAsSegment)) {
        location = uriInfo.getRequestUri().toASCIIString() + "/" + entityKey;

        final Link editLink = new Link();
        editLink.setRel("edit");
        editLink.setTitle(entitySetName);
        editLink.setHref(location);

        result.getPayload().setEditLink(editLink);
      } else {
        location = uriInfo.getRequestUri().toASCIIString() + "(" + entityKey + ")";
      }

      final Response response;
      if ("return-no-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(
            location,
            null,
            null,
            acceptType,
            Response.Status.NO_CONTENT);
      } else {
        response = xml.createResponse(
            location,
            xml.writeEntity(acceptType, result),
            null,
            acceptType,
            Response.Status.CREATED);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object> singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      LOG.error("While creating new entity", e);
      return xml.createFaultResponse(accept, e);
    }
  }

  private void updateInlineEntities(final Entity entity) {
    final String type = entity.getType();
    EntityType entityType;
    Map<String, NavigationProperty> navProperties = Collections.emptyMap();
    if (type != null && type.length() > 0) {
      entityType = metadata.getEntityOrComplexType(type);
      navProperties = entityType.getNavigationPropertyMap();
    }

    for (Property property : entity.getProperties()) {
      if (navProperties.containsKey(property.getName())) {
        Link alink = new Link();
        alink.setTitle(property.getName());
        alink.getAnnotations().addAll(property.getAnnotations());

        alink.setType(navProperties.get(property.getName()).isEntitySet()
            ? Constants.get(ConstantKey.ATOM_LINK_FEED)
                : Constants.get(ConstantKey.ATOM_LINK_ENTRY));

        alink.setRel(Constants.get(ConstantKey.ATOM_LINK_REL) + property.getName());

        if (property.isCollection()) {
          EntityCollection inline = new EntityCollection();
          for (Object value : property.asCollection()) {
            Entity inlineEntity = new Entity();
            inlineEntity.setType(navProperties.get(property.getName()).getType());
            for (Property prop : ((ComplexValue) value).getValue()) {
              inlineEntity.getProperties().add(prop);
            }
            inline.getEntities().add(inlineEntity);
          }
          alink.setInlineEntitySet(inline);
        } else if (property.isComplex()) {
          Entity inline = new Entity();
          inline.setType(navProperties.get(property.getName()).getType());
          for (Property prop : property.asComplex().getValue()) {
            inline.getProperties().add(prop);
          }
          alink.setInlineEntity(inline);

        } else {
          throw new IllegalStateException("Invalid navigation property " + property);
        }
        entity.getNavigationLinks().add(alink);
      }
    }
  }

  private void normalizeAtomEntry(final Entity entry, final String entitySetName, final String entityKey) {
    final org.apache.olingo.fit.metadata.EntitySet entitySet = metadata.getEntitySet(entitySetName);
    final EntityType entityType = metadata.getEntityOrComplexType(entitySet.getType());
    for (Map.Entry<String, org.apache.olingo.fit.metadata.Property> property : entityType.getPropertyMap().entrySet()) {
      if (entry.getProperty(property.getKey()) == null && property.getValue().isNullable()) {
        final Property prop = new Property();
        prop.setName(property.getKey());
        prop.setValue(ValueType.PRIMITIVE, null);
        entry.getProperties().add(prop);
      }
    }

    for (Map.Entry<String, NavigationProperty> property : entityType.getNavigationPropertyMap().entrySet()) {
      boolean found = false;
      for (Link link : entry.getNavigationLinks()) {
        if (link.getTitle().equals(property.getKey())) {
          found = true;
        }
      }

      if (!found) {
        final Link link = new Link();
        link.setTitle(property.getKey());
        link.setType(property.getValue().isEntitySet()
            ? Constants.get(ConstantKey.ATOM_LINK_FEED)
                : Constants.get(ConstantKey.ATOM_LINK_ENTRY));
        link.setRel(Constants.get(ConstantKey.ATOM_LINK_REL) + property.getKey());
        link.setHref(entitySetName + "(" + entityKey + ")/" + property.getKey());
        entry.getNavigationLinks().add(link);
      }
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
        acceptType = Accept.parse(accept);
      }

      final Property property = new Property();
      property.setType("Edm.Int32");
      property.setValue(ValueType.PRIMITIVE, 2);
      final ResWrap<Property> container = new ResWrap<Property>(
          URI.create(Constants.get(ConstantKey.ODATA_METADATA_PREFIX) + property.getType()), null,
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
  @Path("/Person({entityId})/{type:.*}/Sack")
  public Response actionSack(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entityId") final String entityId,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

    if (utils.getKey() == Accept.XML || utils.getKey() == Accept.TEXT) {
      throw new UnsupportedMediaTypeException("Unsupported media type");
    }

    try {
      final Map.Entry<String, InputStream> entityInfo = xml.readEntity("Person", entityId, Accept.ATOM);

      final InputStream entity = entityInfo.getValue();
      final ResWrap<Entity> container = atomDeserializer.toEntity(entity);

      container.getPayload().getProperty("Salary").setValue(ValueType.PRIMITIVE, 0);
      container.getPayload().getProperty("Title").setValue(ValueType.PRIMITIVE, "[Sacked]");

      final FSManager fsManager = FSManager.instance();
      fsManager.putInMemory(xml.writeEntity(Accept.ATOM, container),
          fsManager.getAbsolutePath(Commons.getEntityBasePath("Person", entityId) + Constants.get(
              ConstantKey.ENTITY), Accept.ATOM));

      return utils.getValue().createResponse(null, null, null, utils.getKey(), Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Person/{type:.*}/IncreaseSalaries")
  public Response actionIncreaseSalaries(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("type") final String type,
      final String body) {

    final String name = "Person";
    try {
      final Accept acceptType = Accept.parse(accept);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final JsonNode tree = new ObjectMapper().readTree(body);
      if (!tree.has("n")) {
        throw new Exception("Missing parameter: n");
      }
      final int n = tree.get("n").asInt();

      final StringBuilder path = new StringBuilder(name).
          append(File.separatorChar).append(type).
          append(File.separatorChar);

      path.append(metadata.getEntitySet(name).isSingleton()
          ? Constants.get(ConstantKey.ENTITY)
              : Constants.get(ConstantKey.FEED));

      final InputStream feed = FSManager.instance().readFile(path.toString(), acceptType);

      final ByteArrayOutputStream copy = new ByteArrayOutputStream();
      IOUtils.copy(feed, copy);
      IOUtils.closeQuietly(feed);

      String newContent = new String(copy.toByteArray(), "UTF-8");
      final Pattern salary = Pattern.compile(acceptType == Accept.ATOM
          ? "\\<d:Salary m:type=\"Edm.Int32\"\\>(-?\\d+)\\</d:Salary\\>"
              : "\"Salary\":(-?\\d+),");
      final Matcher salaryMatcher = salary.matcher(newContent);
      while (salaryMatcher.find()) {
        final Long newSalary = Long.valueOf(salaryMatcher.group(1)) + n;
        newContent = newContent.
            replaceAll("\"Salary\":" + salaryMatcher.group(1) + ",",
                "\"Salary\":" + newSalary + ",").
                replaceAll("\\<d:Salary m:type=\"Edm.Int32\"\\>" + salaryMatcher.group(1) + "</d:Salary\\>",
                    "<d:Salary m:type=\"Edm.Int32\">" + newSalary + "</d:Salary>");
      }

      FSManager.instance().putInMemory(IOUtils.toInputStream(newContent, Constants.ENCODING),
          FSManager.instance().getAbsolutePath(path.toString(), acceptType));

      return xml.createResponse(null, null, null, acceptType, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Product({entityId})/ChangeProductDimensions")
  public Response actionChangeProductDimensions(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entityId") final String entityId,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      final String argument) {

    final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

    if (utils.getKey() == Accept.XML || utils.getKey() == Accept.TEXT) {
      throw new UnsupportedMediaTypeException("Unsupported media type");
    }

    try {
      final Map.Entry<String, InputStream> entityInfo = xml.readEntity("Product", entityId, Accept.ATOM);

      final InputStream entity = entityInfo.getValue();
      final ResWrap<Entity> container = atomDeserializer.toEntity(entity);

      final Entity param = xml.readEntity(utils.getKey(), IOUtils.toInputStream(argument, Constants.ENCODING));

      final Property property = param.getProperty("dimensions");
      container.getPayload().getProperty("Dimensions").setValue(property.getValueType(), property.getValue());

      final FSManager fsManager = FSManager.instance();
      fsManager.putInMemory(xml.writeEntity(Accept.ATOM, container),
          fsManager.getAbsolutePath(Commons.getEntityBasePath("Product", entityId) + Constants.get(
              ConstantKey.ENTITY), Accept.ATOM));

      return utils.getValue().createResponse(null, null, null, utils.getKey(), Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/ComputerDetail({entityId})/ResetComputerDetailsSpecifications")
  public Response actionResetComputerDetailsSpecifications(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entityId") final String entityId,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      final String argument) {

    final Map.Entry<Accept, AbstractUtilities> utils = getUtilities(accept, format);

    if (utils.getKey() == Accept.XML || utils.getKey() == Accept.TEXT) {
      throw new UnsupportedMediaTypeException("Unsupported media type");
    }

    try {
      final Map.Entry<String, InputStream> entityInfo = xml.readEntity("ComputerDetail", entityId, Accept.ATOM);

      final InputStream entity = entityInfo.getValue();
      final ResWrap<Entity> container = atomDeserializer.toEntity(entity);

      final Entity param = xml.readEntity(utils.getKey(), IOUtils.toInputStream(argument, Constants.ENCODING));

      Property property = param.getProperty("specifications");
      container.getPayload().getProperty("SpecificationsBag").setValue(property.getValueType(), property.getValue());
      property = param.getProperty("purchaseTime");
      container.getPayload().getProperty("PurchaseDate").setValue(property.getValueType(), property.getValue());

      final FSManager fsManager = FSManager.instance();
      fsManager.putInMemory(xml.writeEntity(Accept.ATOM, container),
          fsManager.getAbsolutePath(Commons.getEntityBasePath("ComputerDetail", entityId) + Constants.get(
              ConstantKey.ENTITY), Accept.ATOM));

      return utils.getValue().createResponse(null, null, null, utils.getKey(), Response.Status.NO_CONTENT);
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
        acceptType = Accept.parse(accept);
      }

      final Accept contentTypeValue = Accept.parse(contentType);
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
        acceptType = Accept.parse(accept);
      }

      final Entity entry = new Entity();
      entry.setType("Microsoft.Test.OData.Services.ODataWCFService.ProductDetail");
      final Property productId = new Property();
      productId.setName("ProductID");
      productId.setType("Edm.Int32");
      productId.setValue(ValueType.PRIMITIVE, Integer.valueOf(entityId));
      entry.getProperties().add(productId);
      final Property productDetailId = new Property();
      productDetailId.setName("ProductDetailID");
      productDetailId.setType("Edm.Int32");
      productDetailId.setValue(ValueType.PRIMITIVE, 2);
      entry.getProperties().add(productDetailId);

      final Link link = new Link();
      link.setRel("edit");
      link.setHref(URI.create(
          Constants.get(ConstantKey.DEFAULT_SERVICE_URL)
              + "ProductDetails(ProductID=6,ProductDetailID=1)").toASCIIString());
      entry.setEditLink(link);

      final EntityCollection feed = new EntityCollection();
      feed.getEntities().add(entry);

      final ResWrap<EntityCollection> container = new ResWrap<EntityCollection>(
          URI.create(Constants.get(ConstantKey.ODATA_METADATA_PREFIX) + "ProductDetail"), null,
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
        acceptType = Accept.parse(accept);
      }

      final Accept contentTypeValue = Accept.parse(contentType);
      final Entity entry = xml.readEntity(contentTypeValue, IOUtils.toInputStream(param, Constants.ENCODING));

      assert 1 == entry.getProperties().size();
      assert entry.getProperty("accessRight") != null;

      final Property property = entry.getProperty("accessRight");
      property.setType("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel");

      final ResWrap<Property> result = new ResWrap<Property>(
          URI.create(Constants.get(ConstantKey.ODATA_METADATA_PREFIX) + property.getType()),
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
      final Accept contentTypeValue = Accept.parse(contentType);
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
      final Accept contentTypeValue = Accept.parse(contentType);
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
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept);
      }

      final Property property = new Property();
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
          FSManager.instance().readFile(Constants.get(ConstantKey.REF)
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
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM })
  public Response postPeople(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      final String entity) {

    if ("{\"@odata.type\":\"#Microsoft.Test.OData.Services.ODataWCFService.Person\"}".equals(entity)) {
      return xml.createFaultResponse(accept, new BadRequestException());
    }

    return postNewEntity(uriInfo, accept, contentType, prefer, "People", entity);
  }

  private Response patchEntityInternal(final UriInfo uriInfo,
      final String accept, final String contentType, final String prefer, final String ifMatch,
      final String entitySetName, final String entityId, final String changes) {

    try {
      final Accept acceptType = Accept.parse(accept);

      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final Map.Entry<String, InputStream> entityInfo = xml.readEntity(entitySetName, entityId, Accept.ATOM);

      final String etag = Commons.getETag(entityInfo.getKey());
      if (StringUtils.isNotBlank(ifMatch) && !ifMatch.equals(etag)) {
        throw new ConcurrentModificationException("Concurrent modification");
      }

      final Accept contentTypeValue = Accept.parse(contentType);

      final Entity entryChanges;

      if (contentTypeValue == Accept.XML || contentTypeValue == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      } else if (contentTypeValue == Accept.ATOM) {
        entryChanges = atomDeserializer.toEntity(
            IOUtils.toInputStream(changes, Constants.ENCODING)).getPayload();
      } else {
        final ResWrap<Entity> jcont = jsonDeserializer.toEntity(IOUtils.toInputStream(changes, Constants.ENCODING));
        entryChanges = jcont.getPayload();
      }

      final ResWrap<Entity> container = atomDeserializer.toEntity(entityInfo.getValue());

      for (Property property : entryChanges.getProperties()) {
        final Property _property = container.getPayload().getProperty(property.getName());
        if (_property == null) {
          container.getPayload().getProperties().add(property);
        } else {
          _property.setValue(property.getValueType(), property.getValue());
        }
      }

      for (Link link : entryChanges.getNavigationLinks()) {
        container.getPayload().getNavigationLinks().add(link);
      }

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);
      atomSerializer.write(writer, container);
      writer.flush();
      writer.close();

      final InputStream res = xml.addOrReplaceEntity(
          entityId, entitySetName, new ByteArrayInputStream(content.toByteArray()), container.getPayload());

      final ResWrap<Entity> cres = atomDeserializer.toEntity(res);

      normalizeAtomEntry(cres.getPayload(), entitySetName, entityId);

      final String path = Commons.getEntityBasePath(entitySetName, entityId);
      FSManager.instance().putInMemory(
          cres, path + File.separatorChar + Constants.get(ConstantKey.ENTITY));

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(
            uriInfo.getRequestUri().toASCIIString(),
            xml.readEntity(entitySetName, entityId, acceptType).getValue(),
            null, acceptType, Response.Status.OK);
      } else {
        res.close();
        response = xml.createResponse(
            uriInfo.getRequestUri().toASCIIString(),
            null,
            null,
            acceptType, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object> singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @PATCH
  @Path("/{entitySetName}({entityId})")
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  public Response patchEntity(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @HeaderParam("If-Match") @DefaultValue(StringUtils.EMPTY) final String ifMatch,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      final String changes) {

    final Response response =
        getEntityInternal(uriInfo.getRequestUri().toASCIIString(),
            accept, entitySetName, entityId, accept, StringUtils.EMPTY, StringUtils.EMPTY);
    return response.getStatus() >= 400 ?
        postNewEntity(uriInfo, accept, contentType, prefer, entitySetName, changes) :
        patchEntityInternal(uriInfo, accept, contentType, prefer, ifMatch, entitySetName, entityId, changes);
  }

  private Response replaceEntity(final UriInfo uriInfo,
      final String accept, final String prefer,
      final String entitySetName, final String entityId, final String entity) {

    try {
      final Accept acceptType = Accept.parse(accept);

      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final InputStream res = getUtilities(acceptType).addOrReplaceEntity(entityId, entitySetName,
          IOUtils.toInputStream(entity, Constants.ENCODING),
          xml.readEntity(acceptType, IOUtils.toInputStream(entity, Constants.ENCODING)));

      final ResWrap<Entity> cres;
      if (acceptType == Accept.ATOM) {
        cres = atomDeserializer.toEntity(res);
      } else {
        cres = jsonDeserializer.toEntity(res);
      }

      final String path = Commons.getEntityBasePath(entitySetName, entityId);
      FSManager.instance().putInMemory(
          cres, path + File.separatorChar + Constants.get(ConstantKey.ENTITY));

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(
            uriInfo.getRequestUri().toASCIIString(),
            xml.readEntity(entitySetName, entityId, acceptType).getValue(),
            null,
            acceptType,
            Response.Status.OK);
      } else {
        res.close();
        response = xml.createResponse(
            uriInfo.getRequestUri().toASCIIString(),
            null,
            null,
            acceptType,
            Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object> singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @PUT
  @Path("/{entitySetName}({entityId})")
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  public Response replaceEntity(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      final String entity) {

    try {
      getEntityInternal(uriInfo.getRequestUri().toASCIIString(),
          accept, entitySetName, entityId, accept, StringUtils.EMPTY, StringUtils.EMPTY);
      return replaceEntity(uriInfo, accept, prefer, entitySetName, entityId, entity);
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

  @DELETE
  @Path("/{entitySetName}({entityId})")
  public Response removeEntity(
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId) {

    try {
      final String basePath = entitySetName + File.separatorChar + Commons.getEntityKey(entityId);

      FSManager.instance().deleteEntity(basePath);

      return xml.createResponse(null, null, null, null, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.XML.toString(), e);
    }
  }

  private Response replaceProperty(
      final String location,
      final String accept,
      final String contentType,
      final String prefer,
      final String entitySetName,
      final String entityId,
      final String path,
      final String format,
      final String changes,
      final boolean justValue) {

    // if the given path is not about any link then search for property
    LOG.info("Retrieve property {}", path);

    try {
      final FSManager fsManager = FSManager.instance();
      final String basePath = Commons.getEntityBasePath(entitySetName, entityId);

      final ResWrap<Entity> container = xml.readContainerEntity(Accept.ATOM,
          fsManager.readFile(basePath + Constants.get(ConstantKey.ENTITY), Accept.ATOM));

      final Entity entry = container.getPayload();

      Property toBeReplaced = null;
      for (String element : path.split("/")) {
        if (toBeReplaced == null) {
          toBeReplaced = entry.getProperty(element.trim());
        } else {
          List<Property> value = toBeReplaced.asComplex().getValue();
          for (Property field : value) {
            if (field.getName().equalsIgnoreCase(element)) {
              toBeReplaced = field;
            }
          }
        }
      }

      if (toBeReplaced == null) {
        throw new NotFoundException();
      }

      if (justValue) {
        // just for primitive values
        toBeReplaced.setValue(ValueType.PRIMITIVE, changes);
      } else {
        final Property pchanges = xml.readProperty(
            Accept.parse(contentType),
            IOUtils.toInputStream(changes, Constants.ENCODING));

        toBeReplaced.setValue(pchanges.getValueType(), pchanges.getValue());
      }

      fsManager.putInMemory(xml.writeEntity(Accept.ATOM, container),
          fsManager.getAbsolutePath(basePath + Constants.get(ConstantKey.ENTITY), Accept.ATOM));

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = getEntityInternal(location, accept, entitySetName, entityId, format, null, null);
      } else {
        Accept acceptType = null;
        if (StringUtils.isNotBlank(format)) {
          acceptType = Accept.valueOf(format.toUpperCase());
        } else if (StringUtils.isNotBlank(accept)) {
          acceptType = Accept.parse(accept, null);
        }

        response = xml.createResponse(null, null, null, acceptType, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object> singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  private Response deletePropertyValue(
      final String accept,
      final String prefer,
      final String entitySetName,
      final String entityId,
      final String path,
      final String format) {
    try {
      Accept acceptType = null;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else if (StringUtils.isNotBlank(accept)) {
        acceptType = Accept.parse(accept, null);
      }

      // if the given path is not about any link then search for property
      LOG.info("Retrieve property {}", path);

      final AbstractUtilities utils = getUtilities(acceptType);

      final InputStream changed = utils.deleteProperty(
          entitySetName,
          entityId,
          Arrays.asList(path.split("/")),
          acceptType);

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(null, changed, null, acceptType, Response.Status.OK);
      } else {
        changed.close();
        response = xml.createResponse(null, null, null, acceptType, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object> singletonList(prefer));
      }

      return response;

    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  /**
   * Replace property value.
   *
   * @param accept
   * @param entitySetName
   * @param entityId
   * @param path
   * @param format
   * @param changes
   * @return response
   */
  @PUT
  @Path("/{entitySetName}({entityId})/{path:.*}/$value")
  public Response replacePropertyValue(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      @PathParam("path") final String path,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      final String changes) {

    return replaceProperty(uriInfo.getRequestUri().toASCIIString(),
        accept, contentType, prefer, entitySetName, entityId, path, format, changes, true);
  }

  /**
   * Replace property.
   *
   * @param accept
   * @param entitySetName
   * @param entityId
   * @param path
   * @param format
   * @param changes
   * @return response
   */
  @PATCH
  @Path("/{entitySetName}({entityId})/{path:.*}")
  public Response patchProperty(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      @PathParam("path") final String path,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      final String changes) {

    return replaceProperty(uriInfo.getRequestUri().toASCIIString(),
        accept, contentType, prefer, entitySetName, entityId, path, format, changes, false);
  }

  @PUT
  @Produces({ MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.WILDCARD, MediaType.APPLICATION_OCTET_STREAM })
  @Path("/{entitySetName}({entityId})/$value")
  public Response replaceMediaEntity(
      @Context final UriInfo uriInfo,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      final String value) {
    try {

      final AbstractUtilities utils = getUtilities(null);

      final InputStream res = utils.putMediaInMemory(
          entitySetName, entityId, IOUtils.toInputStream(value, Constants.ENCODING));

      final String location = uriInfo.getRequestUri().toASCIIString().replace("/$value", "");

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(location, res, null, null, Response.Status.OK);
      } else {
        res.close();
        response = xml.createResponse(location, null, null, null, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object> singletonList(prefer));
      }

      return response;

    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(Accept.JSON.toString(), e);
    }
  }

  /**
   * Replace property.
   *
   * @param accept
   * @param entitySetName
   * @param entityId
   * @param path
   * @param format
   * @param changes
   * @return response
   */
  @PUT
  @Path("/{entitySetName}({entityId})/{path:.*}")
  public Response replaceProperty(
      @Context final UriInfo uriInfo,
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      @PathParam("path") final String path,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
      final String changes) {

    if (xml.isMediaContent(entitySetName + "/" + path)) {
      return replaceMediaProperty(prefer, entitySetName, entityId, path, changes);
    } else {
      return replaceProperty(uriInfo.getRequestUri().toASCIIString(),
          accept, contentType, prefer, entitySetName, entityId, path, format, changes, false);
    }
  }

  private Response replaceMediaProperty(
      final String prefer,
      final String entitySetName,
      final String entityId,
      final String path,
      final String value) {

    try {
      final AbstractUtilities utils = getUtilities(null);

      InputStream res = utils.putMediaInMemory(
          entitySetName, entityId, path, IOUtils.toInputStream(value, Constants.ENCODING));

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = xml.createResponse(null, res, null, null, Response.Status.OK);
      } else {
        res.close();
        response = xml.createResponse(null, null, null, null, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object> singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(Accept.JSON.toString(), e);
    }
  }

  /**
   * Nullify property value.
   *
   * @param accept
   * @param entitySetName
   * @param entityId
   * @param path
   * @param format
   * @return response
   */
  @DELETE
  @Path("/{entitySetName}({entityId})/{path:.*}/$value")
  public Response deleteProperty(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      @PathParam("path") final String path,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {
    return deletePropertyValue(accept, prefer, entitySetName, entityId, path, format);
  }

  /**
   * Retrieve property sample.
   *
   * @param accept Accept header.
   * @param entitySetName Entity set name.
   * @param entityId entity id.
   * @param path path.
   * @param format format query option.
   * @return property.
   */
  @GET
  @Path("/{entitySetName}({entityId})/{path:.*}/$value")
  public Response getPathValue(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      @PathParam("path") final String path,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    try {
      Accept acceptType = null;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else if (StringUtils.isNotBlank(accept)) {
        acceptType = Accept.parse(accept, null);
      }

      return navigateProperty(acceptType, entitySetName, entityId, path, true);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  /**
   * Retrieve property sample.
   *
   * @param accept Accept header.
   * @param entitySetName Entity set name.
   * @param entityId entity id.
   * @param path path.
   * @param format format query option.
   * @return property.
   */
  @GET
  @Path("/{entitySetName}({entityId})/{path:.*}")
  public Response getPath(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entitySetName") final String entitySetName,
      @PathParam("entityId") final String entityId,
      @PathParam("path") final String path,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    // default utilities
    final AbstractUtilities utils = xml;

    try {
      if (utils.isMediaContent(entitySetName + "/" + path)) {
        return navigateStreamedEntity(entitySetName, entityId, path);
      } else {
        Accept acceptType = null;
        if (StringUtils.isNotBlank(format)) {
          acceptType = Accept.valueOf(format.toUpperCase());
        } else if (StringUtils.isNotBlank(accept)) {
          acceptType = Accept.parse(accept, null);
        }

        try {
          final LinkInfo linkInfo = xml.readLinks(entitySetName, entityId, path, Accept.XML);
          final Map.Entry<String, List<String>> links = xml.extractLinkURIs(linkInfo.getLinks());
          final InputStream stream = xml.readEntities(links.getValue(), path, links.getKey(), linkInfo.isFeed());

          final ByteArrayOutputStream content = new ByteArrayOutputStream();
          final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);

          final ResWrap<?> container = linkInfo.isFeed() ? atomDeserializer.toEntitySet(stream) : atomDeserializer.
              toEntity(stream);
          if (acceptType == Accept.ATOM) {
            atomSerializer.write(writer, container);
          } else {
            jsonSerializer.write(writer, container);
          }
          writer.flush();
          writer.close();

          final String basePath = Commons.getEntityBasePath(entitySetName, entityId);

          return xml.createResponse(
              null,
              new ByteArrayInputStream(content.toByteArray()),
              Commons.getETag(basePath),
              acceptType);

        } catch (NotFoundException e) {
          // if the given path is not about any link then search for property
          return navigateProperty(acceptType, entitySetName, entityId, path, false);
        }
      }
    } catch (Exception e) {
      return utils.createFaultResponse(accept, e);
    }
  }

  private Response navigateStreamedEntity(
      final String entitySetName,
      final String entityId,
      final String path) throws Exception {

    final AbstractUtilities utils = getUtilities(null);
    final Map.Entry<String, InputStream> entityInfo = utils.readMediaEntity(entitySetName, entityId, path);
    return utils.createResponse(null, entityInfo.getValue(), Commons.getETag(entityInfo.getKey()), null);
  }

  private Response navigateProperty(
      final Accept acceptType,
      final String entitySetName,
      final String entityId,
      final String path,
      final boolean searchForValue) throws Exception {

    if ((searchForValue && acceptType != null && acceptType != Accept.TEXT) || acceptType == Accept.ATOM) {
      throw new UnsupportedMediaTypeException("Unsupported media type " + acceptType);
    }

    final AbstractUtilities utils = getUtilities(acceptType);

    final Map.Entry<String, InputStream> entityInfo = utils.readEntity(entitySetName, entityId, Accept.ATOM);

    final InputStream entity = entityInfo.getValue();

    final ResWrap<Entity> entryContainer = atomDeserializer.toEntity(entity);

    final String[] pathElems = StringUtils.split(path, "/");
    Property property = entryContainer.getPayload().getProperty(pathElems[0]);
    if (pathElems.length > 1 && property.isComplex()) {
      for (Property sub : property.asComplex().getValue()) {
        if (pathElems[1].equals(sub.getName())) {
          property = sub;
          if (pathElems.length > 2 && property.isComplex()) {
            for (Property subsub : property.asComplex().getValue()) {
              if (pathElems[2].equals(subsub.getName())) {
                property = subsub;
              }
            }
          }
        }
      }
    }

    final ResWrap<Property> container = new ResWrap<Property>(
        URI.create(Constants.get(ConstantKey.ODATA_METADATA_PREFIX) + entitySetName + "(" + entityId + ")/" + path),
        entryContainer.getMetadataETag(),
        property);

    return xml.createResponse(null,
        searchForValue ? IOUtils.toInputStream(
            container.getPayload().isNull() ? StringUtils.EMPTY : stringValue(container.getPayload()),
                Constants.ENCODING) : utils.writeProperty(acceptType, container),
                Commons.getETag(Commons.getEntityBasePath(entitySetName, entityId)),
                acceptType);
  }

  private String stringValue(final Property property) {
    EdmPrimitiveTypeKind kind = EdmPrimitiveTypeKind.valueOfFQN(property.getType());
    try {
      return EdmPrimitiveTypeFactory.getInstance(kind)
          .valueToString(property.asPrimitive(), null, null,
              org.apache.olingo.commons.api.Constants.DEFAULT_PRECISION,
              org.apache.olingo.commons.api.Constants.DEFAULT_SCALE, null);
    } catch (final EdmPrimitiveTypeException e) {
      return property.asPrimitive().toString();
    }
  }

  /**
   * Count sample.
   *
   * @param accept Accept header.
   * @param entitySetName entity set name.
   * @return count.
   */
  @GET
  @Path("/{entitySetName}/$count")
  public Response count(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entitySetName") final String entitySetName) {
    try {
      final Accept acceptType = Accept.parse(accept, Accept.TEXT);

      if (acceptType != Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported type " + accept);
      }

      int count = xml.countAllElements(entitySetName);

      final Response.ResponseBuilder builder = Response.ok();
      builder.entity(count);

      return builder.build();
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
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
        acceptType = Accept.parse(accept);
      }
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final StringBuilder containedPath = containedPath(entityId, containedEntitySetName);
      if (StringUtils.isNotBlank(containedEntityId)) {
        containedPath.append('(').append(containedEntityId).append(')');
      }
      final InputStream entry = FSManager.instance().readFile(containedPath.toString(), Accept.ATOM);

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
      final Accept acceptType = Accept.parse(accept);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final AbstractUtilities utils = getUtilities(acceptType);

      // 1. parse the entry (from Atom or JSON)
      final ResWrap<Entity> entryContainer;
      final Entity entry;
      final Accept contentTypeValue = Accept.parse(contentType);
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
      FSManager.instance().putInMemory(
          utils.writeEntity(Accept.ATOM, entryContainer),
          FSManager.instance().getAbsolutePath(atomEntryRelativePath, Accept.ATOM));

      // 3. Update the contained entity set
      final String atomFeedRelativePath = containedPath(entityId, containedEntitySetName).toString();
      final InputStream feedIS = FSManager.instance().readFile(atomFeedRelativePath, Accept.ATOM);
      final ResWrap<EntityCollection> feedContainer = atomDeserializer.toEntitySet(feedIS);
      feedContainer.getPayload().getEntities().add(entry);

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);
      atomSerializer.write(writer, feedContainer);
      writer.flush();
      writer.close();

      FSManager.instance().putInMemory(
          new ByteArrayInputStream(content.toByteArray()),
          FSManager.instance().getAbsolutePath(atomFeedRelativePath, Accept.ATOM));

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
        acceptType = Accept.parse(accept);
      }
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final Accept contentTypeValue;
      if (StringUtils.isBlank(contentType)) {
        throw new IllegalArgumentException();
      }
      contentTypeValue = Accept.parse(contentType);

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

      FSManager.instance().putInMemory(new ResWrap<Entity>((URI) null, null, original),
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
      final InputStream entry = FSManager.instance().
          readFile(containedPath(entityId, containedEntitySetName).
              append('(').append(containedEntityId).append(')').toString(), Accept.ATOM);
      final ResWrap<Entity> container = atomDeserializer.toEntity(entry);

      // 2. Remove the contained entity
      final String atomEntryRelativePath = containedPath(entityId, containedEntitySetName).
          append('(').append(containedEntityId).append(')').toString();
      FSManager.instance().deleteFile(atomEntryRelativePath);

      // 3. Update the contained entity set
      final String atomFeedRelativePath = containedPath(entityId, containedEntitySetName).toString();
      final InputStream feedIS = FSManager.instance().readFile(atomFeedRelativePath, Accept.ATOM);
      final ResWrap<EntityCollection> feedContainer = atomDeserializer.toEntitySet(feedIS);
      feedContainer.getPayload().getEntities().remove(container.getPayload());

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);
      atomSerializer.write(writer, feedContainer);
      writer.flush();
      writer.close();

      FSManager.instance().putInMemory(
          new ByteArrayInputStream(content.toByteArray()),
          FSManager.instance().getAbsolutePath(atomFeedRelativePath, Accept.ATOM));

      return xml.createResponse(null, null, null, null, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.XML.toString(), e);
    }
  }

  @GET
  @Path("/Accounts({entityId})/{containedEntitySetName:.*}")
  public Response getContainedEntitySet(
      @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
      @PathParam("entityId") final String entityId,
      @PathParam("containedEntitySetName") final String containedEntitySetName,
      @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format) {

    String tempContainedESName = containedEntitySetName;
    if ("MyGiftCard".equals(tempContainedESName)) {
      return getContainedEntity(accept, entityId, tempContainedESName, null, format);
    }

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept);
      }
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      String derivedType = null;
      if (tempContainedESName.contains("/")) {
        final String[] parts = tempContainedESName.split("/");
        tempContainedESName = parts[0];
        derivedType = parts[1];
      }

      final InputStream feed = FSManager.instance().
          readFile(containedPath(entityId, tempContainedESName).toString(), Accept.ATOM);

      final ResWrap<EntityCollection> container = atomDeserializer.toEntitySet(feed);

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
        acceptType = Accept.parse(accept);
      }

      final Property property = new Property();
      property.setType("Microsoft.Test.OData.Services.ODataWCFService.Color");
      property.setValue(ValueType.ENUM, "Red");
      final ResWrap<Property> container = new ResWrap<Property>(
          URI.create(Constants.get(ConstantKey.ODATA_METADATA_PREFIX) + property.getType()), null,
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
        acceptType = Accept.parse(accept);
      }

      final Property property = new Property();
      property.setType("Collection(String)");
      final List<String> value = Arrays.asList("Cheetos", "Mushrooms", "Apple", "Car", "Computer");
      property.setValue(ValueType.COLLECTION_PRIMITIVE, value);
      final ResWrap<Property> container = new ResWrap<Property>(
          URI.create(Constants.get(ConstantKey.ODATA_METADATA_PREFIX) + property.getType()), null,
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
        acceptType = Accept.parse(accept);
      }

      final Property property = new Property();
      property.setType("Collection(Edm.String)");
      property.setValue(ValueType.COLLECTION_PRIMITIVE,
          Arrays.asList("first@olingo.apache.org", "second@olingo.apache.org"));
      final ResWrap<Property> container = new ResWrap<Property>(
          URI.create(Constants.get(ConstantKey.ODATA_METADATA_PREFIX) + property.getType()), null,
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
        acceptType = Accept.parse(accept);
      }

      final Accept contentTypeValue = Accept.parse(contentType);
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

      final InputStream feed = FSManager.instance().readFile(basePath, Accept.JSON_FULLMETA);
      return xml.createResponse(null, feed, Commons.getETag(basePath), Accept.JSON_FULLMETA);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.JSON_FULLMETA.toString(), e);
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
        acceptType = Accept.parse(accept);
      }

      final Accept contentTypeValue = Accept.parse(contentType);
      final Entity entity = xml.readEntity(contentTypeValue, IOUtils.toInputStream(param, Constants.ENCODING));

      assert "Microsoft.Test.OData.Services.ODataWCFService.Address".equals(entity.getType());
      assert entity.getProperty("address").isComplex();

      final ResWrap<Property> result = new ResWrap<Property>(
          URI.create(Constants.get(ConstantKey.ODATA_METADATA_PREFIX)
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
        acceptType = Accept.parse(accept);
      }

      final Accept contentTypeValue = Accept.parse(contentType);
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
  public Response createLinked() {
    return xml.createResponse(null, null, null, Status.NO_CONTENT);
  }

  @POST
  @Path("/Customers(1)/Orders/$ref")
  public Response linkOrderViaRef() {
    return xml.createResponse(null, null, null, Status.NO_CONTENT);
  }

  @DELETE
  @Path("/Products({productId})/Categories({categoryId})/$ref")
  public Response deleteLinked() {
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

    return getEntityInternal(
        uriInfo.getRequestUri().toASCIIString(), accept, "VipCustomer", "1", format, expand, select);
  }

  protected Map.Entry<Accept, AbstractUtilities> getUtilities(final String accept, final String format) {
    Accept acceptType;
    if (StringUtils.isNotBlank(format)) {
      try {
        acceptType = Accept.valueOf(format.toUpperCase());
      } catch (Exception e) {
        acceptType = Accept.parse(format);
      }
    } else {
      acceptType = Accept.parse(accept);
    }

    return new AbstractMap.SimpleEntry<Accept, AbstractUtilities>(acceptType, getUtilities(acceptType));
  }

  protected AbstractUtilities getUtilities(final Accept accept) {
    final AbstractUtilities utils;
    if (accept == Accept.XML || accept == Accept.TEXT || accept == Accept.ATOM) {
      utils = xml;
    } else {
      utils = json;
    }

    return utils;
  }
}
