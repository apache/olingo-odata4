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
package org.apache.olingo.fit.proxy.opentype.opentypesservice;

// CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.fit.proxy.opentype.opentypesservice.types.RowCollection;

// CHECKSTYLE:ON (Maven checkstyle)

@org.apache.olingo.ext.proxy.api.annotations.EntitySet(name = "Row",
    container = "Microsoft.Test.OData.Services.OpenTypesServiceV4.DefaultContainer")
public interface Row
    extends
    org.apache.olingo.ext.proxy.api.EntitySet<org.apache.olingo.fit.proxy.opentype.opentypesservice.types.Row, RowCollection>,
    org.apache.olingo.ext.proxy.api.StructuredCollectionQuery<Row>,
    AbstractEntitySet<org.apache.olingo.fit.proxy.opentype.opentypesservice.types.Row, java.util.UUID, RowCollection> {

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {
    // No additional methods needed for now.
  }
}
