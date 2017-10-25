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
package myservice.mynamespace.data;

import org.apache.olingo.commons.api.data.Entity;

public class DemoEntityActionResult {
  private Entity entity;
  private boolean created = false;

  public Entity getEntity() {
    return entity;
  }

  public DemoEntityActionResult setEntity(final Entity entity) {
    this.entity = entity;
    return this;
  }

  public boolean isCreated() {
    return created;
  }

  public DemoEntityActionResult setCreated(final boolean created) {
    this.created = created;
    return this;
  }

}
