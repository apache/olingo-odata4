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
package org.apache.olingo.client.core.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.domain.AbstractClientValue;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientProperty;

public class ClientComplexValueImpl extends AbstractClientValue implements ClientComplexValue {

  /**
   * Navigation links (might contain in-line entities or entity sets).
   */
  private final List<ClientLink> navigationLinks = new ArrayList<ClientLink>();

  /**
   * Association links.
   */
  private final List<ClientLink> associationLinks = new ArrayList<ClientLink>();

  private final List<ClientAnnotation> annotations = new ArrayList<ClientAnnotation>();

  /**
   * Complex type fields.
   */
  private final Map<String, ClientProperty> fields = new LinkedHashMap<String, ClientProperty>();

  /**
   * Constructor.
   *
   * @param typeName type name.
   */
  public ClientComplexValueImpl(final String typeName) {
    super(typeName);
  }

  @Override
  public boolean isEnum() {
    return false;
  }

  @Override
  public ClientEnumValue asEnum() {
    return null;
  }

  @Override
  public boolean isComplex() {
    return true;
  }

  @Override
  public boolean addLink(final ClientLink link) {
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
    case MEDIA_READ:
      throw new IllegalArgumentException("Complex values cannot have media links!");

    default:
    }

    return result;
  }

  @Override
  public boolean removeLink(final ClientLink link) {
    return associationLinks.remove(link) || navigationLinks.remove(link);
  }

  private ClientLink getLink(final List<ClientLink> links, final String name) {
    ClientLink result = null;
    for (ClientLink link : links) {
      if (name.equals(link.getName())) {
        result = link;
        break;
      }
    }

    return result;
  }

  @Override
  public ClientLink getNavigationLink(final String name) {
    return getLink(navigationLinks, name);
  }

  @Override
  public List<ClientLink> getNavigationLinks() {
    return navigationLinks;
  }

  @Override
  public ClientLink getAssociationLink(final String name) {
    return getLink(associationLinks, name);
  }

  @Override
  public List<ClientLink> getAssociationLinks() {
    return associationLinks;
  }

  @Override
  public Map<String, Object> asJavaMap() {
    final Map<String, Object> result = new LinkedHashMap<String, Object>();
    for (Map.Entry<String, ClientProperty> entry : fields.entrySet()) {
      Object value = null;
      if (entry.getValue().hasPrimitiveValue()) {
        value = entry.getValue().getPrimitiveValue().toValue();
      } else if (entry.getValue().hasComplexValue()) {
        value = entry.getValue().getComplexValue().asJavaMap();
      } else if (entry.getValue().hasCollectionValue()) {
        value = entry.getValue().getCollectionValue().asJavaCollection();
      } else if (entry.getValue().hasEnumValue()) {
        value = entry.getValue().getEnumValue().toString();
      }

      result.put(entry.getKey(), value);
    }

    return result;
  }

  @Override
  public List<ClientAnnotation> getAnnotations() {
    return annotations;
  }

  /**
   * Adds field to the complex type.
   *
   * @param field field to be added.
   */
  @Override
  public ClientComplexValue add(final ClientProperty field) {
    fields.put(field.getName(), field);
    return this;
  }

  /**
   * Gets field.
   *
   * @param name name of the field to be retrieved.
   * @return requested field.
   */
  @Override
  public ClientProperty get(final String name) {
    return fields.get(name);
  }

  /**
   * Complex property fields iterator.
   *
   * @return fields iterator.
   */
  @Override
  public Iterator<ClientProperty> iterator() {
    return fields.values().iterator();
  }

  /**
   * Gets number of fields.
   *
   * @return number of fields.
   */
  @Override
  public int size() {
    return fields.size();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (annotations.hashCode());
    result = prime * result + (associationLinks.hashCode());
    result = prime * result + (fields.hashCode());
    result = prime * result + (navigationLinks.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof ClientComplexValueImpl)) {
      return false;
    }
    ClientComplexValueImpl other = (ClientComplexValueImpl) obj;
    if (annotations == null) {
      if (other.annotations != null) {
        return false;
      }
    } else if (!annotations.equals(other.annotations)) {
      return false;
    }
    if (associationLinks == null) {
      if (other.associationLinks != null) {
        return false;
      }
    } else if (!associationLinks.equals(other.associationLinks)) {
      return false;
    }
    if (fields == null) {
      if (other.fields != null) {
        return false;
      }
    } else if (!fields.equals(other.fields)) {
      return false;
    }
    if (navigationLinks == null) {
      if (other.navigationLinks != null) {
        return false;
      }
    } else if (!navigationLinks.equals(other.navigationLinks)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ClientComplexValueImpl [navigationLinks=" + navigationLinks + ", associationLinks=" + associationLinks
        + ", annotations=" + annotations + ", fields=" + fields + "super[" + super.toString() + "]]";
  }
}
