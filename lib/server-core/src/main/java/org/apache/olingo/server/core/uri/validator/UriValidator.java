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
package org.apache.olingo.server.core.uri.validator;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;

public class UriValidator {

  //@formatter:off (Eclipse formatter)
  //CHECKSTYLE:OFF (Maven checkstyle)
  private static final boolean[][] decisionMatrix =
    {
      /*                                          0-FILTER 1-FORMAT 2-EXPAND 3-ID     4-COUNT  5-ORDERBY 6-SEARCH 7-SELECT 8-SKIP   9-SKIPTOKEN 10-TOP 11-APPLY  12-DELTATOKEN */
      /*                              all  0 */ { true ,   true ,   true ,   false,   true ,   true ,    true ,   true ,   true ,   true ,      true , true,      true  },
      /*                            batch  1 */ { false,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false, false,     false  },
      /*                        crossjoin  2 */ { true ,   true ,   true ,   false,   true ,   true ,    true ,   true ,   true ,   true ,      true , true,      true  },
      /*                         entityId  3 */ { false,   true ,   true ,   true ,   false,   false,    false,   true ,   false,   false,      false, false,     false },
      /*                         metadata  4 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false, false,     false },
      /*                          service  5 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false, false,     false },
      /*                        entitySet  6 */ { true ,   true ,   true ,   false,   true ,   true ,    true ,   true ,   true ,   true ,      true , true,      true  },
      /*                   entitySetCount  7 */ { true ,   false,   false,   false,   false,   false,    true ,   false,   false,   false,      false, true,      true  },
      /*                           entity  8 */ { false,   true ,   true ,   false,   false,   false,    false,   true ,   false,   false,      false, false,     false },
      /*                      mediaStream  9 */ { false,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false, false,     false },
      /*                       references 10 */ { true ,   true ,   false,   false,   true ,   true ,    true ,   false,   true ,   true ,      true , false,     true },
      /*                        reference 11 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false, false,     false },
      /*                  propertyComplex 12 */ { false,   true ,   true ,   false,   false,   false,    false,   true ,   false,   false,      false, false,     false },
      /*        propertyComplexCollection 13 */ { true ,   true ,   true ,   false,   true ,   true ,    false,   true ,   true ,   true ,      true , true ,     true },
      /*   propertyComplexCollectionCount 14 */ { true ,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false, true ,     false },
      /*                propertyPrimitive 15 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false, false,     false },
      /*      propertyPrimitiveCollection 16 */ { true ,   true ,   false,   false,   true ,   true ,    false,   false,   true ,   true ,      true , false,     true },
      /* propertyPrimitiveCollectionCount 17 */ { true ,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false, false,     false },
      /*           propertyPrimitiveValue 18 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false, false,     false },
      /*                             none 19 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false, false,     false }
    };
  //CHECKSTYLE:ON
  //@formatter:on

  private enum UriType {
    all(0),
    batch(1),
    crossjoin(2),
    entityId(3),
    metadata(4),
    service(5),
    entitySet(6),
    entitySetCount(7),
    entity(8),
    mediaStream(9),
    references(10),
    reference(11),
    propertyComplex(12),
    propertyComplexCollection(13),
    propertyComplexCollectionCount(14),
    propertyPrimitive(15),
    propertyPrimitiveCollection(16),
    propertyPrimitiveCollectionCount(17),
    propertyPrimitiveValue(18),
    none(19);

    private final int idx;

    UriType(final int i) {
      idx = i;
    }

    public int getIndex() {
      return idx;
    }
  }

  private static final Map<SystemQueryOptionKind, Integer> OPTION_INDEX;
  static {
    Map<SystemQueryOptionKind, Integer> temp =
        new EnumMap<SystemQueryOptionKind, Integer>(SystemQueryOptionKind.class);
    temp.put(SystemQueryOptionKind.FILTER, 0);
    temp.put(SystemQueryOptionKind.FORMAT, 1);
    temp.put(SystemQueryOptionKind.EXPAND, 2);
    temp.put(SystemQueryOptionKind.ID, 3);
    temp.put(SystemQueryOptionKind.COUNT, 4);
    temp.put(SystemQueryOptionKind.ORDERBY, 5);
    temp.put(SystemQueryOptionKind.SEARCH, 6);
    temp.put(SystemQueryOptionKind.SELECT, 7);
    temp.put(SystemQueryOptionKind.SKIP, 8);
    temp.put(SystemQueryOptionKind.SKIPTOKEN, 9);
    temp.put(SystemQueryOptionKind.TOP, 10);
    temp.put(SystemQueryOptionKind.APPLY, 11);
    temp.put(SystemQueryOptionKind.DELTATOKEN, 12);
    OPTION_INDEX = Collections.unmodifiableMap(temp);
  }

  public void validate(final UriInfo uriInfo, final HttpMethod httpMethod) throws UriValidationException {
    final UriType uriType = getUriType(uriInfo);
    if (HttpMethod.GET == httpMethod) {
      validateReadQueryOptions(uriType, uriInfo.getSystemQueryOptions());
    } else {
      validateNonReadQueryOptions(uriType, isAction(uriInfo), uriInfo.getSystemQueryOptions(), httpMethod);
      validatePropertyOperations(uriInfo, httpMethod);
    }
  }

  private UriType getUriType(final UriInfo uriInfo) throws UriValidationException {
    UriType uriType;

    switch (uriInfo.getKind()) {
    case all:
      uriType = UriType.all;
      break;
    case batch:
      uriType = UriType.batch;
      break;
    case crossjoin:
      uriType = UriType.crossjoin;
      break;
    case entityId:
      uriType = UriType.entityId;
      break;
    case metadata:
      uriType = UriType.metadata;
      break;
    case resource:
      uriType = getUriTypeForResource(uriInfo.getUriResourceParts());
      break;
    case service:
      uriType = UriType.service;
      break;
    default:
      throw new UriValidationException("Unsupported uriInfo kind: " + uriInfo.getKind(),
          UriValidationException.MessageKeys.UNSUPPORTED_URI_KIND, uriInfo.getKind().toString());
    }

    return uriType;
  }

  /**
   * Determines the URI type for a resource path.
   * The URI parser has already made sure that there are enough segments for a given type of the last segment,
   * but don't try to extract always the second-to-last segment, it could cause an {@link IndexOutOfBoundsException}.
   */
  private UriType getUriTypeForResource(final List<UriResource> segments) throws UriValidationException {
    final UriResource lastPathSegment = segments.get(segments.size() - 1);

    UriType uriType;
    switch (lastPathSegment.getKind()) {
    case count:
      uriType = getUriTypeForCount(segments.get(segments.size() - 2));
      break;
    case action:
      uriType = getUriTypeForAction(lastPathSegment);
      break;
    case complexProperty:
      uriType = getUriTypeForComplexProperty(lastPathSegment);
      break;
    case entitySet:
    case navigationProperty:
      uriType = getUriTypeForEntitySet(lastPathSegment);
      break;
    case function:
      uriType = getUriTypeForFunction(lastPathSegment);
      break;
    case primitiveProperty:
      uriType = getUriTypeForPrimitiveProperty(lastPathSegment);
      break;
    case ref:
      uriType = getUriTypeForRef(segments.get(segments.size() - 2));
      break;
    case singleton:
      uriType = UriType.entity;
      break;
    case value:
      uriType = getUriTypeForValue(segments.get(segments.size() - 2));
      break;
    default:
      throw new UriValidationException("Unsupported uriResource kind: " + lastPathSegment.getKind(),
          UriValidationException.MessageKeys.UNSUPPORTED_URI_RESOURCE_KIND, lastPathSegment.getKind().toString());
    }

    return uriType;
  }

  private UriType getUriTypeForValue(final UriResource secondLastPathSegment) throws UriValidationException {
    UriType uriType;
    switch (secondLastPathSegment.getKind()) {
    case primitiveProperty:
      uriType = UriType.propertyPrimitiveValue;
      break;
    case entitySet:
    case navigationProperty:
    case singleton:
      uriType = UriType.mediaStream;
      break;
    case function:
      UriResourceFunction uriFunction = (UriResourceFunction) secondLastPathSegment;
      final EdmFunction function = uriFunction.getFunction();
      uriType = function.getReturnType().getType().getKind() == EdmTypeKind.ENTITY ?
          UriType.mediaStream : UriType.propertyPrimitiveValue;
      break;
    default:
      throw new UriValidationException(
          "Unexpected kind in path segment before $value: " + secondLastPathSegment.getKind(),
          UriValidationException.MessageKeys.UNALLOWED_KIND_BEFORE_VALUE, secondLastPathSegment.toString());
    }
    return uriType;
  }

  private UriType getUriTypeForRef(final UriResource secondLastPathSegment) throws UriValidationException {
    return isCollection(secondLastPathSegment) ? UriType.references : UriType.reference;
  }

  private boolean isCollection(final UriResource pathSegment) throws UriValidationException {
    if (pathSegment instanceof UriResourcePartTyped) {
      return ((UriResourcePartTyped) pathSegment).isCollection();
    } else {
      throw new UriValidationException(
          "Path segment is not an instance of UriResourcePartTyped but " + pathSegment.getClass(),
          UriValidationException.MessageKeys.LAST_SEGMENT_NOT_TYPED, pathSegment.toString());
    }
  }

  private UriType getUriTypeForPrimitiveProperty(final UriResource lastPathSegment) throws UriValidationException {
    return isCollection(lastPathSegment) ? UriType.propertyPrimitiveCollection : UriType.propertyPrimitive;
  }

  private UriType getUriTypeForFunction(final UriResource lastPathSegment) throws UriValidationException {
    final UriResourceFunction uriFunction = (UriResourceFunction) lastPathSegment;
    final boolean isCollection = uriFunction.isCollection();
    final EdmTypeKind typeKind = uriFunction.getFunction().getReturnType().getType().getKind();
    UriType uriType;
    switch (typeKind) {
    case ENTITY:
      uriType = isCollection ? UriType.entitySet : UriType.entity;
      break;
    case PRIMITIVE:
    case ENUM:
    case DEFINITION:
      uriType = isCollection ? UriType.propertyPrimitiveCollection : UriType.propertyPrimitive;
      break;
    case COMPLEX:
      uriType = isCollection ? UriType.propertyComplexCollection : UriType.propertyComplex;
      break;
    default:
      throw new UriValidationException("Unsupported function return type: " + typeKind,
          UriValidationException.MessageKeys.UNSUPPORTED_FUNCTION_RETURN_TYPE, typeKind.toString());
    }

    return uriType;
  }

  private UriType getUriTypeForEntitySet(final UriResource lastPathSegment) throws UriValidationException {
    return isCollection(lastPathSegment) ? UriType.entitySet : UriType.entity;
  }

  private UriType getUriTypeForComplexProperty(final UriResource lastPathSegment) throws UriValidationException {
    return isCollection(lastPathSegment) ? UriType.propertyComplexCollection : UriType.propertyComplex;
  }

  private UriType getUriTypeForAction(final UriResource lastPathSegment) throws UriValidationException {
    final EdmReturnType rt = ((UriResourceAction) lastPathSegment).getAction().getReturnType();
    if (rt == null) {
      return UriType.none;
    }
    UriType uriType;
    switch (rt.getType().getKind()) {
    case ENTITY:
      uriType = rt.isCollection() ? UriType.entitySet : UriType.entity;
      break;
    case PRIMITIVE:
    case ENUM:
    case DEFINITION:
      uriType = rt.isCollection() ? UriType.propertyPrimitiveCollection : UriType.propertyPrimitive;
      break;
    case COMPLEX:
      uriType = rt.isCollection() ? UriType.propertyComplexCollection : UriType.propertyComplex;
      break;
    default:
      throw new UriValidationException("Unsupported action return type: " + rt.getType().getKind(),
          UriValidationException.MessageKeys.UNSUPPORTED_ACTION_RETURN_TYPE, rt.getType().getKind().toString());
    }
    return uriType;
  }

  private UriType getUriTypeForCount(final UriResource secondLastPathSegment) throws UriValidationException {
    UriType uriType;
    switch (secondLastPathSegment.getKind()) {
    case entitySet:
    case navigationProperty:
      uriType = UriType.entitySetCount;
      break;
    case complexProperty:
      uriType = UriType.propertyComplexCollectionCount;
      break;
    case primitiveProperty:
      uriType = UriType.propertyPrimitiveCollectionCount;
      break;
    case function:
      final UriResourceFunction uriFunction = (UriResourceFunction) secondLastPathSegment;
      final EdmFunction function = uriFunction.getFunction();
      final EdmType returnType = function.getReturnType().getType();
      switch (returnType.getKind()) {
      case ENTITY:
        uriType = UriType.entitySetCount;
        break;
      case COMPLEX:
        uriType = UriType.propertyComplexCollectionCount;
        break;
      case PRIMITIVE:
      case ENUM:
      case DEFINITION:
        uriType = UriType.propertyPrimitiveCollectionCount;
        break;
      default:
        throw new UriValidationException("Unsupported return type: " + returnType.getKind(),
            UriValidationException.MessageKeys.UNSUPPORTED_FUNCTION_RETURN_TYPE, returnType.getKind().toString());
      }
      break;
    default:
      throw new UriValidationException("Illegal path part kind before $count: " + secondLastPathSegment.getKind(),
          UriValidationException.MessageKeys.UNALLOWED_KIND_BEFORE_COUNT, secondLastPathSegment.toString());
    }

    return uriType;
  }

  private void validateReadQueryOptions(final UriType uriType, final List<SystemQueryOption> options)
      throws UriValidationException {
    for (final SystemQueryOption option : options) {
      final SystemQueryOptionKind kind = option.getKind();
      if (OPTION_INDEX.containsKey(kind)) {
        final int columnIndex = OPTION_INDEX.get(kind);
        if (!decisionMatrix[uriType.getIndex()][columnIndex]) {
          throw new UriValidationException("System query option not allowed: " + option.getName(),
              UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED, option.getName());
        }
      } else {
        throw new UriValidationException("Unsupported option: " + kind,
            UriValidationException.MessageKeys.UNSUPPORTED_QUERY_OPTION, kind.toString());
      }
    }
  }

  private void validateNonReadQueryOptions(final UriType uriType, final boolean isAction,
      final List<SystemQueryOption> options, final HttpMethod httpMethod) throws UriValidationException {
    if (httpMethod == HttpMethod.POST && isAction) {
      // From the OData specification:
      // For POST requests to an action URL the return type of the action determines the applicable
      // system query options that a service MAY support, following the same rules as GET requests.
      validateReadQueryOptions(uriType, options);

    } else if (httpMethod == HttpMethod.DELETE && uriType == UriType.references) {
      // Only $id is allowed as SystemQueryOption for DELETE on a reference collection.
      for (final SystemQueryOption option : options) {
        if (SystemQueryOptionKind.ID != option.getKind()) {
          throw new UriValidationException(
              "System query option " + option.getName() + " is not allowed for method " + httpMethod,
              UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD,
              option.getName(), httpMethod.toString());
        }
      }

    } else if (!options.isEmpty()) {
      StringBuilder optionsString = new StringBuilder();
      for (final SystemQueryOption option : options) {
        optionsString.append(option.getName()).append(' ');
      }
      throw new UriValidationException(
          "System query option(s) " + optionsString.toString() + "not allowed for method " + httpMethod,
          UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD,
          optionsString.toString(), httpMethod.toString());
    }
  }

  private boolean isAction(final UriInfo uriInfo) {
    List<UriResource> uriResourceParts = uriInfo.getUriResourceParts();
    return !uriResourceParts.isEmpty()
        && UriResourceKind.action == uriResourceParts.get(uriResourceParts.size() - 1).getKind();
  }

  private void validatePropertyOperations(final UriInfo uriInfo, final HttpMethod method)
      throws UriValidationException {
    final List<UriResource> parts = uriInfo.getUriResourceParts();
    final UriResource last = !parts.isEmpty() ? parts.get(parts.size() - 1) : null;
    final UriResource previous = parts.size() > 1 ? parts.get(parts.size() - 2) : null;
    if (last != null
        && (last.getKind() == UriResourceKind.primitiveProperty
        || last.getKind() == UriResourceKind.complexProperty
        || (last.getKind() == UriResourceKind.value
            && previous != null && previous.getKind() == UriResourceKind.primitiveProperty))) {
      final EdmProperty property = ((UriResourceProperty)
          (last.getKind() == UriResourceKind.value ? previous : last)).getProperty();
      if (method == HttpMethod.PATCH && property.isCollection()) {
        throw new UriValidationException("Attempt to patch collection property.",
            UriValidationException.MessageKeys.UNSUPPORTED_HTTP_METHOD, method.toString());
      }
      if (method == HttpMethod.DELETE && !property.isNullable()) {
        throw new UriValidationException("Attempt to delete non-nullable property.",
            UriValidationException.MessageKeys.UNSUPPORTED_HTTP_METHOD, method.toString());
      }
    }
  }
}
