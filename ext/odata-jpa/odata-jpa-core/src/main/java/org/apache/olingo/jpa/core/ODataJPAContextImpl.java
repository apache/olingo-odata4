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
package org.apache.olingo.jpa.core;

import javax.persistence.EntityManagerFactory;

import org.apache.olingo.jpa.api.ODataJPAContext;
import org.apache.olingo.jpa.api.extension.JPACsdlMetaModelExtension;

public class ODataJPAContextImpl implements ODataJPAContext {

  private String pUnitName;
  private EntityManagerFactory emf;
  private JPACsdlMetaModelExtension extension = null;

  @Override
  public String getPersistenceUnitName() {
    return pUnitName;
  }

  @Override
  public void setPersistenceUnitName(final String pUnitName) {
    this.pUnitName = pUnitName;
  }

  @Override
  public EntityManagerFactory getEntityManagerFactory() {
    return emf;
  }

  @Override
  public void setEntityManagerFactory(final EntityManagerFactory emf) {
    this.emf = emf;
  }

  @Override
  public JPACsdlMetaModelExtension getJPACsdlMetaModelExtension() {
    return extension;
  }

  @Override
  public void setJPACsdlMetaModelExtension(JPACsdlMetaModelExtension extension) {
    this.extension = extension;
  }
}
