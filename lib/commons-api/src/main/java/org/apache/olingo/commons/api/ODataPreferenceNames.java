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
package org.apache.olingo.commons.api;

/**
 * Names of preferences defined in the OData standard.
 */
public enum ODataPreferenceNames {

  ALLOW_ENTITY_REFERENCES("odata.allow-entityreferences"),
  CALLBACK("odata.callback"),
  CONTINUE_ON_ERROR("odata.continue-on-error"),
  INCLUDE_ANNOTATIONS("odata.include-annotations"),
  MAX_PAGE_SIZE("odata.maxpagesize"),
  TRACK_CHANGES("odata.track-changes"),
  RETURN("return"),
  RESPOND_ASYNC("respond-async"),
  WAIT("wait");

  private final String preferenceName;

  ODataPreferenceNames(final String preferenceName) {
    this.preferenceName = preferenceName;
  }

  @Override
  public String toString() {
    return preferenceName;
  }
}
