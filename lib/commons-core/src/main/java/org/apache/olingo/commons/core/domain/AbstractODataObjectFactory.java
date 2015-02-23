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

import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataObjectFactory;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public abstract class AbstractODataObjectFactory implements ODataObjectFactory {

  protected final ODataServiceVersion version;

  public AbstractODataObjectFactory(final ODataServiceVersion version) {
    this.version = version;
  }

  @Override
  public ODataInlineEntitySet newDeepInsertEntitySet(final String name, final ODataEntitySet entitySet) {
    return new ODataInlineEntitySet(version, null, ODataLinkType.ENTITY_SET_NAVIGATION, name, entitySet);
  }

  @Override
  public ODataInlineEntity newDeepInsertEntity(final String name, final ODataEntity entity) {
    return new ODataInlineEntity(version, null, ODataLinkType.ENTITY_NAVIGATION, name, entity);
  }
}
