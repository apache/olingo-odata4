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

import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.ExpressionVisitorImpl;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operand.TypedOperand;

public class OrderByHandler {
  public static void applyOrderByOption(final OrderByOption orderByOption, final EntityCollection entitySet,
      final UriInfoResource uriInfo, final Edm edm) throws ODataApplicationException {

    if (orderByOption == null) {
      return;
    }

    try {
      applyOrderByOptionInternal(orderByOption, entitySet, uriInfo, edm);
    } catch (SystemQueryOptionsRuntimeException e) {
      if (e.getCause() instanceof ODataApplicationException) {
        // Throw the nested exception, to send the correct HTTP status code in the HTTP response
        throw (ODataApplicationException) e.getCause();
      } else {
        throw new ODataApplicationException("Exception in orderBy evaluation",
            HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
      }
    }
  }

  private static void applyOrderByOptionInternal(final OrderByOption orderByOption, final EntityCollection entitySet,
      final UriInfoResource uriInfo, final Edm edm) throws ODataApplicationException {
    Collections.sort(entitySet.getEntities(), new Comparator<Entity>() {
      @Override
      @SuppressWarnings("unchecked")
      public int compare(final Entity e1, final Entity e2) {
        // Evaluate the first order option for both entities.
        // If and only if the result of the previous order option is equal to 0
        // evaluate the next order option until all options are evaluated or they are not equal.
        int result = 0;

        for (int i = 0; i < orderByOption.getOrders().size() && result == 0; i++) {
          try {
            final OrderByItem item = orderByOption.getOrders().get(i);
            final TypedOperand op1 =
                item.getExpression().accept(new ExpressionVisitorImpl(e1, uriInfo, edm)).asTypedOperand();
            final TypedOperand op2 =
                item.getExpression().accept(new ExpressionVisitorImpl(e2, uriInfo, edm)).asTypedOperand();

            if (op1.isNull() || op2.isNull()) {
              if (op1.isNull() && op2.isNull()) {
                result = 0; // null is equals to null
              } else {
                result = op1.isNull() ? -1 : 1;
              }
            } else {
              Object o1 = op1.getValue();
              Object o2 = op2.getValue();

              if (o1.getClass() == o2.getClass() && o1 instanceof Comparable) {
                result = ((Comparable<Object>) o1).compareTo(o2);
              } else {
                result = 0;
              }
            }

            result = item.isDescending() ? result * -1 : result;
          } catch (ODataApplicationException e) {
            throw new SystemQueryOptionsRuntimeException(e);
          } catch (ExpressionVisitException e) {
            throw new SystemQueryOptionsRuntimeException(e);
          }
        }
        return result;
      }
    });
  }
}
