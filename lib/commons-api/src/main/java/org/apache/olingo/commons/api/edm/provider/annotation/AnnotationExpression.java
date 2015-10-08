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
package org.apache.olingo.commons.api.edm.provider.annotation;

import java.io.Serializable;

/**
 * Super type of all annotation expressions
 * A expression is either constant or dynamic
 */
public interface AnnotationExpression extends Serializable {

  /**
   * Return true if the expression is constant
   * @return true if the expression is constant
   */
  boolean isConstant();

  /**
   * Casts the expression to {@link org.apache.olingo.commons.api.edm.annotation.EdmConstantAnnotationExpression}
   * @return Constant Expression
   */
  ConstantAnnotationExpression asConstant();

  /**
   * Return true if the expression is dynamic
   * @return true if the expression is dynamic
   */
  boolean isDynamic();

  /**
   * Cast the expression to {@link org.apache.olingo.commons.api.edm.annotation.EdmDynamicAnnotationExpression}
   * @return Dynamic Expression
   */
  DynamicAnnotationExpression asDynamic();
}
