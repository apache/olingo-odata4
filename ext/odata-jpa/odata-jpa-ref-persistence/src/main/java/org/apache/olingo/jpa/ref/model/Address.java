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
package org.apache.olingo.jpa.ref.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

  public Address() {
    super();
  }

  public Address(final short houseNumber, final String streetName, final String city,
      final String country) {
    this();
    this.houseNumber = houseNumber;
    this.streetName = streetName;
    this.city = city;
    this.country = country;
  }

  @Column(name = "HOUSE_NUMBER")
  private short houseNumber;

  @Column(name = "STREET_NAME")
  private String streetName;

  @Column(name = "CITY")
  private String city;

  @Column(name = "COUNTRY")
  private String country;

  @Column(name = "PINCODE")
  private String pincode;

  @Column(name = "STATE")
  private String state;

  public String getPincode() {
    return pincode;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setPincode(final String pincode) {
    this.pincode = pincode;
  }

  public short getHouseNumber() {
    return houseNumber;
  }

  public void setHouseNumber(final short houseNumber) {
    this.houseNumber = houseNumber;
  }

  public String getStreetName() {
    return streetName;
  }

  public void setStreetName(final String streetName) {
    this.streetName = streetName;
  }

  public String getCity() {
    return city;
  }

  public void setCity(final String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(final String country) {
    this.country = country;
  }
}
