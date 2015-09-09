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
package org.apache.olingo.commons.api.edm.constants;

/**
 * A navigation property MAY define one edm:OnDelete element. It describes the action the service will take on 
 * related entities when the entity on which the navigation property is defined is deleted.
 */
public enum EdmOnDelete {
  
  /**
   * Cascade, meaning the related entities will be deleted if the source entity is deleted
   */
  Cascade, 
  
  /**
   * None, meaning a DELETE request on a source entity with related entities will fail,
   */
  None, 
  
  /**
   * SetNull, meaning all properties of related entities that are tied to properties of the source entity via a 
   * referential constraint and that do not participate in other referential constraints will be set to null,
   */
  SetNull, 
  
  /**
   * SetDefault, meaning all properties of related entities that are tied to properties of the source entity via 
   * a referential constraint and that do not participate in other referential constraints will be set to 
   * their default value.
   */
  SetDefault
}
