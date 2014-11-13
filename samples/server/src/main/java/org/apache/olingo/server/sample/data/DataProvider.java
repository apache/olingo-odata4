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
package org.apache.olingo.server.sample.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.server.api.uri.UriParameter;

public class DataProvider {

  private Map<String, EntitySet> data;

  public DataProvider() {
    data = new HashMap<String, EntitySet>();
    data.put("Cars", createCars());
    data.put("Manufacturers", createManufacturers());
  }

  public EntitySet readAll(EdmEntitySet edmEntitySet) {
    return data.get(edmEntitySet.getName());
  }

  public Entity read(final EdmEntitySet edmEntitySet, final List<UriParameter> keys) throws DataProviderException {
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final EntitySet entitySet = data.get(edmEntitySet.getName());
    if (entitySet == null) {
      return null;
    } else {
      try {
        for (final Entity entity : entitySet.getEntities()) {
          boolean found = true;
          for (final UriParameter key : keys) {
            final EdmProperty property = (EdmProperty) entityType.getProperty(key.getName());
            final EdmPrimitiveType type = (EdmPrimitiveType) property.getType();
            if (!type.valueToString(entity.getProperty(key.getName()).getValue(),
                property.isNullable(), property.getMaxLength(), property.getPrecision(), property.getScale(),
                property.isUnicode())
                .equals(key.getText())) {
              found = false;
              break;
            }
          }
          if (found) {
            return entity;
          }
        }
        return null;
      } catch (final EdmPrimitiveTypeException e) {
        throw new DataProviderException("Wrong key!", e);
      }
    }
  }

  public static class DataProviderException extends ODataException {
    private static final long serialVersionUID = 5098059649321796156L;

    public DataProviderException(String message, Throwable throwable) {
      super(message, throwable);
    }

    public DataProviderException(String message) {
      super(message);
    }
  }

  private EntitySet createCars() {
    EntitySet entitySet = new EntitySetImpl();

    entitySet.getEntities().add(new EntityImpl()
        .addProperty(createPrimitive("Id", 1))
        .addProperty(createPrimitive("Model", "F1 W03"))
        .addProperty(createPrimitive("ModelYear", "2012"))
        .addProperty(createPrimitive("Price", 189189.43))
        .addProperty(createPrimitive("Currency", "EUR")));

    entitySet.getEntities().add(new EntityImpl()
        .addProperty(createPrimitive("Id", 2))
        .addProperty(createPrimitive("Model", "F1 W04"))
        .addProperty(createPrimitive("ModelYear", "2013"))
        .addProperty(createPrimitive("Price", 199999.99))
        .addProperty(createPrimitive("Currency", "EUR")));

    entitySet.getEntities().add(new EntityImpl()
        .addProperty(createPrimitive("Id", 3))
        .addProperty(createPrimitive("Model", "F2012"))
        .addProperty(createPrimitive("ModelYear", "2012"))
        .addProperty(createPrimitive("Price", 137285.33))
        .addProperty(createPrimitive("Currency", "EUR")));

    entitySet.getEntities().add(new EntityImpl()
        .addProperty(createPrimitive("Id", 4))
        .addProperty(createPrimitive("Model", "F2013"))
        .addProperty(createPrimitive("ModelYear", "2013"))
        .addProperty(createPrimitive("Price", 145285.00))
        .addProperty(createPrimitive("Currency", "EUR")));

    entitySet.getEntities().add(new EntityImpl()
        .addProperty(createPrimitive("Id", 5))
        .addProperty(createPrimitive("Model", "F1 W02"))
        .addProperty(createPrimitive("ModelYear", "2011"))
        .addProperty(createPrimitive("Price", 167189.00))
        .addProperty(createPrimitive("Currency", "EUR")));

    return entitySet;
  }

  private EntitySet createManufacturers() {
    EntitySet entitySet = new EntitySetImpl();

    entitySet.getEntities().add(new EntityImpl()
        .addProperty(createPrimitive("Id", 1))
        .addProperty(createPrimitive("Name", "Star Powered Racing"))
        .addProperty(createAddress("Star Street 137", "Stuttgart", "70173", "Germany")));

    entitySet.getEntities().add(new EntityImpl()
        .addProperty(createPrimitive("Id", 2))
        .addProperty(createPrimitive("Name", "Horse Powered Racing"))
        .addProperty(createAddress("Horse Street 1", "Maranello", "41053", "Italy")));

    return entitySet;
  }

  private Property createAddress(final String street, final String city, final String zipCode, final String country) {
    List<Property> addressProperties = new ArrayList<Property>();
    addressProperties.add(createPrimitive("Street", street));
    addressProperties.add(createPrimitive("City", city));
    addressProperties.add(createPrimitive("ZipCode", zipCode));
    addressProperties.add(createPrimitive("Country", country));
    return new PropertyImpl(null, "Address", ValueType.COMPLEX, addressProperties);
  }

  private Property createPrimitive(final String name, final Object value) {
    return new PropertyImpl(null, name, ValueType.PRIMITIVE, value);
  }
}
