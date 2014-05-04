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

import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.domain.AbstractODataPrimitiveValue;

public class ODataPrimitiveValueImpl extends AbstractODataPrimitiveValue {

  private static final long serialVersionUID = -5201738902625613179L;

  public static class BuilderImpl extends AbstractBuilder {

    private final ODataPrimitiveValueImpl instance;

    public BuilderImpl(final ODataServiceVersion version) {
      super(version);
      this.instance = new ODataPrimitiveValueImpl();
    }

    @Override
    protected AbstractODataPrimitiveValue getInstance() {
      return instance;
    }

    @Override
    public ODataPrimitiveValueImpl build() {
      return (ODataPrimitiveValueImpl) super.build();
    }

  }

}
