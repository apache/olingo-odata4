/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.edm.xml;

import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmItem;
import org.apache.olingo.commons.api.edm.provider.annotation.AnnotationExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.ConstantAnnotationExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.DynamicAnnotationExpression;

abstract class AbstractClientAnnotationExpression extends CsdlAbstractEdmItem implements AnnotationExpression {

  private static final long serialVersionUID = -4238652997159205377L;

  @Override
  public boolean isConstant() {
    return this instanceof ConstantAnnotationExpression;
  }

  @Override
  public ConstantAnnotationExpression asConstant() {
    return isConstant() ? (ConstantAnnotationExpression) this : null;
  }

  @Override
  public boolean isDynamic() {
    return this instanceof DynamicAnnotationExpression;
  }

  @Override
  public DynamicAnnotationExpression asDynamic() {
    return isDynamic() ? (DynamicAnnotationExpression) this : null;
  }
}
