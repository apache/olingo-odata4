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

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmType;

import java.math.BigDecimal;
import java.util.UUID;

public interface ClientPrimitiveValue extends ClientValue {

  interface Builder {

    Builder setType(EdmType type);

    Builder setType(EdmPrimitiveTypeKind type);

    Builder setValue(Object value);

    ClientPrimitiveValue build();

    ClientPrimitiveValue buildBoolean(Boolean value);

    ClientPrimitiveValue buildInt16(Short value);

    ClientPrimitiveValue buildInt32(Integer value);

    ClientPrimitiveValue buildInt64(Long value);

    ClientPrimitiveValue buildSingle(Float value);

    ClientPrimitiveValue buildDouble(Double value);

    ClientPrimitiveValue buildString(String value);

    ClientPrimitiveValue buildGuid(UUID value);

    ClientPrimitiveValue buildBinary(byte[] value);
    
    ClientPrimitiveValue buildDecimal(BigDecimal value);
    
    ClientPrimitiveValue buildDuration(BigDecimal value);
  }

  EdmPrimitiveTypeKind getTypeKind();

  EdmPrimitiveType getType();

  /**
   * Returns the current value as generic Object.
   * 
   * @return an uncasted instance of this value
   */
  Object toValue();

  /**
   * Returns the current value casted to the given type.
   * 
   * @param <T> cast type
   * @param reference class reference
   * @return the current value as typed java instance
   * @throws EdmPrimitiveTypeException if the object is not assignable to the type T.
   */
  <T> T toCastValue(Class<T> reference) throws EdmPrimitiveTypeException;

  /**
   * Serialize the current value as String.
   * 
   * @return a String representation of this value
   */
  @Override
  String toString();

}
