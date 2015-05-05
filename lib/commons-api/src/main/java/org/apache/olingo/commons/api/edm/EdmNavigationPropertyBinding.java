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
package org.apache.olingo.commons.api.edm;

/**
 * A navigation property binding which binds entity sets or singletons with each other.
 */
public interface EdmNavigationPropertyBinding {

  /**
   * A path contains the full qualified name of the type it is referring to as a first segment. If it is a type
   * nested inside another type the path is separated by forward slashes.
   * @return path which leads to the target.
   */
  String getPath();

  /**
   * @return the entity set or singleton this binding refers to.
   */
  String getTarget();

}
