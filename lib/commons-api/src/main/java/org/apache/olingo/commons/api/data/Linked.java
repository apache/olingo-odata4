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

import java.util.ArrayList;
import java.util.List;

/**
 * Data representation for a linked object.
 */
public abstract class Linked extends AbstractODataObject {

  private final List<Link> associationLinks = new ArrayList<Link>();
  private final List<Link> navigationLinks = new ArrayList<Link>();
  private final List<Link> bindingLinks = new ArrayList<Link>();

  protected Link getOneByTitle(final String name, final List<Link> links) {
    Link result = null;

    for (Link link : links) {
      if (name.equals(link.getTitle())) {
        result = link;
      }
    }

    return result;
  }

  /**
   * Gets association link with given name, if available, otherwise <tt>null</tt>.
   *
   * @param name candidate link name
   * @return association link with given name, if available, otherwise <tt>null</tt>
   */
  public Link getAssociationLink(final String name) {
    return getOneByTitle(name, associationLinks);
  }

  /**
   * Gets association links.
   *
   * @return association links.
   */
  public List<Link> getAssociationLinks() {
    return associationLinks;
  }

  /**
   * Gets navigation link with given name, if available, otherwise <tt>null</tt>.
   *
   * @param name candidate link name
   * @return navigation link with given name, if available, otherwise <tt>null</tt>
   */
  public Link getNavigationLink(final String name) {
    return getOneByTitle(name, navigationLinks);
  }

  /**
   * Gets navigation links.
   *
   * @return links.
   */
  public List<Link> getNavigationLinks() {
    return navigationLinks;
  }

  /**
   * Gets binding link with given name, if available, otherwise <tt>null</tt>.
   * @param name candidate link name
   * @return binding link with given name, if available, otherwise <tt>null</tt>
   */
  public Link getNavigationBinding(final String name) {
    return getOneByTitle(name, bindingLinks);
  }

  /**
   * Gets binding links.
   *
   * @return links.
   */
  public List<Link> getNavigationBindings() {
    return bindingLinks;
  }

  @Override
  public boolean equals(final Object o) {
    return super.equals(o)
        && associationLinks.equals(((Linked) o).associationLinks)
        && navigationLinks.equals(((Linked) o).navigationLinks)
        && bindingLinks.equals(((Linked) o).bindingLinks);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + associationLinks.hashCode();
    result = 31 * result + navigationLinks.hashCode();
    result = 31 * result + bindingLinks.hashCode();
    return result;
  }
}
