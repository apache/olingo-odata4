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
package org.apache.olingo.commons.core.domain;

import org.apache.olingo.commons.api.domain.ClientAnnotation;
import org.apache.olingo.commons.api.domain.ClientEntity;
import org.apache.olingo.commons.api.domain.ClientEntitySet;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ClientEntitySetImpl extends AbstractClientEntitySet implements ClientEntitySet {

  private URI deltaLink;

  private final List<ClientEntity> entities = new ArrayList<ClientEntity>();

  private final List<ClientAnnotation> annotations = new ArrayList<ClientAnnotation>();

  public ClientEntitySetImpl() {
    super();
  }

  public ClientEntitySetImpl(final URI next) {
    super(next);
  }

  @Override
  public List<ClientEntity> getEntities() {
    return entities;
  }

  @Override
  public URI getDeltaLink() {
    return deltaLink;
  }

  @Override
  public void setDeltaLink(final URI deltaLink) {
    this.deltaLink = deltaLink;
  }

  @Override
  public List<ClientAnnotation> getAnnotations() {
    return annotations;
  }

}
