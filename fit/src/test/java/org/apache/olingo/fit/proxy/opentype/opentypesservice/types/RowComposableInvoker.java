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

// CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.AbstractOpenType;

// CHECKSTYLE:ON (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;

public interface RowComposableInvoker
    extends org.apache.olingo.ext.proxy.api.StructuredComposableInvoker<Row, Row.Operations>
    , AbstractOpenType {

  @Override
  RowComposableInvoker select(String... select);

  @Override
  RowComposableInvoker expand(String... expand);

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

  void setId(java.util.UUID _id);

}
