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
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;

public class UriValidator {

  //@formatter:off (Eclipse formatter)
  //CHECKSTYLE:OFF (Maven checkstyle)
  private final boolean[][] decisionMatrix =
    {
      /*                                          0-FILTER 1-FORMAT 2-EXPAND 3-ID     4-COUNT  5-ORDERBY 6-SEARCH 7-SELECT 8-SKIP   9-SKIPTOKEN 10-TOP */
      /*                              all  0 */ { true ,   true ,   true ,   false,   true ,   true ,    true ,   true ,   true ,   true ,      true  },
      /*                            batch  1 */ { false,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false },
      /*                        crossjoin  2 */ { true ,   true ,   true ,   false,   true ,   true ,    true ,   true ,   true ,   true ,      true  },
      /*                         entityId  3 */ { false,   true ,   true ,   true ,   false,   false,    false,   true ,   false,   false,      false },
      /*                         metadata  4 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false },
      /*                          service  5 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false },
      /*                        entitySet  6 */ { true ,   true ,   true ,   false,   true ,   true ,    true ,   true ,   true ,   true ,      true  },
      /*                   entitySetCount  7 */ { true ,   false,   false,   false,   false,   false,    true ,   false,   false,   false,      false },
      /*                           entity  8 */ { false,   true ,   true ,   false,   false,   false,    false,   true ,   false,   false,      false },
      /*                      mediaStream  9 */ { false,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false },
      /*                       references 10 */ { true ,   true ,   false,   true ,   true ,   true ,    true ,   false,   true ,   true ,      true  },
      /*                        reference 11 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false },
      /*                  propertyComplex 12 */ { false,   true ,   true ,   false,   false,   false,    false,   true ,   false,   false,      false },
      /*        propertyComplexCollection 13 */ { true ,   true ,   true ,   false,   true ,   true ,    false,   true ,   true ,   true ,      true  },
      /*   propertyComplexCollectionCount 14 */ { true ,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false },
      /*                propertyPrimitive 15 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false },
      /*      propertyPrimitiveCollection 16 */ { true ,   true ,   false,   false,   true ,   true ,    false,   false,   true ,   true ,      true  },
      /* propertyPrimitiveCollectionCount 17 */ { true ,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false },
      /*           propertyPrimitiveValue 18 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false },
      /*                             none 19 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false }
    };
  //CHECKSTYLE:ON
  //@formatter:on

  private enum RowIndexForUriType {
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

    RowIndexForUriType(final int i) {
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
    top(10);

    private final int idx;

    ColumnIndex(final int i) {
      idx = i;
    }

    public int getIndex() {
      return idx;
    }
  }

  public void validate(final UriInfo uriInfo, final HttpMethod httpMethod) throws UriValidationException {
    if (HttpMethod.GET != httpMethod) {
      validateForHttpMethod(uriInfo, httpMethod);
    }
    validateQueryOptions(uriInfo);
    validateParameters(uriInfo);
    validateKeyPredicates(uriInfo);
    validatePropertyOperations(uriInfo, httpMethod);
  }

  private ColumnIndex colIndex(final SystemQueryOptionKind queryOptionKind) throws UriValidationException {
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
    case TOP:
      idx = ColumnIndex.top;
      break;
    default:
      throw new UriValidationException("Unsupported option: " + queryOptionKind.toString(),
          UriValidationException.MessageKeys.UNSUPPORTED_QUERY_OPTION, queryOptionKind.toString());
    }

    return idx;
  }

  private RowIndexForUriType rowIndexForUriType(final UriInfo uriInfo) throws UriValidationException {
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
      idx = rowIndexForResourceKind(uriInfo);
      break;
    case service:
      idx = RowIndexForUriType.service;
      break;
    default:
      throw new UriValidationException("Unsupported uriInfo kind: " + uriInfo.getKind(),
          UriValidationException.MessageKeys.UNSUPPORTED_URI_KIND, uriInfo.getKind().toString());
    }

    return idx;
  }

  private RowIndexForUriType rowIndexForResourceKind(final UriInfo uriInfo) throws UriValidationException {
    RowIndexForUriType idx;
    
    final int nonComposableFunctionIndex = getIndexOfLastNonComposableFunction(uriInfo);
    if(nonComposableFunctionIndex != -1 && (uriInfo.getUriResourceParts().size() - 1) > nonComposableFunctionIndex) {
      throw new UriValidationException("Non composable functions followed by further resource parts are not allowed", 
          UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH, 
          uriInfo.getUriResourceParts().get(nonComposableFunctionIndex + 1).getSegmentValue());
    }
    
    int lastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 1;
    UriResource lastPathSegment = uriInfo.getUriResourceParts().get(lastPathSegmentIndex);

    switch (lastPathSegment.getKind()) {
    case count:
      idx = rowIndexForCount(uriInfo);
      break;
    case action:
      idx = rowIndexForAction(lastPathSegment);
      break;
    case complexProperty:
      idx = rowIndexForComplexProperty(lastPathSegment);
      break;
    case entitySet:
    case navigationProperty:
      idx = rowIndexForEntitySet(lastPathSegment);
      break;
    case function:
      if(nonComposableFunctionIndex == -1) {
        idx = rowIndexForFunction(lastPathSegment);
      } else {
        idx = RowIndexForUriType.none;
      }
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
      throw new UriValidationException("Unsupported uriResource kind: " + lastPathSegment.getKind(),
          UriValidationException.MessageKeys.UNSUPPORTED_URI_RESOURCE_KIND, lastPathSegment.getKind().toString());
    }

    return idx;
  }

  private int getIndexOfLastNonComposableFunction(final UriInfo uriInfo) {
    for(int i = 0; i < uriInfo.getUriResourceParts().size(); i++) {
      final UriResource resourcePath = uriInfo.getUriResourceParts().get(i);
      
      if(resourcePath instanceof UriResourceFunction) {
        final UriResourceFunction resourceFuntion = (UriResourceFunction) resourcePath;
        if(!resourceFuntion.getFunction().isComposable()) {
          return i;
        }
      }
    }
    
    return -1;
  }

  private RowIndexForUriType rowIndexForValue(final UriInfo uriInfo) throws UriValidationException {
    RowIndexForUriType idx;
    int secondLastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 2;
    UriResource secondLastPathSegment = uriInfo.getUriResourceParts().get(secondLastPathSegmentIndex);

    switch (secondLastPathSegment.getKind()) {
    case primitiveProperty:
      idx = RowIndexForUriType.propertyPrimitiveValue;
      break;
    case entitySet:
    case navigationProperty:
    case singleton:
      idx = RowIndexForUriType.mediaStream;
      break;
    case function:
      UriResourceFunction uriFunction = (UriResourceFunction) secondLastPathSegment;
      final EdmFunctionImport functionImport = uriFunction.getFunctionImport();
      final EdmFunction function = functionImport == null ?
          uriFunction.getFunction() : functionImport.getUnboundFunctions().get(0);
      idx = function.getReturnType().getType().getKind() == EdmTypeKind.ENTITY ?
          RowIndexForUriType.mediaStream : RowIndexForUriType.propertyPrimitiveValue;
      break;
    default:
      throw new UriValidationException("Unexpected kind in path segment before $value: "
          + secondLastPathSegment.getKind(), UriValidationException.MessageKeys.UNALLOWED_KIND_BEFORE_VALUE,
          secondLastPathSegment.toString());
    }
    return idx;
  }

  private RowIndexForUriType rowIndexForRef(final UriInfo uriInfo, final UriResource lastPathSegment)
      throws UriValidationException {
    int secondLastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 2;
    UriResource secondLastPathSegment = uriInfo.getUriResourceParts().get(secondLastPathSegmentIndex);

    if (secondLastPathSegment instanceof UriResourcePartTyped) {
      return ((UriResourcePartTyped) secondLastPathSegment).isCollection() ?
          RowIndexForUriType.references : RowIndexForUriType.reference;
    } else {
      throw new UriValidationException("secondLastPathSegment not a class of UriResourcePartTyped: "
          + lastPathSegment.getClass(), UriValidationException.MessageKeys.LAST_SEGMENT_NOT_TYPED, lastPathSegment
          .toString());
    }
  }

  private RowIndexForUriType rowIndexForPrimitiveProperty(final UriResource lastPathSegment)
      throws UriValidationException {
    if (lastPathSegment instanceof UriResourcePartTyped) {
      return ((UriResourcePartTyped) lastPathSegment).isCollection() ?
          RowIndexForUriType.propertyPrimitiveCollection : RowIndexForUriType.propertyPrimitive;
    } else {
      throw new UriValidationException("lastPathSegment not a class of UriResourcePartTyped: "
          + lastPathSegment.getClass(), UriValidationException.MessageKeys.LAST_SEGMENT_NOT_TYPED, lastPathSegment
          .toString());
    }
  }

  private RowIndexForUriType rowIndexForFunction(final UriResource lastPathSegment) throws UriValidationException {
    RowIndexForUriType idx;
    UriResourceFunction urf = (UriResourceFunction) lastPathSegment;
    EdmReturnType rt = urf.getFunction().getReturnType();

    if(!urf.getFunction().isComposable()) {
      return RowIndexForUriType.none;
    }
    
    
    switch (rt.getType().getKind()) {
    case ENTITY:
      idx = rt.isCollection() && urf.getKeyPredicates().isEmpty() ?
          RowIndexForUriType.entitySet : RowIndexForUriType.entity;
      break;
    case PRIMITIVE:
    case ENUM:
    case DEFINITION:
      idx = rt.isCollection() ? RowIndexForUriType.propertyPrimitiveCollection : RowIndexForUriType.propertyPrimitive;
      break;
    case COMPLEX:
      idx = rt.isCollection() ? RowIndexForUriType.propertyComplexCollection : RowIndexForUriType.propertyComplex;
      break;
    default:
      throw new UriValidationException("Unsupported function return type: " + rt.getType().getKind(),
          UriValidationException.MessageKeys.UNSUPPORTED_FUNCTION_RETURN_TYPE,
          rt.getType().getKind().toString());
    }

    return idx;
  }

  private RowIndexForUriType rowIndexForEntitySet(final UriResource lastPathSegment) throws UriValidationException {
    if (lastPathSegment instanceof UriResourcePartTyped) {
      return ((UriResourcePartTyped) lastPathSegment).isCollection() ?
          RowIndexForUriType.entitySet : RowIndexForUriType.entity;
    } else {
      throw new UriValidationException("lastPathSegment not a class of UriResourcePartTyped: "
          + lastPathSegment.getClass(), UriValidationException.MessageKeys.LAST_SEGMENT_NOT_TYPED,
          lastPathSegment.toString());
    }
  }

  private RowIndexForUriType rowIndexForComplexProperty(final UriResource lastPathSegment)
      throws UriValidationException {
    if (lastPathSegment instanceof UriResourcePartTyped) {
      return ((UriResourcePartTyped) lastPathSegment).isCollection() ?
          RowIndexForUriType.propertyComplexCollection : RowIndexForUriType.propertyComplex;
    } else {
      throw new UriValidationException("lastPathSegment not a class of UriResourcePartTyped: "
          + lastPathSegment.getClass(), UriValidationException.MessageKeys.LAST_SEGMENT_NOT_TYPED,
          lastPathSegment.toString());
    }
  }

  private RowIndexForUriType rowIndexForAction(final UriResource lastPathSegment) throws UriValidationException {
    final EdmReturnType rt = ((UriResourceAction) lastPathSegment).getAction().getReturnType();
    if (rt == null) {
      return RowIndexForUriType.none;
    }
    RowIndexForUriType idx;
    switch (rt.getType().getKind()) {
    case ENTITY:
      idx = rt.isCollection() ? RowIndexForUriType.entitySet : RowIndexForUriType.entity;
      break;
    case PRIMITIVE:
    case ENUM:
    case DEFINITION:
      idx = rt.isCollection() ? RowIndexForUriType.propertyPrimitiveCollection : RowIndexForUriType.propertyPrimitive;
      break;
    case COMPLEX:
      idx = rt.isCollection() ? RowIndexForUriType.propertyComplexCollection : RowIndexForUriType.propertyComplex;
      break;
    default:
      throw new UriValidationException("Unsupported action return type: " + rt.getType().getKind(),
          UriValidationException.MessageKeys.UNSUPPORTED_ACTION_RETURN_TYPE, rt.getType().getKind().toString());
    }
    return idx;
  }

  private RowIndexForUriType rowIndexForCount(final UriInfo uriInfo) throws UriValidationException {
    RowIndexForUriType idx;
    int secondLastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 2;
    UriResource secondLastPathSegment = uriInfo.getUriResourceParts().get(secondLastPathSegmentIndex);
    switch (secondLastPathSegment.getKind()) {
    case entitySet:
    case navigationProperty:
      idx = RowIndexForUriType.entitySetCount;
      break;
    case complexProperty:
      idx = RowIndexForUriType.propertyComplexCollectionCount;
      break;
    case primitiveProperty:
      idx = RowIndexForUriType.propertyPrimitiveCollectionCount;
      break;
    case function:
      final UriResourceFunction uriFunction = (UriResourceFunction) secondLastPathSegment;
      final EdmFunctionImport functionImport = uriFunction.getFunctionImport();
      final EdmFunction function = functionImport == null ?
          uriFunction.getFunction() : functionImport.getUnboundFunctions().get(0);
      final EdmType returnType = function.getReturnType().getType();
      switch (returnType.getKind()) {
      case ENTITY:
        idx = RowIndexForUriType.entitySetCount;
        break;
      case COMPLEX:
        idx = RowIndexForUriType.propertyComplexCollectionCount;
        break;
      case PRIMITIVE:
      case ENUM:
      case DEFINITION:
        idx = RowIndexForUriType.propertyPrimitiveCollectionCount;
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

    return idx;
  }

  private void validateQueryOptions(final UriInfo uriInfo) throws UriValidationException {
    RowIndexForUriType row = rowIndexForUriType(uriInfo);

    for (SystemQueryOption option : uriInfo.getSystemQueryOptions()) {
      ColumnIndex col = colIndex(option.getKind());

      if (!decisionMatrix[row.getIndex()][col.getIndex()]) {
        throw new UriValidationException("System query option not allowed: " + option.getName(),
            UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED, option.getName());
      }
    }
  }

  private void validateForHttpMethod(final UriInfo uriInfo, final HttpMethod httpMethod) throws UriValidationException {
    switch (httpMethod) {
    case POST:
      if (!isAction(uriInfo)) {
        // POST and SystemQueryOptions only allowed if addressed resource is an action
        validateNoQueryOptionsForHttpMethod(uriInfo, httpMethod);
      }
      break;
    case DELETE:
      if (!isReferences(uriInfo)) {
        // DELETE and SystemQueryOptions only allowed if addressed resource is a reference collection
        validateNoQueryOptionsForHttpMethod(uriInfo, httpMethod);
      } else {
        // Only $id allowed as SystemQueryOption for DELETE and references
        for (SystemQueryOption option : uriInfo.getSystemQueryOptions()) {
          if (SystemQueryOptionKind.ID != option.getKind()) {
            throw new UriValidationException(
                "System query option " + option.getName() + " not allowed for method " + httpMethod,
                UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD,
                option.getName(), httpMethod.toString());
          }
        }
      }
      break;
    case PUT:
    case PATCH:
      // PUT and PATCH do not allow system query options
      validateNoQueryOptionsForHttpMethod(uriInfo, httpMethod);
      break;
    default:
      throw new UriValidationException("HTTP method not supported: " + httpMethod,
          UriValidationException.MessageKeys.UNSUPPORTED_HTTP_METHOD, httpMethod.toString());
    }

  }

  private boolean isReferences(final UriInfo uriInfo) {
    if (!uriInfo.getSystemQueryOptions().isEmpty()) {
      List<UriResource> uriResourceParts = uriInfo.getUriResourceParts();
      if (UriResourceKind.ref == uriResourceParts.get(uriResourceParts.size() - 1).getKind()) {
        UriResourcePartTyped previousSegment = (UriResourcePartTyped) uriResourceParts.get(uriResourceParts.size() - 2);
        return previousSegment.isCollection();
      }
    }
    return false;
  }

  private void validateNoQueryOptionsForHttpMethod(final UriInfo uriInfo, final HttpMethod httpMethod)
      throws UriValidationException {
    if (!uriInfo.getSystemQueryOptions().isEmpty()) {
      StringBuilder options = new StringBuilder();
      for (SystemQueryOption option : uriInfo.getSystemQueryOptions()) {
        options.append(option.getName()).append(" ");
      }
      throw new UriValidationException("System query option " + options.toString() + " not allowed for method "
          + httpMethod, UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD,
          options.toString(), httpMethod.toString());
    }
  }

  private boolean isAction(final UriInfo uriInfo) {
    List<UriResource> uriResourceParts = uriInfo.getUriResourceParts();
    if (uriResourceParts.isEmpty()) {
      return false;
    }
    return UriResourceKind.action == uriResourceParts.get(uriResourceParts.size() - 1).getKind();
  }

  private void validateParameters(final UriInfo uriInfo) throws UriValidationException {
    for (UriResource pathSegment : uriInfo.getUriResourceParts()) {
      final boolean isFunction = pathSegment.getKind() == UriResourceKind.function;
      
      if(isFunction) {
        final UriResourceFunction functionPathSegement = (UriResourceFunction) pathSegment;
        final EdmFunction edmFuntion = functionPathSegement.getFunction();
        
        final Map<String, UriParameter> parameters = new HashMap<String, UriParameter>();
        for(final UriParameter parameter : functionPathSegement.getParameters()) {
          parameters.put(parameter.getName(), parameter);
        }
        
        boolean firstParameter = true;
        for(final String parameterName : edmFuntion.getParameterNames()) {
          final UriParameter parameter = parameters.get(parameterName);
          final boolean isNullable = edmFuntion.getParameter(parameterName).isNullable();
          
          if(parameter != null) {
            /** No alias, value explicit null */
            if(parameter.getText() == null 
                && parameter.getAlias() == null && !isNullable) {
              throw new UriValidationException("Missing non nullable parameter " + parameterName, 
                  UriValidationException.MessageKeys.MISSING_PARAMETER, parameterName);
            } else if(parameter.getText() == null && parameter.getAlias() != null) {
              final String valueForAlias = uriInfo.getValueForAlias(parameter.getAlias());
              /** Alias value is missing or explicit null **/
              if(valueForAlias == null && !isNullable) {
                throw new UriValidationException("Missing non nullable parameter " + parameterName, 
                    UriValidationException.MessageKeys.MISSING_PARAMETER, parameterName);
              }
            }
            
            parameters.remove(parameterName);
          } else if(!isNullable && !(firstParameter && edmFuntion.isBound())) {
            // The first parameter of bound functions is implicit provided by the preceding path segment
            throw new UriValidationException("Missing non nullable parameter " + parameterName, 
                UriValidationException.MessageKeys.MISSING_PARAMETER, parameterName);
          }
          
          firstParameter = false;
        }
        
        if(!parameters.isEmpty()) {
          final String parameterName = parameters.keySet().iterator().next();
          throw new UriValidationException("Unsupported parameter " + parameterName, 
              UriValidationException.MessageKeys.UNSUPPORTED_PARAMETER, parameterName);
        }
      }
    }
  }
  
  private void validateKeyPredicates(final UriInfo uriInfo) throws UriValidationException {
    for (UriResource pathSegment : uriInfo.getUriResourceParts()) {
      final boolean isEntitySet = pathSegment.getKind() == UriResourceKind.entitySet;
      final boolean isEntityColFunction = isEntityColFunction(pathSegment);
      
      if (isEntitySet || pathSegment.getKind() == UriResourceKind.navigationProperty || isEntityColFunction) {
        final List<UriParameter> keyPredicates = isEntitySet ?
            ((UriResourceEntitySet) pathSegment).getKeyPredicates() :
              isEntityColFunction ? ((UriResourceFunction) pathSegment).getKeyPredicates()
              : ((UriResourceNavigation) pathSegment).getKeyPredicates();
            
        if (keyPredicates != null) {

          final EdmEntityType entityType = isEntitySet ?
              ((UriResourceEntitySet) pathSegment).getEntityType() :
              isEntityColFunction ? (EdmEntityType) ((UriResourceFunction) pathSegment).getType() 
              : (EdmEntityType) ((UriResourceNavigation) pathSegment).getType();
          final List<String> keyPredicateNames = entityType.getKeyPredicateNames();
          Map<String, EdmKeyPropertyRef> edmKeys = new HashMap<String, EdmKeyPropertyRef>();
          for (EdmKeyPropertyRef key : entityType.getKeyPropertyRefs()) {
            edmKeys.put(key.getName(), key);
            final String alias = key.getAlias();
            if (alias != null) {
              edmKeys.put(alias, key);
            }
          }

          for (UriParameter keyPredicate : keyPredicates) {
            final String name = keyPredicate.getName();
            final String alias = keyPredicate.getAlias();

            if (keyPredicate.getReferencedProperty() == null) {
              final String value = alias == null ?
                  keyPredicate.getText() :
                  uriInfo.getValueForAlias(alias);

              EdmKeyPropertyRef edmKey = edmKeys.get(name);
              if (edmKey == null) {
                if (keyPredicateNames.contains(name)) {
                  throw new UriValidationException("Double key property: " + name,
                      UriValidationException.MessageKeys.DOUBLE_KEY_PROPERTY, name);
                } else {
                  throw new UriValidationException("Unknown key property: " + name,
                      UriValidationException.MessageKeys.INVALID_KEY_PROPERTY, name);
                }
              }

              final EdmProperty property = edmKey.getProperty();
              final EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) property.getType();
              try {
                if (!edmPrimitiveType.validate(edmPrimitiveType.fromUriLiteral(value),
                    property.isNullable(), property.getMaxLength(),
                    property.getPrecision(), property.getScale(), property.isUnicode())) {
                  throw new UriValidationException("PrimitiveTypeException",
                      UriValidationException.MessageKeys.INVALID_KEY_PROPERTY, name);
                }
              } catch (EdmPrimitiveTypeException e) {
                throw new UriValidationException("PrimitiveTypeException", e,
                    UriValidationException.MessageKeys.INVALID_KEY_PROPERTY, name);
              }
            }

            edmKeys.remove(name);
            edmKeys.remove(alias);
          }
        }
      }
    }
  }

  private boolean isEntityColFunction(final UriResource pathSegment) {
    if(pathSegment.getKind() == UriResourceKind.function) {
      final UriResourceFunction resourceFunction = (UriResourceFunction) pathSegment;
      final EdmReturnType returnType = resourceFunction.getFunction().getReturnType();
      
      return returnType.isCollection() && returnType.getType().getKind() == EdmTypeKind.ENTITY;
    } else {
      return false;
    }
  }
  
  private void validatePropertyOperations(final UriInfo uriInfo, final HttpMethod method)
      throws UriValidationException {
    final List<UriResource> parts = uriInfo.getUriResourceParts();
    final UriResource last = parts.size() > 0 ? parts.get(parts.size() - 1) : null;
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
