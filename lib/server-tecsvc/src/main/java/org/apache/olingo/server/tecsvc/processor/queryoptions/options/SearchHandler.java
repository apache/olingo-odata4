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
package org.apache.olingo.server.tecsvc.processor.queryoptions.options;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.search.SearchBinary;
import org.apache.olingo.server.api.uri.queryoption.search.SearchBinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;
import org.apache.olingo.server.api.uri.queryoption.search.SearchTerm;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SearchHandler {
  public static void applySearchSystemQueryOption(final SearchOption searchOption, final EntityCollection entitySet)
      throws ODataApplicationException {

    if (searchOption != null) {
      Iterator<Entity> it = entitySet.getEntities().iterator();
      while(it.hasNext()) {
        boolean keep = false;
        Entity next = it.next();
        List<Property> propertyList = next.getProperties();
        for (Property property : propertyList) {
          SearchExpression se = searchOption.getSearchExpression();
          if(isTrue(se, property)) {
            keep = true;
            break;
          }
        }
        if(!keep) {
          it.remove();
        }
      }
    }
  }

  private static boolean isTrue(SearchTerm term, Property property) {
    if(property.isPrimitive() && !property.isNull()) {
      // TODO: mibo(151117): pass EDM information to do correct 'string' convertation
      String propertyString = property.asPrimitive().toString();
      return propertyString != null && propertyString.contains(term.getSearchTerm());
    }
    return false;
  }

  private static boolean isTrue(SearchBinary binary, Property property) throws ODataApplicationException {
    SearchExpression left = binary.getLeftOperand();
    SearchExpression right = binary.getRightOperand();
    if(binary.getOperator() == SearchBinaryOperatorKind.AND) {
      return isTrue(left, property) && isTrue(right, property);
    } else if(binary.getOperator() == SearchBinaryOperatorKind.OR) {
      return isTrue(left, property) || isTrue(right, property);
    } else {
      throw new ODataApplicationException("Found unknown SearchBinaryOperatorKind: " + binary.getOperator(),
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }
  }

  private static boolean isTrue(SearchExpression searchExpression, Property property) throws ODataApplicationException {
    if(searchExpression.isSearchBinary()) {
      return isTrue(searchExpression.asSearchBinary(), property);
    } else if(searchExpression.isSearchTerm()) {
      return isTrue(searchExpression.asSearchTerm(), property);
    } else if(searchExpression.isSearchUnary()) {
      return !isTrue(searchExpression.asSearchUnary().getOperand(), property);
    }
    throw new ODataApplicationException("Found unknown SearchExpression: " + searchExpression,
        HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
  }
}
