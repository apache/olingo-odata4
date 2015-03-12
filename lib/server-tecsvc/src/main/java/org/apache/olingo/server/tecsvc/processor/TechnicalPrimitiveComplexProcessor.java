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
package org.apache.olingo.server.tecsvc.processor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Builder;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.processor.ActionComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionComplexProcessor;
import org.apache.olingo.server.api.processor.ActionPrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionPrimitiveProcessor;
import org.apache.olingo.server.api.processor.ComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.ComplexProcessor;
import org.apache.olingo.server.api.processor.PrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.PrimitiveProcessor;
import org.apache.olingo.server.api.processor.PrimitiveValueProcessor;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.FixedFormatSerializer;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.PrimitiveValueSerializerOptions;
import org.apache.olingo.server.api.serializer.RepresentationType;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.tecsvc.data.DataProvider;

/**
 * Technical Processor which provides functionality related to primitive and complex types and collections thereof.
 */
public class TechnicalPrimitiveComplexProcessor extends TechnicalProcessor
    implements PrimitiveProcessor, PrimitiveValueProcessor, ActionPrimitiveProcessor,
    PrimitiveCollectionProcessor, ActionPrimitiveCollectionProcessor,
    ComplexProcessor, ActionComplexProcessor,
    ComplexCollectionProcessor, ActionComplexCollectionProcessor {

  public TechnicalPrimitiveComplexProcessor(final DataProvider dataProvider) {
    super(dataProvider);
  }

  @Override
  public void readPrimitive(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType contentType) throws ODataApplicationException, SerializerException {
    readProperty(response, uriInfo, contentType, RepresentationType.PRIMITIVE);
  }

  @Override
  public void updatePrimitive(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    throw new ODataApplicationException("Not supported yet.",
        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
  }

  @Override
  public void deletePrimitive(final ODataRequest request, ODataResponse response, final UriInfo uriInfo)
      throws ODataApplicationException {
    deleteProperty(response, uriInfo);
  }

  @Override
  public void processActionPrimitive(final ODataRequest request, final ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    throw new ODataApplicationException("Not supported yet.",
        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
  }

  @Override
  public void readPrimitiveCollection(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType contentType) throws ODataApplicationException, SerializerException {
    readProperty(response, uriInfo, contentType, RepresentationType.COLLECTION_PRIMITIVE);
  }

  @Override
  public void updatePrimitiveCollection(final ODataRequest request, final ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    throw new ODataApplicationException("Not supported yet.",
        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
  }

  @Override
  public void deletePrimitiveCollection(final ODataRequest request, ODataResponse response, final UriInfo uriInfo)
      throws ODataApplicationException {
    deleteProperty(response, uriInfo);
  }

  @Override
  public void processActionPrimitiveCollection(final ODataRequest request, final ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    throw new ODataApplicationException("Not supported yet.",
        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
  }

  @Override
  public void readComplex(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType contentType) throws ODataApplicationException, SerializerException {
    readProperty(response, uriInfo, contentType, RepresentationType.COMPLEX);
  }

  @Override
  public void updateComplex(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    throw new ODataApplicationException("Not supported yet.",
        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
  }

  @Override
  public void processActionComplex(ODataRequest request, ODataResponse response, UriInfo uriInfo,
      ContentType requestFormat, ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    throw new ODataApplicationException("Not supported yet.",
        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
  }

  @Override
  public void deleteComplex(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo)
      throws ODataApplicationException {
    deleteProperty(response, uriInfo);
  }

  @Override
  public void readComplexCollection(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType contentType) throws ODataApplicationException, SerializerException {
    readProperty(response, uriInfo, contentType, RepresentationType.COLLECTION_COMPLEX);
  }

  @Override
  public void updateComplexCollection(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    throw new ODataApplicationException("Not supported yet.",
        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
  }

  @Override
  public void processActionComplexCollection(ODataRequest request, ODataResponse response,
      UriInfo uriInfo, ContentType requestFormat, ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    throw new ODataApplicationException("Not supported yet.",
        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
  }

  @Override
  public void deleteComplexCollection(final ODataRequest request, ODataResponse response, final UriInfo uriInfo)
      throws ODataApplicationException {
    deleteProperty(response, uriInfo);
  }

  private void readProperty(final ODataResponse response, final UriInfo uriInfo, final ContentType contentType,
      final RepresentationType representationType) throws ODataApplicationException, SerializerException {
    final UriInfoResource resource = uriInfo.asUriInfoResource();
    validateOptions(resource);
    validatePath(resource);
    final EdmEntitySet edmEntitySet = getEdmEntitySet(resource);

    final List<UriResource> resourceParts = resource.getUriResourceParts();
    final List<String> path = getPropertyPath(resourceParts, 0);

    final Entity entity = readEntity(uriInfo);
    final Property property = entity == null ?
        getPropertyData(dataProvider.readFunctionPrimitiveComplex(((UriResourceFunction) resourceParts.get(0))
            .getFunction(),
            ((UriResourceFunction) resourceParts.get(0)).getParameters()), path) :
        getPropertyData(entity, path);

    if (property == null) {
      throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
    } else {
      if (property.getValue() == null) {
        response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
      } else {
        final EdmProperty edmProperty = path.isEmpty() ? null :
            ((UriResourceProperty) resourceParts.get(resourceParts.size() - 1)).getProperty();
        final EdmType type = edmProperty == null ?
            ((UriResourceFunction) resourceParts.get(0)).getType() :
            edmProperty.getType();
        final EdmReturnType returnType = resourceParts.get(0) instanceof UriResourceFunction ?
            ((UriResourceFunction) resourceParts.get(0)).getFunction().getReturnType() : null;

        final ODataFormat format = ODataFormat.fromContentType(contentType);
        ODataSerializer serializer = odata.createSerializer(format);
        final ExpandOption expand = uriInfo.getExpandOption();
        final SelectOption select = uriInfo.getSelectOption();
        final ContextURL contextURL = format == ODataFormat.JSON_NO_METADATA ? null :
            getContextUrl(edmEntitySet, entity, path, type, representationType, expand, select);
        switch (representationType) {
        case PRIMITIVE:
          response.setContent(serializer.primitive((EdmPrimitiveType) type, property,
              PrimitiveSerializerOptions.with().contextURL(contextURL)
                  .nullable(edmProperty == null ? returnType.isNullable() : edmProperty.isNullable())
                  .maxLength(edmProperty == null ? returnType.getMaxLength() : edmProperty.getMaxLength())
                  .precision(edmProperty == null ? returnType.getPrecision() : edmProperty.getPrecision())
                  .scale(edmProperty == null ? returnType.getScale() : edmProperty.getScale())
                  .unicode(edmProperty == null ? null : edmProperty.isUnicode())
                  .build()));
          break;
        case COMPLEX:
          response.setContent(serializer.complex((EdmComplexType) type, property,
              ComplexSerializerOptions.with().contextURL(contextURL)
                  .expand(expand).select(select)
                  .build()));
          break;
        case COLLECTION_PRIMITIVE:
          response.setContent(serializer.primitiveCollection((EdmPrimitiveType) type, property,
              PrimitiveSerializerOptions.with().contextURL(contextURL)
                  .nullable(edmProperty == null ? returnType.isNullable() : edmProperty.isNullable())
                  .maxLength(edmProperty == null ? returnType.getMaxLength() : edmProperty.getMaxLength())
                  .precision(edmProperty == null ? returnType.getPrecision() : edmProperty.getPrecision())
                  .scale(edmProperty == null ? returnType.getScale() : edmProperty.getScale())
                  .unicode(edmProperty == null ? null : edmProperty.isUnicode())
                  .build()));
          break;
        case COLLECTION_COMPLEX:
          response.setContent(serializer.complexCollection((EdmComplexType) type, property,
              ComplexSerializerOptions.with().contextURL(contextURL)
                  .expand(expand).select(select)
                  .build()));
          break;
        default:
          break;
        }
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
      }
    }
  }

  private void deleteProperty(final ODataResponse response, final UriInfo uriInfo) throws ODataApplicationException {
    final UriInfoResource resource = uriInfo.asUriInfoResource();
    validatePath(resource);
    getEdmEntitySet(uriInfo); // including checks

    final List<UriResource> resourceParts = resource.getUriResourceParts();
    final List<String> path = getPropertyPath(resourceParts, 0);

    final Property property = getPropertyData(readEntity(uriInfo), path);

    final EdmProperty edmProperty = ((UriResourceProperty) resourceParts.get(resourceParts.size() - 1))
        .getProperty();

    if (edmProperty.isNullable()) {
      property.setValue(property.getValueType(), edmProperty.isCollection() ? Collections.emptyList() : null);
      response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    } else {
      throw new ODataApplicationException("Not nullable.", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
  }

  private Property getPropertyData(final Entity entity, final List<String> path) {
    return getPropertyData(entity.getProperty(path.get(0)), path.subList(1, path.size()));
  }

  private Property getPropertyData(final Property property, final List<String> path) {
    Property result = property;
    for (final String name : path) {
      if (result != null && property.isComplex()) {
        final List<Property> complex = result.asComplex().getValue();
        result = null;
        for (final Property innerProperty : complex) {
          if (innerProperty.getName().equals(name)) {
            result = innerProperty;
            break;
          }
        }
      }
    }
    return result;
  }

  private List<String> getPropertyPath(final List<UriResource> path, final int trailing) {
    List<String> result = new LinkedList<String>();
    int index = path.size() - trailing - 1;
    while (path.get(index) instanceof UriResourceProperty) {
      result.add(0, ((UriResourceProperty) path.get(index)).getProperty().getName());
      index--;
    }
    return result;
  }

  private String buildPropertyPath(final List<String> path) {
    StringBuilder result = new StringBuilder();
    for (final String segment : path) {
      result.append(result.length() == 0 ? "" : '/').append(segment);
    }
    return result.toString();
  }

  private ContextURL getContextUrl(final EdmEntitySet entitySet, final Entity entity, final List<String> path,
      final EdmType type, final RepresentationType representationType,
      final ExpandOption expand, final SelectOption select) throws SerializerException {
    final UriHelper helper = odata.createUriHelper();
    Builder builder = ContextURL.with();
    builder = entitySet == null ?
        representationType == RepresentationType.PRIMITIVE || representationType == RepresentationType.COMPLEX ?
            builder.type(type) :
            builder.type(type).asCollection() :
        builder.entitySet(entitySet).keyPath(helper.buildKeyPredicate(entitySet.getEntityType(), entity));
    if (entitySet != null && !path.isEmpty()) {
      builder = builder.navOrPropertyPath(buildPropertyPath(path));
    }
    builder = builder.selectList(type.getKind() == EdmTypeKind.PRIMITIVE ? null :
        helper.buildContextURLSelectList((EdmStructuredType) type, expand, select));
    return builder.build();
  }

  @Override
  public void readPrimitiveValue(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType contentType) throws ODataApplicationException, SerializerException {
    final UriInfoResource resource = uriInfo.asUriInfoResource();
    validateOptions(resource);
    validatePath(resource);
    getEdmEntitySet(uriInfo); // including checks

    final List<UriResource> resourceParts = resource.getUriResourceParts();
    final List<String> path = getPropertyPath(resourceParts, 1);

    final Entity entity = readEntity(uriInfo);
    final Property property = entity == null ?
        getPropertyData(dataProvider.readFunctionPrimitiveComplex(((UriResourceFunction) resourceParts.get(0))
            .getFunction(),
            ((UriResourceFunction) resourceParts.get(0)).getParameters()), path) :
        getPropertyData(entity, path);

    if (property == null || property.getValue() == null) {
      response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    } else {
      final EdmProperty edmProperty = path.isEmpty() ? null :
          ((UriResourceProperty) resourceParts.get(resourceParts.size() - 2)).getProperty();
      final EdmPrimitiveType type = (EdmPrimitiveType) (edmProperty == null ?
          ((UriResourceFunction) resourceParts.get(0)).getType() :
          edmProperty.getType());
      final EdmReturnType returnType = resourceParts.get(0) instanceof UriResourceFunction ?
          ((UriResourceFunction) resourceParts.get(0)).getFunction().getReturnType() : null;
      final FixedFormatSerializer serializer = odata.createFixedFormatSerializer();
      response.setContent(type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Binary) ?
          serializer.binary((byte[]) property.getValue()) :
          serializer.primitiveValue(type, property.getValue(),
              PrimitiveValueSerializerOptions.with()
                  .nullable(edmProperty == null ? returnType.isNullable() : edmProperty.isNullable())
                  .maxLength(edmProperty == null ? returnType.getMaxLength() : edmProperty.getMaxLength())
                  .precision(edmProperty == null ? returnType.getPrecision() : edmProperty.getPrecision())
                  .scale(edmProperty == null ? returnType.getScale() : edmProperty.getScale())
                  .unicode(edmProperty == null ? null : edmProperty.isUnicode())
                  .build()));
      response.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    }
  }

  private void validatePath(final UriInfoResource uriInfo) throws ODataApplicationException {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    for (final UriResource segment : resourcePaths.subList(1, resourcePaths.size())) {
      final UriResourceKind kind = segment.getKind();
      if (kind != UriResourceKind.navigationProperty
          && kind != UriResourceKind.primitiveProperty
          && kind != UriResourceKind.complexProperty
          && kind != UriResourceKind.count
          && kind != UriResourceKind.value) {
        throw new ODataApplicationException("Invalid resource type.",
            HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
      }
    }
  }
}
