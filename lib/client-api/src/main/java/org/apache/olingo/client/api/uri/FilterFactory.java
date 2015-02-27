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
package org.apache.olingo.client.api.uri;

import org.apache.olingo.commons.api.edm.EdmEnumType;

public interface FilterFactory {

  FilterArgFactory getArgFactory();

  URIFilter match(FilterArg arg);

  URIFilter eq(String key, Object value);

  URIFilter eq(FilterArg left, FilterArg right);

  URIFilter ne(String key, Object value);

  URIFilter ne(FilterArg left, FilterArg right);

  URIFilter gt(String key, Object value);

  URIFilter gt(FilterArg left, FilterArg right);

  URIFilter ge(String key, Object value);

  URIFilter ge(FilterArg left, FilterArg right);

  URIFilter lt(String key, Object value);

  URIFilter lt(FilterArg left, FilterArg right);

  URIFilter le(String key, Object value);

  URIFilter le(FilterArg left, FilterArg right);

  URIFilter and(URIFilter left, URIFilter right);

  URIFilter or(URIFilter left, URIFilter right);

  URIFilter not(URIFilter filter);
  
  URIFilter has(String key, EdmEnumType enumType, String memberName);

  URIFilter has(FilterArg left, EdmEnumType enumType, String memberName);

}
