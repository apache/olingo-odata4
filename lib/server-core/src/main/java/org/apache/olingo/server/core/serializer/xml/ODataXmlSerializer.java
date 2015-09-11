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
package org.apache.olingo.server.core.serializer.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.ex.ODataErrorDetail;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.serializer.AbstractODataSerializer;
import org.apache.olingo.server.core.serializer.SerializerResultImpl;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.apache.olingo.server.core.serializer.utils.ContextURLBuilder;
import org.apache.olingo.server.core.serializer.utils.ExpandSelectHelper;

public class ODataXmlSerializer extends AbstractODataSerializer {

  private static final String DATA = "d";
  private static final String CONTEXT = "context";
  /** The default character set is UTF-8. */
  public static final String DEFAULT_CHARSET = "UTF-8";
  private static final String ATOM = "a";
  private static final String NS_ATOM = "http://www.w3.org/2005/Atom";
  private static final String METADATA = "m";
  private static final String NS_METADATA = "http://docs.oasis-open.org/odata/ns/metadata";
  private static final String NS_DATA = "http://docs.oasis-open.org/odata/ns/data";
  private static final String NS_SCHEMA = "http://docs.oasis-open.org/odata/ns/scheme";

  @Override
  public SerializerResult serviceDocument(final ServiceMetadata metadata, final String serviceRoot)
      throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      ServiceDocumentXmlSerializer serializer = new ServiceDocumentXmlSerializer(metadata, serviceRoot);
      serializer.writeServiceDocument(writer);

      writer.flush();
      writer.close();
      outputStream.close();

      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final XMLStreamException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult metadataDocument(final ServiceMetadata serviceMetadata) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      MetadataDocumentXmlSerializer serializer = new MetadataDocumentXmlSerializer(serviceMetadata);
      serializer.writeMetadataDocument(writer);

      writer.flush();
      writer.close();
      outputStream.close();

      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final XMLStreamException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult error(final ODataServerError error) throws SerializerException {
    if (error == null) {
      throw new SerializerException("ODataError object MUST NOT be null!",
          SerializerException.MessageKeys.NULL_INPUT);
    }

    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      writer.writeStartDocument(ODataSerializer.DEFAULT_CHARSET, "1.0");

      writer.writeStartElement("error");
      writer.writeDefaultNamespace(NS_METADATA);
      writeErrorDetails(String.valueOf(error.getStatusCode()), error.getMessage(), error.getTarget(), writer);
      if (error.getDetails() != null && !error.getDetails().isEmpty()) {
        writer.writeStartElement("details");
        for (ODataErrorDetail inner : error.getDetails()) {
          writeErrorDetails(inner.getCode(), inner.getMessage(), inner.getTarget(), writer);
        }
        writer.writeEndElement();
      }
      writer.writeEndElement();
      writer.writeEndDocument();

      writer.flush();
      writer.close();
      outputStream.close();

      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final XMLStreamException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  private void writeErrorDetails(String code, String message, String target, XMLStreamWriter writer)
      throws XMLStreamException {
    if (code != null) {
      writer.writeStartElement("code");
      writer.writeCharacters(String.valueOf(code));
      writer.writeEndElement();
    }

    writer.writeStartElement("message");
    writer.writeCharacters(message);
    writer.writeEndElement();

    if (target != null) {
      writer.writeStartElement("target");
      writer.writeCharacters(target);
      writer.writeEndElement();
    }
  }

  @Override
  public SerializerResult entityCollection(final ServiceMetadata metadata,
      final EdmEntityType entityType, final EntityCollection entitySet,
      final EntityCollectionSerializerOptions options) throws SerializerException {

    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    if (options != null && options.onlyReferences()) {
      ReferenceCollectionSerializerOptions rso = ReferenceCollectionSerializerOptions.with()
          .contextURL(contextURL).build();
      return entityReferenceCollection(entitySet, rso);
    }

    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      writer.writeStartDocument(ODataSerializer.DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(ATOM, "feed", NS_ATOM);
      writer.writeNamespace(ATOM, NS_ATOM);
      writer.writeNamespace(METADATA, NS_METADATA);
      writer.writeNamespace(DATA, NS_DATA);

      writer.writeAttribute(METADATA, NS_METADATA, "context",
          ContextURLBuilder.create(contextURL).toASCIIString());
      writeMetadataETag(metadata, writer);

      if (options != null) {
        writer.writeStartElement(ATOM, "id", NS_ATOM);
        writer.writeCharacters(options.getId());
        writer.writeEndElement();
      }

      if (options != null && options.getCount() != null && options.getCount().getValue()
          && entitySet.getCount() != null) {
        writeCount(entitySet, writer);
      }
      if (entitySet.getNext() != null) {
        writeNextLink(entitySet, writer);
      }

      if (options == null) {
        writeEntitySet(metadata, entityType, entitySet, null, null, writer);
      } else {
        writeEntitySet(metadata, entityType, entitySet,
            options.getExpand(), options.getSelect(), writer);
      }

      writer.writeEndElement();
      writer.writeEndDocument();

      writer.flush();
      writer.close();
      outputStream.close();

      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final XMLStreamException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult entity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final EntitySerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());

    if (options != null && options.onlyReferences()) {
      return entityReference(entity,
          ReferenceSerializerOptions.with().contextURL(contextURL).build());
    }

    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      writer.writeStartDocument(ODataSerializer.DEFAULT_CHARSET, "1.0");
      writeEntity(metadata, entityType, entity, contextURL,
          options == null ? null : options.getExpand(),
          options == null ? null : options.getSelect(), writer, true);
      writer.writeEndDocument();

      writer.flush();
      writer.close();
      outputStream.close();

      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final XMLStreamException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  private ContextURL checkContextURL(final ContextURL contextURL) throws SerializerException {
    if (contextURL == null) {
      throw new SerializerException("ContextURL null!", SerializerException.MessageKeys.NO_CONTEXT_URL);
    }
    return contextURL;
  }

  private void writeMetadataETag(final ServiceMetadata metadata, XMLStreamWriter writer)
      throws XMLStreamException {
    if (metadata != null
        && metadata.getServiceMetadataETagSupport() != null
        && metadata.getServiceMetadataETagSupport().getMetadataETag() != null) {
      writer.writeAttribute(METADATA, NS_METADATA, "metadata-etag",
          metadata.getServiceMetadataETagSupport().getMetadataETag());
    }
  }

  protected void writeEntitySet(final ServiceMetadata metadata, final EdmEntityType entityType,
      final EntityCollection entitySet, final ExpandOption expand, final SelectOption select,
      final XMLStreamWriter writer) throws XMLStreamException, SerializerException {
    for (final Entity entity : entitySet.getEntities()) {
      writeEntity(metadata, entityType, entity, null, expand, select, writer, false);
    }
  }

  protected void writeEntity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final ContextURL contextURL, final ExpandOption expand,
      final SelectOption select, final XMLStreamWriter writer, final boolean top)
          throws XMLStreamException, SerializerException {

    writer.writeStartElement(ATOM, "entry", NS_ATOM);
    if (top) {
      writer.writeNamespace(ATOM, NS_ATOM);
      writer.writeNamespace(METADATA, NS_METADATA);
      writer.writeNamespace(DATA, NS_DATA);

      if (contextURL != null) { // top-level entity
        writer.writeAttribute(METADATA, NS_METADATA, CONTEXT,
            ContextURLBuilder.create(contextURL).toASCIIString());
        writeMetadataETag(metadata, writer);

      }
    }
    if (entity.getETag() != null) {
      writer.writeAttribute(METADATA, NS_METADATA, "etag", entity.getETag());
    }

    writer.writeStartElement(NS_ATOM, "id");
    writer.writeCharacters(entity.getId().toASCIIString());
    writer.writeEndElement();

    writerAuthorInfo(entity.getTitle(), writer);

    writer.writeStartElement(NS_ATOM, "link");
    writer.writeAttribute("rel", "edit");
    writer.writeAttribute("href", entity.getId().toASCIIString());
    writer.writeEndElement();

    if (entityType.hasStream()) {
      writer.writeStartElement(NS_ATOM, "content");
      writer.writeAttribute("type", entity.getMediaContentType());
      if (entity.getMediaContentSource() != null) {
        writer.writeAttribute("src", entity.getMediaContentSource().toString());
      } else {
        String id = entity.getId().toASCIIString();
        if (id.endsWith("/")) {
          writer.writeAttribute("src", id + "$value");
        } else {
          writer.writeAttribute("src", id + "/$value");
        }
      }
      writer.writeEndElement();
    }

    // write media links
    for (Link link : entity.getMediaEditLinks()) {
      writeLink(writer, link);
    }

    EdmEntityType resolvedType = resolveEntityType(metadata, entityType, entity.getType());
    writeNavigationProperties(metadata, resolvedType, entity, expand, writer);

    writer.writeStartElement(ATOM, "category", NS_ATOM);
    writer.writeAttribute("scheme", NS_SCHEMA);
    writer.writeAttribute("term", "#" + resolvedType.getFullQualifiedName().getFullQualifiedNameAsString());
    writer.writeEndElement();

    // In the case media, content is sibiling
    if (!entityType.hasStream()) {
      writer.writeStartElement(NS_ATOM, "content");
      writer.writeAttribute("type", "application/xml");
    }

    writer.writeStartElement(METADATA, "properties", NS_METADATA);
    writeProperties(metadata, resolvedType, entity.getProperties(), select, writer);
    writer.writeEndElement(); // properties

    if (!entityType.hasStream()) { // content
      writer.writeEndElement();
    }
    writer.writeEndElement(); // entry
  }

  private void writerAuthorInfo(final String title, final XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartElement(NS_ATOM, "title");
    if (title != null) {
      writer.writeCharacters(title);
    }
    writer.writeEndElement();
    writer.writeStartElement(NS_ATOM, "summary");
    writer.writeEndElement();

    writer.writeStartElement(NS_ATOM, "updated");
    writer.writeCharacters(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .format(new Date(System.currentTimeMillis())));
    writer.writeEndElement();

    writer.writeStartElement(NS_ATOM, "author");
    writer.writeStartElement(NS_ATOM, "name");
    writer.writeEndElement();
    writer.writeEndElement();
  }

  protected EdmEntityType resolveEntityType(final ServiceMetadata metadata, final EdmEntityType baseType,
      final String derivedTypeName) throws SerializerException {
    if (derivedTypeName == null ||
        baseType.getFullQualifiedName().getFullQualifiedNameAsString().equals(derivedTypeName)) {
      return baseType;
    }
    EdmEntityType derivedType = metadata.getEdm().getEntityType(new FullQualifiedName(derivedTypeName));
    if (derivedType == null) {
      throw new SerializerException("EntityType not found",
          SerializerException.MessageKeys.UNKNOWN_TYPE, derivedTypeName);
    }
    EdmEntityType type = derivedType.getBaseType();
    while (type != null) {
      if (type.getFullQualifiedName().getFullQualifiedNameAsString()
          .equals(baseType.getFullQualifiedName().getFullQualifiedNameAsString())) {
        return derivedType;
      }
      type = type.getBaseType();
    }
    throw new SerializerException("Wrong base type",
        SerializerException.MessageKeys.WRONG_BASE_TYPE, derivedTypeName, baseType
            .getFullQualifiedName().getFullQualifiedNameAsString());
  }

  protected EdmComplexType resolveComplexType(final ServiceMetadata metadata, final EdmComplexType baseType,
      final String derivedTypeName) throws SerializerException {
    if (derivedTypeName == null ||
        baseType.getFullQualifiedName().getFullQualifiedNameAsString().equals(derivedTypeName)) {
      return baseType;
    }
    EdmComplexType derivedType = metadata.getEdm().getComplexType(new FullQualifiedName(derivedTypeName));
    if (derivedType == null) {
      throw new SerializerException("Complex Type not found",
          SerializerException.MessageKeys.UNKNOWN_TYPE, derivedTypeName);
    }
    EdmComplexType type = derivedType.getBaseType();
    while (type != null) {
      if (type.getFullQualifiedName().getFullQualifiedNameAsString()
          .equals(baseType.getFullQualifiedName().getFullQualifiedNameAsString())) {
        return derivedType;
      }
      type = type.getBaseType();
    }
    throw new SerializerException("Wrong base type",
        SerializerException.MessageKeys.WRONG_BASE_TYPE, derivedTypeName, baseType
            .getFullQualifiedName().getFullQualifiedNameAsString());
  }

  protected void writeProperties(final ServiceMetadata metadata, final EdmStructuredType type,
      final List<Property> properties, final SelectOption select, final XMLStreamWriter writer)
      throws XMLStreamException, SerializerException {
    final boolean all = ExpandSelectHelper.isAll(select);
    final Set<String> selected = all ? new HashSet<String>() :
        ExpandSelectHelper.getSelectedPropertyNames(select.getSelectItems());
    for (final String propertyName : type.getPropertyNames()) {
      if (all || selected.contains(propertyName)) {
        final EdmProperty edmProperty = type.getStructuralProperty(propertyName);
        final Property property = findProperty(propertyName, properties);
        final Set<List<String>> selectedPaths = all || edmProperty.isPrimitive() ? null :
            ExpandSelectHelper.getSelectedPaths(select.getSelectItems(), propertyName);
        writeProperty(metadata, edmProperty, property, selectedPaths, writer);
      }
    }
  }

  protected void writeNavigationProperties(final ServiceMetadata metadata,
      final EdmStructuredType type, final Linked linked, final ExpandOption expand,
      final XMLStreamWriter writer) throws SerializerException, XMLStreamException {
    if (ExpandSelectHelper.hasExpand(expand)) {
      final boolean expandAll = ExpandSelectHelper.isExpandAll(expand);
      final Set<String> expanded = expandAll ? new HashSet<String>() :
          ExpandSelectHelper.getExpandedPropertyNames(expand.getExpandItems());
      for (final String propertyName : type.getNavigationPropertyNames()) {
        final EdmNavigationProperty property = type.getNavigationProperty(propertyName);
        final Link navigationLink = getOrCreateLink(linked, propertyName);
        if (expandAll || expanded.contains(propertyName)) {
          final ExpandItem innerOptions = expandAll ? null :
              ExpandSelectHelper.getExpandItem(expand.getExpandItems(), propertyName);
          if (innerOptions != null && innerOptions.getLevelsOption() != null) {
            throw new SerializerException("Expand option $levels is not supported.",
                SerializerException.MessageKeys.NOT_IMPLEMENTED);
          }
          if (navigationLink != null) {
            writeLink(writer, navigationLink, false);
            writer.writeStartElement(METADATA, "inline", NS_METADATA);
            writeExpandedNavigationProperty(metadata, property, navigationLink,
                innerOptions == null ? null : innerOptions.getExpandOption(),
                innerOptions == null ? null : innerOptions.getSelectOption(),
                writer);
            writer.writeEndElement();
            writer.writeEndElement();
          }
        } else {
          writeLink(writer, getOrCreateLink(linked, propertyName));
        }
      }
    } else {
      for (final String propertyName : type.getNavigationPropertyNames()) {
        writeLink(writer, getOrCreateLink(linked, propertyName));
      }
    }
    for (Link link : linked.getAssociationLinks()) {
      writeLink(writer, link);
    }
  }

  protected Link getOrCreateLink(final Linked linked, final String navigationPropertyName)
      throws XMLStreamException {
    Link link = linked.getNavigationLink(navigationPropertyName);
    if (link == null) {
      link = new Link();
      link.setRel("http://docs.oasis-open.org/odata/ns/related/" + navigationPropertyName);
      link.setType(Constants.ENTITY_SET_NAVIGATION_LINK_TYPE);
      link.setTitle(navigationPropertyName);
      EntityCollection target = new EntityCollection();
      link.setInlineEntitySet(target);
      link.setHref(linked.getId().toASCIIString() + "/" + navigationPropertyName);
    }
    return link;
  }

  private void writeLink(final XMLStreamWriter writer, final Link link) throws XMLStreamException {
    writeLink(writer, link, true);
  }

  private void writeLink(final XMLStreamWriter writer, final Link link, final boolean close)
      throws XMLStreamException {
    writer.writeStartElement(ATOM, "link", NS_ATOM);
    writer.writeAttribute("rel", link.getRel());
    if (link.getType() != null) {
      writer.writeAttribute("type", link.getType());
    }
    if (link.getTitle() != null) {
      writer.writeAttribute("title", link.getTitle());
    }
    writer.writeAttribute("href", link.getHref());
    if (close) {
      writer.writeEndElement();
    }
  }

  protected void writeExpandedNavigationProperty(final ServiceMetadata metadata,
      final EdmNavigationProperty property, final Link navigationLink,
      final ExpandOption innerExpand, final SelectOption innerSelect, final XMLStreamWriter writer)
      throws XMLStreamException, SerializerException {
    if (property.isCollection()) {
      if (navigationLink != null && navigationLink.getInlineEntitySet() != null) {
        writer.writeStartElement(ATOM, "feed", NS_ATOM);
        writeEntitySet(metadata, property.getType(), navigationLink.getInlineEntitySet(), innerExpand,
            innerSelect, writer);
        writer.writeEndElement();
      }
    } else {
      if (navigationLink != null && navigationLink.getInlineEntity() != null) {
        writeEntity(metadata, property.getType(), navigationLink.getInlineEntity(), null,
            innerExpand, innerSelect, writer, false);
      }
    }
  }

  protected void writeProperty(final ServiceMetadata metadata, final EdmProperty edmProperty,
      final Property property,
      final Set<List<String>> selectedPaths, final XMLStreamWriter writer) throws XMLStreamException,
      SerializerException {
    writer.writeStartElement(DATA, edmProperty.getName(), NS_DATA);
    if (property == null || property.isNull()) {
      if (edmProperty.isNullable() == Boolean.FALSE) {
        throw new SerializerException("Non-nullable property not present!",
            SerializerException.MessageKeys.MISSING_PROPERTY, edmProperty.getName());
      } else {
        writer.writeAttribute(METADATA, NS_METADATA, "null", "true");
      }
    } else {
      writePropertyValue(metadata, edmProperty, property, selectedPaths, writer);
    }
    writer.writeEndElement();
  }

  private String collectionType(EdmType type) {
    return "#Collection(" + type.getFullQualifiedName().getFullQualifiedNameAsString() + ")";
  }

  private String complexType(ServiceMetadata metadata, EdmComplexType baseType, String definedType)
      throws SerializerException {
    EdmComplexType type = resolveComplexType(metadata, baseType, definedType);
    return type.getFullQualifiedName().getFullQualifiedNameAsString();
  }

  private String derivedComplexType(EdmComplexType baseType, String definedType) throws SerializerException {
    String derived = baseType.getFullQualifiedName().getFullQualifiedNameAsString();
    if (derived.equals(definedType)) {
      return null;
    }
    return definedType;
  }

  private void writePropertyValue(final ServiceMetadata metadata, final EdmProperty edmProperty,
      final Property property, final Set<List<String>> selectedPaths,
      final XMLStreamWriter writer) throws XMLStreamException, SerializerException {
    try {
      if (edmProperty.isPrimitive()) {
        if (edmProperty.isCollection()) {
          writer.writeAttribute(METADATA, NS_METADATA, "type", "#Collection(" + edmProperty.getType().getName() + ")");
          writePrimitiveCollection((EdmPrimitiveType) edmProperty.getType(), property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
              writer);
        } else {
          writePrimitive((EdmPrimitiveType) edmProperty.getType(), property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
              writer);
        }
      } else if (edmProperty.isCollection()) {
        writer.writeAttribute(METADATA, NS_METADATA, "type", collectionType(edmProperty.getType()));
        writeComplexCollection(metadata, (EdmComplexType) edmProperty.getType(), property, selectedPaths, writer);
      } else if (property.isComplex()) {
        writer.writeAttribute(METADATA, NS_METADATA, "type",
            "#" + complexType(metadata, (EdmComplexType) edmProperty.getType(), property.getType()));
        writeComplexValue(metadata, (EdmComplexType) edmProperty.getType(), property.asComplex().getValue(),
            selectedPaths, writer);
      } else if (property.isEnum()) {
        writePrimitive((EdmPrimitiveType) edmProperty.getType(), property,
            edmProperty.isNullable(), edmProperty.getMaxLength(),
            edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
            writer);
      } else {
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
      }
    } catch (final EdmPrimitiveTypeException e) {
      throw new SerializerException("Wrong value for property!", e,
          SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
          edmProperty.getName(), property.getValue().toString());
    }
  }

  private void writePrimitiveCollection(final EdmPrimitiveType type, final Property property,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode,
      final XMLStreamWriter writer) throws XMLStreamException, EdmPrimitiveTypeException, SerializerException {
    for (Object value : property.asCollection()) {
      writer.writeStartElement(METADATA, "element", NS_METADATA);
      switch (property.getValueType()) {
      case COLLECTION_PRIMITIVE:
      case COLLECTION_ENUM:
        writePrimitiveValue(type, value, isNullable, maxLength, precision, scale, isUnicode, writer);
        break;
      case COLLECTION_GEOSPATIAL:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      default:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      }
      writer.writeEndElement();
    }
  }

  private void writeComplexCollection(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final Set<List<String>> selectedPaths, final XMLStreamWriter writer)
      throws XMLStreamException, SerializerException {
    for (Object value : property.asCollection()) {
      writer.writeStartElement(METADATA, "element", NS_METADATA);
      if (derivedComplexType(type, property.getType()) != null) {
        writer.writeAttribute(METADATA, NS_METADATA, "type", property.getType());
      }
      switch (property.getValueType()) {
      case COLLECTION_COMPLEX:
        writeComplexValue(metadata, type, ((ComplexValue) value).getValue(), selectedPaths, writer);
        break;
      default:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      }
      writer.writeEndElement();
    }
  }

  private void writePrimitive(final EdmPrimitiveType type, final Property property,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final XMLStreamWriter writer)
      throws EdmPrimitiveTypeException, XMLStreamException, SerializerException {
    if (property.isPrimitive()) {
      if (!(type instanceof EdmString)) {
        writer.writeAttribute(METADATA, NS_METADATA, "type", type.getName());
      }
      writePrimitiveValue(type, property.asPrimitive(),
          isNullable, maxLength, precision, scale, isUnicode, writer);
    } else if (property.isGeospatial()) {
      throw new SerializerException("Property type not yet supported!",
          SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
    } else if (property.isEnum()) {
      writer.writeAttribute(METADATA, NS_METADATA, "type", "#" + type.getName());
      writePrimitiveValue(type, property.asEnum(),
          isNullable, maxLength, precision, scale, isUnicode, writer);
    } else {
      throw new SerializerException("Inconsistent property type!",
          SerializerException.MessageKeys.INCONSISTENT_PROPERTY_TYPE, property.getName());
    }
  }

  protected void writePrimitiveValue(final EdmPrimitiveType type, final Object primitiveValue,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode,
      final XMLStreamWriter writer) throws EdmPrimitiveTypeException, XMLStreamException {
    final String value = type.valueToString(primitiveValue,
        isNullable, maxLength, precision, scale, isUnicode);
    if (value == null) {
      writer.writeAttribute(DATA, NS_DATA, "null", "true");
    } else {
      writer.writeCharacters(value);
    }
  }

  protected void writeComplexValue(final ServiceMetadata metadata, final EdmComplexType type,
      final List<Property> properties, final Set<List<String>> selectedPaths, final XMLStreamWriter writer)
      throws XMLStreamException, SerializerException {
    for (final String propertyName : type.getPropertyNames()) {
      final Property property = findProperty(propertyName, properties);
      if (selectedPaths == null || ExpandSelectHelper.isSelected(selectedPaths, propertyName)) {
        writeProperty(metadata, (EdmProperty) type.getProperty(propertyName), property,
            selectedPaths == null ? null : ExpandSelectHelper.getReducedSelectedPaths(selectedPaths, propertyName),
            writer);
      }
    }
  }

  private Property findProperty(final String propertyName, final List<Property> properties) {
    for (final Property property : properties) {
      if (propertyName.equals(property.getName())) {
        return property;
      }
    }
    return null;
  }

  @Override
  public SerializerResult primitive(final ServiceMetadata metadata, final EdmPrimitiveType type,
      final Property property, final PrimitiveSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());

    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);

      writer.writeStartDocument(ODataSerializer.DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(METADATA, "value", NS_METADATA);
      writer.writeNamespace(METADATA, NS_METADATA);
      if (contextURL != null) {
        writer.writeAttribute(METADATA, NS_METADATA, CONTEXT,
            ContextURLBuilder.create(contextURL).toASCIIString());
      }
      writeMetadataETag(metadata, writer);
      if (property.isNull()) {
        writer.writeAttribute(METADATA, NS_METADATA, "null", "true");
      } else {
        writePrimitive(type, property,
            options == null ? null : options.isNullable(),
            options == null ? null : options.getMaxLength(),
            options == null ? null : options.getPrecision(),
            options == null ? null : options.getScale(),
            options == null ? null : options.isUnicode(),
            writer);
      }
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.flush();
      writer.close();
      outputStream.close();

      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final XMLStreamException e) {
      cachedException = new SerializerException(IO_EXCEPTION_TEXT, e,
          SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (final EdmPrimitiveTypeException e) {
      cachedException = new SerializerException("Wrong value for property!", e,
          SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
          property.getName(), property.getValue().toString());
      throw cachedException;
    } catch (IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult complex(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final ComplexSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());

    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      EdmComplexType resolvedType = resolveComplexType(metadata, type, property.getType());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      writer.writeStartDocument(ODataSerializer.DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(METADATA, "value", NS_METADATA);
      writer.writeNamespace(METADATA, NS_METADATA);
      writer.writeNamespace(DATA, NS_DATA);
      writer.writeAttribute(METADATA, NS_METADATA, "type", "#"
          + resolvedType.getFullQualifiedName().getFullQualifiedNameAsString());
      writer.writeAttribute(METADATA, NS_METADATA, CONTEXT,
          ContextURLBuilder.create(contextURL).toASCIIString());
      writeMetadataETag(metadata, writer);
      if (property.isNull()) {
        writer.writeAttribute(METADATA, NS_METADATA, "null", "true");
      } else {
        final List<Property> values = property.asComplex().getValue();
        writeProperties(metadata, resolvedType, values, options == null ? null : options.getSelect(), writer);
      }
      writer.writeEndDocument();
      writer.flush();
      writer.close();
      outputStream.close();

      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final XMLStreamException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult primitiveCollection(final ServiceMetadata metadata, final EdmPrimitiveType type,
      final Property property, final PrimitiveSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());

    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);

      writer.writeStartDocument(ODataSerializer.DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(METADATA, "value", NS_METADATA);
      writer.writeNamespace(METADATA, NS_METADATA);
      if (contextURL != null) {
        writer.writeAttribute(METADATA, NS_METADATA, CONTEXT,
            ContextURLBuilder.create(contextURL).toASCIIString());
      }
      writeMetadataETag(metadata, writer);
      writer.writeAttribute(METADATA, NS_METADATA, "type", "#Collection(" + type.getName() + ")");
      writePrimitiveCollection(type, property,
          options == null ? null : options.isNullable(),
          options == null ? null : options.getMaxLength(),
          options == null ? null : options.getPrecision(),
          options == null ? null : options.getScale(),
          options == null ? null : options.isUnicode(),
          writer);
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.flush();
      writer.close();
      outputStream.close();

      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final XMLStreamException e) {
      cachedException = new SerializerException(IO_EXCEPTION_TEXT, e,
          SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (final EdmPrimitiveTypeException e) {
      cachedException = new SerializerException("Wrong value for property!", e,
          SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
          property.getName(), property.getValue().toString());
      throw cachedException;
    } catch (IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult complexCollection(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final ComplexSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());

    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);

      writer.writeStartElement(METADATA, "value", NS_METADATA);
      writer.writeNamespace(METADATA, NS_METADATA);
      writer.writeNamespace(DATA, NS_DATA);
      writer.writeAttribute(METADATA, NS_METADATA, "type", collectionType(type));
      writer.writeAttribute(METADATA, NS_METADATA, CONTEXT,
          ContextURLBuilder.create(contextURL).toASCIIString());
      writeMetadataETag(metadata, writer);
      writeComplexCollection(metadata, type, property, null, writer);
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.flush();
      writer.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final XMLStreamException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult reference(final ServiceMetadata metadata, final EdmEntitySet edmEntitySet,
      final Entity entity, final ReferenceSerializerOptions options) throws SerializerException {
    return entityReference(entity, options);
  }

  protected SerializerResult entityReference(final Entity entity, ReferenceSerializerOptions options)
      throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      writer.writeStartDocument(ODataSerializer.DEFAULT_CHARSET, "1.0");
      writeReference(entity, options == null ? null : options.getContextURL(), writer, true);
      writer.writeEndDocument();
      writer.flush();
      writer.close();
      outputStream.close();

      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final XMLStreamException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  private void writeReference(Entity entity, ContextURL contextURL, XMLStreamWriter writer, boolean top)
      throws XMLStreamException {
    writer.writeStartElement(METADATA, "ref", NS_METADATA);
    if (top) {
      writer.writeNamespace(METADATA, NS_METADATA);
      if (contextURL != null) { // top-level entity
        writer.writeAttribute(METADATA, NS_METADATA, CONTEXT,
            ContextURLBuilder.create(contextURL).toASCIIString());
      }
    }
    writer.writeAttribute("id", entity.getId().toASCIIString());
    writer.writeEndElement();
  }

  @Override
  public SerializerResult referenceCollection(final ServiceMetadata metadata, final EdmEntitySet edmEntitySet,
      final EntityCollection entityCollection, ReferenceCollectionSerializerOptions options)
      throws SerializerException {
    return entityReferenceCollection(entityCollection, options);
  }

  protected SerializerResult entityReferenceCollection(final EntityCollection entitySet,
      ReferenceCollectionSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      writer.writeStartDocument(ODataSerializer.DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(ATOM, "feed", NS_ATOM);
      writer.writeNamespace(ATOM, NS_ATOM);
      writer.writeNamespace(METADATA, NS_METADATA);
      if (options != null && options.getContextURL() != null) { // top-level entity
        writer.writeAttribute(METADATA, NS_METADATA, CONTEXT, ContextURLBuilder.create(options.getContextURL())
            .toASCIIString());
      }
      if (options != null && options.getCount() != null && options.getCount().getValue()
          && entitySet.getCount() != null) {
        writeCount(entitySet, writer);
      }
      if (entitySet.getNext() != null) {
        writeNextLink(entitySet, writer);
      }
      for (final Entity entity : entitySet.getEntities()) {
        writeReference(entity, options == null ? null : options.getContextURL(), writer, false);
      }
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.flush();
      writer.close();
      outputStream.close();

      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final XMLStreamException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  private void writeCount(final EntityCollection entitySet, XMLStreamWriter writer)
      throws XMLStreamException {
    writer.writeStartElement(METADATA, "count", NS_METADATA);
    writer.writeCharacters(String.valueOf(entitySet.getCount()));
    writer.writeEndElement();
  }

  private void writeNextLink(final EntityCollection entitySet, XMLStreamWriter writer)
      throws XMLStreamException {
    writer.writeStartElement(ATOM, "link", NS_ATOM);
    writer.writeAttribute("rel", "next");
    writer.writeAttribute("href", entitySet.getNext().toASCIIString());
    writer.writeEndElement();
  }
}
