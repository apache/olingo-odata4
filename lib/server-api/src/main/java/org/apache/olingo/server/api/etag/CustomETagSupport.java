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
package org.apache.olingo.server.api.etag;

import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.server.api.OlingoExtension;

/**
 * <p>Processors that would like to support etags for certain entity sets can implement this
 * interface.</p>
 * <p>If implemented this interface can be registered at the ODataHttpHandler. This will result in change request to
 * require an if-match/if-none-match or an if-modified-since/if-unmodified-since header. Otherwise the request will
 * result in a "Precondition Required" response</p>
 */
public interface CustomETagSupport extends OlingoExtension {

  /**
   * This method will be called for update requests which target an entity or a property of an entity.
   * If this method returns true and an header is not specified we will return a "Precondition Required" response.
   * Validation has to be performed inside the processor methods after the dispatching.
   * If this method returns false and an header is specified we will ignore the header.
   * @param entitySetOrSingleton
   * @return true if the entity set specified needs an if-match/if-none-match header
   */
  boolean hasETag(EdmBindingTarget entitySetOrSingleton);

  /**
   * This method will be called for update requests which target a media entity value.
   * If this method returns true and an header is not specified we will return a "Precondition Required" response.
   * Validation has to be performed inside the processor methods after the dispatching.
   * If this method returns false and an header is specified we will ignore the header.
   * @param entitySetOrSingleton
   * @return true if the entity set specified needs an if-match/if-none-match header
   */
  boolean hasMediaETag(EdmBindingTarget entitySetOrSingleton);
}
