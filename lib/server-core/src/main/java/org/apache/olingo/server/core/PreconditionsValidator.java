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
package org.apache.olingo.server.core;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.server.api.CustomETagSupport;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceNavigation;

public class PreconditionsValidator {

  ;
  private final CustomETagSupport customETagSupport;
  private final UriInfo uriInfo;
  private final String ifMatch;
  private final String ifNoneMatch;

  public PreconditionsValidator(CustomETagSupport customETagSupport, UriInfo uriInfo, String ifMatch,
      String ifNoneMatch) {
    this.customETagSupport = customETagSupport;
    this.uriInfo = uriInfo;
    this.ifMatch = ifMatch;
    this.ifNoneMatch = ifNoneMatch;
  }

  public void validatePreconditions(boolean isMediaValue) throws PreconditionRequiredException {
    EdmEntitySet affectedEntitySet = extractInformation();
    if (affectedEntitySet != null) {
      if ((isMediaValue && customETagSupport.hasMediaETag(affectedEntitySet.getName())) ||
          (!isMediaValue && customETagSupport.hasETag(affectedEntitySet.getName()))) {
        checkETagHeaderPresent();
      }
    }
  }

  private void checkETagHeaderPresent() throws PreconditionRequiredException {
    if (ifMatch == null && ifNoneMatch == null) {
      throw new PreconditionRequiredException("Expected an if-match or if-none-match header",
          PreconditionRequiredException.MessageKeys.MISSING_HEADER);
    }
  }

  private EdmEntitySet extractInformation() {
    EdmEntitySet affectedEntitySet = null;
    List<UriResource> uriResourceParts = uriInfo.getUriResourceParts();
    if (uriResourceParts.size() > 0) {
      UriResource uriResourcePart = uriResourceParts.get(uriResourceParts.size() - 1);
      switch (uriResourcePart.getKind()) {
      case entitySet:
        affectedEntitySet = ((UriResourceEntitySet) uriResourcePart).getEntitySet();
        break;
      case navigationProperty:
        affectedEntitySet = getEntitySetFromBeginning();
        break;
      case value:
        affectedEntitySet = getEntitySetOrNavigationEntitySet(uriResourceParts);
        break;
      case action:
        affectedEntitySet = getEntitySetOrNavigationEntitySet(uriResourceParts);
        break;
      default:
        // TODO: Cannot happen but should we throw an exception?
        break;
      }
    } else {
      // TODO: Cannot happen but should we throw an exception?
    }
    return affectedEntitySet;
  }

  private EdmEntitySet getEntitySetOrNavigationEntitySet(List<UriResource> uriResourceParts) {
    EdmEntitySet affectedEntitySet = null;
    UriResource previousResourcePart = uriResourceParts.get(uriResourceParts.size() - 2);
    if (previousResourcePart.getKind() == UriResourceKind.entitySet) {
      affectedEntitySet = ((UriResourceEntitySet) previousResourcePart).getEntitySet();
    } else if (previousResourcePart.getKind() == UriResourceKind.navigationProperty) {
      affectedEntitySet = getEntitySetFromBeginning();
    }
    return affectedEntitySet;
  }

  private EdmEntitySet getEntitySetFromBeginning() {
    EdmEntitySet lastFoundES = null;
    for (UriResource uriResourcePart : uriInfo.getUriResourceParts()) {
      if (UriResourceKind.function == uriResourcePart.getKind()) {
        EdmFunctionImport functionImport = ((UriResourceFunction) uriResourcePart).getFunctionImport();
        if (functionImport != null && functionImport.getReturnedEntitySet() != null) {
          lastFoundES = functionImport.getReturnedEntitySet();
        } else {
          lastFoundES = null;
          break;
        }
      } else if (UriResourceKind.entitySet == uriResourcePart.getKind()) {
        lastFoundES = ((UriResourceEntitySet) uriResourcePart).getEntitySet();
      } else if (UriResourceKind.navigationProperty == uriResourcePart.getKind()) {
        EdmNavigationProperty navProp = ((UriResourceNavigation) uriResourcePart).getProperty();
        if (lastFoundES != null) {
          lastFoundES = (EdmEntitySet) lastFoundES.getRelatedBindingTarget(navProp.getName());
          if (lastFoundES == null) {
            break;
          }
        }
      } else if (UriResourceKind.value == uriResourcePart.getKind()
          || UriResourceKind.action == uriResourcePart.getKind()) {
        // TODO: Should we validate that we are at the end of the resource path
        break;
      } else {
        lastFoundES = null;
        break;
      }
    }
    return lastFoundES;
  }
}
