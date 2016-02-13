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
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

/** Options for the OData serializer. */
public class EntitySerializerOptions {
  private ContextURL contextURL;
  private ExpandOption expand;
  private SelectOption select;
  private boolean writeOnlyReferences;
  private String xml10InvalidCharReplacement;

  /** Gets the {@link ContextURL}. */
  public ContextURL getContextURL() {
    return contextURL;
  }

  /** Gets the $expand system query option. */
  public ExpandOption getExpand() {
    return expand;
  }

  /** Gets the $select system query option. */
  public SelectOption getSelect() {
    return select;
  }

  /** only writes the references of the entities */
  public boolean getWriteOnlyReferences() {
    return writeOnlyReferences;
  }

  /** Gets the replacement string for unicode characters, that is not allowed in XML 1.0 */
  public String xml10InvalidCharReplacement() {
    return xml10InvalidCharReplacement;
  }  

  private EntitySerializerOptions() {}

  /** Initializes the options builder. */
  public static Builder with() {
    return new Builder();
  }

  /** Builder of OData serializer options. */
  public static final class Builder {

    private final EntitySerializerOptions options;

    private Builder() {
      options = new EntitySerializerOptions();
    }

    /** Sets the {@link ContextURL}. */
    public Builder contextURL(final ContextURL contextURL) {
      options.contextURL = contextURL;
      return this;
    }

    /** Sets the $expand system query option. */
    public Builder expand(final ExpandOption expand) {
      options.expand = expand;
      return this;
    }

    /** Sets the $select system query option. */
    public Builder select(final SelectOption select) {
      options.select = select;
      return this;
    }

    /** Sets to serialize only references */
    public Builder writeOnlyReferences(final boolean ref) {
      options.writeOnlyReferences = ref;
      return this;
    }
    
    /** set the replacement string for xml 1.0 unicode controlled characters that are not allowed */
    public Builder xml10InvalidCharReplacement(final String replacement) {
      options.xml10InvalidCharReplacement = replacement;
      return this;
    } 
    
    /** Builds the OData serializer options. */
    public EntitySerializerOptions build() {
      return options;
    }
  }
}
