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

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.data.AtomEntryImpl;
import org.apache.olingo.commons.core.data.AtomFeedImpl;
import org.apache.olingo.commons.core.data.AtomPropertyImpl;
import org.apache.olingo.commons.core.data.CollectionValueImpl;
import org.apache.olingo.commons.core.data.ComplexValueImpl;
import org.apache.olingo.commons.core.data.JSONEntryImpl;
import org.apache.olingo.commons.core.data.JSONFeedImpl;
import org.apache.olingo.commons.core.data.JSONPropertyImpl;
import org.apache.olingo.commons.core.data.LinkImpl;
import org.apache.olingo.fit.metadata.EntityType;
import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.metadata.NavigationProperty;
import org.springframework.beans.BeanUtils;

public class DataBinder {

  private final ODataServiceVersion version;

  public DataBinder(final ODataServiceVersion version) {
    this.version = version;
  }

  public JSONFeedImpl getJsonFeed(final AtomFeedImpl atomfeed) {
    final JSONFeedImpl jsonfeed = new JSONFeedImpl();

    BeanUtils.copyProperties(atomfeed, jsonfeed, "baseURI", "metadataContextURL");
    jsonfeed.setMetadataContextURL(atomfeed.getBaseURI() == null
            ? null
            : URI.create(atomfeed.getBaseURI().toASCIIString() + "/$metadata").normalize());

    final Collection<Entry> entries = jsonfeed.getEntries();
    for (Entry entry : atomfeed.getEntries()) {
      entries.add(getJsonEntry((AtomEntryImpl) entry));
    }

    return jsonfeed;
  }

  public AtomFeedImpl getAtomFeed(final JSONFeedImpl jsonfeed) {
    final AtomFeedImpl atomfeed = new AtomFeedImpl();

    BeanUtils.copyProperties(jsonfeed, atomfeed, "baseURI", "metadataContextURL");
    atomfeed.setBaseURI(jsonfeed.getBaseURI() == null
            ? null
            : jsonfeed.getBaseURI().toASCIIString() + "/$metadata");

    final Collection<Entry> entries = atomfeed.getEntries();
    for (Entry entry : jsonfeed.getEntries()) {
      entries.add(getAtomEntry((JSONEntryImpl) entry));
    }

    return atomfeed;
  }

  public JSONEntryImpl getJsonEntry(final AtomEntryImpl atomentry) {
    final JSONEntryImpl jsonentry = new JSONEntryImpl();

    BeanUtils.copyProperties(atomentry, jsonentry, "baseURI", "properties", "links");
    jsonentry.setBaseURI(atomentry.getBaseURI() == null ? null : atomentry.getBaseURI().toASCIIString());

    for (Link link : atomentry.getNavigationLinks()) {
      final Link jlink = new LinkImpl();
      jlink.setHref(link.getHref());
      jlink.setTitle(link.getTitle());
      jlink.setType(link.getType());
      jlink.setRel(link.getRel());

      if (link.getInlineEntry() instanceof AtomEntryImpl) {
        final Entry inlineEntry = link.getInlineEntry();
        if (inlineEntry instanceof AtomEntryImpl) {
          jlink.setInlineEntry(getJsonEntry((AtomEntryImpl) link.getInlineEntry()));
        }
      } else if (link.getInlineFeed() instanceof AtomFeedImpl) {
        final Feed inlineFeed = link.getInlineFeed();
        if (inlineFeed instanceof AtomFeedImpl) {
          jlink.setInlineFeed(getJsonFeed((AtomFeedImpl) link.getInlineFeed()));
        }
      }

      jsonentry.getNavigationLinks().add(jlink);
    }

    final Collection<Property> properties = jsonentry.getProperties();
    for (Property property : atomentry.getProperties()) {
      properties.add(getJsonProperty((AtomPropertyImpl) property));
    }

    return jsonentry;
  }

  public AtomEntryImpl getAtomEntry(final JSONEntryImpl jsonentry) {
    final AtomEntryImpl atomentry = new AtomEntryImpl();

    final Metadata metadata = Commons.getMetadata(version);

    BeanUtils.copyProperties(jsonentry, atomentry, "baseURI", "properties", "links");
    atomentry.setBaseURI(jsonentry.getBaseURI() == null ? null : jsonentry.getBaseURI().toASCIIString());

    for (Link link : jsonentry.getNavigationLinks()) {
      final Link alink = new LinkImpl();
      alink.setHref(link.getHref());
      alink.setTitle(link.getTitle());

      final NavigationProperty navPropDetails =
              metadata.getEntityType(jsonentry.getType()).getNavigationProperty(link.getTitle());

      alink.setType(navPropDetails != null && navPropDetails.isFeed()
              ? Constants.get(ConstantKey.ATOM_LINK_FEED) : Constants.get(ConstantKey.ATOM_LINK_ENTRY));
      alink.setRel(link.getRel());

      if (link.getInlineEntry() instanceof JSONEntryImpl) {
        Entry inlineEntry = link.getInlineEntry();
        if (inlineEntry instanceof JSONEntryImpl) {
          alink.setInlineEntry(getAtomEntry((JSONEntryImpl) link.getInlineEntry()));
        }
      } else if (link.getInlineFeed() instanceof JSONFeedImpl) {
        Feed inlineFeed = link.getInlineFeed();
        if (inlineFeed instanceof JSONFeedImpl) {
          alink.setInlineFeed(getAtomFeed((JSONFeedImpl) link.getInlineFeed()));
        }
      }

      atomentry.getNavigationLinks().add(alink);
    }

    final EntityType entityType = StringUtils.isBlank(jsonentry.getType())
            ? null : metadata.getEntityType(jsonentry.getType());
    final Map<String, NavigationProperty> navProperties = entityType == null
            ? Collections.<String, NavigationProperty>emptyMap() : entityType.getNavigationPropertyMap();

    final List<Property> properties = atomentry.getProperties();

    for (Property property : jsonentry.getProperties()) {
      if (navProperties.containsKey(property.getName())) {
        final Link alink = new LinkImpl();
        alink.setTitle(property.getName());

        alink.setType(navProperties.get(property.getName()).isFeed()
                ? Constants.get(ConstantKey.ATOM_LINK_FEED)
                : Constants.get(ConstantKey.ATOM_LINK_ENTRY));

        alink.setRel(Constants.get(ConstantKey.ATOM_LINK_REL) + property.getName());

        if (property.getValue().isComplex()) {
          final Entry inline = new AtomEntryImpl();
          inline.setType(navProperties.get(property.getName()).getType());
          for (Property prop : property.getValue().asComplex().get()) {
            inline.getProperties().add(prop);
          }
          alink.setInlineEntry(inline);

        } else if (property.getValue().isCollection()) {
          final Feed inline = new AtomFeedImpl();
          for (Value entry : property.getValue().asCollection().get()) {
            final Entry inlineEntry = new AtomEntryImpl();
            inlineEntry.setType(navProperties.get(property.getName()).getType());
            for (Property prop : entry.asComplex().get()) {
              inlineEntry.getProperties().add(prop);
            }
            inline.getEntries().add(inlineEntry);
          }
          alink.setInlineFeed(inline);
        } else {
          throw new IllegalStateException("Invalid navigation property " + property);
        }
        atomentry.getNavigationLinks().add(alink);
      } else {
        properties.add(getAtomProperty((JSONPropertyImpl) property, atomentry.getType()));
      }
    }

    return atomentry;
  }

  public JSONPropertyImpl getJsonProperty(final AtomPropertyImpl atomproperty) {
    final JSONPropertyImpl jsonproperty = new JSONPropertyImpl();
    BeanUtils.copyProperties(atomproperty, jsonproperty, "value");

    if (atomproperty.getValue().isComplex()) {
      final ComplexValueImpl complex = new ComplexValueImpl();
      jsonproperty.setValue(complex);

      for (Property field : atomproperty.getValue().asComplex().get()) {
        complex.get().add(getJsonProperty((AtomPropertyImpl) field));
      }
    } else if (atomproperty.getValue().isCollection()) {
      final CollectionValueImpl collection = new CollectionValueImpl();
      jsonproperty.setValue(collection);

      for (Value element : atomproperty.getValue().asCollection().get()) {
        if (element.isComplex()) {
          final ComplexValueImpl complex = new ComplexValueImpl();
          collection.get().add(complex);

          for (Property field : element.asComplex().get()) {
            complex.get().add(getJsonProperty((AtomPropertyImpl) field));
          }
        } else {
          collection.get().add(element);
        }
      }
    } else {
      jsonproperty.setValue(atomproperty.getValue());
    }

    return jsonproperty;
  }

  public AtomPropertyImpl getAtomProperty(final JSONPropertyImpl jsonproperty, final String entryType) {
    final AtomPropertyImpl atomproperty = new AtomPropertyImpl();
    atomproperty.setName(jsonproperty.getName());

    if (StringUtils.isNotBlank(jsonproperty.getType())) {
      atomproperty.setType(jsonproperty.getType());
    } else {
      final EntityType entityType = Commons.getMetadata(version).getEntityType(entryType);
      if (entityType != null) {
        atomproperty.setType(entityType.getProperty(jsonproperty.getName()).getType());
      }
    }

    if (jsonproperty.getValue().isComplex()) {
      final ComplexValueImpl complex = new ComplexValueImpl();
      atomproperty.setValue(complex);

      for (Property field : jsonproperty.getValue().asComplex().get()) {
        complex.get().add(getAtomProperty((JSONPropertyImpl) field, atomproperty.getType()));
      }
    } else if (jsonproperty.getValue().isCollection()) {
      final CollectionValueImpl collection = new CollectionValueImpl();
      atomproperty.setValue(collection);

      for (Value element : jsonproperty.getValue().asCollection().get()) {
        if (element instanceof ComplexValueImpl) {
          final ComplexValueImpl complex = new ComplexValueImpl();
          collection.get().add(complex);

          for (Property field : element.asComplex().get()) {
            complex.get().add(getAtomProperty((JSONPropertyImpl) field,
                    atomproperty.getType().replaceAll("^Collection\\(", "").replaceAll("\\)$", "")));
          }
        } else {
          collection.get().add(element);
        }
      }
    } else {
      atomproperty.setValue(jsonproperty.getValue());
    }

    return atomproperty;
  }
}
