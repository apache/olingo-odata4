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
package org.apache.olingo.server.core.serializer.json;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.IConstants;
import org.apache.olingo.commons.api.constants.Constantsv00;
import org.apache.olingo.commons.api.data.AbstractEntityCollection;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
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
import org.apache.olingo.commons.api.edm.geo.ComposedGeospatial;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
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
import org.apache.olingo.server.core.serializer.utils.ContentTypeHelper;
import org.apache.olingo.server.core.serializer.utils.ContextURLBuilder;
import org.apache.olingo.server.core.serializer.utils.ExpandSelectHelper;
import org.apache.olingo.server.core.uri.UriHelperImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class ODataJsonSerializer extends AbstractODataSerializer {

  private static final Map<Geospatial.Type, String> geoValueTypeToJsonName;
  static {
    Map<Geospatial.Type, String> temp = new EnumMap<Geospatial.Type, String>(Geospatial.Type.class);
    temp.put(Geospatial.Type.POINT, Constants.ELEM_POINT);
    temp.put(Geospatial.Type.MULTIPOINT, Constants.ELEM_MULTIPOINT);
    temp.put(Geospatial.Type.LINESTRING, Constants.ELEM_LINESTRING);
    temp.put(Geospatial.Type.MULTILINESTRING, "MultiLineString");
    temp.put(Geospatial.Type.POLYGON, Constants.ELEM_POLYGON);
    temp.put(Geospatial.Type.MULTIPOLYGON, "MultiPolygon");
    temp.put(Geospatial.Type.GEOSPATIALCOLLECTION, "GeometryCollection");
    geoValueTypeToJsonName = Collections.unmodifiableMap(temp);
  }

  private final boolean isIEEE754Compatible;
  private final boolean isODataMetadataNone;
  private final boolean isODataMetadataFull;
  private IConstants constants;

  public ODataJsonSerializer(final ContentType contentType, final IConstants constants) {
    isIEEE754Compatible = ContentTypeHelper.isODataIEEE754Compatible(contentType);
    isODataMetadataNone = ContentTypeHelper.isODataMetadataNone(contentType);
    isODataMetadataFull = ContentTypeHelper.isODataMetadataFull(contentType);
    this.constants = constants;
  }

  public ODataJsonSerializer(final ContentType contentType) {
    isIEEE754Compatible = ContentTypeHelper.isODataIEEE754Compatible(contentType);
    isODataMetadataNone = ContentTypeHelper.isODataMetadataNone(contentType);
    isODataMetadataFull = ContentTypeHelper.isODataMetadataFull(contentType);
    this.constants = new Constantsv00();
  }

  @Override
  public SerializerResult serviceDocument(final ServiceMetadata metadata, final String serviceRoot)
      throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;

    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      new ServiceDocumentJsonSerializer(metadata, serviceRoot, isODataMetadataNone).writeServiceDocument(json);

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
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
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      new MetadataDocumentJsonSerializer(serviceMetadata).writeMetadataDocument(json);

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult error(final ODataServerError error) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      new ODataErrorSerializer().writeErrorDocument(json, error);

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult entityCollection(final ServiceMetadata metadata,
      final EdmEntityType entityType, final AbstractEntityCollection entitySet,
      final EntityCollectionSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    boolean pagination = false;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();

      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      String name = contextURL == null ? null:contextURL.getEntitySetOrSingletonOrType();
      writeContextURL(contextURL, json);

      writeMetadataETag(metadata, json);

      if (options != null && options.getCount() != null && options.getCount().getValue()) {
        writeInlineCount("", entitySet.getCount(), json);
      }
      writeOperations(entitySet.getOperations(), json);
      json.writeFieldName(Constants.VALUE);
      if (options == null) {
        writeEntitySet(metadata, entityType, entitySet, null, null, null, false, null, name, json);
      } else {
        writeEntitySet(metadata, entityType, entitySet,
            options.getExpand(), null, options.getSelect(), options.getWriteOnlyReferences(), null, name, json);
      }
      writeNextLink(entitySet, json, pagination);
      writeDeltaLink(entitySet, json, pagination);

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (DecoderException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerStreamResult entityCollectionStreamed(ServiceMetadata metadata, EdmEntityType entityType,
      EntityIterator entities, EntityCollectionSerializerOptions options) throws SerializerException {

    return ODataWritableContent.with(entities, entityType, this, metadata, options).build();
  }

  public void entityCollectionIntoStream(final ServiceMetadata metadata,
      final EdmEntityType entityType, final EntityIterator entitySet,
      final EntityCollectionSerializerOptions options, final OutputStream outputStream)
      throws SerializerException {

    SerializerException cachedException;
    boolean pagination = false;
    try {
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();

      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      writeContextURL(contextURL, json);

      writeMetadataETag(metadata, json);

      if (options != null && options.getCount() != null && options.getCount().getValue()) {
        writeInlineCount("", entitySet.getCount(), json);
      }
      json.writeFieldName(Constants.VALUE);
      String name =  contextURL == null ? null:contextURL.getEntitySetOrSingletonOrType() ;
      if (options == null) {
        writeEntitySet(metadata, entityType, entitySet, null, null, null, false, null, name, json);
      } else {
        writeEntitySet(metadata, entityType, entitySet,
            options.getExpand(), null, options.getSelect(), options.getWriteOnlyReferences(), null, name, json);
      }
      // next link support for streaming results
      writeNextLink(entitySet, json, pagination);

      json.close();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (DecoderException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    }
  }

  @Override
  public SerializerResult entity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final EntitySerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      String name =  contextURL == null ? null:contextURL.getEntitySetOrSingletonOrType();
      writeEntity(metadata, entityType, entity, contextURL,
          options == null ? null : options.getExpand(),
          null,
          options == null ? null : options.getSelect(),
          options == null ? false : options.getWriteOnlyReferences(),
          null, name,
          json);

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (DecoderException e) { 
      cachedException =
        new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  ContextURL checkContextURL(final ContextURL contextURL) throws SerializerException {
    if (isODataMetadataNone) {
      return null;
    } else if (contextURL == null) {
      throw new SerializerException("ContextURL null!", SerializerException.MessageKeys.NO_CONTEXT_URL);
    }
    return contextURL;
  }

  protected void writeEntitySet(final ServiceMetadata metadata, final EdmEntityType entityType,
      final AbstractEntityCollection entitySet, final ExpandOption expand, Integer toDepth, final SelectOption select,
      final boolean onlyReference, final Set<String> ancestors, String name, final JsonGenerator json)
          throws IOException, SerializerException, DecoderException {
    json.writeStartArray();
    for (final Entity entity : entitySet) {
      if (onlyReference) {
        json.writeStartObject();
        json.writeStringField(constants.getId(), getEntityId(entity, entityType, name));
        json.writeEndObject();
      } else {
        writeEntity(metadata, entityType, entity, null, expand, toDepth, select, false, ancestors, name, json);
      }
    }
    json.writeEndArray();
  }

  /**
   * Get the ascii representation of the entity id
   * or thrown an {@link SerializerException} if id is <code>null</code>.
   *
   * @param entity the entity
   * @param entityType 
   * @param name 
   * @return ascii representation of the entity id
   */
  private String getEntityId(Entity entity, EdmEntityType entityType, String name) throws SerializerException {
    if(entity != null && entity.getId() == null) {
      if(entityType == null || entityType.getKeyPredicateNames() == null 
          || name == null) {
        throw new SerializerException("Entity id is null.", SerializerException.MessageKeys.MISSING_ID);
      }else{
        final UriHelper uriHelper = new UriHelperImpl(); 
        entity.setId(URI.create(name + '(' + uriHelper.buildKeyPredicate(entityType, entity) + ')'));
      }
    }
    return entity.getId().toASCIIString();
  }

  private boolean areKeyPredicateNamesSelected(SelectOption select, EdmEntityType type) {
    if (select == null || ExpandSelectHelper.isAll(select)) {
      return true;
    }
    final Set<String> selected = ExpandSelectHelper.getSelectedPropertyNames(select.getSelectItems());
    for (String key : type.getKeyPredicateNames()) {
      if (!selected.contains(key)) {
        return false;
      }
    }
    return true;
  }

  protected void writeEntity(final ServiceMetadata metadata, final EdmEntityType entityType, final Entity entity,
      final ContextURL contextURL, final ExpandOption expand, Integer toDepth, 
      final SelectOption select, final boolean onlyReference, Set<String> ancestors, 
      String name, final JsonGenerator json)
      throws IOException, SerializerException, DecoderException {
    boolean cycle = false;
    if (expand != null) {
      if (ancestors == null) {
        ancestors = new HashSet<String>();
      }
      cycle = !ancestors.add(getEntityId(entity, entityType, name));
    }
    try {
      json.writeStartObject();
      if (!isODataMetadataNone) {
        // top-level entity
        if (contextURL != null) {
          writeContextURL(contextURL, json);
          writeMetadataETag(metadata, json);
        }
        if (entity.getETag() != null) {
          json.writeStringField(constants.getEtag(), entity.getETag());
        }
        if (entityType.hasStream()) {
          if (entity.getMediaETag() != null) {
            json.writeStringField(constants.getMediaEtag(), entity.getMediaETag());
          }
          if (entity.getMediaContentType() != null) {
            json.writeStringField(constants.getMediaContentType(), entity.getMediaContentType());
          }
          if (entity.getMediaContentSource() != null) {
            json.writeStringField(constants.getMediaReadLink(), entity.getMediaContentSource().toString());
          }
          if (entity.getMediaEditLinks() != null && !entity.getMediaEditLinks().isEmpty()) {
            json.writeStringField(constants.getMediaEditLink(), entity.getMediaEditLinks().get(0).getHref());
          }
        }
      }
      if (cycle || onlyReference) {
        json.writeStringField(constants.getId(), getEntityId(entity, entityType, name));
      } else {
        final EdmEntityType resolvedType = resolveEntityType(metadata, entityType, entity.getType());
        if ((!isODataMetadataNone && !resolvedType.equals(entityType)) || isODataMetadataFull) {
          json.writeStringField(constants.getType(), "#" + entity.getType());
        }
        if ((!isODataMetadataNone && !areKeyPredicateNamesSelected(select, resolvedType)) || isODataMetadataFull) {
          json.writeStringField(constants.getId(), getEntityId(entity, resolvedType, name));
        }
        
        if (isODataMetadataFull) {
          if (entity.getSelfLink() != null) {
            json.writeStringField(constants.getReadLink(), entity.getSelfLink().getHref());
          }
          if (entity.getEditLink() != null) {
            json.writeStringField(constants.getEditLink(), entity.getEditLink().getHref());
          }
        }
        
        writeProperties(metadata, resolvedType, entity.getProperties(), select, json, entity, expand);
        writeNavigationProperties(metadata, resolvedType, entity, expand, toDepth, ancestors, name, json);
        writeOperations(entity.getOperations(), json);      
      }
      json.writeEndObject();
    } finally {
      if (expand != null && !cycle && ancestors != null) {
        ancestors.remove(getEntityId(entity, entityType, name));
      }
    }
  }

  private void writeOperations(final List<Operation> operations, final JsonGenerator json)
      throws IOException {
    if (isODataMetadataFull) {
      for (Operation operation : operations) {
        json.writeObjectFieldStart(operation.getMetadataAnchor());
        json.writeStringField(Constants.ATTR_TITLE, operation.getTitle());
        json.writeStringField(Constants.ATTR_TARGET, operation.getTarget().toASCIIString());
        json.writeEndObject();
      }
    }
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
      if (type.getFullQualifiedName().equals(baseType.getFullQualifiedName())) {
        return derivedType;
      }
      type = type.getBaseType();
    }
    throw new SerializerException("Wrong base type",
        SerializerException.MessageKeys.WRONG_BASE_TYPE, derivedTypeName,
            baseType.getFullQualifiedName().getFullQualifiedNameAsString());
  }

  protected EdmComplexType resolveComplexType(final ServiceMetadata metadata, final EdmComplexType baseType,
      final String derivedTypeName) throws SerializerException {
      
    String fullQualifiedName = baseType.getFullQualifiedName().getFullQualifiedNameAsString();
    if (derivedTypeName == null ||
      fullQualifiedName.equals(derivedTypeName)) {
      return baseType;
    }
    EdmComplexType derivedType = metadata.getEdm().getComplexType(new FullQualifiedName(derivedTypeName));
    if (derivedType == null) {
      throw new SerializerException("Complex Type not found",
          SerializerException.MessageKeys.UNKNOWN_TYPE, derivedTypeName);
    }
    EdmComplexType type = derivedType.getBaseType();
    while (type != null) {
      if (type.getFullQualifiedName().equals(baseType.getFullQualifiedName())) {
        return derivedType;
      }
      type = type.getBaseType();
    }
    throw new SerializerException("Wrong base type",
        SerializerException.MessageKeys.WRONG_BASE_TYPE, derivedTypeName,
            baseType.getFullQualifiedName().getFullQualifiedNameAsString());
  }

  protected void writeProperties(final ServiceMetadata metadata, final EdmStructuredType type,
      final List<Property> properties,
      final SelectOption select, final JsonGenerator json, Linked linked, ExpandOption expand)
      throws IOException, SerializerException {
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
        writeProperty(metadata, edmProperty, property, selectedPaths, json, expandedPaths, linked, expand);
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
      final Set<String> ancestors, final String name, final JsonGenerator json) 
          throws SerializerException, IOException, DecoderException {
    if (isODataMetadataFull) {
      for (final String propertyName : type.getNavigationPropertyNames()) {
        final Link navigationLink = linked.getNavigationLink(propertyName);
        if (navigationLink != null) {
          json.writeStringField(propertyName + constants.getNavigationLink(), navigationLink.getHref());  
        }
        final Link associationLink = linked.getAssociationLink(propertyName);
        if (associationLink != null) {
          json.writeStringField(propertyName + constants.getAssociationLink(), associationLink.getHref());  
        }
      }
    }
    if ((toDepth != null && toDepth > 1) || (toDepth == null && ExpandSelectHelper.hasExpand(expand))) {
      final ExpandItem expandAll = ExpandSelectHelper.getExpandAll(expand);
      for (final String propertyName : type.getNavigationPropertyNames()) {
        final ExpandItem innerOptions = ExpandSelectHelper.getExpandItemBasedOnType(expand.getExpandItems(), 
            propertyName, type, name);
        if (innerOptions != null || expandAll != null || toDepth != null) {
          Integer levels = null;
          final EdmNavigationProperty property = type.getNavigationProperty(propertyName);
          final Link navigationLink = linked.getNavigationLink(property.getName());
          ExpandOption childExpand = null;
          LevelsExpandOption levelsOption = null;
          if (innerOptions != null) {
            levelsOption = innerOptions.getLevelsOption();
            childExpand = levelsOption == null ? innerOptions.getExpandOption() : new ExpandOptionImpl().addExpandItem(
                innerOptions);
          } else if (expandAll != null) {
            levels = 1;
            levelsOption = expandAll.getLevelsOption();
            childExpand = new ExpandOptionImpl().addExpandItem(expandAll);
          }

          if (levelsOption != null) { 
            levels = levelsOption.isMax() ? Integer.MAX_VALUE : levelsOption.getValue();
          }
          if (toDepth != null) {
            levels = toDepth - 1;
            childExpand = expand;
          }
                             
          writeExpandedNavigationProperty(metadata, property, navigationLink,
            childExpand, levels,
            innerOptions == null ? null : innerOptions.getSelectOption(),
            innerOptions == null ? null : innerOptions.getCountOption(),
            innerOptions == null ? false : innerOptions.hasCountPath(),
            innerOptions == null ? false : innerOptions.isRef(),
            ancestors, name,
            json);
        }
      }
    }
  }

  private void writeExpandedStreamProperty(ExpandOption expand, String propertyName, EdmProperty edmProperty, 
      Linked linked, ExpandItem expandAll, JsonGenerator json) throws SerializerException, 
      DecoderException, IOException {
    final ExpandItem innerOptions = ExpandSelectHelper.getExpandItem(expand.getExpandItems(), propertyName);
    if (innerOptions != null || expandAll != null) {
      if(constants instanceof Constantsv00){
        throw new SerializerException("Expand not supported for Stream Property Type!",
            SerializerException.MessageKeys.UNSUPPORTED_OPERATION_TYPE, "expand", edmProperty.getName());
      }
      Property property = null;
      if (linked instanceof Entity) {
        Entity entity = (Entity) linked;
        property = (Property) entity.getProperty(propertyName);
      } else if (linked instanceof ComplexValue) {
        List<Property> properties = ((ComplexValue) linked).getValue();
        for (Property prop : properties) {
          if (prop.getName().equals(propertyName)) {
            property = prop;
            break;
          }
        }
      }
       
      if((property == null || property.isNull()) && edmProperty.isNullable() == Boolean.FALSE ){
        throw new SerializerException("Non-nullable property not present!",
            SerializerException.MessageKeys.MISSING_PROPERTY, edmProperty.getName());
      }
      Link link = (Link) property.getValue();
      Property stream = link.getInlineEntity().getProperty(propertyName);
      Base64 decoder = new Base64(true);
      byte[] decodedBytes = (byte[]) decoder.decode(stream.getValue());
      json.writeStringField(propertyName, new String(decodedBytes));     
    }
  }

  protected void writeExpandedNavigationProperty(
      final ServiceMetadata metadata, final EdmNavigationProperty property,
      final Link navigationLink, final ExpandOption innerExpand,
      Integer toDepth, final SelectOption innerSelect, final CountOption innerCount,
      final boolean writeOnlyCount, final boolean writeOnlyRef, final Set<String> ancestors,
      String name, final JsonGenerator json) throws IOException, SerializerException, DecoderException {

    if (property.isCollection()) {
      if (writeOnlyCount) {
        if (navigationLink == null || navigationLink.getInlineEntitySet() == null) {
          writeInlineCount(property.getName(), 0, json);
        } else {
          writeInlineCount(property.getName(), navigationLink.getInlineEntitySet().getCount(), json);
        }
      } else {
        if (navigationLink == null || navigationLink.getInlineEntitySet() == null) {
          if (innerCount != null && innerCount.getValue()) {
            writeInlineCount(property.getName(), 0, json);
          }
          json.writeFieldName(property.getName());
          json.writeStartArray();
          json.writeEndArray();
        } else {
          if (innerCount != null && innerCount.getValue()) {
            writeInlineCount(property.getName(), navigationLink.getInlineEntitySet().getCount(), json);
          }
          json.writeFieldName(property.getName());
          writeEntitySet(metadata, property.getType(), navigationLink.getInlineEntitySet(), innerExpand, toDepth,
              innerSelect, writeOnlyRef, ancestors, name, json);
        }
      }
    } else {
      json.writeFieldName(property.getName());
      if (navigationLink == null || navigationLink.getInlineEntity() == null) {
        json.writeNull();
      } else {
        writeEntity(metadata, property.getType(), navigationLink.getInlineEntity(), null,
            innerExpand, toDepth, innerSelect, writeOnlyRef, ancestors, name, json);
      }
    }
  }
  
  private boolean isStreamProperty(EdmProperty edmProperty) {
    final EdmType type = edmProperty.getType();
    return (edmProperty.isPrimitive() && type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Stream));    
  }

  protected void writeProperty(final ServiceMetadata metadata,
      final EdmProperty edmProperty, final Property property,
      final Set<List<String>> selectedPaths, final JsonGenerator json, 
      Set<List<String>> expandedPaths, Linked linked, ExpandOption expand)
      throws IOException, SerializerException {
    boolean isStreamProperty = isStreamProperty(edmProperty);
    writePropertyType(edmProperty, json);
    if (!isStreamProperty) {
      json.writeFieldName(edmProperty.getName());
    }
    if (property == null || property.isNull()) {
      if (edmProperty.isNullable() == Boolean.FALSE && !isStreamProperty) {
        throw new SerializerException("Non-nullable property not present!",
            SerializerException.MessageKeys.MISSING_PROPERTY, edmProperty.getName());
      } else {
        if (!isStreamProperty) {
          if (edmProperty.isCollection()) {
            json.writeStartArray();
            json.writeEndArray();
          } else {
            json.writeNull();
          }
        }
      }
    } else {
      writePropertyValue(metadata, edmProperty, property, selectedPaths, json, 
          expandedPaths, linked, expand);
    }
  }
  
  private void writePropertyType(final EdmProperty edmProperty, JsonGenerator json)
      throws SerializerException, IOException {
    if (!isODataMetadataFull) {
      return;
    }
    String typeName = edmProperty.getName() + constants.getType();
    final EdmType type = edmProperty.getType();
    if (type.getKind() == EdmTypeKind.ENUM || type.getKind() == EdmTypeKind.DEFINITION) {
      if (edmProperty.isCollection()) {
        json.writeStringField(typeName, 
            "#Collection(" + type.getFullQualifiedName().getFullQualifiedNameAsString() + ")");
      } else {
        json.writeStringField(typeName, "#" + type.getFullQualifiedName().getFullQualifiedNameAsString());
      }
    } else if (edmProperty.isPrimitive()) {
      if (edmProperty.isCollection()) {
        json.writeStringField(typeName, "#Collection(" + type.getFullQualifiedName().getName() + ")");
      } else {
        // exclude the properties that can be heuristically determined
        if (type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean) &&
            type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Double) &&
            type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String)) {
          json.writeStringField(typeName, "#" + type.getFullQualifiedName().getName());                  
        }
      }
    } else if (type.getKind() == EdmTypeKind.COMPLEX) {
      // non-collection case written in writeComplex method directly.
      if (edmProperty.isCollection()) {
        json.writeStringField(typeName, 
            "#Collection(" + type.getFullQualifiedName().getFullQualifiedNameAsString() + ")");
      }
    } else {
      throw new SerializerException("Property type not yet supported!",
          SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
    }    
  }

  private void writePropertyValue(final ServiceMetadata metadata, final EdmProperty edmProperty,
      final Property property, final Set<List<String>> selectedPaths, final JsonGenerator json, 
      Set<List<String>> expandedPaths, Linked linked, ExpandOption expand)
      throws IOException, SerializerException {
    final EdmType type = edmProperty.getType();
    try {
      if (edmProperty.isPrimitive()
          || type.getKind() == EdmTypeKind.ENUM || type.getKind() == EdmTypeKind.DEFINITION) {
        if (edmProperty.isCollection()) {
          writePrimitiveCollection((EdmPrimitiveType) type, property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(), json);
        } else {
          writePrimitive((EdmPrimitiveType) type, property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(), json);
          // If there is expand on a stream property
          if (isStreamProperty(edmProperty) && null != expand) {
            final ExpandItem expandAll = ExpandSelectHelper.getExpandAll(expand);
            try {
              writeExpandedStreamProperty(expand, property.getName(), edmProperty, linked, expandAll, json);
            } catch (DecoderException e) {
              throw new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
            }
          }
        }
      } else if (property.isComplex()) {
        if (edmProperty.isCollection()) {
          writeComplexCollection(metadata, (EdmComplexType) type, property, selectedPaths, 
              json, expandedPaths, linked, expand);
        } else {
         writeComplex(metadata, (EdmComplexType) type, property, selectedPaths, json, 
             expandedPaths, linked, expand);
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

  private void writeComplex(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final Set<List<String>> selectedPaths, final JsonGenerator json, 
      Set<List<String>> expandedPaths, Linked linked, ExpandOption expand) 
          throws IOException, SerializerException{
        json.writeStartObject();        
        String derivedName = property.getType();
        EdmComplexType resolvedType = null;
        if (!type.getFullQualifiedName().getFullQualifiedNameAsString().
            equals(derivedName)) {
          if (type.getBaseType() != null && 
              type.getBaseType().getFullQualifiedName().getFullQualifiedNameAsString().
              equals(derivedName)) {
            resolvedType = resolveComplexType(metadata, type.getBaseType(), 
                type.getFullQualifiedName().getFullQualifiedNameAsString());
          } else {
            resolvedType = resolveComplexType(metadata, type, derivedName);
          }
        } else {
          resolvedType = resolveComplexType(metadata, type, derivedName);
        }
        if (!isODataMetadataNone && !resolvedType.equals(type) || isODataMetadataFull) {
           json.writeStringField(constants.getType(), "#" + 
        resolvedType.getFullQualifiedName().getFullQualifiedNameAsString());
        }
        
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
        
        writeComplexValue(metadata, resolvedType, property.asComplex().getValue(), selectedPaths,
             json, expandedPaths, linked, expand, property.getName());
        json.writeEndObject();
  }

  private void writePrimitiveCollection(final EdmPrimitiveType type, final Property property,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartArray();
    for (Object value : property.asCollection()) {
      switch (property.getValueType()) {
      case COLLECTION_PRIMITIVE:
      case COLLECTION_ENUM:
      case COLLECTION_GEOSPATIAL:
        try {
          writePrimitiveValue(property.getName(), type, value, isNullable,
              maxLength, precision, scale, isUnicode, json);
        } catch (EdmPrimitiveTypeException e) {
          throw new SerializerException("Wrong value for property!", e,
              SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
              property.getName(), property.getValue().toString());
        }
        break;
      default:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      }
    }
    json.writeEndArray();
  }

  private void writeComplexCollection(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property,
      final Set<List<String>> selectedPaths, final JsonGenerator json, 
      Set<List<String>> expandedPaths, Linked linked, ExpandOption expand)
      throws IOException, SerializerException {
    json.writeStartArray();
    EdmComplexType derivedType = type;
    Set<List<String>> expandedPaths1 = expandedPaths != null && !expandedPaths.isEmpty() ? 
        expandedPaths : ExpandSelectHelper.getExpandedItemsPath(expand);
    for (Object value : property.asCollection()) {
      expandedPaths = expandedPaths1;
      derivedType = ((ComplexValue) value).getTypeName()!=null ? metadata.getEdm().getComplexType
          (new FullQualifiedName(((ComplexValue) value).getTypeName())): type;          
      switch (property.getValueType()) {
      case COLLECTION_COMPLEX:
        json.writeStartObject();
        if (isODataMetadataFull || (!isODataMetadataNone && !derivedType.equals(type))) {
             json.writeStringField(constants.getType(), "#" + 
                 derivedType.getFullQualifiedName().getFullQualifiedNameAsString());
        }
        expandedPaths = expandedPaths == null || expandedPaths.isEmpty() ? null :
          ExpandSelectHelper.getReducedExpandItemsPaths(expandedPaths, property.getName());
        writeComplexValue(metadata, derivedType, ((ComplexValue) value).getValue(), 
            selectedPaths, json, expandedPaths, (ComplexValue) value, expand, property.getName());
        json.writeEndObject();
        break;
      default:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      }
    }
    json.writeEndArray();
  }

  private void writePrimitive(final EdmPrimitiveType type, final Property property,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final JsonGenerator json)
      throws EdmPrimitiveTypeException, IOException, SerializerException {
    if (property.isPrimitive()) {
      writePrimitiveValue(property.getName(), type, property.asPrimitive(),
          isNullable, maxLength, precision, scale, isUnicode, json);
    } else if (property.isGeospatial()) {
      writeGeoValue(property.getName(), type, property.asGeospatial(), isNullable, json, null);
    } else if (property.isEnum()) {
      writePrimitiveValue(property.getName(), type, property.asEnum(),
          isNullable, maxLength, precision, scale, isUnicode, json);
    } else {
      throw new SerializerException("Inconsistent property type!",
          SerializerException.MessageKeys.INCONSISTENT_PROPERTY_TYPE, property.getName());
    }
  }

  protected void writePrimitiveValue(final String name, final EdmPrimitiveType type, final Object primitiveValue,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final JsonGenerator json) throws EdmPrimitiveTypeException, IOException {
    final String value = type.valueToString(primitiveValue,
        isNullable, maxLength, precision, scale, isUnicode);
    if (value == null) {
      json.writeNull();
    } else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean)) {
      json.writeBoolean(Boolean.parseBoolean(value));
    } else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Byte)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Double)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Single)
        || (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64))
        && !isIEEE754Compatible) {
      json.writeNumber(value);
    } else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Stream)) {
      if (primitiveValue instanceof Link) {
        Link stream = (Link)primitiveValue;
        if (!isODataMetadataNone) {
          if (stream.getMediaETag() != null) {
            json.writeStringField(name+constants.getMediaEtag(), stream.getMediaETag());
          }
          if (stream.getType() != null) {
            json.writeStringField(name+constants.getMediaContentType(), stream.getType());
          }
        }
        if (isODataMetadataFull) {
          if (stream.getRel() != null && stream.getRel().equals(Constants.NS_MEDIA_READ_LINK_REL)) {
            json.writeStringField(name+constants.getMediaReadLink(), stream.getHref());
          }
          if (stream.getRel() == null || stream.getRel().equals(Constants.NS_MEDIA_EDIT_LINK_REL)) {
            json.writeStringField(name+constants.getMediaEditLink(), stream.getHref());
          }
        }
      }
    } else {
      json.writeString(value);
    }
  }

  /** Writes a geospatial value following the GeoJSON specification defined in RFC 7946. */
  protected void writeGeoValue(final String name, final EdmPrimitiveType type, final Geospatial geoValue,
      final Boolean isNullable, JsonGenerator json, SRID parentSrid)
      throws EdmPrimitiveTypeException, IOException, SerializerException {
    if (geoValue == null) {
      if (isNullable == null || isNullable) {
        json.writeNull();
      } else {
        throw new EdmPrimitiveTypeException("The literal 'null' is not allowed.");
      }
    } else {
      if (!type.getDefaultType().isAssignableFrom(geoValue.getClass())) {
        throw new EdmPrimitiveTypeException("The value type " + geoValue.getClass() + " is not supported.");
      }
      json.writeStartObject();
      json.writeStringField(Constants.ATTR_TYPE, geoValueTypeToJsonName.get(geoValue.getGeoType()));
      json.writeFieldName(geoValue.getGeoType() == Geospatial.Type.GEOSPATIALCOLLECTION ?
          Constants.JSON_GEOMETRIES :
          Constants.JSON_COORDINATES);
      json.writeStartArray();
      switch (geoValue.getGeoType()) {
      case POINT:
        writeGeoPoint(json, (Point) geoValue);
        break;
      case MULTIPOINT:
        writeGeoPoints(json, (MultiPoint) geoValue);
        break;
      case LINESTRING:
        writeGeoPoints(json, (LineString) geoValue);
        break;
      case MULTILINESTRING:
        for (final LineString lineString : (MultiLineString) geoValue) {
          json.writeStartArray();
          writeGeoPoints(json, lineString);
          json.writeEndArray();
        }
        break;
      case POLYGON:
        writeGeoPolygon(json, (Polygon) geoValue);
        break;
      case MULTIPOLYGON:
        for (final Polygon polygon : (MultiPolygon) geoValue) {
          json.writeStartArray();
          writeGeoPolygon(json, polygon);
          json.writeEndArray();
        }
        break;
      case GEOSPATIALCOLLECTION:
        for (final Geospatial element : (GeospatialCollection) geoValue) {
          writeGeoValue(name, EdmPrimitiveTypeFactory.getInstance(element.getEdmPrimitiveTypeKind()),
              element, isNullable, json, geoValue.getSrid());
        }
        break;
      }
      json.writeEndArray();
      
      if (geoValue.getSrid() != null && geoValue.getSrid().isNotDefault() 
    		  && (parentSrid == null || !parentSrid.equals(geoValue.getSrid()))) {
    	  srid(json, geoValue.getSrid());
      }
      json.writeEndObject();
    }
  }
  
  private void srid(final JsonGenerator jgen, final SRID srid) throws IOException {
    jgen.writeObjectFieldStart(Constants.JSON_CRS);
	jgen.writeStringField(Constants.ATTR_TYPE, Constants.JSON_NAME);
	jgen.writeObjectFieldStart(Constants.PROPERTIES);
	jgen.writeStringField(Constants.JSON_NAME, "EPSG:" + srid.toString());
	jgen.writeEndObject();
	jgen.writeEndObject();
  }

  private void writeGeoPoint(JsonGenerator json, final Point point) throws IOException {
    json.writeNumber(point.getX());
    json.writeNumber(point.getY());
    if (point.getZ() != 0) {
      json.writeNumber(point.getZ());
    }
  }

  private void writeGeoPoints(JsonGenerator json, final ComposedGeospatial<Point> points) throws IOException {
    for (final Point point : points) {
      json.writeStartArray();
      writeGeoPoint(json, point);
      json.writeEndArray();
    }
  }

  // TODO: There could be a more strict verification that the lines describe boundaries
  //       and have the correct winding order.
  //       But arguably the better place for this is the constructor of the Polygon object.
  private void writeGeoPolygon(JsonGenerator json, final Polygon polygon) throws IOException {
    json.writeStartArray();
    writeGeoPoints(json, polygon.getExterior());
    json.writeEndArray();
    for (int i = 0; i < polygon.getNumberOfInteriorRings(); i++) {
      json.writeStartArray();
      writeGeoPoints(json, polygon.getInterior(i));
      json.writeEndArray();
    }
  }

  protected void writeComplexValue(final ServiceMetadata metadata,
      final EdmComplexType type, final List<Property> properties,
      final Set<List<String>> selectedPaths, final JsonGenerator json, 
      Set<List<String>> expandedPaths, Linked linked, ExpandOption expand, String complexPropName)
      throws IOException, SerializerException {

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
            json, expandedPaths, linked, expand);
      }
    }
    try {
      writeNavigationProperties(metadata, type, linked, expand, null, null, complexPropName, json);
    } catch (DecoderException e) {
      throw new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
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
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);
      writeOperations(property.getOperations(), json);
      if (property.isNull() && options!=null && options.isNullable() != null && !options.isNullable()) {
        throw new SerializerException("Property value can not be null.", SerializerException.MessageKeys.NULL_INPUT);
      } else {
        json.writeFieldName(Constants.VALUE);
        writePrimitive(type, property,
            options == null ? null : options.isNullable(),
            options == null ? null : options.getMaxLength(),
            options == null ? null : options.getPrecision(),
            options == null ? null : options.getScale(),
            options == null ? null : options.isUnicode(), json);
      }
      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (final EdmPrimitiveTypeException e) {
      cachedException = new SerializerException("Wrong value for property!", e,
          SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
          property.getName(), property.getValue().toString());
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult complex(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final ComplexSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      final String name =  contextURL == null ? null:
        contextURL.getEntitySetOrSingletonOrType();
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);      
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
      if (!isODataMetadataNone && !resolvedType.equals(type) || isODataMetadataFull) {
        json.writeStringField(constants.getType(), "#" + 
      resolvedType.getFullQualifiedName().getFullQualifiedNameAsString());
      }
      writeOperations(property.getOperations(), json);      
      final List<Property> values =
          property.isNull() ? Collections.<Property> emptyList() : property.asComplex().getValue();
      writeProperties(metadata, type, values, options == null ? null : options == null ? null : options.getSelect(), 
          json, 
          property.asComplex(), options == null ? null : options.getExpand());
      if (!property.isNull() && property.isComplex()) {
        writeNavigationProperties(metadata, type, property.asComplex(),
            options == null ? null : options.getExpand(), null, null, name, json);
      }
      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (DecoderException e) {
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
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);
      if (isODataMetadataFull) {
        json.writeStringField(constants.getType(),  "#Collection("+type.getFullQualifiedName().getName()+")");
      }
      writeOperations(property.getOperations(), json);
      json.writeFieldName(Constants.VALUE);
      writePrimitiveCollection(type, property,
          options == null ? null : options.isNullable(),
          options == null ? null : options.getMaxLength(),
          options == null ? null : options.getPrecision(),
          options == null ? null : options.getScale(),
          options == null ? null : options.isUnicode(), json);
      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
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
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);
      if (isODataMetadataFull) {
        json.writeStringField(constants.getType(), 
            "#Collection(" + type.getFullQualifiedName().getFullQualifiedNameAsString() + ")");                
      }
      writeOperations(property.getOperations(), json);
      json.writeFieldName(Constants.VALUE);
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
      writeComplexCollection(metadata, type, property, selectedPaths, json, expandPaths, null, 
          options == null ? null : options.getExpand());
      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
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
    OutputStream outputStream = null;
    SerializerException cachedException = null;

    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      final UriHelper uriHelper = new UriHelperImpl();
      outputStream = buffer.getOutputStream();
      final JsonGenerator json = new JsonFactory().createGenerator(outputStream);

      json.writeStartObject();
      writeContextURL(contextURL, json);
      json.writeStringField(constants.getId(), uriHelper.buildCanonicalURL(edmEntitySet, entity));
      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult referenceCollection(final ServiceMetadata metadata, final EdmEntitySet edmEntitySet,
      final AbstractEntityCollection entityCollection, final ReferenceCollectionSerializerOptions options)
      throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    boolean pagination = false ;

    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      final UriHelper uriHelper = new UriHelperImpl();
      outputStream = buffer.getOutputStream();
      final JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();

      writeContextURL(contextURL, json);
      if (options != null && options.getCount() != null && options.getCount().getValue()) {
        writeInlineCount("", entityCollection.getCount(), json);
      }

      json.writeArrayFieldStart(Constants.VALUE);
      for (final Entity entity : entityCollection) {
        json.writeStartObject();
        json.writeStringField(constants.getId(), uriHelper.buildCanonicalURL(edmEntitySet, entity));
        json.writeEndObject();
      }
      json.writeEndArray();

      writeNextLink(entityCollection, json, pagination);

      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }

  }

  void writeContextURL(final ContextURL contextURL, final JsonGenerator json) throws IOException {
    if (!isODataMetadataNone && contextURL != null) {
      json.writeStringField(constants.getContext(), ContextURLBuilder.create(contextURL).toASCIIString());
    }
  }

  void writeMetadataETag(final ServiceMetadata metadata, final JsonGenerator json) throws IOException {
    if (!isODataMetadataNone
        && metadata != null
        && metadata.getServiceMetadataETagSupport() != null
        && metadata.getServiceMetadataETagSupport().getMetadataETag() != null) {
      json.writeStringField(constants.getMetadataEtag(),
          metadata.getServiceMetadataETagSupport().getMetadataETag());
    }
  }

  void writeInlineCount(final String propertyName, final Integer count, final JsonGenerator json)
      throws IOException {
    if (count != null) {
      if (isIEEE754Compatible) {
        json.writeStringField(propertyName + constants.getCount(), String.valueOf(count));
      } else {
        json.writeNumberField(propertyName + constants.getCount(), count);
      }
    }
  }

  void writeNextLink(final AbstractEntityCollection entitySet, final JsonGenerator json, boolean pagination)
      throws IOException {
    if (entitySet.getNext() != null) {
      pagination = true;
      json.writeStringField(constants.getNextLink(), entitySet.getNext().toASCIIString());
    }else{
      pagination = false;
    }
  }
  
  void writeDeltaLink(final AbstractEntityCollection entitySet, final JsonGenerator json, boolean pagination)
      throws IOException {
    if (entitySet.getDeltaLink() != null && !pagination) {
      json.writeStringField(constants.getDeltaLink(), entitySet.getDeltaLink().toASCIIString());
 }
  }
}
