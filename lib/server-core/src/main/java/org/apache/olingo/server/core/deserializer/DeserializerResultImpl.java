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
package org.apache.olingo.server.core.deserializer;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;

public class DeserializerResultImpl implements DeserializerResult {
  private Entity entity;
  private EntitySet entitySet;
  private ExpandOption expandOption;

  private DeserializerResultImpl() {}

  @Override
  public Entity getEntity() {
    return entity;
  }

  @Override
  public EntitySet getEntitySet() {
    return entitySet;
  }

  @Override
  public ExpandOption getExpandTree() {
    return expandOption;
  }
  
  public static DeserializerResultBuilder with() {
    return new DeserializerResultBuilder();
  }
  
  public static class DeserializerResultBuilder {
    private Entity entity;
    private EntitySet entitySet;
    private ExpandOption expandOption;

    public DeserializerResult build() {
      DeserializerResultImpl result = new DeserializerResultImpl();
      result.entity = entity;
      result.entitySet = entitySet;
      result.expandOption = expandOption;

      return result;
    }

    public DeserializerResultBuilder entity(final Entity entity) {
      this.entity = entity;
      return this;
    }

    public DeserializerResultBuilder entitySet(final EntitySet entitySet) {
      this.entitySet = entitySet;
      return this;
    }

    public DeserializerResultBuilder expandOption(final ExpandOption expandOption) {
      this.expandOption = expandOption;
      return this;
    }
  }
}
