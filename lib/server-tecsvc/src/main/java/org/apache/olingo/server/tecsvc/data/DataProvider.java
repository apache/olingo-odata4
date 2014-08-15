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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.LinkedComplexValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.LinkedComplexValueImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.server.api.uri.UriParameter;

public class DataProvider {

  private static final UUID GUID = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");

  private Map<String, EntitySet> data;

  public DataProvider() {
    data = new HashMap<String, EntitySet>();
    data.put("ESTwoPrim", createESTwoPrim());
    data.put("ESAllPrim", createESAllPrim());
    data.put("ESCompAllPrim", createESCompAllPrim());
    data.put("ESCollAllPrim", createESCollAllPrim());
    data.put("ESMixPrimCollComp", createESMixPrimCollComp());
    data.put("ESAllKey", createESAllKey());
    data.put("ESMedia", createESMedia());
  }

  public EntitySet readAll(final EdmEntitySet edmEntitySet) throws DataProviderException {
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

  private EntitySet createESTwoPrim() {
    EntitySet entitySet = new EntitySetImpl();

    Entity entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", 32766));
    entity.addProperty(createPrimitive("PropertyString", "Test String1"));
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", -365));
    entity.addProperty(createPrimitive("PropertyString", "Test String2"));
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", -32766));
    entity.addProperty(createPrimitive("PropertyString", "Test String3"));
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE));
    entity.addProperty(createPrimitive("PropertyString", "Test String4"));
    entitySet.getEntities().add(entity);

    return entitySet;
  }

  private EntitySet createESAllPrim() {
    EntitySet entitySet = new EntitySetImpl();

    Entity entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE));
    entity.addProperty(createPrimitive("PropertyString", "First Resource - positive values"));
    entity.addProperty(createPrimitive("PropertyBoolean", true));
    entity.addProperty(createPrimitive("PropertyByte", 255));
    entity.addProperty(createPrimitive("PropertySByte", Byte.MAX_VALUE));
    entity.addProperty(createPrimitive("PropertyInt32", Integer.MAX_VALUE));
    entity.addProperty(createPrimitive("PropertyInt64", Long.MAX_VALUE));
    entity.addProperty(createPrimitive("PropertySingle", 1.79000000E+20));
    entity.addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+19));
    entity.addProperty(createPrimitive("PropertyDecimal", 34));
    entity.addProperty(createPrimitive("PropertyBinary",
        new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }));
    entity.addProperty(createPrimitive("PropertyDate", getDateTime(2012, 12, 3, 0, 0, 0)));
    entity.addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2012, 12, 3, 7, 16, 23)));
    entity.addProperty(createPrimitive("PropertyDuration", 6));
    entity.addProperty(createPrimitive("PropertyGuid", GUID));
    entity.addProperty(createPrimitive("PropertyTimeOfDay", getTime(3, 26, 5)));
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", Short.MIN_VALUE));
    entity.addProperty(createPrimitive("PropertyString", "Second Resource - negative values"));
    entity.addProperty(createPrimitive("PropertyBoolean", false));
    entity.addProperty(createPrimitive("PropertyByte", 0));
    entity.addProperty(createPrimitive("PropertySByte", Byte.MIN_VALUE));
    entity.addProperty(createPrimitive("PropertyInt32", Integer.MIN_VALUE));
    entity.addProperty(createPrimitive("PropertyInt64", Long.MIN_VALUE));
    entity.addProperty(createPrimitive("PropertySingle", -1.79000000E+08));
    entity.addProperty(createPrimitive("PropertyDouble", -1.7900000000000000E+05));
    entity.addProperty(createPrimitive("PropertyDecimal", -34));
    entity.addProperty(createPrimitive("PropertyBinary",
        new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }));
    entity.addProperty(createPrimitive("PropertyDate", getDateTime(2015, 11, 5, 0, 0, 0)));
    entity.addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 7, 17, 8)));
    entity.addProperty(createPrimitive("PropertyDuration", 9));
    entity.addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789dddfff")));
    entity.addProperty(createPrimitive("PropertyTimeOfDay", getTime(23, 49, 14)));
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", 0));
    entity.addProperty(createPrimitive("PropertyString", ""));
    entity.addProperty(createPrimitive("PropertyBoolean", false));
    entity.addProperty(createPrimitive("PropertyByte", 0));
    entity.addProperty(createPrimitive("PropertySByte", 0));
    entity.addProperty(createPrimitive("PropertyInt32", 0));
    entity.addProperty(createPrimitive("PropertyInt64", 0));
    entity.addProperty(createPrimitive("PropertySingle", 0));
    entity.addProperty(createPrimitive("PropertyDouble", 0));
    entity.addProperty(createPrimitive("PropertyDecimal", 0));
    entity.addProperty(createPrimitive("PropertyBinary", new byte[] {}));
    entity.addProperty(createPrimitive("PropertyDate", getDateTime(1970, 1, 1, 0, 0, 0)));
    entity.addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2005, 12, 3, 0, 0, 0)));
    entity.addProperty(createPrimitive("PropertyDuration", 0));
    entity.addProperty(createPrimitive("PropertyGuid", UUID.fromString("76543201-23ab-cdef-0123-456789cccddd")));
    entity.addProperty(createPrimitive("PropertyTimeOfDay", getTime(0, 1, 1)));
    entitySet.getEntities().add(entity);

    return entitySet;
  }

  private EntitySet createESCompAllPrim() {
    EntitySet entitySet = new EntitySetImpl();

    Entity entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE));
    LinkedComplexValue complexValue = new LinkedComplexValueImpl();
    complexValue.getValue().add(createPrimitive("PropertyString", "First Resource - first"));
    complexValue.getValue().add(createPrimitive("PropertyBinary",
        new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }));
    complexValue.getValue().add(createPrimitive("PropertyBoolean", true));
    complexValue.getValue().add(createPrimitive("PropertyByte", 255));
    complexValue.getValue().add(createPrimitive("PropertyDate", getDateTime(2012, 10, 3, 0, 0, 0)));
    complexValue.getValue().add(createPrimitive("PropertyDateTimeOffset",
        getTimestamp(2012, 10, 3, 7, 16, 23, 123456700)));
    complexValue.getValue().add(createPrimitive("PropertyDecimal", 34.27));
    complexValue.getValue().add(createPrimitive("PropertySingle", 1.79000000E+20));
    complexValue.getValue().add(createPrimitive("PropertyDouble", -1.7900000000000000E+19));
    complexValue.getValue().add(createPrimitive("PropertyDuration", 6));
    complexValue.getValue().add(createPrimitive("PropertyGuid", GUID));
    complexValue.getValue().add(createPrimitive("PropertyInt16", Short.MAX_VALUE));
    complexValue.getValue().add(createPrimitive("PropertyInt32", Integer.MAX_VALUE));
    complexValue.getValue().add(createPrimitive("PropertyInt64", Long.MAX_VALUE));
    complexValue.getValue().add(createPrimitive("PropertySByte", Byte.MAX_VALUE));
    complexValue.getValue().add(createPrimitive("PropertyTimeOfDay", getTime(1, 0, 1)));
    entity.addProperty(new PropertyImpl(null, "PropertyComp", ValueType.LINKED_COMPLEX, complexValue));
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", 7));
    complexValue = new LinkedComplexValueImpl();
    complexValue.getValue().add(createPrimitive("PropertyString", "Second Resource - second"));
    complexValue.getValue().add(createPrimitive("PropertyBinary",
        new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }));
    complexValue.getValue().add(createPrimitive("PropertyBoolean", true));
    complexValue.getValue().add(createPrimitive("PropertyByte", 255));
    complexValue.getValue().add(createPrimitive("PropertyDate", getDateTime(2013, 11, 4, 0, 0, 0)));
    complexValue.getValue().add(createPrimitive("PropertyDateTimeOffset",
        getDateTime(2013, 11, 4, 7, 16, 23)));
    complexValue.getValue().add(createPrimitive("PropertyDecimal", 34.27));
    complexValue.getValue().add(createPrimitive("PropertySingle", 1.79000000E+20));
    complexValue.getValue().add(createPrimitive("PropertyDouble", -1.7900000000000000E+02));
    complexValue.getValue().add(createPrimitive("PropertyDuration", 6));
    complexValue.getValue().add(createPrimitive("PropertyGuid", GUID));
    complexValue.getValue().add(createPrimitive("PropertyInt16", 25));
    complexValue.getValue().add(createPrimitive("PropertyInt32", Integer.MAX_VALUE));
    complexValue.getValue().add(createPrimitive("PropertyInt64", Long.MAX_VALUE));
    complexValue.getValue().add(createPrimitive("PropertySByte", Byte.MAX_VALUE));
    complexValue.getValue().add(createPrimitive("PropertyTimeOfDay", getTimestamp(1, 1, 1, 7, 45, 12, 765432100)));
    entity.addProperty(new PropertyImpl(null, "PropertyComp", ValueType.LINKED_COMPLEX, complexValue));
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", 0));
    complexValue = new LinkedComplexValueImpl();
    complexValue.getValue().add(createPrimitive("PropertyString", "Third Resource - third"));
    complexValue.getValue().add(createPrimitive("PropertyBinary",
        new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }));
    complexValue.getValue().add(createPrimitive("PropertyBoolean", true));
    complexValue.getValue().add(createPrimitive("PropertyByte", 255));
    complexValue.getValue().add(createPrimitive("PropertyDate", getDateTime(2014, 12, 5, 0, 0, 0)));
    complexValue.getValue().add(createPrimitive("PropertyDateTimeOffset",
        getTimestamp(2014, 12, 5, 8, 17, 45, 123456700)));
    complexValue.getValue().add(createPrimitive("PropertyDecimal", 17.98));
    complexValue.getValue().add(createPrimitive("PropertySingle", 1.79000000E+20));
    complexValue.getValue().add(createPrimitive("PropertyDouble", -1.7900000000000000E+02));
    complexValue.getValue().add(createPrimitive("PropertyDuration", 6));
    complexValue.getValue().add(createPrimitive("PropertyGuid", GUID));
    complexValue.getValue().add(createPrimitive("PropertyInt16", -25));
    complexValue.getValue().add(createPrimitive("PropertyInt32", Integer.MAX_VALUE));
    complexValue.getValue().add(createPrimitive("PropertyInt64", Long.MAX_VALUE));
    complexValue.getValue().add(createPrimitive("PropertySByte", Byte.MAX_VALUE));
    complexValue.getValue().add(createPrimitive("PropertyTimeOfDay", getTime(13, 27, 45)));
    entity.addProperty(new PropertyImpl(null, "PropertyComp", ValueType.LINKED_COMPLEX, complexValue));
    entitySet.getEntities().add(entity);

    return entitySet;
  }

  private EntitySet createESCollAllPrim() {
    EntitySet entitySet = new EntitySetImpl();

    Entity entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", 1));
    entity.addProperty(createCollection("CollPropertyString",
        "Employee1@company.example", "Employee2@company.example", "Employee3@company.example"));
    entity.addProperty(createCollection("CollPropertyBoolean", true, false, true));
    entity.addProperty(createCollection("CollPropertyByte", 50, 200, 249));
    entity.addProperty(createCollection("CollPropertySByte", -120, 120, 126));
    entity.addProperty(createCollection("CollPropertyInt16", 1000, 2000, 30112));
    entity.addProperty(createCollection("CollPropertyInt32", 23232323, 11223355, 10000001));
    entity.addProperty(createCollection("CollPropertyInt64", 929292929292L, 333333333333L, 444444444444L));
    entity.addProperty(createCollection("CollPropertySingle", 1.79000000E+03, 2.66000000E+04, 3.21000000E+03));
    entity.addProperty(createCollection("CollPropertyDouble",
        -1.7900000000000000E+04, -2.7800000000000000E+07, 3.2100000000000000E+03));
    entity.addProperty(createCollection("CollPropertyDecimal", 12, -2, 1234));
    entity.addProperty(createCollection("CollPropertyBinary",
        new byte[] { (byte) 0xAB, (byte) 0xCD, (byte) 0xEF },
        new byte[] { 0x01, 0x23, 0x45 },
        new byte[] { 0x54, 0x67, (byte) 0x89 }));
    entity.addProperty(createCollection("CollPropertyDate",
        getDateTime(1958, 12, 3, 0, 0, 0), getDateTime(1999, 8, 5, 0, 0, 0), getDateTime(2013, 6, 25, 0, 0, 0)));
    entity.addProperty(createCollection("CollPropertyDateTimeOffset",
        getDateTime(2015, 8, 12, 3, 8, 34), getDateTime(1970, 3, 28, 12, 11, 10), getDateTime(1948, 2, 17, 9, 9, 9)));
    entity.addProperty(createCollection("CollPropertyDuration", 13, 19680, 3600));
    entity.addProperty(createCollection("CollPropertyGuid",
        UUID.fromString("ffffff67-89ab-cdef-0123-456789aaaaaa"),
        UUID.fromString("eeeeee67-89ab-cdef-0123-456789bbbbbb"),
        UUID.fromString("cccccc67-89ab-cdef-0123-456789cccccc")));
    entity.addProperty(createCollection("CollPropertyTimeOfDay",
        getTime(4, 14, 13), getTime(23, 59, 59), getTime(1, 12, 33)));
    entitySet.getEntities().add(entity);

    Entity entity2 = new EntityImpl();
    entity2.getProperties().addAll(entity.getProperties());
    entity2.getProperties().set(0, createPrimitive("PropertyInt16", 2));
    entitySet.getEntities().add(entity2);

    entity2 = new EntityImpl();
    entity2.getProperties().addAll(entity.getProperties());
    entity2.getProperties().set(0, createPrimitive("PropertyInt16", 3));
    entitySet.getEntities().add(entity2);

    return entitySet;
  }

  private EntitySet createESMixPrimCollComp() {
    EntitySet entitySet = new EntitySetImpl();

    Entity entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE));
    entity.addProperty(createCollection("CollPropertyString",
        "Employee1@company.example", "Employee2@company.example", "Employee3@company.example"));
    LinkedComplexValue complexValue = new LinkedComplexValueImpl();
    complexValue.getValue().add(createPrimitive("PropertyInt16", 111));
    complexValue.getValue().add(createPrimitive("PropertyString", "TEST A"));
    entity.addProperty(new PropertyImpl(null, "PropertyComp", ValueType.LINKED_COMPLEX, complexValue));
    List<LinkedComplexValue> complexCollection = new ArrayList<LinkedComplexValue>();
    complexValue = new LinkedComplexValueImpl();
    complexValue.getValue().add(createPrimitive("PropertyInt16", 123));
    complexValue.getValue().add(createPrimitive("PropertyString", "TEST 1"));
    complexCollection.add(complexValue);
    complexValue = new LinkedComplexValueImpl();
    complexValue.getValue().add(createPrimitive("PropertyInt16", 456));
    complexValue.getValue().add(createPrimitive("PropertyString", "TEST 2"));
    complexCollection.add(complexValue);
    complexValue = new LinkedComplexValueImpl();
    complexValue.getValue().add(createPrimitive("PropertyInt16", 789));
    complexValue.getValue().add(createPrimitive("PropertyString", "TEST 3"));
    complexCollection.add(complexValue);
    entity.addProperty(new PropertyImpl(null, "CollPropertyComp", ValueType.COLLECTION_LINKED_COMPLEX,
        complexCollection));
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", 7));
    entity.addProperty(createCollection("CollPropertyString",
        "Employee1@company.example", "Employee2@company.example", "Employee3@company.example"));
    complexValue = new LinkedComplexValueImpl();
    complexValue.getValue().add(createPrimitive("PropertyInt16", 222));
    complexValue.getValue().add(createPrimitive("PropertyString", "TEST B"));
    entity.addProperty(new PropertyImpl(null, "PropertyComp", ValueType.LINKED_COMPLEX, complexValue));
    entity.addProperty(new PropertyImpl(null, "CollPropertyComp", ValueType.COLLECTION_LINKED_COMPLEX,
        complexCollection));
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", 0));
    entity.addProperty(createCollection("CollPropertyString",
        "Employee1@company.example", "Employee2@company.example", "Employee3@company.example"));
    complexValue = new LinkedComplexValueImpl();
    complexValue.getValue().add(createPrimitive("PropertyInt16", 333));
    complexValue.getValue().add(createPrimitive("PropertyString", "TEST C"));
    entity.addProperty(new PropertyImpl(null, "PropertyComp", ValueType.LINKED_COMPLEX, complexValue));
    entity.addProperty(new PropertyImpl(null, "CollPropertyComp", ValueType.COLLECTION_LINKED_COMPLEX,
        complexCollection));
    entitySet.getEntities().add(entity);

    return entitySet;
  }

  private EntitySet createESAllKey() {
    EntitySet entitySet = new EntitySetImpl();

    Entity entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyString", "First"));
    entity.addProperty(createPrimitive("PropertyBoolean", true));
    entity.addProperty(createPrimitive("PropertyByte", 255));
    entity.addProperty(createPrimitive("PropertySByte", Byte.MAX_VALUE));
    entity.addProperty(createPrimitive("PropertyInt16", Short.MAX_VALUE));
    entity.addProperty(createPrimitive("PropertyInt32", Integer.MAX_VALUE));
    entity.addProperty(createPrimitive("PropertyInt64", Long.MAX_VALUE));
    entity.addProperty(createPrimitive("PropertyDecimal", 34));
    entity.addProperty(createPrimitive("PropertyDate", getDateTime(2012, 12, 3, 0, 0, 0)));
    entity.addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2012, 12, 3, 7, 16, 23)));
    entity.addProperty(createPrimitive("PropertyDuration", 6));
    entity.addProperty(createPrimitive("PropertyGuid", GUID));
    entity.addProperty(createPrimitive("PropertyTimeOfDay", getTime(2, 48, 21)));
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyString", "Second"));
    entity.addProperty(createPrimitive("PropertyBoolean", true));
    entity.addProperty(createPrimitive("PropertyByte", 254));
    entity.addProperty(createPrimitive("PropertySByte", 124));
    entity.addProperty(createPrimitive("PropertyInt16", 32764));
    entity.addProperty(createPrimitive("PropertyInt32", 2147483644));
    entity.addProperty(createPrimitive("PropertyInt64", 9223372036854775804L));
    entity.addProperty(createPrimitive("PropertyDecimal", 34));
    entity.addProperty(createPrimitive("PropertyDate", getDateTime(2012, 12, 3, 0, 0, 0)));
    entity.addProperty(createPrimitive("PropertyDateTimeOffset", getDateTime(2012, 12, 3, 7, 16, 23)));
    entity.addProperty(createPrimitive("PropertyDuration", 6));
    entity.addProperty(createPrimitive("PropertyGuid", GUID));
    entity.addProperty(createPrimitive("PropertyTimeOfDay", getTime(2, 48, 21)));
    entitySet.getEntities().add(entity);

    return entitySet;
  }

  private EntitySet createESMedia() {
    EntitySet entitySet = new EntitySetImpl();

    Entity entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", 1));
    entity.setMediaContentType("image/png");
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", 2));
    entity.setMediaContentType("image/bmp");
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", 3));
    entity.setMediaContentType("image/jpeg");
    entitySet.getEntities().add(entity);

    entity = new EntityImpl();
    entity.addProperty(createPrimitive("PropertyInt16", 4));
    entity.setMediaContentType("foo");
    entitySet.getEntities().add(entity);

    return entitySet;
  }

  private Property createPrimitive(final String name, final Object value) {
    return new PropertyImpl(null, name, ValueType.PRIMITIVE, value);
  }

  private Property createCollection(final String name, final Object... values) {
    return new PropertyImpl(null, name, ValueType.COLLECTION_PRIMITIVE, Arrays.asList(values));
  }

  private Calendar getDateTime(final int year, final int month, final int day,
      final int hour, final int minute, final int second) {
    Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    dateTime.clear();
    dateTime.set(year, month - 1, day, hour, minute, second);
    return dateTime;
  }

  private Calendar getTime(final int hour, final int minute, final int second) {
    Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    time.clear();
    time.set(Calendar.HOUR_OF_DAY, hour);
    time.set(Calendar.MINUTE, minute);
    time.set(Calendar.SECOND, second);
    return time;
  }

  private Timestamp getTimestamp(final int year, final int month, final int day,
      final int hour, final int minute, final int second, final int nanosecond) {
    Timestamp timestamp = new Timestamp(getDateTime(year, month, day, hour, minute, second).getTimeInMillis());
    timestamp.setNanos(nanosecond);
    return timestamp;
  }
}
