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
package org.apache.olingo.jpa.core.factory;

import java.util.HashMap;

import org.apache.olingo.jpa.api.ODataJPAAbstractEdmProvider;
import org.apache.olingo.jpa.api.ODataJPAContext;
import org.apache.olingo.jpa.api.ODataJPAProcessor;
import org.apache.olingo.jpa.api.factory.ODataJPAFactory;
import org.apache.olingo.jpa.core.ODataJPAContextImpl;
import org.apache.olingo.jpa.core.ODataJPAProcessorDefault;
import org.apache.olingo.jpa.core.edm.ODataJPAEdmProvider;
import org.apache.olingo.jpa.core.exception.ODataJPARuntimeException;

public class ODataJPAFactoryImpl extends ODataJPAFactory {

  private HashMap<String, ODataJPAAbstractEdmProvider> edmProviderMap =
      new HashMap<String, ODataJPAAbstractEdmProvider>();

  @Override
  public ODataJPAAbstractEdmProvider getODataJPAEdmProvider(ODataJPAContext odataJPAContext)
      throws ODataJPARuntimeException {
    ODataJPAAbstractEdmProvider odataJPAEdmProvider = null;
    String pUnitName = odataJPAContext.getPersistenceUnitName();
    if (odataJPAContext == null || pUnitName == null) {
      throw new ODataJPARuntimeException("Unable to create Edm Provider", null,
          ODataJPARuntimeException.MessageKeys.NULL_PUNIT, null);
    } else {
      odataJPAEdmProvider = edmProviderMap.get(pUnitName);
      if (odataJPAEdmProvider == null) {
        odataJPAEdmProvider = new ODataJPAEdmProvider(odataJPAContext);
        edmProviderMap.put(pUnitName, odataJPAEdmProvider);
      }
    }

    return odataJPAEdmProvider;
  }

  @Override
  public ODataJPAProcessor getODataJPAProcessor(ODataJPAContext odataJPAContext) {
    return new ODataJPAProcessorDefault();
  }

  @Override
  public ODataJPAContext newODataJPAContext() {
    return new ODataJPAContextImpl();
  }

}
