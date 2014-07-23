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
package org.apache.olingo.server.core.serializer.utils;

import java.net.URI;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.core.Encoder;

public final class ContextURLBuilder {

  public static final URI create(final ContextURL contextURL) {
    StringBuilder result = new StringBuilder();
    if (contextURL.getServiceRoot() != null) {
      result.append(contextURL.getServiceRoot());
    }
    result.append(Constants.METADATA);
    if (contextURL.getEntitySetOrSingletonOrType() != null) {
      result.append('#').append(Encoder.encode(contextURL.getEntitySetOrSingletonOrType()));
    }
    if (contextURL.getDerivedEntity() != null) {
      if (contextURL.getEntitySetOrSingletonOrType() == null) {
        throw new IllegalArgumentException("ContextURL: Derived Type without anything to derive from!");
      }
      result.append('/').append(Encoder.encode(contextURL.getDerivedEntity()));
    }
    if (contextURL.isReference()) {
      if (contextURL.getEntitySetOrSingletonOrType() != null) {
        throw new IllegalArgumentException("ContextURL: $ref with Entity Set");
      }
      result.append('#').append(ContextURL.Suffix.REFERENCE.getRepresentation());
    } else if (contextURL.getSuffix() != null) {
      if (contextURL.getEntitySetOrSingletonOrType() == null) {
        throw new IllegalArgumentException("ContextURL: Suffix without preceding Entity Set!");
      }
      result.append('/').append(contextURL.getSuffix().getRepresentation());
    }
    return URI.create(result.toString());
  }
}
