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
package org.apache.olingo.commons.api.edm.annotation;

import org.apache.olingo.commons.api.edm.EdmAnnotatable;

/**
 * Represents a edm:If expression
 */
public interface EdmIf extends EdmDynamicExpression, EdmAnnotatable {
  
  /**
   * Returns the first expression of the edm:If expression.
   * This expression represents the condition of the if expression
   * 
   * @return First expression of the if expression
   */
  EdmExpression getGuard();
  
  /**
   * Return the second expression of the edm:If expression.
   * If the condition of the condition is evaluated to true,
   * this expression as to be executed.
   * 
   * @return Second Expression of the edm:If expression
   */
  EdmExpression getThen();

  /**
   * Return the third expression of the edm:If expression.
   * If the condition of the condition is evaluated to false,
   * this expression as to be executed.
   * 
   * @return Third Expression of the edm:If expression
   */
  EdmExpression getElse();
}
