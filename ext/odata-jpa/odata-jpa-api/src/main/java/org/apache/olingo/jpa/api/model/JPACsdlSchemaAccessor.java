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

import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

/**
 * The interface provides methods to access CSDL Schema generated from JPA Meta model.
 */
public interface JPACsdlSchemaAccessor extends JPACsdlMetaModelAccessor {
  /**
   * The method returns a CSDL Schema element built from JPA Meta Model.
   * @return an instance of type {@link org.apache.olingo.commons.api.edm.provider.CsdlSchema}
   */
  CsdlSchema getCsdlSchema();

  JPACsdlBuilder getJPACsdlBuilder();

}
