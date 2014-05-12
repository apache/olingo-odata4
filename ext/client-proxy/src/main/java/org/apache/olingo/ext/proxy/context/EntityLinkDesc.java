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
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.ext.proxy.commons.EntityTypeInvocationHandler;

public class EntityLinkDesc implements Serializable {

  private static final long serialVersionUID = 704670372070370762L;

  private final String sourceName;

  private final EntityTypeInvocationHandler<?> source;

  private final Collection<EntityTypeInvocationHandler<?>> targets;

  private final ODataLinkType type;

  public EntityLinkDesc(
          final String sourceName,
          final EntityTypeInvocationHandler<?> source,
          final Collection<EntityTypeInvocationHandler<?>> target,
          final ODataLinkType type) {
    this.sourceName = sourceName;
    this.source = source;
    this.targets = target;
    this.type = type;
  }

  public EntityLinkDesc(
          final String sourceName,
          final EntityTypeInvocationHandler<?> source,
          final EntityTypeInvocationHandler<?> target,
          final ODataLinkType type) {
    this.sourceName = sourceName;
    this.source = source;
    this.targets = Collections.<EntityTypeInvocationHandler<?>>singleton(target);
    this.type = type;
  }

  public String getSourceName() {
    return sourceName;
  }

  public EntityTypeInvocationHandler<?> getSource() {
    return source;
  }

  public Collection<EntityTypeInvocationHandler<?>> getTargets() {
    return targets;
  }

  public ODataLinkType getType() {
    return type;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
