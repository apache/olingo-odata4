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
package org.apache.olingo.server.tecsvc.data.model;

import java.util.List;

public class EtAllPrim {
  private int PropertyInt16;
  private String PropertyString;
  private boolean PropertyBoolean;
  private byte PropertyByte;
  private byte PropertySByte;
  private int PropertyInt32;
  private int PropertyInt64;
  private long PropertySingle;
  private double PropertyDouble;
  private double PropertyDecimal;
  private byte[] PropertyBinary;
  // TODO:Define ----------
  private String PropertyDate;
  private String PropertyDateTimeOffset;
  private String PropertyDuration;
  private String PropertyGuid;
  private String PropertyTimeOfDay;
  // ----------- ----------
  private EtTwoPrim NavPropertyETTwoPrimOne;
  private List<EtTwoPrim> NavPropertyETTwoPrimMany;

  public int getPropertyInt16() {
    return PropertyInt16;
  }

  public void setPropertyInt16(int propertyInt16) {
    PropertyInt16 = propertyInt16;
  }

  public String getPropertyString() {
    return PropertyString;
  }

  public void setPropertyString(String propertyString) {
    PropertyString = propertyString;
  }

  public boolean isPropertyBoolean() {
    return PropertyBoolean;
  }

  public void setPropertyBoolean(boolean propertyBoolean) {
    PropertyBoolean = propertyBoolean;
  }

  public byte getPropertyByte() {
    return PropertyByte;
  }

  public void setPropertyByte(byte propertyByte) {
    PropertyByte = propertyByte;
  }

  public byte getPropertySByte() {
    return PropertySByte;
  }

  public void setPropertySByte(byte propertySByte) {
    PropertySByte = propertySByte;
  }

  public int getPropertyInt32() {
    return PropertyInt32;
  }

  public void setPropertyInt32(int propertyInt32) {
    PropertyInt32 = propertyInt32;
  }

  public int getPropertyInt64() {
    return PropertyInt64;
  }

  public void setPropertyInt64(int propertyInt64) {
    PropertyInt64 = propertyInt64;
  }

  public long getPropertySingle() {
    return PropertySingle;
  }

  public void setPropertySingle(long propertySingle) {
    PropertySingle = propertySingle;
  }

  public double getPropertyDouble() {
    return PropertyDouble;
  }

  public void setPropertyDouble(double propertyDouble) {
    PropertyDouble = propertyDouble;
  }

  public double getPropertyDecimal() {
    return PropertyDecimal;
  }

  public void setPropertyDecimal(double propertyDecimal) {
    PropertyDecimal = propertyDecimal;
  }

  public byte[] getPropertyBinary() {
    return PropertyBinary;
  }

  public void setPropertyBinary(byte[] propertyBinary) {
    PropertyBinary = propertyBinary;
  }

  public String getPropertyDate() {
    return PropertyDate;
  }

  public void setPropertyDate(String propertyDate) {
    PropertyDate = propertyDate;
  }

  public String getPropertyDateTimeOffset() {
    return PropertyDateTimeOffset;
  }

  public void setPropertyDateTimeOffset(String propertyDateTimeOffset) {
    PropertyDateTimeOffset = propertyDateTimeOffset;
  }

  public String getPropertyDuration() {
    return PropertyDuration;
  }

  public void setPropertyDuration(String propertyDuration) {
    PropertyDuration = propertyDuration;
  }

  public String getPropertyGuid() {
    return PropertyGuid;
  }

  public void setPropertyGuid(String propertyGuid) {
    PropertyGuid = propertyGuid;
  }

  public String getPropertyTimeOfDay() {
    return PropertyTimeOfDay;
  }

  public void setPropertyTimeOfDay(String propertyTimeOfDay) {
    PropertyTimeOfDay = propertyTimeOfDay;
  }

  public EtTwoPrim getNavPropertyETTwoPrimOne() {
    return NavPropertyETTwoPrimOne;
  }

  public void setNavPropertyETTwoPrimOne(EtTwoPrim navPropertyETTwoPrimOne) {
    NavPropertyETTwoPrimOne = navPropertyETTwoPrimOne;
  }

  public List<EtTwoPrim> getNavPropertyETTwoPrimMany() {
    return NavPropertyETTwoPrimMany;
  }

  public void setNavPropertyETTwoPrimMany(List<EtTwoPrim> navPropertyETTwoPrimMany) {
    NavPropertyETTwoPrimMany = navPropertyETTwoPrimMany;
  }
}
