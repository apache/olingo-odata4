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

/**
 * The type Csdl entity set path.
 */
public class CsdlEntitySetPath {

  private String bindingParameter;

  private String path;

  /**
   * Gets binding parameter.
   *
   * @return the binding parameter
   */
  public String getBindingParameter() {
    return bindingParameter;
  }

  /**
   * Sets binding parameter.
   *
   * @param bindingParameter the binding parameter
   * @return the binding parameter
   */
  public CsdlEntitySetPath setBindingParameter(final String bindingParameter) {
    this.bindingParameter = bindingParameter;
    return this;
  }

  /**
   * Gets path.
   *
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * Sets path.
   *
   * @param path the path
   * @return the path
   */
  public CsdlEntitySetPath setPath(final String path) {
    this.path = path;
    return this;
  }
}
