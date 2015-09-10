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

import java.util.List;
import java.util.Map;

/**
 * OData error.
 */
public class ODataError {

  private String code;
  private String message;
  private String target;
  private List<ODataErrorDetail> details;
  private Map<String, String> innerError;

  /**
   * The value for the code name/value pair is a language-independent string. Its value is a service-defined error code.
   * This code serves as a sub-status for the HTTP error code specified in the response. MAY be null.
   * @return the error code as a string
   */
  public String getCode() {
    return code;
  }

  /**
   * The value for the code name/value pair is a language-independent string. Its value is a service-defined error code.
   * This code serves as a sub-status for the HTTP error code specified in the response. MAY be null.
   * @param code the service defined error code for this error
   * @return this for method chaining
   */
  public ODataError setCode(final String code) {
    this.code = code;
    return this;
  }

  /**
   * The value for the message name/value pair MUST be a human-readable, language-dependent representation of the error.
   * MUST not be null
   * @return the message string
   */
  public String getMessage() {
    return message;
  }

  /**
   * The value for the message name/value pair MUST be a human-readable, language-dependent representation of the error.
   * MUST not be null
   * @param message message for this error
   * @return this for method chaining
   */
  public ODataError setMessage(final String message) {
    this.message = message;
    return this;
  }

  /**
   * The value for the target name/value pair is the target of the particular error (for example, the name of the
   * property in error). MAY be null.
   * @return the target string
   */
  public String getTarget() {
    return target;
  }

  /**
   * The value for the target name/value pair is the target of the particular error (for example, the name of the
   * property in error). MAY be null.
   * @param target target to which this error is related to
   * @return this for method chaining
   */
  public ODataError setTarget(final String target) {
    this.target = target;
    return this;
  }

  /**
   * Gets error details.
   *
   * @return ODataErrorDetail list.
   */
  public List<ODataErrorDetail> getDetails() {
    return details;
  }

  /**
   * Sets error details.
   *
   * @return this for method chaining.
   */
  public ODataError setDetails(final List<ODataErrorDetail> details) {
    this.details = details;
    return this;
  }

  /**
   * Gets server defined key-value pairs for debug environment only.
   *
   * @return a pair representing server defined object. MAY be null.
   */
  public Map<String, String> getInnerError() {
    return innerError;
  }

  /**
   * Sets server defined key-value pairs for debug environment only.
   *
   * @return this for method chaining.
   */
  public ODataError setInnerError(final Map<String, String> innerError) {
    this.innerError = innerError;
    return this;
  }
}
