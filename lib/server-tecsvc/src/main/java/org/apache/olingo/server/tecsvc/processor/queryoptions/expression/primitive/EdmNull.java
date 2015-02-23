/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.tecsvc.processor.queryoptions.expression.primitive;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.core.edm.primitivetype.SingletonPrimitiveType;


public final class EdmNull extends SingletonPrimitiveType {
  
  private static final EdmNull instance = new EdmNull();
  
  public static EdmNull getInstance() {
    return instance;
  }
  
  @Override
  public Class<?> getDefaultType() {
    return Object.class;
  }

  @Override
  protected <T> T internalValueOfString(String value, Boolean isNullable, Integer maxLength, Integer precision,
      Integer scale, Boolean isUnicode, Class<T> returnType) throws EdmPrimitiveTypeException {
    if (!value.equals("null")) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
    }

    if (returnType.isAssignableFrom(Object.class)) {
      return returnType.cast(new Object());
    } else {
      throw new ClassCastException("unsupported return type " + returnType.getSimpleName());
    }
  }

  @Override
  protected <T> String internalValueToString(T value, Boolean isNullable, Integer maxLength, Integer precision,
      Integer scale, Boolean isUnicode) throws EdmPrimitiveTypeException {
    return "null";
  }

}
