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
package com.msopentech.odatajclient.testservice;

import static com.msopentech.odatajclient.testservice.Constants.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.cxf.helpers.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utilities {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(Utilities.class);

    private static Pattern multiKeyPattern = Pattern.compile("(.*=.*,?)+");

    private static String MEM_PREFIX = "ram://";

    private static String RES_PREFIX = "res://";

    private final ODataVersion version;

    final FileSystemManager fsManager;

    public Utilities(ODataVersion version) throws FileSystemException {
        this.version = version;
        fsManager = VFS.getManager();
    }

    public String getAbsolutePath(final String relativePath, final Accept accept) {
        return File.separatorChar + version.name() + File.separatorChar + relativePath
                + (accept == null ? "" : accept.getExtension());
    }

    public FileObject putInMemory(final FileObject file, final String path) throws IOException {

        final FileObject memObject = fsManager.resolveFile(MEM_PREFIX + path);

        final InputStream is = file.getContent().getInputStream();

        // create in-memory file
        memObject.createFile();

        // read in-memory content
        final OutputStream os = memObject.getContent().getOutputStream();
        IOUtils.copy(is, os);
        os.flush();
        os.close();
        is.close();

        return memObject;
    }

    public InputStream readFile(final String relativePath, final Accept accept) {
        final String path = getAbsolutePath(relativePath, accept);
        LOG.info("Read {}", path);

        try {
            FileObject fileObject = fsManager.resolveFile(MEM_PREFIX + path);

            if (!fileObject.exists()) {
                LOG.warn("In-memory path '{}' not found", path);

                try {
                    fileObject = fsManager.resolveFile(RES_PREFIX + path);
                } catch (FileSystemException fse) {
                    LOG.warn("Resource path '{}' not found", path, fse);
                }
            }

            if (!fileObject.exists()) {
                throw new NotFoundException();
            }

            fileObject = putInMemory(fileObject, path);

            // return new in-memory content
            return fileObject.getContent().getInputStream();
        } catch (IOException e) {
            throw new NotFoundException(e);
        }
    }

    public String getEntityKey(final String entityId) {
        if (multiKeyPattern.matcher(entityId).matches()) {
            // assume correct multi-key
            final String[] keys = entityId.split(",");
            final StringBuilder keyBuilder = new StringBuilder();
            for (String part : keys) {
                if (keyBuilder.length() > 0) {
                    keyBuilder.append(" ");
                }
                keyBuilder.append(part.split("=")[1].replaceAll("'", "").trim());
            }
            return keyBuilder.toString();
        } else {
            return entityId.trim();
        }
    }

    public int countAllElements(final String entitySetName) throws Exception {
        final String basePath = entitySetName + File.separatorChar;
        int count = countFeedElements(readFile(basePath + FEED, Accept.XML), "entry");

        final String skipTokenDirPath = getAbsolutePath(basePath + SKIP_TOKEN, null);

        FileObject skipToken = fsManager.resolveFile(MEM_PREFIX + skipTokenDirPath);
        if (!skipToken.exists()) {
            try {
                skipToken = fsManager.resolveFile(RES_PREFIX + skipTokenDirPath);
            } catch (FileSystemException fse) {
                LOG.debug("Resource path '{}' not found", skipTokenDirPath);
            }
        }

        if (skipToken.exists()) {
            final FileObject[] files = skipToken.findFiles(new FileSelector() {

                @Override
                public boolean includeFile(final FileSelectInfo fileInfo) throws Exception {
                    return fileInfo.getFile().getName().getExtension().equals(Accept.XML.getExtension().substring(1));
                }

                @Override
                public boolean traverseDescendents(final FileSelectInfo fileInfo) throws Exception {
                    return true;
                }
            });

            for (FileObject file : files) {
                count += countFeedElements(
                        readFile(basePath + SKIP_TOKEN + File.separatorChar + file.getName().getBaseName(), null),
                        "entry");
            }
        }

        return count;
    }

    private int countFeedElements(final InputStream is, final String elementName) throws XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

        final XMLEventReader reader = factory.createXMLEventReader(is);

        int count = 0;

        while (reader.hasNext()) {
            final XMLEvent event = reader.nextEvent();

            if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                    && elementName.equals(event.asStartElement().getName().getLocalPart())) {
                count++;
            }
        }

        reader.close();
        return count;
    }

    public InputStream getAtomProperty(final InputStream is, final String[] path)
            throws XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

        final XMLEventReader reader = factory.createXMLEventReader(is);

        final InputStream propertyStream = writeFromStartToEndElement(
                getPropertyStartElement(reader, path),
                reader,
                true);

        reader.close();

        if (propertyStream == null) {
            throw new NotFoundException();
        }

        return propertyStream;
    }

    private InputStream writeFromStartToEndElement(
            final StartElement element, final XMLEventReader reader, final boolean document)
            throws XMLStreamException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        final XMLEventWriter writer = xof.createXMLEventWriter(bos);

        final QName name = element.getName();

        if (document) {
            final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            writer.add(eventFactory.createStartDocument("UTF-8", "1.0"));
            writer.add(element);
            writer.add(eventFactory.createNamespace(ATOM_PROPERTY_PREFIX.substring(0, 1), DATASERVICES_NS));
            writer.add(eventFactory.createNamespace(ATOM_METADATA_PREFIX.substring(0, 1), METADATA_NS));
        } else {
            writer.add(element);
        }

        XMLEvent event = element;

        while (reader.hasNext() && !(event.isEndElement() && name.equals(event.asEndElement().getName()))) {
            event = reader.nextEvent();
            writer.add(event);
        }

        writer.flush();
        writer.close();

        return new ByteArrayInputStream(bos.toByteArray());
    }

    public InputStream getAtomPropertyValue(final InputStream is, final String[] path)
            throws Exception {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

        final XMLEventReader reader = factory.createXMLEventReader(is);

        // search for property start element
        getPropertyStartElement(reader, ArrayUtils.subarray(path, 0, path.length - 1));

        // comsume attributes
        XMLEvent event = reader.nextEvent();
        while (event.getEventType() == XMLStreamConstants.ATTRIBUTE) {
            event = reader.nextEvent();
        }

        final InputStream res;

        // expected text node
        if (event.isCharacters()) {
            res = new ByteArrayInputStream(event.asCharacters().getData().getBytes());
        } else if (event.isEndElement()) {
            throw new NotFoundException();
        } else {
            throw new Exception("The method or operation is not implemented.");
        }

        reader.close();

        return res;
    }

    public String getEdmTypeFromXML(final InputStream is, final String[] path)
            throws XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

        final XMLEventReader reader = factory.createXMLEventReader(is);

        final Attribute type = getPropertyStartElement(reader, path).getAttributeByName(new QName(TYPE));

        reader.close();

        if (type == null) {
            throw new NotFoundException();
        }

        return type.getValue();
    }

    public StartElement getPropertyStartElement(final XMLEventReader reader, final String[] path)
            throws XMLStreamException {

        boolean properties = false;
        int pos = 0;

        StartElement property = null;
        while (reader.hasNext() && pos < path.length) {
            final XMLEvent event = reader.nextEvent();

            if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                    && PROPERTIES.equals(event.asStartElement().getName().getLocalPart())) {
                properties = true;
            } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
                    && PROPERTIES.equals(event.asEndElement().getName().getLocalPart())) {
                properties = false;
            } else if (properties) {
                if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                        && (ATOM_PROPERTY_PREFIX + path[pos].trim()).equals(
                        event.asStartElement().getName().getLocalPart())) {
                    pos++;
                    if (path.length == pos) {
                        property = event.asStartElement();
                    }
                }
            }
        }

        if (property == null) {
            throw new NotFoundException();
        }

        return property;
    }

    public InputStream addAtomInlinecount(
            final InputStream feed, final int count, final Accept accept)
            throws Exception {

        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

        final XMLEventReader reader = factory.createXMLEventReader(feed);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        final XMLEventWriter writer = xof.createXMLEventWriter(bos);

        final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent newLine = eventFactory.createSpace("\n");

        while (reader.hasNext()) {
            final XMLEvent event = reader.nextEvent();
            writer.add(event);

            if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                    && "feed".equals(event.asStartElement().getName().getLocalPart())) {

                writer.add(newLine);

                writer.add(eventFactory.createStartElement("m", null, "count"));
                writer.add(eventFactory.createCharacters(String.valueOf(count)));
                writer.add(eventFactory.createEndElement("m", null, "count"));
                writer.add(newLine);
            }
        }

        writer.flush();
        writer.close();
        reader.close();
        feed.close();

        return new ByteArrayInputStream(bos.toByteArray());
    }

    public InputStream expandLinks(
            final InputStream src,
            final String basePath,
            final String expandOption,
            final Accept accept)
            throws Exception {

        InputStream entity = null;

        switch (accept) {
            case ATOM:
                for (String exp : expandOption.split(",")) {
                    entity = expandAtomEntity(
                            src,
                            exp,
                            basePath + INLINE + File.separatorChar + exp,
                            accept);
                }
                break;
            case JSON_FULLMETA:
            case JSON:
            case JSON_NOMETA:
                for (String exp : expandOption.split(",")) {
                    entity = expandJsonEntity(
                            src,
                            exp,
                            basePath + INLINE + File.separatorChar + exp,
                            accept);

                    try {
                        if (readFile(basePath + INLINE + File.separatorChar + exp + ".next", accept) != null) {
                            entity = expandJsonEntity(
                                    entity,
                                    exp + "@odata.nextLink",
                                    basePath + INLINE + File.separatorChar + exp + ".next",
                                    accept);
                        }
                    } catch (NotFoundException e) {
                        // ignore
                    }
                }
                break;
            default:
                throw new UnsupportedMediaTypeException("Unsupported media type");
        }

        return entity;
    }

    private InputStream expandAtomEntity(
            final InputStream entity,
            final String linkName,
            final String expanded,
            final Accept accept)
            throws Exception {

        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

        final XMLEventReader reader = factory.createXMLEventReader(entity);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        final XMLEventWriter writer = xof.createXMLEventWriter(bos);

        final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent newLine = eventFactory.createSpace("\n");

        boolean found = false;

        while (reader.hasNext()) {
            final XMLEvent event = reader.nextEvent();

            if (event.getEventType() == XMLStreamConstants.COMMENT) {
                writer.add(newLine);
                writer.add(event);
                writer.add(newLine);
            } else {
                writer.add(event);
            }

            if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                    && "link".equals(event.asStartElement().getName().getLocalPart())
                    && linkName.equals(event.asStartElement().getAttributeByName(new QName("title")).getValue())) {
                found = true;
                writer.add(newLine);

                writer.add(eventFactory.createStartElement("m", null, "inline"));
                writer.add(newLine);

                final InputStream inline = readFile(expanded, accept);
                final XMLEventReader inlineReader = factory.createXMLEventReader(inline);

                while (inlineReader.hasNext()) {
                    final XMLEvent inlineEvent = inlineReader.nextEvent();

                    if (inlineEvent.getEventType() != XMLStreamConstants.START_DOCUMENT
                            && inlineEvent.getEventType() != XMLStreamConstants.END_DOCUMENT
                            && inlineEvent.getEventType() != XMLStreamConstants.COMMENT) {
                        writer.add(inlineEvent);
                    }
                }

                inlineReader.close();
                inline.close();
                writer.add(newLine);
                writer.add(eventFactory.createEndElement("m", null, "inline"));
                writer.add(newLine);
            }
        }

        writer.flush();
        writer.close();
        reader.close();
        entity.close();

        if (!found) {
            throw new Exception(String.format("Could not find a property named '%s'", linkName));
        }

        return new ByteArrayInputStream(bos.toByteArray());
    }

    private InputStream expandJsonEntity(
            final InputStream src,
            final String linkName,
            final String expanded,
            final Accept accept)
            throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode srcNode = mapper.readTree(src);
        final JsonNode expandedNode = mapper.readTree(readFile(expanded, accept));

        final LinkedHashMap<String, JsonNode> properties = new LinkedHashMap<String, JsonNode>();

        final Iterator<Map.Entry<String, JsonNode>> iter = srcNode.fields();

        switch (accept) {
            case JSON:
                if (iter.hasNext()) {
                    final Map.Entry<String, JsonNode> entry = iter.next();
                    properties.put(entry.getKey(), entry.getValue());
                }
                break;
            case JSON_FULLMETA:
                boolean found = false;
                while (iter.hasNext() && !found) {
                    final Map.Entry<String, JsonNode> entry = iter.next();
                    properties.put(entry.getKey(), entry.getValue());
                    if (entry.getKey().equals(linkName + "@odata.navigationLinkUrl")) {
                        found = true;
                    }
                }
        }

        properties.put(linkName, expandedNode);

        while (iter.hasNext()) {
            final Map.Entry<String, JsonNode> entry = iter.next();
            properties.put(entry.getKey(), entry.getValue());
        }

        ((ObjectNode) srcNode).removeAll();
        ((ObjectNode) srcNode).putAll(properties);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mapper.writeValue(bos, srcNode);

        final InputStream res = new ByteArrayInputStream(bos.toByteArray());
        bos.close();

        return res;
    }

    public InputStream getJsonProperty(final InputStream src, final String[] path, final String edmType)
            throws Exception {

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode srcNode = mapper.readTree(src);

        final ObjectNode property = new ObjectNode(JsonNodeFactory.instance);
        property.put(ODATA_METADATA_NAME, ODATA_METADATA_PREFIX + edmType);

        JsonNode jsonNode = getJsonProperty(srcNode, path, 0);
        if (jsonNode.isObject()) {
            property.putAll((ObjectNode) jsonNode);
        } else {
            property.put("value", jsonNode.asText());
        }

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mapper.writeValue(bos, property);

        final InputStream res = new ByteArrayInputStream(bos.toByteArray());
        bos.flush();
        bos.close();

        return res;
    }

    private JsonNode getJsonProperty(final JsonNode node, final String[] path, final int index)
            throws NotFoundException {
        final Iterator<Map.Entry<String, JsonNode>> iter = node.fields();
        while (iter.hasNext()) {
            final Map.Entry<String, JsonNode> entry = iter.next();
            if (path[index].equals(entry.getKey())) {
                if (path.length - 1 == index) {
                    return entry.getValue();
                } else {
                    return getJsonProperty(entry.getValue(), path, index + 1);
                }
            }
        }
        throw new NotFoundException();
    }

    public InputStream addJsonInlinecount(
            final InputStream src, final int count, final Accept accept)
            throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode srcNode = mapper.readTree(src);

        ((ObjectNode) srcNode).put(ODATA_COUNT_NAME, count);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mapper.writeValue(bos, srcNode);

        final InputStream res = new ByteArrayInputStream(bos.toByteArray());
        bos.close();

        return res;
    }

    public InputStream selectAtomEntity(
            final InputStream entity, final String[] propertyNames, final Accept accept)
            throws Exception {

        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

        final XMLEventReader reader = factory.createXMLEventReader(entity);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        final XMLEventWriter writer = xof.createXMLEventWriter(bos);

        final List<String> found = new ArrayList<String>(Arrays.asList(propertyNames));

        boolean inProperties = false;
        boolean writeCurrent = true;
        Boolean writeNext = null;
        String currentName = null;

        final List<String> fieldToBeSaved = new ArrayList<String>(Arrays.asList(propertyNames));

        while (reader.hasNext()) {
            final XMLEvent event = reader.nextEvent();
            if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                    && "link".equals(event.asStartElement().getName().getLocalPart())
                    && !fieldToBeSaved.contains(
                    event.asStartElement().getAttributeByName(new QName("title")).getValue())
                    && !"edit".equals(event.asStartElement().getAttributeByName(new QName("rel")).getValue())) {
                writeCurrent = false;
            } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
                    && ("link").equals(event.asEndElement().getName().getLocalPart())) {
                writeNext = true;
            } else if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                    && (PROPERTIES).equals(event.asStartElement().getName().getLocalPart())) {
                writeCurrent = true;
                writeNext = false;
                inProperties = true;
            } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
                    && (PROPERTIES).equals(event.asEndElement().getName().getLocalPart())) {
                writeCurrent = true;
            } else if (inProperties) {
                if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    final String elementName = event.asStartElement().getName().getLocalPart();

                    for (String propertyName : propertyNames) {
                        if ((ATOM_PROPERTY_PREFIX + propertyName.trim()).equals(elementName)) {
                            writeCurrent = true;
                            found.remove(propertyName);
                            currentName = propertyName;
                        }
                    }

                } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
                        && StringUtils.isNotBlank(currentName)
                        && (ATOM_PROPERTY_PREFIX + currentName.trim()).equals(
                        event.asEndElement().getName().getLocalPart())) {
                    writeNext = false;
                    currentName = null;
                }

            }

            if (writeCurrent) {
                writer.add(event);
            }

            if (writeNext != null) {
                writeCurrent = writeNext;
                writeNext = null;
            }
        }

        writer.flush();
        writer.close();
        reader.close();
        entity.close();

        // Do not raise any exception in order to support FC properties as well
        // if (!found.isEmpty()) {
        //     throw new Exception(String.format("Could not find a properties '%s'", found));
        // }

        return new ByteArrayInputStream(bos.toByteArray());
    }

    public InputStream selectJsonEntity(
            final InputStream src, final String[] propertyNames, final Accept accept)
            throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode srcNode = mapper.readTree(src);

        final LinkedHashMap<String, JsonNode> properties = new LinkedHashMap<String, JsonNode>();

        final Iterator<Map.Entry<String, JsonNode>> iter = srcNode.fields();

        final List<String> fieldToBeSaved = new ArrayList<String>();
        for (String link : propertyNames) {
            fieldToBeSaved.add(link.trim() + "@odata.navigationLinkUrl");
        }

        while (iter.hasNext()) {
            final Map.Entry<String, JsonNode> entry = iter.next();
            if (entry.getKey().startsWith("odata.") || fieldToBeSaved.contains(entry.getKey())) {
                properties.put(entry.getKey(), entry.getValue());
            } else {
                for (String propertyName : propertyNames) {
                    if (propertyName.trim().equals(entry.getKey())) {
                        properties.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        ((ObjectNode) srcNode).removeAll();
        ((ObjectNode) srcNode).putAll(properties);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mapper.writeValue(bos, srcNode);

        final InputStream res = new ByteArrayInputStream(bos.toByteArray());
        bos.close();

        return res;
    }

    public String getETag(final String basePath) throws Exception {
        final URL etagURL = this.getClass().getResource(getAbsolutePath(basePath + "etag.txt", null));
        if (etagURL == null) {
            return null;
        } else {
            final InputStream is = new FileInputStream(new File(etagURL.toURI()));
            if (is.available() <= 0) {
                return null;
            } else {
                final String etag = IOUtils.toString(is);
                is.close();
                return etag;
            }
        }
    }

    public Response createResponse(final InputStream entity, final String etag, final Accept accept) {

        final Response.ResponseBuilder builder = Response.ok();

        if (StringUtils.isNotBlank(etag)) {
            builder.header("ETag", etag);
        }

        if (accept != null) {
            builder.header("Content-Type", accept.toString());
        }

        return builder.entity(entity).build();
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
        if (e instanceof UnsupportedMediaTypeException) {
            builder.status(Response.Status.UNSUPPORTED_MEDIA_TYPE);
            src = readFile("/unsupportedMediaType" + ext, null);
        } else if (e instanceof NotFoundException) {
            builder.status(Response.Status.NOT_FOUND);
            src = readFile("/notFound" + ext, null);
        } else {
            builder.status(Response.Status.BAD_REQUEST);
            src = readFile("/badRequest" + ext, null);
        }

        builder.entity(src);
        return builder.build();
    }
}
