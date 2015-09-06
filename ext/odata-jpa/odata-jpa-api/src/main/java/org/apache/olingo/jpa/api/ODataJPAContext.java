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
package org.apache.olingo.jpa.api;

import javax.persistence.EntityManagerFactory;

import org.apache.olingo.jpa.api.extension.JPACsdlMetaModelExtension;

/**
 * This class does the compilation of context objects required for OData JPA
 * Runtime. The context object should be properly initialized with values else
 * the behavior of processor and EDM provider can result in exception.
 * 
 * Following are the mandatory parameter to be set into the context object
 * <ol>
 * <li>Persistence Unit Name</li>
 * <li>An instance of Java Persistence Entity Manager Factory</li>
 * </ol>
 * 
 */
public interface ODataJPAContext {

  /**
   * The method gets the Java Persistence Unit Name set into the context.
   * 
   * @return Java Persistence Unit Name
   */
  String getPersistenceUnitName();

  /**
   * The method sets the Java Persistence Unit Name into the context.
   * 
   * @param pUnitName
   * is the Java Persistence Unit Name.
   * 
   */
  void setPersistenceUnitName(String pUnitName);

  /**
   * The method gets the Java Persistence Entity Manager factory from the
   * context. <br>
   * <b>CAUTION:-</b> Don't use the Entity Manager Factory to instantiate
   * Entity Managers. Instead get reference to Entity Manager using
   * {@link org.apache.olingo.jpa.api.ODataJPAContext#getEntityManager()}
   * 
   * @return an instance of Java Persistence Entity Manager Factory
   */
  EntityManagerFactory getEntityManagerFactory();

  /**
   * The method sets the Java Persistence Entity Manager factory into the
   * context.
   * 
   * @param emf
   * is of type {@link javax.persistence.EntityManagerFactory}
   * 
   */
  void setEntityManagerFactory(EntityManagerFactory emf);

  /**
   * The method returns the extension for JPA Csdl Meta Model.
   * @return
   */
  JPACsdlMetaModelExtension getJPACsdlMetaModelExtension();

  /**
   * The method sets the extension for JPA Csdl Meta model
   * @param extension
   */
  void setJPACsdlMetaModelExtension(JPACsdlMetaModelExtension extension);
}
