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
package org.apache.olingo.commons.api.edmx;

/**
 * edmx:Include elements that specify the schemas to include from the target document.
 */
public class EdmxReferenceInclude {
  private final String namespace;
  private final String alias;

  /**
   * Create include with given namespace and alias.
   *
   * @param namespace of include
   * @param alias of include
   */
  public EdmxReferenceInclude(final String namespace, final String alias) {
    this.namespace = namespace;
    this.alias = alias;
  }

  /**
   * Create include with given namespace and empty (<code>NULL</code>) alias.
   *
   * @param namespace of include
   */
  public EdmxReferenceInclude(final String namespace) {
    this(namespace, null);
  }

  /**
   * @return Namespace of the include
   */
  public String getNamespace() {
    return namespace;
  }

  /**
   * @return alias of the include if one defined; null otherwise
   */
  public String getAlias() {
    return alias;
  }
}