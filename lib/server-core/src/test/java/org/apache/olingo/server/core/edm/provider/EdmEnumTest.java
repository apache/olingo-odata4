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
package org.apache.olingo.server.core.edm.provider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.core.edm.EdmEnumTypeImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.junit.Test;
import org.mockito.Mockito;

public class EdmEnumTest {

  private final EdmEnumType instance;
  private final EdmEnumType otherInstance;
  private final EdmEnumType nonFlagsInstance;
  private final EdmEnumType int16EnumType;
  private final EdmEnumType int32EnumType;
  private final EdmEnumType int32FlagType;

  public EdmEnumTest() {
    final List<CsdlEnumMember> memberList = Arrays.asList(
        new CsdlEnumMember().setName("first").setValue("1"),
        new CsdlEnumMember().setName("second").setValue("64"));

    final FullQualifiedName enumName = new FullQualifiedName("namespace", "name");

    instance = new EdmEnumTypeImpl(mock(EdmProviderImpl.class), enumName,
        new CsdlEnumType().setName("name").setMembers(memberList).setFlags(true)
            .setUnderlyingType(EdmPrimitiveTypeKind.SByte.getFullQualifiedName()));
    
    otherInstance = new EdmEnumTypeImpl(mock(EdmProviderImpl.class), enumName,
        new CsdlEnumType().setName("name").setMembers(memberList).setFlags(true)
            .setUnderlyingType(EdmPrimitiveTypeKind.SByte.getFullQualifiedName()));

    nonFlagsInstance = new EdmEnumTypeImpl(mock(EdmProviderImpl.class), enumName,
        new CsdlEnumType().setName("name").setMembers(memberList).setFlags(false)
            .setUnderlyingType(EdmPrimitiveTypeKind.SByte.getFullQualifiedName()));

    int16EnumType = new EdmEnumTypeImpl(Mockito.mock(Edm.class),
        new FullQualifiedName("testNamespace", "testName"), new CsdlEnumType()
    .setName("MyEnum")
    .setFlags(false)
    .setUnderlyingType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName())
    .setMembers(
        Arrays.asList(
            new CsdlEnumMember().setName("A")
            .setValue("0"),
            new CsdlEnumMember().setName("B")
            .setValue("1"),
            new CsdlEnumMember().setName("C")
            .setValue("2"))));

    int32EnumType =
        new EdmEnumTypeImpl(Mockito.mock(Edm.class),
            new FullQualifiedName("testNamespace", "testName"), new CsdlEnumType()
        .setName("MyEnum")
        .setFlags(false)
        .setUnderlyingType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName())
        .setMembers(
            Arrays
            .asList(new CsdlEnumMember().setName("A").setValue("0"), new CsdlEnumMember().setName("B")
                .setValue("1"),
                new CsdlEnumMember().setName("C").setValue("2"))));

    int32FlagType =
        new EdmEnumTypeImpl(Mockito.mock(Edm.class),
            new FullQualifiedName("testNamespace", "testName"), new CsdlEnumType()
        .setName("MyEnum")
        .setFlags(true)
        .setUnderlyingType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName())
        .setMembers(
            Arrays
            .asList(new CsdlEnumMember().setName("A").setValue("2"), new CsdlEnumMember().setName("B")
                .setValue("4"),
                new CsdlEnumMember().setName("C").setValue("8"))));
  }

  @Test
  public void nameSpace() throws Exception {
    assertEquals("namespace", instance.getNamespace());
  }

  @Test
  public void name() throws Exception {
    assertEquals("name", instance.getName());
  }

  @Test
  public void kind() throws Exception {
    assertEquals(EdmTypeKind.ENUM, instance.getKind());
  }

  @Test
  public void compatibility() {
    assertTrue(instance.isCompatible(instance));
    assertTrue(instance.isCompatible(otherInstance));
    assertFalse(instance.isCompatible(instance.getUnderlyingType()));
  }

  @Test
  public void defaultType() throws Exception {
    assertEquals(Byte.class, instance.getDefaultType());
    EdmEnumType instance = new EdmEnumTypeImpl(Mockito.mock(Edm.class),
        new FullQualifiedName("testNamespace", "testName"),
        new CsdlEnumType()
            .setName("MyEnum"));
    assertEquals(Integer.class, instance.getUnderlyingType().getDefaultType());
  }

  @Test
  public void members() throws Exception {
    assertArrayEquals(new String[] { "first", "second" }, instance.getMemberNames().toArray());
    assertEquals("64", instance.getMember("second").getValue());
    assertNull(instance.getMember("notExisting"));
  }

  @Test
  public void underlyingType() throws Exception {
    assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte), instance.getUnderlyingType());
  }

  @Test
  public void validate() throws Exception {
    assertTrue(instance.validate(null, null, null, null, null, null));
    assertTrue(instance.validate(null, true, null, null, null, null));
    assertFalse(instance.validate(null, false, null, null, null, null));
    assertFalse(instance.validate("", null, null, null, null, null));
    assertFalse(instance.validate("something", null, null, null, null, null));

    assertTrue(instance.validate("second", null, null, null, null, null));
    assertTrue(instance.validate("first,second", null, null, null, null, null));
    assertTrue(instance.validate("64", null, null, null, null, null));
    assertTrue(instance.validate("1,64", null, null, null, null, null));
  }

  @Test
  public void toUriLiteral() throws Exception {
    assertNull(instance.toUriLiteral(null));
    assertEquals("namespace.name'first'", instance.toUriLiteral("first"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertNull(instance.fromUriLiteral(null));
    assertEquals("first", instance.fromUriLiteral("namespace.name'first'"));

    expectErrorInFromUriLiteral(instance, "");
    expectErrorInFromUriLiteral(instance, "name'first'");
    expectErrorInFromUriLiteral(instance, "namespace.name'first");
    expectErrorInFromUriLiteral(instance, "namespace.namespace'first");
  }

  @Test
  public void valueToString() throws Exception {
    assertNull(instance.valueToString(null, null, null, null, null, null));
    assertNull(instance.valueToString(null, true, null, null, null, null));
    assertEquals("first", instance.valueToString(1, null, null, null, null, null));
    assertEquals("first", instance.valueToString((byte) 1, null, null, null, null, null));
    assertEquals("first", instance.valueToString((short) 1, null, null, null, null, null));
    assertEquals("second", instance.valueToString(Integer.valueOf(64), null, null, null, null, null));
    assertEquals("second", instance.valueToString(64L, null, null, null, null, null));
    assertEquals("first,second", instance.valueToString(65, null, null, null, null, null));

    expectNullErrorInValueToString(instance);
    expectContentErrorInValueToString(instance, 3);
    expectTypeErrorInValueToString(instance, 1.0);

    assertEquals("A", int32EnumType.valueToString(0, false, 0, 0, 0, false));
    assertEquals("B", int32EnumType.valueToString(1, false, 0, 0, 0, false));
    assertEquals("C", int32EnumType.valueToString(2, false, 0, 0, 0, false));

    assertEquals("A", int16EnumType.valueToString(0, false, 0, 0, 0, false));
    assertEquals("B", int16EnumType.valueToString(1, false, 0, 0, 0, false));
    assertEquals("C", int16EnumType.valueToString(2, false, 0, 0, 0, false));

    assertEquals("A", int32FlagType.valueToString(2, false, 0, 0, 0, false));
    assertEquals("B", int32FlagType.valueToString(4, false, 0, 0, 0, false));
    assertEquals("C", int32FlagType.valueToString(8, false, 0, 0, 0, false));
    assertEquals("A,B", int32FlagType.valueToString(0x2 + 0x4, false, 0, 0, 0, false));
    assertEquals("B,C", int32FlagType.valueToString(0x4 + 0x8, false, 0, 0, 0, false));
  }

  @Test
  public void valueOfString() throws Exception {
    assertNull(instance.valueOfString(null, null, null, null, null, null, Byte.class));
    assertNull(instance.valueOfString(null, true, null, null, null, null, Byte.class));
    assertEquals(Short.valueOf((short) 1), instance.valueOfString("1", null, null, null, null, null, Short.class));
    assertEquals(Integer.valueOf(1), instance.valueOfString("1", null, null, null, null, null, Integer.class));
    assertEquals(Long.valueOf(64L), instance.valueOfString("64", null, null, null, null, null, Long.class));
    assertEquals(Long.valueOf(1), instance.valueOfString("first", null, null, null, null, null, Long.class));
    assertEquals(Byte.valueOf((byte) 65), instance.valueOfString("first,64", null, null, null, null, null, Byte.class));
    assertEquals(Integer.valueOf(1), instance.valueOfString("1,1,first", null, null, null, null, null, Integer.class));

    assertEquals(Integer.valueOf(1), nonFlagsInstance.valueOfString("1", null, null, null, null, null, Integer.class));
    expectContentErrorInValueOfString(nonFlagsInstance, "1,64");

    expectNullErrorInValueOfString(instance);
    expectContentErrorInValueOfString(instance, "2");
    expectContentErrorInValueOfString(instance, "1,");
    expectContentErrorInValueOfString(instance, ",1");
    expectTypeErrorInValueOfString(instance, "1");

    assertEquals(Integer.valueOf(0), int32EnumType.valueOfString("A", null, null, null, null, null, Integer.class));
    assertEquals(Integer.valueOf(1), int32EnumType.valueOfString("B", null, null, null, null, null, Integer.class));
    assertEquals(Integer.valueOf(2), int32EnumType.valueOfString("C", null, null, null, null, null, Integer.class));

    assertEquals(Integer.valueOf(0), int16EnumType.valueOfString("A", null, null, null, null, null, Integer.class));
    assertEquals(Integer.valueOf(1), int16EnumType.valueOfString("B", null, null, null, null, null, Integer.class));
    assertEquals(Integer.valueOf(2), int16EnumType.valueOfString("C", null, null, null, null, null, Integer.class));

    assertEquals(Integer.valueOf(2), int32FlagType.valueOfString("A", null, null, null, null, null, Integer.class));
    assertEquals(Integer.valueOf(4), int32FlagType.valueOfString("B", null, null, null, null, null, Integer.class));
    assertEquals(Integer.valueOf(8), int32FlagType.valueOfString("C", null, null, null, null, null, Integer.class));
    assertEquals(Integer.valueOf(0x2 + 0x4), int32FlagType.valueOfString("A,B", null, null, null, null, null,
        Integer.class));
    assertEquals(Integer.valueOf(0x4 + 0x8), int32FlagType.valueOfString("B,C", null, null, null, null, null,
        Integer.class));
    assertEquals(Integer.valueOf(0x2 + 0x4), int32FlagType.valueOfString("B,A", null, null, null, null, null,
        Integer.class));
  }

  private void expectErrorInValueToString(final EdmEnumType instance,
      final Object value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode,
      final String message) {
    try {
      instance.valueToString(value, isNullable, maxLength, precision, scale, isUnicode);
      fail("Expected exception not thrown");
    } catch (final EdmPrimitiveTypeException e) {
      assertNotNull(e.getLocalizedMessage());
      assertThat(e.getLocalizedMessage(), containsString(message));
    }
  }

  private void expectErrorInUnderlyingType(
      final EdmPrimitiveTypeKind underlyingType,
      final String message) {
    try {
      new EdmEnumTypeImpl(Mockito.mock(Edm.class),
          new FullQualifiedName("testNamespace", "testName"),
          new CsdlEnumType()
              .setName("MyEnum")
              .setFlags(false)
              .setUnderlyingType(underlyingType.getFullQualifiedName())
              .setMembers(
                  Arrays.asList(
                      new CsdlEnumMember().setName("A")
                          .setValue("0"))));
      fail("Expected exception not thrown");
    } catch (final EdmException e) {
      assertNotNull(e.getLocalizedMessage());
      assertThat(e.getLocalizedMessage(), containsString(message));
    }
  }

  @Test
  public void unsupportedUnderlyingType() throws Exception {
    // Test some random unsupported types
    expectErrorInUnderlyingType(EdmPrimitiveTypeKind.Date, "");
    expectErrorInUnderlyingType(EdmPrimitiveTypeKind.Geography, "");
    expectErrorInUnderlyingType(EdmPrimitiveTypeKind.Guid, "");
  }

  @Test
  public void outOfRangeValueToString() throws Exception {
    expectErrorInValueToString(int16EnumType, Integer.MAX_VALUE, null, null, null, null, null, "");
  }

  protected void expectErrorInFromUriLiteral(final EdmPrimitiveType instance, final String value) {
    try {
      instance.fromUriLiteral(value);
      fail("Expected exception not thrown");
    } catch (final EdmPrimitiveTypeException e) {
      assertNotNull(e.getLocalizedMessage());
      assertThat(e.getLocalizedMessage(), containsString("' has illegal content."));
    }
  }

  private void expectErrorInValueToString(final EdmPrimitiveType instance,
      final Object value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode,
      final String message) {
    try {
      instance.valueToString(value, isNullable, maxLength, precision, scale, isUnicode);
      fail("Expected exception not thrown");
    } catch (final EdmPrimitiveTypeException e) {
      assertNotNull(e.getLocalizedMessage());
      assertThat(e.getLocalizedMessage(), containsString(message));
    }
  }

  protected void expectNullErrorInValueToString(final EdmPrimitiveType instance) {
    expectErrorInValueToString(instance, null, false, null, null, null, null, "The value NULL is not allowed.");
  }

  protected void expectTypeErrorInValueToString(final EdmPrimitiveType instance, final Object value) {
    expectErrorInValueToString(instance, value, null, null, null, null, null, "value type");
  }

  protected void expectContentErrorInValueToString(final EdmPrimitiveType instance, final Object value) {
    expectErrorInValueToString(instance, value, null, null, null, null, null, "' is not valid.");
  }

  private void expectErrorInValueOfString(final EdmPrimitiveType instance,
      final String value, final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<?> returnType,
      final String message) {

    try {
      instance.valueOfString(value, isNullable, maxLength, precision, scale, isUnicode, returnType);
      fail("Expected exception not thrown");
    } catch (final EdmPrimitiveTypeException e) {
      assertNotNull(e.getLocalizedMessage());
      assertThat(e.getLocalizedMessage(), containsString(message));
    }
  }

  protected void expectTypeErrorInValueOfString(final EdmPrimitiveType instance, final String value) {
    expectErrorInValueOfString(instance, value, null, null, null, null, null, Class.class,
        "The value type class java.lang.Class is not supported.");
  }

  protected void expectContentErrorInValueOfString(final EdmPrimitiveType instance, final String value) {
    expectErrorInValueOfString(instance, value, null, null, null, null, null, instance.getDefaultType(),
        "illegal content");
  }

  protected void expectNullErrorInValueOfString(final EdmPrimitiveType instance) {
    expectErrorInValueOfString(instance, null, false, null, null, null, null, instance.getDefaultType(),
        "The literal 'null' is not allowed.");
  }
}
