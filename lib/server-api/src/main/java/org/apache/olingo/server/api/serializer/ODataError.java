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
package org.apache.olingo.server.api.serializer;

//TODO: Where to put this class
public class ODataError {

  String code;
  String message;
  String target;

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
   * @param code
   * @return this for method chaining
   */
  public ODataError setCode(String code) {
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
   * @param message
   * @return this for method chaining
   */
  public ODataError setMessage(String message) {
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
   * @param target
   * @return this for method chaining
   */
  public ODataError setTarget(String target) {
    this.target = target;
    return this;
  }

}
