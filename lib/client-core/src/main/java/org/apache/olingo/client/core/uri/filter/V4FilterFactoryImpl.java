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
package org.apache.olingo.client.core.uri.filter;

import org.apache.olingo.client.api.uri.filter.FilterArg;
import org.apache.olingo.client.api.uri.filter.URIFilter;
import org.apache.olingo.client.api.uri.filter.V4FilterArgFactory;
import org.apache.olingo.client.api.uri.filter.V4FilterFactory;
import org.apache.olingo.commons.api.edm.EdmEnumType;

public class V4FilterFactoryImpl extends AbstractFilterFactory implements V4FilterFactory {

  private static final long serialVersionUID = -5358934829490623191L;

  @Override
  public V4FilterArgFactory getArgFactory() {
    return new V4FilterArgFactoryImpl();
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
