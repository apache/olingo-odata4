/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.producer.core.uri.expression;

import org.apache.olingo.odata4.producer.core.uri.UriInfoImplPath;

public class Member extends Expression implements Visitable {

  private boolean isIT;
  UriInfoImplPath path;

  public Member setIT(boolean isIT) {
    this.isIT = isIT;
    return this;
  }

  public boolean isIT() {
    return isIT;
  }

  public Member setPath(UriInfoImplPath pathSegments) {
    this.path = pathSegments;
    return this;
  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor) throws ExceptionVisitExpression {
    return visitor.visitMember(this);

  }

  public UriInfoImplPath getPath() {
    return path;
  }
}
