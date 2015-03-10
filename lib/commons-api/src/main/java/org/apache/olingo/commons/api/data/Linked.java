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
package org.apache.olingo.commons.api.data;

import java.util.List;
import java.util.Map;

public interface Linked {

  /**
   * Gets association link with given name, if available, otherwise <tt>null</tt>.
   * 
   * @param name candidate link name
   * @return association link with given name, if available, otherwise <tt>null</tt>
   */
  Link getAssociationLink(String name);

  /**
   * Gets association links.
   * 
   * @return association links.
   */
  List<Link> getAssociationLinks();

  /**
   * Gets navigation link with given name, if available, otherwise <tt>null</tt>.
   * 
   * @param name candidate link name
   * @return navigation link with given name, if available, otherwise <tt>null</tt>
   */
  Link getNavigationLink(String name);

  /**
   * Gets navigation links.
   * 
   * @return links.
   */
  List<Link> getNavigationLinks();

  /**
   * Gets binding link with given name, if available, otherwise <tt>null</tt>.
   * @param name candidate link name
   * @return binding link with given name, if available, otherwise <tt>null</tt>
   */
  Link getNavigationBinding(String name);

  /**
   * Gets binding links.
   * 
   * @return links.
   */
  List<Link> getNavigationBindings();
  

  /**
   * Gets in-line entity.
   * 
   * @param name of the navigation
   * @return in-line entity for given name, if available, otherwise <tt>null</tt>
   */
  Entity getInlineEntity(String name);

  /**
   * @return all in-line entities for this entity.
   */
  Map<String, Entity> getAllInlineEntities();
  
  /**
   * Adds an in-line entity for given navigation name. If one is set already it will be replaced with the new value. 
   * 
   * @param name navigation name
   * @param entity entity.
   */
  void addInlineEntity(String name, Entity entity);

  /**
   * Gets in-line entity set.
   * 
   * @param name of the navigation
   * @return in-line entity set for given name, if available, otherwise <tt>null</tt>
   */
  EntitySet getInlineEntitySet(String name);

  /**
   * @return all in-line entity sets for this entity.
   */
  Map<String, EntitySet> getAllInlineEntitySets();
  
  /**
   * Adds an in-line entity set for given navigation name. If one is set already it will be replaced with the new value. 
   * 
   * @param name navigation name
   * @param entitySet entity set.
   */
  void addInlineEntitySet(String name, EntitySet entitySet);
}
