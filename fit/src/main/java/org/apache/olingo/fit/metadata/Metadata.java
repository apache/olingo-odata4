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
package org.apache.olingo.fit.metadata;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Metadata extends AbstractMetadataElement {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(Metadata.class);

  private final Map<String, Schema> schemas;

  public Metadata(final InputStream is) {
    this.schemas = new HashMap<String, Schema>();

    try {
      final XMLInputFactory ifactory = XMLInputFactory.newInstance();
      ifactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
      final XMLEventReader reader = ifactory.createXMLEventReader(is, "UTF-8");

      try {
        while (reader.hasNext()) {
          final XMLEvent event = reader.nextEvent();

          if (event.isStartElement() && event.asStartElement().getName().equals(new QName("Schema"))) {
            final Schema schema = getSchema(event.asStartElement(), reader);
            schemas.put(schema.getNamespace(), schema);
          }
        }

      } catch (Exception ignore) {
        // ignore
      } finally {
        reader.close();
        IOUtils.closeQuietly(is);
      }
    } catch (Exception e) {
      LOG.error("Error parsing metadata", e);
    }

    for (Map.Entry<String, Schema> schemaEntry : schemas.entrySet()) {
      for (EntityType entityType : schemaEntry.getValue().getEntityTypes()) {
        for (NavigationProperty property : entityType.getNavigationProperties()) {
          if (StringUtils.isNotBlank(property.getReleationship())) {
            // V3 ...
            final Association association = schemaEntry.getValue().getAssociation(
                    property.getReleationship().replaceAll(schemaEntry.getKey() + "\\.", ""));
            final Association.Role role = association.getRole(property.getToRole());
            property.setFeed(role.getMultiplicity().equals("*"));
            property.setType(property.isFeed() ? "Collection(" + role.getType() + ")" : role.getType());

            // let me assume that it will be just a single container
            final AssociationSet associationSet = schemaEntry.getValue().getContainers().iterator().next().
                    getAssociationSet(property.getReleationship());

            final AssociationSet.Role associationSetRole = associationSet.getRole(property.getToRole());
            property.setTarget(associationSetRole.getEntitySet());
          } else {
            // V4 ...
            property.setFeed(property.getType().startsWith("Collection("));

            final Collection<EntitySet> entitySets = schemaEntry.getValue().getContainers().iterator().next().
                    getEntitySets(schemaEntry.getKey(), entityType.getName());

            final Iterator<EntitySet> iter = entitySets.iterator();
            boolean found = false;

            while (!found && iter.hasNext()) {
              final EntitySet entitySet = iter.next();
              final String target = entitySet.getTarget(property.getName());
              if (StringUtils.isNotBlank(target)) {
                property.setTarget(entitySet.getTarget(property.getName()));
                found = true;
              }
            }
          }
        }
      }
    }
  }

  public EntitySet getEntitySet(final String name) {
    for (Schema schema : getSchemas()) {
      for (Container container : schema.getContainers()) {
        final EntitySet entitySet = container.getEntitySet(name);
        if (entitySet != null) {
          return entitySet;
        }
      }
    }

    return null;
  }

  public EntityType getEntityType(final String fqn) {
    final int lastDotIndex = fqn.lastIndexOf('.');
    final String ns = fqn.substring(0, lastDotIndex).replaceAll("^#", "");
    final String name = fqn.substring(lastDotIndex + 1);
    return getSchema(ns) == null ? null : getSchema(ns).getEntityType(name);
  }

  public Map<String, NavigationProperty> getNavigationProperties(final String entitySetName) {

    for (Schema schema : getSchemas()) {
      for (Container container : schema.getContainers()) {
        final EntitySet entitySet = container.getEntitySet(entitySetName);
        if (entitySet != null) {
          final String entityTypeFQN = entitySet.getType();
          final int lastDotIndex = entityTypeFQN.lastIndexOf('.');
          final String entityTypeNS = entityTypeFQN.substring(0, lastDotIndex);
          final String entityTypeName = entityTypeFQN.substring(lastDotIndex + 1);

          final EntityType entityType = getSchema(entityTypeNS).getEntityType(entityTypeName);

          return entityType.getNavigationPropertyMap();
        }
      }
    }

    return null;
  }

  public Collection<Schema> getSchemas() {
    return schemas.values();
  }

  public Schema getSchema(final String namespace) {
    return schemas.get(namespace);
  }

  public Metadata addSchema(final String namespace, final Schema schema) {
    schemas.put(namespace, schema);
    return this;
  }

  private Schema getSchema(final StartElement start, final XMLEventReader reader) throws XMLStreamException {
    final Schema schema = new Schema(start.getAttributeByName(new QName("Namespace")).getValue());

    boolean completed = false;

    while (!completed && reader.hasNext()) {
      XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && event.asStartElement().getName().equals(new QName("EntityType"))
              || event.isStartElement() && event.asStartElement().getName().equals(new QName("ComplexType"))) {
        final EntityType entityType = getEntityType(event.asStartElement(), reader);
        schema.addEntityType(entityType.getName(), entityType);
      } else if (event.isStartElement() && event.asStartElement().getName().equals(new QName("EntityContainer"))) {
        final org.apache.olingo.fit.metadata.Container container = getContainer(event.asStartElement(), reader);
        schema.addContainer(container.getName(), container);
      } else if (event.isStartElement() && event.asStartElement().getName().equals(new QName("Association"))) {
        // just for V3
        final Association association = getAssociation(event.asStartElement(), reader);
        schema.addAssociation(association.getName(), association);
      } else if (event.isEndElement() && event.asEndElement().getName().equals(start.getName())) {
        completed = true;
      }
    }

    return schema;
  }

  private org.apache.olingo.fit.metadata.Container getContainer(
          final StartElement start, final XMLEventReader reader) throws XMLStreamException {
    final org.apache.olingo.fit.metadata.Container container =
            new org.apache.olingo.fit.metadata.Container(start.getAttributeByName(new QName("Name")).getValue());

    boolean completed = false;

    while (!completed && reader.hasNext()) {
      XMLEvent event = reader.nextEvent();

      if (event.isStartElement()
              && (event.asStartElement().getName().equals(new QName("EntitySet"))
              || event.asStartElement().getName().equals(new QName("Singleton")))) {
        final EntitySet entitySet = getEntitySet(event.asStartElement(), reader);
        container.addEntitySet(entitySet.getName(), entitySet);
      } else if (event.isStartElement() && event.asStartElement().getName().equals(new QName("AssociationSet"))) {
        // just for V3
        final AssociationSet associationSet = getAssociationSet(event.asStartElement(), reader);
        container.addAssociationSet(associationSet.getAssociation(), associationSet);
      } else if (event.isEndElement() && event.asEndElement().getName().equals(start.getName())) {
        completed = true;
      }
    }

    return container;
  }

  private Association getAssociation(
          final StartElement start, final XMLEventReader reader) throws XMLStreamException {
    final Association association = new Association(start.getAttributeByName(new QName("Name")).getValue());

    boolean completed = false;

    while (!completed && reader.hasNext()) {
      XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && event.asStartElement().getName().equals(new QName("End"))) {
        final String role = event.asStartElement().getAttributeByName(new QName("Role")).getValue();
        final String type = event.asStartElement().getAttributeByName(new QName("Type")).getValue();
        final String multiplicity = event.asStartElement().getAttributeByName(new QName("Multiplicity")).getValue();
        association.addRole(role, type, multiplicity);
      } else if (event.isEndElement() && event.asEndElement().getName().equals(start.getName())) {
        completed = true;
      }
    }

    return association;
  }

  private AssociationSet getAssociationSet(
          final StartElement start, final XMLEventReader reader) throws XMLStreamException {
    final AssociationSet associationSet = new AssociationSet(
            start.getAttributeByName(new QName("Name")).getValue(),
            start.getAttributeByName(new QName("Association")).getValue());

    boolean completed = false;

    while (!completed && reader.hasNext()) {
      XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && event.asStartElement().getName().equals(new QName("End"))) {
        final String role = event.asStartElement().getAttributeByName(new QName("Role")).getValue();
        final String entitySet = event.asStartElement().getAttributeByName(new QName("EntitySet")).getValue();
        associationSet.addRole(role, entitySet);
      } else if (event.isEndElement() && event.asEndElement().getName().equals(start.getName())) {
        completed = true;
      }
    }

    return associationSet;
  }

  private EntityType getEntityType(final StartElement start, final XMLEventReader reader) throws XMLStreamException {
    final EntityType entityType = new EntityType(start.getAttributeByName(new QName("Name")).getValue());

    boolean completed = false;

    while (!completed && reader.hasNext()) {
      XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && event.asStartElement().getName().equals(new QName("Property"))) {
        final org.apache.olingo.fit.metadata.Property property = getProperty(event.asStartElement());
        entityType.addProperty(property.getName(), property);
      } else if (event.isStartElement() && event.asStartElement().getName().equals(new QName("NavigationProperty"))) {
        final NavigationProperty property = getNavigationProperty(event.asStartElement());
        entityType.addNavigationProperty(property.getName(), property);
      } else if (event.isEndElement() && event.asEndElement().getName().equals(start.getName())) {
        completed = true;
      }
    }

    return entityType;
  }

  private org.apache.olingo.fit.metadata.Property getProperty(final StartElement start) throws XMLStreamException {
    final org.apache.olingo.fit.metadata.Property property =
            new org.apache.olingo.fit.metadata.Property(start.getAttributeByName(new QName("Name")).getValue());

    final Attribute type = start.getAttributeByName(new QName("Type"));
    property.setType(type == null ? "Edm.String" : type.getValue());

    final Attribute nullable = start.getAttributeByName(new QName("Nullable"));
    property.setNullable(nullable == null || !"false".equals(nullable.getValue()));

    return property;
  }

  private NavigationProperty getNavigationProperty(final StartElement start) throws XMLStreamException {
    final NavigationProperty property = new NavigationProperty(start.getAttributeByName(new QName("Name")).getValue());

    final Attribute type = start.getAttributeByName(new QName("Type"));
    if (type != null) {
      property.setType(type.getValue());
    }

    final Attribute relationship = start.getAttributeByName(new QName("Relationship"));
    if (relationship != null) {
      property.setReleationship(relationship.getValue());
    }

    final Attribute toRole = start.getAttributeByName(new QName("ToRole"));
    if (toRole != null) {
      property.setToRole(toRole.getValue());
    }

    return property;
  }

  private EntitySet getEntitySet(final StartElement start, final XMLEventReader reader) throws XMLStreamException {
    final EntitySet entitySet = "Singleton".equals(start.getName().getLocalPart())
            ? new EntitySet(start.getAttributeByName(new QName("Name")).getValue(), true)
            : new EntitySet(start.getAttributeByName(new QName("Name")).getValue());

    Attribute type = start.getAttributeByName(new QName("EntityType"));
    if (type == null) {
      type = start.getAttributeByName(new QName("Type"));
      entitySet.setType(type == null ? null : type.getValue());
    } else {
      entitySet.setType(type.getValue());
    }

    boolean completed = false;

    while (!completed && reader.hasNext()) {
      XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && event.asStartElement().getName().equals(new QName("NavigationPropertyBinding"))) {
        final String path = event.asStartElement().getAttributeByName(new QName("Path")).getValue();
        final String target = event.asStartElement().getAttributeByName(new QName("Target")).getValue();
        entitySet.addBinding(path, target);
      } else if (event.isEndElement() && event.asEndElement().getName().equals(start.getName())) {
        completed = true;
      }
    }

    return entitySet;
  }
}
