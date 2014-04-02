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
package org.apache.olingo.fit.utils.v4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.fit.utils.Accept;
import org.apache.olingo.fit.utils.Commons;
import org.apache.olingo.fit.utils.ConstantKey;
import org.apache.olingo.fit.utils.Constants;
import org.apache.olingo.fit.utils.MetadataLinkInfo;
import org.apache.olingo.fit.utils.ODataVersion;
import org.apache.olingo.fit.utils.XmlElement;

public class XMLUtilities extends org.apache.olingo.fit.utils.AbstractXMLUtilities {

  public XMLUtilities() throws Exception {
    super(ODataVersion.v4);
  }

  @Override
  public void retrieveLinkInfoFromMetadata() throws Exception {

    final MetadataLinkInfo metadataLinkInfo = new MetadataLinkInfo();
    Commons.getLinkInfo().put(version, metadataLinkInfo);

    final InputStream metadata = fsManager.readFile(Constants.get(version, ConstantKey.METADATA), Accept.XML);

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    IOUtils.copy(metadata, bos);
    IOUtils.closeQuietly(metadata);

    XMLEventReader reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

    final Set<String> singletons = new HashSet<String>();

    try {
      while (true) {
        final Map.Entry<Integer, XmlElement> entitySetElement =
                extractElement(reader, null, Collections.<String>singletonList("Singleton"),
                null, false, 0, -1, -1);

        final String entitySetName =
                entitySetElement.getValue().getStart().getAttributeByName(new QName("Name")).getValue().trim();
        singletons.add(entitySetName);
      }
    } catch (Exception e) {
    } finally {
      reader.close();
    }

    reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

    try {
      while (true) {
        final Map.Entry<Integer, XmlElement> entitySetElement =
                extractElement(reader, null, Collections.<String>singletonList("EntitySet"),
                null, false, 0, -1, -1);

        retrieveLinks(entitySetElement.getValue(), metadataLinkInfo, singletons);
      }
    } catch (Exception e) {
    } finally {
      reader.close();
    }

    reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

    try {
      while (true) {
        final Map.Entry<Integer, XmlElement> entitySetElement =
                extractElement(reader, null, Collections.<String>singletonList("Singleton"),
                null, false, 0, -1, -1);

        retrieveLinks(entitySetElement.getValue(), metadataLinkInfo, singletons);
      }
    } catch (Exception e) {
    } finally {
      reader.close();
    }
  }

  private void retrieveLinks(
          final XmlElement entitySetElement, final MetadataLinkInfo metadataLinkInfo, final Set<String> singletons)
          throws Exception {

    final String entitySetName = entitySetElement.getStart().getAttributeByName(new QName("Name")).getValue().trim();

    final XMLEventReader entityReader = entitySetElement.getContentReader();
    int size = 0;

    try {
      while (true) {
        final XmlElement navProperty =
                extractElement(entityReader, null, Collections.<String>singletonList("NavigationPropertyBinding"),
                null, false, 0, -1, -1).getValue();

        final String linkName = navProperty.getStart().getAttributeByName(new QName("Path")).getValue();
        final String target = navProperty.getStart().getAttributeByName(new QName("Target")).getValue();
        final boolean feed = !singletons.contains(target);

        metadataLinkInfo.addLink(
                entitySetName,
                linkName,
                target,
                feed);

        size++;
      }
    } catch (Exception e) {
    } finally {
      entityReader.close();
    }

    if (size == 0) {
      metadataLinkInfo.addEntitySet(entitySetName);
    }

    if (singletons.contains(entitySetName)) {
      metadataLinkInfo.setSingleton(entitySetName);
    }
  }
}
