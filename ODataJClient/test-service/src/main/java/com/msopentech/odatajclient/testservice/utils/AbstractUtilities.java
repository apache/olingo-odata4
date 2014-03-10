/**
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
package com.msopentech.odatajclient.testservice.utils;

import com.msopentech.odatajclient.testservice.UnsupportedMediaTypeException;

import static com.msopentech.odatajclient.testservice.utils.Accept.JSON;
import static com.msopentech.odatajclient.testservice.utils.Accept.JSON_FULLMETA;
import static com.msopentech.odatajclient.testservice.utils.Accept.JSON_NOMETA;
import static com.msopentech.odatajclient.testservice.utils.Commons.sequence;
import static com.msopentech.odatajclient.testservice.utils.Constants.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUtilities {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractUtilities.class);

    private static Set<ODataVersion> initialized = EnumSet.noneOf(ODataVersion.class);

    protected final ODataVersion version;

    protected final FSManager fsManager;

    protected final static Pattern entityUriPattern = Pattern.compile(".*\\/.*\\(.*\\)");

    public AbstractUtilities(final ODataVersion version) throws Exception {
        this.version = version;
        this.fsManager = FSManager.instance(version);
        initialize();
    }

    private void initialize() throws Exception {

        if (!initialized.contains(version)) {
            final MetadataLinkInfo metadataLinkInfo = new MetadataLinkInfo();
            Commons.linkInfo.put(version, metadataLinkInfo);

            final InputStream metadata = fsManager.readFile(Constants.METADATA, Accept.XML);
            final XMLEventReader reader = XMLUtilities.getEventReader(metadata);

            int initialDepth = 0;
            try {
                while (true) {
                    Map.Entry<Integer, XmlElement> entityType =
                            XMLUtilities.getAtomElement(reader, null, "EntityType", null, initialDepth, 4, 4, false);
                    initialDepth = entityType.getKey();

                    final XMLEventReader entityReader = XMLUtilities.getEventReader(entityType.getValue().toStream());
                    try {
                        int pos = 0;
                        while (true) {
                            Map.Entry<Integer, XmlElement> navProperty = XMLUtilities.getAtomElement(
                                    entityReader, null, "NavigationProperty", null, pos, 2, 2, false);
                            pos = navProperty.getKey();

                            metadataLinkInfo.addLinkName(
                                    entityType.getValue().getStart().
                                    getAttributeByName(new QName("Name")).getValue(),
                                    navProperty.getValue().
                                    getStart().getAttributeByName(new QName("Name")).getValue());
                        }
                    } catch (Exception e) {
                    } finally {
                        entityReader.close();
                    }
                }
            } catch (Exception e) {
            } finally {
                reader.close();
                initialized.add(version);
            }
        }
    }

    public String getEntitySetFromAlias(final String alias) {
        return Commons.entitySetAlias.get(alias);
    }

    public boolean isMediaContent(final String entityName) {
        return Commons.mediaContent.contains(entityName);
    }

    /**
     * Retrieve JSON entity's links and inlines.
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
                entitySetName + File.separatorChar + Commons.getEntityKey(key) + File.separatorChar + ENTITY;
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

    public InputStream createEntity(
            final String entitySetName, final InputStream is) throws Exception {
        return createEntity(null, entitySetName, is);
    }

    public InputStream createEntity(
            final String key,
            final String entitySetName,
            final InputStream is) throws Exception {


        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(is, bos);
        is.close();

        // -----------------------------------------
        // 0. Get default entry key and path (N.B. operation will consume/close the stream; use a copy instead)
        // -----------------------------------------
        final String entityKey = key == null ? getDefaultEntryKey(
                entitySetName, new ByteArrayInputStream(bos.toByteArray()), getDefaultFormat()) : key;

        final String path = entitySetName + File.separatorChar + Commons.getEntityKey(entityKey) + File.separatorChar;
        // -----------------------------------------

        // -----------------------------------------
        // 1. Retrieve navigation info
        // -----------------------------------------
        final NavigationLinks links =
                retrieveNavigationInfo(entitySetName, new ByteArrayInputStream(bos.toByteArray()));
        // -----------------------------------------

        // -----------------------------------------
        // 2. Normalize navigation info; edit link; ... and save entity ....
        // -----------------------------------------
        final InputStream createdEntity =
                saveSingleEntity(entityKey, entitySetName, new ByteArrayInputStream(bos.toByteArray()), links);
        // -----------------------------------------

        bos.reset();
        IOUtils.copy(createdEntity, bos);

        // -----------------------------------------
        // 3. save the entity
        // -----------------------------------------
        final FileObject fo = fsManager.putInMemory(
                new ByteArrayInputStream(bos.toByteArray()),
                fsManager.getAbsolutePath(path + ENTITY, getDefaultFormat()));
        IOUtils.closeQuietly(bos);
        // -----------------------------------------

        // -----------------------------------------
        // 4. Create links file and provided inlines
        // -----------------------------------------
        for (Map.Entry<String, List<String>> link : links.getLinks()) {
            putLinksInMemory(path, entitySetName, entityKey, link.getKey(), link.getValue());
        }

        for (Map.Entry<String, List<InputStream>> inlineEntry : links.getInlines()) {
            final String inlineEntitySetName = Commons.entitySetAlias.get(entitySetName + "." + inlineEntry.getKey());

            final List<String> hrefs = new ArrayList<String>();

            for (InputStream stream : inlineEntry.getValue()) {
                final ByteArrayOutputStream inlineBos = new ByteArrayOutputStream();
                IOUtils.copy(stream, inlineBos);
                IOUtils.closeQuietly(stream);

                final String inlineEntryKey = getDefaultEntryKey(
                        inlineEntitySetName, new ByteArrayInputStream(inlineBos.toByteArray()), getDefaultFormat());

                createEntity(
                        inlineEntryKey,
                        inlineEntitySetName,
                        new ByteArrayInputStream(inlineBos.toByteArray()));

                hrefs.add(inlineEntitySetName + "(" + inlineEntryKey + ")");
            }

            putLinksInMemory(path, entitySetName, entityKey, inlineEntry.getKey(), hrefs);
        }
        // -----------------------------------------

        return fo.getContent().getInputStream();
    }

    protected void putLinksInMemory(
            final String path,
            final String entitySetName,
            final String entityKey,
            final String linkName,
            final Collection<String> links) throws IOException {
        final HashSet<String> uris = new HashSet<String>();

        if (Commons.feed.contains(entitySetName + "." + linkName)) {
            try {
                final Map.Entry<String, List<String>> currents = XMLUtilities.extractLinkURIs(
                        readLinks(entitySetName, entityKey, linkName, Accept.JSON_FULLMETA).getLinks());
                uris.addAll(currents.getValue());
            } catch (Exception ignore) {
            }
        }

        uris.addAll(links);

        putLinksInMemory(path, entitySetName, linkName, uris);
    }

    protected void putLinksInMemory(
            final String basePath, final String entitySetName, final String linkName, final Collection<String> uris)
            throws IOException {
        fsManager.putInMemory(
                Commons.getLinksAsJSON(entitySetName, new SimpleEntry<String, Collection<String>>(linkName, uris)),
                fsManager.getAbsolutePath(
                basePath + LINKS_FILE_PATH + File.separatorChar + linkName, Accept.JSON_FULLMETA));

        fsManager.putInMemory(
                Commons.getLinksAsATOM(new SimpleEntry<String, Collection<String>>(linkName, uris)),
                fsManager.getAbsolutePath(
                basePath + LINKS_FILE_PATH + File.separatorChar + linkName, Accept.XML));
    }

    public Response createResponse(
            final InputStream entity, final String etag, final Accept accept) {
        return createResponse(entity, etag, accept, null);
    }

    public Response createResponse(
            final InputStream entity, final String etag, final Accept accept, final Response.Status status) {

        final Response.ResponseBuilder builder = Response.ok();

        if (StringUtils.isNotBlank(etag)) {
            builder.header("ETag", etag);
        }

        if (accept != null) {
            builder.header("Content-Type", accept.toString());
        }

        if (status != null) {
            builder.status(status);
        }

        if (entity != null) {
            builder.entity(entity);
        }

        return builder.build();
    }

    public Response createFaultResponse(final String accept, final Exception e) {
        LOG.debug("Create fault response about .... ", e);

        final Response.ResponseBuilder builder = Response.serverError();

        final String ext;
        final Accept contentType;
        if (accept.startsWith("application/json")) {
            ext = ".json";
            contentType = Accept.JSON;
        } else if (accept.startsWith("application/xml") || version == ODataVersion.v3) {
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

    protected String getDefaultEntryKey(
            final String entitySetName, final InputStream entity, final Accept accept) throws IOException {
        try {
            String res;

            if ("Message".equals(entitySetName)) {
                try {
                    final List<String> propertyNames = new ArrayList<String>();
                    propertyNames.add("MessageId");
                    propertyNames.add("FromUsername");

                    final StringBuilder keyBuilder = new StringBuilder();
                    for (Map.Entry<String, InputStream> value
                            : getPropertyValues(entity, propertyNames, accept).entrySet()) {

                        if (keyBuilder.length() > 0) {
                            keyBuilder.append(",");
                        }

                        keyBuilder.append(value.getKey()).append("=").append(IOUtils.toString(value.getValue()));
                    }
                    res = keyBuilder.toString();
                } catch (Exception e) {
                    int messageId = sequence.get(entitySetName) + 1;
                    if (sequence.containsKey(entitySetName)) {
                        res = "MessageId=" + String.valueOf(messageId)
                                + ",FromUsername=1";
                    } else {
                        throw new Exception(String.format("Unable to retrieve entity key value for %s", entitySetName));
                    }
                    sequence.put(entitySetName, Integer.valueOf(messageId));
                }
            } else if ("Order".equals(entitySetName)) {
                try {
                    final Map<String, InputStream> value =
                            getPropertyValues(entity, Collections.<String>singletonList("OrderId"), accept);
                    res = value.isEmpty() ? null : IOUtils.toString(value.values().iterator().next());
                } catch (Exception e) {
                    if (sequence.containsKey(entitySetName)) {
                        res = String.valueOf(sequence.get(entitySetName) + 1);
                    } else {
                        throw new Exception(String.format("Unable to retrieve entity key value for %s", entitySetName));
                    }
                }
                sequence.put(entitySetName, Integer.valueOf(res));
            } else if ("Customer".equals(entitySetName)) {
                try {
                    final Map<String, InputStream> value =
                            getPropertyValues(entity, Collections.<String>singletonList("CustomerId"), accept);
                    res = value.isEmpty() ? null : IOUtils.toString(value.values().iterator().next());
                } catch (Exception e) {
                    if (sequence.containsKey(entitySetName)) {
                        res = String.valueOf(sequence.get(entitySetName) + 1);
                    } else {
                        throw new Exception(String.format("Unable to retrieve entity key value for %s", entitySetName));
                    }
                }
                sequence.put(entitySetName, Integer.valueOf(res));
            } else if ("CustomerInfo".equals(entitySetName)) {
                try {
                    final Map<String, InputStream> value =
                            getPropertyValues(entity, Collections.<String>singletonList("CustomerInfoId"), accept);
                    res = value.isEmpty() ? null : IOUtils.toString(value.values().iterator().next());
                } catch (Exception e) {
                    if (sequence.containsKey(entitySetName)) {
                        res = String.valueOf(sequence.get(entitySetName) + 1);
                    } else {
                        throw new Exception(String.format("Unable to retrieve entity key value for %s", entitySetName));
                    }
                }
                sequence.put(entitySetName, Integer.valueOf(res));
            } else {
                throw new Exception(String.format("EntitySet '%s' not found", entitySetName));
            }

            return res;
        } catch (Exception e) {
            throw new NotFoundException(e);
        } finally {
            IOUtils.closeQuietly(entity);
        }
    }

    private static Map<String, InputStream> getPropertyValues(
            final InputStream is, final List<String> propertyNames, final Accept accept)
            throws Exception {
        final Map<String, InputStream> res = new LinkedHashMap<String, InputStream>();

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(is, bos);
        IOUtils.closeQuietly(is);

        for (String propertyName : propertyNames) {
            final InputStream value;

            switch (accept) {
                case JSON:
                case JSON_FULLMETA:
                case JSON_NOMETA:
                    value = JSONUtilities.getJsonPropertyValue(
                            new ByteArrayInputStream(bos.toByteArray()),
                            propertyName);
                    break;
                default:
                    value = XMLUtilities.getAtomPropertyValue(
                            new ByteArrayInputStream(bos.toByteArray()),
                            new String[] { propertyName, "$value" });
            }
            res.put(propertyName, value);
        }

        IOUtils.closeQuietly(bos);

        return res;
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

        final String basePath =
                entitySetName + File.separatorChar + Commons.getEntityKey(entityId) + File.separatorChar
                + LINKS_FILE_PATH + File.separatorChar;

        final LinkInfo linkInfo = new LinkInfo(fsManager.readFile(basePath + linkName, accept));
        linkInfo.setEtag(Commons.getETag(basePath, version));
        linkInfo.setFeed(Commons.feed.contains(entitySetName + "." + linkName));

        return linkInfo;
    }

    public Map.Entry<String, InputStream> readEntity(
            final String entitySetName, final String entityId, final Accept accept) {
        if (accept == Accept.XML || accept == Accept.TEXT) {
            throw new UnsupportedMediaTypeException("Unsupported media type");
        }

        final String basePath =
                entitySetName + File.separatorChar + Commons.getEntityKey(entityId) + File.separatorChar;
        return new SimpleEntry<String, InputStream>(basePath, fsManager.readFile(basePath + ENTITY, accept));
    }

    public InputStream expandEntity(
            final String entitySetName,
            final String entityId,
            final InputStream entity,
            final String linkName)
            throws Exception {

        // --------------------------------
        // 0. Retrieve all 'linkName' navigation link uris (NotFoundException if missing) 
        // --------------------------------
        final LinkInfo linkInfo = readLinks(entitySetName, entityId, linkName, Accept.XML);
        final Map.Entry<String, List<String>> links = XMLUtilities.extractLinkURIs(linkInfo.getLinks());
        // --------------------------------

        // --------------------------------
        // 1. Retrieve expanded object (entry or feed)
        // --------------------------------
        final InputStream expanded = readEntities(
                links.getValue(),
                linkName,
                links.getKey(),
                linkInfo.isFeed());
        // --------------------------------

        // --------------------------------
        // 2. Retrieve expanded object (entry or feed)
        // --------------------------------
        return replaceLink(entity, linkName, expanded);
        // --------------------------------
    }

    public InputStream patchEntity(
            final String entitySetName,
            final String entityId,
            final InputStream changes,
            final Accept accept,
            final String ifMatch)
            throws Exception {
        final Map.Entry<String, InputStream> entityInfo = readEntity(entitySetName, entityId, accept);

        final String etag = Commons.getETag(entityInfo.getKey(), version);
        if (StringUtils.isNotBlank(ifMatch) && !ifMatch.equals(etag)) {
            throw new ConcurrentModificationException("Concurrent modification");
        }

        final Map<String, InputStream> replacement = getChanges(changes);
        return createEntity(entityId, entitySetName, setChanges(entityInfo.getValue(), replacement));
    }

    public abstract InputStream readEntities(
            final List<String> links, final String linkName, final String next, final boolean forceFeed)
            throws Exception;

    protected abstract InputStream replaceLink(
            final InputStream toBeChanged, final String linkName, final InputStream replacement) throws Exception;

    public abstract InputStream selectEntity(final InputStream entity, final String[] propertyNames) throws Exception;

    protected abstract Accept getDefaultFormat();

    protected abstract Map<String, InputStream> getChanges(final InputStream src) throws Exception;

    protected abstract InputStream setChanges(
            final InputStream toBeChanged, final Map<String, InputStream> properties) throws Exception;
}
