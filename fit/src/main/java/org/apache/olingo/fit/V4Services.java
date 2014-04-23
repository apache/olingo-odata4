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

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.olingo.commons.api.data.CollectionValue;
import org.apache.olingo.commons.api.data.Container;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.data.AtomEntryImpl;
import org.apache.olingo.commons.core.data.AtomFeedImpl;
import org.apache.olingo.commons.core.data.AtomPropertyImpl;
import org.apache.olingo.commons.core.data.CollectionValueImpl;
import org.apache.olingo.commons.core.data.EnumValueImpl;
import org.apache.olingo.commons.core.data.JSONEntryImpl;
import org.apache.olingo.commons.core.data.JSONFeedImpl;
import org.apache.olingo.commons.core.data.JSONPropertyImpl;
import org.apache.olingo.commons.core.data.PrimitiveValueImpl;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.fit.methods.PATCH;
import org.apache.olingo.fit.serializer.JsonFeedContainer;
import org.apache.olingo.fit.serializer.JsonPropertyContainer;
import org.apache.olingo.fit.utils.AbstractUtilities;
import org.apache.olingo.fit.utils.Accept;
import org.apache.olingo.fit.utils.ConstantKey;
import org.apache.olingo.fit.utils.Constants;
import org.apache.olingo.fit.utils.FSManager;
import org.apache.olingo.fit.utils.LinkInfo;
import org.apache.olingo.fit.utils.ResolvingReferencesInterceptor;
import org.apache.olingo.fit.utils.XHTTPMethodInterceptor;
import org.springframework.stereotype.Service;

@Service
@Path("/V40/Static.svc")
@InInterceptors(classes = {XHTTPMethodInterceptor.class, ResolvingReferencesInterceptor.class})
public class V4Services extends AbstractServices {

  public V4Services() throws Exception {
    super(ODataServiceVersion.V40);
  }

  @Override
  protected void setInlineCount(final Feed feed, final String count) {
    if ("true".equals(count)) {
      feed.setCount(feed.getEntries().size());
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
              if (res.getStatus() >= 400) {
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
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @PathParam("path") String path,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

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
                    accept, entitySetName, entityId, accept, StringUtils.EMPTY, StringUtils.EMPTY, false);
    return response.getStatus() >= 400
            ? postNewEntity(uriInfo, accept, contentType, prefer, entitySetName, changes)
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
              accept, entitySetName, entityId, accept, StringUtils.EMPTY, StringUtils.EMPTY, false);
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
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @PathParam("entityId") String entityId,
          @PathParam("containedEntitySetName") String containedEntitySetName,
          @PathParam("containedEntityId") String containedEntityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

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

      final InputStream entry = FSManager.instance(version).
              readFile(containedPath(entityId, containedEntitySetName).
                      append('(').append(containedEntityId).append(')').toString(), Accept.ATOM);

      final Container<AtomEntryImpl> container = atomDeserializer.read(entry, AtomEntryImpl.class);

      return xml.createResponse(
              null,
              xml.writeEntry(acceptType, container),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Accounts({entityId})/{containedEntitySetName:.*}")
  public Response postContainedEntity(
          @Context UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entityId") String entityId,
          @PathParam("containedEntitySetName") String containedEntitySetName,
          final String entity) {

    // default
    try {
      final Accept acceptType = Accept.parse(accept, version);
      if (acceptType == Accept.XML || acceptType == Accept.TEXT) {
        throw new UnsupportedMediaTypeException("Unsupported media type");
      }

      final AbstractUtilities utils = getUtilities(acceptType);

      // 1. parse the entry (from Atom or JSON) into AtomEntryImpl
      final Container<AtomEntryImpl> entryContainer;
      final AtomEntryImpl entry;
      final Accept contentTypeValue = Accept.parse(contentType, version);
      if (Accept.ATOM == contentTypeValue) {
        entryContainer = atomDeserializer.read(IOUtils.toInputStream(entity), AtomEntryImpl.class);
        entry = entryContainer.getObject();
      } else {
        final Container<JSONEntryImpl> jcontainer =
                mapper.readValue(IOUtils.toInputStream(entity), new TypeReference<JSONEntryImpl>() {
                });

        entry = dataBinder.getAtomEntry(jcontainer.getObject());

        entryContainer = new Container<AtomEntryImpl>(
                jcontainer.getContextURL(),
                jcontainer.getMetadataETag(),
                entry);
      }

      final EdmTypeInfo contained = new EdmTypeInfo.Builder().setTypeExpression(getMetadataObj().
              getNavigationProperties("Accounts").get(containedEntitySetName).getType()).build();
      final String entityKey = getUtilities(contentTypeValue).
              getDefaultEntryKey(contained.getFullQualifiedName().getName(), entry);

      // 2. Store the new entity
      final String atomEntryRelativePath = containedPath(entityId, containedEntitySetName).
              append('(').append(entityKey).append(')').toString();
      FSManager.instance(version).putInMemory(
              utils.writeEntry(Accept.ATOM, entryContainer),
              FSManager.instance(version).getAbsolutePath(atomEntryRelativePath, Accept.ATOM));

      // 3. Update the contained entity set
      final String atomFeedRelativePath = containedPath(entityId, containedEntitySetName).toString();
      final InputStream feedIS = FSManager.instance(version).readFile(atomFeedRelativePath, Accept.ATOM);
      final Container<AtomFeedImpl> feedContainer = atomDeserializer.read(feedIS, AtomFeedImpl.class);
      feedContainer.getObject().getEntries().add(entry);

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
              utils.writeEntry(acceptType, entryContainer),
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
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @PathParam("entitySetName") String entitySetName,
          @PathParam("entityId") String entityId,
          @PathParam("containedEntitySetName") String containedEntitySetName,
          @PathParam("containedEntityId") String containedEntityId,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
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

      Container<AtomEntryImpl> container = atomDeserializer.read(links.getLinks(), AtomEntryImpl.class);
      final AtomEntryImpl original = container.getObject();

      final AtomEntryImpl entryChanges;
      if (Accept.ATOM == contentTypeValue) {
        container = atomDeserializer.read(IOUtils.toInputStream(changes), AtomEntryImpl.class);
        entryChanges = container.getObject();
      } else {
        final String entityType = getMetadataObj().getEntitySet(entitySetName).getType();
        final String containedType = getMetadataObj().getEntityType(entityType).
                getNavigationProperty(containedEntitySetName).getType();
        final EdmTypeInfo typeInfo = new EdmTypeInfo.Builder().setTypeExpression(containedType).build();

        final Container<JSONEntryImpl> jsonContainer = mapper.readValue(IOUtils.toInputStream(changes),
                new TypeReference<JSONEntryImpl>() {
                });
        jsonContainer.getObject().setType(typeInfo.getFullQualifiedName().toString());
        entryChanges = dataBinder.getAtomEntry(jsonContainer.getObject());
      }

      for (Property property : entryChanges.getProperties()) {
        final Property old = original.getProperty(property.getName());
        if (old != null) {
          original.getProperties().remove(old);
        }
        original.getProperties().add(property);
      }

      FSManager.instance(version).putInMemory(new Container<AtomEntryImpl>(null, null, original),
              xml.getLinksBasePath(entitySetName, entityId) + containedEntitySetName + "(" + containedEntityId + ")");

      return xml.createResponse(null, null, acceptType, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @DELETE
  @Path("/Accounts({entityId})/{containedEntitySetName}({containedEntityId})")
  public Response removeContainedEntity(
          @PathParam("entityId") String entityId,
          @PathParam("containedEntitySetName") String containedEntitySetName,
          @PathParam("containedEntityId") String containedEntityId) {

    try {
      // 1. Fetch the contained entity to be removed
      final InputStream entry = FSManager.instance(version).
              readFile(containedPath(entityId, containedEntitySetName).
                      append('(').append(containedEntityId).append(')').toString(), Accept.ATOM);
      final Container<AtomEntryImpl> container = atomDeserializer.read(entry, AtomEntryImpl.class);

      // 2. Remove the contained entity
      final String atomEntryRelativePath = containedPath(entityId, containedEntitySetName).
              append('(').append(containedEntityId).append(')').toString();
      FSManager.instance(version).deleteFile(atomEntryRelativePath);

      // 3. Update the contained entity set
      final String atomFeedRelativePath = containedPath(entityId, containedEntitySetName).toString();
      final InputStream feedIS = FSManager.instance(version).readFile(atomFeedRelativePath, Accept.ATOM);
      final Container<AtomFeedImpl> feedContainer = atomDeserializer.read(feedIS, AtomFeedImpl.class);
      feedContainer.getObject().getEntries().remove(container.getObject());

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
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @PathParam("entityId") String entityId,
          @PathParam("containedEntitySetName") String containedEntitySetName,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

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

      final InputStream feed = FSManager.instance(version).
              readFile(containedPath(entityId, containedEntitySetName).toString(), Accept.ATOM);

      final Container<AtomFeedImpl> container = atomDeserializer.read(feed, AtomFeedImpl.class);

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);

      if (acceptType == Accept.ATOM) {
        atomSerializer.write(writer, container);
        writer.flush();
        writer.close();
      } else {
        mapper.writeValue(
                writer, new JsonFeedContainer<JSONFeedImpl>(container.getContextURL(), container.getMetadataETag(),
                        dataBinder.getJsonFeed(container.getObject())));
      }

      return xml.createResponse(
              null,
              new ByteArrayInputStream(content.toByteArray()),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/GetDefaultColor()")
  public Response functionGetDefaultColor(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final AtomPropertyImpl property = new AtomPropertyImpl();
      property.setType("Microsoft.Test.OData.Services.ODataWCFService.Color");
      property.setValue(new EnumValueImpl("Red"));
      final Container<AtomPropertyImpl> container = new Container<AtomPropertyImpl>(
              URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX) + property.getType()), null,
              property);

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);

      if (acceptType == Accept.XML) {
        atomSerializer.write(writer, container);
        writer.flush();
        writer.close();
      } else {
        mapper.writeValue(
                writer, new JsonPropertyContainer<JSONPropertyImpl>(container.getContextURL(),
                        container.getMetadataETag(), dataBinder.getJsonProperty(container.getObject())));
      }

      return xml.createResponse(
              null,
              new ByteArrayInputStream(content.toByteArray()),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/GetPerson2({param:.*})")
  public Response functionGetPerson2(
          @Context UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

    return getEntityInternal(
            uriInfo.getRequestUri().toASCIIString(), accept, "Customers", "1", format, null, null, false);
  }

  @GET
  @Path("/GetPerson({param:.*})")
  public Response functionGetPerson(
          @Context UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

    return getEntityInternal(
            uriInfo.getRequestUri().toASCIIString(), accept, "Customers", "1", format, null, null, false);
  }

  @GET
  @Path("/GetAllProducts()")
  public Response functionGetAllProducts(
          @Context UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

    return getEntitySet(uriInfo, accept, "Products", format, null, null, null, null);
  }

  @GET
  @Path("/GetProductsByAccessLevel({param:.*})")
  public Response functionGetProductsByAccessLevel(
          @Context UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final AtomPropertyImpl property = new AtomPropertyImpl();
      property.setType("Collection(String)");
      final CollectionValue value = new CollectionValueImpl();
      value.get().add(new PrimitiveValueImpl("Cheetos"));
      value.get().add(new PrimitiveValueImpl("Mushrooms"));
      value.get().add(new PrimitiveValueImpl("Apple"));
      value.get().add(new PrimitiveValueImpl("Car"));
      value.get().add(new PrimitiveValueImpl("Computer"));
      property.setValue(value);
      final Container<AtomPropertyImpl> container = new Container<AtomPropertyImpl>(
              URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX) + property.getType()), null,
              property);

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);

      if (acceptType == Accept.XML) {
        atomSerializer.write(writer, container);
        writer.flush();
        writer.close();
      } else {
        mapper.writeValue(
                writer, new JsonPropertyContainer<JSONPropertyImpl>(container.getContextURL(),
                        container.getMetadataETag(), dataBinder.getJsonProperty(container.getObject())));
      }

      return xml.createResponse(
              null,
              new ByteArrayInputStream(content.toByteArray()),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @GET
  @Path("/GetBossEmails({param:.*})")
  public Response functionGetBossEmails(
          @Context UriInfo uriInfo,
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final AtomPropertyImpl property = new AtomPropertyImpl();
      property.setType("Collection(Edm.String)");
      final CollectionValue value = new CollectionValueImpl();
      value.get().add(new PrimitiveValueImpl("first@olingo.apache.org"));
      value.get().add(new PrimitiveValueImpl("second@olingo.apache.org"));
      property.setValue(value);
      final Container<AtomPropertyImpl> container = new Container<AtomPropertyImpl>(
              URI.create(Constants.get(version, ConstantKey.ODATA_METADATA_PREFIX) + property.getType()), null,
              property);

      final ByteArrayOutputStream content = new ByteArrayOutputStream();
      final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);

      if (acceptType == Accept.XML) {
        atomSerializer.write(writer, container);
        writer.flush();
        writer.close();
      } else {
        mapper.writeValue(
                writer, new JsonPropertyContainer<JSONPropertyImpl>(container.getContextURL(),
                        container.getMetadataETag(), dataBinder.getJsonProperty(container.getObject())));
      }

      return xml.createResponse(
              null,
              new ByteArrayInputStream(content.toByteArray()),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/Discount()")
  public Response actionDiscount(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
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
        final Container<AtomPropertyImpl> paramContainer = atomDeserializer.read(
                IOUtils.toInputStream(param, Constants.ENCODING), AtomPropertyImpl.class);
        property = paramContainer.getObject();
      } else {
        final Container<JSONPropertyImpl> paramContainer =
                mapper.readValue(IOUtils.toInputStream(param, Constants.ENCODING),
                        new TypeReference<JSONPropertyImpl>() {
                        });
        property = paramContainer.getObject();
      }

      assert property.getValue().isComplex();
      assert 1 == property.getValue().asComplex().get().size();
      assert "Edm.Int32".equals(property.getValue().asComplex().get().get(0).getType());
      assert property.getValue().asComplex().get().get(0).getValue().isPrimitive();
      assert "percentage".equals(property.getValue().asComplex().get().get(0).getName());

      return xml.createResponse(null, null, null, acceptType, Response.Status.NO_CONTENT);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/ResetBossAddress()")
  public Response actionResetBossAddress(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
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
      if (contentTypeValue == Accept.XML) {
        final Container<AtomPropertyImpl> paramContainer = atomDeserializer.read(
                IOUtils.toInputStream(param, Constants.ENCODING), AtomPropertyImpl.class);
        property = paramContainer.getObject();
      } else {
        final Container<JSONPropertyImpl> paramContainer =
                mapper.readValue(IOUtils.toInputStream(param, Constants.ENCODING),
                        new TypeReference<JSONPropertyImpl>() {
                        });
        property = paramContainer.getObject();
      }

      assert "Microsoft.Test.OData.Services.ODataWCFService.Address".equals(property.getType());
      assert property.getValue().isComplex();

      return xml.createResponse(
              null,
              new ByteArrayInputStream(param.getBytes(Constants.ENCODING)),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }

  @POST
  @Path("/ResetBossEmail()")
  public Response actionResetBossEmail(
          @HeaderParam("Accept") @DefaultValue(StringUtils.EMPTY) String accept,
          @HeaderParam("Content-Type") @DefaultValue(StringUtils.EMPTY) String contentType,
          @QueryParam("$format") @DefaultValue(StringUtils.EMPTY) String format,
          final String param) {

    try {
      final Accept acceptType;
      if (StringUtils.isNotBlank(format)) {
        acceptType = Accept.valueOf(format.toUpperCase());
      } else {
        acceptType = Accept.parse(accept, version);
      }

      final Accept contentTypeValue = Accept.parse(contentType, version);
      Entry entry;
      if (contentTypeValue == Accept.XML) {
        final Container<AtomEntryImpl> paramContainer = atomDeserializer.read(
                IOUtils.toInputStream(param, Constants.ENCODING), AtomEntryImpl.class);
        entry = paramContainer.getObject();
      } else {
        final Container<JSONEntryImpl> paramContainer =
                mapper.readValue(IOUtils.toInputStream(param, Constants.ENCODING),
                        new TypeReference<JSONEntryImpl>() {
                        });
        entry = paramContainer.getObject();
      }

      assert 1 == entry.getProperties().size();
      assert "Collection(Edm.String)".equals(entry.getProperty("emails").getType());
      assert entry.getProperty("emails").getValue().isCollection();

      final StringWriter writer = new StringWriter();
      if (acceptType == Accept.XML) {
        atomSerializer.write(writer, entry.getProperty("emails"));
      } else {
        mapper.writeValue(writer, entry.getProperty("emails"));
      }

      return xml.createResponse(
              null,
              new ByteArrayInputStream(writer.toString().getBytes(Constants.ENCODING)),
              null,
              acceptType);
    } catch (Exception e) {
      return xml.createFaultResponse(accept, e);
    }
  }
}
