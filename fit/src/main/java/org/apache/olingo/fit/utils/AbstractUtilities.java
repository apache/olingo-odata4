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
package org.apache.olingo.fit.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.data.AtomEntityImpl;
import org.apache.olingo.commons.core.data.AtomEntitySetImpl;
import org.apache.olingo.commons.core.data.AtomPropertyImpl;
import org.apache.olingo.commons.core.data.AtomSerializer;
import org.apache.olingo.commons.core.data.JSONEntityImpl;
import org.apache.olingo.commons.core.data.JSONEntitySetImpl;
import org.apache.olingo.commons.core.data.JSONPropertyImpl;
import org.apache.olingo.fit.UnsupportedMediaTypeException;
import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.metadata.NavigationProperty;
import org.apache.olingo.fit.serializer.FITAtomDeserializer;
import org.apache.olingo.fit.serializer.JSONEntryContainer;
import org.apache.olingo.fit.serializer.JSONFeedContainer;
import org.apache.olingo.fit.serializer.JSONPropertyContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUtilities {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractUtilities.class);

  protected static final Pattern ENTITY_URI_PATTERN = Pattern.compile(".*\\/.*\\(.*\\)");

  /**
   * Batch/Changeset content type.
   */
  public static final String MULTIPART_CONTENT_TYPE = "multipart/mixed";

  /**
   * Batch item content type.
   */
  public static final String ITEM_CONTENT_TYPE = "application/http";

  /**
   * Boundary key.
   */
  public static final String BOUNDARY = "boundary";

  protected final ODataServiceVersion version;

  protected final Metadata metadata;

  protected final FSManager fsManager;

  protected final DataBinder dataBinder;

  protected final FITAtomDeserializer atomDeserializer;

  protected final AtomSerializer atomSerializer;

  protected final ObjectMapper mapper;

  public AbstractUtilities(final ODataServiceVersion version, final Metadata metadata) throws Exception {
    this.version = version;
    this.metadata = metadata;
    this.fsManager = FSManager.instance(version);
    this.dataBinder = new DataBinder(version, metadata);
    this.atomDeserializer = Commons.getAtomDeserializer(version);
    this.atomSerializer = Commons.getAtomSerializer(version);
    this.mapper = Commons.getJSONMapper(version);
  }

  public boolean isMediaContent(final String entityName) {
    return Commons.MEDIA_CONTENT.containsKey(entityName);
  }

  /**
   * Add links to the given entity.
   *
   * @param entitySetName
   * @param entitykey
   * @param is
   * @param links links to be added.
   * @return
   * @throws IOException
   */
  protected abstract InputStream addLinks(
          final String entitySetName, final String entitykey, final InputStream is, final Set<String> links)
          throws Exception;

  /**
   * Retrieve all entity link names.
   *
   * @param is
   * @return
   * @throws IOException
   */
  protected abstract Set<String> retrieveAllLinkNames(final InputStream is) throws Exception;

  /**
   * Retrieve entity links and inlines.
   *
   * @param entitySetName
   * @param is
   * @return
   * @throws IOException
   */
  protected abstract NavigationLinks retrieveNavigationInfo(
          final String entitySetName, final InputStream is)
          throws Exception;

  /**
   * Normalize navigation info and add edit link if missing.
   *
   * @param entitySetName
   * @param entityKey
   * @param is
   * @param links
   * @return
   * @throws IOException
   */
  protected abstract InputStream normalizeLinks(
          final String entitySetName, final String entityKey, final InputStream is, final NavigationLinks links)
          throws Exception;

  public InputStream saveSingleEntity(
          final String key,
          final String entitySetName,
          final InputStream is) throws Exception {

    return saveSingleEntity(key, entitySetName, is, null);
  }

  public InputStream saveSingleEntity(
          final String key,
          final String entitySetName,
          final InputStream is,
          final NavigationLinks links) throws Exception {

    // -----------------------------------------
    // 0. Get the path
    // -----------------------------------------
    final String path =
            entitySetName + File.separatorChar + Commons.getEntityKey(key) + File.separatorChar
            + Constants.get(version, ConstantKey.ENTITY);
    // -----------------------------------------

    // -----------------------------------------
    // 1. Normalize navigation info; edit link; ...
    // -----------------------------------------
    final InputStream normalized = normalizeLinks(entitySetName, key, is, links);
    // -----------------------------------------

    // -----------------------------------------
    // 2. save the entity
    // -----------------------------------------
    final FileObject fo = fsManager.putInMemory(normalized, fsManager.getAbsolutePath(path, getDefaultFormat()));
    // -----------------------------------------

    return fo.getContent().getInputStream();
  }

  private InputStream toInputStream(final AtomEntityImpl entry) throws XMLStreamException {
    final StringWriter writer = new StringWriter();
    atomSerializer.write(writer, entry);

    return IOUtils.toInputStream(writer.toString(), Constants.ENCODING);
  }

  public InputStream addOrReplaceEntity(
          final String key,
          final String entitySetName,
          final InputStream is,
          final AtomEntityImpl entry) throws Exception {

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    IOUtils.copy(is, bos);
    IOUtils.closeQuietly(is);

    final Map<String, NavigationProperty> navigationProperties = metadata.getNavigationProperties(entitySetName);

    // -----------------------------------------
    // 0. Retrieve navigation links to be kept
    // -----------------------------------------
    Set<String> linksToBeKept;
    try {
      linksToBeKept = new HashSet<String>(navigationProperties.keySet());
    } catch (Exception e) {
      linksToBeKept = Collections.<String>emptySet();
    }

    for (String availableLink : new HashSet<String>(linksToBeKept)) {
      try {
        fsManager.resolve(Commons.getLinksPath(version, entitySetName, key, availableLink, Accept.JSON_FULLMETA));
      } catch (Exception e) {
        linksToBeKept.remove(availableLink);
      }
    }

    for (String linkName : retrieveAllLinkNames(new ByteArrayInputStream(bos.toByteArray()))) {
      linksToBeKept.remove(linkName);
    }
    // -----------------------------------------

    // -----------------------------------------
    // 1. Get default entry key and path (N.B. operation will consume/close the stream; use a copy instead)
    // -----------------------------------------
    final String entityKey = key == null ? getDefaultEntryKey(entitySetName, entry) : key;

    final String path = Commons.getEntityBasePath(entitySetName, entityKey);
    // -----------------------------------------

    // -----------------------------------------
    // 2. Retrieve navigation info
    // -----------------------------------------
    final NavigationLinks links = retrieveNavigationInfo(entitySetName, new ByteArrayInputStream(bos.toByteArray()));
    // -----------------------------------------

    // -----------------------------------------
    // 3. Normalize navigation info; add edit link; ... and save entity ....
    // -----------------------------------------
    final InputStream createdEntity =
            saveSingleEntity(entityKey, entitySetName, new ByteArrayInputStream(bos.toByteArray()), links);
    // -----------------------------------------

    bos.reset();
    IOUtils.copy(createdEntity, bos);

    // -----------------------------------------
    // 4. Add navigation links to be kept
    // -----------------------------------------
    final InputStream normalizedEntity =
            addLinks(entitySetName, entityKey, new ByteArrayInputStream(bos.toByteArray()), linksToBeKept);
    // -----------------------------------------

    IOUtils.closeQuietly(bos);

    // -----------------------------------------
    // 5. save the entity
    // -----------------------------------------
    final FileObject fo = fsManager.putInMemory(
            normalizedEntity,
            fsManager.getAbsolutePath(path + Constants.get(version, ConstantKey.ENTITY), getDefaultFormat()));
    // -----------------------------------------

    // -----------------------------------------
    // 4. Create links file and provided inlines
    // -----------------------------------------  
    for (Map.Entry<String, List<String>> link : links.getLinks()) {
      putLinksInMemory(path, entitySetName, entityKey, link.getKey(), link.getValue());
    }

    final List<String> hrefs = new ArrayList<String>();

    for (final Link link : entry.getNavigationLinks()) {
      final NavigationProperty navProp =
              navigationProperties == null ? null : navigationProperties.get(link.getTitle());
      if (navProp != null) {
        final String inlineEntitySetName = navProp.getTarget();
        if (link.getInlineEntity() != null) {
          final String inlineEntryKey = getDefaultEntryKey(
                  inlineEntitySetName, (AtomEntityImpl) link.getInlineEntity());

          addOrReplaceEntity(
                  inlineEntryKey,
                  inlineEntitySetName,
                  toInputStream((AtomEntityImpl) link.getInlineEntity()),
                  (AtomEntityImpl) link.getInlineEntity());

          hrefs.add(inlineEntitySetName + "(" + inlineEntryKey + ")");
        } else if (link.getInlineEntitySet() != null) {
          for (Entity subentry : link.getInlineEntitySet().getEntities()) {
            final String inlineEntryKey = getDefaultEntryKey(
                    inlineEntitySetName, (AtomEntityImpl) subentry);

            addOrReplaceEntity(
                    inlineEntryKey,
                    inlineEntitySetName,
                    toInputStream((AtomEntityImpl) subentry),
                    (AtomEntityImpl) subentry);

            hrefs.add(inlineEntitySetName + "(" + inlineEntryKey + ")");
          }
        }

        if (!hrefs.isEmpty()) {
          putLinksInMemory(path, entitySetName, entityKey, link.getTitle(), hrefs);
        }
      }
    }
    // -----------------------------------------

    return fo.getContent().getInputStream();
  }

  public void addMediaEntityValue(
          final String entitySetName,
          final String entityKey,
          final InputStream is) throws Exception {

    // -----------------------------------------
    // 0. Get default entry key and path (N.B. operation will consume/close the stream; use a copy instead)
    // -----------------------------------------
    final String path = Commons.getEntityBasePath(entitySetName, entityKey);
    // -----------------------------------------

    // -----------------------------------------
    // 1. save the media entity value
    // -----------------------------------------
    fsManager.putInMemory(is, fsManager.getAbsolutePath(path
            + Constants.get(version, ConstantKey.MEDIA_CONTENT_FILENAME), null));
    IOUtils.closeQuietly(is);
    // -----------------------------------------
  }

  public void putLinksInMemory(
          final String basePath,
          final String entitySetName,
          final String entityKey,
          final String linkName,
          final Collection<String> links) throws IOException {

    final HashSet<String> uris = new HashSet<String>();

    final Map<String, NavigationProperty> navigationProperties = metadata.getNavigationProperties(entitySetName);

    if (navigationProperties.get(linkName).isEntitySet()) {
      try {
        final Map.Entry<String, List<String>> currents = extractLinkURIs(entitySetName, entityKey, linkName);
        uris.addAll(currents.getValue());
      } catch (Exception ignore) {
      }
    }

    uris.addAll(links);

    putLinksInMemory(basePath, entitySetName, linkName, uris);
  }

  public void putLinksInMemory(
          final String basePath, final String entitySetName, final String linkName, final Collection<String> uris)
          throws IOException {

    fsManager.putInMemory(
            Commons.getLinksAsJSON(version, entitySetName, new SimpleEntry<String, Collection<String>>(linkName, uris)),
            Commons.getLinksPath(version, basePath, linkName, Accept.JSON_FULLMETA));

    fsManager.putInMemory(
            Commons.getLinksAsATOM(version, new SimpleEntry<String, Collection<String>>(linkName, uris)),
            Commons.getLinksPath(version, basePath, linkName, Accept.XML));
  }

  public Response createResponse(
          final String location, final InputStream entity, final String etag, final Accept accept) {
    return createResponse(location, entity, etag, accept, null);
  }

  public Response createAsyncResponse(final String location) {
    final Response.ResponseBuilder builder = Response.accepted();
    if (version.compareTo(ODataServiceVersion.V30) <= 0) {
      builder.header(Constants.get(version, ConstantKey.ODATA_SERVICE_VERSION), version.toString() + ";");
    }

    builder.header("Location", location);
    builder.header("Preference-Applied", "Respond-Async");
    builder.header("Retry-After", "10");

    return builder.build();
  }

  public Response createMonitorResponse(final InputStream res) {
    final Response.ResponseBuilder builder = Response.ok();
    if (version.compareTo(ODataServiceVersion.V30) <= 0) {
      builder.header(Constants.get(version, ConstantKey.ODATA_SERVICE_VERSION), version.toString() + ";");
    }

    builder.header("Content-Type", "application/http");
    builder.header("Content-Transfer-Encoding", "binary");

    return builder.entity(res).build();
  }

  public Response createResponse(final InputStream entity, final String etag, final Accept accept) {
    return createResponse(null, entity, etag, accept, null);
  }

  public Response createBatchResponse(final InputStream stream, final String boundary) {
    final Response.ResponseBuilder builder = version.compareTo(ODataServiceVersion.V30) <= 0
            ? Response.accepted(stream)
            : Response.ok(stream);
    builder.header(Constants.get(version, ConstantKey.ODATA_SERVICE_VERSION), version.toString() + ";");
    return builder.build();
  }

  public Response createResponse(
          final InputStream entity,
          final String etag,
          final Accept accept,
          final Response.Status status) {
    return createResponse(null, entity, etag, accept, status);
  }

  public Response createResponse(
          final String location,
          final InputStream entity,
          final String etag,
          final Accept accept,
          final Response.Status status) {

    final Response.ResponseBuilder builder = Response.ok();
    if (version.compareTo(ODataServiceVersion.V30) <= 0) {
      builder.header(Constants.get(version, ConstantKey.ODATA_SERVICE_VERSION), version.toString() + ";");
    }

    if (StringUtils.isNotBlank(etag)) {
      builder.header("ETag", etag);
    }

    if (status != null) {
      builder.status(status);
    }

    int contentLength = 0;

    String contentTypeEncoding = StringUtils.EMPTY;

    if (entity != null) {
      try {
        final InputStream toBeStreamedBack;

        if (Accept.JSON == accept || Accept.JSON_NOMETA == accept) {
          toBeStreamedBack = Commons.changeFormat(entity, version, accept);
        } else {
          toBeStreamedBack = entity;
        }

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(toBeStreamedBack, bos);
        IOUtils.closeQuietly(toBeStreamedBack);

        contentLength = bos.size();
        builder.entity(new ByteArrayInputStream(bos.toByteArray()));

        contentTypeEncoding = ";odata.streaming=true;charset=utf-8";
      } catch (IOException ioe) {
        LOG.error("Error streaming response entity back", ioe);
      }
    }

    builder.header("Content-Length", contentLength);
    builder.header("Content-Type", (accept == null ? "*/*" : accept.toString(version)) + contentTypeEncoding);

    if (StringUtils.isNotBlank(location)) {
      builder.header("Location", location);
    }

    return builder.build();
  }

  public Response createFaultResponse(final String accept, final Exception e) {
    LOG.debug("Create fault response about .... ", e);

    final Response.ResponseBuilder builder = Response.serverError();
    if (version.compareTo(ODataServiceVersion.V30) <= 0) {
      builder.header(Constants.get(version, ConstantKey.ODATA_SERVICE_VERSION), version + ";");
    }

    final String ext;
    final Accept contentType;
    if (accept.startsWith("application/json")) {
      ext = ".json";
      contentType = Accept.JSON;
    } else if (accept.startsWith("application/xml") || version.compareTo(ODataServiceVersion.V30) <= 0) {
      ext = ".xml";
      contentType = Accept.XML;
    } else {
      ext = ".json";
      contentType = Accept.JSON;
    }

    builder.header("Content-Type", contentType);

    final InputStream src;
    if (e instanceof ConcurrentModificationException) {
      builder.status(Response.Status.PRECONDITION_FAILED);
      src = fsManager.readFile("/badRequest" + ext, null);
    } else if (e instanceof UnsupportedMediaTypeException) {
      builder.status(Response.Status.UNSUPPORTED_MEDIA_TYPE);
      src = fsManager.readFile("/unsupportedMediaType" + ext, null);
    } else if (e instanceof NotFoundException) {
      builder.status(Response.Status.NOT_FOUND);
      src = fsManager.readFile("/notFound" + ext, null);
    } else {
      builder.status(Response.Status.BAD_REQUEST);
      src = fsManager.readFile("/badRequest" + ext, null);
    }

    builder.entity(src);
    return builder.build();
  }

  public AtomEntitySetImpl readEntitySet(final Accept accept, final InputStream entitySet)
          throws XMLStreamException, IOException {

    final AtomEntitySetImpl entry;

    if (accept == Accept.ATOM || accept == Accept.XML) {
      final ResWrap<AtomEntitySetImpl> container = atomDeserializer.read(entitySet, AtomEntitySetImpl.class);
      entry = container.getPayload();
    } else {
      final ResWrap<JSONEntitySetImpl> container =
              mapper.readValue(entitySet, new TypeReference<JSONEntitySetImpl>() {
              });
      entry = dataBinder.toAtomEntitySet(container.getPayload());
    }

    return entry;
  }

  public InputStream writeEntitySet(final Accept accept, final ResWrap<AtomEntitySetImpl> container)
          throws XMLStreamException, IOException {

    final StringWriter writer = new StringWriter();
    if (accept == Accept.ATOM || accept == Accept.XML) {
      atomSerializer.write(writer, container);
      writer.flush();
      writer.close();
    } else {
      mapper.writeValue(
              writer, new JSONFeedContainer(container.getContextURL(),
                      container.getMetadataETag(), dataBinder.toJSONEntitySet(container.getPayload())));
    }

    return IOUtils.toInputStream(writer.toString(), Constants.ENCODING);
  }

  public ResWrap<AtomEntityImpl> readContainerEntity(final Accept accept, final InputStream entity)
          throws XMLStreamException, IOException {
    final ResWrap<AtomEntityImpl> container;

    if (accept == Accept.ATOM || accept == Accept.XML) {
      container = atomDeserializer.read(entity, AtomEntityImpl.class);
    } else {
      final ResWrap<JSONEntityImpl> jcontainer =
              mapper.readValue(entity, new TypeReference<JSONEntityImpl>() {
              });
      container = new ResWrap<AtomEntityImpl>(
              jcontainer.getContextURL(),
              jcontainer.getMetadataETag(),
              dataBinder.toAtomEntity(jcontainer.getPayload()));
    }

    return container;
  }

  public AtomEntityImpl readEntity(final Accept accept, final InputStream entity)
          throws XMLStreamException, IOException {

    return readContainerEntity(accept, entity).getPayload();
  }

  public InputStream writeEntity(final Accept accept, final ResWrap<AtomEntityImpl> container)
          throws XMLStreamException, IOException {

    final StringWriter writer = new StringWriter();
    if (accept == Accept.ATOM || accept == Accept.XML) {
      atomSerializer.write(writer, container);
    } else {
      mapper.writeValue(
              writer, new JSONEntryContainer(container.getContextURL(), container.getMetadataETag(),
                      dataBinder.toJSONEntity(container.getPayload())));
    }

    return IOUtils.toInputStream(writer.toString(), Constants.ENCODING);
  }

  public InputStream writeProperty(final Accept accept, final Property property)
          throws XMLStreamException, IOException {

    final StringWriter writer = new StringWriter();
    if (accept == Accept.XML || accept == Accept.ATOM) {
      atomSerializer.write(writer, property instanceof AtomPropertyImpl
              ? property : dataBinder.toAtomProperty((JSONPropertyImpl) property, property.getType()));
    } else {
      mapper.writeValue(writer, property instanceof JSONPropertyImpl
              ? property : dataBinder.toJSONProperty((AtomPropertyImpl) property));
    }

    return IOUtils.toInputStream(writer.toString(), Constants.ENCODING);
  }

  public AtomPropertyImpl readProperty(final Accept accept, final InputStream property, final String entryType)
          throws XMLStreamException, IOException {
    final AtomPropertyImpl atomProperty;
    if (Accept.ATOM == accept || Accept.XML == accept) {
      final ResWrap<AtomPropertyImpl> container = atomDeserializer.read(property, AtomPropertyImpl.class);
      atomProperty = container.getPayload();
    } else {
      final ResWrap<JSONPropertyImpl> jcontainer = mapper.readValue(property,
              new TypeReference<JSONPropertyImpl>() {
              });

      atomProperty = dataBinder.toAtomProperty(jcontainer.getPayload(), entryType);
    }

    return atomProperty;
  }

  public InputStream writeProperty(final Accept accept, final ResWrap<AtomPropertyImpl> container)
          throws XMLStreamException, IOException {

    final StringWriter writer = new StringWriter();
    if (accept == Accept.XML || accept == Accept.ATOM) {
      atomSerializer.write(writer, container);
    } else {
      mapper.writeValue(
              writer, new JSONPropertyContainer(container.getContextURL(), container.getMetadataETag(),
                      dataBinder.toJSONProperty(container.getPayload())));
    }

    return IOUtils.toInputStream(writer.toString(), Constants.ENCODING);
  }

  private String getDefaultEntryKey(final String entitySetName, final AtomEntityImpl entry, final String propertyName)
          throws Exception {

    String res;
    if (entry.getProperty(propertyName) == null) {
      if (Commons.SEQUENCE.containsKey(entitySetName)) {
        res = String.valueOf(Commons.SEQUENCE.get(entitySetName) + 1);
      } else {
        throw new Exception(String.format("Unable to retrieve entity key value for %s", entitySetName));
      }
    } else {
      res = entry.getProperty(propertyName).getValue().asPrimitive().get();
    }
    Commons.SEQUENCE.put(entitySetName, Integer.valueOf(res));

    return res;
  }

  public String getDefaultEntryKey(final String entitySetName, final AtomEntityImpl entity) throws IOException {
    try {
      String res;

      if ("Message".equals(entitySetName)) {
        int messageId;
        if (entity.getProperty("MessageId") == null || entity.getProperty("FromUsername") == null) {
          if (Commons.SEQUENCE.containsKey(entitySetName)) {
            messageId = Commons.SEQUENCE.get(entitySetName) + 1;
            res = "MessageId=" + String.valueOf(messageId) + ",FromUsername=1";
          } else {
            throw new Exception(String.format("Unable to retrieve entity key value for %s", entitySetName));
          }
        } else {
          messageId = Integer.valueOf(entity.getProperty("MessageId").getValue().asPrimitive().get());
          res = "MessageId=" + entity.getProperty("MessageId").getValue().asPrimitive().get()
                  + ",FromUsername=" + entity.getProperty("FromUsername").getValue().asPrimitive().get();
        }
        Commons.SEQUENCE.put(entitySetName, messageId);
      } else if ("Order".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "OrderId");
      } else if ("Product".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "ProductId");
      } else if ("Orders".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "OrderID");
      } else if ("Customer".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "CustomerId");
      } else if ("Person".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "PersonId");
      } else if ("ComputerDetail".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "ComputerDetailId");
      } else if ("AllGeoTypesSet".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "Id");
      } else if ("CustomerInfo".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "CustomerInfoId");
      } else if ("Car".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "VIN");
      } else if ("RowIndex".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "Id");
      } else if ("Products".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "ProductID");
      } else if ("ProductDetails".equals(entitySetName)) {
        int productId;
        int productDetailId;
        if (entity.getProperty("ProductID") == null || entity.getProperty("ProductDetailID") == null) {
          if (Commons.SEQUENCE.containsKey(entitySetName) && Commons.SEQUENCE.containsKey("Products")) {
            productId = Commons.SEQUENCE.get("Products") + 1;
            productDetailId = Commons.SEQUENCE.get(entitySetName) + 1;
            res = "ProductID=" + String.valueOf(productId) + ",ProductDetailID=" + String.valueOf(productDetailId);
          } else {
            throw new Exception(String.format("Unable to retrieve entity key value for %s", entitySetName));
          }
          Commons.SEQUENCE.put(entitySetName, productDetailId);
        } else {
          productId = Integer.valueOf(entity.getProperty("ProductID").getValue().asPrimitive().get());
          productDetailId = Integer.valueOf(entity.getProperty("ProductDetailID").getValue().asPrimitive().get());
          res = "ProductID=" + entity.getProperty("ProductID").getValue().asPrimitive().get()
                  + ",ProductDetailID=" + entity.getProperty("ProductDetailID").getValue().asPrimitive().get();
        }
        Commons.SEQUENCE.put(entitySetName, productDetailId);
        Commons.SEQUENCE.put("Products", productId);
      } else if ("PaymentInstrument".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "PaymentInstrumentID");
      } else if ("Advertisements".equals(entitySetName)) {
        res = UUID.randomUUID().toString();
      } else if ("People".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entity, "PersonID");
      } else {
        throw new Exception(String.format("EntitySet '%s' not found", entitySetName));
      }

      return res;
    } catch (Exception e) {
      throw new NotFoundException(e);
    }
  }

  public String getLinksBasePath(final String entitySetName, final String entityId) {
    return entitySetName + File.separatorChar + Commons.getEntityKey(entityId) + File.separatorChar
            + Constants.get(version, ConstantKey.LINKS_FILE_PATH) + File.separatorChar;
  }

  /**
   * Retrieves entity links about the given link name.
   *
   * @param entitySetName entity set name.
   * @param entityId entity id.
   * @param linkName link name.
   * @param accept accept header.
   * @return a pair of ETag/links stream
   */
  public LinkInfo readLinks(
          final String entitySetName, final String entityId, final String linkName, final Accept accept)
          throws Exception {

    final String basePath = getLinksBasePath(entitySetName, entityId);

    final LinkInfo linkInfo = new LinkInfo(fsManager.readFile(basePath + linkName, accept));
    linkInfo.setEtag(Commons.getETag(basePath, version));
    final Map<String, NavigationProperty> navigationProperties = metadata.getNavigationProperties(entitySetName);

    linkInfo.setFeed(navigationProperties.get(linkName.replaceAll("\\(.*\\)", "")).isEntitySet());

    return linkInfo;
  }

  public InputStream putMediaInMemory(
          final String entitySetName, final String entityId, final InputStream value)
          throws IOException {
    return putMediaInMemory(entitySetName, entityId, null, value);
  }

  public InputStream putMediaInMemory(
          final String entitySetName, final String entityId, final String name, final InputStream value)
          throws IOException {
    final FileObject fo = fsManager.putInMemory(value, fsManager.getAbsolutePath(
            Commons.getEntityBasePath(entitySetName, entityId)
            + (name == null ? Constants.get(version, ConstantKey.MEDIA_CONTENT_FILENAME) : name), null));

    return fo.getContent().getInputStream();
  }

  public Map.Entry<String, InputStream> readMediaEntity(final String entitySetName, final String entityId) {
    return readMediaEntity(entitySetName, entityId, null);
  }

  public Map.Entry<String, InputStream> readMediaEntity(
          final String entitySetName, final String entityId, final String name) {
    final String basePath = Commons.getEntityBasePath(entitySetName, entityId);
    return new SimpleEntry<String, InputStream>(basePath, fsManager.readFile(basePath
            + (name == null ? Constants.get(version, ConstantKey.MEDIA_CONTENT_FILENAME) : name)));
  }

  public Map.Entry<String, InputStream> readEntity(
          final String entitySetName, final String entityId, final Accept accept) {

    if (accept == Accept.XML || accept == Accept.TEXT) {
      throw new UnsupportedMediaTypeException("Unsupported media type");
    }

    final String basePath = Commons.getEntityBasePath(entitySetName, entityId);
    return new SimpleEntry<String, InputStream>(basePath,
            fsManager.readFile(basePath + Constants.get(version, ConstantKey.ENTITY), accept));
  }

  public InputStream expandEntity(
          final String entitySetName,
          final String entityId,
          final String linkName)
          throws Exception {

    // --------------------------------
    // 0. Retrieve all 'linkName' navigation link uris (NotFoundException if missing) 
    // --------------------------------
    final Map.Entry<String, List<String>> links = extractLinkURIs(entitySetName, entityId, linkName);
    // --------------------------------

    // --------------------------------
    // 1. Retrieve expanded object (entry or feed)
    // --------------------------------
    final Map<String, NavigationProperty> navigationProperties = metadata.getNavigationProperties(entitySetName);

    return readEntities(
            links.getValue(),
            linkName,
            links.getKey(),
            navigationProperties.get(linkName).isEntitySet());
  }

  public InputStream expandEntity(
          final String entitySetName,
          final String entityId,
          final InputStream entity,
          final String linkName)
          throws Exception {
    // --------------------------------
    // 2. Retrieve expanded object (entry or feed)
    // --------------------------------
    return replaceLink(entity, linkName, expandEntity(entitySetName, entityId, linkName));
    // --------------------------------
  }

  public InputStream deleteProperty(
          final String entitySetName,
          final String entityId,
          final List<String> path,
          final Accept accept) throws Exception {
    final String basePath = Commons.getEntityBasePath(entitySetName, entityId);

    final Accept acceptType = accept == null || Accept.TEXT == accept
            ? Accept.XML : accept.getExtension().equals(Accept.JSON.getExtension()) ? Accept.JSON_FULLMETA : accept;

    // read atom
    InputStream stream = fsManager.readFile(basePath + Constants.get(version, ConstantKey.ENTITY), acceptType);

    // change atom
    stream = deleteProperty(stream, path);

    // save atom
    fsManager.putInMemory(stream,
            fsManager.getAbsolutePath(basePath + Constants.get(version, ConstantKey.ENTITY), acceptType));

    return fsManager.readFile(basePath + Constants.get(version, ConstantKey.ENTITY), acceptType);
  }

  public abstract InputStream readEntities(
          final List<String> links, final String linkName, final String next, final boolean forceFeed)
          throws Exception;

  protected abstract InputStream replaceLink(
          final InputStream toBeChanged, final String linkName, final InputStream replacement) throws Exception;

  public abstract InputStream selectEntity(final InputStream entity, final String[] propertyNames) throws Exception;

  protected abstract Accept getDefaultFormat();

  protected abstract Map<String, InputStream> getChanges(final InputStream src) throws Exception;

  public abstract InputStream addEditLink(
          final InputStream content, final String title, final String href) throws Exception;

  public abstract InputStream addOperation(
          final InputStream content, final String name, final String metaAnchor, final String href) throws Exception;

  protected abstract InputStream replaceProperty(
          final InputStream src, final InputStream replacement, final List<String> path, final boolean justValue)
          throws Exception;

  protected abstract InputStream deleteProperty(final InputStream src, final List<String> path) throws Exception;

  public abstract Map.Entry<String, List<String>> extractLinkURIs(final InputStream is) throws Exception;

  public abstract Map.Entry<String, List<String>> extractLinkURIs(
          final String entitySetName, final String entityId, final String linkName) throws Exception;
}
