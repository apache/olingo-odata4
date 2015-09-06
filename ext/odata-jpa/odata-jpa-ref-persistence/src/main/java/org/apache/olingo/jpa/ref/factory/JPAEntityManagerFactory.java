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
package org.apache.olingo.jpa.ref.factory;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAEntityManagerFactory {
  private static HashMap<String, EntityManagerFactory> emfMap;

  public static EntityManagerFactory getEntityManagerFactory(final String pUnit) {
    if (pUnit == null) {
      return null;
    }
    if (emfMap == null) {
      emfMap = new HashMap<String, EntityManagerFactory>();
    }

    if (emfMap.containsKey(pUnit)) {
      return emfMap.get(pUnit);
    } else {
      EntityManagerFactory emf = Persistence.createEntityManagerFactory(pUnit);
      emfMap.put(pUnit, emf);
      return emf;
    }

  }
}