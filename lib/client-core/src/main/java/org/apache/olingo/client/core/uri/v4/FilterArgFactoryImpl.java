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
package org.apache.olingo.client.core.uri.v4;

import org.apache.olingo.client.api.uri.FilterArg;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.api.uri.v4.FilterArgFactory;
import org.apache.olingo.client.core.uri.AbstractFilterArgFactory;
import org.apache.olingo.client.core.uri.FilterFunction;
import org.apache.olingo.client.core.uri.FilterLambda;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class FilterArgFactoryImpl extends AbstractFilterArgFactory implements FilterArgFactory {

  public FilterArgFactoryImpl(final ODataServiceVersion version) {
    super(version);
  }

  @Override
  public FilterArg contains(final FilterArg first, final FilterArg second) {
    return new FilterFunction("contains", first, second);
  }

  @Override
  public FilterArg fractionalseconds(final FilterArg param) {
    return new FilterFunction("fractionalseconds", param);
  }

  @Override
  public FilterArg date(final FilterArg param) {
    return new FilterFunction("date", param);
  }

  @Override
  public FilterArg time(final FilterArg param) {
    return new FilterFunction("time", param);
  }

  @Override
  public FilterArg totaloffsetminutes(final FilterArg param) {
    return new FilterFunction("totaloffsetminutes", param);
  }

  @Override
  public FilterArg now() {
    return new FilterFunction("now");
  }

  @Override
  public FilterArg mindatetime() {
    return new FilterFunction("mindatetime");
  }

  @Override
  public FilterArg maxdatetime() {
    return new FilterFunction("maxdatetime");
  }

  @Override
  public FilterArg totalseconds(final FilterArg param) {
    return new FilterFunction("totalseconds", param);
  }

  @Override
  public FilterArg cast(final FilterArg type) {
    return new FilterFunction("cast", type);
  }

  @Override
  public FilterArg cast(final FilterArg expression, final FilterArg type) {
    return new FilterFunction("cast", expression, type);
  }

  @Override
  public FilterArg geoDistance(final FilterArg first, final FilterArg second) {
    return new FilterFunction("geo.distance", first, second);
  }

  @Override
  public FilterArg geoIntersects(final FilterArg first, final FilterArg second) {
    return new FilterFunction("geo.intersects", first, second);
  }

  @Override
  public FilterArg geoLength(final FilterArg first, final FilterArg second) {
    return new FilterFunction("geo.length", first, second);
  }

  @Override
  public FilterArg any(final FilterArg collection, final URIFilter expression) {
    return new FilterLambda(collection, "any", expression);
  }

  @Override
  public FilterArg all(final FilterArg collection, final URIFilter expression) {
    return new FilterLambda(collection, "all", expression);
  }

}
