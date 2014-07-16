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

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.edm.provider.EnumMember;
import org.apache.olingo.server.api.edm.provider.EnumType;

import java.util.Arrays;

public class EnumTypeProvider {

  public static final FullQualifiedName nameENString = new FullQualifiedName(SchemaProvider.nameSpace, "ENString");

  public EnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
    if (enumTypeName.equals(nameENString)) {
      return new EnumType()
          .setName("ENString")
          .setFlags(true)
          .setUnderlyingType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName())
          .setMembers(Arrays.asList(
              new EnumMember().setName("String1").setValue("1"),
              new EnumMember().setName("String2").setValue("2"),
              new EnumMember().setName("String3").setValue("3")));
    }

    return null;
  }
}
