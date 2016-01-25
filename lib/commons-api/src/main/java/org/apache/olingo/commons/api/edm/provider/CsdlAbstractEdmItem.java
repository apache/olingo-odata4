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
package org.apache.olingo.commons.api.edm.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Super type of all CsdlEdmItems
 */
public abstract class CsdlAbstractEdmItem {

  /**
   * Gets one by name.
   *
   * @param name the name
   * @param items the items
   * @return the one by name
   */
  protected <T extends CsdlNamed> T getOneByName(final String name, final Collection<T> items) {
    final List<T> result = getAllByName(name, items);
    return result.isEmpty() ? null : result.get(0);
  }

  /**
   * Gets all by name.
   *
   * @param name the name
   * @param items the items
   * @return the all by name
   */
  protected <T extends CsdlNamed> List<T> getAllByName(final String name, final Collection<T> items) {
    final List<T> result = new ArrayList<T>();
    for (T type : items) {
      if (name.equals(type.getName())) {
        result.add(type);
      }
    }
    return result;
  }
}
