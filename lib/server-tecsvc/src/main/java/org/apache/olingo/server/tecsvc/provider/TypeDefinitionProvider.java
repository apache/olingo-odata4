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
package org.apache.olingo.server.tecsvc.provider;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;

public class TypeDefinitionProvider {

  public static final FullQualifiedName nameTDString = new FullQualifiedName(SchemaProvider.NAMESPACE, "TDString");

  public CsdlTypeDefinition getTypeDefinition(final FullQualifiedName typeDefinitionName) {
    if (nameTDString.equals(typeDefinitionName)) {
      return new CsdlTypeDefinition().setName(nameTDString.getName())
          .setUnderlyingType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
          .setMaxLength(15);
    }
    return null;
  }
}
