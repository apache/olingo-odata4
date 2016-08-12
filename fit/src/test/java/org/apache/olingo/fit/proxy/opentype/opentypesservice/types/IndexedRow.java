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
package org.apache.olingo.fit.proxy.opentype.opentypesservice.types;

import java.util.concurrent.Future;

import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.OpenTypesServiceV4")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "IndexedRow",
    openType = true,
    hasStream = false,
    isAbstract = false,
    baseType = "Microsoft.Test.OData.Services.OpenTypesServiceV4.Row")
public interface IndexedRow extends Row {

  @Override
  IndexedRow load();

  @Override
  Future<? extends IndexedRow> loadAsync();

  @Override
  IndexedRow refs();

  @Override
  IndexedRow expand(String... expand);

  @Override
  IndexedRow select(String... select);

  @Override
  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Id",
      type = "Edm.Guid",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.util.UUID getId();

  @Override
  void setId(java.util.UUID _id);

  @Override
  Operations operations();

  interface Operations extends
          Row.Operations {
    // No additional methods needed for now.
  }

  @Override
  Annotations annotations();

  interface Annotations extends
          Row.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Id",
        type = "Edm.Guid")
    org.apache.olingo.ext.proxy.api.Annotatable getIdAnnotations();

  }

}
