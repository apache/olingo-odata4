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
package org.apache.olingo.jpa.core.exception;

import org.apache.olingo.jpa.api.exception.ODataJPAException;

public class ODataJPAModelException extends ODataJPAException {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final String DEFAULT_BUNDLE_NAME = "odata-jpa-model-exception-i18n";

  public static enum MessageKeys implements MessageKey {
    INVALID_PERSISTENCE_UNIT;
    
    @Override
    public String getKey() {
      return name();
    }
  }

  public ODataJPAModelException(String developmentMessage, Throwable cause, MessageKey messageKey,
      String[] parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }

  @Override
  protected String getBundleName() {
    return DEFAULT_BUNDLE_NAME ;
  }

}
