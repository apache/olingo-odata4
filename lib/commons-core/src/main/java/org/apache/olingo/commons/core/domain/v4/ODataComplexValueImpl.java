/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.core.domain.v4;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.v4.ODataLinkedComplexValue;
import org.apache.olingo.commons.api.domain.v4.ODataEnumValue;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.core.domain.AbstractODataComplexValue;

public class ODataComplexValueImpl extends AbstractODataComplexValue<ODataProperty> implements ODataLinkedComplexValue {

  private static final long serialVersionUID = 1143925901934898802L;

  /**
   * Navigation links (might contain in-line entities or feeds).
   */
  private final List<ODataLink> navigationLinks = new ArrayList<ODataLink>();

  /**
   * Association links.
   */
  private final List<ODataLink> associationLinks = new ArrayList<ODataLink>();

  public ODataComplexValueImpl(final String typeName) {
    super(typeName);
  }

  @Override
  public boolean isEnum() {
    return false;
  }

  @Override
  public ODataEnumValue asEnum() {
    return null;
  }

  @Override
  public boolean isLinkedComplex() {
    return true;
  }

  @Override
  public ODataLinkedComplexValue asLinkedComplex() {
    return this;
  }

  @Override
  public boolean addLink(final ODataLink link) {
    boolean result = false;

    switch (link.getType()) {
      case ASSOCIATION:
        result = associationLinks.contains(link) ? false : associationLinks.add(link);
        break;

      case ENTITY_NAVIGATION:
      case ENTITY_SET_NAVIGATION:
        result = navigationLinks.contains(link) ? false : navigationLinks.add(link);
        break;

      case MEDIA_EDIT:
        throw new IllegalArgumentException("Complex values cannot have media links!");

      default:
    }

    return result;
  }

  @Override
  public boolean removeLink(final ODataLink link) {
    return associationLinks.remove(link) || navigationLinks.remove(link);
  }

  private ODataLink getLink(final List<ODataLink> links, final String name) {
    ODataLink result = null;
    for (ODataLink link : links) {
      if (name.equals(link.getName())) {
        result = link;
      }
    }

    return result;
  }

  @Override
  public ODataLink getNavigationLink(final String name) {
    return getLink(navigationLinks, name);
  }

  @Override
  public List<ODataLink> getNavigationLinks() {
    return navigationLinks;
  }

  @Override
  public ODataLink getAssociationLink(final String name) {
    return getLink(associationLinks, name);
  }

  @Override
  public List<ODataLink> getAssociationLinks() {
    return associationLinks;
  }

}
