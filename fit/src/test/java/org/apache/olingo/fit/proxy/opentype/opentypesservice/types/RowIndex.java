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

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.OpenTypesServiceV4")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "RowIndex",
    openType = true,
    hasStream = false,
    isAbstract = false)
public interface RowIndex
    extends org.apache.olingo.ext.proxy.api.Annotatable,
    org.apache.olingo.ext.proxy.api.EntityType<RowIndex>, org.apache.olingo.ext.proxy.api.StructuredQuery<RowIndex>,
    AbstractOpenType {

  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Id",
      type = "Edm.Int32",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.Integer getId();

  void setId(java.lang.Integer _id);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Rows",
      type = "Microsoft.Test.OData.Services.OpenTypesServiceV4.Row",
      targetSchema = "Microsoft.Test.OData.Services.OpenTypesServiceV4",
      targetContainer = "DefaultContainer",
      targetEntitySet = "Row",
      containsTarget = false)
  Row getRows();

  void
      setRows(Row _rows);

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {
    // No additional methods needed for now.
  }

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Id",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getIdAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Rows",
        type = "Microsoft.Test.OData.Services.OpenTypesServiceV4.Row")
    org.apache.olingo.ext.proxy.api.Annotatable getRowsAnnotations();
  }

}
