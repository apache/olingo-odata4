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
package org.apache.olingo.client.core.edm.xml.v4;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.olingo.client.api.edm.xml.v4.Function;

@JsonDeserialize(using = FunctionDeserializer.class)
public class FunctionImpl extends ActionImpl implements Function {

  private static final long serialVersionUID = -5888231162358116515L;

  private boolean composable = false;

  @Override
  public boolean isComposable() {
    return composable;
  }

  public void setComposable(final boolean composable) {
    this.composable = composable;
  }

}
