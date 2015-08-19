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
package org.apache.olingo.client.api.domain;

/**
 * Abstract representation of an OData entity property value.
 */
public abstract class AbstractClientValue implements ClientValue {

  /**
   * Type name;
   */
  private final String typeName;

  public AbstractClientValue(final String typeName) {
    this.typeName = typeName;
  }

  @Override
  public String getTypeName() {
    return typeName;
  }

  /**
   * Check is is a primitive value.
   * 
   * @return 'TRUE' if primitive; 'FALSE' otherwise.
   */
  @Override
  public boolean isPrimitive() {
    return (this instanceof ClientPrimitiveValue);
  }

  /**
   * Casts to primitive value.
   * 
   * @return primitive value.
   */
  @Override
  public ClientPrimitiveValue asPrimitive() {
    return isPrimitive() ? (ClientPrimitiveValue) this : null;
  }

  /**
   * Check is is a complex value.
   * 
   * @return 'TRUE' if complex; 'FALSE' otherwise.
   */
  @Override
  public boolean isComplex() {
    return (this instanceof ClientComplexValue);
  }

  /**
   * Casts to complex value.
   * 
   * @return complex value.
   */
  @Override
  public ClientComplexValue asComplex() {
    return isComplex() ? (ClientComplexValue) this : null;
  }

  /**
   * Check is is a collection value.
   * 
   * @return 'TRUE' if collection; 'FALSE' otherwise.
   */
  @Override
  public boolean isCollection() {
    return (this instanceof ClientCollectionValue);
  }

  /**
   * Casts to collection value.
   * 
   * @return collection value.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <OV extends ClientValue> ClientCollectionValue<OV> asCollection() {
    return isCollection() ? (ClientCollectionValue<OV>) this : null;
  }

  
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof AbstractClientValue)) {
      return false;
    }
    AbstractClientValue other = (AbstractClientValue) obj;
    if (typeName == null) {
      if (other.typeName != null) {
        return false;
      }
    } else if (!typeName.equals(other.typeName)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "AbstractClientValue [typeName=" + typeName + "]";
  }
}
