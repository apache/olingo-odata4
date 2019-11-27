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

import java.util.Calendar;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

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

public class SearchHandler {

  public static void applySearchSystemQueryOption(final SearchOption searchOption, EntityCollection entitySet)
      throws ODataApplicationException {
    if (searchOption != null) {
      SearchExpression se = searchOption.getSearchExpression();
      Iterator<Entity> it = entitySet.getEntities().iterator();
      while (it.hasNext()) {
        boolean keep = false;
        Entity entity = it.next();
        keep = isTrue(se, entity);
        if (!keep) {
          it.remove();
        }
      }
    }
  }

  private static boolean isTrue(final SearchTerm term, final Property property) {
    if (property.isNull()) {
      return false;
    } else if (property.isPrimitive()) {
      if (property.isCollection()) {
        for (final Object primitive : property.asCollection()) {
          final String propertyString = asString(primitive);
          if (propertyString != null && propertyString.contains(term.getSearchTerm())) {
            return true;
          }
        }
        return false;
      } else {
        final String propertyString = asString(property.asPrimitive());
        return propertyString != null && propertyString.contains(term.getSearchTerm());
      }
    } else if (property.isComplex()) {
      if (property.isCollection()) {
        for (final Object member : property.asCollection()) {
          if (isTrue(term, (Property) member)) {
            return true;
          }
        }
        return false;
      } else {
        for (final Property innerProperty : property.asComplex().getValue()) {
          if (isTrue(term, innerProperty)) {
            return true;
          }
        }
        return false;
      }
    } else {
      return false;
    }
  }

  private static String asString(final Object primitive) {
    // TODO: improve 'string' conversion; maybe consider only String properties
    if (primitive instanceof String) {
      return (String) primitive;
    } else if (primitive instanceof Calendar) {
      return DatatypeConverter.printDateTime((Calendar) primitive);
    } else if (primitive instanceof byte[]) {
      return DatatypeConverter.printBase64Binary((byte[]) primitive);
    } else {
      return primitive.toString();
    }
  }

  private static boolean isTrue(final SearchBinary binary, final Entity entity) throws ODataApplicationException {
    SearchExpression left = binary.getLeftOperand();
    SearchExpression right = binary.getRightOperand();
    if (binary.getOperator() == SearchBinaryOperatorKind.AND) {
      if (left.isSearchBinary() && right.isSearchBinary()) {
        return isTrue(left, entity) && isTrue(right, entity);
      } else if (left.isSearchUnary() && right.isSearchBinary()) {
        return isTrue(left, entity) && isTrue(right, entity);
      } else if (left.isSearchBinary() && right.isSearchUnary()) {
        return isTrue(left, entity) && isTrue(right, entity);
      } else if (left.isSearchUnary() && right.isSearchUnary()) {
        return isTrue(left, entity) && isTrue(right, entity);
      }
      ListIterator<Property> properties = entity.getProperties().listIterator();
      boolean leftValid = false;
      boolean rightValid = false;
      while (properties.hasNext()) {
        Property property = properties.next();
        if (!leftValid) {
          leftValid = isTrue(left, property);
        }
        if (!rightValid) {
          rightValid = isTrue(right, property);
        }
      }
      return leftValid && rightValid;
    } else if (binary.getOperator() == SearchBinaryOperatorKind.OR) {
      if (left.isSearchBinary() && right.isSearchBinary()) {
        return isTrue(left, entity) || isTrue(right, entity);
      } else if (left.isSearchUnary() && right.isSearchBinary()) {
        return isTrue(left, entity) || isTrue(right, entity);
      } else if (left.isSearchBinary() && right.isSearchUnary()) {
        return isTrue(left, entity) || isTrue(right, entity);
      } else if (left.isSearchUnary() && right.isSearchUnary()) {
        return isTrue(left, entity) || isTrue(right, entity);
      }
      ListIterator<Property> properties = entity.getProperties().listIterator();
      boolean leftValid = false;
      boolean rightValid = false;
      while (properties.hasNext()) {
        Property property = properties.next();
        if (!leftValid) {
          leftValid = isTrue(left, property);
        }
        if (!rightValid) {
          rightValid = isTrue(right, property);
        }
      }
      return leftValid || rightValid;
    } else {
      throw new ODataApplicationException("Found unknown SearchBinaryOperatorKind: " + binary.getOperator(),
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }
  }

  private static boolean isTrue(final SearchBinary binary, final Property property) throws ODataApplicationException {
    SearchExpression left = binary.getLeftOperand();
    SearchExpression right = binary.getRightOperand();
    if (binary.getOperator() == SearchBinaryOperatorKind.AND) {
      return isTrue(left, property) && isTrue(right, property);
    } else if (binary.getOperator() == SearchBinaryOperatorKind.OR) {
      return isTrue(left, property) || isTrue(right, property);
    } else {
      throw new ODataApplicationException("Found unknown SearchBinaryOperatorKind: " + binary.getOperator(),
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }
  }
  
  private static boolean isTrue(final SearchExpression searchExpression, final Entity entity)
      throws ODataApplicationException {
    if (searchExpression.isSearchBinary()) {
      return isTrue(searchExpression.asSearchBinary(), entity);
    } else if (searchExpression.isSearchTerm()) {
      ListIterator<Property> properties = entity.getProperties().listIterator();
      boolean keep = false;
      while (properties.hasNext()) {
        Property property = properties.next();
        if (!keep) {
          keep = isTrue(searchExpression.asSearchTerm(), property);
        }
      }
      return keep;
    } else if (searchExpression.isSearchUnary()) {
      return !isTrue(searchExpression.asSearchUnary().getOperand(), entity);
    }
    throw new ODataApplicationException("Found unknown SearchExpression: " + searchExpression,
        HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
  }
  
  private static boolean isTrue(final SearchExpression searchExpression, final Property property)
      throws ODataApplicationException {
    if (searchExpression.isSearchBinary()) {
      return isTrue(searchExpression.asSearchBinary(), property);
    } else if (searchExpression.isSearchTerm()) {
      return isTrue(searchExpression.asSearchTerm(), property);
    } else if (searchExpression.isSearchUnary()) {
      return !isTrue(searchExpression.asSearchUnary().getOperand(), property);
    }
    throw new ODataApplicationException("Found unknown SearchExpression: " + searchExpression,
        HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
  }
}
