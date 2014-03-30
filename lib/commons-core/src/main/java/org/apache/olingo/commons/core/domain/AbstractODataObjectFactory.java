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
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.CommonODataObjectFactory;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public abstract class AbstractODataObjectFactory implements CommonODataObjectFactory {

  private static final long serialVersionUID = -3769695665946919447L;

  protected final ODataServiceVersion version;

  public AbstractODataObjectFactory(final ODataServiceVersion version) {
    this.version = version;
  }

  @Override
  public ODataInlineEntitySet newInlineEntitySet(final String name, final URI link,
          final CommonODataEntitySet entitySet) {

    return new ODataInlineEntitySet(version, link, ODataLinkType.ENTITY_SET_NAVIGATION, name, entitySet);
  }

  @Override
  public ODataInlineEntitySet newInlineEntitySet(final String name, final URI baseURI, final String href,
          final CommonODataEntitySet entitySet) {

    return new ODataInlineEntitySet(version, baseURI, href, ODataLinkType.ENTITY_SET_NAVIGATION, name, entitySet);
  }

  @Override
  public ODataInlineEntity newInlineEntity(final String name, final URI link, final CommonODataEntity entity) {
    return new ODataInlineEntity(version, link, ODataLinkType.ENTITY_NAVIGATION, name, entity);
  }

  @Override
  public ODataInlineEntity newInlineEntity(final String name, final URI baseURI, final String href,
          final CommonODataEntity entity) {

    return new ODataInlineEntity(version, baseURI, href, ODataLinkType.ENTITY_NAVIGATION, name, entity);
  }

  @Override
  public ODataLink newEntityNavigationLink(final String name, final URI link) {
    return new ODataLink.Builder().setVersion(version).setURI(link).
            setType(ODataLinkType.ENTITY_NAVIGATION).setTitle(name).build();
  }

  @Override
  public ODataLink newEntityNavigationLink(final String name, final URI baseURI, final String href) {
    return new ODataLink.Builder().setVersion(version).setURI(baseURI, href).
            setType(ODataLinkType.ENTITY_NAVIGATION).setTitle(name).build();
  }

  @Override
  public ODataLink newEntitySetNavigationLink(final String name, final URI link) {
    return new ODataLink.Builder().setVersion(version).setURI(link).
            setType(ODataLinkType.ENTITY_SET_NAVIGATION).setTitle(name).build();
  }

  @Override
  public ODataLink newEntitySetNavigationLink(final String name, final URI baseURI, final String href) {
    return new ODataLink.Builder().setVersion(version).setURI(baseURI, href).
            setType(ODataLinkType.ENTITY_SET_NAVIGATION).setTitle(name).build();
  }

  @Override
  public ODataLink newAssociationLink(final String name, final URI link) {
    return new ODataLink.Builder().setVersion(version).setURI(link).
            setType(ODataLinkType.ASSOCIATION).setTitle(name).build();
  }

  @Override
  public ODataLink newAssociationLink(final String name, final URI baseURI, final String href) {
    return new ODataLink.Builder().setVersion(version).setURI(baseURI, href).
            setType(ODataLinkType.ASSOCIATION).setTitle(name).build();
  }

  @Override
  public ODataLink newMediaEditLink(final String name, final URI link) {
    return new ODataLink.Builder().setVersion(version).setURI(link).
            setType(ODataLinkType.MEDIA_EDIT).setTitle(name).build();
  }

  @Override
  public ODataLink newMediaEditLink(final String name, final URI baseURI, final String href) {
    return new ODataLink.Builder().setVersion(version).setURI(baseURI, href).
            setType(ODataLinkType.MEDIA_EDIT).setTitle(name).build();
  }

  @Override
  public ODataPrimitiveValue.Builder newPrimitiveValueBuilder() {
    return new ODataPrimitiveValueImpl.BuilderImpl(version);
  }

  @Override
  public ODataComplexValue newComplexValue(final String typeName) {
    return new ODataComplexValueImpl(typeName);
  }

  @Override
  public ODataCollectionValue newCollectionValue(final String typeName) {
    return new ODataCollectionValueImpl(typeName);
  }

}
