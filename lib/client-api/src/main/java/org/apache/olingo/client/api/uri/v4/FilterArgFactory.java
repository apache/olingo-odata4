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
package org.apache.olingo.client.api.uri.v4;

import org.apache.olingo.client.api.uri.CommonFilterArgFactory;
import org.apache.olingo.client.api.uri.FilterArg;
import org.apache.olingo.client.api.uri.URIFilter;

public interface FilterArgFactory extends CommonFilterArgFactory {

  FilterArg contains(FilterArg first, FilterArg second);

  FilterArg fractionalseconds(FilterArg param);

  FilterArg date(FilterArg param);

  FilterArg time(FilterArg param);

  FilterArg totaloffsetminutes(FilterArg param);

  FilterArg now();

  FilterArg mindatetime();

  FilterArg maxdatetime();

  FilterArg totalseconds(FilterArg param);

  FilterArg cast(FilterArg type);

  FilterArg cast(FilterArg expression, FilterArg type);

  FilterArg geoDistance(FilterArg first, FilterArg second);

  FilterArg geoIntersects(FilterArg first, FilterArg second);

  FilterArg geoLength(FilterArg first, FilterArg second);

  FilterArg any(FilterArg collection, URIFilter expression);

  FilterArg all(FilterArg collection, URIFilter expression);

}
