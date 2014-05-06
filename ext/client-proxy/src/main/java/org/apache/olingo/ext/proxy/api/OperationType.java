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
package org.apache.olingo.ext.proxy.api;

public enum OperationType {

  /**
   * Function or action (not specified explicitly).
   * <br />
   * OData V3 only.
   */
  LEGACY,
  /**
   * Functions MUST NOT have observable side effects and MUST return a single instance or a collection of instances of
   * any type. Functions MAY be composable.
   */
  FUNCTION,
  /**
   * Actions MAY have observable side effects and MAY return a single instance or a collection of instances of any type.
   * Actions cannot be composed with additional path segments.
   */
  ACTION;

}
