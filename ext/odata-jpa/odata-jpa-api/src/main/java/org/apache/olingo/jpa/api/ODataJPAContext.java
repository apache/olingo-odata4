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

  String getPersistenceUnitName();

  void setPersistenceUnitName();

}
