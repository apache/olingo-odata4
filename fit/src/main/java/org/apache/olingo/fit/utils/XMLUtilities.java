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
package org.apache.olingo.fit.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.NotFoundException;
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.metadata.NavigationProperty;

public class XMLUtilities extends AbstractUtilities {

  private static final Pattern ENTITY_URI_PATTERN = Pattern.compile(".*\\/.*\\(.*\\)");

  protected static XMLInputFactory ifactory = null;

  protected static XMLOutputFactory ofactory = null;

  public XMLUtilities(final Metadata metadata) throws IOException {
    super(metadata);
  }

  @Override
  protected Accept getDefaultFormat() {
    return Accept.ATOM;
  }

  protected XMLEventReader getEventReader(final InputStream is) throws XMLStreamException {
    if (ifactory == null) {
      ifactory = XMLInputFactory.newInstance();
    }

    return ifactory.createXMLEventReader(new InputStreamReader(is, Constants.DECODER));
  }

  protected static XMLEventWriter getEventWriter(final OutputStream os) throws XMLStreamException {
    if (ofactory == null) {
      ofactory = XMLOutputFactory.newInstance();
    }

    return ofactory.createXMLEventWriter(os, "UTF-8");
  }

  private void writeEvent(final XMLEvent event, final XMLEventWriter writer) {
    if (writer != null) {
      try {
        writer.add(event);
      } catch (XMLStreamException e) {
        LOG.error("Error writing event {}", event, e);
      }
    }
  }

  private void skipElement(
      final StartElement start,
      final XMLEventReader reader,
      final XMLEventWriter writer,
      final boolean excludeStart)
      throws Exception {

    if (!excludeStart) {
      writeEvent(start, writer);
    }

    int depth = 1;
    boolean found = false;

    while (reader.hasNext() && !found) {
      final XMLEvent event = reader.nextEvent();

      writeEvent(event, writer);

      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
        depth++;
      } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT) {
        depth--;
        found = depth == 0 && start.getName().equals(event.asEndElement().getName());
      }
    }
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected InputStream addLinks(
      final String entitySetName, final String entitykey, final InputStream is, final Set<String> links)
      throws Exception {

    // -----------------------------------------
    // 0. Build reader and writer
    // -----------------------------------------
    final XMLEventReader reader = getEventReader(is);
    final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final XMLEventWriter writer = getEventWriter(bos);
    // -----------------------------------------
    final Map.Entry<Integer, XMLElement> entry =
        extractElement(reader, writer, Collections.singletonList("entry"), 0, 1, 1);

    writer.add(entry.getValue().getStart());

    final Map<String, NavigationProperty> navigationProperties = metadata.getNavigationProperties(entitySetName);

    // add for links
    for (String link : links) {
      final Set<Attribute> attributes = new HashSet<Attribute>();
      attributes.add(eventFactory.createAttribute(new QName("title"), link));
      attributes.add(eventFactory.createAttribute(new QName("href"),
          Commons.getLinksURI(entitySetName, entitykey, link)));
      attributes.add(eventFactory.createAttribute(new QName("rel"),
          Constants.get(ConstantKey.ATOM_LINK_REL) + link));
      attributes.add(eventFactory.createAttribute(new QName("type"),
          navigationProperties.get(link).isEntitySet()
              ? Constants.get(ConstantKey.ATOM_LINK_FEED)
              : Constants.get(ConstantKey.ATOM_LINK_ENTRY)));

      writer.add(eventFactory.createStartElement(
          new QName(Constants.get(ConstantKey.LINK)), attributes.iterator(), null));
      writer.add(eventFactory.createEndElement(new QName(Constants.get(ConstantKey.LINK)), null));
    }

    writer.add(entry.getValue().getContentReader());
    writer.add(entry.getValue().getEnd());
    writer.add(reader);
    IOUtils.closeQuietly(is);

    writer.flush();
    writer.close();
    reader.close();

    return new ByteArrayInputStream(bos.toByteArray());
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected Set<String> retrieveAllLinkNames(final InputStream is) throws Exception {
    final Set<String> links = new HashSet<String>();

    final XMLEventReader reader = getEventReader(is);

    try {

      int startDepth = 0;

      while (true) {
        final Map.Entry<Integer, XMLElement> linkInfo =
            extractElement(reader, null,
                Collections.<String> singletonList(Constants.get(ConstantKey.LINK)), startDepth, 2, 2);

        startDepth = linkInfo.getKey();

        links.add(linkInfo.getValue().getStart().getAttributeByName(new QName("title")).getValue());
      }
    } catch (Exception ignore) {
      // ignore
    } finally {
      reader.close();
      IOUtils.closeQuietly(is);
    }

    return links;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected NavigationLinks retrieveNavigationInfo(
      final String entitySetName, final InputStream is)
      throws Exception {

    final NavigationLinks links = new NavigationLinks();

    final XMLEventReader reader = getEventReader(is);

    try {
      final List<Map.Entry<String, String>> filter = new ArrayList<Map.Entry<String, String>>();
      filter.add(new AbstractMap.SimpleEntry<String, String>("type", "application/atom+xml;type=entry"));
      filter.add(new AbstractMap.SimpleEntry<String, String>("type", "application/atom+xml;type=feed"));

      int startDepth = 0;

      while (true) {
        // a. search for link with type attribute equals to "application/atom+xml;type=entry/feed"
        final Map.Entry<Integer, XMLElement> linkInfo = extractElement(
            reader, null, Collections.<String> singletonList(Constants.get(ConstantKey.LINK)),
            filter, true, startDepth, 2, 2);
        final XMLElement link = linkInfo.getValue();
        startDepth = linkInfo.getKey();

        final String title = link.getStart().getAttributeByName(new QName("title")).getValue();

        final Attribute hrefAttr = link.getStart().getAttributeByName(new QName("href"));
        final String href = hrefAttr == null ? null : hrefAttr.getValue();

        try {
          final XMLElement inlineElement =
              extractElement(link.getContentReader(), null,
                  Collections.<String> singletonList(Constants.get(ConstantKey.INLINE)), 0, -1, -1).
                  getValue();
          final XMLEventReader inlineReader = inlineElement.getContentReader();

          try {
            while (true) {
              final XMLElement entry =
                  extractElement(inlineReader, null, Collections.<String> singletonList("entry"), 0, -1, -1).
                      getValue();
              links.addInlines(title, entry.toStream());
            }
          } catch (Exception e) {
            // Reached the end of document
          }

          inlineReader.close();
        } catch (Exception ignore) {
          // inline element not found (inlines are not mondatory).
          if (StringUtils.isNotBlank(href) && ENTITY_URI_PATTERN.matcher(href).matches()) {
            links.addLinks(title, href.substring(href.lastIndexOf('/') + 1));
          }
        }
      }
    } catch (Exception ignore) {
      // ignore
    } finally {
      reader.close();
    }

    return links;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected InputStream normalizeLinks(
      final String entitySetName, final String entityKey, final InputStream is, final NavigationLinks links)
      throws Exception {

    // -----------------------------------------
    // 0. Build reader and writer
    // -----------------------------------------
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    IOUtils.copy(is, bos);
    is.close();

    final ByteArrayOutputStream tmpBos = new ByteArrayOutputStream();
    final XMLEventWriter writer = getEventWriter(tmpBos);

    final XMLEventReader reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));
    // -----------------------------------------

    // -----------------------------------------
    // 1. Normalize links
    // -----------------------------------------
    final Set<String> added = new HashSet<String>();

    try {
      final List<Map.Entry<String, String>> filter = new ArrayList<Map.Entry<String, String>>();
      filter.add(new AbstractMap.SimpleEntry<String, String>("type", "application/atom+xml;type=entry"));
      filter.add(new AbstractMap.SimpleEntry<String, String>("type", "application/atom+xml;type=feed"));

      Map.Entry<Integer, XMLElement> linkInfo = null;

      while (true) {
        // a. search for link with type attribute equals to "application/atom+xml;type=entry/feed"
        linkInfo = extractElement(
            reader, writer,
            Collections.<String> singletonList(Constants.get(ConstantKey.LINK)), filter, true,
            linkInfo == null ? 0 : linkInfo.getKey(), 2, 2);
        final XMLElement link = linkInfo.getValue();

        final String title = link.getStart().getAttributeByName(new QName("title")).getValue();

        if (!added.contains(title)) {
          added.add(title);

          final String normalizedLink = String.format(
              "<link href=\"%s(%s)/%s\" rel=\"%s\" title=\"%s\" type=\"%s\"/>",
              entitySetName,
              entityKey,
              title,
              link.getStart().getAttributeByName(new QName("rel")).getValue(),
              title,
              link.getStart().getAttributeByName(new QName("type")).getValue());

          addAtomElement(IOUtils.toInputStream(normalizedLink, Constants.ENCODING), writer);
        }
      }
    } catch (Exception ignore) {
      // ignore
    } finally {
      writer.close();
      reader.close();
    }
    // -----------------------------------------

    // -----------------------------------------
    // 2. Add/replace edit link
    // -----------------------------------------
    final InputStream content = addEditLink(
        new ByteArrayInputStream(tmpBos.toByteArray()),
        entitySetName,
        Constants.get(ConstantKey.DEFAULT_SERVICE_URL) + entitySetName + "(" + entityKey + ")");
    // -----------------------------------------

    // -----------------------------------------
    // 3. Add content element if missing
    // -----------------------------------------
    return addAtomContent(
        content,
        entitySetName,
        Constants.get(ConstantKey.DEFAULT_SERVICE_URL) + entitySetName + "(" + entityKey + ")");
    // -----------------------------------------

  }

  public XMLElement getXmlElement(
      final StartElement start,
      final XMLEventReader reader)
      throws Exception {

    final XMLElement res = new XMLElement();
    res.setStart(start);

    final Charset encoding = Charset.forName(org.apache.olingo.commons.api.Constants.UTF8);
    final ByteArrayOutputStream content = new ByteArrayOutputStream();
    final OutputStreamWriter writer = new OutputStreamWriter(content, encoding);

    int depth = 1;

    while (reader.hasNext() && depth > 0) {
      final XMLEvent event = reader.nextEvent();

      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
        depth++;
      } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT) {
        depth--;
      }

      if (depth == 0) {
        res.setEnd(event.asEndElement());
      } else {
        event.writeAsEncodedUnicode(writer);
      }
    }

    writer.flush();
    writer.close();

    res.setContent(new ByteArrayInputStream(content.toByteArray()));

    return res;
  }

  private void addAtomElement(
      final InputStream content,
      final XMLEventWriter writer)
      throws Exception {
    final XMLEventReader reader = getEventReader(content);

    final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent newLine = eventFactory.createSpace("\n");

    try {
      writer.add(newLine);

      while (reader.hasNext()) {
        final XMLEvent event = reader.nextEvent();

        if (event.getEventType() != XMLStreamConstants.START_DOCUMENT
            && event.getEventType() != XMLStreamConstants.END_DOCUMENT
            && event.getEventType() != XMLStreamConstants.COMMENT) {
          writer.add(event);
        }
      }
      writer.add(newLine);
    } finally {
      reader.close();
      IOUtils.closeQuietly(content);
    }
  }

  @Override
  public InputStream addEditLink(
      final InputStream content, final String title, final String href)
      throws Exception {

    final ByteArrayOutputStream copy = new ByteArrayOutputStream();
    IOUtils.copy(content, copy);
    IOUtils.closeQuietly(content);

    XMLEventReader reader = getEventReader(new ByteArrayInputStream(copy.toByteArray()));

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XMLEventWriter writer = getEventWriter(bos);

    final String editLinkElement = String.format("<link rel=\"edit\" title=\"%s\" href=\"%s\" />", title, href);

    try {
      // check edit link existence
      extractElement(reader, writer, Collections.<String> singletonList(Constants.get(ConstantKey.LINK)),
          Collections.<Map.Entry<String, String>> singletonList(
              new AbstractMap.SimpleEntry<String, String>("rel", "edit")), false, 0, -1, -1);

      addAtomElement(IOUtils.toInputStream(editLinkElement, Constants.ENCODING), writer);
      writer.add(reader);

    } catch (Exception e) {
      reader.close();
      reader = getEventReader(new ByteArrayInputStream(copy.toByteArray()));

      bos = new ByteArrayOutputStream();
      writer = getEventWriter(bos);

      final XMLElement entryElement =
          extractElement(reader, writer, Collections.<String> singletonList("entry"), 0, 1, 1).getValue();

      writer.add(entryElement.getStart());

      addAtomElement(IOUtils.toInputStream(editLinkElement, Constants.ENCODING), writer);

      writer.add(entryElement.getContentReader());
      writer.add(entryElement.getEnd());

      writer.add(reader);

      writer.flush();
      writer.close();
    } finally {
      reader.close();
    }

    return new ByteArrayInputStream(bos.toByteArray());
  }

  @Override
  public InputStream addOperation(final InputStream content, final String name, final String metaAnchor,
      final String href) throws Exception {

    final ByteArrayOutputStream copy = new ByteArrayOutputStream();
    IOUtils.copy(content, copy);
    IOUtils.closeQuietly(content);

    final String action = String.format("<m:action metadata=\"%s%s\" title=\"%s\" target=\"%s\"/>",
        Constants.get(ConstantKey.DEFAULT_SERVICE_URL), metaAnchor, name, href);
    final String newContent = new String(copy.toByteArray(), "UTF-8").replaceAll("\\<content ", action + "\\<content ");

    return IOUtils.toInputStream(newContent, "UTF-8");
  }

  private InputStream addAtomContent(
      final InputStream content, final String title, final String href)
      throws Exception {

    final ByteArrayOutputStream copy = new ByteArrayOutputStream();
    IOUtils.copy(content, copy);

    IOUtils.closeQuietly(content);

    XMLEventReader reader = getEventReader(new ByteArrayInputStream(copy.toByteArray()));

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XMLEventWriter writer = getEventWriter(bos);

    try {
      // check edit link existence
      XMLElement contentElement =
          extractElement(reader, writer, Collections.<String> singletonList("content"), 0, 2, 2).getValue();
      writer.add(contentElement.getStart());
      writer.add(contentElement.getContentReader());
      writer.add(contentElement.getEnd());
      writer.add(reader);
    } catch (Exception e) {
      reader.close();
      reader = getEventReader(new ByteArrayInputStream(copy.toByteArray()));

      bos = new ByteArrayOutputStream();
      writer = getEventWriter(bos);

      if (isMediaContent(title)) {
        final XMLElement entryElement =
            extractElement(reader, writer, Collections.<String> singletonList("entry"), 0, 1, 1).getValue();

        writer.add(entryElement.getStart());
        writer.add(entryElement.getContentReader());

        addAtomElement(
            IOUtils.toInputStream(String.format("<content type=\"*/*\" src=\"%s/$value\" />", href)),
            writer);

        writer.add(entryElement.getEnd());
      } else {
        try {
          final XMLElement entryElement =
              extractElement(reader, writer, Collections.<String> singletonList(
                  Constants.get(ConstantKey.PROPERTIES)), 0, 2, 3).getValue();

          addAtomElement(
              IOUtils.toInputStream("<content type=\"application/xml\">"),
              writer);

          writer.add(entryElement.getStart());
          writer.add(entryElement.getContentReader());
          writer.add(entryElement.getEnd());

          addAtomElement(
              IOUtils.toInputStream("</content>"),
              writer);
        } catch (Exception nf) {
          reader.close();
          reader = getEventReader(new ByteArrayInputStream(copy.toByteArray()));

          bos = new ByteArrayOutputStream();
          writer = getEventWriter(bos);

          final XMLElement entryElement =
              extractElement(reader, writer, Collections.<String> singletonList("entry"), 0, 1, 1).getValue();
          writer.add(entryElement.getStart());
          writer.add(entryElement.getContentReader());

          addAtomElement(
              IOUtils.toInputStream("<content type=\"application/xml\"/>"),
              writer);

          writer.add(entryElement.getEnd());
        }
      }

      writer.add(reader);

      writer.flush();
      writer.close();
    } finally {
      reader.close();
    }

    return new ByteArrayInputStream(bos.toByteArray());
  }

  public int countAllElements(final String entitySetName) throws Exception {
    final String basePath = entitySetName + File.separatorChar;
    int count = countFeedElements(fsManager.readFile(basePath + Constants.get(ConstantKey.FEED), Accept.XML),
        "entry");

    final String skipTokenDirPath =
        fsManager.getAbsolutePath(basePath + Constants.get(ConstantKey.SKIP_TOKEN),
            null);

    try {
      final FileObject skipToken = fsManager.resolve(skipTokenDirPath);
      final FileObject[] files = fsManager.find(skipToken, Accept.XML.getExtension().substring(1));

      for (FileObject file : files) {
        count += countFeedElements(fsManager.readFile(
            basePath + Constants.get(ConstantKey.SKIP_TOKEN) + File.separatorChar
                + file.getName().getBaseName(), null), "entry");
      }
    } catch (FileSystemException fse) {
      LOG.debug("Resource path '{}' not found", skipTokenDirPath);
    }

    return count;
  }

  private int countFeedElements(final InputStream is, final String elementName) throws XMLStreamException {
    final XMLEventReader reader = getEventReader(is);

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

  public Map.Entry<Integer, XMLElement> extractElement(
      final XMLEventReader reader, final XMLEventWriter writer, final List<String> path,
      final int startPathPos, final int minPathPos, final int maxPathPos)
      throws Exception {
    return extractElement(reader, writer, path, null, false, startPathPos, minPathPos, maxPathPos);
  }

  public Map.Entry<Integer, XMLElement> extractElement(
      final XMLEventReader reader, final XMLEventWriter writer, final List<String> path,
      final Collection<Map.Entry<String, String>> filter,
      final boolean filterInOr,
      final int startPathPos, final int minPathPos, final int maxPathPos)
      throws Exception {

    StartElement start = null;
    int searchFor = 0;
    int depth = startPathPos;

    // Current inspected element
    String current = null;

    // set defaults
    final List<String> pathElementNames = path == null ? Collections.<String> emptyList() : path;
    final Collection<Map.Entry<String, String>> filterAttrs =
        filter == null ? Collections.<Map.Entry<String, String>> emptySet() : filter;

    while (reader.hasNext() && start == null) {
      final XMLEvent event = reader.nextEvent();

      if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
        depth++;

        if (current != null || ((minPathPos < 0 || minPathPos <= depth) && (maxPathPos < 0 || depth <= maxPathPos))) {
          if (pathElementNames.isEmpty()
              || pathElementNames.get(searchFor).trim().equals(event.asStartElement().getName().getLocalPart())) {

            if (searchFor < pathElementNames.size() - 1) {
              // path exploring not completed
              writeEvent(event, writer);
              current = pathElementNames.get(searchFor).trim();
              searchFor++;
            } else {

              // path exploring completed ... evaluate filter about path element name attribute
              boolean match = filterAttrs.isEmpty() || !filterInOr;

              for (Map.Entry<String, String> filterAttr : filterAttrs) {
                final Attribute attr = event.asStartElement().getAttributeByName(new QName(filterAttr.getKey().trim()));

                if (attr == null || !filterAttr.getValue().trim().equals(attr.getValue())) {
                  match = filterInOr ? match : false;
                } else {
                  match = filterInOr ? true : match;
                }
              }

              if (match) {
                // found searched element
                start = event.asStartElement();
              } else {
                skipElement(event.asStartElement(), reader, writer, false);
                depth--;
              }
            }
          } else if (current == null) {
            writeEvent(event, writer);
          } else {
            // skip element
            skipElement(event.asStartElement(), reader, writer, false);
            depth--;
          }
        } else {
          writeEvent(event, writer);
        }

      } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT) {
        depth--;

        writeEvent(event, writer);

        if (event.asEndElement().getName().getLocalPart().equals(current)) {
          // back step ....
          searchFor--;
          current = searchFor > 0 ? pathElementNames.get(searchFor - 1).trim() : null;
        }
      } else {
        writeEvent(event, writer);
      }
    }

    if (start == null) {
      throw new NotFoundException();
    }

    return new SimpleEntry<Integer, XMLElement>(Integer.valueOf(depth - 1), getXmlElement(start, reader));
  }

  public InputStream addAtomInlinecount(final InputStream feed, final int count) throws Exception {
    final XMLEventReader reader = getEventReader(feed);

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final XMLEventWriter writer = getEventWriter(bos);

    try {

      final XMLElement feedElement =
          extractElement(reader, writer, Collections.<String> singletonList("feed"), 0, 1, 1).getValue();

      writer.add(feedElement.getStart());
      addAtomElement(IOUtils.toInputStream(String.format("<m:count>%d</m:count>", count), Constants.ENCODING), writer);
      writer.add(feedElement.getContentReader());
      writer.add(feedElement.getEnd());

      while (reader.hasNext()) {
        writer.add(reader.nextEvent());
      }

    } finally {
      writer.flush();
      writer.close();
      reader.close();
      IOUtils.closeQuietly(feed);
    }

    return new ByteArrayInputStream(bos.toByteArray());
  }

  @Override
  public InputStream selectEntity(final InputStream entity, final String[] propertyNames) throws Exception {
    final XMLEventReader reader = getEventReader(entity);

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final XMLEventWriter writer = getEventWriter(bos);

    final List<String> found = new ArrayList<String>(Arrays.asList(propertyNames));

    boolean inProperties = false;
    boolean writeCurrent = true;
    Boolean writeNext = null;
    String currentName = null;

    final List<String> fieldToBeSaved = new ArrayList<String>(Arrays.asList(propertyNames));

    while (reader.hasNext()) {
      final XMLEvent event = reader.nextEvent();
      if (event.getEventType() == XMLStreamConstants.START_ELEMENT
          && Constants.get(ConstantKey.LINK).equals(event.asStartElement().getName().getLocalPart())
          && !fieldToBeSaved.contains(
              event.asStartElement().getAttributeByName(new QName("title")).getValue())
          && !"edit".equals(event.asStartElement().getAttributeByName(new QName("rel")).getValue())) {
        writeCurrent = false;
      } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
          && Constants.get(ConstantKey.LINK).equals(event.asEndElement().getName().getLocalPart())) {
        writeNext = true;
      } else if (event.getEventType() == XMLStreamConstants.START_ELEMENT
          && (Constants.get(ConstantKey.PROPERTIES)).equals(
              event.asStartElement().getName().getLocalPart())) {
        writeCurrent = true;
        writeNext = false;
        inProperties = true;
      } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
          && (Constants.get(ConstantKey.PROPERTIES)).equals(
              event.asEndElement().getName().getLocalPart())) {
        writeCurrent = true;
      } else if (inProperties) {
        if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
          final String elementName = event.asStartElement().getName().getLocalPart();

          for (String propertyName : propertyNames) {
            if ((Constants.get(ConstantKey.ATOM_PROPERTY_PREFIX) + propertyName.trim()).equals(elementName)) {
              writeCurrent = true;
              found.remove(propertyName);
              currentName = propertyName;
            }
          }

        } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
            && StringUtils.isNotBlank(currentName)
            && (Constants.get(ConstantKey.ATOM_PROPERTY_PREFIX) + currentName.trim()).equals(
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
    IOUtils.closeQuietly(entity);

    // Do not raise any exception in order to support FC properties as well
    // if (!found.isEmpty()) {
    // throw new Exception(String.format("Could not find a properties '%s'", found));
    // }
    return new ByteArrayInputStream(bos.toByteArray());
  }

  @Override
  public InputStream readEntities(
      final List<String> links, final String linkName, final String next, final boolean forceFeed)
      throws Exception {

    if (links.isEmpty()) {
      throw new NotFoundException();
    }

    final Charset encoding = Charset.forName("UTF-8");

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final OutputStreamWriter writer = new OutputStreamWriter(bos, encoding);

    writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>".toCharArray());

    if (forceFeed || links.size() > 1) {
      // build a feed

      writer.write(("<feed xml:base=\"" + Constants.get(ConstantKey.DEFAULT_SERVICE_URL) + "\" "
          + "xmlns=\"http://www.w3.org/2005/Atom\" "
          + "xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" "
          + "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\">")
          .toCharArray());

      writer.write(("<id>" + Constants.get(ConstantKey.DEFAULT_SERVICE_URL) + "entityset(entityid)/"
          + linkName
          + "</id>").toCharArray());

      writer.write(("<title type=\"text\">" + linkName + "</title>").toCharArray());
      writer.write("<updated>2014-03-03T13:40:49Z</updated>".toCharArray());
      writer.write(("<link rel=\"self\" title=\"" + linkName + "\" href=\"" + linkName + "\" />").toCharArray());
    }

    for (String link : links) {
      try {
        final Map.Entry<String, String> uri = Commons.parseEntityURI(link);

        final XMLElement entry =
            extractElement(
                getEventReader(readEntity(uri.getKey(), uri.getValue(), Accept.ATOM).getValue()),
                null,
                Collections.<String> singletonList("entry"),
                0, 1, 1).getValue();

        IOUtils.copy(entry.toStream(), writer, encoding);
      } catch (Exception e) {
        // log and ignore link
        LOG.warn("Error parsing uri {}", link, e);
      }
    }

    if (forceFeed || links.size() > 1) {

      if (StringUtils.isNotBlank(next)) {
        writer.write(String.format("<link rel=\"next\" href=\"%s\" />", next).toCharArray());
      }

      writer.write("</feed>".toCharArray());
    }

    writer.flush();
    writer.close();

    return new ByteArrayInputStream(bos.toByteArray());
  }

  @Override
  public Map<String, InputStream> getChanges(final InputStream src) throws Exception {
    final Map<String, InputStream> res = new HashMap<String, InputStream>();

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    IOUtils.copy(src, bos);
    IOUtils.closeQuietly(src);

    // retrieve properties ...
    XMLEventReader reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

    final Map.Entry<Integer, XMLElement> propertyElement =
        extractElement(reader, null,
            Collections.<String> singletonList(Constants.get(ConstantKey.PROPERTIES)), 0, 2, 3);
    reader.close();

    reader = propertyElement.getValue().getContentReader();

    try {
      while (true) {
        final XMLElement property = extractElement(reader, null, null, 0, -1, -1).getValue();
        res.put(property.getStart().getName().getLocalPart(), property.toStream());
      }
    } catch (Exception ignore) {
      // end
    }

    reader.close();

    // retrieve links ...
    reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

    try {
      int pos = 0;
      while (true) {
        final Map.Entry<Integer, XMLElement> linkElement =
            extractElement(reader, null,
                Collections.<String> singletonList(Constants.get(ConstantKey.LINK)), pos, 2, 2);

        res.put("[Constants.get(ConstantKey.LINK)]"
            + linkElement.getValue().getStart().getAttributeByName(new QName("title")).getValue(),
            linkElement.getValue().toStream());

        pos = linkElement.getKey();
      }
    } catch (Exception ignore) {
      // end
    }

    return res;
  }

  @Override
  protected InputStream replaceLink(
      final InputStream toBeChanged, final String linkName, final InputStream replacement)
      throws Exception {
    final XMLEventReader reader = getEventReader(toBeChanged);

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final XMLEventWriter writer = getEventWriter(bos);

    final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent newLine = eventFactory.createSpace("\n");

    try {
      final XMLElement linkElement =
          extractElement(reader, writer,
              Collections.<String> singletonList(Constants.get(ConstantKey.LINK)),
              Collections.<Map.Entry<String, String>> singletonList(
                  new SimpleEntry<String, String>("title", linkName)), false, 0, -1, -1).getValue();
      writer.add(linkElement.getStart());

      // ------------------------------------------
      // write inline ...
      // ------------------------------------------
      writer.add(newLine);
      writer.add(eventFactory.createStartElement("m", null, "inline"));

      addAtomElement(replacement, writer);

      writer.add(eventFactory.createEndElement("m", null, "inline"));
      writer.add(newLine);
      // ------------------------------------------

      writer.add(linkElement.getEnd());

      writer.add(reader);
      writer.flush();
      writer.close();
    } finally {
      reader.close();
      IOUtils.closeQuietly(toBeChanged);
    }

    return new ByteArrayInputStream(bos.toByteArray());
  }

  @Override
  public Map.Entry<String, List<String>> extractLinkURIs(
      final String entitySetName, final String entityId, final String linkName)
      throws Exception {

    final LinkInfo links = readLinks(entitySetName, entityId, linkName, Accept.XML);
    return extractLinkURIs(links.getLinks());
  }

  @Override
  public Map.Entry<String, List<String>> extractLinkURIs(final InputStream is)
      throws Exception {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    IOUtils.copy(is, bos);
    IOUtils.closeQuietly(is);

    XMLEventReader reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));
    final List<String> links = new ArrayList<String>();
    try {
      while (true) {
        links.add(IOUtils.toString(extractElement(reader, null, Collections.<String> singletonList("uri"), 0, -1, -1).
            getValue().getContent()));
      }
    } catch (Exception ignore) {
      // End document reached ...
    }
    reader.close();

    String next;

    reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));
    try {
      next = IOUtils.toString(extractElement(reader, null, Collections.<String> singletonList("next"), 0, -1, -1).
          getValue().getContent());
    } catch (Exception ignore) {
      // next link is not mandatory
      next = null;
    }
    reader.close();

    return new AbstractMap.SimpleEntry<String, List<String>>(next, links);
  }

  @Override
  public InputStream replaceProperty(
      final InputStream src, final InputStream replacement, final List<String> path, final boolean justValue)
      throws Exception {

    final List<String> pathElements = new ArrayList<String>();

    for (String element : path) {
      pathElements.add(Constants.get(ConstantKey.ATOM_PROPERTY_PREFIX) + element);
    }

    final XMLEventReader reader = getEventReader(src);

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final XMLEventWriter writer = getEventWriter(bos);

    final Map.Entry<Integer, XMLElement> element = extractElement(reader, writer, pathElements, 0, 3, 4);

    if (justValue) {
      writer.add(element.getValue().getStart());
    }

    final XMLEventReader changesReader = new XMLEventReaderWrapper(replacement);

    while (changesReader.hasNext()) {
      final XMLEvent event = changesReader.nextEvent();
      if (event.isStartElement() && event.asStartElement().getName().equals(element.getValue().getStart().getName())) {
        writer.add(element.getValue().getStart());
        writer.add(changesReader);
      } else {
        writer.add(event);
      }
    }

    changesReader.close();
    IOUtils.closeQuietly(replacement);

    if (justValue) {
      writer.add(element.getValue().getEnd());
    }

    writer.add(reader);

    reader.close();
    IOUtils.closeQuietly(src);

    writer.flush();
    writer.close();

    return new ByteArrayInputStream(bos.toByteArray());
  }

  @Override
  public InputStream deleteProperty(final InputStream src, final List<String> path) throws Exception {
    final XMLEventReader reader = getEventReader(src);

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final XMLEventWriter writer = getEventWriter(bos);

    final XMLEventReader changesReader =
        new XMLEventReaderWrapper(IOUtils.toInputStream(
            String.format("<%s m:null=\"true\" />", path.get(path.size() - 1)), Constants.ENCODING));

    writer.add(changesReader);
    changesReader.close();

    writer.add(reader);

    reader.close();
    IOUtils.closeQuietly(src);

    writer.flush();
    writer.close();

    return new ByteArrayInputStream(bos.toByteArray());
  }
}
