/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.core.edm.provider;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;
import org.apache.olingo.server.api.edm.provider.EnumMember;
import org.apache.olingo.server.api.edm.provider.EnumType;
import org.apache.olingo.server.core.edm.provider.EdmEnumTypeImpl;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;
import org.junit.Test;

public class EdmEnumTest extends PrimitiveTypeBaseTest {

  private final EdmEnumType instance;
  private final EdmEnumType nonFlagsInstance;

  public EdmEnumTest() {
    List<EnumMember> memberList = new ArrayList<EnumMember>();
    memberList.add(new EnumMember().setName("first").setValue("1"));
    memberList.add(new EnumMember().setName("second").setValue("64"));

    EnumType enumType =
        new EnumType().setName("name").setMembers(memberList).setFlags(true).setUnderlyingType(
            EdmPrimitiveTypeKind.SByte.getFullQualifiedName());

    FullQualifiedName enumName = new FullQualifiedName("namespace", "name");
    instance = new EdmEnumTypeImpl(mock(EdmProviderImpl.class), enumName, enumType);

    EnumType enumType2 = new EnumType().setName("name").setMembers(memberList).setFlags(false).setUnderlyingType(
        EdmPrimitiveTypeKind.SByte.getFullQualifiedName());
    nonFlagsInstance = new EdmEnumTypeImpl(mock(EdmProviderImpl.class), enumName, enumType2);

//    EdmMember member1 = mock(EdmMember.class);
//    when(member1.getName()).thenReturn("first");
//    when(member1.getValue()).thenReturn("1");
//    EdmMember member2 = mock(EdmMember.class);
//    when(member2.getName()).thenReturn("second");
//    when(member2.getValue()).thenReturn("64");
//    instance = new EdmEnumImpl("namespace", "name",
//        EdmPrimitiveTypeKind.SByte.getEdmPrimitiveTypeInstance(),
//        Arrays.asList(member1, member2),
//        true);
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
    assertFalse(instance.isCompatible(instance.getUnderlyingType()));
  }

  @Test
  public void defaultType() throws Exception {
    assertEquals(Byte.class, instance.getDefaultType());
  }

  @Test
  public void members() throws Exception {
    assertArrayEquals(new String[] { "first", "second" }, instance.getMemberNames().toArray());
    assertEquals("64", instance.getMember("second").getValue());
    assertNull(instance.getMember("notExisting"));
  }

  @Test
  public void underlyingType() throws Exception {
    assertEquals(EdmPrimitiveTypeKind.SByte.getEdmPrimitiveTypeInstance(), instance.getUnderlyingType());
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
  }
}
