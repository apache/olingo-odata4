/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.consumer.core.deserializer;

import org.apache.olingo.consumer.api.deserializer.NavigationProperty;

public class NavigationPropertyImpl implements NavigationProperty {

  private final String name;
  private String associationLink;
  private String navigationLink;

  public NavigationPropertyImpl(final String name) {
    this.name = parseName(name);
  }

  public NavigationPropertyImpl(final String name, final String link) {
    this(name);
    updateLink(name, link);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getAssociationLink() {
    return associationLink;
  }

  @Override
  public String getNavigationLink() {
    return navigationLink;
  }

  public void updateLink(final String name, final String link) {
    String regexNavigationLink = ".*@odata.navigationLink$";
    String regexAssociationLink = ".*@odata.associationLink$";
    if (name.matches(regexNavigationLink)) {
      navigationLink = link;
    } else if (name.matches(regexAssociationLink)) {
      associationLink = link;
    }
  }

  private String parseName(final String nameToParse) {
    String[] split = nameToParse.split("@");
    if (split.length == 2) {
      return split[0];
    } else {
      throw new IllegalArgumentException("Got OData Navigation with unparseable format '"
          + nameToParse + "'.");
    }
  }

  public void updateLink(final NavigationProperty navigationProperty) {
    if (navigationProperty.getAssociationLink() != null) {
      associationLink = navigationProperty.getAssociationLink();
    }
    if (navigationProperty.getNavigationLink() != null) {
      navigationLink = navigationProperty.getNavigationLink();
    }
  }

  @Override
  public String toString() {
    return "NavigationPropertyImpl [name=" + name + ", associationLink=" + associationLink
        + ", navigationLink=" + navigationLink + "]";
  }
}
