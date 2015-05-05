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

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;

/**
 * Used to describe an action used within an resource path
 * For example: http://.../serviceroot/action()
 */
public interface UriResourceAction extends UriResourcePartTyped {

  /**
   * If the resource path specifies an action import this method will deliver the unbound action for the action import.
   * @return Action used in the resource path or action import
   */
  EdmAction getAction();

  /**
   * Convenience method which returns the {@link EdmActionImport} which was used in
   * the resource path to define the {@link EdmAction}.
   * @return Action Import used in the resource path
   */
  EdmActionImport getActionImport();

}
