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
package org.apache.olingo.commons.core.domain;

import java.net.URI;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.CommonODataObjectFactory;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public abstract class AbstractODataObjectFactory implements CommonODataObjectFactory {

  private static final long serialVersionUID = -3769695665946919447L;

  protected final ODataServiceVersion version;

  public AbstractODataObjectFactory(final ODataServiceVersion version) {
    this.version = version;
  }

  @Override
  public ODataInlineEntitySet newDeepInsertEntitySet(final String name, final CommonODataEntitySet entitySet) {
    return new ODataInlineEntitySet(version, null, ODataLinkType.ENTITY_SET_NAVIGATION, name, entitySet);
  }

  @Override
  public ODataInlineEntity newDeepInsertEntity(final String name, final CommonODataEntity entity) {
    return new ODataInlineEntity(version, null, ODataLinkType.ENTITY_NAVIGATION, name, entity);
  }

  @Override
  public ODataLink newEntityNavigationLink(final String name, final URI link) {
    return new ODataLink.Builder().setVersion(version).setURI(link).
            setType(ODataLinkType.ENTITY_NAVIGATION).setTitle(name).build();
  }

  @Override
  public ODataLink newEntitySetNavigationLink(final String name, final URI link) {
    return new ODataLink.Builder().setVersion(version).setURI(link).
            setType(ODataLinkType.ENTITY_SET_NAVIGATION).setTitle(name).build();
  }
}
