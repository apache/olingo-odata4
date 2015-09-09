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
package org.apache.olingo.commons.api.edm.provider.annotation;

/**
 * Represents a constant expression
 */
public interface ConstantAnnotationExpression extends AnnotationExpression {
  
  /**
   * Type of the constant expression
   */
  public enum Type {
    /**
     * Type Edm.binary
     */
    Binary,
    /**
     * Type Edm.Bool
     */
    Bool,
    /**
     * Type Edm.Date
     */
    Date,
    /**
     * Type Edm.DateTimeOffset
     */
    DateTimeOffset,
    /**
     * Type Edm.Decimal
     */
    Decimal,
    /**
     * Type Edm.Duration
     */
    Duration,
    /**
     * Type Edm.EnumMeber
     */
    EnumMember,
    /**
     * Type Edm.Float
     */
    Float,
    /**
     * Type Edm.GUID
     */
    Guid,
    /**
     * Type Integer
     */
    Int,
    /**
     * Type Edm.String
     */
    String,
    /**
     * Type Edm.TimeOfDay
     */
    TimeOfDay;
    
    /**
     * Creates a new type by a given string e.g. "TimeOfDay"
     * @param value Type as string
     * @return  Type
     */
    public static Type fromString(final String value) {
      Type result = null;
      try {
        result = valueOf(value);
      } catch (IllegalArgumentException e) {
        // ignore
      }
      return result;
    }
  }

  /**
   * Returns the type of the constant exprssion
   * @return type of the constant expresion
   */
  Type getType();
  
  /**
   * Sets the type of the constant expression
   * @param type
   */
  void setType(Type type);
  
  /**
   * Value of the constant expression
   * @return value of the constant expression as String
   */
  String getValue();
  
  /**
   * Sets the value of the constant expression
   * @param value value of the constant expression
   */
  void setValue(String value);
}
