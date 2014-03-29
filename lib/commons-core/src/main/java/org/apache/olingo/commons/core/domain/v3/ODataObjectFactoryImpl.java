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
package org.apache.olingo.commons.core.domain.v3;

import java.net.URI;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v3.ODataObjectFactory;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.domain.AbstractODataObjectFactory;

public class ODataObjectFactoryImpl extends AbstractODataObjectFactory implements ODataObjectFactory {

  public ODataObjectFactoryImpl(final ODataServiceVersion version) {
    super(version);
  }

  @Override
  public ODataEntitySet newEntitySet() {
    return new ODataEntitySetImpl();
  }

  @Override
  public ODataEntitySet newEntitySet(final URI next) {
    return new ODataEntitySetImpl(next);
  }

  @Override
  public ODataEntity newEntity(final String name) {
    return new ODataEntityImpl(name);
  }

  @Override
  public ODataEntity newEntity(final String name, final URI link) {
    final ODataEntityImpl result = new ODataEntityImpl(name);
    result.setLink(link);
    return result;
  }

  @Override
  public ODataProperty newPrimitiveProperty(final String name, final ODataPrimitiveValue value) {
    return new ODataPropertyImpl(name, value);
  }

  @Override
  public ODataProperty newComplexProperty(final String name, final ODataComplexValue value) {
    return new ODataPropertyImpl(name, value);
  }

  @Override
  public ODataProperty newCollectionProperty(final String name, final ODataCollectionValue value) {
    return new ODataPropertyImpl(name, value);
  }

}
