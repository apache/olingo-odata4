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

import org.apache.olingo.commons.api.edm.EdmMapping;

/**
 * Content of this class does not appear within the CSDL metadata document. This class is used to perform server
 * internal mapping for edm primitive types to java types.
 */
public class CsdlMapping implements EdmMapping {

  private String internalName;
  private Class<?> mappedJavaClass;

  
  /**
   * Sets the internal name for this mapped object. This name won`t be used by the Olingo library but can be used by 
   * applications to access their database easier.
   * @param internalName
   * @return this for method chaining
   */
  public CsdlMapping setInternalName(String internalName){
    this.internalName = internalName;
    return this;
  }
  
  /**
   * Sets the class to be used during deserialization to transform an EDM primitive type into this java class. To see
   * which classes work for which primitive type refer to {@link org.apache.olingo.commons.api.edm.EdmPrimitiveType}.
   * @param mappedJavaClass class to which is mapped
   * @return this for method chaining
   */
  public CsdlMapping setMappedJavaClass(final Class<?> mappedJavaClass) {
    this.mappedJavaClass = mappedJavaClass;
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.olingo.commons.api.edm.EdmMapping#getMappedJavaClass()
   */
  @Override
  public Class<?> getMappedJavaClass() {
    return mappedJavaClass;
  }

  /* (non-Javadoc)
   * @see org.apache.olingo.commons.api.edm.EdmMapping#getInternalName()
   */
  @Override
  public String getInternalName() {
    return internalName;
  }

}
