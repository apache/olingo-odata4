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

import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientDelta;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientInlineEntity;
import org.apache.olingo.client.api.domain.ClientInlineEntitySet;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientLinkType;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientSingleton;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class ClientObjectFactoryImpl implements ClientObjectFactory {

  @Override
  public ClientInlineEntitySet newDeepInsertEntitySet(final String name, final ClientEntitySet entitySet) {
    return new ClientInlineEntitySet(null, ClientLinkType.ENTITY_SET_NAVIGATION, name, entitySet);
  }

  @Override
  public ClientInlineEntity newDeepInsertEntity(final String name, final ClientEntity entity) {
    return new ClientInlineEntity(null, ClientLinkType.ENTITY_NAVIGATION, name, entity);
  }

  @Override
  public ClientEntitySet newEntitySet() {
    return new ClientEntitySetImpl();
  }

  @Override
  public ClientEntitySet newEntitySet(final URI next) {
    return new ClientEntitySetImpl(next);
  }

  @Override
  public ClientEntity newEntity(final FullQualifiedName typeName) {
    return new ClientEntityImpl(typeName);
  }

  @Override
  public ClientEntity newEntity(final FullQualifiedName typeName, final URI link) {
    final ClientEntityImpl result = new ClientEntityImpl(typeName);
    result.setLink(link);
    return result;
  }

  @Override
  public ClientSingleton newSingleton(final FullQualifiedName typeName) {
    return new ClientEntityImpl(typeName);
  }

  @Override
  public ClientLink newEntityNavigationLink(final String name, final URI link) {
    return new ClientLink.Builder().setURI(link).
        setType(ClientLinkType.ENTITY_NAVIGATION).setTitle(name).build();
  }

  @Override
  public ClientLink newEntitySetNavigationLink(final String name, final URI link) {
    return new ClientLink.Builder().setURI(link).
        setType(ClientLinkType.ENTITY_SET_NAVIGATION).setTitle(name).build();
  }

  @Override
  public ClientLink newAssociationLink(final String name, final URI link) {
    return new ClientLink.Builder().setURI(link).
        setType(ClientLinkType.ASSOCIATION).setTitle(name).build();
  }

  @Override
  public ClientLink newMediaEditLink(String name, URI link, String type, String eTag) {
    return new ClientLink.Builder().setURI(link).setEtag(eTag).
        setType(ClientLinkType.fromString(Constants.NS_MEDIA_EDIT_LINK_REL,
            type == null ? Constants.MEDIA_EDIT_LINK_TYPE : type))
        .setTitle(name).build();    
  }
  
  public ClientLink newMediaReadLink(String name, URI link, String type, String eTag) {
    return new ClientLink.Builder().setURI(link).setEtag(eTag).
        setType(ClientLinkType.fromString(Constants.NS_MEDIA_READ_LINK_REL,
            type == null ? Constants.MEDIA_EDIT_LINK_TYPE : type))
        .setTitle(name).build();    
  }
  
  @Override
  public ClientPrimitiveValue.Builder newPrimitiveValueBuilder() {
    return new ClientPrimitiveValueImpl.BuilderImpl();
  }

  @Override
  public ClientEnumValue newEnumValue(final String typeName, final String value) {
    return new ClientEnumValueImpl(typeName, value);
  }

  @Override
  public ClientComplexValue newComplexValue(final String typeName) {
    return new ClientComplexValueImpl(typeName);
  }

  @Override
  public ClientCollectionValue<ClientValue> newCollectionValue(final String typeName) {
    return new ClientCollectionValueImpl<ClientValue>(typeName);
  }

  @Override
  public ClientProperty newPrimitiveProperty(final String name, final ClientPrimitiveValue value) {
    return new ClientPropertyImpl(name, value);
  }

  @Override
  public ClientProperty newComplexProperty(final String name, final ClientComplexValue value) {

    return new ClientPropertyImpl(name, value);
  }

  @Override
  public ClientProperty newCollectionProperty(final String name,
      final ClientCollectionValue<? extends ClientValue> value) {

    return new ClientPropertyImpl(name, value);
  }

  @Override
  public ClientProperty newEnumProperty(final String name, final ClientEnumValue value) {
    return new ClientPropertyImpl(name, value);
  }

  @Override
  public ClientDelta newDelta() {
    return new ClientDeltaImpl();
  }

  @Override
  public ClientDelta newDelta(final URI next) {
    return new ClientDeltaImpl(next);
  }
}
