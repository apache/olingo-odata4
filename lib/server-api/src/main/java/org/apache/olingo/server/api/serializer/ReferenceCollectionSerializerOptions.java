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
package org.apache.olingo.server.api.serializer;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.server.api.uri.queryoption.CountOption;

/** Options to pass as additional information to the reference-collection serializer. */
public final class ReferenceCollectionSerializerOptions {
  private ContextURL contextURL;
  private CountOption count;

  /** Gets the {@link ContextURL}. */
  public ContextURL getContextURL() {
    return contextURL;
  }

  /** Gets the $count system query option. */
  public CountOption getCount() {
    return count;
  }

  private ReferenceCollectionSerializerOptions() {}

  /** Initializes the options builder. */
  public static Builder with() {
    return new Builder();
  }

  /** Builder of OData serializer options. */
  public static final class Builder {
    private ReferenceCollectionSerializerOptions options;

    public Builder() {
      options = new ReferenceCollectionSerializerOptions();
    }

    /** Sets the {@link ContextURL}. */
    public Builder contextURL(final ContextURL contextURL) {
      options.contextURL = contextURL;
      return this;
    }

    /** Sets the $count system query option. */
    public Builder count(final CountOption count) {
      options.count = count;
      return this;
    }

    /** Builds the OData serializer options. */
    public ReferenceCollectionSerializerOptions build() {
      return options;
    }
  }
}
