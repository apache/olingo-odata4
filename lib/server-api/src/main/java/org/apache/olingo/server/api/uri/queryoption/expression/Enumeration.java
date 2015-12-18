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

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEnumType;

/**
 * Represents a enumeration expression node in the expression tree
 * <br>
 * A enumeration expression node is inserted in the expression tree for any valid
 * enumeration. E.g. for $filter=style has Sales.Pattern'Yellow'
 */
public interface Enumeration extends Expression {

  /**
   * @return A list of enumeration values
   */
  public List<String> getValues();

  /**
   * @return The enumeration type used before the enumeration values
   */
  public EdmEnumType getType();

}
