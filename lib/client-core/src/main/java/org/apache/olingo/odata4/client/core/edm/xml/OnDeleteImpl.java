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
package org.apache.olingo.odata4.client.core.edm.xml;

import org.apache.olingo.odata4.client.api.edm.xml.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.olingo.odata4.client.api.edm.xml.OnDelete;

public class OnDeleteImpl extends AbstractEdmItem implements OnDelete {

  private static final long serialVersionUID = -5321523424474336347L;

  @JsonProperty(value = "Action", required = true)
  private OnDeleteAction action = OnDeleteAction.None;

  @Override
  public OnDeleteAction getAction() {
    return action;
  }

  public void setAction(final OnDeleteAction action) {
    this.action = action;
  }

}
