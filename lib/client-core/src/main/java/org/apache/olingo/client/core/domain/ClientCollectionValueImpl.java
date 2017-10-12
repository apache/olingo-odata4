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
package org.apache.olingo.client.core.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.client.api.domain.AbstractClientValue;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientValue;

public class ClientCollectionValueImpl<OV extends ClientValue> extends AbstractClientValue
        implements ClientCollectionValue<OV> {

  /**
   * Constructor.
   *
   * @param typeName type name.
   */
  public ClientCollectionValueImpl(final String typeName) {
    super(typeName == null || typeName.startsWith("Collection(") ? typeName : "Collection(" + typeName + ")");
  }

  @Override
  public boolean isEnum() {
    return false;
  }

  @Override
  public ClientEnumValue asEnum() {
    return null;
  }

  @Override
  public boolean isComplex() {
    return false;
  }

  @Override
  public Collection<Object> asJavaCollection() {
    final List<Object> result = new ArrayList<Object>();
    for (ClientValue value : values) {
      if (value.isPrimitive()) {
        result.add(value.asPrimitive().toValue());
      } else if (value.isComplex()) {
        result.add(value.asComplex().asJavaMap());
      } else if (value.isCollection()) {
        result.add(value.asCollection().asJavaCollection());
      } else if (value.isEnum()) {
        result.add(value.asEnum().toString());
      }
    }

    return result;
  }

  /**
   * Values.
   */
  protected final List<OV> values = new ArrayList<OV>();

  /**
   * Adds a value to the collection.
   *
   * @param value value to be added.
   */
  @Override
  @SuppressWarnings("unchecked")
  public ClientCollectionValue<OV> add(final ClientValue value) {
    values.add((OV) value);
    return this;
  }

  /**
   * Value iterator.
   *
   * @return value iterator.
   */
  @Override
  public Iterator<OV> iterator() {
    return values.iterator();
  }

  /**
   * Gets collection size.
   *
   * @return collection size.
   */
  @Override
  public int size() {
    return values.size();
  }

  /**
   * Checks if collection is empty.
   *
   * @return 'TRUE' if empty; 'FALSE' otherwise.
   */
  @Override
  public boolean isEmpty() {
    return values.isEmpty();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (values.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof ClientCollectionValueImpl)) {
      return false;
    }
    ClientCollectionValueImpl<?> other = (ClientCollectionValueImpl<?>) obj;
    if (values == null) {
      if (other.values != null) {
        return false;
      }
    } else if (!values.equals(other.values)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ClientCollectionValueImpl [values=" + values + "super[" + super.toString() + "]]";
  }
}
