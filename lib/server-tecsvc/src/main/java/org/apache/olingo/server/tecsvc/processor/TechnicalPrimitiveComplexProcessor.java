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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.ComplexTypeCollectionProcessor;
import org.apache.olingo.server.api.processor.ComplexTypeProcessor;
import org.apache.olingo.server.api.processor.PrimitiveTypeCollectionProcessor;
import org.apache.olingo.server.api.processor.PrimitiveTypeProcessor;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.ODataSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.tecsvc.data.DataProvider;

/**
 * Technical Processor which provides functionality related to primitive and complex types and collections thereof.
 */
public class TechnicalPrimitiveComplexProcessor extends TechnicalProcessor
    implements PrimitiveTypeProcessor, PrimitiveTypeCollectionProcessor,
    ComplexTypeProcessor, ComplexTypeCollectionProcessor {

  public TechnicalPrimitiveComplexProcessor(final DataProvider dataProvider) {
    super(dataProvider);
  }

  @Override
  public void readPrimitiveType(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType contentType) throws ODataApplicationException, SerializerException {
    readProperty(response, uriInfo, contentType);
  }

  @Override
  public void readPrimitiveTypeCollection(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType contentType) throws ODataApplicationException, SerializerException {
    readProperty(response, uriInfo, contentType);
  }

  @Override
  public void readComplexType(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType contentType) throws ODataApplicationException, SerializerException {
    readProperty(response, uriInfo, contentType);
  }

  @Override
  public void readComplexTypeCollection(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType contentType) throws ODataApplicationException, SerializerException {
    readProperty(response, uriInfo, contentType);
  }

  private void readProperty(ODataResponse response, final UriInfo uriInfo, final ContentType contentType)
      throws ODataApplicationException, SerializerException {
    final UriInfoResource resource = uriInfo.asUriInfoResource();
    validateOptions(resource);
    validatePath(resource);

    final List<UriResource> resourceParts = resource.getUriResourceParts();
    final UriResourceEntitySet resourceEntitySet = (UriResourceEntitySet) resourceParts.get(0);
    final List<String> path = getPropertyPath(resourceParts);

    final Property property = getPropertyData(resourceEntitySet, path);

    if (property == null) {
      throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
    } else {
      if (property.getValue() == null) {
        response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
      } else {
        final EdmEntitySet edmEntitySet = getEdmEntitySet(resource);
        final EdmProperty edmProperty = ((UriResourceProperty) resourceParts.get(path.size())).getProperty();

        final ODataFormat format = ODataFormat.fromContentType(contentType);
        ODataSerializer serializer = odata.createSerializer(format);
        response.setContent(serializer.entityProperty(edmProperty, property,
            ODataSerializerOptions.with().contextURL(format == ODataFormat.JSON_NO_METADATA ? null :
                ContextURL.with().entitySet(edmEntitySet)
                    .keyPath(serializer.buildContextURLKeyPredicate(
                        ((UriResourceEntitySet) resourceParts.get(0)).getKeyPredicates()))
                    .navOrPropertyPath(buildPropertyPath(path))
                    .build())
                .build()));
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
      }
    }
  }

  private Property getPropertyData(final UriResourceEntitySet resourceEntitySet, final List<String> path)
      throws ODataApplicationException {
    final Entity entity = dataProvider.read(resourceEntitySet.getEntitySet(), resourceEntitySet.getKeyPredicates());
    if (entity == null) {
      throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
    } else {
      Property property = entity.getProperty(path.get(0));
      for (final String name : path.subList(1, path.size())) {
        if (property != null && (property.isLinkedComplex() || property.isComplex())) {
          final List<Property> complex = property.isLinkedComplex() ?
              property.asLinkedComplex().getValue() : property.asComplex();
          property = null;
          for (final Property innerProperty : complex) {
            if (innerProperty.getName().equals(name)) {
              property = innerProperty;
              break;
            }
          }
        }
      }
      return property;
    }
  }

  private List<String> getPropertyPath(final List<UriResource> path) {
    List<String> result = new LinkedList<String>();
    int index = 1;
    while (index < path.size() && path.get(index) instanceof UriResourceProperty) {
      result.add(((UriResourceProperty) path.get(index)).getProperty().getName());
      index++;
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

  @Override
  public void readPrimitiveTypeAsValue(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType contentType) throws ODataApplicationException, SerializerException {
    final UriInfoResource resource = uriInfo.asUriInfoResource();
    validateOptions(resource);
    validatePath(resource);

    final List<UriResource> resourceParts = resource.getUriResourceParts();
    final UriResourceEntitySet resourceEntitySet = (UriResourceEntitySet) resourceParts.get(0);
    final List<String> path = getPropertyPath(resourceParts);

    final Property property = getPropertyData(resourceEntitySet, path);

    if (property == null || property.getValue() == null) {
      response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    } else {
      final EdmProperty edmProperty = ((UriResourceProperty) resourceParts.get(path.size())).getProperty();
      final EdmPrimitiveType type = (EdmPrimitiveType) edmProperty.getType();
      if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Binary)) {
        response.setContent(new ByteArrayInputStream((byte[]) property.getValue()));
      } else {
        try {
          final String value = type.valueToString(property.getValue(),
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode());
          response.setContent(new ByteArrayInputStream(value.getBytes("UTF-8")));
        } catch (final EdmPrimitiveTypeException e) {
          throw new ODataApplicationException("Error in value formatting.",
              HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT, e);
        } catch (final UnsupportedEncodingException e) {
          throw new ODataApplicationException("Encoding exception.",
              HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT, e);
        }
      }
      response.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    }
  }

  private void validatePath(final UriInfoResource uriInfo) throws ODataApplicationException {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    for (final UriResource segment : resourcePaths.subList(1, resourcePaths.size())) {
      final UriResourceKind kind = segment.getKind();
      if (kind != UriResourceKind.primitiveProperty
          && kind != UriResourceKind.complexProperty
          && kind != UriResourceKind.count
          && kind != UriResourceKind.value) {
        throw new ODataApplicationException("Invalid resource type.",
            HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
      }
    }
  }
}
