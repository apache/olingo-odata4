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
package org.apache.olingo.server.api.uri.queryoption;

/**
 * Defining the supported system query options
 */

public enum SystemQueryOptionKind {

  /**
   * See {@link FilterOption}<br>
   */
  FILTER("$filter"),

  /**
   * See {@link FormatOption}<br>
   */
  FORMAT("$format"),

  /**
   * See {@link ExpandOption}<br>
   */
  EXPAND("$expand"),

  /**
   * See {@link IdOption}<br>
   */
  ID("$id"),

  /**
   * See {@link CountOption}<br>
   */
  COUNT("$count"),

  /**
   * See {@link OrderByOption}<br>
   */
  ORDERBY("$orderby"),

  /**
   * See {@link SearchOption}<br>
   */
  SEARCH("$search"),

  /**
   * See {@link SelectOption}<br>
   */
  SELECT("$select"),

  /**
   * See {@link SkipOption}<br>
   */
  SKIP("$skip"),

  /**
   * See {@link SkipTokenOption}<br>
   */
  SKIPTOKEN("$skiptoken"),

  /**
   * See {@link TopOption}<br>
   */
  TOP("$top"),

  /**
   * See {@link LevelsExpandOption}<br>
   */
  LEVELS("$level");

  private String syntax;

  SystemQueryOptionKind(final String syntax) {
    this.syntax = syntax;
  }

  @Override
  public String toString() {
    return syntax;
  }
}
