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

public class DynExprDoubleParamOp extends DynExprConstructImpl {

  private static final long serialVersionUID = -7974475975925167731L;

  public static enum Type {

    And,
    Or;

    public static Type fromString(final String value) {
      Type result = null;
      for (Type type : values()) {
        if (value.equals(type.name())) {
          result = type;
        }
      }
      return result;
    }
  }

  private Type type;

  private DynExprConstructImpl left;

  private DynExprConstructImpl right;

  public Type getType() {
    return type;
  }

  public void setType(final Type type) {
    this.type = type;
  }

  public DynExprConstructImpl getLeft() {
    return left;
  }

  public void setLeft(final DynExprConstructImpl left) {
    this.left = left;
  }

  public DynExprConstructImpl getRight() {
    return right;
  }

  public void setRight(final DynExprConstructImpl right) {
    this.right = right;
  }

}
