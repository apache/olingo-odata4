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

public class EtTwoPrim {
  private int PropertyInt16;
  private String PropertyString;

  private EtAllPrim NavPropertyETAllPrimOne;
  private List<EtAllPrim> NavPropertyETAllPrimMany;

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

  public EtAllPrim getNavPropertyETAllPrimOne() {
    return NavPropertyETAllPrimOne;
  }

  public void setNavPropertyETAllPrimOne(EtAllPrim navPropertyETAllPrimOne) {
    NavPropertyETAllPrimOne = navPropertyETAllPrimOne;
  }

  public List<EtAllPrim> getNavPropertyETAllPrimMany() {
    return NavPropertyETAllPrimMany;
  }

  public void setNavPropertyETAllPrimMany(List<EtAllPrim> navPropertyETAllPrimMany) {
    NavPropertyETAllPrimMany = navPropertyETAllPrimMany;
  }

}
