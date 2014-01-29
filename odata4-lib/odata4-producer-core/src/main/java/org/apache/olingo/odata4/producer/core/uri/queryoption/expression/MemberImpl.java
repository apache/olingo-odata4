/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.producer.core.uri.queryoption.expression;

import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.exception.ODataApplicationException;
import org.apache.olingo.odata4.producer.api.uri.UriInfoResource;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.Member;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.VisitableExression;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.UriResourceImplKeyPred;
import org.apache.olingo.odata4.producer.core.uri.UriResourceImplTyped;
import org.apache.olingo.odata4.producer.core.uri.UriResourcePartImpl;

public class MemberImpl extends ExpressionImpl implements Member, VisitableExression {

  private UriInfoResource path;

  @Override
  public UriInfoResource getPath() {
    return path;
  }

  public Member setPath(final UriInfoResource pathSegments) {
    path = pathSegments;
    return this;
  }

  @Override
  public <T> T accept(final ExpressionVisitor<T> visitor) throws ExceptionVisitExpression, ODataApplicationException {
    return visitor.visitMember(path);
  }

  @Override
  public EdmType getType() {

    UriInfoImpl uriInfo = (UriInfoImpl) path;
    UriResourcePartImpl lastResourcePart = (UriResourcePartImpl) uriInfo.getLastResourcePart();

    if (lastResourcePart instanceof UriResourceImplKeyPred) {
      UriResourceImplKeyPred lastKeyPred = (UriResourceImplKeyPred) lastResourcePart;
      if (lastKeyPred.getTypeFilterOnEntry() != null) {
        return lastKeyPred.getTypeFilterOnEntry();
      } else if (lastKeyPred.getTypeFilterOnCollection() != null) {
        return lastKeyPred.getTypeFilterOnCollection();
      }
      return lastKeyPred.getType();
    } else if (lastResourcePart instanceof UriResourceImplTyped) {
      UriResourceImplTyped lastTyped = (UriResourceImplTyped) lastResourcePart;
      EdmType type = lastTyped.getTypeFilter();
      if (type != null) {
        return type;
      }
      return lastTyped.getType();
    }
    return null;
  }

  @Override
  public boolean isCollection() {
    UriInfoImpl uriInfo = (UriInfoImpl) path;
    UriResourcePartImpl lastResourcePart = (UriResourcePartImpl) uriInfo.getLastResourcePart();
    if (lastResourcePart instanceof UriResourceImplTyped) {
      UriResourceImplTyped lastTyped = (UriResourceImplTyped) lastResourcePart;
      return lastTyped.isCollection();
    }

    return false;
  }

}
