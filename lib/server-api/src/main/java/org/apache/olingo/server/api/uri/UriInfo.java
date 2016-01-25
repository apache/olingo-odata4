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

import java.util.List;

import org.apache.olingo.server.api.uri.queryoption.AliasQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;

/**
 * <p>Object acting as general access to URI information extracted from the request URI.</p>
 * <p>Depending on the URI info kind different interfaces are used to provide access to that information.
 * Use method {@link #getKind()} to obtain URI info kind information and to perform an appropriate cast.</p>
 */
public interface UriInfo extends UriInfoService, UriInfoMetadata, UriInfoResource, UriInfoBatch,
UriInfoAll, UriInfoCrossjoin, UriInfoEntityId {

  /**
   * See {@link UriInfoKind} for more details which kinds are allowed.
   * @return the kind of this URI info object.
   */
  UriInfoKind getKind();

  /**
   * Convenience casting method.
   * @return this as a {@link UriInfoService} object
   */
  UriInfoService asUriInfoService();

  /**
   * Convenience casting method.
   * @return this as a {@link UriInfoAll} object
   */
  UriInfoAll asUriInfoAll();

  /**
   * Convenience casting method.
   * @return this as a {@link UriInfoBatch} object
   */
  UriInfoBatch asUriInfoBatch();

  /**
   * Convenience casting method.
   * @return this as a {@link UriInfoCrossjoin} object
   */
  UriInfoCrossjoin asUriInfoCrossjoin();

  /**
   * Convenience casting method.
   * @return this as a {@link UriInfoEntityId} object
   */
  UriInfoEntityId asUriInfoEntityId();

  /**
   * Convenience casting method.
   * @return this as a {@link UriInfoMetadata} object
   */
  UriInfoMetadata asUriInfoMetadata();

  /**
   * Convenience casting method.
   * @return this as a {@link UriInfoResource} object
   */
  UriInfoResource asUriInfoResource();

  /**
   * Gets a list of all system query options which were in the URI.
   * @return a list of all system query options used
   */
  List<SystemQueryOption> getSystemQueryOptions();

  /**
   * Gets a list of all alias definitions which were in the URI (including aliases not used anywhere).
   * @return a list of all alias definitions
   */
  List<AliasQueryOption> getAliases();
}
