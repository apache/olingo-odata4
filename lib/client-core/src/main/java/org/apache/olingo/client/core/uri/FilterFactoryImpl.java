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
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class FilterFactoryImpl extends AbstractFilterFactory implements FilterFactory {

  public FilterFactoryImpl(ODataServiceVersion version) {
    super(version);
  }

  @Override
  public FilterArgFactory getArgFactory() {
    return new FilterArgFactoryImpl(version);
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
