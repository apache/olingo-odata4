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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.AbstractEntityCollection;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.EntityIterator;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.Operation;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.ex.ODataErrorDetail;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.serializer.SerializerStreamResult;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.LevelsExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.ODataWritableContent;
import org.apache.olingo.server.core.serializer.AbstractODataSerializer;
import org.apache.olingo.server.core.serializer.SerializerResultImpl;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.apache.olingo.server.core.serializer.utils.ContextURLBuilder;
import org.apache.olingo.server.core.serializer.utils.ExpandSelectHelper;
import org.apache.olingo.server.core.uri.UriHelperImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;

public class ODataXmlSerializer extends AbstractODataSerializer {

  /** The default character set is UTF-8. */
  private static final String ATOM = "a";
  private static final String NS_ATOM = Constants.NS_ATOM;
  private static final String METADATA = Constants.PREFIX_METADATA;
  private static final String NS_METADATA = Constants.NS_METADATA;
  private static final String DATA = Constants.PREFIX_DATASERVICES;
  private static final String NS_DATA = Constants.NS_DATASERVICES;

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
      writer.writeStartDocument(DEFAULT_CHARSET, "1.0");

      writer.writeStartElement("error");
      writer.writeDefaultNamespace(NS_METADATA);
      writeErrorDetails(String.valueOf(error.getCode()), error.getMessage(), error.getTarget(), writer);
      if (error.getDetails() != null && !error.getDetails().isEmpty()) {
        writer.writeStartElement(Constants.ERROR_DETAILS);
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

  private void writeErrorDetails(final String code, final String message, final String target,
      final XMLStreamWriter writer)
      throws XMLStreamException {
    if (code != null) {
      writer.writeStartElement(Constants.ERROR_CODE);
      writer.writeCharacters(code);
      writer.writeEndElement();
    }

    writer.writeStartElement(Constants.ERROR_MESSAGE);
    writer.writeCharacters(message);
    writer.writeEndElement();

    if (target != null) {
      writer.writeStartElement(Constants.ERROR_TARGET);
      writer.writeCharacters(target);
      writer.writeEndElement();
    }
  }

  @Override
  public SerializerResult entityCollection(final ServiceMetadata metadata,
      final EdmEntityType entityType, final AbstractEntityCollection entitySet,
      final EntityCollectionSerializerOptions options) throws SerializerException {

    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    final String name = contextURL == null ? null : contextURL.getEntitySetOrSingletonOrType();
    if (options != null && options.getWriteOnlyReferences()) {
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
      writer.writeStartDocument(DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(ATOM, Constants.ATOM_ELEM_FEED, NS_ATOM);
      writer.writeNamespace(ATOM, NS_ATOM);
      writer.writeNamespace(METADATA, NS_METADATA);
      writer.writeNamespace(DATA, NS_DATA);

      writer.writeAttribute(METADATA, NS_METADATA, Constants.CONTEXT,
          ContextURLBuilder.create(contextURL).toASCIIString());
      writeMetadataETag(metadata, writer);
      writeOperations(entitySet.getOperations(), writer);
      if (options != null && options.getId() != null) {
        writer.writeStartElement(ATOM, Constants.ATOM_ELEM_ID, NS_ATOM);
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

      boolean writeOnlyRef = (options != null && options.getWriteOnlyReferences());
      if (options == null) {
        writeEntitySet(metadata, entityType, entitySet, null, null, null, null, writer, writeOnlyRef, name, null);
      } else {
        writeEntitySet(metadata, entityType, entitySet,
            options.getExpand(), null, 
            options.getSelect(), options.xml10InvalidCharReplacement(), writer, writeOnlyRef, name, null);
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

  public void entityCollectionIntoStream(ServiceMetadata metadata, EdmEntityType entityType, EntityIterator entitySet,
      EntityCollectionSerializerOptions options, OutputStream outputStream) throws SerializerException {

    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    final String name = contextURL == null ? null : contextURL.getEntitySetOrSingletonOrType();
    SerializerException cachedException;
    try {
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      writer.writeStartDocument(DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(ATOM, Constants.ATOM_ELEM_FEED, NS_ATOM);
      writer.writeNamespace(ATOM, NS_ATOM);
      writer.writeNamespace(METADATA, NS_METADATA);
      writer.writeNamespace(DATA, NS_DATA);

      writer.writeAttribute(METADATA, NS_METADATA, Constants.CONTEXT,
          ContextURLBuilder.create(contextURL).toASCIIString());
      writeMetadataETag(metadata, writer);

      if (options != null && options.getId() != null) {
        writer.writeStartElement(ATOM, Constants.ATOM_ELEM_ID, NS_ATOM);
        writer.writeCharacters(options.getId());
        writer.writeEndElement();
      }

      if (options != null && options.getCount() != null && options.getCount().getValue()
          && entitySet.getCount() != null) {
        writeCount(entitySet, writer);
      }
      if (entitySet!=null && entitySet.getNext() != null) {
        writeNextLink(entitySet, writer);
      }
      boolean writeOnlyRef = (options != null && options.getWriteOnlyReferences());
      if (options == null) {
        writeEntitySet(metadata, entityType, entitySet, null, null, null, null, writer, writeOnlyRef, name, null);
      } else {
        writeEntitySet(metadata, entityType, entitySet,
            options.getExpand(), null, 
            options.getSelect(), options.xml10InvalidCharReplacement(), writer, writeOnlyRef, name, null);
      }

      writer.writeEndElement();
      writer.writeEndDocument();

      writer.flush();
    } catch (final XMLStreamException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    }
  }

  @Override
  public SerializerStreamResult entityCollectionStreamed(ServiceMetadata metadata, EdmEntityType entityType,
      EntityIterator entities, EntityCollectionSerializerOptions options) throws SerializerException {
      return ODataWritableContent.with(entities, entityType, this, metadata, options).build();
  }

  @Override
  public SerializerResult entity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final EntitySerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    final String name = contextURL == null ? null : contextURL.getEntitySetOrSingletonOrType();
    if (options != null && options.getWriteOnlyReferences()) {
      return entityReference(entity,
          ReferenceSerializerOptions.with().contextURL(contextURL).build());
    }

    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      writer.writeStartDocument(DEFAULT_CHARSET, "1.0");
      writeEntity(metadata, entityType, entity, contextURL,
          options == null ? null : options.getExpand(),
          null,
          options == null ? null : options.getSelect(),
          options == null ? null : options.xml10InvalidCharReplacement(),
          writer, true, false, name, null);
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

  private void writeMetadataETag(final ServiceMetadata metadata, final XMLStreamWriter writer)
      throws XMLStreamException {
    if (metadata != null
        && metadata.getServiceMetadataETagSupport() != null
        && metadata.getServiceMetadataETagSupport().getMetadataETag() != null) {
      writer.writeAttribute(METADATA, NS_METADATA, Constants.ATOM_ATTR_METADATAETAG,
          metadata.getServiceMetadataETagSupport().getMetadataETag());
    }
  }

  protected void writeEntitySet(final ServiceMetadata metadata, final EdmEntityType entityType,
      final AbstractEntityCollection entitySet, final ExpandOption expand, 
      final Integer toDepth, final SelectOption select,
      final String xml10InvalidCharReplacement,final XMLStreamWriter writer, 
      final boolean writeOnlyRef, final String name,final Set<String> ancestors) 
          throws XMLStreamException, SerializerException {
    for (final Entity entity : entitySet) {
      writeEntity(metadata, entityType, entity, null, expand, toDepth, select, 
          xml10InvalidCharReplacement, writer, false, writeOnlyRef, name, ancestors);
    }
  }
  
  /**
   * Get the ascii representation of the entity id
   * or thrown an {@link SerializerException} if id is <code>null</code>.
   *
   * @param entity the entity
   * @param entityType the entity Type
   * @param name the entity name
   * @return ascii representation of the entity id
   */
  private String getEntityId(Entity entity, EdmEntityType entityType, String name) throws SerializerException {
    if(entity.getId() == null) {
      if((entity == null || entityType == null || entityType.getKeyPredicateNames() == null 
          || name == null)) {
        throw new SerializerException("Entity id is null.", SerializerException.MessageKeys.MISSING_ID);
      }else{
        final UriHelper uriHelper = new UriHelperImpl(); 
        entity.setId(URI.create(name + '(' + uriHelper.buildKeyPredicate(entityType, entity) + ')'));
      }
    }
    return entity.getId().toASCIIString();
  }  


  protected void writeEntity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final ContextURL contextURL, final ExpandOption expand, final Integer toDepth,
      final SelectOption select, final String xml10InvalidCharReplacement,
      final XMLStreamWriter writer, final boolean top, final boolean writeOnlyRef,String name,  Set<String> ancestors)
      throws XMLStreamException, SerializerException {
    boolean cycle = false;
    if (expand != null) {
      if (ancestors == null) {
        ancestors = new HashSet<String>();
      }
      cycle = !ancestors.add(getEntityId(entity, entityType, name));
    }

    if (cycle || writeOnlyRef) {
      writeReference(entity, contextURL, writer, top);
      return;
    }
    try {
      writer.writeStartElement(ATOM, Constants.ATOM_ELEM_ENTRY, NS_ATOM);
      if (top) {
        writer.writeNamespace(ATOM, NS_ATOM);
        writer.writeNamespace(METADATA, NS_METADATA);
        writer.writeNamespace(DATA, NS_DATA);
  
        if (contextURL != null) { // top-level entity
          writer.writeAttribute(METADATA, NS_METADATA, Constants.CONTEXT,
              ContextURLBuilder.create(contextURL).toASCIIString());
          writeMetadataETag(metadata, writer);
        }
      }
      if (entity.getETag() != null) {
        writer.writeAttribute(METADATA, NS_METADATA, Constants.ATOM_ATTR_ETAG, entity.getETag());
      }
  
      if (entity.getId() != null) {
        writer.writeStartElement(NS_ATOM, Constants.ATOM_ELEM_ID);
        writer.writeCharacters(entity.getId().toASCIIString());
        writer.writeEndElement();
      }
  
      writerAuthorInfo(entity.getTitle(), writer);
  
      if (entity.getId() != null) {
        writer.writeStartElement(NS_ATOM, Constants.ATOM_ELEM_LINK);
        writer.writeAttribute(Constants.ATTR_REL, Constants.EDIT_LINK_REL);
        writer.writeAttribute(Constants.ATTR_HREF, entity.getId().toASCIIString());
        writer.writeEndElement();
      }
  
      if (entityType.hasStream()) {
        writer.writeStartElement(NS_ATOM, Constants.ATOM_ELEM_CONTENT);
        writer.writeAttribute(Constants.ATTR_TYPE, entity.getMediaContentType());
        if (entity.getMediaContentSource() != null) {
          writer.writeAttribute(Constants.ATOM_ATTR_SRC, entity.getMediaContentSource().toString());
        } else {
          String id = entity.getId().toASCIIString();
          writer.writeAttribute(Constants.ATOM_ATTR_SRC,
              id + (id.endsWith("/") ? "" : "/") + "$value");
        }
        writer.writeEndElement();
      }
  
      // write media links
      for (Link link : entity.getMediaEditLinks()) {
        writeLink(writer, link);
      }
  
      EdmEntityType resolvedType = resolveEntityType(metadata, entityType, entity.getType());
      writeNavigationProperties(metadata, resolvedType, entity, expand,
        toDepth, xml10InvalidCharReplacement, ancestors, name, writer);
  
      writer.writeStartElement(ATOM, Constants.ATOM_ELEM_CATEGORY, NS_ATOM);
      writer.writeAttribute(Constants.ATOM_ATTR_SCHEME, Constants.NS_SCHEME);
      writer.writeAttribute(Constants.ATOM_ATTR_TERM,
          "#" + resolvedType.getFullQualifiedName().getFullQualifiedNameAsString());
      writer.writeEndElement();
  
      // In the case media, content is sibiling
      if (!entityType.hasStream()) {
        writer.writeStartElement(NS_ATOM, Constants.ATOM_ELEM_CONTENT);
        writer.writeAttribute(Constants.ATTR_TYPE, "application/xml");
      }
  
      writer.writeStartElement(METADATA, Constants.PROPERTIES, NS_METADATA);
      writeProperties(metadata, resolvedType, entity.getProperties(), select, 
          xml10InvalidCharReplacement, writer, entity, expand);
      writer.writeEndElement(); // properties
  
      if (!entityType.hasStream()) { // content
        writer.writeEndElement();
      }
      
      writeOperations(entity.getOperations(), writer);
      
      writer.writeEndElement(); // entry
    } finally {
      if (!cycle && ancestors != null) {
        ancestors.remove(getEntityId(entity, entityType, name));
      }
    }
  }

  private void writeOperations(final List<Operation> operations,
      final XMLStreamWriter writer) throws XMLStreamException {
    for (Operation operation : operations) {
      boolean action = (operation.getType() != null && operation.getType() == Operation.Type.ACTION);
      writer.writeStartElement(METADATA, action?Constants.ATOM_ELEM_ACTION:Constants.ATOM_ELEM_FUNCTION, NS_METADATA);
      writer.writeAttribute(Constants.ATTR_METADATA, operation.getMetadataAnchor());
      writer.writeAttribute(Constants.ATTR_TITLE, operation.getTitle());
      writer.writeAttribute(Constants.ATTR_TARGET, operation.getTarget().toASCIIString());
      writer.writeEndElement();
    }
  }

  private void writerAuthorInfo(final String title, final XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartElement(NS_ATOM, Constants.ATTR_TITLE);
    if (title != null) {
      writer.writeCharacters(title);
    }
    writer.writeEndElement();
    writer.writeStartElement(NS_ATOM, Constants.ATOM_ELEM_SUMMARY);
    writer.writeEndElement();

    writer.writeStartElement(NS_ATOM, Constants.ATOM_ELEM_UPDATED);
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
      final List<Property> properties, final SelectOption select, final String xml10InvalidCharReplacement, 
      final XMLStreamWriter writer, Linked linked, ExpandOption expand) 
          throws XMLStreamException, SerializerException {
    final boolean all = ExpandSelectHelper.isAll(select);
    final Set<String> selected = all ? new HashSet<String>() :
        ExpandSelectHelper.getSelectedPropertyNames(select.getSelectItems());
    addKeyPropertiesToSelected(selected, type);
    Set<List<String>> expandedPaths = ExpandSelectHelper.getExpandedItemsPath(expand);
    for (final String propertyName : type.getPropertyNames()) {
      if (all || selected.contains(propertyName)) {
        final EdmProperty edmProperty = type.getStructuralProperty(propertyName);
        final Property property = findProperty(propertyName, properties);
        final Set<List<String>> selectedPaths = all || edmProperty.isPrimitive() ? null :
            ExpandSelectHelper.getSelectedPaths(select.getSelectItems(), propertyName);
        writeProperty(metadata, edmProperty, property, selectedPaths, 
            xml10InvalidCharReplacement, writer, expandedPaths, linked, expand);
      }
    }
  }

  private void addKeyPropertiesToSelected(Set<String> selected, EdmStructuredType type) {
    if (!selected.isEmpty() && type instanceof EdmEntityType) {
      List<String> keyNames = ((EdmEntityType) type).getKeyPredicateNames();
      for (String key : keyNames) {
        if (!selected.contains(key)) {
          selected.add(key);
        }
      }
    }
  }
  
  protected void writeNavigationProperties(final ServiceMetadata metadata,
      final EdmStructuredType type, final Linked linked, final ExpandOption expand, final Integer toDepth,
      final String xml10InvalidCharReplacement, final Set<String> ancestors, String name, final XMLStreamWriter writer) 
          throws SerializerException, XMLStreamException {
    if ((toDepth != null && toDepth > 1) || (toDepth == null && ExpandSelectHelper.hasExpand(expand))) {
      final ExpandItem expandAll = ExpandSelectHelper.getExpandAll(expand);
      for (final String propertyName : type.getNavigationPropertyNames()) {
        final ExpandItem innerOptions = ExpandSelectHelper.getExpandItemBasedOnType(expand.getExpandItems(), 
            propertyName, type, name);
        if (expandAll != null || innerOptions != null || toDepth != null) {
          Integer levels = null;
          final EdmNavigationProperty property = type.getNavigationProperty(propertyName);
          final Link navigationLink = getOrCreateLink(linked, propertyName);
          ExpandOption childExpand = null;
          LevelsExpandOption levelsOption = null;
          if (innerOptions != null) {
            levelsOption = innerOptions.getLevelsOption();
            childExpand = levelsOption == null ? innerOptions.getExpandOption() :
              new ExpandOptionImpl().addExpandItem(innerOptions);
          } else if (expandAll != null) {
            levels = 1;
            levelsOption = expandAll.getLevelsOption();
            childExpand = new ExpandOptionImpl().addExpandItem(expandAll);
          } 
          
          if (levelsOption != null) {
            levels = levelsOption.isMax() ? Integer.MAX_VALUE :
              levelsOption.getValue();
          }
          if (toDepth != null) {
            levels = toDepth - 1;
            childExpand = expand;
          }
          writeLink(writer, navigationLink, false);
          writer.writeStartElement(METADATA, Constants.ATOM_ELEM_INLINE, NS_METADATA);
          writeExpandedNavigationProperty(metadata, property, navigationLink,
            childExpand, levels,
            innerOptions == null ? null : innerOptions.getSelectOption(),
            innerOptions == null ? null : innerOptions.getCountOption(),
            innerOptions == null ? false : innerOptions.hasCountPath(),
            innerOptions == null ? false : innerOptions.isRef(),                                    
            xml10InvalidCharReplacement, ancestors, name, writer);
          writer.writeEndElement();
          writer.writeEndElement();
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
      link.setRel(Constants.NS_NAVIGATION_LINK_REL + navigationPropertyName);
      link.setType(Constants.ENTITY_SET_NAVIGATION_LINK_TYPE);
      link.setTitle(navigationPropertyName);
      EntityCollection target = new EntityCollection();
      link.setInlineEntitySet(target);
      if (linked.getId() != null) {
        link.setHref(linked.getId().toASCIIString() + "/" + navigationPropertyName);
      }
    }
    return link;
  }

  private void writeLink(final XMLStreamWriter writer, final Link link) throws XMLStreamException {
    writeLink(writer, link, true);
  }

  private void writeLink(final XMLStreamWriter writer, final Link link, final boolean close)
      throws XMLStreamException {
    writer.writeStartElement(ATOM, Constants.ATOM_ELEM_LINK, NS_ATOM);
    writer.writeAttribute(Constants.ATTR_REL, link.getRel());
    if (link.getType() != null) {
      writer.writeAttribute(Constants.ATTR_TYPE, link.getType());
    }
    if (link.getTitle() != null) {
      writer.writeAttribute(Constants.ATTR_TITLE, link.getTitle());
    }
    if (link.getHref() != null) {
      writer.writeAttribute(Constants.ATTR_HREF, link.getHref());
    }
    if (close) {
      writer.writeEndElement();
    }
  }

  protected void writeExpandedNavigationProperty(final ServiceMetadata metadata,
      final EdmNavigationProperty property, final Link navigationLink,
      final ExpandOption innerExpand, final Integer toDepth, 
      final SelectOption innerSelect, final CountOption coutOption, 
      final boolean writeNavigationCount, final boolean writeOnlyRef,final String xml10InvalidCharReplacement,
      final Set<String> ancestors, String name,
      final XMLStreamWriter writer) throws XMLStreamException, SerializerException {
    if (property.isCollection()) {
      if (navigationLink != null && navigationLink.getInlineEntitySet() != null) {
        writer.writeStartElement(ATOM, Constants.ATOM_ELEM_FEED, NS_ATOM);
        if (writeNavigationCount) {
          writeCount(navigationLink.getInlineEntitySet(), writer);
        } else {
          if (coutOption != null && coutOption.getValue()) {
            writeCount(navigationLink.getInlineEntitySet(), writer);
          }
          writeEntitySet(metadata, property.getType(), navigationLink.getInlineEntitySet(), innerExpand, toDepth,
              innerSelect, xml10InvalidCharReplacement, writer, writeOnlyRef, name, ancestors);
        }
        writer.writeEndElement();
      }
    } else {
      if (navigationLink != null && navigationLink.getInlineEntity() != null) {
        writeEntity(metadata, property.getType(), navigationLink.getInlineEntity(), null,
            innerExpand, toDepth, innerSelect, xml10InvalidCharReplacement, writer, 
            false, writeOnlyRef, name, ancestors);
      }
    }
  }

  protected void writeProperty(final ServiceMetadata metadata,
      final EdmProperty edmProperty, final Property property,
      final Set<List<String>> selectedPaths,
      final String xml10InvalidCharReplacement, final XMLStreamWriter writer, 
      Set<List<String>> expandedPaths, Linked linked, ExpandOption expand)
      throws XMLStreamException, SerializerException {
    writer.writeStartElement(DATA, edmProperty.getName(), NS_DATA);
    if (property == null || property.isNull()) {
      if (edmProperty.isNullable()) {
        writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_NULL, "true");
      } else {
        throw new SerializerException("Non-nullable property not present!",
            SerializerException.MessageKeys.MISSING_PROPERTY, edmProperty.getName());
      }
    } else {
      writePropertyValue(metadata, edmProperty, property, selectedPaths, 
          xml10InvalidCharReplacement, writer, expandedPaths, linked, expand);
    }
    writer.writeEndElement();
  }

  private String collectionType(final EdmType type) {
    return "#Collection(" + type.getFullQualifiedName().getFullQualifiedNameAsString() + ")";
  }

  private String complexType(final ServiceMetadata metadata, final EdmComplexType baseType, final String definedType)
      throws SerializerException {
    EdmComplexType type = resolveComplexType(metadata, baseType, definedType);
    return type.getFullQualifiedName().getFullQualifiedNameAsString();
  }

  private String derivedComplexType(final EdmComplexType baseType,
      final String definedType) throws SerializerException {
    String base = baseType.getFullQualifiedName().getFullQualifiedNameAsString();
    if (base.equals(definedType)) {
      return null;
    }
    return definedType;
  }

  private void writePropertyValue(final ServiceMetadata metadata,
      final EdmProperty edmProperty, final Property property,
      final Set<List<String>> selectedPaths,
      final String xml10InvalidCharReplacement, final XMLStreamWriter writer, 
      Set<List<String>> expandedPaths, Linked linked, ExpandOption expand)
      throws XMLStreamException, SerializerException {
    try {
      if (edmProperty.isPrimitive()
          || edmProperty.getType().getKind() == EdmTypeKind.ENUM
          || edmProperty.getType().getKind() == EdmTypeKind.DEFINITION) {
        if (edmProperty.isCollection()) {
          writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_TYPE,
              edmProperty.isPrimitive() ?
                  "#Collection(" + edmProperty.getType().getName() + ")" :
                  collectionType(edmProperty.getType()));
          writePrimitiveCollection((EdmPrimitiveType) edmProperty.getType(), property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
              xml10InvalidCharReplacement,writer);
        } else {
          writePrimitive((EdmPrimitiveType) edmProperty.getType(), property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
              xml10InvalidCharReplacement, writer);
        }
      } else if (property.isComplex()) {
        if (edmProperty.isCollection()) {
          writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_TYPE, collectionType(edmProperty.getType()));
          writeComplexCollection(metadata, (EdmComplexType) edmProperty.getType(), property, selectedPaths, 
              xml10InvalidCharReplacement, writer, expandedPaths, linked, expand);
        } else {
            writeComplex(metadata, edmProperty, property, selectedPaths, 
                xml10InvalidCharReplacement, writer, expandedPaths, linked, expand);
        }
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

  private void writeComplex(final ServiceMetadata metadata,
      final EdmProperty edmProperty, final Property property,
      final Set<List<String>> selectedPaths,
      final String xml10InvalidCharReplacement, final XMLStreamWriter writer, 
      Set<List<String>> expandedPaths, Linked linked, ExpandOption expand) 
          throws XMLStreamException, SerializerException{
      
       writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_TYPE,
              "#" + complexType(metadata, (EdmComplexType) edmProperty.getType(), 
                      property.getType()));
        String derivedName = property.getType();
       final EdmComplexType resolvedType = resolveComplexType(metadata,
        (EdmComplexType) edmProperty.getType(), derivedName);
       
       if (null != linked) {
         if (linked instanceof Entity) {
           linked = ((Entity)linked).getProperty(property.getName()).asComplex();
         } else if (linked instanceof ComplexValue) {
           List<Property> complexProperties = ((ComplexValue)linked).getValue();
           for (Property prop : complexProperties) {
             if (prop.getName().equals(property.getName())) {
               linked = prop.asComplex();
               break;
             }
           }
         }
         expandedPaths = expandedPaths == null || expandedPaths.isEmpty() ? null :
           ExpandSelectHelper.getReducedExpandItemsPaths(expandedPaths, property.getName());
       }
       
        writeComplexValue(metadata, resolvedType, property.asComplex().getValue(),
           selectedPaths, xml10InvalidCharReplacement, writer, expandedPaths, linked, expand, property.getName());
  }
  private void writePrimitiveCollection(final EdmPrimitiveType type, final Property property,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final String xml10InvalidCharReplacement,
      final XMLStreamWriter writer) throws XMLStreamException, EdmPrimitiveTypeException, SerializerException {
    for (Object value : property.asCollection()) {
      writer.writeStartElement(METADATA, Constants.ELEM_ELEMENT, NS_METADATA);
      switch (property.getValueType()) {
      case COLLECTION_PRIMITIVE:
      case COLLECTION_ENUM:
        writePrimitiveValue(type, value, isNullable, maxLength, precision,
            scale, isUnicode, xml10InvalidCharReplacement, writer);
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

  private void writeComplexCollection(final ServiceMetadata metadata,
      final EdmComplexType type, final Property property, final Set<List<String>> selectedPaths,
      final String xml10InvalidCharReplacement, final XMLStreamWriter writer, 
      Set<List<String>> expandedPaths, Linked linked, ExpandOption expand)
      throws XMLStreamException, SerializerException {
    EdmComplexType complexType = type;
    Set<List<String>> expandedPaths1 = expandedPaths != null && !expandedPaths.isEmpty() ? 
        expandedPaths : ExpandSelectHelper.getExpandedItemsPath(expand);
    for (Object value : property.asCollection()) {
      expandedPaths = expandedPaths1;
      writer.writeStartElement(METADATA, Constants.ELEM_ELEMENT, NS_METADATA);
      String typeName = ((ComplexValue)value).getTypeName();
      String propertyType = typeName != null ? typeName :property.getType();
      if (derivedComplexType(type, propertyType ) != null) {
        writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_TYPE, propertyType);
      }
      if(typeName!=null && !propertyType.equals(type.getFullQualifiedName().getFullQualifiedNameAsString())){
        complexType = (EdmComplexType) (metadata.getEdm().getComplexType(new FullQualifiedName(propertyType)));
      }else{
        complexType = type;
      }
      switch (property.getValueType()) {
      case COLLECTION_COMPLEX:
        expandedPaths = expandedPaths == null || expandedPaths.isEmpty() ? null :
          ExpandSelectHelper.getReducedExpandItemsPaths(expandedPaths, property.getName());
        writeComplexValue(metadata, complexType,
            ((ComplexValue) value).getValue(), selectedPaths,
            xml10InvalidCharReplacement, writer, expandedPaths, (ComplexValue) value, expand, property.getName());
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
      final Boolean isUnicode, final String xml10InvalidCharReplacement, final XMLStreamWriter writer)
      throws EdmPrimitiveTypeException, XMLStreamException, SerializerException {
    if (property.isPrimitive()) {
      if (type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String)) {
        writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_TYPE,
            type.getKind() == EdmTypeKind.DEFINITION ?
                "#" + type.getFullQualifiedName().getFullQualifiedNameAsString() :
                type.getName());
      }
      writePrimitiveValue(type, property.asPrimitive(),
          isNullable, maxLength, precision, scale, isUnicode, xml10InvalidCharReplacement, writer);
    } else if (property.isGeospatial()) {
      throw new SerializerException("Property type not yet supported!",
          SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
    } else if (property.isEnum()) {
      writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_TYPE,
          "#" + type.getFullQualifiedName().getFullQualifiedNameAsString());
      writePrimitiveValue(type, property.asEnum(),
          isNullable, maxLength, precision, scale, isUnicode, xml10InvalidCharReplacement, writer);
    } else {
      throw new SerializerException("Inconsistent property type!",
          SerializerException.MessageKeys.INCONSISTENT_PROPERTY_TYPE, property.getName());
    }
  }

  protected void writePrimitiveValue(final EdmPrimitiveType type, final Object primitiveValue,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final String xml10InvalidCharReplacement,
      final XMLStreamWriter writer) throws EdmPrimitiveTypeException, XMLStreamException {
    final String value = type.valueToString(primitiveValue,
        isNullable, maxLength, precision, scale, isUnicode);
    if (value == null) {
      writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_NULL, "true");
    } else {
      // XML 1.0 does not handle certain unicode characters, they need to be replaced
      writer.writeCharacters(replaceInvalidCharacters(type, value,
          isUnicode, xml10InvalidCharReplacement));
    }
  }

  protected void writeComplexValue(final ServiceMetadata metadata,
      final EdmComplexType type, final List<Property> properties, 
      final Set<List<String>> selectedPaths, final String xml10InvalidCharReplacement, 
      final XMLStreamWriter writer, Set<List<String>> expandedPaths, 
      Linked linked, ExpandOption expand, String complexPropName) throws XMLStreamException, SerializerException {   
    
    if (null != expandedPaths) {
      for(List<String> paths : expandedPaths) {
        if (!paths.isEmpty() && paths.size() == 1) {
          expandedPaths = ExpandSelectHelper.getReducedExpandItemsPaths(expandedPaths, paths.get(0));
        }
      }
    }
    
    for (final String propertyName : type.getPropertyNames()) {
      final Property property = findProperty(propertyName, properties);
      if (selectedPaths == null || ExpandSelectHelper.isSelected(selectedPaths, propertyName)) {
        writeProperty(metadata, (EdmProperty) type.getProperty(propertyName), property,
            selectedPaths == null ? null : ExpandSelectHelper.getReducedSelectedPaths(selectedPaths, propertyName),
            xml10InvalidCharReplacement, writer, expandedPaths, linked, expand);
      }
    }
    writeNavigationProperties(metadata, type, linked, 
        expand, null, xml10InvalidCharReplacement, null, complexPropName, writer);
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

      writer.writeStartDocument(DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(METADATA, Constants.VALUE, NS_METADATA);
      writer.writeNamespace(METADATA, NS_METADATA);
      if (contextURL != null) {
        writer.writeAttribute(METADATA, NS_METADATA, Constants.CONTEXT,
            ContextURLBuilder.create(contextURL).toASCIIString());
      }
      writeMetadataETag(metadata, writer);
      if (property.isNull()) {
        writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_NULL, "true");
      } else {
        writePrimitive(type, property,
            options == null ? null : options.isNullable(),
            options == null ? null : options.getMaxLength(),
            options == null ? null : options.getPrecision(),
            options == null ? null : options.getScale(),
            options == null ? null : options.isUnicode(),
            options == null ? null : options.xml10InvalidCharReplacement(),
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
      EdmComplexType resolvedType = null;
      if (!type.getFullQualifiedName().getFullQualifiedNameAsString().
          equals(property.getType())) {
        if (type.getBaseType() != null && 
            type.getBaseType().getFullQualifiedName().getFullQualifiedNameAsString().
            equals(property.getType())) {
          resolvedType = resolveComplexType(metadata, type.getBaseType(), 
              type.getFullQualifiedName().getFullQualifiedNameAsString());
        } else {
          resolvedType = resolveComplexType(metadata, type, property.getType());
        }
      } else {
        resolvedType = resolveComplexType(metadata, type, property.getType());
      }
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      writer.writeStartDocument(DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(METADATA, Constants.VALUE, NS_METADATA);
      writer.writeNamespace(METADATA, NS_METADATA);
      writer.writeNamespace(DATA, NS_DATA);
      writer.writeNamespace(ATOM, NS_ATOM);
      writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_TYPE,
          "#" + resolvedType.getFullQualifiedName().getFullQualifiedNameAsString());
      writer.writeAttribute(METADATA, NS_METADATA, Constants.CONTEXT,
          ContextURLBuilder.create(contextURL).toASCIIString());
      writeMetadataETag(metadata, writer);
      if (property.isNull()) {
        writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_NULL, "true");
      } else {
        final List<Property> values = property.asComplex().getValue();
        writeProperties(metadata, resolvedType, values, 
            options == null ? null : options.getSelect(),
            options == null ? null : options.xml10InvalidCharReplacement(),
            writer, property.asComplex(), options == null ? null : options.getExpand());
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

      writer.writeStartDocument(DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(METADATA, Constants.VALUE, NS_METADATA);
      writer.writeNamespace(METADATA, NS_METADATA);
      if (contextURL != null) {
        writer.writeAttribute(METADATA, NS_METADATA, Constants.CONTEXT,
            ContextURLBuilder.create(contextURL).toASCIIString());
      }
      writeMetadataETag(metadata, writer);
      writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_TYPE, "#Collection(" + type.getName() + ")");
      writePrimitiveCollection(type, property,
          options == null ? null : options.isNullable(),
          options == null ? null : options.getMaxLength(),
          options == null ? null : options.getPrecision(),
          options == null ? null : options.getScale(),
          options == null ? null : options.isUnicode(),
          options == null ? null : options.xml10InvalidCharReplacement(),
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
      writer.writeStartDocument(DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(METADATA, Constants.VALUE, NS_METADATA);
      writer.writeNamespace(METADATA, NS_METADATA);
      writer.writeNamespace(DATA, NS_DATA);
      writer.writeNamespace(ATOM, NS_ATOM);
      writer.writeAttribute(METADATA, NS_METADATA, Constants.ATTR_TYPE, collectionType(type));
      writer.writeAttribute(METADATA, NS_METADATA, Constants.CONTEXT,
          ContextURLBuilder.create(contextURL).toASCIIString());
      writeMetadataETag(metadata, writer);
      Set<List<String>> selectedPaths = null;
      if (null != options && null != options.getSelect()) {
        final boolean all = ExpandSelectHelper.isAll(options.getSelect());
        selectedPaths = all || property.isPrimitive() ? null : ExpandSelectHelper
            .getSelectedPaths(options.getSelect().getSelectItems());
      }
      Set<List<String>> expandPaths = null;
      if (null != options && null != options.getExpand()) {
        expandPaths = ExpandSelectHelper.getExpandedItemsPath(options.getExpand());
      }
      
      writeComplexCollection(metadata, type, property, selectedPaths, 
          options == null ? null:options.xml10InvalidCharReplacement(), writer, expandPaths, null, 
              options == null ? null : options.getExpand());
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

  protected SerializerResult entityReference(final Entity entity, final ReferenceSerializerOptions options)
      throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      writer.writeStartDocument(DEFAULT_CHARSET, "1.0");
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

  private void writeReference(final Entity entity, final ContextURL contextURL, final XMLStreamWriter writer,
      final boolean top)
      throws XMLStreamException {
    writer.writeStartElement(METADATA, "ref", NS_METADATA);
    if (top) {
      writer.writeNamespace(METADATA, NS_METADATA);
      if (contextURL != null) { // top-level entity
        writer.writeAttribute(METADATA, NS_METADATA, Constants.CONTEXT,
            ContextURLBuilder.create(contextURL).toASCIIString());
      }
    }
    writer.writeAttribute(Constants.ATOM_ATTR_ID, entity.getId().toASCIIString());
    writer.writeEndElement();
  }

  @Override
  public SerializerResult referenceCollection(final ServiceMetadata metadata, final EdmEntitySet edmEntitySet,
      final AbstractEntityCollection entityCollection, final ReferenceCollectionSerializerOptions options)
      throws SerializerException {
    return entityReferenceCollection(entityCollection, options);
  }

  protected SerializerResult entityReferenceCollection(final AbstractEntityCollection entitySet,
      final ReferenceCollectionSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, DEFAULT_CHARSET);
      writer.writeStartDocument(DEFAULT_CHARSET, "1.0");
      writer.writeStartElement(ATOM, Constants.ATOM_ELEM_FEED, NS_ATOM);
      writer.writeNamespace(ATOM, NS_ATOM);
      writer.writeNamespace(METADATA, NS_METADATA);
      if (options != null && options.getContextURL() != null) { // top-level entity
        writer.writeAttribute(METADATA, NS_METADATA, Constants.CONTEXT,
            ContextURLBuilder.create(options.getContextURL()).toASCIIString());
      }
      if (options != null && options.getCount() != null && options.getCount().getValue()
          && entitySet.getCount() != null) {
        writeCount(entitySet, writer);
      }
      if (entitySet.getNext() != null) {
        writeNextLink(entitySet, writer);
      }
      for (final Entity entity : entitySet) {
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

  private void writeCount(final AbstractEntityCollection entitySet, final XMLStreamWriter writer)
      throws XMLStreamException {
    writer.writeStartElement(METADATA, Constants.ATOM_ELEM_COUNT, NS_METADATA);
    writer.writeCharacters(String.valueOf(entitySet.getCount()==null?0:entitySet.getCount()));
    writer.writeEndElement();
  }

  private void writeNextLink(final AbstractEntityCollection entitySet, final XMLStreamWriter writer)
      throws XMLStreamException {
    writer.writeStartElement(ATOM, Constants.ATOM_ELEM_LINK, NS_ATOM);
    writer.writeAttribute(Constants.ATTR_REL, Constants.NEXT_LINK_REL);
    writer.writeAttribute(Constants.ATTR_HREF, entitySet.getNext().toASCIIString());
    writer.writeEndElement();
  }
  
  static String replaceInvalidCharacters(EdmPrimitiveType expectedType,
      String value, Boolean isUniCode, String invalidCharacterReplacement) {
    if (!(expectedType instanceof EdmString)
        || invalidCharacterReplacement == null || isUniCode == null || !isUniCode) {
      return value;
    }
    String s = value;
    StringBuilder result = null;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c <= 0x0020 && c != ' ' && c != '\n' && c != '\t' && c != '\r') {
        if (result == null) {
          result = new StringBuilder();
          result.append(s.substring(0, i));
        }
        result.append(invalidCharacterReplacement);
      } else if (result != null) {
        result.append(c);
      }
    }
    if (result == null) {
      return value;
    }
    return result.toString();
  }
}
