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
package org.apache.olingo.ext.proxy.context;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class EntityUUID implements Serializable {

  private static final long serialVersionUID = 4855025769803086495L;

  // needed by equals and hashcode
  private final int tempKey;

  private final String containerName;

  private final String entitySetName;

  private final FullQualifiedName name;

  private final Object key;

  public EntityUUID(
          final String containerName,
          final String entitySetName,
          final FullQualifiedName name) {
    this(containerName, entitySetName, name, null);
  }

  public EntityUUID(
          final String containerName,
          final String entitySetName,
          final FullQualifiedName name,
          final Object key) {
    this.containerName = containerName;
    this.entitySetName = entitySetName;
    this.name = name;
    this.key = key;
    this.tempKey = (int) (Math.random() * 1000000);
  }

  public String getContainerName() {
    return containerName;
  }

  public String getEntitySetName() {
    return entitySetName;
  }

  public FullQualifiedName getName() {
    return name;
  }

  public Object getKey() {
    return key;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public boolean equals(Object obj) {
    return key == null
            ? EqualsBuilder.reflectionEquals(this, obj)
            : EqualsBuilder.reflectionEquals(this, obj, "tempKey");
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, "tempKey");
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public String toString() {
    return name.getNamespace() + ":" + containerName + ":" + entitySetName + ":" + name.getName()
            + "(" + (key == null ? null : key.toString()) + ")";
  }
}
