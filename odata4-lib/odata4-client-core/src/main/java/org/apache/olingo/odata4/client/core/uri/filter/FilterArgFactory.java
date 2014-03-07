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
package org.apache.olingo.odata4.client.core.uri.filter;

import org.apache.olingo.odata4.client.api.uri.filter.FilterArg;

/**
 * OData filter arguments factory.
 */
public final class FilterArgFactory {

  private FilterArgFactory() {
    // Empty private constructor for static utility classes
  }

  public static FilterArg property(final String propertyPath) {
    return new FilterProperty(propertyPath);
  }

  public static FilterArg literal(final Object value) {
    return new FilterLiteral(value);
  }

  public static FilterArg add(final FilterArg first, final FilterArg second) {
    return new FilterOp("add", first, second);
  }

  public static FilterArg sub(final FilterArg first, final FilterArg second) {
    return new FilterOp("add", first, second);
  }

  public static FilterArg mul(final FilterArg first, final FilterArg second) {
    return new FilterOp("mul", first, second);
  }

  public static FilterArg div(final FilterArg first, final FilterArg second) {
    return new FilterOp("div", first, second);
  }

  public static FilterArg mod(final FilterArg first, final FilterArg second) {
    return new FilterOp("mod", first, second);
  }

  public static FilterArg substringof(final FilterArg first, final FilterArg second) {
    return new FilterFunction("substringof", first, second);
  }

  public static FilterArg endswith(final FilterArg first, final FilterArg second) {
    return new FilterFunction("endswith", first, second);
  }

  public static FilterArg startswith(final FilterArg first, final FilterArg second) {
    return new FilterFunction("startswith", first, second);
  }

  public static FilterArg length(final FilterArg param) {
    return new FilterFunction("length", param);
  }

  public static FilterArg indexof(final FilterArg first, final FilterArg second) {
    return new FilterFunction("indexof", first, second);
  }

  public static FilterArg replace(
          final FilterArg first, final FilterArg second, final FilterArg third) {

    return new FilterFunction("replace", first, second, third);
  }

  public static FilterArg substring(final FilterArg arg, final FilterArg pos) {
    return new FilterFunction("substring", arg, pos);
  }

  public static FilterArg substring(
          final FilterArg arg, final FilterArg pos, final FilterArg length) {

    return new FilterFunction("substring", arg, pos, length);
  }

  public static FilterArg tolower(final FilterArg param) {
    return new FilterFunction("tolower", param);
  }

  public static FilterArg toupper(final FilterArg param) {
    return new FilterFunction("toupper", param);
  }

  public static FilterArg trim(final FilterArg param) {
    return new FilterFunction("trim", param);
  }

  public static FilterArg concat(final FilterArg first, final FilterArg second) {
    return new FilterFunction("concat", first, second);
  }

  public static FilterArg day(final FilterArg param) {
    return new FilterFunction("day", param);
  }

  public static FilterArg hour(final FilterArg param) {
    return new FilterFunction("hour", param);
  }

  public static FilterArg minute(final FilterArg param) {
    return new FilterFunction("minute", param);
  }

  public static FilterArg month(final FilterArg param) {
    return new FilterFunction("month", param);
  }

  public static FilterArg second(final FilterArg param) {
    return new FilterFunction("second", param);
  }

  public static FilterArg year(final FilterArg param) {
    return new FilterFunction("year", param);
  }

  public static FilterArg round(final FilterArg param) {
    return new FilterFunction("round", param);
  }

  public static FilterArg floor(final FilterArg param) {
    return new FilterFunction("floor", param);
  }

  public static FilterArg ceiling(final FilterArg param) {
    return new FilterFunction("ceiling", param);
  }

  public static FilterArg isof(final FilterArg param) {
    return new FilterFunction("isof", param);
  }

  public static FilterArg isof(final FilterArg first, final FilterArg second) {
    return new FilterFunction("isof", first, second);
  }
}
