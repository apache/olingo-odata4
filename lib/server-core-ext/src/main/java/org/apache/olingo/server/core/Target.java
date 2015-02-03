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
package org.apache.olingo.server.core;

import org.apache.olingo.commons.api.edm.FullQualifiedName;


public class Target extends org.apache.olingo.commons.api.edm.Target {
  private String target;
  private FullQualifiedName fqn;

  public Target(String target) {
    final String[] bindingTargetParts = target.split("/");

    if (bindingTargetParts.length == 1) {
      this.target = bindingTargetParts[0];
    } else {
      this.fqn = new FullQualifiedName(bindingTargetParts[0]);
      this.target = bindingTargetParts[1];
    }
  }

  @Override
  public String getTargetName() {
    return target;
  }

  @Override
  public FullQualifiedName getEntityContainer() {
    return this.fqn;
  }
}
