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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.domain.AbstractClientPayload;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientOperation;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class ClientEntityImpl extends AbstractClientPayload implements ClientEntity, ClientSingleton {

  /**
   * Entity id.
   */
  private URI id;
  /**
   * ETag.
   */
  private String eTag;
  /**
   * Media entity flag.
   */
  private boolean mediaEntity = false;
  /**
   * In case of media entity, media content type.
   */
  private String mediaContentType;
  /**
   * In case of media entity, media content source.
   */
  private URI mediaContentSource;
  /**
   * Media ETag.
   */
  private String mediaETag;
  /**
   * Edit link.
   */
  private URI editLink;

  private final List<ClientProperty> properties = new ArrayList<ClientProperty>();

  private final List<ClientAnnotation> annotations = new ArrayList<ClientAnnotation>();

  private final FullQualifiedName typeName;
  /**
   * Navigation links (might contain in-line entities or entity sets).
   */
  private final List<ClientLink> navigationLinks = new ArrayList<ClientLink>();
  /**
   * Association links.
   */
  private final List<ClientLink> associationLinks = new ArrayList<ClientLink>();
  /**
   * Media edit links.
   */
  private final List<ClientLink> mediaEditLinks = new ArrayList<ClientLink>();
  /**
   * Operations (legacy, functions, actions).
   */
  private final List<ClientOperation> operations = new ArrayList<ClientOperation>();

  public ClientEntityImpl(final FullQualifiedName typeName) {
    super(typeName == null ? null : typeName.toString());
    this.typeName = typeName;
  }

  @Override
  public FullQualifiedName getTypeName() {
    return typeName;
  }

  @Override
  public String getETag() {
    return eTag;
  }

  @Override
  public void setETag(final String eTag) {
    this.eTag = eTag;
  }

  @Override
  public ClientOperation getOperation(final String title) {
    ClientOperation result = null;
    for (ClientOperation operation : operations) {
      if (title.equals(operation.getTitle())) {
        result = operation;
        break;
      }
    }

    return result;
  }

  /**
   * Gets operations.
   *
   * @return operations.
   */
  @Override
  public List<ClientOperation> getOperations() {
    return operations;
  }


  @Override
  public ClientProperty getProperty(final String name) {
    ClientProperty result = null;

    if (StringUtils.isNotBlank(name)) {
      for (ClientProperty property : getProperties()) {
        if (name.equals(property.getName())) {
          result = property;
          break;
        }
      }
    }

    return result;
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
        result = mediaEditLinks.contains(link) ? false : mediaEditLinks.add(link);
        break;

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
  public ClientLink getMediaEditLink(final String name) {
    return getLink(mediaEditLinks, name);
  }

  @Override
  public List<ClientLink> getMediaEditLinks() {
    return mediaEditLinks;
  }

  @Override
  public URI getEditLink() {
    return editLink;
  }

  @Override
  public void setEditLink(final URI editLink) {
    this.editLink = editLink;
  }

  @Override
  public URI getLink() {
    return super.getLink() == null ? getEditLink() : super.getLink();
  }

  @Override
  public boolean isReadOnly() {
    return super.getLink() != null;
  }

  @Override
  public boolean isMediaEntity() {
    return mediaEntity;
  }

  @Override
  public void setMediaEntity(final boolean isMediaEntity) {
    mediaEntity = isMediaEntity;
  }

  @Override
  public String getMediaContentType() {
    return mediaContentType;
  }

  @Override
  public void setMediaContentType(final String mediaContentType) {
    this.mediaContentType = mediaContentType;
  }

  @Override
  public URI getMediaContentSource() {
    return mediaContentSource;
  }

  @Override
  public void setMediaContentSource(final URI mediaContentSource) {
    this.mediaContentSource = mediaContentSource;
  }

  @Override
  public String getMediaETag() {
    return mediaETag;
  }

  @Override
  public void setMediaETag(final String eTag) {
    mediaETag = eTag;
  }

  @Override
  public URI getId() {
    return id;
  }

  @Override
  public void setId(final URI id) {
    this.id = id;
  }

  @Override
  public List<ClientProperty> getProperties() {
    return properties;
  }

  @Override
  public List<ClientAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
    result = prime * result + ((associationLinks == null) ? 0 : associationLinks.hashCode());
    result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
    result = prime * result + ((editLink == null) ? 0 : editLink.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((mediaContentSource == null) ? 0 : mediaContentSource.hashCode());
    result = prime * result + ((mediaContentType == null) ? 0 : mediaContentType.hashCode());
    result = prime * result + ((mediaETag == null) ? 0 : mediaETag.hashCode());
    result = prime * result + ((mediaEditLinks == null) ? 0 : mediaEditLinks.hashCode());
    result = prime * result + (mediaEntity ? 1231 : 1237);
    result = prime * result + ((navigationLinks == null) ? 0 : navigationLinks.hashCode());
    result = prime * result + ((operations == null) ? 0 : operations.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
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
    if (!(obj instanceof ClientEntityImpl)) {
      return false;
    }
    ClientEntityImpl other = (ClientEntityImpl) obj;
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
    if (eTag == null) {
      if (other.eTag != null) {
        return false;
      }
    } else if (!eTag.equals(other.eTag)) {
      return false;
    }
    if (editLink == null) {
      if (other.editLink != null) {
        return false;
      }
    } else if (!editLink.equals(other.editLink)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (mediaContentSource == null) {
      if (other.mediaContentSource != null) {
        return false;
      }
    } else if (!mediaContentSource.equals(other.mediaContentSource)) {
      return false;
    }
    if (mediaContentType == null) {
      if (other.mediaContentType != null) {
        return false;
      }
    } else if (!mediaContentType.equals(other.mediaContentType)) {
      return false;
    }
    if (mediaETag == null) {
      if (other.mediaETag != null) {
        return false;
      }
    } else if (!mediaETag.equals(other.mediaETag)) {
      return false;
    }
    if (mediaEditLinks == null) {
      if (other.mediaEditLinks != null) {
        return false;
      }
    } else if (!mediaEditLinks.equals(other.mediaEditLinks)) {
      return false;
    }
    if (mediaEntity != other.mediaEntity) {
      return false;
    }
    if (navigationLinks == null) {
      if (other.navigationLinks != null) {
        return false;
      }
    } else if (!navigationLinks.equals(other.navigationLinks)) {
      return false;
    }
    if (operations == null) {
      if (other.operations != null) {
        return false;
      }
    } else if (!operations.equals(other.operations)) {
      return false;
    }
    if (properties == null) {
      if (other.properties != null) {
        return false;
      }
    } else if (!properties.equals(other.properties)) {
      return false;
    }
    if (typeName == null) {
      if (other.typeName != null) {
        return false;
      }
    } else if (!typeName.equals(other.typeName)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ClientEntityImpl [id=" + id + ", eTag=" + eTag + ", mediaEntity=" + mediaEntity + ", mediaContentType="
        + mediaContentType + ", mediaContentSource=" + mediaContentSource + ", mediaETag=" + mediaETag + ", editLink="
        + editLink + ", properties=" + properties + ", annotations=" + annotations + ", typeName=" + typeName
        + ", navigationLinks=" + navigationLinks + ", associationLinks=" + associationLinks + ", mediaEditLinks="
        + mediaEditLinks + ", operations=" + operations + "super[" + super.toString() + "]]";
  }
}
