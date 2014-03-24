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
package org.apache.olingo.fit.utils.v3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.fit.utils.Accept;
import org.apache.olingo.fit.utils.Commons;
import org.apache.olingo.fit.utils.Constants;
import org.apache.olingo.fit.utils.MetadataLinkInfo;
import org.apache.olingo.fit.utils.ODataVersion;
import org.apache.olingo.fit.utils.XmlElement;

public class XMLUtilities extends org.apache.olingo.fit.utils.AbstractXMLUtilities {

  public XMLUtilities() throws Exception {
    super(ODataVersion.v3);
  }

  @Override
  public void retrieveLinkInfoFromMetadata() throws Exception {

    final MetadataLinkInfo metadataLinkInfo = new MetadataLinkInfo();
    Commons.getLinkInfo().put(version, metadataLinkInfo);

    final InputStream metadata = fsManager.readFile(Constants.METADATA, Accept.XML);
    final XMLEventReader reader = getEventReader(metadata);

    try {
      while (true) {
        final Map.Entry<Integer, XmlElement> entitySetElement =
                extractElement(reader, null, Collections.<String>singletonList("EntitySet"),
                null, false, 0, -1, -1);

        retrieveLinks(entitySetElement.getValue(), metadataLinkInfo);
      }
    } catch (Exception e) {
    } finally {
      reader.close();
    }
  }

  private void retrieveLinks(final XmlElement entitySetElement, final MetadataLinkInfo metadataLinkInfo)
          throws Exception {

    final InputStream metadata = fsManager.readFile(Constants.METADATA, Accept.XML);

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    IOUtils.copy(metadata, bos);
    IOUtils.closeQuietly(metadata);

    final String entitySetName = entitySetElement.getStart().getAttributeByName(new QName("Name")).getValue().trim();
    final String entityType = entitySetElement.getStart().getAttributeByName(new QName("EntityType")).getValue().trim();

    final Collection<Map.Entry<String, String>> filter = new HashSet<Map.Entry<String, String>>();
    filter.add(new SimpleEntry<String, String>(
            "Name", entityType.substring(entityType.lastIndexOf(".") + 1, entityType.length())));
    filter.add(new SimpleEntry<String, String>("BaseType", entityType));

    final XMLEventReader reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

    final Map.Entry<Integer, XmlElement> entityTypeElement = extractElement(
            reader, null, Collections.<String>singletonList("EntityType"), filter, true, 0, -1, -1);

    final XMLEventReader entityReader = entityTypeElement.getValue().getContentReader();
    int size = 0;

    try {
      while (true) {
        final XmlElement navProperty =
                extractElement(entityReader, null, Collections.<String>singletonList("NavigationProperty"),
                null, false, 0, -1, -1).getValue();

        final String linkName = navProperty.getStart().getAttributeByName(new QName("Name")).getValue();
        final Map.Entry<String, Boolean> target = getTargetInfo(navProperty.getStart(), linkName);

        metadataLinkInfo.addLink(
                entitySetName,
                linkName,
                target.getKey(),
                target.getValue());

        size++;
      }
    } catch (Exception e) {
    } finally {
      entityReader.close();
    }

    if (size == 0) {
      metadataLinkInfo.addEntitySet(entitySetName);
    }
  }

  private Map.Entry<String, Boolean> getTargetInfo(final StartElement element, final String linkName)
          throws Exception {
    final InputStream metadata = fsManager.readFile(Constants.METADATA, Accept.XML);

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    IOUtils.copy(metadata, bos);
    IOUtils.closeQuietly(metadata);

    // ------------------------------------
    // Retrieve association
    // ------------------------------------
    XMLEventReader reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

    final String associationName = element.getAttributeByName(new QName("Relationship")).getValue();

    final Map.Entry<Integer, XmlElement> association = extractElement(
            reader, null, Collections.<String>singletonList("Association"),
            Collections.<Map.Entry<String, String>>singleton(new SimpleEntry<String, String>(
            "Name", associationName.substring(associationName.lastIndexOf(".") + 1))), false,
            0, 4, 4);

    reader.close();
    // ------------------------------------

    // ------------------------------------
    // check for feed or not from Association role
    // ------------------------------------
    InputStream associationContent = association.getValue().toStream();
    reader = getEventReader(associationContent);

    Map.Entry<Integer, XmlElement> associationEnd = extractElement(
            reader, null, Collections.<String>singletonList("End"),
            Collections.<Map.Entry<String, String>>singleton(new SimpleEntry<String, String>("Role", linkName)),
            false, 0, -1, -1);

    reader.close();
    IOUtils.closeQuietly(associationContent);

    final boolean feed = associationEnd.getValue().getStart().getAttributeByName(
            new QName("Multiplicity")).getValue().equals("*");
    // ------------------------------------

    // ------------------------------------
    // Retrieve target association set name
    // ------------------------------------
    reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

    final Map.Entry<Integer, XmlElement> associationSet = extractElement(
            reader, null, Collections.<String>singletonList("AssociationSet"),
            Collections.<Map.Entry<String, String>>singleton(new SimpleEntry<String, String>(
            "Association", associationName)), false, 0, -1, -1);

    reader.close();

    associationContent = associationSet.getValue().toStream();
    reader = getEventReader(associationContent);

    associationEnd = extractElement(
            reader, null, Collections.<String>singletonList("End"),
            Collections.<Map.Entry<String, String>>singleton(new SimpleEntry<String, String>("Role", linkName)),
            false, 0, -1, -1);

    reader.close();
    IOUtils.closeQuietly(associationContent);

    final String target = associationEnd.getValue().getStart().getAttributeByName(new QName("EntitySet")).getValue();
    // ------------------------------------

    return new SimpleEntry<String, Boolean>(target, feed);
  }
}
