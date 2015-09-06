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
package org.apache.olingo.jpa.api.model;

import org.apache.olingo.jpa.api.exception.ODataJPAException;

/**
 * JPAEdmBuilder interface provides methods for building elements of an Entity Data Model (EDM) from
 * a Java Persistence Model.
 */
public interface JPACsdlBuilder {

  /**
   * the method builds an returns a reference to JPAEdm MetaModel accessor
   * @return a reference to {@link org.apache.olingo.jpa.api.model.JPACsdlMetaModelAccessor}
   * @throws ODataJPAException
   */
  JPACsdlMetaModelAccessor build() throws ODataJPAException;
}
