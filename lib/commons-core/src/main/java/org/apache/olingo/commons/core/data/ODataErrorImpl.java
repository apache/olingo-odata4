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
package org.apache.olingo.commons.core.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.domain.ODataError;
import org.apache.olingo.commons.api.domain.ODataErrorDetail;

/**
 * Example:
 * <tt>
 * {
 * "error": { "code": "501", "message": "Unsupported functionality", "target": "query", "details": [ { "code": "301",
 * "target": "$search", "message": "$search query option not supported" } ], "innererror": { "trace": [...], "context":
 * {...} } } }
 * </tt>.
 */
public class ODataErrorImpl implements ODataError {

  private String code;

  private String message;

  private String target;

  private List<ODataErrorDetail> details;

  private Map<String, String> innerError = new LinkedHashMap<String, String>();

  @Override
  public String getCode() {
    return code;
  }

  public void setCode(final String code) {
    this.code = code;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  @Override
  public String getTarget() {
    return target;
  }

  public void setTarget(final String target) {
    this.target = target;
  }

  @Override
  public List<ODataErrorDetail> getDetails() {
    return details;
  }

  public void setDetails(final List<ODataErrorDetail> detail) {
    details = detail;
  }

  @Override
  public Map<String, String> getInnerError() {
    return innerError;
  }
}
