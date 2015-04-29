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
package org.apache.olingo.client.api.domain;

import java.util.List;

public interface ClientLinked {

  /**
   * Puts the given link into one of available lists, based on its type.
   * 
   * @param link to be added
   * @return <tt>true</tt> if the given link was added in one of available lists
   */
  boolean addLink(ClientLink link);

  /**
   * Removes the given link from any list (association, navigation, edit-media).
   * 
   * @param link to be removed
   * @return <tt>true</tt> if the given link was contained in one of available lists
   */
  boolean removeLink(ClientLink link);

  /**
   * Gets association link with given name, if available, otherwise <tt>null</tt>.
   * 
   * @param name candidate link name
   * @return association link with given name, if available, otherwise <tt>null</tt>
   */
  ClientLink getAssociationLink(String name);

  /**
   * Returns all entity association links.
   * 
   * @return OData entity links.
   */
  List<ClientLink> getAssociationLinks();

  /**
   * Gets navigation link with given name, if available, otherwise <tt>null</tt>.
   * 
   * @param name candidate link name
   * @return navigation link with given name, if available, otherwise <tt>null</tt>
   */
  ClientLink getNavigationLink(String name);

  /**
   * Returns all entity navigation links (including inline entities / entity sets).
   * 
   * @return OData entity links.
   */
  List<ClientLink> getNavigationLinks();
}
