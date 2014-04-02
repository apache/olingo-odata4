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
package org.apache.olingo.commons.core.data;

import org.apache.olingo.commons.api.data.Container;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.data.v3.XMLLinkCollectionImpl;

public class AtomDeserializer extends AbstractAtomDealer {

  private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

  private final AtomPropertyDeserializer propDeserializer;

  public AtomDeserializer(final ODataServiceVersion version) {
    super(version);
    this.propDeserializer = new AtomPropertyDeserializer(version);
  }

  private Container<AtomPropertyImpl> property(final InputStream input) throws XMLStreamException {
    final XMLEventReader reader = FACTORY.createXMLEventReader(input);
    final StartElement start = skipBeforeFirstStartElement(reader);
    return getContainer(start, propDeserializer.deserialize(reader, start));
  }

  private StartElement skipBeforeFirstStartElement(final XMLEventReader reader) throws XMLStreamException {
    StartElement startEvent = null;
    while (reader.hasNext() && startEvent == null) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement()) {
        startEvent = event.asStartElement();
      }
    }
    if (startEvent == null) {
      throw new IllegalArgumentException("Cannot find any XML start element");
    }

    return startEvent;
  }

  private void common(final XMLEventReader reader, final StartElement start,
          final AbstractODataObject object, final String key) throws XMLStreamException {

    boolean foundEndElement = false;
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        try {
          object.setCommonProperty(key, event.asCharacters().getData());
        } catch (ParseException e) {
          throw new XMLStreamException("While parsing Atom entry or feed common elements", e);
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }
  }

  private void inline(final XMLEventReader reader, final StartElement start, final LinkImpl link)
          throws XMLStreamException {

    boolean foundEndElement = false;
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && inlineQName.equals(event.asStartElement().getName())) {
        StartElement inline = null;
        while (reader.hasNext() && inline == null) {
          final XMLEvent innerEvent = reader.peek();
          if (innerEvent.isCharacters() && innerEvent.asCharacters().isWhiteSpace()) {
            reader.nextEvent();
          } else if (innerEvent.isStartElement()) {
            inline = innerEvent.asStartElement();
          }
        }
        if (inline != null) {
          if (Constants.QNAME_ATOM_ELEM_ENTRY.equals(inline.getName())) {
            link.setInlineEntry(entry(reader, inline));
          }
          if (Constants.QNAME_ATOM_ELEM_FEED.equals(inline.getName())) {
            link.setInlineFeed(feed(reader, inline));
          }
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }
  }

  private Container<XMLLinkCollectionImpl> linkCollection(final InputStream input) throws XMLStreamException {
    final XMLEventReader reader = FACTORY.createXMLEventReader(input);
    final StartElement start = skipBeforeFirstStartElement(reader);
    return getContainer(start, linkCollection(reader, start));
  }

  private XMLLinkCollectionImpl linkCollection(final XMLEventReader reader, final StartElement start)
          throws XMLStreamException {

    final XMLLinkCollectionImpl linkCollection = new XMLLinkCollectionImpl();

    boolean isURI = false;
    boolean isNext = false;
    while (reader.hasNext()) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement()) {
        isURI = uriQName.equals(event.asStartElement().getName());
        isNext = nextQName.equals(event.asStartElement().getName());
      }

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        if (isURI) {
          linkCollection.getLinks().add(URI.create(event.asCharacters().getData()));
          isURI = false;
        } else if (isNext) {
          linkCollection.setNext(URI.create(event.asCharacters().getData()));
          isNext = false;
        }
      }
    }

    return linkCollection;
  }

  private void properties(final XMLEventReader reader, final StartElement start, final AtomEntryImpl entry)
          throws XMLStreamException {
    boolean foundEndProperties = false;
    while (reader.hasNext() && !foundEndProperties) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement()) {
        entry.getProperties().add(propDeserializer.deserialize(reader, event.asStartElement()));
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperties = true;
      }
    }
  }

  private AtomEntryImpl entryRef(final StartElement start) throws XMLStreamException {
    final AtomEntryImpl entry = new AtomEntryImpl();

    final Attribute entryRefId = start.getAttributeByName(Constants.QNAME_ATOM_ATTR_ID);
    if (entryRefId != null) {
      entry.setId(entryRefId.getValue());
    }

    return entry;
  }

  private AtomEntryImpl entry(final XMLEventReader reader, final StartElement start) throws XMLStreamException {
    final AtomEntryImpl entry;
    if (entryRefQName.equals(start.getName())) {
      entry = entryRef(start);
    } else if (Constants.QNAME_ATOM_ELEM_ENTRY.equals(start.getName())) {
      entry = new AtomEntryImpl();
      final Attribute xmlBase = start.getAttributeByName(Constants.QNAME_ATTR_XML_BASE);
      if (xmlBase != null) {
        entry.setBaseURI(xmlBase.getValue());
      }

      final Attribute etag = start.getAttributeByName(etagQName);
      if (etag != null) {
        entry.setETag(etag.getValue());
      }

      boolean foundEndEntry = false;
      while (reader.hasNext() && !foundEndEntry) {
        final XMLEvent event = reader.nextEvent();

        if (event.isStartElement()) {
          if (Constants.QNAME_ATOM_ELEM_ID.equals(event.asStartElement().getName())) {
            common(reader, event.asStartElement(), entry, "id");
          } else if (Constants.QNAME_ATOM_ELEM_TITLE.equals(event.asStartElement().getName())) {
            common(reader, event.asStartElement(), entry, "title");
          } else if (Constants.QNAME_ATOM_ELEM_SUMMARY.equals(event.asStartElement().getName())) {
            common(reader, event.asStartElement(), entry, "summary");
          } else if (Constants.QNAME_ATOM_ELEM_UPDATED.equals(event.asStartElement().getName())) {
            common(reader, event.asStartElement(), entry, "updated");
          } else if (Constants.QNAME_ATOM_ELEM_CATEGORY.equals(event.asStartElement().getName())) {
            final Attribute term = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATOM_ATTR_TERM));
            if (term != null) {
              entry.setType(term.getValue());
            }
          } else if (Constants.QNAME_ATOM_ELEM_LINK.equals(event.asStartElement().getName())) {
            final LinkImpl link = new LinkImpl();
            final Attribute rel = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_REL));
            if (rel != null) {
              link.setRel(rel.getValue());
            }
            final Attribute title = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TITLE));
            if (title != null) {
              link.setTitle(title.getValue());
            }
            final Attribute href = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_HREF));
            if (href != null) {
              link.setHref(href.getValue());
            }
            final Attribute type = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TYPE));
            if (type != null) {
              link.setType(type.getValue());
            }

            if (Constants.SELF_LINK_REL.equals(link.getRel())) {
              entry.setSelfLink(link);
            } else if (Constants.EDIT_LINK_REL.equals(link.getRel())) {
              entry.setEditLink(link);
            } else if (Constants.EDITMEDIA_LINK_REL.equals(link.getRel())) {
              final Attribute mediaETag = event.asStartElement().getAttributeByName(etagQName);
              if (mediaETag != null) {
                entry.setMediaETag(mediaETag.getValue());
              }
            } else if (link.getRel().startsWith(
                    version.getNamespaceMap().get(ODataServiceVersion.NAVIGATION_LINK_REL))) {
              entry.getNavigationLinks().add(link);
              inline(reader, event.asStartElement(), link);
            } else if (link.getRel().startsWith(
                    version.getNamespaceMap().get(ODataServiceVersion.ASSOCIATION_LINK_REL))) {

              entry.getAssociationLinks().add(link);
            } else if (link.getRel().startsWith(
                    version.getNamespaceMap().get(ODataServiceVersion.MEDIA_EDIT_LINK_REL))) {

              final Attribute metag = event.asStartElement().getAttributeByName(etagQName);
              if (metag != null) {
                link.setMediaETag(metag.getValue());
              }
              entry.getMediaEditLinks().add(link);
            }
          } else if (actionQName.equals(event.asStartElement().getName())) {
            final ODataOperation operation = new ODataOperation();
            final Attribute metadata =
                    event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_METADATA));
            if (metadata != null) {
              operation.setMetadataAnchor(metadata.getValue());
            }
            final Attribute title = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TITLE));
            if (title != null) {
              operation.setTitle(title.getValue());
            }
            final Attribute target = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TARGET));
            if (target != null) {
              operation.setTarget(URI.create(target.getValue()));
            }

            entry.getOperations().add(operation);
          } else if (Constants.QNAME_ATOM_ELEM_CONTENT.equals(event.asStartElement().getName())) {
            final Attribute type = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TYPE));
            if (type == null || ContentType.APPLICATION_XML.equals(type.getValue())) {
              properties(reader, skipBeforeFirstStartElement(reader), entry);
            } else {
              entry.setMediaContentType(type.getValue());
              final Attribute src = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATOM_ATTR_SRC));
              if (src != null) {
                entry.setMediaContentSource(src.getValue());
              }
            }
          } else if (propertiesQName.equals(event.asStartElement().getName())) {
            properties(reader, event.asStartElement(), entry);
          }
        }

        if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
          foundEndEntry = true;
        }
      }

      return entry;
    } else {
      entry = null;
    }

    return entry;
  }

  private Container<AtomEntryImpl> entry(final InputStream input) throws XMLStreamException {
    final XMLEventReader reader = FACTORY.createXMLEventReader(input);
    final StartElement start = skipBeforeFirstStartElement(reader);
    return getContainer(start, entry(reader, start));
  }

  private void count(final XMLEventReader reader, final StartElement start, final AtomFeedImpl feed)
          throws XMLStreamException {

    boolean foundEndElement = false;
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        feed.setCount(Integer.valueOf(event.asCharacters().getData()));
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }
  }

  private AtomFeedImpl feed(final XMLEventReader reader, final StartElement start) throws XMLStreamException {
    if (!Constants.QNAME_ATOM_ELEM_FEED.equals(start.getName())) {
      return null;
    }
    final AtomFeedImpl feed = new AtomFeedImpl();
    final Attribute xmlBase = start.getAttributeByName(Constants.QNAME_ATTR_XML_BASE);
    if (xmlBase != null) {
      feed.setBaseURI(xmlBase.getValue());
    }

    boolean foundEndFeed = false;
    while (reader.hasNext() && !foundEndFeed) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement()) {
        if (countQName.equals(event.asStartElement().getName())) {
          count(reader, event.asStartElement(), feed);
        } else if (Constants.QNAME_ATOM_ELEM_ID.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), feed, "id");
        } else if (Constants.QNAME_ATOM_ELEM_TITLE.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), feed, "title");
        } else if (Constants.QNAME_ATOM_ELEM_SUMMARY.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), feed, "summary");
        } else if (Constants.QNAME_ATOM_ELEM_UPDATED.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), feed, "updated");
        } else if (Constants.QNAME_ATOM_ELEM_LINK.equals(event.asStartElement().getName())) {
          final Attribute rel = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_REL));
          if (rel != null && Constants.NEXT_LINK_REL.equals(rel.getValue())) {
            final Attribute href = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_HREF));
            if (href != null) {
              feed.setNext(URI.create(href.getValue()));
            }
          }
        } else if (Constants.QNAME_ATOM_ELEM_ENTRY.equals(event.asStartElement().getName())) {
          feed.getEntries().add(entry(reader, event.asStartElement()));
        } else if (entryRefQName.equals(event.asStartElement().getName())) {
          feed.getEntries().add(entryRef(event.asStartElement()));
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndFeed = true;
      }
    }

    return feed;
  }

  private Container<AtomFeedImpl> feed(final InputStream input) throws XMLStreamException {
    final XMLEventReader reader = FACTORY.createXMLEventReader(input);
    final StartElement start = skipBeforeFirstStartElement(reader);
    return getContainer(start, feed(reader, start));
  }

  private XMLODataErrorImpl error(final XMLEventReader reader, final StartElement start) throws XMLStreamException {
    final XMLODataErrorImpl error = new XMLODataErrorImpl();

    boolean setCode = false;
    boolean codeSet = false;
    boolean setMessage = false;
    boolean messageSet = false;
    boolean setTarget = false;
    boolean targetSet = false;

    boolean foundEndElement = false;
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement()) {
        if (errorCodeQName.equals(event.asStartElement().getName())) {
          setCode = true;
        } else if (errorMessageQName.equals(event.asStartElement().getName())) {
          setMessage = true;
        } else if (errorTargetQName.equals(event.asStartElement().getName())) {
          setTarget = true;
        }
      }

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        if (setCode && !codeSet) {
          error.setCode(event.asCharacters().getData());
          setCode = false;
          codeSet = true;
        }
        if (setMessage && !messageSet) {
          error.setMessage(event.asCharacters().getData());
          setMessage = false;
          messageSet = true;
        }
        if (setTarget && !targetSet) {
          error.setTarget(event.asCharacters().getData());
          setTarget = false;
          targetSet = true;
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }

    return error;
  }

  private Container<XMLODataErrorImpl> error(final InputStream input) throws XMLStreamException {
    final XMLEventReader reader = FACTORY.createXMLEventReader(input);
    final StartElement start = skipBeforeFirstStartElement(reader);
    return getContainer(start, error(reader, start));
  }

  private <T> Container<T> getContainer(final StartElement start, final T object) {
    final Attribute context = start.getAttributeByName(contextQName);
    final Attribute metadataETag = start.getAttributeByName(metadataEtagQName);

    return new Container<T>(
            context == null ? null : URI.create(context.getValue()),
            metadataETag == null ? null : metadataETag.getValue(),
            object);
  }

  @SuppressWarnings("unchecked")
  public <T, V extends T> Container<T> read(final InputStream input, final Class<V> reference)
          throws XMLStreamException {

    if (XMLODataErrorImpl.class.equals(reference)) {
      return (Container<T>) error(input);
    } else if (AtomFeedImpl.class.equals(reference)) {
      return (Container<T>) feed(input);
    } else if (AtomEntryImpl.class.equals(reference)) {
      return (Container<T>) entry(input);
    } else if (AtomPropertyImpl.class.equals(reference)) {
      return (Container<T>) property(input);
    } else if (XMLLinkCollectionImpl.class.equals(reference)) {
      return (Container<T>) linkCollection(input);
    }
    return null;
  }
}
