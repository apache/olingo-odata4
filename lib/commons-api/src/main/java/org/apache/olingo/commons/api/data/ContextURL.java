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

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;

import java.net.URI;

/**
 * High-level representation of a context URL, built from the string value returned by a service; provides access to the
 * various components of the context URL, defined in the <a
 * href="http://docs.oasis-open.org/odata/odata/v4.0/os/part1-protocol/odata-v4.0-os-part1-protocol.html#_Toc372793655">
 * protocol specification</a>.
 */
public class ContextURL {

  private URI uri;

  private URI serviceRoot;

  private String entitySetOrSingletonOrType;

  private String derivedEntity;

  private String selectList;

  private String navOrPropertyPath;

  public enum Suffix {

    ENTITY("$entity"),
    REFERENCE("$ref"),
    DELTA("$delta"),
    DELTA_DELETED_ENTITY("$deletedEntity"),
    DELTA_LINK("$link"),
    DELTA_DELETED_LINK("$deletedLink");

    private final String representation;

    private Suffix(final String representation) {
      this.representation = representation;
    }

    public String getRepresentation() {
      return representation;
    }
  }
  private Suffix suffix;

  private ContextURL() {
  }

  public static ContextURL getInstance(final URI contextURL) {
    final ContextURL instance = new ContextURL();
    instance.uri = contextURL;

    String contextURLasString = instance.uri.toASCIIString();

    if (contextURLasString.endsWith("/$entity") || contextURLasString.endsWith("/@Element")) {
      instance.suffix = Suffix.ENTITY;
      contextURLasString = contextURLasString.replace("/$entity", StringUtils.EMPTY)
              .replace("/@Element", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$ref")) {
      instance.suffix = Suffix.REFERENCE;
      contextURLasString = contextURLasString.replace("/$ref", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$delta")) {
      instance.suffix = Suffix.DELTA;
      contextURLasString = contextURLasString.replace("/$delta", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$deletedEntity")) {
      instance.suffix = Suffix.DELTA_DELETED_ENTITY;
      contextURLasString = contextURLasString.replace("/$deletedEntity", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$link")) {
      instance.suffix = Suffix.DELTA_LINK;
      contextURLasString = contextURLasString.replace("/$link", StringUtils.EMPTY);
    } else if (contextURLasString.endsWith("/$deletedLink")) {
      instance.suffix = Suffix.DELTA_DELETED_LINK;
      contextURLasString = contextURLasString.replace("/$deletedLink", StringUtils.EMPTY);
    }

    instance.serviceRoot = URI.create(StringUtils.substringBefore(contextURLasString, Constants.METADATA));

    final String rest = StringUtils.substringAfter(contextURLasString, Constants.METADATA + "#");

    String firstToken;
    if (rest.startsWith("Collection(")) {
      firstToken = rest.substring(0, rest.indexOf(')') + 1);
      instance.entitySetOrSingletonOrType = firstToken;
    } else {
      final int openParIdx = rest.indexOf('(');
      if (openParIdx == -1) {
        firstToken = StringUtils.substringBefore(rest, "/");

        instance.entitySetOrSingletonOrType = firstToken;
      } else {
        firstToken = StringUtils.substringBeforeLast(rest, ")") + ")";

        instance.entitySetOrSingletonOrType = firstToken.substring(0, openParIdx);
        final int commaIdx = firstToken.indexOf(',');
        if (commaIdx != -1) {
          instance.selectList = firstToken.substring(openParIdx + 1, firstToken.length() - 1);
        }
      }
    }

    final int slashIdx = instance.entitySetOrSingletonOrType.lastIndexOf('/');
    if (slashIdx != -1 && instance.entitySetOrSingletonOrType.substring(slashIdx + 1).indexOf('.') != -1) {
      final String clone = instance.entitySetOrSingletonOrType;
      instance.entitySetOrSingletonOrType = clone.substring(0, slashIdx);
      instance.derivedEntity = clone.substring(slashIdx + 1);
    }

    if (!firstToken.equals(rest)) {
      final String[] pathElems = StringUtils.substringAfter(rest, "/").split("/");
      if (pathElems.length > 0 && pathElems[0].length() > 0) {
        if (pathElems[0].indexOf('.') == -1) {
          instance.navOrPropertyPath = pathElems[0];
        } else {
          instance.derivedEntity = pathElems[0];
        }

        if (pathElems.length > 1) {
          instance.navOrPropertyPath = pathElems[1];
        }
      }
    }

    return instance;
  }

  public URI getURI() {
    return uri;
  }

  public URI getServiceRoot() {
    return serviceRoot;
  }

  public String getEntitySetOrSingletonOrType() {
    return entitySetOrSingletonOrType;
  }

  public String getDerivedEntity() {
    return derivedEntity;
  }

  public String getSelectList() {
    return selectList;
  }

  public String getNavOrPropertyPath() {
    return navOrPropertyPath;
  }

  public boolean isEntity() {
    return suffix == Suffix.ENTITY;
  }

  public boolean isReference() {
    return suffix == Suffix.REFERENCE;
  }

  public boolean isDelta() {
    return suffix == Suffix.DELTA;
  }

  public boolean isDeltaDeletedEntity() {
    return suffix == Suffix.DELTA_DELETED_ENTITY;
  }

  public boolean isDeltaLink() {
    return suffix == Suffix.DELTA_LINK;
  }

  public boolean isDeltaDeletedLink() {
    return suffix == Suffix.DELTA_DELETED_LINK;
  }

  public static final class ContextURLBuilder {

    private ContextURL contextURL = new ContextURL();

    private ContextURLBuilder() {
    }

    public ContextURLBuilder serviceRoot(final URI serviceRoot) {
      contextURL.serviceRoot = serviceRoot;
      return this;
    }

    public ContextURLBuilder entitySet(final EdmEntitySet entitySet) {
      contextURL.entitySetOrSingletonOrType = entitySet.getName();
      return this;
    }

    public ContextURLBuilder derived(final EdmEntityType derivedType) {
      contextURL.derivedEntity = derivedType.getFullQualifiedName().getFullQualifiedNameAsString();
      return this;
    }

    public ContextURLBuilder suffix(final Suffix suffix) {
      contextURL.suffix = suffix;
      return this;
    }

    public ContextURL build() {
      final StringBuilder result = new StringBuilder();
      if (contextURL.serviceRoot != null) {
        result.append(contextURL.serviceRoot);
      }
      result.append(Constants.METADATA);
      if (contextURL.entitySetOrSingletonOrType != null) {
        result.append('#').append(contextURL.entitySetOrSingletonOrType);
      }
      if (contextURL.derivedEntity != null) {
        if (contextURL.entitySetOrSingletonOrType == null) {
          throw new IllegalArgumentException("ContextURL: Derived Type without anything to derive from!");
        }
        result.append('/').append(contextURL.derivedEntity);
      }
      if (contextURL.suffix == Suffix.REFERENCE) {
        if (contextURL.entitySetOrSingletonOrType != null) {
          throw new IllegalArgumentException("ContextURL: $ref with Entity Set");
        }
        result.append('#').append(contextURL.suffix.getRepresentation());
      } else if (contextURL.suffix != null) {
        if (contextURL.entitySetOrSingletonOrType == null) {
          throw new IllegalArgumentException("ContextURL: Suffix without preceding Entity Set!");
        }
        result.append('/').append(contextURL.suffix.getRepresentation());
      }
      contextURL.uri = URI.create(result.toString());
      return contextURL;
    }
  }

  public static final ContextURLBuilder create() {
    return new ContextURLBuilder();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ContextURL other = (ContextURL) obj;
    if (uri != other.uri && (uri == null || !uri.equals(other.uri))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return uri.hashCode();
  }

  @Override
  public String toString() {
    return uri.toString();
  }
}
