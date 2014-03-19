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

import java.util.List;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;

public class SystemQueryValidator {

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

          /*                   entitySetCount  8 */ { false,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          
          /*                           entity  9 */ { false,   true ,   true ,   false,   false,   false,    false,   true ,   false,   false,      true ,    false },
          /*                      mediaStream 10 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*                       references 11 */ { true ,   true ,   false,   false,   false,   true ,    true ,   false,   true ,   true ,      false,    true  },
          /*                        reference 12 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*                  propertyComplex 13 */ { false,   true ,   true ,   false,   false,   false,    false,   true ,   false,   false,      true ,    false },
          /*        propertyComplexCollection 14 */ { true ,   true ,   true ,   false,   true ,   true ,    false,   false,   true ,   true ,      true ,    true  },
          /*   propertyComplexCollectionCount 15 */ { false,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*                propertyPrimitive 16 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*      propertyPrimitiveCollection 17 */ { true ,   true ,   false,   false,   false,   true ,    false,   false,   true ,   true ,      false,    true  },
          /* propertyPrimitiveCollectionCount 18 */ { false,   false,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },
          /*           propertyPrimitiveValue 19 */ { false,   true ,   false,   false,   false,   false,    false,   false,   false,   false,      false,    false },          
      };
  //CHECKSTYLE:ON
  //@formatter:on

  public void validate(final UriInfo uriInfo, final Edm edm) throws UriValidationException {

    validateQueryOptions(uriInfo, edm);
    validateKeyPredicateTypes(uriInfo, edm);

  }

  private int colIndex(SystemQueryOptionKind queryOptionKind) {
    int idx;
    switch (queryOptionKind) {
    case FILTER:
      idx = 0;
      break;
    case FORMAT:
      idx = 1;
      break;
    case EXPAND:
      idx = 2;
      break;
    case ID:
      idx = 3;
      break;
    case COUNT:
      idx = 4;
      break;
    case ORDERBY:
      idx = 5;
      break;
    case SEARCH:
      idx = 6;
      break;
    case SELECT:
      idx = 7;
      break;
    case SKIP:
      idx = 8;
      break;
    case SKIPTOKEN:
      idx = 9;
      break;
    case LEVELS:
      idx = 10;
      break;
    case TOP:
      idx = 11;
      break;
    default:
      throw new ODataRuntimeException("Unsupported option: " + queryOptionKind);
    }

    return idx;
  }

  private int rowIndex(final UriInfo uriInfo, Edm edm) throws UriValidationException {
    int idx;

    switch (uriInfo.getKind()) {
    case all:
      idx = 0;
      break;
    case batch:
      idx = 1;
      break;
    case crossjoin:
      idx = 2;
      break;
    case entityId:
      idx = 3;
      break;
    case metadata:
      idx = 4;
      break;
    case resource:
      idx = rowIndexForResourceKind(uriInfo, edm);
      break;
    case service:
      idx = 6;
      break;
    default:
      throw new ODataRuntimeException("Unsupported uriInfo kind: " + uriInfo.getKind());
    }

    return idx;
  }

  private int rowIndexForResourceKind(UriInfo uriInfo, Edm edm) throws UriValidationException {
    int idx = 5;

    UriResource lastPathSegemnt = uriInfo.getUriResourceLastPart();

    switch (lastPathSegemnt.getKind()) {
    case count:
      List<UriResource> parts = uriInfo.getUriResourceParts();
      UriResource secondLastPart = parts.get(parts.size() - 2);
      switch (secondLastPart.getKind()) {
      case entitySet:
        idx = 8;
        break;
        default : throw new UriValidationException("Illegal path part kind: " + lastPathSegemnt.getKind());
      }
      break;
    case action:
      break;
    case complexProperty:
      break;
    case entitySet:
      idx = 7;
      break;
    case function:
      break;
    case it:
      break;
    case lambdaAll:
      break;
    case lambdaAny:
      break;
    case lambdaVariable:
      break;
    case navigationProperty:
      break;
    case primitiveProperty:
      break;
    case ref:
      break;
    case root:
      break;
    case singleton:
      break;
    case value:
      break;
    default:
      throw new ODataRuntimeException("Unsupported uriResource kind: " + lastPathSegemnt.getKind());
    }

    return idx;
  }

  private void validateQueryOptions(final UriInfo uriInfo, Edm edm) throws UriValidationException {
    try {
      int row = rowIndex(uriInfo, edm);

      for (SystemQueryOption option : uriInfo.getSystemQueryOptions()) {
        int col = colIndex(option.getKind());

        System.out.print("[" + row + "][" + col + "]");

        if (!decisionMatrix[row][col]) {
          throw new UriValidationException("System query option not allowed: " + option.getName());
        }
      }
    } finally {
      System.out.println();
    }

  }

  private void validateKeyPredicateTypes(final UriInfo uriInfo, final Edm edm) throws UriValidationException {}

}
