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
import java.io.OutputStreamWriter;
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
import java.util.regex.Pattern;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.olingo.commons.api.data.Container;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.data.AtomEntryImpl;
import org.apache.olingo.commons.core.data.JSONEntryImpl;
import org.apache.olingo.fit.UnsupportedMediaTypeException;
import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.metadata.NavigationProperty;
import org.apache.olingo.fit.serializer.JsonEntryContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUtilities {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractUtilities.class);

  protected final ODataServiceVersion version;

  protected final FSManager fsManager;

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

  public AbstractUtilities(final ODataServiceVersion version) throws Exception {
    this.version = version;
    this.fsManager = FSManager.instance(version);
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
   * @param entityKey
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

  private InputStream toInputStream(final AtomEntryImpl entry) throws XMLStreamException {
    final StringWriter writer = new StringWriter();
    Commons.getAtomSerializer(version).write(writer, entry);

    return IOUtils.toInputStream(writer.toString(), Constants.ENCODING);
  }

  public InputStream addOrReplaceEntity(
          final String key,
          final String entitySetName,
          final InputStream is,
          final AtomEntryImpl entry) throws Exception {

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    IOUtils.copy(is, bos);
    IOUtils.closeQuietly(is);

    final Map<String, NavigationProperty> navigationProperties =
            Commons.getMetadata(version).getNavigationProperties(entitySetName);

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
      final NavigationProperty navProp = navigationProperties == null? null: navigationProperties.get(link.getTitle());
      if (navProp != null) {
        final String inlineEntitySetName = navProp.getTarget();
        if (link.getInlineEntry() != null) {
          final String inlineEntryKey = getDefaultEntryKey(
                  inlineEntitySetName, (AtomEntryImpl) link.getInlineEntry());

          addOrReplaceEntity(
                  inlineEntryKey,
                  inlineEntitySetName,
                  toInputStream((AtomEntryImpl) link.getInlineEntry()),
                  (AtomEntryImpl) link.getInlineEntry());

          hrefs.add(inlineEntitySetName + "(" + inlineEntryKey + ")");
        } else if (link.getInlineFeed() != null) {
          for (Entry subentry : link.getInlineFeed().getEntries()) {
            final String inlineEntryKey = getDefaultEntryKey(
                    inlineEntitySetName, (AtomEntryImpl) subentry);

            addOrReplaceEntity(
                    inlineEntryKey,
                    inlineEntitySetName,
                    toInputStream((AtomEntryImpl) subentry),
                    (AtomEntryImpl) subentry);

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

  public InputStream addMediaEntity(
          final String entitySetName,
          final InputStream is) throws Exception {

    final String entityKey = getDefaultEntryKey(entitySetName, null);

    addMediaEntityValue(entitySetName, entityKey, is);

    final String path = Commons.getEntityBasePath(entitySetName, entityKey);

    // -----------------------------------------
    // 2. save entity as atom
    // -----------------------------------------
    final String entityURI = Commons.getEntityURI(entitySetName, entityKey);
    String entity = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<entry xml:base=\"" + Constants.get(version, ConstantKey.DEFAULT_SERVICE_URL) + "\" "
            + "xmlns=\"http://www.w3.org/2005/Atom\" "
            + "xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" "
            + "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" "
            + "xmlns:georss=\"http://www.georss.org/georss\" "
            + "xmlns:gml=\"http://www.opengis.net/gml\">"
            + "<id>" + Constants.get(version, ConstantKey.DEFAULT_SERVICE_URL) + entityURI + "</id>"
            + "<category term=\"Microsoft.Test.OData.Services.AstoriaDefaultService." + entitySetName + "\" "
            + "scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\" />"
            + "<link rel=\"edit\" title=\"Car\" href=\"" + entityURI + "\" />"
            + "<link rel=\"edit-media\" title=\"Car\" href=\"" + entityURI + "/$value\" />"
            + "<content type=\"*/*\" src=\"" + entityURI + "/$value\" />"
            + "<m:properties>"
            + "<d:" + Commons.MEDIA_CONTENT.get(entitySetName) + " m:type=\"Edm.Int32\">" + entityKey + "</d:VIN>"
            + "<d:Description m:null=\"true\" />"
            + "</m:properties>"
            + "</entry>";

    fsManager.putInMemory(
            IOUtils.toInputStream(entity),
            fsManager.getAbsolutePath(path + Constants.get(version, ConstantKey.ENTITY), Accept.ATOM));
    // -----------------------------------------

    // -----------------------------------------
    // 3. save entity as json
    // -----------------------------------------
    entity = "{"
            + "\"odata.metadata\": \"" + Constants.get(version, ConstantKey.DEFAULT_SERVICE_URL)
            + "/$metadata#" + entitySetName + "/@Element\","
            + "\"odata.type\": \"Microsoft.Test.OData.Services.AstoriaDefaultService." + entitySetName + "\","
            + "\"odata.id\": \"" + Constants.get(version, ConstantKey.DEFAULT_SERVICE_URL) + entityURI + "\","
            + "\"odata.editLink\": \"" + entityURI + "\","
            + "\"odata.mediaEditLink\": \"" + entityURI + "/$value\","
            + "\"odata.mediaReadLink\": \"" + entityURI + "/$value\","
            + "\"odata.mediaContentType\": \"*/*\","
            + "\"" + Commons.MEDIA_CONTENT.get(entitySetName) + "\": " + entityKey + ","
            + "\"Description\": null" + "}";

    fsManager.putInMemory(
            IOUtils.toInputStream(entity), fsManager.getAbsolutePath(path + Constants.get(version, ConstantKey.ENTITY),
                    Accept.JSON_FULLMETA));
    // -----------------------------------------

    return readEntity(entitySetName, entityKey, getDefaultFormat()).getValue();
  }

  public void putLinksInMemory(
          final String basePath,
          final String entitySetName,
          final String entityKey,
          final String linkName,
          final Collection<String> links) throws IOException {

    final HashSet<String> uris = new HashSet<String>();

    final Metadata metadata = Commons.getMetadata(version);
    final Map<String, NavigationProperty> navigationProperties = metadata.getNavigationProperties(entitySetName);

    if (navigationProperties.get(linkName).isFeed()) {
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

  public AtomEntryImpl readEntry(final Accept contentTypeValue, final InputStream entity)
          throws XMLStreamException, IOException {

    final AtomEntryImpl entry;

    if (Accept.ATOM == contentTypeValue) {
      final Container<AtomEntryImpl> container = Commons.getAtomDeserializer(version).
              read(entity, AtomEntryImpl.class);
      entry = container.getObject();
    } else {
      final Container<JSONEntryImpl> jcontainer =
              Commons.getJsonMapper(version).readValue(entity, new TypeReference<JSONEntryImpl>() {
              });
      entry = new DataBinder(version).getAtomEntry(jcontainer.getObject());
    }

    return entry;
  }

  public InputStream writeEntry(final Accept accept, final Container<AtomEntryImpl> container)
          throws XMLStreamException, IOException {

    final ByteArrayOutputStream content = new ByteArrayOutputStream();
    final OutputStreamWriter writer = new OutputStreamWriter(content, Constants.ENCODING);

    if (accept == Accept.ATOM) {
      Commons.getAtomSerializer(version).write(writer, container);
      writer.flush();
      writer.close();
    } else {
      final ObjectMapper mapper = Commons.getJsonMapper(version);
      mapper.writeValue(
              writer, new JsonEntryContainer<JSONEntryImpl>(container.getContextURL(), container.getMetadataETag(),
                      (new DataBinder(version)).getJsonEntry((AtomEntryImpl) container.getObject())));
    }

    return new ByteArrayInputStream(content.toByteArray());
  }

  private String getDefaultEntryKey(final String entitySetName, final AtomEntryImpl entry, final String propertyName)
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

  public String getDefaultEntryKey(final String entitySetName, final AtomEntryImpl entry) throws IOException {
    try {
      String res;

      if ("Message".equals(entitySetName)) {
        int messageId;
        if (entry.getProperty("MessageId") == null || entry.getProperty("FromUsername") == null) {
          if (Commons.SEQUENCE.containsKey(entitySetName)) {
            messageId = Commons.SEQUENCE.get(entitySetName) + 1;
            res = "MessageId=" + String.valueOf(messageId) + ",FromUsername=1";
          } else {
            throw new Exception(String.format("Unable to retrieve entity key value for %s", entitySetName));
          }
        } else {
          messageId = Integer.valueOf(entry.getProperty("MessageId").getValue().asPrimitive().get());
          res = "MessageId=" + entry.getProperty("MessageId").getValue().asPrimitive().get()
                  + ",FromUsername=" + entry.getProperty("FromUsername").getValue().asPrimitive().get();
        }
        Commons.SEQUENCE.put(entitySetName, messageId);
      } else if ("Order".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entry, "OrderId");
      } else if ("Orders".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entry, "OrderID");
      } else if ("Customer".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entry, "CustomerId");
      } else if ("Person".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entry, "PersonId");
      } else if ("ComputerDetail".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entry, "ComputerDetailId");
      } else if ("AllGeoTypesSet".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entry, "Id");
      } else if ("CustomerInfo".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entry, "CustomerInfoId");
      } else if ("Car".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entry, "VIN");
      } else if ("RowIndex".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entry, "Id");
      } else if ("Products".equals(entitySetName)) {
        res = getDefaultEntryKey(entitySetName, entry, "ProductID");
      } else if ("ProductDetails".equals(entitySetName)) {
        int productId;
        int productDetailId;
        if (entry.getProperty("ProductID") == null || entry.getProperty("ProductDetailID") == null) {
          if (Commons.SEQUENCE.containsKey(entitySetName) && Commons.SEQUENCE.containsKey("Products")) {
            productId = Commons.SEQUENCE.get("Products") + 1;
            productDetailId = Commons.SEQUENCE.get(entitySetName) + 1;
            res = "ProductID=" + String.valueOf(productId) + ",ProductDetailID=" + String.valueOf(productDetailId);
          } else {
            throw new Exception(String.format("Unable to retrieve entity key value for %s", entitySetName));
          }
          Commons.SEQUENCE.put(entitySetName, productDetailId);
        } else {
          productId = Integer.valueOf(entry.getProperty("ProductID").getValue().asPrimitive().get());
          productDetailId = Integer.valueOf(entry.getProperty("ProductDetailID").getValue().asPrimitive().get());
          res = "ProductID=" + entry.getProperty("ProductID").getValue().asPrimitive().get()
                  + ",ProductDetailID=" + entry.getProperty("ProductDetailID").getValue().asPrimitive().get();
        }
        Commons.SEQUENCE.put(entitySetName, productDetailId);
        Commons.SEQUENCE.put("Products", productId);
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
    final Metadata metadata = Commons.getMetadata(version);
    final Map<String, NavigationProperty> navigationProperties = metadata.getNavigationProperties(entitySetName);

    linkInfo.setFeed(navigationProperties.get(linkName.replaceAll("\\(.*\\)", "")).isFeed());

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
    final Metadata metadata = Commons.getMetadata(version);
    final Map<String, NavigationProperty> navigationProperties = metadata.getNavigationProperties(entitySetName);

    return readEntities(
            links.getValue(),
            linkName,
            links.getKey(),
            navigationProperties.get(linkName).isFeed());
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

  public InputStream replaceProperty(
          final String entitySetName,
          final String entityId,
          final InputStream changes,
          final List<String> path,
          final Accept accept,
          final boolean justValue) throws Exception {

    final String basePath = Commons.getEntityBasePath(entitySetName, entityId);

    final Accept acceptType = accept == null || Accept.TEXT == accept
            ? Accept.XML : accept.getExtension().equals(Accept.JSON.getExtension()) ? Accept.JSON_FULLMETA : accept;

    // read atom
    InputStream stream = fsManager.readFile(basePath + Constants.get(version, ConstantKey.ENTITY), acceptType);

    // change atom
    stream = replaceProperty(stream, changes, path, justValue);

    // save atom
    fsManager.putInMemory(stream,
            fsManager.getAbsolutePath(basePath + Constants.get(version, ConstantKey.ENTITY), acceptType));

    return fsManager.readFile(basePath + Constants.get(version, ConstantKey.ENTITY), acceptType);
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

  public abstract InputStream getProperty(
          final String entitySetName, final String entityId, final List<String> path, final String edmType)
          throws Exception;

  public abstract InputStream getPropertyValue(final InputStream is, final List<String> path)
          throws Exception;

  public abstract Map.Entry<String, List<String>> extractLinkURIs(final InputStream is) throws Exception;

  public abstract Map.Entry<String, List<String>> extractLinkURIs(
          final String entitySetName, final String entityId, final String linkName) throws Exception;
}
