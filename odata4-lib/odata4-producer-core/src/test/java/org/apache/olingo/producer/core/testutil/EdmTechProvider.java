/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.producer.core.testutil;

import java.util.Arrays;


import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.helper.EntityContainerInfo;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.EdmProviderAdapter;
import org.apache.olingo.commons.api.edm.provider.EntitySet;
import org.apache.olingo.commons.api.edm.provider.EntityType;
import org.apache.olingo.commons.api.edm.provider.Property;
import org.apache.olingo.commons.api.edm.provider.PropertyRef;
import org.apache.olingo.commons.api.exception.ODataException;
import org.apache.olingo.commons.api.exception.ODataNotImplementedException;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.core.edm.provider.EdmEntityContainerImpl;

public class EdmTechProvider extends EdmProviderAdapter {

  Property propertyInt16NotNullable = new Property()
      .setName("PropertyInt16")
      .setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName())
      .setNullable(false);
//Simple typed Properties
  Property propertyBinary = new Property()
      .setName("PropertyBinary")
      .setType(EdmPrimitiveTypeKind.Binary.getFullQualifiedName());
  Property propertyBoolean = new Property()
      .setName("PropertyBoolean")
      .setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());
  Property propertyByte = new Property()
      .setName("PropertyByte")
      .setType(EdmPrimitiveTypeKind.Byte.getFullQualifiedName());
  Property propertyDate = new Property()
      .setName("PropertyDate")
      .setType(EdmPrimitiveTypeKind.Date.getFullQualifiedName());
  Property propertyDateTimeOffset = new Property()
      .setName("PropertyDateTimeOffset")
      .setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName());
  Property propertyDecimal = new Property()
      .setName("PropertyDecimal")
      .setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName());
  Property propertyDouble = new Property()
      .setName("PropertyDouble")
      .setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName());
  Property propertyDuration = new Property()
      .setName("PropertyDuration")
      .setType(EdmPrimitiveTypeKind.Duration.getFullQualifiedName());
  Property propertyGuid = new Property()
      .setName("PropertyGuid")
      .setType(EdmPrimitiveTypeKind.Guid.getFullQualifiedName());
  Property propertyInt16 = new Property()
      .setName("PropertyInt16")
      .setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName());
  Property propertyInt32 = new Property()
      .setName("PropertyInt32")
      .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
  Property propertyInt64 = new Property()
      .setName("PropertyInt64")
      .setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
  Property propertySByte = new Property()
      .setName("PropertySByte")
      .setType(EdmPrimitiveTypeKind.SByte.getFullQualifiedName());
  Property propertySingle = new Property()
      .setName("PropertySingle")
      .setType(EdmPrimitiveTypeKind.Single.getFullQualifiedName());
  Property propertyString = new Property()
      .setName("PropertyString")
      .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
  Property propertyTimeOfDay = new Property()
      .setName("PropertyTimeOfDay")
      .setType(EdmPrimitiveTypeKind.TimeOfDay.getFullQualifiedName());

  // Properties typed as collection of simple types
  Property collectionPropertyBinary = new Property()
      .setName("CollPropertyBinary")
      .setType(EdmPrimitiveTypeKind.Binary.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyBoolean = new Property()
      .setName("CollPropertyBoolean")
      .setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyByte = new Property()
      .setName("CollPropertyByte")
      .setType(EdmPrimitiveTypeKind.Byte.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyDate = new Property()
      .setName("CollPropertyDate")
      .setType(EdmPrimitiveTypeKind.Date.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyDateTimeOffset = new Property()
      .setName("CollPropertyDateTimeOffset")
      .setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyDecimal = new Property()
      .setName("CollPropertyDecimal")
      .setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyDouble = new Property()
      .setName("CollPropertyDouble")
      .setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyDuration = new Property()
      .setName("CollPropertyDuration")
      .setType(EdmPrimitiveTypeKind.Duration.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyGuid = new Property()
      .setName("CollPropertyGuid")
      .setType(EdmPrimitiveTypeKind.Guid.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyInt16 = new Property()
      .setName("CollPropertyInt16")
      .setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyInt32 = new Property()
      .setName("CollPropertyInt32")
      .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyInt64 = new Property()
      .setName("CollPropertyInt64")
      .setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertySByte = new Property()
      .setName("CollPropertySByte")
      .setType(EdmPrimitiveTypeKind.SByte.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertySingle = new Property()
      .setName("CollPropertySingle")
      .setType(EdmPrimitiveTypeKind.Single.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyString = new Property()
      .setName("CollPropertyString")
      .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
      .setCollection(true);
  Property collectionPropertyTimeOfDay = new Property()
      .setName("CollPropertyTimeOfDay")
      .setType(EdmPrimitiveTypeKind.TimeOfDay.getFullQualifiedName())
      .setCollection(true);

//  EdmEntityContainer entityContainerTest1 = new EdmEntityContainerImpl(
//      new EntityContainerInfo()
//          .setContainerName(new FullQualifiedName("com.sap.odata.test1", "Container"))
//      );

  @Override
  public EntitySet getEntitySet(final FullQualifiedName entityContainer, final String name) throws ODataException {
    if (entityContainer == null) {
      if (name.equals("ESAllPrim")) {

        return new EntitySet()
            .setName("ETAllPrim")
            .setType(new FullQualifiedName("com.sap.odata.test1", "ESAllPrim"));

      } else if (name.equals("ESCollAllPrim")) {

        return new EntitySet()
            .setName("ESCollAllPrim")
            .setType(new FullQualifiedName("com.sap.odata.test1", "ETCollAllPrim"));

      }
    }

    throw new ODataNotImplementedException();
  }

  @Override
  public EntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {

    if (entityTypeName.equals(new FullQualifiedName("com.sap.odata.test1", "ETAllPrim"))) {
      return new EntityType()
          .setName("ETAllPrim")
          .setProperties(Arrays.asList(
              propertyBinary, propertyBoolean, propertyByte,
              propertyDate, propertyDateTimeOffset, propertyDecimal,
              propertyDouble, propertyDuration, propertyDecimal,
              propertyInt16NotNullable, propertyInt32, propertyInt64,
              propertySByte, propertySingle, propertyString,
              propertyTimeOfDay))
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyInt16")));

    } else if (entityTypeName.equals(new FullQualifiedName("com.sap.odata.test1", "ETCollAllPrim"))) {
      return new EntityType()
          .setName("ETCollAllPrim")
          .setProperties(Arrays.asList(
              propertyInt16NotNullable,
              collectionPropertyBinary, collectionPropertyBoolean, collectionPropertyByte,
              collectionPropertyDate, collectionPropertyDateTimeOffset, collectionPropertyDecimal,
              collectionPropertyDouble, collectionPropertyDuration, collectionPropertyDecimal,
              collectionPropertyInt16, collectionPropertyInt32, collectionPropertyInt64,
              collectionPropertySByte, collectionPropertySingle, collectionPropertyString,
              collectionPropertyTimeOfDay))
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("PropertyInt16")));
    }

    throw new ODataNotImplementedException();
  }
}
