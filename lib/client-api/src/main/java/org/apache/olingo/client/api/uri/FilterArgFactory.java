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


public interface FilterArgFactory {

  FilterArg nullValue();

  FilterArg add(FilterArg first, FilterArg second);

  FilterArg ceiling(FilterArg param);

  FilterArg concat(FilterArg first, FilterArg second);

  FilterArg day(FilterArg param);

  FilterArg div(FilterArg first, FilterArg second);

  FilterArg endswith(FilterArg first, FilterArg second);

  FilterArg floor(FilterArg param);

  FilterArg hour(FilterArg param);

  FilterArg indexof(FilterArg first, FilterArg second);

  FilterArg isof(FilterArg type);

  FilterArg isof(FilterArg expression, FilterArg type);

  FilterArg length(FilterArg param);

  FilterArg literal(Object value);

  FilterArg minute(FilterArg param);

  FilterArg mod(FilterArg first, FilterArg second);

  FilterArg month(FilterArg param);

  FilterArg mul(FilterArg first, FilterArg second);

  FilterArg property(String propertyPath);

  FilterArg replace(FilterArg first, FilterArg second, FilterArg third);

  FilterArg round(FilterArg param);

  FilterArg second(FilterArg param);

  FilterArg startswith(FilterArg first, FilterArg second);

  FilterArg sub(FilterArg first, FilterArg second);

  FilterArg substring(FilterArg arg, FilterArg pos);

  FilterArg substring(FilterArg arg, FilterArg pos, FilterArg length);

  FilterArg tolower(FilterArg param);

  FilterArg toupper(FilterArg param);

  FilterArg trim(FilterArg param);

  FilterArg year(FilterArg param);
  
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
  
  FilterArg any(FilterArg collection, String lambdaVariable, URIFilter expression);

  FilterArg all(FilterArg collection, URIFilter expression);
  
  FilterArg all(FilterArg collection, String lambdaVariable, URIFilter expression);

}
