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
package org.apache.olingo.commons.api.edm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A full qualified name of any element in the EDM consists of a name and a namespace.
 */
public class FullQualifiedName {

  private final String namespace;

  private final String name;

  private final String fqn;

  /**
   * @param namespace
   * @param name
   */
  public FullQualifiedName(final String namespace, final String name) {
    this.namespace = namespace;
    this.name = name;
    this.fqn = namespace + "." + name;
  }

  /**
   * @param namespaceAndName
   */
  public FullQualifiedName(final String namespaceAndName) {
    final int dotIdx = namespaceAndName.lastIndexOf('.');
    if (dotIdx == -1 || dotIdx == 0 || dotIdx == namespaceAndName.length() - 1) {
      throw new IllegalArgumentException(
              "Malformed " + FullQualifiedName.class.getSimpleName() + ": " + namespaceAndName);
    }

    this.fqn = namespaceAndName;
    this.namespace = this.fqn.substring(0, dotIdx);
    this.name = this.fqn.substring(dotIdx + 1);
  }

  /**
   * @return namespace
   */
  public String getNamespace() {
    return namespace;
  }

  /**
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * @return namespace.name
   */
  public String getFullQualifiedNameAsString() {
    return fqn;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public String toString() {
    return fqn;
  }
}
