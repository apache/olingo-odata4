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

import java.util.HashMap;
import java.util.List;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;

public class UriValidator {

  //@formatter:off (Eclipse formatter)
  //CHECKSTYLE:OFF (Maven checkstyle)
  private boolean[][] decisionMatrix =
      {
          /*                                          0-FILTER 1-FORMAT 2-EXPAND 3-ID     4-COUNT  5-ORDERBY 6-SEARCH 7-SELECT 8-SKIP   9-SKIPTOKEN 10-LEVELS 11-TOP */
          /*                              all  0 */ { true ,   true ,   true ,   false,   true ,   true ,    true ,   true ,   true ,   true ,      true ,    false },
          /*                            batch  1 */ { false,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*                        crossjoin  2 */ { true ,   true ,   true ,   false,   true ,   true ,    true ,   true ,   true ,   true ,      true ,    true  },
          /*                         entityId  3 */ { false,   true ,   true ,   true ,   false,   false,    false,   true ,   false,   false,      true ,    false },
          /*                         metadata  4 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*                         resource  5 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*                          service  6 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*                        entitySet  7 */ { true ,   true ,   true ,   false,   true ,   true ,    true ,   true ,   true ,   true ,      true ,    true  },
          /*                   entitySetCount  8 */ { true,    false,   false,   false,   false,   false,    true,    false,   false,   false,      false,    false },
          /*                           entity  9 */ { false,   true ,   true ,   false,   false,   false,    false,   true ,   false,   false,      true ,    false },
          /*                      mediaStream 10 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*                       references 11 */ { true ,   true ,   false,   false,   false,   true ,    true ,   false,   true ,   true ,      false,    true  },
          /*                        reference 12 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*                  propertyComplex 13 */ { false,   true ,   true ,   false,   false,   false,    false,   true ,   false,   false,      true ,    false },
          /*        propertyComplexCollection 14 */ { true ,   true ,   true ,   false,   true ,   true ,    false,   false,   true ,   true ,      true ,    true  },
          /*   propertyComplexCollectionCount 15 */ { true,    false,   false,   false,   false,   false,    true,    false,   false,   false,      false,    false },
          /*                propertyPrimitive 16 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*      propertyPrimitiveCollection 17 */ { true ,   true ,   false,   false,   false,   true ,    false,   false,   true ,   true ,      false,    true  },
          /* propertyPrimitiveCollectionCount 18 */ { true,    false,   false,   false,   false,   false,    true,    false,   false,   false,      false,    false },
          /*           propertyPrimitiveValue 19 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },                    
      };

  private boolean[][] decisionMatrixForHttpMethod =
    {
        /*                                          0-FILTER 1-FORMAT 2-EXPAND 3-ID     4-COUNT  5-ORDERBY 6-SEARCH 7-SELECT 8-SKIP   9-SKIPTOKEN 10-LEVELS 11-TOP */
        /*                              GET  0 */ { true ,   true ,   true ,   true,    true ,   true ,    true ,   true ,   true ,   true ,      true ,    true },
        /*                             POST  0 */ { true ,   false ,  true ,   false,   false ,  true ,    false ,  true ,   false ,  false ,     true ,    false },
        /*                              PUT  0 */ { false ,  false ,  false ,  false,   false ,  false ,   false ,  false ,  false ,  false ,     false ,   false },
        /*                           DELETE  0 */ { false ,  false ,  false ,  false,   false ,  false,    false ,  false,   false ,  false ,     false,    false },
        /*                            PATCH  0 */ { false ,  false ,  false ,  false,   false ,  false ,   false ,  false ,  false ,  false ,     false ,   false },
        /*                            MERGE  0 */ { false ,  false ,  false ,  false,   false ,  false ,   false ,  false ,  false ,  false ,     false ,   false },
    };
  
  //CHECKSTYLE:ON
  //@formatter:on

  private enum RowIndexForUriType {
    all(0),
    batch(1),
    crossjoin(2),
    entityId(3),
    metadata(4),
    resource(5),
    service(6),
    entitySet(7),
    entitySetCount(8),
    entity(9),
    mediaStream(10),
    references(11),
    reference(12),
    propertyComplex(13),
    propertyComplexCollection(14),
    propertyComplexCollectionCount(15),
    propertyPrimitive(16),
    propertyPrimitiveCollection(17),
    propertyPrimitiveCollectionCount(18),
    propertyPrimitiveValue(19);

    private int idx;

    RowIndexForUriType(int i) {
      idx = i;
    }

    public int getIndex() {
      return idx;
    }
  }

  private enum ColumnIndex {
    filter(0),
    format(1),
    expand(2),
    id(3),
    count(4),
    orderby(5),
    search(6),
    select(7),
    skip(8),
    skiptoken(9),
    levels(10),
    top(11);

    private int idx;

    ColumnIndex(int i) {
      idx = i;
    }

    public int getIndex() {
      return idx;
    }

  }

  private enum RowIndexForHttpMethod {
    GET(0),
    POST(1),
    PUT(2),
    DELETE(3),
    MERGE(4),
    PATCH(5);

    private int idx;

    RowIndexForHttpMethod(int i) {
      idx = i;
    }

    public int getIndex() {
      return idx;
    }

  }

  public void validate(final UriInfo uriInfo, final Edm edm, String httpMethod) throws UriValidationException {

    validateForHttpMethod(uriInfo, httpMethod);
    validateQueryOptions(uriInfo, edm);
    validateKeyPredicateTypes(uriInfo, edm);

  }

  private ColumnIndex colIndex(SystemQueryOptionKind queryOptionKind) {
    ColumnIndex idx;
    switch (queryOptionKind) {
    case FILTER:
      idx = ColumnIndex.filter;
      break;
    case FORMAT:
      idx = ColumnIndex.format;
      break;
    case EXPAND:
      idx = ColumnIndex.expand;
      break;
    case ID:
      idx = ColumnIndex.id;
      break;
    case COUNT:
      idx = ColumnIndex.count;
      break;
    case ORDERBY:
      idx = ColumnIndex.orderby;
      break;
    case SEARCH:
      idx = ColumnIndex.search;
      break;
    case SELECT:
      idx = ColumnIndex.select;
      break;
    case SKIP:
      idx = ColumnIndex.skip;
      break;
    case SKIPTOKEN:
      idx = ColumnIndex.skiptoken;
      break;
    case LEVELS:
      idx = ColumnIndex.levels;
      break;
    case TOP:
      idx = ColumnIndex.top;
      break;
    default:
      throw new ODataRuntimeException("Unsupported option: " + queryOptionKind);
    }

    return idx;
  }

  private RowIndexForUriType rowIndexForUriType(final UriInfo uriInfo, Edm edm) throws UriValidationException {
    RowIndexForUriType idx;

    switch (uriInfo.getKind()) {
    case all:
      idx = RowIndexForUriType.all;
      break;
    case batch:
      idx = RowIndexForUriType.batch;
      break;
    case crossjoin:
      idx = RowIndexForUriType.crossjoin;
      break;
    case entityId:
      idx = RowIndexForUriType.entityId;
      break;
    case metadata:
      idx = RowIndexForUriType.metadata;
      break;
    case resource:
      idx = rowIndexForResourceKind(uriInfo, edm);
      break;
    case service:
      idx = RowIndexForUriType.service;
      break;
    default:
      throw new ODataRuntimeException("Unsupported uriInfo kind: " + uriInfo.getKind());
    }

    return idx;
  }

  private RowIndexForUriType rowIndexForResourceKind(UriInfo uriInfo, Edm edm) throws UriValidationException {
    RowIndexForUriType idx;

    int lastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 1;
    UriResource lastPathSegment = uriInfo.getUriResourceParts().get(lastPathSegmentIndex);

    switch (lastPathSegment.getKind()) {
    case count:
      idx = rowIndexForCount(uriInfo, lastPathSegment);
      break;
    case action:
      idx = rowIndexForAction(lastPathSegment);
      break;
    case complexProperty:
      idx = rowIndexForComplexProperty(lastPathSegment);
      break;
    case entitySet:
      idx = rowIndexForEntitySet(lastPathSegment);
      break;
    case function:
      idx = rowIndexForFunction(lastPathSegment);
      break;
    case navigationProperty:
      idx =
          ((UriResourceNavigation) lastPathSegment).isCollection() ? RowIndexForUriType.entitySet
              : RowIndexForUriType.entity;
      break;
    case primitiveProperty:
      idx = rowIndexForPrimitiveProperty(lastPathSegment);
      break;
    case ref:
      idx = rowIndexForRef(uriInfo, lastPathSegment);
      break;
    case root:
      idx = RowIndexForUriType.service;
      break;
    case singleton:
      idx = RowIndexForUriType.entity;
      break;
    case value:
      idx = rowIndexForValue(uriInfo);
      break;
    default:
      throw new ODataRuntimeException("Unsupported uriResource kind: " + lastPathSegment.getKind());
    }

    return idx;
  }

  private RowIndexForUriType rowIndexForValue(UriInfo uriInfo) throws UriValidationException {
    RowIndexForUriType idx;
    int secondLastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 2;
    UriResource secondLastPathSegment = uriInfo.getUriResourceParts().get(secondLastPathSegmentIndex);
    switch (secondLastPathSegment.getKind()) {
    case primitiveProperty:
      idx = RowIndexForUriType.propertyPrimitiveValue;
      break;
    case entitySet:
      idx = RowIndexForUriType.mediaStream;
      break;
    default:
      throw new UriValidationException("Unexpected kind in path segment before $value: "
          + secondLastPathSegment.getKind());

    }
    return idx;
  }

  private RowIndexForUriType rowIndexForRef(UriInfo uriInfo, UriResource lastPathSegment)
      throws UriValidationException {
    RowIndexForUriType idx;
    int secondLastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 2;
    UriResource secondLastPathSegment = uriInfo.getUriResourceParts().get(secondLastPathSegmentIndex);

    if (secondLastPathSegment instanceof UriResourcePartTyped) {
      idx =
          ((UriResourcePartTyped) secondLastPathSegment).isCollection() ? RowIndexForUriType.references
              : RowIndexForUriType.reference;
    } else {
      throw new UriValidationException("secondLastPathSegment not a class of UriResourcePartTyped: "
          + lastPathSegment.getClass());
    }

    return idx;
  }

  private RowIndexForUriType rowIndexForPrimitiveProperty(UriResource lastPathSegment)
      throws UriValidationException {
    RowIndexForUriType idx;
    if (lastPathSegment instanceof UriResourcePartTyped) {
      idx =
          ((UriResourcePartTyped) lastPathSegment).isCollection() ? RowIndexForUriType.propertyPrimitiveCollection
              : RowIndexForUriType.propertyPrimitive;
    } else {
      throw new UriValidationException("lastPathSegment not a class of UriResourcePartTyped: "
          + lastPathSegment.getClass());
    }
    return idx;
  }

  private RowIndexForUriType rowIndexForFunction(UriResource lastPathSegment) throws UriValidationException {
    RowIndexForUriType idx;
    UriResourceFunction urf = (UriResourceFunction) lastPathSegment;
    EdmReturnType rt = urf.getFunction().getReturnType();
    switch (rt.getType().getKind()) {
    case ENTITY:
      if (((EdmEntityType) rt.getType()).hasStream()) {
        idx = RowIndexForUriType.mediaStream;
      } else {
        idx = rt.isCollection() ? RowIndexForUriType.entitySet : RowIndexForUriType.entity;
      }
      break;
    case PRIMITIVE:
      idx = rt.isCollection() ? RowIndexForUriType.propertyPrimitiveCollection : RowIndexForUriType.propertyPrimitive;
      break;
    case COMPLEX:
      idx = rt.isCollection() ? RowIndexForUriType.propertyComplexCollection : RowIndexForUriType.propertyComplex;
      break;
    default:
      throw new UriValidationException("Unsupported function return type: " + rt.getType().getKind());
    }

    return idx;
  }

  private RowIndexForUriType rowIndexForEntitySet(UriResource lastPathSegment) throws UriValidationException {
    RowIndexForUriType idx;
    if (lastPathSegment instanceof UriResourcePartTyped) {
      idx =
          ((UriResourcePartTyped) lastPathSegment).isCollection() ? RowIndexForUriType.entitySet
              : RowIndexForUriType.entity;
    } else {
      throw new UriValidationException("lastPathSegment not a class of UriResourcePartTyped: "
          + lastPathSegment.getClass());
    }
    return idx;
  }

  private RowIndexForUriType rowIndexForComplexProperty(UriResource lastPathSegment) throws UriValidationException {
    RowIndexForUriType idx;
    if (lastPathSegment instanceof UriResourcePartTyped) {
      idx =
          ((UriResourcePartTyped) lastPathSegment).isCollection() ? RowIndexForUriType.propertyComplexCollection
              : RowIndexForUriType.propertyComplex;
    } else {
      throw new UriValidationException("lastPathSegment not a class of UriResourcePartTyped: "
          + lastPathSegment.getClass());
    }
    return idx;
  }

  private RowIndexForUriType rowIndexForAction(UriResource lastPathSegment) throws UriValidationException {
    RowIndexForUriType idx;
    UriResourceAction ura = (UriResourceAction) lastPathSegment;
    EdmReturnType rt = ura.getAction().getReturnType();
    switch (rt.getType().getKind()) {
    case ENTITY:
      if (((EdmEntityType) rt.getType()).hasStream()) {
        idx = RowIndexForUriType.mediaStream;
      } else {
        idx = rt.isCollection() ? RowIndexForUriType.entitySet : RowIndexForUriType.entity;
      }
      break;
    case PRIMITIVE:
      idx = rt.isCollection() ? RowIndexForUriType.propertyPrimitiveCollection : RowIndexForUriType.propertyPrimitive;
      break;
    case COMPLEX:
      idx = rt.isCollection() ? RowIndexForUriType.propertyComplexCollection : RowIndexForUriType.propertyComplex;
      break;
    default:
      throw new UriValidationException("Unsupported action return type: " + rt.getType().getKind());
    }

    return idx;
  }

  private RowIndexForUriType rowIndexForCount(UriInfo uriInfo, UriResource lastPathSegment)
      throws UriValidationException {

    RowIndexForUriType idx;
    int secondLastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 2;
    UriResource secondLastPathSegment = uriInfo.getUriResourceParts().get(secondLastPathSegmentIndex);
    switch (secondLastPathSegment.getKind()) {
    case entitySet:
      idx = RowIndexForUriType.entitySetCount;
      break;
    case complexProperty:
      idx = RowIndexForUriType.propertyComplexCollectionCount;
      break;
    case primitiveProperty:
      idx = RowIndexForUriType.propertyPrimitiveCollectionCount;
      break;
    default:
      throw new UriValidationException("Illegal path part kind: " + lastPathSegment.getKind());
    }

    return idx;
  }

  private void validateQueryOptions(final UriInfo uriInfo, Edm edm) throws UriValidationException {
    RowIndexForUriType row = rowIndexForUriType(uriInfo, edm);

    for (SystemQueryOption option : uriInfo.getSystemQueryOptions()) {
      ColumnIndex col = colIndex(option.getKind());

      if (!decisionMatrix[row.getIndex()][col.getIndex()]) {
        throw new UriValidationException("System query option not allowed: " + option.getName());
      }
    }

  }

  private void validateForHttpMethod(UriInfo uriInfo, String httpMethod) throws UriValidationException {
    RowIndexForHttpMethod row = rowIndexForHttpMethod(httpMethod);

    for (SystemQueryOption option : uriInfo.getSystemQueryOptions()) {
      ColumnIndex col = colIndex(option.getKind());
      if (!decisionMatrixForHttpMethod[row.getIndex()][col.getIndex()]) {
        throw new UriValidationException("System query option " + option.getName() + " not allowed for method "
            + httpMethod);
      }
    }

  }

  private RowIndexForHttpMethod rowIndexForHttpMethod(String httpMethod) throws UriValidationException {
    RowIndexForHttpMethod idx;

    if ("GET".equalsIgnoreCase(httpMethod)) {
      idx = RowIndexForHttpMethod.GET;
    } else if ("POST".equalsIgnoreCase(httpMethod)) {
      idx = RowIndexForHttpMethod.POST;
    } else if ("PUT".equalsIgnoreCase(httpMethod)) {
      idx = RowIndexForHttpMethod.PUT;
    } else if ("DELETE".equalsIgnoreCase(httpMethod)) {
      idx = RowIndexForHttpMethod.DELETE;
    } else if ("PATCH".equalsIgnoreCase(httpMethod)) {
      idx = RowIndexForHttpMethod.PATCH;
    } else if ("MERGE".equalsIgnoreCase(httpMethod)) {
      idx = RowIndexForHttpMethod.MERGE;
    } else {
      throw new UriValidationException("HTTP method not supported: " + httpMethod);
    }

    return idx;
  }

  private void validateKeyPredicateTypes(final UriInfo uriInfo, final Edm edm) throws UriValidationException {
    try {
      for (UriResource pathSegment : uriInfo.getUriResourceParts()) {
        if (pathSegment.getKind() == UriResourceKind.entitySet) {
          UriResourceEntitySet pathEntitySet = (UriResourceEntitySet) pathSegment;

          EdmEntityType type = pathEntitySet.getEntityType();
          List<EdmKeyPropertyRef> keys = type.getKeyPropertyRefs();
          List<UriParameter> keyPredicates = pathEntitySet.getKeyPredicates();

          if (null != keyPredicates) {

            HashMap<String, EdmKeyPropertyRef> edmKeys = new HashMap<String, EdmKeyPropertyRef>();
            for (EdmKeyPropertyRef key : keys) {
              edmKeys.put(key.getKeyPropertyName(), key);
            }

            for (UriParameter keyPredicate : keyPredicates) {
              String name = keyPredicate.getName();
              String value = keyPredicate.getText();
              EdmKeyPropertyRef edmKey = edmKeys.get(name);

              if (edmKey == null) {
                throw new UriValidationException("Unknown key property: " + name);
              }

              EdmType edmType = edmKey.getProperty().getType();
              EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) edmType;

              String edmLiteral = edmPrimitiveType.fromUriLiteral(value);
              edmPrimitiveType.validate(edmLiteral, edmKey.getProperty().isNullable(), edmKey.getProperty()
                  .getMaxLength(), edmKey.getProperty().getPrecision(), edmKey.getProperty().getScale(), edmKey
                  .getProperty().isUnicode());
            }
          }
        }
      }
    } catch (EdmPrimitiveTypeException e) {
      throw new UriValidationException(e);
    }
  }
}
