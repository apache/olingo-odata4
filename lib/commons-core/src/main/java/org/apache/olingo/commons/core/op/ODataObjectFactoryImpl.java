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
package org.apache.olingo.commons.core.op;

import java.net.URI;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataGeospatialValue;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataObjectFactory;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class ODataObjectFactoryImpl implements ODataObjectFactory {

  private static final long serialVersionUID = -3769695665946919447L;

  protected final ODataServiceVersion version;

  public ODataObjectFactoryImpl(final ODataServiceVersion version) {
    this.version = version;
  }

  @Override
  public ODataEntitySet newEntitySet() {
    return new ODataEntitySet();
  }

  @Override
  public ODataEntitySet newEntitySet(final URI next) {
    return new ODataEntitySet(next);
  }

  @Override
  public ODataEntity newEntity(final String name) {
    return new ODataEntity(name);
  }

  @Override
  public ODataEntity newEntity(final String name, final URI link) {
    final ODataEntity result = new ODataEntity(name);
    result.setLink(link);
    return result;
  }

  @Override
  public ODataInlineEntitySet newInlineEntitySet(final String name, final URI link,
          final ODataEntitySet entitySet) {

    return new ODataInlineEntitySet(version, link, ODataLinkType.ENTITY_SET_NAVIGATION, name, entitySet);
  }

  @Override
  public ODataInlineEntitySet newInlineEntitySet(final String name, final URI baseURI, final String href,
          final ODataEntitySet entitySet) {

    return new ODataInlineEntitySet(version, baseURI, href, ODataLinkType.ENTITY_SET_NAVIGATION, name, entitySet);
  }

  @Override
  public ODataInlineEntity newInlineEntity(final String name, final URI link, final ODataEntity entity) {
    return new ODataInlineEntity(version, link, ODataLinkType.ENTITY_NAVIGATION, name, entity);
  }

  @Override
  public ODataInlineEntity newInlineEntity(final String name, final URI baseURI, final String href,
          final ODataEntity entity) {

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
  public ODataLink newFeedNavigationLink(final String name, final URI link) {
    return new ODataLink.Builder().setVersion(version).setURI(link).
            setType(ODataLinkType.ENTITY_SET_NAVIGATION).setTitle(name).build();
  }

  @Override
  public ODataLink newFeedNavigationLink(final String name, final URI baseURI, final String href) {
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
  public ODataProperty newPrimitiveProperty(final String name, final ODataPrimitiveValue value) {
    return new ODataProperty(name, value);
  }

  @Override
  public ODataProperty newPrimitiveProperty(final String name, final ODataGeospatialValue value) {
    return new ODataProperty(name, value);
  }

  @Override
  public ODataProperty newComplexProperty(final String name, final ODataComplexValue value) {
    return new ODataProperty(name, value);
  }

  @Override
  public ODataProperty newCollectionProperty(final String name, final ODataCollectionValue value) {
    return new ODataProperty(name, value);
  }

}
