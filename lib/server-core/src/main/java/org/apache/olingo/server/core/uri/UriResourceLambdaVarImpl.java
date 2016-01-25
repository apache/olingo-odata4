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
package org.apache.olingo.server.core.uri;

import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceLambdaVariable;

public class UriResourceLambdaVarImpl extends UriResourceTypedImpl implements UriResourceLambdaVariable {

  private final String variableText;
  private final EdmType type;

  public UriResourceLambdaVarImpl(final String variableText, final EdmType type) {
    super(UriResourceKind.lambdaVariable);
    this.variableText = variableText;
    this.type = type;
  }

  @Override
  public String getVariableName() {
    return variableText;
  }

  @Override
  public EdmType getType() {
    return type;
  }

  @Override
  public boolean isCollection() {
    return false;
  }

  @Override
  public String getSegmentValue() {
    return variableText;
  }
}
