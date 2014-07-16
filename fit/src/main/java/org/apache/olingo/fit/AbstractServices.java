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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.LinkedComplexValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.Valuable;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.serialization.ODataDeserializer;
import org.apache.olingo.commons.api.serialization.ODataSerializer;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.LinkImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.commons.core.serialization.AtomSerializer;
import org.apache.olingo.commons.core.serialization.JsonDeserializer;
import org.apache.olingo.commons.core.serialization.JsonSerializer;
import org.apache.olingo.fit.metadata.EntityType;
import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.metadata.NavigationProperty;
import org.apache.olingo.fit.methods.MERGE;
import org.apache.olingo.fit.methods.PATCH;
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

import javax.mail.Header;
import javax.mail.internet.MimeBodyPart;
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
import javax.ws.rs.core.UriInfo;
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

public abstract class AbstractServices {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractServices.class);

  private static final Pattern REQUEST_PATTERN = Pattern.compile("(.*) (http://.*) HTTP/.*");

  private static final Pattern BATCH_REQUEST_REF_PATTERN = Pattern.compile("(.*) ([$]\\d+)(.*) HTTP/.*");

  private static final Pattern REF_PATTERN = Pattern.compile("([$]\\d+)");

  protected static final String BOUNDARY = "batch_243234_25424_ef_892u748";

  protected static final String MULTIPART_MIXED = "multipart/mixed";

  protected static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

  protected final ODataServiceVersion version;

  protected final Metadata metadata;

  protected final ODataDeserializer atomDeserializer;

  protected final ODataDeserializer jsonDeserializer;

  protected final ODataSerializer atomSerializer;

  protected final ODataSerializer jsonSerializer;

  protected final XMLUtilities xml;

  protected final JSONUtilities json;

  public AbstractServices(final ODataServiceVersion version, final Metadata metadata) throws IOException {
    this.version = version;
    this.metadata = metadata;

    atomDeserializer = new FITAtomDeserializer(version);
    jsonDeserializer = new JsonDeserializer(version, true);
    atomSerializer = new AtomSerializer(version, true);
    jsonSerializer = new JsonSerializer(version, true);

    xml = new XMLUtilities(version, metadata);
    json = new JSONUtilities(version, metadata);
  }

  /**
   * Provide sample services.
   *
   * @param accept Accept header.
   * @return OData services.
   */
  @GET
  public Response getSevices(@HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept) {
    try {
      final Accept acceptType = Accept.parse(accept, version);

      if (acceptType == Accept.ATOM) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      return xml.createResponse(
              null,
              FSManager.instance(version).readFile(Constants.get(version, ConstantKey.SERVICES), acceptType),
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
    return getMetadata(Constants.get(version, ConstantKey.METADATA));
  }

  protected Response getMetadata(final String filename) {
    try {
      return xml.createResponse(null, FSManager.instance(version).readRes(filename, Accept.XML), null, Accept.XML);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.XML.toString(version), e);
    }
  }

  @POST
  @Path("/$batch")
  @Consumes(MULTIPART_MIXED)
  @Produces(APPLICATION_OCTET_STREAM + ";boundary=" + BOUNDARY)
  public Response batch(
          @HeaderParam("Authorization") @DefaultValue(StringUtils.EMPTY) final String authorization,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
          final @Multipart MultipartBody attachment) {
    try {
      final boolean continueOnError = prefer.contains("odata.continue-on-error");
      return xml.createBatchResponse(
              exploreMultipart(attachment.getAllAttachments(), BOUNDARY, continueOnError));
    } catch (IOException e) {
      return xml.createFaultResponse(Accept.XML.toString(version), e);
    }
  }

  // ----------------------------------------------
  // just for non nullable property test into PropertyTestITCase
  // ----------------------------------------------
  @PATCH
  @Path("/Driver('2')")
  public Response patchDriver() {
    return xml.createFaultResponse(Accept.JSON_FULLMETA.toString(version), new Exception("Non nullable properties"));
  }

  @GET
  @Path("/StoredPIs(1000)")
  public Response getStoredPI(@Context final UriInfo uriInfo) {
    final Entity entity = new EntityImpl();
    entity.setType("Microsoft.Test.OData.Services.ODataWCFService.StoredPI");
    final Property id = new PropertyImpl();
    id.setType("Edm.Int32");
    id.setName("StoredPIID");
    id.setValue(ValueType.PRIMITIVE, 1000);
    entity.getProperties().add(id);
    final Link edit = new LinkImpl();
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
      return xml.createFaultResponse(Accept.JSON_FULLMETA.toString(version), e);
    }
  }

  @PATCH
  @Path("/StoredPIs(1000)")
  public Response patchStoredPI() {
    // just for non nullable property test into PropertyTestITCase
    return xml.createFaultResponse(Accept.JSON_FULLMETA.toString(version), new Exception("Non nullable properties"));
  }
  // ----------------------------------------------
  protected Response bodyPartRequest(final MimeBodyPart body) throws Exception {
    return bodyPartRequest(body, Collections.<String, String>emptyMap());
  }

  protected Response bodyPartRequest(final MimeBodyPart body, final Map<String, String> references) throws Exception {
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
      //client.close();
    }

    return res;
  }

  protected abstract InputStream exploreMultipart(
          final List<Attachment> attachments, final String boundary, final boolean continueOnError)
          throws IOException;

  protected void addItemIntro(final ByteArrayOutputStream bos) throws IOException {
    addItemIntro(bos, null);
  }

  protected void addItemIntro(final ByteArrayOutputStream bos, final String contentId) throws IOException {
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

  protected void addChangesetItemIntro(
          final ByteArrayOutputStream bos, final String contentId, final String cboundary) throws IOException {
    bos.write(("--" + cboundary).getBytes());
    bos.write(Constants.CRLF);
    bos.write(("Content-ID: " + contentId).getBytes());
    bos.write(Constants.CRLF);
    addItemIntro(bos);
  }

  protected void addSingleBatchResponse(
          final Response response, final ByteArrayOutputStream bos) throws IOException {
    addSingleBatchResponse(response, null, bos);
  }

  protected void addSingleBatchResponse(
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
    addSingleBatchResponse(xml.createFaultResponse(Accept.XML.toString(version), e), contentId, bos);
  }

  @MERGE
  @Path("/{entitySetName}({entityId})")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  public Response mergeEntity(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
          @HeaderParam("If-Match") @DefaultValue(StringUtils.EMPTY) final String ifMatch,
          @PathParam("entitySetName") final String entitySetName,
          @PathParam("entityId") final String entityId,
          final String changes) {

    return patchEntity(uriInfo, accept, contentType, prefer, ifMatch, entitySetName, entityId, changes);
  }

  @PATCH
  @Path("/{entitySetName}({entityId})")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  public Response patchEntity(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
          @HeaderParam("If-Match") @DefaultValue(StringUtils.EMPTY) final String ifMatch,
          @PathParam("entitySetName") final String entitySetName,
          @PathParam("entityId") final String entityId,
          final String changes) {

    try {
      final Accept acceptType = Accept.parse(accept, version);

      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final Map.Entry<String, InputStream> entityInfo = xml.readEntity(entitySetName, entityId, Accept.ATOM);

      final String etag = Commons.getETag(entityInfo.getKey(), version);
      if (StringUtils.isNotBlank(ifMatch) && !ifMatch.equals(etag)) {
        throw new ConcurrentModificationException("Concurrent modification");
      }

      final Accept contentTypeValue = Accept.parse(contentType, version);

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
      FSManager.instance(version).putInMemory(
              cres, path + File.separatorChar + Constants.get(version, ConstantKey.ENTITY));

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
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @PUT
  @Path("/{entitySetName}({entityId})")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  public Response replaceEntity(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
          @PathParam("entitySetName") final String entitySetName,
          @PathParam("entityId") final String entityId,
          final String entity) {

    try {
      final Accept acceptType = Accept.parse(accept, version);

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
      FSManager.instance(version).putInMemory(
              cres, path + File.separatorChar + Constants.get(version, ConstantKey.ENTITY));

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
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/{entitySetName}")
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM})
  public Response postNewEntity(
          @Context final UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) final String contentType,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
          @PathParam("entitySetName") final String entitySetName,
          final String entity) {

    try {
      final Accept acceptType = Accept.parse(accept, version);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final ResWrap<Entity> container;

      final org.apache.olingo.fit.metadata.EntitySet entitySet = metadata.getEntitySet(entitySetName);

      final Entity entry;
      final String entityKey;
      if (xml.isMediaContent(entitySetName)) {
        entry = new EntityImpl();
        entry.setMediaContentType(ContentType.APPLICATION_OCTET_STREAM.toContentTypeString());
        entry.setType(entitySet.getType());

        entityKey = xml.getDefaultEntryKey(entitySetName, entry);

        xml.addMediaEntityValue(entitySetName, entityKey, IOUtils.toInputStream(entity, Constants.ENCODING));

        final Pair<String, EdmPrimitiveTypeKind> id = Commons.getMediaContent().get(entitySetName);
        if (id != null) {
          final Property prop = new PropertyImpl();
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

        final Link editLink = new LinkImpl();
        editLink.setHref(Commons.getEntityURI(entitySetName, entityKey));
        editLink.setRel("edit");
        editLink.setTitle(entitySetName);
        entry.setEditLink(editLink);

        entry.setMediaContentSource(URI.create(editLink.getHref() + "/$value"));

        container = new ResWrap<Entity>((URI) null, null, entry);
      } else {
        final Accept contentTypeValue = Accept.parse(contentType, version);
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
              URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX)
                      + entitySetName + Constants.get(version, ConstantKey.ODATA_METADATA_ENTITY_SUFFIX)),
              null, result.getPayload());

      final String path = Commons.getEntityBasePath(entitySetName, entityKey);
      FSManager.instance(version).putInMemory(result, path + Constants.get(version, ConstantKey.ENTITY));

      final String location;

      if ((this instanceof V3KeyAsSegment) || (this instanceof V4KeyAsSegment)) {
        location = uriInfo.getRequestUri().toASCIIString() + "/" + entityKey;

        final Link editLink = new LinkImpl();
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
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      LOG.error("While creating new entity", e);
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Person({entityId})/{type:.*}/Sack")
  public Response actionSack(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) final String accept,
          @PathParam("entityId") final String entityId,
          @PathParam("type") final String type,
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

      final FSManager fsManager = FSManager.instance(version);
      fsManager.putInMemory(xml.writeEntity(Accept.ATOM, container),
              fsManager.getAbsolutePath(Commons.getEntityBasePath("Person", entityId) + Constants.get(version,
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
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
          final String body) {

    final String name = "Person";
    try {
      final Accept acceptType = Accept.parse(accept, version);
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
              ? Constants.get(version, ConstantKey.ENTITY)
              : Constants.get(version, ConstantKey.FEED));

      final InputStream feed = FSManager.instance(version).readFile(path.toString(), acceptType);

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

      FSManager.instance(version).putInMemory(IOUtils.toInputStream(newContent, Constants.ENCODING),
              FSManager.instance(version).getAbsolutePath(path.toString(), acceptType));

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

      final FSManager fsManager = FSManager.instance(version);
      fsManager.putInMemory(xml.writeEntity(Accept.ATOM, container),
              fsManager.getAbsolutePath(Commons.getEntityBasePath("Product", entityId) + Constants.get(version,
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

      final FSManager fsManager = FSManager.instance(version);
      fsManager.putInMemory(xml.writeEntity(Accept.ATOM, container),
              fsManager.getAbsolutePath(Commons.getEntityBasePath("ComputerDetail", entityId) + Constants.get(version,
                              ConstantKey.ENTITY), Accept.ATOM));

      return utils.getValue().createResponse(null, null, null, utils.getKey(), Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
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
      final Accept acceptType = Accept.parse(accept, version);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final String basePath = name + File.separatorChar;
      final StringBuilder path = new StringBuilder(name).
              append(File.separatorChar).append(type).
              append(File.separatorChar);

      path.append(metadata.getEntitySet(name).isSingleton()
              ? Constants.get(version, ConstantKey.ENTITY)
              : Constants.get(version, ConstantKey.FEED));

      final InputStream feed = FSManager.instance(version).readFile(path.toString(), acceptType);
      return xml.createResponse(null, feed, Commons.getETag(basePath, version), acceptType);
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
   * @param count inlinecount query option.
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
          @QueryParam("$inlinecount") @DefaultValue(StringUtils.EMPTY) final String count,
          @QueryParam("$filter") @DefaultValue(StringUtils.EMPTY) final String filter,
          @QueryParam("$orderby") @DefaultValue(StringUtils.EMPTY) final String orderby,
          @QueryParam("$skiptoken") @DefaultValue(StringUtils.EMPTY) final String skiptoken) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final String location = uriInfo.getRequestUri().toASCIIString();
      try {
        // search for function ...
        final InputStream func = FSManager.instance(version).readFile(name, acceptType);
        return xml.createResponse(location, func, null, acceptType);
      } catch (NotFoundException e) {
        if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
          throw new UnsupportedMediaTypeException("Unsupported media type");
        }

        // search for entitySet ...
        final String basePath = name + File.separatorChar;

        final StringBuilder builder = new StringBuilder();
        builder.append(basePath);

        if (StringUtils.isNotBlank(orderby)) {
          builder.append(Constants.get(version, ConstantKey.ORDERBY)).append(File.separatorChar).
                  append(orderby).append(File.separatorChar);
        }

        if (StringUtils.isNotBlank(filter)) {
          builder.append(Constants.get(version, ConstantKey.FILTER)).append(File.separatorChar).
                  append(filter.replaceAll("/", "."));
        } else if (StringUtils.isNotBlank(skiptoken)) {
          builder.append(Constants.get(version, ConstantKey.SKIP_TOKEN)).append(File.separatorChar).
                  append(skiptoken);
        } else {
          builder.append(metadata.getEntitySet(name).isSingleton()
                  ? Constants.get(version, ConstantKey.ENTITY)
                  : Constants.get(version, ConstantKey.FEED));
        }

        final InputStream feed = FSManager.instance(version).readFile(builder.toString(), Accept.ATOM);

        final ResWrap<EntitySet> container = atomDeserializer.toEntitySet(feed);

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
                Commons.getETag(basePath, version),
                acceptType);
      }
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  protected abstract void setInlineCount(final EntitySet feed, final String count);

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
          String expand,
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
        container = new ResWrap<Entity>(URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX)
                + entitySetName + Constants.get(version, ConstantKey.ODATA_METADATA_ENTITY_SUFFIX)),
                container.getMetadataETag(), container.getPayload());
      }
      final Entity entry = container.getPayload();

      if ((this instanceof V3KeyAsSegment) || (this instanceof V4KeyAsSegment)) {
        final Link editLink = new LinkImpl();
        editLink.setRel("edit");
        editLink.setTitle(entitySetName);
        editLink.setHref(Constants.get(version, ConstantKey.DEFAULT_SERVICE_URL) + entitySetName + "/" + entityId);

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

      if (StringUtils.isNotBlank(expand)) {
        expand = StringUtils.substringBefore(expand, "(");
        final List<String> links = Arrays.asList(expand.split(","));

        final Map<Link, Link> replace = new HashMap<Link, Link>();

        for (Link link : entry.getNavigationLinks()) {
          if (links.contains(link.getTitle())) {
            // expand link
            final Link rep = new LinkImpl();
            rep.setHref(link.getHref());
            rep.setRel(link.getRel());
            rep.setTitle(link.getTitle());
            rep.setType(link.getType());
            if (link.getType().equals(Constants.get(version, ConstantKey.ATOM_LINK_ENTRY))) {
              // inline entry
              final Entity inline = atomDeserializer.toEntity(
                      xml.expandEntity(entitySetName, entityId, link.getTitle())).getPayload();
              rep.setInlineEntity(inline);
            } else if (link.getType().equals(Constants.get(version, ConstantKey.ATOM_LINK_FEED))) {
              // inline feed
              final EntitySet inline = atomDeserializer.toEntitySet(
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
              Commons.getETag(entityInfo.getKey(), version),
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
              Commons.getETag(entityInfo.getKey(), version),
              null);

    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(accept, e);
    }
  }

  @DELETE
  @Path("/{entitySetName}({entityId})")
  public Response removeEntity(
          @PathParam("entitySetName") final String entitySetName,
          @PathParam("entityId") final String entityId) {

    try {
      final String basePath = entitySetName + File.separatorChar + Commons.getEntityKey(entityId);

      FSManager.instance(version).deleteEntity(basePath);

      return xml.createResponse(null, null, null, null, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(Accept.XML.toString(version), e);
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
      final FSManager fsManager = FSManager.instance(version);

      final String basePath = Commons.getEntityBasePath(entitySetName, entityId);
      final ResWrap<Entity> container = xml.readContainerEntity(Accept.ATOM,
              fsManager.readFile(basePath + Constants.get(version, ConstantKey.ENTITY), Accept.ATOM));

      final Entity entry = container.getPayload();

      Property toBeReplaced = null;
      for (String element : path.split("/")) {
        if (toBeReplaced == null) {
          toBeReplaced = entry.getProperty(element.trim());
        } else {
          List<Property> value = toBeReplaced.asComplex();
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
                Accept.parse(contentType, version),
                IOUtils.toInputStream(changes, Constants.ENCODING));

        toBeReplaced.setValue(pchanges.getValueType(), pchanges.getValue());
      }

      fsManager.putInMemory(xml.writeEntity(Accept.ATOM, container),
              fsManager.getAbsolutePath(basePath + Constants.get(version, ConstantKey.ENTITY), Accept.ATOM));

      final Response response;
      if ("return-content".equalsIgnoreCase(prefer)) {
        response = getEntityInternal(location, accept, entitySetName, entityId, format, null, null);
      } else {
        Accept acceptType = null;
        if (StringUtils.isNotBlank(format)) {
          acceptType = Accept.valueOf(format.toUpperCase());
        } else if (StringUtils.isNotBlank(accept)) {
          acceptType = Accept.parse(accept, version, null);
        }

        response = xml.createResponse(null, null, null, acceptType, Response.Status.NO_CONTENT);
      }

      if (StringUtils.isNotBlank(prefer)) {
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
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
        acceptType = Accept.parse(accept, version, null);
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
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
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
   * @return
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
   * @return
   */
  @MERGE
  @Path("/{entitySetName}({entityId})/{path:.*}")
  public Response mergeProperty(
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

  /**
   * Replace property.
   *
   * @param accept
   * @param entitySetName
   * @param entityId
   * @param path
   * @param format
   * @param changes
   * @return
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
  @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
  @Consumes({MediaType.WILDCARD, MediaType.APPLICATION_OCTET_STREAM})
  @Path("/{entitySetName}({entityId})/$value")
  public Response replaceMediaEntity(
          @Context final UriInfo uriInfo,
          @HeaderParam("Prefer") @DefaultValue(StringUtils.EMPTY) final String prefer,
          @PathParam("entitySetName") final String entitySetName,
          @PathParam("entityId") final String entityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) final String format,
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
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;

    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(Accept.JSON.toString(version), e);
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
   * @return
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
        response.getHeaders().put("Preference-Applied", Collections.<Object>singletonList(prefer));
      }

      return response;
    } catch (Exception e) {
      LOG.error("Error retrieving entity", e);
      return xml.createFaultResponse(Accept.JSON.toString(version), e);
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
   * @return
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
        acceptType = Accept.parse(accept, version, null);
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
          acceptType = Accept.parse(accept, version, null);
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
                  Commons.getETag(basePath, version),
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
    return utils.createResponse(null, entityInfo.getValue(), Commons.getETag(entityInfo.getKey(), version), null);
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
      for (Property sub : property.asComplex()) {
        if (pathElems[1].equals(sub.getName())) {
          property = sub;
          if (pathElems.length > 2 && property.isComplex()) {
            for (Property subsub : property.asComplex()) {
              if (pathElems[2].equals(subsub.getName())) {
                property = subsub;
              }
            }
          }
        }
      }
    }

    final ResWrap<Property> container = new ResWrap<Property>(
            URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX)
            + (version.compareTo(ODataServiceVersion.V40) >= 0 ? entitySetName + "(" + entityId + ")/" + path
            : property.getType())),
            entryContainer.getMetadataETag(),
            property);

    return xml.createResponse(null,
            searchForValue ? IOUtils.toInputStream(
            container.getPayload().isNull() ? StringUtils.EMPTY : stringValue(container.getPayload()),
            Constants.ENCODING) : utils.writeProperty(acceptType, container),
            Commons.getETag(Commons.getEntityBasePath(entitySetName, entityId), version),
            acceptType);
  }

  private String stringValue(final Property property) {
    EdmPrimitiveTypeKind kind = EdmPrimitiveTypeKind.valueOfFQN(version, property.getType());
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
      final Accept acceptType = Accept.parse(accept, version, Accept.TEXT);

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

  public Map.Entry<Accept, AbstractUtilities> getUtilities(final String accept, final String format) {
    Accept acceptType;
    if (StringUtils.isNotBlank(format)) {
      try {
        acceptType = Accept.valueOf(format.toUpperCase());
      } catch (Exception e) {
        acceptType = Accept.parse(format, version);
      }
    } else {
      acceptType = Accept.parse(accept, version);
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

  protected void updateInlineEntities(final Entity entity) {
    final String type = entity.getType();
    EntityType entityType;
    Map<String, NavigationProperty> navProperties = Collections.emptyMap();
    if (type != null && type.length() > 0) {
      entityType = metadata.getEntityOrComplexType(type);
      navProperties = entityType.getNavigationPropertyMap();
    }

    for (Property property : entity.getProperties()) {
      if (navProperties.containsKey(property.getName())) {
        Link alink = new LinkImpl();
        alink.setTitle(property.getName());
        alink.getAnnotations().addAll(property.getAnnotations());

        alink.setType(navProperties.get(property.getName()).isEntitySet()
                ? Constants.get(version, ConstantKey.ATOM_LINK_FEED)
                : Constants.get(version, ConstantKey.ATOM_LINK_ENTRY));

        alink.setRel(Constants.get(version, ConstantKey.ATOM_LINK_REL) + property.getName());

        if (property.isComplex()) {
          Entity inline = new EntityImpl();
          inline.setType(navProperties.get(property.getName()).getType());
          for (Property prop : property.asComplex()) {
            inline.getProperties().add(prop);
          }
          alink.setInlineEntity(inline);

        } else if (property.isCollection()) {
          EntitySet inline = new EntitySetImpl();
          for (Object value : property.asCollection()) {
            Entity inlineEntity = new EntityImpl();
            inlineEntity.setType(navProperties.get(property.getName()).getType());
            for (Property prop : (value instanceof LinkedComplexValue ? ((LinkedComplexValue) value).getValue()
                    : ((Valuable) value).asComplex())) {
              inlineEntity.getProperties().add(prop);
            }
            inline.getEntities().add(inlineEntity);
          }
          alink.setInlineEntitySet(inline);
        } else {
          throw new IllegalStateException("Invalid navigation property " + property);
        }
        entity.getNavigationLinks().add(alink);
      }
    }
  }

  protected void normalizeAtomEntry(final Entity entry, final String entitySetName, final String entityKey) {
    final org.apache.olingo.fit.metadata.EntitySet entitySet = metadata.getEntitySet(entitySetName);
    final EntityType entityType = metadata.getEntityOrComplexType(entitySet.getType());
    for (Map.Entry<String, org.apache.olingo.fit.metadata.Property> property : entityType.getPropertyMap().entrySet()) {
      if (entry.getProperty(property.getKey()) == null && property.getValue().isNullable()) {
        final PropertyImpl prop = new PropertyImpl();
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
        final LinkImpl link = new LinkImpl();
        link.setTitle(property.getKey());
        link.setType(property.getValue().isEntitySet()
                ? Constants.get(version, ConstantKey.ATOM_LINK_FEED)
                : Constants.get(version, ConstantKey.ATOM_LINK_ENTRY));
        link.setRel(Constants.get(version, ConstantKey.ATOM_LINK_REL) + property.getKey());
        link.setHref(entitySetName + "(" + entityKey + ")/" + property.getKey());
        entry.getNavigationLinks().add(link);
      }
    }
  }
}
