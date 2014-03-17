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

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;

public class SystemQueryValidator {

  //@formatter:off (Eclipse formatter)
  //CHECKSTYLE:OFF (Maven checkstyle)
  private boolean[][] decisionMatrix =
      {
          /*                                       FILTER FORMAT EXPAND ID     COUNT  ORDERBY SEARCH SELECT SKIP   SKIPTOKEN LEVELS TOP */
          /*                              all */ { true , true , true , false, true , true ,  true , true , true , true ,    true , false },
          /*                            batch */ { false, false, false, false, false, false,  false, false, false, false,    false, false },
          /*                        crossjoin */ { true , true , true , false, true , true ,  true , true , true , true ,    true , true  },
          /*                         entityId */ { false, true , true , true , false, false,  false, true , false, false,    true , false },
          /*                         metadata */ { false, true , false, false, false, false,  false, false, false, false,    false, false },
          /*                         resource */ { false, true , false, false, false, false,  false, false, false, false,    false, false },
          /*                          service */ { false, true , false, false, false, false,  false, false, false, false,    false, false },
          /*                        entitySet */ { true , true , true , false, true , true ,  true , true , true , true ,    true , true  },
          /*                   entitySetCount */ { false, false, false, false, false, false,  false, false, false, false,    false, false },
          /*                           entity */ { false, true , true , false, false, false,  false, true , false, false,    true , false },
          /*                      mediaStream */ { false, true , false, false, false, false,  false, false, false, false,    false, false },
          /*                       references */ { true , true , false, false, false, true ,  true , false, true , true ,    false, true  },
          /*                        reference */ { false, true , false, false, false, false,  false, false, false, false,    false, false },
          /*                  propertyComplex */ { false, true , true , false, false, false,  false, true , false, false,    true , false },
          /*        propertyComplexCollection */ { true , true , true , false, true , true ,  false, false, true , true ,    true , true  },
          /*   propertyComplexCollectionCount */ { false, false, false, false, false, false,  false, false, false, false,    false, false },
          /*                propertyPrimitive */ { false, true , false, false, false, false,  false, false, false, false,    false, false },
          /*      propertyPrimitiveCollection */ { true , true , false, false, false, true ,  false, false, true , true ,    false, true  },
          /* propertyPrimitiveCollectionCount */ { false, false, false, false, false, false,  false, false, false, false,    false, false },
          /*           propertyPrimitiveValue */ { false, true , false, false, false, false,  false, false, false, false,    false, false },          
      };
  //CHECKSTYLE:ON
  //@formatter:on

  public void validate(final UriInfo uriInfo, final Edm edm) throws UriValidationException {

    validateQueryOptions(uriInfo);
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
      throw new ODataRuntimeException("Unsupported Option: " + queryOptionKind);
    }

    return idx;
  }

  private int rowIndex(final UriInfo uriInfo) {
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
      idx = 5;
      break;
    case service:
      idx = 6;
      break;
    default:
      throw new ODataRuntimeException("Unsupported Option: " + uriInfo.getKind());
    }

    return idx;
  }

  private void validateKeyPredicateTypes(final UriInfo uriInfo, final Edm edm) throws UriValidationException {

  }

  private void validateQueryOptions(final UriInfo uriInfo) throws UriValidationException {
    int row = rowIndex(uriInfo);

    for (SystemQueryOption option : uriInfo.getSystemQueryOptions()) {
      int col = colIndex(option.getKind());
      if (!decisionMatrix[row][col]) {
        throw new UriValidationException("Unsupported System Query Option for Uri Type: " + option.getName());
      }
    }

  }

}
