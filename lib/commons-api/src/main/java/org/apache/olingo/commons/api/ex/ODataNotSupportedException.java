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
package org.apache.olingo.commons.api.ex;

/**
 * Core runtime exception for OData.
 */
public class ODataNotSupportedException extends ODataRuntimeException {

  private static final long serialVersionUID = 42L;

  /**
   * Create with <code>message</code>.
   *
   * @param msg message text for exception
   */
  public ODataNotSupportedException(final String msg) {
    super(msg);
  }

  /**
   * Create with <code>message</code> for and <code>cause</code> of exception.
   *
   * @param msg message text for exception
   * @param cause cause of exception
   */
  public ODataNotSupportedException(final String msg, final Exception cause) {
    super(msg, cause);
  }

  /**
   * Create with <code>cause</code> of exception.
   *
   * @param cause cause of exception
   */
  public ODataNotSupportedException(final Exception cause) {
    super(cause);
  }

}
