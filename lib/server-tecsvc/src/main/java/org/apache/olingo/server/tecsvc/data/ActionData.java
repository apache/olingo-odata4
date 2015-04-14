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
package org.apache.olingo.server.tecsvc.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.tecsvc.data.DataProvider.DataProviderException;

public class ActionData {

  protected static Property primitiveAction(String name, Map<String, Parameter> parameters)
      throws DataProviderException {
    if ("UARTString".equals(name)) {
      return DataCreator.createPrimitive(null, "UARTString string value");
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.");
  }

  protected static Property primitiveCollectionAction(String name, Map<String, Parameter> parameters)
      throws DataProviderException {
    if ("UARTCollStringTwoParam".equals(name)) {
      List<Object> collectionValues = new ArrayList<Object>();
      int loopCount = (Integer) parameters.get("ParameterInt16").asPrimitive();
      EdmPrimitiveType primDuration = OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Duration);
      BigDecimal duration = (BigDecimal) parameters.get("ParameterDuration").asPrimitive();
      BigDecimal addValue = new BigDecimal(1);
      for (int i = 0; i < loopCount; i++) {
        try {
          String value = primDuration.valueToString(duration, false, null, null, null, null);
          collectionValues.add(value);
        } catch (EdmPrimitiveTypeException e) {
          throw new DataProviderException("EdmPrimitiveTypeException", e);
        }
        duration = duration.add(addValue);
      }
      return DataCreator.createPrimitiveCollection(null, collectionValues);
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.");
  }

  protected static Property complexAction(String name, Map<String, Parameter> parameters) throws DataProviderException {
    if ("UARTCTTwoPrimParam".equals(name)) {
      Integer number = (Integer) parameters.get("ParameterInt16").asPrimitive();
      if (number == null) {
        number = new Integer(32767);
      }
      Property complexProp = createCTTwoPrimComplexProperty(number, "UARTCTTwoPrimParam string value");

      return complexProp;
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.");
  }

  private static Property createCTTwoPrimComplexProperty(Integer number, String text) {
    List<Property> props = new ArrayList<Property>();
    Property propInt = new Property();
    propInt.setName("PropertyInt16");
    propInt.setValue(ValueType.PRIMITIVE, number);
    props.add(propInt);
    Property propString = new Property();
    propString.setName("PropertyString");
    propString.setValue(ValueType.PRIMITIVE, text);
    props.add(propString);

    Property complexProp = new Property();
    complexProp.setValue(ValueType.COMPLEX, props);
    return complexProp;
  }

  protected static Property complexCollectionAction(String name, Map<String, Parameter> parameters)
      throws DataProviderException {
    if ("UARTCollCTTwoPrimParam".equals(name)) {
      ArrayList<Property> complexCollection = new ArrayList<Property>();
      complexCollection.add(createCTTwoPrimComplexProperty(16, "Test123"));
      complexCollection.add(createCTTwoPrimComplexProperty(17, "Test456"));
      complexCollection.add(createCTTwoPrimComplexProperty(18, "Test678"));

      Integer number = (Integer) parameters.get("ParameterInt16").asPrimitive();
      if (number != null && number >= 0 && number < complexCollection.size()) {
        complexCollection.subList(number, complexCollection.size() - 1).clear();
      }

      Property complexCollProperty = new Property();
      complexCollProperty.setValue(ValueType.COLLECTION_COMPLEX, complexCollection);
      return complexCollProperty;
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.");
  }

  protected static Entity entityAction(String name, Map<String, Parameter> parameters) throws DataProviderException {
    if ("UARTETTwoKeyTwoPrimParam".equals(name)) {
      Integer number = (Integer) parameters.get("ParameterInt16").asPrimitive();
      if (number == null) {
        number = 0;
      }
      EntityCollection entityCollection = new DataCreator().getData().get("ESTwoKeyTwoPrim");
      for (Entity entity : entityCollection.getEntities()) {
        if (number.equals(entity.getProperty("PropertyInt16").asPrimitive())) {
          return entity;
        }
      }
      // Entity Not found
      throw new DataProviderException("Entity not found with key: " + number, HttpStatusCode.NOT_FOUND);
    } else if ("UARTETAllPrimParam".equals(name)) {
      Calendar date = (Calendar) parameters.get("ParameterDate").asPrimitive();
      EntityCollection entityCollection = new DataCreator().getData().get("ESAllPrim");
      if (date != null) {
        boolean freeKey;
        Short key = 0;
        do {
          freeKey = true;
          for (Entity entity : entityCollection.getEntities()) {
            if (key.equals(entity.getProperty("PropertyInt16"))) {
              freeKey = false;
              break;
            }
          }
          key++;
        } while (!freeKey);
        // TODO: Set create response code
        return createAllPrimEntity(key, "UARTETAllPrimParam string value", date);
      } else {
        return entityCollection.getEntities().get(0);
      }
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.");
  }

  private static Entity createAllPrimEntity(Short key, String val, Calendar date) {
    return new Entity().addProperty(DataCreator.createPrimitive("PropertyInt16", key))
        .addProperty(DataCreator.createPrimitive("PropertyString", val))
        .addProperty(DataCreator.createPrimitive("PropertyBoolean", false))
        .addProperty(DataCreator.createPrimitive("PropertyByte", null))
        .addProperty(DataCreator.createPrimitive("PropertySByte", null))
        .addProperty(DataCreator.createPrimitive("PropertyInt32", null))
        .addProperty(DataCreator.createPrimitive("PropertyInt64", null))
        .addProperty(DataCreator.createPrimitive("PropertySingle", null))
        .addProperty(DataCreator.createPrimitive("PropertyDouble", null))
        .addProperty(DataCreator.createPrimitive("PropertyDecimal", null))
        .addProperty(DataCreator.createPrimitive("PropertyBinary", null))
        .addProperty(DataCreator.createPrimitive("PropertyDate", date))
        .addProperty(DataCreator.createPrimitive("PropertyDateTimeOffset", null))
        .addProperty(DataCreator.createPrimitive("PropertyDuration", null))
        .addProperty(DataCreator.createPrimitive("PropertyGuid", null))
        .addProperty(DataCreator.createPrimitive("PropertyTimeOfDay", null));
  }

  protected static EntityCollection entityCollectionAction(String name, Map<String, Parameter> parameters)
      throws DataProviderException {
    if ("UARTCollETKeyNavParam".equals(name)) {
      Short number = (Short) parameters.get("ParameterInt16").asPrimitive();
      EntityCollection collection = new EntityCollection();
      if (number != null && number > 0) {
        for (int i = 1; i <= number; i++) {
          collection.getEntities().add(createETKeyNavEntity(number));
        }
      } else {
        return collection;
      }
    } else if ("UARTCollETAllPrimParam".equals(name)) {
      Calendar timeOfDay = (Calendar) parameters.get("ParameterTimeOfDay").asPrimitive();
      EntityCollection collection = new EntityCollection();
      if (timeOfDay != null) {
        int count = timeOfDay.get(Calendar.HOUR_OF_DAY);
        for (short i = 1; i <= count; i++) {
          collection.getEntities().add(createAllPrimEntity(i, "UARTCollETAllPrimParam int16 value: " + i, null));
        }
      } else {
        return collection;
      }  
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.");
  }

  @SuppressWarnings("unchecked")
  private static Entity createETKeyNavEntity(Short number) {
    return new Entity()
        .addProperty(DataCreator.createPrimitive("PropertyInt16", number))
        .addProperty(DataCreator.createPrimitive("PropertyString", "UARTCollETKeyNavParam int16 value: " + number))
        .addProperty(
            DataCreator.createComplex("PropertyCompNav", DataCreator.createPrimitive("PropertyInt16", 0)))
        .addProperty(createKeyNavAllPrimComplexValue("PropertyCompAllPrim")).addProperty(
            DataCreator.createComplex("PropertyCompTwoPrim", DataCreator.createPrimitive("PropertyInt16", 0),
                DataCreator.createPrimitive("PropertyString", ""))).addProperty(
            DataCreator.createPrimitiveCollection("CollPropertyString"))
        .addProperty(DataCreator.createPrimitiveCollection("CollPropertyInt16")).addProperty(
            DataCreator.createComplexCollection("CollPropertyComp"))
        .addProperty(
            DataCreator.createComplex("PropertyCompCompNav", DataCreator.createPrimitive("PropertyString", ""),
                DataCreator.createComplex("PropertyCompNav", DataCreator.createPrimitive("PropertyInt16", 0))));
  }

  protected static Property createKeyNavAllPrimComplexValue(final String name) {
    return DataCreator.createComplex(name,
        DataCreator.createPrimitive("PropertyString", ""),
        DataCreator.createPrimitive("PropertyBinary", new byte[] {}),
        DataCreator.createPrimitive("PropertyBoolean", false),
        DataCreator.createPrimitive("PropertyByte", 0),
        DataCreator.createPrimitive("PropertyDate", null),
        DataCreator.createPrimitive("PropertyDateTimeOffset", null),
        DataCreator.createPrimitive("PropertyDecimal", 0),
        DataCreator.createPrimitive("PropertySingle", 0),
        DataCreator.createPrimitive("PropertyDouble", 0),
        DataCreator.createPrimitive("PropertyDuration", 0),
        DataCreator.createPrimitive("PropertyGuid", null),
        DataCreator.createPrimitive("PropertyInt16", null),
        DataCreator.createPrimitive("PropertyInt32", null),
        DataCreator.createPrimitive("PropertyInt64", null),
        DataCreator.createPrimitive("PropertySByte", null),
        DataCreator.createPrimitive("PropertyTimeOfDay", null));
  }
}
