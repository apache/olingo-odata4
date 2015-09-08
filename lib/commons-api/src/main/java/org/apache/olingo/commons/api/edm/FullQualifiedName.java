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

import java.io.Serializable;

/**
 * A full qualified name of any element in the EDM consists of a name and a namespace.
 */
public final class FullQualifiedName implements Serializable {

  private static final long serialVersionUID = -4063629050858999076L;

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
    fqn = namespace + "." + name;
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

    fqn = namespaceAndName;
    namespace = fqn.substring(0, dotIdx);
    name = fqn.substring(dotIdx + 1);
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FullQualifiedName that = (FullQualifiedName) o;

    if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null) {
      return false;
    }
    return !(fqn != null ? !fqn.equals(that.fqn) : that.fqn != null);

  }

  @Override
  public int hashCode() {
    int result = namespace != null ? namespace.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (fqn != null ? fqn.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return fqn;
  }
}
