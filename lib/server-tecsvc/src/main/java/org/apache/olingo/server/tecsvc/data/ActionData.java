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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.tecsvc.data.DataProvider.DataProviderException;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;

public class ActionData {

  /**
   * Performs the named action (i.e., does nothing, currently) and returns the primitive-type result.
   * @param name       name of the action
   * @param parameters parameters of the action 
   */
  protected static Property primitiveAction(final String name, final Map<String, Parameter> parameters)
      throws DataProviderException {
    if ("UARTString".equals(name)) {
      return DataCreator.createPrimitive(null, "UARTString string value");
    } else if ("UARTByteNineParam".equals(name)) {
      return FunctionData.primitiveComplexFunction("UFNRTByteNineParam", parameters, null);
    }else if("_A_RTTimeOfDay_".equals(name)){
        Parameter paramTimeOfDay = parameters.get("ParameterTimeOfDay");
        Calendar timeOfDay = Calendar.getInstance();
      if (paramTimeOfDay != null && !paramTimeOfDay.isNull()) {
        timeOfDay = (Calendar) paramTimeOfDay.asPrimitive();
      }
      return DataCreator.createPrimitive("ParameterTimeOfDay", timeOfDay);
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.",
        HttpStatusCode.NOT_IMPLEMENTED);
  }

  protected static Property primitiveCollectionAction(final String name, final Map<String, Parameter> parameters,
      final OData oData) throws DataProviderException {
    if ("UARTCollStringTwoParam".equals(name)) {
      Parameter paramInt16 = parameters.get("ParameterInt16");
      Parameter paramDuration = parameters.get("ParameterDuration");
      if (paramInt16 == null || paramInt16.isNull() || paramDuration == null || paramDuration.isNull()) {
        try {
          String param16String = valueAsString(paramInt16, EdmPrimitiveTypeKind.Int16, oData);
          String paramDurationString = valueAsString(paramDuration, EdmPrimitiveTypeKind.Duration, oData);

          return new Property(null, name, ValueType.COLLECTION_PRIMITIVE, Arrays.asList(
                  name + " int16 value: " + param16String,
                  name + " duration value: " + paramDurationString));
        } catch (EdmPrimitiveTypeException e) {
          throw new DataProviderException("EdmPrimitiveTypeException", HttpStatusCode.BAD_REQUEST, e);
        }
      }
      short loopCount = (Short) paramInt16.asPrimitive();
      BigDecimal duration = (BigDecimal) paramDuration.asPrimitive();
      EdmPrimitiveType primDuration = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Duration);
      BigDecimal addValue = new BigDecimal(1);
      List<Object> collectionValues = new ArrayList<Object>();
      for (int i = 0; i < loopCount; i++) {
        try {
          String value = primDuration.valueToString(duration, false, null, null, null, null);
          collectionValues.add(name + " duration value: " + value);
        } catch (EdmPrimitiveTypeException e) {
          throw new DataProviderException("EdmPrimitiveTypeException", HttpStatusCode.BAD_REQUEST, e);
        }
        duration = duration.add(addValue);
      }
      return new Property(null, name, ValueType.COLLECTION_PRIMITIVE, collectionValues);
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.",
        HttpStatusCode.NOT_IMPLEMENTED);
  }

  private static String valueAsString(final Parameter parameter, final EdmPrimitiveTypeKind kind, final OData oData)
      throws EdmPrimitiveTypeException {
    return parameter == null ? "null" :
        oData.createPrimitiveTypeInstance(kind)
            .valueToString(parameter.asPrimitive(), null, null, null, null, null);
  }

  protected static Property complexAction(final String name, final Map<String, Parameter> parameters)
      throws DataProviderException {
    if ("UARTCTTwoPrimParam".equals(name)) {
      Parameter paramInt16 = parameters.get("ParameterInt16");
      final Short number = paramInt16 == null || paramInt16.isNull() ?
          (short) 32767 :
          (Short) paramInt16.asPrimitive();
      return createCTTwoPrimComplexProperty(name, number, "UARTCTTwoPrimParam string value");
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.",
        HttpStatusCode.NOT_IMPLEMENTED);
  }

  private static Property createCTTwoPrimComplexProperty(final String name, final Short number, final String text) {
    return DataCreator.createComplex(name,
        ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
        DataCreator.createPrimitive("PropertyInt16", number),
        DataCreator.createPrimitive("PropertyString", text));
  }

  protected static Property complexCollectionAction(final String name, final Map<String, Parameter> parameters)
      throws DataProviderException {
    if ("UARTCollCTTwoPrimParam".equals(name)) {
      List<ComplexValue> complexCollection = new ArrayList<ComplexValue>();
      final Parameter paramInt16 = parameters.get("ParameterInt16");
      final Short number = paramInt16 == null || paramInt16.isNull() ? 0 : (Short) paramInt16.asPrimitive();
      if (number >= 1) {
        complexCollection.add(createCTTwoPrimComplexProperty(null, (short) 16, "Test123").asComplex());
      }
      if (number >= 2) {
        complexCollection.add(createCTTwoPrimComplexProperty(null, (short) 17, "Test456").asComplex());
      }
      if (number >= 3) {
        complexCollection.add(createCTTwoPrimComplexProperty(null, (short) 18, "Test678").asComplex());
      }
      return new Property(null, name, ValueType.COLLECTION_COMPLEX, complexCollection);
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.",
        HttpStatusCode.NOT_IMPLEMENTED);
  }

  protected static EntityActionResult entityAction(final String name, final Map<String, Parameter> parameters,
      final Map<String, EntityCollection> data, final OData oData, final Edm edm) throws DataProviderException {
    if ("UARTETTwoKeyTwoPrimParam".equals(name)) {
      Parameter parameter = parameters.get("ParameterInt16");
      final Short number = parameter == null || parameter.isNull() ? 0 : (Short) parameter.asPrimitive();

      EntityCollection entityCollection = data.get("ESTwoKeyTwoPrim");
      for (Entity entity : entityCollection.getEntities()) {
        Object asPrimitive = entity.getProperty("PropertyInt16").asPrimitive();
        if (number.equals(asPrimitive)) {
          return new EntityActionResult().setEntity(entity);
        }
      }
      // Entity Not found
      throw new DataProviderException("Entity not found with key: " + number, HttpStatusCode.NOT_FOUND);
    } else if ("UARTETAllPrimParam".equals(name)) {
      Parameter paramDate = parameters.get("ParameterDate");
      EntityCollection entityCollection = data.get("ESAllPrim");
      if (paramDate != null) {
        Calendar date = (Calendar) paramDate.asPrimitive();
        boolean freeKey;
        Short key = -1;
        do {
          freeKey = true;
          key++;
          for (Entity entity : entityCollection.getEntities()) {
            Short entityKey = (Short) entity.getProperty("PropertyInt16").asPrimitive();
            if (key.equals(entityKey)) {
              freeKey = false;
              break;
            }
          }
        } while (!freeKey);
        return new EntityActionResult().setEntity(
            createAllPrimEntity(key, "UARTETAllPrimParam string value", date, oData, edm))
            .setCreated(true);
      } else {
        return new EntityActionResult().setEntity(entityCollection.getEntities().get(0));
      }
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.",
        HttpStatusCode.NOT_IMPLEMENTED);
  }

  private static Entity createAllPrimEntity(final Short key, final String val, final Calendar date,
      final OData oData, final Edm edm) throws DataProviderException {
    Entity entity = new Entity().addProperty(DataCreator.createPrimitive("PropertyInt16", key))
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
    setEntityId(entity, "ESAllPrim", oData, edm);
    return entity;
  }

  protected static EntityCollection entityCollectionAction(final String name, final Map<String, Parameter> parameters,
      final OData oData, final Edm edm) throws DataProviderException {
    if ("UARTCollETKeyNavParam".equals(name)) {
      EntityCollection collection = new EntityCollection();
      Parameter paramInt16 = parameters.get("ParameterInt16");
      final Short number = paramInt16 == null || paramInt16.isNull() ? 0 : (Short) paramInt16.asPrimitive();
      if (number > 0) {
        for (short i = 1; i <= number; i++) {
          collection.getEntities().add(createETKeyNavEntity(i, oData, edm));
        }
      }
      return collection;
    } else if ("UARTCollETAllPrimParam".equals(name)) {
      EntityCollection collection = new EntityCollection();
      Parameter paramTimeOfDay = parameters.get("ParameterTimeOfDay");
      if (paramTimeOfDay != null && !paramTimeOfDay.isNull()) {
        Calendar timeOfDay = (Calendar) paramTimeOfDay.asPrimitive();
        int count = timeOfDay.get(Calendar.HOUR_OF_DAY);
        for (short i = 1; i <= count; i++) {
          collection.getEntities().add(
              createAllPrimEntity(i, "UARTCollETAllPrimParam int16 value: " + i, null, oData, edm));
        }
      }
      return collection;
    }
    throw new DataProviderException("Action " + name + " is not yet implemented.",
        HttpStatusCode.NOT_IMPLEMENTED);
  }

  @SuppressWarnings("unchecked")
  private static Entity createETKeyNavEntity(final Short number, final OData oData, final Edm edm)
      throws DataProviderException {
    Entity entity = new Entity()
        .addProperty(DataCreator.createPrimitive("PropertyInt16", number))
        .addProperty(DataCreator.createPrimitive("PropertyString", "UARTCollETKeyNavParam int16 value: " + number))
        .addProperty(DataCreator.createComplex("PropertyCompNav",
            ComplexTypeProvider.nameCTNavFiveProp.getFullQualifiedNameAsString(),
            DataCreator.createPrimitive("PropertyInt16", (short) 0)))
        .addProperty(createKeyNavAllPrimComplexValue("PropertyCompAllPrim"))
        .addProperty(DataCreator.createComplex("PropertyCompTwoPrim", 
              ComplexTypeProvider.nameCTTwoPrim.getFullQualifiedNameAsString(),
              DataCreator.createPrimitive("PropertyInt16", (short) 0),
              DataCreator.createPrimitive("PropertyString", ""))).addProperty(
              DataCreator.createPrimitiveCollection("CollPropertyString"))
        .addProperty(DataCreator.createPrimitiveCollection("CollPropertyInt16"))
        .addProperty(DataCreator.createComplexCollection("CollPropertyComp",
            ComplexTypeProvider.nameCTPrimComp.getFullQualifiedNameAsString()))
        .addProperty(DataCreator.createComplex("PropertyCompCompNav",
            ComplexTypeProvider.nameCTCompNav.getFullQualifiedNameAsString(),
            DataCreator.createPrimitive("PropertyString", ""),
            DataCreator.createComplex("PropertyCompNav", 
                ComplexTypeProvider.nameCTNavFiveProp.getFullQualifiedNameAsString(),
                DataCreator.createPrimitive("PropertyInt16", (short) 0))));
    setEntityId(entity, "ESKeyNav", oData, edm);
    return entity;
  }

  private static void setEntityId(Entity entity, final String entitySetName, final OData oData, final Edm edm)
      throws DataProviderException {
    try {
      entity.setId(URI.create(oData.createUriHelper().buildCanonicalURL(
          edm.getEntityContainer().getEntitySet(entitySetName), entity)));
    } catch (final SerializerException e) {
      throw new DataProviderException("Unable to set entity ID!", HttpStatusCode.INTERNAL_SERVER_ERROR, e);
    }
  }

  protected static Property createKeyNavAllPrimComplexValue(final String name) {
    return DataCreator.createComplex(name,
        ComplexTypeProvider.nameCTAllPrim.getFullQualifiedNameAsString(),
        DataCreator.createPrimitive("PropertyString", ""),
        DataCreator.createPrimitive("PropertyBinary", new byte[] {}),
        DataCreator.createPrimitive("PropertyBoolean", false),
        DataCreator.createPrimitive("PropertyByte", (short) 0),
        DataCreator.createPrimitive("PropertyDate", null),
        DataCreator.createPrimitive("PropertyDateTimeOffset", null),
        DataCreator.createPrimitive("PropertyDecimal", BigDecimal.valueOf(0)),
        DataCreator.createPrimitive("PropertySingle", (float) 0),
        DataCreator.createPrimitive("PropertyDouble", 0D),
        DataCreator.createPrimitive("PropertyDuration", BigDecimal.valueOf(0)),
        DataCreator.createPrimitive("PropertyGuid", null),
        DataCreator.createPrimitive("PropertyInt16", null),
        DataCreator.createPrimitive("PropertyInt32", null),
        DataCreator.createPrimitive("PropertyInt64", null),
        DataCreator.createPrimitive("PropertySByte", null),
        DataCreator.createPrimitive("PropertyTimeOfDay", null));
  }
}
