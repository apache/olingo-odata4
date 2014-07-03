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

import java.util.List;

import org.apache.olingo.commons.api.edm.annotation.EdmAnnotationExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmApply;

public class EdmApplyImpl extends AbstractEdmAnnotatableDynamicAnnotationExpression implements EdmApply {

  private final String function;

  private final List<EdmAnnotationExpression> parameters;

  public EdmApplyImpl(final String function, final List<EdmAnnotationExpression> parameters) {
    this.function = function;
    this.parameters = parameters;
  }

  @Override
  public String getFunction() {
    return function;
  }

  @Override
  public List<EdmAnnotationExpression> getParameters() {
    return parameters;
  }

}
