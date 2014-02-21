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
package org.apache.olingo.odata4.client.core.edm;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.olingo.odata4.client.api.edm.v3.Annotations;

public abstract class AbstractAnnotations extends AbstractEdmItem implements Annotations {

  private static final long serialVersionUID = 4926640428016042620L;

  @JsonProperty(value = "Target", required = true)
  private String target;

  @JsonProperty("Qualifier")
  private String qualifier;

  public String getTarget() {
    return target;
  }

  public void setTarget(final String target) {
    this.target = target;
  }

  public String getQualifier() {
    return qualifier;
  }

  public void setQualifier(final String qualifier) {
    this.qualifier = qualifier;
  }
}
