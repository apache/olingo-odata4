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
package org.apache.olingo.commons.api.data;

import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;

/**
 * High-level representation of a context URL, built from the string value returned by a service; provides access to the
 * various components of the context URL, defined in the protocol specification.
 *
 * {@link http://docs.oasis-open.org/odata/odata/v4.0/os/part1-protocol/odata-v4.0-os-part1-protocol.html#_Toc372793655}
 */
public class ContextURL {

  private URI uri;

  private URI serviceRoot;

  private String entitySetOrSingletonOrType;

  private String derivedEntity;

  private String selectList;

  private String navOrPropertyPath;

  public static ContextURL getInstance(final URI contextURL) {
    final ContextURL instance = new ContextURL();
    instance.uri = contextURL;

    final String contextURLasString = instance.uri.toASCIIString().
            replace("/$entity", StringUtils.EMPTY).
            replace("/@Element", StringUtils.EMPTY);
    
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

    final int slashIdx = instance.entitySetOrSingletonOrType.indexOf('/');
    if (slashIdx != -1) {
      final String clone = instance.entitySetOrSingletonOrType;
      instance.entitySetOrSingletonOrType = clone.substring(0, slashIdx);
      instance.derivedEntity = clone.substring(slashIdx + 1);
    }

    if (!firstToken.equals(rest)) {
      final String[] pathElems = StringUtils.substringAfter(rest, "/").split("/");
      if (pathElems.length > 0) {
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

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ContextURL other = (ContextURL) obj;
    if (this.uri != other.uri && (this.uri == null || !this.uri.equals(other.uri))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return this.uri.hashCode();
  }

  @Override
  public String toString() {
    return this.uri.toString();
  }

}
