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
package org.apache.olingo.commons.core.edm.annotation;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmUrlRef;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlUrlRef;

public class EdmUrlRefImpl extends AbstractEdmAnnotatableDynamicExpression implements EdmUrlRef {

  private final CsdlUrlRef csdlExp;
  private EdmExpression value;

  public EdmUrlRefImpl(Edm edm, CsdlUrlRef csdlExp) {
    super(edm, "UrlRef", csdlExp);
    this.csdlExp = csdlExp;
  }

  @Override
  public EdmExpression getValue() {
    if (value == null) {
      if (csdlExp.getValue() == null) {
        throw new EdmException("URLRef expressions require an expression value.");
      }
      value = getExpression(edm, csdlExp.getValue());
    }
    return value;
  }
  
  @Override
  public EdmExpressionType getExpressionType() {
    return EdmExpressionType.UrlRef;
  }
}
