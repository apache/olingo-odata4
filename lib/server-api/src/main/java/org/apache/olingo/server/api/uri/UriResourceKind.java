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
package org.apache.olingo.server.api.uri;

/**
 * Defining the various resource part types
 */
public enum UriResourceKind {

  /**
   * Class: {@link UriResourceAction}<br>
   * URI: http://.../serviceroot/action()
   */
  action,

  /**
   * Class: {@link UriResourceComplexProperty}<br>
   * URI: http://.../serviceroot/entityset(1)/complexproperty()
   */
  complexProperty,

  /**
   * Class: {@link UriResourceCount}<br>
   * URI: http://.../serviceroot/entityset/$count
   */
  count,

  /**
   * Class: {@link UriResourceEntitySet}<br>
   * URI: http://.../serviceroot/entityset
   */
  entitySet,

  /**
   * Class: {@link UriResourceFunction}<br>
   * URI: http://.../serviceroot/functionimport(P1=1,P2='a')
   */
  function,

  /**
   * Class: {@link UriResourceIt}<br>
   * URI: http://.../serviceroot/entityset?$filter=$it/property
   */
  it,

  /**
   * Class: {@link UriResourceLambdaAll}<br>
   * URI: http://.../serviceroot/entityset/all(...)
   */
  lambdaAll,

  /**
   * Class: {@link UriResourceLambdaAny}<br>
   * URI: http://.../serviceroot/entityset/any(...)
   */
  lambdaAny,

  /**
   * Class: {@link UriResourceLambdaVariable}<br>
   * URI: http://.../serviceroot/entityset/listofstring/any(d: 'string' eq d)
   */
  lambdaVariable,

  /**
   * Class: {@link UriResourceNavigation}<br>
   * URI: http://.../serviceroot/entityset(1)/navProperty
   */
  navigationProperty,

  /**
   * Class: {@link UriResourceRef}<br>
   * URI: http://.../serviceroot/entityset/$ref
   */
  ref,

  /**
   * Class: {@link UriResourceRoot}<br>
   * URI: http://.../serviceroot/entityset(1)?$filter=property eq $root/singleton/configstring
   */
  root,

  /**
   * Class: {@link UriResourceProperty}<br>
   * URI: http://.../serviceroot/entityset(1)/property
   */
  primitiveProperty,

  /**
   * Class: {@link UriResourceSingleton}<br>
   * URI: http://.../serviceroot/singleton
   */
  singleton,

  /**
   * Class: {@link UriResourceValue}<br>
   * URI: http://.../serviceroot/entityset(1)/property/$value
   */
  value,
}
