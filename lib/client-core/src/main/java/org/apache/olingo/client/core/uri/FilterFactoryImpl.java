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
package org.apache.olingo.client.core.uri;

import org.apache.olingo.client.api.uri.FilterArg;
import org.apache.olingo.client.api.uri.FilterArgFactory;
import org.apache.olingo.client.api.uri.FilterFactory;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.commons.api.edm.EdmEnumType;

public class FilterFactoryImpl implements FilterFactory {

  @Override
  public URIFilter match(final FilterArg arg) {
    return new MatchFilter(arg);
  }

  @Override
  public URIFilter eq(final String key, final Object value) {
    return new EqFilter(getArgFactory().property(key), getArgFactory().literal(value));
  }

  @Override
  public URIFilter eq(final FilterArg left, final FilterArg right) {
    return new EqFilter(left, right);
  }

  @Override
  public URIFilter ne(final String key, final Object value) {
    return new NeFilter(getArgFactory().property(key), getArgFactory().literal(value));
  }

  @Override
  public URIFilter ne(final FilterArg left, final FilterArg right) {
    return new NeFilter(left, right);
  }

  @Override
  public URIFilter gt(final String key, final Object value) {
    return new GtFilter(getArgFactory().property(key), getArgFactory().literal(value));
  }

  @Override
  public URIFilter gt(final FilterArg left, final FilterArg right) {
    return new GtFilter(left, right);
  }

  @Override
  public URIFilter ge(final String key, final Object value) {
    return new GeFilter(getArgFactory().property(key), getArgFactory().literal(value));
  }

  @Override
  public URIFilter ge(final FilterArg left, final FilterArg right) {
    return new GeFilter(left, right);
  }

  @Override
  public URIFilter lt(final String key, final Object value) {
    return new LtFilter(getArgFactory().property(key), getArgFactory().literal(value));
  }

  @Override
  public URIFilter lt(final FilterArg left, final FilterArg right) {
    return new LtFilter(left, right);
  }

  @Override
  public URIFilter le(final String key, final Object value) {
    return new LeFilter(getArgFactory().property(key), getArgFactory().literal(value));
  }

  @Override
  public URIFilter le(final FilterArg left, final FilterArg right) {
    return new LeFilter(left, right);
  }

  @Override
  public URIFilter and(final URIFilter left, final URIFilter right) {
    return new AndFilter(left, right);
  }

  @Override
  public URIFilter or(final URIFilter left, final URIFilter right) {
    return new OrFilter(left, right);
  }

  @Override
  public URIFilter not(final URIFilter filter) {
    return new NotFilter(filter);
  }
  
  @Override
  public FilterArgFactory getArgFactory() {
    return new FilterArgFactoryImpl();
  }

  @Override
  public URIFilter has(final String key, final EdmEnumType enumType, final String memberName) {
    return has(getArgFactory().property(key), enumType, memberName);
  }

  @Override
  public URIFilter has(final FilterArg left, final EdmEnumType enumType, final String memberName) {
    return new HasFilter(left, new FilterProperty(enumType.toUriLiteral(memberName)));
  }

}
