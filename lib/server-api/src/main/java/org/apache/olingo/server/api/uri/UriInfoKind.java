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
 * Defining the various info kinds
 */
public enum UriInfoKind {

  /**
   * Class: {@link UriInfoAll}<br>
   * URI: http://.../serviceroot/$all
   */
  all,

  /**
   * Class: {@link UriInfoBatch}<br>
   * URI: http://.../serviceroot/$batch
   */
  batch,

  /**
   * Class: {@link UriInfoCrossjoin}<br>
   * URI: http://.../serviceroot/$crossjoin
   */
  crossjoin,

  /**
   * Class: {@link UriInfoEntityId}<br>
   * URI: http://.../serviceroot/$entity(...)
   */
  entityId,

  /**
   * Class: {@link UriInfoMetadata}<br>
   * URI: http://.../serviceroot/$metadata...
   */
  metadata,

  /**
   * Class: {@link UriInfoResource}<br>
   * URI: http://.../serviceroot/entitySet
   */
  resource,

  /**
   * Class: {@link UriInfoService}<br>
   * URI: http://.../serviceroot
   */
  service
}
