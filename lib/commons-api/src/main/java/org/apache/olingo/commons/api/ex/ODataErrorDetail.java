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
 * OData details， for example <tt>{ "error": {..., "details":[
 * {"code": "301","target": "$search" ,"message": "$search query option not supported"}
 * ],...}}</tt>.
 */
public class ODataErrorDetail {

  private String code;
  private String message;
  private String target;

  /**
   * Gets error code.
   *
   * @return error code.
   */
  public String getCode() {
    return code;
  }

  public ODataErrorDetail setCode(final String code) {
    this.code = code;
    return this;
  }

  /**
   * Gets error message.
   *
   * @return error message.
   */
  public String getMessage() {
    return message;
  }

  public ODataErrorDetail setMessage(final String message) {
    this.message = message;
    return this;
  }

  /**
   * Gets error target.
   *
   * @return error message.
   */
  public String getTarget() {
    return target;
  }

  /**
   * Set the error target.
   *
   * @param target the error target
   * @return this ODataErrorDetail instance (fluent builder)
   */
  public ODataErrorDetail setTarget(final String target) {
    this.target = target;
    return this;
  }
}
