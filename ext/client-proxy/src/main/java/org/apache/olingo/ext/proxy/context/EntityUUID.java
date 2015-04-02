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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.net.URI;

import org.apache.olingo.ext.proxy.api.EntityType;

public class EntityUUID implements Serializable {

  private static final long serialVersionUID = 4855025769803086495L;

  private final URI entitySetURI;

  private final Object key;

  /**
   * Needed when representing a new entity, where key is potentially null. The temp key is used via reflection
   */
  @SuppressWarnings("unused")
  private final int tempKey;

  private Class<?> type;

  public EntityUUID(final URI entitySetURI, final Class<?> type) {
    this(entitySetURI, type, null);
  }

  public EntityUUID(final URI entitySetURI, final Class<?> type, final Object key) {
    this.entitySetURI = entitySetURI;
    this.key = key;
    this.tempKey = (int) (Math.random() * 1000000);

    if (type == null || !Serializable.class.isAssignableFrom(type)) {
      throw new IllegalArgumentException("Invalid Entity type class: " + type);
    }
    if (this.type == null) {
      for (Class<?> clazz : ClassUtils.hierarchy(type, ClassUtils.Interfaces.INCLUDE)) {
        if (ArrayUtils.contains(clazz.getInterfaces(), EntityType.class)) {
          this.type = clazz;
        }
      }
    }
  }

  public URI getEntitySetURI() {
    return entitySetURI;
  }

  public Object getKey() {
    return key;
  }

  public Class<?> getType() {
    return type;
  }

  @Override
  public boolean equals(final Object obj) {
    return key == null
            ? EqualsBuilder.reflectionEquals(this, obj)
            : EqualsBuilder.reflectionEquals(this, obj, "tempKey");
  }

  @Override
  public int hashCode() {
    return key == null
            ? HashCodeBuilder.reflectionHashCode(this)
            : HashCodeBuilder.reflectionHashCode(this, "tempKey");
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }
}
