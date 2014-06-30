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

import java.net.URI;

/**
 * Objects of this class contain information about one entity set inside the EntityDataModel.
 */
public interface EdmEntitySetInfo {

  /**
   * @return the entity container name which contains this entity set.
   */
  String getEntityContainerName();

  /**
   * @return the entity set name
   */
  String getEntitySetName();

  /**
   * We use a {@link URI} object here to ensure the right encoding. If a string representation is needed the
   * toASCIIString() method can be used.
   * 
   * @return the uri to this entity set e.g. "Employees"
   */
  URI getEntitySetUri();

}
