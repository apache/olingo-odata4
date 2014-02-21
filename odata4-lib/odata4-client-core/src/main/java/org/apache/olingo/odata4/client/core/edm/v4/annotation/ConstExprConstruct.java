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
package org.apache.olingo.odata4.client.core.edm.v4.annotation;

public class ConstExprConstruct extends ExprConstruct {

  private static final long serialVersionUID = 2250072064504668969L;

  public enum Type {

    Binary,
    Bool,
    Date,
    DateTimeOffset,
    Decimal,
    Duration,
    EnumMember,
    Float,
    Guid,
    Int,
    String,
    TimeOfDay;

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

  private Type type;

  private String value;

  public Type getType() {
    return type;
  }

  public void setType(final Type type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

}
