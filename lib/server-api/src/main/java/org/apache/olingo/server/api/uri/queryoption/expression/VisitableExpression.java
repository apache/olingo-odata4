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
package org.apache.olingo.server.api.uri.queryoption.expression;

import org.apache.olingo.server.api.ODataApplicationException;

/**
 * Marks an expression node a visitable by an expression visitor
 */
public interface VisitableExpression {

  /**
   * Method {@link #accept(ExpressionVisitor)} is called when traversing the expression tree. This method is invoked on
   * each expression used as node in an expression tree. The implementations should
   * behave as follows:
   * <li>Call accept on all sub nodes and store the returned Objects which are of the generic type T
   * <li>Call the appropriate method on the {@link ExpressionVisitor} instance and provide the stored return objects
   * to that instance
   * <li>Return the object which should be passed to the processing algorithm of the parent expression node
   * <br>
   * <br>
   * @param visitor
   * Visitor object (implementing {@link ExpressionVisitor}) whose methods are called during traversing a
   * expression node of the expression tree.
   * @return
   * Object of type T which should be passed to the processing algorithm of the parent expression node
   * @throws ExpressionVisitException
   * Exception occurred in the OData library while traversing the tree
   * @throws ODataApplicationException
   * Exception thrown by the application who implemented the visitor
   */
  <T> T accept(ExpressionVisitor<T> visitor) throws ExpressionVisitException, ODataApplicationException;

}