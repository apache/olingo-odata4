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
package org.apache.olingo.commons.api.data;

import org.apache.olingo.commons.api.ex.ODataNotSupportedException;

public abstract class ComplexIterator extends CollectionIterator<ComplexValue> {

  /**
   * String representation of type (can be null)
   */
  private final String type;

  public ComplexIterator() {
    this.type = null;
  }

  public ComplexIterator(final String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void remove() {
    //"Remove is not supported for iteration over Collection of Complex values."
    throw new ODataNotSupportedException("Complex Iterator does not support remove()");
  }
}

